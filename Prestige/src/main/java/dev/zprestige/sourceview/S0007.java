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
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1927;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_1927.class_4179;

public class S0007 {
   public class_1927 a;
   public static long b;
   public static String[] c;
   public static String[] d;
   public static Map e;
   public static Object[] f;
   public static String[] g;

   public S0007(class_1927 var1)  throws Throwable {
try {

      this.a = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(11,10);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
case 1:__flow$1$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public void e$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      long var4 = (Long)(Object)(var1[1]);
      var4 = b ^ var4;
      long var6 = var4 ^ 36063296705841L;
      long var8 = var4 ^ 25917143023925L;
      S0120 var10 = __indy$1(new Object[]{this.a, "z", class_1927.class, __indy$0(4150407065331695909L, var4), var8}, 4150960288479926235L, var4);
      __indy$3(var10, new Object[]{__indy$2(var2, 4151033609066388447L, var4), var6}, 4150611464613032407L, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void i$return$e4b0899c(Object[] var1) {
try {

      class_243 var5 = (class_243)(Object)(var1[0]);
      float var6 = (Float)(Object)(var1[1]);
      boolean var4 = (Boolean)(Object)(var1[2]);
      long var2 = (Long)(Object)(var1[3]);
      var2 = b ^ var2;
      long var7 = var2 ^ 120432194133530L;
      long var9 = var2 ^ 138514694075985L;
      long var11 = var2 ^ 76808936693450L;
      long var13 = var2 ^ 108627999394630L;
      long var15 = var2 ^ 86631905863660L;
      long var17 = var2 ^ 73870138408426L;
      long var19 = var2 ^ 69979739984568L;
      long var21 = var2 ^ 8032153262228L;
      __indy$6(this, new Object[]{__indy$5(__indy$4(4798881842071431511L, var2), 4798961084203525520L, var2), var15}, 4798029299803230085L, var2);
      __indy$7(this, new Object[]{null, var7}, 4798427083134415495L, var2);
      double var10001 = __indy$8(var5, 4797894223269528931L, var2);
      Object[] var10004 = new Object[]{null, var13};
      var10004[0] = var10001;
      __indy$9(this, var10004, 4797620319198120374L, var2);
      var10001 = __indy$10(var5, 4797995273728905697L, var2);
      var10004 = new Object[]{null, var9};
      var10004[0] = var10001;
      __indy$11(this, var10004, 4797488647490284403L, var2);
      var10001 = __indy$12(var5, 4799091600194502264L, var2);
      var10004 = new Object[]{null, var21};
      var10004[0] = var10001;
      __indy$13(this, var10004, 4798219979721709983L, var2);
      var10004 = new Object[]{null, var19};
      var10004[0] = var6;
      __indy$14(this, var10004, 4798152175418031302L, var2);
      var10004 = new Object[]{null, var11};
      var10004[0] = var4;
      __indy$15(this, var10004, 4797564547710095541L, var2);
      __indy$17(this, new Object[]{__indy$16(4796992581624038878L, var2), var17}, 4797671181517394095L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite b(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$18().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/G" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
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

   public static Class b(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = a(var0, var2);
      Object var6 = f[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(g[var4]);
            f[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void b$return$e4b0899c(Object[] var1) {
try {

      class_1297 var2 = (class_1297)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = b ^ var3;
      long var5 = var3 ^ 81410241041343L;
      long var7 = var3 ^ 137800294352827L;
      S0120 var9 = __indy$20(
         new Object[]{this.a, __indy$19(4571, 5936642425479660125L ^ var3), class_1927.class, class_1297.class, var7}, -66068941551988395L, var3
      );
      __indy$21(var9, new Object[]{var2, var5}, -65436452264167591L, var3);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field c(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = f[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = g[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            f[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  f[var4] = var13;
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
            var12 = b(479330021029275L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void c$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      long var4 = (Long)(Object)(var1[1]);
      var4 = b ^ var4;
      long var6 = var4 ^ 76181697726179L;
      long var8 = var4 ^ 125445306569447L;
      S0120 var10 = __indy$23(new Object[]{this.a, "x", class_1927.class, __indy$22(7659304349971731191L, var4), var8}, 7658733895448003593L, var4);
      __indy$25(var10, new Object[]{__indy$24(var2, 7658664284102674445L, var4), var6}, 7659368041407914501L, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void h$return$e4b0899c(Object[] var1) {
try {

      class_4179 var2 = (class_4179)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = b ^ var3;
      long var5 = var3 ^ 110697296097359L;
      long var7 = var3 ^ 92304566461515L;
      S0120 var9 = __indy$27(
         new Object[]{this.a, __indy$26(16201, 3080404575937417018L ^ var3), class_1927.class, class_4179.class, var7}, -7141187273551842651L, var3
      );
      __indy$28(var9, new Object[]{var2, var5}, -7140550376598285143L, var3);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void f$return$e4b0899c(Object[] var1) {
try {

      float var4 = (Float)(Object)(var1[0]);
      long var2 = (Long)(Object)(var1[1]);
      var2 = b ^ var2;
      long var5 = var2 ^ 26559806316317L;
      long var7 = var2 ^ 52196774628121L;
      S0120 var9 = __indy$31(
         new Object[]{this.a, __indy$29(24678, 8157141243247591235L ^ var2), class_1927.class, __indy$30(8337567997267861284L, var2), var7},
         8338155813536669175L,
         var2
      );
      __indy$33(var9, new Object[]{__indy$32(var4, 8339256196878449248L, var2), var5}, 8337807545863453691L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = f[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = g[var4];
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
            f[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(479330021029275L, 0L);
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
                  f[var4] = var19;
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
            var23 = b(479330021029275L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void d$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      long var4 = (Long)(Object)(var1[1]);
      var4 = b ^ var4;
      long var6 = var4 ^ 99475561118708L;
      long var8 = var4 ^ 120010389073904L;
      S0120 var10 = __indy$35(new Object[]{this.a, "y", class_1927.class, __indy$34(-7540064229972725792L, var4), var8}, -7539493240842487522L, var4);
      __indy$37(var10, new Object[]{__indy$36(var2, -7539564917798816486L, var4), var6}, -7539987202325160174L, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
try {

      class_1937 var2 = (class_1937)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = b ^ var3;
      long var5 = var3 ^ 115758187194953L;
      long var7 = var3 ^ 103452345073229L;
      S0120 var9 = __indy$39(
         new Object[]{this.a, __indy$38(24205, 5883708569426883833L ^ var3), class_1927.class, class_1937.class, var7}, -4115333591376549725L, var3
      );
      __indy$40(var9, new Object[]{var2, var5}, -4115825350620901713L, var3);
   
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

   public class_1282 a$return$2608793(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = b ^ var2;
      long var4 = var2 ^ 135232027309881L;
      long var6 = var2 ^ 17003289565903L;
      S0120 var8 = __indy$42(
         new Object[]{this.a, __indy$41(20823, 8332812616018255782L ^ var2), class_1927.class, class_1282.class, var6}, 7089005043194203169L, var2
      );
      return (class_1282)(Object)(__indy$43(var8, new Object[]{var4}, 7092475438925382892L, var2));
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = a(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$44(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'b' && var8 != 164 && var8 != 208 && var8 != 'y') {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'W') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 211) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'b') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 164) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 208) {
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

   public static int a(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (g[var4] != null) {
         return var4;
      }

      Object var5 = f[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 5;
         case 1 -> 0;
         case 2 -> 56;
         case 3 -> 35;
         case 4 -> 20;
         case 5 -> 57;
         case 6 -> 2;
         case 7 -> 43;
         case 8 -> 8;
         case 9 -> 41;
         case 10 -> 19;
         case 11 -> 10;
         case 12 -> 34;
         case 13 -> 52;
         case 14 -> 28;
         case 15 -> 9;
         case 16 -> 48;
         case 17 -> 49;
         case 18 -> 50;
         case 19 -> 36;
         case 20 -> 44;
         case 21 -> 4;
         case 22 -> 26;
         case 23 -> 42;
         case 24 -> 30;
         case 25 -> 7;
         case 26 -> 13;
         case 27 -> 45;
         case 28 -> 60;
         case 29 -> 58;
         case 30 -> 22;
         case 31 -> 23;
         case 32 -> 11;
         case 33 -> 40;
         case 34 -> 24;
         case 35 -> 32;
         case 36 -> 55;
         case 37 -> 12;
         case 38 -> 6;
         case 39 -> 14;
         case 40 -> 63;
         case 41 -> 38;
         case 42 -> 29;
         case 43 -> 46;
         case 44 -> 47;
         case 45 -> 18;
         case 46 -> 17;
         case 47 -> 1;
         case 48 -> 3;
         case 49 -> 53;
         case 50 -> 62;
         case 51 -> 25;
         case 52 -> 15;
         case 53 -> 51;
         case 54 -> 27;
         case 55 -> 59;
         case 56 -> 37;
         case 57 -> 33;
         case 58 -> 16;
         case 59 -> 31;
         case 60 -> 54;
         case 61 -> 21;
         case 62 -> 39;
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

      g[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$45().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/G" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static String a(int var0, long var1) {
try {

      int var5 = var0 ^ (int)(var1 & 32767L) ^ 27800;
      if (d[var5] == null) {
         Object[] var4;
         try {
            Long var3 = Thread.currentThread().threadId();
            var4 = (Object[])(Object)(e.get(var3));
            if (var4 == null) {
               var4 = new Object[]{Cipher.getInstance("DES/CBC/PKCS5Padding"), SecretKeyFactory.getInstance("DES"), new IvParameterSpec(new byte[8])};
               e.put(var3, var4);
            }
         } catch (Exception var10) {
            throw new RuntimeException("dev/zprestige/prestige/G", var10);
         }

         byte[] var6 = new byte[8];
         var6[0] = (byte)(var1 >>> 56);

         for (int var7 = 1; var7 < 8; var7++) {
            var6[var7] = (byte)(var1 << var7 * 8 >>> 56);
         }

         DESKeySpec var11 = new DESKeySpec(var6);
         SecretKey var8 = ((SecretKeyFactory)(Object)(var4[1])).generateSecret(var11);
         ((Cipher)(Object)(var4[0])).init(2, var8, (IvParameterSpec)(Object)(var4[2]));
         byte[] var9 = c[var5].getBytes("ISO-8859-1");
         d[var5] = a(((Cipher)(Object)(var4[0])).doFinal(var9));
      }

      return d[var5];
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static String a(byte[] var0) {
try {

      int var1 = 0;
      int var2;
      char[] var3 = new char[var2 = var0.length];

      for (int var4 = 0; var4 < var2; var4++) {
         int var5;
         if ((var5 = 255 & var0[var4]) < 192) {
            var3[var1++] = (char)var5;
         } else if (var5 < 224) {
            char var6 = (char)((char)(var5 & 31) << 6);
            byte var8 = var0[++var4];
            var6 = (char)(var6 | (char)(var8 & 63));
            var3[var1++] = var6;
         } else if (var4 < var2 - 2) {
            char var12 = (char)((char)(var5 & 15) << '\f');
            byte var9 = var0[++var4];
            var12 = (char)(var12 | (char)(var9 & 63) << 6);
            var9 = var0[++var4];
            var12 = (char)(var12 | (char)(var9 & 63));
            var3[var1++] = var12;
         }
      }

      return new String(var3, 0, var1);
   
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

   public static Object a(Lookup var0, MutableCallSite var1, String var2, Object[] var3) {
try {

      int var4 = (Integer)(Object)(var3[0]);
      long var5 = (Long)(Object)(var3[1]);
      String var7 = a(var4, var5);
      MethodHandle var8 = MethodHandles.constant(String.class, var7);
      var1.setTarget(MethodHandles.dropArguments(var8, 0, int.class, long.class));
      return var7;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      f[0] = "\u001dV{5P6\u000bV~oC!\u001c\u001d}iO5\rZj~\u0004$(";
      f[1] = "\\$F\u0016/.)\u0004M\u0019>aH\nF\u0012:;<";
      f[2] = void.class;
      g[2] = "java/lang/Void";
      f[3] = "'8JLDd18O\u0016Ws&sL\u0010[g74[\u0007\u0010w\u0015";
      f[4] = "'kq\r5\u007fRKz\u0002$03Eq\t jG";
      f[5] = ">Rb3%qKri<4>*|b70d^";
      f[6] = "%\u001dx8\u0012\u0000.\u0012iws\u000e%\u0019m-";
      f[7] = "\tO3pDK\u0002@\"?.H\u0016L)t";
      f[8] = "\"0jJl\f)?{\u0005\u0001\f)\"o";
      f[9] = double.class;
      g[9] = "java/lang/Double";
      f[10] = "EeCu1\u000fNjR:Y\u000f@eA";
      f[11] = float.class;
      g[11] = "java/lang/Float";
      f[12] = "wnC\u0001%\f|aRNI\u000frcP\u0001e";
      f[13] = boolean.class;
      g[13] = "java/lang/Boolean";
      f[14] = "(<|\u001e\\-(<kBP\"2wk\\P75\u0006;\u0001\u0001";
      f[15] = "\u007f>]\u000b\u000f\u0011\u007f>JW\u0003\u001eeuJI\u0003\u000bb\u0004\u001f\u0016Z";
      f[16] = "4\u001ag\b\u0003z4\u001apT\u000fu.QpJ\u000f`) !\u0012]";
      f[17] = "\u001e94\t\"\u0017\b91S1\u0000\u001fr2U=\u0014\u000e5%Bv ";
      f[18] = "\n\u001d.-\nQ\u007f=%\"\u001b\u001e\u001e3.)\u001fDj";
      f[19] = ">\u0003%r(VK#.}9\u0019*-%v=C^";
      f[20] = "xnqLG\u0004\rNzCVKl@qHR\u0011\u0018";
      f[21] = "xWh\"[Y\rwc-J\u0016lyh&NL\u0018";
      f[22] = ">\\\u001aX\u000eOK|\u0011W\u001f\u0000*r\u001a\\\u001bZ^";
      f[23] = "ij#\u0007\u007fO\u001cJ(\bn\u0000}D#\u0003jZ\t";
      f[24] = "s\\$\nE=\u0006|/\u0005Trgr$\u000eP(\u0013";
      f[25] = "\u0015:=R*|`\u001a6];3\u0001\u0014=V?iu";
      f[26] = "[\u0018\u007f2\u0011\u0016[\u0018hn\u001d\u0019AShp\u001d\fF\":%NH\u0011\u001eg}\u000f\fjI:+E";
      f[27] = "&N\">\u0011E)\u0019~\"h_p\u001bd?/O\u0019@|*\u0014\u001c'\u0015y9\u0017!&N\">\u0011E)\u0019~\"h";
      f[28] = "MC\u001f:-4\u0005IMgF-\u0011NG<*\u001fE\u000f\u001abFy\r\nF0:1\u0007X\u001b[";
      f[29] = "M%\u001c&$s\u0005/N{Oa\u001d9@+\u00186Bd\u001bG&~\u0003kB<)sCm";
      f[30] = "_\u00175\u0000!B\u001a\u0017=p#s\u0002M/\u001c(NR\u001e<\u0010JO\u001aP=Nt\u0017^\u0017?p";
      f[31] = "g\fADV\u001es\n\u001eC4Ol\u0001\u001b\u0013c\u00186QG\u007f\nFs\u0013\u0003\u0007]Pn\n";
      f[32] = "6[}\u0003U\u0001/^`Bn\u0006]\u0004fW\u0002\r`T5D\u000eod\u0019u[\u0012\u0000b\t<On";
      f[33] = "#1uRxA:4h\u0013CAHnn\u0006/Mu>=\u0015#/qs}\n?@wc4\u001eC";
      f[34] = "Ll2~\u0018\u0010Ui/?#\u0013'3)*O\u001c\u001acz9C~\u001e.:&_\u0011\u0018>s2#";
      f[35] = "\u0017TqWW\t\u0018\u0003-K.1y=\u0007;\u0013\u0013V\u0005{K@\u001fI\u0007";
      f[36] = "\u001e\\#Y2}\u001c\fv]@Xrn\n?}z]VvO.vBT";
      f[37] = "}Ars\u0003\u000fdDo28\u000b\u0016\u001ei'T\u0003+N:4Xa/\u0003z+D\u000e)\u00133?8";
      f[38] = "nfpAe=$:\"FU\u0004\u000fX\u0001(h& `}X;*?b";
      f[39] = "o`(\u001403*` d2\u00022:2\b9?bi!\u0004[;/)>\u00184=?`*d";
      f[40] = "h14.s<q4)oH>\u0003n/z$0>>|i(R:s<v4=<cubH";
      f[41] = "qY\u00138D\u0018h\\\u000ey\u007f\u0019\u001a\u0006\bl\u0013\u0014'V[\u007f\u001fv#\u001b\u001b`\u0003\u0019%\u000bRt\u007f";
      f[42] = "$Z?\\d\u000f=_\"\u001d_\nO\u0005$\b3\u0003rUw\u001b?av\u00187\u0004#\u000ep\b~\u0010_";
      f[43] = "K,y!]\u001dR)d`f\u0013 sbu\n\u0011\u001d#1f\u0006s\u0019nqy\u001a\u001c\u001f~8mf";
      f[44] = "Dbt@#.\u001e{x\u000e[ ~zg\u00167(C*4\u0005;JC\"u\f0{\u0006\"}|";
      f[45] = "O\u001bW?WIMK\u0002;%N\u001b\u0015N4b^rOT'ZL\n\u0018B:C0O\u001bW?WIMK\u0002;%";
      f[46] = "s\u0007?/\u0014[g\u0001`(v\nx\nex!]\"Z;\u0014H\u0003g\u0018}l\u001f\u0015z\u0001";
      f[47] = "\u0011$8\u001a\u0019{\u0005\"g\u001d{*\u001a)bM,}@y?!E#\u0005;zY\u00125\u0018\"";
      f[48] = "(\u0017np=3bK<w\r(q\u0015/tJ8\u0018@9r6)\u007fMjv5V(\u0017np=3bK<w\r";
      f[49] = "t<\u000fj3\n,j\u0016h\u000f_t8\u000fxX\b%k[+\u000fP|1\u0015(4\b*(\u0017";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void g$return$e4b0899c(Object[] var1) {
try {

      boolean var2 = (Boolean)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = b ^ var3;
      long var5 = var3 ^ 108138606498671L;
      long var7 = var3 ^ 93488936765291L;
      S0120 var9 = __indy$48(
         new Object[]{this.a, __indy$46(12247, 506678240455343235L ^ var3), class_1927.class, __indy$47(9207585484307237039L, var3), var7},
         9206887019951559045L,
         var3
      );
      __indy$50(var9, new Object[]{__indy$49(var2, 9206010339129621165L, var3), var5}, 9207518966890313609L, var3);
   
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

   private static native Class __indy$0(long var0, long var2) throws Throwable;

   private static native S0120 __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native Double __indy$2(double var0, long var2, long var4) throws Throwable;

   private static native void __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_310 __indy$4(long var0, long var2) throws Throwable;

   private static native class_638 __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$7(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$13(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$14(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_4179 __indy$16(long var0, long var2) throws Throwable;

   private static native void __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$18() throws Throwable;

   private static native String __indy$19(int var0, long var1) throws Throwable;

   private static native S0120 __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$21(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Class __indy$22(long var0, long var2) throws Throwable;

   private static native S0120 __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native Double __indy$24(double var0, long var2, long var4) throws Throwable;

   private static native void __indy$25(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String __indy$26(int var0, long var1) throws Throwable;

   private static native S0120 __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$28(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String __indy$29(int var0, long var1) throws Throwable;

   private static native Class __indy$30(long var0, long var2) throws Throwable;

   private static native S0120 __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native Float __indy$32(float var0, long var1, long var3) throws Throwable;

   private static native void __indy$33(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Class __indy$34(long var0, long var2) throws Throwable;

   private static native S0120 __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native Double __indy$36(double var0, long var2, long var4) throws Throwable;

   private static native void __indy$37(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String __indy$38(int var0, long var1) throws Throwable;

   private static native S0120 __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$40(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String __indy$41(int var0, long var1) throws Throwable;

   private static native S0120 __indy$42(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$43(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$44(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$45() throws Throwable;

   private static native String __indy$46(int var0, long var1) throws Throwable;

   private static native Class __indy$47(long var0, long var2) throws Throwable;

   private static native S0120 __indy$48(Object var0, long var1, long var3) throws Throwable;

   private static native Boolean __indy$49(boolean var0, long var1, long var3) throws Throwable;

   private static native void __indy$50(Object var0, Object var1, long var2, long var4) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(6706297564401998235L,2);f.pc=1;return;}
case 1:{f.push(-539965283059304021L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(10440241609357L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0007.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(50,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0007.class,"f",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(50,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0007.class,"g",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0007.a$return$9b75();f.pc=15;return;}
case 15:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.util.HashMap.class),1);f.pc=16;return;}
case 16:{f.rearrange(89);f.pc=17;return;}
case 17:{f.push(13,1);f.pc=18;return;}
case 18:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{int.class},new Object[]{dev.zprestige.sourceview.FlowFrame.i(a0)});f.pc=19;return;}
case 19:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0007.class,"e",((java.util.Map)(Object)(f.pop())));f.pc=20;return;}
case 20:{f.push(dev.zprestige.sourceview.S0007.b,2);f.pc=21;return;}
case 21:{f.push(132742601947382L,2);f.pc=22;return;}
case 22:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=23;return;}
case 23:{f.locals[0]=f.pop();f.pc=24;return;}
case 24:{f.push("DES/CBC/PKCS5Padding",1);f.pc=25;return;}
case 25:{Object a0=f.pop();f.push(javax.crypto.Cipher.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=26;return;}
case 26:{f.rearrange(89);f.pc=27;return;}
case 27:{f.locals[2]=f.pop();f.pc=28;return;}
case 28:{f.push(2,1);f.pc=29;return;}
case 29:{f.push("DES",1);f.pc=30;return;}
case 30:{Object a0=f.pop();f.push(javax.crypto.SecretKeyFactory.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=31;return;}
case 31:{f.push(8,1);f.pc=32;return;}
case 32:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=33;return;}
case 33:{f.rearrange(89);f.pc=34;return;}
case 34:{f.push(0,1);f.pc=35;return;}
case 35:{f.push(f.locals[0],2);f.pc=36;return;}
case 36:{f.push(56,1);f.pc=37;return;}
case 37:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=38;return;}
case 38:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=39;return;}
case 39:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=40;return;}
case 40:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=41;return;}
case 41:{f.push(1,1);f.pc=42;return;}
case 42:{f.locals[3]=f.pop();f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.push(f.locals[3],1);f.pc=46;return;}
case 46:{f.push(8,1);f.pc=47;return;}
case 47:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=62;return;}f.pc=48;return;}
case 48:{f.rearrange(89);f.pc=49;return;}
case 49:{f.push(f.locals[3],1);f.pc=50;return;}
case 50:{f.push(f.locals[0],2);f.pc=51;return;}
case 51:{f.push(f.locals[3],1);f.pc=52;return;}
case 52:{f.push(8,1);f.pc=53;return;}
case 53:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a*b,1);f.pc=54;return;}
case 54:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a<<b,2);f.pc=55;return;}
case 55:{f.push(56,1);f.pc=56;return;}
case 56:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=57;return;}
case 57:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=58;return;}
case 58:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=59;return;}
case 59:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=60;return;}
case 60:{f.locals[3]=dev.zprestige.sourceview.FlowFrame.i(f.locals[3])+(1);f.pc=61;return;}
case 61:{f.pc=43;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.DESKeySpec.class),1);f.pc=65;return;}
case 65:{f.rearrange(90);f.pc=66;return;}
case 66:{f.rearrange(95);f.pc=67;return;}
case 67:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=68;return;}
case 68:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.SecretKeyFactory)(Object)receiver).generateSecret(((java.security.spec.KeySpec)(Object)(a0))),1);f.pc=69;return;}
case 69:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.IvParameterSpec.class),1);f.pc=70;return;}
case 70:{f.rearrange(89);f.pc=71;return;}
case 71:{f.push(8,1);f.pc=72;return;}
case 72:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=73;return;}
case 73:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=74;return;}
case 74:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();((javax.crypto.Cipher)(Object)receiver).init(dev.zprestige.sourceview.FlowFrame.i(a0),((java.security.Key)(Object)(a1)),((java.security.spec.AlgorithmParameterSpec)(Object)(a2)));f.pc=75;return;}
case 75:{f.push(6,1);f.pc=76;return;}
case 76:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=77;return;}
case 77:{f.locals[9]=f.pop();f.pc=78;return;}
case 78:{f.push(0,1);f.pc=79;return;}
case 79:{f.locals[7]=f.pop();f.pc=80;return;}
case 80:{f.push("\u00998\u00e1\u00c2`\u0082'@\u000fS\u0015@\u000ep\u0016\u0088\u00109-z>\u0088\u00deL.\u001e\u001b\u00f8[F\u009ar\u00e1\u0018\u0094\u00e3\u00e9;\u000bd\u00bf\u00a8\u00be\u00da^\u00e0\u00cc\u00e7J\u00fe\u00ce\u00de\u008eQ\u008f\u00e1\u00e5Y k\u000c\u00d7\u0098\u00cd\u0095\u000cY\u000f\u00c0\u009a\u00d7iDc\u0009\u00c1\u00feSI\u009c\u00b4\u00be\u0081\u00ed\u0097\u0019\u00d1\u001f\u00aa@",1);f.pc=81;return;}
case 81:{f.rearrange(89);f.pc=82;return;}
case 82:{f.locals[6]=f.pop();f.pc=83;return;}
case 83:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=84;return;}
case 84:{f.locals[8]=f.pop();f.pc=85;return;}
case 85:{f.push(16,1);f.pc=86;return;}
case 86:{f.locals[5]=f.pop();f.pc=87;return;}
case 87:{f.push(-1,1);f.pc=88;return;}
case 88:{f.locals[4]=f.pop();f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.locals[4]=dev.zprestige.sourceview.FlowFrame.i(f.locals[4])+(1);f.pc=92;return;}
case 92:{f.push(f.locals[6],1);f.pc=93;return;}
case 93:{f.push(f.locals[4],1);f.pc=94;return;}
case 94:{f.rearrange(89);f.pc=95;return;}
case 95:{f.push(f.locals[5],1);f.pc=96;return;}
case 96:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=97;return;}
case 97:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).substring(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1)),1);f.pc=98;return;}
case 98:{f.push(-1,1);f.pc=99;return;}
case 99:{f.pc=170;return;}
case 100:{f.pc=101;return;}
case 101:{f.pc=102;return;}
case 102:{f.push(f.locals[9],1);f.pc=103;return;}
case 103:{f.rearrange(95);f.pc=104;return;}
case 104:{f.push(f.locals[7],1);f.pc=105;return;}
case 105:{f.locals[7]=dev.zprestige.sourceview.FlowFrame.i(f.locals[7])+(1);f.pc=106;return;}
case 106:{f.rearrange(95);f.pc=107;return;}
case 107:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=108;return;}
case 108:{f.push(f.locals[4],1);f.pc=109;return;}
case 109:{f.push(f.locals[5],1);f.pc=110;return;}
case 110:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=111;return;}
case 111:{f.rearrange(89);f.pc=112;return;}
case 112:{f.locals[4]=f.pop();f.pc=113;return;}
case 113:{f.push(f.locals[8],1);f.pc=114;return;}
case 114:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=120;return;}f.pc=115;return;}
case 115:{f.push(f.locals[6],1);f.pc=116;return;}
case 116:{f.push(f.locals[4],1);f.pc=117;return;}
case 117:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).charAt(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=118;return;}
case 118:{f.locals[5]=f.pop();f.pc=119;return;}
case 119:{f.pc=89;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.push("\u00e4\u0017\u0007bE\u00ab\u00b5_\u00b7\u00ae%\u00c6\u00a2\\\u001a$ \u00b9\u00cd\u00d0\u00b0\u00d2\u00b2\u00b9\u00f0g\u0010\u0017\u00c2&\u00d4=\u00be\u0094qA\u0085\u0083k\u00bdCz\u00f9;\u00ae\u0090\u0001\u00cex",1);f.pc=123;return;}
case 123:{f.rearrange(89);f.pc=124;return;}
case 124:{f.locals[6]=f.pop();f.pc=125;return;}
case 125:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=126;return;}
case 126:{f.locals[8]=f.pop();f.pc=127;return;}
case 127:{f.push(16,1);f.pc=128;return;}
case 128:{f.locals[5]=f.pop();f.pc=129;return;}
case 129:{f.push(-1,1);f.pc=130;return;}
case 130:{f.locals[4]=f.pop();f.pc=131;return;}
case 131:{f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.locals[4]=dev.zprestige.sourceview.FlowFrame.i(f.locals[4])+(1);f.pc=134;return;}
case 134:{f.push(f.locals[6],1);f.pc=135;return;}
case 135:{f.push(f.locals[4],1);f.pc=136;return;}
case 136:{f.rearrange(89);f.pc=137;return;}
case 137:{f.push(f.locals[5],1);f.pc=138;return;}
case 138:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=139;return;}
case 139:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).substring(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1)),1);f.pc=140;return;}
case 140:{f.push(0,1);f.pc=141;return;}
case 141:{f.pc=170;return;}
case 142:{f.pc=143;return;}
case 143:{f.pc=144;return;}
case 144:{f.push(f.locals[9],1);f.pc=145;return;}
case 145:{f.rearrange(95);f.pc=146;return;}
case 146:{f.push(f.locals[7],1);f.pc=147;return;}
case 147:{f.locals[7]=dev.zprestige.sourceview.FlowFrame.i(f.locals[7])+(1);f.pc=148;return;}
case 148:{f.rearrange(95);f.pc=149;return;}
case 149:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=150;return;}
case 150:{f.push(f.locals[4],1);f.pc=151;return;}
case 151:{f.push(f.locals[5],1);f.pc=152;return;}
case 152:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=153;return;}
case 153:{f.rearrange(89);f.pc=154;return;}
case 154:{f.locals[4]=f.pop();f.pc=155;return;}
case 155:{f.push(f.locals[8],1);f.pc=156;return;}
case 156:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=162;return;}f.pc=157;return;}
case 157:{f.push(f.locals[6],1);f.pc=158;return;}
case 158:{f.push(f.locals[4],1);f.pc=159;return;}
case 159:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).charAt(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=160;return;}
case 160:{f.locals[5]=f.pop();f.pc=161;return;}
case 161:{f.pc=131;return;}
case 162:{f.pc=163;return;}
case 163:{f.pc=164;return;}
case 164:{f.push(f.locals[9],1);f.pc=165;return;}
case 165:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0007.class,"c",((java.lang.String[])(Object)(f.pop())));f.pc=166;return;}
case 166:{f.push(6,1);f.pc=167;return;}
case 167:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=168;return;}
case 168:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0007.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=169;return;}
case 169:{f.pc=184;return;}
case 170:{f.pc=171;return;}
case 171:{f.pc=172;return;}
case 172:{f.rearrange(95);f.pc=173;return;}
case 173:{f.push("ISO-8859-1",1);f.pc=174;return;}
case 174:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).getBytes(((java.lang.String)(Object)(a0))),1);f.pc=175;return;}
case 175:{f.push(f.locals[2],1);f.pc=176;return;}
case 176:{f.rearrange(95);f.pc=177;return;}
case 177:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.Cipher)(Object)receiver).doFinal(((byte[])(Object)(a0))),1);f.pc=178;return;}
case 178:{f.locals[10]=f.pop();f.pc=179;return;}
case 179:{f.push(f.locals[10],1);f.pc=180;return;}
case 180:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0007.a(((byte[])(Object)(a0))),1);f.pc=181;return;}
case 181:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).intern(),1);f.pc=182;return;}
case 182:{f.rearrange(95);f.pc=183;return;}
case 183:{switch(dev.zprestige.sourceview.FlowFrame.i(f.pop())){case 0:f.pc=142;break;default:f.pc=100;}return;}
case 184:{f.pc=185;return;}
case 185:{f.pc=186;return;}
case 186:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
