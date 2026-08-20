package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.GraphicsFanciness;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.tileentity.TileEntity;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.RenderEntityEvent;
import org.privates.client.managers.events.render.RenderShadowEvent;
import org.privates.client.managers.events.render.RenderTileEntityEvent;
import org.privates.client.managers.events.render.RenderWorldLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Optimizer", category = Category.MISC, description = "Оптимизирует игру")
public class Optimizer extends Module {
    public static Optimizer getInstance() {
        return Instance.get(Optimizer.class);
    }

    private final MultiBooleanSetting renderSettings = new MultiBooleanSetting(this, "Настройки рендера",
            BooleanSetting.of("Отключить плавное освещение", true),
            BooleanSetting.of("Отключить тени сущностей", true),
            BooleanSetting.of("Ускоренная графика", true),
            BooleanSetting.of("Отключить покачивание камеры", false),
            BooleanSetting.of("Отключить Mipmap-уровни", false)
    );

    private final MultiBooleanSetting entityOptimizations = new MultiBooleanSetting(this, "Оптимизация сущностей",
            BooleanSetting.of("Скрывать сущности на дистанции", true),
            BooleanSetting.of("Лимит предметов на земле", true),
            BooleanSetting.of("Лимит сфер опыта", true)
    );
    private final SliderSetting entityCullingDistance = new SliderSetting(this, "Дистанция скрытия сущностей", 32, 8, 64, 1).setVisible(() -> entityOptimizations.getValue("Скрывать сущности на дистанции"));
    private final SliderSetting itemLimit = new SliderSetting(this, "Лимит предметов", 20, 1, 100, 1).setVisible(() -> entityOptimizations.getValue("Лимит предметов на земле"));
    private final SliderSetting orbLimit = new SliderSetting(this, "Лимит сфер опыта", 20, 1, 100, 1).setVisible(() -> entityOptimizations.getValue("Лимит сфер опыта"));

    private final MultiBooleanSetting worldOptimizations = new MultiBooleanSetting(this, "Оптимизация мира",
            BooleanSetting.of("Скрывать блочные сущности", true),
            BooleanSetting.of("Отключить частицы", true),
            BooleanSetting.of("Отключить частицы взрывов", true),
            BooleanSetting.of("Отключить погоду", true)
    );
    private final SliderSetting tileEntityCullingDistance = new SliderSetting(this, "Дистанция скрытия блоков", 32, 8, 64, 1).setVisible(() -> worldOptimizations.getValue("Скрывать блочные сущности"));

    private boolean originalViewBobbing;
    private AmbientOcclusionStatus originalSmoothLighting;
    private boolean originalEntityShadows;
    private GraphicsFanciness originalGraphicsFanciness;
    private int originalMipmapLevels;

    private int itemRenderCount;
    private int experienceOrbRenderCount;

    @Override
    public void onEnable() {
        originalViewBobbing = mc.gameSettings.viewBobbing;
        originalSmoothLighting = mc.gameSettings.ambientOcclusionStatus;
        originalEntityShadows = mc.gameSettings.entityShadows;
        originalGraphicsFanciness = mc.gameSettings.graphicFanciness;
        originalMipmapLevels = mc.gameSettings.mipmapLevels;
    }

    @Override
    public void onDisable() {
        if (mc.gameSettings == null) return;
        mc.gameSettings.viewBobbing = originalViewBobbing;
        mc.gameSettings.ambientOcclusionStatus = originalSmoothLighting;
        mc.gameSettings.entityShadows = originalEntityShadows;
        mc.gameSettings.graphicFanciness = originalGraphicsFanciness;
        mc.gameSettings.mipmapLevels = originalMipmapLevels;
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (renderSettings.getValue("Отключить покачивание камеры")) {
            if (mc.gameSettings.viewBobbing) mc.gameSettings.viewBobbing = false;
        }
        if (renderSettings.getValue("Отключить плавное освещение")) {
            if (mc.gameSettings.ambientOcclusionStatus != AmbientOcclusionStatus.OFF)
                mc.gameSettings.ambientOcclusionStatus = AmbientOcclusionStatus.OFF;
        }
        if (renderSettings.getValue("Отключить тени сущностей")) {
            if (mc.gameSettings.entityShadows) mc.gameSettings.entityShadows = false;
        }
        if (renderSettings.getValue("Ускоренная графика")) {
            if (mc.gameSettings.graphicFanciness != GraphicsFanciness.FAST)
                mc.gameSettings.graphicFanciness = GraphicsFanciness.FAST;
        }
        if (renderSettings.getValue("Отключить Mipmap-уровни")) {
            if (mc.gameSettings.mipmapLevels != 0) mc.gameSettings.mipmapLevels = 0;
        }

        if (worldOptimizations.getValue("Отключить погоду") && mc.world != null) {
            if (mc.world.isRaining()) mc.world.setRainStrength(0.0f);
            if (mc.world.isThundering()) mc.world.setThunderStrength(0.0f);
        }
    }

    @EventHandler
    public void onEvent(RenderWorldLastEvent event) {
        itemRenderCount = 0;
        experienceOrbRenderCount = 0;
    }

    @EventHandler
    public void onEvent(PacketEvent event) {
        if (worldOptimizations.getValue("Отключить частицы") && event.getPacket() instanceof SExplosionPacket) {
            event.cancel();
        }
        if (worldOptimizations.getValue("Отключить частицы взрывов") && event.getPacket() instanceof SExplosionPacket) {
            event.cancel();
        }
    }

    @EventHandler
    public void onEvent(RenderEntityEvent.Pre event) {
        if (mc.player == null) return;

        double distanceSq = mc.player.getDistanceSq(event.getEntity());

        if (entityOptimizations.getValue("Скрывать сущности на дистанции")) {
            double cullDistance = entityCullingDistance.getValue();
            if (distanceSq > cullDistance * cullDistance) {
                event.cancel();
                return;
            }
        }

        if (entityOptimizations.getValue("Лимит предметов на земле") && event.getEntity() instanceof ItemEntity) {
            if (itemRenderCount >= itemLimit.getValue()) {
                event.cancel();
            } else {
                itemRenderCount++;
            }
        }

        if (entityOptimizations.getValue("Лимит сфер опыта") && event.getEntity() instanceof ExperienceOrbEntity) {
            if (experienceOrbRenderCount >= orbLimit.getValue()) {
                event.cancel();
            } else {
                experienceOrbRenderCount++;
            }
        }
    }

    @EventHandler
    public void onEvent(RenderShadowEvent event) {
        if (renderSettings.getValue("Отключить тени сущностей")) {
            event.cancel();
        }
    }

    @EventHandler
    public void onEvent(RenderTileEntityEvent.Pre event) {
        if (mc.player == null) return;

        if (worldOptimizations.getValue("Скрывать блочные сущности")) {
            TileEntity tileEntity = event.getTileEntity();
            double cullDistance = tileEntityCullingDistance.getValue();
            double distanceSq = tileEntity.getPos().distanceSq(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), true);
            if (distanceSq > cullDistance * cullDistance) {
                event.cancel();
            }
        }
    }
}