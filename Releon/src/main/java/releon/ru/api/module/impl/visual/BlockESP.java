package releon.ru.api.module.impl.visual;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArraySet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.chunk.WorldChunk;
import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.impl.WorldRenderEvent;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.settings.impl.BooleanSetting;
import releon.ru.api.settings.impl.ColorSetting;
import releon.ru.api.settings.impl.NumberSetting;
import releon.ru.utils.render.Render3D;
import releon.ru.utils.repository.blockesp.BlockESPConfig;
import releon.ru.utils.string.chat.ChatMessage;

public final class BlockESP extends Module {
   private static BlockESP instance;
   private final ColorSetting color = this.register(new ColorSetting("Color", "Block highlight color.", new Color(247, 133, 255)));
   private final NumberSetting range = this.register(new NumberSetting("Range", "Search radius for blocks.", 32.0, 1.0, 128.0, 1.0));
   private final BooleanSetting notifyInChat = this.register(new BooleanSetting("Notify", "Show found blocks in chat.", false));
   private final Set<String> blocksToHighlight = new CopyOnWriteArraySet<>();
   private final Set<String> cachedBlockNames = new HashSet<>();
   private final Set<Block> cachedBlocks = new HashSet<>();
   private final Map<BlockPos, BlockState> renderBlocks = new HashMap<>();
   private final Set<BlockPos> notifiedBlocks = new CopyOnWriteArraySet<>();
   private long lastScanTime;
   private int checkCounter;

   public BlockESP() {
      super("Block ESP", "Highlights selected blocks in the world.", ModuleCategory.VISUAL);
      instance = this;
   }

   public static BlockESP getInstance() {
      return instance;
   }

   public Set<String> getBlocksToHighlight() {
      return this.blocksToHighlight;
   }

   @Override
   protected void onEnable() {
      this.blocksToHighlight.clear();
      this.blocksToHighlight.addAll(BlockESPConfig.getInstance().getBlocks());
      this.refreshBlockCache();
      this.renderBlocks.clear();
      this.notifiedBlocks.clear();
      this.cachedBlockNames.clear();
      this.cachedBlocks.clear();
      this.lastScanTime = 0L;
      this.checkCounter = 0;
   }

   @Override
   protected void onDisable() {
      this.renderBlocks.clear();
      this.notifiedBlocks.clear();
   }

   @Override
   public void onTick(MinecraftClient client) {
      if (client != null && client.world != null && client.player != null) {
         this.refreshBlockCache();
         if (this.blocksToHighlight.isEmpty()) {
            this.renderBlocks.clear();
         } else {
            BlockPos playerPos = client.player.getBlockPos();
            long currentTime = System.nanoTime() / 1000000L;
            if (currentTime - this.lastScanTime >= 2000L) {
               this.scanChunkArea(playerPos, 2, 48, true);
               this.lastScanTime = currentTime;
               this.checkCounter = 0;
            }

            if (this.checkCounter % 5 == 0) {
               this.scanChunkArea(playerPos, 1, 24, false);
            }

            if (this.checkCounter % 60 == 0) {
               this.renderBlocks.entrySet().removeIf(entry -> {
                  BlockPos pos = entry.getKey();
                  Block block = client.world.getBlockState(pos).getBlock();
                  boolean shouldRemove = !this.cachedBlocks.contains(block);
                  if (shouldRemove) {
                     this.notifiedBlocks.remove(pos);
                  }

                  return shouldRemove;
               });
            }

            this.checkCounter++;
         }
      } else {
         this.renderBlocks.clear();
      }
   }

   @SubscribeEvent
   private void onRender3D(WorldRenderEvent event) {
      if (!this.isEnabled() || mc.world == null || mc.player == null) {
         this.renderBlocks.clear();
      } else if (this.blocksToHighlight.isEmpty()) {
         this.renderBlocks.clear();
      } else {
         this.renderHighlightedBlocks(this.color.getValue().getRGB());
      }
   }

