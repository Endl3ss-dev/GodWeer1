/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.FieldInsnNode
 *  org.objectweb.asm.tree.FieldNode
 *  org.objectweb.asm.tree.InsnNode
 *  org.objectweb.asm.tree.MethodInsnNode
 *  org.objectweb.asm.tree.MethodNode
 *  org.objectweb.asm.tree.TypeInsnNode
 *  org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
 *  org.spongepowered.asm.mixin.extensibility.IMixinInfo
 */
package minced.core;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinConfigPlugin
implements IMixinConfigPlugin {
    static String field12142 = "getNarrator";
    static String field12143 = "EMPTY";
    static String field12144 = "field12144";
    static String field12145 = "field12145";
    static String field12146 = "field12146";
    static String field12147;
    static String field12148;
    static String field12149;
    static String field12150;

    public String getRefMapperConfig() {
        return null;
    }

    public void postApply(String param1, ClassNode param2, String param3, IMixinInfo param4) {
    }

    public boolean shouldApplyMixin(String param1, String param2) {
        return true;
    }

    public void acceptTargets(Set param1, Set param2) {
    }

    public void preApply(String param1, ClassNode param2, String param3, IMixinInfo param4) {
        for (MethodNode v6 : param2.methods) {
            if (!field12142.equals(v6.name)) continue;
            v6.instructions.insert((AbstractInsnNode)new InsnNode(176));
            for (FieldNode v8 : param2.fields) {
                if (!field12143.equals(v8.name)) continue;
                v6.instructions.insert((AbstractInsnNode)new FieldInsnNode(178, field12144, field12145, field12146));
                return;
            }
            v6.instructions.insert((AbstractInsnNode)new MethodInsnNode(183, field12147, field12148, field12149, false));
            v6.instructions.insert((AbstractInsnNode)new InsnNode(89));
            v6.instructions.insert((AbstractInsnNode)new TypeInsnNode(187, field12150));
            return;
        }
    }

    public List getMixins() {
        return null;
    }

    public void onLoad(String param1) {
    }

    static {
        field12147 = "com/mojang/text2speech/NarratorDummy";
        field12148 = "<init>";
        field12149 = "()V";
        field12150 = "com/mojang/text2speech/NarratorDummy";
    }
}



