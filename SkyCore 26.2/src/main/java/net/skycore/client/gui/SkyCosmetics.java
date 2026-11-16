package net.skycore.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.skycore.client.module.render.CapeModule;
import net.skycore.client.module.render.CosmeticsModule;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.StringSetting;
import net.skycore.client.SkycoreClient;

/**
 * Единый менеджер косметики: объединяет косметику SkyCore (крила, рюкзаки, шапки,
 * маски, питомцы, мечи, плащ с физикой) с косметикой Arcanic (Wearables, Capes,
 * Emotes) в одном интерфейсе для ArcanicMenu.
 *
 * <p>Все настройки хранятся в существующих модулях SkyCore
 * ({@link CosmeticsModule}, {@link CapeModule}), этот класс только предоставляет
 * унифицированный доступ из меню Arcanic.
 *
 * <p>Адаптировано из Arcanic Visuals ({@code net.arcanic.visuals.cosmetic.Wearables},
 * {@code net.arcanic.visuals.arc.Capes}, {@code net.arcanic.visuals.emote.Emotes})
 * и SkyCore ({@code net.skycore.client.module.render.CosmeticsModule},
 * {@code net.skycore.client.module.render.CapeModule}).
 */
public final class SkyCosmetics {

    private SkyCosmetics() {
    }

    /** Слоты косметики (как в Arcanic Wearables.SLOTS). */
    public static final String[] SLOTS = {"wings", "backpacks", "hats", "masks", "pets"};

    /** Категории косметики для меню. */
    public enum Category {
        WINGS("wings", "Крила"),
        BACKPACKS("backpacks", "Рюкзаки"),
        HATS("hats", "Шапки"),
        MASKS("masks", "Маски"),
        PETS("pets", "Питомцы"),
        CAPE("cape", "Плащ");

        public final String key;
        public final String label;

        Category(String key, String label) {
            this.key = key;
            this.label = label;
        }
    }

    // ---------- Настройки для UI (проксируют CosmeticsModule / CapeModule) ----------

    public final StringSetting wings = new StringSetting("wings", "", 64);
    public final StringSetting backpacks = new StringSetting("backpacks", "", 64);
    public final StringSetting hats = new StringSetting("hats", "", 64);
    public final StringSetting masks = new StringSetting("masks", "", 64);
    public final StringSetting pets = new StringSetting("pets", "", 64);
    public final StringSetting sword = new StringSetting("sword", "", 64);
    public final StringSetting swordModel = new StringSetting("swordModel", "", 64);
    public final BooleanSetting capeEnabled = new BooleanSetting("cape", false);
    public final ModeSetting capeMovement = new ModeSetting("capeMovement", "Vanilla", "Vanilla", "Simulation");
    public final NumberSetting capeGravity = new NumberSetting("capeGravity", 25.0, 5.0, 50.0, 1.0);
    public final BooleanSetting capeWind = new BooleanSetting("capeWind", false);

    /** Все настройки косметики для отображения в меню. */
    public final List<net.skycore.client.setting.Setting<?>> allSettings = new ArrayList<>();

    /** Текущий выбранный дизайн по каждой категории. */
    private static final Map<String, String> selected = new java.util.HashMap<>();

    static {
        for (Category c : Category.values()) {
            selected.put(c.key, "");
        }
    }

    /** Косметический модуль SkyCore (леннивый поиск). */
    private static CosmeticsModule cosmetics() {
        return net.skycore.client.SkycoreClient.modules()
                .get(CosmeticsModule.class).orElse(null);
    }

    /** Модуль плаща SkyCore (леннивый поиск). */
    private static CapeModule cape() {
        return net.skycore.client.SkycoreClient.modules()
                .get(CapeModule.class).orElse(null);
    }

    /** Синхронизирует настройки UI с состоянием модулей SkyCore. */
    public static void syncFromModules() {
        CosmeticsModule cm = cosmetics();
        CapeModule cp = cape();
        if (cm == null && cp == null) return;

        // Синхронизируем через экземпляр SkyCosmetics, который создан в SkycoreClient
        var inst = INSTANCE;
        if (inst == null) return;

        if (cm != null) {
            inst.wings.set(cm.selected("wings"));
            inst.backpacks.set(cm.selected("backpacks"));
            inst.hats.set(cm.selected("hats"));
            inst.masks.set(cm.selected("masks"));
            inst.pets.set(cm.selected("pets"));
            inst.sword.set(cm.selectedSword());
            inst.swordModel.set(cm.selectedSwordModel());
        }
        if (cp != null) {
            inst.capeEnabled.set(cp.isEnabled());
            inst.capeMovement.set(cp.movement.is("Vanilla") ? "Vanilla" : "Simulation");
            inst.capeGravity.set((float) cp.gravity.get());
            inst.capeWind.set(cp.wind.get());
        }
    }

    /** Синхронизирует состояние модулей SkyCore из настроек UI. */
    public static void syncToModules() {
        CosmeticsModule cm = cosmetics();
        CapeModule cp = cape();
        var inst = INSTANCE;
        if (inst == null) return;

        if (cm != null) {
            cm.select("wings", inst.wings.get());
            cm.select("backpacks", inst.backpacks.get());
            cm.select("hats", inst.hats.get());
            cm.select("masks", inst.masks.get());
            cm.select("pets", inst.pets.get());
            cm.selectSword(inst.sword.get(), inst.swordModel.get());
        }
        if (cp != null) {
            cp.setEnabled(inst.capeEnabled.get());
            cp.movement.set(inst.capeMovement.get());
            cp.gravity.set(inst.capeGravity.get());
            cp.wind.set(inst.capeWind.get());
            cp.applyConfig();
        }
    }

