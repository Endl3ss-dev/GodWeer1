package org.privates.client.render.model;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class AngryDogBrain {
    private PlayerEntity entity;
    private Vector3d pos;
    private Vector3d prevPlayerPos;
    private Vector3d motion = Vector3d.ZERO;
    private float yaw, body;
    private float speed = 0.0f;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    private boolean lay;
    private boolean angry;
    private long lastPlayerMove = System.currentTimeMillis();
    private long lastLay = 0;
    private long lastAngry = 0;
    private long lastWag = 0;
    private float tailWag = 0f;

    public void setEntity(PlayerEntity entity) {
        this.entity = entity;
        if (entity != null) {
            this.pos = entity.getPositionVec();
            this.prevPlayerPos = entity.getPositionVec();
        }
    }

    public void onUpdate() {
        if (entity == null) return;
        Vector3d playerPos = entity.getPositionVec();
        if (pos == null) pos = playerPos;
        // Проверка движения игрока
        if (prevPlayerPos == null || playerPos.distanceTo(prevPlayerPos) > 0.01) {
            lastPlayerMove = System.currentTimeMillis();
            prevPlayerPos = playerPos;
        }
        // Если игрок не двигается 5 секунд — собака ложится
        if (System.currentTimeMillis() - lastPlayerMove > 5000) {
            lay = true;
            lastLay = System.currentTimeMillis();
        } else {
            lay = false;
        }
        // Если собака злая — через 3 секунды успокаивается
        if (angry && System.currentTimeMillis() - lastAngry > 3000) {
            angry = false;
        }
        // Дистанция до игрока
        double dist = pos.distanceTo(playerPos);
        // Если слишком далеко — телепорт к игроку
        if (dist > 15) {
            pos = playerPos.add(-1, 0, -1);
            motion = Vector3d.ZERO;
        }
        // Если дальше 1.5 блока — идём к игроку
        if (dist > 1.5) {
            Vector3d toPlayer = playerPos.subtract(pos).normalize();
            // Если злой — бежит быстрее
            double targetSpeed = angry ? 0.6 : Math.min(0.25 + (dist - 1.5) * 0.05, 0.45);
            speed += (targetSpeed - speed) * 0.1;
            motion = motion.add(toPlayer.scale(speed)).scale(0.7);
        } else {
            // Ласковое поведение: иногда подходит ближе, трётся
            if (!angry && Math.random() < 0.01) {
                Vector3d offset = new Vector3d((Math.random() - 0.5) * 0.3, 0, (Math.random() - 0.5) * 0.3);
                motion = motion.add(offset);
            }
            // Плавное торможение
            speed += (0.0 - speed) * 0.2;
            motion = motion.scale(0.5);
        }
        // Гравитация (если нужно)
        motion = new Vector3d(motion.x, 0, motion.z);
        // Поворот головы и тела
        if (motion.lengthSquared() > 0.0001) {
            double angle = Math.atan2(motion.z, motion.x);
            yaw = (float) Math.toDegrees(angle) - 90;
            yaw %= 360;
            if (yaw < 0) yaw += 360;
        }
        float shortestYawPath = (((((yaw - body) % 360) + 540) % 360) - 180);
        body += shortestYawPath * 0.15f;
        // Обновление позиции
        pos = pos.add(motion);
        // Анимация лап и хвоста
        limbTick();
        // Ласковое виляние хвостом
        if (!angry && !lay && System.currentTimeMillis() - lastWag > 200) {
            tailWag = (float) (Math.sin(System.currentTimeMillis() / 200.0) * 20f);
            lastWag = System.currentTimeMillis();
        } else if (angry) {
            tailWag = 0f;
        }
    }

    public void limbTick() {
        prevLimbSwingAmount = limbSwingAmount;
        double d0 = motion.x;
        double d2 = motion.z;
        float f = MathHelper.sqrt((float) (d0 * d0 + d2 * d2)) * (angry ? 8.0F : 6.0F);
        if (f > 1.5F) f = 1.5F;
        limbSwingAmount += (f - limbSwingAmount) * 0.4F;
        limbSwing += limbSwingAmount;
    }

    public void setAngry() {
        angry = true;
        lastAngry = System.currentTimeMillis();
    }

    public boolean isAngry() {
        return angry;
    }

    public boolean isLay() {
        return lay;
    }

    public float getYaw() {
        return yaw + (lay ? 10f * MathHelper.sin((System.currentTimeMillis() % 1000) / 1000f * 6.28f) : 0f);
    }

    public float getPitch() {
        return lay ? 10f * MathHelper.sin((System.currentTimeMillis() % 1200) / 1200f * 6.28f) : 0f;
    }

    public float getBody() {
        return body + (lay ? 5f * MathHelper.sin((System.currentTimeMillis() % 900) / 900f * 6.28f) : 0f);
    }

    public float getTailWag() {
        return tailWag;
    }

    public Vector3d getPos() {
        return pos != null ? pos : Vector3d.ZERO;
    }
} 