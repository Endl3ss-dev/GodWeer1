// deobf by pusia
package su.catlean.utils;

import java.util.Map;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import org.jetbrains.annotations.NotNull;

public final class KeyNames {
   @NotNull
   public static final KeyNames INSTANCE = new KeyNames();
   @NotNull
   private static final Map map;

   private KeyNames() {
   }

   @NotNull
   public final String computeText(int value) {
      String value2 = (String)map.get(value);
      if (value2 == null) {
         value2 = "None";
      }

      return value2;
   }

   static {
      Pair[] pairs = new Pair[]{
         TuplesKt.to(0, "LMB"),
         TuplesKt.to(1, "RMB"),
         TuplesKt.to(2, "MMB"),
         TuplesKt.to(3, "Mouse 4"),
         TuplesKt.to(4, "Mouse 5"),
         TuplesKt.to(32, "Space"),
         TuplesKt.to(39, "Apostrophe"),
         TuplesKt.to(44, "Comma"),
         TuplesKt.to(45, "Minus"),
         TuplesKt.to(46, "Period"),
         TuplesKt.to(47, "Slash"),
         TuplesKt.to(48, "0"),
         TuplesKt.to(49, "1"),
         TuplesKt.to(50, "2"),
         TuplesKt.to(51, "3"),
         TuplesKt.to(52, "4"),
         TuplesKt.to(53, "5"),
         TuplesKt.to(54, "6"),
         TuplesKt.to(55, "7"),
         TuplesKt.to(56, "8"),
         TuplesKt.to(57, "9"),
         TuplesKt.to(59, "SemiColon"),
         TuplesKt.to(61, "Equal"),
         TuplesKt.to(65, "A"),
         TuplesKt.to(66, "B"),
         TuplesKt.to(67, "C"),
         TuplesKt.to(68, "D"),
         TuplesKt.to(69, "E"),
         TuplesKt.to(70, "F"),
         TuplesKt.to(71, "G"),
         TuplesKt.to(72, "H"),
         TuplesKt.to(73, "I"),
         TuplesKt.to(74, "J"),
         TuplesKt.to(75, "K"),
         TuplesKt.to(76, "L"),
         TuplesKt.to(77, "M"),
         TuplesKt.to(78, "N"),
         TuplesKt.to(79, "O"),
         TuplesKt.to(80, "P"),
         TuplesKt.to(81, "Q"),
         TuplesKt.to(82, "R"),
         TuplesKt.to(83, "S"),
         TuplesKt.to(84, "T"),
         TuplesKt.to(85, "U"),
         TuplesKt.to(86, "V"),
         TuplesKt.to(87, "W"),
         TuplesKt.to(88, "X"),
         TuplesKt.to(89, "Y"),
         TuplesKt.to(90, "Z"),
         TuplesKt.to(91, "LeftBracket"),
         TuplesKt.to(92, "BackSlash"),
         TuplesKt.to(93, "RightBracket"),
         TuplesKt.to(96, "Grave"),
         TuplesKt.to(256, "Escape"),
         TuplesKt.to(257, "Enter"),
         TuplesKt.to(258, "Tab"),
         TuplesKt.to(259, "BackSpace"),
         TuplesKt.to(260, "Insert"),
         TuplesKt.to(261, "Delete"),
         TuplesKt.to(262, "Right"),
         TuplesKt.to(263, "Left"),
         TuplesKt.to(264, "Down"),
         TuplesKt.to(265, "Up"),
         TuplesKt.to(266, "PageUp"),
         TuplesKt.to(267, "PageDown"),
         TuplesKt.to(268, "Home"),
         TuplesKt.to(269, "End"),
         TuplesKt.to(280, "CapsLock"),
         TuplesKt.to(281, "ScrollLock"),
         TuplesKt.to(282, "NumLock"),
         TuplesKt.to(283, "PrintScreen"),
         TuplesKt.to(284, "Pause"),
         TuplesKt.to(290, "F1"),
         TuplesKt.to(291, "F2"),
         TuplesKt.to(292, "F3"),
         TuplesKt.to(293, "F4"),
         TuplesKt.to(294, "F5"),
         TuplesKt.to(295, "F6"),
         TuplesKt.to(296, "F7"),
         TuplesKt.to(297, "F8"),
         TuplesKt.to(298, "F9"),
         TuplesKt.to(299, "F10"),
         TuplesKt.to(300, "F11"),
         TuplesKt.to(301, "F12"),
         TuplesKt.to(302, "F13"),
         TuplesKt.to(303, "F14"),
         TuplesKt.to(304, "F15"),
         TuplesKt.to(305, "F16"),
         TuplesKt.to(306, "F17"),
         TuplesKt.to(307, "F18"),
         TuplesKt.to(308, "F19"),
         TuplesKt.to(309, "F20"),
         TuplesKt.to(310, "F21"),
         TuplesKt.to(311, "F22"),
         TuplesKt.to(312, "F23"),
         TuplesKt.to(313, "F24"),
         TuplesKt.to(314, "F25"),
         TuplesKt.to(320, "NUM 0"),
         TuplesKt.to(321, "NUM 1"),
         TuplesKt.to(322, "NUM 2"),
         TuplesKt.to(323, "NUM 3"),
         TuplesKt.to(324, "NUM 4"),
         TuplesKt.to(325, "NUM 5"),
         TuplesKt.to(326, "NUM 6"),
         TuplesKt.to(327, "NUM 7"),
         TuplesKt.to(328, "NUM 8"),
         TuplesKt.to(329, "NUM 9"),
         TuplesKt.to(330, "Deci"),
         TuplesKt.to(331, "Div"),
         TuplesKt.to(332, "Mul"),
         TuplesKt.to(333, "Sub"),
         TuplesKt.to(334, "Add"),
         TuplesKt.to(335, "Enter"),
         TuplesKt.to(336, "Equal"),
         TuplesKt.to(340, "Shift"),
         TuplesKt.to(341, "LCtrl"),
         TuplesKt.to(342, "LAlt"),
         TuplesKt.to(343, "LSuper"),
         TuplesKt.to(344, "RShift"),
         TuplesKt.to(345, "RCtrl"),
         TuplesKt.to(346, "RAlt"),
         TuplesKt.to(347, "RSuper"),
         TuplesKt.to(348, "Menu")
      };
      map = MapsKt.mapOf(pairs);
   }
}