package org.privates.client.managers.module.impl.combat;

import com.mojang.authlib.GameProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NeuroSelf", category = Category.COMBAT, description = "Record your own vector-based aiming style")
public class NeuroSelf extends Module {

    @Getter
    public static class NeuroSample {
        final float yawError;
        final float pitchError;
        final float distance;
        final float targetSpeed;
        final float dYaw;
        final float dPitch;
        final boolean attackMoment;
        final int deltaMs;

        public NeuroSample(float yawError, float pitchError, float distance, float targetSpeed, float dYaw, float dPitch, boolean attackMoment, int deltaMs) {
            this.yawError = yawError;
            this.pitchError = pitchError;
            this.distance = distance;
            this.targetSpeed = targetSpeed;
            this.dYaw = dYaw;
            this.dPitch = dPitch;
            this.attackMoment = attackMoment;
            this.deltaMs = deltaMs;
        }
    }

    @Getter
    public static class NeuroRotationProfile {
        private final List<NeuroSample> samples;
        private final boolean valid;
        private final float maxYawStep;
        private final float maxPitchStep;

        public NeuroRotationProfile(List<NeuroSample> samples, float maxYawStep, float maxPitchStep) {
            this.samples = new ArrayList<>(samples);
            this.valid = samples.size() >= 20;
            this.maxYawStep = maxYawStep;
            this.maxPitchStep = maxPitchStep;
        }

        public NeuroRotationProfile() {
            this.samples = new ArrayList<>();
            this.valid = false;
            this.maxYawStep = 8.0f;
            this.maxPitchStep = 5.0f;
        }
    }

    final BindSetting startRecordBind = new BindSetting(this, "Начать запись", -1);
    final BindSetting stopRecordBind = new BindSetting(this, "Остановить запись", -1);
    final BooleanSetting autoSpawnFake = new BooleanSetting(this, "Авто спавн FakePlayer", true);
    final SliderSetting fakePlayerDistance = new SliderSetting(this, "Дистанция FakePlayer", 3.0F, 2.0F, 6.0F, 0.5F);
    final BooleanSetting showRecordingHUD = new BooleanSetting(this, "Показывать HUD записи", true);

    RemoteClientPlayerEntity fakePlayer;
    boolean isRecording;
    long recordStartTime;
    boolean lastFrameWasAttack;

    float prevYaw;
    float prevPitch;
    long prevSampleTime;
    Vector3d prevTargetPos = Vector3d.ZERO;
    final List<NeuroSample> recordedSamples = new ArrayList<>();
    NeuroRotationProfile generatedProfile = new NeuroRotationProfile();

    public static NeuroSelf getInstance() {
        return Instance.get(NeuroSelf.class);
    }

    public NeuroRotationProfile getGeneratedProfile() {
        return generatedProfile;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (isRecording) stopRecording();
        if (fakePlayer != null) removeFakePlayer();
        reset();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) {
            reset();
            return;
        }

