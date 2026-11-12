package net.skycore.client.hud;

import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.skycore.client.SkycoreClient;
import net.skycore.client.config.ConfigManager;
import net.skycore.client.gui.decide.DecideCloseAnimation;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleManager;
import net.skycore.client.module.combat.SpearDamageIndicatorModule;
import net.skycore.client.module.render.ClickGUIModule;
import net.skycore.client.module.render.InterfaceModule;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.CurrentGuiGraphicsExtractorHelper;

public final class HudRenderer {
   /** Идентификаторы Draggable, которым нужны отдельные ключи в InterfaceModule.elements(). */
   private static final String ELEMENT_TARGET_HUD = "target_hud";
   private static final String ELEMENT_ARMOR_HUD = "armor_hud";
   private static final String ELEMENT_NOTIFICATIONS = "notifications";

   private final ConfigManager config;
   private final Set<String> appliedPositions;
   private final ModuleManager modules;
   private int lastScreenHeight;
   private final DraggableManager draggables = new DraggableManager();
   private long lastFrameAt = System.nanoTime();
   private final InterfaceModule interfaceModule;
   private int lastScreenWidth = -1;
   private final ClickGUIModule clickGuiModule;

   public HudRenderer(ModuleManager interfaceModule2, ConfigManager config2) {
      this.lastScreenHeight = -1;
      this.appliedPositions = new HashSet<>();
      this.interfaceModule = interfaceModule2.get(InterfaceModule.class).orElseThrow();
      this.clickGuiModule = interfaceModule2.get(ClickGUIModule.class).orElseThrow();
      this.modules = interfaceModule2;
      this.config = config2;
      this.draggables.register(this.interfaceModule.watermark().draggable());
      this.draggables.register(this.interfaceModule.information().draggable());
      this.draggables.register(this.interfaceModule.hotkeys().draggable());
      this.draggables.register(this.interfaceModule.binds().draggable());
      this.draggables.register(this.interfaceModule.hotbar().draggable());
      this.draggables.register(this.interfaceModule.potions().draggable());
      this.draggables.register(this.interfaceModule.cooldowns().draggable());
      this.draggables.register(this.interfaceModule.armorHud().draggable());
      this.draggables.register(this.interfaceModule.targetHud().draggable());
      this.draggables.register(this.interfaceModule.notifications().draggable());
      // Порядок тот же, что в HUD-проходе: редактор показывает элементы этим списком.
      this.editorElements.add(this.interfaceModule.watermark().draggable());
      this.editorElements.add(this.interfaceModule.information().draggable());
      this.editorElements.add(this.interfaceModule.hotkeys().draggable());
      this.editorElements.add(this.interfaceModule.binds().draggable());
      this.editorElements.add(this.interfaceModule.hotbar().draggable());
      this.editorElements.add(this.interfaceModule.potions().draggable());
      this.editorElements.add(this.interfaceModule.cooldowns().draggable());
      this.editorElements.add(this.interfaceModule.armorHud().draggable());
      this.editorElements.add(this.interfaceModule.targetHud().draggable());
      this.editorElements.add(this.interfaceModule.notifications().draggable());
      ClientModule.addToggleListener((clientModule, condition) -> {
         this.interfaceModule.notifications().onModuleToggle(clientModule, condition);
      });
   }

   public void applySavedPositions() {
      Minecraft instance = Minecraft.getInstance();
      if (instance.getWindow() != null) {
         int guiScaledWidth = instance.getWindow().getGuiScaledWidth();
         int guiScaledHeight = instance.getWindow().getGuiScaledHeight();

         for (Draggable draggable : this.draggables.all()) {
            if (!this.appliedPositions.contains(draggable.id()) && !(draggable.width() <= 0.0F) && !(draggable.height() <= 0.0F)) {
               ConfigManager.DraggablePosition draggablePosition = this.config.getDraggablePosition(draggable.id());
               float x = draggablePosition == null ? draggable.defaultNormalizedX() : draggablePosition.x();
               float y = draggablePosition == null ? draggable.defaultNormalizedY() : draggablePosition.y();
               if (draggable.axisLock() == Draggable.AxisLock.VERTICAL) {
                  x = 0.5F;
               } else if (draggable.axisLock() == Draggable.AxisLock.HORIZONTAL) {
                  y = 0.5F;
               }

               float computedValue = Math.max(0.0F, guiScaledWidth - draggable.width());
               float computedValue2 = Math.max(0.0F, guiScaledHeight - draggable.height());
               draggable.setPosition(clamp(x, 0.0F, 1.0F) * computedValue, clamp(y, 0.0F, 1.0F) * computedValue2);
               this.appliedPositions.add(draggable.id());
            }
         }

         this.lastScreenWidth = guiScaledWidth;
         this.lastScreenHeight = guiScaledHeight;
      }
   }

