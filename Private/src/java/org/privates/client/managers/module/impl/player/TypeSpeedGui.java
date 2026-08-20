package org.privates.client.managers.module.impl.player;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.privates.client.api.interfaces.IMinecraft.mc;

public class TypeSpeedGui extends Screen {

    private static final String[] RUSSIAN_WORDS = {
            "время", "рука", "дело", "жизнь", "день", "голова", "место", "лицо", "глаз", "вопрос",
            "дом", "сторона", "страна", "мир", "случай", "голос", "город", "работа", "ребенок", "сила",
            "конец", "вид", "система", "часть", "отец", "женщина", "машина", "проблема", "власть",
            "друг", "час", "дорога", "война", "право", "мать", "вещь", "история", "ночь", "земля",
            "книга", "слово", "деньги", "минута", "интерес", "закон", "муж", "программа", "вода", "статья",
            "стол", "душа", "взгляд", "народ", "результат", "утро", "путь", "роль", "смысл", "тело",
            "образ", "форма", "основа", "идея", "связь", "мысль", "число", "судьба", "цель", "служба",
            "нога", "уровень", "любовь", "взор", "письмо", "окно", "палец", "чувство", "сердце", "солнце",
            "река", "дверь", "брат", "сын", "стена", "море", "улица", "период", "девушка", "язык",
            "комната", "группа", "процесс", "театр", "школа", "двор", "цена", "условие", "центр", "человек",
            "начало", "ряд", "игра", "член", "orden", "состояние", "свет", "пора", "класс", "воздух",
            "часы", "труд", "девочка", "имя", "половина", "общество", "действие", "стих", "мера", "школьник",
            "начальник", "союз", "писатель", "край", "совет", "число", "подход", "план", "мама", "решение",
            "предмет", "состав", "падение", "партия", "дума", "способ", "счет", "сон", "речь", "точка",
            "основание", "товарищ", "культура", "данные", "мнение", "документ", "институт", "ход", "проект",
            "встреча", "директор", "служащий", "lakossд", "территория", "мастер", "ответ", "событие", "верх",
            "собака", "театральный", "принцип", "художник", "материал", "неделя", "враг", "боль", "весна",
            "факт", "память", "команда", "вечер", "результат", "закон", "положение", "век", "порядок",
            "комплекс", "организм", "граница", "проход", "этап", "производство", "мужчина", "вкус", "объект",
            "бой", "разговор", "деятель", "бумага", "предел", "рабочий", "тип", "животное", "сфера",
            "карта", "тема", "фамилия", "модель", "метод", "процент", "структура", "природа", "удар",
            "условия", "пункт", "аспект", "признак", "комитет", "название", "мальчик", "использование",
            "правительство", "хозяйство", "управление", "возможность", "большинство", "труд", "движение",
            "причина", "поле", "зритель", "задача", "реальность", "армия", "практика", "степень",
            "настроение", "мощность", "личность", "участие", "оценка", "количество", "объем", "испытание",
            "способность", "счастье", "участник", "содержание", "борьба", "длина", "деревня", "немец",
            "заявление", "искусство", "база", "звезда", "размер", "побед", "рождение", "создание",
            "район", "поведение", "качество", "сцена", "суть", "температура", "мужик", "режим",
            "круг", "область", "тайна", "понятие", "воля", "уголь", "характеристика", "политика",
            "правило", "поколение", "дух", "окончание", "группировка", "господин", "черта", "растение",
            "зал", "направление", "чтение", "орган", "господь", "радость", "пространство", "статус",
            "формирование", "железо", "исполнение", "берег", "капитал", "поэт", "потребность", "войско",
            "наличие", "сущность", "традиция", "ученый", "пол", "множество", "величина", "жена",
            "запад", "передача", "обстоятельство", "тень", "течение", "обязанность", "критик", "сумма",
            "представитель", "участок", "наука", "секунда", "операция", "существование", "эпоха", "обработка",
            "среда", "тенденция", "страх", "описание", "появление", "французский", "повод", "урок",
            "признание", "вывод", "губа", "назначение", "расстояние", "сознание", "представление", "запах",
            "особенность", "требование", "контроль", "ощущение", "господство", "слеза", "сведение", "спектакль",
            "владелец", "песня", "поэзия", "вариант", "растительность", "стоимость", "строительство", "революция",
            "схема", "тишина", "гора", "древность", "мозг", "ценность", "судьба", "король",
            "промышленность", "смена", "звук", "сожаление", "обеспечение", "нарушение", "раз", "помощник",
            "разум", "зима", "зависимость", "заместитель", "пласт", "наблюдение", "крестьянин", "кровать",
            "изучение", "издание", "удовольствие", "лошадь", "положение", "выражение", "масса", "влияние",
            "величие", "чай", "большевик", "читатель", "расчет", "недостаток", "серия", "исследование",
            "душ", "выступление", "дача", "продукт", "профессор", "устройство", "связка", "детство",
            "обстановка", "объяснение", "содержимое", "сохранение", "философ", "преподаватель", "внимание", "выполнение"
    };