        handleKeyBinds();
        if (isRecording) {
            recordSample();
        }
    }

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (isRecording && event.getTarget() instanceof PlayerEntity) {
            lastFrameWasAttack = true;
        }
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!showRecordingHUD.getValue() || !isRecording) return;

        long elapsed = System.currentTimeMillis() - recordStartTime;
        String text = String.format("§cNeuro REC: §f%.1fs §7| §fSamples: §c%d", elapsed / 1000.0, recordedSamples.size());
        MatrixStack matrix = event.getMatrix();
        FontRenderer font = mc.fontRenderer;
        float x = (mw.getScaledWidth() - font.getStringWidth(text)) / 2f;
        float y = mw.getScaledHeight() / 2f + 30f;
        font.drawStringWithShadow(matrix, text, x, y, ColorUtil.WHITE);
    }

    private void handleKeyBinds() {
        int startKey = startRecordBind.getValue();
        int stopKey = stopRecordBind.getValue();
        boolean startPressed = startKey >= 0 && org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), startKey) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
        boolean stopPressed = stopKey >= 0 && org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), stopKey) == org.lwjgl.glfw.GLFW.GLFW_PRESS;

        if (startPressed && !isRecording) startRecording(true);
        if (stopPressed && isRecording) stopRecording();
    }

    private void startRecording(boolean allowFakePlayer) {
        if (isRecording || mc.player == null) return;

        reset();
        isRecording = true;
        recordStartTime = System.currentTimeMillis();
        prevYaw = mc.player.rotationYaw;
        prevPitch = mc.player.rotationPitch;
        prevSampleTime = System.currentTimeMillis();

        if (allowFakePlayer && autoSpawnFake.getValue()) {
            spawnFakePlayer();
            ChatUtil.addText("§a[NeuroSelf]§f FakePlayer заспавнен.");
        }
        ChatUtil.addText("§a[NeuroSelf]§f Запись начата: наводись на игроков и бей.");
    }

    private void stopRecording() {
        if (!isRecording) return;
        isRecording = false;
        if (fakePlayer != null) removeFakePlayer();

        if (recordedSamples.isEmpty()) {
            ChatUtil.addText("§c[NeuroSelf]§f Нет данных для обучения.");
            return;
        }
        generateProfile();
    }

    private void recordSample() {
        if (!(mc.pointedEntity instanceof PlayerEntity target) || target == mc.player || !target.isAlive()) {
            return;
        }

        long now = System.currentTimeMillis();
        long dt = Math.max(1L, now - prevSampleTime);

        Vector2f vecRot = getVectorRotation(target);
        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float yawError = MathHelper.wrapDegrees(vecRot.x - currentYaw);
        float pitchError = vecRot.y - currentPitch;
        float distance = mc.player.getDistance(target);

        Vector3d targetPos = target.getPositionVec();
        float targetSpeed = (float) targetPos.subtract(prevTargetPos).length();
        prevTargetPos = targetPos;

        float dYaw = MathHelper.wrapDegrees(currentYaw - prevYaw);
        float dPitch = (currentPitch - prevPitch);

        // Keep only meaningful moves + attack moments
        if (Math.abs(dYaw) + Math.abs(dPitch) > 0.01f || lastFrameWasAttack) {
            recordedSamples.add(new NeuroSample(
                    yawError, pitchError, distance, targetSpeed,
                    dYaw, dPitch, lastFrameWasAttack, (int) dt
            ));
        }

        prevYaw = currentYaw;
        prevPitch = currentPitch;
        prevSampleTime = now;
        lastFrameWasAttack = false;
    }

    private Vector2f getVectorRotation(LivingEntity target) {
        Vector3d eye = mc.player.getEyePosition(1.0F);
        Vector3d aim = target.getPositionVec().add(0, target.getEyeHeight() * 0.85, 0);
        Vector3d dir = aim.subtract(eye);
        float yaw = (float) Math.toDegrees(Math.atan2(dir.z, dir.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z)));
        return new Vector2f(yaw, pitch);
    }

    private void generateProfile() {
        List<Float> yawSteps = new ArrayList<>();
        List<Float> pitchSteps = new ArrayList<>();
        for (NeuroSample s : recordedSamples) {
            yawSteps.add(Math.abs(s.dYaw));
            pitchSteps.add(Math.abs(s.dPitch));
        }

        float maxYaw = percentile(yawSteps, 0.85f);
        float maxPitch = percentile(pitchSteps, 0.85f);
        maxYaw = MathHelper.clamp(maxYaw, 3.0f, 25.0f);
        maxPitch = MathHelper.clamp(maxPitch, 2.0f, 16.0f);

        generatedProfile = new NeuroRotationProfile(recordedSamples, maxYaw, maxPitch);
        ChatUtil.addText("§a[NeuroSelf]§f Паттерн построен: samples=" + recordedSamples.size()
                + " yawStep=" + String.format("%.2f", maxYaw)
                + " pitchStep=" + String.format("%.2f", maxPitch));
    }

    private float percentile(List<Float> values, float p) {
        if (values.isEmpty()) return 0f;
        values.sort(Comparator.naturalOrder());
        int idx = Math.min(values.size() - 1, Math.max(0, (int) (values.size() * p)));
        return values.get(idx);
    }

    private void spawnFakePlayer() {
        try {
            UUID uuid = UUID.nameUUIDFromBytes("NeuroSelfTarget".getBytes());
            fakePlayer = new RemoteClientPlayerEntity(mc.world, new GameProfile(uuid, "Target"));

            double distance = fakePlayerDistance.getValue();
            double angle = Math.toRadians(mc.player.rotationYaw);
            double offsetX = -Math.sin(angle) * distance;
            double offsetZ = Math.cos(angle) * distance;

            fakePlayer.setPosition(mc.player.getPosX() + offsetX, mc.player.getPosY(), mc.player.getPosZ() + offsetZ);
            fakePlayer.setHealth(20.0f);
            fakePlayer.setInvulnerable(true);
            mc.world.addEntity(1340, fakePlayer);
        } catch (Exception e) {
            ChatUtil.addText("§c[NeuroSelf]§f Ошибка спавна FakePlayer.");
        }
    }

    private void removeFakePlayer() {
        if (fakePlayer != null && mc.world != null) {
            mc.world.removeEntityFromWorld(1340);
            fakePlayer = null;
        }
    }

    private void reset() {
        isRecording = false;
        recordStartTime = 0L;
        lastFrameWasAttack = false;
        recordedSamples.clear();
        prevSampleTime = 0L;
        prevYaw = 0f;
        prevPitch = 0f;
        prevTargetPos = Vector3d.ZERO;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public int recordedFramesCount() {
        return recordedSamples.size();
    }

    public void startFromCommand() {
        startRecording(false);
    }

    public void stopFromCommand() {
        stopRecording();
    }

    public void clearProfile() {
        reset();
        generatedProfile = new NeuroRotationProfile();
    }
}
