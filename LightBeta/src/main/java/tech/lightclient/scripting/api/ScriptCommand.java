package tech.lightclient.scripting.api;

import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.scripting.runtime.ScriptInstance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Client commands declared by a script, mirroring the "Свои команды" docs.
 *
 * <p>A command only answers while its script is switched on, but the name stays reserved so
 * another script cannot take it in the meantime.</p>
 */
public final class ScriptCommand extends Command {

    /** Thrown by argument accessors; turned into a friendly reply instead of a stack trace. */
    public static final class Bad extends RuntimeException {
        public Bad(String message) {
            super(message);
        }
    }

    /** The {@code command("name") { ... }} builder. */
    public static final class Builder {
        final String name;
        final List<String> aliases = new ArrayList<>();
        final Map<String, Builder> subs = new LinkedHashMap<>();
        final Map<Integer, Supplier<List<String>>> completions = new LinkedHashMap<>();
        String usage = "";
        String shortDescription = "";
        Consumer<Context> runs;

        Builder(String name) {
            this.name = name;
        }

        /** The command's primary name, without a prefix. */
        public String name() {
            return name;
        }

        public Builder usage(String value) {
            this.usage = value == null ? "" : value;
            return this;
        }

        public Builder description(String value) {
            this.shortDescription = value == null ? "" : value;
            return this;
        }

        public Builder alias(String value) {
            if (value != null && !value.isBlank()) aliases.add(value.toLowerCase());
            return this;
        }

        /**
         * Declares a subcommand.
         *
         * <p>Named {@code withSub} rather than {@code sub} on purpose: the prelude adds a
         * {@code sub} extension whose lambda has the builder as its <em>receiver</em>, which is the
         * form the docs use. A Java member of the same name would win resolution and silently give
         * the lambda a parameter instead, so the two must not collide.</p>
         */
        public Builder withSub(String name, Consumer<Builder> body) {
            Builder child = new Builder(name.toLowerCase());
            body.accept(child);
            subs.put(child.name, child);
            return this;
        }

        /** What to do when the command runs. See {@link #withSub} for the naming. */
        public Builder withRuns(Consumer<Context> body) {
            this.runs = body;
            return this;
        }

        /** Tab suggestions for one argument index. See {@link #withSub} for the naming. */
        public Builder withCompletions(int index, Supplier<List<String>> options) {
            completions.put(index, options);
            return this;
        }

        /** Fixed list of tab suggestions for one argument index. */
        public Builder completes(int index, String... options) {
            List<String> fixed = List.of(options);
            completions.put(index, () -> fixed);
            return this;
        }
    }
    /** What a {@code runs { }} block can see. */
    public static final class Context {
        private final String label;
        private final List<String> args;

        Context(String label, List<String> args) {
            this.label = label;
            this.args = args;
        }

        public String label() { return label; }

        public List<String> args() { return args; }

        public int argCount() { return args.size(); }

        /** Argument by index; complains clearly when it is missing. */
        public String arg(int index) {
            if (index < 0 || index >= args.size()) {
                throw new Bad("expected an argument at position " + (index + 1));
            }
            return args.get(index);
        }

        public String argOr(int index, String def) {
            return index >= 0 && index < args.size() ? args.get(index) : def;
        }

        public int intArg(int index) {
            String raw = arg(index);
            try {
                return Integer.parseInt(raw.trim());
            } catch (NumberFormatException e) {
                throw new Bad("'" + raw + "' is not a whole number");
            }
        }

        public double doubleArg(int index) {
            String raw = arg(index);
            try {
                return Double.parseDouble(raw.trim());
            } catch (NumberFormatException e) {
                throw new Bad("'" + raw + "' is not a number");
            }
        }

        public boolean booleanArg(int index) {
            String raw = arg(index).trim().toLowerCase();
            return switch (raw) {
                case "true", "on", "yes", "1" -> true;
                case "false", "off", "no", "0" -> false;
                default -> throw new Bad("'" + raw + "' is not a yes/no value");
            };
        }

