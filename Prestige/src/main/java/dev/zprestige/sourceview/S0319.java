package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0017;
import dev.zprestige.sourceview.S0109;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0251;
import dev.zprestige.sourceview.S0254;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0549;
import dev.zprestige.sourceview.S0591;
import net.minecraft.class_1747;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_746;

public class S0319 extends S0260 {
   public S0251 d;
   public S0254 a;
   public S0254 c;
   public static long k;
   public static Object[] l;
   public static String[] m;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
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
         throw new RuntimeException("dev/zprestige/prestige/eO" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class n(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = m(var0, var2);
      Object var6 = l[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(m[var4]);
            l[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field h(Class var0, String var1, Class var2) {
try {

      Field var3 = g(var0, var1, var2);
      if (var3 != null) {
         return var3;
      }

      Class[] var4 = var0.getInterfaces();
      if (var4 != null) {
         for (int var5 = 0; var5 < var4.length; var5++) {
            var3 = h(var4[var5], var1, var2);
            if (var3 != null) {
               return var3;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method h(Class var0, String var1, Class var2, int var3, Class[] var4) {
try {

      Method var5 = g(var0, var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      }

      Class[] var6 = var0.getInterfaces();
      if (var6 != null) {
         for (int var7 = 0; var7 < var6.length; var7++) {
            var5 = h(var6[var7], var1, var2, var3, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void f() {
try {

      l[0] = ")1Q~jo79K1\u0017\u007f7";
      l[1] = "e;89\u0014Yn4)vuWe?-,";
      l[2] = "]n)U\u001acKn,\u000f\tt\\%/\t\u0005`Mb8\u001eNrq";
      l[3] = "!amt\u0005 TAf{\u0014o)Yu|\u001d&A";
      l[4] = "\u0001\u001ckl<;\u0001\u001c|004\u001bW|.0!\u001c&,sa";
      l[5] = "o>qb\u001b~o>f>\u0017quuf \u0017dr\u00042x@";
      l[6] = "F\"`A\u0016LP\"e\u001b\u0005[Gif\u001d\tOV.q\nBl";
      l[7] = "\u000e\u001ff\u0016O;{?m\u0019^t\u001a1f\u0012Z.n";
      l[8] = void.class;
      m[8] = "java/lang/Void";
      l[9] = "d4\u0015?}\u001ad4\u0002cq\u0015~\u007f\u0002}q\u0000y\u000eP&)J";
      l[10] = "Tg@X\u0013nTgW\u0004\u001faN,W\u001a\u001ftI]\u0005AG5";
      l[11] = "?S>\u0012zt4\\/]\u001dv!W/\u0016&";
      l[12] = int.class;
      m[12] = "java/lang/Integer";
      l[13] = "K-pv\u0014`@\"a9xcN cvT";
      l[14] = boolean.class;
      m[14] = "java/lang/Boolean";
      l[15] = "+\u0013})x[=\u0013xskL*X{ugX;\u001flb,O\u0004";
      l[16] = "\u0015~K\u0004*P\u0003~N^9G\u00145MX5S\u0005rZO~E>";
      l[17] = "^l\u0010\u001cR\u001bUc\u0001S1\u0016@n\u000e8\u0004\u0014Q}\u0012\u0014\u0013\u0019";
      l[18] = "\n\u0018%\u0005-a\u0001\u00174JNl\u0014\u0011";
      l[19] = "\nql6A\u0018\u001cqilR\u000f\u000b:jj^\u001b\u001a}}}\u0015\f\u0000";
      l[20] = "Z\u0012T][t/2_RJ;N<TYNa:";
      l[21] = "|rqs\u001e=\tRz|\u000frh\\qw\u000b(\u001c";
      l[22] = "\u0011W nUKEI k8Y(\u0013:aVK\u0010[fuT3";
      l[23] = "?b\u0005Lp\u0019c?\u0006V\u000fOe6ZZX\u0018;a\u000264Fg9YN1E}*";
      l[24] = "]>3y]\u0015\u001b:?8c\u0013a00{[\u001a\f8+&]z\\/4x\u0019J\\d%%c";
      l[25] = "\u000fxb[\",\bli\nLv\\kmW D\u000e&5\u0001L,]nqT>rRuv0";
      l[26] = "\u001e&{\u001f\u0014K\u0012o`\u0016fAHcE\u0004\u0002]C\u001f+\b\u0016Z\u0017uyQ\u001aD.";
      l[27] = "=TUq/4p\u0005\u001e)I>kZ\u0000% 2RT\u00005$Te\u0007T,.eaVU(I";
      l[28] = "\u001dQGzZ\u001e\u0018R]i=\u0003KJD\u007fQ1\u0018\u000e\u001b)=XIYOv\u0002_]R\u001e\u0018";
      l[29] = "\\p\u0003mFJYs\u0019~!W\nk\u0000hMeY/\\0!\f\bx\u000ba\u001e\u000b\u001csZ\u000f";
      l[30] = "\f\u001bpjU!\u001f\u000b0k<weYw+\r|\u001e\u00020qF\u001e";
      l[31] = "c\u0004'S]Ac\u0001{!_E\u0004X'\u0010\tI\u007f\u0003`JB+c\u0004'S]Ac\u0001{!";
      l[32] = "X)!y\u0014\u0000Sz5yr\u00062}jh\fQ\bk%6\u001doY&;wLUOiefr";
      l[33] = "%\u007f=&k-w`z?\u000b%/\u007f\u0003\u007fn04 i-7<*\u00193*{:wsasw$N)f?qy${?3o@";
      l[34] = "!\u007fmT\r\u001e=|lDg\u001dM~t\u0014_\u0014 voIYt}vaW^\u001e//mIg";
      l[35] = "%nSw!\u0003cj_6\u001f\u0005\u0019`Pu'\fthK(!l)hE6&\u0006{1I(\u001f";
      l[36] = "b1~XmJq!>Y\u0004\u001c\u000bsy\u00195\u0017p(>C~u68\"\u0012~E6s3O\u0004";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = d(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$1(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 201 && var8 != 210 && var8 != 'd' && var8 != 248) {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'B') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'z') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 201) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 210) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'd') {
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

   @S0118
   public void a(S0109 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(13,8);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$9$0(f);break;
case 1:__flow$9$1(f);break;
case 2:__flow$9$2(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=22&&f.pc<37&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=39;continue;}
if(f.pc>=35&&f.pc<60&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=62;continue;}
if(f.pc>=45&&f.pc<71&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=73;continue;}
if(f.pc>=68&&f.pc<94&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=96;continue;}
if(f.pc>=79&&f.pc<105&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=107;continue;}
if(f.pc>=117&&f.pc<131&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=133;continue;}
if(f.pc>=139&&f.pc<155&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=157;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   @Override
   public Set a$return$93f6648b(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      return __indy$32(S0591.Crystal, -2447729101718178745L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int m(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (m[var4] != null) {
         return var4;
      }

      Object var5 = l[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 27;
         case 1 -> 46;
         case 2 -> 31;
         case 3 -> 17;
         case 4 -> 22;
         case 5 -> 15;
         case 6 -> 63;
         case 7 -> 59;
         case 8 -> 50;
         case 9 -> 38;
         case 10 -> 20;
         case 11 -> 24;
         case 12 -> 61;
         case 13 -> 25;
         case 14 -> 23;
         case 15 -> 33;
         case 16 -> 2;
         case 17 -> 14;
         case 18 -> 12;
         case 19 -> 29;
         case 20 -> 58;
         case 21 -> 3;
         case 22 -> 18;
         case 23 -> 10;
         case 24 -> 54;
         case 25 -> 45;
         case 26 -> 21;
         case 27 -> 42;
         case 28 -> 51;
         case 29 -> 55;
         case 30 -> 57;
         case 31 -> 32;
         case 32 -> 16;
         case 33 -> 8;
         case 34 -> 34;
         case 35 -> 28;
         case 36 -> 35;
         case 37 -> 13;
         case 38 -> 48;
         case 39 -> 30;
         case 40 -> 49;
         case 41 -> 43;
         case 42 -> 1;
         case 43 -> 5;
         case 44 -> 39;
         case 45 -> 44;
         case 46 -> 7;
         case 47 -> 11;
         case 48 -> 53;
         case 49 -> 40;
         case 50 -> 52;
         case 51 -> 6;
         case 52 -> 9;
         case 53 -> 26;
         case 54 -> 62;
         case 55 -> 60;
         case 56 -> 47;
         case 57 -> 4;
         case 58 -> 41;
         case 59 -> 37;
         case 60 -> 56;
         case 61 -> 36;
         case 62 -> 19;
         default -> 0;
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

      m[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field o(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = l[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = m[var4];
      int var7 = var6.indexOf(8);
      Class var8 = n(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = n(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = g(var12, var10, var11);
         if (var13 != null) {
            l[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = h(var14[var15], var10, var11);
               if (var13 != null) {
                  l[var4] = var13;
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
            var12 = n(107928241813611L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method p(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = l[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = m[var4];
      int var7 = var6.indexOf(8);
      Class var8 = n(Long.parseLong(var6.substring(0, var7), 36), 0L);
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
         var15 = n(Long.parseLong(var6.substring(var12, var17), 36), 0L);
         if (var16 < var13) {
            var14[var16] = var15;
         }

         var12 = var17 + 1;
      }

      Class var23 = var8;

      while (true) {
         Method var26 = g(var23, var10, var15, var13, var14);
         if (var26 != null) {
            l[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = n(107928241813611L, 0L);
            break;
         }
      }

      var23 = var8;

      while (true) {
         Class[] var27;
         if ((var27 = var23.getInterfaces()) != null) {
            for (int var18 = 0; var18 < var27.length; var18++) {
               Method var19 = h(var27[var18], var10, var15, var13, var14);
               if (var19 != null) {
                  l[var4] = var19;
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
            var23 = n(107928241813611L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method g(Class var0, String var1, Class var2, int var3, Class[] var4) {
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

   public static Field g(Class var0, String var1, Class var2) {
try {

      for (Field var6 : var0.getDeclaredFields()) {
         if (var6.getName().equals(var1) && var6.getType() == var2) {
            return var6;
         }
      }

      return null;
   
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

   private static native String[] __indy$2(long var0, long var2) throws Throwable;

   private static native Object __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native class_1799 __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native class_1792 __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native class_1799 __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native class_1792 __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$23(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native int __indy$24(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$30(int var0, int var1, long var2, long var4) throws Throwable;

   private static native void __indy$31(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Set __indy$32(Object var0, long var1, long var3) throws Throwable;







private void __flow$9$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0319.k,2);f.pc=1;return;}
case 1:{f.push(41038981479321L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(35669269430824L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(92);f.pc=10;return;}
case 10:{f.push(59947709854066L,2);f.pc=11;return;}
case 11:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=12;return;}
case 12:{f.locals[6]=f.pop();f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(121903723294134L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[8]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(88);f.pc=18;return;}
case 18:{f.push(1391291803991996585L,2);f.pc=19;return;}
case 19:{f.push(f.locals[2],2);f.pc=20;return;}
case 20:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=21;return;}
case 21:{f.locals[10]=f.pop();f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(f.locals[0],1);f.pc=25;return;}
case 25:{f.push(((dev.zprestige.sourceview.S0319)(Object)f.pop()).d,1);f.pc=26;return;}
case 26:{f.push(1390698781516091344L,2);f.pc=27;return;}
case 27:{f.push(f.locals[2],2);f.pc=28;return;}
case 28:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=29;return;}
case 29:{f.push((java.lang.Boolean)(Object)f.pop(),1);f.pc=30;return;}
case 30:{f.push(1390946981718896809L,2);f.pc=31;return;}
case 31:{f.push(f.locals[2],2);f.pc=32;return;}
case 32:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=33;return;}
case 33:{f.push(f.locals[10],1);f.pc=34;return;}
case 34:{if(f.pop()!=null){f.pc=139;return;}f.pc=35;return;}
case 35:{f.pc=36;return;}
case 36:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=117;return;}f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.pc=45;return;}
case 39:{f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.push(1388071931121965513L,2);f.pc=42;return;}
case 42:{f.push(f.locals[2],2);f.pc=43;return;}
case 43:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=44;return;}
case 44:{throw (Throwable)f.pop();}
case 45:{f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.push(dev.zprestige.sourceview.S0319.b,1);f.pc=48;return;}
case 48:{f.push(1391248259167498668L,2);f.pc=49;return;}
case 49:{f.push(f.locals[2],2);f.pc=50;return;}
case 50:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=51;return;}
case 51:{f.push(1390540742775294607L,2);f.pc=52;return;}
case 52:{f.push(f.locals[2],2);f.pc=53;return;}
case 53:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=54;return;}
case 54:{f.push(1390783012100877850L,2);f.pc=55;return;}
case 55:{f.push(f.locals[2],2);f.pc=56;return;}
case 56:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=57;return;}
case 57:{f.push((f.pop() instanceof net.minecraft.class_1747?1:0),1);f.pc=58;return;}
case 58:{f.push(f.locals[10],1);f.pc=59;return;}
case 59:{if(f.pop()!=null){f.pc=139;return;}f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=68;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.push(1388071931121965513L,2);f.pc=65;return;}
case 65:{f.push(f.locals[2],2);f.pc=66;return;}
case 66:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=67;return;}
case 67:{throw (Throwable)f.pop();}
case 68:{f.pc=69;return;}
case 69:{f.pc=70;return;}
case 70:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=117;return;}f.pc=71;return;}
case 71:{f.pc=72;return;}
case 72:{f.pc=79;return;}
case 73:{f.pc=74;return;}
case 74:{f.pc=75;return;}
case 75:{f.push(1388071931121965513L,2);f.pc=76;return;}
case 76:{f.push(f.locals[2],2);f.pc=77;return;}
case 77:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=78;return;}
case 78:{throw (Throwable)f.pop();}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.push(dev.zprestige.sourceview.S0319.b,1);f.pc=82;return;}
case 82:{f.push(1391248259167498668L,2);f.pc=83;return;}
case 83:{f.push(f.locals[2],2);f.pc=84;return;}
case 84:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=85;return;}
case 85:{f.push(1390589623241239136L,2);f.pc=86;return;}
case 86:{f.push(f.locals[2],2);f.pc=87;return;}
case 87:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=88;return;}
case 88:{f.push(1390783012100877850L,2);f.pc=89;return;}
case 89:{f.push(f.locals[2],2);f.pc=90;return;}
case 90:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=91;return;}
case 91:{f.push((f.pop() instanceof net.minecraft.class_1747?1:0),1);f.pc=92;return;}
case 92:{f.push(f.locals[10],1);f.pc=93;return;}
case 93:{if(f.pop()!=null){f.pc=139;return;}f.pc=94;return;}
case 94:{f.pc=95;return;}
case 95:{f.pc=102;return;}
case 96:{f.pc=97;return;}
case 97:{f.pc=98;return;}
case 98:{f.push(1388071931121965513L,2);f.pc=99;return;}
case 99:{f.push(f.locals[2],2);f.pc=100;return;}
case 100:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=101;return;}
case 101:{throw (Throwable)f.pop();}
case 102:{f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=117;return;}f.pc=105;return;}
case 105:{f.pc=106;return;}
case 106:{f.pc=113;return;}
case 107:{f.pc=108;return;}
case 108:{f.pc=109;return;}
case 109:{f.push(1388071931121965513L,2);f.pc=110;return;}
case 110:{f.push(f.locals[2],2);f.pc=111;return;}
case 111:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=112;return;}
case 112:{throw (Throwable)f.pop();}
case 113:{f.pc=114;return;}
case 114:{f.pc=115;return;}
case 115:{f.pc=116;return;}
case 116:{f.done=true;return;}
case 117:{f.pc=118;return;}
case 118:{f.pc=119;return;}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$9$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.push(f.locals[0],1);f.pc=121;return;}
case 121:{f.push(((dev.zprestige.sourceview.S0319)(Object)f.pop()).a,1);f.pc=122;return;}
case 122:{f.push(f.locals[10],1);f.pc=123;return;}
case 123:{if(f.pop()!=null){f.pc=163;return;}f.pc=124;return;}
case 124:{f.push(1390698781516091344L,2);f.pc=125;return;}
case 125:{f.push(f.locals[2],2);f.pc=126;return;}
case 126:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=127;return;}
case 127:{f.push((java.lang.Integer)(Object)f.pop(),1);f.pc=128;return;}
case 128:{f.push(1391014878553069071L,2);f.pc=129;return;}
case 129:{f.push(f.locals[2],2);f.pc=130;return;}
case 130:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=131;return;}
case 131:{f.pc=132;return;}
case 132:{f.pc=139;return;}
case 133:{f.pc=134;return;}
case 134:{f.pc=135;return;}
case 135:{f.push(1388071931121965513L,2);f.pc=136;return;}
case 136:{f.push(f.locals[2],2);f.pc=137;return;}
case 137:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=138;return;}
case 138:{throw (Throwable)f.pop();}
case 139:{f.pc=140;return;}
case 140:{f.pc=141;return;}
case 141:{f.push(f.locals[0],1);f.pc=142;return;}
case 142:{f.push(((dev.zprestige.sourceview.S0319)(Object)f.pop()).c,1);f.pc=143;return;}
case 143:{f.push(1390698781516091344L,2);f.pc=144;return;}
case 144:{f.push(f.locals[2],2);f.pc=145;return;}
case 145:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$19(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=146;return;}
case 146:{f.push((java.lang.Integer)(Object)f.pop(),1);f.pc=147;return;}
case 147:{f.push(1391014878553069071L,2);f.pc=148;return;}
case 148:{f.push(f.locals[2],2);f.pc=149;return;}
case 149:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=150;return;}
case 150:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a<=b){f.pc=174;return;}f.pc=151;return;}
case 151:{f.pc=152;return;}
case 152:{f.pc=153;return;}
case 153:{f.push(f.locals[0],1);f.pc=154;return;}
case 154:{f.push(((dev.zprestige.sourceview.S0319)(Object)f.pop()).a,1);f.pc=155;return;}
case 155:{f.pc=156;return;}
case 156:{f.pc=163;return;}
case 157:{f.pc=158;return;}
case 158:{f.pc=159;return;}
case 159:{f.push(1388071931121965513L,2);f.pc=160;return;}
case 160:{f.push(f.locals[2],2);f.pc=161;return;}
case 161:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=162;return;}
case 162:{throw (Throwable)f.pop();}
case 163:{f.pc=164;return;}
case 164:{f.pc=165;return;}
case 165:{f.push(f.locals[0],1);f.pc=166;return;}
case 166:{f.push(((dev.zprestige.sourceview.S0319)(Object)f.pop()).c,1);f.pc=167;return;}
case 167:{f.push(1390698781516091344L,2);f.pc=168;return;}
case 168:{f.push(f.locals[2],2);f.pc=169;return;}
case 169:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=170;return;}
case 170:{f.push((java.lang.Integer)(Object)f.pop(),1);f.pc=171;return;}
case 171:{f.push(1387805980416007879L,2);f.pc=172;return;}
case 172:{f.push(f.locals[2],2);f.pc=173;return;}
case 173:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0319.__indy$23(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=174;return;}
case 174:{f.pc=175;return;}
case 175:{f.pc=176;return;}
case 176:{f.pc=177;return;}
case 177:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0017.class),1);f.pc=178;return;}
case 178:{f.rearrange(89);f.pc=179;return;}
case 179:{f.push(dev.zprestige.sourceview.S0319.b,1);f.pc=180;return;}
case 180:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{net.minecraft.class_310.class},new Object[]{((net.minecraft.class_310)(Object)(a0))});f.pc=181;return;}
case 181:{f.locals[11]=f.pop();f.pc=182;return;}
case 182:{f.pc=183;return;}
case 183:{f.pc=184;return;}
case 184:{f.push(f.locals[11],1);f.pc=185;return;}
case 185:{f.push(f.locals[4],2);f.pc=186;return;}
case 186:{f.push(1,1);f.pc=187;return;}
case 187:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=188;return;}
case 188:{f.rearrange(91);f.pc=189;return;}
case 189:{f.rearrange(91);f.pc=190;return;}
case 190:{f.rearrange(87);f.pc=191;return;}
case 191:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=192;return;}
case 192:{f.push(0,1);f.pc=193;return;}
case 193:{f.rearrange(95);f.pc=194;return;}
case 194:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=195;return;}
case 195:{f.push(1388141609432583367L,2);f.pc=196;return;}
case 196:{f.push(f.locals[2],2);f.pc=197;return;}
case 197:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$24(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=198;return;}
case 198:{f.push(f.locals[0],1);f.pc=199;return;}
case 199:{f.push(((dev.zprestige.sourceview.S0319)(Object)f.pop()).a,1);f.pc=200;return;}
case 200:{f.push(1390698781516091344L,2);f.pc=201;return;}
case 201:{f.push(f.locals[2],2);f.pc=202;return;}
case 202:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=203;return;}
case 203:{f.push((java.lang.Integer)(Object)f.pop(),1);f.pc=204;return;}
case 204:{f.push(1391014878553069071L,2);f.pc=205;return;}
case 205:{f.push(f.locals[2],2);f.pc=206;return;}
case 206:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=207;return;}
case 207:{f.push(f.locals[0],1);f.pc=208;return;}
case 208:{f.push(((dev.zprestige.sourceview.S0319)(Object)f.pop()).c,1);f.pc=209;return;}
case 209:{f.push(1390698781516091344L,2);f.pc=210;return;}
case 210:{f.push(f.locals[2],2);f.pc=211;return;}
case 211:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=212;return;}
case 212:{f.push((java.lang.Integer)(Object)f.pop(),1);f.pc=213;return;}
case 213:{f.push(1391014878553069071L,2);f.pc=214;return;}
case 214:{f.push(f.locals[2],2);f.pc=215;return;}
case 215:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=216;return;}
case 216:{f.push(f.locals[6],2);f.pc=217;return;}
case 217:{f.push(3,1);f.pc=218;return;}
case 218:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=219;return;}
case 219:{f.rearrange(91);f.pc=220;return;}
case 220:{f.rearrange(91);f.pc=221;return;}
case 221:{f.rearrange(87);f.pc=222;return;}
case 222:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=223;return;}
case 223:{f.push(2,1);f.pc=224;return;}
case 224:{f.rearrange(95);f.pc=225;return;}
case 225:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=226;return;}
case 226:{f.rearrange(90);f.pc=227;return;}
case 227:{f.rearrange(95);f.pc=228;return;}
case 228:{Object a0=f.pop();f.push(java.lang.Integer.valueOf(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=229;return;}
case 229:{f.push(1,1);f.pc=230;return;}
case 230:{f.rearrange(95);f.pc=231;return;}
case 231:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=232;return;}
case 232:{f.rearrange(90);f.pc=233;return;}
case 233:{f.rearrange(95);f.pc=234;return;}
case 234:{Object a0=f.pop();f.push(java.lang.Integer.valueOf(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=235;return;}
case 235:{f.push(0,1);f.pc=236;return;}
case 236:{f.rearrange(95);f.pc=237;return;}
case 237:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=238;return;}
case 238:{f.push(1388212663653067975L,2);f.pc=239;return;}
case 239:{f.push(f.locals[2],2);f.pc=240;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$9$2(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 240:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$29(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=241;return;}
case 241:{f.push(1387976501022952778L,2);f.pc=242;return;}
case 242:{f.push(f.locals[2],2);f.pc=243;return;}
case 243:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0319.__indy$30(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=244;return;}
case 244:{f.locals[12]=f.pop();f.pc=245;return;}
case 245:{f.pc=246;return;}
case 246:{f.pc=247;return;}
case 247:{f.push(f.locals[11],1);f.pc=248;return;}
case 248:{f.push(f.locals[12],1);f.pc=249;return;}
case 249:{f.push(f.locals[8],2);f.pc=250;return;}
case 250:{f.push(2,1);f.pc=251;return;}
case 251:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=252;return;}
case 252:{f.rearrange(91);f.pc=253;return;}
case 253:{f.rearrange(91);f.pc=254;return;}
case 254:{f.rearrange(87);f.pc=255;return;}
case 255:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=256;return;}
case 256:{f.push(1,1);f.pc=257;return;}
case 257:{f.rearrange(95);f.pc=258;return;}
case 258:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=259;return;}
case 259:{f.rearrange(90);f.pc=260;return;}
case 260:{f.rearrange(95);f.pc=261;return;}
case 261:{Object a0=f.pop();f.push(java.lang.Integer.valueOf(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=262;return;}
case 262:{f.push(0,1);f.pc=263;return;}
case 263:{f.rearrange(95);f.pc=264;return;}
case 264:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=265;return;}
case 265:{f.push(1390901437588205130L,2);f.pc=266;return;}
case 266:{f.push(f.locals[2],2);f.pc=267;return;}
case 267:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0319.__indy$31(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=268;return;}
case 268:{f.pc=269;return;}
case 269:{f.pc=270;return;}
case 270:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

public S0319()throws Throwable{super();}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(3882151802413051814L,2);f.pc=1;return;}
case 1:{f.push(7635650500293907269L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(27094776891262L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0319.class,"k",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(37,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0319.class,"l",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(37,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0319.class,"m",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0319.f();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
