package never.lose.cosmetics;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Единственная точка соприкосновения с модом Figura.
 * Всё через рефлексию: клиент обязан собираться и запускаться без Figura в classpath.
 *
 * Проверено по исходникам Figura 0.1.5 (org.figuramc.figura.*):
 *   AvatarManager.loadLocalAvatar(Path)            - public static
 *   AvatarManager.clearAvatars(UUID)               - public static
 *   AvatarManager.panic                            - public static boolean
 *   AvatarManager.LOADED_USERS / FETCHED_USERS     - private static final Map/Set
 *   UserData(UUID) / getMainAvatar() / clear()     - public
 *   LocalAvatarLoader.loadAvatar(Path, UserData)   - public static
 *   Avatar.tick()                                  - public
 *
 * Все хэндлы резолвятся один раз и кэшируются: в рендере рефлексия только invoke.
 */
public final class FiguraBridge {

    private static final Object LOCK = new Object();

    private static volatile boolean resolved;
    private static volatile boolean available;
    private static volatile String lastError = "";

    private static Class<?> avatarManagerClass;
    private static Method mLoadLocalAvatar;
    private static Method mClearAvatars;
    private static Method mGetLocalUuid;
    private static Field fPanic;
    private static Field fLoadedUsers;
    private static Field fFetchedUsers;

    private static Constructor<?> cUserData;
    private static Method mUserDataGetMain;
    private static Method mUserDataClear;

    private static Method mLocalLoadAvatar;
    private static Method mAvatarTick;

    private FiguraBridge() {
    }

    public static boolean isModPresent() {
        return FabricLoader.getInstance().isModLoaded("figura");
    }

    public static String getLastError() {
        return lastError;
    }

    public static boolean isAvailable() {
        ensureResolved();
        return available;
    }

    private static void ensureResolved() {
        if (resolved) {
            return;
        }

        synchronized (LOCK) {
            if (resolved) {
                return;
            }

            resolved = true;

            if (!isModPresent()) {
                available = false;
                lastError = "Figura не установлена";
                return;
            }

            try {
                avatarManagerClass = Class.forName("org.figuramc.figura.avatar.AvatarManager");
                Class<?> userDataClass = Class.forName("org.figuramc.figura.avatar.UserData");
                Class<?> loaderClass = Class.forName("org.figuramc.figura.avatar.local.LocalAvatarLoader");
                Class<?> avatarClass = Class.forName("org.figuramc.figura.avatar.Avatar");
                Class<?> modClass = Class.forName("org.figuramc.figura.FiguraMod");

                mLoadLocalAvatar = avatarManagerClass.getMethod("loadLocalAvatar", Path.class);
                mClearAvatars = avatarManagerClass.getMethod("clearAvatars", UUID.class);
                mGetLocalUuid = modClass.getMethod("getLocalPlayerUUID");

                fPanic = avatarManagerClass.getField("panic");
                fLoadedUsers = avatarManagerClass.getDeclaredField("LOADED_USERS");
                fFetchedUsers = avatarManagerClass.getDeclaredField("FETCHED_USERS");
                fLoadedUsers.setAccessible(true);
                fFetchedUsers.setAccessible(true);

                cUserData = userDataClass.getConstructor(UUID.class);
                mUserDataGetMain = userDataClass.getMethod("getMainAvatar");
                mUserDataClear = userDataClass.getMethod("clear");

                mLocalLoadAvatar = loaderClass.getMethod("loadAvatar", Path.class, userDataClass);
                mLocalLoadAvatar.setAccessible(true);

                mAvatarTick = avatarClass.getMethod("tick");

                available = true;
                lastError = "";
            } catch (Throwable t) {
                available = false;
                lastError = "Несовместимая версия Figura: " + t.getClass().getSimpleName();
            }
        }
    }

    public static UUID getLocalPlayerUuid() {
        ensureResolved();

        if (!available) {
            return null;
        }

        try {
            Object value = mGetLocalUuid.invoke(null);
            return value instanceof UUID uuid ? uuid : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static void disablePanic() {
        try {
            if (fPanic.getBoolean(null)) {
                fPanic.setBoolean(null, false);
            }
        } catch (Throwable ignored) {
        }
    }

    /** Применяет аватар из папки как локальный. Вызывать с рендер-треда. */
    public static boolean applyAvatar(Path avatarFolder) {
        ensureResolved();

        if (!available || avatarFolder == null) {
            return false;
        }

        try {
            disablePanic();
            mLoadLocalAvatar.invoke(null, avatarFolder);
            return true;
        } catch (Throwable t) {
            lastError = t.getClass().getSimpleName();
            return false;
        }
    }

    /** Снимает локальный аватар. */
    public static boolean clearLocalAvatar() {
        ensureResolved();

        if (!available) {
            return false;
        }

        try {
            UUID uuid = getLocalPlayerUuid();

            if (uuid == null) {
                return false;
            }

            mClearAvatars.invoke(null, uuid);
            return true;
        } catch (Throwable t) {
            lastError = t.getClass().getSimpleName();
            return false;
        }
    }

    /**
     * Создаёт изолированный UserData и запускает загрузку аватара в него.
     * Загрузка внутри Figura асинхронная, готовность проверяется через getPreviewAvatar.
     */
    public static Object createDetachedUser(Path avatarFolder) {
        ensureResolved();

        if (!available || avatarFolder == null) {
            return null;
        }

        try {
            UUID uuid = getLocalPlayerUuid();

            if (uuid == null) {
                return null;
            }

            Object userData = cUserData.newInstance(uuid);
            mLocalLoadAvatar.invoke(null, avatarFolder, userData);
            return userData;
        } catch (Throwable t) {
            lastError = t.getClass().getSimpleName();
            return null;
        }
    }

    public static Object getPreviewAvatar(Object userData) {
        if (!available || userData == null) {
            return null;
        }

        try {
            return mUserDataGetMain.invoke(userData);
        } catch (Throwable t) {
            return null;
        }
    }

    public static void tickAvatar(Object avatar) {
        if (!available || avatar == null) {
            return;
        }

        try {
            mAvatarTick.invoke(avatar);
        } catch (Throwable ignored) {
        }
    }

    public static void releaseUser(Object userData) {
        if (!available || userData == null) {
            return;
        }

        try {
            mUserDataClear.invoke(userData);
        } catch (Throwable ignored) {
        }
    }

    /**
     * На время выполнения renderer подменяет запись игрока в реестрах Figura,
     * чтобы обычный рендер сущности нарисовал превью-аватар вместо текущего.
     * Подмена и откат идут в одном кадре на рендер-треде.
     */
    @SuppressWarnings("unchecked")
    public static boolean renderWithDetachedUser(UUID playerId, Object userData, Runnable renderer) {
        ensureResolved();

        if (!available || playerId == null || userData == null || renderer == null) {
            return false;
        }

        Map<Object, Object> users;
        Set<Object> fetched;

        try {
            users = (Map<Object, Object>) fLoadedUsers.get(null);
            fetched = (Set<Object>) fFetchedUsers.get(null);
        } catch (Throwable t) {
            return false;
        }

        if (users == null || fetched == null) {
            return false;
        }

        Object previous = users.get(playerId);
        boolean hadFetched = fetched.contains(playerId);

        users.put(playerId, userData);
        fetched.add(playerId);

        try {
            renderer.run();
            return true;
        } catch (Throwable t) {
            return false;
        } finally {
            if (previous != null) {
                users.put(playerId, previous);
            } else {
                users.remove(playerId);
            }

            if (!hadFetched) {
                fetched.remove(playerId);
            }
        }
    }
}
