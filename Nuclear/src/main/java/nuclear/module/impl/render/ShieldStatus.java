package nuclear.module.impl.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventAttack;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.ColorSetting;
import nuclear.utils.font.Fonts;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.ProjectUtil;
import nuclear.utils.render.RenderUtils;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@ModuleDescriptor(name = "ShieldStatus", type = ModuleCategory.Render, desc = "Обозначает сломанные и не сломанные щиты")
public class ShieldStatus extends Module {
    private static final long BROKEN_TIME_MS = 5_000L;

    private final Map<UUID, Long> brokenShields = new HashMap<>();
    private final ColorSetting readyColor = new ColorSetting("Цвет целого щита", ColorUtils.rgba(95, 220, 125, 255));
    private final ColorSetting brokenColor = new ColorSetting("Цвет сломанного щита", ColorUtils.rgba(255, 90, 90, 255));

    public ShieldStatus() {
        addSettings(readyColor, brokenColor);
    }

    @Override
    public boolean onEvent(Event event) {
        if (event instanceof EventAttack attack) {
            trackShieldBreak(attack.getTarget());
            return false;
        }

        if (event instanceof EventRender render && render.isRender2D() && mc.player != null && mc.world != null) {
            pruneExpired();
            renderShieldStatus(render.matrixStack);
        }
        return false;
    }

    private void trackShieldBreak(Entity target) {
        if (!(target instanceof PlayerEntity player) || mc.player == null) {
            return;
        }

        ItemStack weapon = mc.player.getHeldItemMainhand();
        if (!(weapon.getItem() instanceof AxeItem)) {
            return;
        }

        if (player.isActiveItemStackBlocking() || isHoldingShield(player)) {
            brokenShields.put(player.getUniqueID(), System.currentTimeMillis() + BROKEN_TIME_MS);
        }
    }

    private void renderShieldStatus(MatrixStack matrixStack) {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive() || mc.player.getDistanceSq(player) > 4096.0) {
                continue;
            }

            Long brokenUntil = brokenShields.get(player.getUniqueID());
            boolean broken = brokenUntil != null && brokenUntil > System.currentTimeMillis();
            ItemStack shield = getShield(player);
            if (!broken && shield.isEmpty()) {
                continue;
            }

            Vector2f screen = ProjectUtil.project2D(player.getPosX(), player.getPosY() + player.getHeight() + 0.35, player.getPosZ());
            if (screen.x == Float.MAX_VALUE || screen.y == Float.MAX_VALUE) {
                continue;
            }

            String text = broken ? "Shield broken" : buildShieldText(shield);
            int color = broken ? brokenColor.get() : readyColor.get();
            float width = Fonts.newcode[13].getWidth(text);

            RenderUtils.Render2D.drawRoundedRect(screen.x - width / 2.0f - 3.0f, screen.y - 2.0f, width + 6.0f, Fonts.newcode[13].getFontHeight() + 3.0f, 1.0f, ColorUtils.rgba(10, 10, 10, 150));
            Fonts.newcode[13].drawCenteredString(matrixStack, text, screen.x, screen.y, color);
        }
    }

    private String buildShieldText(ItemStack shield) {
        if (shield.isEmpty()) {
            return "Shield ready";
        }

        int remainingDurability = Math.max(0, shield.getMaxDamage() - shield.getDamage());
        return "Shield " + remainingDurability;
    }

    private ItemStack getShield(PlayerEntity player) {
        if (player.getHeldItemOffhand().getItem() == Items.SHIELD) {
            return player.getHeldItemOffhand();
        }
        if (player.getHeldItemMainhand().getItem() == Items.SHIELD) {
            return player.getHeldItemMainhand();
        }
        return ItemStack.EMPTY;
    }

    private boolean isHoldingShield(PlayerEntity player) {
        return !getShield(player).isEmpty();
    }

    private void pruneExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, Long>> iterator = brokenShields.entrySet().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getValue() <= now) {
                iterator.remove();
            }
        }
    }
}
