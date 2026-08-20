package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.annotations.HolyWorld;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.utils.autobuy.*;
import org.privates.client.utils.other.Instance;

import java.util.List;
import java.util.stream.Collectors;

@HolyWorld
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoBuyHW", category = Category.MISC)
public class AutoBuyHW extends Module {
    public static AutoBuyHW getInstance() {
        return Instance.get(AutoBuyHW.class);
    }

    private final BindSetting openGui = new BindSetting(this, "Открыть GUI");

    public static long update;
    private static long clickUptime;
    private static boolean click;
    public static boolean send;

    public static boolean enabled = false;
    private static boolean packet, packetSend;
    public static long idkTime;

    private static HistoryItem lastItem;
    static boolean wasGuiKeyPressed = false;

    public static String autoPayCom = "";
    public static int delayInputValue = 411;
    public static int balance;
    private static long lastAhCheckTime = 0;

    private static long lastMoneyCheck = 0;
    private static ItemStack pendingPurchaseItem = null;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        try {
            if (mc == null || mc.player == null || mc.world == null) return;

            boolean isGuiKeyPressed = GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), openGui.getValue()) == GLFW.GLFW_PRESS;
            if (isGuiKeyPressed && !wasGuiKeyPressed && mc.currentScreen == null) {
                mc.displayScreen(new AutoBuyHWGui());
            }
            wasGuiKeyPressed = isGuiKeyPressed;

            if (mc.currentScreen == null || !enabled) return;

            Screen screen = mc.currentScreen;
            if (screen.getTitle() == null || screen.getTitle().getString() == null) return;

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAhCheckTime >= 7500) {
                mc.player.sendChatMessage("/ah");
                lastAhCheckTime = currentTime;
            }

            boolean ah = screen.getTitle().getString().contains("Аукцион");
            boolean accept = screen.getTitle().getString().contains("Покупка предмета");

            if (ah || accept) {
                if (mc.currentScreen instanceof ChestScreen chestScreen) {
                    if (chestScreen.getContainer() == null || chestScreen.getContainer().getInventory() == null) return;

                    if (accept) {
                        if (click && System.currentTimeMillis() > clickUptime) {
                            click = false;
                        }
                        for (int i = 0; i < chestScreen.getContainer().getInventory().size(); i++) {
                            ItemStack stack = chestScreen.getContainer().getInventory().get(i);
                            if (stack == null) continue;

                            String name = stack.getDisplayName().getString();
                            if (name == null) continue;

                            if (stack.getItem().equals(Items.LIME_STAINED_GLASS_PANE) && name.contains("▶ Купить") && !click) {
                                ItemStack actualItem = chestScreen.getContainer().getInventory().get(13);
                                if (pendingPurchaseItem != null &&
                                        actualItem.getItem() == pendingPurchaseItem.getItem() &&
                                        actualItem.getCount() == pendingPurchaseItem.getCount()) {
                                    click(i, 0);
                                    click = true;
                                    clickUptime = System.currentTimeMillis() + 700L;
                                    if (lastItem != null) {
                                        lastItem.purchased = true;
                                        HistoryManager.add(lastItem);
                                        lastItem = null;
                                    }
                                } else {
                                    for (int j = 0; j < chestScreen.getContainer().getInventory().size(); j++) {
                                        ItemStack cancelStack = chestScreen.getContainer().getInventory().get(j);
                                        if (cancelStack != null &&
                                                cancelStack.getItem().equals(Items.RED_STAINED_GLASS_PANE) &&
                                                cancelStack.getDisplayName().getString().contains("▶ Отменить покупку")) {
                                            click2(j, 2);
                                            pendingPurchaseItem = null;
                                            if (lastItem != null) {
                                                lastItem.purchased = false;
                                                HistoryManager.add(lastItem);
                                                lastItem = null;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        click = false;
                        if (!ah && !accept) {
                            pendingPurchaseItem = null;
                        }
                    }
                    if (ah) {
                        if (mc.player.ticksExisted % 20 == 0) {
                            try {
                                String name = autoPayCom.split(" ")[0];
                                int s = Integer.parseInt(autoPayCom.split(" ")[1]);
                                if (balance - s > 0) {
                                    mc.player.sendChatMessage("/pay " + name + " " + (balance - s));
                                }
                            } catch (Exception ignore) {
                            }
                        }
                        checkMoney();

                        for (int i = 0; i < chestScreen.getContainer().getInventory().size(); i++) {
                            ItemStack stack = chestScreen.getContainer().getInventory().get(i);
                            if (stack == null) continue;

                            String name = stack.getDisplayName().getString();
                            if (name == null) continue;

                            if (name.equals("❏ Товары на продаже ❏") || name.equals("❏ Просроченные товары ❏") || name.equals("Следующая страница ▶")
                                    || name.contains("Помощь по аукциону") || name.equals("Помощь по системе аукциона:") || name.equals("сортировка")
                                    || name.equals("Категории предметорв") || name.equals("◀️ Предыдущая страница")
                                    || name.equals("▶ Этот предмет уже купили!"))
                                continue;

                            List<ITextComponent> info = screen.getTooltipFromItem(stack).stream().filter(text -> text.getString().contains("▍")).toList();
                            List<ITextComponent> sumList = info.stream().filter(text -> text.getString().contains("¤")).collect(Collectors.toList());

                            String author = "";
                            try {
                                author = info.stream()
                                        .filter(text -> text.getString().contains("Продавец"))
                                        .toList()
                                        .get(0)
                                        .getString()
                                        .split(":")[1]
                                        .split("\\(")[0]
                                        .trim();
                            } catch (Exception ignore) {
                            }

                            String authorLower = author.toLowerCase().replace(" ", "");
                            if (authorLower.contains("cat") ||
                                    authorLower.contains("kat") ||
                                    authorLower.contains("777") ||
                                    authorLower.contains("hacker") ||
                                    authorLower.contains("cme") ||
                                    authorLower.contains("may") ||
                                    authorLower.contains("range") ||
                                    authorLower.contains("super") ||
                                    authorLower.contains("666") ||
                                    authorLower.contains("pay") ||
                                    authorLower.contains("cuper") ||
                                    authorLower.contains("bups") ||
                                    authorLower.contains("fuga") ||
                                    authorLower.contains("asya") ||
                                    authorLower.contains("sp4") ||
                                    authorLower.contains("s4e") ||
                                    authorLower.contains("i_love_69") ||
                                    authorLower.contains("yavladik") ||
                                    authorLower.contains("lk_offi") ||
                                    authorLower.contains("yulik") ||
                                    authorLower.contains("nefords") ||
                                    authorLower.contains("bobrixa") ||
                                    authorLower.contains("mr_fydie") ||
                                    authorLower.contains("camapa") ||
                                    authorLower.contains("gromaforz") ||
                                    authorLower.contains("alanova") ||
                                    authorLower.contains("toplechlka") ||
                                    authorLower.contains("klemen") ||
                                    authorLower.contains("that0nebear") ||
                                    authorLower.contains("xish3n") ||
                                    authorLower.contains("hikomori") ||
                                    authorLower.contains("_bupsik_") ||
                                    authorLower.contains("apta_he_umba") ||
                                    authorLower.contains("deth_angel") ||
                                    authorLower.contains("derty_1001") ||
                                    authorLower.contains("yarikpr02205") ||
                                    authorLower.contains("_artyfan4ik_") ||
                                    authorLower.contains("cmeetane") ||
                                    authorLower.contains("bancheat") ||
                                    authorLower.contains("m0untain") ||
                                    authorLower.contains("hapyc0t") ||
                                    authorLower.contains("hackercat777") ||
                                    authorLower.contains("hqd_d") ||
                                    authorLower.contains("fl1ckzzz") ||
                                    authorLower.contains("_senpaicrown_") ||
                                    authorLower.contains("asya_masya") ||
                                    authorLower.contains("dumpcave") ||
                                    authorLower.contains("harukakasugano_") ||
                                    authorLower.contains("killme_plz") ||
                                    authorLower.contains("pupsik_bb") ||
                                    authorLower.contains("skyvolik") ||
                                    authorLower.contains("nor1x") ||
                                    authorLower.contains("_darkdeath_") ||
                                    authorLower.contains("imsolegit_") ||
                                    authorLower.contains("akaza3") ||
                                    authorLower.contains("akasha1") ||
                                    authorLower.contains("a11ways") ||
                                    authorLower.contains("the_destr0yer") ||
                                    authorLower.contains("zxcichert") ||
                                    authorLower.contains("dellarba") ||
                                    authorLower.contains("uliana_pim") ||
                                    authorLower.contains("pr3k0l") ||
                                    authorLower.contains("processhacker") ||
                                    authorLower.contains("egorhack9") ||
                                    authorLower.contains("makson1gg") ||
                                    authorLower.contains("jimenez") ||
                                    authorLower.contains("pjk_k") ||
                                    authorLower.contains("banvhub") ||
                                    authorLower.contains("123qwegg") ||
                                    authorLower.contains("vem225") ||
                                    authorLower.contains("tr1ple") ||
                                    authorLower.contains("yokushka") ||
                                    authorLower.contains("3uma_man") ||
                                    authorLower.contains("darkfunny") ||
                                    authorLower.contains("delphinch1kk") ||
                                    authorLower.contains("voobs") ||
                                    authorLower.contains("hiligho09") ||
                                    authorLower.contains("onesummerday") ||
                                    authorLower.contains("plushkina") ||
                                    authorLower.contains("jaketop4ek") ||
                                    authorLower.contains("jehai") ||
                                    authorLower.contains("howtofindreizoo") ||
                                    authorLower.contains("arves") ||
                                    authorLower.contains("podzemochka") ||
                                    authorLower.contains("vad1mkapro228") ||
                                    authorLower.contains("selfscanning") ||
                                    authorLower.contains("coco_cola4") ||
                                    authorLower.contains("anhedonie") ||
                                    authorLower.contains("shadowx") ||
                                    authorLower.contains("oxonomy1x5") ||
                                    authorLower.contains("jugde") ||
                                    authorLower.contains("realkanster") ||
                                    authorLower.contains("stasyan_325") ||
                                    authorLower.contains("werylatte1") ||
                                    authorLower.contains("arsenii4873") ||
                                    authorLower.contains("fanticx") ||
                                    authorLower.contains("manipulatings") ||
                                    authorLower.contains("mila707") ||
                                    authorLower.contains("_krevetka_ban_") ||
                                    authorLower.contains("iwishyourheart") ||
                                    authorLower.contains("afno") ||
                                    authorLower.contains("friendixgg1337") ||
                                    authorLower.contains("0_am") ||
                                    authorLower.contains("mniiika") ||
                                    authorLower.contains("mass1no") ||
                                    authorLower.contains("rjkjs") ||
                                    authorLower.contains("mvp_mons") ||
                                    authorLower.contains("deniskiller") ||
                                    authorLower.contains("true190") ||
                                    authorLower.contains("_liza_2045_") ||
                                    authorLower.contains("oulimpus") ||
                                    authorLower.contains("jlime") ||
                                    authorLower.contains("nremorse") ||
                                    authorLower.contains("whyyoucryy") ||
                                    authorLower.contains("slanthunter") ||
                                    authorLower.contains("dadafasik") ||
                                    authorLower.contains("91252") ||
                                    authorLower.contains("sxcudd") ||
                                    authorLower.contains("wxzarck_") ||
                                    authorLower.contains("christ") ||
                                    authorLower.contains("love_krolik") ||
                                    authorLower.contains("xddeadforskil") ||
                                    authorLower.contains("ro3o4ka_") ||
                                    authorLower.contains("tomiv") ||
                                    authorLower.contains("eg1pt_winter") ||
                                    authorLower.contains("legitb0b") ||
                                    authorLower.contains("mini0620") ||
                                    authorLower.contains("mils") ||
                                    authorLower.contains("MillerXXD") ||
                                    authorLower.contains("thelast") ||
                                    authorLower.contains("i_love_enge") ||
                                    authorLower.contains("unfe")) {
                                continue;
                            }

                            if (IgnoreManager.getIgnore().contains(author.replace(" ", ""))) continue;

                            int[] sum = calcSum(sumList);

                            if (sum != null) {
                                int price = sum[0];
                                if (price <= 0 || price > balance) continue;

                                for (AutoBuyItem item : AutoBuyManager.getItems()) {
                                    if (item instanceof CustomAutoBuyItem) {
                                        if (((CustomAutoBuyItem) item).tryBuy(stack, price, false) && System.currentTimeMillis() > idkTime) {
                                            pendingPurchaseItem = stack.copy();
                                            click(i, 1337);
                                            if (lastItem == null) {
                                                lastItem = new HistoryItem(price, stack);
                                            }
                                            packet = true;
                                            idkTime = System.currentTimeMillis() + 700;
                                        }
                                    }
                                    if (item instanceof DefaultAutoBuyItem) {
                                        if (((DefaultAutoBuyItem) item).tryBuy(stack, price) && System.currentTimeMillis() > idkTime) {
                                            pendingPurchaseItem = stack.copy();
                                            click(i, 1337);
                                            if (lastItem == null) {
                                                lastItem = new HistoryItem(price, stack);
                                            }
                                            packet = true;
                                            idkTime = System.currentTimeMillis() + 700;
                                        }
                                    }
                                }
                            }
                            if (stack.getItem().equals(Items.EMERALD) && i == 47 && System.currentTimeMillis() > update && !packet && System.currentTimeMillis() > idkTime) {
                                click2(i, 2);
                                int randomDelay = 300 + (int) (Math.random() * 300);
                                update = System.currentTimeMillis() + randomDelay;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Место ошибки AutoBuy: " + e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
            System.out.println("Тип ошибки: " + e.getClass().getSimpleName());
            if (e.getMessage() != null) {
                System.out.println("Сообщение об ошибке: " + e.getMessage());
            }
        }
    }

    private static void checkMoney() {
        if (System.currentTimeMillis() - lastMoneyCheck >= 10000) {
            mc.player.sendChatMessage("/money");
            lastMoneyCheck = System.currentTimeMillis();
        }
    }

    private static void click(int slot, int id) {
        mc.playerController.windowClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP, mc.player);
    }

    private static void click2(int slot, int id) {
        assert mc.player != null;
        assert mc.playerController != null;
    }

    public static int[] calcSum(List<ITextComponent> s) {
        if (s == null || s.isEmpty()) return null;
        int i1 = -1;
        int i2 = -1;
        for (ITextComponent t : s) {
            String string = t.getString();
            StringBuilder stringBuilder = new StringBuilder();
            for (char c : string.toCharArray()) {
                try {
                    stringBuilder.append(Integer.parseInt(String.valueOf(c)));
                } catch (Exception ignore) {
                }
            }
            if (!stringBuilder.toString().isEmpty()) {
                if (i1 < 0) {
                    i1 = Integer.parseInt(stringBuilder.toString());
                } else {
                    i2 = Integer.parseInt(stringBuilder.toString());
                }
            }
        }
        if (i1 < 100) return null;
        return new int[]{i1, i2};
    }
}