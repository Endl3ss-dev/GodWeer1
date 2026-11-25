package net.skycore.client.module.misc;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;

/**
 * Test Bridge: a localhost control socket for testing modules with an
 * external driver (see tools/testbridge). While enabled it listens on
 * 127.0.0.1 only and speaks a line protocol an AI or a script can drive:
 *
 * <pre>
 * PING                                  -> PONG
 * STATE                                 -> one-line JSON state snapshot
 * MODULES                               -> OK Name=ON,Other=OFF,...
 * MODULE &lt;name&gt; ON|OFF|TOGGLE         -> OK / ERR ...
 * SETTING &lt;module&gt; &lt;setting&gt; &lt;value&gt;  -> OK / ERR ...
 * KEY &lt;forward|back|left|right|jump|sneak|sprint|attack|use&gt; DOWN|UP
 * KEY &lt;name&gt; TAP [millis]             -> pressed, auto-released
 * KEYS_UP                               -> release every key
 * LOOK &lt;yaw&gt; &lt;pitch&gt;                  -> set view rotation
 * CHAT &lt;text&gt;                         -> chat message or /command
 * SCREENSHOT &lt;name&gt;                   -> OK &lt;path to png under gameDir/testbridge/shots&gt;
 * QUIT                                  -> close the connection
 * </pre>
 *
 * All game actions run on the client thread (drained from a queue in
 * onTick); the socket threads only parse and wait for the answer. This is
 * a development/testing tool: never enable it on an untrusted network
 * (it is loopback-only, but anything local can drive your player).
 */
public final class TestBridgeModule extends ClientModule {
   public static final TestBridgeModule INSTANCE = new TestBridgeModule();

   private final NumberSetting port = this.add(new NumberSetting("Port", 25599.0, 1024.0, 49151.0, 1.0));

   private final ConcurrentLinkedQueue<Request> requests = new ConcurrentLinkedQueue<>();
   private final Map<String, Integer> keyReleaseTicks = new LinkedHashMap<>();
   private volatile ServerSocket serverSocket;
   private Thread serverThread;
   private int tickCounter;

   private TestBridgeModule() {
      super("Test Bridge", "Localhost control socket for automated module tests", ModuleCategory.MISC);
   }

   @Override
   protected void onEnable() {
      this.startServer();
   }

   @Override
   protected void onDisable() {
      this.stopServer();
      this.releaseAllKeys();
   }

   @Override
   protected void onTick() {
      this.tickCounter++;
      Request request;
      while ((request = this.requests.poll()) != null) {
         try {
            request.response = this.execute(request.line);
         } catch (Exception exception) {
            request.response = "ERR " + exception.getClass().getSimpleName() + ": " + exception.getMessage();
         }
         request.done.countDown();
      }
      this.keyReleaseTicks.entrySet().removeIf(entry -> {
         if (this.tickCounter >= entry.getValue()) {
            KeyMapping key = this.keyByName(entry.getKey());
            if (key != null) {
               key.setDown(false);
            }
            return true;
         }
         return false;
      });
   }

   // ------------------------------------------------------------------
   // Socket plumbing
   // ------------------------------------------------------------------

   private void startServer() {
      this.stopServer();
      int listenPort = this.port.get().intValue();
      Thread thread = new Thread(() -> {
         try (ServerSocket server = new ServerSocket(listenPort, 50, InetAddress.getByName("127.0.0.1"))) {
            this.serverSocket = server;
            while (!server.isClosed()) {
               Socket client = server.accept();
               Thread handler = new Thread(() -> this.handleClient(client), "skycore-testbridge-client");
               handler.setDaemon(true);
               handler.start();
            }
         } catch (IOException ignored) {
            // Port busy or socket closed on disable: stay silent, module off.
         }
      }, "skycore-testbridge");
      thread.setDaemon(true);
      this.serverThread = thread;
      thread.start();
   }

   private void stopServer() {
      ServerSocket server = this.serverSocket;
      this.serverSocket = null;
      if (server != null) {
         try {
            server.close();
         } catch (IOException ignored) {
         }
      }
   }

