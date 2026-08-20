/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import ru.dreamix.fabricloader.VMBridge;

public class UserInfo {
    static String field7549 = "sdadsadsa11";
    static String field7550;
    static String field7551;
    static String field7552 = "field7552";
    static String field7553;
    static String field7554;
    static String field7555;
    static String field7556;

    public static void method1839() {
        field7549 = VMBridge.getUserName();
        field7551 = String.valueOf(VMBridge.getUserID());
        field7550 = VMBridge.getSubTill();
    }

    public static String method1840() {
        return field7552;
    }

    public static String method1841() {
        return field7549;
    }

    public static String method1842() {
        return field7550;
    }

    public static String method1843() {
        return field7551;
    }

    static {
        field7551 = "15448";
        field7550 = "14.07.2026";
        field7553 = "Sk3dGuard";
        field7554 = "0";
        field7555 = "01-01-2048";
        field7556 = "Admin";
    }
}



