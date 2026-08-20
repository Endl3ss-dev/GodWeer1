package org.privates.client.managers.module.impl.combat;

import com.mojang.authlib.GameProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SelfRot", description = "NeuroSelf rotation to make", category = Category.COMBAT)
public class SelfRotation extends Module {

    @Getter
    private boolean isRecording = false;
    @Getter
    private boolean isPlaying = false;

    private final List<RotationFrame> recordedFrames = new ArrayList<>();
    private int currentPlaybackFrame = 0;
    private long recordingStartTime = 0;
    private long playbackStartTime = 0;

    private RemoteClientPlayerEntity fakeTarget = null;
    private Vector3d fakePlayerSpawnPos = null;

    private final BindSetting recordBind;
    private final BindSetting stopBind;
    private final BindSetting saveBind;
    private final BindSetting loadBind;

    private boolean wasRecordPressed = false;
    private boolean wasStopPressed = false;
    private boolean wasSavePressed = false;
    private boolean wasLoadPressed = false;

    private static final String ROTATION_DATA_FILE = "private/rotations/self_rotation.dat";
    private static final int FAKE_PLAYER_ENTITY_ID = 13371337;

    public static SelfRotation getInstance() {
        return Instance.get(SelfRotation.class);
    }

    public SelfRotation() {
        this.recordBind = new BindSetting(this, "Начать запись", -1);
        this.stopBind = new BindSetting(this, "Остановить запись", -1);
        this.saveBind = new BindSetting(this, "Сохранить запись", -1);
        this.loadBind = new BindSetting(this, "Загрузить запись", -1);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        handleKeyBinds();

        if (isRecording) {
            recordFrame();
            updateFakePlayerRotation();
        }

        if (isPlaying) {
            playbackFrame();
        }
    }

