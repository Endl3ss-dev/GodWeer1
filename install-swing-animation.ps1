<#
.SYNOPSIS
  Swerve: создаёт модуль SwingAnimation (единый на все анимации взмаха руки).

.DESCRIPTION
  Пишет 3 новых .java-файла и точечно правит swerve.mixins.json + ModuleManager.java.
  Кодировка UTF-8 без BOM, существующие переносы строк сохраняются.
  Скрипт идемпотентен: повторный запуск ничего не дублирует.

.EXAMPLE
  powershell -ExecutionPolicy Bypass -File .\install-swing-animation.ps1 -SwerveRoot "C:\Users\User\Desktop\Swerve"
#>
param(
    [string]$SwerveRoot = "C:\Users\User\Desktop\Swerve"
)

$ErrorActionPreference = "Stop"
$utf8NoBom = New-Object System.Text.UTF8Encoding -ArgumentList $false

$anchor = Join-Path $SwerveRoot "src\main\java\swerve"
if (-not (Test-Path -LiteralPath $anchor)) {
    throw "Не найден '$anchor'. Укажи путь явно: -SwerveRoot <путь к Swerve>"
}

function Write-Utf8NoBom([string]$Path, [string]$Content) {
    $dir = Split-Path -Parent $Path
    if (-not (Test-Path -LiteralPath $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    [System.IO.File]::WriteAllText($Path, $Content, $utf8NoBom)
    Write-Host ("[+] " + $Path) -ForegroundColor Green
}

function Get-NewLine([string]$Text) {
    if ($Text.Contains("`r`n")) { return "`r`n" } else { return "`n" }
}

function Test-HasBom([string]$Path) {
    $bytes = [System.IO.File]::ReadAllBytes($Path)
    return ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF)
}

# ---- src\main\java\swerve\modules\api\SwingMode.java ----
$swingMode = @'
package swerve.modules.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.util.Mth;

/**
 * Все анимации взмаха руки, собранные из клиентов репозитория в один набор.
 *
 * Дубликаты в настройках исключены на уровне языка: константы enum не могут
 * повторяться, а список для ModeSetting строится из values().
 *
 * apply() вызывается каждый кадр для каждой руки, поэтому никаких аллокаций
 * внутри — только примитивы и уже существующий PoseStack.
 *
 * attack  — прогресс взмаха 0..1 (в Mojang-маппингах параметр swingArm)
 * equip   — обратная высота руки (inverseArmHeight)
 * invert  — +1 для правой руки, -1 для левой
 */
public enum SwingMode {

    /** Ванильный взмах: миксин не перехватывает вызов. */
    VANILLA("Vanilla") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
        }
    },

    /** AlphaDLC "Smooth" == Pouch "Smooth". */
    SMOOTH("Smooth") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float f = Mth.sin(attack * attack * (float) Math.PI);
            poseStack.translate(0.56F * invert, -0.52F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(invert * (45.0F + f * -20.0F * strength)));
            float g = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.mulPose(Axis.ZP.rotationDegrees(invert * g * -20.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(g * -80.0F * strength));
            poseStack.mulPose(Axis.YP.rotationDegrees(invert * -45.0F));
        }
    },

    /** Pouch "Static". */
    STATIC("Static") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(invert * 0.56F, -0.42F, -0.72F);
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * -60.0F * strength));
            poseStack.translate(0.0F, -0.1F, 0.0F);
        }
    },

    /** AlphaDLC "Down" == Pouch "Down". */
    DOWN("Down") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(invert * 0.56F, -0.32F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(76.0F * invert));
            poseStack.mulPose(Axis.YP.rotationDegrees(sin2 * -5.0F * strength));
            poseStack.mulPose(Axis.XN.rotationDegrees(sin2 * -100.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * -155.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(-100.0F));
        }
    },

    /** Pouch "Akrien": Down + доборот и смещение. */
    AKRIEN("Akrien") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin1 = Mth.sin(attack * attack * (float) Math.PI);
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(invert * 0.65F, -0.32F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(76.0F * invert));
            poseStack.mulPose(Axis.YP.rotationDegrees(sin2 * -5.0F * strength));
            poseStack.mulPose(Axis.XN.rotationDegrees(sin2 * -100.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * -155.0F * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(-100.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(sin2 * 25.0F * strength));
            poseStack.mulPose(Axis.XN.rotationDegrees(sin2 * -25.0F * strength));
            poseStack.mulPose(Axis.XN.rotationDegrees(sin1 * 15.0F * strength));
            poseStack.translate(sin2 * 0.18F * strength, sin2 * 0.59F * strength, 0.0F);
        }
    },

    /** Pouch "Poke". */
    POKE("Poke") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) (Math.PI / 2.0) * 2.0F);
            float tilt = strength / 3.0F;
            poseStack.translate(invert * 0.56F, -0.52F, -0.72F);
            poseStack.translate(0.0F, 0.0F, tilt * -anim);
            poseStack.mulPose(Axis.YP.rotationDegrees(75.0F * invert));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-75.0F * (strength / 4.0F) * anim - 60.0F) * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(-75.0F));
        }
    },

    /** Pouch "SelfBack" == AlphaDLC "Self". */
    SELF_BACK("SelfBack") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) (Math.PI / 2.0) * 2.0F);
            poseStack.translate(0.65F * invert, -0.30F, -0.80F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F * invert));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-70.0F * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(-100.0F - 60.0F * strength * anim));
        }
    },

    /** Pouch "Feast". */
    FEAST("Feast") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(invert * 0.56F, -0.32F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(30.0F * invert));
            poseStack.mulPose(Axis.YP.rotationDegrees(sin2 * 75.0F * invert * strength));
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * -65.0F * strength));
            poseStack.mulPose(Axis.YP.rotationDegrees(30.0F * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(-80.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(35.0F * invert));
        }
    },

    /** Pouch "ToBack". */
    TO_BACK("ToBack") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float g = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(0.65F * invert, -0.45F, -0.90F);
            poseStack.mulPose(Axis.XP.rotationDegrees(50.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees((-30.0F * (1.0F - g * strength) - 30.0F) * invert));
            poseStack.mulPose(Axis.ZP.rotationDegrees(110.0F * invert));
        }
    },

    /** Pouch "Break". */
    BREAK("Break") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float sin2 = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            poseStack.translate(0.66F * invert, -0.30F, -0.38F);
            poseStack.mulPose(Axis.YP.rotationDegrees(270.0F * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(sin2 * 10.0F * strength));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.translate(-0.1F * invert, 0.2F, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-10.0F * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-105.0F * invert));
        }
    },

    /** Pouch "Slant" == SkyCore "Slant". */
    SLANT("Slant") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) (Math.PI / 2.0) * 2.0F);
            float rotate = 35.0F * strength;
            poseStack.translate(invert * 0.56F, -0.52F, -0.72F);
            poseStack.translate(0.0F, 0.0F, -0.30F * anim * strength);
            poseStack.mulPose(Axis.XP.rotationDegrees(anim * -rotate));
            poseStack.mulPose(Axis.ZP.rotationDegrees(anim * rotate));
        }
    },

    /** Pouch "DropDown" — использует corner и slant. */
    DROP_DOWN("DropDown") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) (Math.PI / 2.0) * 2.0F);
            poseStack.translate(invert * 0.56F, -0.52F, -0.72F);
            poseStack.mulPose(Axis.YP.rotationDegrees(80.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(corner));
            poseStack.mulPose(Axis.XP.rotationDegrees(-slant * anim * strength));
        }
    },

    /** Pouch "Block" == AlphaDLC "BlockHit" — зависит от equip. */
    BLOCK_HIT("BlockHit") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            if (attack > 0.0F) {
                float g = Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
                poseStack.translate(0.56F * invert, equip * -0.20F - 0.50F, -0.70F);
                poseStack.mulPose(Axis.YP.rotationDegrees(45.0F * invert));
                poseStack.mulPose(Axis.XP.rotationDegrees(g * -85.0F * strength));
                poseStack.translate(-0.10F * invert, 0.28F, 0.20F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-85.0F));
                return;
            }
            float n = -0.4F * Mth.sin(Mth.sqrt(attack) * (float) Math.PI);
            float m = 0.2F * Mth.sin(Mth.sqrt(attack) * (float) (Math.PI * 2.0));
            float f1 = -0.2F * Mth.sin(attack * (float) Math.PI);
            poseStack.translate(n * invert * strength, m * strength, f1 * strength);
            poseStack.translate(invert * 0.56F, -0.52F + equip * -0.60F, -0.72F);
            SMOOTH.apply(poseStack, attack, equip, invert, strength, corner, slant);
        }
    },

    /** Pouch "Pander" == AlphaDLC "Pander" — зависит от equip. */
    PANDER("Pander") {
        @Override
        public void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant) {
            float anim = Mth.sin(attack * (float) Math.PI);
            float f = 1.0F - equip;
            poseStack.translate(invert * 0.56F, -0.52F, -0.72F);
            poseStack.translate((0.30F - anim * 0.15F) * invert, 0.20F - f * 0.12F, -0.15F - anim * 0.13F);
            poseStack.mulPose(Axis.YP.rotationDegrees((76.0F - 10.0F * anim) * invert));
            poseStack.mulPose(Axis.ZP.rotationDegrees((-16.0F - 8.0F * anim) * invert));
            poseStack.mulPose(Axis.XP.rotationDegrees(-83.0F - 26.0F * anim));
        }
    };

    private final String displayName;

    SwingMode(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return this.displayName;
    }

    public boolean isVanilla() {
        return this == VANILLA;
    }

    public abstract void apply(PoseStack poseStack, float attack, float equip, int invert, float strength, float corner, float slant);

    public static SwingMode byDisplayName(String name) {
        for (SwingMode mode : VALUES) {
            if (mode.displayName.equals(name)) {
                return mode;
            }
        }
        return VANILLA;
    }

    /** Кэшируем: values() аллоцирует массив на каждый вызов. */
    private static final SwingMode[] VALUES = values();

    /** Имена для ModeSetting, собираются один раз. */
    private static final String[] DISPLAY_NAMES = buildDisplayNames();

    /** Копия: ModeSetting оборачивает массив через Arrays.asList. */
    public static String[] displayNames() {
        return DISPLAY_NAMES.clone();
    }

    private static String[] buildDisplayNames() {
        String[] names = new String[VALUES.length];
        for (int i = 0; i < VALUES.length; i++) {
            names[i] = VALUES[i].displayName;
        }
        return names;
    }
}
'@
Write-Utf8NoBom (Join-Path $SwerveRoot "src\main\java\swerve\modules\api\SwingMode.java") $swingMode

