// deobf by pusia
package su.catlean.client.features.modules.world;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.ElderGuardianParticle;
import net.minecraft.client.particle.ExplodeParticle;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.particle.FireworkParticles.OverlayParticle;
import net.minecraft.client.particle.FireworkParticles.Starter;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.SpectralArrow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.GetFogBufferEvent;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.render.BossBarRenderEvent;
import su.catlean.api.event.events.render.DarknessEffectEvent;
import su.catlean.api.event.events.render.EntityAlphaEvent;
import su.catlean.api.event.events.render.HurtTiltEvent;
import su.catlean.api.event.events.render.ItemNameRenderEvent;
import su.catlean.api.event.events.render.MapRenderEvent;
import su.catlean.api.event.events.render.NauseaRenderEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.render.RenderArmorEvent;
import su.catlean.api.event.events.render.RenderFireOnEntityEvent;
import su.catlean.api.event.events.render.RenderFireOverlayEvent;
import su.catlean.api.event.events.render.RenderGuiBackgroundEvent;
import su.catlean.api.event.events.render.RenderInWallOverlayEvent;
import su.catlean.api.event.events.render.RenderNameTagEvent;
import su.catlean.api.event.events.render.RenderParticleEvent;
import su.catlean.api.event.events.render.RenderPortalOverlayEvent;
import su.catlean.api.event.events.render.RenderSpawnerEntityEvent;
import su.catlean.api.event.events.render.RenderUnderwaterOverlayEvent;
import su.catlean.api.event.events.render.RenderVignetteOverlayEvent;
import su.catlean.api.event.events.render.ScoreBoardRenderEvent;
import su.catlean.api.event.events.render.ShouldRenderEntityEvent;
import su.catlean.api.event.events.render.SignTextRenderEvent;
import su.catlean.api.event.events.render.StatusEffectsRenderEvent;
import su.catlean.api.event.events.render.WeatherRenderEvent;
import su.catlean.api.event.events.world.GetFogModifierEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render3D;

