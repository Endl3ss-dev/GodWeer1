package bench.prestige.portable;

public final class Bridge {
    public static native void monitorEnter(Object value);
    public static native void monitorExit(Object value);
    public static native void setStaticField(Class<?> owner, java.lang.reflect.Field field, Object value);
    public static native void setInstanceField(Class<?> owner, java.lang.reflect.Field field, Object receiver, Object value);
    private Bridge() {}
    public static native void open(String nativePath, String registryPath, String fieldsPath, Class<?> nativeClass, ClassLoader loader, String[] names, String[] values);
    public static native void initializeUi();
    public static native void toggleUi();
    public static native boolean uiVisible();
}
