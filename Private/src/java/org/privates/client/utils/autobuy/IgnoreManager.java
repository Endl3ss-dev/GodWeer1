package org.privates.client.utils.autobuy;

import java.util.ArrayList;
import java.util.List;


public class IgnoreManager {
    private static final List<String> ignore = new ArrayList<>();

    public static List<String> getIgnore() {
        return ignore;
    }

    public static void addIgnore(String name) {
        ignore.add(name);
    }

    public static void removeIgnore(String name) {
        ignore.remove(name);
    }

    public static boolean isIgnored(String name) {
        return ignore.contains(name);
    }
}