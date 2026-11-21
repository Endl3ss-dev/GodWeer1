package net.skycore.client.module.movement.training;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Input;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.RotationController;
import net.skycore.client.combat.RotationManager;
import net.skycore.client.combat.ServerRotationManager;
import net.skycore.client.event.BlockUseObservationEvent;
import net.skycore.client.event.ClientRenderEvent;
import net.skycore.client.event.ClientTickEvent;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.movement.ScaffoldModule;
import net.skycore.client.module.movement.ScaffoldCoachModule;
import net.skycore.client.util.ResetSpeedHelper;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.util.SendDonationHelper;

/** Client-thread capture, bounded queue, background JSONL writer and offline analysis. */
public final class ScaffoldTrainingRecorder {
   public static final ScaffoldTrainingRecorder INSTANCE = new ScaffoldTrainingRecorder();
   private static final Gson JSON = new Gson();
   private static final Gson PRETTY = new GsonBuilder().setPrettyPrinting().create();
   private static final int MAX_SAMPLES = 60_000, MAX_USES = 10_000;
   private static final long MAX_NANOS = TimeUnit.MINUTES.toNanos(20);
   private Session active;
   private boolean initialized;
   private boolean finalizing;
   private Session finishing;
   private ScaffoldTrainingAnalysis.Report pending;
   private boolean reviewed;
   private Input input = Input.EMPTY;
   private long clientTick;
   private int crouchAge;
   private Map<String, Double> backup;
   private Map<String, Double> applied;

   private ScaffoldTrainingRecorder() { }

