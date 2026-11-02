package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Read-only view of the local player, mirroring the "Свой игрок" section of the docs.
 *
 * <p>Every value is read at call time, so a script sees the state as of right now rather than a
 * stale snapshot. Off-world calls return neutral defaults instead of throwing, which keeps
 * scripts that forget an {@code inGame} check from spamming the console.</p>
 */
public final class PlayerApi {

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    private static ClientPlayerEntity p() {
        return mc().player;
    }

    public boolean present() {
        return p() != null;
    }

    // ---- position and rotation --------------------------------------------------------------

    public Vec3d position() {
        ClientPlayerEntity p = p();
        return p == null ? Vec3d.ZERO : p.getEntityPos();
    }

    public Vec3d eyePosition() {
        ClientPlayerEntity p = p();
        return p == null ? Vec3d.ZERO : p.getEyePos();
    }

    public double x() { return position().x; }

    public double y() { return position().y; }

    public double z() { return position().z; }

    public Vec3d previousPosition() {
        ClientPlayerEntity p = p();
        return p == null ? Vec3d.ZERO : new Vec3d(p.lastX, p.lastY, p.lastZ);
    }

    /** Smoothed position for the current frame; use this when drawing. */
    public Vec3d renderPosition() {
        ClientPlayerEntity p = p();
        if (p == null) return Vec3d.ZERO;
        float d = mc().getRenderTickCounter().getTickProgress(false);
        return new Vec3d(
                p.lastRenderX + (p.getX() - p.lastRenderX) * d,
                p.lastRenderY + (p.getY() - p.lastRenderY) * d,
                p.lastRenderZ + (p.getZ() - p.lastRenderZ) * d
        );
    }

    public Vec3d velocity() {
        ClientPlayerEntity p = p();
        return p == null ? Vec3d.ZERO : p.getVelocity();
    }

    public float yaw() {
        ClientPlayerEntity p = p();
        return p == null ? 0f : p.getYaw();
    }

    public float pitch() {
        ClientPlayerEntity p = p();
        return p == null ? 0f : p.getPitch();
    }

    public Box box() {
        ClientPlayerEntity p = p();
        return p == null ? new Box(0, 0, 0, 0, 0, 0) : p.getBoundingBox();
    }

    // ---- state -----------------------------------------------------------------------------

    public float health() {
        ClientPlayerEntity p = p();
        return p == null ? 0f : p.getHealth();
    }

    public float maxHealth() {
        ClientPlayerEntity p = p();
        return p == null ? 0f : p.getMaxHealth();
    }

    public float absorption() {
        ClientPlayerEntity p = p();
        return p == null ? 0f : p.getAbsorptionAmount();
    }

    public int armorPoints() {
        ClientPlayerEntity p = p();
        return p == null ? 0 : p.getArmor();
    }

    public int food() {
        ClientPlayerEntity p = p();
        return p == null ? 0 : p.getHungerManager().getFoodLevel();
    }

    public float saturation() {
        ClientPlayerEntity p = p();
        return p == null ? 0f : p.getHungerManager().getSaturationLevel();
    }

    public boolean alive() {
        ClientPlayerEntity p = p();
        return p != null && p.isAlive();
    }

    public boolean onGround() {
        ClientPlayerEntity p = p();
        return p != null && p.isOnGround();
    }

    public boolean sneaking() {
        ClientPlayerEntity p = p();
        return p != null && p.isSneaking();
    }

    public boolean sprinting() {
        ClientPlayerEntity p = p();
        return p != null && p.isSprinting();
    }

    public boolean inWater() {
        ClientPlayerEntity p = p();
        return p != null && p.isTouchingWater();
    }

    public boolean onFire() {
        ClientPlayerEntity p = p();
        return p != null && p.isOnFire();
    }

    public boolean flying() {
        ClientPlayerEntity p = p();
        return p != null && p.getAbilities().flying;
    }

    public boolean gliding() {
        ClientPlayerEntity p = p();
        return p != null && p.isGliding();
    }

    public boolean climbing() {
        ClientPlayerEntity p = p();
        return p != null && p.isClimbing();
    }

    public boolean riding() {
        ClientPlayerEntity p = p();
        return p != null && p.hasVehicle();
    }

    public boolean creative() {
        ClientPlayerEntity p = p();
        return p != null && p.isCreative();
    }

    public String gameMode() {
        var im = mc().interactionManager;
        return im == null || im.getCurrentGameMode() == null ? "" : im.getCurrentGameMode().getId();
    }

    public int pingMs() {
        ClientPlayerEntity p = p();
        if (p == null || mc().getNetworkHandler() == null) return 0;
        var entry = mc().getNetworkHandler().getPlayerListEntry(p.getUuid());
        return entry == null ? 0 : entry.getLatency();
    }

    public float fallDistanceBlocks() {
        ClientPlayerEntity p = p();
        return p == null ? 0f : (float) p.fallDistance;
    }

    public boolean hasMovementInput() {
        ClientPlayerEntity p = p();
        if (p == null) return false;
        var input = p.getLastPlayerInput();
        return input.forward() || input.backward() || input.left() || input.right();
    }

    public String name() {
        ClientPlayerEntity p = p();
        return p == null ? "" : p.getName().getString();
    }

    public int id() {
        ClientPlayerEntity p = p();
        return p == null ? -1 : p.getId();
    }

