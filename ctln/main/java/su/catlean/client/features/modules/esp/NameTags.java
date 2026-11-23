// deobf by pusia
package su.catlean.client.features.modules.esp;

import net.minecraft.world.item.enchantment.Enchantment;
import su.catlean.utils.KotlinCompat;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Vector4d;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.RenderNameTagEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadRenderer;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.FriendManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.OnlineUsersService;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.WidgetManager;
import su.catlean.client.services.rotation.RotationHelpers;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.inventory.InventoryUtil;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.player.PlayerTargetUtil;
import su.catlean.utils.render.Render2D;

public final class NameTags extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(NameTags.class, "seeCatleanUsers", "getSeeCatleanUsers()Lsu/catlean/zt;", 0), new PropertyReference1Impl(NameTags.class, "nameMode", "getNameMode()Lsu/catlean/m5;", 0), new PropertyReference1Impl(NameTags.class, "components", "getComponents()Lsu/catlean/nl;", 0), new PropertyReference1Impl(NameTags.class, "gamemode", "getGamemode()Z", 0), new PropertyReference1Impl(NameTags.class, "ping", "getPing()Z", 0), new PropertyReference1Impl(NameTags.class, "hp", "getHp()Z", 0), new PropertyReference1Impl(NameTags.class, "distance", "getDistance()Z", 0), new PropertyReference1Impl(NameTags.class, "pops", "getPops()Z", 0), new PropertyReference1Impl(NameTags.class, "enchantss", "getEnchantss()Z", 0), new PropertyReference1Impl(NameTags.class, "onlyHands", "getOnlyHands()Z", 0), new PropertyReference1Impl(NameTags.class, "potions", "getPotions()Z", 0), new PropertyReference1Impl(NameTags.class, "shulkers", "getShulkers()Z", 0), new PropertyReference1Impl(NameTags.class, "selection", "getSelection()Lsu/catlean/nl;", 0), new PropertyReference1Impl(NameTags.class, "spawners", "getSpawners()Z", 0), new PropertyReference1Impl(NameTags.class, "entityOwner", "getEntityOwner()Z", 0), new PropertyReference1Impl(NameTags.class, "fillColorA", "getFillColorA()Ljava/awt/Color;", 0), new PropertyReference1Impl(NameTags.class, "outlineColor", "getOutlineColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(NameTags.class, "fillColorF", "getFillColorF()Ljava/awt/Color;", 0), new PropertyReference1Impl(NameTags.class, "friendOutlineColor", "getFriendOutlineColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(NameTags.class, "armorMode", "getArmorMode()Lsu/catlean/fu;", 0), new PropertyReference1Impl(NameTags.class, "health", "getHealth()Lsu/catlean/z1;", 0), new PropertyReference1Impl(NameTags.class, "healthProvider", "getHealthProvider()Lsu/catlean/d3;", 0) };
   @NotNull
   public static final NameTags INSTANCE = new NameTags();
   @NotNull
   private static final EnumSetting seeCatleanUsers = SettingFactory.defaultImpl(
      INSTANCE, "see-catlean-users", NameTags.SeeCatleanUsers.ON, null, null, 12, null
   );
   @NotNull
   private static final EnumSetting nameMode = SettingFactory.defaultImpl(INSTANCE, "name-mode", NameTags.NameMode.FULL, null, null, 12, null);
   @NotNull
   private static final GroupSetting components = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "components", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting gamemode = SettingFactory.defaultImpl(INSTANCE, "gamemode", false, INSTANCE.getComponents(), null, 8, null);
   @NotNull
   private static final BooleanSetting ping = SettingFactory.defaultImpl(INSTANCE, "ping", false, INSTANCE.getComponents(), null, 8, null);
   @NotNull
   private static final BooleanSetting hP = SettingFactory.defaultImpl(INSTANCE, "h-p", true, INSTANCE.getComponents(), null, 8, null);
   @NotNull
   private static final BooleanSetting distance = SettingFactory.defaultImpl(INSTANCE, "distance", true, INSTANCE.getComponents(), null, 8, null);
   @NotNull
   private static final BooleanSetting totemPops = SettingFactory.defaultImpl(INSTANCE, "totem-pops", true, INSTANCE.getComponents(), null, 8, null);
   @NotNull
   private static final BooleanSetting enchants = SettingFactory.defaultImpl(INSTANCE, "enchants", true, INSTANCE.getComponents(), null, 8, null);
   @NotNull
   private static final BooleanSetting onlyHandsEnchants = SettingFactory.computeBooleanSetting(
      INSTANCE, "only-hands-enchants", false, INSTANCE.getComponents(), () -> INSTANCE.getEnchantss()
   );
   @NotNull
   private static final BooleanSetting potions = SettingFactory.defaultImpl(INSTANCE, "potions", true, INSTANCE.getComponents(), null, 8, null);
   @NotNull
   private static final BooleanSetting shulkers = SettingFactory.defaultImpl(INSTANCE, "shulkers", true, INSTANCE.getComponents(), null, 8, null);
   @NotNull
   private static final GroupSetting selection = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "selection", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting spawners = SettingFactory.defaultImpl(INSTANCE, "spawners", false, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting entityOwner = SettingFactory.defaultImpl(INSTANCE, "entity-owner", false, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final ColorSetting fill = SettingFactory.defaultImpl(INSTANCE, "fill", new Color(150, 150, 150, 255), null, null, 12, null);
   @NotNull
   private static final ColorSetting outline = SettingFactory.defaultImpl(INSTANCE, "outline", new Color(-1), null, null, 12, null);
   @NotNull
   private static final ColorSetting friendFill = SettingFactory.defaultImpl(INSTANCE, "friend-fill", new Color(150, 150, 150, 255), null, null, 12, null);
   @NotNull
   private static final ColorSetting friendOutline = SettingFactory.defaultImpl(INSTANCE, "friend-outline", new Color(0, 255, 0, 255), null, null, 12, null);
   @NotNull
   private static final EnumSetting armorMode = SettingFactory.defaultImpl(INSTANCE, "armor-mode", NameTags.ArmorMode.Full, null, null, 12, null);
   @NotNull
   private static final EnumSetting health = SettingFactory.defaultImpl(INSTANCE, "health", NameTags.Health.Number, null, null, 12, null);
   @NotNull
   private static final EnumSetting healthProvider = SettingFactory.defaultImpl(
      INSTANCE, "health-provider", NameTags.HealthProvider.Entity, null, null, 12, null
   );
   @NotNull
   private static final Map<ResourceKey<Enchantment>, String> map = new HashMap();
   @NotNull
   private static final SvgTexture svgTexture = new SvgTexture("add-friend", 15, 15);
   @NotNull
   private static Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   @NotNull
   private static final TexturedQuadRenderer texturedQuadRenderer = new TexturedQuadRenderer();
   @NotNull
   private static final TexturedQuadRenderer texturedQuadRenderer2 = new TexturedQuadRenderer();
   @NotNull
   private static final Map map2;
   @NotNull
   private static String text = "";

   private NameTags() {
      super("name-tags", SubCategories.getSubCategory7(), null, 4, null);
   }

   @NotNull
   public final NameTags.SeeCatleanUsers getSeeCatleanUsers() {
      return (NameTags.SeeCatleanUsers)seeCatleanUsers.computeObject(this, $$delegatedProperties[0]);
   }

   private final NameTags.NameMode getNameMode() {
      return (NameTags.NameMode)nameMode.computeObject(this, $$delegatedProperties[1]);
   }

   private final GroupValue getComponents() {
      return (GroupValue)components.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getGamemode() {
      return (Boolean)gamemode.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getPing() {
      return (Boolean)ping.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getHp() {
      return (Boolean)hP.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getDistance() {
      return (Boolean)distance.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getPops() {
      return (Boolean)totemPops.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getEnchantss() {
      return (Boolean)enchants.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getOnlyHands() {
      return (Boolean)onlyHandsEnchants.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getPotions() {
      return (Boolean)potions.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getShulkers() {
      return (Boolean)shulkers.computeObject(this, $$delegatedProperties[11]);
   }

   private final GroupValue getSelection() {
      return (GroupValue)selection.computeObject(this, $$delegatedProperties[12]);
   }

   private final boolean getSpawners() {
      return (Boolean)spawners.computeObject(this, $$delegatedProperties[13]);
   }

   private final boolean getEntityOwner() {
      return (Boolean)entityOwner.computeObject(this, $$delegatedProperties[14]);
   }

   private final Color getFillColorA() {
      return (Color)fill.computeObject(this, $$delegatedProperties[15]);
   }

   private final Color getOutlineColor() {
      return (Color)outline.computeObject(this, $$delegatedProperties[16]);
   }

   private final Color getFillColorF() {
      return (Color)friendFill.computeObject(this, $$delegatedProperties[17]);
   }

   private final Color getFriendOutlineColor() {
      return (Color)friendOutline.computeObject(this, $$delegatedProperties[18]);
   }

   private final NameTags.ArmorMode getArmorMode() {
      return (NameTags.ArmorMode)armorMode.computeObject(this, $$delegatedProperties[19]);
   }

   private final NameTags.Health getHealth() {
      return (NameTags.Health)health.computeObject(this, $$delegatedProperties[20]);
   }

   @NotNull
   public final NameTags.HealthProvider getHealthProvider() {
      return (NameTags.HealthProvider)healthProvider.computeObject(this, $$delegatedProperties[21]);
   }

   @Flow
   private final void onRenderNameTag(RenderNameTagEvent renderNameTagEvent) {
      if (renderNameTagEvent.getState() instanceof AvatarRenderState) {
         renderNameTagEvent.cancel();
      }
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      GuiGraphicsExtractor var70 = render2DEvent.getContext();
      animation.applyDoubleValue(
         !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen) && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)
            ? 0.0F
            : 1.0F
      );
      text = "";
      Iterable<AbstractClientPlayer> var72 = MinecraftHolder.getClientLevel().players();

      for (AbstractClientPlayer var82 : (Iterable<AbstractClientPlayer>)CollectionsKt.sortedWith(var72, new NameTags.Ordering())) {
         if (!Intrinsics.areEqual(var82, MinecraftHolder.getMinecraft().player) || !MinecraftHolder.getMinecraft().options.getCameraType().isFirstPerson()) {
            Vector4d var83 = Render2D.INSTANCE.fetchEyeHeight((Entity)var82, this.getHealth() == NameTags.Health.Number ? 0.4F : 0.55F);
            if (var83 != null) {
               String var74 = this.fetchGameProfile((Player)var82);
               float var75 = Fonts.getFontRenderer6().computeAmount(var74);
               float var76 = (float)(var83.x + (float)(var83.z - var83.x) / 2.0F - var75 / 2.0F);
               if (!Float.isNaN(var76)) {
                  float var77 = this.drawAmount((Player)var82, var70, var83.x, var83.y);
                  boolean var78 = false;
                  float var79 = var76 - 2;
                  float var80 = var76 - 2 + var75 + 4;
                  float var81 = WidgetManager.INSTANCE.getAmount();
                  if (var79 <= var81 ? var81 <= var80 : false) {
                     var79 = (float)(var83.y - 13.0F);
                     var80 = (float)(var83.y - 13.0F) + 12;
                     var81 = WidgetManager.INSTANCE.getAmount2();
                     if ((var79 <= var81 ? var81 <= var80 : false) && MinecraftHolder.getMinecraft().gui.screen() != null) {
                        text = var82.getGameProfile().name();
                        var78 = true;
                     }
                  }

                  if (PlayerNameUtil.checkGetGameProfile((Player)var82)) {
                     texturedQuadRenderer2.render(render2DEvent.getContext().pose(), var76 - 2, (float)(var83.y - 13.0F), var75 + 4, 12.0F, var78 ? 0.5F : 1.0F);
                  } else {
                     texturedQuadRenderer.render(render2DEvent.getContext().pose(), var76 - 2, (float)(var83.y - 13.0F), var75 + 4, 12.0F, var78 ? 0.5F : 1.0F);
                  }

                  FontRenderer.defaultImpl(Fonts.getFontRenderer6(), var70.pose(), var74, var76, (float)var83.y - 13.0F, new Color(-1), false, 0, 96, null);
                  if (this.getHealth() != NameTags.Health.Number) {
                     var70.pose().pushMatrix();
                     this.render(
                        var83.x,
                        var83.y,
                        var70,
                        (Player)var82,
                        (float)var82.getAttributeValue(Attributes.MAX_HEALTH),
                        Mth.ceil(var82.getHealth()),
                        Mth.ceil(var82.getAbsorptionAmount())
                     );
                     var70.pose().popMatrix();
                  }

                  if (this.getPotions()) {
                     var77 -= this.drawAmount(var70, (float)var83.x, (float)(var83.y + var77 - 55), (Player)var82);
                  }

                  Item var85 = var82.getMainHandItem().getItem();
                  if (this.getShulkers() && MinecraftHolder.getMinecraft().gui.screen() == null) {
                     var70.pose().translate((float)var83.x - 97.0F, (float)var83.y + var77 - 20.0F);
                     ItemStack var87 = var85 instanceof BlockItem && ((BlockItem)var85).getBlock() instanceof ShulkerBoxBlock
                        ? var82.getMainHandItem()
                        : var82.getOffhandItem();
                     this.render(var70, var87);
                     var70.pose().translate(-((float)var83.x - 97.0F), -((float)var83.y + var77 - 20.0F));
                  }

                  if (MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen
                     || MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen
                     || animation.getAmount() != 0.0F) {
                     SvgTexture.defaultImpl(
                        svgTexture, (Matrix3x2f)var70.pose(), var76 - 18.0, var83.y - 16.0, 0.0F, 0.0F, null, null, animation.getAmount(), 120, null
                     );
                  }

                  var70.pose().popMatrix();
               }
            }
         }
      }

      if (this.getSpawners()) {
         this.render(var70);
      }

      if (this.getEntityOwner()) {
         this.render2(var70);
      }

      texturedQuadRenderer.render(var70.pose(), this.getFillColorA(), this.getOutlineColor(), true);
      texturedQuadRenderer2.render(var70.pose(), this.getFillColorF(), this.getFriendOutlineColor(), true);
      Fonts.getFontRenderer6().update();
      Fonts.getFontRenderer().update();
   }

   @Flow
   private final void onInput(InputEvent inputEvent) {
      if (!StringsKt.isBlank(text)
         && MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen
         && inputEvent.getAction() == InputEvent.Action.Press
         && inputEvent.getDevice() == InputEvent.Device.Mouse) {
         if (FriendManager.INSTANCE.checkContains(text)) {
            FriendManager.INSTANCE.checkRemove(text);
            ChatUtil.defaultImpl(
               this,
               ChatFormatting.RED.toString() + text + ChatFormatting.GRAY + " " + ChatUtil.defaultImpl(this, "removed", new Object[0], false, 4, null),
               false,
               2,
               null
            );
         } else {
            FriendManager.INSTANCE.testText(text);
            ChatUtil.defaultImpl(
               this,
               ChatFormatting.GREEN.toString() + text + ChatFormatting.GRAY + " " + ChatUtil.defaultImpl(this, "added", new Object[0], false, 4, null),
               false,
               2,
               null
            );
         }
      }
   }

   private final float drawAmount(Player player, GuiGraphicsExtractor guiGraphicsExtractor, double amount, double amount2) {
      ArrayList<ItemStack> var26 = new ArrayList();
      float var27 = 0.0F;
      if (this.getArmorMode() != NameTags.ArmorMode.Durability) {
         var26.add(player.getOffhandItem());
      }

      var26.add(InventoryUtil.fetchInventory(player).get(0));
      var26.add(InventoryUtil.fetchInventory(player).get(1));
      var26.add(InventoryUtil.fetchInventory(player).get(2));
      var26.add(InventoryUtil.fetchInventory(player).get(3));
      if (this.getArmorMode() != NameTags.ArmorMode.Durability) {
         var26.add(player.getMainHandItem());
      }

      guiGraphicsExtractor.pose().pushMatrix();
      float var28 = 0.0F;
      if (this.getArmorMode() != NameTags.ArmorMode.None) {
         for (ItemStack var30 : (Iterable<ItemStack>)var26) {
            if (!var30.isEmpty()) {
               if (this.getArmorMode() == NameTags.ArmorMode.Full) {
                  guiGraphicsExtractor.pose().pushMatrix();
                  guiGraphicsExtractor.pose().translate((float)amount - 55.0F + var28, (float)amount2 - 33.0F);
                  guiGraphicsExtractor.pose().scale(1.1F, 1.1F);
                  guiGraphicsExtractor.item(var30, 0, 0);
                  guiGraphicsExtractor.itemDecorations(MinecraftHolder.getMinecraft().font, var30, 0, 0);
                  guiGraphicsExtractor.pose().popMatrix();
               } else {
                  float var31 = var30.getMaxDamage() - var30.getDamageValue();
                  int var32 = (int)(var31 / var30.getMaxDamage() * 100.0F);
                  Color var33 = var32 < 33 ? Color.RED : ((34 <= var32 ? var32 < 66 : false) ? Color.YELLOW : Color.GREEN);
                  FontRenderer.defaultImpl(
                     Fonts.getFontRenderer(),
                     guiGraphicsExtractor.pose(),
                     var32 + "%",
                     (float)amount - 35.0F + var28,
                     (float)amount2 - 22.0F,
                     var33,
                     false,
                     0,
                     96,
                     null
                  );
               }

               float var38 = 0.0F;
               ItemEnchantments var39 = EnchantmentHelper.getEnchantmentsForCrafting(var30);
               if (this.getEnchantss()
                  && (!this.getOnlyHands() || Intrinsics.areEqual(var30, player.getOffhandItem()) || Intrinsics.areEqual(var30, player.getMainHandItem()))) {
                  for (ResourceKey<Enchantment> var34 : map.keySet()) {
                     if (var39.keySet()
                        .contains(((Registry)MinecraftHolder.getClientLevel().registryAccess().lookup(var34.registryKey()).get()).get(var34.identifier()).get())
                        )
                      {
                        String var35 = (String)map.get(var34);
                        int var36 = var39.getLevel(
                           (Holder)((Registry)MinecraftHolder.getClientLevel().registryAccess().lookup(var34.registryKey()).get())
                              .get(var34.identifier())
                              .get()
                        );
                        String var37 = var35 + var36;
                        FontRenderer.defaultImpl(
                           Fonts.getFontRenderer(),
                           guiGraphicsExtractor.pose(),
                           var37,
                           (float)amount - 50.0F + var28,
                           (float)amount2 - 45 + var38,
                           new Color(-1),
                           false,
                           0,
                           96,
                           null
                        );
                        var38 -= 8.0F;
                        if (var27 > var38) {
                           var27 = var38;
                        }
                     }
                  }
               }
            }

            var28 += 18.0F;
         }
      }

      return var27;
   }

   private final String fetchGameProfile(Player player) {
      String var51 = OnlineUsersService.INSTANCE.checkContains2(player.getGameProfile().name())
         ? ChatFormatting.GRAY.toString() + "Ʃ " + ChatFormatting.RESET
         : (OnlineUsersService.INSTANCE.checkContains(player.getGameProfile().name()) ? ChatFormatting.GRAY.toString() + "Ƭ " + ChatFormatting.RESET : "");
      if (this.getPing()) {
         var51 = var51 + this.computeText(this.fetchConnection(player)) + this.fetchConnection(player) + "ms " + ChatFormatting.WHITE;
      }

      if (this.getGamemode()) {
         var51 = var51 + this.computeText(this.fetchConnection2(player)) + " ";
      }
      String var52 = switch (NameTags.WhenMappings.values[this.getNameMode().ordinal()]) {
         case 1 -> RotationHelpers.computeText(player.getDisplayName());
         case 2 -> player.getDisplayName().getString();
         case 3 -> player.getGameProfile().name();
         default -> throw new NoWhenBranchMatchedException();
      };
      CharSequence var53 = var52;
      CharSequence var55 = var53;
      var var56 = new ArrayList(var53.length());

      for (int var58 = 0; var58 < var55.length(); var58++) {
         char var59 = var55.charAt(var58);
         Character var66 = (Character)map2.get(var59);
         var56.add(var66 != null ? var66 : var59);
      }

      var51 = var51 + KotlinCompat.joinToString(var56, "", null, null, 0, null, null, 62, null) + " ";
      if (this.getHp() && this.getHealth() == NameTags.Health.Number) {
         var51 = var51
            + this.computeText(this.fetchScoreboard(player, this.getHealthProvider()))
            + this.computeAmount(this.fetchScoreboard(player, this.getHealthProvider()))
            + " "
            + ChatFormatting.WHITE;
      }

      if (this.getDistance()) {
         Object[] var65 = new Object[]{MinecraftHolder.getLocalPlayer().distanceTo((Entity)player)};
         var51 = var51 + String.format("%.1f", var65) + "m ";
      }

      if (this.getPops() && PlayerTargetUtil.INSTANCE.fetchName(player) != 0) {
         var51 = var51 + ChatFormatting.RESET.toString() + PlayerTargetUtil.INSTANCE.fetchName(player);
      }

      return var51;
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor) {
      for (BlockEntity var20 : (Iterable<BlockEntity>)ServerInfoService.INSTANCE.getItems()) {
         if (var20 instanceof SpawnerBlockEntity) {
            Vector4d var21 = Render2D.INSTANCE
               .computeVector4d(
                  ((SpawnerBlockEntity)var20).getBlockPos().getX() + 0.5,
                  (double)((SpawnerBlockEntity)var20).getBlockPos().getY() + 1.5F,
                  ((SpawnerBlockEntity)var20).getBlockPos().getZ() + 0.5
               );
            if (var21 != null
               && ((SpawnerBlockEntity)var20).getSpawner() != null
               && ((SpawnerBlockEntity)var20)
                     .getSpawner()
                     .getOrCreateDisplayEntity((Level)MinecraftHolder.getClientLevel(), ((SpawnerBlockEntity)var20).getBlockPos())
                  != null) {
               String var10000 = ((SpawnerBlockEntity)var20)
                  .getSpawner()
                  .getOrCreateDisplayEntity((Level)MinecraftHolder.getClientLevel(), ((SpawnerBlockEntity)var20).getBlockPos())
                  .getName()
                  .getString();
               String var24 = "%.1f";
               Object[] var25 = new Object[]{((SpawnerBlockEntity)var20).getSpawner().spawnDelay / 20.0F};
               String var22 = var10000 + " " + String.format(var24, Arrays.copyOf(var25, var25.length)) + "s";
               if (((SpawnerBlockEntity)var20).getSpawner().getSpin() == ((SpawnerBlockEntity)var20).getSpawner().getOSpin()
                  && ((SpawnerBlockEntity)var20).getSpawner().getSpin() == 0.0
                  && ((SpawnerBlockEntity)var20).getSpawner().spawnDelay / 20.0F == 1.0F) {
                  var22 = ((SpawnerBlockEntity)var20)
                        .getSpawner()
                        .getOrCreateDisplayEntity((Level)MinecraftHolder.getClientLevel(), ((SpawnerBlockEntity)var20).getBlockPos())
                        .getName()
                        .getString()
                     + " loot!";
               }

               float var23 = Fonts.getFontRenderer6().computeAmount(var22);
               float var26 = (float)(var21.x + (float)(var21.z - var21.x) / 2.0F - var23 / 2);
               TexturedQuadRenderer.defaultImpl(
                  texturedQuadRenderer, guiGraphicsExtractor.pose(), var26 - 2, (float)(var21.y - 13.0F), var23 + 4, 11.0F, 0.0F, 32, null
               );
               FontRenderer.defaultImpl(
                  Fonts.getFontRenderer6(), guiGraphicsExtractor.pose(), var22, var26, (float)var21.y - 12.5F, new Color(-1), false, 0, 96, null
               );
            }
         }
      }
   }

   private final void render2(GuiGraphicsExtractor guiGraphicsExtractor) {
      for (Entity var18 : MinecraftHolder.getClientLevel().entitiesForRendering()) {
         String var19 = "";
         if (var18 instanceof Projectile) {
            if (((Projectile)var18).getOwner() != null) {
               var19 = ((Projectile)var18).getOwner().getDisplayName().getString();
            }
         } else if (var18 instanceof Horse) {
            LivingEntity var10000 = ((Horse)var18).getOwner();
            if ((var10000 != null ? var10000.getUUID() : null) != null) {
               var19 = ((Horse)var18).getOwner().getUUID().toString();
            }
         } else {
            if (!(var18 instanceof TamableAnimal) || !((TamableAnimal)var18).isTame() || ((TamableAnimal)var18).getOwner() == null) {
               continue;
            }

            var19 = ((TamableAnimal)var18).getOwner().getDisplayName().getString();
         }

         if (var19.length() != 0) {
            Vector4d var20 = Render2D.defaultImpl(Render2D.INSTANCE, var18, 0.0F, 2, null);
            if (var20 != null) {
               String var21 = ChatFormatting.GRAY + "by " + ChatFormatting.RESET + " " + var19;
               float var22 = Fonts.getFontRenderer6().computeAmount(var21);
               float var23 = (float)((var20.x + (float)(var20.z - var20.x) / 2 - var22 / 2) * 1);
               TexturedQuadRenderer.defaultImpl(
                  texturedQuadRenderer, guiGraphicsExtractor.pose(), var23 - 2, (float)(var20.y - 13.0F), var22 + 4, 12.0F, 0.0F, 32, null
               );
               FontRenderer.defaultImpl(
                  Fonts.getFontRenderer6(), guiGraphicsExtractor.pose(), var21, var23, (float)var20.y - 12.0F, new Color(-1), false, 0, 96, null
               );
            }
         }
      }
   }

   public final int fetchConnection(Player entity) {
      ClientPacketListener var10000 = MinecraftHolder.getMinecraft().getConnection();
      if (var10000 != null) {
         PlayerInfo var2 = var10000.getPlayerInfo(entity.getUUID());
         if (var2 != null) {
            return var2.getLatency();
         }
      }

      return 0;
   }

   private final GameType fetchConnection2(Player player) {
      if (player == null) {
         return null;
      } else {
         PlayerInfo var10000 = MinecraftHolder.getMinecraft().getConnection().getPlayerInfo(player.getUUID());
         return var10000 != null ? var10000.getGameMode() : null;
      }
   }

   private final String computeText(GameType gameType) {
      if (gameType == null) {
         return "[BOT]";
      } else {
         return switch (NameTags.WhenMappings.values2[gameType.ordinal()]) {
            case 1 -> "[S]";
            case 2 -> "[C]";
            case 3 -> "[SP]";
            case 4 -> "[A]";
            default -> throw new NoWhenBranchMatchedException();
         };
      }
   }

   public final float fetchScoreboard(Player ent, NameTags.HealthProvider healthProvider) {
      switch (NameTags.WhenMappings.values3[healthProvider.ordinal()]) {
         case 1:
            String var18 = null;
            var18 = "";
            float var20 = 0.0F;
            Scoreboard var10 = ent.level().getScoreboard();
            Objective var11 = var10.getDisplayObjective(DisplaySlot.BELOW_NAME);
            if (var11 != null) {
               String var21;
               label47: {
                  ReadOnlyScoreInfo var15 = var10.getPlayerScoreInfo((ScoreHolder)ent, var11);
                  if (var15 != null) {
                     MutableComponent var16 = var15.formatValue(var11.numberFormatOrDefault((NumberFormat)StyledFormat.NO_STYLE));
                     if (var16 != null) {
                        var21 = var16.getString();
                        break label47;
                     }
                  }

                  var21 = null;
               }

               if (var21 == null) {
                  var21 = "";
               }

               var18 = var21;
            }

            try {
               var20 = Float.parseFloat((String)var18);
            } catch (NumberFormatException var17) {
            }

            return var20;
         case 2:
            Objective var8 = MinecraftHolder.getClientLevel().getScoreboard().getDisplayObjective(DisplaySlot.LIST);
            if ((var8 != null ? var8.getRenderType() : null) == RenderType.HEARTS) {
               ReadOnlyScoreInfo var10000 = MinecraftHolder.getClientLevel()
                  .getScoreboard()
                  .getPlayerScoreInfo(ScoreHolder.fromGameProfile(ent.getGameProfile()), var8);
               Integer var9 = var10000 != null ? var10000.value() : null;
               if (var9 != null) {
                  return var9.intValue();
               }
            }

            return 0.0F;
         default:
            return ent.getHealth() + ent.getAbsorptionAmount();
      }
   }

   private final void render(double amount, double amount2, GuiGraphicsExtractor guiGraphicsExtractor, Player player, float amount3, int value, int value2) {
      guiGraphicsExtractor.pose().pushMatrix();
      guiGraphicsExtractor.pose().translate((float)amount - 44.0F, (float)amount2 + 1.0F);
      guiGraphicsExtractor.pose().scale(1.1F, 1.1F);
      int var17 = Mth.ceil(amount3 / 2.0);
      int var18 = Mth.ceil(value2 / 2.0);
      int var19 = var17 * 2;
      int var20 = 0;

      for (int var21 = var17 + var18 - 1; -1 < var21; var21--) {
         int var22 = var21 % 10;
         int var23 = var22 * 8;
         if (var20 < 10) {
            this.render(guiGraphicsExtractor, NameTags.HeartType.CONTAINER, var23, false, player);
            var20++;
         }

         int var24 = var21 * 2;
         if (var24 < value) {
            boolean var14 = var24 + 1 == value;
            this.render(guiGraphicsExtractor, NameTags.HeartType.NORMAL, var23, var14, player);
         }

         if (var21 >= var17) {
            int var25 = var24 - var19;
            if (var24 - var19 < value2) {
               guiGraphicsExtractor.pose().pushMatrix();
               boolean var26 = var25 + 1 == value2;
               this.render(guiGraphicsExtractor, NameTags.HeartType.ABSORBING, var23, var26, player);
               guiGraphicsExtractor.pose().popMatrix();
            }
         }
      }

      guiGraphicsExtractor.pose().popMatrix();
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, NameTags.HeartType heartType, int value, boolean flag, Player player) {
      if (this.getHealth() == NameTags.Health.Dots) {
         Color var32 = PlayerNameUtil.checkGetGameProfile(player) ? this.getFillColorF() : this.getFillColorA();
         if (heartType == NameTags.HeartType.CONTAINER) {
            Render2D var10000 = Render2D.INSTANCE;
            Matrix3x2fStack var10001 = guiGraphicsExtractor.pose();
            float var10002 = value;
            Color[] var33 = new Color[]{var32};
            float var26 = 3.0F;
            float var27 = 7.0F;
            float var28 = 0.0F;
            float var29 = var10002;
            Matrix3x2fStack var30 = var10001;
            var10000.render(var30, var29, var28, var27, var26, var33);
         } else if (heartType == NameTags.HeartType.NORMAL) {
            if (flag) {
               Render2D var52 = Render2D.INSTANCE;
               Matrix3x2fStack var55 = guiGraphicsExtractor.pose();
               float var58 = value;
               Color[] var49 = new Color[]{this.computeColor(player.getHealth() + player.getAbsorptionAmount())};
               float var34 = 3.0F;
               float var37 = 3.0F;
               float var40 = 0.0F;
               float var43 = var58;
               Matrix3x2fStack var46 = var55;
               var52.render(var46, var43, var40, var37, var34, var49);
               var52 = Render2D.INSTANCE;
               var55 = guiGraphicsExtractor.pose();
               var58 = value + 3.0F;
               var49 = new Color[]{var32};
               var34 = 3.0F;
               var37 = 4.0F;
               var40 = 0.0F;
               var43 = var58;
               var46 = var55;
               var52.render(var46, var43, var40, var37, var34, var49);
            } else {
               Render2D var54 = Render2D.INSTANCE;
               Matrix3x2fStack var57 = guiGraphicsExtractor.pose();
               float var60 = value;
               Color[] var51 = new Color[]{this.computeColor(player.getHealth() + player.getAbsorptionAmount())};
               float var36 = 3.0F;
               float var39 = 7.0F;
               float var42 = 0.0F;
               float var45 = var60;
               Matrix3x2fStack var48 = var57;
               var54.render(var48, var45, var42, var39, var36, var51);
            }
         }
      } else {
         guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, heartType.computeIdentifier(flag), value, 0, 9, 9);
      }
   }

   private final String computeText(float amount) {
      if (amount <= 15.0F && amount > 7.0F) {
         return String.valueOf(ChatFormatting.YELLOW.toString());
      } else {
         return amount > 15.0F
            ? String.valueOf(ChatFormatting.GREEN.toString())
            : String.valueOf(ChatFormatting.RED.toString());
      }
   }

   private final String computeText(int value) {
      if (value > ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null)) {
         return String.valueOf(ChatFormatting.GREEN.toString());
      } else {
         return value <= ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null)
               && Math.abs(value - ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null)) < 15
            ? String.valueOf(ChatFormatting.YELLOW.toString())
            : String.valueOf(ChatFormatting.RED.toString());
      }
   }

   private final Color computeColor(float amount) {
      if (amount <= 15.0F && amount > 7.0F) {
         return Color.YELLOW;
      } else {
         return amount > 15.0F ? Color.GREEN : Color.RED;
      }
   }

   public final float computeAmount(float amount) {
      return !Float.isNaN(amount) && !Float.isInfinite(amount) ? new BigDecimal((double)amount).setScale(1, RoundingMode.HALF_UP).floatValue() : 1.0F;
   }

   private final float drawAmount(GuiGraphicsExtractor guiGraphicsExtractor, float amount, float amount2, Player player) {
      ArrayList var16 = new ArrayList(player.getActiveEffects());
      if (var16.isEmpty()) {
         return 0.0F;
      } else {
         float var15 = amount + var16.size() * 12.5F;
         float var17 = 0.0F;

         for (MobEffectInstance var19 : (Iterable<MobEffectInstance>)com.google.common.collect.Ordering.natural().reverse().sortedCopy(var16)) {
            var15 -= 25.0F;
            String var20 = "";
            switch (var19.getAmplifier()) {
               case 0:
                  var20 = "I";
                  break;
               case 1:
                  var20 = "II";
                  break;
               case 2:
                  var20 = "III";
                  break;
               case 3:
                  var20 = "IV";
                  break;
               case 4:
                  var20 = "V";
            }

            guiGraphicsExtractor.pose().pushMatrix();
            guiGraphicsExtractor.pose().translate(var15, amount2);
            guiGraphicsExtractor.blitSprite(
               RenderPipelines.GUI_TEXTURED, ((ResourceKey)var19.getEffect().unwrapKey().orElseThrow()).identifier(), 0, 0, 18, 18, -1
            );
            String var21 = this.fetchDuration(var19);
            FontRenderer.defaultImpl(
               Fonts.getFontRenderer(),
               guiGraphicsExtractor.pose(),
               var21,
               9.0F - Fonts.getFontRenderer().computeAmount(var21) / 2.0F,
               -8.0F,
               new Color(-1),
               false,
               0,
               96,
               null
            );
            FontRenderer.defaultImpl(
               Fonts.getFontRenderer(),
               guiGraphicsExtractor.pose(),
               var20,
               9.0F - Fonts.getFontRenderer().computeAmount(var20) / 2.0F,
               -16.0F,
               new Color(-1),
               false,
               0,
               96,
               null
            );
            guiGraphicsExtractor.pose().popMatrix();
            var17 = 40.0F;
         }

         return var17;
      }
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack) {
      ItemContainerContents var10000 = (ItemContainerContents)itemStack.get(DataComponents.CONTAINER);
      if (var10000 != null) {
         ItemContainerContents var8 = var10000;
         Item var9 = itemStack.getItem();
         if (var9 instanceof BlockItem && ((BlockItem)var9).getBlock() instanceof ShulkerBoxBlock) {
            List<ItemStack> var10 = var8.allItemsCopyStream().toList();
            if (!var10.isEmpty()) {
               TexturedQuadRenderer.defaultImpl(texturedQuadRenderer, guiGraphicsExtractor.pose(), 12.0F, -79.0F, 169.0F, 60.0F, 0.0F, 32, null);
               int var11 = 0;
               int var12 = 0;

               for (ItemStack var14 : (Iterable<ItemStack>)var10) {
                  guiGraphicsExtractor.item(var14, 16 + var12 * 18, -75 + var11 * 18);
                  guiGraphicsExtractor.itemDecorations(MinecraftHolder.getMinecraft().font, var14, 16 + var12 * 18, -75 + var11 * 18);
                  if (++var12 >= 9) {
                     var12 = 0;
                     var11++;
                  }
               }
            }
         }
      }
   }

   @NotNull
   public final String fetchDuration(MobEffectInstance pe) {
      String var10000;
      if (pe.isInfiniteDuration()) {
         var10000 = "*:*";
      } else {
         int var5 = pe.getDuration() / 1200;
         String var3 = "%02d";
         Object[] var4 = new Object[]{pe.getDuration() % 1200 / 20};
         var10000 = var5 + ":" + String.format(var3, Arrays.copyOf(var4, var4.length));
      }

      return var10000;
   }

   static {
      Pair[] var51 = new Pair[]{
         TuplesKt.to('ᴀ', 'A'),
         TuplesKt.to('ʙ', 'B'),
         TuplesKt.to('ᴄ', 'C'),
         TuplesKt.to('ᴅ', 'D'),
         TuplesKt.to('ᴇ', 'E'),
         TuplesKt.to('ꜰ', 'F'),
         TuplesKt.to('ɢ', 'G'),
         TuplesKt.to('ʜ', 'H'),
         TuplesKt.to('ɪ', 'I'),
         TuplesKt.to('ᴊ', 'J'),
         TuplesKt.to('ᴋ', 'K'),
         TuplesKt.to('ʟ', 'L'),
         TuplesKt.to('ᴍ', 'M'),
         TuplesKt.to('ɴ', 'N'),
         TuplesKt.to('ᴏ', 'O'),
         TuplesKt.to('ᴘ', 'P'),
         TuplesKt.to('ꞯ', 'Q'),
         TuplesKt.to('ʀ', 'R'),
         TuplesKt.to('ꜱ', 'S'),
         TuplesKt.to('ᴛ', 'T'),
         TuplesKt.to('ᴜ', 'U'),
         TuplesKt.to('ᴠ', 'V'),
         TuplesKt.to('ᴡ', 'W'),
         TuplesKt.to('ʏ', 'Y'),
         TuplesKt.to('ᴢ', 'Z')
      };
      map2 = MapsKt.mapOf(var51);
      map.put(Enchantments.BLAST_PROTECTION, "B");
      map.put(Enchantments.PROTECTION, "P");
      map.put(Enchantments.SHARPNESS, "S");
      map.put(Enchantments.EFFICIENCY, "E");
      map.put(Enchantments.UNBREAKING, "U");
      map.put(Enchantments.POWER, "PO");
      map.put(Enchantments.THORNS, "T");
   }

   public static enum ArmorMode {
      None,
      Full,
      Durability;

      private static final NameTags.ArmorMode[] armorModes = $values();
      private static final EnumEntries<NameTags.ArmorMode> enumEntries = EnumEntriesKt.enumEntries(armorModes);

      private ArmorMode() {
      }

      @NotNull
      public static EnumEntries<NameTags.ArmorMode> getEnumEntries() {
         return enumEntries;
      }

      private static final NameTags.ArmorMode[] $values() {
         return new NameTags.ArmorMode[]{None, Full, Durability};
      }
   }

   public static enum Health {
      Number,
      Hearts,
      Dots;

      private static final NameTags.Health[] healths = $values();
      private static final EnumEntries<NameTags.Health> enumEntries = EnumEntriesKt.enumEntries(healths);

      private Health() {
      }

      @NotNull
      public static EnumEntries<NameTags.Health> getEnumEntries() {
         return enumEntries;
      }

      private static final NameTags.Health[] $values() {
         return new NameTags.Health[]{Number, Hearts, Dots};
      }
   }

   public static enum HealthProvider {
      Entity,
      ScoreBoard,
      PlayerList;

      private static final NameTags.HealthProvider[] healthProviders = $values();
      private static final EnumEntries<NameTags.HealthProvider> enumEntries = EnumEntriesKt.enumEntries(healthProviders);

      private HealthProvider() {
      }

      @NotNull
      public static EnumEntries<NameTags.HealthProvider> getEnumEntries() {
         return enumEntries;
      }

      private static final NameTags.HealthProvider[] $values() {
         return new NameTags.HealthProvider[]{Entity, ScoreBoard, PlayerList};
      }
   }

   public static enum HeartType {
      CONTAINER(Identifier.withDefaultNamespace("hud/heart/container"), Identifier.withDefaultNamespace("hud/heart/container")),
      NORMAL(Identifier.withDefaultNamespace("hud/heart/full"), Identifier.withDefaultNamespace("hud/heart/half")),
      ABSORBING(Identifier.withDefaultNamespace("hud/heart/absorbing_full"), Identifier.withDefaultNamespace("hud/heart/absorbing_half"));

      @NotNull
      private final Identifier identifier;
      @NotNull
      private final Identifier identifier2;
      private static final NameTags.HeartType[] heartTypes = $values();
      private static final EnumEntries<NameTags.HeartType> enumEntries = EnumEntriesKt.enumEntries(heartTypes);

      private HeartType(Identifier identifier, Identifier identifier2) {
         this.identifier = identifier;
         this.identifier2 = identifier2;
      }

      @NotNull
      public final Identifier computeIdentifier(boolean flag) {
         return flag ? this.identifier2 : this.identifier;
      }

      @NotNull
      public static EnumEntries<NameTags.HeartType> getEnumEntries() {
         return enumEntries;
      }

      private static final NameTags.HeartType[] $values() {
         return new NameTags.HeartType[]{CONTAINER, NORMAL, ABSORBING};
      }
   }

   public static enum NameMode {
      DEFAULT,
      FULL,
      SEMI;

      private static final NameTags.NameMode[] nameModes = $values();
      private static final EnumEntries<NameTags.NameMode> enumEntries = EnumEntriesKt.enumEntries(nameModes);

      private NameMode() {
      }

      @NotNull
      public static EnumEntries<NameTags.NameMode> getEnumEntries() {
         return enumEntries;
      }

      private static final NameTags.NameMode[] $values() {
         return new NameTags.NameMode[]{DEFAULT, FULL, SEMI};
      }
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         AbstractClientPlayer var11 = (AbstractClientPlayer)object2;
         Comparable var10000 = NameTags.INSTANCE
            .computeVec3((Entity)MinecraftHolder.getLocalPlayer())
            .distanceToSqr(NameTags.INSTANCE.computeVec3((Entity)var11));
         var11 = (AbstractClientPlayer)object;
         Comparable var13 = var10000;
         return ComparisonsKt.compareValues(
            var13, NameTags.INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceToSqr(NameTags.INSTANCE.computeVec3((Entity)var11))
         );
      }
   }

   public static enum SeeCatleanUsers {
      ON,
      ANONYMOUSLY,
      OFF;

      private static final NameTags.SeeCatleanUsers[] seeCatleanUserss = $values();
      private static final EnumEntries<NameTags.SeeCatleanUsers> enumEntries = EnumEntriesKt.enumEntries(seeCatleanUserss);

      private SeeCatleanUsers() {
      }

      @NotNull
      public static EnumEntries<NameTags.SeeCatleanUsers> getEnumEntries() {
         return enumEntries;
      }

      private static final NameTags.SeeCatleanUsers[] $values() {
         return new NameTags.SeeCatleanUsers[]{ON, ANONYMOUSLY, OFF};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;
      public static final int[] values2;
      public static final int[] values3;

      static {
         int[] var0 = new int[NameTags.NameMode.values().length];

         try {
            var0[NameTags.NameMode.FULL.ordinal()] = 1;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var0[NameTags.NameMode.SEMI.ordinal()] = 2;
         } catch (NoSuchFieldError var9) {
         }

         try {
            var0[NameTags.NameMode.DEFAULT.ordinal()] = 3;
         } catch (NoSuchFieldError var8) {
         }

         values = var0;
         var0 = new int[GameType.values().length];

         try {
            var0[GameType.SURVIVAL.ordinal()] = 1;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var0[GameType.CREATIVE.ordinal()] = 2;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var0[GameType.SPECTATOR.ordinal()] = 3;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var0[GameType.ADVENTURE.ordinal()] = 4;
         } catch (NoSuchFieldError var4) {
         }

         values2 = var0;
         var0 = new int[NameTags.HealthProvider.values().length];

         try {
            var0[NameTags.HealthProvider.ScoreBoard.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[NameTags.HealthProvider.PlayerList.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         values3 = var0;
      }
   }
}