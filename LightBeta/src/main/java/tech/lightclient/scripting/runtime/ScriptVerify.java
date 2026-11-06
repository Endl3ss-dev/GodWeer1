package tech.lightclient.scripting.runtime;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Development harness: compiles the prelude plus a representative script against the real API,
 * outside the game, so DSL/type errors surface in a normal build rather than in-game.
 *
 * <p>Run with the {@code verifyScripts} Gradle task.</p>
 */
public final class ScriptVerify {

    public static void main(String[] args) throws Exception {
        int failures = 0;

        failures += check("minimal", """
                chat.print("hello")
                """);

        failures += check("metadata and settings", """
                name("AutoJump")
                description("Jumps for you")
                key(Key.G)
                requireApi(1)

                val delay = slider("Delay", 5, 1, 20)
                val sound = checkBox("Sound", true)
                val alpha = slider(sound, "Alpha", 0.5f, 0f, 1f, 0.05f)
                val speed by slider("Speed", 5f, 1f, 20f, 1f).postfix(Postfixes.MS)
                val on by checkBox("On", true)
                val label by input("Label", "text")
                val mode by selectable("Mode", "A", "B", selected = "A")
                val targets by combo("Targets", "players", "mobs", selected = listOf("players"))
                val colour by colorPicker("Colour", Colors.rgba(90, 200, 255, 255))
                val span by rangeSlider("Delay range", 2f, 8f, 0f, 20f, 1f)
                hotkey("Panic", Key.RIGHT_SHIFT) { enabled = false }
                button("Reset") { chat.print("reset") }
                alpha.visibleWhen { sound.value() }
                delay.onChange { chat.print("now " + it) }

                var ticks = 0
                onEnable { ticks = 0 }
                onDisable { }
                onUnload { }

                on<ClientTickEvent> {
                    if (!inGame) return@on
                    if (++ticks < delay.intValue()) return@on
                    ticks = 0
                    control.jump()
                    if (sound.value()) {
                        world.playSound("minecraft:entity.experience_orb.pickup", 1f, 1f)
                    }
                    if (speed > 1f && on && label.isNotEmpty() && mode == "A"
                        && targets.isNotEmpty() && colour != 0 && span.start < span.endInclusive) {
                        log.info("all delegates read fine")
                    }
                }
                """);

        failures += check("events, priority and cancelling", """
                on<MoveInputEvent>(priority = Priority.LAST) { it.sprint(true); it.jump(true) }
                on<AttackEvent>(ignoreCancelled = true) { e -> log.info("attack " + e.target) }
                val sub = on<ClientTickEvent> { }
                sub.unsubscribe()
                log.info("active: " + sub.active())

                on<PacketReceiveEvent> { e ->
                    val type = e.type()
                    if (type.contains("Velocity")) {
                        onClientThread { control.jump() }
                    }
                }
                on<PacketSendEvent> { e -> if (e.type().contains("Chat")) e.cancel() }
                on<KeyEvent> { }
                on<Render2DEvent> { e ->
                    e.render().text(10f, 20f, 12f, "hud", Colors.WHITE)
                    e.render().rect(0f, 0f, 10f, 10f, Colors.fade(Colors.RED, 0.5f))
                    val screen = e.render().project(Vec(0.0, 0.0, 0.0))
                    if (screen != null) e.render().text(screen.x.toFloat(), screen.y.toFloat(), 10f, "x", Colors.CYAN)
                }
                on<Render3DEvent> { e ->
                    for (target in world.players()) {
                        if (target.isSelf() || target.isAlly()) continue
                        e.render().entityBox(target, Colors.RED, true)
                    }
                    val ring = (0 until 24).map {
                        val angle = Math.toRadians(it * 15.0)
                        player.renderPosition().add(Math.cos(angle) * 3.0, 0.02, Math.sin(angle) * 3.0)
                    }
                    e.render().polygon(ring, Colors.rgba(90, 200, 255, 60), true)
                }
                on<EntitySpawnEvent> { log.info(it.entity().name()) }
                on<EntityRemoveEvent> { }
                on<WorldLoadEvent> { log.info(it.dimension()) }
                on<ModuleToggleEvent> { log.info(it.name() + " -> " + it.enabled()) }
                on<SlotClickEvent> { }
                on<ScreenCloseEvent> { }
                on<JumpEvent> { }
                """);

        failures += check("player, world and raycast", """
                on<ClientTickEvent> {
                    whenInGame {
                        val hp = player.health()
                        val pos = player.position()
                        val eyes = player.eyePosition()
                        val prev = player.previousPosition()
                        val render = player.renderPosition()
                        val target = pos.add(0.0, 1.0, 0.0)
                        val distance = pos.distanceTo(target)
                        val direction = target.subtract(pos).normalize()
                        log.info("" + hp + eyes + prev + render + distance + direction)
                        log.info("" + player.yaw() + player.pitch() + player.velocity())
                        log.info("" + player.onGround() + player.sprinting() + player.sneaking())
                        log.info("" + player.attackCooldown() + player.belowMinimumAttackCharge())
                        log.info("" + player.entityReachBlocks() + player.blockReachBlocks())
                        log.info("" + player.usingItem() + player.itemUseTicks() + player.blocking())
                        log.info("" + player.hasEffect("speed") + player.effects().size)
                        log.info("" + player.armorPoints() + player.food() + player.saturation())
                        log.info("" + player.xpLevel() + player.airTicks() + player.gameMode())
                        log.info("" + player.isNaked() + player.hasMovementInput() + player.pingMs())
                        player.effect("speed")?.amplifier()

                        val block = world.block(0, 64, 0)
                        log.info(block.id() + block.air() + block.replaceable() + block.blocksMovement())
                        log.info("" + world.isDay() + world.isRaining() + world.dimension() + world.time())
                        val near = world.entitiesNear(pos, 48.0, filters.attackable())
                        val nearest = world.nearestEntity(pos, 64.0, filters.enemyPlayers())
                        if (nearest != null) {
                            log.info(nearest.name() + nearest.health() + nearest.type())
                            val aim = rotations.lookAt(nearest)
                            log.info("" + aim.yaw() + aim.pitch())
                            val blocked = raycast.from(aim, 6.0, true) { !it.isAlly() }
                            log.info("" + blocked.distance())
                            interaction.attack(nearest)
                        }
                        log.info("" + near.size + world.isFree(block.box()))

                        val looked = raycast.entityAtCrosshair(6.0) { !it.isAlly() && it.alive() }
                        when (val hit = raycast.crosshair(5.0)) {
                            is Hit.OnBlock -> log.info("block " + hit.side() + hit.blockX())
                            is Hit.OnEntity -> log.info("entity " + hit.entity().name())
                            is Hit.None -> log.info("miss")
                        }
                        if (raycast.canSee(eyes, pos)) log.info("visible")
                        log.info("" + looked)
                    }
                }
                """);

        failures += check("timers, tasks, storage and messages", """
                val since = timer()
                val stats = config("stats")

                on<ClientTickEvent> {
                    if (since.passedAndReset(10)) {
                        notify("tick", NotifyKind.WARN)
                        notify("plain")
                        chat.print(text.literal("styled").color(Colors.GREEN).bold(true))
                        chat.printPrefixed("[T]", "done")
                        log.warn("warned")
                        log.error("bad")
                    }
                    if (since.passed(5) || since.passedMillis(100)) {
                        log.info("" + since.elapsedTicks() + since.elapsedMillis())
                    }
                }

                nextTick { log.info("next") }
                afterTicks(20) { log.info("later") }
                val task = everyTicks(100) { log.info("regular") }
                if (!task.cancelled()) task.cancel()

                val runs = storage.getInt("runs", 0) + 1
                storage.put("runs", runs)
                storage.put("name", "text")
                storage.put("on", true)
                storage.save()
                log.info("" + storage.has("runs") + storage.keys() + storage.dirty())
                stats.put("kills", stats.getInt("kills", 0) + 1)
                stats.save()
                log.info("" + configs.names() + configs.exists("stats"))
                log.info("" + client.millis() + client.tick() + client.fps() + client.tickDelta())
                """);

        failures += check("commands", """
                command("home") {
                    usage("<set|go|list>")
                    alias("h")
                    sub("set") {
                        usage("<name>")
                        runs {
                            storage.put("home." + arg(0), player.position().toString())
                            storage.save()
                            reply("saved " + arg(0))
                        }
                    }
                    sub("go") {
                        completes(0) { storage.keys().filter { it.startsWith("home.") } }
                        runs {
                            val value = storage.get("home." + arg(0), "")
                            if (value.isEmpty()) replyError("no such home") else reply(value)
                        }
                    }
                    sub("list") {
                        completes(0, "all", "mine")
                        runs {
                            reply("count " + storage.keys().size + " label " + label() + " rest " + rest())
                            reply("" + intArg(0) + doubleArg(0) + booleanArg(0) + argOr(1, "x") + argCount() + args())
                        }
                    }
                }
                """);

        failures += check("client modules", """
                val aura = client.modules().find("AttackAura")
                if (aura != null) {
                    log.info(aura.name() + aura.displayName() + aura.enabled() + aura.bind())
                    aura.setEnabled(false)
                    aura.toggle()
                    log.info("" + aura.settings())
                }
                log.info("" + client.modules().all().size + client.modules().exists("AttackAura"))
                log.info("" + client.inGame() + client.singleplayer() + client.apiVersion())
                log.info("" + game.screenOpen() + game.screenKind() + game.singleplayer())
                """);

        failures += checkSandbox();
        failures += checkErrorLineNumbers();

        System.out.println(failures == 0
                ? "\nALL SCRIPT CHECKS PASSED"
                : "\n" + failures + " SCRIPT CHECK(S) FAILED");
        System.exit(failures == 0 ? 0 : 1);
    }

