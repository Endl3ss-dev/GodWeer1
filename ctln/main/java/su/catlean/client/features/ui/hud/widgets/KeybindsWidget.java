// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.KeyBind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.misc.ClientInterface;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ModuleManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.KeyNames;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class KeybindsWidget extends Widget {
   @NotNull
   public static final KeybindsWidget INSTANCE = new KeybindsWidget();
   @NotNull
   private static final List<KeyBind> items = new ArrayList();
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 250L);

   private KeybindsWidget() {
      super("keybinds-widget");
   }

   @NotNull
   public final List<KeyBind> getItems() {
      return items;
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      Iterable<Module> items2 = ModuleManager.INSTANCE.getItems2();
      boolean flag = false;
      var arrayList = new ArrayList();

      for (Object object : items2) {
         Module module = (Module)object;
         if (!Intrinsics.areEqual(module, ClientInterface.INSTANCE)) {
            arrayList.add(object);
         }
      }

      for (Module module2 : (Iterable<Module>)arrayList) {
         var items3 = items;
         boolean flag5;
         if (items3 instanceof Collection && ((Collection)items3).isEmpty()) {
            flag5 = true;
         } else {
            Iterator<KeyBind> iterator = items3.iterator();

            while (true) {
               if (!iterator.hasNext()) {
                  flag5 = true;
                  break;
               }

               KeyBind object2 = iterator.next();
               KeyBind keyBind = (KeyBind)object2;
               if (Intrinsics.areEqual(keyBind.getName(), module2.getText3())) {
                  flag5 = false;
                  break;
               }
            }
         }

         flag = flag5;
         if (module2.getBind().getValue() != -1 && module2.getEnabled() && flag) {
            items.add(new KeyBind(module2.getText3(), KeyNames.INSTANCE.computeText(module2.getBind().getValue()), null, null, false, 28, null));
         } else if (!flag && (module2.getBind().getValue() == -1 || !module2.getEnabled())) {
            items3 = items;
            var arrayList2 = new ArrayList();

            for (Object object3 : items3) {
               KeyBind keyBind2 = (KeyBind)object3;
               if (Intrinsics.areEqual(keyBind2.getName(), module2.getText3()) && Intrinsics.areEqual(keyBind2.getExtra(), "")) {
                  arrayList2.add(object3);
               }
            }

            items3 = arrayList2;

            for (Object object4 : items3) {
               KeyBind keyBind3 = (KeyBind)object4;
               keyBind3.setFlag(true);
            }
         }
      }

      items.removeIf(arg -> {
         ((KeyBind)arg).getAnimation().applyDoubleValue(((KeyBind)arg).isFlag() ? 0.0F : 1.0F);
         return ((KeyBind)arg).isFlag() && ((KeyBind)arg).getAnimation().getAmount() <= 0.0F;
      });
      String text = "Key Binds";
      boolean flag11 = this.getAmount3() > this.getValue2() / 2.0F;
      animation.applyDoubleValue(
         items.isEmpty()
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)
            ? 0.0F
            : 1.0F
      );
      this.render(context, text, animation.getAmount(), flag11);
      float value = 17.0F;
      float amount = Fonts.getFontRenderer().computeAmount(text) + 24.0F;

      for (KeyBind item : (Iterable<KeyBind>)items) {
         FontRenderer fontRenderer = Fonts.getFontRenderer();
         CharSequence extra = item.getExtra();
         CharSequence object5;
         if (extra.length() == 0) {
            FontRenderer fontRenderer2 = fontRenderer;
            object5 = StringsKt.trim(item.getName()).toString();
            fontRenderer = fontRenderer2;
         } else {
            object5 = extra;
         }

         float amount2 = fontRenderer.computeAmount((String)object5);
         float amount3 = Fonts.getFontRenderer().computeAmount(item.getBind());
         if (amount2 + amount3 + 13.0F > amount) {
            amount = amount2 + amount3 + 13.0F;
         }
      }

      for (KeyBind item2 : (Iterable<KeyBind>)items) {
         FontRenderer fontRenderer3 = Fonts.getFontRenderer();
         CharSequence extra2 = item2.getExtra();
         Object context2;
         if (extra2.length() == 0) {
            FontRenderer fontRenderer4 = fontRenderer3;
            context2 = StringsKt.trim(item2.getName()).toString();
            fontRenderer3 = fontRenderer4;
         } else {
            context2 = extra2;
         }

         float amount4 = fontRenderer3.computeAmount((String)context2);
         float amount5 = Fonts.getFontRenderer().computeAmount(item2.getBind());
         float amount32 = flag11
            ? this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * -10 - (amount4 + amount5 + 13.0F - amount)
            : this.getAmount3() - (1.0F - item2.getAnimation().getAmount()) * 10;
         Widget.defaultImpl(this, context.pose(), amount32, this.getAmount4() + value, amount4 + 6.0F, 13.0F, item2.getAnimation().getAmount(), false, 64, null);
         Widget.defaultImpl(
            this,
            context.pose(),
            amount32 + (amount4 + 7.0F) * item2.getAnimation().getAmount(),
            this.getAmount4() + value,
            amount5 + 8.0F,
            13.0F,
            item2.getAnimation().getAmount(),
            false,
            64,
            null
         );
         KeybindsWidget thisValue = this;
         context2 = context;
         CharSequence extra3 = item2.getExtra();
         CharSequence object6;
         if (extra3.length() == 0) {
            String text2 = StringsKt.trim(item2.getName()).toString();
            thisValue = this;
            context2 = context;
            object6 = text2;
         } else {
            object6 = extra3;
         }

         thisValue.render(
            (GuiGraphicsExtractor)context2,
            (String)object6,
            amount32 + 3.0F,
            this.getAmount4() + value + 1.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), item2.getAnimation().getAmount())
         );
         this.render(
            context,
            item2.getBind(),
            amount32 + (amount4 + 11.0F) * item2.getAnimation().getAmount(),
            this.getAmount4() + value + 1.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), item2.getAnimation().getAmount())
         );
         value += 14.0F * item2.getAnimation().getAmount();
      }

      this.updateAmount(amount, value - 2.0F);
   }
}