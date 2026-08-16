package never.lose.modules.impl.Movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BooleanSetting;

@ModuleInfo(name = "Sprint", desc = "Автоматически включает спринт", category = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    public final BooleanSetting ignoreHunger = new BooleanSetting("Игнорировать голод", false);

    private boolean canSprint = true;

    public void setCanSprint(boolean canSprint) {
        this.canSprint = canSprint;
    }

    @Override
    public void onEnable() {
        canSprint = true;
    }

    @Override
    public void onDisable() {
        canSprint = true;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options != null) {
            mc.options.sprintKey.setPressed(false);
        }
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.options == null) {
            return;
        }
        boolean sprintCondition = canSprint && (ignoreHunger.getValue() || player.getHungerManager().getFoodLevel() > 6);
        mc.options.sprintKey.setPressed(sprintCondition);
    }
}
