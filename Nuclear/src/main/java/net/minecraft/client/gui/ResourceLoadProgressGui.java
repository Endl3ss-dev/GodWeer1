package net.minecraft.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import nuclear.utils.IMinecraft;
import nuclear.utils.font.Fonts;
import nuclear.utils.misc.HudUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.util.PropertiesOrdered;
import nuclear.utils.anim.Direction;
import nuclear.utils.anim.impl.EaseBackIn;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtilka;

import static nuclear.ui.clickgui.Panel.getColorByName;
import static nuclear.ui.clickgui.Panel.selectedColor;
import static nuclear.utils.IMinecraft.mc;
import static nuclear.utils.math.MathUtil.lerp;

public class ResourceLoadProgressGui extends LoadingGui {
    private static final ResourceLocation MOJANG_LOGO_TEXTURE = new ResourceLocation("textures/gui/title/mojangstudios.png");
    private static final int field_238627_b_ = ColorHelper.PackedColor.packColor(255, 239, 50, 61);
    private static final int field_238628_c_ = field_238627_b_ & 16777215;
    private final Minecraft mc;
    private final IAsyncReloader asyncReloader;
    private final Consumer<Optional<Throwable>> completedCallback;
    private final boolean reloading;
    private float progress;
    private float targetProgress;
    private long fadeOutStart = -1L;
    private long fadeInStart = -1L;
    private int colorBackground = field_238628_c_;
    private int colorBar = field_238628_c_;
    private int colorOutline = 16777215;
    private int colorProgress = 16777215;
    private GlBlendState blendState = null;
    private boolean fadeOut = false;
    private float progressiv;

    public ResourceLoadProgressGui(Minecraft p_i225928_1_, IAsyncReloader p_i225928_2_, Consumer<Optional<Throwable>> p_i225928_3_, boolean p_i225928_4_) {
        this.mc = p_i225928_1_;
        this.asyncReloader = p_i225928_2_;
        this.completedCallback = p_i225928_3_;
        this.reloading = false;
        this.progress = 0.0F;
        this.targetProgress = 0.0F;
    }

    public static void loadLogoTexture(Minecraft mc) {
        mc.getTextureManager().loadTexture(MOJANG_LOGO_TEXTURE, new MojangLogoTexture());
    }

    public EaseBackIn animation = new EaseBackIn(1250, 1, 2F, Direction.FORWARDS);

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int width = this.mc.getMainWindow().getScaledWidth();
        int height = this.mc.getMainWindow().getScaledHeight();
        long k = Util.milliTime();

        if (this.reloading && (this.asyncReloader.asyncPartDone() || this.mc.currentScreen != null) && this.fadeInStart == -1L) {
            this.fadeInStart = k;
        }

        float f = this.fadeOutStart > -1L ? (float) (k - this.fadeOutStart) / 1000F : -1.0F;
        float f1 = this.fadeInStart > -1L ? (float) (k - this.fadeInStart) / 500.0F : -1.0F;
        float f2 = 1;

