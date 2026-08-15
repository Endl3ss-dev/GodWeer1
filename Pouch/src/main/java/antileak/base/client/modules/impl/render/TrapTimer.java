package antileak.base.client.modules.impl.render;

import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.Event3DRender;
import antileak.base.api.events.implement.EventPacket;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.ModeSetting;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TrapTimer - displays timers for трапки, пласты, драконьи ловушки и драконьи пласты
 */
public class TrapTimer extends Module {

    public static TrapTimer INSTANCE = new TrapTimer();

    private static final String TRAPKA_SOUND = "block.piston.extend";
    private static final float TRAPKA_PITCH = 0.500f;
    private static final float TRAPKA_VOLUME = 0.700f;
    private static final long TRAPKA_DURATION = 15000;

    private static final String PLAST_SOUND = "block.anvil.place";
    private static final float PLAST_PITCH = 1.100f;
    private static final float PLAST_VOLUME = 0.700f;
    private static final long PLAST_DURATION = 15000;

    private static final String DRAGON_TRAP_SOUND = "entity.ender_dragon.growl";
    private static final float DRAGON_TRAP_PITCH = 1.000f;
    private static final float DRAGON_TRAP_VOLUME = 0.200f;
    private static final long DRAGON_TRAP_DURATION = 30000;
    private static final long DRAGON_PLAST_DURATION = 20000;

    private final ModeSetting mode = new ModeSetting(
            "Режим",
            "SpookyTime",
            "SpookyTime"
    );

    private final List<TrapPosition> trapPositions = new ArrayList<>();
    private int previousNetheriteHoeCount = -1;
    private Font font;

    // For 3D to 2D projection
    private final Matrix4f lastProjectionMatrix = new Matrix4f();
    private final Quaternionf lastCameraRotation = new Quaternionf();
    private Vec3d lastCameraPos = Vec3d.ZERO;
    private float lastTickDelta;

    public TrapTimer() {
        super("TrapTimer", "Отображает таймеры для трапок, пластов, драконьих ловушек и драконьих пластов", ModuleCategory.RENDER);
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        trapPositions.clear();
        previousNetheriteHoeCount = -1;
    }

    @Override
    public void onDisable() {
        trapPositions.clear();
        super.onDisable();
    }

    @EventLink
    public void onRender3D(Event3DRender event) {
        lastProjectionMatrix.set(event.getProjectionMatrix());
        lastCameraPos = event.getCamera().getPos();
        lastCameraRotation.set(event.getCamera().getRotation());
        lastTickDelta = event.getTickDelta();
    }

    @EventLink
    public void onUpdate(EventUpdate e) {
        if (mc.player == null || mc.world == null) return;

        // Count netherite hoes in inventory
        int currentCount = countNetheriteHoeInInventory();

        if (previousNetheriteHoeCount == -1) {
            previousNetheriteHoeCount = currentCount;
            return;
        }

        // If netherite hoe count decreased, a trap was placed
        if (previousNetheriteHoeCount > 0 && currentCount < previousNetheriteHoeCount) {
            Vec3d playerPosition = mc.player.getPos();
            TrapPosition trap = new TrapPosition(playerPosition, System.currentTimeMillis(), TrapType.TRAPKA, TRAPKA_DURATION);
            trap.typeDetermined = true;
            trapPositions.add(trap);
        }

        previousNetheriteHoeCount = currentCount;
    }

