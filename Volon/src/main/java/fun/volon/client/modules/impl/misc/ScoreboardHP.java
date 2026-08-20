package fun.volon.client.modules.impl.misc;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.Text;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.misc.NameProtect;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreboardHP extends Module {

    public static final ScoreboardHP INSTANCE = new ScoreboardHP();

    private static final Pattern HP_NUMBER = Pattern.compile("(\\d+(?:[.,]\\d+)?)");
    private static final float MAX_REASONABLE_HP = 1024.0f;

    // Специальное значение, обозначающее "Неизвестно"
    public static final float UNKNOWN_HP = -1.0f;

    private final BooleanSetting gulpvp = new BooleanSetting("GulpVP", false);

    public ScoreboardHP() {
        super("ScoreboardHP", "Обход показа HP для серверов", ModuleCategory.MISC);
        addSettings(gulpvp);
    }

    /**
     * Проверка, нужно ли скрывать HP (возвращать UNKNOWN_HP)
     */
    private static boolean shouldHideHealth(PlayerEntity player) {
        if (!INSTANCE.isEnable() || !INSTANCE.gulpvp.isState()) {
            return false;
        }

        // Проверка на сервер gulpvp.pw
        if (mc.getCurrentServerEntry() != null) {
            String serverAddress = mc.getCurrentServerEntry().address;
            if (serverAddress != null && serverAddress.toLowerCase().contains("gulpvp.pw")) {
                return true;
            }
        }

        // Проверка, скрыто ли ХП (стоит обход)
        // Если на сервере нет стандартного отображения HP в скорборде или ниже имени
        if (isHealthHiddenOnServer(player)) {
            return true;
        }

        return false;
    }

    /**
     * Определяет, скрыто ли стандартное отображение HP на сервере
     */
    private static boolean isHealthHiddenOnServer(PlayerEntity player) {
        if (mc.world == null) return false;

        try {
            Scoreboard scoreboard = mc.world.getScoreboard();

            // Проверяем, есть ли вообще какой-то объектив для отображения HP
            ScoreboardObjective belowName = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
            ScoreboardObjective list = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);

            // Если нет стандартных объективов для HP - вероятно ХП скрыто
            if (belowName == null && list == null) {
                // Дополнительная проверка: пытаемся получить HP из сайдбара (кастомный обход)
                Float sidebarHp = getGulpVpSidebarHealth(player);
                return sidebarHp == null; // Если и в сайдбаре нет - ХП точно скрыто
            }

            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static float getHealth(LivingEntity entity) {
        if (entity == null) {
            return 0.0f;
        }

        if (!INSTANCE.isEnable()) {
            return entity.getHealth();
        }

        if (entity instanceof ClientPlayerEntity) {
            return entity.getHealth();
        }

        if (!(entity instanceof PlayerEntity player)) {
            return entity.getHealth();
        }

        // Новая проверка: если нужно скрыть HP - возвращаем UNKNOWN_HP
        if (shouldHideHealth(player)) {
            return UNKNOWN_HP;
        }

        if (INSTANCE.gulpvp.isState()) {
            Float sidebarHp = getGulpVpSidebarHealth(player);
            if (sidebarHp != null) {
                return sidebarHp;
            }
        }

        if (mc.getCurrentServerEntry() == null) {
            return entity.getHealth();
        }

        return getObjectiveHealth(player);
    }

    public static float getHealthWithAbsorption(LivingEntity entity) {
        if (entity == null) {
            return 0.0f;
        }

        if (!INSTANCE.isEnable()) {
            return Math.max(0.0f, entity.getHealth() + entity.getAbsorptionAmount());
        }

        if (entity instanceof ClientPlayerEntity) {
            return Math.max(0.0f, entity.getHealth() + entity.getAbsorptionAmount());
        }

        if (!(entity instanceof PlayerEntity player)) {
            return Math.max(0.0f, getHealth(entity) + entity.getAbsorptionAmount());
        }

        // Новая проверка: если нужно скрыть HP - возвращаем UNKNOWN_HP
        if (shouldHideHealth(player)) {
            return UNKNOWN_HP;
        }

        if (INSTANCE.gulpvp.isState() && entity instanceof PlayerEntity) {
            Float sidebarHp = getGulpVpSidebarHealth(player);
            if (sidebarHp != null) {
                return Math.max(0.0f, sidebarHp);
            }
        }

        return Math.max(0.0f, getHealth(entity) + entity.getAbsorptionAmount());
    }

    /**
     * Метод для проверки, нужно ли показывать "Неизвестно" в TargetHUD
     */
    public static boolean shouldShowUnknownInTargetHud(LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }
        return shouldHideHealth(player);
    }

    private static float getObjectiveHealth(PlayerEntity player) {
        try {
            Scoreboard scoreboard = player.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
            if (objective == null) {
                objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
            }
            if (objective == null) {
                return player.getHealth();
            }

            ReadableScoreboardScore score = scoreboard.getScore(player, objective);
            if (score == null) {
                return player.getHealth();
            }

            return score.getScore();
        } catch (Exception ignored) {
            return player.getHealth();
        }
    }

    private static Float getGulpVpSidebarHealth(PlayerEntity player) {
        if (mc.world == null) {
            return null;
        }

        try {
            Scoreboard scoreboard = mc.world.getScoreboard();
            ScoreboardObjective sidebar = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            if (sidebar == null) {
                return null;
            }

            NumberFormat numberFormat = sidebar.getNumberFormatOr(StyledNumberFormat.RED);
            String[] nameVariants = collectNameVariants(player);
            Float bestHp = null;
            int bestMatchScore = -1;

            for (ScoreboardEntry entry : scoreboard.getScoreboardEntries(sidebar)) {
                if (entry.hidden()) {
                    continue;
                }

                Team team = scoreboard.getScoreHolderTeam(entry.owner());
                String lineText = stripFormatting(Team.decorateName(team, entry.name()).getString());
                String ownerText = stripFormatting(entry.owner());
                String scoreText = stripFormatting(entry.formatted(numberFormat).getString());

                int matchScore = getNameMatchScore(lineText, ownerText, nameVariants);
                if (matchScore < 0) {
                    continue;
                }

                Float hp = extractSidebarHp(entry, scoreText, lineText);
                if (hp == null) {
                    continue;
                }

                if (matchScore > bestMatchScore) {
                    bestMatchScore = matchScore;
                    bestHp = hp;
                }
            }

            return bestHp;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Float extractSidebarHp(ScoreboardEntry entry, String scoreText, String lineText) {
        Float fromScoreColumn = parseHpNumber(scoreText);
        if (fromScoreColumn != null) {
            return fromScoreColumn;
        }

        if (isReasonableHp(entry.value())) {
            return (float) entry.value();
        }

        Float fromLine = parseHpFromLine(lineText);
        if (fromLine != null) {
            return fromLine;
        }

        return null;
    }

    private static String[] collectNameVariants(PlayerEntity player) {
        String plainName = stripFormatting(player.getName().getString());
        String scoreboardName = stripFormatting(player.getNameForScoreboard());
        String profileName = player.getGameProfile() != null
                ? stripFormatting(player.getGameProfile().getName())
                : "";

        String tabName = "";
        if (mc.getNetworkHandler() != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
            if (entry != null) {
                Text displayName = entry.getDisplayName();
                if (displayName != null) {
                    tabName = stripFormatting(displayName.getString());
                }
            }
        }

        String protectedPlain = protectName(plainName);
        String protectedScoreboard = protectName(scoreboardName);
        String protectedProfile = protectName(profileName);
        String protectedTab = protectName(tabName);

        return dedupeNames(
                plainName,
                protectedPlain,
                scoreboardName,
                protectedScoreboard,
                profileName,
                protectedProfile,
                tabName,
                protectedTab
        );
    }

    private static String[] dedupeNames(String... names) {
        java.util.LinkedHashSet<String> unique = new java.util.LinkedHashSet<>();
        for (String name : names) {
            if (name == null) {
                continue;
            }
            String trimmed = name.trim();
            if (!trimmed.isEmpty()) {
                unique.add(trimmed);
            }
        }
        return unique.toArray(String[]::new);
    }

    private static String protectName(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        NameProtect nameProtect = ModuleClass.INSTANCE != null ? ModuleClass.INSTANCE.nameProtect : null;
        if (nameProtect == null || !nameProtect.isEnable()) {
            return input;
        }
        return nameProtect.patch(input);
    }

    private static int getNameMatchScore(String lineText, String ownerText, String[] nameVariants) {
        int best = -1;

        for (String name : nameVariants) {
            if (name == null || name.isEmpty()) {
                continue;
            }

            if (!ownerText.isEmpty()) {
                if (ownerText.equalsIgnoreCase(name)) {
                    best = Math.max(best, 100);
                } else if (ownerText.contains(name)) {
                    best = Math.max(best, 80);
                }
            }

            if (lineText.isEmpty()) {
                continue;
            }

            if (lineText.equalsIgnoreCase(name)) {
                best = Math.max(best, 95);
                continue;
            }

            if (lineText.endsWith(name) || lineText.endsWith(" " + name)) {
                best = Math.max(best, 90);
                continue;
            }

            int index = indexOfIgnoreCase(lineText, name);
            if (index >= 0) {
                best = Math.max(best, 70 + Math.min(20, name.length()));
            }
        }

        return best;
    }

    private static int indexOfIgnoreCase(String text, String search) {
        if (text == null || search == null || search.isEmpty()) {
            return -1;
        }
        int limit = text.length() - search.length();
        for (int i = 0; i <= limit; i++) {
            if (text.regionMatches(true, i, search, 0, search.length())) {
                return i;
            }
        }
        return -1;
    }

    private static Float parseHpNumber(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        String cleaned = text.replace("❤", "")
                .replace("♥", "")
                .replace("HP", "")
                .replace("hp", "")
                .replace("хп", "")
                .replace("Хп", "")
                .trim();

        Matcher matcher = HP_NUMBER.matcher(cleaned);
        if (!matcher.find()) {
            return null;
        }

        try {
            float value = Float.parseFloat(matcher.group(1).replace(',', '.'));
            return isReasonableHp(value) ? value : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Float parseHpFromLine(String line) {
        if (line == null || line.isEmpty()) {
            return null;
        }

        Matcher matcher = HP_NUMBER.matcher(line);
        Float last = null;
        while (matcher.find()) {
            try {
                float value = Float.parseFloat(matcher.group(1).replace(',', '.'));
                if (isReasonableHp(value)) {
                    last = value;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return last;
    }

    private static boolean isReasonableHp(float value) {
        return value >= 0.0f && value <= MAX_REASONABLE_HP;
    }

    private static boolean isReasonableHp(int value) {
        return value >= 0 && value <= (int) MAX_REASONABLE_HP;
    }

    private static String stripFormatting(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("§[0-9a-fk-orx]", "")
                .replaceAll("(?i)§x(§[0-9a-f]){6}", "")
                .trim();
    }
}