# ---- src\main\java\swerve\modules\impl\render\SwingAnimation.java ----
$swingAnimation = @'
package swerve.modules.impl.render;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.SwingMode;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Единая точка входа для всех анимаций взмаха руки.
 * Список режимов берётся из SwingMode.values(), поэтому повториться в настройках не может.
 */
public final class SwingAnimation extends Module {

    private static final String DISPLAY = "Animation";

    /** Миксин читает это поле каждый кадр — без поиска по менеджеру. */
    private static volatile SwingAnimation active;

    private final ModeSetting animation = new ModeSetting(
            DISPLAY,
            SwingMode.VANILLA.displayName(),
            SwingMode.displayNames());

    private final FloatSetting strength = new FloatSetting("Strength", 1.0F, 0.1F, 3.0F, 0.01F)
            .visible(() -> this.custom() && !this.animation.is(SwingMode.PANDER.displayName()));

    private final FloatSetting corner = new FloatSetting("Corner", 12.0F, 1.0F, 360.0F, 1.0F)
            .visible(() -> this.animation.is(SwingMode.DROP_DOWN.displayName()));

    private final FloatSetting slant = new FloatSetting("Slant", 12.0F, 1.0F, 360.0F, 1.0F)
            .visible(() -> this.animation.is(SwingMode.DROP_DOWN.displayName()));

