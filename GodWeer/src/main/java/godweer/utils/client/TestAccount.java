package godweer.utils.client;

import com.mojang.authlib.properties.PropertyMap;

import net.minecraft.client.User;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public final class TestAccount {

    private static final String OFFLINE_PREFIX = "OfflinePlayer:";
    private static final String FALLBACK_TOKEN = "0";

    private TestAccount() {
    }

    public static User create(String name, String accessToken) {
        UUID offlineUuid = UUID.nameUUIDFromBytes((OFFLINE_PREFIX + name).getBytes(StandardCharsets.UTF_8));
        return new User(
                name,
                offlineUuid,
                accessToken != null ? accessToken : FALLBACK_TOKEN,
                Optional.empty(),
                Optional.empty(),
                User.Type.LEGACY,
                new PropertyMap()
        );
    }
}
