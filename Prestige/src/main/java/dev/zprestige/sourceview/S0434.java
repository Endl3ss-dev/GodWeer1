package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0225;
import dev.zprestige.sourceview.S0432;
import dev.zprestige.sourceview.S0549;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_11909;
import net.minecraft.class_332;

public class S0434 extends S0432 implements S0225 {
   public boolean a = false;
   public boolean b = false;
   public static long c;
   public static Object[] d;
   public static String[] e;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static int e(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (e[var4] != null) {
         return var4;
      }

      Object var5 = d[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 55;
         case 1 -> 47;
         case 2 -> 1;
         case 3 -> 45;
         case 4 -> 6;
         case 5 -> 39;
         case 6 -> 48;
         case 7 -> 16;
         case 8 -> 28;
         case 9 -> 7;
         case 10 -> 52;
         case 11 -> 11;
         case 12 -> 14;
         case 13 -> 37;
         case 14 -> 22;
         case 15 -> 26;
         case 16 -> 31;
         case 17 -> 15;
         case 18 -> 20;
         case 19 -> 54;
         case 20 -> 18;
         case 21 -> 35;
         case 22 -> 44;
         case 23 -> 23;
         case 24 -> 17;
         case 25 -> 57;
         case 26 -> 4;
         case 27 -> 9;
         case 28 -> 25;
         case 29 -> 32;
         case 30 -> 61;
         case 31 -> 40;
         case 32 -> 12;
         case 33 -> 34;
         case 34 -> 49;
         case 35 -> 56;
         case 36 -> 3;
         case 37 -> 30;
         case 38 -> 0;
         case 39 -> 29;
         case 40 -> 50;
         case 41 -> 21;
         case 42 -> 43;
         case 43 -> 2;
         case 44 -> 41;
         case 45 -> 10;
         case 46 -> 58;
         case 47 -> 62;
         case 48 -> 59;
         case 49 -> 19;
         case 50 -> 5;
         case 51 -> 46;
         case 52 -> 38;
         case 53 -> 60;
         case 54 -> 53;
         case 55 -> 33;
         case 56 -> 24;
         case 57 -> 27;
         case 58 -> 63;
         case 59 -> 8;
         case 60 -> 42;
         case 61 -> 51;
         case 62 -> 36;
         default -> 13;
      };
      int[] var7 = new int[6];

      for (int var8 = 0; var8 < 6; var8++) {
         int var9 = 7 * (5 - var8);
         int var10 = (int)(var0 >>> var9 & 127L);
         var10 -= var6;
         if (var10 < 0) {
            var10 += 128;
         }

         var7[var8] = var10;
      }

      char[] var13 = ((String)(Object)(var5)).toCharArray();

      for (int var14 = 0; var14 < var13.length; var14++) {
         int var16 = var7[var14 % var7.length];
         if (var16 == 0) {
            break;
         }

         var13[var14] = (char)(var13[var14] ^ var16);
      }

      e[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'v' && var8 != 217 && var8 != 226 && var8 != 'a') {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 207) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'g') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = g(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'v') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 217) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 226) {
               var9 = var0.findStaticGetter(var12, var18, var14);
            } else {
               var9 = var0.findStaticSetter(var12, var18, var14);
            }
         }

         return MethodHandles.dropArguments(var9, var3.parameterCount() - 2, long.class, long.class);
      } catch (Exception var15) {
         StringBuilder var13 = new StringBuilder();
         var13.append(var15.getClass().getName())
            .append(" : ")
            .append(var10 != null ? var10.toString() : (var11 != null ? var11.toString() : " null "))
            .append(" : ")
            .append(var15.toString());
         throw new RuntimeException(var13.toString());
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = b(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$0(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field c(Class var0, String var1, Class var2) {
try {

      for (Field var6 : var0.getDeclaredFields()) {
         if (var6.getName().equals(var1) && var6.getType() == var2) {
            return var6;
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method c(Class var0, String var1, Class var2, int var3, Class[] var4) {
try {

      label33:
      for (Method var8 : var0.getDeclaredMethods()) {
         if (var8.getName().equals(var1) && var8.getReturnType() == var2) {
            Class[] var9 = var8.getParameterTypes();
            if (var9.length == var3) {
               for (int var10 = 0; var10 < var3; var10++) {
                  if (var9[var10] != var4[var10]) {
                     continue label33;
                  }
               }

               return var8;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method h(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = d[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = e[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      int var11 = -1;
      int var12 = var9;

      do {
         var11++;
         var12++;
      } while ((var12 = var6.indexOf(8, var12)) > -1);

      int var13;
      Class[] var14 = new Class[var13 = var11 - 1];
      Class var15 = null;
      var12 = var9 + 1;

      for (int var16 = 0; var16 < var11; var16++) {
         int var17 = var6.indexOf(8, var12);
         var15 = f(Long.parseLong(var6.substring(var12, var17), 36), 0L);
         if (var16 < var13) {
            var14[var16] = var15;
         }

         var12 = var17 + 1;
      }

      Class var23 = var8;

      while (true) {
         Method var26 = c(var23, var10, var15, var13, var14);
         if (var26 != null) {
            d[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = f(542118883601531L, 0L);
            break;
         }
      }

      var23 = var8;

      while (true) {
         Class[] var27;
         if ((var27 = var23.getInterfaces()) != null) {
            for (int var18 = 0; var18 < var27.length; var18++) {
               Method var19 = d(var27[var18], var10, var15, var13, var14);
               if (var19 != null) {
                  d[var4] = var19;
                  return var19;
               }
            }
         }

         if (var23.getName().equals("java.lang.Object")) {
            StringBuffer var28 = new StringBuffer();
            var28.append("NoSuchMethodException in ").append(var8.getName()).append(' ').append(var15.getName()).append(' ').append(var10).append('(');
            int var29 = 0;

            while (var29 < var13) {
               var28.append(var14[var29].getName());
               if (++var29 < var13) {
                  var28.append(", ");
               }
            }

            var28.append(')');
            throw new RuntimeException(var28.toString());
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = f(542118883601531L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class f(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = e(var0, var2);
      Object var6 = d[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(e[var4]);
            d[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(Class var0, String var1, Class var2, int var3, Class[] var4) {
try {

      Method var5 = c(var0, var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      }

      Class[] var6 = var0.getInterfaces();
      if (var6 != null) {
         for (int var7 = 0; var7 < var6.length; var7++) {
            var5 = d(var6[var7], var1, var2, var3, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field d(Class var0, String var1, Class var2) {
try {

      Field var3 = c(var0, var1, var2);
      if (var3 != null) {
         return var3;
      }

      Class[] var4 = var0.getInterfaces();
      if (var4 != null) {
         for (int var5 = 0; var5 < var4.length; var5++) {
            var3 = d(var4[var5], var1, var2);
            if (var3 != null) {
               return var3;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$1().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/g9" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
try {

      this.b = false;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      d[0] = "\tn?H\u00162\u001fn:\u0012\u0005%\b%9\u0014\t1\u0019b.\u0003B%T";
      d[1] = void.class;
      e[1] = "java/lang/Void";
      d[2] = "\u000b><xoU\u001d>9\"|B\nu:$pV\u001b2-3;D'";
      d[3] = "0*LM@\u0005E\nGBQJ8\u0012TEX\u0003P";
      d[4] = "[!OY\u00023M!J\u0003\u0011$ZjI\u0005\u001d0K-^\u0012V\r^0P\u0001\u001d";
      d[5] = "$z\u0015?C>/u\u0004p 3:x\u000b\u001b\u00151+k\u00177\u0002<";
      d[6] = boolean.class;
      e[6] = "java/lang/Boolean";
      d[7] = "s7e\u0007~\u001fx8tH\u001f\u0011s3p\u0012";
      d[8] = "4MG\u0002\u0003\u0015hUD\u0016`O\r\b_\u001c\u001a\u00157\u0002\b\u0017\u0002%";
      d[9] = "\u000fYi3&\rNF 8[\u00024\n40'\u0018ZO\"c&`";
      d[10] = "LB,>F\u0017\r]e5;\u001bw_|o\u0005\u0002\bBv<;";
      d[11] = "}IQ\u00006(qA\u0002oj:jM\f\u0003Xm+\u0011R^\u000f%wFU\u0017p8}\u0015k";
      d[12] = "N,Q\u001b5\u0001B$\u0002te~\u001e*\u0006\u000bc\u000f\u001c7\u0017\u0018\fEG%\u0014\u001b}GZ4\u0007t";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field g(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = d[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = e[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = f(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = c(var12, var10, var11);
         if (var13 != null) {
            d[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = d(var14[var15], var10, var11);
               if (var13 != null) {
                  d[var4] = var13;
                  return var13;
               }
            }
         }

         if (var12.getName().equals("java.lang.Object")) {
            StringBuffer var19 = new StringBuffer();
            var19.append("NoSuchFieldException in ").append(var8.getName()).append(' ').append(var11.getName()).append(' ').append(var10);
            throw new RuntimeException(var19.toString());
         }

         var12 = var12.getSuperclass();
         if (var12 == null) {
            var12 = f(542118883601531L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_48267() {
try {

   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_25420(class_332 var1, int var2, int var3, float var4) {
try {

      super.method_25420(var1, var2, var3, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_25394(class_332 param1, int param2, int param3, float param4) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(8,5);
f.locals[0]=this;
f.locals[1]=param1;
f.locals[2]=param2;
f.locals[3]=param3;
f.locals[4]=param4;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$18$0(f);break;
case 1:__flow$18$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=8&&f.pc<16&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=18;continue;}
if(f.pc>=14&&f.pc<31&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=33;continue;}
if(f.pc>=24&&f.pc<42&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=44;continue;}
if(f.pc>=39&&f.pc<60&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=60;continue;}
if(f.pc>=71&&f.pc<77&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=79;continue;}
if(f.pc>=75&&f.pc<89&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=91;continue;}
if(f.pc>=97&&f.pc<102&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=104;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public void method_25419() {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(4,5);
f.locals[0]=this;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$19$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=18&&f.pc<27&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=29;continue;}
if(f.pc>=22&&f.pc<41&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=43;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public boolean method_25421() {
try {

      return false;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public boolean method_25402(class_11909 var1, boolean var2) {
try {

      long var3 = c ^ 135669095272799L;
      __indy$19(this, -6893047353806655581L, var3);
      return super.method_25402(var1, var2);
   
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

   private static native Object __indy$0(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$1() throws Throwable;

   private static native String[] __indy$2(long var0, long var2) throws Throwable;

   private static native MatchException __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$4(long var0, long var2) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$12(long var0, long var2) throws Throwable;

   private static native void __indy$13(long var0, long var2) throws Throwable;

   private static native String[] __indy$14(long var0, long var2) throws Throwable;

   private static native boolean __indy$15(long var0, long var2) throws Throwable;

   private static native MatchException __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$17(long var0, long var2) throws Throwable;

   private static native MatchException __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$19(Object var0, long var1, long var3) throws Throwable;

public S0434()throws Throwable{super();}

private void __flow$18$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0434.c,2);f.pc=1;return;}
case 1:{f.push(81361623027718L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[5]=f.pop();f.pc=4;return;}
case 4:{f.push(2382135723402388292L,2);f.pc=5;return;}
case 5:{f.push(f.locals[5],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.locals[7]=f.pop();f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{f.push(((dev.zprestige.sourceview.S0434)(Object)f.pop()).a,1);f.pc=12;return;}
case 12:{f.push(f.locals[7],1);f.pc=13;return;}
case 13:{if(f.pop()!=null){f.pc=71;return;}f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=66;return;}f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.pc=24;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(2381887414609378464L,2);f.pc=21;return;}
case 21:{f.push(f.locals[5],2);f.pc=22;return;}
case 22:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=23;return;}
case 23:{throw (Throwable)f.pop();}
case 24:{f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.push(2382255397928686583L,2);f.pc=27;return;}
case 27:{f.push(f.locals[5],2);f.pc=28;return;}
case 28:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$4(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=29;return;}
case 29:{f.push(f.locals[7],1);f.pc=30;return;}
case 30:{if(f.pop()!=null){f.pc=71;return;}f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=39;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.push(2381887414609378464L,2);f.pc=36;return;}
case 36:{f.push(f.locals[5],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{throw (Throwable)f.pop();}
case 39:{f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=66;return;}f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.pc=50;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.push(2381887414609378464L,2);f.pc=47;return;}
case 47:{f.push(f.locals[5],2);f.pc=48;return;}
case 48:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=49;return;}
case 49:{throw (Throwable)f.pop();}
case 50:{f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{f.pc=53;return;}
case 53:{f.push(f.locals[0],1);f.pc=54;return;}
case 54:{f.push(2382346407499591418L,2);f.pc=55;return;}
case 55:{f.push(f.locals[5],2);f.pc=56;return;}
case 56:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0434.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.done=true;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.push(2381887414609378464L,2);f.pc=63;return;}
case 63:{f.push(f.locals[5],2);f.pc=64;return;}
case 64:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=65;return;}
case 65:{throw (Throwable)f.pop();}
case 66:{f.pc=67;return;}
case 67:{f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.push(f.locals[0],1);f.pc=70;return;}
case 70:{f.push(((dev.zprestige.sourceview.S0434)(Object)f.pop()).a,1);f.pc=71;return;}
case 71:{f.pc=72;return;}
case 72:{f.pc=73;return;}
case 73:{f.push(f.locals[7],1);f.pc=74;return;}
case 74:{if(f.pop()!=null){f.pc=97;return;}f.pc=75;return;}
case 75:{f.pc=76;return;}
case 76:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=130;return;}f.pc=77;return;}
case 77:{f.pc=78;return;}
case 78:{f.pc=85;return;}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.push(2381887414609378464L,2);f.pc=82;return;}
case 82:{f.push(f.locals[5],2);f.pc=83;return;}
case 83:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=84;return;}
case 84:{throw (Throwable)f.pop();}
case 85:{f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.push(f.locals[0],1);f.pc=88;return;}
case 88:{f.push(((dev.zprestige.sourceview.S0434)(Object)f.pop()).b,1);f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=97;return;}
case 91:{f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.push(2381887414609378464L,2);f.pc=94;return;}
case 94:{f.push(f.locals[5],2);f.pc=95;return;}
case 95:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=96;return;}
case 96:{throw (Throwable)f.pop();}
case 97:{f.pc=98;return;}
case 98:{f.pc=99;return;}
case 99:{f.push(f.locals[7],1);f.pc=100;return;}
case 100:{if(f.pop()!=null){f.pc=116;return;}f.pc=101;return;}
case 101:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=130;return;}f.pc=102;return;}
case 102:{f.pc=103;return;}
case 103:{f.pc=110;return;}
case 104:{f.pc=105;return;}
case 105:{f.pc=106;return;}
case 106:{f.push(2381887414609378464L,2);f.pc=107;return;}
case 107:{f.push(f.locals[5],2);f.pc=108;return;}
case 108:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=109;return;}
case 109:{throw (Throwable)f.pop();}
case 110:{f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.pc=113;return;}
case 113:{f.push(2382255397928686583L,2);f.pc=114;return;}
case 114:{f.push(f.locals[5],2);f.pc=115;return;}
case 115:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$12(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=116;return;}
case 116:{f.pc=117;return;}
case 117:{f.pc=118;return;}
case 118:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=124;return;}f.pc=119;return;}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$18$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.push(2382323081948776706L,2);f.pc=122;return;}
case 122:{f.push(f.locals[5],2);f.pc=123;return;}
case 123:{Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0434.__indy$13(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1));f.pc=124;return;}
case 124:{f.pc=125;return;}
case 125:{f.pc=126;return;}
case 126:{f.pc=127;return;}
case 127:{f.push(f.locals[0],1);f.pc=128;return;}
case 128:{f.push(1,1);f.pc=129;return;}
case 129:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0434.class,receiver,"a",(dev.zprestige.sourceview.FlowFrame.i(value) != 0));f.pc=130;return;}
case 130:{f.pc=131;return;}
case 131:{f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$19$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0434.c,2);f.pc=1;return;}
case 1:{f.push(57183625697176L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[1]=f.pop();f.pc=4;return;}
case 4:{f.push(-1832553639273880358L,2);f.pc=5;return;}
case 5:{f.push(f.locals[1],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$14(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.push(f.locals[0],1);f.pc=10;return;}
case 10:{f.push(1,1);f.pc=11;return;}
case 11:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0434.class,receiver,"b",(dev.zprestige.sourceview.FlowFrame.i(value) != 0));f.pc=12;return;}
case 12:{f.pc=13;return;}
case 13:{f.pc=14;return;}
case 14:{f.push(f.locals[0],1);f.pc=15;return;}
case 15:{f.push(0,1);f.pc=16;return;}
case 16:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0434.class,receiver,"a",(dev.zprestige.sourceview.FlowFrame.i(value) != 0));f.pc=17;return;}
case 17:{f.locals[3]=f.pop();f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(f.locals[3],1);f.pc=21;return;}
case 21:{if(f.pop()!=null){f.pc=54;return;}f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.push(-1832677711691508631L,2);f.pc=24;return;}
case 24:{f.push(f.locals[1],2);f.pc=25;return;}
case 25:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$15(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=26;return;}
case 26:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=49;return;}f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.pc=35;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.push(-1832872660475692226L,2);f.pc=32;return;}
case 32:{f.push(f.locals[1],2);f.pc=33;return;}
case 33:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=34;return;}
case 34:{throw (Throwable)f.pop();}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(-1832459522420732260L,2);f.pc=39;return;}
case 39:{f.push(f.locals[1],2);f.pc=40;return;}
case 40:{Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0434.__indy$17(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1));f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.pc=49;return;}
case 43:{f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.push(-1832872660475692226L,2);f.pc=46;return;}
case 46:{f.push(f.locals[1],2);f.pc=47;return;}
case 47:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0434.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=48;return;}
case 48:{throw (Throwable)f.pop();}
case 49:{f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{f.push(f.locals[0],1);f.pc=53;return;}
case 53:{Object receiver=f.pop();super.method_25419();f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(8998037986237408251L,2);f.pc=1;return;}
case 1:{f.push(6523977047684956127L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(239965619429360L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0434.class,"c",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(13,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0434.class,"d",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(13,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0434.class,"e",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0434.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
