package godweer.utils.color;

public class Theme {

    private final String name;
    private final int[] colors;

    public Theme(String name, int... colors) {
        this.name = name;
        this.colors = colors;
    }

    public String getName() {
        return this.name;
    }

    public int[] getColors() {
        return this.colors;
    }

    public boolean isRainbow() {
        return "Rainbow".equals(this.name);
    }

    public int getColor(int index) {
        if (isRainbow()) {
            return ColorUtils.rainbow(10, index, 0.6f, 1.0f, 1.0f);
        }
        return ColorUtils.gradient(5, index, this.colors);
    }

    public int getStaticColor() {
        return this.colors.length == 0 ? -1 : this.colors[0];
    }
}
