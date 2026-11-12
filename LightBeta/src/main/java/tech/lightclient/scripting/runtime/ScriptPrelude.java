package tech.lightclient.scripting.runtime;

/**
 * Kotlin source prepended to every script.
 *
 * <p>This is what makes the documented syntax work without the author writing a single import:
 * it pulls the host object out of the bindings and re-exposes it as the top-level functions,
 * properties and aliases the docs describe.</p>
 *
 * <p>Aliasing is done with real {@code import ... as ...} rather than {@code typealias} because a
 * typealias does not bring nested classes into scope, which would break documented patterns such
 * as {@code Hit.OnBlock}. Imports have to lead the file, which is fine: the prelude is always
 * first, and the sandbox only inspects the author's own source, so a script still may not add
 * imports of its own.</p>
 *
 * <p>The prelude is a fixed, known number of lines ({@link #LINE_COUNT}) so compile errors can be
 * reported against the author's line numbers rather than the combined source.</p>
 */
final class ScriptPrelude {

    private ScriptPrelude() {}

    /** Lines occupied by the prelude, used to shift reported error line numbers. */
    static final int LINE_COUNT;

    static final String SOURCE;

    static {
        String s = String.join("\n",
                // --- events ------------------------------------------------------------------
                "import tech.lightclient.client.events.impl.client.TickEvent as ClientTickEvent",
                "import tech.lightclient.client.events.impl.client.UpdateEvent as PrePlayerTickEvent",
                "import tech.lightclient.client.events.impl.client.UpdateEvent as PlayerTickEvent",
                "import tech.lightclient.client.events.impl.client.PostEvent as PostPlayerTickEvent",
                "import tech.lightclient.client.events.impl.entity.EventAttack as AttackEvent",
                "import tech.lightclient.client.events.impl.entity.EventAttack.Hurt as AttackedEvent",
                "import tech.lightclient.client.events.impl.entity.EventMove as MoveEvent",
                "import tech.lightclient.client.events.impl.player.MoveInputEvent as MoveInputEvent",
                "import tech.lightclient.client.events.impl.player.JumpEvent as JumpEvent",
                "import tech.lightclient.client.events.impl.player.SprintEvent as SprintEvent",
                "import tech.lightclient.client.events.impl.player.RotationEvent as RotationEvent",
                "import tech.lightclient.client.events.impl.client.EventChat as ChatEvent",
                "import tech.lightclient.scripting.api.ScriptPackets.Receive as PacketReceiveEvent",
                "import tech.lightclient.scripting.api.ScriptPackets.Send as PacketSendEvent",
                "import tech.lightclient.client.events.impl.client.EventKey as KeyEvent",
                "import tech.lightclient.client.events.impl.client.EventScroll as MouseScrollEvent",
                "import tech.lightclient.client.events.impl.client.EventClickSlot as SlotClickEvent",
                "import tech.lightclient.client.events.impl.client.EventCloseScreen as ScreenCloseEvent",
                "import tech.lightclient.scripting.api.ScriptRender.Event2D as Render2DEvent",
                "import tech.lightclient.scripting.api.ScriptRender.Event3D as Render3DEvent",
                "import tech.lightclient.scripting.api.ScriptEvents.EntitySpawn as EntitySpawnEvent",
                "import tech.lightclient.scripting.api.ScriptEvents.EntityRemove as EntityRemoveEvent",
                "import tech.lightclient.scripting.api.ScriptEvents.WorldLoad as WorldLoadEvent",
                "import tech.lightclient.scripting.api.ScriptEvents.ModuleToggle as ModuleToggleEvent",
                // --- helper types ------------------------------------------------------------
                "import tech.lightclient.scripting.api.Key",
                "import tech.lightclient.scripting.api.Colors",
                "import tech.lightclient.scripting.api.ScriptSettings.Postfixes",
                "import tech.lightclient.scripting.api.ScriptNotify.Kind as NotifyKind",
                "import tech.lightclient.scripting.api.ScriptPriority as Priority",
                "import net.minecraft.util.math.Vec3d as Vec",
                "import net.minecraft.util.math.Box",
                "import tech.lightclient.scripting.api.WorldApi.ScriptEntity as Entity",
                "import tech.lightclient.scripting.api.WorldApi.ScriptBlock as Block",
                // Imported directly (not aliased) so Hit.OnBlock and friends resolve.
                "import tech.lightclient.scripting.api.RaycastApi.Hit",
                "import tech.lightclient.scripting.api.ScriptRotations.Rotation",
                "import tech.lightclient.scripting.api.ScriptSettings",
                "import tech.lightclient.scripting.api.ScriptTask",
                "import tech.lightclient.scripting.api.ScriptTimer",
                "import tech.lightclient.scripting.api.ScriptShader",
                "import tech.lightclient.scripting.api.ScriptCommand",
                "import tech.lightclient.scripting.runtime.ScriptHost",
                "import tech.lightclient.scripting.runtime.ScriptSubscription",
                "",
                // --- host handle -------------------------------------------------------------
                // The engine already exposes every binding as a property, so this only narrows the
                // type; declaring a val named __host would clash with that generated accessor.
                "@Suppress(\"UNCHECKED_CAST\", \"NOTHING_TO_INLINE\", \"unused\")",
                "val __h: ScriptHost = __host as ScriptHost",
                "",
                // --- api objects -------------------------------------------------------------
                "val player = __h.player()",
                "val world = __h.world()",
                "val client = __h.client()",
                "val chat = __h.chat()",
                "val log = __h.log()",
                "val raycast = __h.raycast()",
                "val control = __h.control()",
                "val interaction = __h.interaction()",
                "val inventory = __h.inventory()",
                "val rotations = __h.rotations()",
                "val text = __h.text()",
                "val game = __h.game()",
                "val storage = __h.storage()",
                "val configs = __h.configs()",
                "val filters = __h.filters()",
                "",
                // --- metadata ----------------------------------------------------------------
                "fun name(value: String) { __h.name(value) }",
                "fun description(value: String) { __h.description(value) }",
                "fun key(value: Int) { __h.key(value) }",
                "fun requireApi(version: Int) { __h.requireApi(version) }",
                "",
                // --- lifecycle ---------------------------------------------------------------
                "fun onEnable(body: () -> Unit) { __h.onEnable(Runnable { body() }) }",
                "fun onDisable(body: () -> Unit) { __h.onDisable(Runnable { body() }) }",
                "fun onUnload(body: () -> Unit) { __h.onUnload(Runnable { body() }) }",
                "",
                // --- state -------------------------------------------------------------------
                "var enabled: Boolean",
                "    get() = __h.isEnabled()",
                "    set(value) { __h.setEnabled(value) }",
                "val inGame: Boolean get() = __h.inGame()",
                "inline fun whenInGame(body: () -> Unit) { if (__h.inGame()) body() }",
                "",
                // --- events ------------------------------------------------------------------
                "inline fun <reified T : Any> on(",
                "    priority: Int = Priority.NORMAL,",
                "    ignoreCancelled: Boolean = false,",
                "    noinline body: (T) -> Unit",
                "): ScriptSubscription =",
                "    __h.on(T::class.java, priority, ignoreCancelled, java.util.function.Consumer { body(it) })",
                "",
                // --- settings ----------------------------------------------------------------
                "fun checkBox(name: String, default: Boolean = false) = __h.checkBox(null, name, default)",
                "fun checkBox(parent: ScriptSettings.Check?, name: String, default: Boolean = false) = __h.checkBox(parent, name, default)",
                "fun slider(name: String, default: Float, min: Float, max: Float, step: Float = 0.01f) = __h.slider(null, name, default, min, max, step)",
                "fun slider(parent: ScriptSettings.Check?, name: String, default: Float, min: Float, max: Float, step: Float = 0.01f) = __h.slider(parent, name, default, min, max, step)",
                "fun slider(name: String, default: Int, min: Int, max: Int) = __h.sliderInt(null, name, default, min, max)",
                "fun slider(parent: ScriptSettings.Check?, name: String, default: Int, min: Int, max: Int) = __h.sliderInt(parent, name, default, min, max)",
                "fun rangeSlider(name: String, from: Float, to: Float, min: Float, max: Float, step: Float = 0.01f) = __h.rangeSlider(null, name, from, to, min, max, step)",
                "fun rangeSlider(parent: ScriptSettings.Check?, name: String, from: Float, to: Float, min: Float, max: Float, step: Float = 0.01f) = __h.rangeSlider(parent, name, from, to, min, max, step)",
                "fun input(name: String, default: String = \"\") = __h.input(null, name, default)",
                "fun input(parent: ScriptSettings.Check?, name: String, default: String = \"\") = __h.input(parent, name, default)",
                "fun selectable(name: String, vararg options: String, selected: String? = null) = __h.selectable(null, name, options.toList(), selected)",
                "fun combo(name: String, vararg options: String, selected: List<String> = emptyList()) = __h.combo(null, name, options.toList(), selected)",
                "fun colorPicker(name: String, default: Int) = __h.colorPicker(null, name, default)",
                "fun colorPicker(parent: ScriptSettings.Check?, name: String, default: Int) = __h.colorPicker(parent, name, default)",
                "fun hotkey(name: String, default: Int, body: () -> Unit) = __h.hotkey(name, default, Runnable { body() })",
                "fun button(name: String, body: () -> Unit) = __h.button(name, Runnable { body() })",
                "",
                // --- property delegates so `val x by slider(...)` works ----------------------
                "operator fun ScriptSettings.Check.getValue(t: Any?, p: Any?): Boolean = value()",
                "operator fun ScriptSettings.Slide.getValue(t: Any?, p: Any?): Float = value()",
                "operator fun ScriptSettings.Input.getValue(t: Any?, p: Any?): String = value()",
                "operator fun ScriptSettings.Select.getValue(t: Any?, p: Any?): String = value()",
                "operator fun ScriptSettings.Combo.getValue(t: Any?, p: Any?): List<String> = value()",
                "operator fun ScriptSettings.Color.getValue(t: Any?, p: Any?): Int = value()",
                "operator fun ScriptSettings.Range.getValue(t: Any?, p: Any?): ClosedFloatingPointRange<Float> = from()..to()",
                "",
                // --- timers and tasks --------------------------------------------------------
                "fun timer() = ScriptTimer()",
                "fun nextTick(body: () -> Unit) = __h.afterTicks(0, Runnable { body() })",
                "fun afterTicks(ticks: Int, body: () -> Unit) = __h.afterTicks(ticks, Runnable { body() })",
                "fun everyTicks(ticks: Int, body: () -> Unit) = __h.everyTicks(ticks, Runnable { body() })",
                "fun onClientThread(body: () -> Unit) { __h.onClientThread(Runnable { body() }) }",
                "",
                // --- messages ----------------------------------------------------------------
                "fun notify(message: Any?, kind: NotifyKind = NotifyKind.OK) { __h.notify(message, kind) }",
                "",
                // --- storage and commands ----------------------------------------------------
                "fun config(name: String) = __h.config(name)",
                // runs { } and sub("x") { } take the argument context as their receiver so that
                // arg(0), reply(...) and friends read as bare calls, as the docs show.
                "fun ScriptCommand.Builder.runs(body: ScriptCommand.Context.() -> Unit) { this.withRuns(java.util.function.Consumer { it.body() }) }",
                "fun ScriptCommand.Builder.sub(name: String, body: ScriptCommand.Builder.() -> Unit) { this.withSub(name, java.util.function.Consumer { it.body() }) }",
                "fun ScriptCommand.Builder.completes(index: Int, options: () -> List<String>) { this.withCompletions(index, java.util.function.Supplier { options() }) }",
                "fun command(name: String, body: ScriptCommand.Builder.() -> Unit) { __h.command(name, java.util.function.Consumer { it.body() }) }",
                "",
                // --- misc --------------------------------------------------------------------
                "fun shader(name: String, fragmentSource: String, vertexSource: String? = null) = __h.shader(name, fragmentSource, vertexSource)",
                ""
        );
        SOURCE = s + "\n";
        LINE_COUNT = countLines(SOURCE);
    }

    private static int countLines(String s) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') n++;
        }
        return n;
    }
}
