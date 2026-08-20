package org.privates.client.managers.module.impl.render;

import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.render.ItemAnimationEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.math.Mathf;

@Category2
@ModuleInfo(name = "ItemAnimation", category = Category.RENDER, description = "Кастомные one-time анимации предмета")
public class ItemAnimation extends Module {

    private final BindSetting key = new BindSetting(this, "Кнопка активации", -1);
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Triton", "TritonNew", "Circle", "Sword").set("Triton");

    private boolean startAnimationTriton = false;
    private long currentOneTime = System.currentTimeMillis();
    private float cam = 0.0f;

    private final float[] animationTriton = new float[3];
    private final float[] animationTritonNew = new float[4];
    private final float[] animationCircle = new float[4];
    private final float[] animationSword = new float[4];

    @EventHandler
    public void onKey(KeyboardPressEvent e) {
        if (mc.player == null || mc.world == null || mc.currentScreen != null) return;
        if (e.isKey(key.getValue()) && !startAnimationTriton) {
            startAnimationTriton = true;
            currentOneTime = defaultValue();
        }
    }

    @EventHandler
    public void onMouse(MousePressEvent e) {
        if (mc.player == null || mc.world == null || mc.currentScreen != null) return;
        int bind = key.getValue();
        if (bind >= 500 && e.isKey(bind - 500) && !startAnimationTriton) {
            startAnimationTriton = true;
            currentOneTime = defaultValue();
        }
    }

    @EventHandler
    public void onItemAnimation(ItemAnimationEvent e) {
        if (e.getHand() != Hand.MAIN_HAND) return;
        if (CustomHand.getInstance() != null && CustomHand.getInstance().isEnabled()) return;

        float side = e.getHandSide() == HandSide.RIGHT ? 1.0f : -1.0f;
        MatrixStack matrix = e.getMatrix();

        switch (mode.getValue()) {
            case "TritonNew" -> animationTritonNew(matrix, side);
            case "Circle" -> animationCircle(matrix, side);
            case "Sword" -> animationSword(matrix, side);
            default -> animation(matrix, side);
        }

        e.cancel();
    }