    /** The sandbox must reject the forbidden APIs and let ordinary code through. */
    private static int checkSandbox() {
        record Case(String label, String source, boolean shouldReject) {}
        Case[] cases = {
                new Case("file access", "val f = java.io.File(\"x\")", true),
                new Case("nio", "java.nio.file.Files.readString(null)", true),
                new Case("thread", "Thread.sleep(100)", true),
                new Case("reflection", "val c = this.javaClass.getDeclaredField(\"x\")", true),
                new Case("runtime exec", "Runtime.getRuntime().exec(\"calc\")", true),
                new Case("system property", "System.getProperty(\"user.home\")", true),
                new Case("raw minecraft", "net.minecraft.client.MinecraftClient.getInstance()", true),
                new Case("client internals", "tech.lightclient.LightClient.getEventBus()", true),
                new Case("author import", "import kotlin.math.abs\nval x = abs(-1)", true),
                new Case("the word Thread in a string", "chat.print(\"Thread safety\")", false),
                new Case("a comment mentioning File", "// uses java.io.File in theory\nchat.print(\"ok\")", false),
                new Case("ordinary script", "val d = slider(\"D\", 5, 1, 20)\non<ClientTickEvent> { }", false),
        };

        int failures = 0;
        for (Case c : cases) {
            ScriptSandbox.Violation v = ScriptSandbox.check(c.source());
            boolean rejected = v != null;
            if (rejected == c.shouldReject()) {
                System.out.println("PASS  sandbox: " + c.label()
                        + (rejected ? " -> rejected (" + v.symbol + ")" : " -> allowed"));
            } else {
                System.out.println("FAIL  sandbox: " + c.label()
                        + " expected " + (c.shouldReject() ? "rejection" : "acceptance")
                        + " but got " + (rejected ? "rejection" : "acceptance"));
                failures++;
            }
        }
        return failures;
    }

