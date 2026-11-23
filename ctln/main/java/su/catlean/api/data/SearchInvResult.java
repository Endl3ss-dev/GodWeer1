// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.InventoryManager;
import su.catlean.utils.inventory.SearchInvResultCompanion;

public final class SearchInvResult {
   @NotNull
   public static final SearchInvResultCompanion searchInvResultCompanion = new SearchInvResultCompanion(null);
   private final int value;
   private final boolean flag;
   @NotNull
   private final ItemStack stack;
   @NotNull
   private static final SearchInvResult INSTANCE = new SearchInvResult(-1, false, ItemStack.EMPTY);

   public SearchInvResult(int value, boolean flag, ItemStack stack) {
      this.value = value;
      this.flag = flag;
      this.stack = stack;
   }

   public final int getValue() {
      return this.value;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   @NotNull
   public final ItemStack getStack() {
      return this.stack;
   }

   private final boolean isFlag2() {
      return this.value < 9;
   }

   public final void update() {
      if (this.flag && this.isFlag2()) {
         InventoryManager.INSTANCE.applyGetInventory2(this.value);
      }
   }

   public final int getValue2() {
      return this.value;
   }

   public final boolean isFlag3() {
      return this.flag;
   }

   @NotNull
   public final ItemStack getStack2() {
      return this.stack;
   }

   @NotNull
   @Override
   public String toString() {
      ItemStack stack2 = this.stack;
      boolean flag2 = this.flag;
      int value2 = this.value;
      return "SearchInvResult(slot=" + value2 + ", found=" + flag2 + ", stack=" + stack2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Integer.hashCode(this.value);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
      return hashCode * 31 + this.stack.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof SearchInvResult searchInvResult)) {
         return false;
      } else if (this.value != searchInvResult.value) {
         return false;
      } else {
         return this.flag != searchInvResult.flag ? false : Intrinsics.areEqual(this.stack, searchInvResult.stack);
      }
   }

   public static final SearchInvResult getInstance() {
      return INSTANCE;
   }
}