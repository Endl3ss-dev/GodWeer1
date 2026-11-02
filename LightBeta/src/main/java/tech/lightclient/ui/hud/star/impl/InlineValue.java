package tech.lightclient.ui.hud.star.impl;

import tech.lightclient.ui.hud.star.SAnim;
import tech.lightclient.ui.hud.star.SEasing;
import tech.lightclient.ui.hud.star.STimer;

public class InlineValue {
    private final String id;
    private final String label;
    private final String suffix;
    private String text = "?";
    private String copyText = "";
    private boolean copied;
    private final STimer copyTimer = new STimer();
    private final SAnim copyAnim = new SAnim(300L, 0.0f, SEasing.BAKEK);
    private final SAnim successAnim = new SAnim(500L, 0.0f, SEasing.BAKEK_SIZE);

    public InlineValue(String id, String label, String suffix) {
        this.id = id;
        this.label = label;
        this.suffix = suffix.isEmpty() ? "" : " " + suffix;
    }

    public InlineValue(String id, String label) {
        this.id = id;
        this.label = label;
        this.suffix = "";
    }

    public String id() {
        return id;
    }

    public String label() {
        return label;
    }

    public void update(String text, String copyText) {
        this.text = text;
        this.copyText = copyText == null ? "" : copyText;
    }

    public void update(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    public String copy() {
        return copyText;
    }

    public String suffix() {
        return suffix;
    }

    public boolean isCopied() {
        return copied;
    }

    public InlineValue setCopied(boolean value) {
        this.copied = value;
        return this;
    }

    public STimer copyTimer() {
        return copyTimer;
    }

    public SAnim copyAnim() {
        return copyAnim;
    }

    public SAnim successAnim() {
        return successAnim;
    }
}
