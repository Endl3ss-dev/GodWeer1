import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import net.minecraft.client.main.Main;

public class Start {

    public static void main(String[] args) {

        //говно код блядский ну похуй ваще
        String url = "https://t.me/bihben2026";
        openWebpage(url);
        String url2 = "https://t.me/enormitydlc";
        openWebpage(url2);
        String url3 = "https://t.me/energydlc";
        openWebpage(url3);
        String url4 = "https://t.me/hometz";
        openWebpage(url4);
        String assets = System.getenv().containsKey("assetDirectory") ? System.getenv("assetDirectory") : "assets";
        Main.main(concat(new String[]{"--version", "mcp", "--accessToken", "0", "--assetsDir", assets, "--assetIndex", "1.16", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static void openWebpage(String urlString) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI(urlString);
                desktop.browse(uri);
            } catch (IOException e) {
                System.err.println("IOException occurred while opening the URL: " + e.getMessage());
            } catch (URISyntaxException e) {
                System.err.println("Invalid URL: " + urlString);
            }
        } else {
            System.err.println("Desktop is not supported on this platform.");
        }
    }
}
