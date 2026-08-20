package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class StaffListRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private static final String POINT = " • ";
    private static final int DOT_COLOR = new Color(180, 180, 200, 255).getRGB();

    private final Pattern prefixMatches = Pattern.compile(".(mod|moder|adm|help|wne|хелп|адм|поддержка|кура|own|taf|curat|dev|supp|yt|сотруд|Адм|осн|миум).");
    private final DragSetting drag;
    private final List<PreviewStaff> previewStaffList;
    private final Map<NetworkPlayerInfo, StaffMember> staffMembers = new HashMap<>();

    private float animatedWidth, animatedHeight;
    @Getter
    @Setter
    private float scaleV, blurStrength = 10f, outlineWidth = 1.0f;
    @Getter
    @Setter
    private boolean blurEnabled = true, enableOutline = false;

    public float scale = 0.9f, titleDarkness = 0.0f, newRectGap = 3.0f, dynamicRectsGap = 3.0f, iconRectGap = 2.0f;
    public boolean showTitleIcon = true, showPlayerIcon = true, showStatusDot = true, showCount = true, showTitle = true, dynamicRects = false, newRect = false, separateIconRect = false;

    private float lastX, lastY, smoothVelocityX, smoothVelocityY, currentRotation, targetRotation, springOffsetX, springOffsetY, dragScaleEffect = 1.0f;
    private boolean isDragging;
    private long lastMoveTime;

    private static class StaffMember {
        NetworkPlayerInfo info;
        Animation animation = new Animation();
        Animation headAnimation = new Animation();

        StaffMember(NetworkPlayerInfo info) {
            this.info = info;
        }
    }

    private record PreviewStaff(String prefix, String name, int color) {
    }

    public StaffListRenderer(DragSetting drag) {
        this.drag = drag;
        this.previewStaffList = List.of(
                new PreviewStaff("[OWNER]", "Fungetx", new Color(255, 85, 85).getRGB()),
                new PreviewStaff("[/ЖИРНЫЙ]", "Zako", new Color(241, 124, 4).getRGB()),
                new PreviewStaff("[ВУМНЫЙ]", "Pioner", new Color(229, 221, 221).getRGB())
        );
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER, () -> scale, v -> scale = v, 0.5f, 2.0f, 0.1f),
                new DragComponent.HudSetting("Title Darkness", DragComponent.SettingType.SLIDER, () -> titleDarkness, v -> titleDarkness = v, 0.0f, 1.0f, 0.05f),
                new DragComponent.HudSetting("Title Icon", DragComponent.SettingType.BOOLEAN, () -> showTitleIcon ? 1f : 0f, v -> showTitleIcon = v > 0.5f),
                new DragComponent.HudSetting("Player Icon", DragComponent.SettingType.BOOLEAN, () -> showPlayerIcon ? 1f : 0f, v -> showPlayerIcon = v > 0.5f),
                new DragComponent.HudSetting("Status Dot", DragComponent.SettingType.BOOLEAN, () -> showStatusDot ? 1f : 0f, v -> showStatusDot = v > 0.5f),
                new DragComponent.HudSetting("Show Count", DragComponent.SettingType.BOOLEAN, () -> showCount ? 1f : 0f, v -> showCount = v > 0.5f),
                new DragComponent.HudSetting("Show Title", DragComponent.SettingType.BOOLEAN, () -> showTitle ? 1f : 0f, v -> showTitle = v > 0.5f),
                new DragComponent.HudSetting("Dynamic Rects", DragComponent.SettingType.BOOLEAN, () -> dynamicRects ? 1f : 0f, v -> dynamicRects = v > 0.5f),
                new DragComponent.HudSetting("Dynamic Rects Gap", DragComponent.SettingType.SLIDER, () -> dynamicRectsGap, v -> dynamicRectsGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("Separate Icon Rect", DragComponent.SettingType.BOOLEAN, () -> separateIconRect ? 1f : 0f, v -> separateIconRect = v > 0.5f),
                new DragComponent.HudSetting("Icon Rect Gap", DragComponent.SettingType.SLIDER, () -> iconRectGap, v -> iconRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("New Rect", DragComponent.SettingType.BOOLEAN, () -> newRect ? 1f : 0f, v -> newRect = v > 0.5f),
                new DragComponent.HudSetting("New Rect Gap", DragComponent.SettingType.SLIDER, () -> newRectGap, v -> newRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN, () -> enableOutline ? 1f : 0f, v -> enableOutline = v > 0.5f),
                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER, () -> outlineWidth, v -> outlineWidth = v, 0.5f, 3.0f, 0.1f)
        );
    }

    private float smoothLerp(float c, float t, float s) {
        return c + (t - c) * (1.0f - (float) Math.pow(1.0f - s, 1.5f));
    }

    private float elasticLerp(float c, float t, float s, float e) {
        float d = t - c, v = d * s;
        if (Math.abs(d) < 0.5f) v += d * e * 0.3f;
        return c + v;
    }

    private void updateDragAnimation(float x, float y, float w, float h) {
        float ivx = x - lastX, ivy = y - lastY;
        boolean moving = Math.abs(ivx) > 0.01f || Math.abs(ivy) > 0.01f;

        if (moving) {
            lastMoveTime = System.currentTimeMillis();
            isDragging = true;
        } else if (System.currentTimeMillis() - lastMoveTime > 50) isDragging = false;

        smoothVelocityX = smoothLerp(smoothVelocityX, ivx, isDragging ? 0.25f : 0.08f);
        smoothVelocityY = smoothLerp(smoothVelocityY, ivy, isDragging ? 0.25f : 0.08f);

        targetRotation = Math.max(-15f, Math.min(15f, smoothVelocityX * 1.8f - smoothVelocityY * 0.3f));
        currentRotation = elasticLerp(currentRotation, targetRotation, isDragging ? 0.18f : 0.06f, 0.15f);
        if (!isDragging && Math.abs(currentRotation) < 0.1f) currentRotation *= 0.9f;

        springOffsetX = Math.max(-5f, Math.min(5f, springOffsetX * 0.85f + (-smoothVelocityX * 0.3f - springOffsetX) * 0.12f));
        springOffsetY = Math.max(-5f, Math.min(5f, springOffsetY * 0.85f + (-smoothVelocityY * 0.2f - springOffsetY) * 0.12f));

        dragScaleEffect = smoothLerp(dragScaleEffect, isDragging ? 1.02f : 1.0f, 0.15f);
        lastX = x;
        lastY = y;
    }

    private void applyDragTransform(MatrixStack m, float x, float y, float w, float h) {
        m.translate(springOffsetX, springOffsetY, 0);
        if (Math.abs(currentRotation) > 0.01f) {
            float px = x + w / 2f, py = y + h * 0.15f;
            m.translate(px, py, 0);
            m.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(currentRotation));
            m.translate(-px, -py, 0);
        }
    }

    @Override
    public void render(Render2DEvent event) {
        render(event, false);
    }

    public void render(Render2DEvent event, boolean glow) {
        boolean isPreview = mc.currentScreen instanceof ChatScreen;
        List<StaffMember> visibleStaff = new ArrayList<>();

        if (!isPreview && mc.player != null && mc.world != null && mc.getConnection() != null) {
            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
                ScorePlayerTeam team = info.getPlayerTeam();
                String prefix = team != null ? team.getPrefix().getString() : "";
                if (prefixMatches.matcher(prefix.toLowerCase()).find()) {
                    StaffMember m = staffMembers.computeIfAbsent(info, StaffMember::new);
                    m.animation.update();
                    m.headAnimation.update();
                    m.animation.run(1, 0.5f, Easings.EXPO_OUT, false);
                    m.headAnimation.run(1, 0.3f, Easings.EXPO_OUT, false);
                    visibleStaff.add(m);
                }
            }
            staffMembers.keySet().retainAll(mc.getConnection().getPlayerInfoMap());
        }

        int staffCount = isPreview ? previewStaffList.size() : visibleStaff.size();
        MatrixStack matrix = event.getMatrix();
        float x = drag.position.x, y = drag.position.y;
        float finalScale = scale;
        float padding = 6f * finalScale, fontSize = 7f * finalScale, radius = 6f * finalScale;
        float titleBoxHeight = showTitle ? 18f * finalScale : 0f, itemRowHeight = 18f * finalScale, iconSize = 10f * finalScale;

        float titleIconSize = 10f * finalScale;
        float titleIconWidth = showTitleIcon ? (titleIconSize + 6f * finalScale) : 0f;
        float titleTextWidth = font.getWidth("Staff", fontSize);
        float gap = 7.5f * finalScale;
        float countBoxWidth = showCount ? (font.getWidth(String.valueOf(staffCount), 6f * finalScale) + 8f * finalScale) : 0f;
        float titleWidth = padding + titleIconWidth + titleTextWidth + (showCount && dynamicRects ? (gap + countBoxWidth) : 0f) + padding;
        float maxWidth = titleWidth;
        float pointWidth = font.getWidth(POINT, fontSize);

        // Sort staff by width for dynamic rects
        List<Object> sortedStaff = new ArrayList<>();
        if (dynamicRects) {
            if (isPreview) {
                sortedStaff.addAll(previewStaffList);
                sortedStaff.sort((o1, o2) -> {
                    PreviewStaff s1 = (PreviewStaff) o1;
                    PreviewStaff s2 = (PreviewStaff) o2;
                    float w1, w2;
                    if (separateIconRect && showPlayerIcon) {
                        float iconRectSize = iconSize + padding * 2;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        float textW1 = padding + font.getWidth(s1.prefix, fontSize) + pointWidth + font.getWidth(s1.name, fontSize) + dw + padding;
                        float textW2 = padding + font.getWidth(s2.prefix, fontSize) + pointWidth + font.getWidth(s2.name, fontSize) + dw + padding;
                        w1 = iconRectSize + iconRectGap * finalScale + textW1;
                        w2 = iconRectSize + iconRectGap * finalScale + textW2;
                    } else {
                        float iw = showPlayerIcon ? (iconSize + 6f * finalScale) : 0f;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        w1 = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(s1.prefix, fontSize) + pointWidth + font.getWidth(s1.name, fontSize) + dw + padding;
                        w2 = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(s2.prefix, fontSize) + pointWidth + font.getWidth(s2.name, fontSize) + dw + padding;
                    }
                    return Float.compare(w2, w1); // Descending order (widest first)
                });
            } else {
                sortedStaff.addAll(visibleStaff);
                sortedStaff.sort((o1, o2) -> {
                    StaffMember s1 = (StaffMember) o1;
                    StaffMember s2 = (StaffMember) o2;
                    ScorePlayerTeam t1 = s1.info.getPlayerTeam();
                    ScorePlayerTeam t2 = s2.info.getPlayerTeam();
                    String p1 = t1 != null ? t1.getPrefix().getString() : "";
                    String p2 = t2 != null ? t2.getPrefix().getString() : "";
                    String n1 = s1.info.getGameProfile().getName();
                    String n2 = s2.info.getGameProfile().getName();
                    float w1, w2;
                    if (separateIconRect && showPlayerIcon) {
                        float iconRectSize = iconSize + padding * 2;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        float textW1 = padding + font.getWidth(p1, fontSize) + pointWidth + font.getWidth(n1, fontSize) + dw + padding;
                        float textW2 = padding + font.getWidth(p2, fontSize) + pointWidth + font.getWidth(n2, fontSize) + dw + padding;
                        w1 = iconRectSize + iconRectGap * finalScale + textW1;
                        w2 = iconRectSize + iconRectGap * finalScale + textW2;
                    } else {
                        float iw = showPlayerIcon ? (iconSize + 6f * finalScale) : 0f;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        w1 = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(p1, fontSize) + pointWidth + font.getWidth(n1, fontSize) + dw + padding;
                        w2 = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(p2, fontSize) + pointWidth + font.getWidth(n2, fontSize) + dw + padding;
                    }
                    return Float.compare(w2, w1); // Descending order (widest first)
                });
            }
            
            // Make title width match the widest staff member (first in sorted list)
            if (!sortedStaff.isEmpty()) {
                if (isPreview) {
                    PreviewStaff widest = (PreviewStaff) sortedStaff.get(0);
                    if (separateIconRect && showPlayerIcon) {
                        float iconRectSize = iconSize + padding * 2;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        float textW = padding + font.getWidth(widest.prefix, fontSize) + pointWidth + font.getWidth(widest.name, fontSize) + dw + padding;
                        titleWidth = iconRectSize + iconRectGap * finalScale + textW;
                    } else {
                        float iw = showPlayerIcon ? (iconSize + 6f * finalScale) : 0f;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        titleWidth = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(widest.prefix, fontSize) + pointWidth + font.getWidth(widest.name, fontSize) + dw + padding;
                    }
                } else {
                    StaffMember widest = (StaffMember) sortedStaff.get(0);
                    ScorePlayerTeam t = widest.info.getPlayerTeam();
                    String p = t != null ? t.getPrefix().getString() : "";
                    String n = widest.info.getGameProfile().getName();
                    if (separateIconRect && showPlayerIcon) {
                        float iconRectSize = iconSize + padding * 2;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        float textW = padding + font.getWidth(p, fontSize) + pointWidth + font.getWidth(n, fontSize) + dw + padding;
                        titleWidth = iconRectSize + iconRectGap * finalScale + textW;
                    } else {
                        float iw = showPlayerIcon ? (iconSize + 6f * finalScale) : 0f;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        titleWidth = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(p, fontSize) + pointWidth + font.getWidth(n, fontSize) + dw + padding;
                    }
                }
                maxWidth = titleWidth;
            }
        } else {
            if (isPreview) {
                sortedStaff.addAll(previewStaffList);
            } else {
                sortedStaff.addAll(visibleStaff);
            }
        }

        if (!dynamicRects) {
            if (isPreview) {
                for (Object o : sortedStaff) {
                    PreviewStaff s = (PreviewStaff) o;
                    float w;
                    if (separateIconRect && showPlayerIcon) {
                        float iconRectSize = iconSize + padding * 2;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        float textW = padding + font.getWidth(s.prefix, fontSize) + pointWidth + font.getWidth(s.name, fontSize) + dw + padding;
                        w = iconRectSize + iconRectGap * finalScale + textW;
                    } else {
                        float iw = showPlayerIcon ? (iconSize + 6f * finalScale) : 0f;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        w = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(s.prefix, fontSize) + pointWidth + font.getWidth(s.name, fontSize) + dw + padding;
                    }
                    maxWidth = Math.max(maxWidth, w);
                }
            } else {
                for (Object o : sortedStaff) {
                    StaffMember s = (StaffMember) o;
                    ScorePlayerTeam t = s.info.getPlayerTeam();
                    String p = t != null ? t.getPrefix().getString() : "";
                    String n = s.info.getGameProfile().getName();
                    float w;
                    if (separateIconRect && showPlayerIcon) {
                        float iconRectSize = iconSize + padding * 2;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        float textW = padding + font.getWidth(p, fontSize) + pointWidth + font.getWidth(n, fontSize) + dw + padding;
                        w = iconRectSize + iconRectGap * finalScale + textW;
                    } else {
                        float iw = showPlayerIcon ? (iconSize + 6f * finalScale) : 0f;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        w = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(p, fontSize) + pointWidth + font.getWidth(n, fontSize) + dw + padding;
                    }
                    maxWidth = Math.max(maxWidth, w);
                }
            }
        }

        float targetHeight = titleBoxHeight + (staffCount * (itemRowHeight + (dynamicRects ? dynamicRectsGap * finalScale : 0)));
        animatedWidth = Mathf.fast(animatedWidth, maxWidth, 14);
        animatedHeight = Mathf.fast(animatedHeight, targetHeight, 14);

        if (x == 0 && y == 0) {
            drag.position.set(4, 250);
            x = 4;
            y = 250;
            lastX = x;
            lastY = y;
        }

        updateDragAnimation(x, y, animatedWidth, animatedHeight);
        scaleV = Mathf.fast(scaleV, (isPreview || !visibleStaff.isEmpty()) ? 1 : 0, 16);
        if (Mathf.round(scaleV, .1d) <= 0) return;

        boolean alignRight = x > mc.getMainWindow().getScaledWidth() / 2f;

        matrix.push();
        matrix.translate(x + animatedWidth / 2F, y + animatedHeight / 2f, 0);
        matrix.scale(scaleV * dragScaleEffect, scaleV * dragScaleEffect, scaleV);
        matrix.translate(-(x + animatedWidth / 2F), -(y + animatedHeight / 2f), 0);
        applyDragTransform(matrix, x, y, animatedWidth, animatedHeight);

        if (!dynamicRects)
            RenderUtil.CustomStyled.draw(matrix, x, y, animatedWidth, animatedHeight, Round.of(radius), blurEnabled, blurStrength);

        float currentY = y;

        if (showTitle) {
            if (dynamicRects) {
                float titleX = alignRight ? x + (animatedWidth - titleWidth) : x;
                RenderUtil.CustomStyled.draw(matrix, titleX, currentY, titleWidth, titleBoxHeight, Round.of(radius), blurEnabled, blurStrength);
                ScissorUtil.enableScissor(titleX, currentY, titleWidth, titleBoxHeight);
                RenderUtil.Rounded.smooth(matrix, titleX, currentY, titleWidth, titleBoxHeight, ColorUtil.replAlpha(0xFF000000, titleDarkness), Round.of(0, radius, 0, radius));
                ScissorUtil.disableScissor();
                renderTitle(matrix, titleX, currentY, titleWidth, titleBoxHeight, fontSize, titleIconSize, finalScale, staffCount, gap, true);
                if (enableOutline)
                    drawOutline(matrix, titleX, currentY, titleWidth, titleBoxHeight, finalScale, radius);
            } else {
                ScissorUtil.enableScissor(x, currentY, animatedWidth, titleBoxHeight);
                RenderUtil.Rounded.smooth(matrix, x, currentY, animatedWidth, animatedHeight, ColorUtil.replAlpha(0xFF000000, titleDarkness), Round.of(0, radius, 0, radius));
                ScissorUtil.disableScissor();
                renderTitle(matrix, x, currentY, animatedWidth, titleBoxHeight, fontSize, titleIconSize, finalScale, staffCount, gap, false);
            }
        }

        currentY += titleBoxHeight + (dynamicRects ? dynamicRectsGap * finalScale : 0);

        if (isPreview) {
            for (Object o : sortedStaff) {
                PreviewStaff s = (PreviewStaff) o;
                if (dynamicRects) {
                    float w;
                    if (separateIconRect && showPlayerIcon) {
                        float iconRectSize = iconSize + padding * 2;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        float textW = padding + font.getWidth(s.prefix, fontSize) + pointWidth + font.getWidth(s.name, fontSize) + dw + padding;
                        w = iconRectSize + iconRectGap * finalScale + textW;
                    } else {
                        float iw = showPlayerIcon ? (iconSize + 6f * finalScale) : 0f;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        w = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(s.prefix, fontSize) + pointWidth + font.getWidth(s.name, fontSize) + dw + padding;
                    }
                    float ix = alignRight ? x + (animatedWidth - w) : x;
                    renderStaffDyn(matrix, ix, currentY, w, itemRowHeight, s.prefix, s.name, s.color, Color.GREEN.getRGB(),
                            null, 1f, 1f, fontSize, iconSize, finalScale, pointWidth, radius, alignRight);
                    currentY += itemRowHeight + dynamicRectsGap * finalScale;
                } else {
                    renderStaff(matrix, x, currentY, animatedWidth, itemRowHeight, s.prefix, s.name, s.color, Color.GREEN.getRGB(),
                            null, 1f, 1f, fontSize, iconSize, finalScale, pointWidth);
                    currentY += itemRowHeight;
                }
            }
        } else {
            for (Object o : sortedStaff) {
                StaffMember s = (StaffMember) o;
                float a = (float) s.animation.getValue(), ha = (float) s.headAnimation.getValue();
                ScorePlayerTeam t = s.info.getPlayerTeam();
                String p = t != null ? t.getPrefix().getString() : "", n = s.info.getGameProfile().getName();
                int pc = getPrefixColor(t);
                AbstractClientPlayerEntity pl = (AbstractClientPlayerEntity) mc.world.getPlayerByUuid(s.info.getGameProfile().getId());
                int sc = getStatusColor(pl);

                if (dynamicRects) {
                    float w;
                    if (separateIconRect && showPlayerIcon) {
                        float iconRectSize = iconSize + padding * 2;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        float textW = padding + font.getWidth(p, fontSize) + pointWidth + font.getWidth(n, fontSize) + dw + padding;
                        w = iconRectSize + iconRectGap * finalScale + textW;
                    } else {
                        float iw = showPlayerIcon ? (iconSize + 6f * finalScale) : 0f;
                        float dw = showStatusDot ? (5f * finalScale + 6f * finalScale) : 0f;
                        w = padding + iw + (showPlayerIcon ? pointWidth : 0) +
                                font.getWidth(p, fontSize) + pointWidth + font.getWidth(n, fontSize) + dw + padding;
                    }
                    float ix = alignRight ? x + (animatedWidth - w) : x;
                    renderStaffDyn(matrix, ix, currentY, w, itemRowHeight * a, p, n, pc, sc, s.info, a, ha, fontSize, iconSize, finalScale, pointWidth, radius, alignRight);
                    currentY += (itemRowHeight + dynamicRectsGap * finalScale) * a;
                } else {
                    renderStaff(matrix, x, currentY, animatedWidth, itemRowHeight * a, p, n, pc, sc, s.info, a, ha, fontSize, iconSize, finalScale, pointWidth);
                    currentY += itemRowHeight * a;
                }
            }
        }

        if (enableOutline && !dynamicRects)
            drawOutline(matrix, x, y, animatedWidth, animatedHeight, finalScale, radius);

        drag.size.set(animatedWidth, animatedHeight);
        matrix.pop();
    }

    private void renderTitle(MatrixStack m, float x, float y, float w, float h, float fs, float is, float sc, int count, float gap, boolean dyn) {
        float p = 6f * sc, oy = 0.25f * sc;
        int hud = Theme.getInstance().clientColor();

        if (dyn) {
            // Align with staff layout: icon at p, text after icon+gap+point, count at right
            float cx = x + p;
            if (showTitleIcon) {
                Fonts.HUD.draw(m, "j", cx, y + (h - is) / 2f - oy, hud, is);
                cx += is + 6f * sc;
            }
            
            // Add point spacing if player icons are shown (to align with staff text)
            if (showPlayerIcon) {
                cx += font.getWidth(POINT, fs);
            }
            
            font.draw(m, "Staff", cx, y + (h - fs) / 2f - oy, -1, fs);
            
            // Count aligned with status dot position (right side)
            if (showCount) {
                float cfs = 6f * sc;
                String ct = String.valueOf(count);
                float cw = font.getWidth(ct, cfs);
                float crx = x + w - cw - p;
                font.draw(m, ct, crx, y + (h - cfs) / 2f - 0.45f + sc, hud, cfs);
            }
        } else {
            float cx = x + p;
            if (showTitleIcon) {
                Fonts.HUD.draw(m, "j", cx, y + (h - is) / 2f - oy, hud, is);
                cx += is + 6f * sc;
            }
            font.draw(m, "Staff", cx + 7.5f * sc, y + (h - fs) / 2f - oy, -1, fs);
            if (showCount) {
                float cfs = 6f * sc;
                String ct = String.valueOf(count);
                float cw = font.getWidth(ct, cfs), cx2 = x + w - p - cw - 4f * sc;
                float ch = cfs + 4f * sc, cr = 3f * sc;
                font.draw(m, ct, cx2 + 1.5f * sc, y + (h - cfs) / 2f - 1 + sc, hud, cfs);
            }
        }
    }

    private void renderStaffDyn(MatrixStack m, float x, float y, float w, float h, String pre, String nm, int pc, int sc,
                                NetworkPlayerInfo inf, float a, float ha, float fs, float is, float fsc, float pw, float r, boolean ar) {
        float p = 6f * fsc, sa = ease3(a), ta = ease4(a);

        if (separateIconRect && showPlayerIcon) {
            // Render icon in separate rect
            float iconRectSize = is + p * 2;
            float iconX = ar ? x : x;
            float textX = ar ? x + iconRectSize + iconRectGap * fsc : x + iconRectSize + iconRectGap * fsc;
            float textW = w - iconRectSize - iconRectGap * fsc;

            // Icon rect
            RenderUtil.CustomStyled.draw(m, iconX, y, iconRectSize, h, Round.of(r), blurEnabled, blurStrength);
            if (enableOutline) drawOutline(m, iconX, y, iconRectSize, h, fsc, r);

            m.push();
            m.translate(ar ? (sa - 1) * 10f * fsc : (1 - sa) * 10f * fsc, 0, 0);
            m.translate(iconX, y + h / 2f, 0);
            m.scale(1f, 0.3f + sa * 0.7f, 1f);
            m.translate(-iconX, -(y + h / 2f), 0);

            Fonts.icons.draw(m, "W", iconX + p + 0.7f, y + (h / a - is) / 2f, ((int) (255 * ta * ha) << 24) | (Theme.getInstance().clientColor() & 0x00FFFFFF), is);

            m.pop();

            // Text rect
            RenderUtil.CustomStyled.draw(m, textX, y, textW, h, Round.of(r), blurEnabled, blurStrength);
            if (enableOutline) drawOutline(m, textX, y, textW, h, fsc, r);

            m.push();
            m.translate(ar ? (sa - 1) * 10f * fsc : (1 - sa) * 10f * fsc, 0, 0);
            m.translate(textX, y + h / 2f, 0);
            m.scale(1f, 0.3f + sa * 0.7f, 1f);
            m.translate(-textX, -(y + h / 2f), 0);

            float cx = textX + p;
            float ty = y + (h / a - fs) / 2f;
            font.draw(m, pre, cx, ty, ((int) (255 * ta) << 24) | (pc & 0x00FFFFFF), fs);
            cx += font.getWidth(pre, fs);
            cx += pw;
            font.draw(m, nm, cx, ty, ColorUtil.getColor(255, 255, 255, (int) (235 * ta)), fs);

            if (showStatusDot) {
                float ds = 5f * fsc;
                RenderUtil.Rounded.smooth(m, textX + textW - p - ds, y + (h / a - ds) / 2f, ds, ds, ((int) (255 * ta) << 24) | (sc & 0x00FFFFFF), Round.of(ds / 2f));
            }

            m.pop();
        } else {
            // Original rendering (icon and text in same rect)
            RenderUtil.CustomStyled.draw(m, x, y, w, h, Round.of(r), blurEnabled, blurStrength);
            if (enableOutline) drawOutline(m, x, y, w, h, fsc, r);

            m.push();
            m.translate(ar ? (sa - 1) * 10f * fsc : (1 - sa) * 10f * fsc, 0, 0);
            m.translate(x, y + h / 2f, 0);
            m.scale(1f, 0.3f + sa * 0.7f, 1f);
            m.translate(-x, -(y + h / 2f), 0);

            float cx = x + p;
            if (showPlayerIcon) {
                Fonts.icons.draw(m, "W", cx + 0.7f, y + (h / a - is) / 2f, ((int) (255 * ta * ha) << 24) | (Theme.getInstance().clientColor() & 0x00FFFFFF), is);
                cx += is + 6f * fsc;
                font.draw(m, POINT, cx, y + (h / a - fs) / 2f, ColorUtil.getColor(DOT_COLOR >> 16 & 0xFF, DOT_COLOR >> 8 & 0xFF, DOT_COLOR & 0xFF, (int) (((DOT_COLOR >> 24 & 0xFF) * ta))), fs);
                cx += pw;
            }

            float ty = y + (h / a - fs) / 2f;
            font.draw(m, pre, cx, ty, ((int) (255 * ta) << 24) | (pc & 0x00FFFFFF), fs);
            cx += font.getWidth(pre, fs);
            cx += pw;
            font.draw(m, nm, cx, ty, ColorUtil.getColor(255, 255, 255, (int) (235 * ta)), fs);

            if (showStatusDot) {
                float ds = 5f * fsc;
                RenderUtil.Rounded.smooth(m, x + w - p - ds, y + (h / a - ds) / 2f, ds, ds, ((int) (255 * ta) << 24) | (sc & 0x00FFFFFF), Round.of(ds / 2f));
            }
            m.pop();
        }
    }

    private void renderStaff(MatrixStack m, float x, float y, float w, float h, String pre, String nm, int pc, int sc,
                             NetworkPlayerInfo inf, float a, float ha, float fs, float is, float fsc, float pw) {
        float p = 6f * fsc, sa = ease3(a), ta = ease4(a);

        m.push();
        m.translate((1 - sa) * 10f * fsc, 0, 0);
        m.translate(x, y + h / 2f, 0);
        m.scale(1f, 0.3f + sa * 0.7f, 1f);
        m.translate(-x, -(y + h / 2f), 0);

        float cx = x + p;
        if (showPlayerIcon) {
            Fonts.icons.draw(m, "W", cx + 0.7f, y + (h / a - is) / 2f, ((int) (255 * ta * ha) << 24) | (Theme.getInstance().clientColor() & 0x00FFFFFF), is);
            cx += is + 6f * fsc;
            font.draw(m, POINT, cx - 2.5f, y + (h / a - fs) / 2f, ColorUtil.getColor(DOT_COLOR >> 16 & 0xFF, DOT_COLOR >> 8 & 0xFF, DOT_COLOR & 0xFF, (int) (((DOT_COLOR >> 24 & 0xFF) * ta))), fs);
            cx += pw;
        }

        float ty = y + (h / a - fs) / 2f;
        font.draw(m, pre, cx, ty, ((int) (255 * ta) << 24) | (pc & 0x00FFFFFF), fs);
        cx += font.getWidth(pre, fs);
        cx += pw;
        font.draw(m, nm, cx, ty, ColorUtil.getColor(255, 255, 255, (int) (235 * ta)), fs);

        if (showStatusDot) {
            float ds = 5f * fsc;
            RenderUtil.Rounded.smooth(m, x + w - p - ds, y + (h / a - ds) / 2f, ds, ds, ((int) (255 * ta) << 24) | (sc & 0x00FFFFFF), Round.of(ds / 2f));
        }
        m.pop();
    }

    private void drawOutline(MatrixStack m, float x, float y, float w, float h, float sc, float r) {
        int oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), Theme.getInstance().clientColor(), 0.5f);
        float hw = outlineWidth * sc / 2f;
        RenderUtil.Rounded.roundedOutline(m, x - hw, y - hw, w + outlineWidth * sc, h + outlineWidth * sc, outlineWidth * sc, oc, Round.of(r));
    }

    private int getPrefixColor(ScorePlayerTeam t) {
        if (t == null) return -1;
        String p = t.getPrefix() != null ? t.getPrefix().getString() : "";
        for (int i = 0; i < p.length() - 1; i++) {
            if (p.charAt(i) == '§') {
                net.minecraft.util.text.TextFormatting f = net.minecraft.util.text.TextFormatting.fromFormattingCode(p.charAt(i + 1));
                if (f != null && f.getColor() != null) return f.getColor();
            }
        }
        net.minecraft.util.text.TextFormatting f = t.getColor();
        return f != null && f.getColor() != null ? f.getColor() : -1;
    }

    private int getStatusColor(AbstractClientPlayerEntity p) {
        if (p == null || p.isInvisible()) return Color.RED.getRGB();
        return mc.player != null && mc.player.getDistance(p) < 16 ? new Color(0, 180, 0).getRGB() : Color.YELLOW.getRGB();
    }

    private float ease3(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private float ease4(float t) {
        return 1 - (float) Math.pow(1 - t, 4);
    }
}