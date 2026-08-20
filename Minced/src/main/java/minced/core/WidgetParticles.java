/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package minced.core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import minced.core.RenderUtil;
import minced.gui.ParticleBurst;
import minced.gui.ParticleTrailPoint;
import net.minecraft.client.gui.DrawContext;

public class WidgetParticles {
    List<ParticleTrailPoint> trailPoints = new ArrayList<>();
    static float field9508;
    static float field9509;
    static float field9510;
    static float field9511;
    static float field9512;
    static float field9513;
    double lastX;
    double lastY;
    List<ParticleBurst> particles = new ArrayList<>();
    static double field9517;
    static double field9518;

    private void renderTrail(DrawContext param1) {
        if (this.trailPoints.size() < 2) {
            return;
        }
        long v2 = System.currentTimeMillis();
        for (int v4 = 0; v4 < this.trailPoints.size() - 1; ++v4) {
            ParticleTrailPoint v5 = (ParticleTrailPoint)this.trailPoints.get(v4);
            ParticleTrailPoint cfr_ignored_0 = (ParticleTrailPoint)this.trailPoints.get(v4 + 1);
            float v7 = (float)(v2 - v5.createdAt) / field9508;
            float v8 = Math.max(0.0f, field9509 * (1.0f - v7));
            if (!(v8 > field9510)) continue;
            Color v9 = new Color(255, 255, 255, (int)(v8 * field9511));
            float v10 = field9512 * (1.0f - v7 * field9513);
            RenderUtil.method397((float)v5.x - v10 / 2.0f, (float)v5.y - v10 / 2.0f, v10, v10, v10 / 2.0f, v9.getRGB());
        }
    }

    public void updatePosition(double param1, double param3) {
        if (this.lastX != param1 || this.lastY != param3) {
            this.trailPoints.add(new ParticleTrailPoint(param1, param3, System.currentTimeMillis()));
            for (int v5 = 0; v5 < 2; ++v5) {
                this.particles.add(new ParticleBurst(param1, param3));
            }
            this.lastX = param1;
            this.lastY = param3;
        }
        if (this.trailPoints.size() > 15) {
            this.trailPoints.removeFirst();
        }
        Iterator v5 = this.particles.iterator();
        while (v5.hasNext()) {
            ParticleBurst v6 = (ParticleBurst)v5.next();
            v6.tick();
            if (!v6.isExpired()) continue;
            v5.remove();
        }
    }

    public void render(DrawContext param1) {
        this.renderTrail(param1);
        this.renderParticles(param1);
    }

    public WidgetParticles() {
        this.lastX = field9517;
        this.lastY = field9518;
    }

    private void renderParticles(DrawContext param1) {
        for (ParticleBurst v3 : this.particles) {
            v3.render(param1);
        }
    }

    static {
        field9517 = -1.0;
        field9518 = -1.0;
        field9508 = 500.0f;
        field9509 = 0.15f;
        field9510 = 0.001f;
        field9511 = 255.0f;
        field9512 = 3.0f;
        field9513 = 0.5f;
    }
}



