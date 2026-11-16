package tech.lightclient.client.utils.player.swap;

/**
 * Состояние очереди свапов: режим байпаса инвентаря и необходимость остановки движения.
 */
public final class SwapConfig {
    public static InvBypassMode invBypassMode = InvBypassMode.HOLY_WORLD;
    public static boolean inventoryStopMoving = true;

    private SwapConfig() {
    }
}
