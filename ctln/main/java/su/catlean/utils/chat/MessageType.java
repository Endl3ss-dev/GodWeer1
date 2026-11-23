// deobf by pusia
package su.catlean.utils.chat;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;

public enum MessageType {
   SUCCESS(ChatFormatting.GREEN),
   INFO(ChatFormatting.AQUA),
   WARNING(ChatFormatting.GOLD),
   ERROR(ChatFormatting.RED),
   ENABLED(ChatFormatting.DARK_GREEN),
   DISABLED(ChatFormatting.DARK_RED);

   @NotNull
   private final ChatFormatting chatFormatting;
   private static final MessageType[] messageTypes = $values();
   private static final EnumEntries<MessageType> enumEntries = EnumEntriesKt.enumEntries(messageTypes);

   private MessageType(ChatFormatting chatFormatting) {
      this.chatFormatting = chatFormatting;
   }

   @NotNull
   public final ChatFormatting getChatFormatting() {
      return this.chatFormatting;
   }

   @NotNull
   public static EnumEntries<MessageType> getEnumEntries() {
      return enumEntries;
   }

   private static final MessageType[] $values() {
      return new MessageType[]{SUCCESS, INFO, WARNING, ERROR, ENABLED, DISABLED};
   }
}