    private static final String[] ENGLISH_WORDS = {
            "time", "person", "year", "way", "day", "thing", "man", "world", "life", "hand",
            "part", "child", "eye", "woman", "place", "work", "week", "case", "point", "government",
            "company", "number", "group", "problem", "fact", "home", "water", "room", "mother", "area",
            "money", "story", "moment", "right", "study", "book", "word", "business", "issue", "side",
            "kind", "head", "house", "service", "friend", "father", "power", "hour", "game", "line",
            "end", "member", "law", "car", "city", "community", "name", "president", "team", "minute",
            "idea", "body", "information", "back", "parent", "face", "others", "level", "office", "door",
            "health", "art", "war", "history", "party", "result", "change", "morning", "reason",
            "research", "girl", "guy", "food", "air", "teacher", "force", "education", "foot",
            "boy", "age", "policy", "process", "music", "market", "sense", "nation", "plan", "college",
            "interest", "death", "experience", "effect", "use", "class", "control", "care", "field",
            "development", "role", "effort", "rate", "heart", "drug", "show", "leader", "light", "voice",
            "wife", "whole", "police", "mind", "finally", "pull", "return", "free", "military", "price",
            "report", "less", "according", "decision", "explain", "son", "hope", "develop", "view", "relationship",
            "carry", "town", "road", "drive", "arm", "true", "federal", "break", "difference", "thank",
            "receive", "value", "international", "building", "action", "full", "model", "join", "season", "society",
            "tax", "director", "position", "player", "agree", "record", "paper", "space", "ground", "form",
            "support", "event", "official", "whose", "matter", "center", "couple", "site", "project", "activity",
            "star", "table", "court", "produce", "eat", "teach", "oil", "half", "situation", "easy",
            "cost", "industry", "figure", "face", "street", "image", "itself", "phone", "either", "data",
            "cover", "quite", "picture", "clear", "practice", "piece", "land", "recent", "describe", "product",
            "doctor", "wall", "patient", "worker", "news", "test", "movie", "certain", "north", "love",
            "personal", "open", "support", "simply", "third", "technology", "catch", "step", "baby", "computer",
            "type", "attention", "draw", "film", "republican", "tree", "source", "red", "nearly", "organization",
            "choose", "cause", "hair", "century", "evidence", "window", "difficult", "listen", "soon", "culture",
            "billion", "chance", "brother", "energy", "period", "course", "summer", "less", "realize", "hundred",
            "available", "plant", "likely", "opportunity", "term", "short", "letter", "condition", "choice", "place",
            "single", "rule", "daughter", "administration", "south", "husband", "congress", "floor", "campaign", "material",
            "population", "well", "call", "economy", "medical", "hospital", "church", "close", "thousand", "risk",
            "current", "fire", "future", "wrong", "involve", "defense", "anyone", "increase", "security", "bank",
            "myself", "certainly", "west", "sport", "board", "seek", "per", "subject", "officer", "private",
            "rest", "behavior", "deal", "performance", "fight", "throw", "top", "quickly", "past", "goal",
            "second", "bed", "order", "author", "fill", "represent", "focus", "foreign", "drop", "plan",
            "blood", "upon", "agency", "push", "nature", "color", "store", "reduce", "sound", "note",
            "fine", "before", "near", "movement", "page", "enter", "share", "than", "common", "poor",
            "natural", "race", "concern", "series", "significant", "similar", "hot", "language", "each", "usually",
            "response", "dead", "rise", "animal", "factor", "decade", "article", "shoot", "east", "save",
            "seven", "artist", "away", "scene", "stock", "career", "despite", "central", "eight", "thus",
            "treatment", "beyond", "happy", "exactly", "protect", "approach", "lie", "size", "dog", "fund",
            "serious", "occur", "media", "ready", "sign", "thought", "list", "individual", "simple", "quality",
            "pressure", "accept", "answer", "hard", "resource", "identify", "left", "meeting", "determine", "prepare",
            "disease", "whatever", "success", "argue", "cup", "particularly", "amount", "ability", "staff", "recognize",
            "indicate", "character", "growth", "loss", "degree", "wonder", "attack", "herself", "region", "television",
            "box", "training", "pretty", "trade", "deal", "election", "everybody", "physical", "lay", "general",
            "feeling", "standard", "bill", "message", "fail", "outside", "arrive", "analysis", "benefit", "name",
            "sex", "forward", "lawyer", "present", "section", "environmental", "glass", "answer", "skill", "sister",
            "rules", "agency", "manager", "nature", "range", "image", "employee", "movie", "everyone", "memory",
            "social", "within", "science", "compare", "game", "others", "including", "consider", "appear", "actually",
            "buy", "probably", "human", "wait", "serve", "market", "die", "send", "expect", "sense",
            "build", "stay", "fall", "nation", "itself", "tool", "fast", "exist", "dark", "claim"
    };

    private enum GameMode {
        SECONDS("time", 30),
        WORDS("words", 10);

        final String name;
        final int defaultValue;

        GameMode(String name, int defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }
    }

    private enum Language {
        RUSSIAN("russian", RUSSIAN_WORDS),
        ENGLISH("english", ENGLISH_WORDS);

        final String name;
        final String[] words;

        Language(String name, String[] words) {
            this.name = name;
            this.words = words;
        }
    }

    private static class TestResult {
        final long timestamp;
        final double wpm;
        final double rawWpm;
        final double accuracy;
        final int correctChars;
        final int totalChars;
        final int errors;
        final Language language;
        final GameMode mode;
        final List<Double> wpmHistory;
        final List<Double> accuracyHistory;

        TestResult(double wpm, double rawWpm, double accuracy, int correctChars, int totalChars, int errors,
                   Language language, GameMode mode, List<Double> wpmHistory, List<Double> accuracyHistory) {
            this.timestamp = System.currentTimeMillis();
            this.wpm = wpm;
            this.rawWpm = rawWpm;
            this.accuracy = accuracy;
            this.correctChars = correctChars;
            this.totalChars = totalChars;
            this.errors = errors;
            this.language = language;
            this.mode = mode;
            this.wpmHistory = new ArrayList<>(wpmHistory);
            this.accuracyHistory = new ArrayList<>(accuracyHistory);
        }
    }

    private static class KeyWeight {
        String word;
        int errors;
        int occurrences;

        KeyWeight(String word) {
            this.word = word;
            this.errors = 0;
            this.occurrences = 0;
        }

        double getWeight() {
            if (occurrences == 0) return 1.0;
            return 1.0 + (errors / (double) occurrences) * 3.0;
        }
    }

    private static class CharacterState {
        char character;
        boolean correct;
        boolean typed;
        Animation colorAnimation = new Animation();
        Animation scaleAnimation = new Animation();

        CharacterState(char character) {
            this.character = character;
            this.correct = false;
            this.typed = false;
            this.colorAnimation.set(0.0);
            this.scaleAnimation.set(0.0);
        }
    }

    private final Animation openAnimation = new Animation();
    private final Animation closeAnimation = new Animation();
    private final Animation resultAnimation = new Animation();
    private final Animation cursorBlinkAnimation = new Animation();
    private final Animation progressBarAnimation = new Animation();
    private final Map<String, Animation> keyPressAnimations = new HashMap<>();
    private final Map<String, Animation> keyHighlightAnimations = new HashMap<>();
    private final Map<String, Animation> elementAnimations = new HashMap<>();
    private boolean closing = false;
    private long cursorBlinkStart = 0;

    private Language currentLanguage = Language.ENGLISH;
    private GameMode currentMode = GameMode.WORDS;
    private final List<String> testWords = new ArrayList<>();
    private final List<List<CharacterState>> wordStates = new ArrayList<>();
    private int currentWordIndex = 0;
    private int currentCharIndex = 0;
    private boolean gameStarted = false;
    private long startTime = 0;
    private long endTime = 0;
    private boolean gameFinished = false;
    private boolean showingResult = false;

    private int correctChars = 0;
    private int incorrectChars = 0;
    private int totalTypedChars = 0;
    private final Map<String, KeyWeight> wordWeights = new HashMap<>();
    private final List<TestResult> sessionHistory = new ArrayList<>();
    private boolean showHistory = false;
    private final List<Double> wpmOverTime = new ArrayList<>();
    private final List<Double> accuracyOverTime = new ArrayList<>();
    private long lastWpmUpdate = 0;

    private String currentInput = "";
    private float targetProgressBarValue = 0;
    private int completedWords = 0;

