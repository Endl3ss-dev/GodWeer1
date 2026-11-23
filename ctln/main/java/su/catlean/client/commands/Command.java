// deobf by pusia
package su.catlean.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.LocalizationManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ComponentUtil;

public abstract class Command {
   @NotNull
   private final String name;
   @NotNull
   private final String text;

   public Command(String name) {
      this.name = name;
      this.text = LocalizationManager.INSTANCE.computeText("commands.descriptions." + this.name, new Object[0]);
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   public abstract void applyExecutes(LiteralArgumentBuilder literalArgumentBuilder);

   public final void applyLiteral(CommandDispatcher dispatcher) {
      LiteralArgumentBuilder<Object> literal = LiteralArgumentBuilder.literal(this.name);
      this.applyExecutes(literal);
      dispatcher.register(literal);
   }

   @NotNull
   public final RequiredArgumentBuilder computeRequiredArgumentBuilder(String name, ArgumentType type) {
      return RequiredArgumentBuilder.argument(name, type);
   }

   @NotNull
   public final LiteralArgumentBuilder<Object> computeLiteralArgumentBuilder(String name) {
      return LiteralArgumentBuilder.literal(name);
   }

   public final void updateText(String msg) {
      this.applyAppend(ComponentUtil.computeComponent(msg));
   }

   public final void applyAppend(Component msg) {
      MutableComponent mutableComponent3 = ComponentUtil.computeMutableComponent3(ComponentUtil.computeMutableComponent(ComponentUtil.getMutableComponent(), ">"), " ");
      mutableComponent3.append(msg);
      if (MinecraftHolder.getMinecraft().isSameThread()) {
         MinecraftHolder.getLocalPlayer().sendSystemMessage((Component)mutableComponent3);
      } else {
         MinecraftHolder.getMinecraft().executeIfPossible(() -> MinecraftHolder.getLocalPlayer().sendSystemMessage((Component)mutableComponent3));
      }
   }
}