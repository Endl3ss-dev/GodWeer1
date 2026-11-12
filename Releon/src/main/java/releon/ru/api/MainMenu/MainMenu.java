package releon.ru.api.MainMenu;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.session.Session;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import releon.ru.utils.render.color.ColorUtil;
import releon.ru.utils.render.ui.Render2D;
import releon.ru.utils.render.ui.font.FontType;

public final class MainMenu extends Screen {
   private static final Identifier BACK_GIF = Identifier.of("cataclysm", "ui/back.gif");
   private static final float GIF_FPS = 30.0F;
   private static final float REF_W = 960.0F;
   private static final float REF_H = 540.0F;
   private static final float BASE_BTN_H = 26.0F;
   private static final float BASE_BTN_GAP = 6.0F;
   private static final float BASE_BTN_R = 7.0F;
   private static final float BASE_PRIMARY_W = 210.0F;
   private static final float BASE_ROW_BTN_W = 64.0F;
   private static final float MAX_PRIMARY_FRAC = 0.28F;
   private final MinecraftClient mc = MinecraftClient.getInstance();
   private GifRenderer backgroundGif;
   private final List<MainMenu.MenuButton> buttons = new ArrayList<>();
   private float contentX;
   private float contentY;
   private float uiScale = 1.0F;
   private float lastDw = -1.0F;
   private float lastDh = -1.0F;
   private Supplier<SkinTextures> skinLookup;
   private String cachedPlayerName = "Player";

   public MainMenu() {
      super(Text.literal("Main Menu"));
   }

   protected void init() {
      super.init();
      if (this.backgroundGif == null) {
         this.backgroundGif = new GifRenderer(BACK_GIF, 30.0F);
      }

      this.backgroundGif.ensureLoaded();
      this.ensureSkinLookup();
      this.rebuildButtons();
   }

   public void removed() {
      super.removed();
   }

   public void close() {
   }

   private float designW() {
      return (float)Render2D.getFixedScaledWidth();
   }

   private float designH() {
      return (float)Render2D.getFixedScaledHeight();
   }

   private float mx(double guiX) {
      return (float)Render2D.guiToFixed(guiX);
   }

   private float my(double guiY) {
      return (float)Render2D.guiToFixed(guiY);
   }

   private float computeUiScale(float dw, float dh) {
      float s = Math.min(dw / 960.0F, dh / 540.0F);
      return Math.max(0.7F, Math.min(s, 1.2F));
   }

   private void ensureSkinLookup() {
      this.cachedPlayerName = this.playerName();

      try {
         Session user = this.mc.getSession();
         if (user == null) {
            this.skinLookup = DefaultSkinHelper::getSteve;
            return;
         }

         UUID uuid = user.getUuidOrNull();
         String name = user.getUsername() != null ? user.getUsername() : "Player";
         GameProfile profile = new GameProfile(uuid, name);
         this.skinLookup = this.mc.getSkinProvider().supplySkinTextures(profile, true);
      } catch (Throwable var5) {
         this.skinLookup = DefaultSkinHelper::getSteve;
      }
   }

   private void rebuildButtons() {
      this.buttons.clear();
      float dw = this.designW();
      float dh = this.designH();
      this.uiScale = this.computeUiScale(dw, dh);
      this.lastDw = dw;
      this.lastDh = dh;
      float s = this.uiScale;
      float btnH = 26.0F * s;
      float gap = 6.0F * s;
      float primaryW = Math.min(210.0F * s, dw * 0.28F);
      primaryW = Math.max(primaryW, 150.0F * s);
      this.contentX = Math.max(28.0F * s, dw * 0.065F);
      this.contentY = dh * 0.28F;
      float y = this.contentY + 66.0F * s;
      this.buttons.add(new MainMenu.MenuButton("multi", "Многопользовательская игра", this.contentX, y, primaryW, btnH, true, true, false));
      y += btnH + gap;
      this.buttons.add(new MainMenu.MenuButton("single", "Одиночная игра", this.contentX, y, primaryW, btnH, false, true, false));
      y += btnH + gap;
      this.buttons.add(new MainMenu.MenuButton("account", "Сменить аккаунт", this.contentX, y, primaryW, btnH, false, true, true));
      y += btnH + gap + 2.0F * s;
      float smallW = 64.0F * s;
      float smallH = Math.max(18.0F, btnH - 2.0F * s);
      float rowGap = 5.0F * s;
      this.buttons.add(new MainMenu.MenuButton("options", "Опции", this.contentX, y, smallW + 8.0F * s, smallH, false, false, false));
      this.buttons.add(new MainMenu.MenuButton("quit", "Выход", this.contentX + smallW + 8.0F * s + rowGap, y, smallW, smallH, false, false, false));
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      float dw = this.designW();
      float dh = this.designH();
      float fmx = this.mx((double)mouseX);
      float fmy = this.my((double)mouseY);
      if (this.buttons.isEmpty() || Math.abs(dw - this.lastDw) > 0.5F || Math.abs(dh - this.lastDh) > 0.5F) {
         this.rebuildButtons();
      }

      Render2D.beginFrame(context);
      Render2D.rect(0.0F, 0.0F, dw, dh, 0.0F, ColorUtil.rgba(6, 8, 12, 255));
      if (this.backgroundGif != null) {
         this.backgroundGif.renderCover(0.0F, 0.0F, dw, dh, ColorUtil.rgba(255, 255, 255, 255));
      }

      Render2D.rect(
         0.0F, 0.0F, dw * 0.52F, dh, 0.0F, ColorUtil.rgba(0, 0, 0, 125), ColorUtil.rgba(0, 0, 0, 0), ColorUtil.rgba(0, 0, 0, 0), ColorUtil.rgba(0, 0, 0, 135)
      );
      Render2D.rect(0.0F, dh - 64.0F * this.uiScale, dw, 64.0F * this.uiScale, 0.0F, ColorUtil.rgba(0, 0, 0, 80));
      this.renderAccountChip();
      this.renderHero();
      this.renderButtons(fmx, fmy);
      Render2D.flush();
   }

