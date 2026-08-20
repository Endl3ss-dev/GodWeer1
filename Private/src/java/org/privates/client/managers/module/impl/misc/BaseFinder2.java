package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.lib.util.time.StopWatch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Category1
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "BaseFinder", category = Category.MISC)
public class BaseFinder2 extends Module {

    // --- Настройки модуля ---
    private final BooleanSetting saveToFile = new BooleanSetting(this, "Сохранять в файл", true);
    private final BooleanSetting caseSensitive = new BooleanSetting(this, "Учитывать регистр", false);
    // private final BooleanSetting onlyVisible = new BooleanSetting(this, "Только видимые", false); // <<<< УДАЛЕНО
    private final BooleanSetting detectOres = new BooleanSetting(this, "Обнаруживать руды", true);
    private final SliderSetting oreSearchRadius = new SliderSetting(this, "Радиус поиска руд", 15F, 5F, 50F, 1F);
    private final SliderSetting batchSize = new SliderSetting(this, "Сущностей за тик", 30F, 10F, 100F, 5F);
    private final SliderSetting scanInterval = new SliderSetting(this, "Интервал сканирования (мс)", 500F, 100F, 2000F, 100F);

    // --- Списки и таймеры ---
    @Getter
    private final List<FoundBase> foundBases = new ArrayList<>();
    private final StopWatch scanTimer = new StopWatch();
    private final StopWatch saveTimer = new StopWatch();

    // --- Оптимизация и кэширование ---
    int currentEntityIndex = 0;
    final List<Entity> cachedEntities = new ArrayList<>();
    final Set<Vector3d> processedPositions = new HashSet<>();
    final Set<BlockPos> processedOrePositions = new HashSet<>();
    final Map<String, String> ownerCache = new HashMap<>();
    final Map<String, Boolean> privateKeywordCache = new HashMap<>();
    long lastEntityRefresh = 0;
    private static final long ENTITY_CACHE_DURATION = 5000; // 5 секунд
    private static final int MAX_PROCESSED_ORES = 10000; // Предотвращение утечки памяти

    // --- Файловая система ---
    File baseFile;
    boolean isScanning = false;

    // --- Константы для поиска ---
    private static final double NEARBY_OWNER_SEARCH_RADIUS = 20.0;
    private static final Set<Block> VALUABLE_BLOCKS = Set.of(
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.GOLD_ORE,
            Blocks.IRON_ORE,
            Blocks.NETHER_GOLD_ORE,
            Blocks.ANCIENT_DEBRIS
    );

