// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Identifiers;

public final class Sound {
   @NotNull
   private final String name;
   private final float amount;
   @NotNull
   private final SoundEvent soundEvent;

   public Sound(String name, float amount) {
      this.name = name;
      this.amount = amount;
      Identifier identifier = Identifiers.computeIdentifier("catlean:" + this.name);
      this.soundEvent = SoundEvent.createVariableRangeEvent(identifier);
      Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, this.soundEvent);
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void applyExecuteIfPossible() {
      MinecraftHolder.getMinecraft().executeIfPossible(() -> {
         SoundManager soundManager = MinecraftHolder.getMinecraft().getSoundManager();
         SoundEvent soundEvent2 = this.soundEvent;
         SoundSource voice = SoundSource.VOICE;
         RandomSource unseededRandom = SoundInstance.createUnseededRandom();
         soundManager.play((SoundInstance)(new su.catlean.client.services.SoundManager.FixedVolumeSound(soundEvent2, voice, unseededRandom)));
      });
   }

   public final void applyExecuteIfPossible(double amount, double amount2, double amount3) {
      MinecraftHolder.getMinecraft()
         .executeIfPossible(
            () -> MinecraftHolder.getClientLevel().playLocalSound(amount, amount2, amount3, this.soundEvent, SoundSource.PLAYERS, this.amount, 1.0F, true)
         );
   }

   @NotNull
   public final String getName2() {
      return this.name;
   }

   public final float getAmount2() {
      return this.amount;
   }

   @NotNull
   @Override
   public String toString() {
      return "Sound(name=" + this.name + ", volume=" + this.amount + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.name.hashCode();
      return hashCode * 31 + Float.hashCode(this.amount);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Sound sound)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.name, sound.name) ? false : Float.compare(this.amount, sound.amount) == 0;
      }
   }
}