   public void initialize() {
      if (this.initialized) return;
      this.initialized = true;
      SkycoreClient.events().subscribe(PlayerInputEvent.class, event -> {
         if (this.active != null) this.input = event.keyPresses();
      });
      SkycoreClient.events().subscribe(ClientTickEvent.class, event -> this.tick(event.client()));
      SkycoreClient.events().subscribe(ClientRenderEvent.class, event -> this.sample(event.client()));
      SkycoreClient.events().subscribe(BlockUseObservationEvent.class, this::observe);
      ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
         ScaffoldCoachModule.INSTANCE.setEnabled(false);
         Session session = this.active != null ? this.active : this.finishing;
         this.stop("client-stopping");
         if (session != null) {
            try {
               session.thread.join(5000);
               if (session.thread.isAlive()) SkycoreClient.LOGGER.warn("Scaffold writer shutdown timed out; recording may be partial");
            } catch (InterruptedException failure) {
               Thread.currentThread().interrupt();
               SkycoreClient.LOGGER.warn("Interrupted while closing Scaffold recording", failure);
            }
         }
      });
   }

   private String contamination(boolean allowCoach) {
      for (ClientModule module : SkycoreClient.modules().enabled()) {
         if (allowCoach && module == ScaffoldCoachModule.INSTANCE) continue;
         if (module.affectsGameplay()) return "Disable gameplay modules before recording: " + module.getName();
      }
      if (!allowCoach && Math.abs(ResetSpeedHelper.getSpeed() - 1) > 0.001F) return "Normal recording requires normal client time";
      if (RotationController.isActive() || RotationManager.INSTANCE.isRotating() || ServerRotationManager.INSTANCE.isRotating()) {
         return "Wait until automatic rotations stop";
      }
      return null;
   }

   public void start(String mode) throws IOException {
      Minecraft mc = Minecraft.getInstance();
      if (this.active != null || this.finalizing) throw new IllegalStateException("A recording is active or still being saved");
      if (mc.player == null || mc.level == null || mc.getSingleplayerServer() == null) {
         throw new IllegalStateException("Recording is allowed only in your singleplayer world");
      }
      String problem = this.contamination("assisted".equals(mode));
      if (problem != null) throw new IllegalStateException(problem);
      if ("assisted".equals(mode) && !ScaffoldCoachModule.INSTANCE.isEnabled()) {
         throw new IllegalStateException("First enable .scafftrain coach on at 20 TPS");
      }
      float expected = "slow".equals(mode) ? 3 : 20;
      if ((!"slow".equals(mode) && !"normal".equals(mode) && !"assisted".equals(mode))
         || !Float.isFinite(mc.level.tickRateManager().tickrate())
         || Math.abs(mc.level.tickRateManager().tickrate() - expected) > 0.1F
         || mc.level.tickRateManager().isFrozen()) {
         throw new IllegalStateException("First run /tick rate " + (int)expected + " and unfreeze the world");
      }
      Path folder = Path.of("skycore", "scaffold-training").toAbsolutePath().normalize();
      // No user-controlled filenames and no writes through existing symlink parents.
      for (Path p = folder; p != null; p = p.getParent()) {
         if (Files.isSymbolicLink(p)) throw new IOException("Refusing a symlink recording directory: " + p);
      }
      Files.createDirectories(folder);
      Session session = new Session(mode, folder, mc.level, mc.player);
      this.pending = null;
      this.reviewed = false;
      this.input = Input.EMPTY;
      this.crouchAge = 0;
      this.active = session;
      session.thread.start();
      info("Recording " + mode + ": " + session.id + ". Build manually. Stop: .scafftrain stop");
   }

   private boolean valid(Minecraft mc) {
      Session s = this.active;
      if (s == null) return false;
      if (mc.level != s.world || mc.player != s.player || mc.getSingleplayerServer() == null) {
         this.stop("world-changed");
         return false;
      }
      if (System.nanoTime() - s.started > MAX_NANOS) {
         this.stop("duration-limit");
         return false;
      }
      float expected = "slow".equals(s.mode) ? 3 : 20;
      if (!Float.isFinite(mc.level.tickRateManager().tickrate())
         || Math.abs(mc.level.tickRateManager().tickrate() - expected) > 0.1F) {
         this.stop("error-rate-changed");
         return false;
      }
      String problem = this.contamination("assisted".equals(s.mode));
      if (problem != null) {
         this.stop("error-contaminated: " + problem);
         return false;
      }
      if (!Double.isFinite(mc.player.getX()) || !Double.isFinite(mc.player.getY()) || !Double.isFinite(mc.player.getZ())
         || !Float.isFinite(mc.player.getYRot()) || !Float.isFinite(mc.player.getXRot())) {
         this.stop("error-invalid-pose");
         return false;
      }
      if ("assisted".equals(s.mode) && !ScaffoldCoachModule.INSTANCE.isEnabled()) {
         this.stop("coach-disabled");
         return false;
      }
      boolean usable = !mc.isPaused() && !mc.level.tickRateManager().isFrozen() && mc.gui.screen() == null
         && GLFW.glfwGetWindowAttrib(mc.getWindow().handle(), GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE
         && mc.player.isAlive() && !mc.player.isSpectator() && !mc.player.isPassenger()
         && !mc.player.getAbilities().flying && !mc.player.isFallFlying();
      if (!usable) {
         s.gap = true;
         this.crouchAge = 0;
      }
      return usable;
   }

   private void tick(Minecraft mc) {
      this.clientTick++;
      if (this.active == null) return;
      if (!this.valid(mc)) {
         this.crouchAge = 0;
         return;
      }
      this.crouchAge = this.input.shift() ? this.crouchAge + 1 : 0;
      this.offer("tick", Map.of("nanos", System.nanoTime() - this.active.started,
         "clientTick", this.clientTick, "worldTick", mc.level.getGameTime()));
      // Also capture when HUD rendering is disabled; render sampling fills between ticks.
      this.sample(mc);
   }

   private void sample(Minecraft mc) {
      if (this.active == null || !this.valid(mc)) return;
      Session s = this.active;
      long now = System.nanoTime();
      if (now - s.lastSample < 25_000_000L) return; // at most 40 wall-clock samples/second
      if (s.samples.size() >= MAX_SAMPLES) {
         this.stop("sample-limit");
         return;
      }
      s.lastSample = now;
      var player = mc.player;
      if (s.gap) {
         s.segment++;
         s.gap = false;
      }
      ScaffoldTrainingAnalysis.Sample sample = new ScaffoldTrainingAnalysis.Sample(now - s.started,
         this.clientTick, mc.level.getGameTime(), mc.level.tickRateManager().tickrate(), s.segment,
         ResetSpeedHelper.getSpeed(), ScaffoldCoachModule.INSTANCE.isEnabled(),
         player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.onGround(),
         this.input.shift(), this.input.jump(), this.input.forward(), this.input.backward(),
         this.input.left(), this.input.right(), this.input.sprint(), player.getInventory().getSelectedSlot());
      s.samples.add(sample);
      this.offer("sample", new Frame(sample, player.getMainHandItem().getItem().toString(), ScaffoldCoachModule.INSTANCE.snapshot()));
   }

   private void observe(BlockUseObservationEvent event) {
      Minecraft mc = Minecraft.getInstance();
      if (this.active == null || !this.valid(mc)) return;
      Session s = this.active;
      if (s.attempts.size() >= MAX_USES) {
         this.stop("use-limit");
         return;
      }
      var pos = event.target();
      var point = event.hit().getLocation();
      double dx = pos.getX() + 0.5 - mc.player.getX(), dz = pos.getZ() + 0.5 - mc.player.getZ();
      double height = mc.player.getY() - pos.getY();
      boolean bridge = height >= 0.9 && height <= 3 && dx * dx + dz * dz <= 3.24
         && mc.level.getBlockState(pos).isCollisionShapeFullBlock(mc.level, pos);
      var placement = new ScaffoldTrainingAnalysis.Placement(System.nanoTime() - s.started,
         this.clientTick, pos.getX(), pos.getY(), pos.getZ(), event.hit().getDirection().name(),
         point.x, point.y, point.z, event.consumed(), event.predictedChange(), bridge,
         mc.player.onGround(), this.crouchAge);
      s.attempts.add(placement);
      this.offer("use", new UseFrame(placement, mc.player.getYRot(), mc.player.getXRot(),
         event.hand().name(), event.item()));
   }

   private void offer(String type, Object value) {
      Session s = this.active;
      if (s != null && !s.queue.offer(JSON.toJson(new Row(type, value)))) this.stop("error-queue-full");
   }

   public void stop(String reason) {
      Session s = this.active;
      if (s == null) return;
      this.active = null;
      this.finalizing = true;
      this.finishing = s;
      s.reason = reason;
      s.stopping = true;
      info("Saving recording and calculating report: " + s.id);
   }

   public void status() {
      if (this.active != null) {
         info("Recording " + this.active.mode + ": samples=" + this.active.samples.size()
            + ", attempts=" + this.active.attempts.size() + ", seconds="
            + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - this.active.started));
      } else {
         info(this.finalizing ? "Saving recording..." : "Not recording. Commands: coach on/off/slow/height/status | start slow/normal/assisted | stop | report | apply | undo");
      }
   }

   public void report() {
      if (this.pending == null) throw new IllegalStateException("No finished report in this client session; use status or finish a recording");
      var report = this.pending;
      info("Report " + report.session() + ": " + report.mode() + ", bridge placements="
         + report.bridgePlacements() + ", motion=" + report.motionSamples() + ", applicable=" + report.applicable());
      report.styleStatistics().forEach((key, value) -> info("Sequence " + key + " = " + value));
      report.proposedSettings().forEach((key, value) -> info(key + " -> " + value));
      report.warnings().forEach(ScaffoldTrainingRecorder::info);
      this.reviewed = true;
      info(report.applicable() ? "Confirm these proposals: .scafftrain apply (does NOT enable Scaffold)"
         : "assisted".equals(report.mode()) ? "Assisted sequence measurements saved; human rotation calibration requires a separate unassisted normal recording."
         : "Not applicable. Record normal construction at 20 TPS with enough ground placements and rotations.");
   }

   public void apply() {
      this.requireIdleScaffold();
      if (this.pending == null || !this.pending.applicable() || !this.reviewed) {
         throw new IllegalStateException("First finish a valid normal recording and review .scafftrain report");
      }
      Map<String, NumberSetting> settings = this.settings();
      Map<String, Double> previous = new LinkedHashMap<>();
      // Validate the full proposal before mutating anything.
      this.pending.proposedSettings().forEach((key, value) -> {
         NumberSetting setting = settings.get(key);
         if (setting == null || !Double.isFinite(value) || value < setting.getMin() || value > setting.getMax()) {
            throw new IllegalStateException("Invalid proposal: " + key);
         }
         previous.put(key, setting.get());
      });
      this.pending.proposedSettings().forEach((key, value) -> settings.get(key).set(value));
      this.backup = previous;
      this.applied = this.pending.proposedSettings();
      this.reviewed = false;
      if (SkycoreClient.config() != null) SkycoreClient.config().save();
      info("Profile applied; Scaffold remains OFF. Undo before restarting: .scafftrain undo");
   }

   public void undo() {
      this.requireIdleScaffold();
      if (this.backup == null) throw new IllegalStateException("No applied profile to undo in this client session");
      Map<String, NumberSetting> settings = this.settings();
      this.applied.forEach((key, value) -> {
         if (!settings.get(key).get().equals(value)) throw new IllegalStateException("Setting changed manually: " + key + "; undo cancelled");
      });
      this.backup.forEach((key, value) -> settings.get(key).set(value));
      this.backup = this.applied = null;
      if (SkycoreClient.config() != null) SkycoreClient.config().save();
      info("Previous settings restored");
   }

   private void requireIdleScaffold() {
      if (this.active != null || this.finalizing) throw new IllegalStateException("Stop recording and wait for saving first");
      if (SkycoreClient.modules().get(ScaffoldModule.class).orElseThrow().isEnabled()) {
         throw new IllegalStateException("Disable Scaffold before applying or undoing a profile");
      }
   }

   private Map<String, NumberSetting> settings() {
      List<String> allowed = List.of("Turn Speed", "Pitch Speed", "Return Speed", "Sneak Delay", "Release Delay", "Jump Turn Speed");
      Map<String, NumberSetting> result = new LinkedHashMap<>();
      for (var setting : SkycoreClient.modules().get(ScaffoldModule.class).orElseThrow().getSettings()) {
         if (setting instanceof NumberSetting number && allowed.contains(setting.getName())) result.put(setting.getName(), number);
      }
      return result;
   }

   private static void info(String message) {
      SendDonationHelper.displayInfo("Scaffold training: " + message);
   }

   private record Row(String type, Object data) { }
   private record Frame(ScaffoldTrainingAnalysis.Sample state, String mainHandItem, ScaffoldCoachModule.State coach) { }
   private record UseFrame(ScaffoldTrainingAnalysis.Placement observation, float yaw, float pitch,
                           String hand, String item) { }

   private final class Session implements Runnable {
      final String id = UUID.randomUUID().toString();
      final String mode;
      final Path folder;
      final Object world, player;
      final long started = System.nanoTime();
      final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(4096);
      final List<ScaffoldTrainingAnalysis.Sample> samples = new ArrayList<>();
      final List<ScaffoldTrainingAnalysis.Placement> attempts = new ArrayList<>();
      final Thread thread;
      long lastSample;
      int segment;
      boolean gap;
      String reason = "manual";
      volatile boolean stopping;

      Session(String mode, Path folder, Object world, Object player) {
         this.mode = mode;
         this.folder = folder;
         this.world = world;
         this.player = player;
         this.thread = new Thread(this, "SkyCore-Scaffold-Recording");
         this.thread.setDaemon(true);
      }

      @Override
      public void run() {
         try (BufferedWriter writer = Files.newBufferedWriter(this.folder.resolve(this.id + ".jsonl"),
            StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            writer.write(JSON.toJson(new Row("session", Map.of("schema", 2, "id", this.id,
               "mode", this.mode, "minecraft", "26.2", "clock", "relative monotonic nanoseconds",
               "placementAuthority", "local prediction only"))));
            writer.newLine();
            while (!this.stopping || !this.queue.isEmpty()) {
               String line = this.queue.poll(200, TimeUnit.MILLISECONDS);
               if (line != null) {
                  writer.write(line);
                  writer.newLine();
               }
            }
            var result = ScaffoldTrainingAnalysis.analyze(this.id, this.mode, this.reason, this.samples, this.attempts);
            writer.write(JSON.toJson(new Row("end", Map.of("reason", this.reason, "samples", this.samples.size(), "complete", true))));
            writer.newLine();
            writer.flush();
            Files.writeString(this.folder.resolve(this.id + "-report.json"), PRETTY.toJson(result),
               StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
            SkycoreClient.LOGGER.info("Scaffold recording saved: {}", this.folder.resolve(this.id + ".jsonl"));
            Minecraft.getInstance().execute(() -> {
               ScaffoldTrainingRecorder.this.pending = result;
               ScaffoldTrainingRecorder.this.reviewed = false;
               ScaffoldTrainingRecorder.this.finalizing = false;
               ScaffoldTrainingRecorder.this.finishing = null;
               info("Saved " + this.id + ". Review: .scafftrain report. Files: " + this.folder);
            });
         } catch (Exception failure) {
            if (failure instanceof InterruptedException) Thread.currentThread().interrupt();
            SkycoreClient.LOGGER.error("Scaffold recording failed; partial file retained", failure);
            Minecraft.getInstance().execute(() -> {
               if (ScaffoldTrainingRecorder.this.active == this) ScaffoldTrainingRecorder.this.active = null;
               ScaffoldTrainingRecorder.this.finalizing = false;
               ScaffoldTrainingRecorder.this.finishing = null;
               ScaffoldTrainingRecorder.this.pending = null;
               info("Recording/save failed; partial file may remain. See client log: " + failure.getMessage());
            });
         }
      }
   }
}
