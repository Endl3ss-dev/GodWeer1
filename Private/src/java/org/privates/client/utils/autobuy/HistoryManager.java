package org.privates.client.utils.autobuy;

import java.util.ArrayList;
import java.util.List;


public class HistoryManager {
    private static final List<HistoryItem> history = new ArrayList<>();

    public static void add(HistoryItem item) {
        if (item != null) {
            history.add(0, item);
            if (history.size() > 100) {
                history.remove(history.size() - 1);
            }
        }
    }

    public static List<HistoryItem> getHistory() {
        return new ArrayList<>(history);
    }

    public static void clear() {
        history.clear();
    }
}