    private final BooleanSetting mainHandOnly = new BooleanSetting("Main hand only", true);

    private volatile SwingMode mode = SwingMode.VANILLA;

    public SwingAnimation() {
        super("Swing Animation", "Single entry point for every hand swing animation", ModuleCategory.RENDER);
        this.addSettings(this.animation, this.strength, this.corner, this.slant, this.mainHandOnly);
        this.syncMode();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.syncMode();
        active = this;
    }

    @Override
    public void onDisable() {
        active = null;
        super.onDisable();
    }

    @Override
    public void onTick() {
        this.syncMode();
    }

    private void syncMode() {
        this.mode = SwingMode.byDisplayName(this.animation.getCurrent());
    }

    private boolean custom() {
        return this.mode != SwingMode.VANILLA;
    }

    /**
     * Вызывается миксином из ItemInHandRenderer#swingArm.
     * @return true, если анимация применена и ванильный взмах не нужен.
     */
    public static boolean transform(PoseStack poseStack, float attack, float equip, int invert, HumanoidArm arm) {
        SwingAnimation module = active;
        if (module == null) {
            return false;
        }
        SwingMode mode = module.mode;
        if (mode.isVanilla()) {
            return false;
        }
        if (module.mainHandOnly.isState() && !module.isMainHand(arm)) {
            return false;
        }
        mode.apply(poseStack, attack, equip, invert, module.strength.get(), module.corner.get(), module.slant.get());
        return true;
    }

