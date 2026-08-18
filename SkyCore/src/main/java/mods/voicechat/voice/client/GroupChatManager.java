package mods.voicechat.voice.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import mods.voicechat.Voicechat;
import mods.voicechat.VoicechatClient;
import mods.voicechat.voice.common.PlayerState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class GroupChatManager {

    private static final ResourceLocation TALK_OUTLINE = new ResourceLocation(Voicechat.MODID + "/textures/icons/talk_outline.png");
    private static final ResourceLocation SPEAKER_OFF_ICON = new ResourceLocation(Voicechat.MODID + "/textures/icons/speaker_small_off.png");

    public static void renderIcons(MatrixStack matrixStack) {
        ClientVoicechat client = ClientManager.getClient();

        if (client == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();

        List<PlayerState> groupMembers = getGroupMembers(VoicechatClient.CLIENT_CONFIG.showOwnGroupIcon.get());

        matrixStack.push();
        int posX = VoicechatClient.CLIENT_CONFIG.groupPlayerIconPosX.get();
        int posY = VoicechatClient.CLIENT_CONFIG.groupPlayerIconPosY.get();
        if (posX < 0) {
            matrixStack.translate(mc.getMainWindow().getScaledWidth(), 0D, 0D);
        }
        if (posY < 0) {
            matrixStack.translate(0D, mc.getMainWindow().getScaledHeight(), 0D);
        }
        matrixStack.translate(posX, posY, 0D);

        float scale = VoicechatClient.CLIENT_CONFIG.groupHudIconScale.get().floatValue();
        matrixStack.scale(scale, scale, 1F);

        boolean vertical = VoicechatClient.CLIENT_CONFIG.groupPlayerIconOrientation.get().equals(GroupPlayerIconOrientation.VERTICAL);

        for (int i = 0; i < groupMembers.size(); i++) {
            PlayerState state = groupMembers.get(i);
            matrixStack.push();
            if (vertical) {
                if (posY < 0) {
                    matrixStack.translate(0D, i * -11D, 0D);
                } else {
                    matrixStack.translate(0D, i * 11D, 0D);
                }
            } else {
                if (posX < 0) {
                    matrixStack.translate(i * -11D, 0D, 0D);
                } else {
                    matrixStack.translate(i * 11D, 0D, 0D);
                }
            }

            matrixStack.pop();
        }

        matrixStack.pop();
    }

    public static List<PlayerState> getGroupMembers() {
        return getGroupMembers(true);
    }

    public static List<PlayerState> getGroupMembers(boolean includeSelf) {
        List<PlayerState> entries = new ArrayList<>();
        UUID group = ClientManager.getPlayerStateManager().getGroupID();

        if (group == null) {
            return entries;
        }

        for (PlayerState state : ClientManager.getPlayerStateManager().getPlayerStates(includeSelf)) {
            if (state.hasGroup() && state.getGroup().equals(group)) {
                entries.add(state);
            }
        }

        entries.sort(Comparator.comparing(PlayerState::getName));

        return entries;
    }

}