   private float interfaceScale(Minecraft minecraft) {
      Window window = minecraft.getWindow();
      float guiScale = Math.max(0.6666667F, window.getGuiScale() / 3.0F);
      Double value = this.clickGuiModule.renderFactor().get();
      float value2 = value.floatValue();
      return guiScale * value2 / 0.5F;
   }

   public void render(GuiRenderState guiRenderState, DeltaTracker deltaTracker) {
      Minecraft instance = Minecraft.getInstance();
      if (instance.level != null && instance.player != null) {
         int guiScaledWidth = instance.getWindow().getGuiScaledWidth();
         int guiScaledHeight = instance.getWindow().getGuiScaledHeight();
         this.reprojectIfNeeded(guiScaledWidth, guiScaledHeight);
         // Анимация закрытия Decide-меню живёт поверх HUD: снимок кадра уже снят,
         // и шейпы рисуются независимо от того, включены ли элементы интерфейса.
         if (DecideCloseAnimation.isActive()) {
            GuiGraphicsExtractor closeExtractor = new GuiGraphicsExtractor(instance, guiRenderState,
                    (int) Math.round(this.mouseX(instance)), (int) Math.round(this.mouseY(instance)));
            CurrentGuiGraphicsExtractorHelper.enter2D(closeExtractor, deltaTracker);
            BeginFrameHelper.beginFrame();
            try {
               DecideCloseAnimation.renderHud(guiScaledWidth, guiScaledHeight);
               BeginFrameHelper.flush();
            } catch (RuntimeException failure) {
               // Эффект закрытия не должен утянуть за собой весь HUD-проход.
               SkycoreClient.LOGGER.warn("Skycore Decide GUI: close animation frame failed", failure);
               DecideCloseAnimation.cancel();
            } finally {
               CurrentGuiGraphicsExtractorHelper.exit2D();
            }
         }
         long nowNanos = System.nanoTime();
         float computedValue = clamp((float)(nowNanos - this.lastFrameAt) / 1.0E9F, 0.0F, 0.05F);
         this.lastFrameAt = nowNanos;
         if (this.draggables.active() != null) {
            // Порт HudEditorScreen: кроме чата перетаскивание живёт и в редакторе HUD.
            // Раньше любой экран, кроме чата, немедленно завершал drag — редактор бы
            // не смог двигать элементы. Позиция сохраняется при отпускании кнопки.
            if (instance.gui.screen() instanceof ChatScreen
                    || instance.gui.screen() instanceof net.skycore.client.gui.hud.HudEditorScreen) {
               this.draggables.dragTo(this.mouseX(instance), this.mouseY(instance), guiScaledWidth, guiScaledHeight);
            } else {
               Draggable draggable = this.draggables.endDrag();
               if (draggable != null) {
                  this.savePosition(draggable, guiScaledWidth, guiScaledHeight);
               }
            }
         }

         this.draggables.updateMotion(computedValue, guiScaledWidth, guiScaledHeight);
         boolean watermarkEnabled = this.interfaceModule.isWatermarkEnabled();
         boolean informationEnabled = this.interfaceModule.isInformationEnabled();
         boolean hotkeysEnabled = this.interfaceModule.isHotkeysEnabled();
         boolean bindsEnabled = this.interfaceModule.isBindsEnabled();
         boolean hotbarEnabled = this.interfaceModule.isHotbarEnabled();
         boolean potionsEnabled = this.interfaceModule.isPotionsEnabled();
         boolean cooldownsEnabled = this.interfaceModule.isCooldownsEnabled();
         boolean armorHudEnabled = this.interfaceModule.isArmorHudEnabled();
         boolean targetHudEnabled = this.interfaceModule.isTargetHudEnabled();
         boolean notificationsEnabled = this.interfaceModule.isNotificationsEnabled();
         boolean spearDamageEnabled = SpearDamageIndicatorModule.INSTANCE.shouldRender(instance, computedValue);
         if (notificationsEnabled) {
            this.interfaceModule.notifications().tick(instance.player);
         } else {
            this.interfaceModule.notifications().tick(null);
         }

         if (watermarkEnabled || informationEnabled || hotkeysEnabled || bindsEnabled || hotbarEnabled || potionsEnabled || cooldownsEnabled || armorHudEnabled || targetHudEnabled || notificationsEnabled || spearDamageEnabled) {
            boolean condition = instance.gui.screen() instanceof ChatScreen;
            GuiGraphicsExtractor guiGraphicsExtractor = new GuiGraphicsExtractor(instance, guiRenderState, (int)Math.round(this.mouseX(instance)), (int)Math.round(this.mouseY(instance)));
            CurrentGuiGraphicsExtractorHelper.enter2D(guiGraphicsExtractor, deltaTracker);
            BeginFrameHelper.beginFrame();

            try {
               BeginFrameHelper.setHudGlowColor(0);
               int color = this.interfaceModule.glowEnabled() ? SkycoreClient.themes().accent() : 0;
               BeginFrameHelper.setHudIconGlowColor(color);
               float value = this.interfaceScale(instance);
               if (watermarkEnabled) {
                  this.interfaceModule.watermark().render(value, 1.0F);
               }

               if (informationEnabled) {
                  this.interfaceModule.information().render(value, 1.0F, condition);
               }

               if (hotkeysEnabled) {
                  this.interfaceModule.hotkeys().render(value, 1.0F, this.modules.getModules(), condition);
               }

               if (bindsEnabled) {
                  this.interfaceModule.binds().render(value, 1.0F, this.modules.getModules(), condition);
               }

               if (hotbarEnabled) {
                  BeginFrameHelper.setHudIconGlowColor(0);
                  this.interfaceModule.hotbar().render(value, 1.0F);
                  BeginFrameHelper.setHudIconGlowColor(color);
               }

               if (potionsEnabled) {
                  this.interfaceModule.potions().render(value, 1.0F, condition);
               }

               if (cooldownsEnabled) {
                  this.interfaceModule.cooldowns().render(value, 1.0F, condition);
               }

               if (armorHudEnabled) {
                  this.interfaceModule.armorHud().render(value, 1.0F, condition);
               }

               if (targetHudEnabled) {
                  this.interfaceModule.targetHud().render(value, condition);
               }

               if (notificationsEnabled) {
                  this.interfaceModule.notifications().render(value, condition, guiScaledWidth);
               }

               if (spearDamageEnabled) {
                  BeginFrameHelper.setHudIconGlowColor(0);
                  SpearDamageIndicatorModule.INSTANCE.renderHud(guiScaledWidth, guiScaledHeight);
                  BeginFrameHelper.setHudIconGlowColor(color);
               }

               BeginFrameHelper.flush();
               this.applySavedPositions();
            } finally {
               BeginFrameHelper.setHudGlowColor(0);
               BeginFrameHelper.setHudIconGlowColor(0);
               CurrentGuiGraphicsExtractorHelper.exit2D();
            }
         }
      }
   }