    private static final String[][] KEYBOARD_LAYOUT = {
            {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"},
            {"a", "s", "d", "f", "g", "h", "j", "k", "l"},
            {"z", "x", "c", "v", "b", "n", "m"}
    };

    public TypeSpeedGui() {
        super(new StringTextComponent("Type Speed Test"));
    }

    @Override
    protected void init() {
        super.init();
        this.openAnimation.run(1.0, 0.6, Easings.EXPO_OUT);
        this.closeAnimation.set(0.0);
        this.resultAnimation.set(0.0);
        this.progressBarAnimation.set(0.0);
        this.closing = false;
        this.cursorBlinkStart = System.currentTimeMillis();
        initializeWordWeights();
        generateTest();

        elementAnimations.put("stats", new Animation());
        elementAnimations.put("text", new Animation());
        elementAnimations.put("keyboard", new Animation());
        elementAnimations.put("topbar", new Animation());

        elementAnimations.get("topbar").run(1.0, 0.4, Easings.EXPO_OUT);
        elementAnimations.get("stats").run(1.0, 0.5, Easings.EXPO_OUT);
        elementAnimations.get("text").run(1.0, 0.6, Easings.EXPO_OUT);
        elementAnimations.get("keyboard").run(1.0, 0.7, Easings.EXPO_OUT);
    }

    private void initializeWordWeights() {
        for (String word : currentLanguage.words) {
            wordWeights.putIfAbsent(word, new KeyWeight(word));
        }
    }

    private void renderTextArea(MatrixStack matrix, float centerX, float y, int clientColor, float alpha) {
        float fontSize = 28f;
        float spaceWidth = Fonts.SF_BOLD.getWidth(" ", fontSize);
        float maxWidth = 800;
        float lineHeight = 45;
        int maxLines = 3;

        List<List<Integer>> lines = new ArrayList<>();
        List<Integer> currentLine = new ArrayList<>();
        float currentWidth = 0;

        int visibleWords = Math.min(testWords.size(), 100);
        for (int i = 0; i < visibleWords; i++) {
            String word = testWords.get(i);
            float wordWidth = Fonts.SF_BOLD.getWidth(word, fontSize);

            if (currentWidth + wordWidth > maxWidth && !currentLine.isEmpty()) {
                lines.add(new ArrayList<>(currentLine));
                currentLine.clear();
                currentWidth = 0;
            }

            currentLine.add(i);
            currentWidth += wordWidth + spaceWidth;
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }

        float startY = y - (Math.min(lines.size(), maxLines) * lineHeight) / 2f;

        for (int lineIdx = 0; lineIdx < Math.min(lines.size(), maxLines); lineIdx++) {
            List<Integer> wordIndices = lines.get(lineIdx);

            float lineWidth = 0;
            for (int wordIdx : wordIndices) {
                String word = testWords.get(wordIdx);
                lineWidth += Fonts.SF_BOLD.getWidth(word, fontSize) + spaceWidth;
            }

            float currentX = centerX - lineWidth / 2f;
            float currentY = startY + lineIdx * lineHeight;

            for (int wordIdx : wordIndices) {
                if (wordIdx >= testWords.size()) break;

                String word = testWords.get(wordIdx);
                List<CharacterState> charStates = wordStates.get(wordIdx);
                boolean isCurrentWord = wordIdx == currentWordIndex;

                for (int charIdx = 0; charIdx < charStates.size(); charIdx++) {
                    CharacterState state = charStates.get(charIdx);
                    String charStr = String.valueOf(state.character);
                    float charWidth = Fonts.SF_BOLD.getWidth(charStr, fontSize);

                    int color;
                    if (!state.typed) {
                        color = new Color(80, 80, 80).getRGB();
                        state.colorAnimation.run(0.0, 0.2, Easings.EXPO_OUT);
                    } else if (state.correct) {
                        color = ColorUtil.WHITE;
                        state.colorAnimation.run(1.0, 0.3, Easings.EXPO_OUT);
                    } else {
                        color = ColorUtil.RED;
                        state.colorAnimation.run(1.0, 0.3, Easings.EXPO_OUT);
                    }

                    float animProgress = state.colorAnimation.get();
                    if (state.typed) {
                        matrix.push();
                        float scale = 0.8f + animProgress * 0.2f;
                        matrix.translate(currentX + charWidth / 2f, currentY + fontSize / 2f, 0);
                        matrix.scale(scale, scale, 1);
                        matrix.translate(-(currentX + charWidth / 2f), -(currentY + fontSize / 2f), 0);
                        Fonts.SF_BOLD.draw(matrix, charStr, currentX, currentY, ColorUtil.multAlpha(color, alpha), fontSize);
                        matrix.pop();
                    } else {
                        Fonts.SF_BOLD.draw(matrix, charStr, currentX, currentY, ColorUtil.multAlpha(color, alpha), fontSize);
                    }

                    if (isCurrentWord && charIdx == currentCharIndex) {
                        renderCursor(matrix, currentX - 2, currentY - 3, fontSize + 6, clientColor, alpha);
                    }

                    currentX += charWidth;
                }

                if (isCurrentWord && currentCharIndex >= word.length()) {
                    renderCursor(matrix, currentX + spaceWidth / 2f - 2, currentY - 3, fontSize + 6, clientColor, alpha);
                }

                currentX += spaceWidth;
            }
        }
    }


    private void generateTest() {
        testWords.clear();
        wordStates.clear();
        wpmOverTime.clear();
        accuracyOverTime.clear();
        currentInput = "";
        completedWords = 0;

        List<String> availableWords = new ArrayList<>(Arrays.asList(currentLanguage.words));
        Random random = new Random();

        int wordsToGenerate = currentMode == GameMode.WORDS ? currentMode.defaultValue : 1000;

        for (int i = 0; i < wordsToGenerate; i++) {
            String word = selectWeightedWord(availableWords, random);
            testWords.add(word);

            List<CharacterState> charStates = new ArrayList<>();
            for (char c : word.toCharArray()) {
                charStates.add(new CharacterState(c));
            }
            wordStates.add(charStates);
        }

        currentWordIndex = 0;
        currentCharIndex = 0;
        correctChars = 0;
        incorrectChars = 0;
        totalTypedChars = 0;
        gameStarted = false;
        gameFinished = false;
        showingResult = false;
        startTime = 0;
        endTime = 0;
        lastWpmUpdate = 0;
        targetProgressBarValue = 0;
        progressBarAnimation.set(0.0);

        updateNextKeyHighlight();
    }

    private String selectWeightedWord(List<String> words, Random random) {
        double totalWeight = words.stream()
                .mapToDouble(w -> wordWeights.getOrDefault(w, new KeyWeight(w)).getWeight())
                .sum();

        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (String word : words) {
            currentWeight += wordWeights.getOrDefault(word, new KeyWeight(word)).getWeight();
            if (randomValue <= currentWeight) {
                wordWeights.get(word).occurrences++;
                return word;
            }
        }

        return words.get(random.nextInt(words.size()));
    }

    private void updateNextKeyHighlight() {
        for (Animation anim : keyHighlightAnimations.values()) {
            anim.run(0.0, 0.2, Easings.EXPO_OUT);
        }

        if (currentWordIndex < testWords.size()) {
            String word = testWords.get(currentWordIndex);
            if (currentCharIndex < word.length()) {
                char nextChar = word.charAt(currentCharIndex);
                String key = String.valueOf(nextChar).toLowerCase();

                Animation highlightAnim = keyHighlightAnimations.computeIfAbsent(key, k -> {
                    Animation a = new Animation();
                    a.set(0.0);
                    return a;
                });
                highlightAnim.run(1.0, 0.3, Easings.EXPO_OUT);
            } else {
                Animation spaceAnim = keyHighlightAnimations.computeIfAbsent("Space", k -> {
                    Animation a = new Animation();
                    a.set(0.0);
                    return a;
                });
                spaceAnim.run(1.0, 0.3, Easings.EXPO_OUT);
            }
        }
    }

    @Override
    public void onClose() {
        if (this.closing) return;
        this.closing = true;
        this.closeAnimation.run(1.0, 0.3, Easings.EXPO_IN);
        this.openAnimation.run(0.0, 0.3, Easings.EXPO_IN);
    }

    private void restartTest() {
        generateTest();
        resultAnimation.set(0.0);

        elementAnimations.get("topbar").set(0.0);
        elementAnimations.get("stats").set(0.0);
        elementAnimations.get("text").set(0.0);
        elementAnimations.get("keyboard").set(0.0);

        elementAnimations.get("topbar").run(1.0, 0.4, Easings.EXPO_OUT);
        elementAnimations.get("stats").run(1.0, 0.5, Easings.EXPO_OUT);
        elementAnimations.get("text").run(1.0, 0.6, Easings.EXPO_OUT);
        elementAnimations.get("keyboard").run(1.0, 0.7, Easings.EXPO_OUT);
    }

    private void finishTest() {
        if (!gameFinished && gameStarted) {
            gameFinished = true;
            endTime = System.currentTimeMillis();

            double timeSeconds = (endTime - startTime) / 1000.0;
            double wpm = (correctChars / 5.0) / (timeSeconds / 60.0);
            double rawWpm = (totalTypedChars / 5.0) / (timeSeconds / 60.0);
            double accuracy = totalTypedChars > 0 ? (correctChars / (double) totalTypedChars) * 100 : 0;

            TestResult result = new TestResult(wpm, rawWpm, accuracy, correctChars, totalTypedChars,
                    incorrectChars, currentLanguage, currentMode, wpmOverTime, accuracyOverTime);
            sessionHistory.add(result);

            showingResult = true;
            resultAnimation.run(1.0, 0.5, Easings.EXPO_OUT);
        }
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (closing && closeAnimation.isFinished() && openAnimation.isFinished()) {
            mc.displayScreen(null);
            return;
        }

        fill(matrix, 0, 0, this.width, this.height, new Color(23, 23, 23).getRGB());

        openAnimation.update();
        closeAnimation.update();
        resultAnimation.update();
        progressBarAnimation.update();

        for (Animation anim : keyPressAnimations.values()) {
            anim.update();
        }
        for (Animation anim : keyHighlightAnimations.values()) {
            anim.update();
        }
        for (Animation anim : elementAnimations.values()) {
            anim.update();
        }

        for (List<CharacterState> states : wordStates) {
            for (CharacterState state : states) {
                state.colorAnimation.update();
                state.scaleAnimation.update();
            }
        }

        Theme theme = Theme.getInstance();
        int clientColor = theme.clientColor();

        float easedAnim = openAnimation.get() * (1.0f - closeAnimation.get());
        if (easedAnim <= 0.01f) return;

        matrix.push();

        if (showHistory) {
            renderHistory(matrix, mouseX, mouseY, clientColor);
        } else if (showingResult && resultAnimation.get() > 0.01f) {
            renderResult(matrix, mouseX, mouseY, clientColor);
        } else {
            renderMainGame(matrix, mouseX, mouseY, clientColor);
        }

        matrix.pop();
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    private void renderMainGame(MatrixStack matrix, int mouseX, int mouseY, int clientColor) {
        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        float topbarAlpha = elementAnimations.get("topbar").get();
        if (topbarAlpha > 0.01f) {
            matrix.push();
            matrix.translate(centerX, 30, 0);
            matrix.scale(0.95f + topbarAlpha * 0.05f, 0.95f + topbarAlpha * 0.05f, 1);
            matrix.translate(-centerX, -30, 0);
            renderTopBar(matrix, centerX, 30, mouseX, mouseY, clientColor, topbarAlpha);
            matrix.pop();
        }

        if (gameStarted && !gameFinished) {
            updateWpmTracking();
        }

        float statsAlpha = elementAnimations.get("stats").get();
        if (statsAlpha > 0.01f) {
            matrix.push();
            matrix.translate(centerX, centerY - 200, 0);
            matrix.scale(0.9f + statsAlpha * 0.1f, 0.9f + statsAlpha * 0.1f, 1);
            matrix.translate(-centerX, -(centerY - 200), 0);
            renderStats(matrix, centerX, centerY - 200, clientColor, statsAlpha);
            matrix.pop();
        }

        float textAlpha = elementAnimations.get("text").get();
        if (textAlpha > 0.01f) {
            renderTextArea(matrix, centerX, centerY - 20, clientColor, textAlpha);
        }

        if (gameStarted || gameFinished) {
            renderProgressBar(matrix, centerX, centerY + 60);
        }

        float keyboardAlpha = elementAnimations.get("keyboard").get();
        if (keyboardAlpha > 0.01f) {
            matrix.push();
            matrix.translate(centerX, this.height - 200, 0);
            matrix.scale(0.95f + keyboardAlpha * 0.05f, 0.95f + keyboardAlpha * 0.05f, 1);
            matrix.translate(-centerX, -(this.height - 200), 0);
            renderKeyboard(matrix, centerX, this.height - 200, clientColor, keyboardAlpha);
            matrix.pop();
        }
    }

    private void renderTopBar(MatrixStack matrix, float centerX, float y, int mouseX, int mouseY, int clientColor, float alpha) {
        float iconSize = 16;
        float spacing = 80;
        float langX = centerX - spacing;
        float modeX = centerX + spacing;

        boolean langHovered = mouseX >= langX - 25 && mouseX <= langX + 50 && mouseY >= y && mouseY <= y + iconSize;
        int langColor = ColorUtil.multAlpha(langHovered ? clientColor : ColorUtil.GRAY, alpha);
        Fonts.SF_BOLD.draw(matrix, currentLanguage.name, langX, y, langColor, 10f);

        boolean modeHovered = mouseX >= modeX - 25 && mouseX <= modeX + 50 && mouseY >= y && mouseY <= y + iconSize;
        int modeColor = ColorUtil.multAlpha(modeHovered ? clientColor : ColorUtil.GRAY, alpha);
        Fonts.SF_BOLD.draw(matrix, currentMode.name, modeX, y, modeColor, 10f);

        float restartX = this.width - 80;
        boolean restartHovered = mouseX >= restartX && mouseX <= restartX + 60 && mouseY >= y && mouseY <= y + iconSize;
        int restartColor = ColorUtil.multAlpha(restartHovered ? clientColor : ColorUtil.GRAY, alpha);
        Fonts.icon.draw(matrix, "r", restartX, y - 2, restartColor, 18f);

        float historyX = 30;
        boolean historyHovered = mouseX >= historyX && mouseX <= historyX + 60 && mouseY >= y && mouseY <= y + iconSize;
        int historyColor = ColorUtil.multAlpha(historyHovered ? clientColor : ColorUtil.GRAY, alpha);
        Fonts.SF_BOLD.draw(matrix, "history", historyX, y, historyColor, 10f);
    }

    private void updateWpmTracking() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastWpmUpdate >= 1000) {
            double elapsed = (currentTime - startTime) / 1000.0;
            if (elapsed > 0) {
                double currentWpm = (correctChars / 5.0) / (elapsed / 60.0);
                wpmOverTime.add(currentWpm);

                double currentAccuracy = totalTypedChars > 0 ? (correctChars / (double) totalTypedChars) * 100 : 100;
                accuracyOverTime.add(currentAccuracy);
            }
            lastWpmUpdate = currentTime;

            if (currentMode == GameMode.SECONDS) {
                double remaining = currentMode.defaultValue - elapsed;
                if (remaining <= 0) {
                    finishTest();
                }
            }
        }

        if (currentMode == GameMode.WORDS) {
            targetProgressBarValue = Math.min(1.0f, completedWords / (float) currentMode.defaultValue);
        } else {
            if (gameStarted) {
                long currentTimestamp = System.currentTimeMillis();
                double elapsed = (currentTimestamp - startTime) / 1000.0;
                targetProgressBarValue = Math.min(1.0f, (float) (elapsed / currentMode.defaultValue));
            }
        }

        float currentProgress = progressBarAnimation.get();
        if (Math.abs(targetProgressBarValue - currentProgress) > 0.001f) {
            progressBarAnimation.run(targetProgressBarValue, 0.3, Easings.EXPO_OUT);
        }
    }

