package org.privates.client.managers.module.impl.misc;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Funtime;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MoveInputEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.DelimiterSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.InventoryUtil;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.render.font.Fonts;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category2
@Funtime
@ModuleInfo(name = "FunTimeHelper", category = Category.MISC, description = "Помощник для сервера FunTime/SpookyTime")
public class FunTimeAssist extends Module {
    private String lastEventKey = "";
    private static final Pattern EVENT_COORD_PATTERN_BRACKETS = Pattern.compile("\\[(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)]");
    private static final Pattern EVENT_COORD_PATTERN_NO_BRACKETS = Pattern.compile("(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§.");


    private final long lastTimeNs = 0L;

    private final Map<String, Float> itemAnimations = new HashMap<>();

    private final BooleanSetting plastPreview = new BooleanSetting(this, "Проекция пласта", true);
    public BindSetting dezor = new BindSetting(this, "Дезориентация", -1);
    public BindSetting yavka = new BindSetting(this, "Явная пыль", -1);
    public final BindSetting trap = new BindSetting(this, "Трапка", -1);
    public BindSetting plast = new BindSetting(this, "Пласт", -1);
    public BindSetting bojka = new BindSetting(this, "Божья аура", -1);
    public BindSetting snejok = new BindSetting(this, "Снежок заморозка", -1);
    public BindSetting serka = new BindSetting(this, "Зелье Радиации", -1);
    public BindSetting otrigka = new BindSetting(this, "Снотворное", -1);
    public BindSetting killera = new BindSetting(this, "Зелье Ассасина", -1);
    public BindSetting medik = new BindSetting(this, "Зелье Медика", -1);
    public BindSetting pobedka = new BindSetting(this, "Зелье Палладина", -1);
    public BindSetting agenta = new BindSetting(this, "Зелье Агента", -1);
    private final BooleanSetting autogps = new BooleanSetting(this, "Авто метка на ивент", true);
    private final DelimiterSetting eventGroup = new DelimiterSetting(this, "Event")
            .setVisible(() -> autogps.getValue());
    private final ModeSetting eventMarkerMode = new ModeSetting(this, "Тип метки ивента", "Стелка до пути", "Метка в мире")
            .setVisible(() -> autogps.getValue());
    private final BooleanSetting ignoreBeacon = new BooleanSetting(this, "Игнорировать Загадочный Маяк", true)
            .setVisible(() -> autogps.getValue());
    private final BooleanSetting autogpsNotify = new BooleanSetting(this, "Оповещения ивентов", true)
            .setVisible(() -> autogps.getValue());

    public final BooleanSetting pukai = new BooleanSetting(this, "Обход Спуки тайма", false);
    private final BooleanSetting notif = new BooleanSetting(this, "Оповещения", true);
    private final BooleanSetting dezorPreview = new BooleanSetting(this, "Отрисовка дезориентации", true);
    private final BooleanSetting yavkaPreview = new BooleanSetting(this, "Отрисовка явной пыли", true);
    private final BooleanSetting trapPreview = new BooleanSetting(this, "Отрисовка трапки", true);
    private final BooleanSetting trapTimer = new BooleanSetting(this, "Таймер трапки", true);
    private final BooleanSetting plastTimer = new BooleanSetting(this, "Таймер пласта", true);


    final InventoryUtil.Hand handUtil = new InventoryUtil.Hand();

    // Переменные для таймера трапки
    private boolean trapTimerActive = false;
    private long trapTimerEndsAtMs = 0L;
    private boolean prevTrapCooldown = false;
    private double trapX = 0.0, trapY = 0.0, trapZ = 0.0; // координаты где была брошена трапка

    // Переменные для таймера пласта
    private boolean plastTimerActive = false;
    private long plastTimerEndsAtMs = 0L;
    private boolean prevPlastCooldown = false;
    private double plastX = 0.0, plastY = 0.0, plastZ = 0.0; // координаты где был брошен пласт

    private boolean ankaDetected = false;

    private final SoundUtil.AudioClip ankaSoundClip = SoundUtil.AudioClip.build("ankadettect.wav", true);
    private final SoundUtil.AudioClipPlayController ankaSoundController =
            SoundUtil.AudioClipPlayController.build(ankaSoundClip, () -> ankaDetected && isEnabled(), false);

    long delay;
    boolean disorientationThrow, trapThrow, ivkaThrow, plastThrow, boshkaThrow, snejokThrow;
    boolean serky, otrigy, kilera, medika, pobydka, agentyka;

    // Универсальный движок свапов/юзов
    final SwapEngine swap = new SwapEngine();

    private void printIfEnabled(String message) {
        if (notif.getValue()) ChatUtil.addText(message);
    }

    private void notifyIfEnabled(String message, boolean success) {
        if (notif.getValue()) {
        }
    }


    @EventHandler
    private void onPacketChat(PacketEvent e) {
        if (!(e.getPacket() instanceof SChatPacket packet)) return;
        if (!autogps.getValue()) return;

        String chatMessage = packet.getChatComponent().getString();

        // Отладочный вывод (можно убрать потом)
        if (chatMessage != null && (chatMessage.toLowerCase().contains("координатах") || chatMessage.contains("]") || chatMessage.matches(".*-?\\d+\\s+-?\\d+\\s+-?\\d+.*"))) {
            System.out.println("[FunTimeHelper] Получено сообщение: " + chatMessage);
        }

        EventData eventData = parseEventData(chatMessage);
        if (eventData == null) return;

        System.out.println("[FunTimeHelper] Распознан ивент: " + eventData.name + " на координатах " + eventData.x + " " + eventData.y + " " + eventData.z);

        if (ignoreBeacon.getValue() && "загадочный маяк".equalsIgnoreCase(eventData.name)) return;

        String eventKey = (eventData.name + "|" + eventData.x + "|" + eventData.y + "|" + eventData.z).toLowerCase(Locale.ROOT);
        if (eventKey.equals(lastEventKey)) return;
        lastEventKey = eventKey;

        String command;
        if (eventMarkerMode.is("Стрелочка")) {
            command = String.format(Locale.ROOT, ".gps set %d %d", eventData.x, eventData.z);
        } else {
            String safeName = eventData.name.replace('"', ' ').trim();
            if (safeName.isEmpty()) {
                safeName = "Ивент";
            }
            command = ".way add " + "Event" + " " + eventData.x + " " + eventData.y + ' ' + eventData.z;
        }

        sendCommand(command);
        if (autogpsNotify.getValue()) {
            printIfEnabled(String.format(Locale.ROOT, "Точка для ивента \"%s\" была добавлена!", eventData.name));
            notifyIfEnabled("Точка для ивента добавлена", true);
        }
    }

    private void sendCommand(String command) {
        if (mc.player != null) {
            mc.player.sendChatMessage(command);
        }
    }

