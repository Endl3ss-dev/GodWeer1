package org.privates.client.managers.other.autobuy.item;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.privates.client.utils.chat.ChatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Расширенный предмет для автопокупки с настройками цен и автокостами
 * Базируется на AutoBuyItem но с дополнительным функционалом
 */
@Getter
@Setter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AutoBuyItem {
    protected Item item;
    protected int price;

    // Дополнительные поля
    protected String name;
    protected ItemStack stack;
    protected boolean containsStack;

    // Настройки покупки/продажи
    protected boolean buying = true;
    protected boolean selling = true;
    protected int buyPrice = 100;
    protected int sellPrice = 100;

    // Автоцены
    protected boolean useAutoCost = true;
    protected int buyMarkup = 20;
    protected int sellMarkup = 20;
    protected int count = 15;

    private final List<Integer> minPrices = new ArrayList<>();
    public boolean added;
    public boolean isConstant = false;

    /**
     * Конструктор совместимый с базовым AutoBuyItem
     */
    public AutoBuyItem(Item item, int price) {
        this.item = item;
        this.price = price;
        this.buyPrice = price;
        this.sellPrice = (int) (price * 1.1f); // +10% к продаже по умолчанию
        this.name = item.getName().getString();
    }

    /**
     * Конструктор с дополнительными параметрами
     */
    public AutoBuyItem(Item item, int price, String name) {
        this(item, price);
        this.name = name;
    }

    /**
     * Проверяет, соответствует ли предмет условиям для покупки
     * Базовый метод совместимый с AutoBuyItem
     */
    public boolean matches(ItemStack stack) {
        return stack.getItem() == item;
    }

    /**
     * Проверяет является ли предмет броней
     */
    public boolean isArmor() {
        return this.item instanceof ArmorItem;
    }

    /**
     * Добавляет цену для расчета автоцен
     */
    public void putPrice(int price) {
        if (this.minPrices.size() < this.count) {
            if (!this.added) {
                this.minPrices.add(price);
            }
        } else {
            int sum = 0;

            for (Integer p : this.minPrices) {
                sum += p;
            }

            int average = sum / this.minPrices.size();
            // Фиксированная скидка 20% для покупки
            int buyPriceCalculated = average * 80 / 100;  // 100% - 20% = 80%
            // Фиксированная наценка 6% для продажи (от цены покупки)
            int sellPriceCalculated = buyPriceCalculated * 106 / 100;  // 100% + 6% = 106%

            this.buyPrice = buyPriceCalculated;
            this.sellPrice = sellPriceCalculated;
            this.price = buyPriceCalculated; // Обновляем базовую цену
            this.minPrices.clear();

            ChatUtil.addText("Конечная цена предмета \"" + this.name + "\": " + buyPriceCalculated);
            ChatUtil.addText("Конечная цена продажи предмета \"" + this.name + "\": " + sellPriceCalculated);
        }
    }

    /**
     * Получает цену покупки как float (совместимость с NeverBuy)
     */
    public float getBuyPrice() {
        return (float) this.buyPrice;
    }

    /**
     * Получает цену продажи как float (совместимость с NeverBuy)
     */
    public float getSellPrice() {
        return (float) this.sellPrice;
    }

    /**
     * Проверяет включена ли покупка
     */
    public boolean isBuy() {
        return this.buying;
    }

    /**
     * Проверяет включена ли продажа
     */
    public boolean isSell() {
        return this.selling;
    }

    /**
     * Проверяет используются ли автоцены
     */
    public boolean isUseAutoCost() {
        return this.useAutoCost;
    }

    /**
     * Получает количество для автоцен
     */
    public int getCountInt() {
        return this.count;
    }

    /**
     * Получает название предмета
     */
    public String getName() {
        return this.name != null ? this.name : (this.item != null ? this.item.getName().getString() : "Unknown");
    }

    /**
     * Получает Item (совместимость с базовым AutoBuyItem)
     */
    public Item getItem() {
        return this.item;
    }

    /**
     * Получает цену (совместимость с базовым AutoBuyItem)
     */
    public int getPrice() {
        return this.price;
    }

    /**
     * Устанавливает цену (совместимость с базовым AutoBuyItem)
     */
    public void setPrice(int price) {
        this.price = price;
        this.buyPrice = price;
    }

    /**
     * Получает ItemStack
     */
    public ItemStack getStack() {
        return this.stack;
    }

    /**
     * Проверяет содержит ли stack
     */
    public boolean isContainsStack() {
        return this.containsStack;
    }

    /**
     * Получает список минимальных цен
     */
    public List<Integer> getMinPrices() {
        return this.minPrices;
    }

    /**
     * Проверяет добавлен ли предмет
     */
    public boolean isAdded() {
        return this.added;
    }

    /**
     * Проверяет является ли предмет константным
     */
    public boolean isConstant() {
        return this.isConstant;
    }

    /**
     * Устанавливает цену покупки
     */
    public void setBuyPrice(int price) {
        this.buyPrice = price;
        this.price = price; // Синхронизируем с базовой ценой
    }

    /**
     * Устанавливает цену продажи
     */
    public void setSellPrice(int price) {
        this.sellPrice = price;
    }

    /**
     * Устанавливает использование автоцен
     */
    public void setUseAutoCost(boolean useAutoCost) {
        this.useAutoCost = useAutoCost;
    }

    /**
     * Устанавливает количество для автоцен
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Очищает список минимальных цен
     */
    public void clearMinPrices() {
        this.minPrices.clear();
    }

    /**
     * Добавляет минимальную цену в список
     */
    public void addMinPrice(int price) {
        if (this.minPrices.size() < this.count) {
            this.minPrices.add(price);
        }
    }

    /**
     * Проверяет готов ли предмет для расчета автоцен
     */
    public boolean isReadyForAutoCost() {
        return this.minPrices.size() >= this.count;
    }

    /**
     * Получает среднюю цену из собранных минимальных цен
     */
    public int getAveragePrice() {
        if (this.minPrices.isEmpty()) {
            return 0;
        }

        int sum = 0;
        for (Integer price : this.minPrices) {
            sum += price;
        }

        return sum / this.minPrices.size();
    }

    /**
     * Рассчитывает оптимальную цену покупки
     */
    public int calculateOptimalBuyPrice() {
        int average = getAveragePrice();
        if (average <= 0) {
            return this.buyPrice;
        }

        // Скидка 20% от средней цены
        return average * 80 / 100;
    }

    /**
     * Рассчитывает оптимальную цену продажи
     */
    public int calculateOptimalSellPrice() {
        int buyPrice = calculateOptimalBuyPrice();
        if (buyPrice <= 0) {
            return this.sellPrice;
        }

        // Наценка 6% от цены покупки
        return buyPrice * 106 / 100;
    }

    /**
     * Обновляет цены на основе собранных данных
     */
    public void updatePricesFromData() {
        if (isReadyForAutoCost() && useAutoCost) {
            int newBuyPrice = calculateOptimalBuyPrice();
            int newSellPrice = calculateOptimalSellPrice();

            setBuyPrice(newBuyPrice);
            setSellPrice(newSellPrice);

            ChatUtil.addText("§6[AutoCost] §fОбновлены цены для §e" + getName());
            ChatUtil.addText("§6[AutoCost] §fПокупка: §a" + newBuyPrice + "¤ §f| Продажа: §b" + newSellPrice + "¤");

            clearMinPrices();
        }
    }

    /**
     * Устанавливает покупку
     */
    public void buying(boolean buying) {
        this.buying = buying;
    }

    /**
     * Устанавливает продажу
     */
    public void selling(boolean selling) {
        this.selling = selling;
    }

    /**
     * Устанавливает название
     */
    public AutoBuyItem name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Устанавливает предмет
     */
    public AutoBuyItem item(Item item) {
        this.item = item;
        return this;
    }

    /**
     * Устанавливает цену (билдер стиль)
     */
    public AutoBuyItem price(int price) {
        setPrice(price);
        return this;
    }

    /**
     * Устанавливает stack
     */
    public AutoBuyItem stack(ItemStack stack) {
        this.stack = stack;
        this.containsStack = stack != null && !stack.isEmpty();
        return this;
    }

    /**
     * Устанавливает цену продажи (билдер стиль)
     */
    public AutoBuyItem sell(int price) {
        setSellPrice(price);
        return this;
    }

    @Override
    public String toString() {
        return "AutoBuyItemMix{" +
                "name='" + name + '\'' +
                ", item=" + (item != null ? item.getName().getString() : "null") +
                ", price=" + price +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", buying=" + buying +
                ", selling=" + selling +
                '}';
    }
}