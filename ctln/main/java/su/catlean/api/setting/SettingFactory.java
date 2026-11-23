// deobf by pusia
package su.catlean.api.setting;

import java.awt.Color;
import kotlin.jvm.functions.Function0;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.ScreenPosition;
import su.catlean.api.setting.settings.ActionSetting;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BlocksSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EntitiesSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.settings.ItemsSetting;
import su.catlean.api.setting.settings.NotationSetting;
import su.catlean.api.setting.settings.ScreenPositionSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.api.setting.values.ActionValue;
import su.catlean.api.setting.values.Bind;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.api.setting.values.EntitiesValue;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.api.setting.values.ItemsValue;
import su.catlean.api.setting.values.NotationValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;

public final class SettingFactory {
   @NotNull
   public static final BooleanSetting computeBooleanSetting(SettingHolder self, String id, boolean flag, GroupValue groupValue, Function0 visible) {
      BooleanSetting booleanSetting = new BooleanSetting(id, flag, groupValue, visible);
      self.getItems5().add(booleanSetting);
      return booleanSetting;
   }

   public static BooleanSetting defaultImpl(
      SettingHolder settingHolder, String text, boolean flag, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeBooleanSetting(settingHolder, text, flag, groupValue, callback);
   }

   @NotNull
   public static final IntSetting computeIntSetting(SettingHolder self, String id, int value, IntRange range, GroupValue groupValue, Function0 visible) {
      IntSetting intSetting = new IntSetting(id, value, range, groupValue, visible);
      self.getItems5().add(intSetting);
      return intSetting;
   }

   public static IntSetting defaultImpl(
      SettingHolder settingHolder, String text, int value, IntRange intRange, GroupValue groupValue, Function0 callback, int value2, Object object
   ) {
      if ((value2 & 8) != 0) {
         groupValue = null;
      }

      if ((value2 & 16) != 0) {
         callback = () -> true;
      }

      return computeIntSetting(settingHolder, text, value, intRange, groupValue, callback);
   }

   @NotNull
   public static final FloatSetting computeFloatSetting(
      SettingHolder self, String id, float amount, ClosedFloatingPointRange range, GroupValue groupValue, Function0 visible
   ) {
      FloatSetting floatSetting = new FloatSetting(id, amount, range, groupValue, visible);
      self.getItems5().add(floatSetting);
      return floatSetting;
   }

   public static FloatSetting defaultImpl(
      SettingHolder settingHolder,
      String text,
      float amount,
      ClosedFloatingPointRange closedFloatingPointRange,
      GroupValue groupValue,
      Function0 callback,
      int value,
      Object object
   ) {
      if ((value & 8) != 0) {
         groupValue = null;
      }

      if ((value & 16) != 0) {
         callback = () -> true;
      }

      return computeFloatSetting(settingHolder, text, amount, closedFloatingPointRange, groupValue, callback);
   }

   @NotNull
   public static final StringSetting computeStringSetting(SettingHolder self, String id, String defaultValue, GroupValue groupValue, Function0 visible) {
      StringSetting stringSetting = new StringSetting(id, defaultValue, groupValue, visible);
      self.getItems5().add(stringSetting);
      return stringSetting;
   }

   public static StringSetting defaultImpl(
      SettingHolder settingHolder, String text, String text2, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeStringSetting(settingHolder, text, text2, groupValue, callback);
   }

   @NotNull
   public static final BindSetting computeBindSetting(SettingHolder self, String id, Bind defaultValue, GroupValue groupValue, Function0 visible) {
      BindSetting bindSetting = new BindSetting(id, defaultValue, visible);
      bindSetting.setGroupValue(groupValue);
      self.getItems5().add(bindSetting);
      return bindSetting;
   }

   public static BindSetting defaultImpl(
      SettingHolder settingHolder, String text, Bind bind, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeBindSetting(settingHolder, text, bind, groupValue, callback);
   }

   @NotNull
   public static final EnumSetting computeEnumSetting(SettingHolder self, String id, Enum defaultValue, GroupValue groupValue, Function0 visible) {
      EnumSetting enumSetting = new EnumSetting(id, defaultValue, groupValue, visible);
      self.getItems5().add(enumSetting);
      return enumSetting;
   }

   public static EnumSetting defaultImpl(
      SettingHolder settingHolder, String text, Enum enumValue, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeEnumSetting(settingHolder, text, enumValue, groupValue, callback);
   }

   @NotNull
   public static final ScreenPositionSetting computeScreenPositionSetting(
      SettingHolder self, String id, ScreenPosition defaultValue, GroupValue groupValue, Function0 visible
   ) {
      ScreenPositionSetting screenPositionSetting = new ScreenPositionSetting(id, defaultValue, groupValue, visible);
      self.getItems5().add(screenPositionSetting);
      return screenPositionSetting;
   }

   public static ScreenPositionSetting defaultImpl(
      SettingHolder settingHolder, String text, ScreenPosition screenPosition, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeScreenPositionSetting(settingHolder, text, screenPosition, groupValue, callback);
   }

   @NotNull
   public static final ColorSetting computeColorSetting(SettingHolder self, String id, Color defaultValue, GroupValue groupValue, Function0 visible) {
      ColorSetting colorSetting = new ColorSetting(id, defaultValue, groupValue, visible);
      self.getItems5().add(colorSetting);
      return colorSetting;
   }

   public static ColorSetting defaultImpl(
      SettingHolder settingHolder, String text, Color color, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeColorSetting(settingHolder, text, color, groupValue, callback);
   }

   @NotNull
   public static final BlocksSetting computeBlocksSetting(SettingHolder self, String id, BlocksValue defaultValue, GroupValue groupValue, Function0 visible) {
      BlocksSetting blocksSetting = new BlocksSetting(id, defaultValue, groupValue, visible);
      self.getItems5().add(blocksSetting);
      return blocksSetting;
   }

