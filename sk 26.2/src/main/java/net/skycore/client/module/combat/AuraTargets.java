package net.skycore.client.module.combat;

import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.skycore.client.config.FriendManager;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.MultiSelectSetting;

/** The active profile is shared by acquisition, target locking and the final pre-hit check. */
final class AuraTargets {
   private static final String BEDWARS = "BedWars";
   private static final String STANDARD = "Standard server";
   private static final String PLAYERS = "Players";
   private static final String PROJECTILES = "Projectiles";
   private static final String ANIMALS = "Animals";
   private static final String MONSTERS = "Monsters";
   private static final String STANDS = "Armor stands";
   private static final String IGNORE_TEAM = "Ignore teammates";
   private static final String IGNORE_INVISIBLE = "Ignore invisible";
   private static final String IGNORE_VILLAGERS = "Ignore villagers";
   private static final String IGNORE_FRIENDS = "Ignore Friends";
   private static final String IGNORE_UNARMORED = "Ignore unarmored";
   private static final String IGNORE_CREATIVE = "Ignore creative";
   private static final String IGNORE_BOTS = "Ignore bots";

   final ModeSetting profile = new ModeSetting("Target profile", STANDARD, STANDARD, BEDWARS);
   final MultiSelectSetting bedWars = new MultiSelectSetting("BedWars targets",
      List.of(PLAYERS, PROJECTILES, MONSTERS, IGNORE_TEAM, IGNORE_INVISIBLE, IGNORE_VILLAGERS, IGNORE_FRIENDS, IGNORE_UNARMORED, IGNORE_BOTS),
      Set.of(PLAYERS, PROJECTILES, IGNORE_TEAM, IGNORE_VILLAGERS, IGNORE_FRIENDS, IGNORE_BOTS));
   final MultiSelectSetting standard = new MultiSelectSetting("Standard targets",
      List.of(PLAYERS, ANIMALS, MONSTERS, STANDS, IGNORE_FRIENDS, IGNORE_INVISIBLE, IGNORE_UNARMORED, IGNORE_CREATIVE, IGNORE_BOTS),
      Set.of(PLAYERS, IGNORE_FRIENDS, IGNORE_CREATIVE, IGNORE_BOTS));

   AuraTargets() {
      bedWars.visibleWhen(() -> profile.is(BEDWARS));
      standard.visibleWhen(() -> profile.is(STANDARD));
   }

   boolean allows(Player self, Entity entity) {
      boolean inBedWars = profile.is(BEDWARS);
      Set<String> selected = (inBedWars ? bedWars : standard).get();
      if (entity == self || !entity.isAlive() || entity.level() != self.level() || entity.isSpectator()) {
         return false;
      }
      if (selected.contains(IGNORE_INVISIBLE) && entity.isInvisible()) {
         return false;
      }
      if (entity.isInvulnerable() && !(entity instanceof Player player && player.isCreative())) {
         return false;
      }

      if (entity instanceof Player player) {
         String name = player.getGameProfile().name();
         return selected.contains(PLAYERS) && !name.equalsIgnoreCase(self.getGameProfile().name())
            && !(selected.contains(IGNORE_CREATIVE) && player.isCreative())
            && !(selected.contains(IGNORE_TEAM) && sameHelmet(self, player))
            && !(selected.contains(IGNORE_UNARMORED) && isUnarmored(player))
            && !(selected.contains(IGNORE_FRIENDS) && FriendManager.INSTANCE.isFriend(name))
            && !(selected.contains(IGNORE_BOTS) && AntiBotModule.isBot(player));
      }
      if (entity instanceof Projectile projectile) {
         return selected.contains(PROJECTILES) && projectile.getOwner() != self && projectile.isPickable() && projectile.isAttackable();
      }
      if (entity instanceof ArmorStand stand) {
         return selected.contains(STANDS) && !stand.isMarker();
      }
      if (entity instanceof AbstractVillager) {
         // Shop NPCs share the BedWars mob switch; turning off all target types must leave no targets.
         return inBedWars && selected.contains(MONSTERS) && !selected.contains(IGNORE_VILLAGERS);
      }
      if (entity.getType().getCategory() == MobCategory.MONSTER) {
         return selected.contains(MONSTERS);
      }
      return selected.contains(ANIMALS) && (entity instanceof Animal || entity instanceof AmbientCreature || entity instanceof WaterAnimal);
   }

   private static boolean sameHelmet(Player self, Player other) {
      ItemStack mine = self.getItemBySlot(EquipmentSlot.HEAD);
      ItemStack theirs = other.getItemBySlot(EquipmentSlot.HEAD);
      return !mine.isEmpty() && !theirs.isEmpty() && ItemStack.isSameItemSameComponents(mine, theirs);
   }

   // Armor-slot check adapted from Phobia/src/main/java/ruhack/phobia/system/modulesystem/impl/rage/killaura/target/TargetSelector.java.
   private static boolean isUnarmored(Player player) {
      return player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()
         && player.getItemBySlot(EquipmentSlot.LEGS).isEmpty() && player.getItemBySlot(EquipmentSlot.FEET).isEmpty();
   }
}
