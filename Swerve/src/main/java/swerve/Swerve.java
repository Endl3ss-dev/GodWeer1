package swerve;

import swerve.managers.ModuleManager;
import swerve.utils.input.ClickGuiController;
import swerve.utils.render.WorldProjection;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Swerve implements ClientModInitializer {
	public static final String MOD_ID = "swerve";
	public static final String MOD_NAME = "Swerve";
	public static final String MOD_VERSION = "0.1";

	// Ник, с которым клиент всегда заходит в мир (см. MixinUser / MixinMinecraft).
	public static final String CLIENT_NICKNAME = "SwerveClientTest";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	private static Swerve instance;

	public static Swerve getInstance() {
		return instance;
	}

	@Override
	public void onInitializeClient() {
		instance = this;

		long start = System.nanoTime();

		// [+] Съём матриц кадра для HUD-элементов, привязанных к точке мира (TargetHud).
		WorldProjection.init();

		// Шрифты грузятся лениво при первом обращении: на этом этапе GL-контекст ещё не готов.
		ModuleManager.init();
		ClickGuiController.init();

		LOGGER.info("{} v{} loaded in {} ms", MOD_NAME, MOD_VERSION, (System.nanoTime() - start) / 1_000_000L);
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
