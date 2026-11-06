package net.skycore.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.skycore.client.ac.AcFamily;
import net.skycore.client.ac.AntiCheatDetector;
import net.skycore.client.ac.AutoBypass;
import net.skycore.client.ac.SpeedTelemetry;

/**
 * .ac         — краткая сводка: что за античит и сколько сетов
 * .ac report  — полный отчёт в чат
 * .ac speed   — телеметрия режимов спидов (что реально даёт скорость)
 * .ac save    — полный отчёт в файл run/skycore-ac-report.txt и в консоль
 * .ac reset   — обнулить накопленные данные
 */
public final class AcCommand extends ClientCommand {
   public AcCommand() {
      super("ac");
   }

   @Override
   public void execute(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
      builder.executes(context -> this.summary());
      builder.then(literal("report").executes(context -> this.fullReport()));
      builder.then(literal("speed").executes(context -> this.speedReport()));
      builder.then(literal("save").executes(context -> this.save()));
      builder.then(literal("auto").executes(context -> this.auto()));
      builder.then(literal("setup").executes(context -> this.setup()));
      builder.then(literal("default").executes(context -> this.defaults()));
      builder.then(literal("apply").then(arg("family", StringArgumentType.word())
         .suggests((context, suggestions) -> {
            for (AcFamily family : AcFamily.values()) {
               suggestions.suggest(family.name().toLowerCase(Locale.ROOT));
            }

            return suggestions.buildFuture();
         })
         .executes(context -> this.apply(StringArgumentType.getString(context, "family")))));
      builder.then(literal("reset").executes(context -> this.reset()));
      builder.then(literal("help").executes(context -> this.help()));
   }

   private int summary() {
      AntiCheatDetector.INSTANCE.init();
      ChatMessages.displayInfo(AntiCheatDetector.INSTANCE.topGuessLine());
      ChatMessages.displayInfo("Каналов собрано: " + this.channelCount() + ", сеты за сессию: " + this.setbackCount());
      ChatMessages.displayInfo(".ac speed — сравнить режимы, .ac save — отчёт в файл");
      return 1;
   }

   private int fullReport() {
      AntiCheatDetector.INSTANCE.init();
      for (String line : AntiCheatDetector.INSTANCE.report().split("\n")) {
         ChatMessages.displayInfo(line);
      }

      return 1;
   }

   private int speedReport() {
      SpeedTelemetry.INSTANCE.init();
      for (String line : SpeedTelemetry.INSTANCE.report().split("\n")) {
         ChatMessages.displayInfo(line);
      }

      return 1;
   }

   private int save() {
      AntiCheatDetector.INSTANCE.init();
      String report = AntiCheatDetector.INSTANCE.report();
      System.out.println(report);

      try {
         Minecraft minecraft = Minecraft.getInstance();
         File dir = minecraft != null && minecraft.gameDirectory != null ? minecraft.gameDirectory : new File(".");
         Path path = new File(dir, "skycore-ac-report.txt").toPath();
         Files.writeString(path, report, StandardCharsets.UTF_8);
         ChatMessages.displayInfo("Отчёт сохранён: " + path.toAbsolutePath());
      } catch (Exception failure) {
         ChatMessages.displayError("Не удалось сохранить отчёт: " + failure.getMessage());
      }

      return 1;
   }

   private int auto() {
      AntiCheatDetector.INSTANCE.init();
      ChatMessages.displayInfo(AntiCheatDetector.INSTANCE.topGuessLine());
      ChatMessages.displayInfo(AutoBypass.INSTANCE.applyAuto());
      return 1;
   }

   private int apply(String name) {
      AcFamily family;
      try {
         family = AcFamily.valueOf(name.toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException failure) {
         ChatMessages.displayError("Неизвестный античит: " + name);
         return 0;
      }

      ChatMessages.displayInfo(AutoBypass.INSTANCE.apply(family));
      return 1;
   }

   private int setup() {
      for (String line : AutoBypass.INSTANCE.currentSetup().split("\n")) {
         ChatMessages.displayInfo(line);
      }

      for (String warning : AutoBypass.INSTANCE.diagnose()) {
         ChatMessages.displayInfo("! " + warning);
      }

      return 1;
   }

   private int defaults() {
      ChatMessages.displayInfo(AutoBypass.INSTANCE.resetV3());
      return 1;
   }

   private int reset() {
      AntiCheatDetector.INSTANCE.reset();
      ChatMessages.displayInfo("Данные детектора и телеметрии обнулены");
      return 1;
   }

   private int help() {
      ChatMessages.displayInfo(".ac — краткая сводка");
      ChatMessages.displayInfo(".ac report — полный отчёт в чат");
      ChatMessages.displayInfo(".ac speed — телеметрия режимов");
      ChatMessages.displayInfo(".ac save — отчёт в файл run/skycore-ac-report.txt");
      ChatMessages.displayInfo(".ac auto — подобрать профиль под найденный AC");
      ChatMessages.displayInfo(".ac apply <grim|matrix|vulcan|...> — профиль вручную");
      ChatMessages.displayInfo(".ac setup — текущие значения Speedv3 + проверка настроек");
      ChatMessages.displayInfo(".ac default — сбросить Speedv3 к значениям по умолчанию");
      ChatMessages.displayInfo(".ac reset — обнулить");
      return 1;
   }

   private int channelCount() {
      String report = AntiCheatDetector.INSTANCE.report();
      int index = report.indexOf("channels (");
      if (index < 0) {
         return 0;
      }

      int end = report.indexOf(')', index);
      if (end < 0) {
         return 0;
      }

      try {
         return Integer.parseInt(report.substring(index + "channels (".length(), end).trim());
      } catch (NumberFormatException failure) {
         return 0;
      }
   }

   private int setbackCount() {
      String report = AntiCheatDetector.INSTANCE.report();
      int index = report.indexOf("setbacks=");
      if (index < 0) {
         return 0;
      }

      int start = index + "setbacks=".length();
      int end = start;
      while (end < report.length() && Character.isDigit(report.charAt(end))) {
         end++;
      }

      try {
         return Integer.parseInt(report.substring(start, end));
      } catch (NumberFormatException failure) {
         return 0;
      }
   }
}
