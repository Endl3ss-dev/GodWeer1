package swerve;

import swerve.config.ConfigManager;
import swerve.cosmetics.CosmeticStore;
import swerve.cosmetics.Wearables;
import swerve.cosmetics.cape.CapeTextures;
import swerve.cosmetics.cape.ClothSimulations;
import swerve.cosmetics.melee.HandMovement;
import swerve.cosmetics.melee.MeleeState;
import swerve.managers.ModuleManager;
import swerve.utils.input.ClickGuiController;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

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

		// Шрифты грузятся лениво при первом обращении: на этом этапе GL-контекст ещё не готов.
		ModuleManager.init();
		// [+] Конфиг читается после менеджера модулей: ключ модуля — имя его класса,
		// поэтому до регистрации модулей восстанавливать нечего.
		ConfigManager.init();
		ClickGuiController.init();

		// Гардероб: сохранённые модели и синхронизация с другими клиентами.
		CosmeticStore.load();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			Wearables.send(false);
			// Ножи: путь игрока для клипа бега, анимация играет, только пока нож рисуется.
			HandMovement.tick(client);
			MeleeState.tick();
			// Плащи: загрузка готовых кадров в GPU и физика ткани.
			CapeTextures.pump(2_000_000L);
			if (client.level != null) {
				ClothSimulations.getInstance().tickAll(client.level);
			} else {
				ClothSimulations.getInstance().clear();
			}
		});

		LOGGER.info("{} v{} loaded in {} ms", MOD_NAME, MOD_VERSION, (System.nanoTime() - start) / 1_000_000L);
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
