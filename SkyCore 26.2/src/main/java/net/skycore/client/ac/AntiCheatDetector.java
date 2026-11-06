package net.skycore.client.ac;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.chat.Component;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PacketReceiveEvent;

/**
 * Определяет античит сервера по пассивным признакам: каналы плагинов, brand сервера,
 * текст чата (в том числе вывод /plugins), имена в таб-листе, плюс поведение сервера
 * (сеты позиции, частота транзакций).
 *
 * Ничего не отправляет на сервер и не меняет поведение клиента.
 */
public final class AntiCheatDetector {
   public static final AntiCheatDetector INSTANCE = new AntiCheatDetector();

   private static final int MAX_TIMELINE = 400;
   private static final String[] FLAG_WORDS = {
      "flag", "flagged", "violation", "suspicious", "kick", "kicked", "ban", "banned",
      "unfair", "cheat", "detected", "failed", "vl ", "vl:", "pre vl"
   };

   private final Map<AcFamily, Double> scores = new EnumMap<>(AcFamily.class);
   private final Map<AcFamily, Set<String>> evidence = new EnumMap<>(AcFamily.class);
   private final Set<String> channels = new LinkedHashSet<>();
   private final Set<String> plugins = new TreeSet<>();
   private final Deque<String> timeline = new ArrayDeque<>();
   private final Deque<Long> setbacks = new ArrayDeque<>();
   private String brand = "?";
   private int pingPackets;
   private int keepAlives;
   private int flagLines;
   private long startedAt = System.currentTimeMillis();
   private boolean initialized;

   private AntiCheatDetector() {
   }

   public void init() {
      if (this.initialized) {
         return;
      }

      this.initialized = true;
      SkycoreClient.events().subscribe(PacketReceiveEvent.class, event -> this.onPacket(event.getPacket()));
      SpeedTelemetry.INSTANCE.init();
   }

   public synchronized void reset() {
      this.scores.clear();
      this.evidence.clear();
      this.channels.clear();
      this.plugins.clear();
      this.timeline.clear();
      this.setbacks.clear();
      this.brand = "?";
      this.pingPackets = 0;
      this.keepAlives = 0;
      this.flagLines = 0;
      this.startedAt = System.currentTimeMillis();
      SpeedTelemetry.INSTANCE.reset();
   }

   private void onPacket(Packet<?> packet) {
      if (packet instanceof ClientboundCustomPayloadPacket custom) {
         if (custom.payload() instanceof BrandPayload brandPayload) {
            this.brand = brandPayload.brand();
            this.scan(this.brand, "BRAND", 1.5);
            this.log("BRAND " + this.brand);
         } else if (custom.payload() instanceof DiscardedPayload discarded) {
            String id = discarded.id().toString();
            if (this.channels.add(id)) {
               this.scan(id, "CHANNEL", 3.0);
               this.log("CHANNEL " + id);
            }
         } else if (custom.payload() != null && custom.payload().type() != null) {
            String id = custom.payload().type().id().toString();
            if (this.channels.add(id)) {
               this.scan(id, "CHANNEL", 3.0);
               this.log("CHANNEL " + id);
            }
         }
      } else if (packet instanceof ClientboundSystemChatPacket chat) {
         Component content = chat.content();
         if (content != null && !chat.overlay()) {
            this.scanChat(content.getString());
         }
      } else if (packet instanceof ClientboundPlayerInfoUpdatePacket info) {
         for (ClientboundPlayerInfoUpdatePacket.Entry entry : info.entries()) {
            Component name = entry.displayName();
            if (name != null) {
               this.scan(name.getString(), "TAB", 2.0);
            }
         }
      } else if (packet instanceof ClientboundPlayerPositionPacket) {
         this.noteSetback();
      } else if (packet instanceof ClientboundPingPacket) {
         this.pingPackets++;
      } else if (packet instanceof ClientboundKeepAlivePacket) {
         this.keepAlives++;
      }
   }

   private void scanChat(String raw) {
      if (raw == null || raw.isEmpty()) {
         return;
      }

      String text = raw.trim();
      if (text.length() > 400) {
         text = text.substring(0, 400);
      }

      String lower = text.toLowerCase(Locale.ROOT);
      for (String word : FLAG_WORDS) {
         if (lower.contains(word)) {
            this.flagLines++;
            this.log("FLAG? " + text);
            SpeedTelemetry.INSTANCE.noteFlag(text);
            break;
         }
      }

      this.collectPlugins(text);
      this.scan(text, "CHAT", 2.0);
   }

