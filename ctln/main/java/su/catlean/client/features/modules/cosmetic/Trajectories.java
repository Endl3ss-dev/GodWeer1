// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Triple;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.player.FreecamStateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.legit.BowMinecart;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.inventory.EnchantmentUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class Trajectories extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(Trajectories.class, "previewBind", "getPreviewBind()Lsu/catlean/dd;", 0), new MutablePropertyReference1Impl(Trajectories.class, "handOffset", "getHandOffset()Z", 0) };
   @NotNull
   public static final Trajectories INSTANCE = new Trajectories();
   @NotNull
   private static final BindSetting preview = SettingFactory.defaultImpl(INSTANCE, "preview", new Bind(0, false, false, 7, null), null, null, 12, null);
   @NotNull
   private static final BooleanSetting handOffset = SettingFactory.defaultImpl(INSTANCE, "hand-offset", false, null, null, 12, null);
   @Nullable
   private static Vec3 vec3;
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   @NotNull
   private static Vec3 vec32 = Vec3.ZERO;

   private Trajectories() {
      super("trajectories", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final Bind getPreviewBind() {
      return (Bind)preview.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getHandOffset() {
      return (Boolean)handOffset.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean testItem(Item item) {
      return item instanceof EnderpearlItem
         || item instanceof TridentItem
         || item instanceof ExperienceBottleItem
         || item instanceof SnowballItem
         || item instanceof EggItem
         || item instanceof SplashPotionItem
         || item instanceof LingeringPotionItem;
   }

   private final float computeAmount(Item item) {
      return item instanceof BowItem ? 1.0F : 0.4F;
   }

   private final float computeAmount2(Item item) {
      if (item instanceof SplashPotionItem || item instanceof LingeringPotionItem) {
         return 0.39F;
      } else if (item instanceof ExperienceBottleItem) {
         return 0.59F;
      } else {
         return item instanceof TridentItem ? 2.0F : 1.5F;
      }
   }

   private final int computeValue(Item item) {
      return !(item instanceof SplashPotionItem) && !(item instanceof LingeringPotionItem) && !(item instanceof ExperienceBottleItem) ? 0 : 20;
   }

   private final float computeAmount3(Item item) {
      return !(item instanceof SplashPotionItem) && !(item instanceof LingeringPotionItem) && !(item instanceof ExperienceBottleItem) ? 0.5F : 4.0F;
   }

   @Flow
   private final void onFreecamState(FreecamStateEvent freecamStateEvent) {
      if (vec3 != null
         && this.getPreviewBind().getValue() != -1
         && InputUtil.checkContains(this.getPreviewBind().getValue())
         && MinecraftHolder.getMinecraft().getCameraEntity() != null) {
         freecamStateEvent.cancel();
         Vec3 vec33 = Render3D.INSTANCE
            .computeVec3(
               MinecraftHolder.getLocalPlayer().getEyePosition(),
               vec3.add(0.0, MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose()), 0.0),
               animation.getAmount()
            );
         freecamStateEvent.setX(vec33.x);
         freecamStateEvent.setY(vec33.y);
         freecamStateEvent.setZ(vec33.z);
         freecamStateEvent.setYaw(MinecraftHolder.getMinecraft().getCameraEntity().getYRot());
         freecamStateEvent.setPitch(0.0F);
      }
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      LocalPlayer player = MinecraftHolder.getMinecraft().player;
      if (player != null) {
         LocalPlayer localPlayer = player;
         vec32 = localPlayer.getDeltaMovement();
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      animation.applyDoubleValue(InputUtil.checkContains(this.getPreviewBind().getValue()) ? 1.0F : 0.0F);
      vec3 = null;
      boolean bobViewItem = (Boolean)MinecraftHolder.getMinecraft().options.bobView().get();
      MinecraftHolder.getMinecraft().options.bobView().set(false);

      for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
         ItemStack mainHandItem = player.getMainHandItem();
         ItemStack offhandItem = player.getOffhandItem();
         InteractionHand ofF_HAND;
         if (!(mainHandItem.getItem() instanceof BowItem) && !(mainHandItem.getItem() instanceof CrossbowItem) && !this.testItem(mainHandItem.getItem())) {
            if (!(offhandItem.getItem() instanceof BowItem) && !(offhandItem.getItem() instanceof CrossbowItem) && !this.testItem(offhandItem.getItem())) {
               continue;
            }

            ofF_HAND = InteractionHand.OFF_HAND;
         } else {
            ofF_HAND = InteractionHand.MAIN_HAND;
         }

         InteractionHand interactionHand = ofF_HAND;
         Item item = interactionHand == InteractionHand.OFF_HAND ? offhandItem.getItem() : mainHandItem.getItem();
         boolean checkContains = EnchantmentUtil.checkContains(offhandItem, Enchantments.MULTISHOT);
         boolean checkContains2 = EnchantmentUtil.checkContains(mainHandItem, Enchantments.MULTISHOT);
         float amount = Render2D.INSTANCE.computeAmount(player.yRotO, player.getYRot());
         if ((!(offhandItem.getItem() instanceof CrossbowItem) || !checkContains) && (!(mainHandItem.getItem() instanceof CrossbowItem) || !checkContains2)) {
            this.applyCos((Player)player, item, amount);
         } else {
            this.applyCos((Player)player, item, amount - 10);
            this.applyCos((Player)player, item, amount);
            this.applyCos((Player)player, item, amount + 10);
         }
      }

      MinecraftHolder.getMinecraft().options.bobView().set(bobViewItem);
   }

   public final void applyCos(Player player, Item item, float amount) {
      vec3 = null;
      double amount2 = Render2D.INSTANCE.computeAmount(player.xo, player.getX());
      double amount3 = Render2D.INSTANCE.computeAmount(player.yo, player.getY());
      double amount4 = Render2D.INSTANCE.computeAmount(player.zo, player.getZ());
      amount3 += player.getEyeHeight(player.getPose());
      if (this.getHandOffset()) {
         amount3 -= 0.1;
         if (Intrinsics.areEqual(player, MinecraftHolder.getMinecraft().player)) {
            if (Intrinsics.areEqual(item, player.getOffhandItem().getItem())) {
               amount2 += (float)Math.cos(amount / 180.0F * (float) Math.PI) * 0.16F;
               amount4 += (float)Math.sin(amount / 180.0F * (float) Math.PI) * 0.16F;
            } else {
               amount2 -= (float)Math.cos(amount / 180.0F * (float) Math.PI) * 0.16F;
               amount4 -= (float)Math.sin(amount / 180.0F * (float) Math.PI) * 0.16F;
            }
         }
      }

      float amount5 = Render2D.INSTANCE.computeAmount(player.xRotO, player.getXRot());
      float amount6 = this.computeAmount(item);
      double value = -((float)Math.sin(amount / 180.0F * (float) Math.PI)) * (float)Math.cos(amount5 / 180.0F * (float) Math.PI) * amount6;
      double value2 = -((float)Math.sin((amount5 - this.computeValue(item)) / 180.0F * 3.141593F)) * amount6;
      double amount7 = (float)Math.cos(amount / 180.0F * (float) Math.PI) * (float)Math.cos(amount5 / 180.0F * (float) Math.PI) * amount6;
      float amount8 = Render2D.INSTANCE.computeAmount(Math.max(0.0F, player.getTicksUsingItem() - 1.0F), (float)player.getTicksUsingItem()) / 20.0F;
      amount8 = (amount8 * amount8 + amount8 * 2.0F) / 3.0F;
      if (amount8 > 1.0F || amount8 == 0.0F) {
         amount8 = 1.0F;
      }

      float value3 = Mth.sqrt((float)(value * value + value2 * value2 + amount7 * amount7));
      value /= value3;
      value2 /= value3;
      amount7 /= value3;
      float amount22 = (item instanceof BowItem ? amount8 * 2.0F : (item instanceof CrossbowItem ? 2.2F : 1.0F)) * this.computeAmount2(item);
      value *= amount22;
      value2 *= amount22;
      amount7 *= amount22;
      if (Intrinsics.areEqual(player, MinecraftHolder.getMinecraft().player)) {
         value += Render2D.INSTANCE.computeAmount(vec32.x, player.getDeltaMovement().x);
         value2 += Render2D.INSTANCE.computeAmount(vec32.y, player.getDeltaMovement().y);
         amount7 += Render2D.INSTANCE.computeAmount(vec32.z, player.getDeltaMovement().z);
      }

      Vec3 vec33 = null;

      for (int i = 0; i < 300; i++) {
         vec33 = new Vec3(amount2, amount3, amount4);
         amount2 += value;
         amount3 += value2;
         amount4 += amount7;
         if (Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(BlockPos.containing(amount2, amount3, amount4)).getBlock(), Blocks.WATER)) {
            value *= 0.8;
            value2 *= 0.8;
            amount7 *= 0.8;
         } else {
            value *= 0.99;
            value2 *= 0.99;
            amount7 *= 0.99;
         }

         value2 -= item instanceof BowItem ? 0.05F : (player.getMainHandItem().getItem() instanceof CrossbowItem ? 0.05F : 0.03);
         Vec3 vec34 = new Vec3(amount2, amount3, amount4);
         BlockHitResult clip = MinecraftHolder.getClientLevel().clip(new ClipContext((Vec3)vec33, vec34, Block.COLLIDER, Fluid.NONE, (Entity)player));
         if (clip.getType() == Type.BLOCK) {
            if (BowMinecart.INSTANCE.isFlag()) {
               AABB setMaxY = new AABB(clip.getBlockPos().above()).setMaxY(clip.getBlockPos().above().getY() + 0.125);
               float blockInteractionRange = (float)MinecraftHolder.getMinecraft().player.blockInteractionRange();
               Vec3 eyePosition = MinecraftHolder.getLocalPlayer().getEyePosition();
               BlockPos blockPos = clip.getBlockPos();
               double distanceTo = eyePosition.distanceTo(Vec3iUtil.computeVec3(blockPos));
               Color color = (2.3F <= distanceTo ? distanceTo <= blockInteractionRange : false) ? ThemeManager.INSTANCE.computeColor(0) : Color.RED;
               Render3D.defaultImpl3(Render3D.INSTANCE, setMaxY, color, null, 4, null);
               Render3D.defaultImpl2(Render3D.INSTANCE, setMaxY, Render2D.INSTANCE.computeColor(color, Render2D.INSTANCE.fetchAlpha(color) / 4.0F), null, 4, null);
            } else {
               this.applyCos(clip.getLocation(), this.computeAmount3(item), clip.getDirection(), ThemeManager.INSTANCE.computeColor(i * 10));
            }

            Render3D.defaultImpl(Render3D.INSTANCE, (Vec3)vec33, clip.getLocation(), ThemeManager.INSTANCE.computeColor(i), null, false, 24, null);
            if (item instanceof EnderpearlItem && Intrinsics.areEqual(player, MinecraftHolder.getMinecraft().player)) {
               vec3 = clip.getLocation();
            }
            break;
         }

         Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
         var arrayList = new ArrayList();

         for (Object object : entitiesForRendering) {
            Entity entity = (Entity)object;
            if (!(entity instanceof Projectile)
               && !Intrinsics.areEqual(entity, MinecraftHolder.getMinecraft().player)
               && (entity.distanceToSqr(vec34) < 25.0 || entity.distanceToSqr((Vec3)vec33) < 25.0)) {
               arrayList.add(object);
            }
         }

         List list = arrayList;
         boolean flag4 = false;
         Iterable iterable = list;

         for (Object object2 : iterable) {
            Entity entity2 = (Entity)object2;
            AABB boundingBox = entity2.getBoundingBox();
            Optional<Vec3> clip2 = boundingBox.clip((Vec3)vec33, vec34);
            if (clip2.isPresent() && !flag4) {
               Render3D.defaultImpl(Render3D.INSTANCE, (Vec3)vec33, (Vec3)clip2.get(), ThemeManager.INSTANCE.computeColor(i), null, false, 24, null);
               flag4 = true;
               if (item instanceof EnderpearlItem && Intrinsics.areEqual(player, MinecraftHolder.getMinecraft().player)) {
                  vec3 = entity2.getBoundingBox().getCenter();
               }
            }
         }

         if (flag4 || amount3 <= -65.0) {
            break;
         }

         if (value != 0.0 || value2 != 0.0 || amount7 != 0.0) {
            Render3D.defaultImpl(Render3D.INSTANCE, (Vec3)vec33, vec34, ThemeManager.INSTANCE.computeColor(i), null, false, 24, null);
         }
      }
   }

   private final void applyCos(Vec3 vec3, float amount, Direction direction, Color color) {
      List<Vec3> arrayList = new ArrayList();
      double value = vec3.x;
      double value2 = vec3.y;
      double value3 = vec3.z;
      byte byteValue = 10;
      int value4 = 0;
      int progressionLastElement = ProgressionUtilKt.getProgressionLastElement(0, 360, byteValue);
      if (value4 <= progressionLastElement) {
         while (true) {
            double value5 = value4 * Math.PI / 180.0;

            Triple triple = switch (Trajectories.WhenMappings.values[direction.ordinal()]) {
               case 1 -> new Triple(Math.sin(value5) * amount, Math.cos(value5) * amount, 0.0);
               case 2 -> new Triple(Math.sin(value5) * amount, Math.cos(value5) * amount, 0.0);
               case 3 -> new Triple(0.0, Math.sin(value5) * amount, -Math.cos(value5) * amount);
               case 4 -> new Triple(0.0, Math.sin(value5) * amount, -Math.cos(value5) * amount);
               case 5 -> new Triple(Math.cos(value5) * amount, 0.0, Math.sin(value5) * amount);
               case 6 -> new Triple(Math.cos(value5) * amount, 0.0, Math.sin(value5) * amount);
               default -> throw new NoWhenBranchMatchedException();
            };
            double value6 = ((Number)triple.component1()).doubleValue();
            double value7 = ((Number)triple.component2()).doubleValue();
            double value8 = ((Number)triple.component3()).doubleValue();
            arrayList.add(new Vec3(value + value6, value2 + value7, value3 + value8));
            if (value4 == progressionLastElement) {
               break;
            }

            value4 += byteValue;
         }
      }

      value4 = 0;

      for (int i = arrayList.size() - 1; value4 < i; value4++) {
         Render3D.defaultImpl(Render3D.INSTANCE, (Vec3)arrayList.get(value4), (Vec3)arrayList.get(value4 + 1), color, null, false, 24, null);
      }
   }

   static {
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Direction.values().length];

         try {
            values2[Direction.NORTH.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Direction.SOUTH.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[Direction.EAST.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[Direction.WEST.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[Direction.UP.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values2[Direction.DOWN.ordinal()] = 6;
         } catch (NoSuchFieldError exception6) {
         }

         values = values2;
      }
   }
}