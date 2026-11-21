package releon.ru.screens.mainmenu;

import java.util.List;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import releon.ru.utils.render.ui.Render2D;
import releon.ru.utils.render.ui.font.FontType;
import releon.ru.utils.render.ui.outline.outline360.BuiltOutline360;
import releon.ru.utils.render.ui.outline.outline360.Outline360Range;
import releon.ru.utils.render.ui.zippy.BuiltZippy;

public final class NeonMainMenuScreen extends Screen {
   private static final float BUTTON_WIDTH = 114.0F;
   private static final float BUTTON_HEIGHT = 21.0F;
   private static final float BUTTON_GAP = 1.5F;
   private static final float BUTTON_RADIUS = 5.5F;
   private final List<NeonMainMenuScreen.MenuAction> actions = List.of(
      new NeonMainMenuScreen.MenuAction("ОДИНОЧНАЯ ИГРА", this::openSingleplayer),
      new NeonMainMenuScreen.MenuAction("СЕТЕВАЯ ИГРА", this::openMultiplayer),
      new NeonMainMenuScreen.MenuAction("НАСТРОЙКИ", this::openOptions),
      new NeonMainMenuScreen.MenuAction("АККАУНТЫ", this::openAccounts)
   );
   private final float[] hover = new float[this.actions.size()];
   private long lastFrameNanos;

   public NeonMainMenuScreen() {
      super(Text.literal("Releon"));
   }

   protected void init() {
      this.lastFrameNanos = System.nanoTime();
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (this.exitChordPressed()) {
         this.client.scheduleStop();
      } else {
         float designWidth = (float)Render2D.getFixedScaledWidth();
         float designHeight = (float)Render2D.getFixedScaledHeight();
         float fixedMouseX = Render2D.guiToFixed((float)mouseX);
         float fixedMouseY = Render2D.guiToFixed((float)mouseY);
         float deltaSeconds = this.frameDeltaSeconds();
         float contentScale = Math.min(1.0F, Math.max(0.82F, designWidth / 420.0F));
         float buttonWidth = 114.0F * contentScale;
         float buttonHeight = 21.0F * contentScale;
         float buttonGap = 1.5F * contentScale;
         float stackHeight = (float)this.actions.size() * buttonHeight + (float)(this.actions.size() - 1) * buttonGap;
         float startY = (designHeight - stackHeight) * 0.5F;
         float x = (designWidth - buttonWidth) * 0.5F;
         renderBackground(context, this.width, this.height);
         context.createNewRootLayer();
         Render2D.beginFrame(context);

         for (int i = 0; i < this.actions.size(); i++) {
            float y = startY + (float)i * (buttonHeight + buttonGap);
            boolean hovered = contains((double)fixedMouseX, (double)fixedMouseY, x, y, buttonWidth, buttonHeight);
            this.hover[i] = approach(this.hover[i], hovered ? 1.0F : 0.0F, deltaSeconds * 7.5F);
            this.renderButton(this.actions.get(i).label(), x, y, buttonWidth, buttonHeight, contentScale, this.hover[i], i);
         }

         this.renderExitHint(startY + stackHeight + 6.0F, designWidth);
         Render2D.flush();
      }
   }

   public static void renderBackground(DrawContext graphics, int width, int height) {
      if (!VanillaMenuTheme.active()) {
         width = Render2D.getFixedScaledWidth();
         height = Render2D.getFixedScaledHeight();
      }

      Render2D.beginFrame(graphics);
      Render2D.rect(-2.0F, -2.0F, (float)width + 4.0F, (float)height + 4.0F, -16776697);
      float time = (float)(System.nanoTime() % 240000000000L) / 1.0E9F;
      float pulseA = wave(time * 0.18F, 0.0F);
      float pulseB = wave(time * 0.14F, 2.1F);
      float pulseC = wave(time * 0.11F, 4.4F);
      Render2D.zippy(
         new BuiltZippy(
            (float)(-width) * 0.24F,
            (float)(-height) * 0.28F,
            (float)width * 1.48F,
            (float)height * 1.55F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            alpha(-16756824, Math.round(55.0F + pulseA * 70.0F)),
            1.0F,
            -27.0F
         )
      );
      Render2D.zippy(
         new BuiltZippy(
            (float)(-width) * 0.08F,
            (float)(-height) * 0.14F,
            (float)width * 1.18F,
            (float)height * 1.28F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            alpha(-16740376, Math.round(40.0F + pulseB * 65.0F)),
            1.0F,
            19.0F
         )
      );
      Render2D.zippy(
         new BuiltZippy(
            (float)(-width) * 0.35F,
            (float)(-height) * 0.24F,
            (float)width * 1.45F,
            (float)height * 1.52F,
            0.0F,
            0.0F,
            0.0F,
            0.0F,
            alpha(-15030304, Math.round(20.0F + pulseC * 52.0F)),
            1.0F,
            63.0F
         )
      );
      Render2D.rect(0.0F, 0.0F, (float)width, (float)height, 0.0F, -671088638, -1258289648, -838860281, -419430399);
      Render2D.rect(0.0F, 0.0F, (float)width, (float)height, 0.0F, 436220776, 100663296, 570427416, 150994944);
      Render2D.flush();
   }

