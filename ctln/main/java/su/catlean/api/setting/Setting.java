// deobf by pusia
package su.catlean.api.setting;

import su.catlean.utils.KotlinCompat;
import java.util.ArrayList;
import java.util.Locale;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.setting.values.GroupValue;

public abstract class Setting {
   @NotNull
   public static final Setting.Companion companion = new Setting.Companion(null);
   @NotNull
   private final String id;
   private final Object object;
   private Object object2;
   @Nullable
   private GroupValue groupValue;
   @NotNull
   private Function0 visible;
   @NotNull
   private final String text;
   @NotNull
   private static final Json json = KotlinCompat.Json(null, arg -> {
      arg.setIgnoreUnknownKeys(true);
      arg.setEncodeDefaults(true);
      return Unit.INSTANCE;
   }, 1, null);

   public Setting(String id, Object object, Object object2, GroupValue groupValue, Function0 visible) {
      this.id = id;
      this.object = object;
      this.object2 = object2;
      this.groupValue = groupValue;
      this.visible = visible;
      StringBuilder stringBuilder = new StringBuilder();
      StringBuilder stringBuilder2 = stringBuilder;
      CharSequence charSequence = this.id;
      String[] texts = new String[]{"-"};
      Iterable<String> splitdefault = KotlinCompat.split(charSequence, texts, false, 0, 6, null);
      var arrayList = new ArrayList();

      for (Object object3 : splitdefault) {
         String text2 = (String)object3;
         if (text2.length() > 0) {
            arrayList.add(object3);
         }
      }

      for (Object object4 : arrayList) {
         String text3 = (String)object4;
         stringBuilder2.append(" ");
         stringBuilder2.append(text3.substring(0, 1).toUpperCase(Locale.ROOT));
         stringBuilder2.append(text3.substring(1).toLowerCase(Locale.ROOT));
      }

      this.text = stringBuilder.toString();
   }

   public Setting(
      String text, Object object, Object object2, GroupValue groupValue, Function0 callback, int value, DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value & 4) != 0) {
         object2 = object;
      }

      if ((value & 8) != 0) {
         groupValue = null;
      }

      if ((value & 16) != 0) {
         callback = () -> true;
      }

      this(text, object, object2, groupValue, callback);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   public final Object getObject() {
      return this.object;
   }

   public final Object getObject2() {
      return this.object2;
   }

   public final void setObject2(Object object) {
      this.object2 = object;
   }

   @Nullable
   public final GroupValue getGroupValue() {
      return this.groupValue;
   }

   public final void setGroupValue(GroupValue groupValue) {
      this.groupValue = groupValue;
   }

   @NotNull
   public final Function0 getVisible() {
      return this.visible;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   public final Object computeObject(Object object, KProperty property) {
      return this.object2;
   }

   public final void updateObject(Object object, KProperty property, Object object2) {
      this.object2 = object2;
   }

   public final void update() {
      this.object2 = this.object;
   }

   public abstract void applyDecodeFromString(String text2);

   public static final Json getJson() {
      return json;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Json getJson() {
         return Setting.getJson();
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}