/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.media;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class MediaTrack {
    public static final MediaTrack EMPTY = new MediaTrack("", "", "", "", 0, "", 0L);
    private final String title;
    private final String artist;
    private final String albumTitle;
    private final String albumArtist;
    private final int trackNumber;
    private final String appId;
    private final long durationMillis;

    public MediaTrack(String title, String artist, String albumTitle, String albumArtist, int trackNumber, String appId, long durationMillis) {
        this.title = title;
        this.artist = artist;
        this.albumTitle = albumTitle;
        this.albumArtist = albumArtist;
        this.trackNumber = trackNumber;
        this.appId = appId;
        this.durationMillis = durationMillis;
    }

    public String title() {
        return this.title;
    }

    public String artist() {
        return this.artist;
    }

    public String albumTitle() {
        return this.albumTitle;
    }

    public String albumArtist() {
        return this.albumArtist;
    }

    public int trackNumber() {
        return this.trackNumber;
    }

    public String appId() {
        return this.appId;
    }

    public long durationMillis() {
        return this.durationMillis;
    }

    public boolean isEmpty() {
        return this.title.isEmpty() && this.artist.isEmpty();
    }

    public int durationSeconds() {
        return (int)(this.durationMillis / 1000L);
    }

    public String display() {
        if (this.artist.isEmpty()) {
            return this.title;
        }
        return this.artist + " - " + this.title;
    }

    public String toString() {
        return "MediaTrack(" + this.display() + " " + this.durationMillis + "ms app=" + this.appId + ")";
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MediaTrack)) {
            return false;
        }
        MediaTrack t = (MediaTrack)other;
        return this.trackNumber == t.trackNumber && this.durationMillis == t.durationMillis && this.title.equals(t.title) && this.artist.equals(t.artist) && this.albumTitle.equals(t.albumTitle) && this.albumArtist.equals(t.albumArtist) && this.appId.equals(t.appId);
    }

    public int hashCode() {
        int result = this.title.hashCode();
        result = 31 * result + this.artist.hashCode();
        result = 31 * result + this.albumTitle.hashCode();
        result = 31 * result + this.albumArtist.hashCode();
        result = 31 * result + Integer.hashCode(this.trackNumber);
        result = 31 * result + this.appId.hashCode();
        result = 31 * result + Long.hashCode(this.durationMillis);
        return result;
    }
}

