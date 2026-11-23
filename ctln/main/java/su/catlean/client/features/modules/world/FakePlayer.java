// deobf by pusia
package su.catlean.client.features.modules.world;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ActionSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class FakePlayer extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(FakePlayer.class, "recordButton", "getRecordButton()Lsu/catlean/cr;", 0), new PropertyReference1Impl(FakePlayer.class, "button", "getButton()Lsu/catlean/cr;", 0), new PropertyReference1Impl(FakePlayer.class, "copyInventory", "getCopyInventory()Z", 0), new PropertyReference1Impl(FakePlayer.class, "name", "getName()Ljava/lang/String;", 0) };
   @NotNull
   public static final FakePlayer INSTANCE = new FakePlayer();
   @NotNull
   private static final ActionSetting actionSetting = SettingFactory.defaultImpl(INSTANCE, "record-/-stop", () -> {
      FakePlayer.flag = !FakePlayer.flag;
      return Unit.INSTANCE;
   }, null, () -> !FakePlayer.flag2, 4, null);
   @NotNull
   private static final ActionSetting actionSetting2 = SettingFactory.defaultImpl(INSTANCE, "play-/-stop", () -> {
      FakePlayer.flag2 = !FakePlayer.flag2;
      return Unit.INSTANCE;
   }, null, () -> !FakePlayer.flag, 4, null);
   @NotNull
   private static final BooleanSetting copyInventory = SettingFactory.defaultImpl(INSTANCE, "copy-inventory", true, null, null, 12, null);
   @NotNull
   private static final StringSetting name = SettingFactory.defaultImpl(INSTANCE, "name", "OLD_GOD", null, null, 12, null);
   @Nullable
   private static RemotePlayer remotePlayer;
   @NotNull
   private static final List<FakePlayer.PlayerState> items = new ArrayList();
   private static int value;
   private static boolean flag;
   private static boolean flag2;

   private FakePlayer() {
      super("fake-player", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final boolean getCopyInventory() {
      return (Boolean)copyInventory.computeObject(this, $$delegatedProperties[2]);
   }

   private final String getName() {
      return (String)name.computeObject(this, $$delegatedProperties[3]);
   }

   @Nullable
   public final RemotePlayer getRemotePlayer() {
      return remotePlayer;
   }

   public final void setRemotePlayer(RemotePlayer remotePlayer) {
      FakePlayer.remotePlayer = remotePlayer;
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().player != null) {
         remotePlayer = new RemotePlayer(
            MinecraftHolder.getClientLevel(), new GameProfile(UUID.fromString("66123666-6666-6666-6666-613376666600"), this.getName())
         );
         remotePlayer.copyPosition((Entity)MinecraftHolder.getLocalPlayer());
         remotePlayer.setId(4204204);
         if (this.getCopyInventory()) {
            remotePlayer.setItemInHand(InteractionHand.MAIN_HAND, MinecraftHolder.getLocalPlayer().getMainHandItem().copy());
            remotePlayer.setItemInHand(InteractionHand.OFF_HAND, MinecraftHolder.getLocalPlayer().getOffhandItem().copy());
            byte byteValue = 4;

            for (int i = 0; i < byteValue; i++) {
               remotePlayer.getInventory().setItem(35 + i, MinecraftHolder.getLocalPlayer().getInventory().getItem(35 + i).copy());
            }

            remotePlayer.getAttributes().assignAllValues(MinecraftHolder.getLocalPlayer().getAttributes());
         }

         MinecraftHolder.getClientLevel().addEntity((Entity)remotePlayer);
         remotePlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 9999, 2));
         remotePlayer.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 9999, 4));
         remotePlayer.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 9999, 1));
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      RemotePlayer remotePlayer2 = remotePlayer;
      if (receivePacket.getPacket() instanceof ClientboundExplodePacket && remotePlayer2 != null && remotePlayer2.hurtTime == 0) {
         remotePlayer2.handleDamageEvent(MinecraftHolder.getClientLevel().damageSources().generic());
         remotePlayer2.setHealth(
            remotePlayer2.getHealth()
               + (
                  remotePlayer2.getAbsorptionAmount()
                     - CrystalPredictor.defaultImpl(
                        CrystalPredictor.INSTANCE, ((ClientboundExplodePacket)receivePacket.getPacket()).center(), (Player)remotePlayer2, 0, false, null, null, 60, null
                     )
               )
         );
         if (remotePlayer2.isDeadOrDying()) {
            remotePlayer2.setHealth(10.0F);
            ClientboundEntityEventPacket clientboundEntityEventPacket = new ClientboundEntityEventPacket((Entity)remotePlayer, (byte)35);
            new ReceivePacket((Packet<?>)clientboundEntityEventPacket).call();
         }
      }
   }

   @Flow
   private final void onPreSync(PreSyncEvent preSyncEvent) {
      if (flag) {
         items.add(
            new FakePlayer.PlayerState(
               MinecraftHolder.getLocalPlayer().getX(),
               MinecraftHolder.getLocalPlayer().getY(),
               MinecraftHolder.getLocalPlayer().getZ(),
               MinecraftHolder.getLocalPlayer().getYRot(),
               MinecraftHolder.getLocalPlayer().getXRot()
            )
         );
      } else {
         if (remotePlayer != null) {
            if (remotePlayer.tickCount % 8 == 0) {
               RemotePlayer remotePlayer2 = remotePlayer;
               remotePlayer2.setHealth(remotePlayer2.getHealth() + 0.5F);
            }

            if (this.computeVec3((Entity)remotePlayer).distanceToSqr(this.computeVec3((Entity)MinecraftHolder.getLocalPlayer())) > 400.0) {
               this.applyUnplug();
               return;
            }

            if (flag2 && !items.isEmpty()) {
               int value2 = value++;
               if (value >= items.size()) {
                  value = 0;
                  return;
               }

               FakePlayer.PlayerState value3 = (FakePlayer.PlayerState)items.get(value);
               remotePlayer.setYRot(value3.getAmount4());
               remotePlayer.setXRot(value3.getAmount5());
               remotePlayer.yHeadRot = value3.getAmount4();
               remotePlayer.syncPacketPositionCodec(value3.getAmount(), value3.getAmount2(), value3.getAmount3());
               remotePlayer.moveOrInterpolateTo(new Vec3(value3.getAmount(), value3.getAmount2(), value3.getAmount3()), value3.getAmount4(), value3.getAmount5());
            } else {
               value = 0;
            }

            if (!Intrinsics.areEqual(remotePlayer.getOffhandItem().getItem(), Items.TOTEM_OF_UNDYING)) {
               remotePlayer.setItemInHand(InteractionHand.OFF_HAND, new ItemStack((ItemLike)Items.TOTEM_OF_UNDYING));
            }
         } else {
            this.applyUnplug();
         }
      }
   }

   @Override
   public void update4() {
      if (remotePlayer != null) {
         remotePlayer.setRemoved(RemovalReason.KILLED);
         remotePlayer.onClientRemoval();
         remotePlayer = null;
         items.clear();
      }
   }

   static {
   }

   public static final class PlayerState {
      private final double amount;
      private final double amount2;
      private final double amount3;
      private final float amount4;
      private final float amount5;

      public PlayerState(double amount, double amount2, double amount3, float amount4, float amount5) {
         this.amount = amount;
         this.amount2 = amount2;
         this.amount3 = amount3;
         this.amount4 = amount4;
         this.amount5 = amount5;
      }

      public final double getAmount() {
         return this.amount;
      }

      public final double getAmount2() {
         return this.amount2;
      }

      public final double getAmount3() {
         return this.amount3;
      }

      public final float getAmount4() {
         return this.amount4;
      }

      public final float getAmount5() {
         return this.amount5;
      }

      public final double getAmount6() {
         return this.amount;
      }

      public final double getAmount7() {
         return this.amount2;
      }

      public final double getAmount8() {
         return this.amount3;
      }

      public final float getAmount9() {
         return this.amount4;
      }

      public final float getAmount10() {
         return this.amount5;
      }

      @NotNull
      public final FakePlayer.PlayerState computePlayerState(double amount, double amount2, double amount3, float amount4, float amount5) {
         return new FakePlayer.PlayerState(amount, amount2, amount3, amount4, amount5);
      }

      @NotNull
      @Override
      public String toString() {
         float amount52 = this.amount5;
         float amount42 = this.amount4;
         double amount32 = this.amount3;
         double amount22 = this.amount2;
         double amount6 = this.amount;
         return "PlayerState(x=" + amount6 + ", y=" + amount22 + ", z=" + amount32 + ", yaw=" + amount42 + ", pitch=" + amount52 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Double.hashCode(this.amount);
         hashCode = hashCode * 31 + Double.hashCode(this.amount2);
         hashCode = hashCode * 31 + Double.hashCode(this.amount3);
         hashCode = hashCode * 31 + Float.hashCode(this.amount4);
         return hashCode * 31 + Float.hashCode(this.amount5);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof FakePlayer.PlayerState playerState)) {
            return false;
         } else if (Double.compare(this.amount, playerState.amount) != 0) {
            return false;
         } else if (Double.compare(this.amount2, playerState.amount2) != 0) {
            return false;
         } else if (Double.compare(this.amount3, playerState.amount3) != 0) {
            return false;
         } else {
            return Float.compare(this.amount4, playerState.amount4) != 0 ? false : Float.compare(this.amount5, playerState.amount5) == 0;
         }
      }
   }
}