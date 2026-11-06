package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_315;
import net.minecraft.class_5498;
import net.minecraft.class_746;

public class S0320 extends S0260 {
   public S0251 d;
   public float a = 0.0F;
   public float c = 0.0F;
   public class_5498 e = null;
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


   @Override
   public void e$return$e4b0899c(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      __indy$1(__indy$0(b, 3993986671426378680L, var2), this.e, 3990385278039763824L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite b(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$2().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/eP" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public boolean b$return$e4b089a0(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      return false;
   
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

   public static void f() {
try {

      l[0] = "\u000f$\u0001ocW\u0019$\u00045p@\u000eo\u00073|T\u001f(\u0010$7F#";
      l[1] = "\u0018\u0015\"N:|m5)A+3\f;\"J/ix";
      l[2] = void.class;
      m[2] = "java/lang/Void";
      l[3] = "xx\u0019Dcgnx\u001c\u001eppy3\u001f\u0018|dht\b\u000f7uW";
      l[4] = "QoUirX$O^fc\u0017EAUmgM1";
      l[5] = double.class;
      m[5] = "java/lang/Double";
      l[6] = ">^O\n`bK~D\u0005q-*pO\u000euw^";
      l[7] = "l<168\u0006l<&j4\tvw&t4\u001cq\u0006v)`";
      l[8] = "@\f`Ji>@\fw\u0016e1ZGw\be$]6!P=o";
      l[9] = "r.9))kr..u%dhe.k%qo\u0014~6t";
      l[10] = "s\u0007!\u0019\u0011\u0007s\u00076E\u001d\biL6[\u001d\u001dn=b\u0003J";
      l[11] = "E_f(q\u0003S_crb\u0014D\u0014`tn\u0000USwc%\u0016q";
      l[12] = ".\bK/w\u000b%\u0007Z`\u0014\u00060\nU\u000b!\u0004!\u0019I'6\t";
      l[13] = float.class;
      m[13] = "java/lang/Float";
      l[14] = "`k7\u001e\rpkd&Qasef$\u001eM";
      l[15] = boolean.class;
      m[15] = "java/lang/Boolean";
      l[16] = "\u0019iWbCI\u000fiR8P^\u0018\"Q>\\J\teF)\u0017]6";
      l[17] = "Lc\b?*7Gl\u0019pK9Lg\u001d*";
      l[18] = "\fJx(#2\u001aJ}r0%\r\u0001~t<1\u001cFicw#[";
      l[19] = "ApI\"\u0019'4PB-\bhU^I&\f2!";
      l[20] = "\u001bs2ztsnS9ue<\u000f]2~af{";
      l[21] = "IT\u001d\u001b\u0002zH\n\u0017\u0017ix\u0019I\u0007\u0017\u0005JO\f_LU\u001dII\u0001\u0016\u0019m\u0014H\u0005\ti";
      l[22] = "*Xy<x\u0017+\u0006s0\u0013\u0015zEc0\u007f',\u0000;k(p*Ee1c\u0000wDa.\u0013IwV>=\"\u001e+R~W";
      l[23] = "\u000bS.mtN\u000bJl|\u001dJT]z}J\u001d\n\n\"\u0011\"MW\t' \u007fFJR";
      l[24] = "<\u0006f\u0001\u0019\u0001rHtQk\u0004\u0003IoVT\u0006r\u0007#\u000f\u0007m3\b$S\u0000\u001c}D}\u0000k";
      l[25] = "\u0016\\\u007f\u001bj3\u0015\u0019'~`R\u001d\u000f\u007fNl/\u0010Yy\u0014\tk\u0010\f#\u00148<L\bc~";
      l[26] = "\u0005>G}\u0019\u0018\u00054\u0013et\u001b?:\u0012fD\u0017B7D`\u001er\u00043\u001b;\u000b\u0010\\+\u0002ct";
      l[27] = "F\u0015.\u000eK?F\u001fz\u0016&?|\u0011{\u0015\u00160\u0001\u001c-\u0013LUG\u0018rHY7\u001f\u0000k\u0010&";
      l[28] = "U\u0019\u0011.o\u0002\b\u0018\u00151\u001f\u001c\t\b\u0013$HHV[Iu\u001fO\u0014\u0003\u00118o\u0012\u0015\u0007\u000e";
      l[29] = "bs\u0010_{\u0017?x\r\u0004BC0f\u0012\u0001.qf$N[\u007f&l|\u001d\u0019>EgkI\rB";
      l[30] = "3\u0002wlj55RvaP8=_)h94\u0004Q)x=R3X jnm(F*wP";
      l[31] = "+I\u001a*&&xL\u001b2^v@C\u000b)nx=N]/4\u001dyN\bu4,.\u0012\f5^";
      l[32] = "-Q/^y?)_l@A8G^mKy>+\u00047O;Q";
      l[33] = "\u001d0\u001d\u0017#$\u001d)_\u0006J B>I\u0007\u001dw\u001db\u0015kw/Y8F\fvqS4";
      l[34] = "\u000f\r'N+\t\\\b&VSXd\u00076McW\u0019\n`K92]\n5\u00119\u0003\nV1QS";
      l[35] = "\u0013Wx|\u0019,N\\e' xABz\"LJ\u0017\u0000&x\u001c\u001d\u001dXu:\\~\u0016O!. ";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'F' && var8 != 231 && var8 != 253 && var8 != 'X') {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 255) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 252) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'F') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 231) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 253) {
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

   public static Object d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = d(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$3(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public void d$return$e4b0899c(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);

      try {
         this.a = __indy$5(__indy$4(b, 3243603162228196705L, var2), 3246689766548528983L, var2);
         this.c = __indy$7(__indy$6(b, 3243603162228196705L, var2), 3242943407827523422L, var2);
         this.e = __indy$9(__indy$8(b, 3246574386586157531L, var2), 3243481570997003328L, var2);
         if (__indy$11((Boolean)(Object)(__indy$10(this.d, 3246605599656002918L, var2)), 3243118532624358377L, var2)) {
            __indy$14(__indy$12(b, 3246574386586157531L, var2), __indy$13(3242960844634700170L, var2), 3243693201233031443L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$15(var4, 3242689128596036214L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0032 var1) {
try {

      long var2 = k ^ 108264680958348L;
      __indy$16(var1, new Object[]{this.a}, 5400839644221768586L, var2);
      __indy$17(var1, new Object[]{this.c}, 5403438006613181225L, var2);
      __indy$18(var1, new Object[0], 5403601175089792259L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0113 var1) {
try {

      long var2 = k ^ 89789039749889L;
      this.a = (float)(this.a + __indy$19(var1, new Object[0], 5219829586572592099L, var2) * 0.1F);
      this.c = (float)(this.c + __indy$20(var1, new Object[0], 5219929038037144863L, var2) * 0.1F);
      __indy$21(var1, new Object[0], 5219743974642960270L, var2);
   
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
         case 0 -> 54;
         case 1 -> 8;
         case 2 -> 58;
         case 3 -> 48;
         case 4 -> 38;
         case 5 -> 14;
         case 6 -> 15;
         case 7 -> 5;
         case 8 -> 19;
         case 9 -> 11;
         case 10 -> 16;
         case 11 -> 57;
         case 12 -> 18;
         case 13 -> 50;
         case 14 -> 6;
         case 15 -> 7;
         case 16 -> 42;
         case 17 -> 39;
         case 18 -> 1;
         case 19 -> 25;
         case 20 -> 30;
         case 21 -> 49;
         case 22 -> 23;
         case 23 -> 40;
         case 24 -> 13;
         case 25 -> 22;
         case 26 -> 24;
         case 27 -> 10;
         case 28 -> 2;
         case 29 -> 32;
         case 30 -> 56;
         case 31 -> 44;
         case 32 -> 55;
         case 33 -> 33;
         case 34 -> 34;
         case 35 -> 43;
         case 36 -> 3;
         case 37 -> 29;
         case 38 -> 35;
         case 39 -> 27;
         case 40 -> 21;
         case 41 -> 17;
         case 42 -> 51;
         case 43 -> 47;
         case 44 -> 28;
         case 45 -> 4;
         case 46 -> 45;
         case 47 -> 53;
         case 48 -> 37;
         case 49 -> 26;
         case 50 -> 36;
         case 51 -> 9;
         case 52 -> 52;
         case 53 -> 63;
         case 54 -> 46;
         case 55 -> 20;
         case 56 -> 62;
         case 57 -> 0;
         case 58 -> 59;
         case 59 -> 41;
         case 60 -> 60;
         case 61 -> 31;
         case 62 -> 12;
         default -> 61;
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
            var12 = n(1242386447306355L, 0L);
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
            var23 = n(1242386447306355L, 0L);
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
            var23 = n(1242386447306355L, 0L);
         }
      }
   
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

   private static native class_315 __indy$0(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$1(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$2() throws Throwable;

   private static native Object __indy$3(MethodHandle var0, Object[] var1) throws Throwable;

   private static native class_746 __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native float __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native float __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native class_315 __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native class_5498 __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native class_315 __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native class_5498 __indy$13(long var0, long var2) throws Throwable;

   private static native void __indy$14(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$16(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$18(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$19(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$20(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$21(Object var0, Object var1, long var2, long var4) throws Throwable;

public S0320()throws Throwable{super();}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(2027247885877632269L,2);f.pc=1;return;}
case 1:{f.push(-785947692118700123L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(259698657210932L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0320.class,"k",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(36,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0320.class,"l",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(36,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0320.class,"m",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0320.f();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
