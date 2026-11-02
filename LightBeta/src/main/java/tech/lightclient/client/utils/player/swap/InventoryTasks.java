package tech.lightclient.client.utils.player.swap;

public final class InventoryTasks {
    private static final int STOP_TICKS = 2;

    private InventoryTasks() {
    }

    public static void addTask(Class<?> provider, Runnable task) {
        addTaskPriority(provider, task, 0);
    }

    public static void addTaskPriority(Class<?> provider, Runnable task, int priority) {
        SwapScriptManager.ScriptTask scriptTask = SwapScriptManager.getTask(provider)
                .schedule(new SwapScriptManager.StopStep(task, SwapConfig.inventoryStopMoving, STOP_TICKS));
        SwapScriptManager.addTask(scriptTask, priority);
    }

    public static boolean isRealCompleted(Class<?> provider) {
        return SwapScriptManager.isCompleted(provider);
    }

    public static boolean isCompleted(Class<?> provider) {
        return isRealCompleted(provider);
    }
}