   private void renderExitHint(float y, float designWidth) {
      String hint = "Для выхода нажми комбинацию клавиш K+L";
      float shimmer = wave((float)(System.nanoTime() % 60000000000L) / 1.0E9F * 1.15F, 0.0F);
      float size = 5.15F;
      int color = alpha(-1838598, Math.round(92.0F + shimmer * 88.0F));
      float textWidth = Render2D.textWidth(FontType.REGULARNEW, hint, size);
      Render2D.text(FontType.REGULARNEW, hint, (designWidth - textWidth) * 0.5F, y, size, color);
   }

   private void renderButton(String label, float x, float y, float buttonWidth, float buttonHeight, float scale, float hoverProgress, int index) {
      float eased = 1.0F - (1.0F - hoverProgress) * (1.0F - hoverProgress);
      float radius = 5.5F * scale;
      float time = (float)(System.nanoTime() % 120000000000L) / 1.0E9F;
      float blurWave = 0.5F + 0.5F * (float)Math.sin((double)(time * (0.62F + (float)index * 0.07F) + (float)index * 1.73F));
      float blurStrength = 7.0F + blurWave * 17.0F;
      int blurTint = alpha(-8994817, Math.round(24.0F + blurWave * 35.0F));
      int top = alpha(-2954753, Math.round(6.0F + blurWave * 3.0F));
      int bottom = alpha(-12417593, Math.round(2.0F + blurWave * 2.0F));
      if (eased > 0.01F) {
         float edgeAngle = (time * 220.0F + (float)index * 57.0F) % 360.0F;
         Outline360Range farTail = new Outline360Range(edgeAngle - 190.0F, edgeAngle + 14.0F, alpha(-16755528, Math.round(eased * 62.0F)));
         Outline360Range middle = new Outline360Range(edgeAngle - 118.0F, edgeAngle + 14.0F, alpha(-16741121, Math.round(eased * 155.0F)));
         Outline360Range head = new Outline360Range(edgeAngle - 46.0F, edgeAngle + 14.0F, alpha(-3738625, Math.round(eased * 255.0F)));
         Outline360Range haloMiddle = new Outline360Range(edgeAngle - 105.0F, edgeAngle + 18.0F, alpha(-16745473, Math.round(eased * 48.0F)));
         Outline360Range haloHead = new Outline360Range(edgeAngle - 42.0F, edgeAngle + 18.0F, alpha(-12138241, Math.round(eased * 105.0F)));
         Render2D.outline360(
            new BuiltOutline360(x - 2.8F, y - 2.8F, buttonWidth + 5.6F, buttonHeight + 5.6F, radius + 2.8F, 3.0F, 0, List.of(haloMiddle, haloHead))
               .withSmoothness(4.0F)
               .withBlendDegrees(30.0F)
         );
         Render2D.outline360(
            new BuiltOutline360(x - 1.3F, y - 1.3F, buttonWidth + 2.6F, buttonHeight + 2.6F, radius + 1.3F, 1.55F, 0, List.of(haloMiddle, haloHead))
               .withSmoothness(2.2F)
               .withBlendDegrees(24.0F)
         );
         Render2D.outline360(
            new BuiltOutline360(x - 0.15F, y - 0.15F, buttonWidth + 0.3F, buttonHeight + 0.3F, radius + 0.15F, 0.32F, 0, List.of(farTail))
               .withSmoothness(0.75F)
               .withBlendDegrees(28.0F)
         );
         Render2D.outline360(
            new BuiltOutline360(x - 0.28F, y - 0.28F, buttonWidth + 0.56F, buttonHeight + 0.56F, radius + 0.28F, 0.52F, 0, List.of(middle))
               .withSmoothness(0.8F)
               .withBlendDegrees(20.0F)
         );
         Render2D.outline360(
            new BuiltOutline360(x - 0.42F, y - 0.42F, buttonWidth + 0.84F, buttonHeight + 0.84F, radius + 0.42F, 0.78F, 0, List.of(head))
               .withSmoothness(0.85F)
               .withBlendDegrees(12.0F)
         );
      }

      Render2D.blur(x, y, buttonWidth, buttonHeight, radius, blurStrength, 1.0F, blurTint);
      Render2D.glass(
         x,
         y,
         buttonWidth,
         buttonHeight,
         radius,
         alpha(-1641217, 20),
         0.34F + blurWave * 0.12F,
         42.0F,
         alpha(-4596481, 22),
         0.07F,
         true,
         0.1F,
         0.003F + blurWave * 0.003F,
         1.0F,
         0.0F
      );
      Render2D.rect(x, y, buttonWidth, buttonHeight, radius, top, top, bottom, bottom);
      float textSize = 6.15F * scale;
      int textColor = -1430730538;
      float textWidth = Render2D.textWidth(FontType.REGULARNEW, label, textSize);
      Render2D.text(FontType.REGULARNEW, label, x + (buttonWidth - textWidth) * 0.5F, y + (buttonHeight - textSize) * 0.5F - 0.5F, textSize, textColor);
   }

