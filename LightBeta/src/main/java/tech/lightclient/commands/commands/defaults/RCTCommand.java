package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RCTCommand extends Command implements ILogger {

    // "СолоЛайт 8", "Дуо Лайт #14", "ТриоЛайт '30'"
    private static final Pattern LITE_PATTERN = Pattern.compile(
            "(соло|дуо|трио|клан)\\s*лайт[^0-9]{0,6}(\\d{1,3})",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    private static final int COMPASS_MENU_SLOT = 13;
    private static final int MAX_ANARCHY = 51;

    private enum Phase { IDLE, HUB, COMPASS, COMPASS_MENU, CATEGORY_MENU, ANARCHY_MENU }

    private boolean subscribed;

    private Phase phase = Phase.IDLE;
    private long startedAt;
    private long phaseStartedAt;
    private int waitTicks;
    private ClientWorld hubWorld;
    private int targetAnarchy;
    private int categorySlot;
    private int anarchySlot;
    private int lastSyncId = -1;
    private int lastSize = -1;

    protected RCTCommand() {
        super("rct");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (mc.player == null || mc.world == null) {
            logDirect("Вы не в игре", Formatting.RED);
            return;
        }

        int anarchy;
        if (args.hasAny()) {
            args.requireExactly(1);
            Integer parsed = args.getAsOrNull(Integer.class);
            if (parsed == null) {
                logDirect("Использование: .rct [1-" + MAX_ANARCHY + "]", Formatting.RED);
                return;
            }
            anarchy = parsed;
        } else {
            anarchy = getCurrentAnarchy();
            if (anarchy == -1) {
                logDirect("Не удалось определить анархию по скорборду. Укажите номер: .rct <1-" + MAX_ANARCHY + ">", Formatting.RED);
                return;
            }
        }

        if (anarchy < 1 || anarchy > MAX_ANARCHY) {
            logDirect("Анархия должна быть от 1 до " + MAX_ANARCHY, Formatting.RED);
            return;
        }

        targetAnarchy = anarchy;
        categorySlot = categoryOf(anarchy);
        anarchySlot = slotOf(anarchy);

        if (!subscribed) {
            LightClient.getEventBus().subscribe(this);
            subscribed = true;
        }

        hubWorld = mc.world;
        lastSyncId = -1;
        lastSize = -1;
        waitTicks = 0;
        phase = Phase.IDLE;
        mc.player.networkHandler.sendChatCommand("hub");
        logDirect("Перезаход: " + categoryName(anarchy) + " " + anarchy, Formatting.GREEN);
        setPhase(Phase.HUB);
    }

    @EventHandler
    public void onTick(UpdateEvent event) {
        if (phase == Phase.IDLE) return;

        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        if (System.currentTimeMillis() - startedAt > 40_000L) {
            fail("Перезаход отменён (таймаут)");
            return;
        }

        if (waitTicks > 0) {
            waitTicks--;
            return;
        }

        switch (phase) {
            case HUB -> tickHub();
            case COMPASS -> tickCompass();
            case COMPASS_MENU -> tickCompassMenu();
            case CATEGORY_MENU -> tickCategoryMenu();
            case ANARCHY_MENU -> tickAnarchyMenu();
            default -> {
            }
        }
    }

    private void tickHub() {
        if (mc.world != hubWorld) {
            hubWorld = mc.world;
            setPhase(Phase.COMPASS);
            return;
        }
        // Уже в хабе — сразу к компасу
        if (phaseElapsed(600)) setPhase(Phase.COMPASS);
    }

    private void tickCompass() {
        if (container() != null) {
            setPhase(Phase.COMPASS_MENU);
            return;
        }

        if (!mc.player.getMainHandStack().isOf(Items.COMPASS)) {
            int slot = findCompass();
            if (slot == -1) {
                // Инвентарь может быть ещё не прогружен после смены сервера
                if (phaseElapsed(5000)) fail("Компас не найден в хотбаре");
                return;
            }
            mc.player.getInventory().setSelectedSlot(slot);
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }

        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        setPhase(Phase.COMPASS_MENU);
    }

    private void tickCompassMenu() {
        GenericContainerScreenHandler c = container();
        if (c == null) {
            if (phaseElapsed(600)) setPhase(Phase.COMPASS);
            return;
        }
        if (c.getInventory().size() < COMPASS_MENU_SLOT) {
            if (phaseElapsed(3000)) fail("Открылось не то меню");
            return;
        }
        if (!slotReady(c, COMPASS_MENU_SLOT - 1)) return;

        remember(c);
        click(c, COMPASS_MENU_SLOT - 1);
        setPhase(Phase.CATEGORY_MENU);
    }

    private void tickCategoryMenu() {
        GenericContainerScreenHandler c = container();
        if (!isNewMenu(c) || c.getInventory().size() < categorySlot) {
            if (phaseElapsed(4000)) fail("Меню категорий не открылось");
            return;
        }
        if (!slotReady(c, categorySlot - 1)) return;

        remember(c);
        click(c, categorySlot - 1);
        setPhase(Phase.ANARCHY_MENU);
    }

    private void tickAnarchyMenu() {
        GenericContainerScreenHandler c = container();
        if (!isNewMenu(c)) {
            if (phaseElapsed(4000)) fail("Меню анархий не открылось");
            return;
        }
        if (c.getInventory().size() < anarchySlot) {
            if (phaseElapsed(1000)) fail("Слот " + anarchySlot + " вне меню (размер " + c.getInventory().size() + ")");
            return;
        }
        if (!slotReady(c, anarchySlot - 1)) return;

        click(c, anarchySlot - 1);
        logDirect("Заходим на " + categoryName(targetAnarchy) + " " + targetAnarchy, Formatting.GREEN);
        setPhase(Phase.IDLE);
    }

    /** Слот готов к клику: предмет уже прислан сервером, либо истекло короткое ожидание. */
    private boolean slotReady(GenericContainerScreenHandler c, int index) {
        return !c.getInventory().getStack(index).isEmpty() || phaseElapsed(400);
    }

    private GenericContainerScreenHandler container() {
        return mc.player.currentScreenHandler instanceof GenericContainerScreenHandler c ? c : null;
    }

    /** Меню считается новым, если сменился syncId или размер (сервер открыл/подменил окно). */
    private boolean isNewMenu(GenericContainerScreenHandler c) {
        return c != null && (c.syncId != lastSyncId || c.getInventory().size() != lastSize);
    }

    private void remember(GenericContainerScreenHandler c) {
        lastSyncId = c.syncId;
        lastSize = c.getInventory().size();
    }

    private void click(GenericContainerScreenHandler c, int index) {
        mc.interactionManager.clickSlot(c.syncId, index, 0, SlotActionType.PICKUP, mc.player);
    }

    private int findCompass() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.COMPASS)) return i;
        }
        return -1;
    }

    private void setPhase(Phase newPhase) {
        if (phase == Phase.IDLE && newPhase != Phase.IDLE) startedAt = System.currentTimeMillis();
        phase = newPhase;
        phaseStartedAt = System.currentTimeMillis();
    }

    private boolean phaseElapsed(long ms) {
        return System.currentTimeMillis() - phaseStartedAt >= ms;
    }

    private void fail(String reason) {
        phase = Phase.IDLE;
        logDirect(reason, Formatting.RED);
    }

    /** Слот категории в меню 1x9: Соло=1, Дуо=2, Трио=3, Клан=4. */
    private static int categoryOf(int n) {
        if (n <= 12) return 1;
        if (n <= 26) return 2;
        if (n <= 39) return 3;
        return 4;
    }

    /** Слот анархии внутри меню категории (1-based, как в описании сервера). */
    private static int slotOf(int n) {
        if (n <= 12) return n + 18;   // 1..12  -> 19..30
        if (n <= 26) return n + 6;    // 13..26 -> 19..32
        if (n <= 39) return n - 8;    // 27..39 -> 19..31
        return n - 21;                // 40..51 -> 19..30
    }

    private static String categoryName(int n) {
        return switch (categoryOf(n)) {
            case 1 -> "Соло Лайт";
            case 2 -> "Дуо Лайт";
            case 3 -> "Трио Лайт";
            default -> "Клан Лайт";
        };
    }

    /** Номер текущей анархии по скорборду, -1 если не найден. */
    private int getCurrentAnarchy() {
        if (mc.world == null || mc.player == null) return -1;
        Scoreboard scoreboard = mc.world.getScoreboard();
        if (scoreboard == null) return -1;

        ScoreboardObjective objective = null;
        Team team = scoreboard.getScoreHolderTeam(mc.player.getNameForScoreboard());
        if (team != null) {
            ScoreboardDisplaySlot slot = ScoreboardDisplaySlot.fromFormatting(team.getColor());
            if (slot != null) objective = scoreboard.getObjectiveForSlot(slot);
        }
        if (objective == null) objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (objective == null) return -1;

        int found = parse(objective.getDisplayName().getString());
        if (found != -1) return found;

        for (ScoreboardEntry entry : scoreboard.getScoreboardEntries(objective)) {
            if (entry.hidden()) continue;
            Team entryTeam = scoreboard.getScoreHolderTeam(entry.owner());
            found = parse(Team.decorateName(entryTeam, entry.name()).getString());
            if (found != -1) return found;
        }
        return -1;
    }

    private static int parse(String raw) {
        String text = stripSectionCodes(raw);
        if (text.isEmpty()) return -1;
        Matcher matcher = LITE_PATTERN.matcher(text);
        if (!matcher.find()) return -1;
        try {
            int n = Integer.parseInt(matcher.group(2));
            return n >= 1 && n <= MAX_ANARCHY ? n : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String stripSectionCodes(String input) {
        if (input == null) return "";
        if (input.indexOf('\u00A7') < 0) return input;

        StringBuilder result = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '\u00A7') {
                if (i + 1 < input.length()) i++;
                continue;
            }
            result.append(ch);
        }
        return result.toString();
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            return Stream.iterate(1, i -> i + 1).limit(MAX_ANARCHY).map(String::valueOf);
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Перезаходит на анархию";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Перезаходит на Лайт-анархию через /hub и компас",
                "",
                "Использование:",
                "> rct <anarchy> - Заходит на <anarchy> (1-" + MAX_ANARCHY + ")",
                "> rct - Перезаходит на анархию, определённую по скорборду"
        );
    }
}