    /**
     * A compile error must be reported against the author's line, not the combined source, or the
     * message in the docs ("auto-jump.kts:12") would be meaningless.
     */
    private static int checkErrorLineNumbers() throws Exception {
        // The mistake is deliberately on line 3 of the author's own file.
        String source = "name(\"LineTest\")\nval a = 1\nthisDoesNotExist()\n";
        File tmp = File.createTempFile("linetest-", ".kts");
        Files.writeString(tmp.toPath(), source, StandardCharsets.UTF_8);
        try {
            ScriptInstance script = new ScriptInstance(tmp);
            ScriptHost host = new ScriptHost(script);
            var bindings = ScriptEngineHost.newBindings();
            bindings.put("__host", host);
            ScriptEngineHost.eval(ScriptPrelude.SOURCE + source, bindings);
            System.out.println("FAIL  error line numbers: the broken script compiled");
            return 1;
        } catch (Throwable t) {
            String shifted = shiftLine(ScriptEngineHost.describe(t));
            if (shifted.contains("script line 3")) {
                System.out.println("PASS  error line numbers: reported on the author's line 3");
                return 0;
            }
            System.out.println("FAIL  error line numbers: expected the author's line 3, got: " + shifted);
            return 1;
        } finally {
            tmp.delete();
        }
    }