   private List<Draggable> activeDraggables() {
      ArrayList elements = new ArrayList();
      if (this.interfaceModule.isWatermarkEnabled()) {
         elements.add(this.interfaceModule.watermark().draggable());
      }

      if (this.interfaceModule.isInformationEnabled()) {
         elements.add(this.interfaceModule.information().draggable());
      }

      if (this.interfaceModule.isHotkeysEnabled()) {
         elements.add(this.interfaceModule.hotkeys().draggable());
      }

      if (this.interfaceModule.isBindsEnabled()) {
         elements.add(this.interfaceModule.binds().draggable());
      }

      if (this.interfaceModule.isHotbarEnabled()) {
         elements.add(this.interfaceModule.hotbar().draggable());
      }

      if (this.interfaceModule.isPotionsEnabled()) {
         elements.add(this.interfaceModule.potions().draggable());
      }

      if (this.interfaceModule.isCooldownsEnabled()) {
         elements.add(this.interfaceModule.cooldowns().draggable());
      }

      if (this.interfaceModule.isArmorHudEnabled()) {
         elements.add(this.interfaceModule.armorHud().draggable());
      }

      if (this.interfaceModule.isTargetHudEnabled()) {
         elements.add(this.interfaceModule.targetHud().draggable());
      }

      if (this.interfaceModule.isNotificationsEnabled()) {
         elements.add(this.interfaceModule.notifications().draggable());
      }

      return elements;
   }

