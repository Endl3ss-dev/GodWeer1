/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.maksai.core;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class Paging {
    private Paging() {
    }

    public static int pageCount(int total, int perPage) {
        if (perPage < 1) {
            perPage = 1;
        }
        return Math.max(1, (total + perPage - 1) / perPage);
    }

    public static int clampPage(int page, int total, int perPage) {
        return Math.max(0, Math.min(page, Paging.pageCount(total, perPage) - 1));
    }

    public static int firstIndex(int page, int perPage) {
        return Math.max(0, page) * Math.max(1, perPage);
    }
}

