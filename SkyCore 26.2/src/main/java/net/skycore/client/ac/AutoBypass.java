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

   public String currentSetup() {
      StringBuilder builder = new StringBuilder();
      builder.append("Speedv3: ").append(SpeedV3Module.INSTANCE.isEnabled() ? "вкл" : "выкл").append('\n');
      for (Setting<?> setting : SpeedV3Module.INSTANCE.getSettings()) {
         builder.append("  ").append(setting.getName()).append(" = ").append(String.valueOf(setting.get())).append('\n');
      }

      builder.append("Speed: ").append(SpeedModule.INSTANCE.isEnabled() ? "вкл" : "выкл")
         .append(", Speedv2: ").append(SpeedV2Module.INSTANCE.isEnabled() ? "вкл" : "выкл").append('\n');
      return builder.toString();
   }
}
