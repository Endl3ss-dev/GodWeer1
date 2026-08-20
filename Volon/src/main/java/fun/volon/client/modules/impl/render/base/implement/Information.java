package fun.volon.client.modules.impl.render.base.implement;

import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.math.MathUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.impl.render.base.HudRectStyle;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

public class Information extends InterfaceProcessing {

    public Information(Draggable draggable) {
        super(draggable);
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        DefaultStyle(eventRender);
        super.onRender(eventRender);
    }

    public void DefaultStyle(EventRender.Default eventRender) {
        float x = draggable.getX(), y = draggable.getY();
        Font font = Fonts.getFont("suisse", 13);
        Font iconFont = Fonts.getFont("icon", 16);

        int colorTheme;
        if (!Volon.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            colorTheme = Volon.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            colorTheme = ColorUtils.getThemeColor();
        }
        HudRectStyle rectStyle = getHudRectStyle();

        int px = (int) Math.floor(mc.player.getX());
        int py = (int) Math.floor(mc.player.getY());
        int pz = (int) Math.floor(mc.player.getZ());

        float height = 24f;
        double bps = MathUtils.calculateBPS();
        String bpsLine = "Bps: " + formatTwoDecimals(bps);
        String xyzLine = "Xyz: " + px + " " + py + " " + pz;
        float totalWidth = Math.max(82f, Math.max(
                12f + iconFont.getWidth("c") + 3f + font.getWidth(bpsLine),
                12f + iconFont.getWidth("x") + 3f + font.getWidth(xyzLine)
        ));

        drawHudBackground(eventRender.getContext().getMatrices(), x, y, totalWidth, height, colorTheme);

        float line1Y = y + 6.0f;
        float line2Y = y + 13.5f;
        int time = (int) ((System.currentTimeMillis() % 2400L) / 2400f * 360f);
        int left = ColorUtils.getThemeColor(time);
        int right = ColorUtils.getThemeColor(time + 140);
        iconFont.draw(eventRender.getContext().getMatrices(), "c", x + 3.25f, line1Y, left);
        iconFont.draw(eventRender.getContext().getMatrices(), "x", x + 3.25f, line2Y, right);
        font.drawGradientStringHorizontal(eventRender.getContext().getMatrices(), bpsLine, x + 13.5f, line1Y, left, right);
        font.drawGradientStringHorizontal(eventRender.getContext().getMatrices(), xyzLine, x + 13.5f, line2Y, right, left);


        draggable.setHeight(height);
        draggable.setWidth(totalWidth);
    }

    public void WaveStyle(EventRender.Default eventRender) {
        float x = draggable.getX(), y = draggable.getY();

        float time = (System.currentTimeMillis() % 2000) / 2000f * 360f;

        int leftTop1 = ColorUtils.getThemeColor((int) time);
        int leftBottom1 = ColorUtils.getThemeColor((int) (time + 30));
        int centerTop1 = ColorUtils.getThemeColor((int) (time + 90));
        int centerBottom1 = ColorUtils.getThemeColor((int) (time + 120));
        int rightTop1 = ColorUtils.getThemeColor((int) (time + 180));
        int rightBottom1 = ColorUtils.getThemeColor((int) (time + 210));

        String title = "info";
        String xText = "x: " + (int) mc.player.getPos().getX();
        String yText = "y: " + (int) mc.player.getPos().getY();
        String zText = "z: " + (int) mc.player.getPos().getZ();
        String bpsText = "bps: " + (int) MathUtils.calculateBPS();

        var font = Fonts.getFont("suisse", 15);

        float xWidth = font.getWidth(xText);
        float yWidth = font.getWidth(yText);
        float zWidth = font.getWidth(zText);
        float bpsWidth = font.getWidth(bpsText);
        float titleWidth = font.getWidth(title);

        float maxCoordWidth = Math.max(Math.max(xWidth, Math.max(yWidth, zWidth)), bpsWidth);

        float padding = 9f;
        float rectWidth = maxCoordWidth + padding;
        float rectHeight = 48;

        rectWidth = Math.max(rectWidth, 35);

        float centerX = x + rectWidth / 2;

        RenderUtils.drawWaveHudPanel(eventRender.getContext().getMatrices(), x, y, rectWidth, rectHeight, ColorUtils.rgba(25, 25, 25, 150),
                3.5f, 0, 10, 10,
                leftTop1, leftBottom1, centerTop1, centerBottom1, rightTop1, rightBottom1);

        float barPadding = 5f;
        RenderUtils.drawWaveHudHeader(eventRender.getContext().getMatrices(), x + barPadding, y + 12, rectWidth - barPadding * 2, 2.5f, 0,
                10, 10, leftTop1, leftBottom1, centerTop1, centerBottom1, rightTop1, rightBottom1);

        font.drawStringWithShadow(eventRender.getContext().getMatrices(), title, centerX - titleWidth / 2, y + 5, -1);
        font.drawStringWithShadow(eventRender.getContext().getMatrices(), xText, x + 4.5f, y + 17f, -1);
        font.drawStringWithShadow(eventRender.getContext().getMatrices(), yText, x + 4.5f, y + 24f, -1);
        font.drawStringWithShadow(eventRender.getContext().getMatrices(), zText, x + 4.5f, y + 31f, -1);
        font.drawStringWithShadow(eventRender.getContext().getMatrices(), bpsText, x + 4.5f, y + 38f, -1);

        draggable.setWidth(rectWidth);
        draggable.setHeight(rectHeight);
    }

    private String formatTwoDecimals(double value) {
        int scaled = (int) Math.round(value * 100.0D);
        int fraction = Math.abs(scaled % 100);
        return (scaled / 100) + "." + (fraction < 10 ? "0" : "") + fraction;
    }
}
