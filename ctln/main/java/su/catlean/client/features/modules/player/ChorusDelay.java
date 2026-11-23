// deobf by pusia
package su.catlean.client.features.modules.player;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import java.awt.Color;
import java.util.LinkedList;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.FinishUsingItemEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render3D;

public final class ChorusDelay extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ChorusDelay.class, "confirm", "getConfirm()Lsu/catlean/dd;", 0), new PropertyReference1Impl(ChorusDelay.class, "color", "getColor()Ljava/awt/Color;", 0) };
   @NotNull
   public static final ChorusDelay INSTANCE = new ChorusDelay();
   @NotNull
   private static final BindSetting confirm = SettingFactory.defaultImpl(INSTANCE, "confirm", new Bind(340, false, false, 6, null), null, null, 12, null);
   @NotNull
   private static final ColorSetting color = SettingFactory.defaultImpl(INSTANCE, "color", new Color(16777215), null, null, 12, null);
   private static boolean flag;
   @NotNull
   private static final LinkedList<Packet<? extends PacketListener>> linkedList = new LinkedList();

   private ChorusDelay() {
      super("chorus-delay", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final Bind getConfirm() {
      return (Bind)confirm.computeObject(this, $$delegatedProperties[0]);
   }

   private final Color getColor() {
      return (Color)color.computeObject(this, $$delegatedProperties[1]);
   }

   @Override
   public void update2() {
      linkedList.clear();
      flag = false;
   }

   @Override
   public void update4() {
      if (flag) {
         this.applyHandle();
      }
   }

   @Flow
   public final void onRender3D(Render3DEvent e) {
      if (!linkedList.isEmpty()) {
         Render3D.defaultImpl3(
            Render3D.INSTANCE,
            new AABB(
               ((ClientboundPlayerPositionPacket)CollectionsKt.first(linkedList)).change().position().add(-0.27, 0.0, -0.27),
               ((ClientboundPlayerPositionPacket)CollectionsKt.first(linkedList)).change().position().add(0.27, 1.8, 0.27)
            ),
            this.getColor(),
            null,
            4,
            null
         );
      }
   }

   @Flow
   public final void onInput(InputEvent e) {
      if (e.getKey() == this.getConfirm().getValue() && e.getAction() == InputEvent.Action.Release) {
         this.applyHandle();
      }
   }

   @Flow
   public final void onReceivePacket(ReceivePacket e) {
      if (e.getPacket() instanceof ClientboundPlayerPositionPacket
         && (flag || Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getUseItem().getItem(), Items.CHORUS_FRUIT))) {
         linkedList.add(e.getPacket());
         e.cancel();
      }
   }

   @Flow
   private final void onFinishUsingItem(FinishUsingItemEvent finishUsingItemEvent) {
      if (finishUsingItemEvent.getItem() != null && Intrinsics.areEqual(finishUsingItemEvent.getItem().getItem(), Items.CHORUS_FRUIT)) {
         flag = true;
      }
   }

   private final void applyHandle() {
      flag = false;

      while (!linkedList.isEmpty()) {
         ((ClientboundPlayerPositionPacket)linkedList.poll()).handle((ClientGamePacketListener)MinecraftHolder.getClientPacketListener());
      }
   }

   static {
   }
}