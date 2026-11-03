package net.skycore.client.module.render;

import java.util.List;
import java.util.Set;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.MultiSelectSetting;
import net.skycore.client.setting.NumberSetting;

public final class ClickGUIModule extends ClientModule {
   public static final String EFFECT_GRAYSCALE = "Серый фон";
   public static final String EFFECT_DIM = "Затемнять фон";
   public static final String EFFECT_BLUR = "Размывать фон";
   public static final String EFFECT_SHADER = "Шейдер";
   public static final String EFFECT_PARTICLES = "Частицы";
   public static final String EFFECT_SCANLINES = "Скан линии";
   public static final String EFFECT_GLOW = "Свечение";
   public static final String EFFECT_DOTS = "Точки";

   private Runnable openAction;
   private final ModeSetting menuStyle = this.add(
      new ModeSetting("GUI Style", "Decide", "Decide", "Workspace", "Swerve", "Skycore")
   );
   private final MultiSelectSetting effects = this.add(
      new MultiSelectSetting(
         "Эффекты",
         List.of(
            EFFECT_GRAYSCALE,
            EFFECT_DIM,
            EFFECT_BLUR,
            EFFECT_SHADER,
            EFFECT_PARTICLES,
            EFFECT_SCANLINES,
            EFFECT_GLOW,
            EFFECT_DOTS
         ),
         Set.of(
            EFFECT_DIM,
            EFFECT_BLUR,
            EFFECT_PARTICLES,
            EFFECT_SCANLINES,
            EFFECT_GLOW,
            EFFECT_DOTS
         )
      )
   );
   private final ModeSetting closeAnimation = this.add(
      new ModeSetting("Анимация закрытия", "Default", "Default", "3D", "Shatter")
   );
   private final NumberSetting decideSize = this.add(
      new NumberSetting("Размер", 1.0, 0.5, 1.5, 0.1)
   );
   private final BooleanSetting runWithGui = this.add(
      new BooleanSetting("Бегать с открытым GUI", false)
   );
   private final NumberSetting renderFactor = this.add(
      new NumberSetting("Render factor", "ClickGUI render scale", 0.45, 0.35, 0.75, 0.01, "x")
   );
   private final ModeSetting style = this.add(
      new ModeSetting("Style", "Transparent", "Solid", "Transparent")
   );

   public ClickGUIModule() {
      super("ClickGUI", "Opens the Skycore interface", ModuleCategory.RENDER);
      this.setKeyBind(344);
      this.effects.visibleWhen(() -> this.menuStyle.is("Decide"));
      this.closeAnimation.visibleWhen(() -> this.menuStyle.is("Decide"));
      this.decideSize.visibleWhen(() -> this.menuStyle.is("Decide"));
      this.runWithGui.visibleWhen(() -> this.menuStyle.is("Decide"));
      this.style.visibleWhen(() -> !this.menuStyle.is("Decide"));
   }

   public ModeSetting style() {
      return this.style;
   }

   public ModeSetting menuStyle() {
      return this.menuStyle;
   }

   public NumberSetting renderFactor() {
      return this.renderFactor;
   }

   public MultiSelectSetting effects() {
      return this.effects;
   }

   public boolean effectEnabled(String name) {
      return this.effects.isSelected(name);
   }

   public ModeSetting closeAnimation() {
      return this.closeAnimation;
   }

   public NumberSetting decideSize() {
      return this.decideSize;
   }

   public BooleanSetting runWithGui() {
      return this.runWithGui;
   }

   @Override
   protected void onEnable() {
      if (this.openAction != null) {
         this.openAction.run();
      }

      this.setEnabled(false);
   }

   public void setOpenAction(Runnable openAction2) {
      this.openAction = openAction2;
   }
}