    private void renderStats(MatrixStack matrix, float centerX, float y, int clientColor, float alpha) {
        String wpmText = "0";
        String accText = "100%";

        if (gameStarted) {
            long currentTime = System.currentTimeMillis();
            double elapsed = (currentTime - startTime) / 1000.0;

            if (elapsed > 0) {
                double wpm = (correctChars / 5.0) / (elapsed / 60.0);
                wpmText = String.format("%.0f", Math.max(0, wpm));
            }

            if (totalTypedChars > 0) {
                double accuracy = (correctChars / (double) totalTypedChars) * 100;
                accText = String.format("%.0f%%", accuracy);
            }
        }

        float wpmWidth = Fonts.SF_BOLD.getWidth(wpmText, 48f);
        Fonts.SF_BOLD.draw(matrix, wpmText, centerX - wpmWidth / 2f, y, ColorUtil.multAlpha(clientColor, alpha), 48f);

        float accWidth = Fonts.SF_BOLD.getWidth(accText, 20f);
        Fonts.SF_BOLD.draw(matrix, accText, centerX - accWidth / 2f, y + 55,
                ColorUtil.multAlpha(new Color(100, 100, 100).getRGB(), alpha), 20f);

        String wordsText = completedWords + " words";
        float wordsWidth = Fonts.SF_BOLD.getWidth(wordsText, 14f);
        Fonts.SF_BOLD.draw(matrix, wordsText, centerX - wordsWidth / 2f - 150, y + 30,
                ColorUtil.multAlpha(new Color(120, 120, 120).getRGB(), alpha), 14f);
    }


