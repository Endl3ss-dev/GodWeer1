package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.GLUtil;
import org.privates.client.utils.render.draw.Project;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import static org.privates.client.screen.hud.IRenderer.font;
import static org.privates.client.screen.hud.IRenderer.fontSize;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Projectiles", category = Category.RENDER, description = "Показывает линией куда прилетит предмет типо Эндер жемчуга или стрелы")
public class Projectiles extends Module implements IRender {
    public static Projectiles getInstance() {
        return Instance.get(Projectiles.class);
    }

    private final BooleanSetting renderName = new BooleanSetting(this, "Показывать владельца", false);
    private final BooleanSetting renderHead = new BooleanSetting(this, "Показывать голову", true);
    private final MultiBooleanSetting projectiles = new MultiBooleanSetting(this, "Снаряды",
            BooleanSetting.of("Эндер Пёрл", true),
            BooleanSetting.of("Стрела", true),
            BooleanSetting.of("Трезубец", true));

    private final BooleanSetting renderHandProjectile = new BooleanSetting(this, "Траектория в руке", true);
    private final BooleanSetting renderBlockHighlight = new BooleanSetting(this, "Подсветка блока", true)
            .setVisible(renderHandProjectile::getValue);
    private final SliderSetting blockOutlineThickness = new SliderSetting(this, "Толщина обводки", 1.5F, 0.5F, 5.0F, 0.1F)
            .setVisible(() -> renderHandProjectile.getValue() && renderBlockHighlight.getValue());
    private final BooleanSetting blockFill = new BooleanSetting(this, "Заливка блока", true)
            .setVisible(() -> renderHandProjectile.getValue() && renderBlockHighlight.getValue());
    private final SliderSetting blockFillAlpha = new SliderSetting(this, "Прозрачность заливки", 0.2F, 0.1F, 1.0F, 0.05F)
            .setVisible(() -> renderHandProjectile.getValue() && renderBlockHighlight.getValue() && blockFill.getValue());

    BlockPos predictedBlockPos;
    Vector3d predictedHitVec;

    @EventHandler
    public void onEvent(Render2DEvent event) {
        Theme theme = Theme.getInstance();

        for (Entity entity : mc.world.getAllEntities()) {
            if (validEntity(entity) && noMove(entity)) {
                Item item = entity instanceof EnderPearlEntity ? Items.ENDER_PEARL : entity instanceof ArrowEntity ? Items.ARROW : Items.TRIDENT;
                Entity shooter = ((ProjectileEntity) entity).getShooter();
                String shooterName = shooter != null ? shooter.getName().getString() : "Неизвестно";
                Vector3d pearlPosition = entity.getPositionVec();
                Vector3d pearlMotion = entity.getMotion();
                Vector3d lastPosition = new Vector3d(0, 0, 0);

                for (int i = 0; i <= 300; i++) {
                    lastPosition = pearlPosition;
                    pearlPosition = pearlPosition.add(pearlMotion);
                    pearlMotion = updatePearlMotion(entity, pearlMotion, pearlPosition);

                    if (shouldEntityHit(pearlPosition, lastPosition) || pearlPosition.y <= 0) {
                        break;
                    }
                }

                Vector2f position = Project.project2D(lastPosition.x, lastPosition.y, lastPosition.z);
                if (position.x == Float.MAX_VALUE && position.y == Float.MAX_VALUE) return;

                float headSize = renderHead.getValue() && shooter instanceof AbstractClientPlayerEntity ? 5 : 0;
                float width = renderName.getValue() ? font.getWidth(shooterName, fontSize) : -10;
                float x = position.x - width / 2 - 5;
                float y = position.y + 5 + headSize;
                float stackSize = 8;
                float size = (stackSize / 2F);

                if (renderHead.getValue() && shooter instanceof AbstractClientPlayerEntity playerShooter) {
                    float headX = position.x - 8;
                    float headY = position.y - 15;
                    drawPlayerHead(event.getMatrix(), playerShooter, headX, headY, 20);
                }

                theme.drawClientRect(event.getMatrix(), x - 5, y - 3, 10, 10, 0.8F, 2);
                if (renderName.getValue()) {
                    theme.drawClientRect(event.getMatrix(), x + 7, y - 3, 5 + width, 10, 0.8F, 2);
                    font.draw(event.getMatrix(), shooterName, x + 10, y - 1.5F, theme.textColor(), fontSize);
                }

                GLUtil.startScale(x + (stackSize / 2F), y + (stackSize / 2F), 0.5F);
                RenderSystem.translated((x - stackSize - size), (y - stackSize), 0);
                mc.getItemRenderer().renderItemAndEffectIntoGUI(item.getDefaultInstance(), 0, 0);
                RenderSystem.translated(-(x - stackSize - size), -(y - stackSize), 0);
                GLUtil.endScale();
            }
        }
    }