    private EventData parseEventData(String rawMessage) {
        if (rawMessage == null || rawMessage.isEmpty()) return null;

        String message = COLOR_CODE_PATTERN.matcher(rawMessage).replaceAll("");
        message = message.replace('\u00A7', ' ').trim();
        message = message.replaceAll("\\s+", " ").trim();

        // Пробуем найти координаты в скобках или без них
        Matcher matcher = EVENT_COORD_PATTERN_BRACKETS.matcher(message);
        if (!matcher.find()) {
            // Если не нашли в скобках, ищем без скобок
            matcher = EVENT_COORD_PATTERN_NO_BRACKETS.matcher(message);
            if (!matcher.find()) return null;
        }

        int x;
        int y;
        int z;
        try {
            x = Integer.parseInt(matcher.group(1));
            y = Integer.parseInt(matcher.group(2));
            z = Integer.parseInt(matcher.group(3));
        } catch (NumberFormatException ex) {
            return null;
        }

        // Извлекаем текст перед координатами
        String prefix = message.substring(0, matcher.start()).trim();

        // Убираем лишние части текста
        prefix = prefix.replaceAll("(?i)(?:на\\s+координатах.*)$", "").trim();
        prefix = prefix.replaceAll("(?i)(?:появился|появился|замечен|обнаружен|активирован|запущен).*$", "").trim();
        prefix = prefix.replaceAll("(?i)(?:появился\\s+на\\s+координатах.*)$", "").trim();
        prefix = prefix.replaceAll("(?i)^(?:ивент|событие|event)[:\\s\"«]+", "").trim();
        prefix = prefix.replaceAll("^[\\-:\\s]+", "").trim();
        prefix = prefix.replaceAll("[\"«»]", "").trim();

        // Ищем название в квадратных скобках в начале (например, [Маяк убийца])
        Pattern bracketNamePattern = Pattern.compile("\\[([^\\]]+)\\]");
        Matcher bracketMatcher = bracketNamePattern.matcher(rawMessage);
        if (bracketMatcher.find()) {
            String bracketName = bracketMatcher.group(1);
            bracketName = COLOR_CODE_PATTERN.matcher(bracketName).replaceAll("");
            bracketName = bracketName.replace('\u00A7', ' ').trim();
            if (!bracketName.isEmpty()) {
                prefix = bracketName;
            }
        }

        // Если после всех обработок префикс пустой, пытаемся найти любое слово перед координатами
        if (prefix.isEmpty()) {
            // Берем последние 1-3 слова перед координатами
            String[] words = message.substring(0, matcher.start()).trim().split("\\s+");
            if (words.length > 0) {
                StringBuilder sb = new StringBuilder();
                int startIdx = Math.max(0, words.length - 3);
                for (int i = startIdx; i < words.length; i++) {
                    if (!words[i].isEmpty() && !words[i].matches("(?i)(на|координатах|появился|замечен)")) {
                        if (sb.length() > 0) sb.append(" ");
                        sb.append(words[i]);
                    }
                }
                prefix = sb.toString().trim();
            }
        }

        if (prefix.isEmpty()) {
            prefix = "Ивент";
        }

        return new EventData(prefix, x, y, z);
    }

    private record EventData(String name, int x, int y, int z) {
    }

    @EventHandler
    public void onKeyPress(KeyboardPressEvent e) {
        if (e.getScreen() != null) return;
        if (pukai.getValue()) {
            if (e.isKey(dezor.getValue())) {
                disorientationThrow = true;
            } else if (e.isKey(trap.getValue())) {
                trapThrow = true;
            } else if (e.isKey(plast.getValue())) {
                plastThrow = true;
            } else if (e.isKey(yavka.getValue())) {
                ivkaThrow = true;
            } else if (e.isKey(bojka.getValue())) {
                boshkaThrow = true;
            } else if (e.isKey(snejok.getValue())) {
                snejokThrow = true;
            } else if (e.isKey(serka.getValue())) {
                actionPotion("радиац", " Зелье радиации использована!", "Зелье радиации не найдена!");
            } else if (e.isKey(otrigka.getValue())) {
                actionPotion("снотво", "Снотворное использована!", "Снотворное не найдена!");
            } else if (e.isKey(killera.getValue())) {
                actionPotion("ассас", "Зелье Ассасина использовано!", "Зелье Ассасина не найдено!");
            } else if (e.isKey(medik.getValue())) {
                actionPotion("медик", "Зелье медика использовано!", "Зелье медика не найдено!");
            } else if (e.isKey(pobedka.getValue())) {
                actionPotion("палла", "паллаилка использована!", "паллаилка не найдена!");
            } else if (e.isKey(agenta.getValue())) {
                actionPotion("агент", "Зелье агента использовано!", "Зелье агента не найдено!");
            }
        } else {
            if (e.isKey(dezor.getValue())) {
                boolean success = swap.use(Items.ENDER_EYE, null, false);
                if (success) {
                    printIfEnabled("Дезориентация использована!");
                    notifyIfEnabled("Дезориентация использована!", true);
                } else {
                    printIfEnabled("Дезориентация не найдена!");
                    notifyIfEnabled("Дезориентация не найдена!", false);
                }
            } else if (e.isKey(trap.getValue())) {
                boolean success = swap.use(Items.NETHERITE_SCRAP, null, false);
                if (success) {
                    printIfEnabled("Трапка использована!");
                    notifyIfEnabled("Трапка использована!", true);
                } else {
                    printIfEnabled("Трапка не найдена!");
                    notifyIfEnabled("Трапка не найдена!", false);
                }
            } else if (e.isKey(plast.getValue())) {
                boolean success = swap.use(Items.DRIED_KELP, null, false);
                if (success) {
                    printIfEnabled("Пласт использован!");
                    notifyIfEnabled("Пласт использован!", true);
                } else {
                    printIfEnabled("Пласт не найден!");
                    notifyIfEnabled("Пласт не найден!", false);
                }
            } else if (e.isKey(yavka.getValue())) {
                boolean success = swap.use(Items.SUGAR, null, false);
                if (success) {
                    printIfEnabled("Явная пыль использована!");
                    notifyIfEnabled("Явная пыль использована!", true);
                } else {
                    printIfEnabled("Явная пыль не найдена!");
                    notifyIfEnabled("Явная пыль не найдена!", false);
                }
            } else if (e.isKey(bojka.getValue())) {
                boolean success = swap.use(Items.PHANTOM_MEMBRANE, null, false);
                if (success) {
                    printIfEnabled("Божья аура использована!");
                    notifyIfEnabled("Божья аура использована!", true);
                } else {
                    printIfEnabled("Божья аура не найдена!");
                    notifyIfEnabled("Божья аура не найдена!", false);
                }
            } else if (e.isKey(snejok.getValue())) {
                boolean success = swap.use(Items.SNOWBALL, null, false);
                if (success) {
                    printIfEnabled("Снежок использован!");
                    notifyIfEnabled("Снежок использован!", true);
                } else {
                    printIfEnabled("Снежок не найден!");
                    notifyIfEnabled("Снежок не найден!", false);
                }
            } else if (e.isKey(serka.getValue())) {
                actionPotion("радиац", "Зелье радиации использована!", "Зелье радиации не найдена!");
            } else if (e.isKey(otrigka.getValue())) {
                actionPotion("снотв", "Снотворное использована!", "Снотворное не найдена!");
            } else if (e.isKey(killera.getValue())) {
                actionPotion("ассас", "Зелье Ассасина использовано!", "Зелье Ассасина не найдено!");
            } else if (e.isKey(medik.getValue())) {
                actionPotion("медик", "Зелье медика использовано!", "Зелье медика не найдено!");
            } else if (e.isKey(pobedka.getValue())) {
                actionPotion("палла", "паллаилка использована!", "паллаилка не найдена!");
            } else if (e.isKey(agenta.getValue())) {
                actionPotion("агент", "Зелье агента использовано!", "Зелье агента не найдено!");
            }
        }
    }