    private void renderCursor(MatrixStack matrix, float x, float y, float height, int clientColor, float alpha) {
        if (!gameStarted || gameFinished) {
            long time = System.currentTimeMillis() - cursorBlinkStart;
            float blinkCycle = (time % 1000) / 1000.0f;
            float blinkAlpha = (float) (0.4 + 0.6 * Math.abs(Math.sin(blinkCycle * Math.PI)));
            alpha *= blinkAlpha;
        }

        float cursorWidth = 3.5f;
        RenderUtil.Rounded.roundedRect(matrix, x, y, cursorWidth, height,
                ColorUtil.multAlpha(clientColor, alpha), Round.of(1.5f));
    }

    private void renderProgressBar(MatrixStack matrix, float centerX, float y) {
        float barWidth = 600;
        float barHeight = 4;
        float barX = centerX - barWidth / 2f;

        float progress = progressBarAnimation.get();

        RenderUtil.Rounded.roundedRect(matrix, barX, y, barWidth, barHeight,
                new Color(40, 40, 40).getRGB(), Round.of(2f));
        if (progress > 0) {
            RenderUtil.Rounded.roundedRect(matrix, barX, y, barWidth * progress, barHeight,
                    Theme.getInstance().clientColor(), Round.of(2f));
        }
    }

