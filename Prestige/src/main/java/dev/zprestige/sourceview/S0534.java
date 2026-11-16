package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_10017;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_897;

@CTransformer(class_897.class)
public abstract class S0534<T extends class_1297, S extends class_10017> {
   @CInject(method = "getAndUpdateRenderState", target = @CTarget("RETURN"))
   public void b(T entity, float tickDelta) {
try {

      if (!S0211.w && entity == class_310.method_1551().field_1724) {
         if (S0211.b != null && S0211.p != null && S0190.aX(S0211.p) && S0190.aY(S0211.b, S0062.class)) {
            S0190.ba(S0190.aZ(entity, S0599.POST));
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "getSkyLight", target = @CTarget("RETURN"), cancellable = true)
   public void c(T entity, class_2338 pos, InjectionCallback cir) {
try {

      if (!S0211.w) {
         if (S0190.bb(S0211.b, S0033.class)) {
            S0033 event = S0190.bc();
            if (S0190.bd(event)) {
               cir.setReturnValue(15);
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "updateRenderState", target = @CTarget("RETURN"))
   public void d(T entity, S state, float tickDelta) {
try {

      if (!S0211.w && S0211.b != null && state.field_53337 != null) {
         boolean hasLabelText = S0190.be(S0211.b, S0132.class);
         boolean hasHealthIndicators = S0190.bf(S0211.b, S0144.class);
         boolean hasRenderLabel = S0190.bg(S0211.b, S0147.class);
         if (hasLabelText || hasHealthIndicators || hasRenderLabel) {
            class_2561 originalDisplayName = state.field_53337;
            class_2561 displayName = originalDisplayName;
            if (entity instanceof class_1657 player && entity.method_5805() && (hasLabelText || hasHealthIndicators)) {
               S0132 labelTextEvent = S0190.bh(entity, displayName);
               boolean labelTextCancelled = S0190.bi(labelTextEvent);
               displayName = S0190.bj(labelTextEvent);
               if (!labelTextCancelled) {
                  S0144 event = S0190.bk(player, "");
                  if (S0190.bl(event)) {
                     displayName = class_2561.method_30163(__indy$0(displayName.getString(), S0190.bm(event)));
                  }
               }
            }

            if (hasRenderLabel) {
               S0147 event = S0190.bn(originalDisplayName, displayName, entity);
               if (S0190.bo(event)) {
                  state.field_53337 = null;
                  return;
               }
            }

            state.field_53337 = displayName != null && !displayName.getString().isEmpty() ? displayName : null;
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "getAndUpdateRenderState", target = @CTarget("HEAD"))
   public void a(T entity, float tickDelta) {
try {

      if (!S0211.w && entity == class_310.method_1551().field_1724) {
         if (S0211.b != null && S0211.o != null && S0190.aT(S0211.o) != null && S0190.aU(S0211.b, S0062.class)) {
            S0190.aW(S0190.aV(entity, S0599.PRE));
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static native String __indy$0(String var0, String var1) throws Throwable;
}
