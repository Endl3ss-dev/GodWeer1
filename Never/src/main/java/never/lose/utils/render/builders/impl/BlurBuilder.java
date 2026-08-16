package never.lose.utils.render.builders.impl;

import never.lose.utils.render.builders.AbstractBuilder;
import never.lose.utils.render.builders.states.QuadColorState;
import never.lose.utils.render.builders.states.QuadRadiusState;
import never.lose.utils.render.builders.states.SizeState;
import never.lose.utils.render.renderers.impl.BuiltBlur;

public final class BlurBuilder extends AbstractBuilder<BuiltBlur> {

    private SizeState       size;
    private QuadRadiusState radius;
    private QuadColorState  color;
    private float           smoothness;
    private float           blurRadius;

    public BlurBuilder size(SizeState size)           { this.size = size;            return this; }
    public BlurBuilder radius(QuadRadiusState radius) { this.radius = radius;         return this; }
    public BlurBuilder color(QuadColorState color)    { this.color = color;           return this; }
    public BlurBuilder smoothness(float s)            { this.smoothness = s;          return this; }
    public BlurBuilder blurRadius(float r)            { this.blurRadius = r;          return this; }

    @Override
    protected BuiltBlur _build() {
        return new BuiltBlur(size, radius, color, smoothness, blurRadius);
    }

    @Override
    protected void reset() {
        this.size       = SizeState.NONE;
        this.radius     = QuadRadiusState.NO_ROUND;
        this.color      = QuadColorState.TRANSPARENT;
        this.smoothness = 1.0f;
        this.blurRadius = 0;
    }
}