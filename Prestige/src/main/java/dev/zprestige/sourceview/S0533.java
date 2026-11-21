package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;

@CTransformer(class_1297.class)
public abstract class S0533 {
   @CInject(method = "getBoundingBox", target = @CTarget("RETURN"), cancellable = true)
   public void b(InjectionCallback cir) {
try {

      if (!S0211.w) {
         class_1297 entity = (class_1297)(Object)(this);
         if (entity != null) {
            if (S0190.aH(S0211.b, S0056.class)) {
               class_238 box = (class_238)(Object)(cir.getReturnValue());
               S0056 event = S0190.aI(entity, box);
               if (S0190.aJ(event)) {
                  cir.setReturnValue(S0190.aK(event));
               }
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "setVelocityClient", target = @CTarget("HEAD"), cancellable = true)
   public void c(class_243 velocity, InjectionCallback ci) {
try {

      if (!S0211.w) {
         class_1297 entity = (class_1297)(Object)(this);
         if (entity == class_310.method_1551().field_1724) {
            S0114 event = S0190.aL(velocity.field_1352, velocity.field_1351, velocity.field_1350);
            if (S0190.aM(event)) {
               entity.method_18800(S0190.aN(event), S0190.aO(event), S0190.aP(event));
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "getRotationVec", target = @CTarget("HEAD"), cancellable = true)
   public void d(float tickDelta, InjectionCallback cir) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(5,4);
f.locals[0]=this;
f.locals[1]=tickDelta;
f.locals[2]=cir;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$3$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   @CInject(method = "updateVelocity", target = @CTarget("HEAD"), cancellable = true)
   public void a(float speed, class_243 movementInput, InjectionCallback ci) {
try {

      if (!S0211.w) {
         class_1297 entity = (class_1297)(Object)(this);
         class_310 mc = class_310.method_1551();
         if (entity == mc.field_1724 && S0211.o != null && S0190.aA(S0211.o) != null) {
            S0136 event = S0190.aB(entity.method_36454(), movementInput);
            if (S0190.aC(event)) {
               float yaw = S0190.aD(event);
               class_243 input = S0190.aE(event) != null ? S0190.aF(event) : movementInput;
               double d = input.method_1027();
               class_243 vec3d;
               if (d < 1.0E-7) {
                  vec3d = class_243.field_1353;
               } else {
                  class_243 scaled = (d > 1.0 ? input.method_1029() : input).method_1021(speed);
                  float f = class_3532.method_15374(yaw * (float) (Math.PI / 180.0));
                  float g = class_3532.method_15362(yaw * (float) (Math.PI / 180.0));
                  vec3d = S0190.aG(scaled.field_1352 * g - scaled.field_1350 * f, scaled.field_1351, scaled.field_1350 * g + scaled.field_1352 * f);
               }

               entity.method_18799(entity.method_18798().method_1019(vec3d));
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}



private void __flow$3$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.pc=1;return;}
case 1:{f.pc=2;return;}
case 2:{f.push(net.minecraft.class_310.method_1551(),1);f.pc=3;return;}
case 3:{f.locals[3]=f.pop();f.pc=4;return;}
case 4:{f.pc=5;return;}
case 5:{f.pc=6;return;}
case 6:{f.push(f.locals[3],1);f.pc=7;return;}
case 7:{if(f.pop()==null){f.pc=19;return;}f.pc=8;return;}
case 8:{f.push(f.locals[3],1);f.pc=9;return;}
case 9:{f.push(((net.minecraft.class_310)(Object)f.pop()).field_1724,1);f.pc=10;return;}
case 10:{if(f.pop()==null){f.pc=19;return;}f.pc=11;return;}
case 11:{f.push(f.locals[0],1);f.pc=12;return;}
case 12:{f.push(f.locals[3],1);f.pc=13;return;}
case 13:{f.push(((net.minecraft.class_310)(Object)f.pop()).field_1724,1);f.pc=14;return;}
case 14:{Object b=f.pop(),a=f.pop();if(a!=b){f.pc=19;return;}f.pc=15;return;}
case 15:{f.push(dev.zprestige.sourceview.S0211.w,1);f.pc=16;return;}
case 16:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=19;return;}f.pc=17;return;}
case 17:{f.push(dev.zprestige.sourceview.S0211.o,1);f.pc=18;return;}
case 18:{if(f.pop()!=null){f.pc=23;return;}f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.done=true;return;}
case 23:{f.pc=24;return;}
case 24:{f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.push(dev.zprestige.sourceview.S0211.o,1);f.pc=27;return;}
case 27:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0190.aQ(((dev.zprestige.sourceview.S0241)(Object)(a0))),1);f.pc=28;return;}
case 28:{f.locals[4]=f.pop();f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.push(f.locals[4],1);f.pc=32;return;}
case 32:{if(f.pop()==null){f.pc=44;return;}f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.push(f.locals[2],1);f.pc=36;return;}
case 36:{f.push(f.locals[3],1);f.pc=37;return;}
case 37:{f.push(((net.minecraft.class_310)(Object)f.pop()).field_1724,1);f.pc=38;return;}
case 38:{f.push(f.locals[4],1);f.pc=39;return;}
case 39:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0190.aR(((dev.zprestige.sourceview.S0240)(Object)(a0))),1);f.pc=40;return;}
case 40:{f.push(f.locals[4],1);f.pc=41;return;}
case 41:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0190.aS(((dev.zprestige.sourceview.S0240)(Object)(a0))),1);f.pc=42;return;}
case 42:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((net.minecraft.class_746)(Object)receiver).method_5631(dev.zprestige.sourceview.FlowFrame.f(a0),dev.zprestige.sourceview.FlowFrame.f(a1)),1);f.pc=43;return;}
case 43:{Object a0=f.pop();Object receiver=f.pop();((net.lenni0451.classtransform.InjectionCallback)(Object)receiver).setReturnValue(((java.lang.Object)(Object)(a0)));f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.done=true;return;}
case 48:{f.pc=49;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