    public String uuid() {
        ClientPlayerEntity p = p();
        return p == null ? "" : p.getUuidAsString();
    }

    // ---- combat ----------------------------------------------------------------------------

    public float attackCooldown() {
        ClientPlayerEntity p = p();
        return p == null ? 0f : p.getAttackCooldownProgress(0f);
    }

    public boolean belowMinimumAttackCharge() {
        return attackCooldown() < 0.9f;
    }

    public double entityReachBlocks() {
        ClientPlayerEntity p = p();
        return p == null ? 3.0 : p.getEntityInteractionRange();
    }

    public double blockReachBlocks() {
        ClientPlayerEntity p = p();
        return p == null ? 4.5 : p.getBlockInteractionRange();
    }

    public boolean usingItem() {
        ClientPlayerEntity p = p();
        return p != null && p.isUsingItem();
    }

    public String usingHand() {
        ClientPlayerEntity p = p();
        if (p == null || !p.isUsingItem()) return "";
        return p.getActiveHand() == net.minecraft.util.Hand.MAIN_HAND ? "MAIN_HAND" : "OFF_HAND";
    }

    public int itemUseTicks() {
        ClientPlayerEntity p = p();
        return p == null ? 0 : p.getItemUseTime();
    }

    public boolean blocking() {
        ClientPlayerEntity p = p();
        return p != null && p.isBlocking();
    }

    public int hurtTicks() {
        ClientPlayerEntity p = p();
        return p == null ? 0 : p.hurtTime;
    }

    // ---- effects ---------------------------------------------------------------------------

    /** Wrapper so scripts see a friendly effect object rather than raw Minecraft types. */
    public static final class Effect {
        private final StatusEffectInstance instance;

        Effect(StatusEffectInstance instance) {
            this.instance = instance;
        }

        public String id() {
            return Registries.STATUS_EFFECT.getId(instance.getEffectType().value()) + "";
        }

        public String name() {
            return instance.getEffectType().value().getName().getString();
        }

        public int amplifier() { return instance.getAmplifier(); }

        public int durationTicks() { return instance.getDuration(); }

        public boolean infinite() { return instance.isInfinite(); }

        public boolean beneficial() { return instance.getEffectType().value().isBeneficial(); }
    }

    private static RegistryEntry<net.minecraft.entity.effect.StatusEffect> effectEntry(String id) {
        if (id == null || id.isBlank()) return null;
        String full = id.contains(":") ? id : "minecraft:" + id;
        Identifier ident = Identifier.tryParse(full);
        if (ident == null) return null;
        return Registries.STATUS_EFFECT.getEntry(ident).orElse(null);
    }

    public boolean hasEffect(String id) {
        ClientPlayerEntity p = p();
        RegistryEntry<net.minecraft.entity.effect.StatusEffect> entry = effectEntry(id);
        return p != null && entry != null && p.hasStatusEffect(entry);
    }

    public Effect effect(String id) {
        ClientPlayerEntity p = p();
        RegistryEntry<net.minecraft.entity.effect.StatusEffect> entry = effectEntry(id);
        if (p == null || entry == null) return null;
        StatusEffectInstance inst = p.getStatusEffect(entry);
        return inst == null ? null : new Effect(inst);
    }

    public List<Effect> effects() {
        List<Effect> out = new ArrayList<>();
        ClientPlayerEntity p = p();
        if (p == null) return out;
        for (StatusEffectInstance inst : p.getStatusEffects()) out.add(new Effect(inst));
        return out;
    }

    // ---- items -----------------------------------------------------------------------------

    public ItemStack mainHandItem() {
        ClientPlayerEntity p = p();
        return p == null ? ItemStack.EMPTY : p.getMainHandStack();
    }

    public ItemStack offHandItem() {
        ClientPlayerEntity p = p();
        return p == null ? ItemStack.EMPTY : p.getOffHandStack();
    }

    /** slot: 0 boots .. 3 helmet, matching the vanilla armour order. */
    public ItemStack armorItem(int slot) {
        ClientPlayerEntity p = p();
        if (p == null || slot < 0 || slot > 3) return ItemStack.EMPTY;
        return p.getInventory().getStack(36 + slot);
    }

    public List<ItemStack> armorItems() {
        List<ItemStack> out = new ArrayList<>();
        for (int i = 0; i < 4; i++) out.add(armorItem(i));
        return out;
    }

    public boolean isNaked() {
        for (ItemStack s : armorItems()) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    /** Identifier of the held item, e.g. "minecraft:diamond_sword". */
    public String held() {
        ItemStack stack = mainHandItem();
        return stack.isEmpty() ? "minecraft:air" : Registries.ITEM.getId(stack.getItem()).toString();
    }

    // ---- misc ------------------------------------------------------------------------------

    public void respawn() {
        ClientPlayerEntity p = p();
        if (p != null) mc().execute(p::requestRespawn);
    }

    public int xpLevel() {
        ClientPlayerEntity p = p();
        return p == null ? 0 : p.experienceLevel;
    }

    public float xpProgress() {
        ClientPlayerEntity p = p();
        return p == null ? 0f : p.experienceProgress;
    }

    public int airTicks() {
        ClientPlayerEntity p = p();
        return p == null ? 0 : p.getAir();
    }
}
