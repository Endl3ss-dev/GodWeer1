package swerve.bot;

/**
 * Готовые цепочки шагов: чтобы не собирать типового бота с нуля.
 *
 * <p>Шаблон не заменяет поведение, а добавляет свою цепочку в конец — так его
 * можно накладывать на уже собранное (например, сначала авторизация, потом ферма).
 * Все добавленные шаги — обычные {@link BotStep}, их можно править и удалять.
 */
public enum BotPreset {

    AUTH("Авторизация",
            "ждать сервер → /login паролем из профиля → ждать «Успешно»",
            "Вход на сервер с AuthMe и подобными плагинами."),

    REGISTER("Регистрация",
            "ждать сервер → /reg паролем из профиля → ждать ответа",
            "Первый вход нового ника на сервере с регистрацией."),

    FARM("Ферма / АФК",
            "круг: анти-АФК → ждать 20–60 с → случайный поворот",
            "Держит бота в сети и не даёт серверу выкинуть за неактивность."),

    CHAT("Чат-бот",
            "ответ на «координаты» → ждать сообщение",
            "Реагирует на сообщения в чате готовой фразой.");

    private final String title;
    private final String summary;
    private final String description;

    BotPreset(String title, String summary, String description) {
        this.title = title;
        this.summary = summary;
        this.description = description;
    }

    public String title() {
        return this.title;
    }

    public String summary() {
        return this.summary;
    }

    public String description() {
        return this.description;
    }

    /**
     * Добавляет шаги шаблона в конец поведения профиля.
     *
     * @return сколько шагов добавлено
     */
    public int apply(BotProfile profile) {
        if (profile == null) {
            return 0;
        }
        int before = profile.getSteps().size();
        String password = profile.getPassword();

        switch (this) {
            case AUTH -> {
                add(profile, BotStepType.WAIT_ONLINE, "20");
                add(profile, BotStepType.AUTO_LOGIN);
                BotStep wait = add(profile, BotStepType.WAIT_CHAT, "Успешн", "15");
                if (wait != null) {
                    wait.set(0, "Успешн");
                    wait.set(1, "15");
                }
                add(profile, BotStepType.WAIT, "1");
            }
            case REGISTER -> {
                add(profile, BotStepType.WAIT_ONLINE, "20");
                add(profile, BotStepType.AUTO_REGISTER);
                add(profile, BotStepType.WAIT_CHAT).set(0, "Успешн");
                add(profile, BotStepType.WAIT, "2");
                add(profile, BotStepType.AUTO_LOGIN);
            }
            case FARM -> {
                add(profile, BotStepType.WAIT_ONLINE, "30");
                add(profile, BotStepType.LOOP).set(0, "0");
                add(profile, BotStepType.ANTI_AFK).set(0, "3");
                add(profile, BotStepType.WAIT_RANDOM).set(0, "20");
                profile.getSteps().get(profile.getSteps().size() - 1).set(1, "60");
                add(profile, BotStepType.LOOK_RANDOM).set(0, "90");
                add(profile, BotStepType.LOOP_END);
            }
            case CHAT -> {
                add(profile, BotStepType.WAIT_ONLINE, "20");
                add(profile, BotStepType.REPLY_CHAT).set(0, "координаты");
                profile.getSteps().get(profile.getSteps().size() - 1).set(1, "идёт");
                add(profile, BotStepType.WAIT_CHAT_ANY).set(0, "30");
            }
            default -> {
                // новых шаблонов без реализации быть не должно
            }
        }

        if (password != null && !password.isBlank()) {
            profile.setPassword(password);
        }
        BotStore.markDirty();
        return profile.getSteps().size() - before;
    }

    private static BotStep add(BotProfile profile, BotStepType type, String... values) {
        BotStep step = new BotStep(type);
        for (int i = 0; i < values.length && i < step.valueCount(); i++) {
            step.set(i, values[i]);
        }
        profile.addStep(step);
        return step;
    }
}
