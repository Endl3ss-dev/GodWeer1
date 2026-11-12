package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Doing things: moving, hitting, placing, swapping slots.
 *
 * <p>Everything here hops onto the client thread before touching the game, so a script may call
 * these from a packet handler without the usual consequences.</p>
 */
public final class ScriptActions {

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    private static void onClient(Runnable r) {
        MinecraftClient mc = mc();
        if (mc.isOnThread()) r.run();
        else mc.execute(r);
    }

    /** Movement and key state, exposed as {@code control}. */
    public static final class Control {

        /** Makes the player jump if they are on the ground. */
        public void jump() {
            onClient(() -> {
                MinecraftClient mc = mc();
                if (mc.player != null && mc.player.isOnGround()) mc.player.jump();
            });
        }

        public void sneak(boolean pressed) {
            press(mc().options.sneakKey, pressed);
        }

        public void sprint(boolean pressed) {
            press(mc().options.sprintKey, pressed);
        }

        public void forward(boolean pressed) {
            press(mc().options.forwardKey, pressed);
        }

        public void backward(boolean pressed) {
            press(mc().options.backKey, pressed);
        }

        public void left(boolean pressed) {
            press(mc().options.leftKey, pressed);
        }

        public void right(boolean pressed) {
            press(mc().options.rightKey, pressed);
        }

        public void useKey(boolean pressed) {
            press(mc().options.useKey, pressed);
        }

        public void attackKey(boolean pressed) {
            press(mc().options.attackKey, pressed);
        }

        private static void press(KeyBinding key, boolean pressed) {
            if (key == null) return;
            onClient(() -> key.setPressed(pressed));
        }

        /** Sets your velocity outright. */
        public void velocity(double x, double y, double z) {
            onClient(() -> {
                if (mc().player != null) mc().player.setVelocity(x, y, z);
            });
        }

        /** Points the head somewhere. */
        public void look(float yaw, float pitch) {
            onClient(() -> {
                if (mc().player == null) return;
                mc().player.setYaw(yaw);
                mc().player.setPitch(Math.max(-90f, Math.min(90f, pitch)));
            });
        }

        public void lookAt(Vec3d target) {
            ScriptRotations.Rotation r = new ScriptRotations().lookAt(target);
            look(r.yaw(), r.pitch());
        }
    }

    /** Hitting, using and placing, exposed as {@code interaction}. */
    public static final class Interaction {

        /** Attacks an entity, cooldown and all. */
        public void attack(WorldApi.ScriptEntity target) {
            if (target == null) return;
            onClient(() -> {
                MinecraftClient mc = mc();
                if (mc.player == null || mc.interactionManager == null) return;
                mc.interactionManager.attackEntity(mc.player, target.raw());
                mc.player.swingHand(Hand.MAIN_HAND);
            });
        }

        /** Attacks whatever is under the crosshair. */
        public void attackCrosshair() {
            onClient(() -> {
                MinecraftClient mc = mc();
                if (mc.player == null) return;
                WorldApi.ScriptEntity target = new RaycastApi()
                        .entityAtCrosshair(mc.player.getEntityInteractionRange());
                if (target != null) attack(target);
            });
        }

        public void swing(String hand) {
            onClient(() -> {
                if (mc().player != null) mc().player.swingHand(handOf(hand));
            });
        }

        public void useItem(String hand) {
            onClient(() -> {
                MinecraftClient mc = mc();
                if (mc.player == null || mc.interactionManager == null) return;
                mc.interactionManager.interactItem(mc.player, handOf(hand));
            });
        }

        public void stopUsingItem() {
            onClient(() -> {
                MinecraftClient mc = mc();
                if (mc.player != null && mc.interactionManager != null) {
                    mc.interactionManager.stopUsingItem(mc.player);
                }
            });
        }

