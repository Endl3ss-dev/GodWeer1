// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.InventoryManager;

public final class PvpResourcesWidget extends Widget {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(PvpResourcesWidget.class, "totems", "getTotems()Z", 0), new PropertyReference1Impl(PvpResourcesWidget.class, "crystals", "getCrystals()Z", 0), new PropertyReference1Impl(PvpResourcesWidget.class, "gapples", "getGapples()Z", 0), new PropertyReference1Impl(PvpResourcesWidget.class, "crapples", "getCrapples()Z", 0), new PropertyReference1Impl(PvpResourcesWidget.class, "fireworks", "getFireworks()Z", 0), new PropertyReference1Impl(PvpResourcesWidget.class, "xp", "getXp()Z", 0), new PropertyReference1Impl(PvpResourcesWidget.class, "obby", "getObby()Z", 0) };
   @NotNull
   public static final PvpResourcesWidget INSTANCE = new PvpResourcesWidget();
   @NotNull
   private static final BooleanSetting totems = SettingFactory.defaultImpl(INSTANCE, "totems", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting crystals = SettingFactory.defaultImpl(INSTANCE, "crystals", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting gapples = SettingFactory.defaultImpl(INSTANCE, "gapples", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting crapples = SettingFactory.defaultImpl(INSTANCE, "crapples", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting fireworks = SettingFactory.defaultImpl(INSTANCE, "fireworks", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting xp = SettingFactory.defaultImpl(INSTANCE, "xp", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting obby = SettingFactory.defaultImpl(INSTANCE, "obby", false, null, null, 12, null);

   private PvpResourcesWidget() {
      super("pvp-resources-widget");
   }

   private final boolean getTotems() {
      return (Boolean)totems.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getCrystals() {
      return (Boolean)crystals.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getGapples() {
      return (Boolean)gapples.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getCrapples() {
      return (Boolean)crapples.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getFireworks() {
      return (Boolean)fireworks.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getXp() {
      return (Boolean)xp.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getObby() {
      return (Boolean)obby.computeObject(this, $$delegatedProperties[6]);
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      byte byteValue = 2;
      List<Item> arrayList = new ArrayList();
      if (this.getTotems()) {
         arrayList.add(Items.TOTEM_OF_UNDYING);
      }

      if (this.getCrystals()) {
         arrayList.add(Items.END_CRYSTAL);
      }

      if (this.getXp()) {
         arrayList.add(Items.EXPERIENCE_BOTTLE);
      }

      if (this.getGapples()) {
         arrayList.add(Items.ENCHANTED_GOLDEN_APPLE);
      }

      if (this.getCrapples()) {
         arrayList.add(Items.GOLDEN_APPLE);
      }

      if (this.getFireworks()) {
         arrayList.add(Items.FIREWORK_ROCKET);
      }

      if (this.getObby()) {
         arrayList.add(Items.OBSIDIAN);
      }

      Widget.defaultImpl(this, context.pose(), this.getAmount3(), this.getAmount4(), 1.0F + 20.0F * arrayList.size(), 29.0F, 0.0F, false, 96, null);
      context.pose().pushMatrix();
      context.pose().translate(this.getAmount3(), this.getAmount4());

      for (Item item : (Iterable<Item>)arrayList) {
         if (!Intrinsics.areEqual(item, CollectionsKt.last(arrayList))) {
            Widget.defaultImpl(this, context.pose(), byteValue + 17.5F, 7.0F, 0.0F, 8, null);
         }

         context.item(item.getDefaultInstance(), byteValue, 2);
         int defaultImpl = InventoryManager.defaultImpl(InventoryManager.INSTANCE, item, false, 2, null);
         String text = defaultImpl > 0 ? String.valueOf(defaultImpl) : "-";
         FontRenderer.defaultImpl(
            Fonts.getFontRenderer(),
            context.pose(),
            text,
            byteValue + 8.0F - Fonts.getFontRenderer().computeAmount(text) / 2.0F,
            18.0F,
            Color.WHITE,
            false,
            0,
            96,
            null
         );
         byteValue += 20;
      }

      context.pose().popMatrix();
      this.updateAmount(1.0F + arrayList.size() * 20.0F, 29.0F);
   }

   @Override
   public void render2(GuiGraphicsExtractor context) {
      Fonts.getFontRenderer().update();
   }

   static {
   }
}