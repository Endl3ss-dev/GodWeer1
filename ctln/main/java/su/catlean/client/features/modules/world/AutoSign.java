// deobf by pusia
package su.catlean.client.features.modules.world;

import su.catlean.utils.KotlinCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.client.ScreenEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.SignEditScreenAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.InputPacketUtil;

public final class AutoSign extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoSign.class, "line1", "getLine1()Ljava/lang/String;", 0), new PropertyReference1Impl(AutoSign.class, "line2", "getLine2()Ljava/lang/String;", 0), new PropertyReference1Impl(AutoSign.class, "line3", "getLine3()Ljava/lang/String;", 0), new PropertyReference1Impl(AutoSign.class, "line4", "getLine4()Ljava/lang/String;", 0), new PropertyReference1Impl(AutoSign.class, "dateFormat", "getDateFormat()Ljava/lang/String;", 0), new PropertyReference1Impl(AutoSign.class, "glow", "getGlow()Z", 0) };
   @NotNull
   public static final AutoSign INSTANCE = new AutoSign();
   @NotNull
   private static final StringSetting line1 = SettingFactory.defaultImpl(INSTANCE, "line1", "<player>", null, null, 12, null);
   @NotNull
   private static final StringSetting line2 = SettingFactory.defaultImpl(INSTANCE, "line2", "was here", null, null, 12, null);
   @NotNull
   private static final StringSetting line3 = SettingFactory.defaultImpl(INSTANCE, "line3", "<------------->", null, null, 12, null);
   @NotNull
   private static final StringSetting line4 = SettingFactory.defaultImpl(INSTANCE, "line4", "<date>", null, null, 12, null);
   @NotNull
   private static final StringSetting dateFormat = SettingFactory.defaultImpl(INSTANCE, "date-format", "dd/MM/yyyy", null, () -> INSTANCE.isFlag(), 4, null);
   @NotNull
   private static final BooleanSetting glowing = SettingFactory.defaultImpl(INSTANCE, "glowing", false, null, null, 12, null);

   private AutoSign() {
      super("auto-sign", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final String getLine1() {
      return (String)line1.computeObject(this, $$delegatedProperties[0]);
   }

   private final String getLine2() {
      return (String)line2.computeObject(this, $$delegatedProperties[1]);
   }

   private final String getLine3() {
      return (String)line3.computeObject(this, $$delegatedProperties[2]);
   }

   private final String getLine4() {
      return (String)line4.computeObject(this, $$delegatedProperties[3]);
   }

   private final String getDateFormat() {
      return (String)dateFormat.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getGlow() {
      return (Boolean)glowing.computeObject(this, $$delegatedProperties[5]);
   }

   @Flow
   public final void onScreen(ScreenEvent e) {
      if (e.getScreen() instanceof SignEditScreen) {
         SignEditScreenAccessor screen = (SignEditScreenAccessor)e.getScreen();
         e.cancel();
         Packet serverboundSignUpdatePacket = (Packet)(
            new ServerboundSignUpdatePacket(
               screen.getSign().getBlockPos(),
               screen.isIsFrontText(),
               this.computeText(this.getLine1()),
               this.computeText(this.getLine2()),
               this.computeText(this.getLine3()),
               this.computeText(this.getLine4())
            )
         );
         PacketUtil.applySend(serverboundSignUpdatePacket);
         if (this.getGlow() && !screen.getSign().getText(true).hasGlowingText()) {
            InventoryManager instance = InventoryManager.INSTANCE;
            Item[] items = new Item[]{Items.GLOW_INK_SAC};
            SearchInvResult searchInvResult = instance.computeSearchInvResult(items);
            boolean flag = MinecraftHolder.getMinecraft().player.getOffhandItem().getItem() == Items.GLOW_INK_SAC;
            boolean isShiftKeyDown = MinecraftHolder.getMinecraft().player.isShiftKeyDown();
            if (searchInvResult.isFlag() || flag) {
               InventoryManager.INSTANCE.applyGetInventory();
               searchInvResult.update();
               if (isShiftKeyDown) {
                  InputPacketUtil.INSTANCE.applyWrite(false);
               }

               MultiPlayerGameMode multiPlayerGameMode = MinecraftHolder.getMultiPlayerGameMode();
               LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
               InteractionHand ofF_HAND = flag ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
               BlockPos blockPos = screen.getSign().getBlockPos();
               multiPlayerGameMode.useItemOn(
                  localPlayer, ofF_HAND, new BlockHitResult(Vec3iUtil.computeVec3(blockPos).add(0.0, 0.5, 0.0), Direction.UP, screen.getSign().getBlockPos(), false)
               );
               if (isShiftKeyDown) {
                  InputPacketUtil.INSTANCE.applyWrite(true);
               }

               serverboundSignUpdatePacket = (Packet)(new ServerboundSwingPacket(flag ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND));
               PacketUtil.applySend(serverboundSignUpdatePacket);
               InventoryManager.INSTANCE.update();
            }
         }
      }
   }

   private final String computeText(String text) {
      String text2 = "dd/MM/yyyy";

      try {
         text2 = new SimpleDateFormat(this.getDateFormat()).format(new Date());
      } catch (Exception exception) {
         ChatUtil.defaultImpl(this, ChatFormatting.RED.toString() + "Your date format is wrong!", false, 2, null);
      }

      return KotlinCompat.replace(
         KotlinCompat.replace(text, "<player>", MinecraftHolder.getMinecraft().getUser().getName(), false, 4, null), "<date>", text2, false, 4, null
      );
   }

   private final boolean isFlag() {
      return KotlinCompat.contains(this.getLine1(), "<date>", false, 2, null)
         || KotlinCompat.contains(this.getLine2(), "<date>", false, 2, null)
         || KotlinCompat.contains(this.getLine3(), "<date>", false, 2, null)
         || KotlinCompat.contains(this.getLine4(), "<date>", false, 2, null);
   }

   static {
   }
}