    public void animationTritonNew(MatrixStack matrixStackIn, float side) {
        long elapsed = System.currentTimeMillis() - currentOneTime;

        if (startAnimationTriton) {
            if (elapsed < 900L) {
                animationTritonNew[0] = Mathf.fast(animationTritonNew[0], 10, 8);
                cam = Mathf.fast(cam, 0.4f, 10);
            } else if (elapsed < 1800L) {
                animationTritonNew[0] = Mathf.fast(animationTritonNew[0], 10, 8);
                animationTritonNew[1] = Mathf.fast(animationTritonNew[1], 50, 7);
                animationTritonNew[2] = Mathf.fast(animationTritonNew[2], 50, 7);
                cam = Mathf.fast(cam, 0.33f, 10);
            } else if (elapsed < 2800L) {
                animationTritonNew[3] = Mathf.fast(animationTritonNew[3], 10, 8);
                cam = Mathf.fast(cam, 0.33f, 10);
            } else if (elapsed > 5000L) {
                startAnimationTriton = false;
                currentOneTime = defaultValue();
            }
        } else {
            animationTritonNew[0] = Mathf.fast(animationTritonNew[0], side * -45.0F, 15);
            animationTritonNew[1] = Mathf.fast(animationTritonNew[1], 0, 15);
            animationTritonNew[2] = Mathf.fast(animationTritonNew[2], 0, 15);
            cam = Mathf.fast(cam, 0.0f, 15);
        }

        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(animationTritonNew[0]));
        matrixStackIn.rotate(Vector3f.XN.rotationDegrees(animationTritonNew[1]));
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(animationTritonNew[2]));
        matrixStackIn.translate(0.0D, 0.0D, -cam * 0.12F);
    }

    public void animation(MatrixStack matrixStackIn, float side) {
        long elapsed = System.currentTimeMillis() - currentOneTime;

        if (startAnimationTriton) {
            if (elapsed < 900L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], 10, 8);
                cam = Mathf.fast(cam, 0.4f, 10);
            } else if (elapsed < 1600L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], 10, 8);
                animationTriton[1] = Mathf.fast(animationTriton[1], 50, 7);
                cam = Mathf.fast(cam, 0.35f, 10);
            } else if (elapsed < 2400L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], 10, 8);
                animationTriton[1] = Mathf.fast(animationTriton[1], 50, 5);
                animationTriton[2] = Mathf.fast(animationTriton[2], -45, 6);
                cam = Mathf.fast(cam, 0.3f, 10);
            } else if (elapsed < 3200L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], 10, 8);
                animationTriton[1] = Mathf.fast(animationTriton[1], 50, 5);
                animationTriton[2] = Mathf.fast(animationTriton[2], 45, 6);
            } else if (elapsed < 4600L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], 40, 8);
                animationTriton[1] = Mathf.fast(animationTriton[1], -130, 4);
                animationTriton[2] = Mathf.fast(animationTriton[2], 130, 4);
                cam = Mathf.fast(cam, 0.5f, 10);
            } else if (elapsed < 5600L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], 90, 2);
                animationTriton[1] = Mathf.fast(animationTriton[1], 90, 4);
                animationTriton[2] = Mathf.fast(animationTriton[2], 115, 4);
            } else if (elapsed < 6200L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], 90, 4);
                animationTriton[1] = Mathf.fast(animationTriton[1], 90, 4);
                animationTriton[2] = Mathf.fast(animationTriton[2], 100, 4);
                cam = Mathf.fast(cam, 0.2f, 10);
            } else if (elapsed < 7300L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], 20, 4);
                animationTriton[1] = Mathf.fast(animationTriton[1], 40, 4);
                animationTriton[2] = Mathf.fast(animationTriton[2], 185, 4);
            } else if (elapsed < 8200L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], 20, 4);
                animationTriton[1] = Mathf.fast(animationTriton[1], 10, 4);
                animationTriton[2] = Mathf.fast(animationTriton[2], 0, 4);
            } else if (elapsed < 9000L) {
                animationTriton[0] = Mathf.fast(animationTriton[0], side * -45.0F, 15);
                animationTriton[1] = Mathf.fast(animationTriton[1], 0, 15);
                animationTriton[2] = Mathf.fast(animationTriton[2], 0, 15);
                cam = Mathf.fast(cam, 0.0f, 10);
            } else {
                startAnimationTriton = false;
                currentOneTime = defaultValue();
            }
        } else {
            animationTriton[0] = Mathf.fast(animationTriton[0], side * -45.0F, 15);
            animationTriton[1] = Mathf.fast(animationTriton[1], 0, 15);
            animationTriton[2] = Mathf.fast(animationTriton[2], 0, 15);
            cam = Mathf.fast(cam, 0.0f, 15);
        }

        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(animationTriton[0]));
        matrixStackIn.rotate(Vector3f.XN.rotationDegrees(animationTriton[1]));
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(animationTriton[2]));
        matrixStackIn.translate(0.0D, 0.0D, -cam * 0.12F);
    }

    public void animationCircle(MatrixStack matrixStackIn, float side) {
        long elapsed = System.currentTimeMillis() - currentOneTime;

        if (startAnimationTriton) {
            if (elapsed < 900L) {
                animationCircle[0] = Mathf.fast(animationCircle[0], 10, 8);
                animationCircle[1] = Mathf.fast(animationCircle[1], 0, 8);
                cam = Mathf.fast(cam, 0.3f, 10);
            } else if (elapsed < 1200L) {
                animationCircle[1] = Mathf.fast(animationCircle[1], -50, 8);
                cam = Mathf.fast(cam, 0.4f, 10);
            } else if (elapsed < 2500L) {
                animationCircle[1] = Mathf.fast(animationCircle[1], 360 * 2.0f, 4);
            } else if (elapsed < 3000L) {
                animationCircle[2] = Mathf.fast(animationCircle[2], -60, 7);
                animationCircle[1] = 0;
                cam = Mathf.fast(cam, 0.3f, 10);
            } else if (elapsed < 3800L) {
                animationCircle[2] = Mathf.fast(animationCircle[2], 60, 5);
            } else if (elapsed < 4700L) {
                animationCircle[3] = Mathf.fast(animationCircle[3], 60, 5);
            } else if (elapsed < 5700L) {
                animationCircle[0] = Mathf.fast(animationCircle[0], 150, 6);
                cam = Mathf.fast(cam, 0.35f, 10);
            } else if (elapsed < 6500L) {
                animationCircle[1] = Mathf.fast(animationCircle[1], -20, 8);
                animationCircle[3] = Mathf.fast(animationCircle[3], -60, 5);
            } else if (elapsed < 7200L) {
                animationCircle[2] = Mathf.fast(animationCircle[2], -50, 5);
                animationCircle[3] = Mathf.fast(animationCircle[3], -80, 5);
                cam = Mathf.fast(cam, 0.2f, 10);
            } else if (elapsed < 8000L) {
                animationCircle[0] = Mathf.fast(animationCircle[0], side * -45, 5);
                animationCircle[1] = Mathf.fast(animationCircle[1], 0, 4);
                animationCircle[2] = Mathf.fast(animationCircle[2], 0, 10);
                animationCircle[3] = Mathf.fast(animationCircle[3], 0, 5);
                cam = Mathf.fast(cam, 0.0f, 10);
            } else {
                startAnimationTriton = false;
                currentOneTime = defaultValue();
            }
        } else {
            animationCircle[0] = Mathf.fast(animationCircle[0], side * -45, 5);
            animationCircle[1] = Mathf.fast(animationCircle[1], 0, 4);
            animationCircle[2] = Mathf.fast(animationCircle[2], 0, 10);
            animationCircle[3] = Mathf.fast(animationCircle[3], 0, 5);
            cam = Mathf.fast(cam, 0.0f, 15);
        }

        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(animationCircle[0]));
        matrixStackIn.rotate(Vector3f.XN.rotationDegrees(animationCircle[1]));
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(animationCircle[2]));
        matrixStackIn.rotate(Vector3f.ZN.rotationDegrees(animationCircle[3]));
        matrixStackIn.translate(0.0D, 0.0D, -cam * 0.12F);
    }

    public void animationSword(MatrixStack matrixStackIn, float side) {
        long elapsed = System.currentTimeMillis() - currentOneTime;

        if (startAnimationTriton) {
            if (elapsed < 900L) {
                animationSword[0] = Mathf.fast(animationSword[0], 10, 8);
                animationSword[1] = Mathf.fast(animationSword[1], 0, 8);
                cam = Mathf.fast(cam, 0.3f, 10);
            } else if (elapsed < 1200L) {
                animationSword[1] = Mathf.fast(animationSword[1], -50, 8);
                cam = Mathf.fast(cam, 0.2f, 10);
            } else if (elapsed < 2500L) {
                animationSword[1] = Mathf.fast(animationSword[1], 360 * 2.0f, 3);
                cam = Mathf.fast(cam, 0.4f, 10);
            } else if (elapsed < 3000L) {
                cam = Mathf.fast(cam, 0.5f, 10);
                animationSword[1] = Mathf.fast(animationSword[1], 360 * 2.0f + 50, 4);
            } else if (elapsed < 3900L) {
                animationSword[1] = Mathf.fast(animationSword[1], 0, 5);
                cam = Mathf.fast(cam, 0.4f, 10);
            } else if (elapsed < 4500L) {
                animationSword[2] = Mathf.fast(animationSword[2], -60, 7);
                animationSword[1] = 0;
                cam = Mathf.fast(cam, 0.3f, 10);
            } else if (elapsed < 5200L) {
                animationSword[2] = Mathf.fast(animationSword[2], 60, 5);
            } else if (elapsed < 5800L) {
                animationSword[3] = Mathf.fast(animationSword[3], 60, 5);
                cam = Mathf.fast(cam, 0.34f, 10);
            } else if (elapsed < 6300L) {
                animationSword[0] = Mathf.fast(animationSword[0], 150, 6);
            } else if (elapsed < 6800L) {
                animationSword[1] = Mathf.fast(animationSword[1], -20, 8);
                animationSword[3] = Mathf.fast(animationSword[3], -60, 5);
                cam = Mathf.fast(cam, 0.2f, 10);
            } else if (elapsed < 7500L) {
                animationSword[2] = Mathf.fast(animationSword[2], -50, 5);
                animationSword[3] = Mathf.fast(animationSword[3], -80, 5);
                cam = Mathf.fast(cam, 0.1f, 10);
            } else if (elapsed < 8000L) {
                animationSword[0] = Mathf.fast(animationSword[0], side * -45, 5);
                animationSword[1] = Mathf.fast(animationSword[1], 0, 4);
                animationSword[2] = Mathf.fast(animationSword[2], 0, 10);
                animationSword[3] = Mathf.fast(animationSword[3], 0, 5);
                cam = Mathf.fast(cam, 0.0f, 10);
            } else {
                startAnimationTriton = false;
                currentOneTime = defaultValue();
            }
        } else {
            animationSword[0] = Mathf.fast(animationSword[0], side * -45, 5);
            animationSword[1] = Mathf.fast(animationSword[1], 0, 4);
            animationSword[2] = Mathf.fast(animationSword[2], 0, 10);
            animationSword[3] = Mathf.fast(animationSword[3], 0, 5);
            cam = Mathf.fast(cam, 0.0f, 15);
        }

        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(animationSword[0]));
        matrixStackIn.rotate(Vector3f.XN.rotationDegrees(animationSword[1]));
        matrixStackIn.rotate(Vector3f.YN.rotationDegrees(animationSword[2]));
        matrixStackIn.translate(0.0D, 0.0D, -cam * 0.12F);
    }

    public void reset(float side) {
        startAnimationTriton = false;

        animationTriton[0] = Mathf.fast(animationTriton[0], side * -45.0F, 15);
        animationTriton[1] = Mathf.fast(animationTriton[1], 0, 15);
        animationTriton[2] = Mathf.fast(animationTriton[2], 0, 15);

        animationSword[0] = Mathf.fast(animationSword[0], side * -45, 5);
        animationSword[1] = Mathf.fast(animationSword[1], 0, 4);
        animationSword[2] = Mathf.fast(animationSword[2], 0, 10);
        animationSword[3] = Mathf.fast(animationSword[3], 0, 5);

        animationCircle[0] = Mathf.fast(animationCircle[0], side * -45, 5);
        animationCircle[1] = Mathf.fast(animationCircle[1], 0, 4);
        animationCircle[2] = Mathf.fast(animationCircle[2], 0, 10);
        animationCircle[3] = Mathf.fast(animationCircle[3], 0, 5);

        cam = Mathf.fast(cam, 0.0f, 15);
    }

    @Override
    protected void onDisable() {
        reset(1.0f);
        currentOneTime = defaultValue();
        super.onDisable();
    }

    private long defaultValue() {
        return System.currentTimeMillis();
    }
}

