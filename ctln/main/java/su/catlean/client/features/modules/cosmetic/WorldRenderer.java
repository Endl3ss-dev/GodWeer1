// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import net.minecraft.network.PacketListener;
import java.awt.Color;
import java.time.ZonedDateTime;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.ClearColorEvent;
import su.catlean.api.event.events.world.ApplyFogEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;

public final class WorldRenderer extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(WorldRenderer.class, "fogModify", "getFogModify()Z", 0), new PropertyReference1Impl(WorldRenderer.class, "fogStart", "getFogStart()I", 0), new PropertyReference1Impl(WorldRenderer.class, "fogEnd", "getFogEnd()I", 0), new PropertyReference1Impl(WorldRenderer.class, "fogColor", "getFogColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(WorldRenderer.class, "timeChanger", "getTimeChanger()Z", 0), new PropertyReference1Impl(WorldRenderer.class, "realTime", "getRealTime()Z", 0), new MutablePropertyReference1Impl(WorldRenderer.class, "ctimeVal", "getCtimeVal()I", 0) };
   @NotNull
   public static final WorldRenderer INSTANCE = new WorldRenderer();
   @NotNull
   private static final BooleanSetting fogModify = SettingFactory.defaultImpl(INSTANCE, "fog-modify", true, null, null, 12, null);
   @NotNull
   private static final IntSetting fogStart = SettingFactory.defaultImpl(
      INSTANCE, "fog-start", 0, new IntRange(0, 256), null, () -> INSTANCE.getFogModify(), 8, null
   );
   @NotNull
   private static final IntSetting fogEnd = SettingFactory.defaultImpl(
      INSTANCE, "fog-end", 64, new IntRange(10, 256), null, () -> INSTANCE.getFogModify(), 8, null
   );
   @NotNull
   private static final ColorSetting fogColor = SettingFactory.defaultImpl(
      INSTANCE, "fog-color", new Color(11075839), null, () -> INSTANCE.getFogModify(), 4, null
   );
   @NotNull
   private static final BooleanSetting timeChanger = SettingFactory.defaultImpl(INSTANCE, "time-changer", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting realTime = SettingFactory.defaultImpl(INSTANCE, "real-time", false, null, () -> INSTANCE.getTimeChanger(), 4, null);
   @NotNull
   private static final IntSetting time = SettingFactory.defaultImpl(
      INSTANCE, "time", 21000, new IntRange(0, 23000), null, () -> INSTANCE.getTimeChanger(), 8, null
   );
   private static long time2;

   private WorldRenderer() {
      super("world-renderer", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final boolean getFogModify() {
      return (Boolean)fogModify.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getFogStart() {
      return ((Number)fogStart.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final int getFogEnd() {
      return ((Number)fogEnd.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final Color getFogColor() {
      return (Color)fogColor.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getTimeChanger() {
      return (Boolean)timeChanger.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getRealTime() {
      return (Boolean)realTime.computeObject(this, $$delegatedProperties[5]);
   }

   private final int getCtimeVal() {
      return ((Number)time.computeObject(this, $$delegatedProperties[6])).intValue();
   }

   private final void updateValue(int value) {
      time.updateObject(this, $$delegatedProperties[6], value);
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().level != null) {
         time2 = MinecraftHolder.getClientLevel().getGameTime();
      }
   }

   @Override
   public void update4() {
      if (MinecraftHolder.getMinecraft().level != null) {
         MinecraftHolder.getClientLevel().setTimeFromServer(time2);
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      Packet<? extends PacketListener> packet = receivePacket.getPacket();
      if (packet instanceof ClientboundSetTimePacket && this.getTimeChanger()) {
         time2 = ((ClientboundSetTimePacket)packet).gameTime();
         receivePacket.cancel();
      }
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      if (this.getTimeChanger()) {
         if (this.getRealTime()) {
            this.updateValue(this.getValue());
         }

         MinecraftHolder.getClientLevel().setTimeFromServer(this.getCtimeVal());
      }
   }

   @Flow
   private final void onApplyFog(ApplyFogEvent applyFogEvent) {
      if (this.getFogModify() && this.isFlag6()) {
         applyFogEvent.setColorVec(
            new Vector4f(
               Render2D.INSTANCE.fetchRed(this.getFogColor()),
               Render2D.INSTANCE.fetchGreen(this.getFogColor()),
               Render2D.INSTANCE.fetchBlue(this.getFogColor()),
               Render2D.INSTANCE.fetchAlpha(this.getFogColor())
            )
         );
         applyFogEvent.setEStart(this.getFogStart());
         applyFogEvent.setEEnd(this.getFogEnd());
         applyFogEvent.setRStart(this.getFogStart());
         applyFogEvent.setREnd(this.getFogEnd());
         applyFogEvent.cancel();
      }
   }

   @Flow
   private final void onClearColor(ClearColorEvent clearColorEvent) {
      if (this.getFogColor().getAlpha() != 0 && this.getFogModify()) {
         clearColorEvent.cancel();
      }
   }

   public final int getValue() {
      double secondOfDay = ZonedDateTime.now().toLocalTime().toSecondOfDay();
      double value = secondOfDay / 86400.0 * 24000.0;
      return (int)((value - 6000 + 24000) % 24000);
   }

   static {
   }
}