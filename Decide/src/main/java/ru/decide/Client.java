/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback
 *  net.minecraft.class_10055
 *  net.minecraft.class_1007
 *  net.minecraft.class_3883
 *  net.minecraft.class_3887
 *  net.minecraft.class_591
 */
package ru.decide;

import java.lang.invoke.MethodHandles;
import lombok.Generated;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_591;
import ru.decide.command.CommandManager;
import ru.decide.config.ConfigManager;
import ru.decide.cosmetics.CosmeticFeatureRenderer;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.custompet.CustomPetCosmetics;
import ru.decide.cosmetics.itemskins.AppleEatingEffects;
import ru.decide.cosmetics.itemskins.ItemSkinEffects;
import ru.decide.cosmetics.itemskins.ItemSkinRegistry;
import ru.decide.cosmetics.maksai.MaksaiCosmetics;
import ru.decide.cosmetics.maksai.hats.HatLayer;
import ru.decide.cosmetics.melee.MeleeController;
import ru.decide.cosmetics.melee.MeleeRegistry;
import ru.decide.cosmetics.melee.MeleeRules;
import ru.decide.cosmetics.melee.MeleeSettings;
import ru.decide.cosmetics.melee.MeleeSounds;
import ru.decide.friend.FriendManager;
import ru.decide.inventorypreset.InventoryPresetManager;
import ru.decide.lang.Lang;
import ru.decide.manager.GuiManager;
import ru.decide.manager.events.orbit.EventBus;
import ru.decide.manager.rotation.ComponentManager;
import ru.decide.module.api.ModuleManager;
import ru.decide.module.impl.utils.EffectSounds;
import ru.decide.module.impl.utils.VeloTuneModule;
import ru.decide.optimization.FrameSyncManager;
import ru.decide.optimization.velotune.VeloTuneManager;
import ru.decide.rpc.RPC;
import ru.decide.screen.Menu;
import ru.decide.ui.compat.GuiContext;
import ru.decide.ui.theme.ThemePersistence;
import ru.decide.utils.render.ChinaHatFeatureRenderer;
import ru.decide.utils.render.Render2D;
import ru.decide.utils.render.font.FontInitializer;
import ru.decide.utils.render.lyrics.LyricText3D;

@Environment(value=EnvType.CLIENT)
public class Client
implements ClientModInitializer {
    public static Client get;
    private static final EventBus eventHandler;
    private ModuleManager moduleManager;
    private Render2D render2D;
    private Menu clickGuiScreen;
    private ComponentManager componentManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private FriendManager friendManager;
    private InventoryPresetManager inventoryPresetManager;
    private final RPC rpc = new RPC();
    public static String build;
    private GuiManager guiManager;

    public static Client get() {
        return get;
    }

    public void start() {
        eventHandler.registerLambdaFactory("", (lookupInMethod, klass) -> (MethodHandles.Lookup)lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        FontInitializer.register();
        LyricText3D.registerResourceReload();
    }

    public void onInitializeClient() {
        System.out.print("\u0412\u0445\u043e\u0434");
        FrameSyncManager.applyLwjglTweaks();
        get = this;
        Lang.init();
        this.start();
        this.rpc.startRpc();
        this.configManager = new ConfigManager();
        this.configManager.setup();
        this.friendManager = new FriendManager();
        this.friendManager.init();
        this.inventoryPresetManager = new InventoryPresetManager();
        this.inventoryPresetManager.init();
        this.moduleManager = new ModuleManager();
        this.moduleManager.init();
        this.componentManager = new ComponentManager();
        this.componentManager.init();
        this.commandManager = new CommandManager();
        this.commandManager.init();
        this.configManager.init();
        GuiContext.registerDecideVisuals();
        this.guiManager = new GuiManager();
        this.guiManager.init();
        ThemePersistence.init();
        this.render2D = new Render2D();
        this.clickGuiScreen = new Menu();
        Menu.selectedTheme = this.guiManager.getCurrentTheme();
        Menu.preSelectedTheme = this.guiManager.getCurrentTheme();
        VeloTuneManager.init();
        VeloTuneModule veloTuneModule = this.moduleManager.get(VeloTuneModule.class);
        if (veloTuneModule != null) {
            veloTuneModule.loadFromConfig();
        }
        CosmeticManager.getInstance().init();
        MaksaiCosmetics.initialize();
        CustomPetCosmetics.initialize(CosmeticManager.getInstance());
        MeleeSounds.initialize();
        MeleeRegistry.registerCosmeticEntries();
        ItemSkinRegistry.registerCosmeticEntries();
        this.configManager.applyPendingCosmetics();
        EffectSounds effectSounds = this.moduleManager.get(EffectSounds.class);
        if (effectSounds != null && effectSounds.isEnabled()) {
            effectSounds.playGreetingOnStart();
        }
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (effectSounds != null) {
                effectSounds.tickUiSounds();
            }
            AppleEatingEffects.tick(client);
            ItemSkinEffects.tick(client);
            if (MeleeSettings.get().pollInspectPressed() && MeleeRules.active()) {
                MeleeController.getInstance().onInspect();
            }
        });
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof class_1007) {
                class_1007 playerRenderer = (class_1007)entityRenderer;
                registrationHelper.register((class_3887)new CosmeticFeatureRenderer((class_3883<class_10055, class_591>)playerRenderer));
                registrationHelper.register((class_3887)new HatLayer((class_3883<class_10055, class_591>)playerRenderer));
                registrationHelper.register((class_3887)new ChinaHatFeatureRenderer((class_3883<class_10055, class_591>)playerRenderer));
                System.out.println("[DecideVisual Cosmetics] CosmeticFeatureRenderer \u0437\u0430\u0440\u0435\u0433\u0438\u0441\u0442\u0440\u0438\u0440\u043e\u0432\u0430\u043d");
            } else {
                System.out.println("[DecideVisual Cosmetics] \u0440\u0435\u043d\u0434\u0435\u0440\u0435\u0440 \u043d\u0435 PlayerEntityRenderer: " + String.valueOf(entityRenderer));
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(this::unload));
    }

    public void unload() {
        if (this.configManager != null) {
            this.configManager.autoSave();
        }
    }

    @Generated
    public ModuleManager moduleManager() {
        return this.moduleManager;
    }

    @Generated
    public Render2D render2D() {
        return this.render2D;
    }

    @Generated
    public Menu clickGuiScreen() {
        return this.clickGuiScreen;
    }

    @Generated
    public ComponentManager componentManager() {
        return this.componentManager;
    }

    @Generated
    public CommandManager commandManager() {
        return this.commandManager;
    }

    @Generated
    public ConfigManager configManager() {
        return this.configManager;
    }

    @Generated
    public FriendManager friendManager() {
        return this.friendManager;
    }

    @Generated
    public InventoryPresetManager inventoryPresetManager() {
        return this.inventoryPresetManager;
    }

    @Generated
    public RPC rpc() {
        return this.rpc;
    }

    @Generated
    public GuiManager guiManager() {
        return this.guiManager;
    }

    @Generated
    public static EventBus eventHandler() {
        return eventHandler;
    }

    static {
        eventHandler = EventBus.threadSafe();
        build = "5.0";
    }
}

