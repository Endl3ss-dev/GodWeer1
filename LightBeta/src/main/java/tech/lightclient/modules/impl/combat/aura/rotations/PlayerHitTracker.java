package tech.lightclient.modules.impl.combat.aura.rotations;

/**
 * Отслеживает СОБСТВЕННЫЕ удары локального игрока по конкретной цели.
 *
 * Нужен записчикам датасета (lneuro/neuro), чтобы засчитывать удар только если:
 *   1) удар нанёс сам игрок (а не другой игрок рядом), и
 *   2) удар не был миссом (сервер подтвердил урон ростом hurtTime у той же
 *      цели, по которой был удар; для клиентского FakePlayer подтверждение
 *      делается по факту атаки, т.к. сервер о нём не знает).
 *
 * {@link #onAttack(int)} вызывается из хука attackEntity (только локальный
 * игрок), поэтому чужие удары сюда не попадают.
 */
public final class PlayerHitTracker {
    private static volatile int targetId = Integer.MIN_VALUE;
    private static volatile long attackMs = 0L;
    private static volatile boolean pending = false;

    private PlayerHitTracker() {
    }

    /** Локальный игрок атаковал сущность с данным id. */
    public static void onAttack(int entityId) {
        targetId = entityId;
        attackMs = System.currentTimeMillis();
        pending = true;
    }

    public static int getTargetId() {
        return targetId;
    }

    /** Есть ли неразрешённый собственный удар в пределах окна ожидания. */
    public static boolean isPending(long windowMs) {
        return pending && System.currentTimeMillis() - attackMs <= windowMs;
    }

    /** Пометить текущий собственный удар как учтённый (одноразово). */
    public static void consume() {
        pending = false;
    }
}
