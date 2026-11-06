package net.skycore.client.ac;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.player.LocalPlayer;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.movement.SpeedModule;
import net.skycore.client.module.movement.SpeedV2Module;
import net.skycore.client.module.movement.SpeedV3Module;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.Setting;

/**
 * Телеметрия спидов: для каждого включённого мода/режима считает фактическую скорость,
 * долю воздуха, сеты и подозрительные строки чата.
 *
 * Важно: средняя скорость «за всё время» бесполезна — пока стоишь, она падает к нулю.
 * Поэтому отдельно считается скорость В ДВИЖЕНИИ (тики быстрее 1 bps) и отношение
 * к базлайну — скорости без включённых модулей.
 *
 * Скачки больше 1.2 блока за тик (сет, телепорт, лаг-компенсация) не портят avg/max
 * и считаются отдельным счётчиком.
 */
public final class SpeedTelemetry {
   public static final SpeedTelemetry INSTANCE = new SpeedTelemetry();

   private static final double TELEPORT_DISTANCE = 1.2;
   private static final double MOVING_BPS = 1.0;
   private static final int MIN_REPORT_TICKS = 10;

   private final List<Segment> segments = new ArrayList<>();
   private Segment current;
   private double lastX;
   private double lastZ;
   private boolean hasLast;
   private boolean initialized;
   private int switches;

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
      this.switches = 0;
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
         if (this.current != null) {
            this.switches++;
         }

         this.current = new Segment(label);
         this.segments.add(this.current);
      }

      this.current.ticks++;
      if (!player.onGround()) {
         this.current.airTicks++;
      }

      if (distance > TELEPORT_DISTANCE) {
         this.current.teleports++;
         return;
      }

      double bps = distance * 20.0;
      this.current.sumBps += bps;
      if (bps > this.current.maxBps) {
         this.current.maxBps = bps;
      }

      if (bps >= MOVING_BPS) {
         this.current.movingTicks++;
         this.current.movingSum += bps;
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
         String mode = modeName(SpeedModule.INSTANCE);
         return "Speed > " + (mode == null ? "?" : mode);
      }

      return "-";
   }

   /** Имя выбранного режима модуля (первый ModeSetting в списке настроек). */
   public static String modeName(ClientModule module) {
      for (Setting<?> setting : module.getSettings()) {
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

      double baselineMove = 0.0;
      long baselineTicks = 0L;
      double totalMove = 0.0;
      long totalMoveTicks = 0L;
      int totalTeleports = 0;
      int totalSetbacks = 0;
      int totalFlags = 0;

      for (Segment segment : this.segments) {
         totalMove += segment.movingSum;
         totalMoveTicks += segment.movingTicks;
         totalTeleports += segment.teleports;
         totalSetbacks += segment.setbacks;
         totalFlags += segment.flags;
         if ("-".equals(segment.label)) {
            baselineMove += segment.movingSum;
            baselineTicks += segment.movingTicks;
         }
      }

      double baseline = baselineTicks == 0L ? 0.0 : baselineMove / (double)baselineTicks;

      for (Segment segment : this.segments) {
         if (segment.ticks < MIN_REPORT_TICKS && segment.setbacks == 0 && segment.flags == 0) {
            continue;
         }

         double moveAvg = segment.movingTicks == 0L ? 0.0 : segment.movingSum / (double)segment.movingTicks;
         double air = segment.ticks == 0L ? 0.0 : (double)segment.airTicks * 100.0 / (double)segment.ticks;
         builder.append(segment.label)
            .append(" | тиков ").append(segment.ticks)
            .append(" | в движении ").append(String.format(Locale.ROOT, "%.2f", moveAvg)).append(" bps")
            .append(" (тиков ").append(segment.movingTicks).append(")")
            .append(" | max ").append(String.format(Locale.ROOT, "%.2f", segment.maxBps)).append(" bps")
            .append(" | воздух ").append(String.format(Locale.ROOT, "%.0f", air)).append("%")
            .append(" | сеты ").append(segment.setbacks)
            .append(" | флаги ").append(segment.flags);
         if (segment.teleports > 0) {
            builder.append(" | скачков ").append(segment.teleports);
         }

         if (baseline > MOVING_BPS && !"-".equals(segment.label) && moveAvg > 0.0) {
            builder.append(" | x").append(String.format(Locale.ROOT, "%.2f", moveAvg / baseline)).append(" к базлайну");
         }

         builder.append('\n');
      }

      double totalAvg = totalMoveTicks == 0L ? 0.0 : totalMove / (double)totalMoveTicks;
      builder.append("ИТОГО в движении ").append(String.format(Locale.ROOT, "%.2f", totalAvg)).append(" bps")
         .append(" | сеты ").append(totalSetbacks)
         .append(" | флаги ").append(totalFlags)
         .append(" | скачков ").append(totalTeleports)
         .append(" | смен режима ").append(this.switches)
         .append('\n');

      if (baseline > MOVING_BPS) {
         builder.append("базлайн без модуля: ").append(String.format(Locale.ROOT, "%.2f", baseline)).append(" bps")
            .append(" (ванильный спринт ~5.6-6.1)\n");
      } else {
         builder.append("базлайн не измерен: выключите Speed и пройдитесь 5 секунд\n");
      }

      if (totalSetbacks == 0 && totalAvg > baseline * 1.4 && baseline > MOVING_BPS) {
         builder.append("вывод: скорость выше базлайна в ")
            .append(String.format(Locale.ROOT, "%.1f", totalAvg / baseline))
            .append("x и сетов нет — на этом сервере это либо не флагает, либо AC не видит движение\n");
      }

      if (totalSetbacks > 0) {
         builder.append("сеты есть: смотрите настройки конверта (.ac setup) и профиль (.ac auto)\n");
      }

      if (totalTicksAll() > 20 && this.switches > totalTicksAll() / 100) {
         builder.append("внимание: режим переключался очень часто (")
            .append(this.switches)
            .append(") — сравнение режимов получится некорректным\n");
      }

      return builder.toString();
   }

   private long totalTicksAll() {
      long total = 0L;
      for (Segment segment : this.segments) {
         total += segment.ticks;
      }

      return total;
   }

   private static final class Segment {
      private final String label;
      private long ticks;
      private long airTicks;
      private long movingTicks;
      private double sumBps;
      private double movingSum;
      private double maxBps;
      private int setbacks;
      private int flags;
      private int teleports;

      private Segment(String label) {
         this.label = label;
      }
   }
}
