package antileak.base.client.ui.account;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private static AccountManager instance;
    private final List<Account> accounts = new ArrayList<>();
    private final File file;
    private Account selected;

    private AccountManager() {
        file = new File("punch/accounts.txt");
        load();
    }

    public static AccountManager getInstance() {
        if (instance == null) instance = new AccountManager();
        return instance;
    }

    public List<Account> getAccounts() { return accounts; }
    public Account getSelected() { return selected; }
    public void setSelected(Account a) { this.selected = a; }

    public void add(Account a) {
        accounts.add(a);
        save();
    }

    public void remove(Account a) {
        accounts.remove(a);
        if (selected == a) selected = null;
        save();
    }

    public void generateOffline(String name) {
        String uuid = java.util.UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)).toString();
        accounts.add(new Account(name, uuid, Account.Type.OFFLINE));
        save();
    }

    private void load() {
        accounts.clear();
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                    Account.Type type = parts[2].equals("MS") ? Account.Type.MICROSOFT : Account.Type.OFFLINE;
                    accounts.add(new Account(parts[0], parts[1], type));
                }
            }
        } catch (IOException ignored) {}
    }

    public void save() {
        try {
            file.getParentFile().mkdirs();
            try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
                for (Account a : accounts) {
                    w.write(a.getName() + ":" + a.getUuid() + ":" + (a.getType() == Account.Type.MICROSOFT ? "MS" : "OFF"));
                    w.newLine();
                }
            }
        } catch (IOException ignored) {}
    }
}
