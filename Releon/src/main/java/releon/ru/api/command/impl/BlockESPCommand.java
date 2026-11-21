package releon.ru.api.command.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.RunCommand;
import net.minecraft.text.HoverEvent.ShowText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import releon.ru.api.command.Command;
import releon.ru.api.command.CommandManager;
import releon.ru.api.command.helpers.Paginator;
import releon.ru.api.command.helpers.TabCompleteHelper;
import releon.ru.api.module.impl.visual.BlockESP;
import releon.ru.utils.repository.blockesp.BlockESPConfig;

public final class BlockESPCommand extends Command {
   public BlockESPCommand() {
      super("blockesp", "Manage the Block ESP block list", "besp");
   }

   @Override
   public void execute(String label, String[] args) {
      CommandManager manager = CommandManager.getInstance();
      BlockESPConfig config = BlockESPConfig.getInstance();
      String action = args.length > 0 ? args[0].toLowerCase(Locale.US) : "help";
      switch (action) {
         case "add":
            this.addBlock(manager, config, args);
            break;
         case "remove":
         case "del":
         case "delete":
            this.removeBlock(manager, config, args);
            break;
         case "clear":
            this.clearBlocks(config);
            break;
         case "list":
            this.listBlocks(manager, label, args);
            break;
         case "blocks":
         case "allblocks":
            this.listAllBlocks(manager, label, args);
            break;
         default:
            this.printHelp();
      }
   }

   @Override
   public Stream<String> tabComplete(String label, String[] args) {
      if (args.length == 1) {
         return new TabCompleteHelper().append("add", "remove", "del", "delete", "list", "clear", "blocks", "allblocks").filterPrefix(args[0]).stream();
      } else {
         if (args.length == 2) {
            String action = args[0].toLowerCase(Locale.US);
            if (action.equals("add")) {
               return Registries.BLOCK
                  .getIds()
                  .stream()
                  .<String>map(Identifier::toString)
                  .filter(name -> name.toLowerCase(Locale.US).contains(args[1].toLowerCase(Locale.US)))
                  .limit(50L);
            }

            if (action.equals("remove") || action.equals("del") || action.equals("delete")) {
               return new TabCompleteHelper().append(BlockESPConfig.getInstance().getBlockList().toArray(new String[0])).filterPrefix(args[1]).stream();
            }
         }

         return Stream.empty();
      }
   }

   @Override
   public List<String> getLongDesc() {
      return Arrays.asList(
         "Manage the block list used by Block ESP.",
         "Usage:",
         "> blockesp add <block>",
         "> blockesp remove <block>",
         "> blockesp list",
         "> blockesp clear",
         "> blockesp blocks"
      );
   }

   private void addBlock(CommandManager manager, BlockESPConfig config, String[] args) {
      if (args.length < 2) {
         this.logDirect("Usage: blockesp add <block>", Formatting.RED);
      } else {
         String blockId = this.normalizeBlockId(args[1]);
         Identifier identifier = Identifier.tryParse(blockId);
         if (identifier == null) {
            this.logDirect("Invalid block id: " + args[1], Formatting.RED);
         } else {
            Block block = (Block)Registries.BLOCK.get(identifier);
            if (block != null && block != Blocks.AIR) {
               String registryName = Registries.BLOCK.getId(block).toString();
               if (config.hasBlock(registryName)) {
                  this.logDirect("Block already in the list: " + registryName, Formatting.RED);
               } else {
                  config.addBlockAndSave(registryName);
                  this.syncModule();
                  this.logDirect("Block added: " + registryName, Formatting.GREEN);
               }
            } else {
               this.logDirect("Block not found: " + args[1], Formatting.RED);
            }
         }
      }
   }

   private void removeBlock(CommandManager manager, BlockESPConfig config, String[] args) {
      if (args.length < 2) {
         this.logDirect("Usage: blockesp remove <block>", Formatting.RED);
      } else {
         String blockId = this.normalizeBlockId(args[1]);
         Identifier identifier = Identifier.tryParse(blockId);
         String registryName = blockId;
         if (identifier != null) {
            Block block = (Block)Registries.BLOCK.get(identifier);
            if (block != null) {
               registryName = Registries.BLOCK.getId(block).toString();
            }
         }

         if (!config.hasBlock(registryName)) {
            this.logDirect("Block not found in list: " + registryName, Formatting.RED);
         } else {
            config.removeBlockAndSave(registryName);
            this.syncModule();
            this.logDirect("Block removed: " + registryName, Formatting.GREEN);
         }
      }
   }

