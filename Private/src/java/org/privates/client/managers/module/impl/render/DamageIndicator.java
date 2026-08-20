package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.Project;
import org.privates.client.utils.render.font.Font;
import org.privates.client.utils.render.font.Fonts;
import org.privates.lib.util.time.StopWatch;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "DamageIndicator", category = Category.RENDER, description = "Маркер дамага по противнику")
public class DamageIndicator extends Module {
    public static DamageIndicator getInstance() {
        return Instance.get(DamageIndicator.class);
    }

    private final Font font = Fonts.SF_MEDIUM;
    private final Map<Entity, List<DamageInfo>> damageMap = new ConcurrentHashMap<>();
    private final Map<Entity, Float> lastHealthMap = new ConcurrentHashMap<>();
    private final Random random = new Random();

    @EventHandler
    public void onAttack(AttackEvent event) {
        Entity target = event.getTarget();
        if (!(target instanceof LivingEntity livingTarget)) return;

        float currentHealth = livingTarget.getHealth();
        float previousHealth = lastHealthMap.getOrDefault(target, currentHealth);
        float damage = Math.max(0, previousHealth - currentHealth);

        if (damage > 0) {
            float randomYOffset = -1.5f + random.nextFloat() * 1.2f;
            boolean moveRight = random.nextBoolean();

            boolean isCritical = mc.player.fallDistance > 0 && !mc.player.isOnGround() &&
                    !mc.player.isInWater() && !mc.player.isPotionActive(net.minecraft.potion.Effects.BLINDNESS);

            DamageInfo info = new DamageInfo(
                    damage,
                    target.getPositionVec().add(0, target.getHeight() + randomYOffset, 0),
                    moveRight,
                    isCritical
            );

            damageMap.computeIfAbsent(target, k -> new ArrayList<>()).add(info);
        }

        lastHealthMap.put(target, currentHealth);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (damageMap.isEmpty()) return;

        MatrixStack matrix = event.getMatrix();
        Iterator<Map.Entry<Entity, List<DamageInfo>>> entityIterator = damageMap.entrySet().iterator();

        while (entityIterator.hasNext()) {
            Map.Entry<Entity, List<DamageInfo>> entry = entityIterator.next();
            Entity entity = entry.getKey();
            List<DamageInfo> damages = entry.getValue();

            if (!entity.isAlive()) {
                entityIterator.remove();
                continue;
            }

            Iterator<DamageInfo> damageIterator = damages.iterator();
            while (damageIterator.hasNext()) {
                DamageInfo damage = damageIterator.next();

                if (damage.timer.finished(2000)) {
                    damageIterator.remove();
                    continue;
                }

                updateDamageInfo(damage);
                renderDamageInfo(matrix, damage);
            }

            if (damages.isEmpty()) {
                entityIterator.remove();
            }
        }
    }

    private void updateDamageInfo(DamageInfo damage) {
        damage.animation.update();

        long elapsed = damage.timer.elapsedTime();
        float progress = elapsed / 2000f;

        if (elapsed < 200) {
            damage.animation.run(1.0f, 0.2, Easings.BACK_OUT, true);
        } else if (progress > 0.7f) {
            damage.animation.run(0.0f, 0.3, Easings.CUBIC_IN, true);
        }

        float horizontalMove = (damage.moveRight ? 0.03f : -0.03f) * (elapsed / 100f);
        damage.position = damage.initialPosition.add(horizontalMove, progress * 0.5f, 0);
    }

    private void renderDamageInfo(MatrixStack matrix, DamageInfo damage) {
        Vector3d worldPos = damage.position;

        AxisAlignedBB aabb = new AxisAlignedBB(worldPos, worldPos).grow(0.1);
        Vector2f min = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
        Vector2f max = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);

        for (Vector3d corner : aabb.getCorners()) {
            org.joml.Vector2f projected = Project.project2D(corner);
            if (projected.x != Float.MAX_VALUE && projected.y != Float.MAX_VALUE) {
                min.x = Math.min(min.x, projected.x);
                min.y = Math.min(min.y, projected.y);
                max.x = Math.max(max.x, projected.x);
                max.y = Math.max(max.y, projected.y);
            }
        }

        if (min.x == Float.MAX_VALUE) return;

        float centerX = (min.x + max.x) / 2;
        float centerY = min.y;

        String damageText = formatDamage(damage.damage);
        float textWidth = font.getWidth(damageText, 14f);
        float textHeight = 14f;

        float scale = damage.animation.get();

        matrix.push();
        matrix.translate(centerX, centerY, 0);
        matrix.scale(scale, scale, 1);

        if (damage.isCritical) {
            float totalHeight = textHeight + 7f + 2f;
            matrix.translate(-textWidth / 2, -totalHeight / 2, 0);
        } else {
            matrix.translate(-textWidth / 2, -textHeight / 2, 0);
        }

        int color = getDamageColor(damage);
        color = ColorUtil.replAlpha(color, damage.animation.get());

        font.draw(matrix, damageText, 0, 0, color, 14f);

        if (damage.isCritical) {
            String critText = "Крит!";
            float critWidth = font.getWidth(critText, 7f);
            int critColor = ColorUtil.replAlpha(0xFFFF8800, damage.animation.get());
            font.draw(matrix, critText, (textWidth - critWidth) / 2, textHeight + 2f, critColor, 7f);
        }

        matrix.pop();
    }

    private String formatDamage(float damage) {
        if (damage < 1) {
            return String.format("-%.1f", damage);
        } else if (damage < 10) {
            return String.format("-%.1f", damage);
        } else {
            return String.format("-%.0f", damage);
        }
    }

    private int getDamageColor(DamageInfo damage) {
        if (damage.damage <= 2) {
            return 0xFF00FF00;
        } else if (damage.damage <= 5) {
            return 0xFFFFFF00;
        } else if (damage.damage <= 10) {
            return 0xFFFF8800;
        } else {
            return 0xFFFF0000;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        damageMap.clear();
        lastHealthMap.clear();
    }

    @Getter
    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DamageInfo {
        final float damage;
        @NonFinal
        Vector3d position;
        final Vector3d initialPosition;
        final boolean moveRight;
        final boolean isCritical;
        final StopWatch timer = new StopWatch();
        final Animation animation = new Animation();

        public DamageInfo(float damage, Vector3d position, boolean moveRight, boolean isCritical) {
            this.damage = damage;
            this.position = position;
            this.initialPosition = position;
            this.moveRight = moveRight;
            this.isCritical = isCritical;
            this.timer.reset();
            this.animation.set(0);
        }
    }
}