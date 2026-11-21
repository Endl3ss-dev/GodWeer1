package releon.ru.utils.repository.blockesp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import releon.ru.utils.repository.RepositoryStorage;

public final class BlockESPConfig {
   private static final String FILE_NAME = "blockesp";
   private static final String KEY = "blocks";
   private static BlockESPConfig instance;
   private final Set<String> blocks = new CopyOnWriteArraySet<>();

   private BlockESPConfig() {
   }

   public static BlockESPConfig getInstance() {
      if (instance == null) {
         instance = new BlockESPConfig();
      }

      return instance;
   }

   public Set<String> getBlocks() {
      return this.blocks;
   }

   public boolean hasBlock(String block) {
      return this.blocks.contains(block);
   }

   public void addBlock(String block) {
      if (block != null && !block.isBlank()) {
         this.blocks.add(block);
      }
   }

   public void addBlockAndSave(String block) {
      this.addBlock(block);
      this.save();
   }

   public boolean removeBlock(String block) {
      return this.blocks.remove(block);
   }

   public boolean removeBlockAndSave(String block) {
      boolean removed = this.removeBlock(block);
      if (removed) {
         this.save();
      }

      return removed;
   }

   public void clear() {
      this.blocks.clear();
   }

   public void clearAndSave() {
      this.clear();
      this.save();
   }

   public int size() {
      return this.blocks.size();
   }

   public List<String> getBlockList() {
      return new ArrayList<>(this.blocks);
   }

   public void load() {
      this.blocks.clear();
      this.blocks.addAll(RepositoryStorage.loadStringList("blockesp", "blocks"));
   }

   public void save() {
      RepositoryStorage.saveStringList("blockesp", "blocks", this.getBlockList());
   }
}
