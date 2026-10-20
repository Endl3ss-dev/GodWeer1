package swerve.ui.menu;

import swerve.modules.Module;
import swerve.modules.api.setting.Setting;
import swerve.ui.menu.MenuText.Weight;
import swerve.utils.input.KeyBoardUtils;

import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/**
 * Окно настроек модуля (порт Arcanic Visuals, GPL-3.0).
 *
 * <p>Открывается рядом с карточкой модуля, привязывается к ней тонкой линией и
 * прокручивается, если настроек больше, чем влезает.
 */
final class ModulePopup {

    private final MenuScreen menu;

    ModulePopup(MenuScreen menu) {
        this.menu = menu;
    }

    void modulePopup(GuiGraphics g) {
        float k = menu.an(MenuAnim.POPUP, menu.popMod != null ? 1 : 0, menu.popMod != null ? 15 : 22);
        if (menu.popMod != null) {
            menu.popShown = menu.popMod;
        } else if (k < .01f) {
            menu.popShown = null;
        }
        Module m = menu.popShown;
        if (m == null) {
            return;
        }
        float a = menu.ease(k);
        boolean live = menu.popMod != null && !menu.blocked() && k > .5f;
        int sw = Px.width();
        int sh = Px.height();
        int mi = menu.moduleIndex(m);

        menu.compact = true;
        int w = menu.S(300);
        int pad = menu.S(18);
        int cw = w - pad * 2;
        int head = menu.S(66);
        int inner = head;
        List<Setting> all = m.getSettings();
        for (int i = 0, size = all.size(); i < size; i++) {
            if (all.get(i).visible()) {
                inner += menu.rowH(all.get(i), cw);
            }
        }
        inner += menu.S(8) + menu.S(40) + menu.S(8);
        int h = Math.min(inner, Math.min(menu.ph, sh - menu.S(24)));
        boolean fitsR = menu.px + menu.pw + menu.S(14) + w <= sw - menu.S(10);
        boolean fitsL = menu.px - menu.S(14) - w >= menu.S(10);
        boolean leftColumn = menu.ancX + menu.ancW / 2 < menu.px + menu.pw / 2;
        boolean right = leftColumn ? !fitsL : (fitsR || !fitsL);
        int x = right ? menu.px + menu.pw + menu.S(14) : menu.px - menu.S(14) - w;
        if (x < menu.S(10)) {
            right = true;
            x = Math.min(sw - w - menu.S(10), menu.ancX + menu.ancW + menu.S(10));
        }
        int y = Math.max(
                Math.max(menu.S(12), menu.py),
                Math.min(Math.min(sh - menu.S(12), menu.py + menu.ph) - h, menu.ancY - menu.S(6)));
        menu.pmX = x;
        menu.pmY = y;
        menu.pmW = w;
        menu.pmH = h;
        float kf = Px.fade;
        Px.fade = kf * Math.min(1, k * 1.4f);
        g.pose().pushPose();
        if (a < .999f) {
            Px.translate(g, (1 - a) * (right ? -menu.S(14) : menu.S(14)), 0);
        }
        boolean lt = menu.light();
        int bg = lt ? 0xFFFFFFFF : 0xF40C0D12;
        Px.shadow(g, x, y + menu.S(6), w, h, menu.S(18), Px.a(0, lt ? 30 : 70));
        Px.round(g, x, y, w, h, menu.S(13), bg);
        Px.outline(g, x, y, w, h, menu.S(13), lt ? Px.a(menu.T().text(), 26) : 0x18FFFFFF);
        menu.gradHairline(g, x, y, w, menu.S(13));
        // тонкая связь с карточкой, к которой окно относится
        int tieY = Math.max(y + menu.S(16), Math.min(y + h - menu.S(16), menu.ancY + menu.ancH / 2));
        if (right) {
            int tieW = x - menu.px - menu.pw;
            if (MenuPrefs.gradient) {
                // связь перетекает из акцента во второй цвет — к самому окну
                Px.hgrad(g, menu.px + menu.pw, tieY, tieW, Math.max(1, menu.S(1)),
                        Px.a(MenuTheme.accent(), (int) (120 * a)), Px.a(MenuTheme.accent2(), (int) (120 * a)));
            } else {
                Px.rect(g, menu.px + menu.pw, tieY, tieW, Math.max(1, menu.S(1)),
                        Px.a(MenuTheme.accent(), (int) (120 * a)));
            }
        } else {
            int tieW = menu.px - x - w;
            if (MenuPrefs.gradient) {
                Px.hgrad(g, x + w, tieY, tieW, Math.max(1, menu.S(1)),
                        Px.a(MenuTheme.accent2(), (int) (120 * a)), Px.a(MenuTheme.accent(), (int) (120 * a)));
            } else {
                Px.rect(g, x + w, tieY, tieW, Math.max(1, menu.S(1)), Px.a(MenuTheme.accent(), (int) (120 * a)));
            }
        }
        int t = y + menu.S(3);
        int b = y + h - menu.S(3);
        float sc = Math.round(menu.scrolled(7, inner, y, y + h));
        menu.scrollTotal[7] = inner;
        g.enableScissor(x, t, x + w, b);
        g.pose().pushPose();
        Px.translate(g, 0, -sc);
        int cx = x + pad;
        // заголовок как у карточки модуля: название, описание и тумблер модуля справа
        int tgW = menu.S(34);
        Px.text(g, Px.ellipsize(m.getName(), Weight.SEMIBOLD, menu.S(15), cw - tgW - menu.S(12)),
                cx, y + menu.S(15), Weight.SEMIBOLD, menu.S(15), menu.T().text());
        Px.text(g, Px.ellipsize(m.getDescription(), Weight.MEDIUM, menu.S(11), cw - tgW - menu.S(12)),
                cx, y + menu.S(36), Weight.MEDIUM, menu.S(11), menu.lt(menu.T().dim(), 0xFF757D8A));
        float mOn = menu.tw(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_ON, m.isEnable() ? 1 : 0, 180);
        boolean tgHv = live && menu.hover(cx + cw - tgW, Math.round(y + menu.S(12) - sc), tgW, menu.S(30));
        menu.toggle(g, cx + cw - tgW, y + menu.S(12), tgW, menu.S(30), mOn, tgHv ? 1 : 0);
        if (live) {
            menu.clipped(cx + cw - tgW - menu.S(4), y + menu.S(8), tgW + menu.S(8), menu.S(38), sc, t, b,
                    MenuScreen.ACT_POPUP_TOGGLE, mi);
        }
        Px.rect(g, cx, y + head - menu.S(4), cw, Math.max(1, menu.S(1)), lt ? Px.a(menu.T().text(), 18) : 0x12FFFFFF);
        int cy = menu.settings(g, m, cx, y + head, cw, sc, t, b, live);
        cy += menu.S(8);
        Px.rect(g, cx, cy, cw, Math.max(1, menu.S(1)), Px.a(menu.T().text(), lt ? 20 : 14));
        boolean cap = menu.binding == m;
        int rh = menu.S(40);
        boolean khv = live && menu.my >= t && menu.my < b && menu.hover(cx, Math.round(cy - sc), cw, rh);
        if (khv) {
            menu.hot(MenuScreen.HOT_BIND);
        }
        Px.text(g, "Клавиша", cx, cy + (rh - menu.S(13)) / 2, Weight.MEDIUM, menu.S(13), menu.T().muted());
        String kt = cap ? "Нажми клавишу" : m.getKey() >= 0 ? KeyBoardUtils.getBindName(m.getKey()) : "—";
        menu.valueBox(g, kt, cx + cw, cy + (rh - menu.S(26)) / 2, cap, khv);
        if (live) {
            // левая кнопка — назначить клавишу, правая — сбросить
            menu.clipped(cx, cy, cw, rh, sc, t, b, MenuScreen.ACT_POPUP_KEY, MenuScreen.ACT_POPUP_KEY_RESET, mi);
        }
        g.pose().popPose();
        g.disableScissor();
        if (inner > h) {
            menu.edges(g, x + menu.S(1), w - menu.S(2), t, b, sc, bg);
            menu.scrollbar(g, x + w - menu.S(7), t + menu.S(8), b - menu.S(8), inner, sc, 7);
        }
        g.pose().popPose();
        Px.fade = kf;
        menu.compact = false;
    }
}