    private static int check(String label, String source) throws Exception {
        File tmp = File.createTempFile("verify-", ".kts");
        Files.writeString(tmp.toPath(), source, StandardCharsets.UTF_8);
        ScriptInstance script = new ScriptInstance(tmp);
        ScriptHost host = new ScriptHost(script);

        var bindings = ScriptEngineHost.newBindings();
        bindings.put("__host", host);

        long start = System.currentTimeMillis();
        try {
            ScriptEngineHost.eval(ScriptPrelude.SOURCE + source, bindings);
            System.out.println("PASS  " + label + "  (" + (System.currentTimeMillis() - start) + " ms)");
            return 0;
        } catch (Throwable t) {
            String message = ScriptEngineHost.describe(t);
            // Compilation diagnostics carry Kotlin's "ERROR ..." marker. Anything else means the
            // script compiled and then failed at run time, which is expected here: there is no
            // running game, so the first call into the API throws. That still proves the point of
            // this harness, which is that the DSL type-checks.
            if (!message.contains("ERROR ")) {
                System.out.println("PASS  " + label + "  (compiled; no game at run time: "
                        + firstLine(message) + ")");
                return 0;
            }
            System.out.println("FAIL  " + label);
            for (String line : message.split("\n")) {
                if (line.contains("WARNING ")) continue;
                System.out.println("      " + shiftLine(line));
            }
            return 1;
        } finally {
            tmp.delete();
        }
    }

    private static String firstLine(String message) {
        if (message == null) return "";
        String line = message.split("\n")[0].trim();
        return line.length() > 90 ? line.substring(0, 90) + "..." : line;
    }

    /** Rewrites reported line numbers so they refer to the test snippet, not the combined source. */
    private static String shiftLine(String line) {
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("\\(([A-Za-z0-9_-]+\\.kts):(\\d+):(\\d+)\\)").matcher(line);
        StringBuilder out = new StringBuilder();
        int last = 0;
        while (m.find()) {
            int absolute = Integer.parseInt(m.group(2));
            int userLine = absolute - ScriptPrelude.LINE_COUNT;
            out.append(line, last, m.start());
            out.append(userLine >= 1
                    ? "(script line " + userLine + ":" + m.group(3) + ")"
                    : "(PRELUDE line " + absolute + ":" + m.group(3) + ")");
            last = m.end();
        }
        out.append(line.substring(last));
        return out.toString();
    }

    private ScriptVerify() {}
}