        if (f >= 1.0F) {
            this.fadeOut = true;

            animation.setDirection(Direction.BACKWARDS);

            if (this.mc.currentScreen != null) {
                this.mc.currentScreen.render(matrixStack, 0, 0, partialTicks);
            }
            int l = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            RenderUtilka.Render2D.drawRect(0, 0, width, height, ColorUtils.rgba(21, 25, 32, l));
            f2 = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);

        } else if (this.reloading) {
            if (this.mc.currentScreen != null && f1 < 1.0F) {
                this.mc.currentScreen.render(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
        int firstColor2 = selectedColor;
        int textColor = getColorByName("textColor");
        RenderUtilka.Render2D.drawImage(new ResourceLocation("nuclear/images/background.png"), 0, 0,
                mc.getMainWindow().scaledWidth(), mc.getMainWindow().scaledHeight(),
                -1);

        RenderUtilka.Render2D.drawImage(new ResourceLocation("nuclear/images/logo2.png"), (mc.getMainWindow().scaledWidth() / 2f) - 75, IMinecraft.mc.getMainWindow().scaledHeight() / 15f - 30, 150, 150, -1);
        int targetPING = (int) (progress * 100);
        progressiv = lerp(progressiv, targetPING, 0.055f);
        String prog = (int) progressiv + "%";

        String loadingMessage;
        if (progress < 0.2f) {
            loadingMessage = "Loading libraries...";
        } else if (progress < 0.6f) {
            loadingMessage = "Loading native...";
        } else if (progress < 0.8f) {
            loadingMessage = "Loading assets...";
        } else if (progress < 0.96f) {
            loadingMessage = "Loading configurations...";
        } else {
            loadingMessage = "Finalizing resources...";
        }

        Fonts.intl[13].drawString(matrixStack, prog, (width / 2f) + 109 - (Fonts.intl[13].getWidth(prog)), height / 2f - 6, ColorUtils.setAlpha(textColor, 184));

        Fonts.intl[13].drawString(matrixStack, loadingMessage, (width / 2f) - 109, height / 2f - 6, ColorUtils.setAlpha(textColor, 184));

        RenderUtilka.Render2D.drawRoundedRect(width / 2f - 110f, height / 2f + 4, 220, 1f, 0.75F, ColorUtils.rgba(255, 255, 255, 10));
        RenderUtilka.Render2D.drawRoundedRect(width / 2f - 110f, height / 2f + 4, 220 * progress, 1f, 0.75F, ColorUtils.setAlpha(firstColor2, 245));

        float f3 = this.asyncReloader.estimateExecutionSpeed();
        this.targetProgress = MathHelper.clamp(f3, 0.0F, 1.0F);

        this.progress = MathHelper.lerp(0.1F, this.progress, this.targetProgress);

        Reflector.ClientModLoader_renderProgressText.call();

        if (f >= 2.0F) {
            this.mc.setLoadingGui((LoadingGui) null);
        }

        if (this.fadeOutStart == -1L && this.asyncReloader.fullyDone() && (!this.reloading || f1 >= 2.0F)) {
            this.fadeOutStart = Util.milliTime();
            this.progress = 1.0F;

            try {
                this.asyncReloader.join();
                this.completedCallback.accept(Optional.empty());
            } catch (Throwable throwable) {
                this.completedCallback.accept(Optional.of(throwable));
            }

            if (this.mc.currentScreen != null) {
                this.mc.currentScreen.init(this.mc, this.mc.getMainWindow().getScaledWidth(), this.mc.getMainWindow().getScaledHeight());
            }
        }
    }

    public boolean isPauseScreen() {
        return true;
    }

    public void update() {
        this.colorBackground = field_238628_c_;
        this.colorBar = field_238628_c_;
        this.colorOutline = 16777215;
        this.colorProgress = 16777215;

        if (Config.isCustomColors()) {
            try {
                String s = "optifine/color.properties";
                ResourceLocation resourcelocation = new ResourceLocation(s);

                if (!Config.hasResource(resourcelocation)) {
                    return;
                }

                InputStream inputstream = Config.getResourceStream(resourcelocation);
                Config.dbg("Loading " + s);
                Properties properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                this.colorBackground = readColor(properties, "screen.loading", this.colorBackground);
                this.colorOutline = readColor(properties, "screen.loading.outline", this.colorOutline);
                this.colorBar = readColor(properties, "screen.loading.bar", this.colorBar);
                this.colorProgress = readColor(properties, "screen.loading.progress", this.colorProgress);
                this.blendState = ShaderPackParser.parseBlendState(properties.getProperty("screen.loading.blend"));
            } catch (Exception exception) {
                Config.warn("" + exception.getClass().getName() + ": " + exception.getMessage());
            }
        }
    }

    private static int readColor(Properties p_readColor_0_, String p_readColor_1_, int p_readColor_2_) {
        String s = p_readColor_0_.getProperty(p_readColor_1_);

        if (s == null) {
            return p_readColor_2_;
        } else {
            s = s.trim();
            int i = parseColor(s, p_readColor_2_);

            if (i < 0) {
                Config.warn("Invalid color: " + p_readColor_1_ + " = " + s);
                return i;
            } else {
                Config.dbg(p_readColor_1_ + " = " + s);
                return i;
            }
        }
    }

    private static int parseColor(String p_parseColor_0_, int p_parseColor_1_) {
        if (p_parseColor_0_ == null) {
            return p_parseColor_1_;
        } else {
            p_parseColor_0_ = p_parseColor_0_.trim();

            try {
                return Integer.parseInt(p_parseColor_0_, 16) & 16777215;
            } catch (NumberFormatException numberformatexception) {
                return p_parseColor_1_;
            }
        }
    }

    public boolean isFadeOut() {
        return this.fadeOut;
    }

    static class MojangLogoTexture extends SimpleTexture {
        public MojangLogoTexture() {
            super(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE);
        }

        protected TextureData getTextureData(IResourceManager resourceManager) {
            Minecraft minecraft = Minecraft.getInstance();
            VanillaPack vanillapack = minecraft.getPackFinder().getVanillaPack();

            try (InputStream inputstream = getLogoInputStream(resourceManager, vanillapack)) {
                return new TextureData(new TextureMetadataSection(true, true), NativeImage.read(inputstream));
            } catch (IOException ioexception1) {
                return new TextureData(ioexception1);
            }
        }

        private static InputStream getLogoInputStream(IResourceManager p_getLogoInputStream_0_, VanillaPack p_getLogoInputStream_1_) throws IOException {
            return p_getLogoInputStream_0_.hasResource(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE) ? p_getLogoInputStream_0_.getResource(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE).getInputStream() : p_getLogoInputStream_1_.getResourceStream(ResourcePackType.CLIENT_RESOURCES, ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE);
        }
    }
}