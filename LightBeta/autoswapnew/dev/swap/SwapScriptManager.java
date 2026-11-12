package dev.swap;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BooleanSupplier;

public final class SwapScriptManager {
	private static final LinkedList<ScriptTask> TASKS = new LinkedList<>();

	private SwapScriptManager() {
	}

	public static void tick() {
		ScriptTask current = TASKS.peek();
		if (current == null) {
			return;
		}

		if (current.tryTick()) {
			TASKS.poll();
		}
	}

	public static ScriptTask getTask(Class<?> owner) {
		for (ScriptTask task : TASKS) {
			if (task.owner == owner) {
				return task;
			}
		}
		return new ScriptTask(owner);
	}

	public static void addTask(ScriptTask task, int priority) {
		if (task == null || TASKS.contains(task)) {
			return;
		}
		task.priority = priority;
		if (priority > getMaxPriority()) {
			if (TASKS.isEmpty()) {
				TASKS.addFirst(task);
			} else {
				TASKS.add(1, task);
			}
		} else {
			TASKS.add(task);
		}
	}

	public static boolean isFinished() {
		return TASKS.isEmpty();
	}

	public static boolean isCompleted(Class<?> owner) {
		for (ScriptTask task : TASKS) {
			if (task.owner == owner && !task.isCompleted()) {
				return false;
			}
		}
		return true;
	}

	private static int getMaxPriority() {
		int max = Integer.MIN_VALUE;
		for (ScriptTask task : TASKS) {
			max = Math.max(max, task.priority);
		}
		return max == Integer.MIN_VALUE ? 0 : max;
	}

	public static final class StopStep implements BooleanSupplier {
		private final Runnable task;
		private final boolean stopMoving;
		private final int requiredTicks;
		private int stopTicks;

		public StopStep(Runnable task, boolean stopMoving, int requiredTicks) {
			this.task = task;
			this.stopMoving = stopMoving;
			this.requiredTicks = requiredTicks;
		}

		@Override
		public boolean getAsBoolean() {
			if (stopMoving) {
				MovementStop.force();
				if (stopTicks++ < requiredTicks) {
					return false;
				}
			}
			task.run();
			InventoryUtil.closeScreen();
			InvBypass.restore(SwapUtil.getMovementKeys());
			return true;
		}
	}

	public static final class ScriptTask {
		private final Class<?> owner;
		private final Queue<BooleanSupplier> steps = new LinkedList<>();
		private int priority;

		public ScriptTask(Class<?> owner) {
			this.owner = owner;
		}

		public ScriptTask schedule(BooleanSupplier step) {
			steps.add(step);
			return this;
		}

		public boolean tryTick() {
			BooleanSupplier step = steps.peek();
			if (step == null) {
				return true;
			}
			if (step.getAsBoolean()) {
				steps.poll();
			}
			return steps.isEmpty();
		}

		public boolean isCompleted() {
			return steps.isEmpty();
		}
	}
}
