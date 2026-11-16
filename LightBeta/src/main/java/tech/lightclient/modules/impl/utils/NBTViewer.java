package tech.lightclient.modules.impl.utils;

import tech.lightclient.client.events.impl.client.EventKey;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

@FunctionAdd(name = "NBTViewer", alias = "NBT Viewer", category = Category.Utilities, description = "Копирует компоненты предмета из основной руки")
public class NBTViewer extends Function {
    private final BindSetting copyBind = new BindSetting("Копировать NBT", -1);

    public NBTViewer() {
        addSettings(copyBind);
    }

    @EventHandler
    public void onKey(EventKey event) {
        if (!event.isKeyDown(copyBind.get()) || nullCheck()) return;

        ItemStack stack = mc.player.getMainHandStack();
        if (stack.isEmpty()) {
            sendMessage("В основной руке нет предмета.");
            return;
        }

        MinecraftClient.getInstance().keyboard.setClipboard(stack.getComponents().toString());
        sendMessage("NBT скопирован: " + stack.getName().getString());
    }
}
