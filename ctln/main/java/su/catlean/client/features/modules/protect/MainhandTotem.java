// deobf by pusia
package su.catlean.client.features.modules.protect;

import net.minecraft.network.PacketListener;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.ThreadManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;

public final class MainhandTotem extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(MainhandTotem.class, "hp", "getHp()F", 0), new PropertyReference1Impl(MainhandTotem.class, "pauseOnEating", "getPauseOnEating()Z", 0) };
   @NotNull
   public static final MainhandTotem INSTANCE = new MainhandTotem();
   @NotNull
   private static final FloatSetting hp = SettingFactory.defaultImpl(INSTANCE, "hp", 16.0F, RangesKt.rangeTo(0.0F, 36.0F), null, null, 24, null);
   @NotNull
   private static final BooleanSetting pauseWhileEating = SettingFactory.defaultImpl(INSTANCE, "pause-while-eating", false, null, null, 12, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private MainhandTotem() {
      super("mainhand-totem", SubCategories.getSubCategory(), null, 4, null);
   }

   private final float getHp() {
      return ((Number)hp.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   private final boolean getPauseOnEating() {
      return (Boolean)pauseWhileEating.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (this.getAmount() <= this.getHp() && stopWatch.testValue(1000) && !MinecraftHolder.getLocalPlayer().isUsingItem()) {
         this.applyIsUsingItem(this.getValue());
         stopWatch.update();
      }
   }

   @Flow
   public final void onReceivePacket(ReceivePacket e) {
      Packet<? extends PacketListener> packet = e.getPacket();
      if (ThreadManager.INSTANCE.isFlag()) {
         try {
            if (packet instanceof ClientboundAddEntityPacket
               && Intrinsics.areEqual(((ClientboundAddEntityPacket)packet).getType(), EntityTypes.END_CRYSTAL)
               && this.getAmount()
                     - CrystalPredictor.defaultImpl(
                        CrystalPredictor.INSTANCE,
                        new Vec3(
                           ((ClientboundAddEntityPacket)packet).getX(), ((ClientboundAddEntityPacket)packet).getY(), ((ClientboundAddEntityPacket)packet).getZ()
                        ),
                        (Player)MinecraftHolder.getLocalPlayer(),
                        3,
                        false,
                        null,
                        null,
                        56,
                        null
                     )
                  <= this.getHp()) {
               this.applyIsUsingItem(this.getValue());
            }

            if (packet instanceof ClientboundEntityEventPacket
               && ((ClientboundEntityEventPacket)packet).getEventId() == 35
               && Intrinsics.areEqual(
                  ((ClientboundEntityEventPacket)packet).getEntity((Level)MinecraftHolder.getClientLevel()), MinecraftHolder.getLocalPlayer()
               )) {
               this.applyIsUsingItem(this.getValue());
            }

            if (packet instanceof ClientboundSetHealthPacket && this.getAmount() <= this.getHp()) {
               this.applyIsUsingItem(this.getValue());
            }
         } catch (Exception exception) {
            System.out.println("Mainhand Totem Error: " + exception.getLocalizedMessage());
         }
      }
   }

   private final void applyIsUsingItem(int value) {
      if (value != -1) {
         if (!MinecraftHolder.getLocalPlayer().isUsingItem() || !this.getPauseOnEating()) {
            if (InventoryManager.INSTANCE.fetchItem(Items.TOTEM_OF_UNDYING) > 1 || !Offhand.INSTANCE.getEnabled()) {
               InventoryManager.INSTANCE.applyGetInventory2(value);
            }
         }
      }
   }

   private final int getValue() {
      InventoryManager instance = InventoryManager.INSTANCE;
      Item[] items = new Item[]{Items.TOTEM_OF_UNDYING};
      SearchInvResult searchInvResult = instance.computeSearchInvResult(items);
      return searchInvResult.isFlag() && searchInvResult.getValue() != MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() ? searchInvResult.getValue() : -1;
   }

   private final float getAmount() {
      return MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount();
   }

   static {
   }
}