    private void renderKeyboard(MatrixStack matrix, float centerX, float y, int clientColor, float alpha) {
        float keySize = 40;
        float keySpacing = 7;
        float currentY = y;

        for (String[] row : KEYBOARD_LAYOUT) {
            float rowWidth = (keySize + keySpacing) * row.length - keySpacing;
            float currentX = centerX - rowWidth / 2f;

            for (String key : row) {
                Animation pressAnim = keyPressAnimations.computeIfAbsent(key, k -> {
                    Animation a = new Animation();
                    a.set(0.0);
                    return a;
                });

                Animation highlightAnim = keyHighlightAnimations.computeIfAbsent(key, k -> {
                    Animation a = new Animation();
                    a.set(0.0);
                    return a;
                });

                float pressValue = pressAnim.get();
                float highlightValue = highlightAnim.get();

                int baseColor = new Color(40, 40, 40).getRGB();
                int pressColor = ColorUtil.interpolate(baseColor, clientColor, pressValue * 0.8f);
                int finalColor = ColorUtil.interpolate(pressColor, clientColor, highlightValue * 0.4f);

                RenderUtil.Rounded.roundedRect(matrix, currentX, currentY, keySize, keySize,
                        ColorUtil.multAlpha(finalColor, alpha), Round.of(5));

                String displayKey = key;
                if (currentLanguage == Language.RUSSIAN) {
                    displayKey = getRussianKey(key);
                }

                float textWidth = Fonts.SF_BOLD.getWidth(displayKey, 12f);
                int textColor = highlightValue > 0.5f ? clientColor : ColorUtil.GRAY;
                Fonts.SF_BOLD.draw(matrix, displayKey, currentX + keySize / 2f - textWidth / 2f,
                        currentY + keySize / 2f - 5, ColorUtil.multAlpha(textColor, alpha), 12f);

                currentX += keySize + keySpacing;
            }

            currentY += keySize + keySpacing;
        }

        float spaceWidth = 280;
        float spaceX = centerX - spaceWidth / 2f;
        float spaceY = currentY;

        Animation spacePress = keyPressAnimations.computeIfAbsent("Space", k -> {
            Animation a = new Animation();
            a.set(0.0);
            return a;
        });

        Animation spaceHighlight = keyHighlightAnimations.computeIfAbsent("Space", k -> {
            Animation a = new Animation();
            a.set(0.0);
            return a;
        });

        int baseColor = new Color(40, 40, 40).getRGB();
        int pressColor = ColorUtil.interpolate(baseColor, clientColor, spacePress.get() * 0.8f);
        int finalColor = ColorUtil.interpolate(pressColor, clientColor, spaceHighlight.get() * 0.4f);

        RenderUtil.Rounded.roundedRect(matrix, spaceX, spaceY, spaceWidth, keySize,
                ColorUtil.multAlpha(finalColor, alpha), Round.of(5));
    }

    private void renderResult(MatrixStack matrix, int mouseX, int mouseY, int clientColor) {
        if (sessionHistory.isEmpty()) return;

        TestResult result = sessionHistory.get(sessionHistory.size() - 1);
        float alpha = resultAnimation.get();

        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;

        matrix.push();
        matrix.translate(centerX, centerY, 0);
        float scale = 0.95f + alpha * 0.05f;
        matrix.scale(scale, scale, 1);
        matrix.translate(-centerX, -centerY, 0);

        float contentY = centerY - 200;

        String wpmText = String.format("%.0f", result.wpm);
        float wpmWidth = Fonts.SF_BOLD.getWidth(wpmText, 72f);
        Fonts.SF_BOLD.draw(matrix, wpmText, centerX - wpmWidth / 2f, contentY, ColorUtil.multAlpha(clientColor, alpha), 72f);

        Fonts.SF_BOLD.draw(matrix, "wpm", centerX - Fonts.SF_BOLD.getWidth("wpm", 20f) / 2f, contentY + 80,
                ColorUtil.multAlpha(ColorUtil.GRAY, alpha), 20f);

        contentY += 140;

        float statSpacing = 150;
        float stat1X = centerX - statSpacing;
        float stat2X = centerX + statSpacing;

        Fonts.SF_BOLD.draw(matrix, "acc", stat1X - 20, contentY, ColorUtil.multAlpha(ColorUtil.GRAY, alpha), 12f);
        String accText = String.format("%.0f%%", result.accuracy);
        Fonts.SF_BOLD.draw(matrix, accText, stat1X - 20, contentY + 20, ColorUtil.multAlpha(ColorUtil.WHITE, alpha), 24f);

        Fonts.SF_BOLD.draw(matrix, "raw", stat2X - 15, contentY, ColorUtil.multAlpha(ColorUtil.GRAY, alpha), 12f);
        String rawText = String.format("%.0f", result.rawWpm);
        Fonts.SF_BOLD.draw(matrix, rawText, stat2X - 15, contentY + 20, ColorUtil.multAlpha(ColorUtil.WHITE, alpha), 24f);

        contentY += 80;

        Fonts.SF_BOLD.draw(matrix, "characters", centerX - Fonts.SF_BOLD.getWidth("characters", 12f) / 2f, contentY,
                ColorUtil.multAlpha(ColorUtil.GRAY, alpha), 12f);
        String charsText = String.format("%d / %d / %d", result.correctChars, result.errors, result.totalChars);
        Fonts.SF_BOLD.draw(matrix, charsText, centerX - Fonts.SF_BOLD.getWidth(charsText, 16f) / 2f, contentY + 20,
                ColorUtil.multAlpha(ColorUtil.WHITE, alpha), 16f);

        contentY += 60;

        if (!result.wpmHistory.isEmpty()) {
            renderDetailedGraph(matrix, centerX - 300, contentY, 600, 120, result.wpmHistory,
                    result.accuracyHistory, clientColor, alpha, mouseX, mouseY);
        }

        float buttonY = centerY + 220;
        float buttonWidth = 120;
        float buttonHeight = 35;
        float buttonX = centerX - buttonWidth / 2f;

        boolean buttonHovered = mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
        int buttonColor = ColorUtil.multAlpha(buttonHovered ? clientColor : new Color(50, 50, 50).getRGB(), alpha);

        RenderUtil.Rounded.roundedRect(matrix, buttonX, buttonY, buttonWidth, buttonHeight, buttonColor, Round.of(6));
        Fonts.SF_BOLD.draw(matrix, "next test", buttonX + buttonWidth / 2f - Fonts.SF_BOLD.getWidth("next test", 11f) / 2f,
                buttonY + 12, ColorUtil.multAlpha(ColorUtil.WHITE, alpha), 11f);

        matrix.pop();
    }

    private void renderDetailedGraph(MatrixStack matrix, float x, float y, float width, float height,
                                     List<Double> wpmData, List<Double> accData, int clientColor,
                                     float alpha, int mouseX, int mouseY) {
        if (wpmData.isEmpty()) return;

        double maxWpm = wpmData.stream().mapToDouble(d -> d).max().orElse(100);
        double minWpm = Math.max(0, wpmData.stream().mapToDouble(d -> d).min().orElse(0) - 10);
        double range = maxWpm - minWpm;
        if (range < 20) {
            range = 20;
            maxWpm = minWpm + range;
        }

        int bgColor = ColorUtil.multAlpha(new Color(30, 30, 30).getRGB(), alpha);
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, bgColor, Round.of(8));

        float padding = 40;
        float graphX = x + padding;
        float graphY = y + 10;
        float graphWidth = width - padding - 10;
        float graphHeight = height - 30;

        Fonts.SF_BOLD.draw(matrix, String.format("%.0f", maxWpm), x + 5, graphY,
                ColorUtil.multAlpha(ColorUtil.GRAY, alpha), 9f);
        Fonts.SF_BOLD.draw(matrix, String.format("%.0f", (maxWpm + minWpm) / 2), x + 5, graphY + graphHeight / 2f,
                ColorUtil.multAlpha(ColorUtil.GRAY, alpha), 9f);
        Fonts.SF_BOLD.draw(matrix, String.format("%.0f", minWpm), x + 5, graphY + graphHeight,
                ColorUtil.multAlpha(ColorUtil.GRAY, alpha), 9f);

