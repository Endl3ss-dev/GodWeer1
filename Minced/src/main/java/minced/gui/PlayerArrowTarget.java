/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.impl.client.screen.ScreenExtensions
 *  net.fabricmc.fabric.mixin.screen.ScreenAccessor
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.ParentElement
 *  net.minecraft.client.gui.navigation.Navigable
 *  net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookProvider
 *  net.minecraft.entity.player.PlayerEntity
 */
package minced.gui;

import minced.core.FadeAnimation;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.Navigable;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerArrowTarget
implements Navigable,
Element,
ParentElement,
Drawable {
    @Override
    public boolean isDragging() { return false; }

    @Override
    public void setDragging(boolean p0) { }

    @Override
    public net.minecraft.client.gui.Element getFocused() { return null; }

    @Override
    public void setFocused(net.minecraft.client.gui.Element p0) { }

    @Override
    public java.util.List<? extends net.minecraft.client.gui.Element> children() { return java.util.List.of(); }

    @Override
    public void render(net.minecraft.client.gui.DrawContext p0, int p1, int p2, float p3) { }



    public minced.core.FadeAnimation fadeAnimation;
    public net.minecraft.entity.player.PlayerEntity player;

    public FadeAnimation getFadeAnimation() {
        return this.fadeAnimation;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    public PlayerArrowTarget(PlayerEntity param1, FadeAnimation param2) {
        this.player = param1;
        this.fadeAnimation = param2;
    }
}

