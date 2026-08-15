package antileak.base.client.modules.impl.render;

import antileak.base.Punch;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.events.implement.Event3DRender;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.client.modules.Module;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class ShulkerViewer extends Module {

    public static ShulkerViewer INSTANCE = new ShulkerViewer();

    // Projection data
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Quaternionf inverseCameraRotation = new Quaternionf();
    private Vec3d cameraPos = Vec3d.ZERO;
    private float tickDelta;
    private int scaledWidth;
    private int scaledHeight;
    private final Vector3f projectionScratch = new Vector3f();
    private final Vector4f clipScratch = new Vector4f();

    public ShulkerViewer() {
        super("ShulkerViewer", "Показывает содержимое шалкера в руке игрока", ModuleCategory.RENDER);
    }

    @EventLink
    public void onRender3D(Event3DRender event) {
        // Capture projection data for 2D rendering
        projectionMatrix.set(event.getProjectionMatrix());
        cameraPos = event.getCamera().getPos();
        inverseCameraRotation.set(event.getCamera().getRotation()).conjugate();
        tickDelta = event.getTickDelta();
        scaledWidth = mc.getWindow().getScaledWidth();
        scaledHeight = mc.getWindow().getScaledHeight();
    }

    @EventLink
    public void onRender(EventRender.Default event) {
        if (mc.player == null || mc.world == null) return;

        DrawContext context = event.getContext();

        try {
            for (Entity ent : mc.world.getEntities()) {
                if (ent instanceof PlayerEntity player) {
                    if (player == mc.player && mc.options.getPerspective().isFirstPerson()) continue;
                    renderShulkerFromPlayer(context, player);
                }

                if (ent instanceof ItemEntity itemEntity) {
                    renderShulkerFromDroppedItem(context, itemEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renderShulkerFromPlayer(DrawContext context, PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        if (stack.isEmpty() || !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock)) return;

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container == null || container.stream().allMatch(ItemStack::isEmpty)) return;

        double x = interpolate(player.prevX, player.getX(), tickDelta);
        double y = interpolate(player.prevY, player.getY(), tickDelta) + player.getHeight() + 1.2;
        double z = interpolate(player.prevZ, player.getZ(), tickDelta);

        ProjectedPoint point = projectToScreen(x, y, z);
        if (point == null) return;

        renderShulkerContents(context, container, point.x, point.y);
    }

    private void renderShulkerFromDroppedItem(DrawContext context, ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getStack();
        if (!(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock)) return;

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container == null || container.stream().allMatch(ItemStack::isEmpty)) return;

        double x = interpolate(itemEntity.prevX, itemEntity.getX(), tickDelta);
        double y = interpolate(itemEntity.prevY, itemEntity.getY(), tickDelta) + 0.75;
        double z = interpolate(itemEntity.prevZ, itemEntity.getZ(), tickDelta);

        ProjectedPoint point = projectToScreen(x, y, z);
        if (point == null) return;

        renderShulkerContents(context, container, point.x, point.y);
    }

    private ProjectedPoint projectToScreen(double worldX, double worldY, double worldZ) {
        projectionScratch.set(
                (float) (worldX - cameraPos.x),
                (float) (worldY - cameraPos.y),
                (float) (worldZ - cameraPos.z)
        );
        projectionScratch.rotate(inverseCameraRotation);

        clipScratch.set(projectionScratch.x, projectionScratch.y, projectionScratch.z, 1.0f);
        projectionMatrix.transform(clipScratch);

        float w = clipScratch.w;
        if (w <= 0.00001f) return null;

        float ndcX = clipScratch.x / w;
        float ndcY = clipScratch.y / w;
        float ndcZ = clipScratch.z / w;

        if (ndcZ <= 0 || ndcZ >= 1) return null;

        float screenX = (ndcX * 0.5f + 0.5f) * scaledWidth;
        float screenY = (1.0f - (ndcY * 0.5f + 0.5f)) * scaledHeight;

        if (Float.isNaN(screenX) || Float.isNaN(screenY)) return null;
        if (Float.isInfinite(screenX) || Float.isInfinite(screenY)) return null;

        return new ProjectedPoint(screenX, screenY);
    }

    private void renderShulkerContents(DrawContext context, ContainerComponent container, float screenX, float screenY) {
        float slotSize = 18f;
        float padding = 5f;

        float width = (slotSize * 9) + (padding * 2);
        float height = (slotSize * 3) + (padding * 2);
        float rectX = screenX - width / 2f;
        float rectY = screenY;

        var matrices = context.getMatrices();
        
        // Рисуем фон в стиле HUD панели
        int themeColor = Punch.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        RenderUtils.drawDefaultHudThemedPanel(matrices, rectX, rectY, width, height, 4, 5, themeColor);

        // Слоты
        List<ItemStack> items = new ArrayList<>();
        container.stream().forEach(items::add);

        for (int i = 0; i < 27; i++) {
            int row = i / 9;
            int col = i % 9;

            float slotX = rectX + padding + col * slotSize;
            float slotY = rectY + padding + row * slotSize;

            // Рисуем предмет
            if (i < items.size()) {
                ItemStack item = items.get(i);
                if (!item.isEmpty()) {
                    RenderSystem.enableDepthTest();
                    DiffuseLighting.disableGuiDepthLighting();
                    context.drawItem(item, (int) slotX, (int) slotY);
                    context.drawStackOverlay(mc.textRenderer, item, (int) slotX, (int) slotY);
                }
            }
        }
    }

    private double interpolate(double prev, double current, float delta) {
        return prev + (current - prev) * delta;
    }

    private record ProjectedPoint(float x, float y) {}
}
