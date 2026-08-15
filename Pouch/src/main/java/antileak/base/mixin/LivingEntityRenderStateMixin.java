package antileak.base.mixin;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import antileak.base.client.modules.impl.render.SeeInvisiblesRenderState;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements SeeInvisiblesRenderState {

    @Unique
    private boolean punch$seeInvisiblesTarget;

    @Override
    public boolean punch$isSeeInvisiblesTarget() {
        return punch$seeInvisiblesTarget;
    }

    @Override
    public void punch$setSeeInvisiblesTarget(boolean value) {
        punch$seeInvisiblesTarget = value;
    }
}
