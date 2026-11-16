package net.skycore.client.ac;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.skycore.client.module.movement.SpeedModule;
import net.skycore.client.module.movement.SpeedV2Module;
import net.skycore.client.module.movement.SpeedV3Module;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;

/**
 * Подбор стиля обхода под найденный античит.
 *
 * Профиль — это конкретные значения настроек Speedv3 (конверт предсказания),
 * плюс рекомендация режима для обычного Speed. Ничего не «включает молча»:
 * вызывается только через .ac auto / .ac apply <family>.
 */
public final class AutoBypass {
   public static final AutoBypass INSTANCE = new AutoBypass();

   private AutoBypass() {
   }

   public String applyAuto() {
      List<AntiCheatDetector.AcGuess> guesses = AntiCheatDetector.INSTANCE.guesses();
      AcFamily family = guesses.isEmpty() ? AcFamily.GRIM : guesses.get(0).family();
      return this.apply(family);
   }

   public String apply(AcFamily family) {
      List<String> applied = new ArrayList<>();
      switch (family) {
         case MATRIX, ALOHA -> {
            this.num(applied, "Air Factor", 1.03);
            this.num(applied, "Ground Factor", 1.02);
            this.num(applied, "Offset", 0.0002);
            this.num(applied, "Momentum", 1.0);
            this.num(applied, "Strictness", 0.0011);
            this.flag(applied, "Micro Jitter", false);
            this.num(applied, "First Air Boost", 1.0);
         }
         case VULCAN, WATCHDOG -> {
            this.num(applied, "Air Factor", 0.95);
            this.num(applied, "Ground Factor", 0.98);
            this.num(applied, "Offset", 0.00035);
            this.num(applied, "Momentum", 0.95);
            this.num(applied, "Strictness", 0.0007);
            this.num(applied, "Bhop Boost", 0.98);
            this.num(applied, "First Air Boost", 0.99);
            this.flag(applied, "Micro Jitter", true);
            this.num(applied, "Jitter Strength", 0.00002);
         }
         case INTAVE -> {
            this.num(applied, "Air Factor", 0.9);
            this.num(applied, "Ground Factor", 0.95);
            this.num(applied, "Momentum", 0.97);
            this.num(applied, "Strictness", 0.0006);
            this.num(applied, "First Air Boost", 1.0);
            this.flag(applied, "Micro Jitter", false);
         }
         case VERUS, HYLEX -> {
            this.num(applied, "Strictness", 0.0006);
            this.num(applied, "Momentum", 1.0);
            this.flag(applied, "Micro Jitter", true);
            this.num(applied, "Jitter Strength", 0.00003);
         }
         case SPARTAN -> {
            this.num(applied, "Strictness", 0.0012);
            this.num(applied, "First Air Boost", 1.03);
            this.num(applied, "Bhop Boost", 1.02);
            this.num(applied, "Momentum", 1.0);
         }
         case NCP, AAC -> {
            this.num(applied, "Air Factor", 0.98);
            this.num(applied, "Ground Factor", 0.95);
            this.num(applied, "Momentum", 0.95);
            this.num(applied, "Strictness", 0.0011);
            this.num(applied, "Offset", 0.0003);
         }
         case KARHU, ERRATA, NEGATIVITY, THEMIS, FIONA, KAURI -> {
            this.num(applied, "Strictness", 0.0011);
            this.num(applied, "Momentum", 1.0);
            this.num(applied, "Air Factor", 1.0);
            this.flag(applied, "Micro Jitter", true);
            this.num(applied, "Jitter Strength", 0.000015);
         }
         case POLAR -> {
            this.num(applied, "Strictness", 0.0008);
            this.num(applied, "Air Factor", 1.0);
            this.num(applied, "First Air Boost", 1.0);
            this.num(applied, "Bhop Boost", 1.0);
            this.flag(applied, "Micro Jitter", false);
         }
         default -> {
            this.num(applied, "Air Factor", 1.0);
            this.num(applied, "Ground Factor", 1.0);
            this.num(applied, "Offset", 0.00045);
            this.num(applied, "Momentum", 1.0);
            this.num(applied, "Strictness", 0.00085);
            this.num(applied, "Input Scale", 1.0);
            this.num(applied, "Friction Mult", 1.0);
            this.num(applied, "Bhop Boost", 1.0);
            this.num(applied, "First Air Boost", 1.0);
            this.flag(applied, "Micro Jitter", false);
         }
      }

      String modeHint = this.modeHint(family);
      String result = "Профиль " + family.display() + ": "
         + (applied.isEmpty() ? "Speedv3 не найден в модулях" : String.join(", ", applied))
         + " | Speed-режим: " + modeHint;
      return result;
   }

   private String modeHint(AcFamily family) {
      return switch (family) {
         case GRIM, KARHU, ERRATA, NEGATIVITY, THEMIS, FIONA, KAURI -> "Speedv3 (конверт Grim)";
         case MATRIX, ALOHA -> "Nuclear [Matrix]";
         case VULCAN, WATCHDOG -> "Swerve [Множитель] 1.10 (таймер <= 1.05)";
         case INTAVE -> "Private [NCP]";
         case VERUS, HYLEX -> "Speedv3 + Micro Jitter";
         case SPARTAN -> "Private [Spartan]";
         case NCP, AAC -> "Private [AAC5]";
         case POLAR -> "Nuclear [MetaHvH] без бафа";
         default -> "Speedv3";
      };
   }

