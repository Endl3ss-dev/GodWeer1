package dev.swap;

public enum SphereMode {
	CERBERUS,
	MYTHIC,
	LEGENDARY,
	TALISMAN,
	NONE;

	@Override
	public String toString() {
		return switch (this) {
			case CERBERUS -> "CERBERUS";
			case MYTHIC -> "MYTHIC";
			case LEGENDARY -> "LEGENDARY";
			case TALISMAN -> "TALISMAN";
			case NONE -> "NONE";
		};
	}
}