public final class NoRender extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(NoRender.class, "screen", "getScreen()Lsu/catlean/nl;", 0), new PropertyReference1Impl(NoRender.class, "hurtCam", "getHurtCam()Z", 0), new PropertyReference1Impl(NoRender.class, "potions", "getPotions()Z", 0), new PropertyReference1Impl(NoRender.class, "vignette", "getVignette()Z", 0), new PropertyReference1Impl(NoRender.class, "portal", "getPortal()Z", 0), new PropertyReference1Impl(NoRender.class, "bossbar", "getBossbar()Z", 0), new PropertyReference1Impl(NoRender.class, "fireOverlay", "getFireOverlay()Z", 0), new PropertyReference1Impl(NoRender.class, "waterOverlay", "getWaterOverlay()Z", 0), new PropertyReference1Impl(NoRender.class, "blockOverlay", "getBlockOverlay()Z", 0), new PropertyReference1Impl(NoRender.class, "nausea", "getNausea()Z", 0), new PropertyReference1Impl(NoRender.class, "blindness", "getBlindness()Z", 0), new PropertyReference1Impl(NoRender.class, "guiBackGround", "getGuiBackGround()Z", 0), new PropertyReference1Impl(NoRender.class, "noScoreBoard", "getNoScoreBoard()Z", 0), new PropertyReference1Impl(NoRender.class, "hotbarItemName", "getHotbarItemName()Z", 0), new PropertyReference1Impl(NoRender.class, "antiTitle", "getAntiTitle()Z", 0), new PropertyReference1Impl(NoRender.class, "entities", "getEntities()Lsu/catlean/nl;", 0), new PropertyReference1Impl(NoRender.class, "xp", "getXp()Z", 0), new PropertyReference1Impl(NoRender.class, "arrows", "getArrows()Z", 0), new PropertyReference1Impl(NoRender.class, "eggs", "getEggs()Z", 0), new PropertyReference1Impl(NoRender.class, "elderGuardian", "getElderGuardian()Z", 0), new PropertyReference1Impl(NoRender.class, "armor", "getArmor()Z", 0), new PropertyReference1Impl(NoRender.class, "items", "getItems()Z", 0), new PropertyReference1Impl(NoRender.class, "crystals", "getCrystals()Z", 0), new PropertyReference1Impl(NoRender.class, "fireworks", "getFireworks()Z", 0), new PropertyReference1Impl(NoRender.class, "noArmorStands", "getNoArmorStands()Z", 0), new PropertyReference1Impl(NoRender.class, "particles", "getParticles()Lsu/catlean/nl;", 0), new PropertyReference1Impl(NoRender.class, "explosions", "getExplosions()Z", 0), new PropertyReference1Impl(NoRender.class, "campFire", "getCampFire()Z", 0), new PropertyReference1Impl(NoRender.class, "breakParticles", "getBreakParticles()Z", 0), new PropertyReference1Impl(NoRender.class, "noWeather", "getNoWeather()Z", 0), new PropertyReference1Impl(NoRender.class, "world", "getWorld()Lsu/catlean/nl;", 0), new PropertyReference1Impl(NoRender.class, "maps", "getMaps()Z", 0), new PropertyReference1Impl(NoRender.class, "spawnerEntity", "getSpawnerEntity()Z", 0), new PropertyReference1Impl(NoRender.class, "fireEntity", "getFireEntity()Z", 0), new PropertyReference1Impl(NoRender.class, "signText", "getSignText()Z", 0), new PropertyReference1Impl(NoRender.class, "darkness", "getDarkness()Z", 0), new PropertyReference1Impl(NoRender.class, "fog", "getFog()Z", 0), new PropertyReference1Impl(NoRender.class, "other", "getOther()Lsu/catlean/nl;", 0), new PropertyReference1Impl(NoRender.class, "antiPlayerCollision", "getAntiPlayerCollision()Z", 0) };
   @NotNull
   public static final NoRender INSTANCE = new NoRender();
   @NotNull
   private static final GroupSetting screen = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "screen", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting hurtCam = SettingFactory.defaultImpl(INSTANCE, "hurt-cam", true, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting potions = SettingFactory.defaultImpl(INSTANCE, "potions", false, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting vignette = SettingFactory.defaultImpl(INSTANCE, "vignette", true, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting portal = SettingFactory.defaultImpl(INSTANCE, "portal", true, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting bossbar = SettingFactory.defaultImpl(INSTANCE, "bossbar", false, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting fireOverlay = SettingFactory.defaultImpl(INSTANCE, "fire-overlay", false, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting waterOverlay = SettingFactory.defaultImpl(INSTANCE, "water-overlay", false, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting blockOverlay = SettingFactory.defaultImpl(INSTANCE, "block-overlay", false, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting nausea = SettingFactory.defaultImpl(INSTANCE, "nausea", false, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting blindness = SettingFactory.defaultImpl(INSTANCE, "blindness", false, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting guiBackGround = SettingFactory.defaultImpl(INSTANCE, "gui-back-ground", true, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting noScoreBoard = SettingFactory.defaultImpl(INSTANCE, "no-score-board", true, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting hotbarItemName = SettingFactory.defaultImpl(INSTANCE, "hotbar-item-name", false, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final BooleanSetting antiTitle = SettingFactory.defaultImpl(INSTANCE, "anti-title", false, INSTANCE.getScreen(), null, 8, null);
   @NotNull
   private static final GroupSetting entities = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "entities", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting xp = SettingFactory.defaultImpl(INSTANCE, "xp", false, INSTANCE.getEntities(), null, 8, null);
   @NotNull
   private static final BooleanSetting arrows = SettingFactory.defaultImpl(INSTANCE, "arrows", false, INSTANCE.getEntities(), null, 8, null);
   @NotNull
   private static final BooleanSetting eggs = SettingFactory.defaultImpl(INSTANCE, "eggs", false, INSTANCE.getEntities(), null, 8, null);
   @NotNull
   private static final BooleanSetting guardian = SettingFactory.defaultImpl(INSTANCE, "guardian", false, INSTANCE.getEntities(), null, 8, null);
   @NotNull
   private static final BooleanSetting armor = SettingFactory.defaultImpl(INSTANCE, "armor", false, INSTANCE.getEntities(), null, 8, null);
   @NotNull
   private static final BooleanSetting items = SettingFactory.defaultImpl(INSTANCE, "items", false, INSTANCE.getEntities(), null, 8, null);
   @NotNull
   private static final BooleanSetting crystals = SettingFactory.defaultImpl(INSTANCE, "crystals", false, INSTANCE.getEntities(), null, 8, null);
   @NotNull
   private static final BooleanSetting fireworks = SettingFactory.defaultImpl(INSTANCE, "fireworks", false, INSTANCE.getEntities(), null, 8, null);
   @NotNull
   private static final BooleanSetting noArmorStands = SettingFactory.defaultImpl(INSTANCE, "no-armor-stands", false, INSTANCE.getEntities(), null, 8, null);
   @NotNull
   private static final GroupSetting particles = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "particles", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting explosions = SettingFactory.defaultImpl(INSTANCE, "explosions", true, INSTANCE.getParticles(), null, 8, null);
   @NotNull
   private static final BooleanSetting campFire = SettingFactory.defaultImpl(INSTANCE, "camp-fire", false, INSTANCE.getParticles(), null, 8, null);
   @NotNull
   private static final BooleanSetting breakParticles = SettingFactory.defaultImpl(INSTANCE, "break-particles", true, INSTANCE.getParticles(), null, 8, null);
   @NotNull
   private static final BooleanSetting noWeather = SettingFactory.defaultImpl(INSTANCE, "no-weather", false, INSTANCE.getParticles(), null, 8, null);
   @NotNull
   private static final GroupSetting world = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "world", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting maps = SettingFactory.defaultImpl(INSTANCE, "maps", false, INSTANCE.getWorld(), null, 8, null);
   @NotNull
   private static final BooleanSetting spawnerEntity = SettingFactory.defaultImpl(INSTANCE, "spawner-entity", false, INSTANCE.getWorld(), null, 8, null);
   @NotNull
   private static final BooleanSetting fireOnEntity = SettingFactory.defaultImpl(INSTANCE, "fire-on-entity", true, INSTANCE.getWorld(), null, 8, null);
   @NotNull
   private static final BooleanSetting signText = SettingFactory.defaultImpl(INSTANCE, "sign-text", false, INSTANCE.getWorld(), null, 8, null);
   @NotNull
   private static final BooleanSetting darkness = SettingFactory.defaultImpl(INSTANCE, "darkness", false, INSTANCE.getWorld(), null, 8, null);
   @NotNull
   private static final BooleanSetting fog = SettingFactory.defaultImpl(INSTANCE, "fog", false, INSTANCE.getWorld(), null, 8, null);
   @NotNull
   private static final GroupSetting other = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "other", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting antiPlayerCollision = SettingFactory.defaultImpl(
      INSTANCE, "anti-player-collision", true, INSTANCE.getOther(), null, 8, null
   );

   private NoRender() {
      super("no-render", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final GroupValue getScreen() {
      return (GroupValue)screen.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getHurtCam() {
      return (Boolean)hurtCam.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getPotions() {
      return (Boolean)potions.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getVignette() {
      return (Boolean)vignette.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getPortal() {
      return (Boolean)portal.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getBossbar() {
      return (Boolean)bossbar.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getFireOverlay() {
      return (Boolean)fireOverlay.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getWaterOverlay() {
      return (Boolean)waterOverlay.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getBlockOverlay() {
      return (Boolean)blockOverlay.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getNausea() {
      return (Boolean)nausea.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getBlindness() {
      return (Boolean)blindness.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getGuiBackGround() {
      return (Boolean)guiBackGround.computeObject(this, $$delegatedProperties[11]);
   }

   private final boolean getNoScoreBoard() {
      return (Boolean)noScoreBoard.computeObject(this, $$delegatedProperties[12]);
   }

   private final boolean getHotbarItemName() {
      return (Boolean)hotbarItemName.computeObject(this, $$delegatedProperties[13]);
   }

   private final boolean getAntiTitle() {
      return (Boolean)antiTitle.computeObject(this, $$delegatedProperties[14]);
   }

   private final GroupValue getEntities() {
      return (GroupValue)entities.computeObject(this, $$delegatedProperties[15]);
   }

   private final boolean getXp() {
      return (Boolean)xp.computeObject(this, $$delegatedProperties[16]);
   }

   private final boolean getArrows() {
      return (Boolean)arrows.computeObject(this, $$delegatedProperties[17]);
   }

   private final boolean getEggs() {
      return (Boolean)eggs.computeObject(this, $$delegatedProperties[18]);
   }

   private final boolean getElderGuardian() {
      return (Boolean)guardian.computeObject(this, $$delegatedProperties[19]);
   }

   private final boolean getArmor() {
      return (Boolean)armor.computeObject(this, $$delegatedProperties[20]);
   }

   private final boolean getItems() {
      return (Boolean)items.computeObject(this, $$delegatedProperties[21]);
   }

   private final boolean getCrystals() {
      return (Boolean)crystals.computeObject(this, $$delegatedProperties[22]);
   }

   private final boolean getFireworks() {
      return (Boolean)fireworks.computeObject(this, $$delegatedProperties[23]);
   }

   private final boolean getNoArmorStands() {
      return (Boolean)noArmorStands.computeObject(this, $$delegatedProperties[24]);
   }

   private final GroupValue getParticles() {
      return (GroupValue)particles.computeObject(this, $$delegatedProperties[25]);
   }

   private final boolean getExplosions() {
      return (Boolean)explosions.computeObject(this, $$delegatedProperties[26]);
   }

   private final boolean getCampFire() {
      return (Boolean)campFire.computeObject(this, $$delegatedProperties[27]);
   }

   private final boolean getBreakParticles() {
      return (Boolean)breakParticles.computeObject(this, $$delegatedProperties[28]);
   }

   private final boolean getNoWeather() {
      return (Boolean)noWeather.computeObject(this, $$delegatedProperties[29]);
   }

   private final GroupValue getWorld() {
      return (GroupValue)world.computeObject(this, $$delegatedProperties[30]);
   }

   private final boolean getMaps() {
      return (Boolean)maps.computeObject(this, $$delegatedProperties[31]);
   }

   private final boolean getSpawnerEntity() {
      return (Boolean)spawnerEntity.computeObject(this, $$delegatedProperties[32]);
   }

   private final boolean getFireEntity() {
      return (Boolean)fireOnEntity.computeObject(this, $$delegatedProperties[33]);
   }

   private final boolean getSignText() {
      return (Boolean)signText.computeObject(this, $$delegatedProperties[34]);
   }

   private final boolean getDarkness() {
      return (Boolean)darkness.computeObject(this, $$delegatedProperties[35]);
   }

   private final boolean getFog() {
      return (Boolean)fog.computeObject(this, $$delegatedProperties[36]);
   }

   private final GroupValue getOther() {
      return (GroupValue)other.computeObject(this, $$delegatedProperties[37]);
   }

   private final boolean getAntiPlayerCollision() {
      return (Boolean)antiPlayerCollision.computeObject(this, $$delegatedProperties[38]);
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundSetTitleTextPacket && this.getAntiTitle()) {
         receivePacket.cancel();
      }
   }

   @Flow
   private final void onSignTextRender(SignTextRenderEvent signTextRenderEvent) {
      if (this.getSignText()
         && (
            MinecraftHolder.getMinecraft().hitResult == null
               || MinecraftHolder.getMinecraft().hitResult.getType() != Type.BLOCK
               || !(MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult)
               || !Intrinsics.areEqual(MinecraftHolder.getMinecraft().hitResult.getLocation(), signTextRenderEvent.getPos())
         )) {
         signTextRenderEvent.cancel();
      }
   }

   @Flow
   private final void onRenderArmor(RenderArmorEvent renderArmorEvent) {
      if (this.getArmor()) {
         renderArmorEvent.cancel();
      }
   }

   @Flow
   private final void onMapRender(MapRenderEvent mapRenderEvent) {
      if (this.getMaps()) {
         mapRenderEvent.cancel();
      }
   }

   @Flow
   private final void onRenderFireOverlay(RenderFireOverlayEvent renderFireOverlayEvent) {
      if (this.getFireOverlay()) {
         renderFireOverlayEvent.cancel();
      }
   }

   @Flow
   private final void onWeatherRender(WeatherRenderEvent weatherRenderEvent) {
      if (this.getNoWeather()) {
         weatherRenderEvent.cancel();
      }
   }

   @Flow
   private final void onRenderUnderwaterOverlay(RenderUnderwaterOverlayEvent renderUnderwaterOverlayEvent) {
      if (this.getWaterOverlay()) {
         renderUnderwaterOverlayEvent.cancel();
      }
   }

   @Flow
   private final void onRenderGuiBackground(RenderGuiBackgroundEvent renderGuiBackgroundEvent) {
      if (this.getGuiBackGround()) {
         renderGuiBackgroundEvent.cancel();
      }
   }

   @Flow
   private final void onRenderInWallOverlay(RenderInWallOverlayEvent renderInWallOverlayEvent) {
      if (this.getBlockOverlay()) {
         renderInWallOverlayEvent.cancel();
      }
   }

   @Flow
   private final void onDarknessEffect(DarknessEffectEvent darknessEffectEvent) {
      if (this.getDarkness()) {
         darknessEffectEvent.cancel();
      }
   }

   @Flow
   private final void onRenderSpawnerEntity(RenderSpawnerEntityEvent renderSpawnerEntityEvent) {
      if (this.getSpawnerEntity()) {
         renderSpawnerEntityEvent.cancel();
      }
   }

   @Flow
   private final void onNauseaRender(NauseaRenderEvent nauseaRenderEvent) {
      if (this.getNausea()) {
         nauseaRenderEvent.cancel();
      }
   }

   @Flow
   private final void onStatusEffectsRender(StatusEffectsRenderEvent statusEffectsRenderEvent) {
      if (this.getPotions()) {
         statusEffectsRenderEvent.cancel();
      }
   }

   @Flow
   private final void onRenderPortalOverlay(RenderPortalOverlayEvent renderPortalOverlayEvent) {
      if (this.getPortal()) {
         renderPortalOverlayEvent.cancel();
      }
   }

   @Flow
   private final void onRenderVignetteOverlay(RenderVignetteOverlayEvent renderVignetteOverlayEvent) {
      if (this.getVignette()) {
         renderVignetteOverlayEvent.cancel();
      }
   }

   @Flow
   private final void onScoreBoardRender(ScoreBoardRenderEvent scoreBoardRenderEvent) {
      if (this.getNoScoreBoard()) {
         scoreBoardRenderEvent.cancel();
      }
   }

   @Flow
   private final void onItemNameRender(ItemNameRenderEvent itemNameRenderEvent) {
      if (this.getHotbarItemName()) {
         itemNameRenderEvent.cancel();
      }
   }

   @Flow
   private final void onRenderFireOnEntity(RenderFireOnEntityEvent renderFireOnEntityEvent) {
      if (this.getFireEntity()) {
         renderFireOnEntityEvent.cancel();
      }
   }

   @Flow
   private final void onBossBarRender(BossBarRenderEvent bossBarRenderEvent) {
      if (this.getBossbar()) {
         bossBarRenderEvent.cancel();
      }
   }

   @Flow
   private final void onHurtTilt(HurtTiltEvent hurtTiltEvent) {
      if (this.getHurtCam() && MinecraftHolder.getLocalPlayer().hurtTime - Render3D.INSTANCE.getAmount4() > 0.0F) {
         hurtTiltEvent.cancel();
      }
   }

   @Flow
   private final void onEntityAlpha(EntityAlphaEvent entityAlphaEvent) {
      if (this.getEnabled()
         && this.getAntiPlayerCollision()
         && !Intrinsics.areEqual(entityAlphaEvent.getPos(), this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()))) {
         entityAlphaEvent.setAlpha(Math.clamp((float)(MinecraftHolder.getLocalPlayer().distanceToSqr(entityAlphaEvent.getPos()) / 3.0F) + 0.2F, 0.0F, 1.0F));
         entityAlphaEvent.cancel();
      }
   }

   @Flow
   private final void onRenderParticle(RenderParticleEvent renderParticleEvent) {
      if (this.getElderGuardian() && renderParticleEvent.getParticle() instanceof ElderGuardianParticle) {
         renderParticleEvent.cancel();
      } else if (!this.getExplosions()
         || !(renderParticleEvent.getParticle() instanceof HugeExplosionParticle) && !(renderParticleEvent.getParticle() instanceof ExplodeParticle)) {
         if (this.getCampFire() && renderParticleEvent.getParticle() instanceof CampfireSmokeParticle) {
            renderParticleEvent.cancel();
         } else if (this.getBreakParticles() && renderParticleEvent.getParticle() instanceof TerrainParticle) {
            renderParticleEvent.cancel();
         } else if (this.getFireworks()
            && (renderParticleEvent.getParticle() instanceof Starter || renderParticleEvent.getParticle() instanceof OverlayParticle)) {
            renderParticleEvent.cancel();
         }
      } else {
         renderParticleEvent.cancel();
      }
   }

   @Flow
   private final void onShouldRenderEntity(ShouldRenderEntityEvent shouldRenderEntityEvent) {
      if (this.testObject(shouldRenderEntityEvent.getEntity())) {
         shouldRenderEntityEvent.cancel();
      }
   }

   @Flow
   private final void onRenderNameTag(RenderNameTagEvent renderNameTagEvent) {
      if (renderNameTagEvent.getState() instanceof ArmorStandRenderState && this.getNoArmorStands()) {
         renderNameTagEvent.cancel();
      }
   }

   @Flow
   private final void applyCancel(GetFogBufferEvent getFogBufferEvent) {
      if (this.getFog()) {
         getFogBufferEvent.cancel();
      }
   }

   @Flow
   private final void onGetFogModifier(GetFogModifierEvent getFogModifierEvent) {
      if (this.getBlindness()) {
         getFogModifierEvent.cancel();
      }
   }

   @Flow
   public final void onRender3D(Render3DEvent event) {
      GofraState.INSTANCE.setShouldRender(this.isFlag());
   }

   private final boolean testObject(Object object) {
      if (object instanceof EndCrystal && this.getCrystals()) {
         return true;
      } else if (object instanceof ArmorStand && this.getNoArmorStands()) {
         return true;
      } else if (object instanceof ItemEntity && this.getItems()) {
         return true;
      } else if (object instanceof ThrownEgg && this.getEggs()) {
         return true;
      } else if ((object instanceof ThrownExperienceBottle || object instanceof ExperienceOrb) && this.getXp()) {
         return true;
      } else {
         return object instanceof Arrow && this.getArrows() ? true : object instanceof SpectralArrow && this.getArrows();
      }
   }

   private final boolean isFlag() {
      return this.getEnabled() && (this.getCrystals() || this.getNoArmorStands() || this.getItems() || this.getEggs() || this.getXp() || this.getArrows());
   }

   static {
   }
}