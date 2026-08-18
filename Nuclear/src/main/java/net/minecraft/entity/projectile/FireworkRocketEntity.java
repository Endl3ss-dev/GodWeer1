package net.minecraft.entity.projectile;

import nuclear.control.Manager;
import nuclear.module.impl.combat.AttackAura;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.OptionalInt;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.client.Minecraft.player;

public class FireworkRocketEntity extends ProjectileEntity implements IRendersAsItem {
    private static final DataParameter<ItemStack> FIREWORK_ITEM;
    private static final DataParameter<OptionalInt> BOOSTED_ENTITY_ID;
    private static final DataParameter<Boolean> field_213895_d;
    private int fireworkAge;
    private int lifetime;
    private LivingEntity boostedEntity;

    public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> p_i50164_1_, World p_i50164_2_) {
        super(p_i50164_1_, p_i50164_2_);
    }

    public FireworkRocketEntity(World worldIn, double x, double y, double z, ItemStack givenItem) {
        super(EntityType.FIREWORK_ROCKET, worldIn);
        this.fireworkAge = 0;
        this.setPosition(x, y, z);
        int i = 1;
        if (!givenItem.isEmpty() && givenItem.hasTag()) {
            this.dataManager.set(FIREWORK_ITEM, givenItem.copy());
            i += givenItem.getOrCreateChildTag("Fireworks").getByte("Flight");
        }
        this.setMotion(this.rand.nextGaussian() * 0.001, 0.05, this.rand.nextGaussian() * 0.001);
        this.lifetime = 10 * i + this.rand.nextInt(6) + this.rand.nextInt(7);
    }

    public FireworkRocketEntity(World p_i231581_1_, @Nullable Entity p_i231581_2_, double p_i231581_3_, double p_i231581_5_, double p_i231581_7_, ItemStack p_i231581_9_) {
        this(p_i231581_1_, p_i231581_3_, p_i231581_5_, p_i231581_7_, p_i231581_9_);
        this.setShooter(p_i231581_2_);
    }

    public FireworkRocketEntity(World p_i47367_1_, ItemStack p_i47367_2_, LivingEntity p_i47367_3_) {
        this(p_i47367_1_, p_i47367_3_, p_i47367_3_.getPosX(), p_i47367_3_.getPosY(), p_i47367_3_.getPosZ(), p_i47367_2_);
        this.dataManager.set(BOOSTED_ENTITY_ID, OptionalInt.of(p_i47367_3_.getEntityId()));
        this.boostedEntity = p_i47367_3_;
    }

    public FireworkRocketEntity(World p_i50165_1_, ItemStack p_i50165_2_, double p_i50165_3_, double p_i50165_5_, double p_i50165_7_, boolean p_i50165_9_) {
        this(p_i50165_1_, p_i50165_3_, p_i50165_5_, p_i50165_7_, p_i50165_2_);
        this.dataManager.set(field_213895_d, p_i50165_9_);
    }

    public FireworkRocketEntity(World p_i231582_1_, ItemStack p_i231582_2_, Entity p_i231582_3_, double p_i231582_4_, double p_i231582_6_, double p_i231582_8_, boolean p_i231582_10_) {
        this(p_i231582_1_, p_i231582_2_, p_i231582_4_, p_i231582_6_, p_i231582_8_, p_i231582_10_);
        this.setShooter(p_i231582_3_);
    }

    protected void registerData() {
        this.dataManager.register(FIREWORK_ITEM, ItemStack.EMPTY);
        this.dataManager.register(BOOSTED_ENTITY_ID, OptionalInt.empty());
        this.dataManager.register(field_213895_d, false);
    }

    public boolean isInRangeToRenderDist(double distance) {
        return distance < 4096.0 && !this.isAttachedToEntity();
    }

    public boolean isInRangeToRender3d(double x, double y, double z) {
        return super.isInRangeToRender3d(x, y, z) && !this.isAttachedToEntity();
    }

    public void tick() {
        super.tick();
        if (this.isAttachedToEntity()) {
            if (this.boostedEntity == null) {
                ((OptionalInt) this.dataManager.get(BOOSTED_ENTITY_ID)).ifPresent((p_213891_1_) -> {
                    Entity entity = this.world.getEntityByID(p_213891_1_);
                    if (entity instanceof LivingEntity) {
                        this.boostedEntity = (LivingEntity) entity;
                    }
                });
            }

            if (this.boostedEntity != null && this.boostedEntity.isElytraFlying()) {
                if (!Manager.FUNCTION_MANAGER.antiTarget.state) {
                    Vector3d vector3d = this.boostedEntity.getLookVec();

                    Vector3d vector3d1 = this.boostedEntity.getMotion();

                    float yaw = player.getYaw();
                    float pitch = player.getPitch();
                    float normalizedYaw2 = yaw % 180;
                    if (normalizedYaw2 > 90) normalizedYaw2 -= 180;
                    else if (normalizedYaw2 < -90) normalizedYaw2 += 180;

                    float normalizedPitch2 = pitch % 180;
                    if (normalizedPitch2 > 90) normalizedPitch2 -= 180;
                    else if (normalizedPitch2 < -90) normalizedPitch2 += 180;

                    float normalizedYaw = yaw % 180;
                    if (normalizedYaw > 90) normalizedYaw -= 180;
                    else if (normalizedYaw < -90) normalizedYaw += 180;

                    float normalizedPitch = pitch % 180;
                    if (normalizedPitch > 90) normalizedPitch -= 180;
                    else if (normalizedPitch < -90) normalizedPitch += 180;

                    float speed4 = getSpeedForPitch(normalizedPitch, normalizedYaw);
                    float speedy4 = getSpeedYForPitch(normalizedPitch);
                    float speedtarget = getTargetSpeed(normalizedPitch2, normalizedYaw2);
                    float speedtarget4 = getSpeedTargetForPitch(normalizedPitch2, normalizedYaw2);
                    float speedtargety4 = getSpeedTargetYForPitch(normalizedPitch2);

                    float speed = getSpeedForPitchRange(normalizedPitch, normalizedYaw);
                    float speed2 = getSpeed2ForPitchRange(normalizedPitch2, normalizedYaw2);
                    float speedy = getSpeedYForPitchRange(normalizedPitch);
                    float speedtargety = getSpeedTargetYForPitchRange(normalizedPitch);

                    float speedcustom = getSpeedCustom(normalizedYaw, normalizedPitch);
                    float speedcustomtarget = getSpeedCustomTarget(normalizedYaw2, normalizedPitch2);
                    float baseSpeedcustomtarget = getBaseSpeedCustomTarget(normalizedYaw2, normalizedPitch2, speedcustomtarget);
                    float speedcustomy = getSpeedCustomY(normalizedPitch);
                    float speedcustomytarget = getSpeedCustomYTarget(normalizedPitch2);

                    float speedtop = Manager.FUNCTION_MANAGER.auraFunction.state ? Manager.FUNCTION_MANAGER.auraFunction.speedtop : 0.0f;

                    float finalSpeedXZ = 0.0f;
                    float finalSpeedY = 0.0f;

                    if (Manager.FUNCTION_MANAGER.elytraBooster.mode.is("Кастомный")) {
                        finalSpeedXZ = Manager.FUNCTION_MANAGER.elytraBooster.maxspeed.get() ? speedcustom : Manager.FUNCTION_MANAGER.elytraBooster.speedxz.getValue().floatValue();
                        finalSpeedY = Manager.FUNCTION_MANAGER.elytraBooster.maxspeed.get() ? speedcustomy : Manager.FUNCTION_MANAGER.elytraBooster.speedy.getValue().floatValue();
                    } else if (Manager.FUNCTION_MANAGER.elytraBooster.mode.is("ReallyWorld")) {
                        finalSpeedXZ = speed;
                        finalSpeedY = speedy;
                    } else {
                        finalSpeedXZ = speed4;
                        finalSpeedY = speedy4;
                    }


                    if (Manager.FUNCTION_MANAGER.elytraBooster.state) {
                        if (!Manager.FUNCTION_MANAGER.elytraBooster.mode.is("Кастомный")) {
                            if (Manager.FUNCTION_MANAGER.elytraBooster.mode.is("ReallyWorld")) {
                                if (Manager.FUNCTION_MANAGER.auraFunction.target != null) {
                                    this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * speed2 - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * speedtargety - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * speed2 - vector3d1.z) * 0.5));
                                } else {
                                    this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * finalSpeedXZ - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * finalSpeedY - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * finalSpeedXZ - vector3d1.z) * 0.5));
                                }
                            } else {
                                if (Manager.FUNCTION_MANAGER.auraFunction.target != null) {
                                    this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * (AttackAura.obgon ? speedtarget :speedtarget4)  - speedtop - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * speedtargety4 - speedtop - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * (AttackAura.obgon ? speedtarget :speedtarget4) - speedtop - vector3d1.z) * 0.5));
                                } else {
                                    this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * finalSpeedXZ - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * finalSpeedY - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * finalSpeedXZ - vector3d1.z) * 0.5));
                                }
                            }
                        } else {
                            if (Manager.FUNCTION_MANAGER.auraFunction.target != null) {
                                this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * (Manager.FUNCTION_MANAGER.elytraBooster.maxspeed.get() ? baseSpeedcustomtarget : finalSpeedXZ) - speedtop - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * (Manager.FUNCTION_MANAGER.elytraBooster.maxspeed.get() ? speedcustomytarget : finalSpeedY) - speedtop - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * (Manager.FUNCTION_MANAGER.elytraBooster.maxspeed.get() ? baseSpeedcustomtarget : finalSpeedXZ) - speedtop - vector3d1.z) * 0.5));
                            } else {
                                this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * finalSpeedXZ - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * finalSpeedY - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * finalSpeedXZ - vector3d1.z) * 0.5));
                            }
                        }
                    } else {
                        this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * 1.5 - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * 1.5 - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * 1.5 - vector3d1.z) * 0.5));
                    }
                } else {
                    Vector3d vector3d = this.boostedEntity.getLookVec();
                    if (Manager.FUNCTION_MANAGER.auraFunction.target != null) {
                        Vector3d vector3d1 = this.boostedEntity.getMotion();
                        this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * (Manager.FUNCTION_MANAGER.antiTarget.mod.is("Обычный") ? Manager.FUNCTION_MANAGER.antiTarget.speedantitarget.getValue().floatValue() : 2.5) - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * Manager.FUNCTION_MANAGER.antiTarget.speedantitarget.getValue().floatValue() - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * Manager.FUNCTION_MANAGER.antiTarget.speedantitarget.getValue().floatValue() - vector3d1.z) * 0.5));
                    } else {
                        Vector3d vector3d1 = this.boostedEntity.getMotion();
                        this.boostedEntity.setMotion(vector3d1.add(vector3d.x * 0.1 + (vector3d.x * (Manager.FUNCTION_MANAGER.antiTarget.mod.is("Обычный") ? Manager.FUNCTION_MANAGER.antiTarget.speedantitarget.getValue().floatValue() : 2.5) - vector3d1.x) * 0.5, vector3d.y * 0.1 + (vector3d.y * Manager.FUNCTION_MANAGER.antiTarget.speedantitarget.getValue().floatValue() - vector3d1.y) * 0.5, vector3d.z * 0.1 + (vector3d.z * Manager.FUNCTION_MANAGER.antiTarget.speedantitarget.getValue().floatValue() - vector3d1.z) * 0.5));
                    }
                }
                this.setPosition(this.boostedEntity.getPosX(), this.boostedEntity.getPosY(), this.boostedEntity.getPosZ());
                this.setMotion(this.boostedEntity.getMotion());
            }
        } else {
            if (!this.func_213889_i()) {
                double d2 = this.collidedHorizontally ? 1.0 : 1.15;
                this.setMotion(this.getMotion().mul(d2, 1.0, d2).add(0.0, 0.04, 0.0));
            }
            Vector3d vector3d = this.getMotion();
            this.move(MoverType.SELF, vector3d);
            this.setMotion(vector3d);
        }

        RayTraceResult raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_);
        if (!this.noClip) {
            this.onImpact(raytraceresult);
            this.isAirBorne = true;
        }

        this.func_234617_x_();
        if (this.fireworkAge == 0 && !this.isSilent()) {
            this.world.playSound((PlayerEntity) null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
        }

        ++this.fireworkAge;
        if (this.world.isRemote && this.fireworkAge % 2 < 2) {
            this.world.addParticle(ParticleTypes.FIREWORK, this.getPosX(), this.getPosY() - 0.3, this.getPosZ(), this.rand.nextGaussian() * 0.05, -this.getMotion().y * 0.5, this.rand.nextGaussian() * 0.05);
        }

        if (!this.world.isRemote && this.fireworkAge > this.lifetime) {
            this.func_213893_k();
        }
    }

    private float getTargetSpeed(float pitch, float yaw) {
        float speed = (float) (Manager.FUNCTION_MANAGER.auraFunction.target != null ? AttackAura.bpstarget() / 20.3 : 35 / 23.5);
        float absPitch = Math.abs(pitch);
        float absYaw = Math.abs(yaw);

        float maxClamp;
        if (absPitch >= 38 && absPitch <= 52) maxClamp = 2.02f;
        else if (absPitch >= 32 && absPitch <= 58) maxClamp = 1.98f;
        else if (absPitch >= 28 && absPitch <= 62) maxClamp = 1.97f;
        else if ((absYaw >= 29 && absYaw <= 61) || (absPitch >= 29 && absPitch <= 61)) maxClamp = 1.981f;
        else if ((absYaw >= 28 && absYaw <= 60) || (absPitch >= 28 && absPitch <= 60)) maxClamp = 1.973f;
        else if ((absYaw >= 26 && absYaw <= 64) || (absPitch >= 26 && absPitch <= 64)) maxClamp = 1.894f;
        else if ((absYaw >= 24 && absYaw <= 66) || (absPitch >= 24 && absPitch <= 66)) maxClamp = 1.79f;
        else if ((absYaw >= 15 && absYaw <= 75) || (absPitch >= 15 && absPitch <= 75)) maxClamp = 1.72f;
        else if ((absYaw >= 13 && absYaw <= 77) || (absPitch >= 13 && absPitch <= 77)) maxClamp = 1.70f;
        else if ((absYaw >= 12 && absYaw <= 78) || (absPitch >= 12 && absPitch <= 78)) maxClamp = 1.70f;
        else if ((absYaw >= 8 && absYaw <= 82) || (absPitch >= 11 && absPitch <= 79)) maxClamp = 1.68f;
        else if ((absYaw >= 5 && absYaw <= 85) || (absPitch >= 8 && absPitch <= 82)) maxClamp = 1.655f;
        else if (absYaw <= 90 || absPitch <= 90) maxClamp = 1.642f;
        else maxClamp = 1.641f;

        maxClamp = pitch > 15 ? maxClamp - 0.068f : maxClamp;

        return MathHelper.clamp(speed, 1.3f, maxClamp);
    }

    private float getSpeedForPitch(float pitch, float yaw) {
        float absPitch = Math.abs(pitch);
        float absYaw = Math.abs(yaw);

        float speed;
        if (absPitch >= 38 && absPitch <= 52) speed = 2f;
        else if (absPitch >= 32 && absPitch <= 58) speed = 1.96f;
        else if (absPitch >= 28 && absPitch <= 62) speed = 1.95f;
        else if ((absYaw >= 29 && absYaw <= 61) || (absPitch >= 29 && absPitch <= 61)) speed = 1.963f;
        else if ((absYaw >= 28 && absYaw <= 60) || (absPitch >= 28 && absPitch <= 60)) speed = 1.954f;
        else if ((absYaw >= 26 && absYaw <= 64) || (absPitch >= 26 && absPitch <= 64)) speed = 1.874f;
        else if ((absYaw >= 24 && absYaw <= 66) || (absPitch >= 24 && absPitch <= 66)) speed = 1.77f;
        else if ((absYaw >= 15 && absYaw <= 75) || (absPitch >= 15 && absPitch <= 75)) speed = 1.70f;
        else if ((absYaw >= 13 && absYaw <= 77) || (absPitch >= 13 && absPitch <= 77)) speed = 1.68f;
        else if ((absYaw >= 12 && absYaw <= 78) || (absPitch >= 12 && absPitch <= 78)) speed = 1.68f;
        else if ((absYaw >= 8 && absYaw <= 82) || (absPitch >= 11 && absPitch <= 79)) speed = 1.66f;
        else if ((absYaw >= 5 && absYaw <= 85) || (absPitch >= 8 && absPitch <= 82)) speed = 1.635f;
        else if (absYaw <= 90 || absPitch <= 90) speed = 1.622f;
        else speed = 1.621f;

        return pitch > 15 ? speed - 0.068f : speed;
    }

    private float getSpeedYForPitch(float pitch) {
        if (Math.abs(pitch) >= 37 && Math.abs(pitch) <= 38) return 2.03f;
        if (Math.abs(pitch) >= 25 && Math.abs(pitch) <= 30) return 2f;
        if (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 45) return 1.99f;
        if (Math.abs(pitch) >= 40 && Math.abs(pitch) <= 50) return 1.97f;
        if (Math.abs(pitch) >= 50 && Math.abs(pitch) <= 60) return 1.96f;
        if (Math.abs(pitch) >= 51 && Math.abs(pitch) <= 61) return 1.87f;
        if (Math.abs(pitch) >= 52 && Math.abs(pitch) <= 65) return 1.70f;
        return 1.59f;
    }

    private float getSpeedTargetForPitch(float pitch, float yaw) {
        if ((Math.abs(pitch) >= 40 && Math.abs(pitch) <= 50)) return 2f;
        if ((Math.abs(pitch) >= 38 && Math.abs(pitch) <= 52) ) return 1.96f;
        if ((Math.abs(pitch) >= 32 && Math.abs(pitch) <= 58)) return 1.96f;
        if ((Math.abs(yaw) >= 29 && Math.abs(yaw) <= 61) || (Math.abs(pitch) >= 29 && Math.abs(pitch) <= 61)) return 1.963f;
        if ((Math.abs(yaw) >= 27 && Math.abs(yaw) <= 63) || (Math.abs(pitch) >= 27 && Math.abs(pitch) <= 63)) return 1.84f;
        if ((Math.abs(yaw) >= 26 && Math.abs(yaw) <= 64) || (Math.abs(pitch) >= 26 && Math.abs(pitch) <= 64)) return 1.8f;
        if ((Math.abs(yaw) >= 15 && Math.abs(yaw) <= 75) || (Math.abs(pitch) >= 15 && Math.abs(pitch) <= 75)) return 1.74f;
        if ((Math.abs(yaw) >= 13 && Math.abs(yaw) <= 77) || (Math.abs(pitch) >= 13 && Math.abs(pitch) <= 77)) return 1.7f;
        if ((Math.abs(yaw) >= 12 && Math.abs(yaw) <= 78) || (Math.abs(pitch) >= 12 && Math.abs(pitch) <= 78)) return 1.671f;
        return 1.626f;
    }

    private float getSpeedTargetYForPitch(float pitch) {
        if (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 40) return 2f;
        if (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 45) return 1.99f;
        if (Math.abs(pitch) >= 40 && Math.abs(pitch) <= 50) return 1.97f;
        if (Math.abs(pitch) >= 50 && Math.abs(pitch) <= 60) return 1.96f;
        if (Math.abs(pitch) >= 51 && Math.abs(pitch) <= 61) return 1.89f;
        if (Math.abs(pitch) >= 52 && Math.abs(pitch) <= 65) return 1.70f;
        return 1.602f;
    }

    private float getSpeedForPitchRange(float pitch, float yaw) {
        if (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 40) return 1.85f;
        if (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 45) return 1.8f;
        if (Math.abs(pitch) >= 45 && Math.abs(pitch) <= 55) return 1.76f;
        if ((Math.abs(yaw) >= 30 && Math.abs(yaw) <= 60) || (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 60)) return 1.8f;
        if ((Math.abs(yaw) >= 28 && Math.abs(yaw) <= 62) || (Math.abs(pitch) >= 28 && Math.abs(pitch) <= 62)) return 1.76f;
        if ((Math.abs(yaw) >= 26 && Math.abs(yaw) <= 64) || (Math.abs(pitch) >= 26 && Math.abs(pitch) <= 64)) return 1.7f;
        if ((Math.abs(yaw) >= 24 && Math.abs(yaw) <= 66) || (Math.abs(pitch) >= 24 && Math.abs(pitch) <= 66)) return 1.69f;
        if ((Math.abs(yaw) >= 22 && Math.abs(yaw) <= 68) || (Math.abs(pitch) >= 22 && Math.abs(pitch) <= 68)) return 1.68f;
        if ((Math.abs(yaw) >= 20 && Math.abs(yaw) <= 70) || (Math.abs(pitch) >= 20 && Math.abs(pitch) <= 70)) return 1.67f;
        if ((Math.abs(yaw) >= 18 && Math.abs(yaw) <= 72) || (Math.abs(pitch) >= 18 && Math.abs(pitch) <= 72)) return 1.66f;
        if ((Math.abs(yaw) >= 16 && Math.abs(yaw) <= 74) || (Math.abs(pitch) >= 16 && Math.abs(pitch) <= 74)) return 1.65f;
        if ((Math.abs(yaw) >= 12 && Math.abs(yaw) <= 78) || (Math.abs(pitch) >= 12 && Math.abs(pitch) <= 78)) return 1.64f;
        if ((Math.abs(yaw) >= 10 && Math.abs(yaw) <= 80) || (Math.abs(pitch) >= 10 && Math.abs(pitch) <= 80)) return 1.63f;
        if ((Math.abs(yaw) >= 8 && Math.abs(yaw) <= 82) || (Math.abs(pitch) >= 8 && Math.abs(pitch) <= 82)) return 1.62f;
        return 1.61f;
    }

    private float getSpeed2ForPitchRange(float pitch, float yaw) {
        if (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 40) return 1.95f;
        if (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 45) return 1.9f;
        if (Math.abs(pitch) >= 45 && Math.abs(pitch) <= 55) return 1.86f;
        if ((Math.abs(yaw) >= 30 && Math.abs(yaw) <= 60) || (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 60)) return 1.75f;
        if ((Math.abs(yaw) >= 28 && Math.abs(yaw) <= 62) || (Math.abs(pitch) >= 28 && Math.abs(pitch) <= 62)) return 1.72f;
        if ((Math.abs(yaw) >= 26 && Math.abs(yaw) <= 64) || (Math.abs(pitch) >= 26 && Math.abs(pitch) <= 64)) return 1.71f;
        if ((Math.abs(yaw) >= 24 && Math.abs(yaw) <= 66) || (Math.abs(pitch) >= 24 && Math.abs(pitch) <= 66)) return 1.7f;
        if ((Math.abs(yaw) >= 22 && Math.abs(yaw) <= 68) || (Math.abs(pitch) >= 22 && Math.abs(pitch) <= 68)) return 1.69f;
        if ((Math.abs(yaw) >= 20 && Math.abs(yaw) <= 70) || (Math.abs(pitch) >= 20 && Math.abs(pitch) <= 70)) return 1.68f;
        if ((Math.abs(yaw) >= 18 && Math.abs(yaw) <= 72) || (Math.abs(pitch) >= 18 && Math.abs(pitch) <= 72)) return 1.67f;
        return 1.65f;
    }

    private float getSpeedYForPitchRange(float pitch) {
        if (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 40) return 1.69f;
        if (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 45) return 1.68f;
        if (Math.abs(pitch) >= 45 && Math.abs(pitch) <= 55) return 1.65f;
        return 1.59f;
    }

    private float getSpeedTargetYForPitchRange(float pitch) {
        if (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 40) return 1.71f;
        if (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 45) return 1.7f;
        if (Math.abs(pitch) >= 45 && Math.abs(pitch) <= 55) return 1.67f;
        return 1.59f;
    }

    private float getSpeedCustom(float yaw, float pitch) {
        if ((Math.abs(yaw) >= 40 && Math.abs(yaw) <= 50) || (Math.abs(pitch) >= 40 && Math.abs(pitch) <= 50)) return Manager.FUNCTION_MANAGER.elytraBooster.speed40.getValue().floatValue();
        if ((Math.abs(yaw) >= 35 && Math.abs(yaw) <= 55) || (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 55)) return Manager.FUNCTION_MANAGER.elytraBooster.speed35.getValue().floatValue();
        if ((Math.abs(yaw) >= 30 && Math.abs(yaw) <= 60) || (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 60)) return Manager.FUNCTION_MANAGER.elytraBooster.speed30.getValue().floatValue();
        if ((Math.abs(yaw) >= 25 && Math.abs(yaw) <= 65) || (Math.abs(pitch) >= 25 && Math.abs(pitch) <= 65)) return Manager.FUNCTION_MANAGER.elytraBooster.speed25.getValue().floatValue();
        if ((Math.abs(yaw) >= 20 && Math.abs(yaw) <= 70) || (Math.abs(pitch) >= 20 && Math.abs(pitch) <= 70)) return Manager.FUNCTION_MANAGER.elytraBooster.speed20.getValue().floatValue();
        if ((Math.abs(yaw) >= 15 && Math.abs(yaw) <= 75) || (Math.abs(pitch) >= 15 && Math.abs(pitch) <= 75)) return Manager.FUNCTION_MANAGER.elytraBooster.speed15.getValue().floatValue();
        if ((Math.abs(yaw) >= 10 && Math.abs(yaw) <= 80) || (Math.abs(pitch) >= 10 && Math.abs(pitch) <= 80)) return Manager.FUNCTION_MANAGER.elytraBooster.speed10.getValue().floatValue();
        return Manager.FUNCTION_MANAGER.elytraBooster.speed5.getValue().floatValue();
    }
    private float getSpeedCustomTarget(float yaw, float pitch) {
        if ((Math.abs(yaw) >= 40 && Math.abs(yaw) <= 50) || (Math.abs(pitch) >= 40 && Math.abs(pitch) <= 50)) return Manager.FUNCTION_MANAGER.elytraBooster.speed40.getValue().floatValue() + 0.0075f;
        if ((Math.abs(yaw) >= 35 && Math.abs(yaw) <= 55) || (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 55)) return Manager.FUNCTION_MANAGER.elytraBooster.speed35.getValue().floatValue() + 0.0075f;
        if ((Math.abs(yaw) >= 30 && Math.abs(yaw) <= 60) || (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 60)) return Manager.FUNCTION_MANAGER.elytraBooster.speed30.getValue().floatValue() + 0.0075f;
        if ((Math.abs(yaw) >= 25 && Math.abs(yaw) <= 65) || (Math.abs(pitch) >= 25 && Math.abs(pitch) <= 65)) return Manager.FUNCTION_MANAGER.elytraBooster.speed25.getValue().floatValue() + 0.0075f;
        if ((Math.abs(yaw) >= 20 && Math.abs(yaw) <= 70) || (Math.abs(pitch) >= 20 && Math.abs(pitch) <= 70)) return Manager.FUNCTION_MANAGER.elytraBooster.speed20.getValue().floatValue() + 0.0075f;
        if ((Math.abs(yaw) >= 15 && Math.abs(yaw) <= 75) || (Math.abs(pitch) >= 15 && Math.abs(pitch) <= 75)) return Manager.FUNCTION_MANAGER.elytraBooster.speed15.getValue().floatValue() + 0.0075f;
        if ((Math.abs(yaw) >= 10 && Math.abs(yaw) <= 80) || (Math.abs(pitch) >= 10 && Math.abs(pitch) <= 80)) return Manager.FUNCTION_MANAGER.elytraBooster.speed10.getValue().floatValue() + 0.0075f;
        return Manager.FUNCTION_MANAGER.elytraBooster.speed5.getValue().floatValue() + 0.0075f;
    }

    private float getBaseSpeedCustomTarget(float yaw, float pitch, float speedcustomtarget) {
        if ((Math.abs(yaw) >= 40 && Math.abs(yaw) <= 50) || (Math.abs(pitch) >= 40 && Math.abs(pitch) <= 50)) return speedcustomtarget;
        if ((Math.abs(yaw) >= 35 && Math.abs(yaw) <= 55) || (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 55)) return speedcustomtarget + 0.01f;
        if ((Math.abs(yaw) >= 30 && Math.abs(yaw) <= 60) || (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 60)) return speedcustomtarget + 0.015f;
        return speedcustomtarget + 0.022f;
    }

    private float getSpeedCustomY(float pitch) {
        if (Math.abs(pitch) >= 40 && Math.abs(pitch) <= 50) return Manager.FUNCTION_MANAGER.elytraBooster.speed40y.getValue().floatValue();
        if (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 55) return Manager.FUNCTION_MANAGER.elytraBooster.speed35y.getValue().floatValue();
        if (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 60) return Manager.FUNCTION_MANAGER.elytraBooster.speed30y.getValue().floatValue();
        if (Math.abs(pitch) >= 25 && Math.abs(pitch) <= 65) return Manager.FUNCTION_MANAGER.elytraBooster.speed25y.getValue().floatValue();
        if (Math.abs(pitch) >= 20 && Math.abs(pitch) <= 70) return Manager.FUNCTION_MANAGER.elytraBooster.speed20y.getValue().floatValue();
        if (Math.abs(pitch) >= 15 && Math.abs(pitch) <= 75) return Manager.FUNCTION_MANAGER.elytraBooster.speed15y.getValue().floatValue();
        if (Math.abs(pitch) >= 10 && Math.abs(pitch) <= 80) return Manager.FUNCTION_MANAGER.elytraBooster.speed10y.getValue().floatValue();
        return Manager.FUNCTION_MANAGER.elytraBooster.speed5y.getValue().floatValue();
    }

    private float getSpeedCustomYTarget(float pitch) {
        if (Math.abs(pitch) >= 40 && Math.abs(pitch) <= 50) return Manager.FUNCTION_MANAGER.elytraBooster.speed40y.getValue().floatValue();
        if (Math.abs(pitch) >= 35 && Math.abs(pitch) <= 55) return Manager.FUNCTION_MANAGER.elytraBooster.speed35y.getValue().floatValue();
        if (Math.abs(pitch) >= 30 && Math.abs(pitch) <= 60) return Manager.FUNCTION_MANAGER.elytraBooster.speed30y.getValue().floatValue();
        if (Math.abs(pitch) >= 25 && Math.abs(pitch) <= 65) return Manager.FUNCTION_MANAGER.elytraBooster.speed25y.getValue().floatValue();
        if (Math.abs(pitch) >= 20 && Math.abs(pitch) <= 70) return Manager.FUNCTION_MANAGER.elytraBooster.speed20y.getValue().floatValue();
        if (Math.abs(pitch) >= 15 && Math.abs(pitch) <= 75) return Manager.FUNCTION_MANAGER.elytraBooster.speed15y.getValue().floatValue();
        if (Math.abs(pitch) >= 10 && Math.abs(pitch) <= 80) return Manager.FUNCTION_MANAGER.elytraBooster.speed10y.getValue().floatValue();
        return Manager.FUNCTION_MANAGER.elytraBooster.speed5y.getValue().floatValue();
    }

    private void func_213893_k() {
        this.world.setEntityState(this, (byte) 17);
        this.dealExplosionDamage();
        this.remove();
    }

    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        if (!this.world.isRemote) {
            this.func_213893_k();
        }
    }

    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        BlockPos blockpos = new BlockPos(p_230299_1_.getPos());
        this.world.getBlockState(blockpos).onEntityCollision(this.world, blockpos, this);
        if (!this.world.isRemote() && this.func_213894_l()) {
            this.func_213893_k();
        }
        super.func_230299_a_(p_230299_1_);
    }

    private boolean func_213894_l() {
        ItemStack itemstack = (ItemStack) this.dataManager.get(FIREWORK_ITEM);
        CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
        ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
        return listnbt != null && !listnbt.isEmpty();
    }

    private void dealExplosionDamage() {
        float f = 0.0F;
        ItemStack itemstack = (ItemStack) this.dataManager.get(FIREWORK_ITEM);
        CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
        ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
        if (listnbt != null && !listnbt.isEmpty()) {
            f = 5.0F + (float) (listnbt.size() * 2);
        }

        if (f > 0.0F) {
            if (this.boostedEntity != null) {
                this.boostedEntity.attackEntityFrom(DamageSource.func_233548_a_(this, this.func_234616_v_()), 5.0F + (float) (listnbt.size() * 2));
            }

            double d0 = 5.0;
            Vector3d vector3d = this.getPositionVec();
            Iterator var8 = this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(5.0)).iterator();

            while (var8.hasNext()) {
                LivingEntity livingentity = (LivingEntity) var8.next();
                if (livingentity != this.boostedEntity && this.getDistanceSq(livingentity) <= 25.0) {
                    boolean flag = false;
                    for (int i = 0; i < 2; ++i) {
                        Vector3d vector3d1 = new Vector3d(livingentity.getPosX(), livingentity.getPosYHeight(0.5 * (double) i), livingentity.getPosZ());
                        RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
                        if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        float f1 = f * (float) Math.sqrt((5.0 - (double) this.getDistance(livingentity)) / 5.0);
                        livingentity.attackEntityFrom(DamageSource.func_233548_a_(this, this.func_234616_v_()), f1);
                    }
                }
            }
        }
    }

    private boolean isAttachedToEntity() {
        return ((OptionalInt) this.dataManager.get(BOOSTED_ENTITY_ID)).isPresent();
    }

    public boolean func_213889_i() {
        return (Boolean) this.dataManager.get(field_213895_d);
    }

    public void handleStatusUpdate(byte id) {
        if (id == 17 && this.world.isRemote) {
            if (!this.func_213894_l()) {
                for (int i = 0; i < this.rand.nextInt(3) + 2; ++i) {
                    this.world.addParticle(ParticleTypes.POOF, this.getPosX(), this.getPosY(), this.getPosZ(), this.rand.nextGaussian() * 0.05, 0.005, this.rand.nextGaussian() * 0.05);
                }
            } else {
                ItemStack itemstack = (ItemStack) this.dataManager.get(FIREWORK_ITEM);
                CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
                Vector3d vector3d = this.getMotion();
                this.world.makeFireworks(this.getPosX(), this.getPosY(), this.getPosZ(), vector3d.x, vector3d.y, vector3d.z, compoundnbt);
            }
        }
        super.handleStatusUpdate(id);
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Life", this.fireworkAge);
        compound.putInt("LifeTime", this.lifetime);
        ItemStack itemstack = (ItemStack) this.dataManager.get(FIREWORK_ITEM);
        if (!itemstack.isEmpty()) {
            compound.put("FireworksItem", itemstack.write(new CompoundNBT()));
        }
        compound.putBoolean("ShotAtAngle", (Boolean) this.dataManager.get(field_213895_d));
    }

    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.fireworkAge = compound.getInt("Life");
        this.lifetime = compound.getInt("LifeTime");
        ItemStack itemstack = ItemStack.read(compound.getCompound("FireworksItem"));
        if (!itemstack.isEmpty()) {
            this.dataManager.set(FIREWORK_ITEM, itemstack);
        }
        if (compound.contains("ShotAtAngle")) {
            this.dataManager.set(field_213895_d, compound.getBoolean("ShotAtAngle"));
        }
    }

    public ItemStack getItem() {
        ItemStack itemstack = (ItemStack) this.dataManager.get(FIREWORK_ITEM);
        return itemstack.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : itemstack;
    }

    public boolean canBeAttackedWithItem() {
        return false;
    }

    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    static {
        FIREWORK_ITEM = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.ITEMSTACK);
        BOOSTED_ENTITY_ID = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.OPTIONAL_VARINT);
        field_213895_d = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.BOOLEAN);
    }
}