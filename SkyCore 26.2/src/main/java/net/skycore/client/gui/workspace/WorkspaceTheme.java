package net.skycore.client.gui.workspace;

import net.skycore.client.gui.menu.MenuDraw;
import net.skycore.client.gui.menu.MenuPalette;

/**
 * Цветовые токены меню Workspace.
 *
 * <p>Значения берутся из общей палитры клиента ({@link MenuPalette}) — так Workspace
 * автоматически поддерживает все двадцать палитр, включая светлую, и плавный переход
 * цвета при смене темы, не дублируя таблицу цветов. Собственные константы здесь только
 * там, где нужен оттенок, которого в палитре нет (подложки, тени, дорожки контролов):
 * они выражены через токены палитры, а не заданы «магическими» числами.
 *
 * <p>Класс не зависит от Minecraft: {@code MenuPalette} — чистая целочисленная
 * математика над ARGB.
 */
public final class WorkspaceTheme {

    public boolean light;
    public int window;
    public int sidebar;
    public int header;
    public int card;
    public int cardHover;
    public int cardActive;
    public int inset;
    public int insetHover;
    public int border;
    public int borderStrong;
    public int text;
    public int muted;
    public int dim;
    public int accent;
    public int accent2;
    public int onAccent;
    public int track;
    public int knob;
    public int shadow;
    public int backdrop;
    public int danger;

    public WorkspaceTheme() {
        sync();
    }

    /** Пересчитывает токены из текущей палитры. Вызывается в начале кадра. */
    public void sync() {
        this.light = MenuPalette.p().light();
        this.text = MenuPalette.text();
        this.muted = MenuPalette.muted();
        this.dim = MenuPalette.dim();
        this.accent = MenuPalette.accent() | 0xFF000000;
        this.accent2 = MenuPalette.accent2() | 0xFF000000;
        this.onAccent = MenuDraw.onAccent(this.accent);
        this.danger = 0xFFFF5C72;

        int bg = MenuPalette.bg();
        this.backdrop = MenuDraw.a(0x05040A, 150);
        this.shadow = MenuDraw.a(0, this.light ? 40 : 170);

        if (this.light) {
            this.window = 0xF7F7F8FB;
            this.sidebar = 0xFFFFFFFF;
            this.header = 0xFFFFFFFF;
            this.card = 0xFFF1F1F5;
            this.cardHover = 0xFFE9E9F0;
            this.cardActive = MenuDraw.mix(0xFFF1F1F5, this.accent, 0.14F) | 0xFF000000;
            this.inset = 0xFFEFEFF4;
            this.insetHover = 0xFFE3E3EB;
            this.border = MenuDraw.a(0x1B1530, 26);
            this.borderStrong = MenuDraw.a(this.accent, 120);
            this.track = 0xFFD9D9E2;
            this.knob = 0xFFFFFFFF;
        } else {
            this.window = MenuDraw.a(bg, 246);
            this.sidebar = MenuDraw.a(MenuPalette.p().side(), 236);
            this.header = MenuDraw.a(MenuPalette.p().card(), 200);
            this.card = MenuDraw.a(MenuPalette.p().card(), 150);
            this.cardHover = MenuDraw.a(MenuPalette.p().card(), 220);
            this.cardActive = MenuDraw.mix(MenuPalette.p().card(), this.accent, 0.13F) | 0xE6000000;
            this.inset = MenuDraw.a(0xFFFFFF, 10);
            this.insetHover = MenuDraw.a(0xFFFFFF, 18);
            this.border = MenuDraw.a(0xFFFFFF, 18);
            this.borderStrong = MenuDraw.a(this.accent, 140);
            this.track = MenuDraw.a(0xFFFFFF, 26);
            this.knob = 0xFFF4F6FA;
        }
    }

    /** Подложка строки/контрола с учётом наведения. */
    public int inset(boolean hovered) {
        return hovered ? this.insetHover : this.inset;
    }

    /** Текст, гаснущий к приглушённому при выключенном состоянии. */
    public int stateText(boolean on) {
        return on ? this.text : this.muted;
    }
}
