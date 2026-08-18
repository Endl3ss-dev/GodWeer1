package mods.voicechat.gui.widgets;

import mods.voicechat.VoicechatClient;
import mods.voicechat.voice.client.VolumeManager;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MicAmplificationSlider extends DebouncedSlider {

    public MicAmplificationSlider(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn, new StringTextComponent(""), (VoicechatClient.CLIENT_CONFIG.microphoneGain.get() - VolumeManager.MIN_GAIN) / (VolumeManager.MAX_GAIN - VolumeManager.MIN_GAIN));
        updateMessage();
    }

    protected void updateMessage() {
        double gainDb = sliderValue * (VolumeManager.MAX_GAIN - VolumeManager.MIN_GAIN) + VolumeManager.MIN_GAIN;
        long amp = Math.round(gainDb);
        setMessage(new TranslationTextComponent("message.voicechat.microphone_amplification", (amp > 0F ? "+" : "") + amp + " dB"));
    }

    @Override
    public void applyDebounced() {
        double gainDb = sliderValue * (VolumeManager.MAX_GAIN - VolumeManager.MIN_GAIN) + VolumeManager.MIN_GAIN;
        VoicechatClient.CLIENT_CONFIG.microphoneGain.set(gainDb).save();
    }

    @Override
    protected void func_230979_b_() {

    }

    @Override
    protected void func_230972_a_() {

    }
}