   private void renderHighlightedBlocks(int highlightColor) {
      for (Entry<BlockPos, BlockState> entry : this.renderBlocks.entrySet()) {
         BlockPos pos = entry.getKey();
         BlockState state = entry.getValue();
         VoxelShape shape = state.getOutlineShape(mc.world, pos);
         if (shape.isEmpty()) {
            shape = state.getCollisionShape(mc.world, pos);
         }

         if (shape.isEmpty()) {
            Box box = new Box(pos);
            Render3D.drawBox(box, highlightColor, 1.0F);
            Render3D.drawBoxOverlay(box, highlightColor, 1.25F);
         } else if (shape == VoxelShapes.fullCube()) {
            Box box = new Box(pos);
            Render3D.drawBox(box, highlightColor, 1.0F);
            Render3D.drawBoxOverlay(box, highlightColor, 1.25F);
         } else {
            Render3D.drawShapeAlternative(pos, shape, highlightColor, 1.0F, true, false);
            Render3D.drawShapeOverlay(pos, shape, highlightColor, 1.25F);
         }
      }
   }

   private void scanChunkArea(BlockPos playerPos, int chunkRange, int yRange, boolean fullRefresh) {
      if (mc.world != null && mc.player != null) {
         if (fullRefresh) {
            this.renderBlocks.clear();
         }

         double maxDistanceSqr = (double)this.range.getFloat() * (double)this.range.getFloat();
         int minYBase = mc.world.getBottomY();
         Mutable mutablePos = new Mutable();

         for (int x = -chunkRange; x <= chunkRange; x++) {
            for (int z = -chunkRange; z <= chunkRange; z++) {
               int chunkX = (playerPos.getX() >> 4) + x;
               int chunkZ = (playerPos.getZ() >> 4) + z;
               if (mc.world.getChunkManager().isChunkLoaded(chunkX, chunkZ)) {
                  WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(chunkX, chunkZ);
                  if (chunk != null) {
                     int cx = chunk.getPos().x << 4;
                     int cz = chunk.getPos().z << 4;

                     for (int bx = 0; bx < 16; bx++) {
                        for (int bz = 0; bz < 16; bz++) {
                           int columnX = cx + bx;
                           int columnZ = cz + bz;
                           int minY = Math.max(minYBase, playerPos.getY() - yRange);
                           int maxY = Math.min(mc.world.getTopY(Type.WORLD_SURFACE, columnX, columnZ), playerPos.getY() + yRange);

                           for (int by = minY; by <= maxY; by++) {
                              mutablePos.set(columnX, by, columnZ);
                              double dist = mc.player.squaredDistanceTo((double)columnX + 0.5, (double)by + 0.5, (double)columnZ + 0.5);
                              if (!(dist > maxDistanceSqr)) {
                                 BlockState state = mc.world.getBlockState(mutablePos);
                                 Block block = state.getBlock();
                                 if (this.cachedBlocks.contains(block) && !this.renderBlocks.containsKey(mutablePos)) {
                                    BlockPos immutablePos = mutablePos.toImmutable();
                                    this.renderBlocks.put(immutablePos, state);
                                    if (this.notifyInChat.getValue() && !this.notifiedBlocks.contains(mutablePos)) {
                                       String blockName = Registries.BLOCK.getId(block).toString();
                                       this.notifyBlockFound(immutablePos, blockName);
                                       this.notifiedBlocks.add(immutablePos);
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void refreshBlockCache() {
      if (!this.cachedBlockNames.equals(this.blocksToHighlight)) {
         this.cachedBlockNames.clear();
         this.cachedBlockNames.addAll(this.blocksToHighlight);
         this.cachedBlocks.clear();

         for (String blockName : this.cachedBlockNames) {
            Identifier identifier = Identifier.tryParse(blockName);
            if (identifier != null) {
               Block block = (Block)Registries.BLOCK.get(identifier);
               if (block != null) {
                  this.cachedBlocks.add(block);
               }
            }
         }
      }
   }

   private void notifyBlockFound(BlockPos pos, String blockName) {
      if (mc.player != null) {
         mc.player
            .sendMessage(
               ChatMessage.blockesp().copy().append(Text.literal(" -> " + blockName + " @ " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())), false
            );
      }
   }
}