   private void renderAccountChip() {
      String name = this.playerName();
      float s = this.uiScale;
      float pillX = 16.0F * s;
      float pillY = 12.0F * s;
      float pillH = 20.0F * s;
      float face = 14.0F * s;
      float nameSize = 7.5F * s;
      float nameW = Render2D.textWidth(FontType.SEMIBOLD, name, nameSize);
      float pillW = 7.0F * s + face + 6.0F * s + nameW + 10.0F * s;
      Render2D.rect(pillX, pillY, pillW, pillH, pillH * 0.5F, ColorUtil.rgba(0, 0, 0, 70));
      Render2D.outline(pillX, pillY, pillW, pillH, pillH * 0.5F, 0.5F, ColorUtil.rgba(255, 255, 255, 18));
      float faceX = pillX + 3.0F * s;
      float faceY = pillY + (pillH - face) * 0.5F;
      this.drawPlayerHead(faceX, faceY, face);
      Render2D.text(FontType.SEMIBOLD, name, faceX + face + 5.0F * s, pillY + (pillH - nameSize) * 0.48F, nameSize, ColorUtil.rgba(220, 222, 230, 240));
   }

   private void renderHero() {
      String name = this.playerName().toUpperCase(Locale.ROOT);
      float s = this.uiScale;
      float x = this.contentX;
      float y = this.contentY;
      Render2D.text(FontType.SEMIBOLD, "ДАВНО НЕ ВИДЕЛИСЬ, " + name + " !", x, y, 6.0F * s, ColorUtil.rgba(130, 160, 255, 210));
      y += 12.0F * s;
      Render2D.text(FontType.SEMIBOLD, "Приветствуем тебя в", x, y, 14.5F * s, ColorUtil.rgba(245, 246, 250, 255));
      y += 17.0F * s;
      Render2D.text(FontType.SEMIBOLD, "Releon Client!", x, y, 16.0F * s, ColorUtil.rgba(255, 255, 255, 255));
      y += 20.0F * s;
      Render2D.text(FontType.REGULARNEW, "Есть план или доверимся случайному направлению?", x, y, 7.5F * s, ColorUtil.rgba(175, 178, 188, 200));
   }

   private void renderButtons(float mx, float my) {
      float s = this.uiScale;
      float radius = 7.0F * s;

      for (MainMenu.MenuButton b : this.buttons) {
         boolean hov = b.contains(mx, my);
         float blurR = hov ? 22.0F : 18.0F;
         if (b.primary) {
            int tint = hov ? ColorUtil.rgba(100, 120, 245, 95) : ColorUtil.rgba(90, 110, 230, 72);
            Render2D.blur(b.x, b.y, b.w, b.h, radius, blurR, 1.1F, tint);
            Render2D.rect(b.x, b.y, b.w, b.h, radius, hov ? ColorUtil.rgba(120, 140, 255, 38) : ColorUtil.rgba(100, 120, 240, 22));
            Render2D.outline(b.x, b.y, b.w, b.h, radius, 0.65F, ColorUtil.rgba(180, 195, 255, hov ? 90 : 55));
            float size = 9.0F * s;
            Render2D.text(FontType.SEMIBOLD, b.label, b.x + 12.0F * s, b.y + (b.h - size) * 0.48F, size, ColorUtil.rgba(255, 255, 255, 255));
            float chevron = 11.0F * s;
            Render2D.rect(b.x + b.w - 20.0F * s, b.y + (b.h - chevron) * 0.5F, chevron, chevron, 3.5F * s, ColorUtil.rgba(255, 255, 255, 40));
         } else {
            int tint = hov ? ColorUtil.rgba(255, 255, 255, 48) : ColorUtil.rgba(0, 0, 0, 55);
            Render2D.blur(b.x, b.y, b.w, b.h, radius, blurR, 1.1F, tint);
            Render2D.rect(b.x, b.y, b.w, b.h, radius, hov ? ColorUtil.rgba(255, 255, 255, 18) : ColorUtil.rgba(0, 0, 0, 28));
            Render2D.outline(b.x, b.y, b.w, b.h, radius, 0.6F, ColorUtil.rgba(255, 255, 255, hov ? 55 : 28));
            float size = (b.wide ? 9.0F : 8.0F) * s;
            float textPadLeft = 12.0F * s;
            if (b.showHead) {
               float face = Math.min(14.0F * s, b.h - 8.0F * s);
               float faceX = b.x + b.w - face - 8.0F * s;
               float faceY = b.y + (b.h - face) * 0.5F;
               this.drawPlayerHead(faceX, faceY, face);
            }

            float tw = Render2D.textWidth(FontType.SEMIBOLD, b.label, size);
            float tx = b.wide ? b.x + textPadLeft : b.x + (b.w - tw) * 0.5F;
            Render2D.text(FontType.SEMIBOLD, b.label, tx, b.y + (b.h - size) * 0.48F, size, ColorUtil.rgba(235, 237, 242, 245));
         }
      }
   }

