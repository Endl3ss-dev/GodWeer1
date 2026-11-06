package dev.zprestige.sourceview;

public class S0058 extends S0046 {
   public static boolean a;
   public double b;
   public double c;
   public double d;

   public S0058(double var1, double var3, double var5)  throws Throwable {
try {

      this.b = var1;
      this.c = var3;
      this.d = var5;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,1);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   public final void b$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      this.b = var2;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public final double b$return$e4b0898a(Object[] var1) {
try {

      return this.c;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public final double c$return$e4b0898a(Object[] var1) {
try {

      return this.d;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public final void c$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      this.c = var2;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public final void d$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      this.d = var2;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public final double a$return$e4b0898a(Object[] var1) {
try {

      return this.b;
   
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



private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.pc=1;return;}
case 1:{f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0058.class,"a",(dev.zprestige.sourceview.FlowFrame.i(f.pop()) != 0));f.pc=4;return;}
case 4:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