   public static BlocksSetting defaultImpl(
      SettingHolder settingHolder, String text, BlocksValue blocksValue, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeBlocksSetting(settingHolder, text, blocksValue, groupValue, callback);
   }

   @NotNull
   public static final EntitiesSetting computeEntitiesSetting(
      SettingHolder self, String id, EntitiesValue defaultValue, GroupValue groupValue, Function0 visible
   ) {
      EntitiesSetting entitiesSetting = new EntitiesSetting(id, defaultValue, groupValue, visible);
      self.getItems5().add(entitiesSetting);
      return entitiesSetting;
   }

   public static EntitiesSetting defaultImpl(
      SettingHolder settingHolder, String text, EntitiesValue entitiesValue, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeEntitiesSetting(settingHolder, text, entitiesValue, groupValue, callback);
   }

   @NotNull
   public static final ItemsSetting computeItemsSetting(SettingHolder self, String id, ItemsValue defaultValue, GroupValue groupValue, Function0 visible) {
      ItemsSetting itemsSetting = new ItemsSetting(id, defaultValue, groupValue, visible);
      self.getItems5().add(itemsSetting);
      return itemsSetting;
   }

   public static ItemsSetting defaultImpl(
      SettingHolder settingHolder, String text, ItemsValue itemsValue, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeItemsSetting(settingHolder, text, itemsValue, groupValue, callback);
   }

   @NotNull
   public static final ActionSetting computeActionSetting(SettingHolder self, String id, Function0 defaultValue, GroupValue groupValue, Function0 visible) {
      ActionSetting actionSetting = new ActionSetting(id, new ActionValue(defaultValue), groupValue, visible);
      self.getItems5().add(actionSetting);
      return actionSetting;
   }

   public static ActionSetting defaultImpl(
      SettingHolder settingHolder, String text, Function0 callback, GroupValue groupValue, Function0 callback2, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback2 = () -> true;
      }

      return computeActionSetting(settingHolder, text, callback, groupValue, callback2);
   }

   @NotNull
   public static final NotationSetting computeNotationSetting(
      SettingHolder self, String id, NotationValue defaultValue, GroupValue groupValue, Function0 visible
   ) {
      NotationSetting notationSetting = new NotationSetting(id, defaultValue, groupValue, visible);
      self.getItems5().add(notationSetting);
      return notationSetting;
   }

   public static NotationSetting defaultImpl(
      SettingHolder settingHolder, String text, NotationValue notationValue, GroupValue groupValue, Function0 callback, int value, Object object
   ) {
      if ((value & 4) != 0) {
         groupValue = null;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeNotationSetting(settingHolder, text, notationValue, groupValue, callback);
   }

   @NotNull
   public static final GroupSetting computeGroupSetting(SettingHolder self, String id, PlaceModule.Pause type, Function0 visible) {
      GroupValue groupValue = new GroupValue(false, type);
      GroupSetting groupSetting = new GroupSetting(id, groupValue, visible);
      self.getItems5().add(groupSetting);
      return groupSetting;
   }

   public static GroupSetting defaultImpl(SettingHolder settingHolder, String text, PlaceModule.Pause pause, Function0 callback, int value, Object object) {
      if ((value & 2) != 0) {
         pause = PlaceModule.Pause.DEFAULT;
      }

      if ((value & 4) != 0) {
         callback = () -> true;
      }

      return computeGroupSetting(settingHolder, text, pause, callback);
   }

   @NotNull
   public static final GroupSetting computeGroupSetting(Module parent, String id, PlaceModule.Pause type, Function0 visible) {
      GroupValue groupValue = new GroupValue(false, type);
      GroupSetting groupSetting = new GroupSetting(id, groupValue, visible);
      parent.getItems5().add(groupSetting);
      return groupSetting;
   }

   public static GroupSetting defaultImpl(Module module, String text, PlaceModule.Pause pause, Function0 callback, int value, Object object) {
      if ((value & 4) != 0) {
         pause = PlaceModule.Pause.DEFAULT;
      }

      if ((value & 8) != 0) {
         callback = () -> true;
      }

      return computeGroupSetting(module, text, pause, callback);
   }

   @NotNull
   public static final FloatSetting computeFloatSetting(
      Module parent, String id, float amount, ClosedFloatingPointRange range, GroupValue groupValue, Function0 visible
   ) {
      FloatSetting floatSetting = new FloatSetting(id, amount, range, groupValue, visible);
      parent.getItems5().add(floatSetting);
      return floatSetting;
   }

   @NotNull
   public static final IntSetting computeIntSetting(Module parent, String id, int value, IntRange range, GroupValue groupValue, Function0 visible) {
      IntSetting intSetting = new IntSetting(id, value, range, groupValue, visible);
      parent.getItems5().add(intSetting);
      return intSetting;
   }

   @NotNull
   public static final BooleanSetting computeBooleanSetting(Module parent, String id, boolean flag, GroupValue groupValue, Function0 visible) {
      BooleanSetting booleanSetting = new BooleanSetting(id, flag, groupValue, visible);
      parent.getItems5().add(booleanSetting);
      return booleanSetting;
   }

   @NotNull
   public static final EnumSetting computeEnumSetting(Module parent, String id, Enum defaultValue, GroupValue groupValue, Function0 visible) {
      EnumSetting enumSetting = new EnumSetting(id, defaultValue, groupValue, visible);
      parent.getItems5().add(enumSetting);
      return enumSetting;
   }

   private static final boolean isFlag() {
      return true;
   }

   private static final boolean isFlag2() {
      return true;
   }

   private static final boolean isFlag3() {
      return true;
   }

   private static final boolean isFlag4() {
      return true;
   }
}