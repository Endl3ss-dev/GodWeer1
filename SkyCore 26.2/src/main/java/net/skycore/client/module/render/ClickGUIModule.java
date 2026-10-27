package net.skycore.client.module.render;

import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.ModeSetting;

public final class ClickGUIModule extends ClientModule {
   private Runnable openAction;
   private final NumberSetting renderFactor = this.add(new NumberSetting("Render factor", "ClickGUI render scale", 0.45, 0.35, 0.75, 0.01, "x"));
   private final ModeSetting style = this.add(new ModeSetting("Style", "Transparent", "Solid", "Transparent"));
   private final ModeSetting menuStyle = this.add(new ModeSetting("Interface", "Workspace", "Workspace", "Swerve", "Skycore"));

   public ModeSetting style() {
      return this.style;
   }

   /**
    * Какое меню открывать по бинду ClickGUI: рабочее пространство Workspace
    * ({@code Workspace}, основное), перенесённое из Swerve ({@code Swerve}) или
    * исходное меню Skycore ({@code Skycore}).
    *
    * <p>Настройка называется {@code Interface}, а не {@code Menu style}, намеренно.
    * Конфиг хранит значения по имени настройки, поэтому старый ключ {@code Menu style}
    * со значениями {@code Swerve}/{@code Skycore} продолжал бы перебивать новый вариант
    * по умолчанию: пользователь оставался в старом экране, где клик по режиму
    * проваливается в модуль позади, и переключиться на Workspace было нельзя.
    * С новым именем старое значение просто не читается, а выбор делается один раз —
    * уже внутри Workspace, где настройка доступна в карточке ClickGUI.
    */
   public ModeSetting menuStyle() {
      return this.menuStyle;
   }

   public NumberSetting renderFactor() {
      return this.renderFactor;
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

   public ClickGUIModule() {
      super("ClickGUI", "Opens the Skycore interface", ModuleCategory.RENDER);
      this.setKeyBind(344);
   }
}
