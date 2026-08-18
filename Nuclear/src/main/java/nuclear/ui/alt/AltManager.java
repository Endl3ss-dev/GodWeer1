package nuclear.ui.alt;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import nuclear.utils.anim.Animation;
import nuclear.utils.anim.Direction;
import nuclear.utils.anim.impl.DecelerateAnimation;
import nuclear.utils.font.Fonts;
import nuclear.utils.render.*;
import nuclear.utils.render.animation.AnimationMath;
import nuclear.utils.render.shader.ShaderUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static nuclear.utils.IMinecraft.mc;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.opengl.GL11.*;

public class AltManager extends Screen {
    private static final long initTime = System.currentTimeMillis();
    private final Animation screenAnimation = new DecelerateAnimation(150, 1.0);
    private boolean exiting = false;

    private float userHover, passHover, searchHover, backHover;
    private final float[] actionButtonsHover = new float[3];

    public ArrayList<Account> accounts = new ArrayList<>();
    private final Map<Account, Float> accountHoverMap = new HashMap<>();
    private final Map<Account, Float> accountAlphaMap = new HashMap<>();
    private final List<Account> pendingRemoval = new ArrayList<>();

    private float scroll, scrollAn;
    private String altName = "", altPass = "", searchQuery = "";
    private boolean typing, typingPass, searching;