    @EventHandler
    public void onMousePress(MousePressEvent e) {
        if (e.getScreen() != null) return;
        int bindValue = e.getKey() + 500;
        if (pukai.getValue()) {
            if (bindValue == dezor.getValue()) {
                disorientationThrow = true;
            } else if (bindValue == trap.getValue()) {
                trapThrow = true;
            } else if (bindValue == plast.getValue()) {
                plastThrow = true;
            } else if (bindValue == yavka.getValue()) {
                ivkaThrow = true;
            } else if (bindValue == bojka.getValue()) {
                boshkaThrow = true;
            } else if (bindValue == snejok.getValue()) {
                snejokThrow = true;
            } else if (bindValue == serka.getValue()) {
                actionPotion("снотв", "Зелье радиации использована!", "Зелье радиации не найдена!");
            } else if (bindValue == otrigka.getValue()) {
                actionPotion("снотв", "Снотворное использована!", "Снотворное не найдена!");
            } else if (bindValue == killera.getValue()) {
                actionPotion("ассас", "Зелье Ассасина использовано!", "Зелье Ассасина не найдено!");
            } else if (bindValue == medik.getValue()) {
                actionPotion("медик", "Зелье медика использовано!", "Зелье медика не найдено!");
            } else if (bindValue == pobedka.getValue()) {
                actionPotion("палла", "паллаилка использована!", "паллаилка не найдена!");
            } else if (bindValue == agenta.getValue()) {
                actionPotion("агент", "Зелье агента использовано!", "Зелье агента не найдено!");
            }
        } else {
            if (bindValue == dezor.getValue()) {
                boolean success = swap.use(Items.ENDER_EYE, null, false);
                if (success) {
                    printIfEnabled("Дезориентация использована!");
                    notifyIfEnabled("Дезориентация использована!", true);
                } else {
                    printIfEnabled("Дезориентация не найдена!");
                    notifyIfEnabled("Дезориентация не найдена!", false);
                }
            } else if (bindValue == trap.getValue()) {
                boolean success = swap.use(Items.NETHERITE_SCRAP, null, false);
                if (success) {
                    printIfEnabled("Трапка использована!");
                    notifyIfEnabled("Трапка использована!", true);
                } else {
                    printIfEnabled("Трапка не найдена!");
                    notifyIfEnabled("Трапка не найдена!", false);
                }
            } else if (bindValue == plast.getValue()) {
                boolean success = swap.use(Items.DRIED_KELP, null, false);
                if (success) {
                    printIfEnabled("Пласт использован!");
                    notifyIfEnabled("Пласт использован!", true);
                } else {
                    printIfEnabled("Пласт не найден!");
                    notifyIfEnabled("Пласт не найден!", false);
                }
            } else if (bindValue == yavka.getValue()) {
                boolean success = swap.use(Items.SUGAR, null, false);
                if (success) {
                    printIfEnabled("Явная пыль использована!");
                    notifyIfEnabled("Явная пыль использована!", true);
                } else {
                    printIfEnabled("Явная пыль не найдена!");
                    notifyIfEnabled("Явная пыль не найдена!", false);
                }
            } else if (bindValue == bojka.getValue()) {
                boolean success = swap.use(Items.PHANTOM_MEMBRANE, null, false);
                if (success) {
                    printIfEnabled("Божья аура использована!");
                    notifyIfEnabled("Божья аура использована!", true);
                } else {
                    printIfEnabled("Божья аура не найдена!");
                    notifyIfEnabled("Божья аура не найдена!", false);
                }
            } else if (bindValue == snejok.getValue()) {
                boolean success = swap.use(Items.SNOWBALL, null, false);
                if (success) {
                    printIfEnabled("Снежок использован!");
                    notifyIfEnabled("Снежок использован!", true);
                } else {
                    printIfEnabled("Снежок не найден!");
                    notifyIfEnabled("Снежок не найден!", false);
                }
            } else if (bindValue == serka.getValue()) {
                actionPotion("радиац", "Зелье радиации использована!", "Зелье радиации не найдена!");
            } else if (bindValue == otrigka.getValue()) {
                actionPotion("снотв", "Снотворное использована!", "Снотворное не найдена!");
            } else if (bindValue == killera.getValue()) {
                actionPotion("ассас", "Зелье Ассасина использовано!", "Зелье Ассасина не найдено!");
            } else if (bindValue == medik.getValue()) {
                actionPotion("медик", "Зелье медика использовано!", "Зелье медика не найдено!");
            } else if (bindValue == pobedka.getValue()) {
                actionPotion("палла", "паллаилка использована!", "паллаилка не найдена!");
            } else if (bindValue == agenta.getValue()) {
                actionPotion("агент", "Зелье агента использовано!", "Зелье агента не найдено!");
            }
        }
    }

    private void actionPotion(String nameLower, String okMsg, String failMsg) {
        boolean success = swap.use(Items.SPLASH_POTION, nameLower, false);
        if (success) {
            printIfEnabled(okMsg);
            notifyIfEnabled(okMsg, true);
        } else {
            printIfEnabled(failMsg);
            notifyIfEnabled(failMsg, false);
        }
    }


    private boolean isPlayerNearby(double radius) {
        if (mc.world == null || mc.player == null) return false;
        return mc.world.getPlayers().stream()
                .anyMatch(p -> p != mc.player &&
                        !p.isSpectator() &&
                        !Excellent.inst().friendManager().isFriend(p.getGameProfile().getName()) &&
                        p.getDistanceSq(mc.player) <= radius * radius);
    }

