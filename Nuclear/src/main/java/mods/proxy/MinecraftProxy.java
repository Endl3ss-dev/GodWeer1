package mods.proxy;

import nuclear.control.Manager;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;
import java.net.Proxy;

public class MinecraftProxy {
    private static boolean initialized = false;
    private static Field proxyField = null;

    public static void initialize() {
        if (!initialized) {
            try {
                proxyField = findProxyField();
                if (proxyField != null) {
                    proxyField.setAccessible(true);
                    initialized = true;
                    System.out.println("Система прокси успешно инициализирована");
                    updateMinecraftProxy();
                } else {
                    System.err.println("Не удалось найти поле proxy в классе Minecraft");
                }
            } catch (Exception e) {
                System.err.println("Ошибка инициализации системы прокси: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public static void updateMinecraftProxy() {
        if (initialized && proxyField != null) {
            try {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft == null) {
                    System.err.println("Экземпляр Minecraft недоступен");
                    return;
                }

                ProxyConnection proxyConnection = Manager.PROXY_CONN;
                if (proxyConnection == null) {
                    System.err.println("ProxyConnection недоступен в менеджере");
                    return;
                }

                Proxy javaProxy = proxyConnection.getJavaProxy();
                if (!proxyField.isAccessible()) {
                    proxyField.setAccessible(true);
                }

                Object oldProxy = proxyField.get(minecraft);
                proxyField.set(minecraft, javaProxy);
                String proxyInfo = proxyConnection.getProxyString();
                System.out.println("Прокси обновлен в Minecraft: " + proxyInfo);
                String var10001 = oldProxy != null ? oldProxy.toString() : "null";
                System.out.println("Предыдущий прокси: " + var10001);
            } catch (IllegalAccessException e) {
                System.err.println("Нет доступа к полю proxy: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Ошибка обновления прокси в Minecraft: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            System.err.println("Система прокси не инициализирована. Вызовите initialize() сначала.");
        }
    }

    public static Proxy getCurrentMinecraftProxy() {
        if (initialized && proxyField != null) {
            try {
                Minecraft minecraft = Minecraft.getInstance();
                return minecraft == null ? null : (Proxy)proxyField.get(minecraft);
            } catch (Exception e) {
                System.err.println("Ошибка получения текущего прокси сервера: " + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    private static Field findProxyField() {
        try {
            Class<?> minecraftClass = Minecraft.class;
            String[] possibleNames = new String[]{"proxy", "field_71390_s", "f_91062_"};

            for(String name : possibleNames) {
                try {
                    Field field = minecraftClass.getDeclaredField(name);
                    if (field.getType() == Proxy.class) {
                        System.out.println("Найдено поле proxy с именем: " + name);
                        return field;
                    }
                } catch (NoSuchFieldException var10) {
                }
            }

            System.out.println("Поиск поля proxy в рефлексии...");
            Field[] fields = minecraftClass.getDeclaredFields();
            Field proxyField = null;
            int proxyFieldsCount = 0;

            for(Field field : fields) {
                if (field.getType() == Proxy.class) {
                    proxyField = field;
                    ++proxyFieldsCount;
                    System.out.println("Найдено поле Proxy: " + field.getName());
                }
            }

            if (proxyFieldsCount == 1) {
                System.out.println("Выбран уникальный поле Proxy: " + proxyField.getName());
                return proxyField;
            }

            if (proxyFieldsCount > 1) {
                System.err.println("Найдено несколько полей Proxy (" + proxyFieldsCount + "), выбираем первый");
                return proxyField;
            }

            System.err.println("Не найдено поле Proxy в классе Minecraft");

            for(Class<?> currentClass = minecraftClass.getSuperclass(); currentClass != null && currentClass != Object.class; currentClass = currentClass.getSuperclass()) {
                System.out.println("Проверяем в классе: " + currentClass.getName());

                for(Field field : currentClass.getDeclaredFields()) {
                    if (field.getType() == Proxy.class) {
                        System.out.println("Найдено поле proxy в суперклассе: " + field.getName());
                        return field;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка поиска поля proxy: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void reset() {
        initialized = false;
        proxyField = null;
    }
}