    private static final Pattern[] OWNER_PATTERNS = {
            Pattern.compile("(?:Владелец|Owner|Хозяин|Создатель|Creator)\\s*:?\\s*([a-zA-Z0-9_]{3,16})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([a-zA-Z0-9_]{3,16})'s\\s+(?:приват|private|база|base|дом|house|схрон)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?:приват|private|база|base)\\s+([a-zA-Z0-9_]{3,16})", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\[([a-zA-Z0-9_]{3,16})\\]", Pattern.CASE_INSENSITIVE),
            Pattern.compile("([a-zA-Z0-9_]{3,16})\\s*[-|]\\s*(?:приват|private|база|base)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(?:приват|private|база|base)\\s*[-|]\\s*([a-zA-Z0-9_]{3,16})", Pattern.CASE_INSENSITIVE)
    };

    private static final Set<String> PRIVATE_KEYWORDS = Set.of("приват", "private");
    private static final Set<String> BASE_INDICATOR_KEYWORDS = Set.of("база", "base", "дом", "house", "home", "сх", "sh", "shulker", "схрон", "хранилище", "storage");

    @Getter
    public static class FoundBase {
        private final Vector3d position;
        private final String hologramText;
        private final String owner;
        private final long foundTime;
        private final int anarchyNumber;
        private final String serverInfo;
        private final String baseType;

        public FoundBase(Vector3d position, String hologramText, String owner, String baseType) {
            this.position = position;
            this.hologramText = hologramText;
            this.owner = owner;
            this.baseType = baseType;
            this.foundTime = System.currentTimeMillis();
            this.anarchyNumber = PlayerUtil.getAnarchy();
            this.serverInfo = PlayerUtil.getServerIp();
        }

        public String getCoordinates() {
            return String.format("X: %.0f, Y: %.0f, Z: %.0f", position.x, position.y, position.z);
        }

        public String getAnarchyInfo() {
            return anarchyNumber != -1 ? "Анархия-" + anarchyNumber : "Не анархия";
        }

        public String getOwnerInfo() {
            return owner != null && !owner.isEmpty() ? "Владелец: " + owner : "Владелец: неизвестен";
        }

        public String getBaseTypeInfo() {
            return baseType != null ? baseType : "Объект";
        }

        public String getFullInfo() {
            return String.format("%s - %s (%s) [%s]", getBaseTypeInfo(), getCoordinates(), getAnarchyInfo(), getOwnerInfo());
        }

        public String getDistance() {
            if (mc.player == null) return "0м";
            double distance = mc.player.getPositionVec().distanceTo(position);
            return String.format("%.0fм", distance);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null) return;
        initializeBaseFile();
        resetState();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isScanning = false;
        if (saveToFile.getValue()) {
            saveBasesToFile(false);
        }
    }

    private void resetState() {
        scanTimer.reset();
        saveTimer.reset();
        currentEntityIndex = 0;
        foundBases.clear();
        cachedEntities.clear();
        processedPositions.clear();
        processedOrePositions.clear();
        ownerCache.clear();
        privateKeywordCache.clear();
        lastEntityRefresh = 0;
    }

    @EventHandler
    public void onUpdate(GameUpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (scanTimer.finished(scanInterval.getValue().longValue())) {
            scanForBases();
            if (detectOres.getValue()) {
                scanForValuableBlocks();
            }
            scanTimer.reset();
        }

        if (saveToFile.getValue() && saveTimer.finished(60000)) {
            saveBasesToFile(true);
            saveTimer.reset();
        }
    }

    private void scanForBases() {
        if (mc.world == null) return;
        isScanning = true;

        if (System.currentTimeMillis() - lastEntityRefresh > ENTITY_CACHE_DURATION) {
            refreshEntityCache();
        }

        processBatchOfEntities();
        isScanning = false;
    }

    private void refreshEntityCache() {
        cachedEntities.clear();
        if (mc.world != null) {
            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof ArmorStandEntity) {
                    cachedEntities.add(entity);
                }
            }
        }
        lastEntityRefresh = System.currentTimeMillis();
        currentEntityIndex = 0;
    }

    // =========================================================================================
    // ИСПРАВЛЕННЫЙ МЕТОД
    // =========================================================================================
    private void processBatchOfEntities() {
        int batchSizeValue = batchSize.getValue().intValue();
        int processed = 0;
        int totalEntities = cachedEntities.size();

        while (processed < batchSizeValue && currentEntityIndex < totalEntities) {
            Entity entity = cachedEntities.get(currentEntityIndex++);
            if (entity == null || !entity.isAlive() || !(entity instanceof ArmorStandEntity)) continue;

            // Проверка на видимость ПОЛНОСТЬЮ УДАЛЕНА
            // if (onlyVisible.getValue() && !mc.player.canEntityBeSeen(entity)) continue;

            Vector3d roundedPos = roundVector(entity.getPositionVec());
            if (processedPositions.contains(roundedPos)) continue;

            List<String> hologramTexts = collectHologramTexts((ArmorStandEntity) entity);

            if (!hologramTexts.isEmpty()) {
                analyzeHologramTexts(entity.getPositionVec(), hologramTexts);
                processedPositions.add(roundedPos);
            }
            processed++;
        }
    }

    private List<String> collectHologramTexts(ArmorStandEntity initialArmorStand) {
        List<String> texts = new ArrayList<>();
        Set<Entity> visited = new HashSet<>();
        collectHologramTextsRecursively(initialArmorStand, texts, visited);
        return texts;
    }

    private void collectHologramTextsRecursively(Entity entity, List<String> texts, Set<Entity> visited) {
        if (entity == null || !visited.add(entity)) {
            return;
        }

        if (entity instanceof ArmorStandEntity armorStand) {
            if (armorStand.hasCustomName()) {
                ITextComponent customName = armorStand.getCustomName();
                if (customName != null) {
                    String text = customName.getString().trim();
                    if (!text.isEmpty()) {
                        texts.add(text);
                    }
                }
            }
        }

        for (Entity passenger : entity.getPassengers()) {
            collectHologramTextsRecursively(passenger, texts, visited);
        }
    }

    private void analyzeHologramTexts(Vector3d position, List<String> hologramTexts) {
        if (hologramTexts.isEmpty()) return;

        String fullHologramText = String.join(" | ", hologramTexts);
        String lowerCaseFullText = fullHologramText.toLowerCase();
        String owner = null;
        String baseType = null;

        for (String text : hologramTexts) {
            owner = extractOwnerAdvanced(text);
            if (owner != null) break;
        }

        boolean isPrivate = PRIVATE_KEYWORDS.stream().anyMatch(lowerCaseFullText::contains);
        if (isPrivate) {
            baseType = "Приват";
        } else {
            boolean isBaseIndicator = BASE_INDICATOR_KEYWORDS.stream().anyMatch(lowerCaseFullText::contains);
            if (isBaseIndicator) {
                baseType = "База/Схрон";
            }
        }

        if (baseType != null) {
            checkAndAddBase(position, fullHologramText, owner, baseType);
        }
    }

    private String extractOwnerAdvanced(String text) {
        return ownerCache.computeIfAbsent(text, t -> {
            for (Pattern pattern : OWNER_PATTERNS) {
                Matcher matcher = pattern.matcher(t);
                if (matcher.find()) {
                    String potentialOwner = matcher.group(1);
                    if (potentialOwner != null && !potentialOwner.isEmpty()) {
                        return potentialOwner;
                    }
                }
            }
            return "NONE";
        }).equals("NONE") ? null : ownerCache.get(text);
    }

    private void scanForValuableBlocks() {
        if (mc.world == null || mc.player == null) return;

        if (processedOrePositions.size() > MAX_PROCESSED_ORES) {
            processedOrePositions.clear();
        }

        int radius = oreSearchRadius.getValue().intValue();
        BlockPos playerBlockPos = mc.player.getPosition();
        World world = mc.world;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = playerBlockPos.add(x, y, z);
                    if (processedOrePositions.contains(currentPos) || !world.isAreaLoaded(currentPos, BlockPos.fromLong(1))) {
                        continue;
                    }

                    Block block = world.getBlockState(currentPos).getBlock();
                    if (VALUABLE_BLOCKS.contains(block)) {
                        processedOrePositions.add(currentPos);

                        String blockName = block.getTranslationKey().replaceAll("block\\.minecraft\\.", "");
                        String owner = findNearbyOwner(currentPos);
                        Vector3d blockVec = new Vector3d(currentPos.getX() + 0.5, currentPos.getY(), currentPos.getZ() + 0.5);

                        checkAndAddBase(blockVec, "Найдена руда: " + blockName, owner, "Ценные ресурсы");
                    }
                }
            }
        }
    }

    private String findNearbyOwner(BlockPos blockPos) {
        if (mc.world == null) return null;

        AxisAlignedBB searchBox = new AxisAlignedBB(blockPos).grow(NEARBY_OWNER_SEARCH_RADIUS);
        List<ArmorStandEntity> nearbyHolograms = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, searchBox);

        for (ArmorStandEntity armorStand : nearbyHolograms) {
            List<String> hologramTexts = collectHologramTexts(armorStand);
            for (String text : hologramTexts) {
                String owner = extractOwnerAdvanced(text);
                if (owner != null) {
                    return owner;
                }
            }
        }
        return null;
    }

    private void checkAndAddBase(Vector3d position, String hologramText, String owner, String baseType) {
        boolean alreadyFound = foundBases.stream()
                .anyMatch(base -> base.getPosition().distanceTo(position) < 10.0 && base.getBaseTypeInfo().equals(baseType));

        if (!alreadyFound) {
            FoundBase foundBase = new FoundBase(position, hologramText, owner, baseType);
            foundBases.add(foundBase);

            if (saveToFile.getValue()) {
                appendBaseToFile(foundBase);
            }
        }
    }

    private void initializeBaseFile() {
        try {
            String serverIp = PlayerUtil.getServerIp().replaceAll("[:.]", "_");
            String fileName = String.format("BaseFinder_%s.txt", serverIp);
            File dataDir = new File(mc.gameDir, "PrivateDlc" + File.separator + "BaseFinder");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            baseFile = new File(dataDir, fileName);

            if (!baseFile.exists()) {
                baseFile.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(baseFile, true))) {
                    writer.write("=== BaseFinder Log (v1.16.5) ===\n");
                    writer.write("Сервер: " + PlayerUtil.getServerIp() + "\n");
                    writer.write("Время создания: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                    writer.write("Формат: [Время] [Тип] [Координаты] [Анархия] [Владелец] [Описание]\n");
                    writer.write("================================\n\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendBaseToFile(FoundBase base) {
        if (baseFile == null || !baseFile.canWrite()) return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(baseFile, true))) {
            writeBaseEntry(writer, base);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBasesToFile(boolean isPeriodicUpdate) {
        if (baseFile == null || !baseFile.canWrite() || foundBases.isEmpty()) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(baseFile, true))) {
            writer.write(String.format("\n=== %s сессии @ %s ===\n",
                    isPeriodicUpdate ? "Промежуточное сохранение" : "Завершение",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            writer.write("Всего найдено в этой сессии: " + foundBases.size() + "\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBaseEntry(BufferedWriter writer, FoundBase base) throws IOException {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date(base.getFoundTime()));
        writer.write(String.format("[%s] [%s] [%.0f %.0f %.0f] [%s] [%s] \"%s\"\n",
                timestamp,
                base.getBaseTypeInfo(),
                base.getPosition().x, base.getPosition().y, base.getPosition().z,
                base.getAnarchyInfo(),
                base.getOwnerInfo().replace("Владелец: ", "").trim(),
                base.getHologramText().replace("\n", " ")));
    }

    private Vector3d roundVector(Vector3d vec) {
        return new Vector3d(Math.floor(vec.x), Math.floor(vec.y), Math.floor(vec.z));
    }
}