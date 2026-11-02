/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_11659
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  net.minecraft.class_3883
 *  net.minecraft.class_3887
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_591
 *  net.minecraft.class_742
 *  net.minecraft.class_9799
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.cosmetics;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_591;
import net.minecraft.class_742;
import net.minecraft.class_9799;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.model.CosmeticModel;
import ru.decide.cosmetics.render.CosmeticCapeData;
import ru.decide.cosmetics.render.CosmeticCapes;
import ru.decide.cosmetics.render.CosmeticCommandCapeRenderer;
import ru.decide.cosmetics.render.CosmeticRenderer;
import ru.decide.cosmetics.sync.PlayerCosmeticsSync;

@Environment(value=EnvType.CLIENT)
public class CosmeticFeatureRenderer
extends class_3887<class_10055, class_591> {
    private static final Logger DEBUG_LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:CosmeticOnPlayer");
    private static long lastDebugMs;

    public CosmeticFeatureRenderer(class_3883<class_10055, class_591> context) {
        super(context);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(class_4587 matrices, class_11659 queue, int light, class_10055 state, float limbAngle, float limbDistance) {
        CosmeticCapeData cape;
        boolean isLocal;
        if (state.field_53542 || state.field_53333) {
            CosmeticFeatureRenderer.debug("\u043f\u0440\u043e\u043f\u0443\u0441\u043a: spectator=" + state.field_53542 + " invisible=" + state.field_53333);
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return;
        }
        class_742 player = CosmeticFeatureRenderer.resolvePlayer(client, state);
        if (player == null || player.method_5767()) {
            CosmeticFeatureRenderer.debug("\u043f\u0440\u043e\u043f\u0443\u0441\u043a: player \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u0438\u043b\u0438 \u043d\u0435\u0432\u0438\u0434\u0438\u043c");
            return;
        }
        boolean bl = isLocal = player == client.field_1724;
        List<CosmeticModel> models = isLocal ? CosmeticManager.getInstance().getEquipped3DModels() : (player.method_5477() != null ? PlayerCosmeticsSync.getEquippedModels(player.method_5477().getString()) : null);
        class_591 playerModel = (class_591)this.method_17165();
        float tickDelta = client.method_61966().method_60637(true);
        if (models != null && !models.isEmpty()) {
            class_4597.class_4598 immediate = class_4597.method_22991((class_9799)new class_9799(1024));
            try {
                int drawn = 0;
                for (CosmeticModel model : models) {
                    if (model == null || model.getTextureId() == null) continue;
                    CosmeticRenderer.getInstance().renderCosmetic(model, player, matrices, (class_4597)immediate, light, playerModel, tickDelta);
                    ++drawn;
                }
                immediate.method_22993();
                CosmeticFeatureRenderer.debug("\u043e\u0442\u0440\u0438\u0441\u043e\u0432\u0430\u043d\u043e \u043c\u043e\u0434\u0435\u043b\u0435\u0439: " + drawn + "/" + models.size());
            }
            finally {
                immediate.method_22993();
            }
        } else {
            CosmeticFeatureRenderer.debug("\u0441\u043f\u0438\u0441\u043e\u043a \u043d\u0430\u0434\u0435\u0442\u043e\u0433\u043e \u043f\u0443\u0441\u0442");
        }
        if (isLocal && (cape = CosmeticCapes.getCapeData()) != null) {
            matrices.method_22903();
            playerModel.field_3391.method_22703(matrices);
            CosmeticCommandCapeRenderer.render(cape, matrices, queue, light, CosmeticFeatureRenderer.currentTimeSeconds(), state);
            matrices.method_22909();
        }
    }

    private static float currentTimeSeconds() {
        return (float)(System.nanoTime() % 1000000000L) / 1.0E9f;
    }

    private static void debug(String message) {
        long now = System.currentTimeMillis();
        if (now - lastDebugMs < 2000L) {
            return;
        }
        lastDebugMs = now;
        DEBUG_LOGGER.info("[cosmetic-on-player] {}", (Object)message);
    }

    private static class_742 resolvePlayer(class_310 client, class_10055 state) {
        class_742 player;
        if (client.field_1724 != null && state.field_53528 == client.field_1724.method_5628()) {
            return client.field_1724;
        }
        class_1297 entity = client.field_1687.method_8469(state.field_53528);
        return entity instanceof class_742 ? (player = (class_742)entity) : null;
    }
}

