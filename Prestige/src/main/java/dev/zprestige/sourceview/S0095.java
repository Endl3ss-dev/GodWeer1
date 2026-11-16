package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0093;
import dev.zprestige.sourceview.S0094;
import dev.zprestige.sourceview.S0226;
import dev.zprestige.sourceview.S0549;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_638;

public class S0095 extends S0094 {
   public static long a;
   public static Object[] e;
   public static String[] f;

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
      if (f[var4] != null) {
         return var4;
      }

      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 4;
         case 1 -> 8;
         case 2 -> 57;
         case 3 -> 11;
         case 4 -> 63;
         case 5 -> 56;
         case 6 -> 28;
         case 7 -> 0;
         case 8 -> 49;
         case 9 -> 5;
         case 10 -> 44;
         case 11 -> 55;
         case 12 -> 36;
         case 13 -> 38;
         case 14 -> 59;
         case 15 -> 17;
         case 16 -> 61;
         case 17 -> 12;
         case 18 -> 46;
         case 19 -> 3;
         case 20 -> 24;
         case 21 -> 62;
         case 22 -> 21;
         case 23 -> 45;
         case 24 -> 22;
         case 25 -> 60;
         case 26 -> 23;
         case 27 -> 40;
         case 28 -> 37;
         case 29 -> 16;
         case 30 -> 14;
         case 31 -> 52;
         case 32 -> 27;
         case 33 -> 53;
         case 34 -> 20;
         case 35 -> 30;
         case 36 -> 10;
         case 37 -> 26;
         case 38 -> 31;
         case 39 -> 1;
         case 40 -> 29;
         case 41 -> 50;
         case 42 -> 51;
         case 43 -> 54;
         case 44 -> 42;
         case 45 -> 48;
         case 46 -> 19;
         case 47 -> 35;
         case 48 -> 39;
         case 49 -> 34;
         case 50 -> 25;
         case 51 -> 58;
         case 52 -> 32;
         case 53 -> 33;
         case 54 -> 9;
         case 55 -> 6;
         case 56 -> 2;
         case 57 -> 43;
         case 58 -> 18;
         case 59 -> 47;
         case 60 -> 13;
         case 61 -> 7;
         case 62 -> 41;
         default -> 15;
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