    public Map<Item, ItemBindData> getKeyBindings() {
        Map<Item, ItemBindData> bindings = new LinkedHashMap<>();

        bindings.put(Items.ENDER_EYE, new ItemBindData("Дезориентация", dezor, null, null));
        bindings.put(Items.SUGAR, new ItemBindData("Явная пыль", yavka, null, null));
        bindings.put(Items.NETHERITE_SCRAP, new ItemBindData("Трапка", trap, null, null));
        bindings.put(Items.DRIED_KELP, new ItemBindData("Пласт", plast, null, null));
        bindings.put(Items.PHANTOM_MEMBRANE, new ItemBindData("Божья аура", bojka, null, null));
        bindings.put(Items.SNOWBALL, new ItemBindData("Снежок", snejok, null, null));

        bindings.put(Items.SPLASH_POTION, new ItemBindData("Зелье радиации", serka, "радиац", 823822));

        return bindings;
    }

    public record ItemBindData(String title, BindSetting bind, String nameFilter, Integer tintRGB) {
    }

    @EventHandler
    private void onUpdate(UpdateEvent e) {
        swap.tick(); // обработка отложенного восстановления

        // Проверка scoreboard на наличие анки (как в NameProtect)
        if (mc.world == null || mc.player == null) {
            ankaDetected = false;
        } else {
            Scoreboard scoreboard = mc.world.getScoreboard();
            if (scoreboard == null) {
                ankaDetected = false;
            } else {
                ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
                if (objective == null) {
                    ankaDetected = false;
                } else {
                    ITextComponent header = objective.getDisplayName();
                    if (header == null) {
                        ankaDetected = false;
                    } else {
                        String headerString = header.getString();
                        String headerLower = TextFormatting.getTextWithoutFormattingCodes(headerString).toLowerCase();
                        // Проверяем наличие "216" в header
                        ankaDetected = (headerLower.contains("216") && PlayerUtil.isFuntime() || headerLower.contains("215") && PlayerUtil.isFuntime() || headerLower.contains("217") || headerLower.contains("105") && PlayerUtil.isFuntime() || headerLower.contains("106") && PlayerUtil.isFuntime() || headerLower.contains("312") && PlayerUtil.isFuntime() || headerLower.contains("313") && PlayerUtil.isFuntime());
                    }
                }
            }
        }

        // Обновление статуса воспроизведения звука
        boolean shouldPlay = ankaDetected && isEnabled();
        ankaSoundController.updatePlayingStatus();

        // Установка громкости звука (если звук играет)
        if (ankaSoundController.isSucessPlaying()) {
            ankaSoundClip.setVolume(1); // 50% громкость
        } else if (shouldPlay && !ankaSoundClip.isPlaying()) {
            // Если звук должен играть, но не играет - пробуем запустить вручную
            try {
                ankaSoundClip.startPlayingAudio();
                ankaSoundClip.setVolume(1);
            } catch (Exception ex) {
                // Игнорируем ошибки, возможно файл не найден
            }
        }

        // Отслеживание кулдауна трапки для таймера
        if (trapTimer.getValue() && mc.player != null) {
            boolean cdNow = mc.player.getCooldownTracker().hasCooldown(Items.NETHERITE_SCRAP);
            // Если трапка только что вошла в кулдаун
            if (cdNow && !prevTrapCooldown) {
                trapTimerActive = true;
                trapTimerEndsAtMs = System.currentTimeMillis() + 15_000L; // 15 секунд действия

                // Сохраняем координаты трапки (позиция прицела - куда смотришь)
                Vector3d eyePos = new Vector3d(mc.player.getPosX(), mc.player.getPosY() + mc.player.getEyeHeight(), mc.player.getPosZ());
                trapX = eyePos.x;
                trapY = eyePos.y;
                trapZ = eyePos.z;

                // Отладочная информация
                printIfEnabled("Трапка на координатах: " + String.format("%.1f %.1f %.1f", trapX, trapY, trapZ));
            }
            prevTrapCooldown = cdNow;

            // Проверяем, не истек ли таймер
            if (trapTimerActive && System.currentTimeMillis() >= trapTimerEndsAtMs) {
                trapTimerActive = false;
            }
        } else {
            trapTimerActive = false;
            prevTrapCooldown = false;
        }

        // Отслеживание кулдауна пласта для таймера
        if (plastTimer.getValue() && mc.player != null) {
            boolean cdNow = mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP);
            // Если пласт только что вошёл в кулдаун
            if (cdNow && !prevPlastCooldown) {
                plastTimerActive = true;
                plastTimerEndsAtMs = System.currentTimeMillis() + 20_000L; // 20 секунд действия

                // Сохраняем координаты пласта (позиция прицела + 2 блока впереди)
                Vector3d eyePos = new Vector3d(mc.player.getPosX(), mc.player.getPosY() + mc.player.getEyeHeight(), mc.player.getPosZ());
                Vector3d lookVec = mc.player.getLookVec();
                // Пласт появляется в 2 блоках перед прицелом по направлению взгляда
                Vector3d plastPos = eyePos.add(lookVec.scale(2.0));
                plastX = plastPos.x;
                plastY = plastPos.y;
                plastZ = plastPos.z;

                // Отладочная информация
                printIfEnabled("Пласт на координатах: " + String.format("%.1f %.1f %.1f", plastX, plastY, plastZ));
            }
            prevPlastCooldown = cdNow;

            // Проверяем, не истек ли таймер
            if (plastTimerActive && System.currentTimeMillis() >= plastTimerEndsAtMs) {
                plastTimerActive = false;
            }
        } else {
            plastTimerActive = false;
            prevPlastCooldown = false;
        }

        if (!pukai.getValue()) return; // Если обход отключен, не обрабатываем флаги

