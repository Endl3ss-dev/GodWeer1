// deobf by pusia
package su.catlean.client.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.addon.feature.AddonCommand;
import su.catlean.api.addon.impl.AddonCommandContextImpl;
import su.catlean.client.commands.Command;

public final class AddonsCommand extends Command {
   @NotNull
   private final AddonCommand addon;

   public AddonsCommand(AddonCommand addon) {
      super(addon.getName());
      this.addon = addon;
   }

   @NotNull
   public final AddonCommand getAddon() {
      return this.addon;
   }

   @Override
   public void applyExecutes(LiteralArgumentBuilder builder) {
      builder.executes(arg -> this.addon.execute(new AddonCommandContextImpl(this)));
   }
}