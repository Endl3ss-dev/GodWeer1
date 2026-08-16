package never.lose.utils.render.builders.impl;

import never.lose.utils.render.builders.AbstractBuilder;
import never.lose.utils.render.msdf.MsdfFont;
import never.lose.utils.render.renderers.impl.BuiltText;

import java.awt.Color;

public final class TextBuilder extends AbstractBuilder<BuiltText> {

    private MsdfFont font;
    private String   text;
    private float    size, thickness, smoothness, spacing, outlineThickness;
    private int      color, outlineColor;

    public TextBuilder font(MsdfFont font)           { this.font = font;                     return this; }
    public TextBuilder text(String text)             { this.text = text;                     return this; }
    public TextBuilder size(float size)              { this.size = size;                     return this; }
    public TextBuilder thickness(float t)            { this.thickness = t;                   return this; }
    public TextBuilder color(Color c)                { this.color = c.getRGB();              return this; }
    public TextBuilder color(int c)                  { this.color = c;                       return this; }
    public TextBuilder smoothness(float s)           { this.smoothness = s;                  return this; }
    public TextBuilder spacing(float s)              { this.spacing = s;                     return this; }
    public TextBuilder outline(Color c, float t)     { this.outlineColor = c.getRGB(); this.outlineThickness = t; return this; }
    public TextBuilder outline(int c, float t)       { this.outlineColor = c; this.outlineThickness = t;          return this; }

    @Override
    protected BuiltText _build() {
        return new BuiltText(font, text, size, thickness, color, smoothness, spacing, outlineColor, outlineThickness);
    }

    @Override
    protected void reset() {
        this.font             = null;
        this.text             = "";
        this.size             = 0;
        this.thickness        = 0.05f;
        this.color            = -1;
        this.smoothness       = 0.5f;
        this.spacing          = 0;
        this.outlineColor     = 0;
        this.outlineThickness = 0;
    }
}