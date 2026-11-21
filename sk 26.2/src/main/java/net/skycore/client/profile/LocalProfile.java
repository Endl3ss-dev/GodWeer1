package net.skycore.client.profile;

/** Display profile without hardware identifiers or remote license calls. */
public final class LocalProfile {
    public static final String DISPLAY_NAME = "Skycore Offline";
    private static final LocalProfile INSTANCE = new LocalProfile();
    private LocalProfile() {}
    public static LocalProfile getInstance() { return INSTANCE; }
    public String profile(String field) {
        return switch (field) {
            case "username", "role" -> DISPLAY_NAME;
            case "uid" -> "0";
            default -> "";
        };
    }
}
