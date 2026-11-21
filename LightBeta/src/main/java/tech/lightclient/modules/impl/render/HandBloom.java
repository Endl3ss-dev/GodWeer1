package tech.lightclient.modules.impl.render;

import tech.lightclient.client.utils.render3d.HandsRenderer;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;

@FunctionAdd(name = "HandBloom", alias = "Hand Bloom", category = Category.Render, description = "Warm hand bloom with trail")
public class HandBloom extends Function {
    @Override
    public void onEnable() {
        HandsRenderer.clearTrail();
    }

    @Override
    public void onDisable() {
        HandsRenderer.clearTrail();
    }
}
