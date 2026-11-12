/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1657
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 */
package ru.decide.module.impl.render.richdog;

import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import ru.decide.module.impl.render.richdog.util.PetAnimation;

@Environment(value=EnvType.CLIENT)
public final class PetBrain {
    private static final class_310 mc = class_310.method_1551();
    private class_243 pos;
    private class_243 motion = class_243.field_1353;
    private float direction = PetBrain.randomAngle();
    private float yaw;
    private float body;
    private final PetAnimation x = new PetAnimation();
    private final PetAnimation y = new PetAnimation();
    private final PetAnimation z = new PetAnimation();
    private final PetAnimation bodyAnim = new PetAnimation();
    private final PetAnimation yawAnim = new PetAnimation();
    private final PetAnimation pitchAnim = new PetAnimation();
    private boolean lay;
    private long stayingStart = 0L;
    public float prevLimbSwingAmount;
    public float limbSwingAmount;
    public float limbSwing;
    private class_1657 entity;

    public void setEntity(class_1657 entity) {
        this.entity = entity;
    }

    public void onUpdate() {
        if (this.entity == null || PetBrain.mc.field_1687 == null) {
            return;
        }
        class_243 playerPos = this.entity.method_73189();
        if (this.pos == null || this.pos.method_1022(playerPos) > 10.0) {
            this.pos = playerPos;
            this.x.animate((float)this.pos.field_1352, 1);
            this.y.animate((float)this.pos.field_1351, 1);
            this.z.animate((float)this.pos.field_1350, 1);
        }
        this.motion = this.motion.method_1031(0.0, -0.2, 0.0);
        class_243 newPos = this.pos.method_1019(this.motion);
        if (this.isBlockSolid(newPos.field_1352, newPos.field_1351, newPos.field_1350)) {
            int blockY = (int)newPos.field_1351;
            double correctedY = (double)(blockY + 1) + 0.1;
            newPos = new class_243(newPos.field_1352, correctedY, newPos.field_1350);
            this.motion = new class_243(this.motion.field_1352, 0.0, this.motion.field_1350);
        }
        if (this.collidesHorizontally(newPos)) {
            newPos = new class_243(this.pos.field_1352, newPos.field_1351, this.pos.field_1350);
            this.motion = new class_243(-this.motion.field_1352 * 0.35, this.motion.field_1351, -this.motion.field_1350 * 0.35);
            this.direction = PetBrain.randomAngle();
        }
        this.motion = new class_243(this.motion.field_1352, 0.0, this.motion.field_1350);
        if (newPos.method_1022(playerPos) > 2.0) {
            this.motion = this.motion.method_1019(playerPos.method_1020(newPos).method_1029());
        }
        this.handleRotation();
        this.pos = newPos;
        if (this.pos.method_1022(playerPos) < 0.1) {
            this.direction = PetBrain.randomAngle();
            double xMot = -Math.sin(Math.toRadians(this.direction)) * 0.1;
            double zMot = Math.cos(Math.toRadians(this.direction)) * 0.1;
            this.motion = this.motion.method_1031(xMot, 0.0, zMot);
        }
        this.motion = this.motion.method_18805(0.5, 0.0, 0.5);
        int speed = 150;
        this.x.animate((float)this.pos.field_1352, speed);
        this.y.animate((float)this.pos.field_1351, speed);
        this.z.animate((float)this.pos.field_1350, speed);
        this.limbTick();
        if (Math.abs(this.pos.field_1352 - (double)this.x.get()) > (double)0.1f || Math.abs(this.pos.field_1350 - (double)this.z.get()) > (double)0.1f) {
            this.stayingStart = System.currentTimeMillis();
        }
        this.lay = System.currentTimeMillis() - this.stayingStart >= 1000L;
    }

