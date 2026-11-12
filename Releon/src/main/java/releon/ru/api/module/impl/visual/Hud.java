package releon.ru.api.module.impl.visual;

import java.awt.Color;
import java.util.List;
import net.minecraft.client.gui.Click;
import releon.ru.api.drag.core.ElementManager;
import releon.ru.api.drag.core.ElementScreen;
import releon.ru.api.drag.core.HudElement;
import releon.ru.api.drag.impl.AnimationShowcase;
import releon.ru.api.drag.impl.Armor;
import releon.ru.api.drag.impl.Binds;
import releon.ru.api.drag.impl.Cooldowns;
import releon.ru.api.drag.impl.HotKeys;
import releon.ru.api.drag.impl.Hotbar;
import releon.ru.api.drag.impl.HudPanel;
import releon.ru.api.drag.impl.Info;
import releon.ru.api.drag.impl.Inventory;
import releon.ru.api.drag.impl.MediaPlayer;
import releon.ru.api.drag.impl.Notifications;
import releon.ru.api.drag.impl.Potions;
import releon.ru.api.drag.impl.Staff;
import releon.ru.api.drag.impl.TargetHud;
import releon.ru.api.drag.impl.Watermark;
import releon.ru.api.drag.impl.WatermarkIcons;
import releon.ru.api.events.impl.DrawEvent;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.settings.impl.BooleanSetting;
import releon.ru.api.settings.impl.ColorSetting;
import releon.ru.api.settings.impl.MultiModeSetting;
import releon.ru.api.settings.impl.NumberSetting;
import releon.ru.utils.render.LoadingVisualGuard;
import releon.ru.utils.render.color.ColorUtil;
import releon.ru.utils.render.item.RenderItem;
import releon.ru.utils.render.ui.Render2D;
import releon.ru.utils.render.ui.blur.BuiltBlur;

public class Hud extends Module {
   private static final float NO_BLUR_RECT_ALPHA_SCALE = 0.78431374F;
   private static final float HUD_ACCENT_ALPHA = 1.0F;
   private static final String[] ELEMENTS = new String[]{
      "Watermark", "Hotbar", "Armor", "HotKeys", "Binds", "Notifications", "Potions", "Cooldowns", "TargetHud", "Staff", "Inventory", "MediaPlayer", "Info"
   };
   private static final String[] DEFAULT_ELEMENTS = new String[]{
      "Watermark", "Hotbar", "Armor", "HotKeys", "Binds", "Notifications", "Potions", "Cooldowns", "TargetHud", "Staff", "Inventory"
   };
   private static Hud instance;
   private final MultiModeSetting elements = this.register(new MultiModeSetting("Elements", "HUD elements to render.", ELEMENTS, DEFAULT_ELEMENTS));
   private final BooleanSetting blur = new BooleanSetting("Blur", "Use blurred HUD backgrounds.", true);
   private final BooleanSetting glow = new BooleanSetting("Glow", "Use glow effects on HUD elements.", true);
   private final ColorSetting color = this.register(new ColorSetting("Icon HudColor", "", new Color(247, 133, 255, 255)));
   private final NumberSetting particleCount = this.register(new NumberSetting("TargetHud Particle Count", "Particles per hit.", 22.0, 1.0, 40.0, 1.0));
   private final BooleanSetting particles = this.register(new BooleanSetting("TargetHud Hit Particles", "Particles on target hit.", true));
   private final List<HudElement> elementsList = List.of(
      new Watermark(),
      new Hotbar(),
      new Armor(),
      new HotKeys(),
      new Info(),
      new Binds(),
      new Notifications(),
      new Potions(),
      new Cooldowns(),
      new TargetHud(),
      new Staff(),
      new Inventory(),
      new MediaPlayer(),
      new WatermarkIcons(),
      new AnimationShowcase()
   );

   public Hud() {
      super("HUD", "Renders draggable HUD elements.", ModuleCategory.VISUAL);
      instance = this;
      this.particles.visibleWhen(() -> this.elements.isSelected("TargetHud"));
      this.particleCount.visibleWhen(() -> this.particles.getValue());
      this.setEnabled(true);
   }

