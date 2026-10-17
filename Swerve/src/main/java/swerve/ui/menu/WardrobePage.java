package swerve.ui.menu;

import swerve.cosmetics.CosmeticCatalogue;
import swerve.cosmetics.CosmeticItem;
import swerve.cosmetics.Wearables;
import swerve.utils.client.NotificationManager;

import java.util.List;

import swerve.ui.menu.MenuText.Weight;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Страница гардероба: крылья, рюкзак, питомец и головной убор.
 *
 * <p>Сетка карточек с иконкой модели; клик по надетой модели снимает её.
 */
final class WardrobePage {

    static final String[] TITLES = {"Крылья", "Рюкзак", "Питомец", "Головной убор"};
    static final String[] ICONS = {"wind", "bag", "paw-print", "wizard-hat"};

    /** Открытый слот гардероба. */
    static int slot;

    private WardrobePage() {
    }

    static void render(MenuScreen menu, GuiGraphics g) {
        List<CosmeticItem> items = CosmeticCatalogue.of(CosmeticCatalogue.SLOTS[slot]);
        int cols = 6;
        int gap = menu.S(10);
        int cw = (menu.cw0 - gap * (cols - 1)) / cols;
        int ch = cw + menu.S(30);
        int total = ((items.size() + cols - 1) / cols) * (ch + gap);
        int sc = Math.round(menu.scrolled(WardrobePage.SCROLL, total, menu.top, menu.bottom));
        g.enableScissor(menu.cx0 - menu.S(12), menu.top, menu.cx0 + menu.cw0 + menu.S(12), menu.bottom);
        g.pose().pushPose();
        Px.translate(g, 0, -sc);
        for (int i = 0; i < items.size(); i++) {
            CosmeticItem it = items.get(i);
            int x = menu.cx0 + (i % cols) * (cw + gap);
            int y = menu.top + menu.S(2) + (i / cols) * (ch + gap);
            if (y - sc > menu.bottom || y + ch - sc < menu.top) {
                continue;
            }
            boolean worn = Wearables.isWorn(slot, it.n());
            float hv = card(menu, g, it.n(), x, y, cw, ch, sc, worn);
            label(menu, g, it.name(), x, y + ch - menu.S(30), cw, worn, hv);
            if (!menu.under()) {
                menu.clipped(x, y, cw, ch, sc, menu.top, menu.bottom, MenuScreen.ACT_COSMETIC, it.n());
            }
        }
        g.pose().popPose();
        g.disableScissor();
        menu.edges(g, menu.cx0 - menu.S(12), menu.cw0 + menu.S(24), menu.top, menu.bottom, sc, menu.baseColour());
        menu.scrollbar(g, menu.cx0 + menu.cw0 + menu.S(14), menu.top, menu.bottom, total, sc, WardrobePage.SCROLL);
    }

    /** Карточка модели: подложка, иконка и галочка у надетой модели. */
    private static float card(MenuScreen menu, GuiGraphics g, int n, int x, int y, int w, int h, float sc, boolean worn) {
        boolean hov = !menu.under() && menu.my >= menu.top && menu.my < menu.bottom
                && menu.hover(x, Math.round(y - sc), w, h);
        if (hov) {
            menu.hot(MenuScreen.HOT_WARDROBE + n);
        }
        int key = MenuAnim.WARDROBE_BASE + (n - 14) * MenuAnim.WARDROBE_STRIDE;
        float hv = menu.an(key, hov ? 1 : 0, 16);
        float wa = menu.tw(key + 1, worn ? 1 : 0, 220);
        boolean lt = menu.light();
        int R = menu.S(9);
        Px.round(g, x, y, w, h, R, Px.mix(menu.plate(), lt ? 0xFFFFFFFF : Px.mix(menu.T().bg() | 0xFF000000, 0xFFFFFFFF, .07f), hv));
        if (wa > .001f) {
            Px.round(g, x, y, w, h, R, Px.a(MenuScreen.acc(), (int) ((lt ? 20 : 32) * wa)));
        }
        Px.outline(g, x, y, w, h, R,
                Px.mix(Px.a(menu.T().text(), (int) ((lt ? 20 : 13) + 14 * hv)), Px.a(MenuScreen.acc(), 230), wa));
        int art = h - menu.S(44);
        int size = Math.min(w - menu.S(20), art);
        size = Math.round(size * (1 + .05f * hv));
        ResourceLocation icon = CosmeticCatalogue.icon(n);
        float k = Px.fade;
        Px.fade = k * wa;
        Px.image(g, icon, x + (w - size) / 2, y + menu.S(10) + (art - size) / 2, size, size, 0xFFFFFFFF);
        Px.fade = k;
        if (wa > .01f) {
            int r = menu.S(10);
            int ccx = x + w - menu.S(12) - r;
            int ccy = y + menu.S(12) + r;
            float o = Px.fade;
            Px.fade = o * wa;
            Px.circle(g, ccx, ccy, r, MenuScreen.acc());
            MenuScreen.glyph(g, "check", ccx - menu.S(6), ccy - menu.S(6), menu.S(12), 0xFFFFFFFF);
            Px.fade = o;
        }
        return hv;
    }

    /** Подпись модели под карточкой. */
    private static void label(MenuScreen menu, GuiGraphics g, String name, int x, int y, int w, boolean worn, float hv) {
        int fs = menu.S(12);
        String s = Px.ellipsize(name, Weight.MEDIUM, fs, w - menu.S(14));
        Px.textCenter(g, s, x + w / 2, y + menu.S(4), Weight.MEDIUM, fs,
                Px.mix(menu.T().text(), MenuScreen.accText(), worn ? 1 : hv * .4f));
        if (worn) {
            Px.circle(g, x + w / 2, y + menu.S(22), menu.S(2), MenuScreen.acc());
        }
    }

    /** Надеть или снять модель. */
    static void toggle(MenuScreen menu, int n) {
        if (n < 0) {
            return;
        }
        if (Wearables.isWorn(slot, n)) {
            Wearables.set(slot, -1);
            NotificationManager.info("Гардероб", "снято");
        } else {
            Wearables.set(slot, n);
            NotificationManager.success("Гардероб", nameOf(n));
        }
        Wearables.send(true);
        menu.content(MenuSound.SELECT);
    }

    private static String nameOf(int n) {
        CosmeticItem it = CosmeticCatalogue.byNumber(n);
        return it == null ? "—" : it.name();
    }

    /** Контекст прокрутки страницы гардероба. */
    static final int SCROLL = 11;
}
