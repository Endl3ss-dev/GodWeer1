package swerve.ui.menu;

import swerve.cosmetics.CosmeticCatalogue;
import swerve.cosmetics.CosmeticItem;
import swerve.cosmetics.Wearables;
import swerve.cosmetics.cape.CapeState;
import swerve.cosmetics.cape.Capes;
import swerve.cosmetics.melee.MeleeCatalog;
import swerve.cosmetics.melee.MeleeDesign;
import swerve.cosmetics.melee.MeleeState;
import swerve.utils.client.NotificationManager;

import java.util.List;

import swerve.ui.menu.MenuText.Weight;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * Страница гардероба: крылья, рюкзак, питомец, головной убор и ножи.
 *
 * <p>Сетка карточек с иконкой модели; клик по надетой модели снимает её.
 */
final class WardrobePage {

    static final String[] TITLES = {"Крылья", "Рюкзак", "Питомец", "Головной убор", "Ножи", "Плащи"};
    static final String[] ICONS = {"wind", "bag", "paw-print", "wizard-hat", "sword", "award"};

    /** Вкладка с ножами. */
    static final int MELEE_SLOT = 4;

    /** Вкладка с плащами. */
    static final int CAPE_SLOT = 5;

    /** Открытый слот гардероба. */
    static int slot;

    /** Геометрия ползунка размера ножа (заполняется при отрисовке). */
    static int scaleX;
    static int scaleW;

    private WardrobePage() {
    }