      f[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void b$return$9b75() {
try {

      e[0] = "u}$c\rUu}3?\u0001Zo63!\u0001OhGf~X";
      e[1] = "\u000e(\u0000E@!\u000e(\u0017\u0019L.\u0014c\u0017\u0007L;\u0013\u0012FX\u001ep";
      e[2] = "U!F|2kU!Q >dOjQ>>qH\u001b\u0000dg2";
      e[3] = "\u0007  N\n!\u0011 %\u0014\u00196\u0006k&\u0012\u0015\"\u0017,1\u0005^0+";
      e[4] = "%\u000eeo\u0003qP.n`\u0012>-6}g\u001bwE";
      e[5] = "AY\u001e+|\u000bAY\twp\u0004[\u0012\tip\u0011\\cY4!";
      e[6] = "\nR\bgxb\nR\u001f;tm\u0010\u0019\u001f%tx\u0017hN}&";
      e[7] = double.class;
      f[7] = "java/lang/Double";
      e[8] = ")fq/\n5?ftu\u0019\"(-ws\u001569j`d^'\u007f";
      e[9] = "{oO\fJ?\u000eOD\u0003[poAO\b_*\u001b";
      e[10] = void.class;
      f[10] = "java/lang/Void";
      e[11] = "O H?x2D/Yp\u001b?Q\"V\u001b.=@1J790";
      e[12] = "\"\u00177R\u0005Y4\u00172\b\u0016N#\\1\u000e\u001aZ2\u001b&\u0019QKw";
      e[13] = "b{fl\u0000z\u0017[mc\u00115vUfh\u0015o\u0002";
      e[14] = boolean.class;
      f[14] = "java/lang/Boolean";
      e[15] = "`)c:\u001a k&ru{.`-v/";
      e[16] = "d\r\u0001AEe7\u0019NA9n5\b_DU\\aL\u0006\u001297!J]_Wd5\u0005]#\u0005rf\u0016CMVf)\u0016?";
      e[17] = "aH+uQJ2G3\u0005Z[$Z/ih\u0006c\u0000p\u0005G_ H.o[Ra:q}]]<\n\";\u0001\\X";
      e[18] = "d!QpKG5`FzrC^'DhJ\u00103dGj\u0015)";
      e[19] = "q+V\\;=w(T\u0013\u0006oL\u007f\u0002\u0000z`/,\u0013\u001d:\u0005s*\u001a\u001c7|w>R\t\u0006";
      e[20] = "H\"\u007fd(>N!}+\u0015lux01gy\u0010y< -\u0006J5()jcK99c\u0015";
      e[21] = "5\u0004u\fz;|@%\u0014\u001ajo\u0016+\tM=0Kpe#vm\u000b!\u0019pyu";
      e[22] = "K+PGO[\u0018?\u001fG3[\u0016?\nId\fLoV%\u000fKI2\u000b]AZ\r;";
      e[23] = "m3.\u0005VS>'a\u0005*S0't\u000b}\u0004jw*g\u0016Co*u\u001fXR+#";
      e[24] = "\u000b0o,}\u0004X$ ,\u0001\u0004V$5\"VS\fthN=\u0014\t)46s\u0005M ";
      e[25] = "\nI\u001eI\fpPT\u001bLs`:\u0007\bM\u000flYT\u0019PO\tS\t\u0011LMi\u0000\u0006\u001b\u0013s";
      e[26] = "_Xz\u000b\u0012RCU;y\u0011U[Qe\u0015#\u0004\u0016\u000f9Ft\u0004Y\u000fb\u001c\fJHKkyHF\u0019Qg\u0001\u0006W]X\u0002E\n\u0006GTz\u000b\u001bBN1z\u0010\fJA[f\u001dM8";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite b(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$0().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/b2" + " : " + var1 + " : " + var2.toString(), var5);
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
      return __indy$1(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'm' && var8 != 'j' && var8 != 'I' && var8 != 'L') {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'H') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'B') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = g(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'm') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'j') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'I') {
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

   public void b$return$e4b0899c(Object[] var1) {
try {

      S0093 var2 = (S0093)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = a ^ var3;
      var2.c = __indy$2(var2.c, var2.d, 9129976673921261758L, var3);
      var2.d = new class_243(
         __indy$3(var2.d, 9130463853390569445L, var3) / 0.999998F,
         __indy$4(var2.d, 9130548574119261786L, var3) - 1.5E-6,
         __indy$5(var2.d, 9130387740269171692L, var3) / 0.999998F
      );
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException b(MatchException var0) {
try {

      return var0;
   
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

   public static Method h(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = f[var4];
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
            e[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = f(1074594488183404L, 0L);
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
                  e[var4] = var19;
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
            var23 = f(1074594488183404L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class f(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = e(var0, var2);
      Object var6 = e[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(f[var4]);
            e[var4] = var5;
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

   @Override
   public void a$return$e4b0899c(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(14,12);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$15$0(f);break;
case 1:__flow$15$1(f);break;
case 2:__flow$15$2(f);break;
case 3:__flow$15$3(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=33&&f.pc<40&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=42;continue;}
if(f.pc>=37&&f.pc<71&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=73;continue;}
if(f.pc>=118&&f.pc<165&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=167;continue;}
if(f.pc>=208&&f.pc<259&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=261;continue;}
if(f.pc>=302&&f.pc<328&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=330;continue;}
if(f.pc>=307&&f.pc<361&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=363;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static Field g(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = f[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = f(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = c(var12, var10, var11);
         if (var13 != null) {
            e[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = d(var14[var15], var10, var11);
               if (var13 != null) {
                  e[var4] = var13;
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
            var12 = f(1074594488183404L, 0L);
         }
      }
   
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

   private static native MethodHandle __indy$0() throws Throwable;

   private static native Object __indy$1(MethodHandle var0, Object[] var1) throws Throwable;

   private static native class_243 __indy$2(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$6(long var0, long var2) throws Throwable;

   private static native MatchException __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$8(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native class_2338 __indy$15(double var0, double var2, double var4, long var6, long var8) throws Throwable;

   private static native class_2680 __indy$16(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$24(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native class_2338 __indy$27(double var0, double var2, double var4, long var6, long var8) throws Throwable;

   private static native class_2680 __indy$28(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$29(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$32(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$33(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$34(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$37(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$38(Object var0, long var1, long var3) throws Throwable;

   private static native class_2338 __indy$39(double var0, double var2, double var4, long var6, long var8) throws Throwable;

   private static native class_2680 __indy$40(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$41(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$42(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$43(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$44(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$45(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$46(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$47(Object var0, Object var1, long var2, long var4) throws Throwable;

public S0095()throws Throwable{super();}

private void __flow$15$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((dev.zprestige.sourceview.S0093)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[3]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Boolean)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Boolean)(Object)receiver).booleanValue(),1);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(89);f.pc=13;return;}
case 13:{f.push(2,1);f.pc=14;return;}
case 14:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=15;return;}
case 15:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=16;return;}
case 16:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=17;return;}
case 17:{f.locals[4]=f.pop();f.pc=18;return;}
case 18:{f.rearrange(87);f.pc=19;return;}
case 19:{f.push(f.locals[4],2);f.pc=20;return;}
case 20:{f.rearrange(92);f.pc=21;return;}
case 21:{f.push(114389671997888L,2);f.pc=22;return;}
case 22:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=23;return;}
case 23:{f.locals[6]=f.pop();f.pc=24;return;}
case 24:{f.rearrange(92);f.pc=25;return;}
case 25:{f.push(8606540417602L,2);f.pc=26;return;}
case 26:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=27;return;}
case 27:{f.locals[8]=f.pop();f.pc=28;return;}
case 28:{f.rearrange(88);f.pc=29;return;}
case 29:{f.push(9029418090854610801L,2);f.pc=30;return;}
case 30:{f.push(f.locals[4],2);f.pc=31;return;}
case 31:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$6(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=32;return;}
case 32:{f.locals[10]=f.pop();f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.push(f.locals[10],1);f.pc=36;return;}
case 36:{if(f.pop()!=null){f.pc=79;return;}f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(f.locals[2],1);f.pc=39;return;}
case 39:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=83;return;}f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.pc=48;return;}
case 42:{f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.push(9029537475649104985L,2);f.pc=45;return;}
case 45:{f.push(f.locals[4],2);f.pc=46;return;}
case 46:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=47;return;}
case 47:{throw (Throwable)f.pop();}
case 48:{f.pc=49;return;}
case 49:{f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.push(f.locals[0],1);f.pc=52;return;}
case 52:{f.push(f.locals[3],1);f.pc=53;return;}
case 53:{f.push(f.locals[8],2);f.pc=54;return;}
case 54:{f.push(2,1);f.pc=55;return;}
case 55:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=56;return;}
case 56:{f.rearrange(91);f.pc=57;return;}
case 57:{f.rearrange(91);f.pc=58;return;}
case 58:{f.rearrange(87);f.pc=59;return;}
case 59:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=60;return;}
case 60:{f.push(1,1);f.pc=61;return;}
case 61:{f.rearrange(95);f.pc=62;return;}
case 62:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=63;return;}
case 63:{f.rearrange(90);f.pc=64;return;}
case 64:{f.rearrange(95);f.pc=65;return;}
case 65:{f.push(0,1);f.pc=66;return;}
case 66:{f.rearrange(95);f.pc=67;return;}
case 67:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=68;return;}
case 68:{f.push(9029337096177727321L,2);f.pc=69;return;}
case 69:{f.push(f.locals[4],2);f.pc=70;return;}
case 70:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0095.__indy$8(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=71;return;}
case 71:{f.pc=72;return;}
case 72:{f.pc=79;return;}
case 73:{f.pc=74;return;}
case 74:{f.pc=75;return;}
case 75:{f.push(9029537475649104985L,2);f.pc=76;return;}
case 76:{f.push(f.locals[4],2);f.pc=77;return;}
case 77:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=78;return;}
case 78:{throw (Throwable)f.pop();}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.pc=82;return;}
case 82:{f.done=true;return;}
case 83:{f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.pc=86;return;}
case 86:{f.push(dev.zprestige.sourceview.S0226.b,1);f.pc=87;return;}
case 87:{f.push(9029500030936472195L,2);f.pc=88;return;}
case 88:{f.push(f.locals[4],2);f.pc=89;return;}
case 89:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=90;return;}
case 90:{f.push(f.locals[3],1);f.pc=91;return;}
case 91:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).c,1);f.pc=92;return;}
case 92:{f.push(9029624503945239583L,2);f.pc=93;return;}
case 93:{f.push(f.locals[4],2);f.pc=94;return;}
case 94:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=95;return;}
case 95:{f.push(f.locals[3],1);f.pc=96;return;}
case 96:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).c,1);f.pc=97;return;}
case 97:{f.push(9028730267512206752L,2);f.pc=98;return;}
case 98:{f.push(f.locals[4],2);f.pc=99;return;}
case 99:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=100;return;}
case 100:{f.push(f.locals[3],1);f.pc=101;return;}
case 101:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).c,1);f.pc=102;return;}
case 102:{f.push(9029682740724846614L,2);f.pc=103;return;}
case 103:{f.push(f.locals[4],2);f.pc=104;return;}
case 104:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=105;return;}
case 105:{f.push(f.locals[3],1);f.pc=106;return;}
case 106:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=107;return;}
case 107:{f.push(9029682740724846614L,2);f.pc=108;return;}
case 108:{f.push(f.locals[4],2);f.pc=109;return;}
case 109:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=110;return;}
case 110:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=111;return;}
case 111:{f.push(9028818494541146922L,2);f.pc=112;return;}
case 112:{f.push(f.locals[4],2);f.pc=113;return;}
case 113:{Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$15(dev.zprestige.sourceview.FlowFrame.d(a0),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2),dev.zprestige.sourceview.FlowFrame.j(a3),dev.zprestige.sourceview.FlowFrame.j(a4)),1);f.pc=114;return;}
case 114:{f.push(9029201786889247388L,2);f.pc=115;return;}
case 115:{f.push(f.locals[4],2);f.pc=116;return;}
case 116:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$16(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=117;return;}
case 117:{f.locals[11]=f.pop();f.pc=118;return;}
case 118:{f.pc=119;return;}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$15$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.push(f.locals[0],1);f.pc=121;return;}
case 121:{f.push(f.locals[11],1);f.pc=122;return;}
case 122:{f.push(f.locals[6],2);f.pc=123;return;}
case 123:{f.push(2,1);f.pc=124;return;}
case 124:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=125;return;}
case 125:{f.rearrange(91);f.pc=126;return;}
case 126:{f.rearrange(91);f.pc=127;return;}
case 127:{f.rearrange(87);f.pc=128;return;}
case 128:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=129;return;}
case 129:{f.push(1,1);f.pc=130;return;}
case 130:{f.rearrange(95);f.pc=131;return;}
case 131:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=132;return;}
case 132:{f.rearrange(90);f.pc=133;return;}
case 133:{f.rearrange(95);f.pc=134;return;}
case 134:{f.push(0,1);f.pc=135;return;}
case 135:{f.rearrange(95);f.pc=136;return;}
case 136:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=137;return;}
case 137:{f.push(9028659109259312839L,2);f.pc=138;return;}
case 138:{f.push(f.locals[4],2);f.pc=139;return;}
case 139:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$17(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=140;return;}
case 140:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=173;return;}f.pc=141;return;}
case 141:{f.pc=142;return;}
case 142:{f.pc=143;return;}
case 143:{f.push(f.locals[3],1);f.pc=144;return;}
case 144:{f.push(dev.zprestige.sourceview.FlowFrame.pending(net.minecraft.class_243.class),1);f.pc=145;return;}
case 145:{f.rearrange(89);f.pc=146;return;}
case 146:{f.push(f.locals[3],1);f.pc=147;return;}
case 147:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=148;return;}
case 148:{f.push(9029624503945239583L,2);f.pc=149;return;}
case 149:{f.push(f.locals[4],2);f.pc=150;return;}
case 150:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=151;return;}
case 151:{f.push(f.locals[3],1);f.pc=152;return;}
case 152:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=153;return;}
case 153:{f.push(9028730267512206752L,2);f.pc=154;return;}
case 154:{f.push(f.locals[4],2);f.pc=155;return;}
case 155:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$19(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=156;return;}
case 156:{f.push(f.locals[3],1);f.pc=157;return;}
case 157:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=158;return;}
case 158:{f.push(9029682740724846614L,2);f.pc=159;return;}
case 159:{f.push(f.locals[4],2);f.pc=160;return;}
case 160:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=161;return;}
case 161:{f.push(Double.longBitsToDouble(-4617991057905706598L),2);f.pc=162;return;}
case 162:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a*b,2);f.pc=163;return;}
case 163:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{double.class,double.class,double.class},new Object[]{dev.zprestige.sourceview.FlowFrame.d(a0),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2)});f.pc=164;return;}
case 164:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0093.class,receiver,"d",((net.minecraft.class_243)(Object)(value)));f.pc=165;return;}
case 165:{f.pc=166;return;}
case 166:{f.pc=173;return;}
case 167:{f.pc=168;return;}
case 168:{f.pc=169;return;}
case 169:{f.push(9029537475649104985L,2);f.pc=170;return;}
case 170:{f.push(f.locals[4],2);f.pc=171;return;}
case 171:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=172;return;}
case 172:{throw (Throwable)f.pop();}
case 173:{f.pc=174;return;}
case 174:{f.pc=175;return;}
case 175:{f.pc=176;return;}
case 176:{f.push(dev.zprestige.sourceview.S0226.b,1);f.pc=177;return;}
case 177:{f.push(9029500030936472195L,2);f.pc=178;return;}
case 178:{f.push(f.locals[4],2);f.pc=179;return;}
case 179:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=180;return;}
case 180:{f.push(f.locals[3],1);f.pc=181;return;}
case 181:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).c,1);f.pc=182;return;}
case 182:{f.push(9029624503945239583L,2);f.pc=183;return;}
case 183:{f.push(f.locals[4],2);f.pc=184;return;}
case 184:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=185;return;}
case 185:{f.push(f.locals[3],1);f.pc=186;return;}
case 186:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).c,1);f.pc=187;return;}
case 187:{f.push(9028730267512206752L,2);f.pc=188;return;}
case 188:{f.push(f.locals[4],2);f.pc=189;return;}
case 189:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$24(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=190;return;}
case 190:{f.push(f.locals[3],1);f.pc=191;return;}
case 191:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=192;return;}
case 192:{f.push(9028730267512206752L,2);f.pc=193;return;}
case 193:{f.push(f.locals[4],2);f.pc=194;return;}
case 194:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=195;return;}
case 195:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=196;return;}
case 196:{f.push(f.locals[3],1);f.pc=197;return;}
case 197:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).c,1);f.pc=198;return;}
case 198:{f.push(9029682740724846614L,2);f.pc=199;return;}
case 199:{f.push(f.locals[4],2);f.pc=200;return;}
case 200:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=201;return;}
case 201:{f.push(9028818494541146922L,2);f.pc=202;return;}
case 202:{f.push(f.locals[4],2);f.pc=203;return;}
case 203:{Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$27(dev.zprestige.sourceview.FlowFrame.d(a0),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2),dev.zprestige.sourceview.FlowFrame.j(a3),dev.zprestige.sourceview.FlowFrame.j(a4)),1);f.pc=204;return;}
case 204:{f.push(9029201786889247388L,2);f.pc=205;return;}
case 205:{f.push(f.locals[4],2);f.pc=206;return;}
case 206:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$28(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=207;return;}
case 207:{f.locals[12]=f.pop();f.pc=208;return;}
case 208:{f.pc=209;return;}
case 209:{f.pc=210;return;}
case 210:{f.push(f.locals[0],1);f.pc=211;return;}
case 211:{f.push(f.locals[12],1);f.pc=212;return;}
case 212:{f.push(f.locals[6],2);f.pc=213;return;}
case 213:{f.push(2,1);f.pc=214;return;}
case 214:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=215;return;}
case 215:{f.rearrange(91);f.pc=216;return;}
case 216:{f.rearrange(91);f.pc=217;return;}
case 217:{f.rearrange(87);f.pc=218;return;}
case 218:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=219;return;}
case 219:{f.push(1,1);f.pc=220;return;}
case 220:{f.rearrange(95);f.pc=221;return;}
case 221:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=222;return;}
case 222:{f.rearrange(90);f.pc=223;return;}
case 223:{f.rearrange(95);f.pc=224;return;}
case 224:{f.push(0,1);f.pc=225;return;}
case 225:{f.rearrange(95);f.pc=226;return;}
case 226:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=227;return;}
case 227:{f.push(9028659109259312839L,2);f.pc=228;return;}
case 228:{f.push(f.locals[4],2);f.pc=229;return;}
case 229:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$29(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=230;return;}
case 230:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=267;return;}f.pc=231;return;}
case 231:{f.pc=232;return;}
case 232:{f.pc=233;return;}
case 233:{f.push(f.locals[3],1);f.pc=234;return;}
case 234:{f.push(dev.zprestige.sourceview.FlowFrame.pending(net.minecraft.class_243.class),1);f.pc=235;return;}
case 235:{f.rearrange(89);f.pc=236;return;}
case 236:{f.push(f.locals[3],1);f.pc=237;return;}
case 237:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=238;return;}
case 238:{f.push(9029624503945239583L,2);f.pc=239;return;}
case 239:{f.push(f.locals[4],2);f.pc=240;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$15$2(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 240:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$30(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=241;return;}
case 241:{f.push(Double.longBitsToDouble(4607173411716726784L),2);f.pc=242;return;}
case 242:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a*b,2);f.pc=243;return;}
case 243:{f.push(f.locals[3],1);f.pc=244;return;}
case 244:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=245;return;}
case 245:{f.push(9028730267512206752L,2);f.pc=246;return;}
case 246:{f.push(f.locals[4],2);f.pc=247;return;}
case 247:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$31(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=248;return;}
case 248:{f.push(Double.longBitsToDouble(-4619792497756654797L),2);f.pc=249;return;}
case 249:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a*b,2);f.pc=250;return;}
case 250:{f.push(f.locals[3],1);f.pc=251;return;}
case 251:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=252;return;}
case 252:{f.push(9029682740724846614L,2);f.pc=253;return;}
case 253:{f.push(f.locals[4],2);f.pc=254;return;}
case 254:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$32(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=255;return;}
case 255:{f.push(Double.longBitsToDouble(4607173411716726784L),2);f.pc=256;return;}
case 256:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a*b,2);f.pc=257;return;}
case 257:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{double.class,double.class,double.class},new Object[]{dev.zprestige.sourceview.FlowFrame.d(a0),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2)});f.pc=258;return;}
case 258:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0093.class,receiver,"d",((net.minecraft.class_243)(Object)(value)));f.pc=259;return;}
case 259:{f.pc=260;return;}
case 260:{f.pc=267;return;}
case 261:{f.pc=262;return;}
case 262:{f.pc=263;return;}
case 263:{f.push(9029537475649104985L,2);f.pc=264;return;}
case 264:{f.push(f.locals[4],2);f.pc=265;return;}
case 265:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$33(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=266;return;}
case 266:{throw (Throwable)f.pop();}
case 267:{f.pc=268;return;}
case 268:{f.pc=269;return;}
case 269:{f.pc=270;return;}
case 270:{f.push(dev.zprestige.sourceview.S0226.b,1);f.pc=271;return;}
case 271:{f.push(9029500030936472195L,2);f.pc=272;return;}
case 272:{f.push(f.locals[4],2);f.pc=273;return;}
case 273:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$34(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=274;return;}
case 274:{f.push(f.locals[3],1);f.pc=275;return;}
case 275:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).c,1);f.pc=276;return;}
case 276:{f.push(9029624503945239583L,2);f.pc=277;return;}
case 277:{f.push(f.locals[4],2);f.pc=278;return;}
case 278:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$35(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=279;return;}
case 279:{f.push(f.locals[3],1);f.pc=280;return;}
case 280:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=281;return;}
case 281:{f.push(9029624503945239583L,2);f.pc=282;return;}
case 282:{f.push(f.locals[4],2);f.pc=283;return;}
case 283:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$36(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=284;return;}
case 284:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=285;return;}
case 285:{f.push(f.locals[3],1);f.pc=286;return;}
case 286:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).c,1);f.pc=287;return;}
case 287:{f.push(9028730267512206752L,2);f.pc=288;return;}
case 288:{f.push(f.locals[4],2);f.pc=289;return;}
case 289:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$37(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=290;return;}
case 290:{f.push(f.locals[3],1);f.pc=291;return;}
case 291:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).c,1);f.pc=292;return;}
case 292:{f.push(9029682740724846614L,2);f.pc=293;return;}
case 293:{f.push(f.locals[4],2);f.pc=294;return;}
case 294:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$38(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=295;return;}
case 295:{f.push(9028818494541146922L,2);f.pc=296;return;}
case 296:{f.push(f.locals[4],2);f.pc=297;return;}
case 297:{Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$39(dev.zprestige.sourceview.FlowFrame.d(a0),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2),dev.zprestige.sourceview.FlowFrame.j(a3),dev.zprestige.sourceview.FlowFrame.j(a4)),1);f.pc=298;return;}
case 298:{f.push(9029201786889247388L,2);f.pc=299;return;}
case 299:{f.push(f.locals[4],2);f.pc=300;return;}
case 300:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$40(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=301;return;}
case 301:{f.locals[13]=f.pop();f.pc=302;return;}
case 302:{f.pc=303;return;}
case 303:{f.pc=304;return;}
case 304:{f.push(f.locals[0],1);f.pc=305;return;}
case 305:{f.push(f.locals[10],1);f.pc=306;return;}
case 306:{if(f.pop()!=null){f.pc=373;return;}f.pc=307;return;}
case 307:{f.pc=308;return;}
case 308:{f.push(f.locals[13],1);f.pc=309;return;}
case 309:{f.push(f.locals[6],2);f.pc=310;return;}
case 310:{f.push(2,1);f.pc=311;return;}
case 311:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=312;return;}
case 312:{f.rearrange(91);f.pc=313;return;}
case 313:{f.rearrange(91);f.pc=314;return;}
case 314:{f.rearrange(87);f.pc=315;return;}
case 315:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=316;return;}
case 316:{f.push(1,1);f.pc=317;return;}
case 317:{f.rearrange(95);f.pc=318;return;}
case 318:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=319;return;}
case 319:{f.rearrange(90);f.pc=320;return;}
case 320:{f.rearrange(95);f.pc=321;return;}
case 321:{f.push(0,1);f.pc=322;return;}
case 322:{f.rearrange(95);f.pc=323;return;}
case 323:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=324;return;}
case 324:{f.push(9028659109259312839L,2);f.pc=325;return;}
case 325:{f.push(f.locals[4],2);f.pc=326;return;}
case 326:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$41(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=327;return;}
case 327:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=369;return;}f.pc=328;return;}
case 328:{f.pc=329;return;}
case 329:{f.pc=336;return;}
case 330:{f.pc=331;return;}
case 331:{f.pc=332;return;}
case 332:{f.push(9029537475649104985L,2);f.pc=333;return;}
case 333:{f.push(f.locals[4],2);f.pc=334;return;}
case 334:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$42(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=335;return;}
case 335:{throw (Throwable)f.pop();}
case 336:{f.pc=337;return;}
case 337:{f.pc=338;return;}
case 338:{f.pc=339;return;}
case 339:{f.push(f.locals[3],1);f.pc=340;return;}
case 340:{f.push(dev.zprestige.sourceview.FlowFrame.pending(net.minecraft.class_243.class),1);f.pc=341;return;}
case 341:{f.rearrange(89);f.pc=342;return;}
case 342:{f.push(f.locals[3],1);f.pc=343;return;}
case 343:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=344;return;}
case 344:{f.push(9029624503945239583L,2);f.pc=345;return;}
case 345:{f.push(f.locals[4],2);f.pc=346;return;}
case 346:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$43(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=347;return;}
case 347:{f.push(Double.longBitsToDouble(-4617991057905706598L),2);f.pc=348;return;}
case 348:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a*b,2);f.pc=349;return;}
case 349:{f.push(f.locals[3],1);f.pc=350;return;}
case 350:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=351;return;}
case 351:{f.push(9028730267512206752L,2);f.pc=352;return;}
case 352:{f.push(f.locals[4],2);f.pc=353;return;}
case 353:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$44(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=354;return;}
case 354:{f.push(f.locals[3],1);f.pc=355;return;}
case 355:{f.push(((dev.zprestige.sourceview.S0093)(Object)f.pop()).d,1);f.pc=356;return;}
case 356:{f.push(9029682740724846614L,2);f.pc=357;return;}
case 357:{f.push(f.locals[4],2);f.pc=358;return;}
case 358:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$45(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=359;return;}
case 359:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{double.class,double.class,double.class},new Object[]{dev.zprestige.sourceview.FlowFrame.d(a0),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2)});f.pc=360;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$15$3(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 360:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0093.class,receiver,"d",((net.minecraft.class_243)(Object)(value)));f.pc=361;return;}
case 361:{f.pc=362;return;}
case 362:{f.pc=369;return;}
case 363:{f.pc=364;return;}
case 364:{f.pc=365;return;}
case 365:{f.push(9029537475649104985L,2);f.pc=366;return;}
case 366:{f.push(f.locals[4],2);f.pc=367;return;}
case 367:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0095.__indy$46(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=368;return;}
case 368:{throw (Throwable)f.pop();}
case 369:{f.pc=370;return;}
case 370:{f.pc=371;return;}
case 371:{f.pc=372;return;}
case 372:{f.push(f.locals[0],1);f.pc=373;return;}
case 373:{f.pc=374;return;}
case 374:{f.pc=375;return;}
case 375:{f.push(f.locals[3],1);f.pc=376;return;}
case 376:{f.push(f.locals[8],2);f.pc=377;return;}
case 377:{f.push(2,1);f.pc=378;return;}
case 378:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=379;return;}
case 379:{f.rearrange(91);f.pc=380;return;}
case 380:{f.rearrange(91);f.pc=381;return;}
case 381:{f.rearrange(87);f.pc=382;return;}
case 382:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=383;return;}
case 383:{f.push(1,1);f.pc=384;return;}
case 384:{f.rearrange(95);f.pc=385;return;}
case 385:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=386;return;}
case 386:{f.rearrange(90);f.pc=387;return;}
case 387:{f.rearrange(95);f.pc=388;return;}
case 388:{f.push(0,1);f.pc=389;return;}
case 389:{f.rearrange(95);f.pc=390;return;}
case 390:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=391;return;}
case 391:{f.push(9029337096177727321L,2);f.pc=392;return;}
case 392:{f.push(f.locals[4],2);f.pc=393;return;}
case 393:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0095.__indy$47(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=394;return;}
case 394:{f.pc=395;return;}
case 395:{f.pc=396;return;}
case 396:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-3195692642352587794L,2);f.pc=1;return;}
case 1:{f.push(-5846721918730229781L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(205982284939809L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0095.class,"a",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(27,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0095.class,"e",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(27,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0095.class,"f",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0095.b$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
