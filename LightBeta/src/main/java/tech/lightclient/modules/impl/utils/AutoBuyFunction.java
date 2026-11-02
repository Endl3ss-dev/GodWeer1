package tech.lightclient.modules.impl.utils;

import tech.lightclient.autobuy.AutoBuyConfig;
import tech.lightclient.autobuy.AutoBuyEngine;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.StringSetting;

/** Public AutoBuy controls. Internal automation parameters stay hidden. */
@FunctionAdd(name = "AutoBuy", alias = "AutoBuy", category = Category.Utilities,
        description = "Покупка предметов на аукционе")
public final class AutoBuyFunction extends Function {
    public AutoBuyFunction() {
        addSettings(
                bool("Автообновление", "autoRefresh", true),
                bool("Мин. цена", "minPriceEnabled", true),
                number("Минимальная цена", "minPrice", "15000"),
                number("Парсинг, %", "parsePercent", "80"),
                bool("AutoSell", "autoSell", false),
                bool("AutoRelist", "autoRelist", false),
                bool("AutoPay", "autoPay", false),
                number("Удерживать баланс", "autoPayKeepBalance", "5000000"),
                number("Интервал, сек", "autoPayIntervalSeconds", "300"),
                string("Ник твинка", "autoPayNick", "")
        );
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // Minecraft pauses the client when the window loses focus if this
        // option is enabled. AutoBuy is driven by client ticks, so keeping
        // that pause enabled would stop the auction automation whenever the
        // game is minimized or another window is focused.
        mc.options.pauseOnLostFocus = false;
        AutoBuyConfig.cfg().enabled = true;
        AutoBuyConfig.cfg().autoParse = true;
        AutoBuyConfig.save();
    }

    @Override
    public void onDisable() {
        AutoBuyConfig.cfg().enabled = false;
        AutoBuyConfig.cfg().autoSell = false;
        AutoBuyConfig.cfg().autoRelist = false;
        AutoBuyConfig.save();
        super.onDisable();
    }

    private BooleanSetting bool(String name, String field, boolean def) {
        return new BooleanSetting(name, def) {
            @Override public Boolean get() { return readBoolean(field, def); }
            @Override public void set(Boolean value) {
                writeBoolean(field, value);
                AutoBuyConfig.save();
                if ("autoSell".equals(field)) {
                    if (value) {
                        // AutoSell is also useful for items already in the
                        // inventory; do not wait for the next purchase.
                        AutoBuyEngine.INSTANCE.seller.trigger();
                    } else {
                        AutoBuyEngine.INSTANCE.seller.stop();
                    }
                }
            }
        };
    }

    private StringSetting number(String name, String field, String def) {
        return new StringSetting(name, def) {
            @Override public String get() { return readString(field, def); }
            @Override public void set(String value) {
                writeNumber(field, value);
                AutoBuyConfig.save();
            }
        };
    }

    private StringSetting string(String name, String field, String def) {
        return new StringSetting(name, def) {
            @Override public String get() { return readString(field, def); }
            @Override public void set(String value) {
                writeString(field, value);
                AutoBuyConfig.save();
            }
        };
    }

    private boolean readBoolean(String field, boolean def) {
        try {
            return AutoBuyConfig.class.getField(field).getBoolean(AutoBuyConfig.cfg());
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            return def;
        }
    }

    private String readString(String field, String def) {
        try {
            Object value = AutoBuyConfig.class.getField(field).get(AutoBuyConfig.cfg());
            return value == null ? def : String.valueOf(value);
        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            return def;
        }
    }

    private void writeBoolean(String field, boolean value) {
        try {
            AutoBuyConfig.class.getField(field).setBoolean(AutoBuyConfig.cfg(), value);
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) {
        }
    }

    private void writeNumber(String field, String value) {
        String normalized = value == null ? "" : value.replaceAll("[^0-9-]", "");
        if (normalized.isEmpty() || normalized.equals("-")) return;
        try {
            var f = AutoBuyConfig.class.getField(field);
            if (f.getType() == int.class) {
                f.setInt(AutoBuyConfig.cfg(), Integer.parseInt(normalized));
            } else {
                f.setLong(AutoBuyConfig.cfg(), Long.parseLong(normalized));
            }
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) {
        }
    }

    private void writeString(String field, String value) {
        try {
            AutoBuyConfig.class.getField(field).set(AutoBuyConfig.cfg(), value == null ? "" : value);
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) {
        }
    }
}
