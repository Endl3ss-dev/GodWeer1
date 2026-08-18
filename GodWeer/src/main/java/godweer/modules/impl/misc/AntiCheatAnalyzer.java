package godweer.modules.impl.misc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class AntiCheatAnalyzer {

    private static final int MAX_EVIDENCE = 160;
    private static final int MAX_MESSAGES = 80;
    private static final int LATENCY_BUCKETS = 6;
    private static final Pattern VERSION_PATTERN = Pattern.compile(
            "(?i)(?:version|ver|build|v)\\s*[:#=\\-]?\\s*(\\d+(?:\\.\\d+){1,3}(?:[-+._][a-z0-9]+)?)"
    );

    private final EnumMap<Family, Candidate> candidates = new EnumMap<>(Family.class);
    private final Set<String> evidenceKeys = new HashSet<>();
    private final Set<String> observedCommands = new HashSet<>();
    private final Set<String> observedPayloads = new HashSet<>();
    private final Set<String> observedMessages = new HashSet<>();
    private final List<String> evidence = new ArrayList<>();
    private final List<String> relevantSystemMessages = new ArrayList<>();
    private final long[] stageTicks = new long[AntiCheatDetector.ScanStage.values().length];
    private final int[] stageReactionCorrections = new int[AntiCheatDetector.ScanStage.values().length];
    private final int[] stageAmbientCorrections = new int[AntiCheatDetector.ScanStage.values().length];
    private final int[] stageTeleports = new int[AntiCheatDetector.ScanStage.values().length];
    private final int[] stageActions = new int[AntiCheatDetector.ScanStage.values().length];
    private final double[] stageCorrectionDistance = new double[AntiCheatDetector.ScanStage.values().length];
    private final double[] stageMaximumCorrection = new double[AntiCheatDetector.ScanStage.values().length];
    private final int[] actionAttempts = new int[AntiCheatDetector.ProbeAction.values().length];
    private final int[] actionSuccesses = new int[AntiCheatDetector.ProbeAction.values().length];
    private final int[] actionReactions = new int[AntiCheatDetector.ProbeAction.values().length];
    private final int[][] actionLatencyBuckets = new int[AntiCheatDetector.ProbeAction.values().length][LATENCY_BUCKETS];
    private final long[] actionLatencyTotal = new long[AntiCheatDetector.ProbeAction.values().length];

    private String serverAddress = "unknown";
    private String serverName = "unknown";
    private String serverBrand = "unknown";
    private String minecraftVersion = "1.21.4";
    private Instant startedAt;
    private Instant finishedAt;
    private int customPayloads;
    private int commandNodes;
    private int disconnects;
    private int totalCorrectionPackets;
    private int totalProbeReactions;
    private int totalAmbientCorrections;
    private int totalTeleports;

    AntiCheatAnalyzer() {
        for (Family family : Family.values()) {
            candidates.put(family, new Candidate(family));
        }
    }

    void begin(String address, String name, String brand, String clientVersion) {
        serverAddress = sanitize(address);
        serverName = sanitize(name);
        serverBrand = sanitize(brand);
        minecraftVersion = sanitize(clientVersion);
        startedAt = Instant.now();
        observeText("server-brand", serverBrand, 35);
    }

    void finish() {
        finishedAt = Instant.now();
    }

    void updateBrand(String brand) {
        if (brand == null || brand.isBlank() || brand.equals(serverBrand)) {
            return;
        }
        serverBrand = sanitize(brand);
        addEvidence("brand", serverBrand);
        observeText("server-brand", serverBrand, 35);
    }

    void recordTick(AntiCheatDetector.ScanStage stage) {
        stageTicks[stage.ordinal()]++;
    }

    void recordActionAttempt(AntiCheatDetector.ProbeAction action) {
        actionAttempts[action.ordinal()]++;
    }

    void recordActionSuccess(AntiCheatDetector.ScanStage stage, AntiCheatDetector.ProbeAction action) {
        stageActions[stage.ordinal()]++;
        actionSuccesses[action.ordinal()]++;
    }

    void recordCorrection(AntiCheatDetector.ScanStage stage, double distance,
                          AntiCheatDetector.CorrectionKind kind,
                          AntiCheatDetector.ProbeAction action, int latencyTicks) {
        totalCorrectionPackets++;
        int stageIndex = stage.ordinal();
        switch (kind) {
            case PROBE_REACTION -> {
                totalProbeReactions++;
                stageReactionCorrections[stageIndex]++;
                stageCorrectionDistance[stageIndex] += distance;
                if (distance > stageMaximumCorrection[stageIndex]) {
                    stageMaximumCorrection[stageIndex] = distance;
                }
                if (action != null) {
                    int actionIndex = action.ordinal();
                    actionReactions[actionIndex]++;
                    actionLatencyTotal[actionIndex] += latencyTicks;
                    actionLatencyBuckets[actionIndex][latencyBucket(latencyTicks)]++;
                }
                addEvidence("probe-reaction", stage.getDisplayName() + ", action="
                        + (action == null ? "unknown" : action.getDisplayName())
                        + ", distance=" + format(distance) + ", latency=" + latencyTicks + " ticks");
            }
            case AMBIENT -> {
                totalAmbientCorrections++;
                stageAmbientCorrections[stageIndex]++;
            }
            case TELEPORT -> {
                totalTeleports++;
                stageTeleports[stageIndex]++;
            }
        }
    }

    void recordCommand(String command) {
        if (command == null || command.isBlank()) {
            return;
        }
        String normalized = command.toLowerCase(Locale.ROOT);
        if (!observedCommands.add(normalized)) {
            return;
        }
        commandNodes++;
        observeText("command-tree", command, 72);
    }

    void recordCustomPayload(String channel) {
        if (channel == null || channel.isBlank()) {
            return;
        }
        String normalized = channel.toLowerCase(Locale.ROOT);
        if (!observedPayloads.add(normalized)) {
            return;
        }
        customPayloads++;
        observeText("payload", channel, 92);
        if (!isCommonChannel(channel)) {
            addEvidence("payload", channel);
        }
    }

    void recordSystemMessage(String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        String clean = sanitize(message);
        if (!observedMessages.add(clean)) {
            return;
        }
        boolean signatureMatched = observeText("system-message", clean, 58);
        if ((signatureMatched || isReactionMessage(clean)) && relevantSystemMessages.size() < MAX_MESSAGES) {
            relevantSystemMessages.add(clean);
        }
    }

    void recordDisconnect(String reason) {
        disconnects++;
        String clean = sanitize(reason);
        addEvidence("disconnect", clean);
        observeText("disconnect", clean, 82);
        if (isReactionMessage(clean) && relevantSystemMessages.size() < MAX_MESSAGES) {
            relevantSystemMessages.add("disconnect: " + clean);
        }
    }

    String buildReport(long requestedDurationSeconds, long actualDurationSeconds, String finishReason) {
        List<Candidate> ranked = new ArrayList<>(candidates.values());
        ranked.sort((left, right) -> Integer.compare(right.score, left.score));

        StringBuilder report = new StringBuilder(10_240);
        report.append("GodWeer AntiCheat Detector report\n");
        report.append("=================================\n");
        report.append("Server: ").append(serverName).append(" (").append(serverAddress).append(")\n");
        report.append("Server brand: ").append(serverBrand).append('\n');
        report.append("Minecraft client: ").append(minecraftVersion).append('\n');
        report.append("Started UTC: ").append(startedAt == null ? "unknown" : startedAt).append('\n');
        report.append("Finished UTC: ").append(finishedAt == null ? Instant.now() : finishedAt).append('\n');
        report.append("Finish reason: ").append(finishReason).append('\n');
        report.append("Requested duration: ").append(requestedDurationSeconds).append(" s\n");
        report.append("Actual duration: ").append(actualDurationSeconds).append(" s\n\n");

        report.append("Result\n");
        report.append("------\n");
        Candidate best = ranked.getFirst();
        if (best.score < 25) {
            report.append("Family: UNKNOWN\n");
            report.append("Confidence: insufficient metadata\n");
            report.append("Version: impossible to infer reliably\n");
            report.append("Reason: the server did not expose a unique command, payload, brand or message signature.\n");
        } else {
            int displayed = 0;
            for (int i = 0; i < ranked.size() && displayed < 4; i++) {
                Candidate candidate = ranked.get(i);
                if (candidate.score < 18) {
                    continue;
                }
                displayed++;
                report.append(displayed).append(") ").append(candidate.family.displayName)
                        .append(" — score ").append(Math.min(candidate.score, 100)).append("/100, ")
                        .append(confidence(candidate.score)).append('\n');
                report.append("   Version: ");
                if (candidate.version != null) {
                    report.append(candidate.version).append(" (version-like value near the server signature; verify manually)");
                } else {
                    report.append(candidate.family.versionHint).append(" (family range only, not an exact version)");
                }
                report.append('\n');
                for (int sourceIndex = 0; sourceIndex < candidate.sources.size(); sourceIndex++) {
                    report.append("   Evidence: ").append(candidate.sources.get(sourceIndex)).append('\n');
                }
            }
        }

        report.append("\nProbe reactions\n");
        report.append("---------------\n");
        AntiCheatDetector.ScanStage[] stages = AntiCheatDetector.ScanStage.values();
        for (int i = 0; i < stages.length; i++) {
            double average = stageReactionCorrections[i] == 0
                    ? 0.0D : stageCorrectionDistance[i] / stageReactionCorrections[i];
            report.append(stages[i].getDisplayName()).append(": ")
                    .append(stageTicks[i] / 20L).append(" s, actions=").append(stageActions[i])
                    .append(", reactions=").append(stageReactionCorrections[i])
                    .append(", ambient=").append(stageAmbientCorrections[i])
                    .append(", teleports=").append(stageTeleports[i])
                    .append(", avg=").append(format(average))
                    .append(", max=").append(format(stageMaximumCorrection[i])).append('\n');
        }

        report.append("\nAction verification and response latency\n");
        report.append("----------------------------------------\n");
        AntiCheatDetector.ProbeAction[] actions = AntiCheatDetector.ProbeAction.values();
        for (int i = 0; i < actions.length; i++) {
            double averageLatency = actionReactions[i] == 0
                    ? 0.0D : (double) actionLatencyTotal[i] / actionReactions[i];
            report.append(actions[i].getDisplayName())
                    .append(": attempted=").append(actionAttempts[i])
                    .append(", confirmed=").append(actionSuccesses[i])
                    .append(", reactions=").append(actionReactions[i])
                    .append(", avg-latency=").append(format(averageLatency)).append(" ticks")
                    .append(", latency-buckets=")
                    .append("0-2:").append(actionLatencyBuckets[i][0]).append(' ')
                    .append("3-5:").append(actionLatencyBuckets[i][1]).append(' ')
                    .append("6-10:").append(actionLatencyBuckets[i][2]).append(' ')
                    .append("11-20:").append(actionLatencyBuckets[i][3]).append(' ')
                    .append("21-40:").append(actionLatencyBuckets[i][4]).append(' ')
                    .append("41-60:").append(actionLatencyBuckets[i][5]).append('\n');
        }
        report.append("Correction packets: total=").append(totalCorrectionPackets)
                .append(", probe-reactions=").append(totalProbeReactions)
                .append(", ambient=").append(totalAmbientCorrections)
                .append(", teleports-filtered=").append(totalTeleports)
                .append(", custom-payloads=").append(customPayloads)
                .append(", command-nodes=").append(commandNodes)
                .append(", disconnects=").append(disconnects).append('\n');

        report.append("\nRaw evidence\n");
        report.append("------------\n");
        if (evidence.isEmpty()) {
            report.append("none\n");
        } else {
            for (int i = 0; i < evidence.size(); i++) {
                report.append("- ").append(evidence.get(i)).append('\n');
            }
        }

        report.append("\nRelevant AC/system messages\n");
        report.append("---------------------------\n");
        if (relevantSystemMessages.isEmpty()) {
            report.append("none\n");
        } else {
            for (int i = 0; i < relevantSystemMessages.size(); i++) {
                report.append("- ").append(relevantSystemMessages.get(i)).append('\n');
            }
        }

        report.append("\nInterpretation limits\n");
        report.append("---------------------\n");
        report.append("- Only corrections within 60 ticks after a confirmed probe count as probe reactions.\n");
        report.append("- Position changes above 16 blocks are classified as teleports, not anti-cheat setbacks.\n");
        report.append("- Exact plugin versions are reported only when the server exposes a version-like signature.\n");
        report.append("- Proxy networks can hide or combine several anti-cheats.\n");
        report.append("- Boundary mode uses unusual but valid packets and never sends malformed or exploit payloads.\n");
        return report.toString();
    }

    private boolean observeText(String source, String value, int weight) {
        String normalized = value.toLowerCase(Locale.ROOT);
        boolean matched = false;
        for (Family family : Family.values()) {
            String matchedToken = family.findToken(normalized);
            if (matchedToken == null) {
                continue;
            }
            matched = true;
            Candidate candidate = candidates.get(family);
            int effectiveWeight = weight;
            if (source.equals("command-tree") && !isCommandMatch(normalized, matchedToken)) {
                effectiveWeight = Math.min(effectiveWeight, 34);
            }
            candidate.addScore(effectiveWeight, source + " contains '" + matchedToken + "'");
            String version = extractVersion(normalized);
            if (version != null) {
                candidate.version = version;
            }
            addEvidence(source, value);
        }
        return matched;
    }

    private boolean isReactionMessage(String value) {
        String normalized = value.toLowerCase(Locale.ROOT);
        return normalized.contains("violation")
                || normalized.contains("failed check")
                || normalized.contains("invalid movement")
                || normalized.contains("illegal movement")
                || normalized.contains("suspicious movement")
                || normalized.contains("setback")
                || normalized.contains("flagged")
                || normalized.contains("cheating")
                || normalized.contains("anti cheat")
                || normalized.contains("anti-cheat")
                || normalized.contains("античит")
                || normalized.contains("нарушение")
                || normalized.contains("подозрительное движение")
                || normalized.contains("обнаружен чит")
                || normalized.contains("проверка не пройдена");
    }

    private boolean isCommandMatch(String command, String token) {
        String clean = command.startsWith("/") ? command.substring(1) : command;
        int namespace = clean.indexOf(':');
        if (namespace >= 0 && namespace + 1 < clean.length()) {
            clean = clean.substring(namespace + 1);
        }
        return clean.equals(token) || clean.startsWith(token + " ") || clean.startsWith(token + ":");
    }

    private String extractVersion(String text) {
        Matcher matcher = VERSION_PATTERN.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }

    private int latencyBucket(int latencyTicks) {
        if (latencyTicks <= 2) {
            return 0;
        }
        if (latencyTicks <= 5) {
            return 1;
        }
        if (latencyTicks <= 10) {
            return 2;
        }
        if (latencyTicks <= 20) {
            return 3;
        }
        if (latencyTicks <= 40) {
            return 4;
        }
        return 5;
    }

    private void addEvidence(String type, String value) {
        if (evidence.size() >= MAX_EVIDENCE) {
            return;
        }
        String clean = sanitize(value);
        String key = type + '\u0000' + clean;
        if (evidenceKeys.add(key)) {
            evidence.add(type + ": " + clean);
        }
    }

    private boolean isCommonChannel(String channel) {
        String normalized = channel.toLowerCase(Locale.ROOT);
        return normalized.equals("minecraft:brand")
                || normalized.startsWith("minecraft:")
                || normalized.startsWith("fabric:")
                || normalized.startsWith("fabric-screen-handler-api-v1:");
    }

    private String confidence(int score) {
        if (score >= 90) {
            return "high confidence";
        }
        if (score >= 60) {
            return "medium confidence";
        }
        return "low confidence";
    }

    private String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        StringBuilder clean = new StringBuilder(Math.min(value.length(), 300));
        int limit = Math.min(value.length(), 300);
        for (int i = 0; i < limit; i++) {
            char character = value.charAt(i);
            if (character == '§' && i + 1 < limit) {
                i++;
                continue;
            }
            if (character >= 32 && character != 127) {
                clean.append(character);
            }
        }
        return clean.toString().trim();
    }

    private String format(double value) {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private enum Family {
        GRIM("GrimAC", "2.x/3.x compatible branch", "grimac", "grim"),
        VULCAN("Vulcan", "2.x compatible branch", "vulcan"),
        MATRIX("Matrix", "7.x compatible branch", "matrix"),
        NO_CHEAT_PLUS("NoCheatPlus", "NCP/Updated-NCP branch", "nocheatplus", "updatedncp", "ncp"),
        AAC("AAC", "5.x/6.x compatible branch", "aac5", "aac6", "aac"),
        INTAVE("Intave", "14.x compatible branch", "intave"),
        VERUS("Verus", "current 1.21-compatible branch", "verus"),
        SPARTAN("Spartan", "Phase 5/1.21-compatible branch", "spartan"),
        POLAR("Polar", "current hosted branch", "polar"),
        KARHU("Karhu", "2.x compatible branch", "karhu"),
        THEMIS("Themis", "0.x compatible branch", "themis"),
        NEGATIVITY("Negativity", "2.x compatible branch", "negativity"),
        LIGHT_ANTI_CHEAT("LightAntiCheat", "1.x compatible branch", "lightanticheat", "lightac", "lac"),
        HAWK("Hawk", "legacy/current fork", "hawk"),
        HORIZON("Horizon", "current compatible branch", "horizon"),
        MEDUSA("Medusa", "current compatible branch", "medusa"),
        ANTI_GAMING_CHAIR("AntiGamingChair", "legacy/fork", "antigamingchair", "agc"),
        WATCHDOG("Watchdog", "private server-side build", "watchdog"),
        GUARDIAN("Guardian", "private/current branch", "guardian"),
        SOAROMA_SAC("SoaromaSAC", "current compatible branch", "soaromasac"),
        WRAITH("Wraith", "current compatible branch", "wraith"),
        CUSTOM_ANTI_CHEAT("Custom/embedded AC", "server-specific build", "anticheat", "anti-cheat");

        private final String displayName;
        private final String versionHint;
        private final String[] tokens;

        Family(String displayName, String versionHint, String... tokens) {
            this.displayName = displayName;
            this.versionHint = versionHint;
            this.tokens = tokens;
        }

        private String findToken(String value) {
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                if (containsToken(value, token)) {
                    return token;
                }
            }
            return null;
        }

        private boolean containsToken(String value, String token) {
            int from = 0;
            while (from < value.length()) {
                int index = value.indexOf(token, from);
                if (index < 0) {
                    return false;
                }
                int end = index + token.length();
                boolean leftBoundary = index == 0 || !Character.isLetterOrDigit(value.charAt(index - 1));
                boolean rightBoundary = end == value.length() || !Character.isLetterOrDigit(value.charAt(end));
                if (leftBoundary && rightBoundary) {
                    return true;
                }
                from = index + 1;
            }
            return false;
        }
    }

    private static final class Candidate {

        private final Family family;
        private final List<String> sources = new ArrayList<>(4);
        private int score;
        private String version;

        private Candidate(Family family) {
            this.family = family;
        }

        private void addScore(int value, String source) {
            if (!sources.contains(source)) {
                sources.add(source);
                score = Math.min(100, score + value);
            }
        }
    }
}
