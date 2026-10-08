package never.lose.utils.render.builders.impl;

import never.lose.utils.render.builders.AbstractBuilder;
import never.lose.utils.render.builders.states.QuadColorState;
import never.lose.utils.render.builders.states.QuadRadiusState;
import never.lose.utils.render.builders.states.SizeState;
import never.lose.utils.render.renderers.impl.BuiltCircle;
import never.lose.utils.color.ClientColors;

import java.awt.Color;

public final class CircleBuilder extends AbstractBuilder<BuiltCircle> {

    float width, height, r, thickness, start, end;
    Color c1, c2;

    public CircleBuilder set(float width, float height, float r, float thickness,
                             float start, float end, Color c1, Color c2) {
        this.width = width; this.height = height; this.r = r;
        this.thickness = thickness; this.start = start; this.end = end;
        this.c1 = c1; this.c2 = c2;
        return this;
    }

    @Override
    protected BuiltCircle _build() {
        return new BuiltCircle(width, height, r, thickness, start, end, c1, c2);
    }

    @Override
    protected void reset() {}
}