   private void num(List<String> applied, String settingName, double value) {
      Setting<?> setting = this.find(settingName);
      if (setting instanceof NumberSetting numberSetting) {
         numberSetting.set(value);
         applied.add(settingName + "=" + String.format(Locale.ROOT, "%.6f", value));
      }
   }

   private void flag(List<String> applied, String settingName, boolean value) {
      Setting<?> setting = this.find(settingName);
      if (setting instanceof BooleanSetting booleanSetting) {
         booleanSetting.set(value);
         applied.add(settingName + (value ? "=on" : "=off"));
      }
   }

   private Setting<?> find(String settingName) {
      for (Setting<?> setting : SpeedV3Module.INSTANCE.getSettings()) {
         if (setting.getName().equals(settingName)) {
            return setting;
         }
      }

      return null;
   }

   /** Сброс всех настроек Speedv3 к дефолтным значениям. */
   public String resetV3() {
      for (Setting<?> setting : SpeedV3Module.INSTANCE.getSettings()) {
         setting.reset();
      }

      return "Настройки Speedv3 сброшены к значениям по умолчанию";
   }

   public String currentSetup() {
      StringBuilder builder = new StringBuilder();
      builder.append("Speedv3: ").append(SpeedV3Module.INSTANCE.isEnabled() ? "вкл" : "выкл").append('\n');
      for (Setting<?> setting : SpeedV3Module.INSTANCE.getSettings()) {
         builder.append("  ").append(setting.getName()).append(" = ").append(String.valueOf(setting.get())).append('\n');
      }

      builder.append("Speed: ").append(SpeedModule.INSTANCE.isEnabled() ? "вкл" : "выкл")
         .append(" (").append(describeMode(SpeedModule.INSTANCE)).append(')')
         .append(", Speedv2: ").append(SpeedV2Module.INSTANCE.isEnabled() ? "вкл" : "выкл").append('\n');
      return builder.toString();
   }

   private static String describeMode(net.skycore.client.module.ClientModule module) {
      String mode = SpeedTelemetry.modeName(module);
      return mode == null ? "режим не найден" : mode;
   }

   /**
    * Проверка текущего состояния Speedv3: настройки, которые гарантированно режут скорость
    * или ломают обход. Возвращает список предупреждений (пусто — всё в норме).
    */
   public List<String> diagnose() {
      List<String> warnings = new ArrayList<>();
      if (!SpeedV3Module.INSTANCE.isEnabled()) {
         warnings.add("Speedv3 выключен — телеметрия показывает только другие модули");
      }

      if (this.numValue("Jump Factor", 1.0) < 0.99) {
         warnings.add("Jump Factor " + this.fmt("Jump Factor") + " — прыжок слабее ванильного, высота и скорость режутся");
      }

      if (this.numValue("Sprint Mult", 1.0) < 0.995) {
         warnings.add("Sprint Mult " + this.fmt("Sprint Mult") + " — гасит спринт-буст");
      }

      if (this.numValue("Bhop Boost", 1.0) < 0.995) {
         warnings.add("Bhop Boost " + this.fmt("Bhop Boost") + " — первый воздушный тик не усиливается");
      }

      if (this.numValue("First Air Boost", 1.0) < 0.995) {
         warnings.add("First Air Boost " + this.fmt("First Air Boost") + " — срез скорости на выходе с земли");
      }

      if (!this.boolValue("Exact Attributes", true)) {
         warnings.add("Exact Attributes выкл — атрибуты движения считаются грубо, расхождение с предсказанием");
      }

      if (!this.boolValue("Smart Bhop", true)) {
         warnings.add("Smart Bhop выкл — нет ровного цикла прыжков");
      }

      if (!this.boolValue("Yaw Damp", true)) {
         warnings.add("Yaw Damp выкл — резкий поворот даёт скачок направления");
      }

      if (!this.boolValue("Micro Jitter", false) && this.numValue("Jitter Strength", 0.0) <= 0.0) {
         warnings.add("джиттера нет — повторяемые значения легко ловятся статистикой");
      }

      if (this.numValue("Offset", 0.00045) > 0.0008) {
         warnings.add("Offset " + this.fmt("Offset") + " — шире допуска Grim, будут сеты");
      }

      if (this.numValue("Strictness", 0.00085) > 0.0012) {
         warnings.add("Strictness " + this.fmt("Strictness") + " — слишком мягкий конверт для Grim");
      }

      if (warnings.isEmpty()) {
         warnings.add("настройки Speedv3 выглядят рабочими");
      }

      return warnings;
   }

   private double numValue(String settingName, double fallback) {
      Setting<?> setting = this.find(settingName);
      if (setting instanceof NumberSetting numberSetting) {
         Double value = numberSetting.get();
         return value == null ? fallback : value;
      }

      return fallback;
   }

   private boolean boolValue(String settingName, boolean fallback) {
      Setting<?> setting = this.find(settingName);
      if (setting instanceof BooleanSetting booleanSetting) {
         Boolean value = booleanSetting.get();
         return value == null ? fallback : value;
      }

      return fallback;
   }

   private String fmt(String settingName) {
      return String.format(Locale.ROOT, "%.5f", this.numValue(settingName, 0.0));
   }
}
