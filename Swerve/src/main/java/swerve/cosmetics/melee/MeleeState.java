package swerve.cosmetics.melee;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;

import swerve.cosmetics.CosmeticStore;
import swerve.cosmetics.Wearables;

/**
 * Состояние ножей: какой скин надет, вид от первого/третьего лица и масштаб в третьем.
 *
 * <p>Свой скин показывается другим клиентам Swerve через строку чата {@code SWCOS}
 * (пятое число — идентификатор скина).
 */
public final class MeleeState {

    /** Минимальный и максимальный размер ножа в третьем лице. */
    public static final float SCALE_MIN = 0.8F;
    public static final float SCALE_MAX = 2.5F;

    /** Скины, надетые на других игроков: UUID -> идентификатор скина. */
    private static final Map<UUID, Integer> REMOTE = new HashMap<>();

    private static String equippedKey;
    private static boolean firstPerson = true;
    private static boolean thirdPerson = true;
    private static float thirdScale = 1.6F;

    private MeleeState() {
    }

    // ---- что надето

    public static MeleeDesign equipped() {
        return MeleeCatalog.byKey(equippedKey);
    }

    public static String equippedKey() {
        return equippedKey;
    }

    /** Надеть скин (null или пустая строка — снять). */
    public static void equip(String key) {
        String next = key == null || key.isEmpty() ? null : key;
        if (equippedKey == null ? next == null : equippedKey.equals(next)) {
            return;
        }
        equippedKey = next;
        MeleeAnimator.resetAll();
        Wearables.touch();
        CosmeticStore.save();
    }

    public static void unequip() {
        equip(null);
    }

    public static boolean firstPerson() {
        return firstPerson;
    }

    public static void firstPerson(boolean on) {
        if (firstPerson != on) {
            firstPerson = on;
            CosmeticStore.save();
        }
    }

    public static boolean thirdPerson() {
        return thirdPerson;
    }

    public static void thirdPerson(boolean on) {
        if (thirdPerson != on) {
            thirdPerson = on;
            CosmeticStore.save();
        }
    }

    public static float thirdScale() {
        return thirdScale;
    }

    public static void thirdScale(float value) {
        float v = Math.max(SCALE_MIN, Math.min(SCALE_MAX, value));
        if (Math.abs(thirdScale - v) > 0.001F) {
            thirdScale = v;
            CosmeticStore.save();
        }
    }

    // ---- синхронизация с другими клиентами

    /** Идентификатор своего скина для служебной строки чата (0 — ножа нет). */
    public static int equippedId() {
        MeleeDesign design = equipped();
        return design == null ? 0 : design.getId();
    }

    public static void applyRemote(UUID id, int meleeId) {
        REMOTE.put(id, meleeId);
    }

    public static int remoteId(UUID id) {
        Integer value = REMOTE.get(id);
        return value == null ? 0 : value;
    }

    public static void forget(UUID id) {
        REMOTE.remove(id);
    }

    /** Скин игрока: свой или присланный другим клиентом. */
    public static MeleeDesign designOf(Player player) {
        if (player == null) {
            return null;
        }
        if (player == Minecraft.getInstance().player) {
            return equipped();
        }
        return MeleeCatalog.byId(remoteId(player.getUUID()));
    }

    // ---- как ведёт себя нож

    /** Под нож подходит только меч. */
    public static boolean applies(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(ItemTags.SWORDS);
    }

    public static boolean active() {
        return equipped() != null;
    }

    private static LocalPlayer player() {
        return Minecraft.getInstance().player;
    }

    /** В руке игрока сейчас меч — нож его заменяет. */
    public static boolean holdsSkinned(InteractionHand hand) {
        LocalPlayer p = player();
        return p != null && applies(p.getItemInHand(hand));
    }

    public static boolean handlesAnyHand() {
        return active() && (holdsSkinned(InteractionHand.MAIN_HAND) || holdsSkinned(InteractionHand.OFF_HAND));
    }

    public static boolean rendersFirstPerson(ItemStack stack) {
        return firstPerson && active() && applies(stack);
    }

    public static boolean rendersThirdPerson(ItemStack stack) {
        return thirdPerson && active() && applies(stack);
    }

    /** Пустая основная рука прячется, если нож рисуется в другой. */
    public static boolean hidesEmptyMainHand(Player p, InteractionHand hand, ItemStack stack) {
        return p != null
                && hand == InteractionHand.MAIN_HAND
                && stack != null
                && stack.isEmpty()
                && rendersFirstPerson(p.getOffhandItem());
    }

    /** Щит, лук, еда — у них своя поза руки, нож их не трогает. */
    private static boolean keepsVanillaPose(ItemStack stack) {
        return stack != null && stack.getUseAnimation() != ItemUseAnimation.NONE;
    }

    private static boolean usesHand(Player p, InteractionHand hand) {
        return p != null && p.isUsingItem() && p.getUsedItemHand() == hand;
    }

    /** Предмет из другой руки прикрепляется к кисти ножа. */
    public static boolean attachesHandItem(Player p, InteractionHand hand, ItemStack stack) {
        if (!firstPerson
                || p == null
                || stack == null
                || stack.isEmpty()
                || keepsVanillaPose(stack)
                || stack.has(DataComponents.MAP_ID)
                || usesHand(p, hand)) {
            return false;
        }
        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        return holdsSkinned(other);
    }

    /** Тик: анимация ножа играет, только когда нож действительно рисуется. */
    public static void tick() {
        boolean first = firstPerson && handlesAnyHand();
        boolean third = thirdPerson && handlesAnyHand();
        if (!first && !third) {
            MeleeAnimator.getInstance().stop();
            MeleeAnimator.offHand().stop();
            return;
        }
        if (!MeleeAnimator.getInstance().consumeRendered()) {
            MeleeAnimator.getInstance().stop();
        }
        if (!MeleeAnimator.offHand().consumeRendered()) {
            MeleeAnimator.offHand().stop();
        }
    }
}
