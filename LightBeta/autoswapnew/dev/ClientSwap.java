package dev;

import dev.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSwap implements ModInitializer {
	public static final String MOD_ID = "swap-helper";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigManager.load();
		LOGGER.info("{} initialized", MOD_ID);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
