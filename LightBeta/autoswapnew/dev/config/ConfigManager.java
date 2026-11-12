package dev.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.ClientSwap;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static ModConfig config = new ModConfig();

	private ConfigManager() {
	}

	public static ModConfig get() {
		return config;
	}

	public static void load() {
		Path path = getConfigPath();
		if (!Files.exists(path)) {
			save();
			return;
		}

		try {
			String json = Files.readString(path);
			ModConfig loaded = GSON.fromJson(json, ModConfig.class);
			if (loaded != null) {
				config = loaded;
			}
		} catch (IOException exception) {
			ClientSwap.LOGGER.error("Failed to load config", exception);
		}
	}

	public static void save() {
		try {
			Files.createDirectories(getConfigPath().getParent());
			Files.writeString(getConfigPath(), GSON.toJson(config));
		} catch (IOException exception) {
			ClientSwap.LOGGER.error("Failed to save config", exception);
		}
	}

	private static Path getConfigPath() {
		return FabricLoader.getInstance().getConfigDir().resolve(ClientSwap.MOD_ID + ".json");
	}
}
