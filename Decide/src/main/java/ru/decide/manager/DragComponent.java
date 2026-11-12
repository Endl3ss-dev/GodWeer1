/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_3675
 *  net.minecraft.class_408
 *  net.minecraft.class_4587
 *  org.joml.Matrix3x2f
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 */
package ru.decide.manager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_3675;
import net.minecraft.class_408;
import net.minecraft.class_4587;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import ru.decide.Client;
import ru.decide.manager.event_impl.MousePressEvent;
import ru.decide.manager.event_impl.MouseReleaseEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.manager.rotation.Component;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.display.InterFace;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.taskript.Script;

@Environment(value=EnvType.CLIENT)
public class DragComponent
extends Component
implements IMinecraft {
    private DragSetting selected;
    private final Vector2f offset = new Vector2f();
    private final List<Module> modules = new CopyOnWriteArrayList<Module>();
    public static List<Line> lines = new CopyOnWriteArrayList<Line>();
    private final Script script = new Script();
    private static final float GRID = 8.0f;
    private static final float SNAP = 5.0f;
    private final List<Float> guidesV = new CopyOnWriteArrayList<Float>();
    private final List<Float> guidesH = new CopyOnWriteArrayList<Float>();
    private final Animation gridAnim = new Animation();
    private boolean deleteWasPressed = false;
    private final Animation guideVAlpha = new Animation();
    private final Animation guideHAlpha = new Animation();
    private final Animation guideVPos = new Animation();
    private final Animation guideHPos = new Animation();
    private static final Map<String, String> DRAG_TO_ELEMENT = Map.of("WaterMark", "Water mark", "Information", "Information", "Key Binds", "Key Binds", "Potions", "Potions", "Cooldowns", "Cooldowns", "Music Player", "Music Player", "Notifications", "Notifications", "Target Hud", "Target Hud", "Armor Hud", "Armor Hud");

    public void post(Matrix3x2f matrices) {
        boolean freeMove;
        boolean shouldRender;
        this.script.update();
        class_1041 window = mc.method_22683();
        float targetScale = 2.0f;
        float currentScale = mc.method_22683().method_4495();
        float scaleFix = targetScale / currentScale;
        int screenWidth = (int)((float)mc.method_22683().method_4486() / scaleFix);
        int screenHeight = (int)((float)mc.method_22683().method_4502() / scaleFix);
        boolean inChat = DragComponent.mc.field_1755 instanceof class_408;
        boolean holdingShift = class_3675.method_15987((class_1041)mc.method_22683(), (int)344);
        boolean bl = shouldRender = inChat || DragComponent.mc.field_1755 == null && holdingShift;
        if (!shouldRender) {
            this.selected = null;
        }
        this.initModules();
        this.handleAnimation();
        boolean dragging = shouldRender && this.selected != null && this.selected.active;
        this.gridAnim.update();
        this.gridAnim.run(dragging ? 1.0 : 0.0, 0.25, Easings.SINE_OUT, true);
        if (this.gridAnim.get() > 0.01f) {
            this.drawGrid(screenWidth, screenHeight, this.gridAnim.get());
        }
        this.guidesV.clear();
        this.guidesH.clear();
        boolean bl2 = freeMove = class_3675.method_15987((class_1041)mc.method_22683(), (int)342) || class_3675.method_15987((class_1041)mc.method_22683(), (int)346);
        if (shouldRender && class_3675.method_15987((class_1041)mc.method_22683(), (int)261)) {
            if (!this.deleteWasPressed) {
                this.deleteWasPressed = true;
                float delMouseX = (float)(DragComponent.mc.field_1729.method_1603() / 2.0);
                float delMouseY = (float)(DragComponent.mc.field_1729.method_1604() / 2.0);
                for (Module module : this.modules) {
                    for (Setting<?> setting : module.getSettings()) {
                        if (!(setting instanceof DragSetting)) continue;
                        DragSetting ds = (DragSetting)setting;
                        if (!ds.active || !DragComponent.isHover(delMouseX, delMouseY, ds.position.x, ds.position.y, ds.size.x, ds.size.y)) continue;
                        this.toggleElementOff(ds);
                    }
                }
            }
        } else {
            this.deleteWasPressed = false;
        }
        if (this.selected != null && this.selected.active) {
            float mouseX = (float)(DragComponent.mc.field_1729.method_1603() / 2.0);
            float mouseY = (float)(DragComponent.mc.field_1729.method_1604() / 2.0);
            float posX = mouseX + this.offset.x;
            float posY = mouseY + this.offset.y;
            if (!this.selected.lockX) {
                this.selected.targetPosition.x = posX;
            }
            this.selected.targetPosition.y = posY;
            lines.clear();
            if (!freeMove) {
                this.applySnapping();
            }
            this.handleDrags(screenWidth, screenHeight);
        }
        this.updateGuideAnimations();
        this.drawGuides(screenWidth, screenHeight, this.gridAnim.get());
    }

    private void updateGuideAnimations() {
        float target;
        boolean hasV = !this.guidesV.isEmpty();
        boolean hasH = !this.guidesH.isEmpty();
        this.guideVAlpha.update();
        this.guideHAlpha.update();
        this.guideVPos.update();
        this.guideHPos.update();
        if (hasV) {
            target = this.guidesV.get(0).floatValue();
            if (this.guideVAlpha.get() <= 0.01f) {
                this.guideVPos.set(target);
            } else {
                this.guideVPos.run(target, 0.15, Easings.BACK_OUT, true);
            }
        }
        if (hasH) {
            target = this.guidesH.get(0).floatValue();
            if (this.guideHAlpha.get() <= 0.01f) {
                this.guideHPos.set(target);
            } else {
                this.guideHPos.run(target, 0.15, Easings.BACK_OUT, true);
            }
        }
        this.guideVAlpha.run(hasV ? 1.0 : 0.0, 0.2, Easings.SINE_OUT, true);
        this.guideHAlpha.run(hasH ? 1.0 : 0.0, 0.2, Easings.SINE_OUT, true);
    }

    private void drawGrid(int width, int height, float alpha) {
        int color = ColorUtil.getColor(255, 255, 255, (int)(16.0f * alpha));
        int colorMajor = ColorUtil.getColor(255, 255, 255, (int)(34.0f * alpha));
        int i = 0;
        float x = 0.0f;
        while (x <= (float)width) {
            RenderUtil.Render2D.rect(x, 0.0f, 0.5f, height, i % 5 == 0 ? colorMajor : color);
            x += 8.0f;
            ++i;
        }
        i = 0;
        float y = 0.0f;
        while (y <= (float)height) {
            RenderUtil.Render2D.rect(0.0f, y, width, 0.5f, i % 5 == 0 ? colorMajor : color);
            y += 8.0f;
            ++i;
        }
        int center = ColorUtil.getColor(255, 120, 170, (int)(70.0f * alpha));
        RenderUtil.Render2D.rect((float)width / 2.0f, 0.0f, 0.6f, height, center);
        RenderUtil.Render2D.rect(0.0f, (float)height / 2.0f, width, 0.6f, center);
    }

    private void drawGuides(int width, int height, float alpha) {
        int color;
        if (alpha <= 0.01f) {
            return;
        }
        float aV = this.guideVAlpha.get() * alpha;
        float aH = this.guideHAlpha.get() * alpha;
        if (aV > 0.01f) {
            color = ColorUtil.getColor(255, 120, 200, (int)(200.0f * aV));
            RenderUtil.Render2D.rect(this.guideVPos.get() - 0.3f, 0.0f, 0.6f, height, color);
        }
        if (aH > 0.01f) {
            color = ColorUtil.getColor(255, 120, 200, (int)(200.0f * aH));
            RenderUtil.Render2D.rect(0.0f, this.guideHPos.get() - 0.3f, width, 0.6f, color);
        }
    }

    private void applySnapping() {
        float sx = this.selected.targetPosition.x;
        float sy = this.selected.targetPosition.y;
        float sw = this.selected.size.x;
        float sh = this.selected.size.y;
        float[] sXs = new float[]{sx, sx + sw / 2.0f, sx + sw};
        float[] sYs = new float[]{sy, sy + sh / 2.0f, sy + sh};
        Float bestX = null;
        float bestXDist = 5.0f;
        float guideX = 0.0f;
        Float bestY = null;
        float bestYDist = 5.0f;
        float guideY = 0.0f;
        for (Module module : this.modules) {
            for (Setting<?> setting : module.getSettings()) {
                float d;
                DragSetting o;
                if (!(setting instanceof DragSetting) || (o = (DragSetting)setting) == this.selected || !o.active) continue;
                float[] oXs = new float[]{o.position.x, o.position.x + o.size.x / 2.0f, o.position.x + o.size.x};
                float[] oYs = new float[]{o.position.y, o.position.y + o.size.y / 2.0f, o.position.y + o.size.y};
                for (float oe : oXs) {
                    for (float se : sXs) {
                        d = Math.abs(se - oe);
                        if (!(d < bestXDist)) continue;
                        bestXDist = d;
                        bestX = Float.valueOf(sx + (oe - se));
                        guideX = oe;
                    }
                }
                for (float oe : oYs) {
                    for (float se : sYs) {
                        d = Math.abs(se - oe);
                        if (!(d < bestYDist)) continue;
                        bestYDist = d;
                        bestY = Float.valueOf(sy + (oe - se));
                        guideY = oe;
                    }
                }
            }
        }
        if (!this.selected.lockX) {
            if (bestX != null) {
                this.selected.targetPosition.x = bestX.floatValue();
                this.guidesV.add(Float.valueOf(guideX));
            } else {
                this.selected.targetPosition.x = (float)Math.round(sx / 8.0f) * 8.0f;
            }
        }
        if (bestY != null) {
            this.selected.targetPosition.y = bestY.floatValue();
            this.guidesH.add(Float.valueOf(guideY));
        } else {
            this.selected.targetPosition.y = (float)Math.round(sy / 8.0f) * 8.0f;
        }
    }

    private void handleDrags(int width, int height) {
        for (Module module : this.modules) {
            for (Setting<?> setting : module.getSettings()) {
                if (!(setting instanceof DragSetting)) continue;
                DragSetting drag = (DragSetting)setting;
                if (!drag.active) continue;
                drag.position.x = (float)drag.animationX.getValue();
                drag.position.y = (float)drag.animationY.getValue();
                drag.position.x = Math.max(0.0f, Math.min((float)width - drag.size.x, drag.position.x));
                drag.position.y = Math.max(0.0f, Math.min((float)height - drag.size.y, drag.position.y));
                drag.targetPosition.x = Math.max(0.0f, Math.min((float)width - drag.size.x, drag.targetPosition.x));
                drag.targetPosition.y = Math.max(0.0f, Math.min((float)height - drag.size.y, drag.targetPosition.y));
            }
        }
    }

    private void handleSnaps(class_4587 matrices, int color) {
        block4: for (Line snap : lines) {
            switch (snap.direction.ordinal()) {
                case 0: {
                    double closest = Double.MAX_VALUE;
                    for (float y = -this.selected.size.y; y <= 0.0f; y += this.selected.size.y / 2.0f) {
                        double dist;
                        if (y == -this.selected.size.y / 2.0f && !snap.center || y == -this.selected.size.y && !snap.left || y == 0.0f && !snap.right || !((dist = (double)Math.abs(this.selected.targetPosition.y - (snap.position + y))) < (double)snap.distance) || !(dist < closest)) continue;
                        closest = dist;
                        this.selected.targetPosition.y = snap.position + y;
                    }
                    continue block4;
                }
                case 1: {
                    double closest = Double.MAX_VALUE;
                    for (float x = -this.selected.size.x; x <= 0.0f; x += this.selected.size.x / 2.0f) {
                        float dist;
                        if (x == -this.selected.size.x / 2.0f && !snap.center || x == -this.selected.size.x && !snap.left || x == 0.0f && !snap.right || !((dist = Math.abs(this.selected.targetPosition.x - (snap.position + x))) < snap.distance) || !((double)dist < closest)) continue;
                        closest = dist;
                        this.selected.targetPosition.x = snap.position + x;
                    }
                    break;
                }
            }
        }
    }

    private void handleAnimation() {
        this.modules.forEach(m -> m.getSettings().forEach(s -> {
            if (s instanceof DragSetting) {
                DragSetting drag = (DragSetting)s;
                drag.animationX.update();
                drag.animationY.update();
                if (this.script.isFinished()) {
                    drag.animationX.run((double)drag.targetPosition.x, 0.06, true);
                    drag.animationY.run((double)drag.targetPosition.y, 0.06, true);
                }
            }
        }));
    }

    private void initModules() {
        this.modules.clear();
        Client.get().moduleManager().values().stream().filter(m -> m.isEnabled() && m.getSettings().stream().anyMatch(s -> s instanceof DragSetting)).forEach(this.modules::add);
    }

    @EventHandler
    public void onEvent(MousePressEvent event) {
        boolean canDrag;
        if (event.getKey() != 0) {
            return;
        }
        boolean inChat = event.getScreen() instanceof class_408;
        boolean holdingShift = class_3675.method_15987((class_1041)mc.method_22683(), (int)344);
        boolean bl = canDrag = inChat || event.getScreen() == null && holdingShift;
        if (!canDrag) {
            return;
        }
        for (Module module : this.modules) {
            for (Setting<?> setting : module.getSettings()) {
                if (!(setting instanceof DragSetting)) continue;
                DragSetting dragSetting = (DragSetting)setting;
                if (!dragSetting.active) continue;
                Vector2f position = dragSetting.position;
                Vector2f scale = dragSetting.size;
                Vector2f mouse = this.getMouse((float)event.getMouseX(), (float)event.getMouseY());
                double mouseX = mouse.x;
                double mouseY = mouse.y;
                if (!dragSetting.active) {
                    return;
                }
                if (dragSetting.structure || !DragComponent.isHover(mouseX, mouseY, position.x, position.y, scale.x, scale.y)) continue;
                this.selected = dragSetting;
                this.offset.set((double)position.x - mouseX, (double)position.y - mouseY);
            }
        }
    }

    @EventHandler
    public void onEvent(MouseReleaseEvent event) {
        if (this.selected != null) {
            this.script.cleanup().addTickStep(0, () -> {
                this.selected.targetPosition.set((Vector2fc)this.selected.position);
                this.selected = null;
            }, () -> this.selected != null && this.selected.animationX.isFinished() && this.selected.animationY.isFinished());
        }
    }

    public static boolean isHover(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public Vector2f getMouse(double mouseX, double mouseY) {
        return new Vector2f((float)(mouseX * (double)mc.method_22683().method_4495() / 2.0), (float)(mouseY * (double)mc.method_22683().method_4495() / 2.0));
    }

    private void toggleElementOff(DragSetting ds) {
        BooleanSetting setting;
        String elementName = DRAG_TO_ELEMENT.get(ds.getName());
        if (elementName != null && (setting = InterFace.getInstance().element.get(elementName)) != null && setting.getValue().booleanValue()) {
            setting.set(false);
        }
    }

    @Generated
    public DragSetting getSelected() {
        return this.selected;
    }

    @Generated
    public Vector2f getOffset() {
        return this.offset;
    }

    @Generated
    public List<Module> getModules() {
        return this.modules;
    }

    @Generated
    public Script getScript() {
        return this.script;
    }

    @Generated
    public List<Float> getGuidesV() {
        return this.guidesV;
    }

    @Generated
    public List<Float> getGuidesH() {
        return this.guidesH;
    }

    @Generated
    public Animation getGridAnim() {
        return this.gridAnim;
    }

    @Generated
    public boolean isDeleteWasPressed() {
        return this.deleteWasPressed;
    }

    @Generated
    public Animation getGuideVAlpha() {
        return this.guideVAlpha;
    }

    @Generated
    public Animation getGuideHAlpha() {
        return this.guideHAlpha;
    }

    @Generated
    public Animation getGuideVPos() {
        return this.guideVPos;
    }

    @Generated
    public Animation getGuideHPos() {
        return this.guideHPos;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Line {
        public float position;
        public float distance;
        public Direction direction;
        public boolean center;
        public boolean right;
        public boolean left;

        @Generated
        public Line(float position, float distance, Direction direction, boolean center, boolean right, boolean left) {
            this.position = position;
            this.distance = distance;
            this.direction = direction;
            this.center = center;
            this.right = right;
            this.left = left;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Direction {
        VERTICAL,
        HORIZONTAL;

    }
}