    // ---------- Singleton ----------

    private static SkyCosmetics INSTANCE;

    static SkyCosmetics instance() {
        if (INSTANCE == null) INSTANCE = new SkyCosmetics();
        return INSTANCE;
    }

    static {
        var inst = instance();
        inst.allSettings.add(inst.wings);
        inst.allSettings.add(inst.backpacks);
        inst.allSettings.add(inst.hats);
        inst.allSettings.add(inst.masks);
        inst.allSettings.add(inst.pets);
        inst.allSettings.add(inst.sword);
        inst.allSettings.add(inst.swordModel);
        inst.allSettings.add(inst.capeEnabled);
        inst.allSettings.add(inst.capeMovement);
        inst.allSettings.add(inst.capeGravity);
        inst.allSettings.add(inst.capeWind);
    }

    // ---------- Доступ к настройкам ----------

    /** Возвращает настройку для категории. */
    public static net.skycore.client.setting.Setting<?> setting(Category cat) {
        var inst = instance();
        return switch (cat) {
            case WINGS -> inst.wings;
            case BACKPACKS -> inst.backpacks;
            case HATS -> inst.hats;
            case MASKS -> inst.masks;
            case PETS -> inst.pets;
            case CAPE -> inst.capeEnabled;
        };
    }

    /** Возвращает выбранный дизайн для категории SkyCore. */
    public static String getSkyCore(String category) {
        CosmeticsModule cm = cosmetics();
        if (cm == null) return selected.getOrDefault(category, "");
        return cm.selected(category);
    }

    /** Устанавливает дизайн для категории SkyCore. */
    public static void setSkyCore(String category, String design) {
        CosmeticsModule cm = cosmetics();
        if (cm == null) {
            selected.put(category, design);
            return;
        }
        cm.select(category, design);
        selected.put(category, design);
    }

    /** Возвращает выбранный меч (SkyCore). */
    public static String getSword() {
        CosmeticsModule cm = cosmetics();
        return cm == null ? "" : cm.selectedSword();
    }

    /** Устанавливает меч (SkyCore). */
    public static void setSword(String sword, String model) {
        CosmeticsModule cm = cosmetics();
        if (cm != null) cm.selectSword(sword, model);
    }

    // ---------- Cape ----------

    /** Возвращает включён ли плащ. */
    public static boolean isCapeEnabled() {
        CapeModule cm = cape();
        return cm != null && cm.isEnabled();
    }

    /** Включает/выключает плащ. */
    public static void setCapeEnabled(boolean enabled) {
        CapeModule cm = cape();
        if (cm != null) cm.setEnabled(enabled);
    }

    /** Режим движения плаща: Vanilla / Simulation. */
    public static String getCapeMovement() {
        CapeModule cm = cape();
        return cm == null ? "Vanilla" : (cm.movement.is("Vanilla") ? "Vanilla" : "Simulation");
    }

    /** Устанавливает режим движения плаща. */
    public static void setCapeMovement(String mode) {
        CapeModule cm = cape();
        if (cm == null) return;
        cm.movement.set(mode);
        cm.applyConfig();
    }

    /** Сила гравитации плаща. */
    public static float getCapeGravity() {
        CapeModule cm = cape();
        return cm == null ? 25.0f : (float) cm.gravity.get();
    }

    /** Устанавливает силу гравитации плаща. */
    public static void setCapeGravity(float gravity) {
        CapeModule cm = cape();
        if (cm == null) return;
        cm.gravity.set((double) gravity);
        cm.applyConfig();
    }

    /** Ветер плаща включён. */
    public static boolean isCapeWind() {
        CapeModule cm = cape();
        return cm != null && cm.wind.get();
    }

    /** Включить/выключить ветер плаща. */
    public static void setCapeWind(boolean wind) {
        CapeModule cm = cape();
        if (cm == null) return;
        cm.wind.set(wind);
        cm.applyConfig();
    }

    // ---------- Unified access ----------

    /**
     * Возвращает выбранный дизайн для категории (SkyCore или Arcanic).
     */
    public static String get(Category cat) {
        return getSkyCore(cat.key);
    }

    /**
     * Устанавливает дизайн для категории.
     */
    public static void set(Category cat, String design) {
        setSkyCore(cat.key, design);
    }

    /** Все доступные дизайны для категории (заглушка — загружаются из каталога ресурсов). */
    public static List<String> designs(Category cat) {
        // В будущем здесь будет чтение из skycore:cosmetics_port/catalog.json
        // и arcanic wear/index.json
        return List.of("—");
    }

    /** Иконка косметики для предпросмотра в меню. */
    public static String iconKey(Category cat) {
        return switch (cat) {
            case WINGS -> "w.swatch";
            case BACKPACKS -> "w.folder";
            case HATS -> "w.bookmark";
            case MASKS -> "w.headset";
            case PETS -> "w.plus";
            case CAPE -> "w.bolt";
        };
    }
}
