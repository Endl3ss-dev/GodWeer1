package org.privates.client;

import com.adl.nativeprotect.Native;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import org.privates.DONT_DELETE_THIS_FLAG_FOR_EVENT;
import org.privates.client.api.client.Constants;
import org.privates.client.api.events.orbit.EventBus;
import org.privates.client.managers.command.AdviceCommandFactoryImpl;
import org.privates.client.managers.command.ParametersFactoryImpl;
import org.privates.client.managers.command.PrefixImpl;
import org.privates.client.managers.command.StandaloneCommandDispatcher;
import org.privates.client.managers.command.api.*;
import org.privates.client.managers.command.api.logger.ConsoleLogger;
import org.privates.client.managers.command.api.logger.MinecraftLogger;
import org.privates.client.managers.command.api.logger.MultiLogger;
import org.privates.client.managers.command.impl.*;
import org.privates.client.managers.component.ComponentManager;
import org.privates.client.managers.module.ModuleManager;
import org.privates.client.managers.other.autobuy.AutoBuyManager;
import org.privates.client.managers.other.config.ConfigFile;
import org.privates.client.managers.other.config.ConfigManager;
import org.privates.client.managers.other.friend.FriendFile;
import org.privates.client.managers.other.friend.FriendManager;
import org.privates.client.managers.other.macros.MacrosFile;
import org.privates.client.managers.other.macros.MacrosManager;
import org.privates.client.managers.other.staff.StaffFile;
import org.privates.client.managers.other.staff.StaffManager;
import org.privates.client.screen.WelcomeThemeSelectorGui;
import org.privates.client.screen.account.AccountFile;
import org.privates.client.screen.account.AccountGuiScreen;
import org.privates.client.screen.account.AccountManager;
import org.privates.client.screen.autobuy.AutoBuyScreen;
import org.privates.client.screen.autobuy.AutoBuyUI;
import org.privates.client.screen.clickgui.ClickGuiScreen;
import org.privates.client.screen.countergui.CounterGui;
import org.privates.client.screen.csgo.CsGoScreen;
import org.privates.client.screen.csgui.WexsideGui;
import org.privates.client.screen.eternity.EternityScreen;
import org.privates.client.screen.figmagui.FigmaGuiScreen;
import org.privates.client.screen.flatgui.FlatGuiScreen;
import org.privates.client.screen.mainmenu.MainMenu;
import org.privates.client.screen.rockgui.RockGuiScreen;
import org.privates.client.screen.rockstar.RockstarScreen;
import org.privates.client.screen.umbrella.UmbrellaScreen;
import org.privates.client.utils.file.FileManager;
import org.privates.client.utils.other.Console;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.shader.ShaderManager;
import org.privates.client.utils.server.ServerTPS;
import org.privates.client.utils.time.Profiler;
import org.privates.common.impl.proxy.ProxyConfig;
import org.privates.common.impl.viaversion.ViaMCP;
import org.privates.common.impl.waveycapes.WaveyCapesBase;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("IfStatementWithIdenticalBranches")
@Log4j2
@Getter
@Accessors(fluent = true)
public class Excellent {
    @Getter
    private static final Excellent inst = new Excellent();

    @Getter
    private static boolean initialized = false;

    @Getter
    private static final EventBus eventHandler = EventBus.threadSafe();

    private final long loadTime = System.currentTimeMillis();
    private FileManager fileManager;
    private ModuleManager moduleManager;
    private CommandDispatcher commandDispatcher;
    private ComponentManager componentManager;
    private ConfigManager configManager;
    private FriendManager friendManager;
    private AccountManager accountManager;
    private StaffManager staffManager;
    private WexsideGui wexsideGui;

    private MacrosManager macrosManager;
    private org.privates.client.screen.notification.NotificationManager notifManager;

    private AutoBuyManager autoBuyManager;

    private WaveyCapesBase waveyCapes;
    private RockGuiScreen rockGuiScreen;

