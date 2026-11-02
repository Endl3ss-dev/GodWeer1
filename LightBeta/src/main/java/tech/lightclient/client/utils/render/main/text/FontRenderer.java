package tech.lightclient.client.utils.render.main.text;

public interface FontRenderer {
    void drawText(float x, float y, float size, String text, int rgbaPremul);

    void drawText(float x, float y, float size, String text, int rgbaPremul, float[] transform);

    void drawText(float x, float y, float size, String text, int rgbaPremul, String alignKey);

    void drawText(float x, float y, float size, String text, int rgbaPremul, String alignKey, float[] transform);

    void drawGradientText(float x, float y, float size, String text, int startColor, int endColor);

    void drawGradientText(float x, float y, float size, String text, int startColor, int endColor, String alignKey);

    void drawGradientText(float x, float y, float size, String text, int startColor, int endColor, String alignKey, float[] transform);

    TextRenderer.TextMetrics measureText(String text, float size);
}