   private void reprojectIfNeeded(int lastScreenWidth2, int lastScreenHeight2) {
      if (this.lastScreenWidth < 0 || this.lastScreenHeight < 0) {
         this.lastScreenWidth = lastScreenWidth2;
         this.lastScreenHeight = lastScreenHeight2;
      } else if (lastScreenWidth2 != this.lastScreenWidth || lastScreenHeight2 != this.lastScreenHeight) {
         for (Draggable draggable : this.draggables.all()) {
            float computedValue = Math.max(1.0F, this.lastScreenWidth - draggable.width());
            float computedValue2 = Math.max(1.0F, this.lastScreenHeight - draggable.height());
            float computedValue3 = clamp(draggable.targetX() / computedValue, 0.0F, 1.0F);
            float computedValue4 = clamp(draggable.targetY() / computedValue2, 0.0F, 1.0F);
            float computedValue5 = Math.max(0.0F, lastScreenWidth2 - draggable.width());
            float computedValue6 = Math.max(0.0F, lastScreenHeight2 - draggable.height());
            draggable.setPosition(computedValue3 * computedValue5, computedValue4 * computedValue6);
         }

         this.lastScreenWidth = lastScreenWidth2;
         this.lastScreenHeight = lastScreenHeight2;
      }
   }

   public DraggableManager draggables() {
      return this.draggables;
   }

   // ---- API редактора HUD (gui/hud/HudEditorScreen)
   //
   // Редактор — порт HudEditorScreen из Arcanic, но работает не с модулями HUD,
   // а с теми же Draggable, которыми HUD уже живёт. Ничего не дублируется:
   // порядок элементов задаёт список, собранный в конструкторе, а видимость —
   // мультивыбор InterfaceModule.elements(), то есть ровно то поле, что и в меню.

   /** Элементы HUD в порядке регистрации (порядок нужен для стабильных индексов). */
   private final List<Draggable> editorElements = new ArrayList<>();

   /**
    * Рисует все элементы HUD для редактора.
    *
    * <p>В редакторе HUD-проход игры не выполняется: {@code Gui.extractRenderState}
    * (и вместе с ним {@link #render}) в 26.2 не вызывается, пока открыт
    * {@code Screen} — HUD продолжает жить только в памяти, а размеры элементов
    * ({@code Draggable.setBounds}) в обычном режиме задаются именно из элементов
    * во время их отрисовки. Поэтому редактор вызывает этот метод: элементы получают
    * реальные размеры, а рамки и хит-тесты попадают точно по своим местам.
    *
    * <p>Выключенные элементы тоже рисуются — иначе их нельзя было бы включить
    * в редакторе (в источнике для этого был флаг {@code preview}).
    */
   public void renderEditorPreview(float alpha) {
      Minecraft instance = Minecraft.getInstance();
      if (instance.player == null) {
         return;
      }
      float scale = this.interfaceScale(instance);
      boolean chat = false;
      int color = this.interfaceModule.glowEnabled() ? SkycoreClient.themes().accent() : 0;
      BeginFrameHelper.setHudGlowColor(0);
      BeginFrameHelper.setHudIconGlowColor(color);

      this.interfaceModule.watermark().render(scale, alpha);
      this.interfaceModule.information().render(scale, alpha, chat);
      this.interfaceModule.hotkeys().render(scale, alpha, this.modules.getModules(), chat);
      this.interfaceModule.binds().render(scale, alpha, this.modules.getModules(), chat);
      BeginFrameHelper.setHudIconGlowColor(0);
      this.interfaceModule.hotbar().render(scale, alpha);
      BeginFrameHelper.setHudIconGlowColor(color);
      this.interfaceModule.potions().render(scale, alpha, chat);
      this.interfaceModule.cooldowns().render(scale, alpha, chat);
      this.interfaceModule.armorHud().render(scale, alpha, chat);
      this.interfaceModule.targetHud().render(alpha, chat);
      this.interfaceModule.notifications().render(scale, chat, instance.getWindow().getGuiScaledWidth());

      BeginFrameHelper.flush();
      this.applySavedPositions();
      BeginFrameHelper.setHudGlowColor(0);
      BeginFrameHelper.setHudIconGlowColor(0);
   }

   /** Модель видимости/настроек HUD — редактор переключает через неё те же ключи, что и меню. */
   public InterfaceModule interfaceModule() {
      return this.interfaceModule;
   }

   /** Сколько элементов показывает редактор. */
   public int elementCount() {
      return this.editorElements.size();
   }

   public Draggable elementAt(int index) {
      if (index < 0 || index >= this.editorElements.size()) {
         return null;
      }
      return this.editorElements.get(index);
   }

   /**
    * Индекс элемента под точкой или {@code -1}.
    *
    * <p>Идём с конца: последний отрисованный элемент лежит сверху, и клик должен
    * попадать в него, как это делает {@code DraggableManager.beginDrag}.
    */
   public int elementAt(double x, double y) {
      for (int i = this.editorElements.size() - 1; i >= 0; i--) {
         Draggable draggable = this.editorElements.get(i);
         if (draggable.width() > 0.0F && draggable.height() > 0.0F && draggable.contains(x, y)) {
            return i;
         }
      }
      return -1;
   }

