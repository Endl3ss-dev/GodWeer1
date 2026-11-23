// deobf by pusia
package su.catlean.client.features.modules.attack;

import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.LocalPlayerAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;

public final class Criticals extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Criticals.class, "mode", "getMode()Lsu/catlean/c5;", 0), new PropertyReference1Impl(Criticals.class, "ncpMode", "getNcpMode()Lsu/catlean/h8;", 0), new PropertyReference1Impl(Criticals.class, "packet1", "getPacket1()Lsu/catlean/nl;", 0), new PropertyReference1Impl(Criticals.class, "sendPacket1", "getSendPacket1()Z", 0), new PropertyReference1Impl(Criticals.class, "ground1", "getGround1()Z", 0), new PropertyReference1Impl(Criticals.class, "full1", "getFull1()Z", 0), new PropertyReference1Impl(Criticals.class, "offset1", "getOffset1()F", 0), new PropertyReference1Impl(Criticals.class, "packet2", "getPacket2()Lsu/catlean/nl;", 0), new PropertyReference1Impl(Criticals.class, "sendPacket2", "getSendPacket2()Z", 0), new PropertyReference1Impl(Criticals.class, "ground2", "getGround2()Z", 0), new PropertyReference1Impl(Criticals.class, "full2", "getFull2()Z", 0), new PropertyReference1Impl(Criticals.class, "offset2", "getOffset2()F", 0), new PropertyReference1Impl(Criticals.class, "packet3", "getPacket3()Lsu/catlean/nl;", 0), new PropertyReference1Impl(Criticals.class, "sendPacket3", "getSendPacket3()Z", 0), new PropertyReference1Impl(Criticals.class, "ground3", "getGround3()Z", 0), new PropertyReference1Impl(Criticals.class, "full3", "getFull3()Z", 0), new PropertyReference1Impl(Criticals.class, "offset3", "getOffset3()F", 0), new PropertyReference1Impl(Criticals.class, "packet4", "getPacket4()Lsu/catlean/nl;", 0), new PropertyReference1Impl(Criticals.class, "sendPacket4", "getSendPacket4()Z", 0), new PropertyReference1Impl(Criticals.class, "ground4", "getGround4()Z", 0), new PropertyReference1Impl(Criticals.class, "full4", "getFull4()Z", 0), new PropertyReference1Impl(Criticals.class, "offset4", "getOffset4()F", 0), new PropertyReference1Impl(Criticals.class, "packet5", "getPacket5()Lsu/catlean/nl;", 0), new PropertyReference1Impl(Criticals.class, "sendPacket5", "getSendPacket5()Z", 0), new PropertyReference1Impl(Criticals.class, "ground5", "getGround5()Z", 0), new PropertyReference1Impl(Criticals.class, "full5", "getFull5()Z", 0), new PropertyReference1Impl(Criticals.class, "offset5", "getOffset5()F", 0) };
   @NotNull
   public static final Criticals INSTANCE = new Criticals();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", Criticals.Mode.NCP, null, null, 12, null);
   @NotNull
   private static final EnumSetting ncpMode = SettingFactory.defaultImpl(
      INSTANCE, "ncp-mode", Criticals.NcpMode.NEW, null, () -> INSTANCE.getMode() == Criticals.Mode.NCP, 4, null
   );
   @NotNull
   private static final GroupSetting packet1 = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "packet-1", null, () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM, 2, null
   );
   @NotNull
   private static final BooleanSetting sendPacket1 = SettingFactory.computeBooleanSetting(
      INSTANCE, "send-packet-1", true, INSTANCE.getPacket1(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting ground1 = SettingFactory.computeBooleanSetting(
      INSTANCE, "ground-1", true, INSTANCE.getPacket1(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting full1 = SettingFactory.computeBooleanSetting(
      INSTANCE, "full-1", true, INSTANCE.getPacket1(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final FloatSetting offset1 = SettingFactory.computeFloatSetting(
      INSTANCE, "offset-1", 0.0F, RangesKt.rangeTo(0.0F, 1.5F), INSTANCE.getPacket1(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final GroupSetting packet2 = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "packet-2", null, () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM, 2, null
   );
   @NotNull
   private static final BooleanSetting sendPacket2 = SettingFactory.computeBooleanSetting(
      INSTANCE, "send-packet-2", true, INSTANCE.getPacket2(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting ground2 = SettingFactory.computeBooleanSetting(
      INSTANCE, "ground-2", false, INSTANCE.getPacket2(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting full2 = SettingFactory.computeBooleanSetting(
      INSTANCE, "full-2", true, INSTANCE.getPacket2(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final FloatSetting offset2 = SettingFactory.computeFloatSetting(
      INSTANCE, "offset-2", 0.0625F, RangesKt.rangeTo(0.0F, 1.5F), INSTANCE.getPacket2(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final GroupSetting packet3 = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "packet-3", null, () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM, 2, null
   );
   @NotNull
   private static final BooleanSetting sendPacket3 = SettingFactory.computeBooleanSetting(
      INSTANCE, "send-packet-3", true, INSTANCE.getPacket3(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting ground3 = SettingFactory.computeBooleanSetting(
      INSTANCE, "ground-3", false, INSTANCE.getPacket3(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting full3 = SettingFactory.computeBooleanSetting(
      INSTANCE, "full-3", true, INSTANCE.getPacket3(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final FloatSetting offset3 = SettingFactory.computeFloatSetting(
      INSTANCE, "offset-3", 0.04535F, RangesKt.rangeTo(0.0F, 1.5F), INSTANCE.getPacket3(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final GroupSetting packet4 = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "packet-4", null, () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM, 2, null
   );
   @NotNull
   private static final BooleanSetting sendPacket4 = SettingFactory.computeBooleanSetting(
      INSTANCE, "send-packet-4", false, INSTANCE.getPacket4(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting ground4 = SettingFactory.computeBooleanSetting(
      INSTANCE, "ground-4", false, INSTANCE.getPacket4(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting full4 = SettingFactory.computeBooleanSetting(
      INSTANCE, "full-4", false, INSTANCE.getPacket4(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final FloatSetting offset4 = SettingFactory.computeFloatSetting(
      INSTANCE, "offset-4", 0.0F, RangesKt.rangeTo(0.0F, 1.5F), INSTANCE.getPacket4(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final GroupSetting packet5 = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "packet-5", null, () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM, 2, null
   );
   @NotNull
   private static final BooleanSetting sendPacket5 = SettingFactory.computeBooleanSetting(
      INSTANCE, "send-packet-5", false, INSTANCE.getPacket5(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting ground5 = SettingFactory.computeBooleanSetting(
      INSTANCE, "ground-5", false, INSTANCE.getPacket5(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final BooleanSetting full5 = SettingFactory.computeBooleanSetting(
      INSTANCE, "full-5", false, INSTANCE.getPacket5(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );
   @NotNull
   private static final FloatSetting offset5 = SettingFactory.computeFloatSetting(
      INSTANCE, "offset-5", 0.0F, RangesKt.rangeTo(0.0F, 1.5F), INSTANCE.getPacket5(), () -> INSTANCE.getMode() == Criticals.Mode.CUSTOM
   );

   private Criticals() {
      super("criticals", SubCategories.getSubCategory2(), null, 4, null);
   }

   @NotNull
   public final Criticals.Mode getMode() {
      return (Criticals.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final Criticals.NcpMode getNcpMode() {
      return (Criticals.NcpMode)ncpMode.computeObject(this, $$delegatedProperties[1]);
   }

   private final GroupValue getPacket1() {
      return (GroupValue)packet1.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getSendPacket1() {
      return (Boolean)sendPacket1.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getGround1() {
      return (Boolean)ground1.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getFull1() {
      return (Boolean)full1.computeObject(this, $$delegatedProperties[5]);
   }

   private final float getOffset1() {
      return ((Number)offset1.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   private final GroupValue getPacket2() {
      return (GroupValue)packet2.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getSendPacket2() {
      return (Boolean)sendPacket2.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getGround2() {
      return (Boolean)ground2.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getFull2() {
      return (Boolean)full2.computeObject(this, $$delegatedProperties[10]);
   }

   private final float getOffset2() {
      return ((Number)offset2.computeObject(this, $$delegatedProperties[11])).floatValue();
   }

   private final GroupValue getPacket3() {
      return (GroupValue)packet3.computeObject(this, $$delegatedProperties[12]);
   }

   private final boolean getSendPacket3() {
      return (Boolean)sendPacket3.computeObject(this, $$delegatedProperties[13]);
   }

   private final boolean getGround3() {
      return (Boolean)ground3.computeObject(this, $$delegatedProperties[14]);
   }

   private final boolean getFull3() {
      return (Boolean)full3.computeObject(this, $$delegatedProperties[15]);
   }

   private final float getOffset3() {
      return ((Number)offset3.computeObject(this, $$delegatedProperties[16])).floatValue();
   }

   private final GroupValue getPacket4() {
      return (GroupValue)packet4.computeObject(this, $$delegatedProperties[17]);
   }

   private final boolean getSendPacket4() {
      return (Boolean)sendPacket4.computeObject(this, $$delegatedProperties[18]);
   }

   private final boolean getGround4() {
      return (Boolean)ground4.computeObject(this, $$delegatedProperties[19]);
   }

   private final boolean getFull4() {
      return (Boolean)full4.computeObject(this, $$delegatedProperties[20]);
   }

   private final float getOffset4() {
      return ((Number)offset4.computeObject(this, $$delegatedProperties[21])).floatValue();
   }

   private final GroupValue getPacket5() {
      return (GroupValue)packet5.computeObject(this, $$delegatedProperties[22]);
   }

   private final boolean getSendPacket5() {
      return (Boolean)sendPacket5.computeObject(this, $$delegatedProperties[23]);
   }

   private final boolean getGround5() {
      return (Boolean)ground5.computeObject(this, $$delegatedProperties[24]);
   }

   private final boolean getFull5() {
      return (Boolean)full5.computeObject(this, $$delegatedProperties[25]);
   }

   private final float getOffset5() {
      return ((Number)offset5.computeObject(this, $$delegatedProperties[26])).floatValue();
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundAttackPacket) {
         Entity entity = this.fetchEntity((ServerboundAttackPacket)sendPacket.getPacket());
         if (entity == null || entity instanceof EndCrystal) {
            return;
         }

         this.applyGetAbilities();
      }
   }

   private final void applyGetAbilities() {
      if ((MinecraftHolder.getLocalPlayer().onGround() || MinecraftHolder.getLocalPlayer().getAbilities().flying)
         && !MinecraftHolder.getLocalPlayer().isInLava()
         && !MinecraftHolder.getLocalPlayer().isUnderWater()) {
         switch (Criticals.WhenMappings.values[this.getMode().ordinal()]) {
            case 1:
               this.applyOrdinal();
               break;
            case 2:
               this.update();
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }
   }

   private final void update() {
      if (this.getSendPacket1()) {
         this.applyGetX(this.getOffset1(), this.getFull1(), this.getGround1());
      }

      if (this.getSendPacket2()) {
         this.applyGetX(this.getOffset2(), this.getFull2(), this.getGround2());
      }

      if (this.getSendPacket3()) {
         this.applyGetX(this.getOffset3(), this.getFull3(), this.getGround3());
      }

      if (this.getSendPacket4()) {
         this.applyGetX(this.getOffset4(), this.getFull4(), this.getGround4());
      }

      if (this.getSendPacket5()) {
         this.applyGetX(this.getOffset5(), this.getFull5(), this.getGround5());
      }
   }

   private final void applyOrdinal() {
      switch (Criticals.WhenMappings.values2[this.getNcpMode().ordinal()]) {
         case 1:
            defaultImpl(this, 1.058293536E-5, false, false, 6, null);
            defaultImpl(this, 9.16580235E-6, false, false, 6, null);
            defaultImpl(this, 1.0371854E-7, false, false, 6, null);
            break;
         case 2:
            defaultImpl(this, 0.0625, false, false, 4, null);
            defaultImpl(this, 0.0, false, false, 4, null);
            break;
         case 3:
            defaultImpl(this, 2.71875E-7, false, false, 6, null);
            defaultImpl(this, 0.0, false, false, 6, null);
            break;
         case 4:
            defaultImpl(this, 0.062600301692775, false, false, 6, null);
            defaultImpl(this, 0.07260029960661, false, false, 6, null);
            defaultImpl(this, 0.0, false, false, 6, null);
            defaultImpl(this, 0.0, false, false, 6, null);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }
   }

   private final void applyGetX(double amount, boolean flag, boolean flag2) {
      LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
      if (!flag) {
         Packet pos = (Packet)(new Pos(localPlayer.getX(), localPlayer.getY() + amount, localPlayer.getZ(), flag2, localPlayer.horizontalCollision));
         PacketUtil.applySend(pos);
      } else {
         Packet posRot = (Packet)(
            new PosRot(
               localPlayer.getX(),
               localPlayer.getY() + amount,
               localPlayer.getZ(),
               ((LocalPlayerAccessor)MinecraftHolder.getMinecraft().player).getLastYaw(),
               ((LocalPlayerAccessor)MinecraftHolder.getMinecraft().player).getLastPitch(),
               flag2,
               localPlayer.horizontalCollision
            )
         );
         PacketUtil.applySend(posRot);
      }
   }

   public static void defaultImpl(Criticals criticals, double amount, boolean flag, boolean flag2, int value, Object object) {
      if ((value & 2) != 0) {
         flag = false;
      }

      if ((value & 4) != 0) {
         flag2 = false;
      }

      criticals.applyGetX(amount, flag, flag2);
   }

   @Nullable
   public final Entity fetchEntity(ServerboundAttackPacket packet) {
      return MinecraftHolder.getClientLevel().getEntity(packet.entityId);
   }

   static {
   }

   public static enum Mode {
      NCP,
      CUSTOM;

      private static final Criticals.Mode[] modes = $values();
      private static final EnumEntries<Criticals.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<Criticals.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final Criticals.Mode[] $values() {
         return new Criticals.Mode[]{NCP, CUSTOM};
      }
   }

   public static enum NcpMode {
      NORMAL,
      STRICT,
      OLD,
      NEW;

      private static final Criticals.NcpMode[] ncpModes = $values();
      private static final EnumEntries<Criticals.NcpMode> enumEntries = EnumEntriesKt.enumEntries(ncpModes);

      private NcpMode() {
      }

      @NotNull
      public static EnumEntries<Criticals.NcpMode> getEnumEntries() {
         return enumEntries;
      }

      private static final Criticals.NcpMode[] $values() {
         return new Criticals.NcpMode[]{NORMAL, STRICT, OLD, NEW};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;
      public static final int[] values2;

      static {
         int[] values3 = new int[Criticals.Mode.values().length];

         try {
            values3[Criticals.Mode.NCP.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values3[Criticals.Mode.CUSTOM.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values3;
         values3 = new int[Criticals.NcpMode.values().length];

         try {
            values3[Criticals.NcpMode.OLD.ordinal()] = 1;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values3[Criticals.NcpMode.NORMAL.ordinal()] = 2;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values3[Criticals.NcpMode.NEW.ordinal()] = 3;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values3[Criticals.NcpMode.STRICT.ordinal()] = 4;
         } catch (NoSuchFieldError exception6) {
         }

         values2 = values3;
      }
   }
}