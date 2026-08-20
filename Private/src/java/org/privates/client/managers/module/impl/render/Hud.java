package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.events.orbit.EventPriority;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.*;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.screen.hud.impl.*;
import org.privates.client.screen.notification.Notification;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.draw.RenderUtil;

import static org.lwjgl.glfw.GLFW.*;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Hud", category = Category.RENDER, description = "Интерфейс клиента", autoEnabled = true, allowDisable = false)
public class Hud extends Module {
    public static Hud getInstance() {
        return Instance.get(Hud.class);
    }

    private final WatermarkRenderer watermarkRenderer;
    private final TargetHudRenderer targetHudRenderer;
    private final TargetHudRock tHudRenderer2;
    private final THudRenderer2 tHudRenderer22;
    private final THudRenderer3 tHudRenderer3;
    private final TargetHudMinimal targeTHudRenderer2;
    private final TargetHudMinimal2 targeTHudRenderer1;
    private final TargetHudMinimal3 targeTHudRenderer3;
    private final TargetHudRenderer4 targeTHudRenderer4;
    private final TargetHudRenderer5 targetHudRenderer5;
    private final TargetHudFigma targetHudFigma;
    private final TargetHudExpensive targetHudExpensive;
    private final TargetHudHelsing targetHudHelsing;
    private final TargetHudSilent targetHudSilent;
    private final ItemCountRenderer itemCountRenderer;

    private final KeybindsRenderer keybindsRenderer;
    private final PotionsRenderer potionsRenderer;
    private final InformationRenderer informationRenderer;
    private final ItemBindRenderer itemBindRenderer;
    private final ScoreBoardRenderer scoreBoardRenderer;
    private final InventoryHudRenderer inventoryHudRenderer;
    private final ArrayListRenderer arrayListRenderer;
    private final ArmorHudRenderer armorHudRenderer;
    private final ArmorSmartRenderer armorSmartRenderer;
    private final StaffListRenderer staffListRenderer;
    private final CooldownRenderer cooldownRenderer;
    private final KeystrokesRenderer keystrokesRenderer;
    private final MotionGraphRenderer motionGraphRenderer;
    private final BossBarRenderer bossBarRenderer;
    private final NearbyPlayersRenderer nearbyPlayersRenderer;
    private final LeftHandItemRenderer leftHandItemRenderer;
    private final HotbarRenderer hotbarRenderer;
    private final HealthRenderer healthRenderer;
    private final ArmorRenderer armorRenderer;
    private final HungerRenderer hungerRenderer;
    private final DynamicIslandRenderer dynamicIslandRenderer;
    private final MusicHudRenderer musicHudRenderer;

    public final MultiBooleanSetting checks = new MultiBooleanSetting(this, "Элементы",
            BooleanSetting.of("Watermark", true),
            BooleanSetting.of("TargetHud", true),
            BooleanSetting.of("Keybinds", true),
            BooleanSetting.of("ArrayList", true),
            BooleanSetting.of("Potions", true),
            BooleanSetting.of("Information", true),
            BooleanSetting.of("ItemBinds", true),
            BooleanSetting.of("ItemCount", true),
            BooleanSetting.of("ScoreBoard", false),
            BooleanSetting.of("ArmorHud", true),
            BooleanSetting.of("StaffList", true),
            BooleanSetting.of("InventoryHud", true),
            BooleanSetting.of("Cooldowns", true),
            BooleanSetting.of("Keystrokes", false),
            BooleanSetting.of("MotionGraph", false),
            BooleanSetting.of("NearbyPlayers", false),
            BooleanSetting.of("BossBar", false),
            BooleanSetting.of("LeftHandItem", true),
            BooleanSetting.of("MusicHud", false),
            BooleanSetting.of("CustomHotbar", false),
            BooleanSetting.of("CustomHealth", false),
            BooleanSetting.of("CustomArmor", false),
            BooleanSetting.of("CustomHunger", false)
    );

    private final ModeSetting armorHudMode = new ModeSetting(this, "ArmorHud Mode", "Обычный", "Клиентский")
            .setVisible(() -> checks.getValue("ArmorHud"));

    private final ModeSetting targetHudMode = new ModeSetting(this, "TargetHud Mode", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
    )
            .setVisible(() -> checks.getValue("TargetHud"));

    private final ModeSetting targetHudDisplay = new ModeSetting(this, "TargetHud Display", "Screen", "World")
            .setVisible(() -> checks.getValue("TargetHud"));

    private final BooleanSetting blurOptimization = new BooleanSetting(this, "Оптимизировать размытие", false);