    @EventLink
    public void onPacket(EventPacket event) {
        if (event.getType() != EventPacket.Type.RECEIVE) return;
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof PlaySoundS2CPacket packet) {
            try {
                // Проверяем что звук существует
                var soundEntry = packet.getSound().value();
                if (soundEntry == null) return;
                
                var soundId = Registries.SOUND_EVENT.getId(soundEntry);
                if (soundId == null) return;
                
                String soundName = soundId.getPath();
                float pitch = packet.getPitch();
                float volume = packet.getVolume();
                Vec3d position = new Vec3d(packet.getX(), packet.getY(), packet.getZ());

                // Debug: выводим все звуки в консоль
                System.out.println(String.format("[TrapTimer DEBUG] Sound: %s | Pitch: %.3f | Volume: %.3f | Mode: %s", 
                    soundName, pitch, volume, mode.getCurrent()));

                TrapType detectedType = null;
                long duration = 0;

                // SpookyTime - Трапка
                if (mode.is("SpookyTime") &&
                        soundName.equals(TRAPKA_SOUND) &&
                        Math.abs(pitch - 0.500f) < 0.01f &&
                        Math.abs(volume - 0.500f) < 0.01f) {
                    detectedType = TrapType.TRAPKA;
                    duration = 15000;
                    System.out.println("[TrapTimer] Обнаружена ТРАПКА (SpookyTime)");
                }
                // SpookyTime - Пласт (needs netherite check)
                else if (mode.is("SpookyTime") &&
                        soundName.equals(PLAST_SOUND) &&
                        Math.abs(pitch - 0.500f) < 0.01f &&
                        Math.abs(volume - 0.500f) < 0.01f) {
                    TrapPosition tempPosition = new TrapPosition(position, System.currentTimeMillis(), TrapType.PLAST, 20000);
                tempPosition.typeDetermined = false;
                tempPosition.needsNetheriteCheck = true;
                trapPositions.add(tempPosition);
                System.out.println("[TrapTimer] Обнаружен ПЛАСТ (SpookyTime, нужна проверка незерита)");
                return;
            }
            // SpookyTime - Dragon (0.700 pitch, 0.500 volume)
            else if (mode.is("SpookyTime") &&
                    soundName.equals(DRAGON_TRAP_SOUND) &&
                    Math.abs(pitch - 0.700f) < 0.01f &&
                    Math.abs(volume - 0.500f) < 0.01f) {
                TrapPosition tempPosition = new TrapPosition(position, System.currentTimeMillis(), TrapType.DRAGON_PLAST, DRAGON_PLAST_DURATION);
                tempPosition.typeDetermined = false;
                trapPositions.add(tempPosition);
                determineDragonType(tempPosition);
                System.out.println("[TrapTimer] Обнаружена ДРАКОНКА (SpookyTime)");
                return;
            }

            if (detectedType != null) {
                trapPositions.add(new TrapPosition(position, System.currentTimeMillis(), detectedType, duration));
                System.out.println(String.format("[TrapTimer] Добавлен таймер на позиции X=%.1f Y=%.1f Z=%.1f", 
                    position.x, position.y, position.z));
            }
            } catch (Exception e) {
                // Игнорируем ошибки при обработке звуков
                System.out.println("[TrapTimer] Error processing sound packet: " + e.getMessage());
            }
        }
    }

    @EventLink
    public void onRender2D(EventRender.Default event) {
        if (mc.player == null || mc.world == null) return;

        MatrixStack matrices = event.getContext().getMatrices();
        long currentTime = System.currentTimeMillis();

        // Determine types for pending traps - create a copy to avoid ConcurrentModificationException
        List<TrapPosition> trapsToCheck = new ArrayList<>(trapPositions);
        for (TrapPosition trap : trapsToCheck) {
            if (!trap.typeDetermined) {
                if (currentTime - trap.creationTime > 50) {
                    if (trap.type == TrapType.DRAGON_PLAST) {
                        determineDragonType(trap);
                    }
                    if (currentTime - trap.creationTime > 300) {
                        trap.typeDetermined = true;
                    }
                }
            }

            // Check netherite blocks for спуки тайм plast detection
            if (trap.needsNetheriteCheck && currentTime - trap.creationTime >= 500) {
                if (hasNetheriteBlockInRadius(trap.position, 5, 9, 5)) {
                    trap.type = TrapType.DRAGON_PLAST;
                    trap.duration = 30000;
                    System.out.println("[TrapTimer] Определён как ДРАКОНИЙ ПЛАСТ (найден незерит)");
                } else {
                    trap.type = TrapType.PLAST;
                    trap.duration = 20000;
                    System.out.println("[TrapTimer] Определён как обычный ПЛАСТ (незерит не найден)");
                }
                trap.needsNetheriteCheck = false;
                trap.typeDetermined = true;
            }
        }

        // Remove expired traps
        Iterator<TrapPosition> iterator = trapPositions.iterator();
        while (iterator.hasNext()) {
            TrapPosition trap = iterator.next();
            if ((trap.typeDetermined && currentTime - trap.creationTime > trap.duration) ||
                    (!trap.typeDetermined && currentTime - trap.creationTime > 1000)) {
                iterator.remove();
            }
        }

        if (trapPositions.isEmpty()) return;

        // Initialize font
        if (font == null) {
            font = Fonts.getFont("sf_regular", 14);
            if (font == null) return;
        }

        // Debug: показываем сколько трапок в списке
        if (trapPositions.size() > 0) {
            System.out.println("[TrapTimer] Рендерим " + trapPositions.size() + " трапок");
        }

        // Render each trap - create a copy to avoid ConcurrentModificationException
        List<TrapPosition> trapsCopy = new ArrayList<>(trapPositions);
        for (TrapPosition trap : trapsCopy) {
            if (!trap.typeDetermined && (currentTime - trap.creationTime < 50)) continue;

            long elapsed = currentTime - trap.creationTime;
            long remaining = trap.duration - elapsed;
            float remainingSeconds = remaining / 1000.0f;

            if (remainingSeconds <= 0) continue;

            // Project 3D position to 2D screen
            Vec3d screenPos = worldToScreen(new Vec3d(trap.position.x, trap.position.y + 2.0, trap.position.z));
            if (screenPos == null) {
                System.out.println("[TrapTimer] Не удалось спроецировать на экран");
                continue;
            }

            String timeLabel = String.format("%.1fs", remainingSeconds);
            float textWidth = font.getStringWidth(timeLabel);
            
            // Размеры и отступы
            float iconSize = 10.0f;
            float iconScale = 0.625f;
            float gap = 4.0f;
            float padding = 2.5f;
            float totalHeight = 12.0f;
            
            // Центрируем весь блок
            float totalWidth = iconSize + gap + textWidth + padding * 2;
            float x = (float) screenPos.x - totalWidth / 2;
            float y = (float) screenPos.y;

            matrices.push();
            matrices.translate(x, y, 0);

            // Draw background for icon
            int bgColor = ColorUtils.rgba(0, 0, 0, 100);
            RenderUtils.drawRoundedRect(matrices, 0, 0, iconSize + padding * 2, totalHeight, 2, bgColor);

            // Draw item icon based on type
            ItemStack icon;
            if (trap.type == TrapType.TRAPKA || trap.type == TrapType.DRAGON_TRAP) {
                icon = Items.NETHERITE_SCRAP.getDefaultStack();
            } else {
                icon = Items.DRIED_KELP.getDefaultStack();
            }

            matrices.push();
            matrices.translate(padding - 0.5f, 0.5f, 0); // иконку чуть левее и выше
            matrices.scale(iconScale, iconScale, 1.0f);
            event.getContext().drawItem(icon, 0, 0);
            matrices.pop();

            // Draw background for time text
            float textX = iconSize + padding * 2 + gap;
            RenderUtils.drawRoundedRect(matrices, textX, 0, textWidth + padding * 2, totalHeight, 2, bgColor);

            // Draw time text (чуть ниже)
            int textColor = ColorUtils.rgba(255, 255, 255, 255);
            float textY = (totalHeight - 7.0f) / 2 + 1.0f; // текст чуть ниже
            font.drawString(matrices, timeLabel, textX + padding, textY, textColor);

            matrices.pop();
        }
    }

    private Vec3d worldToScreen(Vec3d worldPos) {
        Vector3f relative = new Vector3f(
                (float) (worldPos.x - lastCameraPos.x),
                (float) (worldPos.y - lastCameraPos.y),
                (float) (worldPos.z - lastCameraPos.z)
        );

        relative.rotate(new Quaternionf(lastCameraRotation).conjugate());

        Vector4f clip = new Vector4f(relative.x, relative.y, relative.z, 1.0f);
        lastProjectionMatrix.transform(clip);

        float w = clip.w;
        if (w <= 0.00001f) return null;

        float ndcX = clip.x / w;
        float ndcY = clip.y / w;
        float ndcZ = clip.z / w;

        float screenX = (ndcX * 0.5f + 0.5f) * mc.getWindow().getScaledWidth();
        float screenY = (1.0f - (ndcY * 0.5f + 0.5f)) * mc.getWindow().getScaledHeight();

        if (Float.isNaN(screenX) || Float.isNaN(screenY) || Float.isInfinite(screenX) || Float.isInfinite(screenY)) {
            return null;
        }

        return new Vec3d(screenX, screenY, ndcZ);
    }

    private void determineDragonType(TrapPosition trap) {
        if (trap.typeDetermined || mc.world == null) return;

        if (hasRespawnAnchorInRadius(trap.position, 6)) {
            trap.type = TrapType.DRAGON_TRAP;
            trap.duration = DRAGON_TRAP_DURATION;
            trap.typeDetermined = true;
        }
    }

    private boolean hasRespawnAnchorInRadius(Vec3d position, int radius) {
        if (mc.world == null) return false;

        BlockPos centerPos = BlockPos.ofFloored(position);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -3; y <= 3; y++) {
                    BlockPos checkPos = centerPos.add(x, y, z);
                    try {
                        if (mc.world.getBlockState(checkPos).getBlock() == Blocks.RESPAWN_ANCHOR) {
                            return true;
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }
        }

        return false;
    }

    private boolean hasNetheriteBlockInRadius(Vec3d position, int radiusX, int radiusY, int radiusZ) {
        if (mc.world == null) return false;

        BlockPos centerPos = BlockPos.ofFloored(position);

        for (int x = -radiusX; x <= radiusX; x++) {
            for (int z = -radiusZ; z <= radiusZ; z++) {
                for (int y = -radiusY; y <= radiusY; y++) {
                    BlockPos checkPos = centerPos.add(x, y, z);
                    try {
                        if (mc.world.getBlockState(checkPos).getBlock() == Blocks.NETHERITE_BLOCK) {
                            return true;
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }
        }

        return false;
    }

    private int countNetheriteHoeInInventory() {
        if (mc.player == null || mc.player.getInventory() == null) return 0;

        int count = 0;
        for (int i = 0; i < 45; i++) {
            net.minecraft.item.ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.NETHERITE_HOE) {
                count += stack.getCount();
            }
        }
        return count;
    }

    /**
     * Manually add a trap timer at position (called from FunTimeHelper)
     */
    public void addTrapTimer(Vec3d position) {
        if (mode.is("SpookyTime")) return;

        TrapPosition trap = new TrapPosition(position, System.currentTimeMillis(), TrapType.TRAPKA, TRAPKA_DURATION);
        trap.typeDetermined = true;
        trapPositions.add(trap);
    }

    /**
     * Manually add a plast timer at position (called from FunTimeHelper)
     */
    public void addPlastTimer(Vec3d position) {
        if (mode.is("SpookyTime")) return;

        TrapPosition trap = new TrapPosition(position, System.currentTimeMillis(), TrapType.PLAST, PLAST_DURATION);
        trap.typeDetermined = true;
        trapPositions.add(trap);
    }

    enum TrapType {
        TRAPKA,
        PLAST,
        DRAGON_TRAP,
        DRAGON_PLAST
    }

    private static class TrapPosition {
        final Vec3d position;
        final long creationTime;
        TrapType type;
        long duration;
        boolean typeDetermined;
        boolean needsNetheriteCheck;

        TrapPosition(Vec3d position, long creationTime, TrapType type, long duration) {
            this.position = position;
            this.creationTime = creationTime;
            this.type = type;
            this.duration = duration;
            this.typeDetermined = (type != null);
            this.needsNetheriteCheck = false;
        }
    }
}
