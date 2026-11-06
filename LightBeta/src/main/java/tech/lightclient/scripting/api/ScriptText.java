package tech.lightclient.scripting.api;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.util.Formatting;

import java.net.URI;

/**
 * Builder for styled chat text, mirroring the {@code text} object in the docs.
 *
 * <p>Wrapping Minecraft's {@code Text} keeps the fluent, mutable style the docs describe
 * ({@code text.literal("x").color(...).bold(true)}) while hiding the vanilla API from scripts.</p>
 */
public final class ScriptText {

    private MutableText delegate;

    private ScriptText(MutableText delegate) {
        this.delegate = delegate;
    }

    // ---- constructors ----------------------------------------------------------------------

    public static final class Factory {
        public ScriptText literal(String s) {
            return new ScriptText(Text.literal(s == null ? "" : s));
        }

        public ScriptText empty() {
            return new ScriptText(Text.empty());
        }

        public ScriptText translatable(String key, Object... args) {
            Object[] mapped = new Object[args == null ? 0 : args.length];
            for (int i = 0; i < mapped.length; i++) {
                Object a = args[i];
                mapped[i] = a instanceof ScriptText st ? st.build() : a;
            }
            return new ScriptText(Text.translatable(key, mapped));
        }

        public ScriptText keybind(String key) {
            return new ScriptText(Text.keybind(key));
        }

        /** Parses legacy section-sign colour codes into a real style. */
        public ScriptText legacy(String s) {
            return new ScriptText(LegacyText.parse(s));
        }

        public ScriptText fromJson(String json) {
            try {
                com.google.gson.JsonElement element = com.google.gson.JsonParser.parseString(json);
                Text parsed = net.minecraft.text.TextCodecs.CODEC
                        .parse(com.mojang.serialization.JsonOps.INSTANCE, element)
                        .result()
                        .orElse(null);
                return new ScriptText(parsed == null ? Text.empty() : parsed.copy());
            } catch (Throwable t) {
                return new ScriptText(Text.literal(json == null ? "" : json));
            }
        }
    }

    public MutableText build() {
        return delegate;
    }

    // ---- styling ---------------------------------------------------------------------------

    public ScriptText color(int argb) {
        delegate = delegate.styled(s -> s.withColor(argb & 0x00FFFFFF));
        return this;
    }

    public ScriptText bold(boolean v) {
        delegate = delegate.styled(s -> s.withBold(v));
        return this;
    }

    public ScriptText italic(boolean v) {
        delegate = delegate.styled(s -> s.withItalic(v));
        return this;
    }

    public ScriptText underlined(boolean v) {
        delegate = delegate.styled(s -> s.withUnderline(v));
        return this;
    }

    public ScriptText strikethrough(boolean v) {
        delegate = delegate.styled(s -> s.withStrikethrough(v));
        return this;
    }

    public ScriptText obfuscated(boolean v) {
        delegate = delegate.styled(s -> s.withObfuscated(v));
        return this;
    }

    public ScriptText font(String id) {
        net.minecraft.util.Identifier ident = net.minecraft.util.Identifier.tryParse(id);
        if (ident != null) {
            var source = new net.minecraft.text.StyleSpriteSource.Font(ident);
            delegate = delegate.styled(s -> s.withFont(source));
        }
        return this;
    }

    public ScriptText append(ScriptText other) {
        if (other != null) delegate = delegate.append(other.delegate);
        return this;
    }

    public ScriptText append(String s) {
        delegate = delegate.append(Text.literal(s == null ? "" : s));
        return this;
    }

    public ScriptText insertion(String v) {
        delegate = delegate.styled(s -> s.withInsertion(v));
        return this;
    }

    // ---- clicks and hovers -----------------------------------------------------------------

    public ScriptText runCommand(String command) {
        String cmd = command == null ? "" : command;
        delegate = delegate.styled(s -> s.withClickEvent(new ClickEvent.RunCommand(cmd)));
        return this;
    }

    public ScriptText suggestCommand(String command) {
        String cmd = command == null ? "" : command;
        delegate = delegate.styled(s -> s.withClickEvent(new ClickEvent.SuggestCommand(cmd)));
        return this;
    }

    /** Only http and https are accepted, as the docs promise. */
    public ScriptText openUrl(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();
            if (scheme == null) return this;
            if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) return this;
            delegate = delegate.styled(s -> s.withClickEvent(new ClickEvent.OpenUrl(uri)));
        } catch (Throwable ignored) {
        }
        return this;
    }

    public ScriptText copyToClipboard(String value) {
        String v = value == null ? "" : value;
        delegate = delegate.styled(s -> s.withClickEvent(new ClickEvent.CopyToClipboard(v)));
        return this;
    }

    public ScriptText hoverText(ScriptText tooltip) {
        if (tooltip != null) {
            delegate = delegate.styled(s -> s.withHoverEvent(new HoverEvent.ShowText(tooltip.delegate)));
        }
        return this;
    }

    public ScriptText hoverText(String tooltip) {
        Text t = Text.literal(tooltip == null ? "" : tooltip);
        delegate = delegate.styled(s -> s.withHoverEvent(new HoverEvent.ShowText(t)));
        return this;
    }

    public ScriptText hoverItem(net.minecraft.item.ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            delegate = delegate.styled(s -> s.withHoverEvent(new HoverEvent.ShowItem(stack)));
        }
        return this;
    }

    // ---- reading ---------------------------------------------------------------------------

    /** The plain text a player sees, with styling discarded. */
    public String string() {
        return delegate.getString();
    }

    public String toJson() {
        try {
            return net.minecraft.text.TextCodecs.CODEC
                    .encodeStart(com.mojang.serialization.JsonOps.INSTANCE, delegate)
                    .result()
                    .map(com.google.gson.JsonElement::toString)
                    .orElseGet(() -> "\"" + string() + "\"");
        } catch (Throwable t) {
            return "\"" + string() + "\"";
        }
    }

    /** Independent copy, for when restyling must not be seen by other holders. */
    public ScriptText copy() {
        return new ScriptText(delegate.copy());
    }

    @Override
    public String toString() {
        return string();
    }

    /** Turns section-sign codes into a styled text tree. */
    private static final class LegacyText {
        static MutableText parse(String input) {
            MutableText root = Text.empty();
            if (input == null || input.isEmpty()) return root;

            StringBuilder buffer = new StringBuilder();
            Style style = Style.EMPTY;
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c == '\u00A7' && i + 1 < input.length()) {
                    if (buffer.length() > 0) {
                        root.append(Text.literal(buffer.toString()).setStyle(style));
                        buffer.setLength(0);
                    }
                    char code = Character.toLowerCase(input.charAt(++i));
                    Formatting fmt = Formatting.byCode(code);
                    if (fmt == Formatting.RESET) {
                        style = Style.EMPTY;
                    } else if (fmt != null) {
                        style = fmt.isColor() ? Style.EMPTY.withFormatting(fmt) : style.withFormatting(fmt);
                    }
                    continue;
                }
                buffer.append(c);
            }
            if (buffer.length() > 0) {
                root.append(Text.literal(buffer.toString()).setStyle(style));
            }
            return root;
        }
    }
}