        /** Places against the given face of the given block. */
        public void placeBlock(int x, int y, int z, String side, String hand) {
            onClient(() -> {
                MinecraftClient mc = mc();
                if (mc.player == null || mc.interactionManager == null) return;
                Direction dir = directionOf(side);
                BlockPos pos = new BlockPos(x, y, z);
                Vec3d centre = new Vec3d(x + 0.5 + dir.getOffsetX() * 0.5,
                        y + 0.5 + dir.getOffsetY() * 0.5,
                        z + 0.5 + dir.getOffsetZ() * 0.5);
                mc.interactionManager.interactBlock(mc.player, handOf(hand),
                        new BlockHitResult(centre, dir, pos, false));
            });
        }

        /** Starts or continues breaking a block. */
        public void breakBlock(int x, int y, int z, String side) {
            onClient(() -> {
                MinecraftClient mc = mc();
                if (mc.interactionManager == null) return;
                mc.interactionManager.updateBlockBreakingProgress(new BlockPos(x, y, z), directionOf(side));
            });
        }

        public void interactEntity(WorldApi.ScriptEntity target, String hand) {
            if (target == null) return;
            onClient(() -> {
                MinecraftClient mc = mc();
                if (mc.player == null || mc.interactionManager == null) return;
                mc.interactionManager.interactEntity(mc.player, target.raw(), handOf(hand));
            });
        }
    }

    /** Inventory access, exposed as {@code inventory}. */
    public static final class Inventory {

        public static final int HOTBAR_SIZE = 9;

        /** The held stack's identifier, e.g. "minecraft:stone". */
        public String held() {
            MinecraftClient mc = mc();
            if (mc.player == null) return "minecraft:air";
            var stack = mc.player.getMainHandStack();
            return stack.isEmpty() ? "minecraft:air"
                    : net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).toString();
        }

        public int selectedSlot() {
            MinecraftClient mc = mc();
            return mc.player == null ? 0 : mc.player.getInventory().getSelectedSlot();
        }

        public void selectSlot(int slot) {
            if (slot < 0 || slot >= HOTBAR_SIZE) return;
            onClient(() -> {
                if (mc().player != null) mc().player.getInventory().setSelectedSlot(slot);
            });
        }

        /** Item id in a given inventory slot; 0-8 is the hotbar. */
        public String item(int slot) {
            MinecraftClient mc = mc();
            if (mc.player == null) return "minecraft:air";
            var stack = mc.player.getInventory().getStack(slot);
            return stack.isEmpty() ? "minecraft:air"
                    : net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).toString();
        }

        public int count(int slot) {
            MinecraftClient mc = mc();
            if (mc.player == null) return 0;
            return mc.player.getInventory().getStack(slot).getCount();
        }

        /** First slot holding the given item, or -1. */
        public int find(String itemId) {
            MinecraftClient mc = mc();
            if (mc.player == null) return -1;
            String want = itemId.contains(":") ? itemId : "minecraft:" + itemId;
            var inv = mc.player.getInventory();
            for (int i = 0; i < inv.size(); i++) {
                var stack = inv.getStack(i);
                if (stack.isEmpty()) continue;
                if (net.minecraft.registry.Registries.ITEM.getId(stack.getItem()).toString().equals(want)) return i;
            }
            return -1;
        }

        public int findInHotbar(String itemId) {
            int slot = find(itemId);
            return slot >= 0 && slot < HOTBAR_SIZE ? slot : -1;
        }

        /** True while the client is already moving items about. */
        public boolean busy() {
            MinecraftClient mc = mc();
            return mc.currentScreen instanceof net.minecraft.client.gui.screen.ingame.HandledScreen<?>;
        }
    }

    // ---- shared helpers --------------------------------------------------------------------

    static Hand handOf(String hand) {
        return hand != null && hand.toUpperCase().startsWith("OFF") ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    static Direction directionOf(String side) {
        if (side == null) return Direction.UP;
        try {
            return Direction.valueOf(side.toUpperCase());
        } catch (Throwable t) {
            return Direction.UP;
        }
    }

    private ScriptActions() {}
}