        if (wpmData.size() < 2) return;

        float stepX = graphWidth / (wpmData.size() - 1);
        int hoveredIndex = -1;

        for (int i = 0; i < wpmData.size() - 1; i++) {
            double wpm1 = wpmData.get(i);
            double wpm2 = wpmData.get(i + 1);

            float x1 = graphX + i * stepX;
            float y1 = graphY + graphHeight - (float) ((wpm1 - minWpm) / range * graphHeight);
            float x2 = graphX + (i + 1) * stepX;
            float y2 = graphY + graphHeight - (float) ((wpm2 - minWpm) / range * graphHeight);

            int lineColor = ColorUtil.multAlpha(clientColor, alpha * 0.8f);
            RenderUtil.Rounded.drawLine(matrix, x1, y1, x2, y2, 2.5f, lineColor);

            if (mouseX >= x1 - 5 && mouseX <= x1 + 5 && mouseY >= graphY && mouseY <= graphY + graphHeight) {
                hoveredIndex = i;
            }
        }

        for (int i = 0; i < wpmData.size(); i++) {
            double wpm = wpmData.get(i);
            float pointX = graphX + i * stepX;
            float pointY = graphY + graphHeight - (float) ((wpm - minWpm) / range * graphHeight);

            float dotSize = 4;
            if (i == hoveredIndex) {
                dotSize = 6;
                RenderUtil.Rounded.roundedRect(matrix, pointX - dotSize / 2f, pointY - dotSize / 2f,
                        dotSize, dotSize, ColorUtil.multAlpha(clientColor, alpha), Round.of(dotSize / 2f));

                String tooltip = String.format("%.0f wpm", wpm);
                if (i < accData.size()) {
                    tooltip += String.format(" | %.0f%% acc", accData.get(i));
                }
                float tooltipWidth = Fonts.SF_BOLD.getWidth(tooltip, 10f) + 10;
                float tooltipHeight = 20;
                float tooltipX = pointX - tooltipWidth / 2f;
                float tooltipY = pointY - tooltipHeight - 10;

                RenderUtil.Rounded.roundedRect(matrix, tooltipX, tooltipY, tooltipWidth, tooltipHeight,
                        ColorUtil.multAlpha(new Color(40, 40, 40).getRGB(), alpha), Round.of(4));
                Fonts.SF_BOLD.draw(matrix, tooltip, tooltipX + 5, tooltipY + 6,
                        ColorUtil.multAlpha(ColorUtil.WHITE, alpha), 10f);
            } else {
                RenderUtil.Rounded.roundedRect(matrix, pointX - dotSize / 2f, pointY - dotSize / 2f,
                        dotSize, dotSize, ColorUtil.multAlpha(clientColor, alpha * 0.6f), Round.of(dotSize / 2f));
            }
        }
    }

    private void renderHistory(MatrixStack matrix, int mouseX, int mouseY, int clientColor) {
        float centerX = this.width / 2.0f;

        Fonts.SF_BOLD.draw(matrix, "test history", centerX - Fonts.SF_BOLD.getWidth("test history", 24f) / 2f, 50, clientColor, 24f);

        float backX = 40;
        float backY = 50;
        boolean backHovered = mouseX >= backX && mouseX <= backX + 60 && mouseY >= backY && mouseY <= backY + 20;
        int backColor = backHovered ? clientColor : ColorUtil.GRAY;
        Fonts.SF_BOLD.draw(matrix, "< back", backX, backY, backColor, 12f);

        if (sessionHistory.isEmpty()) {
            Fonts.SF_BOLD.draw(matrix, "no tests yet", centerX - Fonts.SF_BOLD.getWidth("no tests yet", 16f) / 2f,
                    this.height / 2.0f, ColorUtil.GRAY, 16f);
            return;
        }

        TestResult best = sessionHistory.stream().max(Comparator.comparingDouble(r -> r.wpm)).orElse(null);
        if (best != null) {
            String bestText = String.format("best: %.0f wpm", best.wpm);
            Fonts.SF_BOLD.draw(matrix, bestText, centerX - Fonts.SF_BOLD.getWidth(bestText, 14f) / 2f, 90, clientColor, 14f);
        }

        float listY = 130;
        float itemHeight = 100;
        float itemWidth = 700;
        float itemX = centerX - itemWidth / 2f;

        List<TestResult> reversed = new ArrayList<>(sessionHistory);
        Collections.reverse(reversed);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.makeScissorBox(itemX, listY, itemX + itemWidth, this.height - 50);

        for (int i = 0; i < reversed.size(); i++) {
            TestResult result = reversed.get(i);
            float itemY = listY + i * (itemHeight + 15);

            if (itemY > this.height - 50) break;

            RenderUtil.Rounded.roundedRect(matrix, itemX, itemY, itemWidth, itemHeight, new Color(30, 30, 30).getRGB(), Round.of(8));

            float contentX = itemX + 25;
            float contentY = itemY + 20;

            Fonts.SF_BOLD.draw(matrix, String.format("%.0f wpm", result.wpm), contentX, contentY, clientColor, 24f);
            Fonts.SF_BOLD.draw(matrix, String.format("acc %.0f%% | raw %.0f", result.accuracy, result.rawWpm),
                    contentX, contentY + 30, ColorUtil.GRAY, 12f);
            Fonts.SF_BOLD.draw(matrix, result.language.name + " | " + result.mode.name,
                    contentX, contentY + 50, new Color(100, 100, 100).getRGB(), 10f);

            if (!result.wpmHistory.isEmpty() && result.wpmHistory.size() > 1) {
                renderWpmGraph(matrix, itemX + itemWidth - 220, itemY + 10, 200, 80, result.wpmHistory, clientColor, 1.0f);
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    private void renderWpmGraph(MatrixStack matrix, float x, float y, float width, float height,
                                List<Double> wpmData, int clientColor, float alpha) {
        if (wpmData.isEmpty()) return;

        double maxWpm = wpmData.stream().mapToDouble(d -> d).max().orElse(100);
        double minWpm = Math.max(0, wpmData.stream().mapToDouble(d -> d).min().orElse(0) - 10);
        double range = maxWpm - minWpm;
        if (range < 20) {
            range = 20;
            maxWpm = minWpm + range;
        }

        int bgColor = ColorUtil.multAlpha(new Color(30, 30, 30).getRGB(), alpha);
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, bgColor, Round.of(6));

        if (wpmData.size() < 2) return;

        float stepX = width / (wpmData.size() - 1);

        for (int i = 0; i < wpmData.size() - 1; i++) {
            double wpm1 = wpmData.get(i);
            double wpm2 = wpmData.get(i + 1);

            float x1 = x + i * stepX;
            float y1 = y + height - (float) ((wpm1 - minWpm) / range * (height - 10)) - 5;
            float x2 = x + (i + 1) * stepX;
            float y2 = y + height - (float) ((wpm2 - minWpm) / range * (height - 10)) - 5;

            int lineColor = ColorUtil.multAlpha(clientColor, alpha);
            RenderUtil.Rounded.drawLine(matrix, x1, y1, x2, y2, 2.5f, lineColor);
        }
    }

    private String getRussianKey(String key) {
        Map<String, String> layout = new HashMap<>();
        layout.put("q", "й");
        layout.put("w", "ц");
        layout.put("e", "у");
        layout.put("r", "к");
        layout.put("t", "е");
        layout.put("y", "н");
        layout.put("u", "г");
        layout.put("i", "ш");
        layout.put("o", "щ");
        layout.put("p", "з");
        layout.put("a", "ф");
        layout.put("s", "ы");
        layout.put("d", "в");
        layout.put("f", "а");
        layout.put("g", "п");
        layout.put("h", "р");
        layout.put("j", "о");
        layout.put("k", "л");
        layout.put("l", "д");
        layout.put("z", "я");
        layout.put("x", "ч");
        layout.put("c", "с");
        layout.put("v", "м");
        layout.put("b", "и");
        layout.put("n", "т");
        layout.put("m", "ь");
        return layout.getOrDefault(key, key);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        if (showHistory || showingResult || gameFinished) return false;

        if (!gameStarted) {
            gameStarted = true;
            startTime = System.currentTimeMillis();
            lastWpmUpdate = startTime;
            cursorBlinkStart = startTime;
        }

        if (currentWordIndex >= testWords.size()) return false;

        String currentWord = testWords.get(currentWordIndex);
        List<CharacterState> charStates = wordStates.get(currentWordIndex);

        if (character == ' ') {
            if (currentCharIndex >= currentWord.length()) {
                currentWordIndex++;
                currentCharIndex = 0;
                currentInput = "";
                completedWords++;

                if (currentMode == GameMode.WORDS && completedWords >= currentMode.defaultValue) {
                    finishTest();
                } else {
                    updateNextKeyHighlight();
                }
            }
            animateKeyPress("Space");
            return true;
        }

        totalTypedChars++;
        currentInput += character;

        if (currentCharIndex < charStates.size()) {
            CharacterState currentState = charStates.get(currentCharIndex);
            currentState.typed = true;
            currentState.scaleAnimation.set(0.0);
            currentState.scaleAnimation.run(1.0, 0.3, Easings.EXPO_OUT);

            char expected = currentWord.charAt(currentCharIndex);
            if (character == expected) {
                currentState.correct = true;
                correctChars++;
            } else {
                currentState.correct = false;
                incorrectChars++;
                KeyWeight weight = wordWeights.get(currentWord);
                if (weight != null) {
                    weight.errors++;
                }
            }

            currentCharIndex++;
            animateKeyPress(String.valueOf(character));
            updateNextKeyHighlight();
        }

        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (showHistory) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                showHistory = false;
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (showingResult) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (currentCharIndex > 0) {
                currentCharIndex--;

                if (currentWordIndex < wordStates.size()) {
                    List<CharacterState> charStates = wordStates.get(currentWordIndex);
                    if (currentCharIndex < charStates.size()) {
                        CharacterState state = charStates.get(currentCharIndex);

                        if (state.typed) {
                            if (!state.correct) {
                                incorrectChars--;
                            } else {
                                correctChars--;
                            }
                            totalTypedChars--;
                        }

                        state.typed = false;
                        state.correct = false;

                        if (currentInput.length() > 0) {
                            currentInput = currentInput.substring(0, currentInput.length() - 1);
                        }

                        updateNextKeyHighlight();
                    }
                }
            } else if (currentWordIndex > 0) {
                currentWordIndex--;
                completedWords = Math.max(0, completedWords - 1);
                String prevWord = testWords.get(currentWordIndex);
                currentCharIndex = prevWord.length();
                currentInput = prevWord;

                updateNextKeyHighlight();
            }
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_TAB && (modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
            restartTest();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void animateKeyPress(String key) {
        Animation anim = keyPressAnimations.computeIfAbsent(key, k -> {
            Animation a = new Animation();
            a.set(0.0);
            return a;
        });
        anim.run(1.0, 0.05, Easings.EXPO_OUT);
        anim.run(0.0, 0.15, Easings.EXPO_IN);
    }

    private void generateAdditionalWords() {
        List<String> availableWords = new ArrayList<>(Arrays.asList(currentLanguage.words));
        Random random = new Random();

        int wordsToAdd = 50;

        for (int i = 0; i < wordsToAdd; i++) {
            String word = selectWeightedWord(availableWords, random);
            testWords.add(word);

            List<CharacterState> charStates = new ArrayList<>();
            for (char c : word.toCharArray()) {
                charStates.add(new CharacterState(c));
            }
            wordStates.add(charStates);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float centerX = this.width / 2.0f;

        if (showHistory) {
            float backX = 40;
            float backY = 50;
            if (mouseX >= backX && mouseX <= backX + 60 && mouseY >= backY && mouseY <= backY + 20) {
                showHistory = false;
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if (showingResult) {
            float buttonY = this.height / 2.0f + 220;
            float buttonWidth = 120;
            float buttonX = centerX - buttonWidth / 2f;
            if (mouseX >= buttonX && mouseX <= buttonX + buttonWidth && mouseY >= buttonY && mouseY <= buttonY + 35) {
                restartTest();
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        float iconY = 30;
        float iconSize = 16;
        float spacing = 80;
        float langX = centerX - spacing;
        float modeX = centerX + spacing;

        if (mouseX >= langX - 25 && mouseX <= langX + 50 && mouseY >= iconY && mouseY <= iconY + iconSize) {
            currentLanguage = currentLanguage == Language.RUSSIAN ? Language.ENGLISH : Language.RUSSIAN;
            initializeWordWeights();
            restartTest();
            return true;
        }

        if (mouseX >= modeX - 25 && mouseX <= modeX + 50 && mouseY >= iconY && mouseY <= iconY + iconSize) {
            currentMode = currentMode == GameMode.SECONDS ? GameMode.WORDS : GameMode.SECONDS;
            restartTest();
            return true;
        }

        float restartX = this.width - 80;
        if (mouseX >= restartX && mouseX <= restartX + 60 && mouseY >= iconY && mouseY <= iconY + iconSize) {
            restartTest();
            return true;
        }

        float historyX = 30;
        if (mouseX >= historyX && mouseX <= historyX + 60 && mouseY >= iconY && mouseY <= iconY + iconSize) {
            showHistory = true;
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}