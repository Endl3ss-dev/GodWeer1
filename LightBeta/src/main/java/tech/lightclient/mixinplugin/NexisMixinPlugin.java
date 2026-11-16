package tech.lightclient.mixinplugin;


import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class NexisMixinPlugin implements IMixinConfigPlugin {

    private static final String BARITONE_LOOK_BEHAVIOR_TARGET = "baritone.behavior.LookBehavior";
    private static final boolean HAS_BARITONE_LOOK_BEHAVIOR = isClassPresent(BARITONE_LOOK_BEHAVIOR_TARGET);

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (BARITONE_LOOK_BEHAVIOR_TARGET.equals(targetClassName)) {
            return HAS_BARITONE_LOOK_BEHAVIOR;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isClassPresent(String className) {
        String resourcePath = className.replace('.', '/') + ".class";
        return NexisMixinPlugin.class.getClassLoader().getResource(resourcePath) != null;
    }
}