    private void handleRotation() {
        float gradus1;
        if (this.motion.field_1352 != 0.0 || this.motion.field_1350 != 0.0) {
            double angle = Math.atan2(this.motion.field_1350, this.motion.field_1352);
            this.yaw = (float)Math.toDegrees(angle) - 90.0f;
            this.yaw %= 360.0f;
            if (this.yaw < 0.0f) {
                this.yaw += 360.0f;
            }
        }
        float[] rotation = PetBrain.lookAt(this.pos, this.entity.method_33571());
        float gradus = this.lay ? 200.0f : 150.0f;
        float f = gradus1 = this.lay ? 100.0f : 50.0f;
        if (rotation[0] - this.yaw < -gradus || rotation[0] - this.yaw > gradus) {
            this.yaw = rotation[0];
        }
        float shortestYawPath = ((this.yaw - this.body) % 360.0f + 540.0f) % 360.0f - 180.0f;
        if (!this.lay) {
            this.bodyAnim.animate(this.body + shortestYawPath, 150);
        }
        this.yawAnim.animate(class_3532.method_15363((float)(rotation[0] - this.yaw), (float)(-gradus1), (float)gradus1), 150);
        this.pitchAnim.animate(rotation[1], 150);
        this.body += shortestYawPath;
    }

    private void limbTick() {
        double d2;
        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d0 = (double)this.x.get() - this.pos.field_1352;
        float f = class_3532.method_15355((float)((float)(d0 * d0 + (d2 = (double)this.z.get() - this.pos.field_1350) * d2))) * 4.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        this.limbSwingAmount += (f - this.limbSwingAmount) * 0.4f;
        this.limbSwing += this.limbSwingAmount;
    }

    public float getBody() {
        return this.bodyAnim.get();
    }

    public float getYaw() {
        return this.yawAnim.get();
    }

    public float getPitch() {
        return this.pitchAnim.get();
    }

    public boolean isLay() {
        return this.lay;
    }

    public class_243 getPos() {
        return new class_243((double)this.x.get(), (double)this.y.get(), (double)this.z.get());
    }

    private static float[] lookAt(class_243 from, class_243 to) {
        double dx = to.field_1352 - from.field_1352;
        double dy = to.field_1351 - from.field_1351;
        double dz = to.field_1350 - from.field_1350;
        double dxz = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float)Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        float pitch = (float)Math.toDegrees(-Math.atan2(dy, dxz));
        return new float[]{yaw, pitch};
    }

    private static float randomAngle() {
        return ThreadLocalRandom.current().nextFloat() * 360.0f;
    }

    private boolean isBlockSolid(double bx, double by, double bz) {
        if (PetBrain.mc.field_1687 == null) {
            return false;
        }
        class_2338 bp = class_2338.method_49637((double)bx, (double)by, (double)bz);
        return !PetBrain.mc.field_1687.method_8320(bp).method_26220((class_1922)PetBrain.mc.field_1687, bp).method_1110();
    }

    private boolean collidesHorizontally(class_243 position) {
        double[] heights;
        double radius = 0.35;
        double baseY = this.horizontalBaseY(position);
        for (double height : heights = new double[]{0.0, 0.45}) {
            double sy = baseY + height;
            if (!this.isBlockSolid(position.field_1352 + radius, sy, position.field_1350 + radius) && !this.isBlockSolid(position.field_1352 + radius, sy, position.field_1350 - radius) && !this.isBlockSolid(position.field_1352 - radius, sy, position.field_1350 + radius) && !this.isBlockSolid(position.field_1352 - radius, sy, position.field_1350 - radius)) continue;
            return true;
        }
        return false;
    }

    private double horizontalBaseY(class_243 position) {
        double[][] offsets;
        double supportRadius = 0.32;
        double probeY = position.field_1351 - 0.2;
        double highest = Double.NEGATIVE_INFINITY;
        for (double[] off : offsets = new double[][]{{0.0, 0.0}, {supportRadius, supportRadius}, {supportRadius, -supportRadius}, {-supportRadius, supportRadius}, {-supportRadius, -supportRadius}}) {
            if (!this.isBlockSolid(position.field_1352 + off[0], probeY, position.field_1350 + off[1])) continue;
            highest = Math.max(highest, Math.floor(probeY) + 1.0);
        }
        if (highest == Double.NEGATIVE_INFINITY) {
            return position.field_1351 + 0.05;
        }
        return Math.max(position.field_1351 + 0.05, highest + 0.05);
    }
}

