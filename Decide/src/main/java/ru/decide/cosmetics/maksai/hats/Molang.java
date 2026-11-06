/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.maksai.hats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class Molang {
    static final Pattern TOKEN = Pattern.compile("\\s*(?:(\\d+(?:\\.\\d*)?(?:[eE][+-]?\\d+)?|\\.\\d+)|([A-Za-z_][A-Za-z_0-9.]*)|('(?:[^']*)'|\"(?:[^\"]*)\")|(\\?\\?|==|!=|<=|>=|&&|\\|\\||\\+=|-=|\\*=|/=|[+*/%<>=!?:;,(){}\\-]))");

    static String norm(String s) {
        return s.toLowerCase(Locale.ROOT).replace("query.", "q.").replace("context.", "c.").replace("variable.", "v.").replace("temp.", "t.");
    }

    public static double finite(double x) {
        return Double.isFinite(x) ? Molang.clamp(x, -1.0E12, 1.0E12) : 0.0;
    }

    public static double clamp(double x, double a, double b) {
        return Math.max(Math.min(a, b), Math.min(Math.max(a, b), x));
    }

    public static double wrap(double x) {
        return ((x + 180.0) % 360.0 + 360.0) % 360.0 - 180.0;
    }

    public static double ease(String s, double v) {
        String var5;
        double x = Molang.clamp(v, 0.0, 1.0);
        return switch (var5 = Molang.norm(s).replace("_", "")) {
            case "easeinoutsine" -> -(Math.cos(Math.PI * x) - 1.0) / 2.0;
            case "easeinsine" -> 1.0 - Math.cos(x * Math.PI / 2.0);
            case "easeoutsine" -> Math.sin(x * Math.PI / 2.0);
            case "easeinoutquad" -> {
                if (x < 0.5) {
                    yield 2.0 * x * x;
                }
                yield 1.0 - Math.pow(-2.0 * x + 2.0, 2.0) / 2.0;
            }
            case "easeinquad" -> x * x;
            case "easeoutquad" -> 1.0 - (1.0 - x) * (1.0 - x);
            case "easeinoutcubic" -> {
                if (x < 0.5) {
                    yield 4.0 * x * x * x;
                }
                yield 1.0 - Math.pow(-2.0 * x + 2.0, 3.0) / 2.0;
            }
            case "easeinoutquint" -> {
                if (x < 0.5) {
                    yield 16.0 * Math.pow(x, 5.0);
                }
                yield 1.0 - Math.pow(-2.0 * x + 2.0, 5.0) / 2.0;
            }
            case "easeoutexpo" -> {
                if (x == 1.0) {
                    yield 1.0;
                }
                yield 1.0 - Math.pow(2.0, -10.0 * x);
            }
            case "easeoutback" -> 1.0 + 2.70158 * Math.pow(x - 1.0, 3.0) + 1.70158 * Math.pow(x - 1.0, 2.0);
            default -> x;
        };
    }

    public static Program compile(String s) {
        if (s == null || s.isBlank()) {
            s = "0";
        }
        s = s.replace("bmath.cos(", "math.cos(");
        try {
            Parser p = new Parser(s);
            Node n = p.sequence(false);
            if (!p.end()) {
                throw p.error("trailing");
            }
            return new Program(n, p.script);
        }
        catch (IllegalArgumentException var3) {
            throw new IllegalArgumentException(var3.getMessage() + " source=" + s);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Parser {
        List<String> t = new ArrayList<String>();
        int at;
        int depth;
        boolean script;

        Parser(String s) {
            if (s.length() > 100000) {
                throw new IllegalArgumentException("script limit");
            }
            int p = 0;
            while (p < s.length() && !s.substring(p).isBlank()) {
                Matcher m = TOKEN.matcher(s);
                m.region(p, s.length());
                if (!m.lookingAt()) {
                    throw new IllegalArgumentException("Token near " + s.substring(p, Math.min(p + 30, s.length())));
                }
                for (int g = 1; g <= 4; ++g) {
                    if (m.group(g) == null) continue;
                    this.t.add(m.group(g));
                }
                p = m.end();
                if (this.t.size() <= 20000) continue;
                throw new IllegalArgumentException("token limit");
            }
        }

        boolean end() {
            return this.at >= this.t.size();
        }

        String peek() {
            return this.end() ? "" : this.t.get(this.at);
        }

        boolean take(String s) {
            if (this.peek().equals(s)) {
                ++this.at;
                return true;
            }
            return false;
        }

        void need(String s) {
            if (!this.take(s)) {
                throw this.error("expected " + s);
            }
        }

        IllegalArgumentException error(String s) {
            return new IllegalArgumentException(s + " near " + this.peek());
        }

        Node sequence(boolean block) {
            ArrayList<Node> list = new ArrayList<Node>();
            while (!(this.end() || block && this.peek().equals("}"))) {
                if (this.take(";")) continue;
                if (this.take("return")) {
                    Node n = this.assign();
                    list.add(e -> {
                        throw new Return(n.eval(e));
                    });
                    this.script = true;
                } else {
                    list.add(this.assign());
                }
                if (this.take(";") || this.end() || block && this.peek().equals("}")) continue;
                throw this.error("separator");
            }
            return e -> {
                double v = 0.0;
                for (Node nx : list) {
                    v = nx.eval(e);
                }
                return v;
            };
        }

        Node assign() {
            if (this.at + 1 < this.t.size() && this.t.get(this.at).matches("[A-Za-z_][A-Za-z_0-9.]*") && Set.of("=", "+=", "-=", "*=", "/=").contains(this.t.get(this.at + 1))) {
                String k = this.t.get(this.at++);
                String op = this.t.get(this.at++);
                Node n = this.assign();
                this.script = true;
                return e -> {
                    double v = n.eval(e);
                    double old = e.get(k);
                    e.put(k, switch (op) {
                        case "+=" -> old + v;
                        case "-=" -> old - v;
                        case "*=" -> old * v;
                        case "/=" -> {
                            if (Math.abs(v) > 1.0E-10) {
                                yield old / v;
                            }
                            yield 0.0;
                        }
                        default -> v;
                    });
                    return 0.0;
                };
            }
            Node c = this.coalesce();
            if (this.take("?")) {
                Node yes = this.assign();
                Node no = this.take(":") ? this.assign() : e -> 0.0;
                return e -> c.eval(e) != 0.0 ? yes.eval(e) : no.eval(e);
            }
            return c;
        }

        Node coalesce() {
            Node n = this.or();
            while (this.take("??")) {
                Node a = n;
                Node b = this.or();
                n = e -> a.defined(e) ? a.eval(e) : b.eval(e);
            }
            return n;
        }

        Node or() {
            Node n = this.and();
            while (this.take("||")) {
                Node a = n;
                Node b = this.and();
                n = e -> a.eval(e) == 0.0 && b.eval(e) == 0.0 ? 0.0 : 1.0;
            }
            return n;
        }

        Node and() {
            Node n = this.compare();
            while (this.take("&&")) {
                Node a = n;
                Node b = this.compare();
                n = e -> a.eval(e) != 0.0 && b.eval(e) != 0.0 ? 1.0 : 0.0;
            }
            return n;
        }

        Node compare() {
            Node n = this.add();
            while (Set.of("==", "!=", "<", ">", "<=", ">=").contains(this.peek())) {
                String op = this.t.get(this.at++);
                Node a = n;
                Node b = this.add();
                n = e -> {
                    double x = a.eval(e);
                    double y = b.eval(e);
                    return switch (op) {
                        case "==" -> {
                            if (x == y) {
                                yield 1.0;
                            }
                            yield 0.0;
                        }
                        case "!=" -> {
                            if (x != y) {
                                yield 1.0;
                            }
                            yield 0.0;
                        }
                        case "<" -> {
                            if (x < y) {
                                yield 1.0;
                            }
                            yield 0.0;
                        }
                        case ">" -> {
                            if (x > y) {
                                yield 1.0;
                            }
                            yield 0.0;
                        }
                        case "<=" -> {
                            if (x <= y) {
                                yield 1.0;
                            }
                            yield 0.0;
                        }
                        default -> x >= y ? 1.0 : 0.0;
                    };
                };
            }
            return n;
        }

        Node add() {
            Node n = this.mul();
            while (this.peek().equals("+") || this.peek().equals("-")) {
                String op = this.t.get(this.at++);
                Node a = n;
                Node b = this.mul();
                n = e -> op.equals("+") ? a.eval(e) + b.eval(e) : a.eval(e) - b.eval(e);
            }
            return n;
        }

        Node mul() {
            Node n = this.unary();
            while (Set.of("*", "/", "%").contains(this.peek())) {
                String op = this.t.get(this.at++);
                Node a = n;
                Node b = this.unary();
                n = e -> {
                    double x = a.eval(e);
                    double y = b.eval(e);
                    return op.equals("*") ? x * y : (Math.abs(y) < 1.0E-10 ? 0.0 : (op.equals("/") ? x / y : x % y));
                };
            }
            return n;
        }

        Node unary() {
            if (this.take("-")) {
                Node n = this.unary();
                return e -> -n.eval(e);
            }
            if (this.take("+")) {
                return this.unary();
            }
            if (this.take("!")) {
                Node n = this.unary();
                return e -> n.eval(e) == 0.0 ? 1.0 : 0.0;
            }
            return this.primary();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        Node primary() {
            if (++this.depth > 128) {
                throw this.error("depth");
            }
            try {
                String s;
                if (this.take("(")) {
                    Node n = this.assign();
                    this.need(")");
                    Node node = n;
                    return node;
                }
                if (this.take("{")) {
                    Node n = this.sequence(true);
                    this.need("}");
                    this.script = true;
                    Node node = n;
                    return node;
                }
                if (this.end()) {
                    throw this.error("expression");
                }
                if ((s = this.t.get(this.at++)).startsWith("'") || s.startsWith("\"")) {
                    Node node = e -> 0.0;
                    return node;
                }
                double v = Double.parseDouble(s);
                Node node = e -> v;
                return node;
            }
            finally {
                --this.depth;
            }
        }

        private static /* synthetic */ double lambda$primary$17(List a, String s, Env e) {
            double[] vx = new double[a.size()];
            for (int i = 0; i < vx.length; ++i) {
                vx[i] = ((Node)a.get(i)).eval(e);
            }
            return e.call(s, vx);
        }

        private static /* synthetic */ double lambda$primary$16(Env e) {
            return 0.0;
        }

        private static /* synthetic */ double lambda$primary$15(Env e) {
            return 1.0;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Node {
        public double eval(Env var1);

        default public boolean defined(Env e) {
            return true;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Program(Node node, boolean script) {
        public double eval(Env e) {
            double v;
            if (this.script && e.cache.containsKey(this.node)) {
                return e.cache.get(this.node);
            }
            try {
                v = Molang.finite(this.node.eval(e));
            }
            catch (Return var5) {
                v = Molang.finite(var5.v);
            }
            if (this.script) {
                e.cache.put(this.node, v);
            }
            return v;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Return
    extends RuntimeException {
        double v;

        Return(double v) {
            super(null, null, false, false);
            this.v = v;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Env {
        public Map<String, Double> values = new HashMap<String, Double>();
        public Map<Node, Double> cache = new IdentityHashMap<Node, Double>();
        public Set<String> unsupported = new TreeSet<String>();
        Random random = new Random(7919L);
        public double time;
        public double animTime;

        public void put(String k, double v) {
            this.values.put(Molang.norm(k), Molang.finite(v));
        }

        public double get(String k) {
            return (k = Molang.norm(k)).equals("q.life_time") ? this.time : (k.equals("q.anim_time") ? this.animTime : this.values.getOrDefault(k, 0.0));
        }

        double call(String raw, double[] a) {
            double z;
            String n = Molang.norm(raw);
            double x = a.length > 0 ? a[0] : 0.0;
            double y = a.length > 1 ? a[1] : 0.0;
            double d = z = a.length > 2 ? a[2] : 0.0;
            if (n.startsWith("q.") && n.contains("_smooth")) {
                return this.get(n.substring(0, n.indexOf("_smooth")));
            }
            return Molang.finite(switch (n) {
                case "math.sin" -> Math.sin(Math.toRadians(x));
                case "math.cos" -> Math.cos(Math.toRadians(x));
                case "math.tan" -> Math.tan(Math.toRadians(x));
                case "math.asin" -> Math.toDegrees(Math.asin(Molang.clamp(x, -1.0, 1.0)));
                case "math.acos" -> Math.toDegrees(Math.acos(Molang.clamp(x, -1.0, 1.0)));
                case "math.abs" -> Math.abs(x);
                case "math.floor" -> Math.floor(x);
                case "math.ceil" -> Math.ceil(x);
                case "math.round" -> Math.floor(x + 0.5);
                case "math.sqrt" -> Math.sqrt(Math.max(0.0, x));
                case "math.pow" -> Math.pow(x, y);
                case "math.min" -> Arrays.stream(a).min().orElse(0.0);
                case "math.max" -> Arrays.stream(a).max().orElse(0.0);
                case "math.clamp" -> Molang.clamp(x, y, z);
                case "math.mod" -> {
                    if (y == 0.0) {
                        yield 0.0;
                    }
                    yield x % y;
                }
                case "math.lerp" -> x + (y - x) * z;
                case "math.lerprotate" -> x + Molang.wrap(y - x) * z;
                case "math.random" -> {
                    if (a.length == 0) {
                        yield this.random.nextDouble();
                    }
                    yield x + this.random.nextDouble() * (y - x);
                }
                case "math.random_integer", "lunar.random_integer" -> Math.floor(x + this.random.nextDouble() * (y - x + 1.0));
                case "lunar.sigmoid" -> z * Math.tanh(x * y / Math.max(Math.abs(z), 1.0E-8));
                case "lunar.sigmoid_swing" -> Math.tanh(z / Math.max(Math.abs(x), 1.0E-8)) * y;
                case "lunar.movewave_cos" -> Math.cos(Math.toRadians(this.time * x + z)) * y;
                case "lunar.movewave_sin" -> Math.sin(Math.toRadians(this.time * x + z)) * y;
                case "lunar.speed_vibration" -> Math.sin(this.time * 70.0) * this.get("q.ground_speed") * x;
                case "lunar.interval" -> {
                    if (x > 0.0 && this.time % x < y) {
                        yield 1.0;
                    }
                    yield 0.0;
                }
                case "lunar.linear_wave" -> 1.0 - Math.abs(((x + y) % 360.0 + 360.0) % 360.0 / 180.0 - 1.0);
                case "lunar.lopsided_wave" -> Math.pow((Math.sin(Math.toRadians(x)) + 1.0) / 2.0, Math.max(0.01, Math.abs(y)));
                case "lunar.easeinback" -> 2.70158 * x * x * x - 1.70158 * x * x;
                case "lunar.smoothclamp" -> Molang.clamp(x, y, z);
                case "lunar.smoothmin" -> {
                    double k = Math.max(Math.abs(z), 1.0E-6);
                    double h = Molang.clamp(0.5 + 0.5 * (y - x) / k, 0.0, 1.0);
                    yield y + (x - y) * h - k * h * (1.0 - h);
                }
                case "lunar.set_tick_step_value" -> {
                    this.put("c.tick_step3", this.get("c.tick_step2"));
                    this.put("c.tick_step2", this.get("c.tick_step1"));
                    this.put("c.tick_step1", this.get("c.tick_step0"));
                    this.put("c.tick_step0", x);
                    yield 0.0;
                }
                case "lunar.check_for_biome" -> {
                    if (Math.round(x) == 6L) {
                        yield this.get("q.biome_swamp");
                    }
                    this.unsupported.add(n + "(" + x + ")");
                    yield 0.0;
                }
                case "lunar.check_for_desert" -> this.get("q.biome_desert");
                case "lunar.check_for_ocean" -> this.get("q.biome_ocean");
                case "lunar.check_for_nether" -> this.get("q.dimension_nether");
                case "lunar.check_for_theend" -> this.get("q.dimension_end");
                default -> {
                    this.unsupported.add(n);
                    yield 0.0;
                }
            });
        }
    }
}

