/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.ArrayList;
import java.util.List;

public class AccountStorage
{
    List<AccountEntry> accounts = new ArrayList<>();
    String selectedAccountName;

    public List<AccountEntry> getAccounts() {
        return this.accounts;
    }

    public String getSelectedAccountName() {
        return this.selectedAccountName;
    }

    public void setSelectedAccountName(String param1) {
        this.selectedAccountName = param1;
    }
}

