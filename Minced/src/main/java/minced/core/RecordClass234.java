package minced.core;

public final class RecordClass234 {
    public final SliderSetting newSlider;
    public final float newSliderX;
    public final Object newBinding;
    public final boolean handled;
    public final boolean clearBindingRequested;

    public RecordClass234(SliderSetting newSlider, float newSliderX, Object newBinding, boolean handled, boolean clearBindingRequested) {
        this.newSlider = newSlider;
        this.newSliderX = newSliderX;
        this.newBinding = newBinding;
        this.handled = handled;
        this.clearBindingRequested = clearBindingRequested;
    }

    public SliderSetting newSlider() { return newSlider; }
    public float newSliderX() { return newSliderX; }
    public Object newBinding() { return newBinding; }
    public boolean handled() { return handled; }
    public boolean clearBindingRequested() { return clearBindingRequested; }

    public static RecordClass234 withSlider(SliderSetting param0, float param1) {
        return new RecordClass234(param0, param1, null, true, false);
    }

    public static RecordClass234 withBinding(Object param0) {
        return new RecordClass234(null, 0.0f, param0, true, false);
    }

    public static RecordClass234 clearBinding() {
        return new RecordClass234(null, 0.0f, null, true, true);
    }

    public static RecordClass234 consumed() {
        return new RecordClass234(null, 0.0f, null, true, false);
    }

    public static RecordClass234 empty() {
        return new RecordClass234(null, 0.0f, null, false, false);
    }
}
