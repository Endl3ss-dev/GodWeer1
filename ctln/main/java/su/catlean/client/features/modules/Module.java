// deobf by pusia
package su.catlean.client.features.modules;

import su.catlean.utils.KotlinCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.category.SubCategory;
import su.catlean.api.setting.Keybindable;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.SettingHolder;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.services.NotificationManager;
import su.catlean.gofra.Gofra;
import su.catlean.interfaces.IEntity;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.chat.MessageType;

public abstract class Module implements SettingHolder, Keybindable {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(Module.class, "enabled", "getEnabled()Z", 0), new MutablePropertyReference1Impl(Module.class, "bind", "getBind()Lsu/catlean/dd;", 0) };
   @NotNull
   private final String id;
   @NotNull
   private final SubCategory category;
   @NotNull
   private final List aliases;
   @NotNull
   private final List items;
   @NotNull
   private final List items2;
   @NotNull
   private final String text;
   @NotNull
   private final BooleanSetting enabled;
   @NotNull
   private final BindSetting bind;

   public Module(String id, SubCategory category, List aliases) {
      this.id = id;
      this.category = category;
      this.aliases = aliases;
      this.items = new ArrayList();
      this.items2 = new ArrayList();
      StringBuilder stringBuilder = new StringBuilder();
      StringBuilder stringBuilder2 = stringBuilder;
      CharSequence charSequence = this.id;
      String[] texts = new String[]{"-"};

      for (Object splitdefaultItem : KotlinCompat.split(charSequence, texts, false, 0, 6, null)) {
         String text2 = (String)splitdefaultItem;
         StringBuilder stringBuilder3 = stringBuilder2;
         String text3;
         if (text2.length() > 0) {
            StringBuilder stringBuilder4 = new StringBuilder();
            char charAt = text2.charAt(0);
            StringBuilder stringBuilder5 = stringBuilder4;
            byte byteValue = 0;
            CharSequence upperCase = String.valueOf(charAt).toUpperCase(Locale.ROOT);
            stringBuilder3 = stringBuilder2;
            StringBuilder stringBuilder6 = stringBuilder5.append((Object)upperCase);
            byteValue = 1;
            text3 = stringBuilder6.append(text2.substring(byteValue)).toString();
         } else {
            text3 = text2;
         }

         stringBuilder3.append(text3);
         stringBuilder2.append(' ');
         StringsKt.trim(stringBuilder2);
      }

      this.text = stringBuilder.toString();
      this.enabled = SettingFactory.defaultImpl(this, "enabled", false, null, null, 12, null);
      this.bind = SettingFactory.defaultImpl(this, "bind", new Bind(0, false, false, 7, null), null, null, 12, null);
   }

   public Module(String text, SubCategory subCategory, List items, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 4) != 0) {
         items = CollectionsKt.emptyList();
      }

      this(text, subCategory, items);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final SubCategory getCategory() {
      return this.category;
   }

   @NotNull
   public final List getAliases() {
      return this.aliases;
   }

   @NotNull
   @Override
   public List getItems5() {
      return this.items;
   }

   @NotNull
   @Override
   public List getItems6() {
      return this.items2;
   }

   @NotNull
   public final String getText3() {
      return this.text;
   }

   public boolean getEnabled() {
      return (Boolean)this.enabled.computeObject(this, $$delegatedProperties[0]);
   }

   public void updateFlag(boolean flag) {
      this.enabled.updateObject(this, $$delegatedProperties[0], flag);
   }

   @NotNull
   public Bind getBind() {
      return (Bind)this.bind.computeObject(this, $$delegatedProperties[1]);
   }

   public void updateBind(Bind bind) {
      this.bind.updateObject(this, $$delegatedProperties[1], bind);
   }

   public final boolean isFlag6() {
      return MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getMinecraft().level != null;
   }

   @Override
   public void update5() {
      if (this.getEnabled()) {
         this.applyUnplug();
      } else {
         this.applyPlug();
      }
   }

   @Override
   public void applyPlug() {
      this.updateFlag(true);
      Gofra.INSTANCE.unplug(this);
      Gofra.INSTANCE.plug(this);
      this.update2();
      NotificationManager.defaultImpl(
         NotificationManager.INSTANCE, this.text, ChatUtil.computeText(this, "enable", new Object[0], true), 1, MessageType.ENABLED, null, 16, null
      );
   }

   public final void updateText10(String message) {
      this.applyUnplug();
      ChatUtil.defaultImpl(this, message, false, 2, null);
   }

   @Override
   public void applyUnplug() {
      this.updateFlag(false);
      Gofra.INSTANCE.unplug(this);
      this.update4();
      NotificationManager.defaultImpl(
         NotificationManager.INSTANCE, this.text, ChatUtil.computeText(this, "disable", new Object[0], true), 1, MessageType.DISABLED, null, 16, null
      );
   }

   public void update2() {
   }

   public void update4() {
   }

   @NotNull
   public final Vec3 computeVec3(Entity self) {
      return ((IEntity)self).catLean$getPosition();
   }

   static {
   }
}