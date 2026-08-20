package org.privates.client.managers.module.impl.player;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector2f;
import org.privates.client.api.annotations.Category3;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.math.MathUtility;

import java.util.UUID;

@Category3
@ModuleInfo(name = "FakePlayer", category = Category.PLAYER, description = "Тайлер дёрден")
public class FakePlayer extends Module {

    private RemoteClientPlayerEntity fakePlayer;

    public FakePlayer() {
        setEnabled(false, true);
    }

    @Subscribe
    public void onMotion(MotionEvent e) {
        if (fakePlayer == null) return;
        Vector2f rot = MathUtility.rotationToEntity(mc.player);
        fakePlayer.rotationYaw = rot.x;
        fakePlayer.rotationPitch = rot.y;
    }

    @Subscribe
    private void onUpdate(UpdateEvent e) {

    }

    private void spawnFakePlayer() {
        UUID var1 = UUID.nameUUIDFromBytes("1337".getBytes());
        fakePlayer = new RemoteClientPlayerEntity(FakePlayer.mc.world, new GameProfile(var1, "FakePlayer"));
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        fakePlayer.renderYawOffset = mc.player.renderYawOffset;
        fakePlayer.rotationPitchHead = mc.player.rotationPitchHead;
        fakePlayer.container = mc.player.container;
        fakePlayer.inventory = mc.player.inventory;
        fakePlayer.setHealth(1337.0f);
        mc.world.addEntity(1337, fakePlayer);
    }

    @Override
    public void onDisable() {
        removeFakePlayer();
        super.onDisable();
    }

    @Override
    protected void onEnable() {
        spawnFakePlayer();
    }


    protected float[] rotations(PlayerEntity player) {
        return new float[0];
    }

    private void removeFakePlayer() {
        FakePlayer.mc.world.removeEntityFromWorld(1337);
    }
}