    static void render(MenuScreen menu, GuiGraphics g) {
        if (slot == MELEE_SLOT) {
            renderMelee(menu, g);
            return;
        }
        if (slot == CAPE_SLOT) {
            renderCapes(menu, g);
            return;
        }
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
            float hv = card(menu, g, CosmeticCatalogue.icon(it.n()), x, y, cw, ch, sc, worn,
                    MenuAnim.WARDROBE_BASE + (it.n() - 14) * MenuAnim.WARDROBE_STRIDE, it.n());
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

    // ---- ножи

    /** Ножи: переключатели вида и ползунок размера, под ними сетка скинов. */
    private static void renderMelee(MenuScreen menu, GuiGraphics g) {
        int stripH = menu.S(46);
        meleeStrip(menu, g, menu.cx0, menu.top, menu.cw0, stripH);
        int gridTop = menu.top + stripH + menu.S(12);
        List<MeleeDesign> skins = MeleeCatalog.all();
        int cols = 6;
        int gap = menu.S(10);
        int cw = (menu.cw0 - gap * (cols - 1)) / cols;
        int ch = cw + menu.S(30);
        int total = ((skins.size() + cols - 1) / cols) * (ch + gap);
        int sc = Math.round(menu.scrolled(WardrobePage.SCROLL, total, gridTop, menu.bottom));
        g.enableScissor(menu.cx0 - menu.S(12), gridTop, menu.cx0 + menu.cw0 + menu.S(12), menu.bottom);
        g.pose().pushPose();
        Px.translate(g, 0, -sc);
        for (int i = 0; i < skins.size(); i++) {
            MeleeDesign design = skins.get(i);
            int x = menu.cx0 + (i % cols) * (cw + gap);
            int y = gridTop + (i / cols) * (ch + gap);
            if (y - sc > menu.bottom || y + ch - sc < gridTop) {
                continue;
            }
            boolean worn = design.getKey().equals(MeleeState.equippedKey());
            float hv = card(menu, g, design.getIcon(), x, y, cw, ch, sc, worn,
                    meleeAnimKey(i), 200 + i);
            label(menu, g, design.getDisplayName(), x, y + ch - menu.S(30), cw, worn, hv);
            if (!menu.under()) {
                menu.clipped(x, y, cw, ch, sc, gridTop, menu.bottom, MenuScreen.ACT_MELEE_SKIN, i);
            }
        }
        g.pose().popPose();
        g.disableScissor();
        menu.edges(g, menu.cx0 - menu.S(12), menu.cw0 + menu.S(24), gridTop, menu.bottom, sc, menu.baseColour());
        menu.scrollbar(g, menu.cx0 + menu.cw0 + menu.S(14), gridTop, menu.bottom, total, sc, WardrobePage.SCROLL);
    }

    /** Полоса настроек ножа: вид от первого и третьего лица, размер в третьем. */
    private static void meleeStrip(MenuScreen menu, GuiGraphics g, int x, int y, int w, int h) {
        boolean lt = menu.light();
        int R = menu.S(12);
        Px.round(g, x, y, w, h, R, lt ? 0xF8F8F7FA : Px.a(menu.T().bg() | 0xFF000000, 0xF2));
        Px.outline(g, x, y, w, h, R, lt ? Px.a(menu.T().text(), 22) : 0x18FFFFFF);
        int pad = menu.S(18);
        int rowY = y + (h - menu.S(24)) / 2;
        int fs = menu.S(14);

        // От первого лица
        Px.text(g, "От первого лица", x + pad, rowY, Weight.MEDIUM, fs, menu.T().text());
        int tw = menu.S(44);
        int t1 = x + pad + menu.S(132);
        menu.toggle(g, t1, rowY, tw, menu.S(24), MeleeState.firstPerson() ? 1 : 0, 0);
        if (!menu.under()) {
            menu.region(t1, rowY, tw, menu.S(24), MenuScreen.ACT_MELEE_OPT, 0);
        }

        // От третьего лица
        int t2 = t1 + tw + menu.S(28);
        Px.text(g, "От третьего лица", t2, rowY, Weight.MEDIUM, fs, menu.T().text());
        int t2x = t2 + menu.S(132);
        menu.toggle(g, t2x, rowY, tw, menu.S(24), MeleeState.thirdPerson() ? 1 : 0, 0);
        if (!menu.under()) {
            menu.region(t2x, rowY, tw, menu.S(24), MenuScreen.ACT_MELEE_OPT, 1);
        }

        // Размер в третьем лице
        int right = x + w - pad;
        String value = String.format(java.util.Locale.ROOT, "%.2f×", MeleeState.thirdScale());
        Px.textRight(g, value, right, rowY, Weight.SEMIBOLD, menu.S(13), MenuScreen.accText());
        int slw = menu.S(150);
        int slx = right - menu.S(52) - slw;
        int ty = rowY + menu.S(12);
        Px.round(g, slx, ty - menu.S(2), slw, menu.S(4), menu.S(2), Px.a(menu.T().text(), lt ? 40 : 30));
        float f = (MeleeState.thirdScale() - MeleeState.SCALE_MIN) / (MeleeState.SCALE_MAX - MeleeState.SCALE_MIN);
        f = Math.max(0, Math.min(1, f));
        int knob = menu.S(10);
        int kx = slx + Math.round((slw - knob) * f);
        if (MenuPrefs.gradient) {
            // заполненная часть дорожки — градиент из акцента во второй цвет
            Px.roundGrad(g, slx, ty - menu.S(2), Math.max(menu.S(4), kx + knob / 2 - slx), menu.S(4), menu.S(2),
                    MenuScreen.acc(), MenuScreen.acc2());
        }
        menu.accentFill(g, kx, ty - knob / 2, knob, knob, knob / 2);
        scaleX = slx;
        scaleW = slw;
        if (!menu.under()) {
            menu.region(slx, ty - menu.S(9), slw, menu.S(18), MenuScreen.ACT_MELEE_SCALE, 0);
        }
    }

    // ---- плащи

    /** Плащи: сетка рисунков, клик надевает или снимает плащ. */
    private static void renderCapes(MenuScreen menu, GuiGraphics g) {
        String[] designs = Capes.designs();
        int cols = 4;
        int gap = menu.S(10);
        int cw = (menu.cw0 - gap * (cols - 1)) / cols;
        int ch = cw + menu.S(30);
        int total = ((designs.length + cols - 1) / cols) * (ch + gap);
        int sc = Math.round(menu.scrolled(WardrobePage.SCROLL, total, menu.top, menu.bottom));
        g.enableScissor(menu.cx0 - menu.S(12), menu.top, menu.cx0 + menu.cw0 + menu.S(12), menu.bottom);
        g.pose().pushPose();
        Px.translate(g, 0, -sc);
        for (int i = 0; i < designs.length; i++) {
            String design = designs[i];
            int x = menu.cx0 + (i % cols) * (cw + gap);
            int y = menu.top + menu.S(2) + (i / cols) * (ch + gap);
            if (y - sc > menu.bottom || y + ch - sc < menu.top) {
                continue;
            }
            boolean worn = design.equals(CapeState.design());
            float hv = card(menu, g, Capes.preview(design), x, y, cw, ch, sc, worn,
                    capeAnimKey(i), 300 + i);
            label(menu, g, design, x, y + ch - menu.S(30), cw, worn, hv);
            if (!menu.under()) {
                menu.clipped(x, y, cw, ch, sc, menu.top, menu.bottom, MenuScreen.ACT_CAPE, i);
            }
        }
        g.pose().popPose();
        g.disableScissor();
        menu.edges(g, menu.cx0 - menu.S(12), menu.cw0 + menu.S(24), menu.top, menu.bottom, sc, menu.baseColour());
        menu.scrollbar(g, menu.cx0 + menu.cw0 + menu.S(14), menu.top, menu.bottom, total, sc, WardrobePage.SCROLL);
    }

    /** Ключ анимации карточки ножа. */
    private static int meleeAnimKey(int index) {
        return MenuAnim.WARDROBE_BASE + 104 + index * MenuAnim.WARDROBE_STRIDE;
    }

    /** Ключ анимации карточки плаща. */
    private static int capeAnimKey(int index) {
        return MenuAnim.WARDROBE_BASE + 176 + index * MenuAnim.WARDROBE_STRIDE;
    }

    /** Карточка модели: подложка, иконка и галочка у надетой модели. */
    private static float card(MenuScreen menu, GuiGraphics g, ResourceLocation icon, int x, int y, int w, int h,
                              float sc, boolean worn, int animKey, int hotKey) {
        boolean hov = !menu.under() && menu.my >= menu.top && menu.my < menu.bottom
                && menu.hover(x, Math.round(y - sc), w, h);
        if (hov) {
            menu.hot(MenuScreen.HOT_WARDROBE + hotKey);
        }
        float hv = menu.an(animKey, hov ? 1 : 0, 16);
        float wa = menu.tw(animKey + 1, worn ? 1 : 0, 220);
        boolean lt = menu.light();
        int R = menu.S(9);
        Px.round(g, x, y, w, h, R, Px.mix(menu.plate(), lt ? 0xFFFFFFFF : Px.mix(menu.T().bg() | 0xFF000000, 0xFFFFFFFF, .07f), hv));
        if (wa > .001f) {
            menu.accentWash(g, x, y, w, h, R, (int) ((lt ? 20 : 32) * wa));
        }
        Px.outline(g, x, y, w, h, R,
                Px.mix(Px.a(menu.T().text(), (int) ((lt ? 20 : 13) + 14 * hv)), Px.a(MenuScreen.acc(), 230), wa));
        int art = h - menu.S(44);
        int size = Math.min(w - menu.S(20), art);
        size = Math.round(size * (1 + .05f * hv));
        if (icon != null) {
            float k = Px.fade;
            Px.fade = k * wa;
            Px.image(g, icon, x + (w - size) / 2, y + menu.S(10) + (art - size) / 2, size, size, 0xFFFFFFFF);
            Px.fade = k;
        }
        if (wa > .01f) {
            int r = menu.S(10);
            int ccx = x + w - menu.S(12) - r;
            int ccy = y + menu.S(12) + r;
            float o = Px.fade;
            Px.fade = o * wa;
            menu.accentFill(g, ccx - r, ccy - r, r * 2, r * 2, r);
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

    /** Надеть или снять нож. */
    static void toggleMelee(MenuScreen menu, int index) {
        List<MeleeDesign> skins = MeleeCatalog.all();
        if (index < 0 || index >= skins.size()) {
            return;
        }
        MeleeDesign design = skins.get(index);
        if (design.getKey().equals(MeleeState.equippedKey())) {
            MeleeState.unequip();
            NotificationManager.info("Ножи", "снято");
        } else {
            MeleeState.equip(design.getKey());
            NotificationManager.success("Ножи", design.getDisplayName());
        }
        Wearables.send(true);
        menu.content(MenuSound.SELECT);
    }

    /** Надеть или снять плащ. */
    static void toggleCape(MenuScreen menu, int index) {
        String[] designs = Capes.designs();
        if (index < 0 || index >= designs.length) {
            return;
        }
        String design = designs[index];
        if (design.equals(CapeState.design())) {
            CapeState.unequip();
            NotificationManager.info("Плащи", "снято");
        } else {
            CapeState.design(design);
            NotificationManager.success("Плащи", design);
        }
        Wearables.send(true);
        menu.content(MenuSound.SELECT);
    }

    /** Переключатель вида ножа. */
    static void toggleOption(int option) {
        if (option == 0) {
            MeleeState.firstPerson(!MeleeState.firstPerson());
        } else {
            MeleeState.thirdPerson(!MeleeState.thirdPerson());
        }
    }

    private static String nameOf(int n) {
        CosmeticItem it = CosmeticCatalogue.byNumber(n);
        return it == null ? "—" : it.name();
    }

    /** Контекст прокрутки страницы гардероба. */
    static final int SCROLL = 11;
}
