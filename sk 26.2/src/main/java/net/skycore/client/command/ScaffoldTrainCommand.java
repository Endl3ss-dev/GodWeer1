package net.skycore.client.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.IOException;
import net.skycore.client.SkycoreClient;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.skycore.client.module.movement.training.ScaffoldTrainingRecorder;

/** Local-only command; never sends these commands or datasets to a server. */
public final class ScaffoldTrainCommand extends ClientCommand {
   public ScaffoldTrainCommand() {
      super("scafftrain");
      ScaffoldTrainingRecorder.INSTANCE.initialize();
   }

   private static void start(String mode) {
      try {
         ScaffoldTrainingRecorder.INSTANCE.start(mode);
      } catch (IOException failure) {
         SkycoreClient.LOGGER.error("Cannot create Scaffold training directory", failure);
         throw new IllegalStateException("Cannot create recording directory: " + failure.getMessage(), failure);
      }
   }

   @Override
   public void execute(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
      var recorder = ScaffoldTrainingRecorder.INSTANCE;
      builder.executes(context -> { recorder.status(); return 1; });
      builder.then(literal("status").executes(context -> { recorder.status(); return 1; }));
      builder.then(literal("start")
         .then(literal("slow").executes(context -> { start("slow"); return 1; }))
         .then(literal("normal").executes(context -> { start("normal"); return 1; })));
      builder.then(literal("stop").executes(context -> { recorder.stop("manual"); return 1; }));
      builder.then(literal("report").executes(context -> { recorder.report(); return 1; }));
      builder.then(literal("apply").executes(context -> { recorder.apply(); return 1; }));
      builder.then(literal("undo").executes(context -> { recorder.undo(); return 1; }));
   }
}
