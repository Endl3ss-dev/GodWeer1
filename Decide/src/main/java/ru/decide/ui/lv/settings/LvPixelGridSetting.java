/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.lv.settings;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.settings.impl.PixelGridSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.LvRenderHelper;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.Position;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public class LvPixelGridSetting
extends SettingWidget {
    private static final float TITLE_H = 16.0f;
    private static final float PAD = 4.0f;
    private static final float CELL_MAX = 4.0f;
    private static final float EDGE = 2.0f;
    private final PixelGridSetting backend;
    private boolean painting;
    private boolean erase;

    public LvPixelGridSetting(PixelGridSetting pixelGridSetting) {
        this.backend = pixelGridSetting;
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    @Override
    public float height() {
        return 20.0f + this.gridSize() + 4.0f;
    }

    private float gridSize() {
        return (float)this.backend.size * this.cellSize();
    }

    private float cellSize() {
        return Math.max(2.0f, Math.min(4.0f, (Math.max(40.0f, 170.0f) - 8.0f - 4.0f) / (float)this.backend.size));
    }

    private static int rgba(int n, int n2, int n3, float f) {
        int n4 = Math.max(0, Math.min(255, Math.round(f)));
        return n4 <= 0 ? 0 : new Color(n, n2, n3, n4).getRGB();
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        int[] hover;
        LvRenderHelper.drawName(this.backend.getName(), f, f2, f3 - 60.0f, f4);
        String sizeText = this.backend.size + "x" + this.backend.size;
        float stw = FontsLV.MONTSERRAT_MEDIUM.width(sizeText, 6.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(sizeText, f + f3 - stw, f2 + 5.0f, 6.0f, LvPixelGridSetting.rgba(255, 255, 255, 150.0f * f4));
        if (this.painting && (hover = this.cellAt(f, f2, f3, Position.mouseX(), Position.mouseY())) != null) {
            this.backend.put(hover[0], hover[1], !this.erase);
        }
        float cell = this.cellSize();
        int size = this.backend.size;
        float gridX = f + (f3 - this.gridSize()) * 0.5f;
        float gridY = f2 + 16.0f + 4.0f;
        Draw.rect(gridX - 2.0f, gridY - 2.0f, this.gridSize() + 4.0f, this.gridSize() + 4.0f, LvPixelGridSetting.rgba(0, 0, 0, 70.0f * f4), 2.0f);
        int[] hover2 = this.cellAt(f, f2, f3, Position.mouseX(), Position.mouseY());
        int hoverX = hover2 == null ? -1 : hover2[0];
        int hoverY = hover2 == null ? -1 : hover2[1];
        for (int y = 0; y < size; ++y) {
            for (int x = 0; x < size; ++x) {
                float px = gridX + (float)x * cell;
                float py = gridY + (float)y * cell;
                boolean filled = this.backend.get(x, y);
                if (filled) {
                    Draw.rect(px, py, cell, cell, ThemeManager.accentSoft(220.0f * f4), 0.5f);
                    continue;
                }
                if (x == hoverX && y == hoverY) {
                    Draw.rect(px, py, cell, cell, LvPixelGridSetting.rgba(255, 255, 255, 90.0f * f4), 0.5f);
                    continue;
                }
                Draw.rect(px, py, cell, cell, LvPixelGridSetting.rgba(255, 255, 255, 12.0f * f4), 0.5f);
            }
        }
    }

    @Override
    public boolean isVisible() {
        return this.backend.getVisible().get();
    }

    @Override
    public boolean click(float f, float f2, float f3, float f4, float f5) {
        int[] cell = this.cellAt(f, f2, f3, f4, f5);
        if (cell == null) {
            return false;
        }
        this.erase = this.backend.get(cell[0], cell[1]);
        this.backend.put(cell[0], cell[1], !this.erase);
        this.painting = true;
        GuiSounds.sliderGrab();
        return true;
    }

    @Override
    public void releaseDrag() {
        if (this.painting) {
            this.painting = false;
            GuiSounds.sliderRelease();
        }
    }

    private int[] cellAt(float f, float f2, float f3, float mouseX, float mouseY) {
        int[] nArray;
        int y;
        float cell = this.cellSize();
        int size = this.backend.size;
        float gridX = f + (f3 - this.gridSize()) * 0.5f;
        float gridY = f2 + 16.0f + 4.0f;
        if (mouseX < gridX || mouseX > gridX + this.gridSize() || mouseY < gridY || mouseY > gridY + this.gridSize()) {
            return null;
        }
        int x = (int)Math.floor((mouseX - gridX) / cell);
        if (this.backend.inBounds(x, y = (int)Math.floor((mouseY - gridY) / cell))) {
            int[] nArray2 = new int[2];
            nArray2[0] = x;
            nArray = nArray2;
            nArray2[1] = y;
        } else {
            nArray = null;
        }
        return nArray;
    }

    @Override
    public float preferredWidth() {
        return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + 40.0f + 8.0f;
    }
}

