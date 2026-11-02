package tech.lightclient.scripting.runtime;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import tech.lightclient.LightClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/** Talks to DeepSeek so the IDE can write scripts from a plain-language description. */
public final class DeepSeekClient {

    private static final String ENDPOINT = "https://api.deepseek.com/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .build();

    private static String apiKey = "sk-630395c260d3438f8d6b72e84267ec16";
    /**
     * This key's account serves {@code deepseek-v4-flash} and {@code deepseek-v4-pro}; the older
     * {@code deepseek-chat} name is rejected, so flash is the default.
     */
    private static String model = "deepseek-v4-flash";

    public static void setApiKey(String key) {
        if (key != null && !key.isBlank()) apiKey = key.trim();
    }

    public static String apiKey() {
        return apiKey;
    }

    public static void setModel(String m) {
        if (m != null && !m.isBlank()) model = m.trim();
    }

    /**
     * Teaches the model the dialect it has to produce. Being specific here matters far more than
     * post-processing: the API is bespoke, so without the surface spelled out the model writes
     * plausible-looking Kotlin that does not compile.
     */
    private static final String SYSTEM_PROMPT = """
            Ты пишешь скрипты для Light Client на Kotlin (.kts). Отвечай ТОЛЬКО кодом скрипта,
            без markdown-ограждений ``` и без пояснений до или после.

            Правила:
            - Никаких import: всё API уже в области видимости.
            - Запрещены файлы, сеть, потоки, рефлексия, java.io, java.nio, Thread, System.getProperty,
              а также прямое обращение к net.minecraft.* и tech.lightclient.*. Скрипт с ними не загрузится.
            - Обработчик события обязан укладываться в 250 мс. Никаких бесконечных циклов.

            Метаданные и настройки объявляются на верхнем уровне:
              name("Имя"); description("Что делает"); key(Key.G); requireApi(1)
              val a = checkBox("Имя", true)                       // a.value()
              val b = slider("Имя", 5f, 1f, 20f, 1f)              // b.value(), b.intValue()
              val c = slider("Имя", 5, 1, 20)                     // целочисленный слайдер
              val d = rangeSlider("Имя", 2f, 8f, 0f, 20f)         // d.from(), d.to()
              val e = input("Имя", "текст")                       // e.value()
              val f = selectable("Имя", "A", "B", selected = "A")  // f.value()
              val g = combo("Имя", "A", "B", selected = listOf("A"))
              val h = colorPicker("Имя", Colors.rgba(90, 200, 255, 255))
              hotkey("Имя", Key.G) { }
              button("Имя") { }
              slider("Имя", 200f, 0f, 1000f, 50f).postfix(Postfixes.MS)
              val x by slider("Имя", 5f, 1f, 20f)   // делегат отдаёт значение
              настройку можно вложить: slider(parentCheckBox, "Имя", ...)
              и спрятать: setting.visibleWhen { other.value() }

            Жизненный цикл: onEnable { }, onDisable { }, onUnload { }
            Состояние: enabled, inGame, whenInGame { }

            События: on<ClientTickEvent> { }, а также PrePlayerTickEvent, PostPlayerTickEvent,
            AttackEvent, AttackedEvent, MoveEvent, MoveInputEvent, JumpEvent, SprintEvent,
            ChatEvent, PacketReceiveEvent, PacketSendEvent, KeyEvent, MouseScrollEvent,
            SlotClickEvent, ScreenCloseEvent, Render2DEvent, Render3DEvent,
            EntitySpawnEvent, EntityRemoveEvent, WorldLoadEvent, ModuleToggleEvent.
            Приоритет и отмена: on<MoveInputEvent>(priority = Priority.LAST) { }, e.cancel(),
            on<AttackEvent>(ignoreCancelled = true) { }. Подписка: val s = on<...> { }; s.unsubscribe().
            Пакеты приходят на сетевом потоке: всё, что трогает игру, оборачивай в onClientThread { }.

            Данные: player.health(), player.position(), player.onGround(), player.sprinting(),
            player.attackCooldown(), player.hasEffect("speed"), player.mainHandItem(),
            world.players(), world.entitiesNear(pos, radius, filters.attackable()),
            world.block(x, y, z), world.playSound("minecraft:...", 1f, 1f), client.millis(), client.tick().
            Прицел: raycast.entityAtCrosshair(6.0), raycast.crosshair(5.0) -> Hit.OnBlock/OnEntity/None,
            raycast.canSee(a, b). Повороты: rotations.lookAt(pos).
            Действия: control.jump(), control.sneak(true), interaction.attack(target),
            interaction.useItem("MAIN_HAND"), inventory.selectSlot(0).
            Сообщения: chat.print(...), chat.sendToServer(...), chat.runCommand("spawn"),
            notify("текст", NotifyKind.WARN), log.info(...).
            Время: val t = timer(); t.passed(10); t.passedAndReset(10); afterTicks(20) { }; everyTicks(100) { }.
            Хранение: storage.getInt("k", 0), storage.put("k", 1), storage.save(), config("stats").
            Команды: command("ping") { runs { reply("понг") } } с sub("...") { }, usage(...), completes(0, "a", "b").
            Рисование: on<Render2DEvent> { e -> e.render().text(10f, 20f, 12f, "hi", Colors.WHITE) },
            on<Render3DEvent> { e -> e.render().entityBox(target, Colors.RED, true) }.
            Цвета: Colors.RED, Colors.rgba(r, g, b, a), Colors.withAlpha(c, a), Colors.mix(a, b, t).
            """;

