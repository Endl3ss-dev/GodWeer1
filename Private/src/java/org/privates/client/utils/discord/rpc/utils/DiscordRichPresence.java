package org.privates.client.utils.discord.rpc.utils;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiscordRichPresence extends Structure {
    public String largeImageKey;
    public String largeImageText;
    public String smallImageText;
    public String partyPrivacy;
    public long startTimestamp;
    public int instance;
    public String partyId;
    public int partySize;
    public long endTimestamp;
    public String details;
    public String joinSecret;
    public String spectateSecret;
    public String smallImageKey;
    public String matchSecret;
    public String state;
    public int partyMax;
    public String button_url_1;
    public String button_label_1;
    public String button_url_2;
    public String button_label_2;

    public DiscordRichPresence() {
        setStringEncoding("UTF-8");
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
                "state", "details", "startTimestamp", "endTimestamp",
                "largeImageKey", "largeImageText", "smallImageKey", "smallImageText",
                "partyId", "partySize", "partyMax", "partyPrivacy",
                "matchSecret", "joinSecret", "spectateSecret",
                "button_label_1", "button_url_1", "button_label_2", "button_url_2",
                "instance"
        );
    }

    public static class Builder {
        private final DiscordRichPresence richPresence = new DiscordRichPresence();

        public Builder setDetails(String details) {
            if (details != null && !details.isEmpty()) {
                richPresence.details = details.substring(0, Math.min(details.length(), 128));
            }
            return this;
        }

        public Builder setState(String state) {
            if (state != null && !state.isEmpty()) {
                richPresence.state = state.substring(0, Math.min(state.length(), 128));
            }
            return this;
        }

        public Builder setLargeImage(String key, String text) {
            richPresence.largeImageKey = key;
            richPresence.largeImageText = text;
            return this;
        }

        public Builder setSmallImage(String key, String text) {
            richPresence.smallImageKey = key;
            richPresence.smallImageText = text;
            return this;
        }

        public Builder setStartTimestamp(long epochSeconds) {
            richPresence.startTimestamp = epochSeconds;
            return this;
        }

        public Builder setButtons(RPCButton button) {
            return setButtons(Collections.singletonList(button));
        }

        public Builder setButtons(RPCButton button1, RPCButton button2) {
            return setButtons(Arrays.asList(button1, button2));
        }

        public Builder setButtons(List<RPCButton> buttons) {
            if (buttons == null || buttons.isEmpty()) {
                return this;
            }
            int count = Math.min(2, buttons.size());
            richPresence.button_label_1 = buttons.get(0).getLabel();
            richPresence.button_url_1 = buttons.get(0).getUrl();
            if (count == 2) {
                richPresence.button_label_2 = buttons.get(1).getLabel();
                richPresence.button_url_2 = buttons.get(1).getUrl();
            }
            return this;
        }

        public DiscordRichPresence build() {
            return richPresence;
        }
    }
}

