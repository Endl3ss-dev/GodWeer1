package fun.volon.api.utils.render.fonts.msdf;

/**
 * Data class representing a single glyph's metrics and atlas coordinates.
 * This class holds raw glyph data parsed from MSDF JSON font configuration files.
 * 
 * <p>MSDF (Multi-channel Signed Distance Field) fonts store each glyph with:
 * <ul>
 *   <li>Unicode code point</li>
 *   <li>Plane bounds - logical bounds in font space</li>
 *   <li>Atlas bounds - pixel coordinates in the texture atlas</li>
 *   <li>Advance - horizontal spacing for the next character</li>
 * </ul>
 */
public class GlyphData {
    
    /**
     * Unicode code point for this glyph (e.g., 65 for 'A')
     */
    private final int unicode;
    
    /**
     * Horizontal advance width - the distance to move horizontally after rendering this glyph
     */
    private final float advance;
    
    /**
     * Plane bounds - the logical bounds of the glyph in font coordinate space
     */
    private final Bounds planeBounds;
    
    /**
     * Atlas bounds - the pixel coordinates of the glyph in the texture atlas
     */
    private final Bounds atlasBounds;
    
    /**
     * Creates a new GlyphData with all metrics specified.
     *
     * @param unicode The Unicode code point for this glyph
     * @param advance The horizontal advance width
     * @param planeBounds The logical bounds in font space (can be null for whitespace characters)
     * @param atlasBounds The texture atlas pixel coordinates (can be null for whitespace characters)
     */
    public GlyphData(int unicode, float advance, Bounds planeBounds, Bounds atlasBounds) {
        this.unicode = unicode;
        this.advance = advance;
        this.planeBounds = planeBounds;
        this.atlasBounds = atlasBounds;
    }
    
    /**
     * Gets the Unicode code point for this glyph.
     *
     * @return The Unicode value (e.g., 65 for 'A')
     */
    public int getUnicode() {
        return unicode;
    }
    
    /**
     * Gets the horizontal advance width.
     * This is the distance to move the cursor after rendering this glyph.
     *
     * @return The advance width in font units
     */
    public float getAdvance() {
        return advance;
    }
    
    /**
     * Gets the plane bounds - the logical bounds of the glyph in font coordinate space.
     *
     * @return The plane bounds, or null if not defined (e.g., for spaces)
     */
    public Bounds getPlaneBounds() {
        return planeBounds;
    }
    
    /**
     * Gets the atlas bounds - the pixel coordinates in the texture atlas.
     *
     * @return The atlas bounds, or null if not defined (e.g., for spaces)
     */
    public Bounds getAtlasBounds() {
        return atlasBounds;
    }
    
    /**
     * Checks if this glyph has visible geometry (i.e., has plane and atlas bounds).
     *
     * @return true if the glyph has visible geometry, false otherwise
     */
    public boolean hasGeometry() {
        return planeBounds != null && atlasBounds != null;
    }
    
    /**
     * Gets the character this glyph represents.
     *
     * @return The character
     */
    public char getChar() {
        return (char) unicode;
    }
    
    @Override
    public String toString() {
        return "GlyphData{" +
                "unicode=" + unicode +
                " ('" + (char) unicode + "')" +
                ", advance=" + advance +
                ", planeBounds=" + planeBounds +
                ", atlasBounds=" + atlasBounds +
                '}';
    }
    
    /**
     * Represents a rectangular bounding box with left, bottom, right, and top coordinates.
     * Used for both plane bounds (logical font space) and atlas bounds (texture pixels).
     */
    public static class Bounds {
        private final float left;
        private final float bottom;
        private final float right;
        private final float top;
        
        /**
         * Creates a new Bounds with the specified coordinates.
         *
         * @param left The left coordinate
         * @param bottom The bottom coordinate
         * @param right The right coordinate
         * @param top The top coordinate
         */
        public Bounds(float left, float bottom, float right, float top) {
            this.left = left;
            this.bottom = bottom;
            this.right = right;
            this.top = top;
        }
        
        /**
         * Gets the left coordinate.
         *
         * @return The left value
         */
        public float getLeft() {
            return left;
        }
        
        /**
         * Gets the bottom coordinate.
         *
         * @return The bottom value
         */
        public float getBottom() {
            return bottom;
        }
        
        /**
         * Gets the right coordinate.
         *
         * @return The right value
         */
        public float getRight() {
            return right;
        }
        
        /**
         * Gets the top coordinate.
         *
         * @return The top value
         */
        public float getTop() {
            return top;
        }
        
        /**
         * Calculates the width of this bounds.
         *
         * @return The width (right - left)
         */
        public float getWidth() {
            return right - left;
        }
        
        /**
         * Calculates the height of this bounds.
         *
         * @return The height (top - bottom)
         */
        public float getHeight() {
            return top - bottom;
        }
        
        @Override
        public String toString() {
            return "Bounds{" +
                    "left=" + left +
                    ", bottom=" + bottom +
                    ", right=" + right +
                    ", top=" + top +
                    ", width=" + getWidth() +
                    ", height=" + getHeight() +
                    '}';
        }
    }
}