    /**
     * Asks for a script. Exactly one callback argument is non-null.
     * The callback runs on a background thread.
     */
    public static void generate(String userPrompt, BiConsumer<String, String> callback) {
        JSONObject payload = new JSONObject();
        payload.put("model", model);
        payload.put("stream", false);
        payload.put("temperature", 0.3);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", SYSTEM_PROMPT));
        messages.put(new JSONObject().put("role", "user").put("content", userPrompt));
        payload.put("messages", messages);

        Request request = new Request.Builder()
                .url(ENDPOINT)
                .header("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(payload.toString(), JSON))
                .build();

        HTTP.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.accept(null, "нет соединения: " + (e.getMessage() == null ? "неизвестная ошибка" : e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (Response r = response) {
                    String body = r.body() != null ? r.body().string() : "";
                    if (!r.isSuccessful()) {
                        callback.accept(null, explain(r.code(), body));
                        return;
                    }
                    JSONObject json = new JSONObject(body);
                    String content = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                    callback.accept(stripFence(content), null);
                } catch (Throwable t) {
                    LightClient.LOGGER.error("[DeepSeek] could not parse the reply", t);
                    callback.accept(null, "не удалось разобрать ответ: " + t.getMessage());
                }
            }
        });
    }

    /** Turns an HTTP failure into something a user can act on. */
    private static String explain(int code, String body) {
        String detail = "";
        try {
            detail = new JSONObject(body).getJSONObject("error").optString("message", "");
        } catch (Throwable ignored) {
        }
        return switch (code) {
            case 401 -> "ключ API отклонён (401). Проверьте ключ DeepSeek.";
            case 402 -> "на счёте DeepSeek нет средств (402). Пополните баланс аккаунта, ключ рабочий.";
            case 429 -> "слишком много запросов (429). Подождите немного.";
            case 400 -> "запрос отклонён (400)" + (detail.isEmpty() ? "" : ": " + detail);
            default -> "HTTP " + code + (detail.isEmpty() ? ": " + shorten(body) : ": " + detail);
        };
    }

    /** Strips a markdown code fence, which the model still adds now and then. */
    private static String stripFence(String s) {
        String t = s.trim();
        if (t.startsWith("```")) {
            int nl = t.indexOf('\n');
            if (nl >= 0) t = t.substring(nl + 1);
            int close = t.lastIndexOf("```");
            if (close >= 0) t = t.substring(0, close);
        }
        return t.trim();
    }

    private static String shorten(String s) {
        if (s == null) return "";
        return s.length() > 200 ? s.substring(0, 200) : s;
    }

    private DeepSeekClient() {}
}
