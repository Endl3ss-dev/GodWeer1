// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.Collection;
import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionfc;
import su.catlean.client.features.modules.basic.ElytraRecast;
import su.catlean.client.features.modules.esp.Tracers;
import su.catlean.client.features.modules.player.ElytraFlyPlus;
import su.catlean.client.features.modules.rage.ElytraPlus;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.inventory.EnchantmentUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.WorldUtil;

public final class FlightWidget extends Widget {
   @NotNull
   public static final FlightWidget INSTANCE = new FlightWidget();
   @NotNull
   private static final ArrayDeque<Float> arrayDeque = new ArrayDeque(400);
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 300L);

   private FlightWidget() {
      super("flight-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      animation.applyDoubleValue(
         (!MinecraftHolder.getLocalPlayer().isFallFlying() || ElytraRecast.INSTANCE.getEnabled())
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)
            ? 0.0F
            : 1.0F
      );
      if (!(animation.getAmount() <= 0.0F)) {
         context.pose().pushMatrix();
         context.pose().translate(this.getAmount3() + 60.0F, this.getAmount4() + 28.5F);
         context.pose().scale(Math.clamp(animation.getAmount(), 0.0F, 2.0F), Math.clamp(animation.getAmount(), 0.0F, 2.0F));
         context.pose().translate(-(this.getAmount3() + 60.0F), -(this.getAmount4() + 28.5F));
         Matrix3x2fStack pose = context.pose();
         float amount3 = this.getAmount3();
         float amount4 = this.getAmount4();
         boolean flag = false;
         float value = 1.0F;
         float value2 = 57.0F;
         float value3 = 135.0F;
         float value4 = amount4;
         float value5 = amount3;
         Matrix3x2fStack matrix3x2fStack = pose;
         this.render(matrix3x2fStack, value5, value4, value3, value2, value, flag);
         pose = context.pose();
         amount3 = this.getAmount3() + 2;
         amount4 = this.getAmount4() + 2;
         flag = false;
         value = 0.6F;
         value2 = 53.0F;
         value3 = 53.0F;
         value4 = amount4;
         value5 = amount3;
         matrix3x2fStack = pose;
         this.render(matrix3x2fStack, value5, value4, value3, value2, value, flag);
         pose = context.pose();
         amount3 = this.getAmount3() + 57;
         amount4 = this.getAmount4() + 2;
         flag = false;
         value = 0.6F;
         value2 = 53.0F;
         value3 = 8.0F;
         value4 = amount4;
         value5 = amount3;
         matrix3x2fStack = pose;
         this.render(matrix3x2fStack, value5, value4, value3, value2, value, flag);
         float amount32 = this.getAmount3() + 27.5F;
         float amount42 = this.getAmount4() + 27.5F;
         float yRot = MinecraftHolder.getLocalPlayer().getYRot() + 90.0F;
         PoseStack poseStack = new PoseStack();
         poseStack.pushPose();
         poseStack.translate(this.getAmount3() + 60.0F, this.getAmount4() + 28.5F, 0.0F);
         poseStack.scale(Math.clamp(animation.getAmount(), 0.0F, 2.0F), Math.clamp(animation.getAmount(), 0.0F, 2.0F), 0.0F);
         poseStack.translate(-(this.getAmount3() + 60.0F), -(this.getAmount4() + 28.5F), 0.0F);
         poseStack.translate(amount32 + 1.0F, amount42 + 1.0F, 0.0F);
         poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(yRot));
         poseStack.translate(-amount32 - 1.0F, -amount42 - 1.0F, 0.0F);
         Tracers.INSTANCE.render(poseStack, amount32 + 1.0F, amount42, 15.0F, ThemeManager.INSTANCE.computeColor(0));
         poseStack.popPose();
         float xRot = MinecraftHolder.getLocalPlayer().getXRot() / 90.0F * 26.5F + 26.5F;
         Object object = null;
         byte byteValue = 2;
         boolean flag2 = false;
         Item fireworK_ROCKET = Items.FIREWORK_ROCKET;
         int defaultImpl = InventoryManager.defaultImpl(InventoryManager.INSTANCE, fireworK_ROCKET, flag2, byteValue, object);
         Object object2 = null;
         byte byteValue2 = 2;
         boolean flag3 = false;
         Item elytra = Items.ELYTRA;
         int defaultImpl2 = InventoryManager.defaultImpl(InventoryManager.INSTANCE, elytra, flag3, byteValue2, object2);
         int maxDamage = 0;
         float averageOfFloat = 0.0F;
         float amount = (int)(WorldUtil.INSTANCE.getAmount() * 72.0F * 10.0F) / 10.0F;
         float value6 = (int)((MinecraftHolder.getLocalPlayer().getY() - MinecraftHolder.getLocalPlayer().yo) * 72.0F * 10.0F) / 10.0F;
         ItemStack itemBySlot = MinecraftHolder.getLocalPlayer().getItemBySlot(EquipmentSlot.CHEST);
         if (itemBySlot.is(Items.ELYTRA) && !((Collection)arrayDeque).isEmpty()) {
            int itemEnchantmentLevel = EnchantmentUtil.fetchItemEnchantmentLevel(itemBySlot, Enchantments.UNBREAKING);
            maxDamage = itemBySlot.getMaxDamage() - itemBySlot.getDamageValue();
            averageOfFloat = (float)(maxDamage * CollectionsKt.averageOfFloat((Iterable)arrayDeque));
            averageOfFloat = (int)(averageOfFloat * 10) / 10.0F;
            if (itemEnchantmentLevel > 0) {
               averageOfFloat *= itemEnchantmentLevel - 1;
            }
         }

         this.render(context, "X", this.getAmount3() + 25.5F, this.getAmount4() + 1.0F, ThemeManager.INSTANCE.getColor7());
         this.render(context, "z", this.getAmount3() + 4.0F, this.getAmount4() + 22.0F, ThemeManager.INSTANCE.getColor7());
         this.render(context, "x", this.getAmount3() + 25.5F, this.getAmount4() + 44.0F, ThemeManager.INSTANCE.getColor7());
         this.render(context, "Z", this.getAmount3() + 49.0F, this.getAmount4() + 22.0F, ThemeManager.INSTANCE.getColor7());
         this.render(context, "Fireworks: " + defaultImpl, this.getAmount3() + 67.0F, this.getAmount4(), ThemeManager.INSTANCE.getColor7());
         this.render(context, "Durability: " + maxDamage, this.getAmount3() + 67.0F, this.getAmount4() + 9.0F, ThemeManager.INSTANCE.getColor7());
         this.render(context, "Res (km): " + averageOfFloat, this.getAmount3() + 67.0F, this.getAmount4() + 18.0F, ThemeManager.INSTANCE.getColor7());
         this.render(context, "Elytras: " + defaultImpl2, this.getAmount3() + 67.0F, this.getAmount4() + 27.0F, ThemeManager.INSTANCE.getColor7());
         this.render(context, "XZ Speed: " + amount, this.getAmount3() + 67.0F, this.getAmount4() + 36.0F, ThemeManager.INSTANCE.getColor7());
         this.render(context, "Y Speed: " + value6, this.getAmount3() + 67.0F, this.getAmount4() + 45.0F, ThemeManager.INSTANCE.getColor7());
         if (ElytraPlus.INSTANCE.getEnabled() && ElytraPlus.INSTANCE.getMode() == ElytraPlus.Mode.INFINITE) {
            float amount2 = ElytraFlyPlus.INSTANCE.getAmount2();
            float value7 = amount2 / 90.0F * 26.5F + 26.5F;
            Render2D instance = Render2D.INSTANCE;
            pose = context.pose();
            amount3 = this.getAmount3() + 57.0F;
            amount4 = this.getAmount4() + value7;
            Color[] colors = new Color[]{Color.RED};
            value = 1.0F;
            value2 = 8.0F;
            value3 = amount4;
            value4 = amount3;
            Matrix3x2fStack matrix3x2fStack2 = pose;
            instance.render(matrix3x2fStack2, value4, value3, value2, value, colors);
         }

         Render2D instance2 = Render2D.INSTANCE;
         pose = context.pose();
         amount3 = this.getAmount3() + 57.0F;
         amount4 = this.getAmount4() + xRot;
         Color[] colors2 = new Color[]{new Color(1157627903, true)};
         value = 1.0F;
         value2 = 8.0F;
         value3 = amount4;
         value4 = amount3;
         Matrix3x2fStack matrix3x2fStack3 = pose;
         instance2.render(matrix3x2fStack3, value4, value3, value2, value, colors2);
         context.pose().popMatrix();
         this.updateAmount(120.0F, 55.0F);
      }
   }

   @Override
   public void update() {
      if (arrayDeque.size() >= 400) {
         arrayDeque.removeFirst();
      }

      arrayDeque.add(WorldUtil.INSTANCE.getAmount() * 20.0F);
   }
}