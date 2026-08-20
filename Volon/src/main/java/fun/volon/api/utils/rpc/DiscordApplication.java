package fun.volon.api.utils.rpc;

/**
 * Настройки Discord-приложения (Developer Portal → Application ID, имя, Rich Presence Art Assets).
 * RPC берёт {@link #LARGE_IMAGE_KEY} и {@link #NAME} только отсюда.
 */
public final class DiscordApplication {

    /** Application ID из Discord Developer Portal. */
    public static final long APPLICATION_ID = 1513094036309671956L;

    /** Название приложения (отображается в RPC как подпись к логотипу). */
    public static final String NAME = "Volon";

    /**
     * Ключ картинки из Developer Portal → Rich Presence → Art Assets (например {@code logo}).
     * Не URL — только имя загруженного ассета.
     */
    public static final String LARGE_IMAGE_KEY = "logo";

    public static final String TELEGRAM_BUTTON_LABEL = "Телеграмм";
    public static final String TELEGRAM_BUTTON_URL = "https://t.me/evelbeta";


    private DiscordApplication() {
    }

    public static String applicationId() {
        return String.valueOf(APPLICATION_ID);
    }
}
