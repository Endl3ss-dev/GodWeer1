package dev.swap;

public final class SwapLocks {
	private SwapLocks() {
	}

	public static boolean isBusy() {
		return !SwapScriptManager.isFinished();
	}
}
