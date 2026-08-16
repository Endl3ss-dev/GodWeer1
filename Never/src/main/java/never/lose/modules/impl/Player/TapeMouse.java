package never.lose.modules.impl.Player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import never.lose.mixins.accessor.MinecraftClientInvoker;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.ModeSetting;
import never.lose.modules.api.setting.SliderSetting;

@ModuleInfo(name = "TapeMouse", desc = "Автоматически кликает", category = ModuleCategory.PLAYER)
public class TapeMouse extends Module {

    public final BooleanSetting entityRaytrace = new BooleanSetting("Проверка на сущность", false);
    public final ModeSetting attackMode = new ModeSetting("Режим ударов", "По кулдауну", "По кулдауну", "По задержке");
    public final SliderSetting delay = new SliderSetting("Задержка", 1000, 100, 5000, 100)
            .setVisible(() -> attackMode.is("По задержке"));

    private long lastClick;

    @Override
    public void onEnable() {
        lastClick = 0L;
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.interactionManager == null || mc.options == null) {
            return;
        }

        if (entityRaytrace.getValue() && (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY)) {
            return;
        }

        if (attackMode.is("По задержке") && System.currentTimeMillis() - lastClick >= delay.getValue()) {
            performClick(mc);
            lastClick = System.currentTimeMillis();
        }

        if (attackMode.is("По кулдауну") && mc.player.getAttackCooldownProgress(1.0F) >= 1.0F) {
            performClick(mc);
        }
    }

    private void performClick(MinecraftClient mc) {
        if (mc.crosshairTarget instanceof EntityHitResult entityHit) {
            mc.interactionManager.attackEntity(mc.player, entityHit.getEntity());
            mc.player.swingHand(Hand.MAIN_HAND);
            return;
        }
        ((MinecraftClientInvoker) mc).endless$doAttack();
    }
}