   private void clearBlocks(BlockESPConfig config) {
      int count = config.size();
      config.clearAndSave();
      this.syncModule();
      this.logDirect("BlockESP list cleared. Removed: " + count, Formatting.GREEN);
   }

   private void listBlocks(CommandManager manager, String label, String[] args) {
      int page = this.parsePage(args);
      List<String> blocks = BlockESPConfig.getInstance().getBlockList();
      if (blocks.isEmpty()) {
         this.logDirect("BlockESP list is empty.", Formatting.RED);
      } else {
         Paginator<String> paginator = new Paginator<>(blocks);
         paginator.setPage(page);
         paginator.display(() -> {
            this.logDirectRaw(Text.literal(HelpCommand.getLine()));
            this.logDirect("Block ESP list (" + blocks.size() + ")");
            this.logDirectRaw(Text.literal(HelpCommand.getLine()));
         }, blockName -> this.createBlockListRow(manager, blockName), manager.getPrefix() + label + " list");
      }
   }

   private void listAllBlocks(CommandManager manager, String label, String[] args) {
      int page = this.parsePage(args);
      List<String> allBlocks = Registries.BLOCK.getIds().stream().<String>map(Identifier::toString).sorted().toList();
      Paginator<String> paginator = new Paginator<>(allBlocks, 15);
      paginator.setPage(page);
      paginator.display(() -> {
         this.logDirectRaw(Text.literal(HelpCommand.getLine()));
         this.logDirect("All blocks (" + allBlocks.size() + ")");
         this.logDirectRaw(Text.literal(HelpCommand.getLine()));
      }, blockName -> this.createAllBlocksRow(manager, blockName), manager.getPrefix() + label + " blocks");
   }

   private MutableText createBlockListRow(CommandManager manager, String blockName) {
      String shortName = blockName.replace("minecraft:", "");
      MutableText component = Text.literal("  * " + shortName).append(Text.literal(" (" + blockName + ")"));
      MutableText hoverText = Text.literal("Click to remove " + shortName);
      component.setStyle(
         component.getStyle().withHoverEvent(new ShowText(hoverText)).withClickEvent(new RunCommand(manager.getPrefix() + "blockesp remove " + blockName))
      );
      return component;
   }

   private MutableText createAllBlocksRow(CommandManager manager, String blockName) {
      boolean inList = BlockESPConfig.getInstance().hasBlock(blockName);
      String prefix = inList ? "[x]" : "[ ]";
      MutableText component = Text.literal("  " + prefix + " " + blockName.replace("minecraft:", ""));
      String command = inList ? manager.getPrefix() + "blockesp remove " + blockName : manager.getPrefix() + "blockesp add " + blockName;
      MutableText hoverText = Text.literal(inList ? "Click to remove" : "Click to add");
      component.setStyle(component.getStyle().withHoverEvent(new ShowText(hoverText)).withClickEvent(new RunCommand(command)));
      return component;
   }

   private void printHelp() {
      this.logDirectRaw(Text.literal(HelpCommand.getLine()));
      this.logDirect("BLOCK ESP");
      this.logDirectRaw(Text.literal(HelpCommand.getLine()));
      this.logDirect("> blockesp add <block> - add block");
      this.logDirect("> blockesp remove <block> - remove block");
      this.logDirect("> blockesp list - show saved blocks");
      this.logDirect("> blockesp clear - clear the list");
      this.logDirect("> blockesp blocks - show all blocks");
      this.logDirectRaw(Text.literal(HelpCommand.getLine()));
   }

   private void syncModule() {
      BlockESP module = BlockESP.getInstance();
      if (module != null) {
         module.getBlocksToHighlight().clear();
         module.getBlocksToHighlight().addAll(BlockESPConfig.getInstance().getBlocks());
      }
   }

   private int parsePage(String[] args) {
      if (args.length > 1) {
         try {
            return Math.max(1, Integer.parseInt(args[1]));
         } catch (NumberFormatException var3) {
         }
      }

      return 1;
   }

   private String normalizeBlockId(String raw) {
      String blockId = raw.toLowerCase(Locale.US);
      if (!blockId.contains(":")) {
         blockId = "minecraft:" + blockId;
      }

      return blockId;
   }
}
