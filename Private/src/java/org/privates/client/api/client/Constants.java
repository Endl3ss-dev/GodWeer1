package org.privates.client.api.client;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class Constants {
    public final String NAME = "Private";
    public final String NAMESPACE = NAME.toLowerCase();
    public final String RELEASE = "Recode";
    public final String VERSION = "1371";
    public final String WEBSITE_SHORT = "privatebuy.xyz";
    public final String WEBSITE = "https://" + WEBSITE_SHORT;
    public final String TITLE = String.format("%s %s (%s) -> %s", NAME, VERSION, RELEASE, WEBSITE);
    public final Path MAIN_DIR = Paths.get("C:/PrivateBeta/game");
    public final String FILE_FORMAT = ".pv";
    public final String DEVELOPER = "fungetx";
}
