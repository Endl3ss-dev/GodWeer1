package dev.swap;

public enum InvBypassMode {
	HOLY_WORLD,
	ADVANCED;

	@Override
	public String toString() {
		return switch (this) {
			case HOLY_WORLD -> "HolyWorld";
			case ADVANCED -> "Advanced";
		};
	}
}
