package net.skycore.client.integration;

/** Compatibility accessors for removed remote services; no endpoint can be configured. */
public final class RemoteEndpoints {
    private RemoteEndpoints() {}
    public static String pathPresence() { return ""; }
    public static String pathPartyDecline() { return ""; }
    public static String pathSendMessage() { return ""; }
    public static String decodeApiKey() { return ""; }
    public static String pathSetPrefix() { return ""; }
    public static String pathLeaveParty() { return ""; }
    public static String pathGetUpdates() { return ""; }
    public static String pathDisbandParty() { return ""; }
    public static String pathSendPartyMessage() { return ""; }
    public static String jsonApiKey() { return ""; }
    public static String pathUpdateCoordinates() { return ""; }
    public static String statusOnline() { return "online"; }
    public static String pathGetPartyCoordinates() { return ""; }
    public static String statusOffline() { return "offline"; }
    public static String decodeApiUrl() { return ""; }
    public static String pathPartyAccept() { return ""; }
    public static String pathPartyInvite() { return ""; }
    public static String pathOnlineCount() { return ""; }
}
