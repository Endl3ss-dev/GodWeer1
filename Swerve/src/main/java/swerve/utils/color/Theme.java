package swerve.utils.color;

public class Theme {

    private final String name;
    private final String hexCode;
    private final int[] colors;

    public Theme(String name, int... colors) {
        this.name = name;
        this.colors = colors;
        this.hexCode = createHexCode(colors.length == 0 ? -1 : colors[0]);
    }

    public String getName() {
        return this.name;
    }

    public int[] getColors() {
        return this.colors;
    }

    public String getHexCode() {
        return this.hexCode;
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

    private static String createHexCode(int color) {
        char[] hex = new char[7];
        hex[0] = '#';
        int rgb = color & 0x00FFFFFF;
        for (int i = 6; i > 0; i--) {
            int digit = rgb & 0xF;
            hex[i] = (char) (digit < 10 ? '0' + digit : 'A' + digit - 10);
            rgb >>>= 4;
        }
        return new String(hex);
    }
}