    private final DragSetting targetHudRendererDrag = new DragSetting(this, "TargetHud");
    private final DragSetting targetHudRenderer22Drag = new DragSetting(this, "TargetHud");
    private final DragSetting targeTHudRenderer2Drag = new DragSetting(this, "TargetHud");
    private final DragSetting targeTHudRenderer3Drag = new DragSetting(this, "TargetHud");
    private final DragSetting targeTHudRenderer4Drag = new DragSetting(this, "TargetHud");
    private final DragSetting targeTHudRenderer5Drag = new DragSetting(this, "TargetHud");
    private final DragSetting targeTHudRenderer6Drag = new DragSetting(this, "TargetHud");
    private final DragSetting targeTHudRenderer7Drag = new DragSetting(this, "TargetHud");
    private final DragSetting targetHudFigmaDrag = new DragSetting(this, "TargetHud");
    private final DragSetting targetHudHexagonDrag = new DragSetting(this, "TargetHud");
    private final DragSetting targetHudExpensiveDrag = new DragSetting(this, "TargetHud");
    private final DragSetting targetHudHelsingDrag = new DragSetting(this, "TargetHud");
    private final DragSetting targetHudSilentDrag = new DragSetting(this, "TargetHud");

    private final DragSetting cooldownsDrag = new DragSetting(this, "Cooldowns");
    private final DragSetting keybindsRendererDrag = new DragSetting(this, "Keybinds");
    private final DragSetting potionsRendererDrag = new DragSetting(this, "Potions");
    private final DragSetting itemBindRendererDrag = new DragSetting(this, "ItemBinds");
    private final DragSetting scoreBoardDrag = new DragSetting(this, "ScoreBoard");
    private final DragSetting staffListDrag = new DragSetting(this, "StaffList");
    private final DragSetting arrayListDrag = new DragSetting(this, "ArrayList");
    private final DragSetting inventoryHudDrag = new DragSetting(this, "InventoryHud");
    private final DragSetting watermarkDrag = new DragSetting(this, "Watermark");
    private final DragSetting infoDrag = new DragSetting(this, "Information");
    private final DragSetting armorHudDrag = new DragSetting(this, "ArmorHud");
    private final DragSetting motionGraphDrag = new DragSetting(this, "MotionGraph");
    private final DragSetting itemCountHudDrag = new DragSetting(this, "ItemCount");

    private final DragSetting keystrokesDrag = new DragSetting(this, "Keystrokes");
    private final DragSetting displayHelperDrag = new DragSetting(this, "DisplayHelper");
    private final DragSetting bossBarDrag = new DragSetting(this, "BossBar");
    private final DragSetting nearbyPlayersDrag = new DragSetting(this, "NearbyPlayers");
    private final DragSetting leftHandItemDrag = new DragSetting(this, "LeftHandItem");
    private final DragSetting dynamicIslandDrag = new DragSetting(this, "DynamicIsland");
    private final DragSetting musicHudDrag = new DragSetting(this, "MusicHud");


    public Hud() {
        targetHudRenderer = new TargetHudRenderer(targetHudRendererDrag);
        targeTHudRenderer2 = new TargetHudMinimal(targeTHudRenderer2Drag);
        targeTHudRenderer3 = new TargetHudMinimal3(targeTHudRenderer3Drag);
        targeTHudRenderer4 = new TargetHudRenderer4(targeTHudRenderer4Drag);
        targetHudRenderer5 = new TargetHudRenderer5(targeTHudRenderer7Drag);
        tHudRenderer2 = new TargetHudRock(targeTHudRenderer5Drag);
        targeTHudRenderer1 = new TargetHudMinimal2(targeTHudRenderer5Drag);
        tHudRenderer3 = new THudRenderer3(targeTHudRenderer6Drag);
        tHudRenderer22 = new THudRenderer2(targetHudRenderer22Drag);
        targetHudFigma = new TargetHudFigma(targetHudFigmaDrag);
        targetHudExpensive = new TargetHudExpensive(targetHudExpensiveDrag);
        targetHudHelsing = new TargetHudHelsing(targetHudHelsingDrag);
        targetHudSilent = new TargetHudSilent(targetHudSilentDrag);
        cooldownRenderer = new CooldownRenderer(cooldownsDrag);

        itemCountRenderer = new ItemCountRenderer(itemCountHudDrag);
        keybindsRenderer = new KeybindsRenderer(keybindsRendererDrag);
        potionsRenderer = new PotionsRenderer(potionsRendererDrag);
        itemBindRenderer = new ItemBindRenderer(itemBindRendererDrag);
        scoreBoardRenderer = new ScoreBoardRenderer(scoreBoardDrag);
        watermarkRenderer = new WatermarkRenderer(watermarkDrag);
        inventoryHudRenderer = new InventoryHudRenderer(inventoryHudDrag);

        arrayListRenderer = new ArrayListRenderer(arrayListDrag);
        informationRenderer = new InformationRenderer(infoDrag);
        armorHudRenderer = new ArmorHudRenderer(armorHudDrag);
        armorSmartRenderer = new ArmorSmartRenderer(armorHudDrag);
        staffListRenderer = new StaffListRenderer(staffListDrag);
        keystrokesRenderer = new KeystrokesRenderer(keystrokesDrag);
        motionGraphRenderer = new MotionGraphRenderer(motionGraphDrag);

        bossBarRenderer = new BossBarRenderer(bossBarDrag);

        nearbyPlayersRenderer = new NearbyPlayersRenderer(nearbyPlayersDrag);

        leftHandItemRenderer = new LeftHandItemRenderer(leftHandItemDrag);

        hotbarRenderer = new HotbarRenderer();
        healthRenderer = new HealthRenderer();
        armorRenderer = new ArmorRenderer();
        hungerRenderer = new HungerRenderer();
        dynamicIslandRenderer = new DynamicIslandRenderer(dynamicIslandDrag);
        musicHudRenderer = new MusicHudRenderer(musicHudDrag);

    }

