// deobf by pusia
package su.catlean.mixins.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.CommandSuggestions.SuggestionsList;
import net.minecraft.commands.SharedSuggestionProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.catlean.api.event.events.client.InputSuggestorEvent;

@Mixin({CommandSuggestions.class})
public abstract class MixinCommandSuggestions {
   @Final
   @Shadow
   EditBox input;
   @Shadow
   boolean keepSuggestions;
   @Shadow
   private ParseResults<SharedSuggestionProvider> currentParse;
   @Shadow
   private CompletableFuture<Suggestions> pendingSuggestions;
   @Shadow
   private SuggestionsList suggestions;

   public MixinCommandSuggestions() {
   }

   @Shadow
   protected abstract void updateUsageInfo(ParseResults<SharedSuggestionProvider> parseResults, Suggestions suggestions2);

   @Inject(
      method = {"updateCommandInfo"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/brigadier/StringReader;canRead()Z",
         remap = false
      )},
      cancellable = true
   )
   public void refreshHook(CallbackInfo callbackInfo, @Local StringReader stringReader) {
      if (InputSuggestorEvent.INSTANCE.call()
         && stringReader.canRead(InputSuggestorEvent.INSTANCE.getPrefix().length())
         && stringReader.getString().startsWith(InputSuggestorEvent.INSTANCE.getPrefix(), stringReader.getCursor())) {
         stringReader.setCursor(stringReader.getCursor() + 1);
         if (this.currentParse == null) {
            this.currentParse = Objects.requireNonNull(InputSuggestorEvent.INSTANCE.getDispatcher())
               .parse(stringReader, (SharedSuggestionProvider)Objects.requireNonNull(InputSuggestorEvent.INSTANCE.getSource()));
         }

         int cursorPosition = this.input.getCursorPosition();
         if (cursorPosition >= 1 && (this.suggestions == null || !this.keepSuggestions)) {
            this.pendingSuggestions = Objects.requireNonNull(InputSuggestorEvent.INSTANCE.getDispatcher()).getCompletionSuggestions(this.currentParse, cursorPosition);
            this.pendingSuggestions.thenAccept(this::lambda$refreshHook$0);
         }

         callbackInfo.cancel();
      }
   }

   private void lambda$refreshHook$0(Suggestions suggestions) {
      if (this.pendingSuggestions.isDone()) {
         this.updateUsageInfo(this.currentParse, suggestions);
      }
   }
}