    private boolean isMainHand(HumanoidArm arm) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft == null ? null : minecraft.player;
        return player == null || player.getMainArm() == arm;
    }
}
'@
Write-Utf8NoBom (Join-Path $SwerveRoot "src\main\java\swerve\modules\impl\render\SwingAnimation.java") $swingAnimation

# ---- src\main\java\swerve\mixins\client\MixinItemInHandRenderer.java ----
$mixinIIR = @'
package swerve.mixins.client;

import swerve.modules.impl.render.SwingAnimation;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Перехватывает обычный взмах руки в ItemInHandRenderer#renderArmWithItem.
 *
 * ordinal = 2 — это вызов в финальном else, то есть взмах без использования предмета,
 * без блока, без riptide и не для BUNDLE. Два других вызова (ordinal 0 и 1) не трогаем,
 * иначе ломаются анимации лука, еды и мешка.
 *
 * Сигнатура сверена с Mojang-маппингами 1.21.4:
 *   private void swingArm(float attack, float inverseArmHeight, PoseStack, int invert, HumanoidArm)
 */
@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    @Shadow
    protected abstract void swingArm(float attack, float inverseArmHeight, PoseStack poseStack, int invert, HumanoidArm arm);

    @Redirect(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;swingArm(FFLcom/mojang/blaze3d/vertex/PoseStack;ILnet/minecraft/world/entity/HumanoidArm;)V",
                    ordinal = 2
            )
    )
    private void swerve$swingArm(ItemInHandRenderer self, float attack, float inverseArmHeight, PoseStack poseStack, int invert, HumanoidArm arm) {
        if (!SwingAnimation.transform(poseStack, attack, inverseArmHeight, invert, arm)) {
            this.swingArm(attack, inverseArmHeight, poseStack, invert, arm);
        }
    }
}
'@
Write-Utf8NoBom (Join-Path $SwerveRoot "src\main\java\swerve\mixins\client\MixinItemInHandRenderer.java") $mixinIIR

# ---- swerve.mixins.json ----
$mjPath = Join-Path $SwerveRoot "src\main\resources\swerve.mixins.json"
$mj = [System.IO.File]::ReadAllText($mjPath)
if ($mj.Contains("MixinItemInHandRenderer")) {
    Write-Host "[=] swerve.mixins.json: миксин уже зарегистрирован" -ForegroundColor Yellow
} else {
    $nl = Get-NewLine $mj
    $needle = '"client.MixinMouseHandler"'
    if (-not $mj.Contains($needle)) {
        throw 'В swerve.mixins.json не найден якорь "client.MixinMouseHandler". Добавь "client.MixinItemInHandRenderer" в массив "client" вручную.'
    }
    $mj = $mj.Replace($needle, $needle + "," + $nl + "`t`t" + '"client.MixinItemInHandRenderer"')
    [System.IO.File]::WriteAllText($mjPath, $mj, $utf8NoBom)
    Write-Host ("[+] " + $mjPath) -ForegroundColor Green
}

