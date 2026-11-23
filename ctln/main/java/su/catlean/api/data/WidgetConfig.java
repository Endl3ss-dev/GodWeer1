// deobf by pusia
package su.catlean.api.data;

import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Serializable
public final class WidgetConfig {
   @NotNull
   public static final WidgetConfig.Companion companion = new WidgetConfig.Companion(null);
   @NotNull
   private final String id;
   @NotNull
   private List widgets;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public WidgetConfig(String id, List widgets) {
      this.id = id;
      this.widgets = widgets;
   }

   public WidgetConfig(String text, List items, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         items = CollectionsKt.emptyList();
      }

      this(text, items);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final List getWidgets() {
      return this.widgets;
   }

   public final void updateItems(List items) {
      this.widgets = items;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final List getWidgets2() {
      return this.widgets;
   }

   @NotNull
   @Override
   public String toString() {
      return "WidgetConfig(id=" + this.id + ", widgets=" + this.widgets + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      return hashCode * 31 + this.widgets.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof WidgetConfig widgetConfig)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.id, widgetConfig.id) ? false : Intrinsics.areEqual(this.widgets, widgetConfig.widgets);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(WidgetConfig widgetConfig, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      Lazy[] lazys2 = lazys;
      compositeEncoder.encodeStringElement(serialDescriptor, 0, widgetConfig.id);
      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 1) ? true : !Intrinsics.areEqual(widgetConfig.widgets, CollectionsKt.emptyList())) {
         compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)lazys2[1].getValue(), widgetConfig.widgets);
      }
   }

   public WidgetConfig(int value, String text, List items, SerializationConstructorMarker serializationConstructorMarker) {
      if (1 != (1)) {
         PluginExceptionsKt.throwMissingFieldException(value, 1, WidgetConfig$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      if ((value) == 0) {
         this.widgets = CollectionsKt.emptyList();
      } else {
         this.widgets = items;
      }
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         null, LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)ModuleValue$$serializer.INSTANCE)))
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)WidgetConfig$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}