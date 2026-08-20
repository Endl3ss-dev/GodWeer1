package org.privates.client.utils.autobuy.gui;

import java.awt.*;

/**
 * Utility class for managing colors in the AutoBuy GUI
 */
public class ColorManager {
    private static Color currentColor = new Color(0, 0, 0, 150);

    /**
     * Get the current color
     *
     * @return the current color
     */
    public static Color getCurrentColor() {
        return currentColor;
    }

    /**
     * Set the current color
     *
     * @param color the new color
     */
    public static void setCurrentColor(Color color) {
        currentColor = color;
    }

    /**
     * Convert java.awt.Color to an integer representation
     *
     * @param color the color to convert
     * @return the integer representation of the color
     */
    public static int getColorInt(Color color) {
        return color.getRGB();
    }

    /**
     * Create a color with specified RGBA values
     *
     * @param r red component (0-255)
     * @param g green component (0-255)
     * @param b blue component (0-255)
     * @param a alpha component (0-255)
     * @return the created color
     */
    public static int getColor(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    /**
     * Create a color with specified RGBA values
     *
     * @param r red component (0-255)
     * @param g green component (0-255)
     * @param b blue component (0-255)
     * @param a alpha component (0.0-1.0)
     * @return the created color
     */
    public static int getColor(int r, int g, int b, float a) {
        return new Color(r, g, b, (int) (a * 255)).getRGB();
    }

    /**
     * Gets a black color with specified alpha
     *
     * @param alpha the alpha value (0-255)
     * @return the black color with given alpha
     */
    public static int getBlackColor(int alpha) {
        return new Color(0, 0, 0, alpha).getRGB();
    }

    /**
     * Gets a black color with specified alpha
     *
     * @param alpha the alpha value (0.0-1.0)
     * @return the black color with given alpha
     */
    public static int getBlackColor(float alpha) {
        return new Color(0, 0, 0, (int) (alpha * 255)).getRGB();
    }
}