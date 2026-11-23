// deobf by pusia
package su.catlean.client.services;

import su.catlean.utils.KotlinCompat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;
import su.catlean.api.event.events.client.InputSuggestorEvent;
import su.catlean.api.event.events.network.SendMessageEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.service.Service;
import su.catlean.client.commands.Command;
import su.catlean.client.commands.impl.AngleCommand;
import su.catlean.client.commands.impl.BindCommand;
import su.catlean.client.commands.impl.ConfigCommand;
import su.catlean.client.commands.impl.EasingsDebugCommand;
import su.catlean.client.commands.impl.FixAllCommand;
import su.catlean.client.commands.impl.FriendCommand;
import su.catlean.client.commands.impl.GpsCommand;
import su.catlean.client.features.modules.esp.Tracers;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class CommandManager implements Service {
   @NotNull
   public static final CommandManager INSTANCE = new CommandManager();
   @NotNull
   private static String text = "^";
   @Nullable
   private static BlockPos blockPos;
   @NotNull
   private static final List<Command> items = new ArrayList();
   @NotNull
   private static final CommandDispatcher commandDispatcher = new CommandDispatcher();

   private CommandManager() {
   }

   @NotNull
   public final String getText() {
      return text;
   }

   @Nullable
   public final BlockPos getBlockPos() {
      return blockPos;
   }

   public final void setBlockPos(BlockPos blockPos) {
      CommandManager.blockPos = blockPos;
   }

   @NotNull
   public final List<Command> getItems() {
      return items;
   }

   @NotNull
   public final CommandDispatcher getCommandDispatcher() {
      return commandDispatcher;
   }

   private final void updateCommand(Command command) {
      this.applyIsEmpty(command);
   }

   public final void applyIsEmpty(Command command) {
      var items2 = items;
      boolean flag;
      if (items2 instanceof Collection && ((Collection)items2).isEmpty()) {
         flag = false;
      } else {
         Iterator<Command> iterator = items2.iterator();

         while (true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Command object = iterator.next();
            Command command2 = (Command)object;
            if (Intrinsics.areEqual(command2.getName(), command.getName())) {
               flag = true;
               break;
            }
         }
      }

      if (!flag) {
         command.applyLiteral(commandDispatcher);
         items.add(command);
      }
   }

   @Flow
   private final void onSendMessage(SendMessageEvent sendMessageEvent) {
      if (KotlinCompat.startsWith(sendMessageEvent.getMessage(), text, false, 2, null)) {
         try {
            commandDispatcher.execute(
               sendMessageEvent.getMessage().substring(text.length()), MinecraftHolder.getMinecraft().getConnection().getSuggestionsProvider()
            );
         } catch (CommandSyntaxException exception) {
         }

         sendMessageEvent.cancel();
      }
   }

   @Flow
   private final void onInputSuggestor(InputSuggestorEvent inputSuggestorEvent) {
      inputSuggestorEvent.setSource(MinecraftHolder.getMinecraft().getConnection().getSuggestionsProvider());
      inputSuggestorEvent.setPrefix(text);
      inputSuggestorEvent.setDispatcher(commandDispatcher);
      inputSuggestorEvent.cancel();
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (blockPos != null) {
         float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F;
         float value = 40.0F;
         PoseStack poseStack = new PoseStack();
         poseStack.pushPose();
         poseStack.translate(guiScaledWidth, value, 0.0F);
         poseStack.mulPose(
            (Quaternionfc)Axis.XP.rotationDegrees(90.0F / Math.abs(90.0F / Math.clamp(55.0F, MinecraftHolder.getLocalPlayer().getXRot(), 90.0F)) - 102)
         );
         poseStack.translate(-guiScaledWidth, -value, 0.0F);
         poseStack.pushPose();
         double amount = Render2D.INSTANCE
            .computeAmount(MinecraftHolder.getLocalPlayer().xo, MinecraftHolder.getLocalPlayer().getX(), (double)Render3D.INSTANCE.getAmount4());
         double amount2 = Render2D.INSTANCE
            .computeAmount(MinecraftHolder.getLocalPlayer().zo, MinecraftHolder.getLocalPlayer().getZ(), (double)Render3D.INSTANCE.getAmount4());
         float wrapDegrees = (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(blockPos.getZ() - amount2, blockPos.getX() - amount)) - 90.0)
            - MinecraftHolder.getLocalPlayer().getYRot();
         poseStack.translate(guiScaledWidth, value, 0.0F);
         poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(wrapDegrees));
         poseStack.translate(-guiScaledWidth, -value, 0.0F);
         Tracers.INSTANCE.render(poseStack, guiScaledWidth, value, 32.0F, ThemeManager.INSTANCE.computeColor(0));
         poseStack.translate(guiScaledWidth, value, 0.0F);
         poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(-wrapDegrees));
         poseStack.translate(-guiScaledWidth, -value, 0.0F);
         poseStack.popPose();
         poseStack.popPose();
         BlockPos blockPos2 = blockPos;
         BlockPos blockPos3 = blockPos;
         String amount3 = (int)WorldUtil.INSTANCE.computeAmount(Vec3iUtil.computeVec3(blockPos2).x, Vec3iUtil.computeVec3(blockPos3).z) + "m";
         float amount4 = Fonts.getFontRenderer().computeAmount(amount3);
         TexturedQuadPass.defaultImpl(
            render2DEvent.getContext().pose(),
            guiScaledWidth - amount4 / 2.0F - 2.0F,
            56.0F,
            amount4 + 4.0F,
            11.0F,
            3.0F,
            1.5F,
            0.0F,
            1.0F,
            ThemeManager.INSTANCE.getColor5(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         Fonts.getFontRenderer().render2(render2DEvent.getContext(), amount3, guiScaledWidth, 60.0F, Color.WHITE);
      }
   }

   static {
      INSTANCE.updateCommand(BindCommand.INSTANCE);
      INSTANCE.updateCommand(FriendCommand.INSTANCE);
      INSTANCE.updateCommand(ConfigCommand.INSTANCE);
      INSTANCE.updateCommand(GpsCommand.INSTANCE);
      INSTANCE.updateCommand(FixAllCommand.INSTANCE);
      INSTANCE.updateCommand(AngleCommand.INSTANCE);
      INSTANCE.updateCommand(EasingsDebugCommand.INSTANCE);
   }
}