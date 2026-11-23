// deobf by pusia
package su.catlean.client.features.modules.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.world.AfterEntitySpawn;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;

public final class PearlChaser extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(PearlChaser.class, "onlyTarget", "getOnlyTarget()Z", 0), new PropertyReference1Impl(PearlChaser.class, "switch", "getSwitch()Lsu/catlean/uv;", 0) };
   @NotNull
   public static final PearlChaser INSTANCE = new PearlChaser();
   @NotNull
   private static final BooleanSetting onlyTarget = SettingFactory.defaultImpl(INSTANCE, "only-target", false, null, null, 12, null);
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, null, 12, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @Nullable
   private static BlockPos blockPos;
   private static int value;
   private static int value2;
   @NotNull
   private static final HashMap<Player, Long> map = new HashMap();

   private PearlChaser() {
      super("pearl-chaser", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final boolean getOnlyTarget() {
      return (Boolean)onlyTarget.computeObject(this, $$delegatedProperties[0]);
   }

   private final AutoSoup.Switch getSwitch() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onAfterEntitySpawn(AfterEntitySpawn afterEntitySpawn) {
      if (afterEntitySpawn.getEntity() instanceof ThrownEnderpearl) {
         MinecraftHolder.getClientLevel()
            .players()
            .stream()
            .min(Comparator.comparingDouble(arg -> arg.distanceToSqr(INSTANCE.computeVec3(afterEntitySpawn.getEntity()))))
            .ifPresent(arg2 -> {
               if (Intrinsics.areEqual(arg2, MinecraftHolder.getLocalPlayer())) {
                  value2 = afterEntitySpawn.getEntity().getId();
               }
            });
      }
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      blockPos = null;
      if (this.getOnlyTarget()) {
         if (Aura.INSTANCE.getEntity() != null && Aura.INSTANCE.getEnabled() && Aura.INSTANCE.getEntity() instanceof Player) {
            Player entity = (Player)Aura.INSTANCE.getEntity();
            if (!map.containsKey(entity)) {
               map.put(entity, MinecraftHolder.getTime());
            }
         }

         if (AutoCrystal.INSTANCE.getPlayer() != null && AutoCrystal.INSTANCE.getEnabled() && AutoCrystal.INSTANCE.getPlayer() != null) {
            Player player = AutoCrystal.INSTANCE.getPlayer();
            if (!map.containsKey(player)) {
               map.put(player, MinecraftHolder.getTime());
            }
         }

         Map hashMap = new HashMap(map);

         for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)hashMap.entrySet()) {
            if (MinecraftHolder.getTime() - ((Number)entrySetItem.getValue()).longValue() > 10000L) {
               map.remove(entrySetItem.getKey());
            }
         }
      }

      if (!(MinecraftHolder.getLocalPlayer().getHealth() < 5.0F)) {
         if (stopWatch.testValue(500)) {
            for (Entity entitiesForRenderingItem : MinecraftHolder.getClientLevel().entitiesForRendering()) {
               if (entitiesForRenderingItem instanceof ThrownEnderpearl && ((ThrownEnderpearl)entitiesForRenderingItem).getId() != value && ((ThrownEnderpearl)entitiesForRenderingItem).getId() != value2) {
                  Iterable<AbstractClientPlayer> players = MinecraftHolder.getClientLevel().players();
                  boolean flag3 = false;
                  var arrayList = new ArrayList();

                  for (Object object : players) {
                     AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object;
                     if ((map.containsKey(abstractClientPlayer) || !INSTANCE.getOnlyTarget()) && !Intrinsics.areEqual(abstractClientPlayer, MinecraftHolder.getLocalPlayer())) {
                        arrayList.add(object);
                     }
                  }

                  players = arrayList;
                  flag3 = false;
                  Iterator<AbstractClientPlayer> iterator = players.iterator();
                  AbstractClientPlayer object2;
                  if (!iterator.hasNext()) {
                     object2 = null;
                  } else {
                     AbstractClientPlayer object3 = iterator.next();
                     if (!iterator.hasNext()) {
                        object2 = object3;
                     } else {
                        AbstractClientPlayer abstractClientPlayer2 = (AbstractClientPlayer)object3;
                        double distanceToSqr = abstractClientPlayer2.distanceToSqr(INSTANCE.computeVec3(entitiesForRenderingItem));

                        do {
                           AbstractClientPlayer object4 = iterator.next();
                           AbstractClientPlayer abstractClientPlayer3 = (AbstractClientPlayer)object4;
                           double distanceToSqr2 = abstractClientPlayer3.distanceToSqr(INSTANCE.computeVec3(entitiesForRenderingItem));
                           if (Double.compare(distanceToSqr, distanceToSqr2) > 0) {
                              object3 = object4;
                              distanceToSqr = distanceToSqr2;
                           }
                        } while (iterator.hasNext());

                        object2 = object3;
                     }
                  }

                  AbstractClientPlayer abstractClientPlayer4 = (AbstractClientPlayer)object2;
                  if (abstractClientPlayer4 != null) {
                     blockPos = INSTANCE.computeBlockPos(INSTANCE.computeVec3(entitiesForRenderingItem), ((ThrownEnderpearl)entitiesForRenderingItem).getDeltaMovement());
                     value = ((ThrownEnderpearl)entitiesForRenderingItem).getId();
                  }
               }
            }

            if (blockPos != null) {
               LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
               BlockPos blockPos2 = blockPos;
               if (!(localPlayer.distanceToSqr(Vec3iUtil.computeVec3(blockPos2)) < 49.0)) {
                  blockPos2 = blockPos;
                  float amount = Rotation.vectorUtil.computeRotation(Vec3iUtil.computeVec3(blockPos2)).getAmount();
                  float amount2 = this.computeAmount(blockPos, amount);
                  if (!Float.isNaN(amount2)) {
                     BlockPos eyePosition = this.fetchEyePosition(amount, amount2);
                     if (eyePosition != null && !(blockPos.distToCenterSqr((Position)Vec3iUtil.computeVec3(eyePosition)) > 36.0)) {
                        Rotation rotation = new Rotation(amount, Math.clamp(amount2, -89.0F, 89.0F), false, null, 12, null);
                        RotationManager.INSTANCE.updateRotationAction(new RotationAction(rotation, 150, () -> {
                           InventoryManager.INSTANCE.checkOrdinal(Items.ENDER_PEARL, INSTANCE.getSwitch(), () -> {
                              ContainerUtil.computeInteractionResult(InteractionHand.MAIN_HAND, rotation.getAmount(), rotation.getAmount2());
                              Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
                              PacketUtil.applySend(serverboundSwingPacket);
                              return Unit.INSTANCE;
                           });
                           return Unit.INSTANCE;
                        }));
                        blockPos = null;
                        stopWatch.update();
                     }
                  }
               }
            }
         }
      }
   }

   private final float computeAmount(BlockPos blockPos, float amount) {
      Vec3 eyePosition = MinecraftHolder.getLocalPlayer().getEyePosition();
      ArrayList<Pair<BlockPos, Float>> arrayList = new ArrayList();
      int amount2 = (int)amount - 10;
      int amount3 = (int)amount + 10;
      if (amount2 <= amount3) {
         while (true) {
            for (int i = 10; -86 < i; i--) {
               BlockPos blockPos2 = this.computeBlockPos(eyePosition, this.computeVec3(i, amount2));
               if (blockPos2 != null && Vec3iUtil.computeVec3(blockPos2).distanceTo(Vec3iUtil.computeVec3(blockPos)) < 5.0) {
                  arrayList.add(TuplesKt.to(blockPos2, (float)i));
               }
            }

            if (amount2 == amount3) {
               break;
            }

            amount2++;
         }
      }

      Iterable<Pair<BlockPos, Float>> iterable = arrayList;
      Iterator<Pair<BlockPos, Float>> iterator = iterable.iterator();
      Pair<BlockPos, Float> object;
      if (!iterator.hasNext()) {
         object = null;
      } else {
         Pair<BlockPos, Float> object2 = iterator.next();
         if (!iterator.hasNext()) {
            object = object2;
         } else {
            Pair pair = (Pair)object2;
            double distanceTo = Vec3iUtil.computeVec3((BlockPos)pair.getFirst()).distanceTo(Vec3iUtil.computeVec3(blockPos));

            do {
               Pair<BlockPos, Float> object3 = iterator.next();
               Pair pair2 = (Pair)object3;
               double distanceTo2 = Vec3iUtil.computeVec3((BlockPos)pair2.getFirst()).distanceTo(Vec3iUtil.computeVec3(blockPos));
               if (Double.compare(distanceTo, distanceTo2) > 0) {
                  object2 = object3;
                  distanceTo = distanceTo2;
               }
            } while (iterator.hasNext());

            object = object2;
         }
      }

      return (Pair)object != null ? ((Number)((Pair)object).getSecond()).floatValue() : Float.NaN;
   }

   private final BlockPos fetchEyePosition(float amount, float amount2) {
      return this.computeBlockPos(MinecraftHolder.getLocalPlayer().getEyePosition(), this.computeVec3(amount2, amount));
   }

   private final BlockPos computeBlockPos(Vec3 vec3, Vec3 vec32) {
      Vec3 vec322 = vec32;
      Vec3 vec33 = vec3;
      Vec3 vec34 = vec3;

      for (int i = 0; i < 201; i++) {
         vec322 = vec322.add(0.0, -0.03, 0.0);
         vec322 = this.computeVec3(vec322, vec33);
         BlockHitResult clip = MinecraftHolder.getClientLevel()
            .clip(new ClipContext(vec34, vec33, Block.OUTLINE, Fluid.NONE, (Entity)MinecraftHolder.getLocalPlayer()));
         if (clip.getType() != Type.MISS) {
            return clip.getBlockPos();
         }

         for (Entity entitiesForRenderingItem : MinecraftHolder.getClientLevel().entitiesForRendering()) {
            if (entitiesForRenderingItem.distanceToSqr(vec33) < 64.0 && !Intrinsics.areEqual(entitiesForRenderingItem, MinecraftHolder.getLocalPlayer())) {
               Vec3 vec35 = vec33.add(vec322);
               Optional<Vec3> clip2 = entitiesForRenderingItem.getBoundingBox().clip(vec33, vec35);
               if (!clip2.isEmpty()) {
                  return BlockPos.containing((Position)clip2.get());
               }
            }
         }

         vec34 = vec33;
         vec33 = vec33.add(vec322);
      }

      return null;
   }

   private final Vec3 computeVec3(Vec3 vec3, Vec3 vec32) {
      return vec3.scale(MinecraftHolder.getClientLevel().getBlockState(BlockPos.containing((Position)vec32)).getBlock() instanceof LiquidBlock ? 0.8 : 0.99);
   }

   private final Vec3 computeVec3(float amount, float amount2) {
      double value3 = Math.PI / 180.0;
      double amount3 = -Math.sin(amount2 * value3) * Math.cos(amount * value3);
      double value4 = -Math.sin(amount * value3);
      double amount22 = Math.cos(amount2 * value3) * Math.cos(amount * value3);
      return new Vec3(amount3, value4, amount22)
         .scale(1.5)
         .add(
            MinecraftHolder.getLocalPlayer().getKnownMovement().x,
            MinecraftHolder.getLocalPlayer().onGround() ? 0.0 : MinecraftHolder.getLocalPlayer().getKnownMovement().y,
            MinecraftHolder.getLocalPlayer().getKnownMovement().z
         );
   }

   static {
   }
}