package never.lose;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import never.lose.managers.CfgManager;
import never.lose.managers.ClientPrefixManager;
import never.lose.managers.FriendManager;
import never.lose.managers.ModuleManager;
import never.lose.managers.ThemeManager;
import never.lose.events.client.ClientChatListener;
import never.lose.ui.ThemeEditor;
import never.lose.utils.font.FontManager;
import never.lose.utils.render.RenderConst;
import org.slf4j.Logger;

public class NeverLose implements ClientModInitializer {

    public static final String NAME       = "Endless";
    public static final String VERSION    = "1.0";
    public static final long   START_TIME = System.currentTimeMillis();

    public static MinecraftClient mc  = MinecraftClient.getInstance();
    public static final Logger    LOG = LogUtils.getLogger();

    private static NeverLose INSTANCE;

    public static NeverLose getInstance() { return INSTANCE; }

    private FontManager  fontManager;
    private ThemeManager themeManager;
    private ModuleManager moduleManager;

    public FontManager   getFontManager()   { return fontManager;   }
    public ThemeManager  getThemeManager()  { return themeManager;  }
    public ModuleManager getModuleManager() { return moduleManager; }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        mc       = MinecraftClient.getInstance();

        LOG.info("[{}] Initializing v{}", NAME, VERSION);

        fontManager   = new FontManager();
        themeManager  = new ThemeManager();
        moduleManager = ModuleManager.get();
        moduleManager.init();

        ClientPrefixManager.init();
        FriendManager.init();
        CfgManager.init();

        ClientChatListener.register();

        RenderConst.register();
        ThemeEditor.applyStartupTheme();

        LOG.info("[{}] Ready. ClickGui bind: Right Shift", NAME);
    }
}
