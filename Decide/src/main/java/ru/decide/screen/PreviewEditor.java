/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 *  net.minecraft.class_745
 */
package ru.decide.screen;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_745;
import ru.decide.Client;
import ru.decide.lang.Lang;
import ru.decide.module.api.Module;
import ru.decide.module.api.preview.ModulePreview;
import ru.decide.module.api.preview.PreviewContext;
import ru.decide.module.api.preview.PreviewSettings;
import ru.decide.screen.editor.EditorButton;
import ru.decide.screen.editor.EditorWidgets;
import ru.decide.screen.editor.OverlayEditor;
import ru.decide.screen.editor.Rect;
import ru.decide.screen.editor.SettingsPanel;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class PreviewEditor
implements OverlayEditor,
IMinecraft {
    private static final PreviewEditor INSTANCE = new PreviewEditor();
    private static final UUID DUMMY_UUID = UUID.fromString("32d0a964-137a-2c03-7cc9-df6700000101");
    private static final String DUMMY_NAME = "Decide Visual";
    private final EditorWidgets widgets = new EditorWidgets();
    private final SettingsPanel panel = new SettingsPanel();
    private final PreviewContext context = new PreviewContext();
    private final EditorButton saveButton = new EditorButton("\u0412\u044b\u0439\u0442\u0438 \u0438 \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c", true);
    private final EditorButton spawnButton = new EditorButton("\u041f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u0441\u0435\u0439\u0447\u0430\u0441", false);
    private final Animation visibility = new Animation();
    private boolean active;
    private Module module;
    private ModulePreview preview;
    private boolean restoreEnabled;
    private class_745 dummy;
    private long nextSpawnAt;
    private boolean freeCamera;
    private Rect freeCameraBounds = Rect.EMPTY;
    private boolean draggingCamera;
    private float lastCameraMouseX;
    private float lastCameraMouseY;
    private class_243 frozenOrigin;
    private double frozenForwardX;
    private double frozenForwardZ;
    private float frozenFacingYaw;

    private PreviewEditor() {
    }

    public static PreviewEditor getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    public boolean isEditing(Module candidate) {
        return this.active && this.module == candidate;
    }

    public Module module() {
        return this.module;
    }

    public void open(Module target) {
        if (!(target instanceof ModulePreview)) {
            return;
        }
        ModulePreview modulePreview = (ModulePreview)((Object)target);
        if (this.active) {
            this.deactivate();
        }
        this.active = true;
        this.module = target;
        this.preview = modulePreview;
        this.restoreEnabled = target.isEnabled();
        if (!target.isEnabled()) {
            target.setEnabled(true, false);
        }
        this.context.reset();
        this.panel.reset();
        this.panel.unplace();
        this.setFreeCamera(false);
        this.updateAnchor();
        if (modulePreview.previewNeedsDummy()) {
            this.spawnDummy();
        }
        this.context.setDummy(this.dummy);
        modulePreview.previewStart(this.context);
        this.nextSpawnAt = System.currentTimeMillis() + 250L;
        this.visibility.set(0.0);
        this.visibility.run(1.0, (double)0.24f, Easings.EXPO_OUT);
    }

    @Override
    public void saveAndExit() {
        if (!this.active) {
            return;
        }
        if (Client.get() != null && Client.get().configManager() != null) {
            Client.get().configManager().autoSave();
        }
        this.deactivate();
    }

    @Override
    public void closeFromMenuRemoval() {
        this.saveAndExit();
    }

    private void deactivate() {
        if (this.preview != null) {
            this.preview.previewStop();
        }
        this.setFreeCamera(false);
        this.removeDummy();
        if (this.module != null && this.module.isEnabled() != this.restoreEnabled) {
            this.module.setEnabled(this.restoreEnabled, false);
        }
        this.panel.releaseDrag();
        this.panel.closePickers();
        this.active = false;
        this.module = null;
        this.preview = null;
        this.context.reset();
    }

    private void spawnDummy() {
        if (PreviewEditor.mc.field_1687 == null || PreviewEditor.mc.field_1724 == null) {
            return;
        }
        this.removeDummy();
        class_745 entity = new class_745(PreviewEditor.mc.field_1687, new GameProfile(DUMMY_UUID, DUMMY_NAME));
        class_243 anchor = this.context.anchor();
        entity.method_5808(anchor.field_1352, anchor.field_1351, anchor.field_1350, this.facingYaw(), 0.0f);
        entity.method_6122(class_1268.field_5808, new class_1799((class_1935)class_1802.field_22022));
        entity.method_6122(class_1268.field_5810, new class_1799((class_1935)class_1802.field_8288));
        entity.method_6033(20.0f);
        entity.method_6073(0.0f);
        PreviewEditor.mc.field_1687.method_53875((class_1297)entity);
        this.dummy = entity;
        this.context.setDummy(entity);
    }

    private void removeDummy() {
        if (this.dummy != null) {
            this.dummy.method_31472();
            this.dummy = null;
        }
        this.context.setDummy(null);
    }

    private float facingYaw() {
        if (this.frozenOrigin != null) {
            return this.frozenFacingYaw;
        }
        return PreviewEditor.mc.field_1724 == null ? 0.0f : class_3532.method_15393((float)(PreviewEditor.mc.field_1724.method_36454() + 180.0f));
    }

    private void updateAnchor() {
        double forwardZ;
        double forwardX;
        class_243 origin;
        if (this.module == null || this.preview == null || PreviewEditor.mc.field_1724 == null) {
            return;
        }
        PreviewSettings settings = this.preview.previewSettings();
        float distance = settings.distance(4.0f);
        float height = settings.height(0.0f);
        class_243 class_2432 = origin = this.frozenOrigin != null ? this.frozenOrigin : PreviewEditor.mc.field_1724.method_73189();
        if (this.frozenOrigin != null) {
            forwardX = this.frozenForwardX;
            forwardZ = this.frozenForwardZ;
        } else {
            double rad = Math.toRadians(PreviewEditor.mc.field_1724.method_36454());
            forwardX = -Math.sin(rad);
            forwardZ = Math.cos(rad);
        }
        this.context.setDistance(distance);
        this.context.setHeight(height);
        this.context.setAnchor(new class_243(origin.field_1352 + forwardX * (double)distance, origin.field_1351 + (double)height, origin.field_1350 + forwardZ * (double)distance));
    }

    private void setFreeCamera(boolean enabled) {
        this.freeCamera = enabled;
        this.draggingCamera = false;
        if (!enabled || PreviewEditor.mc.field_1724 == null) {
            this.frozenOrigin = null;
            return;
        }
        double rad = Math.toRadians(PreviewEditor.mc.field_1724.method_36454());
        this.frozenOrigin = PreviewEditor.mc.field_1724.method_73189();
        this.frozenForwardX = -Math.sin(rad);
        this.frozenForwardZ = Math.cos(rad);
        this.frozenFacingYaw = class_3532.method_15393((float)(PreviewEditor.mc.field_1724.method_36454() + 180.0f));
    }

    private void rotateCamera(float mouseX, float mouseY) {
        if (PreviewEditor.mc.field_1724 == null) {
            return;
        }
        float dx = (mouseX - this.lastCameraMouseX) * 0.6f;
        float dy = (mouseY - this.lastCameraMouseY) * 0.6f;
        this.lastCameraMouseX = mouseX;
        this.lastCameraMouseY = mouseY;
        float yaw = PreviewEditor.mc.field_1724.method_36454() + dx;
        float pitch = class_3532.method_15363((float)(PreviewEditor.mc.field_1724.method_36455() + dy), (float)-90.0f, (float)90.0f);
        PreviewEditor.mc.field_1724.method_36456(yaw);
        PreviewEditor.mc.field_1724.method_36457(pitch);
        PreviewEditor.mc.field_1724.field_5982 = yaw;
        PreviewEditor.mc.field_1724.field_6004 = pitch;
        PreviewEditor.mc.field_1724.method_5847(yaw);
    }

    private void updatePreview() {
        if (!this.active || this.preview == null) {
            return;
        }
        if (PreviewEditor.mc.field_1687 == null || PreviewEditor.mc.field_1724 == null) {
            return;
        }
        this.updateAnchor();
        if (this.preview.previewNeedsDummy()) {
            if (this.dummy == null || this.dummy.method_31481()) {
                this.spawnDummy();
            } else if (!this.preview.previewControlsDummy()) {
                class_243 anchor = this.context.anchor();
                this.dummy.method_5814(anchor.field_1352, anchor.field_1351, anchor.field_1350);
                float yaw = this.facingYaw();
                this.dummy.method_36456(yaw);
                this.dummy.method_5636(yaw);
                this.dummy.method_5847(yaw);
                this.dummy.method_36457(0.0f);
            }
        }
        this.preview.previewTick(this.context);
        if (!this.preview.previewSettings().repeats()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now >= this.nextSpawnAt) {
            this.preview.previewSpawn(this.context);
            this.context.nextPhase();
            this.nextSpawnAt = now + Math.max(200L, this.preview.previewSettings().intervalMs());
        }
    }

    @Override
    public void render(float width, float height, float mouseX, float mouseY, float parentAlpha) {
        if (!this.active || this.module == null) {
            return;
        }
        this.widgets.setMouse(mouseX, mouseY);
        this.updatePreview();
        this.panel.layout(this.module, this.module.getBigName(), width - 168.0f - 16.0f, 14.0f, width, height, this.widgets);
        boolean repeats = this.preview.previewSettings().repeats();
        this.layoutButtons(width, height, repeats);
        this.saveButton.update(mouseX, mouseY);
        if (repeats) {
            this.spawnButton.update(mouseX, mouseY);
        }
        this.visibility.update();
        float alpha = this.visibility.get() * parentAlpha;
        this.drawBand(0.0f, 0.0f, width, 62.0f, alpha, true);
        this.drawBand(0.0f, height - 62.0f, width, 62.0f, alpha, false);
        Client.get().render2D().flushAll();
        Fonts.sf_regular.drawCentered(Lang.tr("\u041f\u0440\u0435\u0434\u043f\u043e\u043a\u0430\u0437") + " \u2014 " + this.module.getBigName(), width / 2.0f, 16.0f, 8.0f, ColorUtil.getColor(240, alpha));
        Fonts.sf_regular.drawCentered(Lang.tr(repeats ? "\u042d\u0444\u0444\u0435\u043a\u0442 \u043f\u043e\u0432\u0442\u043e\u0440\u044f\u0435\u0442\u0441\u044f \u0441\u0430\u043c \u2014 \u043a\u0440\u0443\u0442\u0438\u0442\u0435 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u0441\u043f\u0440\u0430\u0432\u0430" : "\u042d\u0444\u0444\u0435\u043a\u0442 \u0432\u0438\u0434\u0435\u043d \u043f\u043e\u0441\u0442\u043e\u044f\u043d\u043d\u043e \u2014 \u043a\u0440\u0443\u0442\u0438\u0442\u0435 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u0441\u043f\u0440\u0430\u0432\u0430"), width / 2.0f, 29.0f, 6.0f, ColorUtil.getColor(255, alpha * 0.45f));
        Fonts.sf_regular.drawCentered(Lang.tr(this.freeCamera ? "\u0422\u0430\u0449\u0438\u0442\u0435 \u043f\u043e \u043f\u0443\u0441\u0442\u043e\u043c\u0443 \u043c\u0435\u0441\u0442\u0443, \u0447\u0442\u043e\u0431\u044b \u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c\u0441\u044f" : "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043f\u0440\u0435\u0434\u043f\u043e\u043a\u0430\u0437\u0430 \u2014 \u0432 \u0441\u0430\u043c\u043e\u043c \u043d\u0438\u0437\u0443 \u043f\u0430\u043d\u0435\u043b\u0438") + "   \u2022   " + Lang.tr("Esc \u2014 \u0432\u044b\u0439\u0442\u0438"), width / 2.0f, 39.0f, 5.5f, ColorUtil.getColor(255, alpha * 0.3f));
        this.widgets.drawToggleRow(this.freeCameraBounds, Lang.tr("\u0421\u0432\u043e\u0431\u043e\u0434\u043d\u0430\u044f \u043a\u0430\u043c\u0435\u0440\u0430"), this.freeCamera, "preview:freecamera", alpha);
        if (repeats) {
            this.widgets.drawButton(this.spawnButton, alpha);
        }
        this.widgets.drawButton(this.saveButton, alpha);
        this.panel.draw(this.widgets, alpha);
    }

    private void drawBand(float x, float y, float width, float height, float alpha, boolean fadeDown) {
        int solid = ColorUtil.getColor(0, 0.55f * alpha);
        int clear = ColorUtil.getColor(0, 0.0f);
        int top = fadeDown ? solid : clear;
        int bottom = fadeDown ? clear : solid;
        RenderUtil.Render2D.gradientRect(x, y, width, height, new int[]{top, top, bottom, bottom}, 0.0f);
    }

    private void layoutButtons(float width, float height, boolean repeats) {
        float buttonH = 18.0f;
        float saveW = 120.0f;
        float spawnW = 118.0f;
        float bottomY = height - 30.0f;
        this.saveButton.set(width / 2.0f - saveW / 2.0f, bottomY, saveW, buttonH);
        this.spawnButton.set(width / 2.0f - spawnW / 2.0f, bottomY - 24.0f, spawnW, repeats ? buttonH : 0.0f);
        float rowY = bottomY - 24.0f - (repeats ? 22.0f : 0.0f);
        this.freeCameraBounds = new Rect(width / 2.0f - 80.0f, rowY, 160.0f, 13.0f);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        if (!this.active) {
            return false;
        }
        if ((button == 0 || button == 1) && this.panel.handleClick(mouseX, mouseY, button)) {
            return true;
        }
        if (button == 0) {
            if (this.freeCameraBounds.contains(mouseX, mouseY)) {
                this.setFreeCamera(!this.freeCamera);
            } else if (this.saveButton.contains(mouseX, mouseY)) {
                this.saveButton.press();
                this.saveAndExit();
            } else if (this.spawnButton.contains(mouseX, mouseY)) {
                this.spawnButton.press();
                this.spawnNow();
            }
        }
        return true;
    }

    private boolean overControls(float mouseX, float mouseY) {
        return this.panel.bounds().contains(mouseX, mouseY) || this.freeCameraBounds.contains(mouseX, mouseY) || this.saveButton.contains(mouseX, mouseY) || this.spawnButton.contains(mouseX, mouseY);
    }

    private void spawnNow() {
        if (this.preview == null || PreviewEditor.mc.field_1724 == null) {
            return;
        }
        if (!this.preview.previewSettings().repeats()) {
            return;
        }
        this.updateAnchor();
        this.preview.previewSpawn(this.context);
        this.context.nextPhase();
        this.nextSpawnAt = System.currentTimeMillis() + Math.max(200L, this.preview.previewSettings().intervalMs());
    }

    @Override
    public boolean mouseReleased(int button) {
        if (!this.active) {
            return false;
        }
        if (button == 0) {
            this.panel.releaseDrag();
            this.draggingCamera = false;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(float mouseX, float mouseY, double verticalAmount) {
        if (!this.active) {
            return false;
        }
        this.panel.handleScroll(mouseX, mouseY, verticalAmount);
        return true;
    }

    @Override
    public void rawMouseButton(float mouseX, float mouseY, int button, boolean pressed) {
        if (!this.active) {
            return;
        }
        if (!pressed) {
            if (button == 0) {
                this.panel.releaseDrag();
                this.draggingCamera = false;
            }
            return;
        }
        if (this.freeCamera && button == 0 && !this.overControls(mouseX, mouseY)) {
            this.draggingCamera = true;
            this.lastCameraMouseX = mouseX;
            this.lastCameraMouseY = mouseY;
        }
    }

    @Override
    public void rawMouseMoved(float mouseX, float mouseY) {
        if (!this.active) {
            return;
        }
        if (this.panel.rawMouseMoved(mouseX, mouseY)) {
            return;
        }
        if (this.draggingCamera) {
            this.rotateCamera(mouseX, mouseY);
        }
    }

    public Rect panelBounds() {
        return this.panel.bounds();
    }
}

