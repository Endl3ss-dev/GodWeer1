package antileak.base.client.ui.account;

public class Account {
    private String name;
    private String uuid;
    private Type type;

    public enum Type {
        OFFLINE,
        MICROSOFT
    }

    public Account(String name, String uuid, Type type) {
        this.name = name;
        this.uuid = uuid;
        this.type = type;
    }

    public String getName() { return name; }
    public String getUuid() { return uuid; }
    public Type getType() { return type; }

    public void setName(String name) { this.name = name; }
    public void setUuid(String uuid) { this.uuid = uuid; }
}
