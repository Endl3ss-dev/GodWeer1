package net.skycore.client.ac;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.player.LocalPlayer;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.module.movement.SpeedModule;
import net.skycore.client.module.movement.SpeedV2Module;
import net.skycore.client.module.movement.SpeedV3Module;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.Setting;

/**
 * Телеметрия спидов: для каждого включённого мода/режима считает фактическую скорость,
 * долю воздуха, сеты и подозрительные строки чата. Нужна, чтобы сравнивать режимы
 * в одинаковых условиях и видеть, где именно сервер возвращает игрока.
 */
public final class SpeedTelemetry {
   public static final SpeedTelemetry INSTANCE = new SpeedTelemetry();

   private final List<Segment> segments = new ArrayList<>();
   private Segment current;
   private double lastX;
   private double lastZ;
   private boolean hasLast;
   private boolean initialized;

   private SpeedTelemetry() {
   }

   public void init() {
      if (this.initialized) {
         return;
      }

      this.initialized = true;
      SkycoreClient.events().subscribe(PlayerTickEndEvent.class, event -> this.tick(event.player()));
   }

   public synchronized void reset() {
      this.segments.clear();
      this.current = null;
      this.hasLast = false;
   }

   private synchronized void tick(LocalPlayer player) {
      if (player == null) {
         return;
      }

      String label = this.label();
      double x = player.getX();
      double z = player.getZ();
      double distance = this.hasLast ? Math.sqrt((x - this.lastX) * (x - this.lastX) + (z - this.lastZ) * (z - this.lastZ)) : 0.0;
      this.lastX = x;
      this.lastZ = z;
      this.hasLast = true;

      if (this.current == null || !this.current.label.equals(label)) {
         this.current = new Segment(label);
         this.segments.add(this.current);
      }

      this.current.ticks++;
      if (!player.onGround()) {
         this.current.airTicks++;
      }

      double bps = distance * 20.0;
      this.current.sumBps += bps;
      if (bps > this.current.maxBps) {
         this.current.maxBps = bps;
      }
   }

   private String label() {
      if (SpeedV3Module.INSTANCE.isEnabled()) {
         return "Speedv3 (MetaHvH+Grim)";
      }

      if (SpeedV2Module.INSTANCE.isEnabled()) {
         return "Speedv2 (Releon RW)";
      }

      if (SpeedModule.INSTANCE.isEnabled()) {
         String mode = this.modeOf(SpeedModule.INSTANCE.getSettings());
         return "Speed > " + (mode == null ? "?" : mode);
      }

      return "-";
   }

   private String modeOf(List<Setting<?>> settings) {
      for (Setting<?> setting : settings) {
         if (setting instanceof ModeSetting modeSetting) {
            Object value = modeSetting.get();
            return value == null ? null : value.toString();
         }
      }

      return null;
   }

   public synchronized void noteSetback() {
      if (this.current != null) {
         this.current.setbacks++;
      }
   }

   public synchronized void noteFlag(String text) {
      if (this.current != null) {
         this.current.flags++;
      }
   }

   public synchronized String report() {
      StringBuilder builder = new StringBuilder();
      if (this.segments.isEmpty()) {
         builder.append("нет данных: включите Speed-мод и подвигайтесь\n");
         return builder.toString();
      }

      for (Segment segment : this.segments) {
         double avg = segment.ticks == 0 ? 0.0 : segment.sumBps / (double)segment.ticks;
         double air = segment.ticks == 0 ? 0.0 : (double)segment.airTicks * 100.0 / (double)segment.ticks;
         builder.append(segment.label)
            .append(" | тиков ").append(segment.ticks)
            .append(" | avg ").append(String.format(Locale.ROOT, "%.2f", avg)).append(" bps")
            .append(" | max ").append(String.format(Locale.ROOT, "%.2f", segment.maxBps)).append(" bps")
            .append(" | воздух ").append(String.format(Locale.ROOT, "%.0f", air)).append("%")
            .append(" | сеты ").append(segment.setbacks)
            .append(" | флаги ").append(segment.flags)
            .append('\n');
      }

      return builder.toString();
   }

   private static final class Segment {
      private final String label;
      private long ticks;
      private long airTicks;
      private double sumBps;
      private double maxBps;
      private int setbacks;
      private int flags;

      private Segment(String label) {
         this.label = label;
      }
   }
}