    private void drawPlayerHead(MatrixStack matrix, AbstractClientPlayerEntity player, float x, float y, float size) {
        float hurtPC = (float) Math.sin(player.hurtTime * (18F * Math.PI / 180F));
        int headColor = ColorUtil.overCol(ColorUtil.WHITE, ColorUtil.RED, hurtPC);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        RenderSystem.enableTexture();

        mc.getTextureManager().bindTexture(player.getLocationSkin());

        float[] rgba = ColorUtil.getRGBAf(headColor);
        RenderSystem.color4f(rgba[0], rgba[1], rgba[2], rgba[3]);

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());

        float pivotX = x + size / 2.0F;
        float pivotY = y + size / 2.0F;

        RenderSystem.translatef(pivotX, pivotY, 0);
        RenderSystem.rotatef(180, 0, 0, 1);
        RenderSystem.translatef(-pivotX, -pivotY, 0);

        RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, size / 7F, rgba[3], 0);

        RenderSystem.popMatrix();

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
    }

    @EventHandler
    public void onEvent(Render3DPosedEvent event) {
        MatrixStack matrix = event.getMatrix();

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());
        RenderSystem.translated(-mc.getRenderManager().renderPosX(), -mc.getRenderManager().renderPosY(), -mc.getRenderManager().renderPosZ());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.lineWidth(1.5F);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        BUFFER.begin(1, DefaultVertexFormats.POSITION_COLOR);

        for (Entity entity : mc.world.getAllEntities()) {
            if (validEntity(entity) && noMove(entity))
                renderLine(entity);
        }

        if (renderHandProjectile.getValue() && isHoldingProjectile(mc.player)) {
            renderHandProjectileLine(mc.player);
        }

        TESSELLATOR.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.translated(mc.getRenderManager().renderPosX(), mc.getRenderManager().renderPosY(), mc.getRenderManager().renderPosZ());
        RenderSystem.popMatrix();
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (!renderHandProjectile.getValue() || !renderBlockHighlight.getValue()) {
            predictedBlockPos = null;
            predictedHitVec = null;
            return;
        }

        if (!isHoldingProjectile(mc.player)) {
            predictedBlockPos = null;
            predictedHitVec = null;
            return;
        }

        if (predictedBlockPos == null || predictedHitVec == null) return;

        MatrixStack matrixStack = event.getMatrix();
        matrixStack.push();
        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrixStack.getLast().getMatrix());

        double renderX = mc.getRenderManager().renderPosX();
        double renderY = mc.getRenderManager().renderPosY();
        double renderZ = mc.getRenderManager().renderPosZ();
        RenderSystem.translated(-renderX, -renderY, -renderZ);

        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableCull();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.lineWidth(blockOutlineThickness.getValue());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        int baseColor = Theme.getInstance().clientColor();
        int outlineColor = ColorUtil.setAlpha(baseColor, (baseColor >> 24 & 0xFF));
        int fillColor = ColorUtil.replAlpha(baseColor, (int) (blockFillAlpha.getValue() * 255));

        AxisAlignedBB aabb = new AxisAlignedBB(predictedBlockPos);

        double minX = aabb.minX;
        double minY = aabb.minY;
        double minZ = aabb.minZ;
        double maxX = aabb.maxX;
        double maxY = aabb.maxY;
        double maxZ = aabb.maxZ;

        if (blockFill.getValue()) {
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderFilledBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, fillColor);
            tessellator.draw();
        }

        bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        RenderUtil3D.renderWireframeBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, outlineColor);
        tessellator.draw();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
        matrixStack.pop();
    }

    private void renderHandProjectileLine(PlayerEntity player) {
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.isEmpty()) heldItem = player.getHeldItemOffhand();

        Vector3d startPos = player.getEyePosition(mc.getRenderPartialTicks());
        Vector3d lookVec = player.getLook(mc.getRenderPartialTicks());

        Vector3d motion = calculateInitialMotion(heldItem, lookVec, player);
        Vector3d position = startPos.add(lookVec.scale(0.3));

        predictedBlockPos = null;
        predictedHitVec = null;

        for (int i = 0; i <= 300; i++) {
            Vector3d lastPosition = position;
            position = position.add(motion);
            motion = updateMotionForItem(heldItem, motion, position);

            BlockPos checkPos = new BlockPos(position);
            if (shouldHit(position, lastPosition) || position.y <= 0) {
                predictedBlockPos = checkPos;
                predictedHitVec = position;
                break;
            }

            int color = ColorUtil.fade(i * 5);
            BUFFER.pos(lastPosition.x, lastPosition.y, lastPosition.z).color(color).endVertex();
            BUFFER.pos(position.x, position.y, position.z).color(color).endVertex();
        }
    }

    private Vector3d calculateInitialMotion(ItemStack stack, Vector3d lookVec, PlayerEntity player) {
        Item item = stack.getItem();
        float velocity = 1.5f;

        if (item instanceof EnderPearlItem || item instanceof SnowballItem || item instanceof EggItem) {
            velocity = 1.5f;
        } else if (item instanceof BowItem) {
            int useDuration = stack.getUseDuration() - player.getItemInUseCount();
            float charge = Math.min(useDuration / 20.0f, 1.0f);
            velocity = charge * 3.0f;
            if (velocity < 0.1f) velocity = 0.1f;

            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
            velocity += powerLevel * 0.5f;
        } else if (item instanceof CrossbowItem) {
            velocity = 3.15f;
        } else if (item instanceof TridentItem) {
            velocity = 2.5f;
        } else if (item instanceof PotionItem && isSplashPotion(stack)) {
            velocity = 0.5f;
        } else if (item instanceof ExperienceBottleItem) {
            velocity = 0.7f;
        } else {
            velocity = 1.5f;
        }

        return lookVec.scale(velocity);
    }

    private boolean isSplashPotion(ItemStack stack) {
        if (!(stack.getItem() instanceof PotionItem)) return false;
        return stack.getItem() instanceof SplashPotionItem || stack.getItem() instanceof LingeringPotionItem;
    }

    private Vector3d updateMotionForItem(ItemStack stack, Vector3d motion, Vector3d position) {
        Item item = stack.getItem();
        Vector3d newMotion = motion;

        boolean isInWater = mc.world.getBlockState(new BlockPos(position)).getBlock() == Blocks.WATER;

        if (item instanceof TridentItem) {
            newMotion = newMotion.scale(0.99);
            if (!isInWater) {
                newMotion = new Vector3d(newMotion.x, newMotion.y - 0.05, newMotion.z);
            }
        } else if (item instanceof EnderPearlItem || item instanceof SnowballItem || item instanceof EggItem) {
            if (isInWater) {
                newMotion = newMotion.scale(0.8);
            } else {
                newMotion = newMotion.scale(0.99);
            }
            newMotion = new Vector3d(newMotion.x, newMotion.y - 0.03, newMotion.z);
        } else if (item instanceof BowItem || item instanceof CrossbowItem) {
            if (isInWater) {
                newMotion = newMotion.scale(0.6);
            } else {
                newMotion = newMotion.scale(0.99);
            }
            newMotion = new Vector3d(newMotion.x, newMotion.y - 0.05, newMotion.z);
        } else if (item instanceof PotionItem || item instanceof ExperienceBottleItem) {
            if (isInWater) {
                newMotion = newMotion.scale(0.8);
            } else {
                newMotion = newMotion.scale(0.99);
            }
            newMotion = new Vector3d(newMotion.x, newMotion.y - 0.04, newMotion.z);
        } else {
            if (isInWater) {
                newMotion = newMotion.scale(0.8);
            } else {
                newMotion = newMotion.scale(0.99);
            }
            newMotion = new Vector3d(newMotion.x, newMotion.y - 0.03, newMotion.z);
        }

        return newMotion;
    }

    private boolean isHoldingProjectile(PlayerEntity player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();

        return isProjectileItem(mainHand) || isProjectileItem(offHand);
    }

    private boolean isProjectileItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();

        return item instanceof EnderPearlItem ||
                item instanceof SnowballItem ||
                item instanceof EggItem ||
                item instanceof BowItem ||
                item instanceof CrossbowItem ||
                item instanceof TridentItem ||
                (item instanceof PotionItem && isSplashPotion(stack)) ||
                item instanceof ExperienceBottleItem;
    }

    private void renderLine(Entity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec().add(0, 0, 0);
        Vector3d pearlMotion = pearl.getMotion();
        Vector3d lastPosition;
        for (int i = 0; i <= 300; i++) {
            lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = updatePearlMotion(pearl, pearlMotion, lastPosition);

            if (shouldEntityHit(pearlPosition, lastPosition) || pearlPosition.y <= 0) {
                break;
            }

            int color = ColorUtil.fade(i * 5);
            BUFFER.pos(lastPosition.x, lastPosition.y, lastPosition.z).color(color).endVertex();
            BUFFER.pos(pearlPosition.x, pearlPosition.y, pearlPosition.z).color(color).endVertex();
        }
    }

    public Vector3d updatePearlMotion(Entity entity, Vector3d originalPearlMotion, Vector3d pearlPosition) {
        Vector3d pearlMotion = originalPearlMotion;

        if ((entity.isInWater() || mc.world.getBlockState(new BlockPos(pearlPosition)).getBlock() == Blocks.WATER) && !(entity instanceof TridentEntity)) {
            float scale = entity instanceof EnderPearlEntity ? 0.8f : 0.6f;
            pearlMotion = pearlMotion.scale(scale);
        } else {
            pearlMotion = pearlMotion.scale(0.99f);
        }

        if (!entity.hasNoGravity())
            pearlMotion.y -= entity instanceof EnderPearlEntity ? 0.03 : 0.05;

        return pearlMotion;
    }

    public boolean shouldEntityHit(Vector3d pearlPosition, Vector3d lastPosition) {
        final RayTraceContext rayTraceContext = new RayTraceContext(
                lastPosition,
                pearlPosition,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                mc.player
        );
        final BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);

        return blockHitResult.getType() == RayTraceResult.Type.BLOCK;
    }

    public boolean shouldHit(Vector3d position, Vector3d lastPosition) {
        final RayTraceContext rayTraceContext = new RayTraceContext(
                lastPosition,
                position,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                mc.player
        );
        final BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);

        return blockHitResult.getType() == RayTraceResult.Type.BLOCK;
    }

    boolean noMove(Entity entity) {
        return entity.prevPosY != entity.getPosY() || entity.prevPosX != entity.getPosX() || entity.prevPosZ != entity.getPosZ();
    }

    boolean validEntity(Entity entity) {
        return (entity instanceof EnderPearlEntity && projectiles.getValue("Эндер Пёрл"))
                || (entity instanceof ArrowEntity && projectiles.getValue("Стрела"))
                || (entity instanceof TridentEntity && projectiles.getValue("Трезубец"));
    }
}