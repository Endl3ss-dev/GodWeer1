// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.LivingEntityAccessor;
import su.catlean.mixins.accessors.MinecraftAccessor;
import su.catlean.utils.MinecraftHolder;

public final class NoDelay extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(NoDelay.class, "delay", "getDelay()I", 0), new MutablePropertyReference1Impl(NoDelay.class, "jump", "getJump()Z", 0), new MutablePropertyReference1Impl(NoDelay.class, "blocks", "getBlocks()Z", 0), new MutablePropertyReference1Impl(NoDelay.class, "crystals", "getCrystals()Z", 0), new MutablePropertyReference1Impl(NoDelay.class, "xp", "getXp()Z", 0), new MutablePropertyReference1Impl(NoDelay.class, "all", "getAll()Z", 0) };
   @NotNull
   public static final NoDelay INSTANCE = new NoDelay();
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 1, new IntRange(0, 4), null, null, 24, null);
   @NotNull
   private static final BooleanSetting jump = SettingFactory.defaultImpl(INSTANCE, "jump", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting blocks = SettingFactory.defaultImpl(INSTANCE, "blocks", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting crystals = SettingFactory.defaultImpl(INSTANCE, "crystals", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting xP = SettingFactory.defaultImpl(INSTANCE, "x-p", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting all = SettingFactory.defaultImpl(INSTANCE, "all", true, null, null, 12, null);

   private NoDelay() {
      super("no-delay", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final boolean getJump() {
      return (Boolean)jump.computeObject(this, $$delegatedProperties[1]);
   }

   public final boolean getBlocks() {
      return (Boolean)blocks.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getCrystals() {
      return (Boolean)crystals.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getXp() {
      return (Boolean)xP.computeObject(this, $$delegatedProperties[4]);
   }

   public final boolean getAll() {
      return (Boolean)all.computeObject(this, $$delegatedProperties[5]);
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      if (((LivingEntityAccessor)MinecraftHolder.getLocalPlayer()).getLastJumpCooldown() > 0 && this.getJump()) {
         ((LivingEntityAccessor)MinecraftHolder.getLocalPlayer()).setLastJumpCooldown(0);
      }

      if (this.testItem(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem())
         && ((MinecraftAccessor)MinecraftHolder.getMinecraft()).getUseCooldown() > this.getDelay()) {
         ((MinecraftAccessor)MinecraftHolder.getMinecraft()).setUseCooldown(this.getDelay());
      }
   }

   private final boolean testItem(Item item) {
      return item instanceof BlockItem && this.getBlocks()
         || item == Items.END_CRYSTAL && this.getCrystals()
         || item == Items.EXPERIENCE_BOTTLE && this.getXp()
         || this.getAll();
   }

   static {
   }
}