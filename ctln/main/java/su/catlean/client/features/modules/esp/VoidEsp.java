// deobf by pusia
package su.catlean.client.features.modules.esp;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class VoidEsp extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(VoidEsp.class, "mode", "getMode()Lsu/catlean/fa;", 0), new PropertyReference1Impl(VoidEsp.class, "color", "getColor()Ljava/awt/Color;", 0), new MutablePropertyReference1Impl(VoidEsp.class, "range", "getRange()I", 0) };
   @NotNull
   public static final VoidEsp INSTANCE = new VoidEsp();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", VoidEsp.Mode.CROSS, null, null, 12, null);
   @NotNull
   private static final ColorSetting color = SettingFactory.defaultImpl(INSTANCE, "color", Color.WHITE, null, null, 12, null);
   @NotNull
   private static final IntSetting range = SettingFactory.defaultImpl(INSTANCE, "range", 10, new IntRange(1, 64), null, null, 24, null);
   @NotNull
   private static List items = new ArrayList();

   private VoidEsp() {
      super("void-esp", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final VoidEsp.Mode getMode() {
      return (VoidEsp.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final Color getColor() {
      return (Color)color.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getRange() {
      return ((Number)range.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      for (Object item : items) {
         BlockPos blockPos = (BlockPos)item;
         AABB aabb = new AABB(blockPos);
         switch (VoidEsp.WhenMappings.values[INSTANCE.getMode().ordinal()]) {
            case 1:
               Render3D.defaultImpl2(Render3D.INSTANCE, aabb.setMaxY(blockPos.getY()), INSTANCE.getColor(), null, 4, null);
               Render3D.defaultImpl3(Render3D.INSTANCE, aabb.setMaxY(blockPos.getY()), Render2D.INSTANCE.computeColor(INSTANCE.getColor(), 1.0F), null, 4, null);
               break;
            case 2:
               Render3D.defaultImpl2(Render3D.INSTANCE, aabb, INSTANCE.getColor(), null, 4, null);
               Render3D.defaultImpl3(Render3D.INSTANCE, aabb, Render2D.INSTANCE.computeColor(INSTANCE.getColor(), 1.0F), null, 4, null);
               break;
            case 3:
               Render3D.defaultImpl3(Render3D.INSTANCE, aabb, Render2D.INSTANCE.computeColor(INSTANCE.getColor(), 1.0F), null, 4, null);
               break;
            case 4:
               Render3D.defaultImpl(
                  Render3D.INSTANCE,
                  new Vec3(aabb.minX, aabb.minY, aabb.maxZ),
                  new Vec3(aabb.maxX, aabb.minY, aabb.minZ),
                  INSTANCE.getColor(),
                  null,
                  true,
                  8,
                  null
               );
               Render3D.defaultImpl(
                  Render3D.INSTANCE,
                  new Vec3(aabb.maxX, aabb.minY, aabb.maxZ),
                  new Vec3(aabb.minX, aabb.minY, aabb.minZ),
                  INSTANCE.getColor(),
                  null,
                  true,
                  8,
                  null
               );
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }
   }

   @Flow
   private final void setItems(PlayerUpdateEvent playerUpdateEvent) {
      items = this.getItems();
   }

   private final List<BlockPos> getItems() {
      ArrayList<BlockPos> arrayList = new ArrayList();
      int range2 = (int)(MinecraftHolder.getLocalPlayer().getX() - this.getRange());

      for (int i = (int)(MinecraftHolder.getLocalPlayer().getX() + this.getRange()); range2 < i; range2++) {
         int range3 = (int)(MinecraftHolder.getLocalPlayer().getZ() - this.getRange());

         for (int j = (int)(MinecraftHolder.getLocalPlayer().getZ() + this.getRange()); range3 < j; range3++) {
            BlockPos containing = BlockPos.containing(range2, MinecraftHolder.getClientLevel().getMinY(), range3);
            if (!Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(containing).getBlock(), Blocks.BEDROCK)) {
               arrayList.add(containing);
            }
         }
      }

      return arrayList;
   }

   static {
   }

   public static enum Mode {
      FLAT,
      BOX,
      WIRE_FRAME,
      CROSS;

      private static final VoidEsp.Mode[] modes = $values();
      private static final EnumEntries<VoidEsp.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<VoidEsp.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final VoidEsp.Mode[] $values() {
         return new VoidEsp.Mode[]{FLAT, BOX, WIRE_FRAME, CROSS};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         String[] texts = new String[2];
         int[] values2 = new int[VoidEsp.Mode.values().length];

         try {
            values2[VoidEsp.Mode.FLAT.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[VoidEsp.Mode.BOX.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[VoidEsp.Mode.WIRE_FRAME.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[VoidEsp.Mode.CROSS.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         values = values2;
      }
   }
}