# ---- ModuleManager.java ----
$mmPath = Join-Path $SwerveRoot "src\main\java\swerve\managers\ModuleManager.java"
$mm = [System.IO.File]::ReadAllText($mmPath)
$nl = Get-NewLine $mm
$changed = $false

if (-not $mm.Contains("import swerve.modules.impl.render.SwingAnimation;")) {
    $imp = "import swerve.modules.impl.render.ChinaHat;"
    if (-not $mm.Contains($imp)) { throw "В ModuleManager.java не найден якорь '$imp'." }
    $mm = $mm.Replace($imp, $imp + $nl + "import swerve.modules.impl.render.SwingAnimation;")
    $changed = $true
}
if ($mm -notmatch 'new\s+SwingAnimation\s*\(\s*\)') {
    $reg = "register(new Arrows());"
    if (-not $mm.Contains($reg)) { throw "В ModuleManager.java не найден якорь '$reg'." }
    $mm = $mm.Replace($reg, $reg + $nl + "        register(new SwingAnimation());")
    $changed = $true
}
if ($changed) {
    [System.IO.File]::WriteAllText($mmPath, $mm, $utf8NoBom)
    Write-Host ("[+] " + $mmPath) -ForegroundColor Green
} else {
    Write-Host "[=] ModuleManager.java: уже зарегистрирован" -ForegroundColor Yellow
}

# ---- контроль ----
Write-Host ""
Write-Host "=== Контроль ===" -ForegroundColor Cyan
$expected = @(
    @{ Path = "src\main\java\swerve\modules\api\SwingMode.java";                 Min = 11000 },
    @{ Path = "src\main\java\swerve\modules\impl\render\SwingAnimation.java";    Min = 3000  },
    @{ Path = "src\main\java\swerve\mixins\client\MixinItemInHandRenderer.java"; Min = 1500  }
)
$fail = 0
foreach ($e in $expected) {
    $p = Join-Path $SwerveRoot $e.Path
    if (-not (Test-Path -LiteralPath $p)) { Write-Host ("  [X] нет файла: " + $e.Path) -ForegroundColor Red; $fail++; continue }
    $len = (Get-Item -LiteralPath $p).Length
    $hasBom = Test-HasBom $p
    if ($len -lt $e.Min -or $hasBom) {
        Write-Host ("  [X] " + $e.Path + " size=" + $len + " bom=" + $hasBom) -ForegroundColor Red; $fail++
    } else {
        Write-Host ("  [OK] " + $e.Path + "  size=" + $len) -ForegroundColor Green
    }
}

$smPath = Join-Path $SwerveRoot "src\main\java\swerve\modules\api\SwingMode.java"
$modes = ([regex]::Matches([System.IO.File]::ReadAllText($smPath), '(?m)^\s+[A-Z_]+\("[^"]+"\)')).Count
Write-Host ("  Режимов анимации в SwingMode: " + $modes + " (ожидалось 14)")
if ($modes -ne 14) { $fail++ }

try {
    $null = [System.IO.File]::ReadAllText($mjPath) | ConvertFrom-Json
    Write-Host "  [OK] swerve.mixins.json - валидный JSON" -ForegroundColor Green
} catch {
    Write-Host "  [X] swerve.mixins.json - невалидный JSON" -ForegroundColor Red; $fail++
}

$mmFinal = [System.IO.File]::ReadAllText($mmPath)
if ($mmFinal.Contains("import swerve.modules.impl.render.SwingAnimation;") -and ($mmFinal -match 'new\s+SwingAnimation\s*\(\s*\)')) {
    Write-Host "  [OK] ModuleManager: импорт + регистрация на месте" -ForegroundColor Green
} else {
    Write-Host "  [X] ModuleManager: не хватает импорта или регистрации" -ForegroundColor Red; $fail++
}

Write-Host ""
if ($fail -eq 0) {
    Write-Host ("Готово. Дальше: cd '" + $SwerveRoot + "'; .\gradlew.bat runClient") -ForegroundColor Cyan
} else {
    Write-Host ("Ошибок: " + $fail) -ForegroundColor Red
    exit 1
}
