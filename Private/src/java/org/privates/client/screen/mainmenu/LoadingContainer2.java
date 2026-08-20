package org.privates.client.screen.mainmenu;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Namespaced;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.gif.GifRender;

public class LoadingContainer2 extends Screen {
    final Animation alpha = new Animation();
    GifRender gifRender;
    final Animation gifAnim = new Animation();
    final String gifName;
    final String message;
    final long startTime;

    public LoadingContainer2(String gifName, String message) {
        super(StringTextComponent.EMPTY);
        this.gifName = gifName;
        this.message = message;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        alpha.set(0F);
        alpha.run(1, 0.45, Easings.EXPO_OUT);
        gifAnim.set(0F);
        gifAnim.run(1, 0.65, Easings.EXPO_OUT);
        try {
            if (gifRender == null) {
                gifRender = new GifRender(new Namespaced("texture/" + gifName + ".gif"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        alpha.update();
        gifAnim.update();
        int w = width, h = height;
        int bg = ColorUtil.getColor(5, 5, 8, (int) (255 * alpha.get()));
        RenderUtil.Rounded.roundedRect(matrix, 0, 0, w, h, bg, Round.of(0));
        float gifSize = Math.min(w, h) * 0.18f;
        float gifX = w / 2f - gifSize / 2f;
        float gifY = h / 2f - gifSize / 2f - 32f;
        if (gifRender != null) {
            matrix.push();
            matrix.translate(gifX + gifSize / 2f, gifY + gifSize / 2f, 0);
            float anim = gifAnim.get();
            matrix.scale(anim, anim, 1);
            matrix.translate(-gifSize / 2f, -gifSize / 2f, 0);
            gifRender.draw(matrix, 0, 0, gifSize, gifSize, alpha.get(), 15, true);
            matrix.pop();
        }
        Fonts.SF_MEDIUM.drawCenter(matrix, message, w / 2f, h / 2f + gifSize / 2f + 14f, 0xFFE6D3FF, 13.5f);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}