// deobf by pusia
package su.catlean.client.features.modules.esp;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class PearlPhaseEsp extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(PearlPhaseEsp.class, "range", "getRange()F", 0), new PropertyReference1Impl(PearlPhaseEsp.class, "safe", "getSafe()Ljava/awt/Color;", 0), new PropertyReference1Impl(PearlPhaseEsp.class, "unsafe", "getUnsafe()Ljava/awt/Color;", 0) };
   @NotNull
   public static final PearlPhaseEsp INSTANCE = new PearlPhaseEsp();
   @NotNull
   private static final FloatSetting range = SettingFactory.defaultImpl(INSTANCE, "range", 1.0F, RangesKt.rangeTo(0.01F, 3.0F), null, null, 24, null);
   @NotNull
   private static final ColorSetting safe = SettingFactory.defaultImpl(INSTANCE, "safe", new Color(255, 255, 255, 60), null, null, 12, null);
   @NotNull
   private static final ColorSetting unsafe = SettingFactory.defaultImpl(INSTANCE, "unsafe", new Color(255, 100, 100, 60), null, null, 12, null);

   private PearlPhaseEsp() {
      super("pearl-phase-esp", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final float getRange() {
      return ((Number)range.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   private final Color getSafe() {
      return (Color)safe.computeObject(this, $$delegatedProperties[1]);
   }

   private final Color getUnsafe() {
      return (Color)unsafe.computeObject(this, $$delegatedProperties[2]);
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (MinecraftHolder.getLocalPlayer().onGround()
         && MinecraftHolder.getClientLevel()
            .getBlockState(BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).add(0.0, 0.21, 0.0)))
            .canBeReplaced()) {
         Map<AABB, Object> map = this.getMap();

         for (Map.Entry<AABB, Object> entrySetItem : map.entrySet()) {
            Render3D.defaultImpl2(Render3D.INSTANCE, (AABB)entrySetItem.getKey(), (Color)entrySetItem.getValue(), null, 4, null);
            Render3D.defaultImpl3(Render3D.INSTANCE, (AABB)entrySetItem.getKey(), Render2D.INSTANCE.computeColor((Color)entrySetItem.getValue(), 1.0F), null, 4, null);
         }
      }
   }

   private final Map<AABB, Object> getMap() {
      Map<AABB, Object> linkedHashMap = new LinkedHashMap();

      for (VoxelShape blockCollision : MinecraftHolder.getClientLevel()
         .getBlockCollisions(
            (Entity)MinecraftHolder.getLocalPlayer(),
            MinecraftHolder.getLocalPlayer()
               .getBoundingBox()
               .inflate(this.getRange(), 0.0, this.getRange())
               .setMinY(MinecraftHolder.getLocalPlayer().getY() + 0.21)
               .setMaxY(MinecraftHolder.getLocalPlayer().getY() + 0.21)
         )) {
         linkedHashMap.put(
            blockCollision.bounds().setMaxY(blockCollision.bounds().minY),
            MinecraftHolder.getClientLevel().getBlockState(BlockPos.containing((Position)blockCollision.bounds().getCenter()).below()).canBeReplaced()
               ? this.getUnsafe()
               : this.getSafe()
         );
      }

      return linkedHashMap;
   }

   static {
   }
}