package net.skycore.client.gui.menu;

import net.skycore.client.gui.menu.MenuText.Weight;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.KeyBinds;
import net.skycore.client.setting.Setting;
import net.skycore.client.util.IconGlyphs;

import java.util.List;

/**
 * Окно настроек модуля.
 *
 * <p>Порт {@code swerve.ui.menu.ModulePopup} (Swerve, порт Arcanic Visuals, GPL-3.0).
 * Открывается рядом с карточкой модуля, привязывается к ней тонкой линией и
 * прокручивается, если строк больше, чем помещается.
 *
 * <p>Адаптация под принимающий клиент: заголовок и строка «Клавиша» закреплены,
 * прокручивается только список настроек. В источнике прокручивалось всё окно вместе
 * с заголовком — при десятке настроек название модуля и тумблер уезжали за край, а
 * тумблер при этом оставался точкой клика, «приклеенной» к заголовку.
 */
final class ModulePopup {

    /** Идентификатор наведения кнопки включения в заголовке окна. */
    private static final int HOT_POPUP_TOGGLE = 11_000;

    private final MenuScreen menu;

    ModulePopup(MenuScreen menu) {
        this.menu = menu;
    }

    void modulePopup() {
        float appear = this.menu.an(MenuAnim.POPUP, this.menu.popMod != null ? 1.0F : 0.0F,
                this.menu.popMod != null ? 15.0F : 22.0F);
        if (this.menu.popMod != null) {
            this.menu.popShown = this.menu.popMod;
        } else if (appear < 0.01F) {
            this.menu.popShown = null;
        }

        ClientModule module = this.menu.popShown;
        if (module == null) {
            return;
        }

        float alpha = this.menu.ease(appear);
        boolean live = this.menu.popMod != null && this.menu.pop == null && appear > 0.5F;
        int moduleIndex = this.menu.moduleIndex(module);

        float width = this.menu.S(300);
        float pad = this.menu.S(18);
        float headHeight = this.menu.S(66);
        float keyHeight = this.menu.S(38);
        float inset = this.menu.S(8);

        // ---- вертикальный размер: по числу видимых настроек, но не больше экрана
        float rowsHeight = 0.0F;
        List<Setting<?>> settings = module.getSettings();
        for (Setting<?> setting : settings) {
            if (setting.isVisible()) {
                rowsHeight += this.menu.rowHeight(setting, width - pad * 2.0F);
            }
        }

        float minHeight = headHeight + keyHeight + inset * 2.0F;
        // потолок — и экран, и сама панель: иначе короткий список настроек
        // растягивался на всю высоту экрана
        float maxHeight = Math.max(minHeight,
                Math.min(this.menu.ph, this.menu.screenHeight() - this.menu.S(24)));
        float height = Math.min(maxHeight, minHeight + rowsHeight + this.menu.S(8));

        // ---- горизонтальное положение: сбоку от панели, если есть место
        boolean anchorOnRight = this.menu.ancX + this.menu.ancW * 0.5F > this.menu.px + this.menu.pw * 0.5F;
        float spaceRight = this.menu.screenWidth() - (this.menu.px + this.menu.pw) - this.menu.S(10);
        float spaceLeft = this.menu.px - this.menu.S(10);

        float x;
        boolean outside;
        if (spaceRight >= width) {
            x = this.menu.px + this.menu.pw + this.menu.S(10);
            outside = true;
        } else if (spaceLeft >= width) {
            x = this.menu.px - this.menu.S(10) - width;
            outside = true;
        } else {
            // места сбоку нет — окно ложится поверх панели со стороны карточки
            x = anchorOnRight ? this.menu.px + this.menu.S(10)
                    : this.menu.px + this.menu.pw - width - this.menu.S(10);
            outside = false;
        }
        x = Math.max(this.menu.S(6), Math.min(this.menu.screenWidth() - width - this.menu.S(6), x));

        float y = Math.max(
                Math.max(this.menu.S(12), this.menu.py),
                Math.min(Math.min(this.menu.screenHeight() - this.menu.S(12), this.menu.py + this.menu.ph) - height,
                        this.menu.ancY - this.menu.S(6)));

        this.menu.pmX = x;
        this.menu.pmY = y;
        this.menu.pmW = width;
        this.menu.pmH = height;

        float keep = MenuDraw.fade;
        MenuDraw.fade = keep * Math.min(1.0F, appear * 1.4F);

        // окно выезжает от панели: правое — влево, левое — вправо
        boolean onRight = x > this.menu.px + this.menu.pw * 0.5F;
        float slide = (1.0F - alpha) * this.menu.S(14);
        float drawX = x + (onRight ? -slide : slide);
        float radius = this.menu.S(11);

        MenuDraw.shadow(drawX, y + this.menu.S(5), width, height, radius, this.menu.S(16),
                MenuDraw.a(0, this.menu.light() ? 30 : 80));
        MenuDraw.round(drawX, y, width, height, radius,
                this.menu.light() ? MenuDraw.a(0xFFFFFF, 250) : MenuDraw.a(MenuPalette.bg(), 246));
        MenuDraw.outline(drawX, y, width, height, radius,
                MenuDraw.a(MenuPalette.text(), this.menu.light() ? 22 : 24));
        this.menu.gradHairline(drawX, y, width, radius);

        // тонкая связь с карточкой, к которой окно относится
        int tieColour = MenuDraw.a(MenuPalette.accent(), (int) (120 * alpha));
        if (outside) {
            float tieY = Math.max(y + this.menu.S(14), Math.min(y + height - this.menu.S(14),
                    this.menu.ancY + this.menu.ancH * 0.5F));
            if (drawX > this.menu.px + this.menu.pw) {
                float from = this.menu.px + this.menu.pw;
                MenuDraw.hgrad(from, tieY, drawX - from, Math.max(1.0F, this.menu.S(1)), tieColour, tieColour);
            } else {
                float from = drawX + width;
                MenuDraw.hgrad(from, tieY, Math.max(0.0F, this.menu.px - from), Math.max(1.0F, this.menu.S(1)),
                        tieColour, tieColour);
            }
        }

        // ---- заголовок: название, описание, тумблер модуля
        float contentX = drawX + pad;
        float contentW = width - pad * 2.0F;
        float toggleW = this.menu.S(34);
        float titleX = contentX;
        float titleW = contentW - toggleW - this.menu.S(10);

        MenuDraw.text(MenuDraw.ellipsize(module.getName(), Weight.SEMIBOLD, this.menu.S(13), titleW),
                titleX, y + this.menu.S(12), Weight.SEMIBOLD, this.menu.S(13), MenuPalette.text());
        MenuDraw.text(MenuDraw.ellipsize(module.getDescription(), Weight.MEDIUM, this.menu.S(10), titleW),
                titleX, y + this.menu.S(30), Weight.MEDIUM, this.menu.S(10),
                this.menu.lt(MenuPalette.dim(), 0xFF757D8A));

        float moduleToggleX = drawX + width - pad - toggleW;
        float moduleOn = this.menu.tw(MenuAnim.MODULE_BASE + moduleIndex * MenuAnim.MOD_STRIDE + MenuAnim.MOD_ON,
                module.isEnabled() ? 1.0F : 0.0F, 180);
        boolean toggleHover = live && this.menu.hover(moduleToggleX, y + this.menu.S(10), toggleW,
                this.menu.S(26));
        if (toggleHover) {
            this.menu.hot(HOT_POPUP_TOGGLE);
        }
        this.menu.toggle(moduleToggleX, y + this.menu.S(8), toggleW, this.menu.S(28), moduleOn,
                toggleHover ? 1.0F : 0.0F);
        if (live) {
            this.menu.region(moduleToggleX, y + this.menu.S(8), toggleW, this.menu.S(28),
                    MenuScreen.ACT_POPUP_TOGGLE, moduleIndex);
        }

        float dividerY = y + headHeight - this.menu.S(6);
        MenuDraw.rect(contentX, dividerY, contentW, Math.max(1.0F, this.menu.S(1)),
                this.menu.light() ? MenuDraw.a(MenuPalette.text(), 18) : 0x12FFFFFF);

        // ---- прокручиваемый список настроек
        float viewTop = dividerY + this.menu.S(4);
        float viewBottom = y + height - keyHeight - inset;
        float scrollAmount = 0.0F;
        if (!settings.isEmpty()) {
            scrollAmount = this.menu.scrolled(MenuAnim.POPUP_SCROLL, rowsHeight, viewTop, viewBottom);
            MenuDraw.scissor(drawX + this.menu.S(1), viewTop, width - this.menu.S(2),
                    Math.max(0.0F, viewBottom - viewTop));
            try {
                this.menu.compact = true;
                try {
                    this.menu.settings(contentX, viewTop + this.menu.S(2), contentW, scrollAmount, viewTop,
                            viewBottom, live);
                } finally {
                    this.menu.compact = false;
                }
            } finally {
                MenuDraw.scissorEnd();
            }
            if (rowsHeight > viewBottom - viewTop) {
                this.menu.edges(drawX + this.menu.S(1), width - this.menu.S(2), viewTop, viewBottom, scrollAmount,
                        this.menu.light() ? 0xFFFFFFFF : 0xF40C0D12);
                this.menu.scrollbar(drawX + width - this.menu.S(6), viewTop, viewBottom, rowsHeight, scrollAmount,
                        MenuAnim.POPUP_SCROLL);
            }
        } else {
            MenuDraw.textMid("У модуля нет настроек", contentX, viewTop, viewBottom - viewTop, Weight.MEDIUM,
                    this.menu.S(10), MenuPalette.dim());
        }

        // ---- закреплённая строка бинда модуля
        float keyRowY = y + height - keyHeight - this.menu.S(2);
        MenuDraw.rect(contentX, keyRowY, contentW, Math.max(1.0F, this.menu.S(1)),
                this.menu.light() ? MenuDraw.a(MenuPalette.text(), 16) : 0x10FFFFFF);
        float bindSize = this.menu.S(16);
        float labelX = contentX + bindSize + this.menu.S(8);
        MenuDraw.iconCs(IconGlyphs.BIND, contentX + bindSize * 0.5F, keyRowY + keyHeight * 0.5F, bindSize,
                MenuDraw.a(accTextSafe(), 190));
        MenuDraw.text("Клавиша", labelX, keyRowY + (keyHeight - MenuText.lineHeight(Weight.MEDIUM,
                        this.menu.S(12))) * 0.5F, Weight.MEDIUM, this.menu.S(12), MenuPalette.muted());

        boolean waiting = this.menu.binding == module;
        String keyLabel = waiting ? "Нажми клавишу"
                : module.getKeyBind() >= 0 ? KeyBinds.name(module.getKeyBind()) : "—";
        boolean keyHover = live && this.menu.hover(contentX, keyRowY, contentW, keyHeight);
        if (keyHover) {
            this.menu.hot(MenuScreen.HOT_BIND);
        }
        this.menu.valueBox(keyLabel, contentX + contentW, keyRowY + (keyHeight - this.menu.S(24)) * 0.5F,
                waiting, keyHover);
        if (live) {
            // левая кнопка — назначить клавишу, правая — сбросить
            this.menu.region(contentX, keyRowY, contentW, keyHeight, MenuScreen.ACT_POPUP_KEY,
                    MenuScreen.ACT_POPUP_KEY_RESET, moduleIndex);
        }

        MenuDraw.fade = keep;
    }

    private static int accTextSafe() {
        return MenuScreen.accText();
    }

}
