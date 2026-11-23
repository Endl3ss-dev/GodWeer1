// deobf by pusia
package su.catlean.client.services.discord.ipc;

import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.properties.Delegates;
import kotlin.properties.ObservableProperty;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Activity;
import su.catlean.api.data.ActivityAsset;
import su.catlean.api.data.ActivityButton;
import su.catlean.api.data.ActivityTimestamp;

public final class DiscordPresence extends DiscordIpcClient {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(DiscordPresence.class, "appId", "getAppId()Ljava/lang/Long;", 0), new MutablePropertyReference1Impl(DiscordPresence.class, "details", "getDetails()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(DiscordPresence.class, "state", "getState()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(DiscordPresence.class, "largeImage", "getLargeImage()Lkotlin/Pair;", 0), new MutablePropertyReference1Impl(DiscordPresence.class, "smallImage", "getSmallImage()Lkotlin/Pair;", 0), new MutablePropertyReference1Impl(DiscordPresence.class, "button1", "getButton1()Lkotlin/Pair;", 0), new MutablePropertyReference1Impl(DiscordPresence.class, "button2", "getButton2()Lkotlin/Pair;", 0) };
   @NotNull
   public static final DiscordPresence INSTANCE = new DiscordPresence();
   @NotNull
   private static final ReadWriteProperty readWriteProperty = defaultImpl(INSTANCE, true, null, 2, null);
   @NotNull
   private static final ReadWriteProperty readWriteProperty2 = defaultImpl(INSTANCE, false, null, 3, null);
   @NotNull
   private static final ReadWriteProperty readWriteProperty3 = defaultImpl(INSTANCE, false, null, 3, null);
   @NotNull
   private static final ReadWriteProperty readWriteProperty4 = defaultImpl(INSTANCE, false, null, 3, null);
   @NotNull
   private static final ReadWriteProperty readWriteProperty5 = defaultImpl(INSTANCE, false, null, 3, null);
   @NotNull
   private static final ReadWriteProperty readWriteProperty6 = defaultImpl(INSTANCE, false, null, 3, null);
   @NotNull
   private static final ReadWriteProperty readWriteProperty7 = defaultImpl(INSTANCE, false, null, 3, null);

   private DiscordPresence() {
   }

   @Nullable
   public final Long getAppId() {
      return (Long)readWriteProperty.getValue(this, $$delegatedProperties[0]);
   }

   public final void updateLongValue(Long longValue) {
      readWriteProperty.setValue(this, $$delegatedProperties[0], longValue);
   }

   @Nullable
   public final String getDetails() {
      return (String)readWriteProperty2.getValue(this, $$delegatedProperties[1]);
   }

   public final void updateText(String text) {
      readWriteProperty2.setValue(this, $$delegatedProperties[1], text);
   }

   @Nullable
   public final String getState() {
      return (String)readWriteProperty3.getValue(this, $$delegatedProperties[2]);
   }

   public final void updateText2(String text) {
      readWriteProperty3.setValue(this, $$delegatedProperties[2], text);
   }

   @Nullable
   public final Pair getLargeImage() {
      return (Pair)readWriteProperty4.getValue(this, $$delegatedProperties[3]);
   }

   public final void updatePair(Pair pair) {
      readWriteProperty4.setValue(this, $$delegatedProperties[3], pair);
   }

   @Nullable
   public final Pair getSmallImage() {
      return (Pair)readWriteProperty5.getValue(this, $$delegatedProperties[4]);
   }

   public final void updatePair2(Pair pair) {
      readWriteProperty5.setValue(this, $$delegatedProperties[4], pair);
   }

   @Nullable
   public final Pair getButton1() {
      return (Pair)readWriteProperty6.getValue(this, $$delegatedProperties[5]);
   }

   public final void updatePair3(Pair pair) {
      readWriteProperty6.setValue(this, $$delegatedProperties[5], pair);
   }

   @Nullable
   public final Pair getButton2() {
      return (Pair)readWriteProperty7.getValue(this, $$delegatedProperties[6]);
   }

   public final void updatePair4(Pair pair) {
      readWriteProperty7.setValue(this, $$delegatedProperties[6], pair);
   }

   @NotNull
   public final Activity getActivity() {
      List<ActivityButton> arrayList = new ArrayList();
      Pair button1 = this.getButton1();
      if (button1 != null) {
         Pair pair = button1;
         arrayList.add(new ActivityButton((String)pair.getFirst(), (String)pair.getSecond()));
      }

      button1 = this.getButton2();
      if (button1 != null) {
         Pair pair2 = button1;
         arrayList.add(new ActivityButton((String)pair2.getFirst(), (String)pair2.getSecond()));
      }

      String details = this.getDetails();
      String state = this.getState();
      ActivityAsset activityAsset;
      if (this.getLargeImage() == null && this.getSmallImage() == null) {
         activityAsset = null;
      } else {
         Pair largeImage = this.getLargeImage();
         String first = largeImage != null ? (String)largeImage.getFirst() : null;
         Pair largeImage2 = this.getLargeImage();
         String second = largeImage2 != null ? (String)largeImage2.getSecond() : null;
         Pair smallImage = this.getSmallImage();
         String first2 = smallImage != null ? (String)smallImage.getFirst() : null;
         Pair smallImage2 = this.getSmallImage();
         activityAsset = new ActivityAsset(first, second, first2, smallImage2 != null ? (String)smallImage2.getSecond() : null);
      }

      ActivityAsset activityAsset2 = activityAsset;
      ActivityTimestamp activityTimestamp = new ActivityTimestamp(this.getTime(), null, 2, null);
      return new Activity(details, state, activityTimestamp, activityAsset2, arrayList);
   }

   private final ReadWriteProperty computeReadWriteProperty(boolean flag, Object object) {
      Delegates instance = Delegates.INSTANCE;
      return (ReadWriteProperty)(new DiscordPresence.ChangeObserver(object, flag));
   }

   public static ReadWriteProperty defaultImpl(DiscordPresence discordPresence, boolean flag, Object object, int value, Object object2) {
      if ((value & 1) != 0) {
         flag = false;
      }

      if ((value & 2) != 0) {
         object = null;
      }

      return discordPresence.computeReadWriteProperty(flag, object);
   }

   public final void update() {
      IpcDrafts.INSTANCE.setValue(0);
      IpcDrafts.INSTANCE.setFlag(true);
   }

   public final void update2() {
      IpcDrafts.INSTANCE.setFlag(false);
   }

   static {
   }

   public static final class ChangeObserver extends ObservableProperty {
      public final boolean flag;

      public ChangeObserver(Object object, boolean flag) {
         super(object);
         this.flag = flag;
      }

      public void afterChange(KProperty property, Object object, Object object2) {
         if (!Intrinsics.areEqual(object, object2)) {
            if (this.flag) {
               String text = "null cannot be cast to non-null type kotlin.Long";
               DiscordPresence.INSTANCE.applyEncodeToJsonElement((Long)object2);
            } else {
               DiscordPresence.INSTANCE.updateActivity(DiscordPresence.INSTANCE.getActivity());
            }
         }
      }
   }
}