   private void handleClient(Socket client) {
      try (client;
           BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
           BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8))) {
         writer.write("OK TestBridge ready\n");
         writer.flush();
         String line;
         while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
               continue;
            }
            if (line.equalsIgnoreCase("QUIT")) {
               writer.write("OK bye\n");
               writer.flush();
               return;
            }
            Request request = new Request(line);
            this.requests.add(request);
            String response = request.done.await(10, TimeUnit.SECONDS)
               ? request.response
               : "ERR timeout (is the client ticking?)";
            writer.write(response + "\n");
            writer.flush();
         }
      } catch (Exception ignored) {
         // Driver went away; nothing to clean up beyond the socket.
      }
   }

   private static final class Request {
      private final String line;
      private final CountDownLatch done = new CountDownLatch(1);
      private volatile String response = "ERR no response";

      private Request(String line) {
         this.line = line;
      }
   }

   // ------------------------------------------------------------------
   // Commands (client thread)
   // ------------------------------------------------------------------

   private String execute(String line) {
      Minecraft minecraft = Minecraft.getInstance();
      String[] head = line.split("\\s+", 2);
      String verb = head[0].toUpperCase(Locale.ROOT);
      String rest = head.length > 1 ? head[1] : "";
      return switch (verb) {
         case "PING" -> "PONG";
         case "STATE" -> this.stateJson(minecraft);
         case "MODULES" -> this.modulesLine();
         case "MODULE" -> this.moduleCommand(rest);
         case "SETTING" -> this.settingCommand(rest);
         case "KEY" -> this.keyCommand(minecraft, rest);
         case "KEYS_UP" -> {
            this.releaseAllKeys();
            yield "OK";
         }
         case "LOOK" -> this.lookCommand(minecraft, rest);
         case "CHAT" -> this.chatCommand(minecraft, rest);
         case "SCREENSHOT" -> this.screenshotCommand(minecraft, rest);
         default -> "ERR unknown command: " + verb;
      };
   }

   private String stateJson(Minecraft minecraft) {
      LocalPlayer player = minecraft.player;
      if (player == null || minecraft.level == null) {
         return "{\"inWorld\":false}";
      }
      Vec3 pos = player.position();
      List<String> enabled = new ArrayList<>();
      for (ClientModule module : SkycoreClient.modules().getModules()) {
         if (module.isEnabled()) {
            enabled.add(module.getName());
         }
      }
      return String.format(Locale.ROOT,
         "{\"inWorld\":true,\"player\":\"%s\",\"x\":%.2f,\"y\":%.2f,\"z\":%.2f,"
            + "\"yaw\":%.1f,\"pitch\":%.1f,\"hp\":%.1f,\"food\":%d,\"onGround\":%s,"
            + "\"sprinting\":%s,\"screenOpen\":%s,\"modules\":[%s]}",
         player.getGameProfile().name(),
         pos.x, pos.y, pos.z,
         player.getYRot(), player.getXRot(),
         player.getHealth(), player.getFoodData().getFoodLevel(),
         player.onGround(), player.isSprinting(),
         minecraft.gui.screen() != null,
         enabled.stream().map(name -> "\"" + name + "\"").reduce((a, b) -> a + "," + b).orElse(""));
   }

   private String modulesLine() {
      List<String> parts = new ArrayList<>();
      for (ClientModule module : SkycoreClient.modules().getModules()) {
         parts.add(module.getName() + "=" + (module.isEnabled() ? "ON" : "OFF"));
      }
      return "OK " + String.join(",", parts);
   }

   private String moduleCommand(String rest) {
      String trimmed = rest.trim();
      int lastSpace = trimmed.lastIndexOf(' ');
      if (lastSpace < 0) {
         return "ERR usage: MODULE <name> ON|OFF|TOGGLE";
      }
      String name = trimmed.substring(0, lastSpace).trim();
      String action = trimmed.substring(lastSpace + 1).toUpperCase(Locale.ROOT);
      ClientModule module = SkycoreClient.modules().get(name).orElse(null);
      if (module == null) {
         return "ERR no module: " + name;
      }
      switch (action) {
         case "ON" -> module.setEnabled(true);
         case "OFF" -> module.setEnabled(false);
         case "TOGGLE" -> module.toggle();
         default -> {
            return "ERR unknown action: " + action;
         }
      }
      return "OK " + module.getName() + "=" + (module.isEnabled() ? "ON" : "OFF");
   }

   private String settingCommand(String rest) {
      // Module names may contain spaces; settings never do. Try progressively
      // longer module-name prefixes, then setting name, then the value.
      String[] tokens = rest.trim().split("\\s+");
      for (int words = Math.min(3, tokens.length - 2); words >= 1; words--) {
         StringBuilder name = new StringBuilder();
         for (int i = 0; i < words; i++) {
            if (i > 0) {
               name.append(' ');
            }
            name.append(tokens[i]);
         }
         ClientModule module = SkycoreClient.modules().get(name.toString()).orElse(null);
         if (module == null || tokens.length <= words) {
            continue;
         }
         String settingName = tokens[words];
         StringBuilder value = new StringBuilder();
         for (int i = words + 1; i < tokens.length; i++) {
            if (i > words + 1) {
               value.append(' ');
            }
            value.append(tokens[i]);
         }
         for (Setting<?> setting : module.getSettings()) {
            if (!setting.getName().equalsIgnoreCase(settingName)) {
               continue;
            }
            String text = value.toString();
            if (setting instanceof BooleanSetting booleanSetting) {
               boolean parsed = text.equalsIgnoreCase("true") || text.equalsIgnoreCase("on")
                  || text.equalsIgnoreCase("1");
               booleanSetting.set(parsed);
               return "OK";
            }
            if (setting instanceof NumberSetting numberSetting) {
               try {
                  numberSetting.set(Double.parseDouble(text));
                  return "OK";
               } catch (NumberFormatException exception) {
                  return "ERR not a number: " + text;
               }
            }
            if (setting instanceof ModeSetting modeSetting) {
               modeSetting.set(text);
               return "OK";
            }
            return "ERR unsupported setting type: " + setting.getName();
         }
         return "ERR no setting " + settingName + " on " + module.getName();
      }
      return "ERR usage: SETTING <module> <setting> <value>";
   }

   private String keyCommand(Minecraft minecraft, String rest) {
      String[] tokens = rest.trim().split("\\s+");
      if (tokens.length < 2) {
         return "ERR usage: KEY <name> DOWN|UP|TAP [millis]";
      }
      KeyMapping key = this.keyByName(tokens[0]);
      if (key == null) {
         return "ERR unknown key: " + tokens[0];
      }
      switch (tokens[1].toUpperCase(Locale.ROOT)) {
         case "DOWN" -> key.setDown(true);
         case "UP" -> {
            key.setDown(false);
            this.keyReleaseTicks.remove(tokens[0].toLowerCase(Locale.ROOT));
         }
         case "TAP" -> {
            int millis = tokens.length > 2 ? Integer.parseInt(tokens[2]) : 120;
            key.setDown(true);
            this.keyReleaseTicks.put(tokens[0].toLowerCase(Locale.ROOT),
               this.tickCounter + Math.max(1, millis / 50));
         }
         default -> {
            return "ERR unknown key action: " + tokens[1];
         }
      }
      return "OK";
   }

   private KeyMapping keyByName(String name) {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft == null || minecraft.options == null) {
         return null;
      }
      return switch (name.toLowerCase(Locale.ROOT)) {
         case "forward" -> minecraft.options.keyUp;
         case "back" -> minecraft.options.keyDown;
         case "left" -> minecraft.options.keyLeft;
         case "right" -> minecraft.options.keyRight;
         case "jump" -> minecraft.options.keyJump;
         case "sneak" -> minecraft.options.keyShift;
         case "sprint" -> minecraft.options.keySprint;
         case "attack" -> minecraft.options.keyAttack;
         case "use" -> minecraft.options.keyUse;
         default -> null;
      };
   }

   private void releaseAllKeys() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft == null || minecraft.options == null) {
         return;
      }
      for (String name : List.of("forward", "back", "left", "right", "jump", "sneak", "sprint", "attack", "use")) {
         KeyMapping key = this.keyByName(name);
         if (key != null) {
            key.setDown(false);
         }
      }
      this.keyReleaseTicks.clear();
   }

   private String lookCommand(Minecraft minecraft, String rest) {
      LocalPlayer player = minecraft.player;
      if (player == null) {
         return "ERR not in world";
      }
      String[] tokens = rest.trim().split("\\s+");
      if (tokens.length < 2) {
         return "ERR usage: LOOK <yaw> <pitch>";
      }
      float yaw = Float.parseFloat(tokens[0]);
      float pitch = Mth.clamp(Float.parseFloat(tokens[1]), -90.0F, 90.0F);
      player.setYRot(yaw);
      player.setXRot(pitch);
      player.yRotO = yaw;
      player.xRotO = pitch;
      return "OK";
   }

   private String chatCommand(Minecraft minecraft, String rest) {
      LocalPlayer player = minecraft.player;
      if (player == null || player.connection == null) {
         return "ERR not in world";
      }
      String text = rest.trim();
      if (text.startsWith("/")) {
         player.connection.sendCommand(text.substring(1));
      } else {
         player.connection.sendChat(text);
      }
      return "OK";
   }

   private String screenshotCommand(Minecraft minecraft, String rest) {
      String name = rest.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
      if (name.isEmpty()) {
         name = "shot";
      }
      try {
         Path out = minecraft.gameDirectory.toPath()
            .resolve("testbridge").resolve("shots").resolve(name + ".png");
         Files.createDirectories(out.getParent());
         // The main render target moved from Minecraft to GameRenderer in the
         // 26.2 mappings; every other renderer in the client reads it the same
         // way (see AcquireViewHelper / AddQuadHelper).
         RenderTarget target = minecraft.gameRenderer.mainRenderTarget();
         if (target == null) {
            return "ERR screenshot failed: no render target";
         }
         try (NativeImage image = Screenshot.takeScreenshot(target)) {
            image.writeToFile(out);
         }
         return "OK " + out;
      } catch (Exception exception) {
         return "ERR screenshot failed: " + exception.getMessage();
      }
   }
}
