/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1703
 *  net.minecraft.class_1735
 *  net.minecraft.class_1799
 */
package ru.decide.utils.render;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1799;

@Environment(value=EnvType.CLIENT)
public final class ItemMoveAnimator {
    private static final float DURATION = 0.14f;
    private static class_1703 lastMenu;
    private static long lastNs;
    private static class_1799 lastCarried;
    private static final Map<class_1735, class_1799> lastItems;
    private static final Map<class_1735, int[]> lastPos;
    private static final Map<class_1735, Anim> anims;
    private static final Map<class_1735, float[]> offsets;

    private ItemMoveAnimator() {
    }

    public static void beginFrame(class_1703 menu, int mouseX, int mouseY, int leftPos, int topPos) {
        offsets.clear();
        if (menu != lastMenu) {
            lastMenu = menu;
            lastItems.clear();
            lastPos.clear();
            anims.clear();
            lastCarried = class_1799.field_8037;
            lastNs = 0L;
        }
        long now = System.nanoTime();
        float dt = lastNs == 0L ? 0.016f : Math.min(0.1f, (float)(now - lastNs) / 1.0E9f);
        lastNs = now;
        HashMap<class_1735, class_1799> cur = new HashMap<class_1735, class_1799>();
        HashMap<class_1735, int[]> pos = new HashMap<class_1735, int[]>();
        for (Object slot : menu.field_7761) {
            cur.put((class_1735)slot, slot.method_7677());
            pos.put((class_1735)slot, new int[]{((class_1735)slot).field_7873, ((class_1735)slot).field_7872});
        }
        class_1799 carried = menu.method_34255();
        lastItems.keySet().retainAll(cur.keySet());
        lastPos.keySet().retainAll(cur.keySet());
        anims.keySet().retainAll(cur.keySet());
        for (Map.Entry entry : cur.entrySet()) {
            class_1735 slot = (class_1735)entry.getKey();
            class_1799 current = (class_1799)entry.getValue();
            if (current.method_7960()) {
                anims.remove(slot);
                continue;
            }
            class_1799 previous = lastItems.getOrDefault(slot, class_1799.field_8037);
            if (ItemMoveAnimator.sameKey(current, previous) || anims.containsKey(slot)) continue;
            Anim anim = new Anim();
            int[] targetPos = (int[])pos.get(slot);
            if (targetPos == null) continue;
            class_1735 fromSlot = ItemMoveAnimator.findFrom(cur, slot, current, pos, targetPos);
            if (fromSlot != null) {
                int[] fromPos = lastPos.getOrDefault(fromSlot, (int[])pos.get(fromSlot));
                if (fromPos != null) {
                    anim.fromX = fromPos[0] - targetPos[0];
                    anim.fromY = fromPos[1] - targetPos[1];
                }
            } else if (ItemMoveAnimator.sameKey(current, lastCarried) && !ItemMoveAnimator.sameKey(carried, lastCarried)) {
                anim.fromX = (float)(mouseX - leftPos - targetPos[0]) - 8.0f;
                anim.fromY = (float)(mouseY - topPos - targetPos[1]) - 8.0f;
            } else {
                anim.pop = true;
            }
            anims.put(slot, anim);
        }
        Iterator<Map.Entry<class_1735, Anim>> it = anims.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<class_1735, Anim> entry = it.next();
            Anim anim = entry.getValue();
            anim.progress += dt / 0.14f;
            if (anim.progress >= 1.0f) {
                it.remove();
                continue;
            }
            float eased = ItemMoveAnimator.easeOut(anim.progress);
            if (anim.pop) {
                offsets.put(entry.getKey(), new float[]{0.0f, 0.0f, 0.55f + 0.45f * eased});
                continue;
            }
            float f = 1.0f - eased;
            offsets.put(entry.getKey(), new float[]{anim.fromX * f, anim.fromY * f, 1.0f});
        }
        lastItems.clear();
        for (Map.Entry entry : cur.entrySet()) {
            lastItems.put((class_1735)entry.getKey(), ((class_1799)entry.getValue()).method_7972());
        }
        lastPos.clear();
        lastPos.putAll(pos);
        lastCarried = carried.method_7972();
    }

    public static float[] offset(class_1735 slot) {
        return offsets.get(slot);
    }

    private static class_1735 findFrom(Map<class_1735, class_1799> cur, class_1735 target, class_1799 item, Map<class_1735, int[]> pos, int[] targetPos) {
        class_1735 best = null;
        long bestDist = Long.MAX_VALUE;
        for (Map.Entry<class_1735, class_1799> entry : lastItems.entrySet()) {
            long dy;
            long dx;
            long dist;
            int[] fromPos;
            class_1799 nowThere;
            class_1735 slot = entry.getKey();
            class_1799 value = entry.getValue();
            if (slot == target || !ItemMoveAnimator.sameKey(value, item) || (nowThere = cur.get(slot)) != null && ItemMoveAnimator.sameKey(nowThere, item) || (fromPos = lastPos.getOrDefault(slot, pos.get(slot))) == null || (dist = (dx = (long)(fromPos[0] - targetPos[0])) * dx + (dy = (long)(fromPos[1] - targetPos[1])) * dy) >= bestDist) continue;
            bestDist = dist;
            best = slot;
        }
        return best;
    }

    private static boolean sameKey(class_1799 a, class_1799 b) {
        if (a.method_7960() && b.method_7960()) {
            return true;
        }
        if (a.method_7960() || b.method_7960()) {
            return false;
        }
        return class_1799.method_31577((class_1799)a, (class_1799)b);
    }

    private static float easeOut(float t) {
        float clamped = t < 0.0f ? 0.0f : (t > 1.0f ? 1.0f : t);
        float u = 1.0f - clamped;
        return 1.0f - u * u * u;
    }

    static {
        lastCarried = class_1799.field_8037;
        lastItems = new HashMap<class_1735, class_1799>();
        lastPos = new HashMap<class_1735, int[]>();
        anims = new HashMap<class_1735, Anim>();
        offsets = new HashMap<class_1735, float[]>();
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Anim {
        float fromX;
        float fromY;
        float progress;
        boolean pop;

        private Anim() {
        }
    }
}