   private String playerName() {
      try {
         if (this.mc.getSession() != null && this.mc.getSession().getUsername() != null && !this.mc.getSession().getUsername().isBlank()) {
            return this.mc.getSession().getUsername();
         }
      } catch (Throwable var2) {
      }

      return this.cachedPlayerName != null ? this.cachedPlayerName : "Player";
   }

   private void drawPlayerHead(float x, float y, float size) {
      float radius = Math.max(2.5F, size * 0.22F);

      try {
         if (this.mc.player != null) {
            String texture = this.mc.player.getSkin().body().texturePath().toString();
            this.drawSkinFace(texture, x, y, size, radius);
            return;
         }

         if (this.skinLookup == null) {
            this.ensureSkinLookup();
         }

         if (this.skinLookup != null) {
            SkinTextures skin = this.skinLookup.get();
            if (skin != null && skin.body() != null) {
               Identifier path = skin.body().texturePath();
               if (path != null) {
                  this.drawSkinFace(path.toString(), x, y, size, radius);
                  return;
               }
            }
         }

         try {
            UUID uuid = this.mc.getSession() != null ? this.mc.getSession().getUuidOrNull() : null;
            SkinTextures def = uuid != null ? DefaultSkinHelper.getSkinTextures(uuid) : DefaultSkinHelper.getSteve();
            if (def != null && def.body() != null) {
               this.drawSkinFace(def.body().texturePath().toString(), x, y, size, radius);
               return;
            }
         } catch (Throwable var7) {
         }
      } catch (Throwable var8) {
      }

      Render2D.rect(x, y, size, size, radius, ColorUtil.rgba(50, 54, 64, 230));
   }

   private void drawSkinFace(String texture, float x, float y, float size, float radius) {
      int col = ColorUtil.rgba(255, 255, 255, 255);
      Render2D.imageUvNearest(texture, x, y, size, size, radius, 0.8F, 0.125F, 0.125F, 0.25F, 0.25F, col);
      Render2D.imageUvNearest(texture, x, y, size, size, radius, 0.8F, 0.625F, 0.125F, 0.75F, 0.25F, col);
   }

   public boolean mouseClicked(Click click, boolean doubled) {
      if (click.button() != 0) {
         return super.mouseClicked(click, doubled);
      } else {
         float mx = this.mx(click.x());
         float my = this.my(click.y());

         for (MainMenu.MenuButton b : this.buttons) {
            if (b.contains(mx, my)) {
               this.onButton(b.id);
               return true;
            }
         }

         return super.mouseClicked(click, doubled);
      }
   }

   private void onButton(String id) {
      switch (id) {
         case "multi":
            this.mc.setScreen(new MultiplayerScreen(this));
            break;
         case "single":
            this.mc.setScreen(new SelectWorldScreen(this));
            break;
         case "options":
            this.mc.setScreen(new OptionsScreen(this, this.mc.options));
            break;
         case "account":
            this.openAccountSwitcher();
            break;
         case "quit":
            this.mc.scheduleStop();
      }
   }

   private void openAccountSwitcher() {
      try {
         Class<?> cls = Class.forName("ru.vidtu.ias.screen.AccountScreen");
         Constructor<?> ctor = cls.getConstructor(Screen.class);
         if (ctor.newInstance(this) instanceof Screen s) {
            this.mc.setScreen(s);
            return;
         }
      } catch (ClassNotFoundException var5) {
      } catch (Throwable var6) {
         var6.printStackTrace();
      }

      this.mc.setScreen(new MultiplayerScreen(this));
   }

   public boolean shouldPause() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   private static final class MenuButton {
      final String id;
      final String label;
      final float x;
      final float y;
      final float w;
      final float h;
      final boolean primary;
      final boolean wide;
      final boolean showHead;

      MenuButton(String id, String label, float x, float y, float w, float h, boolean primary, boolean wide, boolean showHead) {
         this.id = id;
         this.label = label;
         this.x = x;
         this.y = y;
         this.w = w;
         this.h = h;
         this.primary = primary;
         this.wide = wide;
         this.showHead = showHead;
      }

      boolean contains(float mx, float my) {
         return mx >= this.x && mx <= this.x + this.w && my >= this.y && my <= this.y + this.h;
      }
   }
}
