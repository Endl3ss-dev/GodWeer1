package godweer.utils.render.draw;

public final class Round {

    public final float topLeft;
    public final float topRight;
    public final float bottomRight;
    public final float bottomLeft;

    private Round(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.bottomLeft = bottomLeft;
    }

    public static Round of(float all) {
        return new Round(all, all, all, all);
    }

    public static Round of(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        return new Round(topLeft, topRight, bottomRight, bottomLeft);
    }

    public static Round zero() {
        return new Round(0, 0, 0, 0);
    }
}
