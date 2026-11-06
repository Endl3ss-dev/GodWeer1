/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10042
 *  software.bernie.geckolib.constant.DataTickets
 *  software.bernie.geckolib.constant.dataticket.DataTicket
 *  software.bernie.geckolib.renderer.base.GeoRenderState
 */
package ru.decide.module.impl.render.custompet.render;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10042;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

@Environment(value=EnvType.CLIENT)
public final class CustomPetRenderState
extends class_10042
implements GeoRenderState {
    private final Map<DataTicket<?>, Object> geckolibData = new Reference2ObjectOpenHashMap();

    public <D> void addGeckolibData(DataTicket<D> dataTicket, D data) {
        this.geckolibData.put(dataTicket, data);
    }

    public boolean hasGeckolibData(DataTicket<?> dataTicket) {
        return this.geckolibData.containsKey(dataTicket);
    }

    public int getPackedLight() {
        return (Integer)this.getOrDefaultGeckolibData(DataTickets.PACKED_LIGHT, this.field_61820);
    }

    public double getAnimatableAge() {
        return (Double)this.getOrDefaultGeckolibData((DataTicket)DataTickets.TICK, this.field_53328);
    }

    public Map<DataTicket<?>, Object> getDataMap() {
        return this.geckolibData;
    }
}