    private CsGoScreen csgoGui;
    private ClickGuiScreen clickGui;
    private FigmaGuiScreen figmaGuiScreen;
    private UmbrellaScreen umbrellaScreen;
    private AutoBuyUI autoBuyUI;
    private AutoBuyScreen autoBuyScreen;
    private FlatGuiScreen flatGui;
    private EternityScreen eterkaGui;
    private CounterGui csgui;
    private RockstarScreen rockStarScreen;

    private org.privates.client.screen.newgui.ClickGuiScreen newGui;

    private AccountGuiScreen accountGui;
    private final Profiler profiler = new Profiler();
    private ServerTPS serverTps;

    public final List<String> learn = List.of("mc.funtime.su");

    @Native
    public void start() {
        eventHandler.registerLambdaFactory(DONT_DELETE_THIS_FLAG_FOR_EVENT.class.getPackage().getName(), (lookupInMethod, klass) ->
                (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
    }

    @Native
    public void load() {
        Fonts.loadFonts();
        ShaderManager.loadShaders();
        ProxyConfig.loadConfig();
        org.privates.client.utils.autobuy.AutoBuyManager.init();
        this.initVia();
        log("Initializing..");
        this.profiler.start();
        initManagers();
        initCommands();

        this.profiler.stop();

        log("Initialized in " + profiler.getTotalTimeMS() + "ms.");

        loadAllConfigs();

        if (!configManager.hasCompletedWelcome()) {
            Minecraft.getInstance().displayScreen(new WelcomeThemeSelectorGui());
        } else {
            Minecraft.getInstance().displayScreen(new MainMenu());
        }
        Minecraft.getInstance().getMainWindow().setWindowTitle("Private Beta ");

        initialized = true;

        Runtime.getRuntime().addShutdownHook(new Thread(this::unload));
    }

    public void initWaveyCapes() {
        this.waveyCapes = new WaveyCapesBase();
        this.waveyCapes.init();
    }

    public void initScreens() {
        this.rockGuiScreen = new RockGuiScreen();
        this.csgoGui = new CsGoScreen();
        this.figmaGuiScreen = new FigmaGuiScreen();
        this.clickGui = new ClickGuiScreen();
        this.umbrellaScreen = new UmbrellaScreen();
        this.flatGui = new FlatGuiScreen();
        this.eterkaGui = new EternityScreen();
        this.csgui = new CounterGui();
        this.rockStarScreen = new RockstarScreen();
        this.newGui = new org.privates.client.screen.newgui.ClickGuiScreen();
        this.accountGui = new AccountGuiScreen();
        this.autoBuyUI = new AutoBuyUI();
        this.autoBuyScreen = new AutoBuyScreen();
    }

    private void initManagers() {
        this.fileManager = new FileManager();
        this.serverTps = new ServerTPS();
        this.notifManager = new org.privates.client.screen.notification.NotificationManager();
        this.moduleManager = new ModuleManager();
        this.moduleManager.init();
        this.componentManager = new ComponentManager();
        this.componentManager.init();
        this.configManager = new ConfigManager();
        this.friendManager = new FriendManager();
        this.accountManager = new AccountManager();
        this.staffManager = new StaffManager();
        this.macrosManager = new MacrosManager();
        this.autoBuyManager = new AutoBuyManager();
    }

    private void initVia() {
        ViaMCP.create();
    }

    @Native
    private void loadAllConfigs() {
        loadConfigs();
        loadFriends();
        loadAccounts();
        loadStaffs();
        loadMacros();
    }

    public void unload() {
        if (configManager != null) {
            configManager.set();
        }
        if (friendManager != null) {
            friendManager.set();
        }
        if (accountManager != null) {
            accountManager.set();
        }
        if (staffManager != null) {
            staffManager.set();
        }
        if (macrosManager != null) {
            macrosManager.set();
        }
    }

    public static void log(String str, Object... args) {
        log.info("{}{}{} -> {}{}",
                Console.getConsoleBackground(),
                Console.getConsoleText(),
                Constants.NAME,
                str.formatted(args),
                Console.getConsoleReset());
    }

    @Native
    public void loadConfigs() {
        try {
            configManager.update();
            final ConfigFile config = configManager.get("default");

            if (config == null) {
                log("Config is null, creating new default config");
                configManager.set();
                return;
            }

            if (!config.getFile().exists()) {
                log("Config file doesn't exist, creating new default config");
                configManager.set();
                return;
            }

            boolean readSuccess = false;
            try {
                readSuccess = config.read();
            } catch (Exception readException) {
                log("Error reading config, attempting to create backup");
                readException.printStackTrace();

                File backupFile = new File(config.getFile().getParentFile(), "default_backup_" + System.currentTimeMillis() + ".json");
                try {
                    java.nio.file.Files.copy(config.getFile().toPath(), backupFile.toPath());
                    log("Backup created: " + backupFile.getName());
                } catch (Exception backupException) {
                    log("Failed to create backup");
                    backupException.printStackTrace();
                }
            }

            if (!readSuccess) {
                log("Failed to read config, will not delete existing file");
            } else {
                log("Config loaded successfully");
            }

        } catch (Exception e) {
            log("Critical error in loadConfigs");
            e.printStackTrace();
        }
    }

    public void loadFriends() {
        try {
            final FriendFile friend = friendManager.get();

            if (friend == null) {
                log("Friend file is null, creating new");
                friendManager.set();
                return;
            }

            if (!friend.getFile().exists()) {
                log("Friend file doesn't exist, creating new");
                friendManager.set();
                return;
            }

            boolean readSuccess = false;
            try {
                readSuccess = friend.read();
            } catch (Exception readException) {
                log("Error reading friends, creating backup");
                readException.printStackTrace();

                File backupFile = new File(friend.getFile().getParentFile(), "friends_backup_" + System.currentTimeMillis() + ".json");
                try {
                    java.nio.file.Files.copy(friend.getFile().toPath(), backupFile.toPath());
                    log("Friends backup created: " + backupFile.getName());
                } catch (Exception backupException) {
                    log("Failed to create friends backup");
                    backupException.printStackTrace();
                }
            }

            if (!readSuccess) {
                log("Failed to read friends file");
            } else {
                log("Friends loaded successfully");
            }

        } catch (Exception e) {
            log("Critical error in loadFriends");
            e.printStackTrace();
        }
    }

    public void loadAccounts() {
        try {
            final AccountFile account = accountManager.get();

            if (account == null) {
                log("Account file is null, creating new");
                accountManager.set();
                return;
            }

            if (!account.getFile().exists()) {
                log("Account file doesn't exist, creating new");
                accountManager.set();
                return;
            }

            boolean readSuccess = false;
            try {
                readSuccess = account.read();
            } catch (Exception readException) {
                log("Error reading accounts, creating backup");
                readException.printStackTrace();

                File backupFile = new File(account.getFile().getParentFile(), "accounts_backup_" + System.currentTimeMillis() + ".json");
                try {
                    java.nio.file.Files.copy(account.getFile().toPath(), backupFile.toPath());
                    log("Accounts backup created: " + backupFile.getName());
                } catch (Exception backupException) {
                    log("Failed to create accounts backup");
                    backupException.printStackTrace();
                }
            }

            if (!readSuccess) {
                log("Failed to read accounts file");
            } else {
                log("Accounts loaded successfully");
            }

        } catch (Exception e) {
            log("Critical error in loadAccounts");
            e.printStackTrace();
        }
    }

    private static boolean baritoneEnabled = true;

    public static void enableBaritone() {
        baritoneEnabled = true;
    }

    public static void disableBaritone() {
        baritoneEnabled = false;
    }

    public static boolean hasBaritoneEnabled() {
        return baritoneEnabled;
    }

    public void loadStaffs() {
        try {
            final StaffFile staff = staffManager.get();

            if (staff == null) {
                log("Staff file is null, creating new");
                staffManager.set();
                return;
            }

            if (!staff.getFile().exists()) {
                log("Staff file doesn't exist, creating new");
                staffManager.set();
                return;
            }

            boolean readSuccess = false;
            try {
                readSuccess = staff.read();
            } catch (Exception readException) {
                log("Error reading staff, creating backup");
                readException.printStackTrace();

                File backupFile = new File(staff.getFile().getParentFile(), "staff_backup_" + System.currentTimeMillis() + ".json");
                try {
                    java.nio.file.Files.copy(staff.getFile().toPath(), backupFile.toPath());
                    log("Staff backup created: " + backupFile.getName());
                } catch (Exception backupException) {
                    log("Failed to create staff backup");
                    backupException.printStackTrace();
                }
            }

            if (!readSuccess) {
                log("Failed to read staff file");
            } else {
                log("Staff loaded successfully");
            }

        } catch (Exception e) {
            log("Critical error in loadStaffs");
            e.printStackTrace();
        }
    }

    public WexsideGui wexsideGui() {
        if (wexsideGui == null) {
            wexsideGui = new WexsideGui();
        }
        return wexsideGui;
    }

    public void loadMacros() {
        try {
            final MacrosFile macros = macrosManager.get();

            if (macros == null) {
                log("Macros file is null, creating new");
                macrosManager.set();
                return;
            }

            if (!macros.getFile().exists()) {
                log("Macros file doesn't exist, creating new");
                macrosManager.set();
                return;
            }

            boolean readSuccess = false;
            try {
                readSuccess = macros.read();
            } catch (Exception readException) {
                log("Error reading macros, creating backup");
                readException.printStackTrace();

                File backupFile = new File(macros.getFile().getParentFile(), "macros_backup_" + System.currentTimeMillis() + ".json");
                try {
                    java.nio.file.Files.copy(macros.getFile().toPath(), backupFile.toPath());
                    log("Macros backup created: " + backupFile.getName());
                } catch (Exception backupException) {
                    log("Failed to create macros backup");
                    backupException.printStackTrace();
                }
            }

            if (!readSuccess) {
                log("Failed to read macros file");
            } else {
                log("Macros loaded successfully");
            }

        } catch (Exception e) {
            log("Critical error in loadMacros");
            e.printStackTrace();
        }
    }

    @Native
    private void initCommands() {
        Logger logger = new MultiLogger(List.of(new ConsoleLogger(), new MinecraftLogger()));
        List<Command> commands = new ArrayList<>();
        Prefix prefix = new PrefixImpl();
        commands.add(new ListCommand(commands, logger));
        commands.add(new FriendCommand(friendManager, prefix, logger));
        commands.add(new StaffCommand(staffManager, prefix, logger));
        commands.add(new BindCommand(prefix, logger));
        commands.add(new LoginCommand(logger));
        commands.add(new NameCommand(logger));
        commands.add(new PanicCommand(logger));
        commands.add(new ServerInfoCommand(logger));
        commands.add(new GPSCommand(prefix));
        commands.add(new WayCommand(prefix, logger));
        commands.add(new ConfigCommand(configManager, prefix, logger));
        commands.add(new MacroCommand(macrosManager, prefix, logger));
        commands.add(new VClipCommand(prefix, logger));
        commands.add(new HClipCommand(prefix, logger));
        commands.add(new MemoryCommand(logger));
        commands.add(new RCTCommand(logger));
        commands.add(new NeuroCommand(prefix, logger));

        AdviceCommandFactory adviceCommandFactory = new AdviceCommandFactoryImpl(logger);
        ParametersFactory parametersFactory = new ParametersFactoryImpl();

        commandDispatcher = new StandaloneCommandDispatcher(commands, adviceCommandFactory, prefix, parametersFactory, logger);
    }
}