        /** Everything after the command as one string. */
        public String rest() {
            return rest(0);
        }

        public String rest(int fromIndex) {
            if (fromIndex >= args.size()) return "";
            return String.join(" ", args.subList(fromIndex, args.size()));
        }

        public void reply(Object message) {
            chat().print(message);
        }

        public void replyError(Object message) {
            chat().print(new ScriptText.Factory().literal(String.valueOf(message)).color(Colors.RED));
        }

        private ChatApi chat() {
            return new ChatApi("command");
        }
    }

    private final ScriptInstance script;
    private final Builder root;

    public ScriptCommand(ScriptInstance script, Builder root) {
        super(namesOf(root));
        this.script = script;
        this.root = root;
    }

    private static String[] namesOf(Builder root) {
        List<String> names = new ArrayList<>();
        names.add(root.name);
        names.addAll(root.aliases);
        return names.toArray(new String[0]);
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        // A command belonging to a switched-off script stays silent.
        if (!script.isEnabled()) return;

        List<String> raw = new ArrayList<>();
        while (args.hasAny()) raw.add(args.getString());

        Builder target = root;
        int consumed = 0;
        while (consumed < raw.size()) {
            Builder child = target.subs.get(raw.get(consumed).toLowerCase());
            if (child == null) break;
            target = child;
            consumed++;
        }

        Builder finalTarget = target;
        List<String> remaining = raw.subList(consumed, raw.size());
        Context context = new Context(label, remaining);

        if (finalTarget.runs == null) {
            context.replyError("usage: ." + root.name + (finalTarget.usage.isEmpty() ? "" : " " + finalTarget.usage));
            return;
        }

        tech.lightclient.scripting.runtime.ScriptWatchdog.runGuarded(script, "command ." + root.name, () -> {
            try {
                finalTarget.runs.accept(context);
                script.noteSuccess();
            } catch (Bad bad) {
                // An argument problem is the user's mistake, not a script failure.
                context.replyError(bad.getMessage());
            } catch (Throwable t) {
                script.reportFailure("command ." + root.name, t);
                context.replyError("the command failed, see the script console");
            }
        });
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (!script.isEnabled()) return Stream.empty();

        List<String> raw = new ArrayList<>();
        while (args.hasAny()) raw.add(args.getString());

        Builder target = root;
        int consumed = 0;
        while (consumed < raw.size()) {
            Builder child = target.subs.get(raw.get(consumed).toLowerCase());
            if (child == null) break;
            target = child;
            consumed++;
        }

        int argIndex = Math.max(0, raw.size() - consumed - 1);
        String partial = raw.isEmpty() ? "" : raw.get(raw.size() - 1).toLowerCase();

        List<String> options = new ArrayList<>();
        // Subcommands are offered at the position directly after their parent.
        if (raw.size() - consumed <= 1) options.addAll(target.subs.keySet());

        Supplier<List<String>> supplier = target.completions.get(argIndex);
        if (supplier != null) {
            try {
                List<String> extra = supplier.get();
                if (extra != null) options.addAll(extra);
            } catch (Throwable ignored) {
            }
        }
        return options.stream().filter(o -> o != null && o.toLowerCase().startsWith(partial));
    }

    @Override
    public String getShortDesc() {
        return root.shortDescription.isEmpty()
                ? "script command from " + script.displayName()
                : root.shortDescription;
    }

    @Override
    public List<String> getLongDesc() {
        List<String> out = new ArrayList<>();
        out.add(getShortDesc());
        if (!root.usage.isEmpty()) {
            out.add("");
            out.add("usage: ." + root.name + " " + root.usage);
        }
        if (!root.subs.isEmpty()) {
            out.add("");
            out.add("subcommands: " + String.join(", ", root.subs.keySet()));
        }
        return out;
    }

    public String rootName() {
        return root.name;
    }

    public static Builder builder(String name) {
        return new Builder(name.toLowerCase());
    }
}
