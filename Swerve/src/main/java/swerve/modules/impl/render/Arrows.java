package swerve.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;

import swerve.Swerve;
import swerve.managers.FriendManager;
import swerve.managers.ModuleManager;
import swerve.managers.ThemeManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.utils.color.ColorUtils;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Arrows extends Module {

    private static final ResourceLocation[] TEXTURES = {
            Swerve.id("textures/modules/arrows/arrow.png"),
            Swerve.id("textures/modules/arrows/arrow2.png"),
            Swerve.id("textures/modules/arrows/arrow3.png"),
            Swerve.id("textures/modules/arrows/arrow4.png"),
            Swerve.id("textures/modules/arrows/arrow5.png"),
            Swerve.id("textures/modules/arrows/arrow6.png")
    };

    private static final int FRIENDLY_COLOR = ColorUtils.rgb(70, 220, 110);
    private static final int HOSTILE_COLOR = ColorUtils.rgb(235, 60, 60);
    private static final int DEFAULT_FRIEND_COLOR = ColorUtils.rgb(80, 170, 255);

    private static final float ROTATION_SMOOTHING = 0.18F;
    private static final float FADE_IN = 0.12F;
    private static final float FADE_OUT = 0.10F;

    private static boolean eventsRegistered;

    private final ModeSetting texture = new ModeSetting("Текстура", "1", "1", "2", "3", "4", "5", "6");
    private final ModeSetting colorMode = new ModeSetting("Цвет", "Клиентский", "Клиентский", "Свой/Чужой", "Кастомное");
    private final FloatSetting playerRed = new FloatSetting("Игроки: красный", 230.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting playerGreen = new FloatSetting("Игроки: зелёный", 50.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting playerBlue = new FloatSetting("Игроки: синий", 57.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting friendRed = new FloatSetting("Друзья: красный", 80.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting friendGreen = new FloatSetting("Друзья: зелёный", 170.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final FloatSetting friendBlue = new FloatSetting("Друзья: синий", 255.0F, 0.0F, 255.0F, 1.0F)
            .visible(() -> colorMode.is("Кастомное"));
    private final BooleanSetting onlyNaked = new BooleanSetting("Голых", false);
    private final BooleanSetting showFriends = new BooleanSetting("Друзьях", true);
    private final FloatSetting size = new FloatSetting("Размер", 13.0F, 8.0F, 28.0F, 0.5F);
    private final FloatSetting distance = new FloatSetting("Дистанция", 58.0F, 30.0F, 120.0F, 1.0F);

    /**
     * Состояния стрелок списком, а не {@code HashMap}.
     *
     * <p>Итерация {@code HashMap} в каждом кадре создаёт объект-итератор, а
     * {@code entrySet().removeIf(...)} — ещё и захватывающую лямбду. Игроков рядом
     * единицы, поэтому линейный поиск по массиву дешевле двух аллокаций за кадр.
     * Пометка «видел в этом кадре» заменяет прежний {@code HashSet} и его {@code clear()}.
     */
    private final List<ArrowState> states = new ArrayList<>();
    private int frame;

    /** Scratch-буферы интерполяции (x, y, z): кадр рисуется в одном потоке. */
    private static final double[] SELF = new double[3];
    private static final double[] OTHER = new double[3];

    public Arrows() {
        super("Arrows", "Стрелочки на игроков вокруг прицела", ModuleCategory.RENDER);
        addSettings(texture, colorMode, playerRed, playerGreen, playerBlue,
                friendRed, friendGreen, friendBlue, onlyNaked, showFriends, size, distance);
        registerEvents();
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            Arrows module = ModuleManager.get(Arrows.class);
            if (module != null && module.isEnable()) {
                module.onHudRender(guiGraphics.pose(), deltaTracker.getGameTimeDeltaPartialTick(true));
            }
        });
    }

    @Override
    public void onDisable() {
        states.clear();
        super.onDisable();
    }

    private void onHudRender(PoseStack poseStack, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || minecraft.options.hideGui) {
            states.clear();
            return;
        }
        if (!minecraft.options.getCameraType().isFirstPerson()) {
            fadeAll();
            return;
        }

        float centerX = minecraft.getWindow().getGuiScaledWidth() * 0.5F;
        float centerY = minecraft.getWindow().getGuiScaledHeight() * 0.5F;
        float arrowSize = size.get();
        float y = centerY - distance.get();
        float selfYaw = Mth.lerp(partialTick, minecraft.player.yRotO, minecraft.player.getYRot());
        double[] self = SELF;
        interpolate(minecraft.player, partialTick, self);
        ResourceLocation arrowTexture = TEXTURES[texture.getIndex()];

        frame++;
        if (frame == Integer.MAX_VALUE) {
            // Переполнение счётчика: иначе все стрелки разом посчитались бы пропавшими.
            frame = 1;
            for (int i = 0; i < states.size(); i++) {
                states.get(i).seenFrame = 0;
            }
        }

        // Индексный обход: for-each по списку игроков тоже создаёт итератор.
        List<AbstractClientPlayer> players = minecraft.level.players();
        for (int index = 0; index < players.size(); index++) {
            AbstractClientPlayer player = players.get(index);
            if (player == minecraft.player || !player.isAlive() || player.isSpectator()) {
                continue;
            }

            boolean friend = FriendManager.isFriend(player);
            if (friend && !showFriends.get()) {
                continue;
            }
            if (!friend && onlyNaked.get() && !isNaked(player)) {
                continue;
            }

            ArrowState state = stateFor(player.getUUID());
            state.seenFrame = frame;

            double[] other = OTHER;
            interpolate(player, partialTick, other);
            float targetYaw = relativeYaw(other, self, selfYaw);
            state.rotation = interpolateAngle(state.rotation, targetYaw, ROTATION_SMOOTHING);
            state.alpha = Mth.lerp(FADE_IN, state.alpha, 1.0F);
            float alpha = Mth.clamp(state.alpha, 0.0F, 1.0F);
            if (alpha <= 0.01F) {
                continue;
            }

            int color = resolveColor(friend);
            int drawColor = ColorUtils.applyAlpha(color, alpha);
            int shadowColor = ColorUtils.applyAlpha(ColorUtils.darken(color, 0.55F), alpha * 0.65F);
            renderArrow(poseStack, arrowTexture, centerX, centerY, y, arrowSize, state.rotation, drawColor, shadowColor);
        }

        fadeMissing();
    }

    /** Состояние игрока по UUID; новый объект создаётся только для нового игрока. */
    private ArrowState stateFor(UUID id) {
        for (int i = 0; i < states.size(); i++) {
            ArrowState state = states.get(i);
            if (state.id.equals(id)) {
                return state;
            }
        }
        ArrowState created = new ArrowState(id);
        states.add(created);
        return created;
    }

    /** Гашение стрелок игроков, которых не было в этом кадре (бывший removeIf). */
    private void fadeMissing() {
        for (int i = states.size() - 1; i >= 0; i--) {
            ArrowState state = states.get(i);
            if (state.seenFrame == frame) {
                continue;
            }
            state.alpha = Mth.lerp(FADE_OUT, state.alpha, 0.0F);
            if (state.alpha <= 0.02F) {
                states.remove(i);
            }
        }
    }

    private void renderArrow(PoseStack poseStack, ResourceLocation arrowTexture, float centerX, float centerY,
                             float y, float arrowSize, float rotation, int color, int shadowColor) {
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0.0F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        poseStack.translate(-centerX, -centerY, 0.0F);

        float x = centerX - arrowSize * 0.5F;
        drawTexture(poseStack, arrowTexture, x + 0.75F, y + 0.75F, arrowSize, arrowSize, shadowColor);
        drawTexture(poseStack, arrowTexture, x, y, arrowSize, arrowSize, color);

        poseStack.popPose();
    }

    private void drawTexture(PoseStack poseStack, ResourceLocation location, float x, float y,
                             float width, float height, int color) {
        int alpha = ColorUtils.alpha(color);
        if (alpha <= 0) {
            return;
        }
        int red = ColorUtils.red(color);
        int green = ColorUtils.green(color);
        int blue = ColorUtils.blue(color);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture(0, location);

        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.addVertex(matrix, x, y, 0.0F).setUv(0.0F, 0.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x, y + height, 0.0F).setUv(0.0F, 1.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x + width, y + height, 0.0F).setUv(1.0F, 1.0F).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x + width, y, 0.0F).setUv(1.0F, 0.0F).setColor(red, green, blue, alpha);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
        RenderSystem.disableBlend();
    }

    private int resolveColor(boolean friend) {
        if (colorMode.is("Свой/Чужой")) {
            return friend ? FRIENDLY_COLOR : HOSTILE_COLOR;
        }
        if (colorMode.is("Кастомное")) {
            return friend
                    ? ColorUtils.rgb((int) friendRed.get(), (int) friendGreen.get(), (int) friendBlue.get())
                    : ColorUtils.rgb((int) playerRed.get(), (int) playerGreen.get(), (int) playerBlue.get());
        }
        return friend ? DEFAULT_FRIEND_COLOR : ThemeManager.getColor();
    }

    private boolean isNaked(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
                && player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()
                && player.getItemBySlot(EquipmentSlot.FEET).isEmpty();
    }

    private void fadeAll() {
        for (int i = states.size() - 1; i >= 0; i--) {
            ArrowState state = states.get(i);
            state.alpha = Mth.lerp(FADE_OUT, state.alpha, 0.0F);
            if (state.alpha <= 0.02F) {
                states.remove(i);
            }
        }
    }

    /**
     * Интерполяция позиции в scratch-буфер {@code out} (x, y, z).
     *
     * <p>Раньше метод возвращал {@code Vec3}, то есть создавал объект на каждого игрока
     * в каждом кадре. Значения те же: {@code Mth.lerp(partialTick, prev, current)}.
     */
    private void interpolate(Player player, float partialTick, double[] out) {
        out[0] = Mth.lerp(partialTick, player.xo, player.getX());
        out[1] = Mth.lerp(partialTick, player.yo, player.getY());
        out[2] = Mth.lerp(partialTick, player.zo, player.getZ());
    }

    private float relativeYaw(double[] target, double[] self, float selfYaw) {
        double dx = target[0] - self[0];
        double dz = target[2] - self[2];
        float yaw = (float) -Math.toDegrees(Math.atan2(dx, dz));
        return Mth.wrapDegrees(yaw - selfYaw);
    }

    private float interpolateAngle(float current, float target, float factor) {
        return current + Mth.wrapDegrees(target - current) * factor;
    }

    private static final class ArrowState {
        private final UUID id;
        private float alpha;
        private float rotation;
        /** Номер кадра, в котором игрока видели; замена прежнему множеству seen. */
        private int seenFrame;

        private ArrowState(UUID id) {
            this.id = id;
        }
    }
}
