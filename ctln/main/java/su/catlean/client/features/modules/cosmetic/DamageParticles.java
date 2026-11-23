// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.esp.HitParticle;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class DamageParticles extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(DamageParticles.class, "color1", "getColor1()Ljava/awt/Color;", 0), new PropertyReference1Impl(DamageParticles.class, "color2", "getColor2()Ljava/awt/Color;", 0), new PropertyReference1Impl(DamageParticles.class, "size", "getSize()F", 0), new PropertyReference1Impl(DamageParticles.class, "ticks", "getTicks()I", 0) };
   @NotNull
   public static final DamageParticles INSTANCE = new DamageParticles();
   @NotNull
   private static final ColorSetting healColor = SettingFactory.defaultImpl(INSTANCE, "heal-color", Color.GREEN, null, null, 12, null);
   @NotNull
   private static final ColorSetting damageColor = SettingFactory.defaultImpl(INSTANCE, "damage-color", Color.RED, null, null, 12, null);
   @NotNull
   private static final FloatSetting size = SettingFactory.defaultImpl(INSTANCE, "size", 0.5F, RangesKt.rangeTo(0.1F, 3.0F), null, null, 24, null);
   @NotNull
   private static final IntSetting ticks = SettingFactory.defaultImpl(INSTANCE, "ticks", 35, new IntRange(5, 60), null, null, 24, null);
   @NotNull
   private static final HashMap<Integer, Float> map = new HashMap();
   @NotNull
   private static final ArrayList<HitParticle> items = new ArrayList();
   @NotNull
   private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");

   private DamageParticles() {
      super("damage-particles", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final Color getColor1() {
      return (Color)healColor.computeObject(this, $$delegatedProperties[0]);
   }

   private final Color getColor2() {
      return (Color)damageColor.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getTicks() {
      return ((Number)ticks.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   @NotNull
   public final DecimalFormat getDecimalFormat() {
      return decimalFormat;
   }

   @Override
   public void update2() {
      items.clear();
      map.clear();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      for (Entity entitiesForRenderingItem : MinecraftHolder.getClientLevel().entitiesForRendering()) {
         if (!(MinecraftHolder.getLocalPlayer().distanceTo(entitiesForRenderingItem) > 10.0F) && entitiesForRenderingItem instanceof LivingEntity) {
            Float orDefault = (Float)map.getOrDefault(((LivingEntity)entitiesForRenderingItem).getId(), ((LivingEntity)entitiesForRenderingItem).getMaxHealth());
            float value = orDefault != null ? orDefault : 20.0F;
            float health = ((LivingEntity)entitiesForRenderingItem).getHealth() + ((LivingEntity)entitiesForRenderingItem).getAbsorptionAmount();
            map.put(((LivingEntity)entitiesForRenderingItem).getId(), health);
            if (value != health) {
               items.add(
                  new HitParticle(
                     entitiesForRenderingItem,
                     health - value,
                     ((LivingEntity)entitiesForRenderingItem).getX() + MathUtil.defaultImpl(-0.5, 0.5, false, 4, null),
                     ((LivingEntity)entitiesForRenderingItem).getY() + ((LivingEntity)entitiesForRenderingItem).getBoundingBox().getYsize() / 2.0F,
                     ((LivingEntity)entitiesForRenderingItem).getZ() + MathUtil.defaultImpl(-0.5, 0.5, false, 4, null),
                     0,
                     32,
                     null
                  )
               );
            }
         }
      }

      items.removeIf(arg -> {
         int value2 = ((HitParticle)arg).getValue();
         ((HitParticle)arg).setValue(value2 + 1);
         return value2 >= INSTANCE.getTicks();
      });
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      for (Object item : items) {
         HitParticle hitParticle = (HitParticle)item;
         float ticks2 = INSTANCE.getTicks() / 2.0F;
         float coerceIn = hitParticle.getValue() < ticks2 ? RangesKt.coerceIn(hitParticle.getValue() / 4.0F, 0.0F, 1.0F) : 1.0F - (hitParticle.getValue() - ticks2) / ticks2;
         Render3D instance = Render3D.INSTANCE;
         String amount = decimalFormat.format(hitParticle.getAmount());
         Vec3 vec3 = new Vec3(hitParticle.getAmount2(), hitParticle.getAmount3(), hitParticle.getAmount4());
         Color color = Render2D.INSTANCE.computeColor(hitParticle.getAmount() > 0.0F ? INSTANCE.getColor1() : INSTANCE.getColor2(), coerceIn);
         FontRenderer fontRenderer8 = Fonts.getFontRenderer8();
         Object object = null;
         byte byteValue = 24;
         FontRenderer fontRenderer = fontRenderer8;
         boolean flag = true;
         float value = 0.0F;
         float value2 = 0.0F;
         Color color2 = color;
         Vec3 vec32 = vec3;
         String text = amount;
         Render3D.defaultImpl(instance, text, vec32, color2, value2, value, flag, fontRenderer, byteValue, object);
      }
   }

   static {
   }
}