   /** Подпись элемента для редактора. */
   public String elementLabel(String id) {
      if (ELEMENT_NOTIFICATIONS.equals(id)) {
         return "Notifications";
      }
      if (ELEMENT_TARGET_HUD.equals(id)) {
         return "TargetHUD";
      }
      if (ELEMENT_ARMOR_HUD.equals(id)) {
         return "ArmorHUD";
      }
      return id == null ? "" : id.substring(0, 1).toUpperCase(Locale.ROOT) + id.substring(1);
   }

   /** Включён ли элемент в {@link InterfaceModule#elements()}. */
   public boolean isElementEnabled(String id) {
      String key = elementSettingKey(id);
      return key != null && this.interfaceModule.elements().isSelected(key);
   }

   /**
    * Переключает видимость элемента.
    *
    * <p>У {@code Notifications} и {@code TargetHUD} в {@code InterfaceModule} свои
    * условия ({@code isEnabled()} модуля), поэтому переключение идёт через тот же
    * мультивыбор — других полей у видимости в клиенте нет.
    */
   public void toggleElement(String id) {
      String key = elementSettingKey(id);
      if (key != null) {
         this.interfaceModule.elements().toggle(key);
         this.config.save();
      }
   }

   public void beginElementDrag(int index, double mouseX, double mouseY) {
      Draggable draggable = this.elementAt(index);
      if (draggable != null) {
         draggable.beginDrag(mouseX, mouseY);
         this.draggables.beginDrag(mouseX, mouseY, this.editorElements);
      }
   }

   public void dragElement(double mouseX, double mouseY, int width, int height) {
      this.draggables.dragTo(mouseX, mouseY, width, height);
   }

   /** Завершает перетаскивание и сохраняет нормализованную позицию. */
   public void endElementDrag(int width, int height) {
      Draggable draggable = this.draggables.endDrag();
      if (draggable != null) {
         this.savePosition(draggable, width, height);
      }
   }

   /** Соответствие идентификатора Draggable ключу в {@code InterfaceModule.elements()}. */
   private static String elementSettingKey(String id) {
      if (id == null) {
         return null;
      }
      if ("target_hud".equals(id)) {
         return "TargetHUD";
      }
      if ("armor_hud".equals(id)) {
         return "ArmorHUD";
      }
      String normalized = id.substring(0, 1).toUpperCase(Locale.ROOT) + id.substring(1);
      return normalized;
   }

   private double mouseX(Minecraft minecraft) {
      return minecraft.mouseHandler.getScaledXPos(minecraft.getWindow());
   }

   public void onMouseButton(MouseButtonInfo mouseButtonInfo, int value) {
      Minecraft instance = Minecraft.getInstance();
      if (instance.gui.screen() instanceof ChatScreen && mouseButtonInfo.button() == 0) {
         int guiScaledWidth = instance.getWindow().getGuiScaledWidth();
         int guiScaledHeight = instance.getWindow().getGuiScaledHeight();
         double value2 = this.mouseX(instance);
         double value3 = this.mouseY(instance);
         var draggables2 = this.activeDraggables();
         if (value == 1) {
            this.draggables.beginDrag(value2, value3, draggables2);
         } else if (value == 0) {
            Draggable draggable = this.draggables.endDrag();
            if (draggable != null) {
               this.savePosition(draggable, guiScaledWidth, guiScaledHeight);
            }
         }
      }
   }

   private void savePosition(Draggable draggable, int value, int value2) {
      float value3 = draggable.normalizedTargetX(value);
      float value4 = draggable.normalizedTargetY(value2);
      if (draggable.axisLock() == Draggable.AxisLock.VERTICAL) {
         value3 = 0.5F;
      } else {
         Draggable.AxisLock axisLock = draggable.axisLock();
         if (axisLock == Draggable.AxisLock.HORIZONTAL) {
            value4 = 0.5F;
         }
      }

      ConfigManager config2 = this.config;
      String text = draggable.id();
      config2.setDraggablePosition(text, value3, value4);
      this.config.save();
   }

   private double mouseY(Minecraft minecraft) {
      return minecraft.mouseHandler.getScaledYPos(minecraft.getWindow());
   }

   private static float clamp(float value, float value2, float value3) {
      return Math.max(value2, Math.min(value3, value));
   }
}
