package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class S0454 {
   public static Map a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$0$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static Method b(Class var0, String var1, Class var2, int var3, Class[] var4) {
try {

      Method var5 = a(var0, var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      }

      Class[] var6 = var0.getInterfaces();
      if (var6 != null) {
         for (int var7 = 0; var7 < var6.length; var7++) {
            var5 = b(var6[var7], var1, var2, var3, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class b(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = a(var0, var2);
      Object var6 = c[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(d[var4]);
            c[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field b(Class var0, String var1, Class var2) {
try {

      Field var3 = a(var0, var1, var2);
      if (var3 != null) {
         return var3;
      }

      Class[] var4 = var0.getInterfaces();
      if (var4 != null) {
         for (int var5 = 0; var5 < var4.length; var5++) {
            var3 = b(var4[var5], var1, var2);
            if (var3 != null) {
               return var3;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field c(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = c[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = d[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            c[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  c[var4] = var13;
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
            var12 = b(896277040366464L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = c[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = d[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
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
         var15 = b(Long.parseLong(var6.substring(var12, var17), 36), 0L);
         if (var16 < var13) {
            var14[var16] = var15;
         }

         var12 = var17 + 1;
      }

      Class var23 = var8;

      while (true) {
         Method var26 = a(var23, var10, var15, var13, var14);
         if (var26 != null) {
            c[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(896277040366464L, 0L);
            break;
         }
      }

      var23 = var8;

      while (true) {
         Class[] var27;
         if ((var27 = var23.getInterfaces()) != null) {
            for (int var18 = 0; var18 < var27.length; var18++) {
               Method var19 = b(var27[var18], var10, var15, var13, var14);
               if (var19 != null) {
                  c[var4] = var19;
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
            var23 = b(896277040366464L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      c[0] = "[Lb)\u001cW^Yi)\u001fPQPbk^gx\f5";
      c[1] = int.class;
      d[1] = "java/lang/Integer";
      c[2] = float.class;
      d[2] = "java/lang/Float";
      c[3] = void.class;
      d[3] = "java/lang/Void";
      c[4] = "\u0006\u0002=C>.\u0010\u00028\u0019-9\u0007I;\u001f!-\u0016\u000e,\bj9(";
      c[5] = "!}q,4o*r`cIw9ui*";
      c[6] = boolean.class;
      d[6] = "java/lang/Boolean";
      c[7] = "So";
      c[8] = "D3C\"^\u001eO<Rm9\u001cZ7R&\u0002";
      c[9] = "Y\u0013\u0002L\u0012_R\u001c\u0013\u0003\u007f[R\u0000'HMFV\u001c\u0017H";
      c[10] = "Dk";
      c[11] = "UB)Mp,KJ3\u0002\u00138O";
      c[12] = "\"\u000e\u00014D\u000f)\u0001\u0010{%\u0001\"\n\u0014!";
      c[13] = ".npHh\n0fj\u0007 \n*lr@)\u0011jIsG%\u000b-`h";
      c[14] = "\fU\u0019EyE\u0011@Ag8H\tF";
      c[15] = "j\\";
      c[16] = "2@`\u001c \u0006\u007f\ru\u001a\u0011\u0017nMC]|\u0015e1tCx\u001dsAh\u001f`x";
      c[17] = "7$>;\u000f~|r?a?/`(<vC)F#\raD)a1D;C<m?'mVt?MujS<j/(;F5\u0007}8sU6d+-;\u0007D";
      c[18] = "kw<V\u0011\u0011&:)P \u00007z\n\u0000Lo\"b9\bP\u001f>>!m";
      c[19] = "|\u000e0~=\rq]n\u001a9R@Uot1Lx\u000fo\u001a.RtV~j2\u000el3v~?SmCj\"'6eWg\u007f&Fy\u000b\u007f\u001a.RtV~j2\u000el3v~?SmCj\"'6'_o'kKa\f|!V";
      c[20] = "Npz+M:C#$OIer+%!A{J\b-3\\`_q*1&yK$!?Ve\u0017<DsK;E}.!FdKM.w[<\u001f?>3Jj/w(.\u001b<R1{=\u001d\u0001";
      c[21] = "\u001c\u0018sd.Y\u0011K-\u0000*\u0006 C,n\"\u0018\u0018\u001e,\u0000=\u0006\u0014@=p!Z\f%5d,\u0007\rU)84b\u0005A$e5\u0012\u0019\u001d<\u0000=\u0006\u0014@=p!Z\f%wl$_@X1?7Y}";
      c[22] = "-\u000bu|C} X+\u0018G\"\u0011P*vO<)\u000f%\u0018P\"%S;hL~=6rw\u0017$tG4\u007f\u0011+L\f'y\u0015{1Jtj\u0013F";
      c[23] = "\u000f\"V3m!I;Kp\r [>DfJ02\"]bh.B>\u0001z\rcJ5\u0001kr%S(B\u000b";
      c[24] = "8ZZ7/T~CGtOJc^kf+Vh\"Mk&NuRQ7>+";
      c[25] = "rdK\u001a[^\u007f7\u0015~_\u0001N?\u0014\u0010W\u001fv`\u0014~H\u0001z<\u0005\u000eT]bY\r\u001aY\u0000c)\u0011FAe)5\u0014C\r\u0018of\u0007E0";
      c[26] = "^?/\"1\u0015SlqF5Jbdp(=TZ>\u007fF\"JVga6>\u0016N\u0002()eL\u0007sn!cC?;~y8\u0016N}v\u007f7.\u0006m.$b_@e(+Z\u0017P=s~+QX;|F`B^?,;&\u0011M9\u0011";
      c[27] = "tg*K\u0017U9*?M&D(j\f\u0014[FEn\"\u0019C[5r~\u0001&";
      c[28] = "]e\u0016F`AP6H\"d\u001ea>ILl\u0000YcF\"s\u001eU=XRoBMX\u0011M4\u0018\u0004)WE2\u0017<aG\u001diBM'O\u001bfz\u00057\u0017@3\u000bC?\u0011O\u000b@P9\u0015\u001fv\u0006\u0003*\u0013\"";
      c[29] = "\u0014>\f\"zh\u0019mRF~7(eS(v)\u00109\\Fi7\u001cfB6uk\u0004\u0003\u000b).1MrM!(>u:]ysk\u0004|U\u007f|SOoS{,.\t<@}\u0011";
      c[30] = "@\u0005\u0001V0TMV_24\u000bnUCo=\u000eO_EW\u001f\bJQCS<\t!@[[>\u001fQ\\\u0007C[QL\u0002[\b*PS[V2#\u000bH]OB?WP8";
      c[31] = "S\u0019\u0015\u00025~^JKf1!oBJ\b9?WaB\u001a$$B\u001fE\u0018^=VMN\u0016.!\nU+Z3\u007fX\u0014A\b> V$A^#x\u0002VQ\u001a2.2\u001eG\u0007cxOX\u0014\u0014eE";
      c[32] = "G:q?XYJi/[\\\u0006{a.5T\u0018C=.[K\u0006Ob?+WZW\u00077?Z\u0007Vw+cBb^c&>C\u0012B?>[\t\u000eG:r&O]T<O";
      c[33] = "kiUv&\u0018f:\u000b\u0012\"GW2\n|*Yo\u0011\u0002n7Bzn\u0005lM[n=\u000eb=G2%k. \u0019`d\u0001|-FnT\u0001*0\u001e:&\u0011n!H\nn\u0007sp\u001ew(T`v#";
      c[34] = "oK!O_IjO\u007f\\!_TK{\u001bCX6\u0016{GG6,\u0013.EQF0O6 ";
      c[35] = "M\u0018.8LX\u0000U;>}I\u0011\u0015\u000bg\u0005F\u0015i:g\u0014C\f\u0019&;\f&";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0453 a(int var0) {
try {

      return new S0453(var0);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int a(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (d[var4] != null) {
         return var4;
      }

      Object var5 = c[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 56;
         case 1 -> 5;
         case 2 -> 22;
         case 3 -> 54;
         case 4 -> 46;
         case 5 -> 15;
         case 6 -> 36;
         case 7 -> 33;
         case 8 -> 63;
         case 9 -> 50;
         case 10 -> 37;
         case 11 -> 29;
         case 12 -> 62;
         case 13 -> 3;
         case 14 -> 51;
         case 15 -> 14;
         case 16 -> 8;
         case 17 -> 45;
         case 18 -> 38;
         case 19 -> 23;
         case 20 -> 19;
         case 21 -> 6;
         case 22 -> 1;
         case 23 -> 42;
         case 24 -> 61;
         case 25 -> 16;
         case 26 -> 30;
         case 27 -> 11;
         case 28 -> 27;
         case 29 -> 48;
         case 30 -> 52;
         case 31 -> 25;
         case 32 -> 60;
         case 33 -> 58;
         case 34 -> 55;
         case 35 -> 34;
         case 36 -> 26;
         case 37 -> 9;
         case 38 -> 17;
         case 39 -> 4;
         case 40 -> 7;
         case 41 -> 49;
         case 42 -> 10;
         case 43 -> 40;
         case 44 -> 2;
         case 45 -> 28;
         case 46 -> 0;
         case 47 -> 43;
         case 48 -> 53;
         case 49 -> 35;
         case 50 -> 39;
         case 51 -> 44;
         case 52 -> 47;
         case 53 -> 31;
         case 54 -> 59;
         case 55 -> 12;
         case 56 -> 32;
         case 57 -> 41;
         case 58 -> 13;
         case 59 -> 57;
         case 60 -> 20;
         case 61 -> 24;
         case 62 -> 21;
         default -> 18;
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

      d[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$41().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/gJ" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'Z' && var8 != 214 && var8 != 'F' && var8 != 219) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 163) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 234) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'Z') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 214) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'F') {
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

   public static Method a(Class var0, String var1, Class var2, int var3, Class[] var4) {
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

   public static Object a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = a(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$42(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field a(Class var0, String var1, Class var2) {
try {

      for (Field var6 : var0.getDeclaredFields()) {
         if (var6.getName().equals(var1) && var6.getType() == var2) {
            return var6;
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Map lambda$getLocation$0(Integer var0) {
try {

      return new HashMap();
   
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

   public static native boolean __indy$0(S0453 var0, Object var1) throws Throwable;

   public static native String __indy$1(S0453 var0) throws Throwable;

   public static native int __indy$2(S0453 var0) throws Throwable;

   public static native int __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$4(int var0, boolean var1, Object var2, long var3, long var5) throws Throwable;

   public static native int __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$6(int var0, float var1, float var2, float var3, float var4, long var5, long var7) throws Throwable;

   public static native int __indy$7(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$8(int var0, boolean var1, Object var2, long var3, long var5) throws Throwable;

   public static native int __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$10(int var0, boolean var1, Object var2, long var3, long var5) throws Throwable;

   public static native int __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$12(int var0, int var1, int var2, int var3, long var4, long var6) throws Throwable;

   public static native int __indy$13(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$14(int var0, float var1, float var2, float var3, long var4, long var6) throws Throwable;

   public static native int __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$16(int var0, float var1, float var2, long var3, long var5) throws Throwable;

   public static native int __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$18(int var0, float var1, long var2, long var4) throws Throwable;

   public static native int __indy$19(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$20(int var0, int var1, int var2, int var3, int var4, long var5, long var7) throws Throwable;

   public static native int __indy$21(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$22(int var0, float var1, float var2, float var3, float var4, long var5, long var7) throws Throwable;

   public static native Integer __indy$23(int var0, long var1, long var3) throws Throwable;

   public static native Function __indy$24() throws Throwable;

   public static native Object __indy$25(Object var0, Object var1, Object var2, long var3, long var5) throws Throwable;

   public static native Function __indy$26(S0453 var0) throws Throwable;

   public static native Object __indy$27(Object var0, Object var1, Object var2, long var3, long var5) throws Throwable;

   public static native int __indy$28(Object var0, long var1, long var3) throws Throwable;

   public static native int __indy$29(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$30(int var0, int var1, long var2, long var4) throws Throwable;

   public static native int __indy$31(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native void __indy$32(int var0, int var1, int var2, long var3, long var5) throws Throwable;

   public static native int __indy$33(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native int __indy$34(Object var0, long var1, long var3) throws Throwable;

   public static native int __indy$35(Object var0, long var1, long var3) throws Throwable;

   public static native int __indy$36(Object var0, long var1, long var3) throws Throwable;

   public static native int __indy$37(Object var0, long var1, long var3) throws Throwable;

   public static native void __indy$38(int var0, float var1, float var2, float var3, float var4, long var5, long var7) throws Throwable;

   public static native int __indy$39(int var0, Object var1, long var2, long var4) throws Throwable;

   public static native Integer __indy$40(int var0, long var1, long var3) throws Throwable;

   public static native MethodHandle __indy$41() throws Throwable;

   public static native Object __indy$42(MethodHandle var0, Object[] var1) throws Throwable;

private static void __flow$0$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(9044625937805726149L,2);f.pc=1;return;}
case 1:{f.push(-3353409769685717328L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(38353154285064L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0454.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(36,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0454.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(36,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0454.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0454.a$return$9b75();f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.util.HashMap.class),1);f.pc=18;return;}
case 18:{f.rearrange(89);f.pc=19;return;}
case 19:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=20;return;}
case 20:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0454.class,"a",((java.util.Map)(Object)(f.pop())));f.pc=21;return;}
case 21:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
