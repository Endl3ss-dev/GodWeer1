// deobf by pusia
package su.catlean.client.features.modules.basic;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.CollisionEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class Avoid extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Avoid.class, "unloaded", "getUnloaded()Z", 0), new PropertyReference1Impl(Avoid.class, "voidAir", "getVoidAir()Z", 0), new PropertyReference1Impl(Avoid.class, "cactus", "getCactus()Z", 0), new PropertyReference1Impl(Avoid.class, "fire", "getFire()Z", 0), new PropertyReference1Impl(Avoid.class, "berryBush", "getBerryBush()Z", 0), new PropertyReference1Impl(Avoid.class, "powderSnow", "getPowderSnow()Z", 0), new PropertyReference1Impl(Avoid.class, "lava", "getLava()Z", 0), new PropertyReference1Impl(Avoid.class, "trapString", "getTrapString()Z", 0), new PropertyReference1Impl(Avoid.class, "plate", "getPlate()Z", 0), new PropertyReference1Impl(Avoid.class, "turtleEggs", "getTurtleEggs()Z", 0) };
   @NotNull
   public static final Avoid INSTANCE = new Avoid();
   @NotNull
   private static final BooleanSetting unloaded = SettingFactory.defaultImpl(INSTANCE, "unloaded", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting voidAir = SettingFactory.defaultImpl(INSTANCE, "void-air", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting cactus = SettingFactory.defaultImpl(INSTANCE, "cactus", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting fire = SettingFactory.defaultImpl(INSTANCE, "fire", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting berryBush = SettingFactory.defaultImpl(INSTANCE, "berry-bush", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting powderSnow = SettingFactory.defaultImpl(INSTANCE, "powder-snow", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting lava = SettingFactory.defaultImpl(INSTANCE, "lava", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting trapString = SettingFactory.defaultImpl(INSTANCE, "trap-string", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting plate = SettingFactory.defaultImpl(INSTANCE, "plate", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting turtleEggs = SettingFactory.defaultImpl(INSTANCE, "turtle-eggs", false, null, null, 12, null);

   private Avoid() {
      super("avoid", SubCategories.getSubCategory4(), null, 4, null);
   }

   private final boolean getUnloaded() {
      return (Boolean)unloaded.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getVoidAir() {
      return (Boolean)voidAir.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getCactus() {
      return (Boolean)cactus.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getFire() {
      return (Boolean)fire.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getBerryBush() {
      return (Boolean)berryBush.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getPowderSnow() {
      return (Boolean)powderSnow.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getLava() {
      return (Boolean)lava.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getTrapString() {
      return (Boolean)trapString.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getPlate() {
      return (Boolean)plate.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getTurtleEggs() {
      return (Boolean)turtleEggs.computeObject(this, $$delegatedProperties[9]);
   }

   @Flow
   private final void onCollision(CollisionEvent collisionEvent) {
      BlockState state = collisionEvent.getState();
      boolean flag = !MinecraftHolder.getClientLevel()
            .hasChunk(
               (int)INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).x() >> 4,
               (int)INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).z() >> 4
            )
         && INSTANCE.getUnloaded();
      boolean flag2 = collisionEvent.getPos().getY() < MinecraftHolder.getClientLevel().getMinY() && INSTANCE.getVoidAir();
      boolean flag3 = Intrinsics.areEqual(state.getBlock(), Blocks.CACTUS) && INSTANCE.getCactus();
      boolean flag4 = (Intrinsics.areEqual(state.getBlock(), Blocks.FIRE) || Intrinsics.areEqual(state.getBlock(), Blocks.SOUL_FIRE)) && INSTANCE.getFire();
      boolean flag5 = Intrinsics.areEqual(state.getBlock(), Blocks.SWEET_BERRY_BUSH) && INSTANCE.getBerryBush();
      boolean flag6 = Intrinsics.areEqual(state.getBlock(), Blocks.POWDER_SNOW) && INSTANCE.getPowderSnow();
      boolean flag7 = Intrinsics.areEqual(state.getBlock(), Blocks.LAVA) && INSTANCE.getLava();
      boolean flag8 = (
            state.getBlock() instanceof PressurePlateBlock
               || Intrinsics.areEqual(state.getBlock(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE)
               || Intrinsics.areEqual(state.getBlock(), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)
         )
         && INSTANCE.getPlate();
      boolean flag9 = state.getBlock() instanceof TripWireBlock && (Boolean)state.getValue((Property)TripWireHookBlock.ATTACHED) && INSTANCE.getTrapString();
      boolean flag10 = state.getBlock() instanceof TurtleEggBlock && INSTANCE.getTurtleEggs();
      if (flag || flag4 || flag3 || flag7 || flag5 || flag6 || flag8 || flag9 || flag2 || flag10) {
         collisionEvent.setState(Blocks.DIRT.defaultBlockState());
      }
   }

   static {
   }
}