   public static Hud getInstance() {
      return instance;
   }

   public static boolean shouldRenderCustomHotbar() {
      Hud hud = instance;
      return hud != null && hud.isEnabled() && hud.elements.isSelected("Hotbar");
   }

   public static boolean isBlurEnabled() {
      Hud hud = instance;
      return hud == null || hud.blur.getValue();
   }

   public static boolean isGlowEnabled() {
      Hud hud = instance;
      return hud == null || hud.glow.getValue();
   }

   public static void renderHudBackground(float x, float y, float width, float height, float radius, float blurRadius, float smoothness, int color) {
      if (isBlurEnabled()) {
         Render2D.blur(x, y, width, height, radius, blurRadius, smoothness, color);
      } else {
         Render2D.rect(x, y, width, height, radius, noBlurRectColor(color));
      }
   }

   public static void renderHudBackground(BuiltBlur blur) {
      if (blur != null) {
         if (isBlurEnabled()) {
            Render2D.blur(blur);
         } else {
            Render2D.rect(
               blur.x(),
               blur.y(),
               blur.width(),
               blur.height(),
               blur.radiusTopLeft(),
               blur.radiusTopRight(),
               blur.radiusBottomRight(),
               blur.radiusBottomLeft(),
               noBlurRectColor(blur.color())
            );
         }
      }
   }

   public static void renderHudGlow(String texture, float x, float y, float width, float height, float radius, int color) {
      if (isGlowEnabled()) {
         Render2D.image(texture, x, y, width, height, radius, color);
      }
   }

   private static int noBlurRectColor(int color) {
      int alpha = color >>> 24 & 0xFF;
      int scaledAlpha = Math.round((float)alpha * 0.78431374F);
      return color & 16777215 | scaledAlpha << 24;
   }

   public void renderHudLayer(DrawEvent event) {
      if (mc.player != null && mc.world != null && mc.getWindow() != null && !LoadingVisualGuard.shouldSuppressHud(mc)) {
         ElementScreen screen = ElementScreen.current();
         ElementManager elementManager = ElementManager.getInstance();
         elementManager.frame(screen);
         if (event.getLayer() == DrawEvent.Layer.CHAT_OVERLAY) {
            elementManager.updateActiveElementFromMouse();
         }

         RenderItem.beginFrame(event.getGraphics());

         try {
            for (HudElement element : this.elementsList) {
               boolean selected = this.elements.isSelected(this.elementName(element));
               this.setElementVisible(element, selected);
               if (selected && this.shouldRenderElementInLayer(element, event.getLayer())) {
                  element.render();
               }
            }
         } finally {
            RenderItem.flush();
         }

         boolean editLayer = event.getLayer() == DrawEvent.Layer.CHAT_OVERLAY && elementManager.canEditCurrentScreen();
         if (editLayer) {
            elementManager.renderEditorOverlay(event.getGraphics(), screen);
         }
      }
   }

   public boolean handleMouseClicked(Click event, boolean doubled) {
      if (this.isEnabled() && event != null && event.button() == 0) {
         for (int i = this.elementsList.size() - 1; i >= 0; i--) {
            HudElement element = this.elementsList.get(i);
            if (this.elements.isSelected(this.elementName(element)) && element.mouseClicked(event, doubled)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean shouldRenderElementInLayer(HudElement element, DrawEvent.Layer layer) {
      return true;
   }

   private String elementName(HudElement element) {
      return element.elementName();
   }

   public boolean particles() {
      return this.particles.getValue();
   }

   public int hitParticleCount() {
      return Math.round(this.particleCount.getFloat());
   }

   public int iconColor() {
      return ColorUtil.multAlpha(this.color.getValue().getRGB(), 1.0F);
   }

   private void setElementVisible(HudElement element, boolean visible) {
      if (element instanceof HudPanel panel) {
         panel.setHudVisible(visible);
      } else {
         if (element instanceof Watermark watermark) {
            watermark.setHudVisible(visible);
         }
      }
   }
}
