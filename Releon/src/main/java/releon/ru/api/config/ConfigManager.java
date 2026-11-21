package releon.ru.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import lightning.sdk.LightningSDK;
import lightning.sdk.annotation.LgVM;
import lightning.sdk.annotation.LgVM.Level;
import net.fabricmc.loader.api.FabricLoader;
import releon.ru.api.config.exception.ConfigException;
import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.impl.TickEvent;
import releon.ru.api.module.ModuleManager;

public final class ConfigManager {
   public static final String ROOT_FOLDER = "releon";
   public static final String USER_CONFIG_FOLDER = "config";
   public static final String SYSTEM_FOLDER = "system";
   public static final String CONFIG_EXTENSION = ".releon";
   private static final long AUTO_SAVE_DELAY_MS = 1000L;
   private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private final Path systemDirectory = systemDirectory();
   private final Path modulesFile = this.systemDirectory.resolve("modules.releon");
   private final ModuleConfigStorage moduleStorage;
   private boolean dirty;
   private long dirtySince;

   public ConfigManager(ModuleManager moduleManager) {
      this.moduleStorage = new ModuleConfigStorage(moduleManager);
   }

   public static Path rootDirectory() {
      return FabricLoader.getInstance().getGameDir().resolve("releon");
   }

   public static Path userConfigDirectory() {
      return rootDirectory().resolve("config");
   }

   public static Path systemDirectory() {
      return rootDirectory().resolve("system");
   }

   public static Path legacyDirectory() {
      return FabricLoader.getInstance().getConfigDir().resolve("releon");
   }

   @LgVM(Level.FULL)
   public void init() {
      if (LightningSDK.checkAuthAlive()) {
         try {
            Files.createDirectories(userConfigDirectory());
            Files.createDirectories(this.systemDirectory);
         } catch (IOException var2) {
            throw new ConfigException("Failed to create config directories under: " + rootDirectory(), var2);
         }
      }
   }

   public void loadAll() {
      this.loadModules();
      this.dirty = false;
   }

   public void load(String name) {
      this.loadModules(this.resolveUserConfigFile(name));
      this.dirty = false;
   }

   public void saveAll() {
      this.saveModules();
      this.dirty = false;
   }

   public void save(String name) {
      this.saveModules(this.resolveUserConfigFile(name));
      this.dirty = false;
   }

   public List<String> listUserConfigs() {
      try {
         Files.createDirectories(userConfigDirectory());

         List var2;
         try (Stream<Path> stream = Files.list(userConfigDirectory())) {
            var2 = stream.filter(x$0 -> Files.isRegularFile(x$0))
               .map(path -> path.getFileName().toString())
               .filter(fileName -> fileName.endsWith(".releon"))
               .map(fileName -> fileName.substring(0, fileName.length() - ".releon".length()))
               .sorted(String.CASE_INSENSITIVE_ORDER)
               .toList();
         }

         return var2;
      } catch (IOException var6) {
         throw new ConfigException("Failed to list configs in: " + userConfigDirectory(), var6);
      }
   }

   public void markDirty() {
      if (!this.dirty) {
         this.dirtySince = System.currentTimeMillis();
      }

      this.dirty = true;
   }

   @SubscribeEvent
   private void onTick(TickEvent.Post event) {
      if (this.dirty && System.currentTimeMillis() - this.dirtySince >= 1000L) {
         this.saveAll();
      }
   }

   private void loadModules() {
      this.loadModules(this.resolveModulesFileForRead());
   }

   private void loadModules(Path file) {
      if (!Files.exists(file)) {
         if (file.equals(this.modulesFile)) {
            this.saveModules();
         }
      } else {
         try {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
               JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
               this.moduleStorage.load(root);
            }
         } catch (Exception var7) {
            throw new ConfigException("Failed to load module config: " + file, var7);
         }
      }
   }

   private void saveModules() {
      this.saveModules(this.modulesFile);
   }

   private void saveModules(Path file) {
      try {
         Files.createDirectories(file.getParent());

         try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            this.gson.toJson(this.moduleStorage.save(), writer);
         }
      } catch (IOException var7) {
         throw new ConfigException("Failed to save module config: " + file, var7);
      }
   }

   private Path resolveModulesFileForRead() {
      if (Files.exists(this.modulesFile)) {
         return this.modulesFile;
      } else {
         Path legacyJson = legacyDirectory().resolve("modules.json");
         return Files.exists(legacyJson) ? legacyJson : this.modulesFile;
      }
   }

   private Path resolveUserConfigFile(String name) {
      String safeName = this.sanitizeConfigName(name);
      if (safeName.isBlank()) {
         throw new ConfigException("Config name is empty.");
      } else {
         return userConfigDirectory().resolve(safeName + ".releon");
      }
   }

   private String sanitizeConfigName(String name) {
      if (name == null) {
         return "";
      } else {
         String normalized = name.trim();
         if (normalized.endsWith(".releon")) {
            normalized = normalized.substring(0, normalized.length() - ".releon".length());
         }

         return normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_\\-а-яё]", "_").replaceAll("_+", "_").replaceAll("^_+|_+$", "");
      }
   }
}
