package never.lose.utils.render.builders.impl;

import never.lose.utils.render.builders.AbstractBuilder;
import never.lose.utils.render.builders.states.QuadColorState;
import never.lose.utils.render.builders.states.QuadRadiusState;
import never.lose.utils.render.builders.states.SizeState;
import never.lose.utils.render.renderers.impl.BuiltSmoke;
import never.lose.utils.color.ClientColors;

import java.awt.Color;

public final class SmokeBuilder extends AbstractBuilder<BuiltSmoke> {

    private SizeState       size;
    private QuadRadiusState radius;
    private Color           color;
    private float           smoothness;
    private float           time;

    public SmokeBuilder size(SizeState size)           { this.size = size;    return this; }
    public SmokeBuilder radius(QuadRadiusState radius) { this.radius = radius; return this; }
    public SmokeBuilder color(Color color)             { this.color = color;   return this; }
    public SmokeBuilder smoothness(float s)            { this.smoothness = s;  return this; }
    public SmokeBuilder time(float t)                  { this.time = t;        return this; }

    @Override
    protected BuiltSmoke _build() {
        return new BuiltSmoke(size, radius, color, smoothness, time);
    }

    @Override
    protected void reset() {
        this.size       = SizeState.NONE;
        this.radius     = QuadRadiusState.NO_ROUND;
        this.color      = ClientColors.TRANSPARENT;
        this.smoothness = 1.0f;
        this.time       = 0;
    }
}