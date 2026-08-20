/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.text.CharacterVisitor
 *  net.minecraft.text.Style
 *  org.joml.Matrix4f
 */
package minced.core;

import minced.core.FontRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import org.joml.Matrix4f;

final class TextVertexBuilder
implements CharacterVisitor {
    FontRenderer arg$1;
    int arg$2;
    int arg$3;
    float arg$4;
    float[] arg$5;
    BufferBuilder arg$6;
    Matrix4f arg$7;
    float arg$8;

    private TextVertexBuilder(FontRenderer param1, int param2, int param3, float param4, float[] param5, BufferBuilder param6, Matrix4f param7, float param8) {
        this.arg$1 = param1;
        this.arg$2 = param2;
        this.arg$3 = param3;
        this.arg$4 = param4;
        this.arg$5 = param5;
        this.arg$6 = param6;
        this.arg$7 = param7;
        this.arg$8 = param8;
    }

    public boolean accept(int param1, Style param2, int param3) {
        return true;
    }
}

