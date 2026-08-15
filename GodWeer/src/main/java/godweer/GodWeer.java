package godweer;

import godweer.managers.ModuleManager;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GodWeer implements ClientModInitializer {
	public static final String MOD_ID = "godweer";
	public static final String MOD_NAME = "GodWeer";
	public static final String MOD_VERSION = "0.1";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	private static GodWeer instance;

	public static GodWeer getInstance() {
		return instance;
	}

	@Override
	public void onInitializeClient() {
		instance = this;

		long start = System.nanoTime();

		// Шрифты грузятся лениво при первом обращении: на этом этапе GL-контекст ещё не готов.
		ModuleManager.init();

		LOGGER.info("{} v{} loaded in {} ms", MOD_NAME, MOD_VERSION, (System.nanoTime() - start) / 1_000_000L);
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