   /** Вывод /plugins: "[Server] GrimAC, Vulcan, ..." — режем на токены и запоминаем. */
   private void collectPlugins(String text) {
      String cleaned = text.replace('[', ' ').replace(']', ' ').replace('(', ' ').replace(')', ' ')
         .replace(',', ' ').replace(':', ' ').replace('|', ' ').replace('»', ' ').replace('«', ' ');

      for (String token : cleaned.split("\\s+")) {
         String name = token.trim();
         if (name.length() < 3 || name.length() > 24) {
            continue;
         }

         String lower = name.toLowerCase(Locale.ROOT);
         for (AcFamily family : AcFamily.values()) {
            if (family == AcFamily.UNKNOWN) {
               continue;
            }

            for (String keyword : family.keywords()) {
               if (lower.contains(keyword)) {
                  this.plugins.add(name);
                  break;
               }
            }
         }
      }
   }

   private void scan(String text, String source, double weight) {
      if (text == null) {
         return;
      }

      String lower = text.toLowerCase(Locale.ROOT);
      for (AcFamily family : AcFamily.values()) {
         if (family == AcFamily.UNKNOWN) {
            continue;
         }

         for (String keyword : family.keywords()) {
            if (keyword.isEmpty() || !lower.contains(keyword)) {
               continue;
            }

            this.addScore(family, weight, source + ": " + text);
            break;
         }
      }
   }

   private void addScore(AcFamily family, double weight, String note) {
      this.scores.merge(family, weight, Double::sum);
      this.evidence.computeIfAbsent(family, key -> new LinkedHashSet<>()).add(note);
   }

   private void noteSetback() {
      long now = System.currentTimeMillis();
      this.setbacks.addLast(now);
      while (this.setbacks.size() > 200) {
         this.setbacks.removeFirst();
      }

      this.log("SETBACK position");
      SpeedTelemetry.INSTANCE.noteSetback();
   }

   private void log(String line) {
      this.timeline.addLast("[" + java.time.LocalTime.now().withNano(0) + "] " + line);
      while (this.timeline.size() > MAX_TIMELINE) {
         this.timeline.removeFirst();
      }
   }

   public synchronized String report() {
      StringBuilder builder = new StringBuilder();
      long seconds = Math.max(1L, (System.currentTimeMillis() - this.startedAt) / 1000L);
      builder.append("=== SkyCore AC report ===\n");
      builder.append("brand: ").append(this.brand).append('\n');
      builder.append("channels (").append(this.channels.size()).append("): ")
         .append(String.join(", ", this.channels)).append('\n');
      builder.append("plugins: ").append(this.plugins.isEmpty() ? "-" : String.join(", ", this.plugins)).append('\n');
      builder.append("chat-flag-lines: ").append(this.flagLines).append('\n');
      builder.append("behaviour: setbacks=").append(this.setbacks.size())
         .append("  ping-packets=").append(this.pingPackets)
         .append("  keepalives=").append(this.keepAlives)
         .append("  session=").append(seconds).append("s\n");

      List<AcGuess> guesses = this.guesses();
      builder.append("\n-- догадки --\n");
      if (guesses.isEmpty()) {
         builder.append("1) не определён — признаков не найдено\n");
      } else {
         int index = 1;
         for (AcGuess guess : guesses) {
            builder.append(index++).append(") ").append(guess.family().display())
               .append("  score=").append(String.format(Locale.ROOT, "%.1f", guess.score()))
               .append("  ").append(String.join("; ", guess.evidence())).append('\n');
         }
      }

      builder.append("\n-- что делать --\n");
      AcFamily top = guesses.isEmpty() ? AcFamily.UNKNOWN : guesses.get(0).family();
      builder.append(top.recommendation()).append('\n');

      builder.append("\n-- телеметрия спидов --\n");
      builder.append(SpeedTelemetry.INSTANCE.report());

      builder.append("\n-- последние события --\n");
      int printed = 0;
      List<String> tail = new ArrayList<>(this.timeline);
      for (int index = tail.size() - 1; index >= 0 && printed < 60; index--) {
         builder.append(tail.get(index)).append('\n');
         printed++;
      }

      return builder.toString();
   }

   public synchronized List<AcGuess> guesses() {
      List<AcGuess> result = new ArrayList<>();
      for (Map.Entry<AcFamily, Double> entry : this.scores.entrySet()) {
         Set<String> notes = this.evidence.getOrDefault(entry.getKey(), Set.of());
         result.add(new AcGuess(entry.getKey(), entry.getValue(), new ArrayList<>(notes)));
      }

      result.sort((a, b) -> Double.compare(b.score(), a.score()));
      return result;
   }

   public synchronized String topGuessLine() {
      List<AcGuess> guesses = this.guesses();
      if (guesses.isEmpty()) {
         return "AC: не определён";
      }

      AcGuess best = guesses.get(0);
      return "AC: " + best.family().display() + " (score " + String.format(Locale.ROOT, "%.1f", best.score()) + ")";
   }

   public record AcGuess(AcFamily family, double score, List<String> evidence) {
   }
}
