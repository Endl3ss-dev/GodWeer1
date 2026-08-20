package fun.volon.mixin;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import fun.volon.client.modules.impl.render.SeeInvisiblesRenderState;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements SeeInvisiblesRenderState {

    @Unique
    private boolean Volon$seeInvisiblesTarget;

    @Override
    public boolean Volon$isSeeInvisiblesTarget() {
        return Volon$seeInvisiblesTarget;
    }

    @Override
    public void Volon$setSeeInvisiblesTarget(boolean value) {
        Volon$seeInvisiblesTarget = value;
    }
}