    private final float cachedBlurStrength = -1f;
    private boolean settingsDirty = true;

    private void updateBlurAndStrengthSettings() {
        if (!settingsDirty) return;
        float strength = Theme.getInstance().blurStrength().getValue().floatValue();
        if (strength == cachedBlurStrength) {
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(Render2DEvent event) {
        if (glfwGetWindowAttrib(mc.getMainWindow().getHandle(), GLFW_FOCUSED) != GLFW_TRUE) return;

        settingsDirty = true;
        updateBlurAndStrengthSettings();

        if (blurOptimization.getValue()) {
            RenderUtil.CustomStyled.renderBlur(Theme.getInstance().blurStrength().getValue().intValue());
            RenderUtil.CustomStyled.renderRects();
            RenderUtil.CustomStyled.clear();
        }

        if (checks.getValue("Watermark")) watermarkRenderer.render(event);
        if (checks.getValue("Information")) informationRenderer.render(event);
        if (checks.getValue("ArmorHud")) {
            if (armorHudMode.getValue().equals("Обычный")) {
                armorSmartRenderer.render(event);
            } else {
                armorHudRenderer.render(event);
            }
        }

        // Render TargetHud only in Screen mode
        if (checks.getValue("TargetHud") && targetHudDisplay.getValue().equals("Screen")) {
            switch (targetHudMode.getValue()) {
                case "1" -> targetHudRenderer.render(event);
                case "2" -> targeTHudRenderer2.render(event);
                case "3" -> targeTHudRenderer1.render(event);
                case "4" -> targeTHudRenderer3.render(event);
                case "5" -> targetHudRenderer5.render(event);
                case "6" -> tHudRenderer2.render(event);
                case "7" -> tHudRenderer3.render(event);
                case "8" -> targetHudFigma.render(event);
                case "9" -> tHudRenderer22.render(event);
                case "10" -> targetHudExpensive.render(event);
                case "11" -> targetHudHelsing.render(event);
                case "12" -> targetHudSilent.render(event);
            }
        }

        if (checks.getValue("Keybinds")) keybindsRenderer.render(event);
        if (checks.getValue("Potions")) potionsRenderer.render(event);
        if (checks.getValue("ItemBinds")) itemBindRenderer.render(event);
        if (checks.getValue("StaffList")) staffListRenderer.render(event);
        if (checks.getValue("InventoryHud")) inventoryHudRenderer.render(event);
        if (checks.getValue("Cooldowns")) cooldownRenderer.render(event);
        if (checks.getValue("Keystrokes")) keystrokesRenderer.render(event);
        if (checks.getValue("ArrayList")) arrayListRenderer.render(event);
        if (checks.getValue("NearbyPlayers")) nearbyPlayersRenderer.render(event);

        if (checks.getValue("MotionGraph")) motionGraphRenderer.render(event);
        if (checks.getValue("BossBar")) bossBarRenderer.render(event);
        if (checks.getValue("LeftHandItem")) leftHandItemRenderer.render(event);
        if (checks.getValue("DynamicIsland")) dynamicIslandRenderer.render(event);
        if (checks.getValue("MusicHud")) musicHudRenderer.render(event);

        renderNotifications(event);
    }

    private void renderNotifications(Render2DEvent event) {
        float offsetY = mc.getMainWindow().getScaledHeight() / 2f + 30;

        for (Notification notification : Excellent.inst().notifManager().getNotifications()) {
            if (System.currentTimeMillis() - notification.getTime() >= notification.getMs()) {
                notification.setScale(0);
            }

            if (notification.getScale() == 0 && notification.getScaleV() <= .1f) {
                Excellent.inst().notifManager().getNotifications().remove(notification);
                return;
            }

            notification.render(event.getMatrix(), offsetY);
            offsetY += notification.getHeight() * notification.getScaleV() + 1.5f;
        }
    }

    @EventHandler
    public void onEvent(RenderScoreBoardEvent event) {
        if (glfwGetWindowAttrib(mc.getMainWindow().getHandle(), GLFW_FOCUSED) != GLFW_TRUE) return;
        if (checks.getValue("ScoreBoard")) scoreBoardRenderer.renderScoreBoard(event);
    }

    @EventHandler
    public void onEvent(AttackEvent event) {
        if (glfwGetWindowAttrib(mc.getMainWindow().getHandle(), GLFW_FOCUSED) != GLFW_TRUE) return;
        if (checks.getValue("TargetHud")) {
            switch (targetHudMode.getValue()) {
                case "1" -> targetHudRenderer.attack(event);
                case "2" -> targeTHudRenderer2.attack(event);
                case "3" -> targeTHudRenderer3.attack(event);
                case "5" -> targetHudRenderer5.attack(event);
                case "10" -> targetHudExpensive.attack(event);
                case "11" -> targetHudHelsing.attack(event);
                case "12" -> targetHudSilent.attack(event);
            }
        }
    }

    public DragSetting getElementDrag(String elementName) {
        return switch (elementName.toLowerCase()) {
            case "watermark" -> watermarkDrag;
            case "targethud" -> targetHudRendererDrag;
            case "keybinds" -> keybindsRendererDrag;
            case "potions" -> potionsRendererDrag;
            case "itembinds" -> itemBindRendererDrag;
            case "information" -> infoDrag;
            case "scoreboard" -> scoreBoardDrag;
            case "inventoryhud" -> inventoryHudDrag;
            case "arraylist" -> arrayListDrag;
            case "armorhud" -> armorHudDrag;
            case "stafflist" -> staffListDrag;
            case "nearbyplayers" -> nearbyPlayersDrag;
            case "cooldowns" -> cooldownsDrag;
            case "itemcount" -> itemCountHudDrag;
            case "keystrokes" -> keystrokesDrag;
            case "displayhelper" -> displayHelperDrag;
            case "motiongraph" -> motionGraphDrag;
            case "bossbar" -> bossBarDrag;
            case "dynamicisland" -> dynamicIslandDrag;
            case "musichud" -> musicHudDrag;
            default -> null;
        };
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (glfwGetWindowAttrib(mc.getMainWindow().getHandle(), GLFW_FOCUSED) != GLFW_TRUE) return;
        if (checks.getValue("MusicHud")) musicHudRenderer.mousePress(event);
    }


    @EventHandler
    public void onRenderHotbar(RenderHotbarEvent event) {
        if (checks.getValue("CustomHotbar")) {
            hotbarRenderer.renderHotbar(event);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRenderHealth(RenderHealthEvent event) {
        if (checks.getValue("CustomHealth")) {
            healthRenderer.renderHealth(event);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRenderArmor(RenderArmorEvent event) {
        if (checks.getValue("CustomArmor")) {
            armorRenderer.renderArmor(event);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRenderHunger(RenderHungerEvent event) {
        if (checks.getValue("CustomHunger")) {
            hungerRenderer.renderHunger(event);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (glfwGetWindowAttrib(mc.getMainWindow().getHandle(), GLFW_FOCUSED) != GLFW_TRUE) return;

        // Render TargetHud in World mode
        if (checks.getValue("TargetHud") && targetHudDisplay.getValue().equals("World")) {
            switch (targetHudMode.getValue()) {
                case "1" -> targetHudRenderer.renderInWorld(event);
                case "2" -> targeTHudRenderer2.renderInWorld(event);
                case "3" -> targeTHudRenderer1.renderInWorld(event);
                case "4" -> targeTHudRenderer3.renderInWorld(event);
                case "5" -> targetHudRenderer5.renderInWorld(event);
                case "6" -> tHudRenderer2.renderInWorld(event);
                case "7" -> tHudRenderer3.renderInWorld(event);
                case "8" -> targetHudFigma.renderInWorld(event);
                case "9" -> tHudRenderer22.renderInWorld(event);
                case "10" -> targetHudExpensive.renderInWorld(event);
                case "11" -> targetHudHelsing.renderInWorld(event);
                case "12" -> targetHudSilent.renderInWorld(event);
            }
        }
    }
}
