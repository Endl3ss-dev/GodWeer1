package tech.lightclient.modules.api;



import tech.lightclient.LightClient;

import tech.lightclient.client.events.impl.client.EventKey;

import tech.lightclient.client.utils.client.SoundUtil;

import tech.lightclient.client.utils.eventbus.EventHandler;

import tech.lightclient.modules.api.settings.api.Setting;

import tech.lightclient.modules.api.settings.impl.BooleanSetting;

import tech.lightclient.modules.impl.combat.*;

import tech.lightclient.modules.impl.movement.*;

import tech.lightclient.modules.impl.player.*;

import tech.lightclient.modules.impl.render.*;

import tech.lightclient.modules.impl.utils.*;

import lombok.Getter;

import net.minecraft.client.util.InputUtil;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;


import java.util.Arrays;

import java.util.Comparator;

import java.util.List;

import java.util.Locale;

import java.util.concurrent.CopyOnWriteArrayList;

import java.util.stream.Collectors;



@Getter

public class FunctionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionManager.class);

    final List<Function> functions = new CopyOnWriteArrayList<>();

    Speed speed;

    GrimGlide grimGlide;

    Interface anInterface;

    NoRender noRender;

    CameraTweaks cameraTweaks;

    PlayerUtilsFunction playerUtilsFunction;

    ThrowableHelper throwableHelper;

    ClickAction clickAction;

    ServerAssistant serverAssistant;

    AuraModule auraModule;

    ThrowableAim throwableAim;

    AutoTotem autoTotem;

    AutoZamok autoZamok;

    AutoCrystal autoCrystal;

    NoVelocity noVelocity;

    BackTrack backTrack;

    AutoSwap autoSwap;

    AutoPotions autoPotions;

    PotionThrower potionThrower;

    ShaderEsp shaderEsp;

    ShaderTest shaderTest;

    TargetPearl targetPearl;

    NoFriendDamage noFriendDamage;

    TriggerBot triggerBot;


    ServerCrasher crasher;

    StreamerMode streamerMode;

    ItemScroller itemScroller;

    Notifications notifications;

    StructureInfo structureInfo;

    HitSounds hitSounds;

    SoundFX soundFX;

    AutoSprint autoSprint;

    GuiMove guiMove;

    NoSlow noSlow;

    Spider spider;

    AirStuck airStuck;

    WaterWalk waterWalk;

    Scaffold scaffold;

    FreeLook freeLook;

    TargetESP targetESP;

    Ambience ambience;

    Particles particles;

    ChorusRadius chorusRadius;

    ItemRadius itemRadius;

    BlockOverlay blockOverlay;

    HitBoxESP hitBoxESP;

    Timer timer;

    NoServerDesync noServerDesync;

    IRC irc;

    Globals globals;

    SwingAnimations swingAnimations;

    ViewModel viewModel;

    HandGlow handGlow;

    Hands hands;

    HoldMyItems holdMyItems;

    ShaderTestFog shaderTestFog;

    MotionBlur motionBlur;

    NameTags nameTags;

    Tweaks tweaks;

    Arrows arrows;

    Brightness brightness;

    Beautifully beautifully;

    AncientXray ancientXray;

    AncientFarm ancientFarm;

    FreeCamera freeCamera;

    FakePlayer fakePlayer;

    ChestStealer chestStealer;

    AutoWarden autoWarden;

    WardenFarm wardenFarm;

    SkyShader skyShader;

    AutoTool autoTool;

    AutoMine autoMine;

    AutoEnd autoEnd;

    AutoEat autoEat;

    CaptchaSolver captchaSolver;

    AutoSell autoSell;

    AutoInvis autoInvis;

    AntiAFK antiAFK;

    NBTViewer nbtViewer;

    RpSpoofer rpSpoofer;

    SpookyJoin spookyJoin;

    ClientHide clientHide;

    AutoTP autoTP;

    Blink blink;

    PingSpoof pingSpoof;

    ClanUpgrade clanUpgrade;

    ClanInvest clanInvest;

    AutoLeave autoLeave;

    FixHP fixHP;

    Tracker tracker;

    ElytraFunctional elytraFunctional;

    ElytraFly elytraFly;

    MaceExploit maceExploit;

    ElytraMotion elytraMotion;

    ElytraSwap elytraSwap;

    FastExp fastExp;

    DanjTags danjTags;

    AuctionHelper auctionHelper;

    AutoArmor autoArmor;

    RotationRecorderModule rotationRecorderModule;

    AimBot aimBot;

    NoEffects noEffects;

    SeeInvisibles seeInvisibles;

    AntiSpamModule antiSpamModule;

    ThrowPrediction throwPrediction;

    BlockESP blockESP;

    JumpCircle jumpCircle;

    ArmorDurability armorDurability;

    ClickGui clickGui;

    RotationRender rotationRender;

    Crosshair crosshair;

    AutoBuyFunction autoBuyFunction;

    KTLeave ktLeave;

    AutoSword autoSword;

    AppleFarmer appleFarmer;

    InventoryHelper inventoryHelper;

    Portals portals;



    public void init() {

        registerAll(

                clickGui = new ClickGui(),

                autoBuyFunction = new AutoBuyFunction(),

                anInterface = new Interface(),

                noRender = new NoRender(),

                danjTags = new DanjTags(),

                blockESP = new BlockESP(),

                crosshair = new Crosshair(),

                portals = new Portals(),

                armorDurability = new ArmorDurability(),

                cameraTweaks = new CameraTweaks(),

                auctionHelper = new AuctionHelper(),

                jumpCircle = new JumpCircle(),

                rotationRender = new RotationRender(),

                arrows = new Arrows(),

                brightness = new Brightness(),

                beautifully = new Beautifully(),

                ancientXray = new AncientXray(),

                ancientFarm = new AncientFarm(),

                maceExploit = new MaceExploit(),

                rotationRecorderModule = new RotationRecorderModule(),

                playerUtilsFunction = new PlayerUtilsFunction(),

                clickAction = new ClickAction(),

                autoZamok = new AutoZamok(),

                serverAssistant = new ServerAssistant(),

                auraModule = new AuraModule(),

                autoSwap = new AutoSwap(),

                autoPotions = new AutoPotions(),

                potionThrower = new PotionThrower(),

                shaderEsp = new ShaderEsp(),

                shaderTest = new ShaderTest(),

                throwPrediction = new ThrowPrediction(),

                noEffects = new NoEffects(),

                targetPearl = new TargetPearl(),

                noFriendDamage = new NoFriendDamage(),

                speed = new Speed(),

                grimGlide = new GrimGlide(),

                autoTotem = new AutoTotem(),

                autoCrystal = new AutoCrystal(),

                throwableAim = new ThrowableAim(),

                crasher = new ServerCrasher(),

                noVelocity = new NoVelocity(),

                backTrack = new BackTrack(),

                streamerMode = new StreamerMode(),

                itemScroller = new ItemScroller(),

                notifications = new Notifications(),

                structureInfo = new StructureInfo(),

                hitSounds = new HitSounds(),

                soundFX = new SoundFX(),

                throwableHelper = new ThrowableHelper(),

                autoSprint = new AutoSprint(),

                guiMove = new GuiMove(),

                noSlow = new NoSlow(),

                spider = new Spider(),

                airStuck = new AirStuck(),

                waterWalk = new WaterWalk(),

                scaffold = new Scaffold(),

                freeLook = new FreeLook(),

                targetESP = new TargetESP(),

                ambience = new Ambience(),

                particles = new Particles(),

                chorusRadius = new ChorusRadius(),

                itemRadius = new ItemRadius(),

                blockOverlay = new BlockOverlay(),

                skyShader = new SkyShader(),

                new ProjectilePrediction(),

                new ItemReplacer(),

                hitBoxESP = new HitBoxESP(),

                timer = new Timer(),

                noServerDesync = new NoServerDesync(),

                irc = new IRC(),

                globals = new Globals(),

                swingAnimations = new SwingAnimations(),

                viewModel = new ViewModel(),

                hands = new Hands(),

                holdMyItems = new HoldMyItems(),

                shaderTestFog = new ShaderTestFog(),

                motionBlur = new MotionBlur(),

                nameTags = new NameTags(),

                tweaks = new Tweaks(),

                freeCamera = new FreeCamera(),

                fakePlayer = new FakePlayer(),

                chestStealer = new ChestStealer(),

                autoWarden = new AutoWarden(),

                wardenFarm = new WardenFarm(),

                autoTool = new AutoTool(),

                autoMine = new AutoMine(),

                autoEnd = new AutoEnd(),

                autoEat = new AutoEat(),

                captchaSolver = new CaptchaSolver(),

                autoSell = new AutoSell(),

                autoInvis = new AutoInvis(),

                antiAFK = new AntiAFK(),

                nbtViewer = new NBTViewer(),

                rpSpoofer = new RpSpoofer(),

                spookyJoin = new SpookyJoin(),

                clientHide = new ClientHide(),

                autoTP = new AutoTP(),

                blink = new Blink(),

                pingSpoof = new PingSpoof(),

                clanUpgrade = new ClanUpgrade(),

                clanInvest = new ClanInvest(),

                autoLeave = new AutoLeave(),

                fixHP = new FixHP(),

                tracker = new Tracker(),

                elytraFunctional = new ElytraFunctional(),

                elytraFly = new ElytraFly(),

                elytraMotion = new ElytraMotion(),

                elytraSwap = new ElytraSwap(),

                fastExp = new FastExp(),

                autoArmor = new AutoArmor(),

                aimBot = new AimBot(),

                triggerBot = new TriggerBot(),

                seeInvisibles = new SeeInvisibles(),

                antiSpamModule = new AntiSpamModule(),

                ktLeave = new KTLeave(),

                autoSword = new AutoSword(),

                appleFarmer = new AppleFarmer(),

                inventoryHelper = new InventoryHelper()

        );



        LightClient.getEventBus().subscribe(DanjTags.getInstance());

        LightClient.getEventBus().subscribe(this);

    }



    private void registerAll(Function... features) {

        Arrays.sort(features, Comparator.comparing(Function::getAlias));

        this.functions.addAll(List.of(features));

    }



    public IRC getIRC() {

        return irc;

    }



    @EventHandler

    public void onKey(EventKey e) {        if (LightClient.isHide()) {

            return;

        }



        if (e.getType() != InputUtil.Type.MOUSE && e.getKey() == -1)

            return;



        for (Function function : functions) {

            if (!function.isVisible()) continue;

            if (e.getAction() == 1 && e.isKeyDown(function.getBind())) {

                function.toggle();

            }

        }

        if (e.getAction() == 1) {

            for (Function function : functions) {

                if (!function.isVisible()) continue;

                for (Setting<?> setting : function.getSettings()) {

                    if (!(setting instanceof BooleanSetting boolSetting)) continue;

                    if (!boolSetting.isBound()) continue;

                    if (!e.isKeyDown(boolSetting.getBind())) continue;

                    boolean newVal = !boolSetting.get();

                    boolSetting.set(newVal);



                    SoundFX sounds = soundFX;

                    if (sounds != null && sounds.isState()) {

                        String fileName = sounds.getFileName(newVal);

                        float volume = sounds.volume.get();

                        SoundUtil.playSound("functions/" + fileName, volume, false);

                    }

                }

            }

        }

    }



    public List<Function> getFunctionsByCategory(Category category) {

        return functions.stream()

                .filter(function -> function.getCategory() == category && function.isVisible())

                .collect(Collectors.toList());

    }



    public Function getFunctionByName(String name) {

        if (name == null || name.isEmpty()) return null;

        String lower = name.toLowerCase(Locale.US);

        return functions.stream()

                .filter(f -> f.isVisible() && f.getName().toLowerCase(Locale.US).equals(lower))

                .findFirst()

                .orElse(null);

    }



    public Function getFunction(String name) {

        return getFunctionByName(name);

    }



    public AuraModule getAttackAura() {

        return auraModule;

    }



    public AimBot getAimBot() {

        return aimBot;

    }



    public RotationRender getRotationRender() {

        return rotationRender;

    }



    public List<Function> getVisibleFunctions() {

        return functions.stream()

                .filter(Function::isVisible)

                .collect(Collectors.toList());

    }

}

