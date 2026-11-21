package dev.zprestige.sourceview;

import java.util.function.Consumer;
import java.util.function.IntSupplier;

public final class S0392 {
   public static S0475 a;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,3);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$0$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static S0467 b$return$99ec2543(Object[] var0) {
try {

      int var1 = (Integer)(Object)(var0[0]);
      boolean var2 = (Boolean)(Object)(var0[1]);
      boolean var3 = (Boolean)(Object)(var0[2]);
      return new S0467(var1, var2, var3);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0465 b$return$99ec2524(Object[] var0) {
try {

      boolean var1 = (Boolean)(Object)(var0[0]);
      return new S0465(var1, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0391 b$return$99ec223c(Object[] var0) {
try {

      Consumer var1 = (Consumer)(Object)(var0[0]);
      return new S0394(var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0461 a$return$99ec24e6(Object[] var0) {
try {

      boolean var3 = (Boolean)(Object)(var0[0]);
      boolean var6 = (Boolean)(Object)(var0[1]);
      int var1 = (Integer)(Object)(var0[2]);
      int var4 = (Integer)(Object)(var0[3]);
      int var5 = (Integer)(Object)(var0[4]);
      int var2 = (Integer)(Object)(var0[5]);
      return new S0461(var3, var6, var1, var4, var5, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0463 a$return$99ec2505(Object[] var0) {
try {

      int var2 = (Integer)(Object)(var0[0]);
      int var1 = (Integer)(Object)(var0[1]);
      return new S0463(var2, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0473 a$return$99ec25a0(Object[] var0) {
try {

      boolean var1 = (Boolean)(Object)(var0[0]);
      boolean var2 = (Boolean)(Object)(var0[1]);
      return new S0473(var1, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0469 a$return$99ec2562(Object[] var0) {
try {

      boolean var1 = (Boolean)(Object)(var0[0]);
      boolean var2 = (Boolean)(Object)(var0[1]);
      int var5 = (Integer)(Object)(var0[2]);
      int var3 = (Integer)(Object)(var0[3]);
      int var6 = (Integer)(Object)(var0[4]);
      int var4 = (Integer)(Object)(var0[5]);
      return new S0469(var1, var2, var5, var3, var6, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0477 a$return$99ec25de(Object[] var0) {
try {

      Consumer var1 = (Consumer)(Object)(var0[0]);
      return new S0477(var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0391 a$return$99ec223c(Object[] var0) {
try {

      Consumer var1 = (Consumer)(Object)(var0[0]);
      return new S0393(var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0465 a$return$99ec2524(Object[] var0) {
try {

      boolean var1 = (Boolean)(Object)(var0[0]);
      boolean var2 = (Boolean)(Object)(var0[1]);
      return new S0465(var1, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0467 a$return$99ec2543(Object[] var0) {
try {

      int var1 = (Integer)(Object)(var0[0]);
      boolean var2 = (Boolean)(Object)(var0[1]);
      return new S0467(var1, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0471 a$return$99ec2581(Object[] var0) {
try {

      S0388 var1 = (S0388)(Object)(var0[0]);
      return new S0471(var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0475 a$return$99ec25bf(Object[] var0) {
try {

      int var1 = (Integer)(Object)(var0[0]);
      return new S0475(var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   static {
      try {
         __initializeClass();
      } catch (Throwable var1) {
         if (!(var1 instanceof Error)) {
            throw new ExceptionInInitializerError(var1);
         } else {
            throw (Error)(Object)(var1);
         }
      }
   }

   public static native IntSupplier __indy$0() throws Throwable;

private static void __flow$0$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.pc=1;return;}
case 1:{f.pc=2;return;}
case 2:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0475.class),1);f.pc=3;return;}
case 3:{f.rearrange(89);f.pc=4;return;}
case 4:{f.push(dev.zprestige.sourceview.S0392.__indy$0(),1);f.pc=5;return;}
case 5:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.util.function.IntSupplier.class},new Object[]{((java.util.function.IntSupplier)(Object)(a0))});f.pc=6;return;}
case 6:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0392.class,"a",((dev.zprestige.sourceview.S0475)(Object)(f.pop())));f.pc=7;return;}
case 7:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
