package godweer.modules.api.setting;

import java.util.function.Supplier;

public class TextSetting extends Setting {

    private final int maxLength;

    private String text;

    public TextSetting(String name, String text) {
        this(name, text, 32);
    }

    public TextSetting(String name, String text, int maxLength) {
        super(name);
        this.maxLength = Math.max(1, maxLength);
        setText(text);
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public String get() {
        return this.text;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        if (text == null) {
            this.text = "";
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length() && builder.length() < this.maxLength; i++) {
            char chr = text.charAt(i);
            if (!Character.isISOControl(chr)) {
                builder.append(chr);
            }
        }
        this.text = builder.toString();
    }

    public TextSetting visible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