    private void handleKeyBinds() {
        boolean recordPressed = org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), recordBind.getValue()) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
        boolean stopPressed = org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), stopBind.getValue()) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
        boolean savePressed = org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), saveBind.getValue()) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
        boolean loadPressed = org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), loadBind.getValue()) == org.lwjgl.glfw.GLFW.GLFW_PRESS;

        if (recordPressed && !wasRecordPressed) {
            startRecording();
        }

        if (stopPressed && !wasStopPressed) {
            stopRecording();
        }

        if (savePressed && !wasSavePressed) {
            saveRecording();
        }

        if (loadPressed && !wasLoadPressed) {
            loadRecording();
        }

        wasRecordPressed = recordPressed;
        wasStopPressed = stopPressed;
        wasSavePressed = savePressed;
        wasLoadPressed = loadPressed;
    }

    public void startRecording() {
        if (isRecording) {
            ChatUtil.addText(TextFormatting.RED + "Запись уже идёт!");
            return;
        }

        if (mc.player == null || mc.world == null) return;

        recordedFrames.clear();
        currentPlaybackFrame = 0;

        fakePlayerSpawnPos = mc.player.getPositionVec().add(
                mc.player.getLookVec().scale(4.0)
        );

        spawnFakePlayer();

        isRecording = true;
        recordingStartTime = System.currentTimeMillis();

        ChatUtil.addText(TextFormatting.GREEN + "Начата запись ротации на FakePlayer!");
        ChatUtil.addText(TextFormatting.YELLOW + "FakePlayer заспавнен перед вами");
    }

    private void spawnFakePlayer() {
        if (mc.world == null || mc.player == null) return;

        UUID uuid = UUID.nameUUIDFromBytes("SelfRotationTarget".getBytes());
        fakeTarget = new RemoteClientPlayerEntity(mc.world, new GameProfile(uuid, "RotationTarget"));

        if (fakePlayerSpawnPos != null) {
            fakeTarget.setPosition(fakePlayerSpawnPos.x, fakePlayerSpawnPos.y + 3, fakePlayerSpawnPos.z);
        } else {
            fakeTarget.copyLocationAndAnglesFrom(mc.player);
        }

        fakeTarget.rotationYawHead = mc.player.rotationYawHead;
        fakeTarget.renderYawOffset = mc.player.renderYawOffset;
        fakeTarget.rotationPitchHead = mc.player.rotationPitchHead;
        fakeTarget.container = mc.player.container;
        fakeTarget.inventory = mc.player.inventory;
        fakeTarget.setHealth(20.0f);

        mc.world.addEntity(FAKE_PLAYER_ENTITY_ID, fakeTarget);
    }

    private void updateFakePlayerRotation() {
        if (fakeTarget == null || mc.player == null) return;

        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());
        Vector3d fakePos = fakeTarget.getPositionVec().add(0, fakeTarget.getEyeHeight(), 0);

        Vector3d direction = playerEyePos.subtract(fakePos).normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.hypot(direction.x, direction.z)));

        fakeTarget.rotationYaw = targetYaw;
        fakeTarget.rotationPitch = targetPitch;
        fakeTarget.rotationYawHead = targetYaw;
        fakeTarget.rotationPitchHead = targetPitch;
        fakeTarget.renderYawOffset = targetYaw;
        fakeTarget.prevRotationYaw = targetYaw;
        fakeTarget.prevRotationPitch = targetPitch;
    }

    public void stopRecording() {
        if (!isRecording) {
            ChatUtil.addText(TextFormatting.RED + "Запись не была начата!");
            return;
        }

        isRecording = false;

        removeFakePlayer();

        ChatUtil.addText(TextFormatting.GREEN + "Запись остановлена!");
        ChatUtil.addText(TextFormatting.YELLOW + "Записано кадров: " + recordedFrames.size());
        ChatUtil.addText(TextFormatting.YELLOW + "Длительность: " + (System.currentTimeMillis() - recordingStartTime) + "мс");
    }

    private void removeFakePlayer() {
        if (fakeTarget != null && mc.world != null) {
            mc.world.removeEntityFromWorld(FAKE_PLAYER_ENTITY_ID);
            fakeTarget = null;
        }
    }

    private void recordFrame() {
        if (mc.player == null || fakeTarget == null) return;

        long timestamp = System.currentTimeMillis() - recordingStartTime;

        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;

        float prevYaw = mc.player.prevRotationYaw;
        float prevPitch = mc.player.prevRotationPitch;

        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d playerMotion = mc.player.getMotion();
        Vector3d eyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());

        Vector3d fakePos = fakeTarget.getPositionVec();
        Vector3d targetVec = fakePos.subtract(eyePos);

        double distance = playerPos.distanceTo(fakePos);

        float yawDelta = yaw - prevYaw;
        float pitchDelta = pitch - prevPitch;

        float yawSpeed = Math.abs(yawDelta);
        float pitchSpeed = Math.abs(pitchDelta);

        Vector2f lerpRotation = AuraNew.getInstance() != null ?
                AuraNew.getInstance().getLerpRotation() : new Vector2f(yaw, pitch);

        float yawAcceleration = 0f;
        float pitchAcceleration = 0f;

        if (!recordedFrames.isEmpty()) {
            RotationFrame lastFrame = recordedFrames.get(recordedFrames.size() - 1);
            yawAcceleration = yawSpeed - lastFrame.yawSpeed;
            pitchAcceleration = pitchSpeed - lastFrame.pitchSpeed;
        }

        float smoothnessYaw = yawSpeed > 0 ? Math.min(yawDelta / yawSpeed, 1.0f) : 1.0f;
        float smoothnessPitch = pitchSpeed > 0 ? Math.min(pitchDelta / pitchSpeed, 1.0f) : 1.0f;

        RotationFrame frame = new RotationFrame(
                timestamp,
                yaw,
                pitch,
                prevYaw,
                prevPitch,
                yawDelta,
                pitchDelta,
                yawSpeed,
                pitchSpeed,
                yawAcceleration,
                pitchAcceleration,
                smoothnessYaw,
                smoothnessPitch,
                playerPos,
                playerMotion,
                eyePos,
                fakePos,
                targetVec,
                distance,
                lerpRotation
        );

        recordedFrames.add(frame);
    }

    public void startPlayback() {
        if (recordedFrames.isEmpty()) {
            ChatUtil.addText(TextFormatting.WHITE + "Запишите ротацию через SelfRotation");
            return;
        }

        if (isPlaying) {
            ChatUtil.addText(TextFormatting.RED + "Воспроизведение уже идёт!");
            return;
        }

        currentPlaybackFrame = 0;
        playbackStartTime = System.currentTimeMillis();
        isPlaying = true;

        ChatUtil.addText(TextFormatting.GREEN + "Начато воспроизведение записанной ротации!");
        ChatUtil.addText(TextFormatting.YELLOW + "Кадров для воспроизведения: " + recordedFrames.size());
    }

    public void stopPlayback() {
        isPlaying = false;
        currentPlaybackFrame = 0;

        ChatUtil.addText(TextFormatting.GREEN + "Воспроизведение остановлено!");
    }

    private void playbackFrame() {
        if (recordedFrames.isEmpty() || mc.player == null) {
            stopPlayback();
            return;
        }

        long currentPlaybackTime = System.currentTimeMillis() - playbackStartTime;

        while (currentPlaybackFrame < recordedFrames.size()) {
            RotationFrame frame = recordedFrames.get(currentPlaybackFrame);

            if (frame.timestamp > currentPlaybackTime) {
                break;
            }

            applyRotationFrame(frame);
            currentPlaybackFrame++;
        }

        if (currentPlaybackFrame >= recordedFrames.size()) {
            stopPlayback();
            ChatUtil.addText(TextFormatting.GREEN + "Воспроизведение завершено!");
        }
    }

    private void applyRotationFrame(RotationFrame frame) {
        if (mc.player == null) return;

        if (AuraNew.getInstance() != null) {
            AuraNew.getInstance().setLerpRotation(frame.lerpRotation);
        }

        Rotation rotation = new Rotation(frame.yaw, frame.pitch);

        int yawSpeed = (int) Math.max(1, Math.min(360, frame.yawSpeed * 60));
        int pitchSpeed = (int) Math.max(1, Math.min(180, frame.pitchSpeed * 60));

        RotationComponent.update(rotation, yawSpeed, pitchSpeed, 0, 5);
    }

    public void saveRecording() {
        if (recordedFrames.isEmpty()) {
            ChatUtil.addText(TextFormatting.RED + "Нет данных для сохранения!");
            return;
        }

        try {
            Path filePath = Paths.get(ROTATION_DATA_FILE);
            Files.createDirectories(filePath.getParent());

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(filePath.toFile()))) {
                oos.writeInt(recordedFrames.size());
                for (RotationFrame frame : recordedFrames) {
                    frame.serialize(oos);
                }
            }

            ChatUtil.addText(TextFormatting.GREEN + "Запись успешно сохранена!");
            ChatUtil.addText(TextFormatting.YELLOW + "Файл: " + ROTATION_DATA_FILE);
            ChatUtil.addText(TextFormatting.YELLOW + "Кадров: " + recordedFrames.size());
        } catch (IOException e) {
            ChatUtil.addText(TextFormatting.RED + "Ошибка при сохранении: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadRecording() {
        try {
            Path filePath = Paths.get(ROTATION_DATA_FILE);

            if (!Files.exists(filePath)) {
                ChatUtil.addText(TextFormatting.RED + "Файл записи не найден!");
                return;
            }

            recordedFrames.clear();

            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(filePath.toFile()))) {
                int frameCount = ois.readInt();
                for (int i = 0; i < frameCount; i++) {
                    RotationFrame frame = RotationFrame.deserialize(ois);
                    recordedFrames.add(frame);
                }
            }

            ChatUtil.addText(TextFormatting.GREEN + "Запись успешно загружена!");
            ChatUtil.addText(TextFormatting.YELLOW + "Загружено кадров: " + recordedFrames.size());
        } catch (IOException e) {
            ChatUtil.addText(TextFormatting.RED + "Ошибка при загрузке: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean hasRecording() {
        return !recordedFrames.isEmpty();
    }

    public int getRecordedFrameCount() {
        return recordedFrames.size();
    }

    public void clearRecording() {
        recordedFrames.clear();
        currentPlaybackFrame = 0;
        isPlaying = false;
        isRecording = false;

        removeFakePlayer();

        ChatUtil.addText(TextFormatting.YELLOW + "Запись очищена!");
    }

    private record RotationFrame(long timestamp, float yaw, float pitch, float prevYaw, float prevPitch, float yawDelta,
                                 float pitchDelta, float yawSpeed, float pitchSpeed, float yawAcceleration,
                                 float pitchAcceleration, float smoothnessYaw, float smoothnessPitch,
                                 Vector3d playerPos, Vector3d playerMotion, Vector3d eyePos, Vector3d fakePos,
                                 Vector3d targetVec, double distance, Vector2f lerpRotation) implements Serializable {
        private static final long serialVersionUID = 1L;

        void serialize(ObjectOutputStream oos) throws IOException {
            oos.writeLong(timestamp);
            oos.writeFloat(yaw);
            oos.writeFloat(pitch);
            oos.writeFloat(prevYaw);
            oos.writeFloat(prevPitch);
            oos.writeFloat(yawDelta);
            oos.writeFloat(pitchDelta);
            oos.writeFloat(yawSpeed);
            oos.writeFloat(pitchSpeed);
            oos.writeFloat(yawAcceleration);
            oos.writeFloat(pitchAcceleration);
            oos.writeFloat(smoothnessYaw);
            oos.writeFloat(smoothnessPitch);

            oos.writeDouble(playerPos.x);
            oos.writeDouble(playerPos.y);
            oos.writeDouble(playerPos.z);

            oos.writeDouble(playerMotion.x);
            oos.writeDouble(playerMotion.y);
            oos.writeDouble(playerMotion.z);

            oos.writeDouble(eyePos.x);
            oos.writeDouble(eyePos.y);
            oos.writeDouble(eyePos.z);

            oos.writeDouble(fakePos.x);
            oos.writeDouble(fakePos.y);
            oos.writeDouble(fakePos.z);

            oos.writeDouble(targetVec.x);
            oos.writeDouble(targetVec.y);
            oos.writeDouble(targetVec.z);

            oos.writeDouble(distance);

            oos.writeFloat(lerpRotation.x);
            oos.writeFloat(lerpRotation.y);
        }

        static RotationFrame deserialize(ObjectInputStream ois) throws IOException {
            long timestamp = ois.readLong();
            float yaw = ois.readFloat();
            float pitch = ois.readFloat();
            float prevYaw = ois.readFloat();
            float prevPitch = ois.readFloat();
            float yawDelta = ois.readFloat();
            float pitchDelta = ois.readFloat();
            float yawSpeed = ois.readFloat();
            float pitchSpeed = ois.readFloat();
            float yawAcceleration = ois.readFloat();
            float pitchAcceleration = ois.readFloat();
            float smoothnessYaw = ois.readFloat();
            float smoothnessPitch = ois.readFloat();

            Vector3d playerPos = new Vector3d(ois.readDouble(), ois.readDouble(), ois.readDouble());
            Vector3d playerMotion = new Vector3d(ois.readDouble(), ois.readDouble(), ois.readDouble());
            Vector3d eyePos = new Vector3d(ois.readDouble(), ois.readDouble(), ois.readDouble());
            Vector3d fakePos = new Vector3d(ois.readDouble(), ois.readDouble(), ois.readDouble());
            Vector3d targetVec = new Vector3d(ois.readDouble(), ois.readDouble(), ois.readDouble());

            double distance = ois.readDouble();

            Vector2f lerpRotation = new Vector2f(ois.readFloat(), ois.readFloat());

            return new RotationFrame(timestamp, yaw, pitch, prevYaw, prevPitch,
                    yawDelta, pitchDelta, yawSpeed, pitchSpeed,
                    yawAcceleration, pitchAcceleration, smoothnessYaw, smoothnessPitch,
                    playerPos, playerMotion, eyePos, fakePos, targetVec, distance, lerpRotation);
        }
    }
}