        // Дезориентация
        if (disorientationThrow) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);

            int hbSlot = getItem(Items.ENDER_EYE, true);
            int invSlot = getItem(Items.ENDER_EYE, false);

            if (invSlot == -1 && hbSlot == -1) {
                disorientationThrow = false;
                printIfEnabled("Дезориентация не найдена!");
                notifyIfEnabled("Дезориентация не найдена!", false);
                return;
            }

            if (!mc.player.getCooldownTracker().hasCooldown(Items.ENDER_EYE)) {
                int slot = findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }
                printIfEnabled("Дезориентация использована!");
                notifyIfEnabled("Дезориентация использована!", true);
            } else {
                printIfEnabled("Дезориентация в КД!");
                notifyIfEnabled("Дезориентация в КД!", false);
            }
            disorientationThrow = false;
        }

        // Трапка
        if (trapThrow) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);

            int hbSlot = getItem(Items.NETHERITE_SCRAP, true);
            int invSlot = getItem(Items.NETHERITE_SCRAP, false);

            if (invSlot == -1 && hbSlot == -1) {
                trapThrow = false;
                printIfEnabled("Трапка не найдена!");
                notifyIfEnabled("Трапка не найдена!", false);
                return;
            }

            if (!mc.player.getCooldownTracker().hasCooldown(Items.NETHERITE_SCRAP)) {
                int slot = findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }
                printIfEnabled("Трапка использована!");
                notifyIfEnabled("Трапка использована!", true);
            } else {
                printIfEnabled("Трапка в КД!");
                notifyIfEnabled("Трапка в КД!", false);
            }
            trapThrow = false;
        }

        // Пласт
        if (plastThrow) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);

            int hbSlot = getItem(Items.DRIED_KELP, true);
            int invSlot = getItem(Items.DRIED_KELP, false);

            if (invSlot == -1 && hbSlot == -1) {
                plastThrow = false;
                printIfEnabled("Пласт не найден!");
                notifyIfEnabled("Пласт не найден!", false);
                return;
            }

            if (!mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP)) {
                int slot = findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }
                printIfEnabled("Пласт использован!");
                notifyIfEnabled("Пласт использован!", true);
            } else {
                printIfEnabled("Пласт в КД!");
                notifyIfEnabled("Пласт в КД!", false);
            }
            plastThrow = false;
        }

        // Явная пыль
        if (ivkaThrow) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);

            int hbSlot = getItem(Items.SUGAR, true);
            int invSlot = getItem(Items.SUGAR, false);

            if (invSlot == -1 && hbSlot == -1) {
                ivkaThrow = false;
                printIfEnabled("Явная пыль не найдена!");
                notifyIfEnabled("Явная пыль не найдена!", false);
                return;
            }

            if (!mc.player.getCooldownTracker().hasCooldown(Items.SUGAR)) {
                int slot = findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }
                printIfEnabled("Явная пыль использована!");
                notifyIfEnabled("Явная пыль использована!", true);
            } else {
                printIfEnabled("Явная пыль в КД!");
                notifyIfEnabled("Явная пыль в КД!", false);
            }
            ivkaThrow = false;
        }

        // Божья аура
        if (boshkaThrow) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);

            int hbSlot = getItem(Items.PHANTOM_MEMBRANE, true);
            int invSlot = getItem(Items.PHANTOM_MEMBRANE, false);

            if (invSlot == -1 && hbSlot == -1) {
                boshkaThrow = false;
                printIfEnabled("Божья аура не найдена!");
                notifyIfEnabled("Божья аура не найдена!", false);
                return;
            }

            if (!mc.player.getCooldownTracker().hasCooldown(Items.PHANTOM_MEMBRANE)) {
                int slot = findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }
                printIfEnabled("Божья аура использована!");
                notifyIfEnabled("Божья аура использована!", true);
            } else {
                printIfEnabled("Божья аура в КД!");
                notifyIfEnabled("Божья аура в КД!", false);
            }
            boshkaThrow = false;
        }

        // Снежок
        if (snejokThrow) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);

            int hbSlot = getItem(Items.SNOWBALL, true);
            int invSlot = getItem(Items.SNOWBALL, false);

            if (invSlot == -1 && hbSlot == -1) {
                snejokThrow = false;
                printIfEnabled("Снежок не найден!");
                notifyIfEnabled("Снежок не найден!", false);
                return;
            }

            if (!mc.player.getCooldownTracker().hasCooldown(Items.SNOWBALL)) {
                int slot = findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }
                printIfEnabled("Снежок использован!");
                notifyIfEnabled("Снежок использован!", true);
            } else {
                printIfEnabled("Снежок в КД!");
                notifyIfEnabled("Снежок в КД!", false);
            }
            snejokThrow = false;
        }

        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
    }

    @EventHandler
    private void onPacket(PacketEvent e) {
        this.handUtil.onEventPacket(e);
    }

    @EventHandler
    public void onEvent(MoveInputEvent e) {
        if (ankaDetected) {
            e.setForward(0);
            e.setStrafe(0);
            e.setSneaking(false);
            e.setJump(false);
        }
    }

    @Override
    public void onDisable() {
        disorientationThrow = trapThrow = ivkaThrow = boshkaThrow = plastThrow = snejokThrow = false;
        delay = 0;
        swap.reset();
        trapTimerActive = false;
        trapTimerEndsAtMs = 0L;
        prevTrapCooldown = false;
        trapX = trapY = trapZ = 0.0;
        plastTimerActive = false;
        plastTimerEndsAtMs = 0L;
        prevPlastCooldown = false;
        plastX = plastY = plastZ = 0.0;
        ankaDetected = false;

        // Останавливаем звук анки
        ankaSoundController.updatePlayingStatus();
        if (ankaSoundClip.isPlaying()) {
            ankaSoundClip.stopPlayingAudio();
        }

        super.onDisable();
    }

    private int findAndTrowItem(int hbSlot, int invSlot) {
        if (hbSlot != -1) {
            this.handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return hbSlot;
        }
        if (invSlot != -1) {
            handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            mc.playerController.pickItem(invSlot);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return invSlot;
        }
        return -1;
    }

    private int getItem(Item input, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (itemStack.isEmpty()) {
                continue;
            }
            if (itemStack.getItem() == input) {
                return i;
            }
        }
        return -1;
    }


    @EventHandler
    private void onRender3D(Render3DPosedEvent event) {
        MatrixStack matrix = event.getMatrix();
        ActiveRenderInfo activeRenderInfo = event.getActiveRenderInfo();

        if (activeRenderInfo.isValid() && mc.getRenderManager().options != null && mc.player != null && mc.world != null) {
            // Рендерим таймер пласта в 3D пространстве
            if (plastTimer.getValue() && plastTimerActive) {
                long msLeft = Math.max(0L, plastTimerEndsAtMs - System.currentTimeMillis());
                float secLeft = msLeft / 1000.0f;

                if (secLeft > 0.01f) {
                    String text = "До пропадания пласта: " + String.format(java.util.Locale.ROOT, "%.1fс", secLeft);
                    render3DText(matrix, event, text, plastX, plastY, plastZ);
                } else {
                    plastTimerActive = false;
                }
            }

            // Рендерим проекцию пласта 5x5, когда игрок держит ламинарию в руке
            renderPlastPreview(event);

            // Рендерим проекцию трапки вокруг игрока
            renderTrapPreview(event);

            // Рендерим проекции для дезориентации, явной пыли и жемчуга края
            renderCircularPreview(event, Items.ENDER_EYE, dezorPreview);
            renderCircularPreview(event, Items.SUGAR, yavkaPreview);
        }
    }

    private void renderPlastPreview(Render3DPosedEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (!plastPreview.getValue()) return;

        ItemStack mainHand = mc.player.getHeldItemMainhand();
        ItemStack offHand = mc.player.getHeldItemOffhand();
        boolean hasLaminaria = (mainHand.getItem() == Items.DRIED_KELP) || (offHand.getItem() == Items.DRIED_KELP);

        if (!hasLaminaria) return;

        float pitch = mc.player.rotationPitch;
        float yaw = mc.player.rotationYaw;

        // Определяем расстояние: если смотрим вверх/вниз - на 1 блок дальше, горизонтально (вперед) - на 1 блок дальше
        double baseDistance = (Math.abs(pitch) > 60F) ? 4.0 : 3.0;

        Vector3d eyePos = new Vector3d(mc.player.getPosX(), mc.player.getPosY() + mc.player.getEyeHeight(), mc.player.getPosZ());
        Vector3d lookVec = mc.player.getLookVec().normalize();
        Vector3d targetPos = eyePos.add(lookVec.scale(baseDistance));

        BlockPos centerBlock = new BlockPos(targetPos);
        BlockPos baseBlockPos = centerBlock;

        AxisAlignedBB playerBB = mc.player.getBoundingBox();
        List<Entity> entities = new ArrayList<>((Collection) mc.world.getAllEntities());

        // Нормализуем yaw к диапазону 0-360
        float normalizedYaw = yaw % 360;
        if (normalizedYaw < 0) normalizedYaw += 360;

        Direction facing;
        Direction widthDir;

        // Определяем ориентацию по pitch и yaw
        // Если смотрим вверх (pitch от -45 до -90) или вниз (pitch от 45 до 90)
        if ((pitch >= -90 && pitch <= -45) || (pitch >= 45 && pitch <= 90)) {
            // Смотрим вверх/вниз - горизонтальная плоскость, ориентация зависит от yaw
            if ((normalizedYaw >= 0 && normalizedYaw < 45) || (normalizedYaw >= 315 && normalizedYaw <= 360)) {
                facing = Direction.NORTH; // Вперед
                widthDir = Direction.EAST;
            } else if (normalizedYaw >= 45 && normalizedYaw < 135) {
                facing = Direction.EAST; // Вправо
                widthDir = Direction.SOUTH;
            } else if (normalizedYaw >= 135 && normalizedYaw < 225) {
                facing = Direction.SOUTH; // Назад
                widthDir = Direction.WEST;
            } else {
                // Влево (225-315)
                facing = Direction.WEST;
                widthDir = Direction.NORTH;
            }
        } else {
            // Смотрим горизонтально - определяем направление по yaw
            // Вбок вперед налево: 120-160 (приоритетная проверка)
            if (normalizedYaw >= 120 && normalizedYaw < 160) {
                facing = Direction.NORTH;
                widthDir = Direction.WEST;
            } else if ((normalizedYaw >= 0 && normalizedYaw < 45) || (normalizedYaw >= 315 && normalizedYaw <= 360)) {
                // Вперед
                facing = Direction.NORTH;
                widthDir = Direction.EAST;
            } else if (normalizedYaw >= 45 && normalizedYaw < 135) {
                // Вправо
                facing = Direction.EAST;
                widthDir = Direction.SOUTH;
            } else if (normalizedYaw >= 135 && normalizedYaw < 225) {
                // Назад
                facing = Direction.SOUTH;
                widthDir = Direction.WEST;
            } else {
                // Влево (225-315)
                facing = Direction.WEST;
                widthDir = Direction.NORTH;
            }
        }

        renderPlastPlaneByBlocks(event, baseBlockPos, facing, widthDir, playerBB, entities);
    }

    private void renderPlastPlaneByBlocks(Render3DPosedEvent event,
                                          BlockPos basePos,
                                          Direction facing,
                                          Direction widthDir,
                                          AxisAlignedBB playerBB,
                                          List<Entity> entities) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int greenOutline = ColorUtil.getColor(0, 255, 0, 180);
        int redOutline = ColorUtil.getColor(255, 0, 0, 180);

        // Создаем общий bounding box для всего пласта
        AxisAlignedBB plastBB = null;
        List<BlockPos> blockPositions = new ArrayList<>();

        // Собираем все позиции блоков пласта
        for (int w = -2; w <= 2; w++) {
            for (int h = -2; h <= 2; h++) {
                for (int depth = 0; depth < 2; depth++) {
                    BlockPos blockPos = basePos
                            .offset(widthDir, w)
                            .offset(Direction.UP, h)
                            .offset(facing, depth);

                    blockPositions.add(blockPos);

                    AxisAlignedBB blockBB = new AxisAlignedBB(
                            blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                            blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1
                    );

                    if (plastBB == null) {
                        plastBB = blockBB;
                    } else {
                        plastBB = plastBB.union(blockBB);
                    }
                }
            }
        }

        // Проверяем пересечение всего пласта с энтити
        boolean plastIntersectsEntity = false;
        if (plastBB != null) {
            for (Entity entity : entities) {
                if (entity == mc.player) continue;
                if (plastBB.intersects(entity.getBoundingBox())) {
                    plastIntersectsEntity = true;
                    break;
                }
            }
        }

        int outlineColor = plastIntersectsEntity ? redOutline : greenOutline;

        RenderUtil3D.setup3dForBlockPos(event, () -> {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(2.0f);

            // Рендерим все блоки пласта строго по блокам
            for (BlockPos blockPos : blockPositions) {
                double blockX = blockPos.getX() + 0.5;
                double blockY = blockPos.getY() + 0.5;
                double blockZ = blockPos.getZ() + 0.5;

                AxisAlignedBB blockBB = new AxisAlignedBB(
                        blockX - 0.5, blockY - 0.5, blockZ - 0.5,
                        blockX + 0.5, blockY + 0.5, blockZ + 0.5
                );

                if (blockBB.intersects(playerBB)) {
                    continue;
                }

                // Используем общий цвет для всех блоков
                RenderUtil3D.drawCanisterBox(event.getMatrix(), buffer, tessellator, blockBB, true, false, false, outlineColor, 0, 0);
            }

            GL11.glLineWidth(1.0f);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }, false, false);
    }


    private void renderCircularPreview(Render3DPosedEvent event, Item item, BooleanSetting setting) {
        if (mc.player == null || mc.world == null) return;

        // Для жемчуга края всегда показываем, для остальных проверяем настройку
        if (setting != null && !setting.getValue()) return;

        ItemStack mainHand = mc.player.getHeldItemMainhand();
        ItemStack offHand = mc.player.getHeldItemOffhand();
        boolean hasItem = (mainHand.getItem() == item) || (offHand.getItem() == item);

        if (!hasItem) return;

        // Радиус 10 блоков
        double radius = 10.0;
        BlockPos playerBlockPos = mc.player.getPosition();
        Vector3d playerPos = new Vector3d(mc.player.getPosX(), playerBlockPos.getY() + 0.5, mc.player.getPosZ());

        AxisAlignedBB playerBB = mc.player.getBoundingBox();
        List<Entity> entities = new ArrayList<>((Collection) mc.world.getAllEntities());

        int greenOutline = ColorUtil.getColor(0, 255, 0, 180);
        int redOutline = ColorUtil.getColor(255, 0, 0, 180);

        // Для дезориентации и явной пыли проверяем весь круг на энтити
        boolean circleHasEntity = false;
        if (item == Items.ENDER_EYE || item == Items.SUGAR) {
            for (Entity entity : entities) {
                if (entity == mc.player) continue;
                Vector3d entityPos = entity.getPositionVec().add(0, entity.getHeight() / 2.0, 0);
                double distX = entityPos.x - playerPos.x;
                double distZ = entityPos.z - playerPos.z;
                double dist = Math.sqrt(distX * distX + distZ * distZ);
                if (dist <= radius) {
                    circleHasEntity = true;
                    break;
                }
            }
        }

        int circleColor = circleHasEntity ? redOutline : greenOutline;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderUtil3D.setup3dForBlockPos(event, () -> {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(2.0f);
            GL11.glDisable(GL11.GL_DEPTH_TEST); // Видно через блоки

            // Проходимся по всем блокам в квадрате радиуса
            int radiusInt = (int) Math.ceil(radius);
            for (int x = -radiusInt; x <= radiusInt; x++) {
                for (int z = -radiusInt; z <= radiusInt; z++) {
                    BlockPos blockPos = playerBlockPos.add(x, 0, z);
                    double blockX = blockPos.getX() + 0.5;
                    double blockY = blockPos.getY() + 0.5;
                    double blockZ = blockPos.getZ() + 0.5;

                    // Вычисляем расстояние от центра блока до игрока
                    double distX = blockX - playerPos.x;
                    double distZ = blockZ - playerPos.z;
                    double dist = Math.sqrt(distX * distX + distZ * distZ);

                    // Проверяем, находится ли блок на окружности (с допуском 0.5 блока)
                    if (Math.abs(dist - radius) > 0.5) continue;

                    AxisAlignedBB blockBB = new AxisAlignedBB(
                            blockX - 0.5, blockY - 0.5, blockZ - 0.5,
                            blockX + 0.5, blockY + 0.5, blockZ + 0.5
                    );

                    if (blockBB.intersects(playerBB)) {
                        continue;
                    }

                    // Используем общий цвет для всего круга (для дезориентации и явной пыли)
                    int outline = (item == Items.ENDER_EYE || item == Items.SUGAR) ? circleColor : greenOutline;
                    RenderUtil3D.drawCanisterBox(event.getMatrix(), buffer, tessellator, blockBB, true, false, false, outline, 0, 0);
                }
            }

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glLineWidth(1.0f);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }, false, false);
    }

    private void renderTrapPreview(Render3DPosedEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (!trapPreview.getValue()) return;

        // Проверяем, держит ли игрок трапку в руке
        ItemStack mainHand = mc.player.getHeldItemMainhand();
        ItemStack offHand = mc.player.getHeldItemOffhand();
        boolean hasTrap = (mainHand.getItem() == Items.NETHERITE_SCRAP) || (offHand.getItem() == Items.NETHERITE_SCRAP);

        if (!hasTrap) return;

        BlockPos basePos = mc.player.getPosition().down();
        AxisAlignedBB playerBB = mc.player.getBoundingBox();
        List<Entity> entities = new ArrayList<>((Collection) mc.world.getAllEntities());

        int greenOutline = ColorUtil.getColor(0, 255, 0, 180);
        int redOutline = ColorUtil.getColor(255, 0, 0, 180);

        // Создаем общий bounding box для всего куба трапки
        AxisAlignedBB trapCubeBB = new AxisAlignedBB(
                basePos.getX() - 2.5, basePos.getY(), basePos.getZ() - 2.5,
                basePos.getX() + 3.5, basePos.getY() + 5, basePos.getZ() + 3.5
        );

        // Проверяем, есть ли энтити внутри всего куба
        boolean trapHasEntity = false;
        for (Entity entity : entities) {
            if (entity == mc.player) continue;
            if (trapCubeBB.intersects(entity.getBoundingBox())) {
                trapHasEntity = true;
                break;
            }
        }

        int trapColor = trapHasEntity ? redOutline : greenOutline;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderUtil3D.setup3dForBlockPos(event, () -> {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(2.0f);

            for (int x = -2; x <= 2; x++) {
                for (int y = 0; y <= 4; y++) {
                    for (int z = -2; z <= 2; z++) {
                        boolean shell = Math.abs(x) == 2 || Math.abs(z) == 2 || y == 0 || y == 4;
                        if (!shell) continue;

                        BlockPos blockPos = basePos.add(x, y, z);
                        double blockX = blockPos.getX() + 0.5;
                        double blockY = blockPos.getY() + 0.5;
                        double blockZ = blockPos.getZ() + 0.5;

                        AxisAlignedBB blockBB = new AxisAlignedBB(
                                blockX - 0.5, blockY - 0.5, blockZ - 0.5,
                                blockX + 0.5, blockY + 0.5, blockZ + 0.5
                        );

                        if (blockBB.intersects(playerBB)) {
                            continue;
                        }

                        // Используем общий цвет для всего куба
                        RenderUtil3D.drawCanisterBox(event.getMatrix(), buffer, tessellator, blockBB, true, false, false, trapColor, 0, 0);
                    }
                }
            }

            GL11.glLineWidth(1.0f);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }, false, false);
    }

    private void render3DText(MatrixStack matrix, Render3DPosedEvent event, String text, double posX, double posY, double posZ) {
        matrix.push();

        // Используем координаты камеры из event
        double cameraX = event.getX();
        double cameraY = event.getY();
        double cameraZ = event.getZ();

        // Перемещаемся к позиции (текст всегда на месте posX, posY, posZ)
        matrix.translate(posX - cameraX, posY + 1.0 - cameraY, posZ - cameraZ);

        // Поворачиваем к камере
        matrix.rotate(mc.getRenderManager().getCameraOrientation());

        // Масштабируем текст
        float scale = 0.025F;
        matrix.scale(scale, -scale, scale);

        // Отражаем текст (как в DebugRenderer)
        matrix.scale(-1.0F, 1.0F, 1.0F);

        // Центрируем текст
        float textWidth = mc.fontRenderer.getStringWidth(text);
        matrix.translate(-textWidth / 2.0F, 0.0F, 0.0F);

        // Получаем финальную матрицу
        Matrix4f matrix4f = matrix.getLast().getMatrix();

        // Настройки рендеринга
        RenderSystem.enableTexture();
        RenderSystem.disableDepthTest(); // Виден сквозь стены
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();

        IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        Fonts.SF_MEDIUM.draw(matrix, text, 0, 0, -1, 9);
        buffer.finish();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableDepthTest();

        matrix.pop();
    }


    record HudEntry(String title, Item item, BindSetting bind, String nameFilterLower, Integer tintRGB) {
        HudEntry(String title, Item item, BindSetting bind) {
            this(title, item, bind, null, null);
        }

        HudEntry(String title, Item item, BindSetting bind, String nameFilter) {
            this(title, item, bind, nameFilter, null);
        }

        HudEntry(String title, Item item, BindSetting bind, String nameFilterLower, Integer tintRGB) {
            this.title = title;
            this.item = item;
            this.bind = bind;
            this.nameFilterLower = nameFilterLower == null ? null : nameFilterLower.toLowerCase();
            this.tintRGB = tintRGB;
        }
    }

    final class SwapEngine {
        final long RESTORE_DELAY_MS = 100;

        int originalHotbarSlot = -1;
        boolean needRestore = false;
        long restoreAtMs = 0L;
        long lastUseMs = 0L;

        int srcInvIndex = -1;
        boolean usedFromInventory = false;


        boolean use(Item item, String nameFilterLower, boolean allowOffhand) {
            if (mc.player == null || mc.world == null) return false;

            // Не используем предметы, если открыт инвентарь/сундук
            if (mc.currentScreen instanceof ContainerScreen) return false;

            long now = System.currentTimeMillis();
            if (now - lastUseMs < 50) return false;

            CooldownTracker cd = mc.player.getCooldownTracker();
            if (cd.hasCooldown(item)) return false;

            // offhand
            if (allowOffhand) {
                ItemStack off = mc.player.getHeldItemOffhand();
                if (!off.isEmpty() && off.getItem() == item && (nameFilterLower == null || containsName(off, nameFilterLower))) {
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                    lastUseMs = now;
                    return true;
                }
            }

            int hb = findInHotbar(item, nameFilterLower);
            if (hb != -1) {
                return useFromHotbar(hb);
            }

            int inv = findInInventory(item, nameFilterLower);
            if (inv != -1) {
                return useFromInventory(inv);
            }

            return false;
        }

        void tick() {
            if (needRestore && System.currentTimeMillis() >= restoreAtMs) {
                restore();
            }
        }

        void reset() {
            needRestore = false;
            originalHotbarSlot = -1;
            restoreAtMs = 0L;
            lastUseMs = 0L;
            usedFromInventory = false;
            srcInvIndex = -1;
        }

        // ----- helpers -----

        private boolean useFromHotbar(int targetHotbar) {
            int current = mc.player.inventory.currentItem;

            if (current != targetHotbar) {
                originalHotbarSlot = current;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(targetHotbar));
                mc.player.inventory.currentItem = targetHotbar;
            } else {
                originalHotbarSlot = -1; // уже на целевом
            }

            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));

            scheduleRestore();
            lastUseMs = System.currentTimeMillis();
            return true;
        }

        private boolean useFromInventory(int invIndex) {
            // берём в текущую руку через pickItem, не меняя выбранный хотбар-слот
            originalHotbarSlot = mc.player.inventory.currentItem;
            usedFromInventory = true;
            srcInvIndex = invIndex;

            mc.playerController.pickItem(invIndex); // invIndex <-> current hotbar

            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));

            scheduleRestore();
            lastUseMs = System.currentTimeMillis();
            handUtil.handleItemChange(System.currentTimeMillis() - delay > 200L);
            return true;
        }

        private void scheduleRestore() {
            needRestore = true;
            restoreAtMs = System.currentTimeMillis() + RESTORE_DELAY_MS;
        }

        private void restore() {
            if (!needRestore || mc.player == null) return;

            // 1) если брали из инвентаря — вернуть предмет назад в srcInvIndex
            if (usedFromInventory && srcInvIndex >= 9 && srcInvIndex < 36) {
                // в руке сейчас остаток предмета (или пусто, если израсходован)
                ItemStack held = mc.player.inventory.getStackInSlot(originalHotbarSlot);
                if (!held.isEmpty()) {
                    // вернуть обратно: swap хотбар <-> srcInvIndex
                    mc.playerController.pickItem(srcInvIndex);
                }
            }

            // 2) восстановить выбранный хотбар-слот (на случай внешних переключений)
            if (originalHotbarSlot >= 0 && originalHotbarSlot <= 8 &&
                    mc.player.inventory.currentItem != originalHotbarSlot) {
                mc.player.connection.sendPacket(new CHeldItemChangePacket(originalHotbarSlot));
                mc.player.inventory.currentItem = originalHotbarSlot;
            }

            // очистка флагов
            needRestore = false;
            originalHotbarSlot = -1;
            usedFromInventory = false;
            srcInvIndex = -1;
            restoreAtMs = 0L;
        }

        private int findInHotbar(Item item, String nameFilterLower) {
            for (int i = 0; i < 9; i++) {
                ItemStack st = mc.player.inventory.getStackInSlot(i);
                if (st.isEmpty()) continue;
                if (st.getItem() != item) continue;
                if (nameFilterLower != null && !containsName(st, nameFilterLower)) continue;
                return i;
            }
            return -1;
        }

        private int findInInventory(Item item, String nameFilterLower) {
            for (int i = 9; i < 36; i++) {
                ItemStack st = mc.player.inventory.getStackInSlot(i);
                if (st.isEmpty()) continue;
                if (st.getItem() != item) continue;
                if (nameFilterLower != null && !containsName(st, nameFilterLower)) continue;
                return i;
            }
            return -1;
        }

        public BindSetting getDezor() {
            return dezor;
        }

        public BindSetting getYavka() {
            return yavka;
        }

        public BindSetting getTrap() {
            return trap;
        }

        public BindSetting getPlast() {
            return plast;
        }

        public BindSetting getBojka() {
            return bojka;
        }

        public BindSetting getSnejok() {
            return snejok;
        }

        public BindSetting getSerka() {
            return serka;
        }

        public BindSetting getOtrigka() {
            return otrigka;
        }

        public BindSetting getKillera() {
            return killera;
        }

        public BindSetting getMedik() {
            return medik;
        }

        public BindSetting getPobedka() {
            return pobedka;
        }

        public BindSetting getAgenta() {
            return agenta;
        }

        private boolean containsName(ItemStack st, String nameLower) {
            String nm = TextFormatting.getTextWithoutFormattingCodes(st.getDisplayName().getString());
            return nm != null && nm.toLowerCase().contains(nameLower);
        }
    }
}