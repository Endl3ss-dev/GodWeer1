/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_1291
 *  net.minecraft.class_1293
 *  net.minecraft.class_1304
 *  net.minecraft.class_1799
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_408
 *  net.minecraft.class_5321
 *  net.minecraft.class_6880
 *  org.joml.Matrix3x2fStack
 *  org.joml.Vector2fc
 */
package ru.decide.module.impl.display.interfaceimpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_408;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2fc;
import ru.decide.Client;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.display.InterFace;
import ru.decide.module.impl.display.interfaceimpl.element;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorFormatting;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.notification.NotificationManager;
import ru.decide.utils.render.ItemRender;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class Notify
implements element {
    private final Animation notifyGhostAnim = new Animation();
    private final Set<String> notifiedEffectExpiry = new HashSet<String>();
    private final Map<String, Boolean> prevModuleStates = new HashMap<String, Boolean>();
    private boolean moduleStatesInitialized = false;
    private final Set<class_1304> armorNotified = new HashSet<class_1304>();
    private static float S = 1.0f;
    private static float FONT_GHOST = 5.5f * S;
    private static float FONT_MAIN = 6.5f * S;
    private static float RADIUS = 5.0f * S;
    private static float ITEM_H = 16.0f * S;
    private static float GLOW_H = 15.5f * S;
    private static float STEP_Y = 22.0f * S;
    private static float GHOST_W = 80.0f * S;
    private static float GHOST_H = 14.0f * S;
    private static float STACK_BASE_W = 24.0f * S;
    private static float ITEM_BASE_W = 12.0f * S;
    private boolean positionInitialized = false;

    public void onTick(BooleanSetting notifModules, BooleanSetting notifArmor, BooleanSetting notifEffects) {
        if (Notify.mc.field_1724 == null || Notify.mc.field_1687 == null) {
            return;
        }
        Collection<Module> allMods = Client.get().moduleManager().values();
        if (!this.moduleStatesInitialized) {
            for (Module module : allMods) {
                this.prevModuleStates.put(module.getName(), module.isEnabled());
            }
            this.moduleStatesInitialized = true;
        } else if (notifModules.getValue().booleanValue()) {
            for (Module module : allMods) {
                int curr;
                int prev = this.prevModuleStates.getOrDefault(module.getName(), false).booleanValue();
                if (prev != (curr = module.isEnabled())) {
                    String g = switch (module.getCategory()) {
                        default -> throw new MatchException(null, null);
                        case Category.VISUALS -> "u";
                        case Category.HUD -> "H";
                        case Category.UTILITIES -> "L";
                        case Category.COSMETICS -> "C";
                        case Category.CONFIGS -> "P";
                        case Category.MARKERS -> "S";
                    };
                    NotificationManager.send("\u0424\u0443\u043d\u043a\u0446\u0438\u044f " + module.getBigName() + (curr != 0 ? " \u0430\u043a\u0442\u0438\u0432\u0438\u0440\u043e\u0432\u0430\u043d\u0430" : " \u0434\u0435\u0430\u043a\u0442\u0438\u0432\u0438\u0440\u043e\u0432\u0430\u043d\u0430"), NotificationManager.Type.MODULE, module.getBigName());
                }
                this.prevModuleStates.put(module.getName(), curr != 0);
            }
        } else {
            for (Module module : allMods) {
                this.prevModuleStates.put(module.getName(), module.isEnabled());
            }
        }
        if (notifArmor.getValue().booleanValue()) {
            class_1304[] slots;
            for (class_1304 slot : slots = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166}) {
                class_1799 stack = Notify.mc.field_1724.method_6118(slot);
                if (stack.method_7960() || stack.method_7936() == 0) {
                    this.armorNotified.remove(slot);
                    continue;
                }
                float dur = 1.0f - (float)stack.method_7919() / (float)stack.method_7936();
                if (dur < 0.1f && !this.armorNotified.contains(slot)) {
                    this.armorNotified.add(slot);
                    String slotName = switch (slot) {
                        case class_1304.field_6169 -> "\u0428\u043b\u0435\u043c";
                        case class_1304.field_6174 -> "\u041d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a";
                        case class_1304.field_6172 -> "\u041f\u043e\u043d\u043e\u0436\u0438";
                        case class_1304.field_6166 -> "\u0411\u043e\u0442\u0438\u043d\u043a\u0438";
                        default -> slot.method_5923();
                    };
                    NotificationManager.send(slotName + " \u043f\u043e\u0447\u0442\u0438 \u0441\u043b\u043e\u043c\u0430\u043d!", NotificationManager.Type.WARNING, 5000L);
                    continue;
                }
                if (!(dur >= 0.1f)) continue;
                this.armorNotified.remove(slot);
            }
        }
        if (notifEffects.getValue().booleanValue()) {
            for (class_1293 class_12932 : Notify.mc.field_1724.method_6026()) {
                String name = ((class_1291)class_12932.method_5579().comp_349()).method_5560().getString();
                int ticks = class_12932.method_5584();
                if (ticks <= 0) continue;
                if (ticks > 200) {
                    this.notifiedEffectExpiry.remove(name);
                    continue;
                }
                if (this.notifiedEffectExpiry.contains(name)) continue;
                this.notifiedEffectExpiry.add(name);
                class_2960 tex = Notify.getEffectTexture((class_6880<class_1291>)class_12932.method_5579());
                NotificationManager.send(name + " \u0437\u0430\u043a\u0430\u043d\u0447\u0438\u0432\u0430\u0435\u0442\u0441\u044f!", NotificationManager.Type.EFFECT, tex, 4000L);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onRender(DragSetting drag, InterFace interFace, EventDisplay eventDisplay) {
        S = InterFace.getInstance().sizeHud.getValue().floatValue();
        FONT_GHOST = 5.5f * S;
        FONT_MAIN = 6.5f * S;
        RADIUS = 5.0f * S;
        ITEM_H = 16.0f * S;
        GLOW_H = 15.5f * S;
        STEP_Y = 22.0f * S;
        GHOST_W = 80.0f * S;
        GHOST_H = 14.0f * S;
        STACK_BASE_W = 24.0f * S;
        ITEM_BASE_W = 12.0f * S;
        float targetScale = 2.0f;
        float currentScale = mc.method_22683().method_4495();
        float scaleFix = targetScale / currentScale;
        int screenWidth = (int)((float)mc.method_22683().method_4486() / scaleFix);
        float alpha = Notify.getAlpha();
        List<NotificationManager.Entry> list = NotificationManager.entries();
        synchronized (list) {
            List<NotificationManager.Entry> notifs = NotificationManager.entries();
            notifs.removeIf(e -> e.removing && e.anim.get() <= 0.01f);
            for (NotificationManager.Entry e2 : notifs) {
                e2.anim.update();
                if (e2.isExpired()) {
                    e2.removing = true;
                }
                e2.anim.run(e2.removing ? 0.0 : 1.0, (double)0.15f, Easings.SINE_OUT);
            }
            float stackW = GHOST_W;
            float stackH = 0.0f;
            for (NotificationManager.Entry e3 : notifs) {
                float a = e3.anim.get();
                if (a <= 0.01f) continue;
                float tw = Fonts.sf_regular.getWidth(e3.text, FONT_GHOST);
                stackW = Math.max(stackW, STACK_BASE_W + tw);
                stackH += (ITEM_H + 2.0f * S) * a;
            }
            if (stackH <= 0.0f) {
                stackH = ITEM_H;
            }
            drag.size.set(stackW, stackH);
            if (!this.positionInitialized) {
                this.positionInitialized = true;
                if (drag.position.x == 0.0f && drag.position.y == 0.0f) {
                    drag.position.x = (float)screenWidth / 2.0f - stackW / 2.0f;
                    drag.position.y = (float)mc.method_22683().method_4502() / scaleFix * 0.35f;
                    drag.targetPosition.set((Vector2fc)drag.position);
                }
            }
            float ny = drag.position.y;
            boolean showGhost = Notify.mc.field_1755 instanceof class_408 && notifs.stream().noneMatch(e -> e.anim.get() > 0.01f);
            drag.active = showGhost || notifs.stream().anyMatch(e -> e.anim.get() > 0.01f);
            this.notifyGhostAnim.update();
            this.notifyGhostAnim.run(showGhost ? 1.0 : 0.0, 0.2, Easings.BACK_OUT, true);
            float ga = class_3532.method_15363((float)this.notifyGhostAnim.get(), (float)0.0f, (float)1.0f);
            if (ga > 0.01f) {
                float gw = GHOST_W * (0.6f + 0.4f * this.notifyGhostAnim.get());
                float gh = GHOST_H * (0.6f + 0.4f * this.notifyGhostAnim.get());
                float gx = drag.position.x + (stackW - gw) / 2.0f;
                float gy = ny + (GHOST_H - gh) / 2.0f;
                RenderUtil.Render2D.glow(gx, gy, gw, gh, ColorUtil.getColor(0, 0.1f * ga), RADIUS, 8.0f, 1.0f);
                RenderUtil.Blur.blur(gx, gy, gw, gh, 1.0f, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(ga * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.35f, 1.0f)));
                Fonts.sf_regular.draw("\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f", gx + (gw - Fonts.sf_regular.getWidth("\u0423\u0432\u0435\u0434\u043e\u043c\u043b\u0435\u043d\u0438\u044f", FONT_GHOST)) / 2.0f, gy + (gh - FONT_GHOST) / 2.0f, FONT_GHOST, ColorUtil.getColor(175, 0.6f * ga));
            }
            for (NotificationManager.Entry e4 : notifs) {
                float a = e4.anim.get();
                if (a <= 0.01f) continue;
                float tw = Fonts.sf_regular.getWidth(e4.text, FONT_MAIN);
                boolean hasIcon = e4.itemStack != null || e4.effectTexture != null;
                float iconW = hasIcon ? 8.0f * S : 0.0f;
                float padX = hasIcon ? 4.0f * S : 7.0f * S;
                float nw = (hasIcon ? iconW + padX : padX) + tw + padX;
                float nx = drag.position.x + (stackW - nw) / 2.0f;
                float actualY = ny;
                RenderUtil.Render2D.glow(nx, actualY, nw, ITEM_H, ColorUtil.getColor(0, 0.1f * a), RADIUS, 8.0f, 1.0f);
                RenderUtil.Blur.blur(nx, actualY, nw, ITEM_H, 1.0f, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(a * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.35f, 1.0f)));
                if (hasIcon) {
                    this.drawIcon(eventDisplay, e4, nx, actualY, a, scaleFix);
                }
                Fonts.sf_regular.draw(e4.text, nx + (hasIcon ? iconW + padX : padX), actualY + (ITEM_H - FONT_MAIN) / 2.0f, FONT_MAIN, ColorUtil.replAlpha(ColorUtil.getColor(255), a * 0.95f));
                ny += STEP_Y * a;
            }
        }
    }

    private void drawIcon(EventDisplay eventDisplay, NotificationManager.Entry e, float nx, float actualY, float a, float scaleFix) {
        if (e.itemStack != null) {
            Matrix3x2fStack matrix = eventDisplay.getDrawContext().method_51448();
            matrix.pushMatrix();
            matrix.translate((nx + 4.0f * S) * scaleFix, (actualY + 8.0f * S) * scaleFix);
            matrix.scale(0.6f * S, 0.6f * S);
            ItemRender.drawItemWithContext(eventDisplay.getDrawContext(), e.itemStack, -8.0f, -8.0f, a, a);
            matrix.popMatrix();
        } else if (e.effectTexture != null) {
            Matrix3x2fStack matrix = eventDisplay.getDrawContext().method_51448();
            matrix.pushMatrix();
            matrix.translate((nx + 2.0f * S) * scaleFix, (actualY + 4.0f * S) * scaleFix);
            eventDisplay.getDrawContext().method_52707(class_10799.field_56883, e.effectTexture, 0, 0, (int)(8.0f * S * scaleFix), (int)(8.0f * S * scaleFix), ColorUtil.getColor(255, a));
            matrix.popMatrix();
        }
    }

    private static String formatText(NotificationManager.Entry e, float a, float alpha) {
        if (e.iconGlyph == null || e.iconGlyph.isEmpty() || !e.text.contains(e.iconGlyph)) {
            return e.text;
        }
        return e.text.replace(e.iconGlyph, ColorFormatting.getColor(ThemeColor.getHudColor(a * alpha)) + e.iconGlyph + ColorFormatting.reset());
    }

    private static float getAlpha() {
        return ThemeColor.getOpacity() < 0.98f ? ThemeColor.getOpacity() : 0.98f;
    }

    private static class_2960 getEffectTexture(class_6880<class_1291> effect) {
        return effect.method_40230().map(class_5321::method_29177).map(id -> id.method_45138("mob_effect/")).orElse(class_2960.method_60656((String)"mob_effect/speed"));
    }

    @Override
    public void onRender(DragSetting drag, InterFace interFace) {
    }
}