   public boolean mouseClicked(Click click, boolean doubled) {
      if (click.button() != 0) {
         return super.mouseClicked(click, doubled);
      } else {
         float designWidth = (float)Render2D.getFixedScaledWidth();
         float designHeight = (float)Render2D.getFixedScaledHeight();
         double fixedMouseX = Render2D.guiToFixed(click.x());
         double fixedMouseY = Render2D.guiToFixed(click.y());
         float scale = Math.min(1.0F, Math.max(0.82F, designWidth / 420.0F));
         float buttonWidth = 114.0F * scale;
         float buttonHeight = 21.0F * scale;
         float gap = 1.5F * scale;
         float stackHeight = (float)this.actions.size() * buttonHeight + (float)(this.actions.size() - 1) * gap;
         float startY = (designHeight - stackHeight) * 0.5F;
         float x = (designWidth - buttonWidth) * 0.5F;

         for (int i = 0; i < this.actions.size(); i++) {
            float y = startY + (float)i * (buttonHeight + gap);
            if (contains(fixedMouseX, fixedMouseY, x, y, buttonWidth, buttonHeight)) {
               this.actions.get(i).action().run();
               return true;
            }
         }

         return super.mouseClicked(click, doubled);
      }
   }

   public boolean keyPressed(KeyInput input) {
      if (this.exitChordPressed()) {
         this.client.scheduleStop();
         return true;
      } else {
         return input.key() == 256 ? true : super.keyPressed(input);
      }
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void close() {
   }

   private void openSingleplayer() {
      this.client.setScreen(new SelectWorldScreen(this));
   }

   private void openMultiplayer() {
      this.client.setScreen(new MultiplayerScreen(this));
   }

   private void openOptions() {
      this.client.setScreen(new OptionsScreen(this, this.client.options));
   }

   private void openAccounts() {
      this.client.setScreen(new AltManagerScreen());
   }

   private boolean exitChordPressed() {
      long window = this.client.getWindow().getHandle();
      return GLFW.glfwGetKey(window, 75) == 1 && GLFW.glfwGetKey(window, 76) == 1;
   }

   private float frameDeltaSeconds() {
      long now = System.nanoTime();
      float delta = this.lastFrameNanos == 0L ? 0.0F : (float)(now - this.lastFrameNanos) / 1.0E9F;
      this.lastFrameNanos = now;
      return Math.min(delta, 0.05F);
   }

   private static boolean contains(double mouseX, double mouseY, float x, float y, float width, float height) {
      return mouseX >= (double)x && mouseX <= (double)(x + width) && mouseY >= (double)y && mouseY <= (double)(y + height);
   }

   private static float approach(float current, float target, float amount) {
      return current < target ? Math.min(target, current + amount) : Math.max(target, current - amount);
   }

   private static float wave(float time, float phase) {
      return 0.5F + 0.5F * (float)Math.sin((double)(time + phase));
   }

   private static int alpha(int color, int alpha) {
      return Math.max(0, Math.min(255, alpha)) << 24 | color & 16777215;
   }

   private static record MenuAction(String label, Runnable action) {
   }
}
