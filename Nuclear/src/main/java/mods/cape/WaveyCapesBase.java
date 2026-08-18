package mods.cape;

import mods.cape.config.Config;

public class WaveyCapesBase {
    public static WaveyCapesBase INSTANCE;
    public static Config config;

    public void init() {
        INSTANCE = this;
        config = new Config();
    }
}
