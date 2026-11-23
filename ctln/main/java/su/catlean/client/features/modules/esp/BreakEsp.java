// deobf by pusia
package su.catlean.client.features.modules.esp;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.BlockAnimation;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.InteractionManagerAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class BreakEsp extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(BreakEsp.class, "external", "getExternal()Z", 0), new PropertyReference1Impl(BreakEsp.class, "mode", "getMode()Lsu/catlean/hd;", 0), new PropertyReference1Impl(BreakEsp.class, "primary", "getPrimary()Ljava/awt/Color;", 0), new PropertyReference1Impl(BreakEsp.class, "secondary", "getSecondary()Ljava/awt/Color;", 0), new PropertyReference1Impl(BreakEsp.class, "text", "getText()Ljava/awt/Color;", 0) };
   @NotNull
   public static final BreakEsp INSTANCE = new BreakEsp();
   @NotNull
   private static final BooleanSetting external = SettingFactory.defaultImpl(INSTANCE, "external", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", BreakEsp.Mode.SHRINK, null, null, 12, null);
   @NotNull
   private static final ColorSetting primary = SettingFactory.defaultImpl(INSTANCE, "primary", new Color(255, 255, 255, 100), null, null, 12, null);
   @NotNull
   private static final ColorSetting secondary = SettingFactory.defaultImpl(INSTANCE, "secondary", new Color(255, 255, 255, 100), null, null, 12, null);
   @NotNull
   private static final ColorSetting text = SettingFactory.defaultImpl(INSTANCE, "text", new Color(255, 255, 255, 255), null, null, 12, null);
   private static float amount;
   private static float amount2;
   @NotNull
   private static final CopyOnWriteArrayList<BlockAnimation> items = new CopyOnWriteArrayList();

   private BreakEsp() {
      super("break-esp", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final boolean getExternal() {
      return (Boolean)external.computeObject(this, $$delegatedProperties[0]);
   }

   private final BreakEsp.Mode getMode() {
      return (BreakEsp.Mode)mode.computeObject(this, $$delegatedProperties[1]);
   }

   private final Color getPrimary() {
      return (Color)primary.computeObject(this, $$delegatedProperties[2]);
   }

   private final Color getSecondary() {
      return (Color)secondary.computeObject(this, $$delegatedProperties[3]);
   }

   private final Color getText() {
      return (Color)text.computeObject(this, $$delegatedProperties[4]);
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (this.getExternal() && receivePacket.getPacket() instanceof ClientboundBlockDestructionPacket) {
         BlockPos pos = ((ClientboundBlockDestructionPacket)receivePacket.getPacket()).getPos();
         if (Vec3iUtil.computeVec3(pos).distanceToSqr(this.computeVec3((Entity)MinecraftHolder.getLocalPlayer())) <= 144.0) {
            var items2 = items;
            boolean flag;
            if (items2 instanceof Collection && items2.isEmpty()) {
               flag = false;
            } else {
               Iterator<BlockAnimation> iterator = items2.iterator();

               while (true) {
                  if (!iterator.hasNext()) {
                     flag = false;
                     break;
                  }

                  BlockAnimation object = iterator.next();
                  BlockAnimation blockAnimation = (BlockAnimation)object;
                  if (Intrinsics.areEqual(blockAnimation.getPos(), ((ClientboundBlockDestructionPacket)receivePacket.getPacket()).getPos())) {
                     flag = true;
                     break;
                  }
               }
            }

            if (!flag) {
               items.add(new BlockAnimation(((ClientboundBlockDestructionPacket)receivePacket.getPacket()).getPos(), 0.0F, 0.0F, 6, null));
            }
         }
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      items.removeIf(arg -> ((BlockAnimation)arg).isFlag());
      amount2 = amount;
      amount = ((InteractionManagerAccessor)MinecraftHolder.getMultiPlayerGameMode()).getCurBlockDamageMP();
   }

   @Flow
   public final void onRender3D(Render3DEvent e) {
      if (MinecraftHolder.getMinecraft().hitResult != null && MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult) {
         BlockPos blockPos = ((BlockHitResult)MinecraftHolder.getMinecraft().hitResult).getBlockPos();
         if (MinecraftHolder.getMultiPlayerGameMode().isDestroying() && !MinecraftHolder.getClientLevel().isEmptyBlock(blockPos)) {
            this.applyGetCenter(blockPos, RangesKt.coerceIn(Render2D.INSTANCE.computeAmount(amount2, amount, Render2D.INSTANCE.getAmount2()), 0.0F, 1.0F));
         }
      }

      for (Object item : items) {
         BlockAnimation blockAnimation = (BlockAnimation)item;
         INSTANCE.applyGetCenter(
            blockAnimation.getPos(),
            RangesKt.coerceIn(Render2D.INSTANCE.computeAmount(blockAnimation.getAmount2(), blockAnimation.getAmount(), Render2D.INSTANCE.getAmount2()), 0.0F, 1.0F)
         );
      }
   }

   private final void applyGetCenter(BlockPos blockPos, float amount) {
      AABB aabb = this.computeAABB(blockPos, amount);
      Render3D.defaultImpl2(Render3D.INSTANCE, aabb, Render2D.INSTANCE.fetchAlpha(this.getPrimary(), this.getSecondary(), amount), null, 4, null);
      Render3D.defaultImpl3(
         Render3D.INSTANCE,
         aabb,
         Render2D.INSTANCE.computeColor(Render2D.INSTANCE.fetchAlpha(this.getPrimary(), this.getSecondary(), amount), 1.0F),
         null,
         4,
         null
      );
      Render3D.defaultImpl(Render3D.INSTANCE, (int)(amount * 100) + "%", aabb.getCenter(), this.getText(), 0.0F, 0.0F, false, null, 120, null);
   }

   private final AABB computeAABB(BlockPos blockPos, float amount) {
      return switch (BreakEsp.WhenMappings.values[this.getMode().ordinal()]) {
         case 1 -> new AABB(blockPos);
         case 2 -> {
            float amount3 = 1.0F - amount;
            AABB move = new AABB(blockPos).contract(amount3, amount3, amount3).move((double)amount3 / 2.0F, (double)amount3 / 2.0F, (double)amount3 / 2.0F);
            yield move;
         }
         case 3 -> {
            AABB move2 = new AABB(blockPos).contract(amount, amount, amount).move((double)amount / 2.0F, (double)amount / 2.0F, (double)amount / 2.0F);
            yield move2;
         }
         case 4 -> {
            AABB setMaxY = new AABB(blockPos).setMaxY((double)blockPos.getY() + amount);
            yield setMaxY;
         }
         case 5 -> {
            AABB setMaxY2 = new AABB(blockPos).setMaxY((double)blockPos.getY() + (1.0F - amount));
            yield setMaxY2;
         }
         default -> throw new NoWhenBranchMatchedException();
      };
   }

   static {
   }

   public static enum Mode {
      DEFAULT,
      GROW,
      SHRINK,
      FILL,
      EMPTY;

      private static final BreakEsp.Mode[] modes = $values();
      private static final EnumEntries<BreakEsp.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<BreakEsp.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final BreakEsp.Mode[] $values() {
         return new BreakEsp.Mode[]{DEFAULT, GROW, SHRINK, FILL, EMPTY};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         String text = "rXetM";
         int[] values2 = new int[BreakEsp.Mode.values().length];

         try {
            values2[BreakEsp.Mode.DEFAULT.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[BreakEsp.Mode.GROW.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[BreakEsp.Mode.SHRINK.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[BreakEsp.Mode.FILL.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[BreakEsp.Mode.EMPTY.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         values = values2;
      }
   }
}