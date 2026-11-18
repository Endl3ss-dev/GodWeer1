package net.skycore.client.integration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.skycore.client.util.SendDonationHelper;

/** Local grouping and markers for players already visible in the current world. */
public final class PartyClient {
    private static final Map<String, String> MEMBER_NAMES = new LinkedHashMap<>();
    private static final Map<String, PartyMember> MEMBERS = new ConcurrentHashMap<>();
    private static volatile String partyCode = "";
    private static volatile String partyName = "";
    private static volatile String partyLeader = "";

    private PartyClient() {}

    public static void init() {}

    public static synchronized void create(String name) {
        if (hasParty()) {
            partySystem("Локальная группа уже создана.");
            return;
        }
        if (name == null || name.isBlank()) {
            partySystem("Укажи название локальной группы.");
            return;
        }
        partyName = name.trim();
        partyCode = "local-" + UUID.randomUUID().toString().substring(0, 8);
        partyLeader = PartyIdentity.resolveUsername();
        if (partyLeader.isBlank()) {
            partyLeader = "Player";
        }
        MEMBER_NAMES.clear();
        MEMBER_NAMES.put(partyLeader.toLowerCase(Locale.ROOT), partyLeader);
        partySystem("Создана локальная группа: " + partyName);
    }

    public static synchronized void invite(String name) {
        if (!hasParty()) {
            partySystem("Сначала создай локальную группу.");
            return;
        }
        if (name == null || name.isBlank()) {
            return;
        }
        String member = name.trim();
        MEMBER_NAMES.put(member.toLowerCase(Locale.ROOT), member);
        partySystem("Добавлен в локальную группу: " + member);
    }

    public static void accept() {
        partySystem("Входящие приглашения отключены в офлайн-сборке.");
    }

    public static void decline() {
        partySystem("Входящих приглашений нет.");
    }

    public static synchronized void kick(String name) {
        if (!hasParty() || name == null || name.isBlank()) {
            return;
        }
        String key = name.trim().toLowerCase(Locale.ROOT);
        if (key.equals(partyLeader.toLowerCase(Locale.ROOT))) {
            partySystem("Для удаления своей группы используй .party disband.");
            return;
        }
        if (MEMBER_NAMES.remove(key) != null) {
            MEMBERS.remove(key);
            partySystem("Удалён из локальной группы: " + name.trim());
        }
    }

    public static void sendChat(String message) {
        if (!hasParty()) {
            partySystem("Сначала создай локальную группу.");
        } else if (message != null && !message.isBlank()) {
            partySystem("[Локально] " + PartyIdentity.resolveUsername() + ": " + message.trim());
        }
    }

    public static synchronized void leave() {
        boolean hadParty = hasParty();
        clearLocalParty();
        if (hadParty) {
            partySystem("Локальная группа закрыта.");
        }
    }

    public static void disband() {
        leave();
    }

    public static synchronized void leaveOnExit() {
        clearLocalParty();
    }

    public static boolean hasParty() {
        return !partyCode.isEmpty();
    }

    public static boolean isLeader() {
        return hasParty();
    }

    public static String getPartyCode() {
        return partyCode;
    }

    public static String getPartyName() {
        return partyName;
    }

    public static String getLeader() {
        return partyLeader;
    }

    public static synchronized List<String> getMemberNames() {
        return List.copyOf(MEMBER_NAMES.values());
    }

    public static boolean hasPendingInvite() {
        return false;
    }

    public static String getPendingInviteFrom() {
        return "";
    }

    public static String getPendingInvitePartyName() {
        return "";
    }

    public static synchronized boolean isMetkaEnabled() {
        return hasParty() && MEMBER_NAMES.size() > 1;
    }

    public static List<PartyMember> getMembersForRender() {
        Minecraft client = Minecraft.getInstance();
        if (!isMetkaEnabled() || client == null || client.level == null) {
            MEMBERS.clear();
            return List.of();
        }
        List<String> names = getMemberNames().stream().map(name -> name.toLowerCase(Locale.ROOT)).toList();
        List<String> visible = new ArrayList<>();
        List<PartyMember> result = new ArrayList<>();
        for (var player : client.level.players()) {
            if (player == client.player) {
                continue;
            }
            String name = player.getGameProfile().name();
            String key = name.toLowerCase(Locale.ROOT);
            if (names.contains(key)) {
                PartyMember member = MEMBERS.computeIfAbsent(key, ignored -> new PartyMember(name));
                // The existing data holder also supports positions observed locally.
                member.applyNetwork(player.getX(), player.getY(), player.getZ(), player.getHealth(), true);
                visible.add(key);
                result.add(member);
            }
        }
        MEMBERS.keySet().retainAll(visible);
        return List.copyOf(result);
    }

    private static void clearLocalParty() {
        partyCode = "";
        partyName = "";
        partyLeader = "";
        MEMBER_NAMES.clear();
        MEMBERS.clear();
    }

    private static void partySystem(String text) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return;
        }
        Runnable message = () -> SendDonationHelper.sendParty(
                Component.literal(text).withStyle(ChatFormatting.GRAY));
        if (client.isSameThread()) {
            message.run();
        } else {
            client.execute(message);
        }
    }
}
