package antileak.base.client.modules.impl.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.text.Text;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventPacket;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.ListSetting;
import antileak.base.client.modules.settings.implement.TextSetting;

import java.lang.reflect.Field;
import java.util.*;

public class AutoEZ extends Module {

    public static final AutoEZ INSTANCE = new AutoEZ();

    private final ListSetting triggerSettings = new ListSetting("Писать при",
            new BooleanSetting("Убийстве", true),
            new BooleanSetting("Тотеме", true)
    );
    public final TextSetting killMessage = new TextSetting("При убийстве", "{name} пососал хуй");
    public final TextSetting totemMessage = new TextSetting("При тотеме", "{name} ливай сучка");
    private final BooleanSetting writeInGlobal = new BooleanSetting("Писать в глобал", true);
    private final BooleanSetting debug = new BooleanSetting("Дебаг (в чат)", false);

    private static Field entityIdField = null;
    private static boolean reflectionInitialized = false;

    private final Random random = new Random();
    private final Map<Integer, Long> attackedPlayers = new HashMap<>();
    private final Map<Integer, Long> alreadyKilled = new HashMap<>();
    private long lastKillMessageTime = 0;
    private long lastTotemMessageTime = 0;

    public AutoEZ() {
        super("AutoEZ", "Пишет в чат при убийстве/тотеме", ModuleCategory.MISC);
        addSettings(triggerSettings, killMessage, totemMessage, writeInGlobal, debug);
    }

    private int getEntityIdFromPacket(PlayerInteractEntityC2SPacket packet) {
        if (!reflectionInitialized) initReflection();
        if (entityIdField != null) {
            try { return (int) entityIdField.get(packet); } catch (Exception ignored) {}
        }
        for (Field f : PlayerInteractEntityC2SPacket.class.getDeclaredFields()) {
            if (f.getType() == int.class) {
                try { f.setAccessible(true); return (int) f.get(packet); } catch (Exception ignored) {}
            }
        }
        return -1;
    }

    private void initReflection() {
        reflectionInitialized = true;
        for (Field f : PlayerInteractEntityC2SPacket.class.getDeclaredFields()) {
            if (f.getType() == int.class) {
                try {
                    f.setAccessible(true);
                    entityIdField = f;
                    debug("§a[AutoEZ] Нашёл поле entityId: " + f.getName());
                    return;
                } catch (Exception ignored) {}
            }
        }
        debug("§c[AutoEZ] Не удалось найти поле entityId!");
    }

    private void debug(String msg) {
        if (debug.isState() && mc.player != null && mc.inGameHud != null) {
            mc.inGameHud.getChatHud().addMessage(Text.literal(msg));
        }
    }

    @EventLink
    public void onSendPacket(EventPacket event) {
        if (event.getType() != EventPacket.Type.SEND) return;
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet) {
            int entityId = getEntityIdFromPacket(packet);
            if (entityId == -1) return;

            Entity entity = mc.world.getEntityById(entityId);
            if (entity instanceof PlayerEntity player && player != mc.player) {
                attackedPlayers.put(entityId, System.currentTimeMillis());
                debug("§7[AutoEZ] Атакован: " + player.getName().getString());
            }
        }
    }

    @EventLink
    public void onReceivePacket(EventPacket event) {
        if (event.getType() != EventPacket.Type.RECEIVE) return;
        if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) return;

        if (event.getPacket() instanceof EntityStatusS2CPacket packet) {
            byte status = packet.getStatus();
            Entity entity = packet.getEntity(mc.world);
            if (entity == null) return;

            long currentTime = System.currentTimeMillis();

            attackedPlayers.entrySet().removeIf(e -> currentTime - e.getValue() > 5000);
            alreadyKilled.entrySet().removeIf(e -> currentTime - e.getValue() > 30000);

            if (status == 3 && entity instanceof PlayerEntity player && triggerSettings.is("Убийстве")) {
                if (player == mc.player) return;
                int entityId = player.getId();
                if (attackedPlayers.containsKey(entityId) && !alreadyKilled.containsKey(entityId) && currentTime - lastKillMessageTime > 3000) {
                    debug("§a[AutoEZ] Убийство: " + player.getName().getString());
                    sendMessage(player.getName().getString(), killMessage.get());
                    alreadyKilled.put(entityId, currentTime);
                    attackedPlayers.remove(entityId);
                    lastKillMessageTime = currentTime;
                }
            }

            if (status == 35 && entity instanceof PlayerEntity player && triggerSettings.is("Тотеме")) {
                if (player == mc.player) return;
                int entityId = player.getId();
                if (attackedPlayers.containsKey(entityId) && currentTime - lastTotemMessageTime > 15000) {
                    debug("§a[AutoEZ] Тотем: " + player.getName().getString());
                    sendMessage(player.getName().getString(), totemMessage.get());
                    lastTotemMessageTime = currentTime;
                }
            }
        }
    }

    private void sendMessage(String playerName, String text) {
        if (text == null || text.trim().isEmpty()) return;
        if (mc.player == null || mc.player.networkHandler == null) return;
        String prefix = writeInGlobal.isState() ? "!" : "";
        String msg = prefix + text.replace("{name}", replaceRandomLetter(playerName));
        mc.player.networkHandler.sendChatMessage(msg);
    }

    private String replaceRandomLetter(String name) {
        Map<Character, Character> letterMap = new HashMap<>();
        letterMap.put('p', 'р'); letterMap.put('a', 'а'); letterMap.put('e', 'е');
        letterMap.put('o', 'о'); letterMap.put('k', 'к'); letterMap.put('c', 'с');
        letterMap.put('x', 'х'); letterMap.put('y', 'у'); letterMap.put('P', 'Р');
        letterMap.put('A', 'А'); letterMap.put('E', 'Е'); letterMap.put('O', 'О');
        letterMap.put('K', 'К'); letterMap.put('T', 'Т'); letterMap.put('H', 'Н');
        letterMap.put('B', 'В'); letterMap.put('M', 'М'); letterMap.put('C', 'С');
        letterMap.put('X', 'Х'); letterMap.put('Y', 'У'); letterMap.put('r', 'г');
        letterMap.put('n', 'п'); letterMap.put('N', 'И'); letterMap.put('m', 'м');
        letterMap.put('b', 'ь'); letterMap.put('w', 'ш'); letterMap.put('W', 'Ш');
        letterMap.put('0', 'O');

        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < name.length(); i++) {
            if (letterMap.containsKey(name.charAt(i))) positions.add(i);
        }
        if (positions.isEmpty()) return name;

        int pos = positions.get(random.nextInt(positions.size()));
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(pos, letterMap.get(name.charAt(pos)));
        return sb.toString();
    }

    @Override
    public void onDisable() {
        attackedPlayers.clear();
        alreadyKilled.clear();
        lastKillMessageTime = 0;
        lastTotemMessageTime = 0;
        super.onDisable();
    }
}