    public AltManager() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        exiting = false;
        screenAnimation.setDirection(Direction.FORWARDS);
        screenAnimation.reset();
        super.init();
    }

    private void exitMenu() {
        if (!exiting) {
            exiting = true;
            screenAnimation.setDirection(Direction.BACKWARDS);
        }
    }

    private void addAccountWithAnim(Account acc) {
        accounts.add(acc);
        accountAlphaMap.put(acc, 0f);
        AltConfig.updateFile();
    }

    private ArrayList<Account> getFilteredAccounts() {
        ArrayList<Account> source = new ArrayList<>(accounts);
        if (!searchQuery.isEmpty()) {
            String lowerQuery = searchQuery.toLowerCase();
            source.removeIf(acc -> !acc.accountName.toLowerCase().contains(lowerQuery));
        }
        ArrayList<Account> sorted = new ArrayList<>();
        for (Account account : source) { if (account.pinned) sorted.add(account); }
        for (Account account : source) { if (!account.pinned) sorted.add(account); }
        return sorted;
    }

    @Override
    public void tick() {
        scrollAn = AnimationMath.lerp(scrollAn, scroll, 25f);
        accountHoverMap.keySet().removeIf(acc -> !accounts.contains(acc) && !pendingRemoval.contains(acc));
        accountAlphaMap.keySet().removeIf(acc -> !accounts.contains(acc) && !pendingRemoval.contains(acc));
    }

    private void drawStyledRect(float x, float y, float w, float h, float radius, int outline, int body, int blurBody) {
        GaussianBlur.startBlur();
        RenderUtils.Render2D.drawRoundedRect(x, y, w, h, radius, blurBody);
        GaussianBlur.endBlur(8, 1);
        RenderUtils.Render2D.drawRoundedRect(x - 0.5f, y - 0.5f, w + 1f, h + 1f, radius, outline);
        RenderUtils.Render2D.drawRoundedRect(x, y, w, h, radius, body);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int width = mc.getMainWindow().scaledWidth();
        int height = mc.getMainWindow().scaledHeight();
        float animationValue = (float) screenAnimation.getOutput();

        if (exiting && animationValue <= 0.01f) {
            mc.displayGuiScreen(new MainMenuScreen());
            return;
        }

        mc.gameRenderer.setupOverlayRendering(2);
        RenderUtils.Render2D.drawImage(new ResourceLocation("nuclear/images/mainmenu/background.png"), 0, 0, width, height, -1);

        if (ShaderUtil.sun_rays != null) {
            glDisable(GL_TEXTURE_2D);
            GlStateManager.enableBlend();
            glBlendFunc(GL_SRC_ALPHA, GL_ONE);
            ShaderUtil.sun_rays.attach();
            ShaderUtil.sun_rays.setUniformf("u_resolution", (float)mc.getMainWindow().getFramebufferWidth(), (float)mc.getMainWindow().getFramebufferHeight());
            ShaderUtil.sun_rays.setUniformf("u_time", (System.currentTimeMillis() - initTime) / 1000f);
            ShaderUtil.sun_rays.setUniformf("u_rays", 0.1f);
            ShaderUtil.sun_rays.setUniformf("u_colors[0]", 1.0f, 1.0f, 1.0f, 1.0f);
            ShaderUtil.sun_rays.setUniformf("u_colors[1]", 1.0f, 1.0f, 1.0f, 1.0f);
            ShaderUtil.sun_rays.setUniformf("u_intensity", 0.12f * animationValue);
            ShaderUtil.sun_rays.setUniformf("u_reach", 0.4f);
            RenderUtils.Render2D.drawRect(0, 0, width, height, -1);
            ShaderUtil.sun_rays.detach();
            GlStateManager.disableBlend();
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_TEXTURE_2D);
        }

        if (animationValue <= 0.001f && !exiting) return;
        int globalAlpha = (int) (animationValue * 255);

        int normalOutline = ColorUtils.rgba(35, 35, 35, (int)(205 * animationValue));
        int hoverOutline = ColorUtils.rgba(100, 100, 100, globalAlpha);
        int normalText = ColorUtils.rgba(160, 160, 160, globalAlpha);
        int bodyColor = ColorUtils.rgba(12, 12, 12, (int)(230 * animationValue));
        int activeBodyColor = ColorUtils.rgba(30, 30, 30, (int)(250 * animationValue));
        int blurBodyColor = ColorUtils.rgba(30, 30, 30, (int)(100 * animationValue));

        float pX = width / 2f - 172f;
        float pY = height / 2f - 45f;
        float lX = pX + 124;
        float lY = height / 2f - 80f;

        drawStyledRect(pX, pY, 120, 90, 4f, normalOutline, bodyColor, blurBodyColor);
        Fonts.newcode[18].drawCenteredString(matrixStack, "Alt Manager", pX + 60f, pY + 10f, normalText);

        boolean bh = RenderUtils.isInRegion(mouseX, mouseY, pX + 103, pY + 6, 12, 12) && !exiting;
        backHover = AnimationMath.lerp(backHover, bh ? 1f : 0f, 30f);
        drawStyledRect(pX + 103, pY + 6, 12, 12, 2f, ColorUtils.interpolateColor(normalOutline, hoverOutline, backHover), bodyColor, blurBodyColor);
        Fonts.icon[16].drawCenteredString(matrixStack, "1", pX + 109, pY + 11f, ColorUtils.interpolateColor(normalText, -1, backHover));

        for (int i = 0; i < 2; i++) {
            float fY = pY + 25f + (i * 18f);
            boolean h = RenderUtils.isInRegion(mouseX, mouseY, pX + 10, fY, 100, 16) && !exiting;
            if (i == 0) userHover = AnimationMath.lerp(userHover, h ? 1f : 0f, 30f);
            else passHover = AnimationMath.lerp(passHover, h ? 1f : 0f, 30f);

            float cAn = (i == 0) ? userHover : passHover;
            drawStyledRect(pX + 10, fY, 100, 16, 3f, ColorUtils.interpolateColor(normalOutline, hoverOutline, cAn), bodyColor, blurBodyColor);

            String content;
            if (i == 0) {
                content = (altName.isEmpty() && !typing ? "Username" : altName + (typing && System.currentTimeMillis() % 1000 > 500 ? "_" : ""));
            } else {
                String stars = altPass.replaceAll(".", "*");
                content = (altPass.isEmpty() && !typingPass ? "Password" : stars + (typingPass && System.currentTimeMillis() % 1000 > 500 ? "_" : ""));
            }

            Fonts.newcode[14].drawString(matrixStack, content, pX + 24, fY + 6.5f, ColorUtils.interpolateColor(normalText, -1, cAn));
            Fonts.icon[14].drawString(matrixStack, (i == 0 ? "U" : "v"), pX + 14, fY + 7f, ColorUtils.interpolateColor(normalText, -1, cAn));
        }

        float btnY = pY + 65;
        for (int i = 0; i < 3; i++) {
            float bx = pX + 35 + (i * 18);
            boolean hbtn = RenderUtils.isInRegion(mouseX, mouseY, bx, btnY, 14, 14) && !exiting;
            actionButtonsHover[i] = AnimationMath.lerp(actionButtonsHover[i], hbtn ? 1f : 0f, 30f);
            drawStyledRect(bx, btnY, 14, 14, 2f, ColorUtils.interpolateColor(normalOutline, hoverOutline, actionButtonsHover[i]), bodyColor, blurBodyColor);
            if (i == 0) {
                RenderUtils.Render2D.drawRect(bx+3, btnY+3, 3.5f, 3.5f, ColorUtils.setAlpha(new Color(242, 80, 34).getRGB(), globalAlpha));
                RenderUtils.Render2D.drawRect(bx+7.5f, btnY+3, 3.5f, 3.5f, ColorUtils.setAlpha(new Color(127, 186, 0).getRGB(), globalAlpha));
                RenderUtils.Render2D.drawRect(bx+3, btnY+7.5f, 3.5f, 3.5f, ColorUtils.setAlpha(new Color(0, 164, 239).getRGB(), globalAlpha));
                RenderUtils.Render2D.drawRect(bx+7.5f, btnY+7.5f, 3.5f, 3.5f, ColorUtils.setAlpha(new Color(255, 185, 0).getRGB(), globalAlpha));
            } else Fonts.icon2[16].drawCenteredString(matrixStack, (i == 1 ? "v" : "w"), bx + 7f, btnY + 6f, ColorUtils.interpolateColor(normalText, -1, actionButtonsHover[i]));
        }

        drawStyledRect(lX, lY, 220, 160, 4f, normalOutline, bodyColor, blurBodyColor);

        boolean sh = RenderUtils.isInRegion(mouseX, mouseY, lX + 8, lY + 6.5f, 204, 16) && !exiting;
        searchHover = AnimationMath.lerp(searchHover, sh ? 1f : 0f, 30f);
        drawStyledRect(lX + 8, lY + 6.5f, 204, 16, 4f, ColorUtils.interpolateColor(normalOutline, hoverOutline, searchHover), bodyColor, blurBodyColor);
        String sTxt = (searchQuery.isEmpty() && !searching ? "Search.." : searchQuery + (searching && System.currentTimeMillis() % 1000 > 500 ? "_" : ""));
        Fonts.icon[15].drawString(matrixStack, "O", lX + 13, lY + 13, ColorUtils.interpolateColor(normalText, -1, searchHover));
        Fonts.newcode[15].drawString(matrixStack, sTxt, lX + 24, lY + 13, ColorUtils.interpolateColor(normalText, -1, searchHover));

        StencilUtils.initStencilToWrite();
        RenderUtils.Render2D.drawRoundedRect(lX, lY + 25, 220, 130, 4f, -1);
        StencilUtils.readStencilBuffer(1);

        List<Account> filtered = getFilteredAccounts();
        for (int i = 0; i < filtered.size(); i++) {
            Account acc = filtered.get(i);
            float acX = lX + 8 + (i % 2) * 102;
            float acY = lY + 28 + (i / 2) * 26 + scrollAn;

            float alpha = accountAlphaMap.getOrDefault(acc, 1.0f);
            if (pendingRemoval.contains(acc)) {
                alpha = AnimationMath.lerp(alpha, 0f, 20f);
                if (alpha < 0.01f) { accounts.remove(acc); pendingRemoval.remove(acc); continue; }
            } else alpha = AnimationMath.lerp(alpha, 1.0f, 20f);
            accountAlphaMap.put(acc, alpha);

            boolean isActive = acc.accountName.equalsIgnoreCase(mc.session.getUsername());
            boolean h = RenderUtils.isInRegion(mouseX, mouseY, acX, acY, 100, 24) && !pendingRemoval.contains(acc) && !exiting;
            float hAn = AnimationMath.lerp(accountHoverMap.getOrDefault(acc, 0f), h ? 1f : 0f, 30f);
            accountHoverMap.put(acc, hAn);

            float fAlpha = alpha * animationValue;
            int curBody = isActive ? activeBodyColor : bodyColor;
            int curOut = ColorUtils.interpolateColor(normalOutline, (isActive ? hoverOutline : ColorUtils.rgba(80, 80, 80, 255)), hAn);

            drawStyledRect(acX, acY, 100, 24, 4f,
                    ColorUtils.setAlpha(curOut, (int)(ColorUtils.getAlpha(curOut) * fAlpha)),
                    ColorUtils.setAlpha(curBody, (int)(ColorUtils.getAlpha(curBody) * fAlpha)),
                    ColorUtils.setAlpha(blurBodyColor, (int)(100 * fAlpha)));

            RenderUtils.Render2D.drawRoundFace(acX + 3, acY + 3, 18, 18, 3f, fAlpha, acc.skin);
            Fonts.newcode[13].drawString(matrixStack, acc.accountName, acX + 24, acY + 6, ColorUtils.setAlpha(isActive ? -1 : normalText, (int)(255 * fAlpha)));
            Fonts.newcode[12].drawString(matrixStack, new SimpleDateFormat("dd.MM.yyyy").format(new Date(acc.dateAdded)), acX + 24, acY + 14, ColorUtils.rgba(130, 130, 130, (int)(255 * fAlpha)));
            int pCol = acc.pinned ? ColorUtils.rgba(255, 200, 50, (int)(255 * fAlpha)) : ColorUtils.rgba(60, 60, 60, (int)(255 * fAlpha));
            Fonts.icon2[22].drawCenteredString(matrixStack, "p", acX + 91, acY + 9, pCol);
        }
        StencilUtils.uninitStencilBuffer();
        mc.gameRenderer.setupOverlayRendering();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (exiting) return false;
        float pX = mc.getMainWindow().scaledWidth() / 2f - 172f;
        float pY = mc.getMainWindow().scaledHeight() / 2f - 45f;
        float lX = pX + 124f;
        float lY = mc.getMainWindow().scaledHeight() / 2f - 80f;

        if (RenderUtils.isInRegion(mouseX, mouseY, lX + 8, lY + 6.5f, 204, 16)) { searching = !searching; typing = typingPass = false; return true; }
        if (RenderUtils.isInRegion(mouseX, mouseY, pX + 10, pY + 25, 100, 16)) { typing = !typing; typingPass = searching = false; return true; }
        if (RenderUtils.isInRegion(mouseX, mouseY, pX + 10, pY + 43, 100, 16)) { typingPass = !typingPass; typing = searching = false; return true; }

        if (button == 0) {
            if (RenderUtils.isInRegion(mouseX, mouseY, pX + 103, pY + 6, 12, 12)) { exitMenu(); return true; }
            float bX = pX + 35; float bY = pY + 65;
            if (RenderUtils.isInRegion(mouseX, mouseY, bX, bY, 14, 14)) MicrosoftAuth.login();
            if (RenderUtils.isInRegion(mouseX, mouseY, bX + 18, bY, 14, 14)) addAccountWithAnim(new Account(RandomStringUtils.randomAlphabetic(8)));
            if (RenderUtils.isInRegion(mouseX, mouseY, bX + 36, bY, 14, 14) && !altName.isEmpty()) { addAccountWithAnim(new Account(altName)); altName = altPass = ""; }
        }

        List<Account> filtered = getFilteredAccounts();
        for (int i = 0; i < filtered.size(); i++) {
            Account acc = filtered.get(i);
            float acX = lX + 8 + (i % 2) * 102;
            float acY = lY + 28 + (i / 2) * 26 + scrollAn;
            if (RenderUtils.isInRegion(mouseX, mouseY, acX + 85, acY + 3, 12, 12)) { acc.pinned = !acc.pinned; AltConfig.updateFile(); return true; }
            if (RenderUtils.isInRegion(mouseX, mouseY, acX, acY, 100, 24)) {
                if (button == 0) mc.session = new Session(acc.accountName, "", "", "mojang");
                else if (button == 1) { if (!pendingRemoval.contains(acc)) pendingRemoval.add(acc); AltConfig.updateFile(); }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW_KEY_ESCAPE) {
            exitMenu();
            return true;
        }

        if (exiting) return false;

        if (searching) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) searching = false;
        }

        else if (typing || typingPass) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (typing && !altName.isEmpty()) altName = altName.substring(0, altName.length() - 1);
                if (typingPass && !altPass.isEmpty()) altPass = altPass.substring(0, altPass.length() - 1);
            }

            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                if (!altName.isEmpty()) {
                    addAccountWithAnim(new Account(altName));
                    altName = "";
                    altPass = "";
                }
                typing = false;
                typingPass = false;
            }

            if (keyCode == GLFW.GLFW_KEY_TAB) {
                typing = !typing;
                typingPass = !typingPass;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override public boolean charTyped(char codePoint, int modifiers) {
        if (exiting) return false;
        if (searching) searchQuery += codePoint;
        else if (typing) altName += codePoint;
        else if (typingPass) altPass += codePoint;
        return super.charTyped(codePoint, modifiers);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float totalH = (float) Math.ceil(getFilteredAccounts().size() / 2.0) * 26f;
        float maxScroll = totalH > 130 ? -(totalH - 130) : 0;
        scroll = MathHelper.clamp(scroll + (float)delta * 30f, maxScroll, 0);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
