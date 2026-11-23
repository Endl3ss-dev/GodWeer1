// deobf by pusia
package su.catlean.client.services;

import kotlin.random.Random;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Sound;
import su.catlean.api.event.events.client.CloseSoundManagerEvent;
import su.catlean.api.event.events.client.PostTickEvent;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.misc.Assistant;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.SoundEngineAccessor;
import su.catlean.mixins.accessors.SoundManagerAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;

public final class SoundManager implements Service {
   @NotNull
   public static final SoundManager INSTANCE = new SoundManager();
   private static boolean flag;
   @NotNull
   private static final Sound sound = new Sound("startup", 1.0F);
   @NotNull
   private static final Sound sound2 = new Sound("shutdown", 1.0F);
   @NotNull
   private static final Sound sound3 = new Sound("first-launch", 1.0F);
   @NotNull
   private static final Sound sound4 = new Sound("settings-open", 1.0F);
   @NotNull
   private static final Sound sound5 = new Sound("settings-close", 1.0F);
   @NotNull
   private static final Sound sound6 = new Sound("no-settings", 1.0F);
   @NotNull
   private static final Sound sound7 = new Sound("switch-right", 0.7F);
   @NotNull
   private static final Sound sound8 = new Sound("switch-left", 0.7F);
   @NotNull
   private static final Sound sound9 = new Sound("enable", 0.7F);
   @NotNull
   private static final Sound sound10 = new Sound("disable", 0.7F);
   @NotNull
   private static final Sound sound11 = new Sound("lag-back", 1.0F);
   @NotNull
   private static final Sound sound12 = new Sound("lag", 1.0F);
   @NotNull
   private static final Sound sound13 = new Sound("armor-alert", 1.0F);
   @NotNull
   private static final Sound sound14 = new Sound("chat-mention", 1.0F);
   @NotNull
   private static final Sound sound15 = new Sound("totem-pop", 1.0F);
   @NotNull
   private static final Sound sound16 = new Sound("range-in", 1.0F);
   @NotNull
   private static final Sound sound17 = new Sound("range-out", 1.0F);
   @NotNull
   private static final Sound sound18 = new Sound("config-load", 1.0F);
   @NotNull
   private static final Sound sound19 = new Sound("config-save", 1.0F);
   @NotNull
   private static final Sound sound20 = new Sound("kill", 1.0F);
   @NotNull
   private static final Sound sound21 = new Sound("crystal-kill", 1.0F);
   @NotNull
   private static final Sound sound22 = new Sound("found-stash", 1.0F);
   @NotNull
   private static final Sound sound23 = new Sound("lego-breaking", 1.0F);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private SoundManager() {
   }

   @NotNull
   public final Sound getSound4() {
      return sound4;
   }

   @NotNull
   public final Sound getSound5() {
      return sound5;
   }

   @NotNull
   public final Sound getSound6() {
      return sound6;
   }

   @NotNull
   public final Sound getSound7() {
      return sound7;
   }

   @NotNull
   public final Sound getSound8() {
      return sound8;
   }

   @NotNull
   public final Sound getSound9() {
      return sound9;
   }

   @NotNull
   public final Sound getSound10() {
      return sound10;
   }

   @NotNull
   public final Sound getSound11() {
      return sound11;
   }

   @NotNull
   public final Sound getSound12() {
      return sound12;
   }

   @NotNull
   public final Sound getSound13() {
      return sound13;
   }

   @NotNull
   public final Sound getSound14() {
      return sound14;
   }

   @NotNull
   public final Sound getSound15() {
      return sound15;
   }

   @NotNull
   public final Sound getSound16() {
      return sound16;
   }

   @NotNull
   public final Sound getSound17() {
      return sound17;
   }

   @NotNull
   public final Sound getSound18() {
      return sound18;
   }

   @NotNull
   public final Sound getSound19() {
      return sound19;
   }

   @NotNull
   public final Sound getSound22() {
      return sound22;
   }

   @NotNull
   public final Sound getSound23() {
      return sound23;
   }

   public final void updateFlag(boolean flag) {
      if (Assistant.INSTANCE.getEnabled() && stopWatch.testValue(2000)) {
         if (flag && Random.Default.nextBoolean()) {
            sound21.applyExecuteIfPossible();
         } else {
            sound20.applyExecuteIfPossible();
         }

         stopWatch.update();
      }
   }

   @Flow
   private final void onPostTick(PostTickEvent postTickEvent) {
      SoundEngineAccessor soundEngine = (SoundEngineAccessor)((SoundManagerAccessor)MinecraftHolder.getMinecraft().getSoundManager()).getSoundEngine();
      if (!flag && soundEngine.isLoaded()) {
         flag = true;
         if (!ProxyManager.INSTANCE.getSharedDataSource().getSharedConfig().isFlag()) {
            sound3.applyExecuteIfPossible();
         } else {
            sound.applyExecuteIfPossible();
         }
      }
   }

   @Flow
   private final void onCloseSoundManager(CloseSoundManagerEvent closeSoundManagerEvent) {
      sound2.applyExecuteIfPossible();
      try {
         Thread.sleep(2000L);
      } catch (InterruptedException exception) {
         Thread.currentThread().interrupt();
      }
   }

   public static final class FixedVolumeSound extends AbstractSoundInstance {
      public FixedVolumeSound(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource) {
         super(soundEvent, soundSource, randomSource);
      }

      public float getVolume() {
         return super.getVolume() * this.volume;
      }
   }
}