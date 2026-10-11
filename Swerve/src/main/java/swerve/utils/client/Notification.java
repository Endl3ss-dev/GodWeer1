package swerve.utils.client;

// Запись уведомления по модели исходного клиента (Private):
// время жизни держит менеджер, а анимационные поля (появление, слайд, фейд, ширина)
// продвигает рендер один раз за кадр — так же, как Notification.render в оригинале.
// Экземпляры переиспользуются пулом NotificationManager.
public final class Notification {

    public enum Type {

        WARNING('A', 0xFFFFDC00),
        ERROR('B', 0xFFFF5555),
        INFO('C', 0xFF64C8FF),
        SUCCESS('D', 0xFF96FF96),
        MODULE('E', 0xFFFFFFFF);

        private final char icon;
        private final int color;

        Type(char icon, int color) {
            this.icon = icon;
            this.color = color;
        }

        public char getIcon() {
            return this.icon;
        }

        public int getColor() {
            return this.color;
        }
    }

    private static final float RELEASE_EPSILON = 0.05f;

    private String text = "";
    private String highlight = "";
    private Type type = Type.INFO;
    private long createdMs;
    private long durationMs;
    private float appear;
    private float targetAppear = 1.0f;
    private float slide;
    private float fade;
    private float animatedWidth;
    private float targetWidth;
    private float height = 18.0f;
    private boolean positive = true;

    public String getText() {
        return this.text;
    }

    public String getHighlight() {
        return this.highlight;
    }

    public Type getType() {
        return this.type;
    }

    public long getCreatedMs() {
        return this.createdMs;
    }

    public long getDurationMs() {
        return this.durationMs;
    }

    public float getAppear() {
        return this.appear;
    }

    public void setAppear(float appear) {
        this.appear = appear;
    }

    public float getTargetAppear() {
        return this.targetAppear;
    }

    public float getSlide() {
        return this.slide;
    }

    public void setSlide(float slide) {
        this.slide = slide;
    }

    public float getFade() {
        return this.fade;
    }

    public void setFade(float fade) {
        this.fade = fade;
    }

    public float getAnimatedWidth() {
        return this.animatedWidth;
    }

    public void setAnimatedWidth(float animatedWidth) {
        this.animatedWidth = animatedWidth;
    }

    public float getTargetWidth() {
        return this.targetWidth;
    }

    public void setTargetWidth(float targetWidth) {
        this.targetWidth = targetWidth;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isPositive() {
        return this.positive;
    }

    public boolean isExpired(long now) {
        return now - this.createdMs >= this.durationMs;
    }

    // Уведомление можно возвращать в пул, когда оно истекло и уже сжалось до невидимого состояния.
    public boolean isReleased() {
        return this.targetAppear == 0.0f && this.appear <= RELEASE_EPSILON;
    }

    boolean matches(String text, String highlight) {
        return this.text.equals(text) && this.highlight.equals(highlight);
    }

    void apply(String text, String highlight, Type type, long durationMs, long createdMs, boolean positive) {
        this.text = text;
        this.highlight = highlight;
        this.type = type;
        this.durationMs = durationMs;
        this.createdMs = createdMs;
        this.positive = positive;
        this.appear = 0.0f;
        this.targetAppear = 1.0f;
        this.slide = 0.0f;
        this.fade = 0.0f;
        this.animatedWidth = 0.0f;
        this.targetWidth = 0.0f;
        this.height = 18.0f;
    }

    // Повтор того же текста не создаёт новую плашку, а продлевает текущую.
    void refresh(long now, long durationMs, boolean positive) {
        this.createdMs = now;
        this.durationMs = durationMs;
        this.positive = positive;
        this.targetAppear = 1.0f;
    }

    void markReleased() {
        this.targetAppear = 0.0f;
    }

    void reset() {
        this.appear = 0.0f;
        this.targetAppear = 1.0f;
        this.slide = 0.0f;
        this.fade = 0.0f;
        this.animatedWidth = 0.0f;
        this.targetWidth = 0.0f;
    }
}
