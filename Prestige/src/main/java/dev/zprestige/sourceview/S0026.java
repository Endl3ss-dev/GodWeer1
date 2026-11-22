package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_3726;
import net.minecraft.class_3959;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class S0026 {
   public static S0121 a;
   public static S0121 b;
   public static S0121 c;
   public static S0121 d;
   public static S0121 e;
   public class_3959 f;
   public static long g;
   public static Object[] h;
   public static String[] i;

   public S0026(class_3959 var1)  throws Throwable {
try {

      this.f = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(13,10);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
case 1:__flow$1$1(f);break;
case 2:__flow$1$2(f);break;
case 3:__flow$1$3(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public void e$return$e4b0899c(Object[] var1) {
try {

      class_3726 var2 = (class_3726)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = g ^ var3;
      long var5 = var3 ^ 44372707042920L;
      __indy$5(e, new Object[]{this.f, var2, var5}, 5831399386608241678L, var3);
   
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
      Object var6 = h[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(i[var4]);
            h[var4] = var5;
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

      class_243 var4 = (class_243)(Object)(var1[0]);
      long var2 = (Long)(Object)(var1[1]);
      var2 = g ^ var2;
      long var5 = var2 ^ 110818825862992L;
      __indy$6(b, new Object[]{this.f, var4, var5}, 5320165448664749366L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field c(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = h[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = i[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            h[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  h[var4] = var13;
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
            var12 = b(934463513324175L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void c$return$e4b0899c(Object[] var1) {
try {

      class_3960 var4 = (class_3960)(Object)(var1[0]);
      long var2 = (Long)(Object)(var1[1]);
      var2 = g ^ var2;
      long var5 = var2 ^ 126506247994672L;
      __indy$7(c, new Object[]{this.f, var4, var5}, -8379772233245075626L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void f$return$e4b0899c(Object[] var1) {
try {

      class_243 var3 = (class_243)(Object)(var1[0]);
      class_243 var2 = (class_243)(Object)(var1[1]);
      class_3960 var8 = (class_3960)(Object)(var1[2]);
      class_242 var4 = (class_242)(Object)(var1[3]);
      class_1297 var5 = (class_1297)(Object)(var1[4]);
      long var6 = (Long)(Object)(var1[5]);
      var6 = g ^ var6;
      long var9 = var6 ^ 24220807918370L;
      long var11 = var6 ^ 11779660362290L;
      long var13 = var6 ^ 101718732711209L;
      long var15 = var6 ^ 90037529176426L;
      long var17 = var6 ^ 77137574341386L;
      __indy$8(this, new Object[]{var3, var9}, -3421948297685666488L, var6);
      __indy$9(this, new Object[]{var2, var17}, -3421078723521768379L, var6);
      __indy$10(this, new Object[]{var8, var15}, -3421287628131207358L, var6);
      __indy$11(this, new Object[]{var4, var13}, -3421164925092211109L, var6);
      __indy$13(this, new Object[]{__indy$12(var5, -3421978187941991680L, var6), var11}, -3421199816711250997L, var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = h[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = i[var4];
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
            h[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(934463513324175L, 0L);
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
                  h[var4] = var19;
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
            var23 = b(934463513324175L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void d$return$e4b0899c(Object[] var1) {
try {

      class_242 var2 = (class_242)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = g ^ var3;
      long var5 = var3 ^ 139014231716211L;
      __indy$14(d, new Object[]{this.f, var2, var5}, -4038008211647653099L, var3);
   
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

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'k' && var8 != 209 && var8 != 236 && var8 != 'G') {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'u') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'j') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'k') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 209) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 236) {
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

   public static Object a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = a(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$15(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$16().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/Y" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
try {

      class_243 var2 = (class_243)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = g ^ var3;
      long var5 = var3 ^ 58182339569528L;
      __indy$17(a, new Object[]{this.f, var2, var5}, 1296287278417136926L, var3);
   
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

   public static int a(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (i[var4] != null) {
         return var4;
      }

      Object var5 = h[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 36;
         case 1 -> 4;
         case 2 -> 12;
         case 3 -> 14;
         case 4 -> 17;
         case 5 -> 13;
         case 6 -> 58;
         case 7 -> 57;
         case 8 -> 10;
         case 9 -> 31;
         case 10 -> 56;
         case 11 -> 37;
         case 12 -> 61;
         case 13 -> 6;
         case 14 -> 30;
         case 15 -> 54;
         case 16 -> 63;
         case 17 -> 21;
         case 18 -> 19;
         case 19 -> 9;
         case 20 -> 52;
         case 21 -> 7;
         case 22 -> 55;
         case 23 -> 34;
         case 24 -> 39;
         case 25 -> 2;
         case 26 -> 53;
         case 27 -> 43;
         case 28 -> 44;
         case 29 -> 20;
         case 30 -> 11;
         case 31 -> 25;
         case 32 -> 1;
         case 33 -> 24;
         case 34 -> 8;
         case 35 -> 27;
         case 36 -> 3;
         case 37 -> 28;
         case 38 -> 26;
         case 39 -> 45;
         case 40 -> 16;
         case 41 -> 42;
         case 42 -> 23;
         case 43 -> 48;
         case 44 -> 46;
         case 45 -> 22;
         case 46 -> 15;
         case 47 -> 41;
         case 48 -> 5;
         case 49 -> 32;
         case 50 -> 40;
         case 51 -> 51;
         case 52 -> 59;
         case 53 -> 47;
         case 54 -> 35;
         case 55 -> 0;
         case 56 -> 49;
         case 57 -> 33;
         case 58 -> 18;
         case 59 -> 29;
         case 60 -> 60;
         case 61 -> 38;
         case 62 -> 62;
         default -> 50;
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

      i[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      h[0] = "\u0016\u0011`:wW\u0000\u0011e`d@\u0017ZffhT\u0006\u001dqq#E ";
      h[1] = ")!5BUp\\\u0001>MD?=\u000f5F@eI";
      h[2] = void.class;
      i[2] = "java/lang/Void";
      h[3] = "_\u0004-VvKI\u0004(\fe\\^O+\niHO\b<\u001d\"Xm";
      h[4] = "||%HHh\t\\.GY'hR%L]}\u001c";
      h[5] = "[qB\u0000iSMqGZzDZ:D\\vPK}SK=z";
      h[6] = "l`O@Q?\u0019@DO@pxNODD*\f";
      h[7] = "\fW,#\u001eGyw',\u000f\b\u0018y,'\u000bRl";
      h[8] = "%vb\\AtPViSP;1XbXTaE";
      h[9] = "{/}z\u0018z{/j&\u0014uadj8\u0014`f\u0015:cG%";
      h[10] = "\u0005Wz\u001dn0\u0005WmAb?\u001f\u001cm_b*\u0018m?\u0001:n";
      h[11] = "b\u0017\u000b\u001cKo\u00177\u0000\u0013Z v9\u000b\u0018^z\u0002";
      h[12] = "zjE9Fi\u000fJN6W&nDE=S|\u001a";
      h[13] = "bfL<\u000f7ii]sn9bbY)";
      h[14] = "z=+!du(;}w\u0016}Alw?mi1!w/&\u0014x-t'\u007fl$3}|\u0016";
      h[15] = "\rc\u0014^);L<\u001aKN=^.\u0015]\"\u000f\nlL\u000bsX\f=\u001aZ\u007f*Z0\u0016\u0005Nf\u00023\u0011]-']=\u0004:";
      h[16] = "|tX\u001f\rB.r\u000eI\u007fNG%\u0004\u0001\u0004^7h\u0004\u0011O#~d\u0007\u0019\u0016[\"z\u000eB\u007f";
      h[17] = "\r\u0006A\u0014AF_\u0000\u0017B3L6W\u001d\nHZF\u001a\u001d\u001a\u0003'\u000f\u0016\u001e\u0012Z_S\b\u0017I3";
      h[18] = "W\u0002O[!\u007f\u0005\u0004\u0019\rStlS\u0013E(c\u001c\u001e\u0013Uc\u001eU\u0012\u0010]:f\t\f\u0019\u0006S";
      h[19] = "u;\rZ\u0017S'=[\fe^NjQD\u001eO>'QTU2w+R\\\fJ+5[\u0007e";
      h[20] = "H0v'fsE~4YsJI\":\"`:\u0004\"*i\u001ds\b!\"0e/\u0016(yY";
      h[21] = "YF]Hc\u0004\u0000\u001b\u0005V\u0002\u001ccI\r^y\b\u0013\u0004\rN2uR\u0006^RyL_H\u001c,";
   
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

   private static native S0121 __indy$0(Object var0, long var1, long var3) throws Throwable;

   private static native S0121 __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native S0121 __indy$2(Object var0, long var1, long var3) throws Throwable;

   private static native S0121 __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native S0121 __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$7(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$8(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$10(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_3726 __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$13(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$14(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$15(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$16() throws Throwable;

   private static native void __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(5261791953522435809L,2);f.pc=1;return;}
case 1:{f.push(-7048710023625895329L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(136881078431119L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0026.class,"g",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0026.g,2);f.pc=9;return;}
case 9:{f.push(19767041053844L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[9]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[9],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(37182518057368L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[11]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(88);f.pc=18;return;}
case 18:{f.push(22,1);f.pc=19;return;}
case 19:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=20;return;}
case 20:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0026.class,"h",((java.lang.Object[])(Object)(f.pop())));f.pc=21;return;}
case 21:{f.push(22,1);f.pc=22;return;}
case 22:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=23;return;}
case 23:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0026.class,"i",((java.lang.String[])(Object)(f.pop())));f.pc=24;return;}
case 24:{dev.zprestige.sourceview.S0026.a$return$9b75();f.pc=25;return;}
case 25:{f.push("DES/CBC/PKCS5Padding",1);f.pc=26;return;}
case 26:{Object a0=f.pop();f.push(javax.crypto.Cipher.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=27;return;}
case 27:{f.rearrange(89);f.pc=28;return;}
case 28:{f.locals[1]=f.pop();f.pc=29;return;}
case 29:{f.push(2,1);f.pc=30;return;}
case 30:{f.push("DES",1);f.pc=31;return;}
case 31:{Object a0=f.pop();f.push(javax.crypto.SecretKeyFactory.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=32;return;}
case 32:{f.push(8,1);f.pc=33;return;}
case 33:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=34;return;}
case 34:{f.rearrange(89);f.pc=35;return;}
case 35:{f.push(0,1);f.pc=36;return;}
case 36:{f.push(f.locals[9],2);f.pc=37;return;}
case 37:{f.push(56,1);f.pc=38;return;}
case 38:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=39;return;}
case 39:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=40;return;}
case 40:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=41;return;}
case 41:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=42;return;}
case 42:{f.push(1,1);f.pc=43;return;}
case 43:{f.locals[2]=f.pop();f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.push(f.locals[2],1);f.pc=47;return;}
case 47:{f.push(8,1);f.pc=48;return;}
case 48:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=63;return;}f.pc=49;return;}
case 49:{f.rearrange(89);f.pc=50;return;}
case 50:{f.push(f.locals[2],1);f.pc=51;return;}
case 51:{f.push(f.locals[9],2);f.pc=52;return;}
case 52:{f.push(f.locals[2],1);f.pc=53;return;}
case 53:{f.push(8,1);f.pc=54;return;}
case 54:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a*b,1);f.pc=55;return;}
case 55:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a<<b,2);f.pc=56;return;}
case 56:{f.push(56,1);f.pc=57;return;}
case 57:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=58;return;}
case 58:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=59;return;}
case 59:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=60;return;}
case 60:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=61;return;}
case 61:{f.locals[2]=dev.zprestige.sourceview.FlowFrame.i(f.locals[2])+(1);f.pc=62;return;}
case 62:{f.pc=44;return;}
case 63:{f.pc=64;return;}
case 64:{f.pc=65;return;}
case 65:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.DESKeySpec.class),1);f.pc=66;return;}
case 66:{f.rearrange(90);f.pc=67;return;}
case 67:{f.rearrange(95);f.pc=68;return;}
case 68:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=69;return;}
case 69:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.SecretKeyFactory)(Object)receiver).generateSecret(((java.security.spec.KeySpec)(Object)(a0))),1);f.pc=70;return;}
case 70:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.IvParameterSpec.class),1);f.pc=71;return;}
case 71:{f.rearrange(89);f.pc=72;return;}
case 72:{f.push(8,1);f.pc=73;return;}
case 73:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=74;return;}
case 74:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=75;return;}
case 75:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();((javax.crypto.Cipher)(Object)receiver).init(dev.zprestige.sourceview.FlowFrame.i(a0),((java.security.Key)(Object)(a1)),((java.security.spec.AlgorithmParameterSpec)(Object)(a2)));f.pc=76;return;}
case 76:{f.push(5,1);f.pc=77;return;}
case 77:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=78;return;}
case 78:{f.locals[0]=f.pop();f.pc=79;return;}
case 79:{f.push(0,1);f.pc=80;return;}
case 80:{f.locals[6]=f.pop();f.pc=81;return;}
case 81:{f.push("\u00c5$\u0016\u00f1\u00f0#\u00b6\u00dd\u0004\u0097)/g\u00d9\n\u00f3\u0008I\u0002@\u00b7\u00e2\\\u0015\u00aa\u0010\u00fcc\u00b4\u0088\u00ea\u00ed\u00ec>7\u00ed\u0005\u0004\u00bf2\u00f4\u00c7",1);f.pc=82;return;}
case 82:{f.rearrange(89);f.pc=83;return;}
case 83:{f.locals[5]=f.pop();f.pc=84;return;}
case 84:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=85;return;}
case 85:{f.locals[7]=f.pop();f.pc=86;return;}
case 86:{f.push(16,1);f.pc=87;return;}
case 87:{f.locals[4]=f.pop();f.pc=88;return;}
case 88:{f.push(-1,1);f.pc=89;return;}
case 89:{f.locals[3]=f.pop();f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.pc=92;return;}
case 92:{f.locals[3]=dev.zprestige.sourceview.FlowFrame.i(f.locals[3])+(1);f.pc=93;return;}
case 93:{f.push(f.locals[5],1);f.pc=94;return;}
case 94:{f.push(f.locals[3],1);f.pc=95;return;}
case 95:{f.rearrange(89);f.pc=96;return;}
case 96:{f.push(f.locals[4],1);f.pc=97;return;}
case 97:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=98;return;}
case 98:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).substring(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1)),1);f.pc=99;return;}
case 99:{f.push(-1,1);f.pc=100;return;}
case 100:{f.pc=166;return;}
case 101:{f.pc=102;return;}
case 102:{f.pc=103;return;}
case 103:{f.push(f.locals[0],1);f.pc=104;return;}
case 104:{f.rearrange(95);f.pc=105;return;}
case 105:{f.push(f.locals[6],1);f.pc=106;return;}
case 106:{f.locals[6]=dev.zprestige.sourceview.FlowFrame.i(f.locals[6])+(1);f.pc=107;return;}
case 107:{f.rearrange(95);f.pc=108;return;}
case 108:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=109;return;}
case 109:{f.push(f.locals[3],1);f.pc=110;return;}
case 110:{f.push(f.locals[4],1);f.pc=111;return;}
case 111:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=112;return;}
case 112:{f.rearrange(89);f.pc=113;return;}
case 113:{f.locals[3]=f.pop();f.pc=114;return;}
case 114:{f.push(f.locals[7],1);f.pc=115;return;}
case 115:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=121;return;}f.pc=116;return;}
case 116:{f.push(f.locals[5],1);f.pc=117;return;}
case 117:{f.push(f.locals[3],1);f.pc=118;return;}
case 118:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).charAt(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=119;return;}
case 119:{f.locals[4]=f.pop();f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=90;return;}
case 121:{f.pc=122;return;}
case 122:{f.pc=123;return;}
case 123:{f.push("\u00a1\u00f0\u00da\u000b\u0014\u0019\u00e5+\u0008\u000f\u00ce-\u001a^\u00f2\u0080]",1);f.pc=124;return;}
case 124:{f.rearrange(89);f.pc=125;return;}
case 125:{f.locals[5]=f.pop();f.pc=126;return;}
case 126:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=127;return;}
case 127:{f.locals[7]=f.pop();f.pc=128;return;}
case 128:{f.push(8,1);f.pc=129;return;}
case 129:{f.locals[4]=f.pop();f.pc=130;return;}
case 130:{f.push(-1,1);f.pc=131;return;}
case 131:{f.locals[3]=f.pop();f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.pc=134;return;}
case 134:{f.locals[3]=dev.zprestige.sourceview.FlowFrame.i(f.locals[3])+(1);f.pc=135;return;}
case 135:{f.push(f.locals[5],1);f.pc=136;return;}
case 136:{f.push(f.locals[3],1);f.pc=137;return;}
case 137:{f.rearrange(89);f.pc=138;return;}
case 138:{f.push(f.locals[4],1);f.pc=139;return;}
case 139:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=140;return;}
case 140:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).substring(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1)),1);f.pc=141;return;}
case 141:{f.push(0,1);f.pc=142;return;}
case 142:{f.pc=166;return;}
case 143:{f.pc=144;return;}
case 144:{f.pc=145;return;}
case 145:{f.push(f.locals[0],1);f.pc=146;return;}
case 146:{f.rearrange(95);f.pc=147;return;}
case 147:{f.push(f.locals[6],1);f.pc=148;return;}
case 148:{f.locals[6]=dev.zprestige.sourceview.FlowFrame.i(f.locals[6])+(1);f.pc=149;return;}
case 149:{f.rearrange(95);f.pc=150;return;}
case 150:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=151;return;}
case 151:{f.push(f.locals[3],1);f.pc=152;return;}
case 152:{f.push(f.locals[4],1);f.pc=153;return;}
case 153:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=154;return;}
case 154:{f.rearrange(89);f.pc=155;return;}
case 155:{f.locals[3]=f.pop();f.pc=156;return;}
case 156:{f.push(f.locals[7],1);f.pc=157;return;}
case 157:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=163;return;}f.pc=158;return;}
case 158:{f.push(f.locals[5],1);f.pc=159;return;}
case 159:{f.push(f.locals[3],1);f.pc=160;return;}
case 160:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).charAt(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=161;return;}
case 161:{f.locals[4]=f.pop();f.pc=162;return;}
case 162:{f.pc=132;return;}
case 163:{f.pc=164;return;}
case 164:{f.pc=165;return;}
case 165:{f.pc=180;return;}
case 166:{f.pc=167;return;}
case 167:{f.pc=168;return;}
case 168:{f.rearrange(95);f.pc=169;return;}
case 169:{f.push("ISO-8859-1",1);f.pc=170;return;}
case 170:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).getBytes(((java.lang.String)(Object)(a0))),1);f.pc=171;return;}
case 171:{f.push(f.locals[1],1);f.pc=172;return;}
case 172:{f.rearrange(95);f.pc=173;return;}
case 173:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.Cipher)(Object)receiver).doFinal(((byte[])(Object)(a0))),1);f.pc=174;return;}
case 174:{f.locals[8]=f.pop();f.pc=175;return;}
case 175:{f.push(f.locals[8],1);f.pc=176;return;}
case 176:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0026.a(((byte[])(Object)(a0))),1);f.pc=177;return;}
case 177:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).intern(),1);f.pc=178;return;}
case 178:{f.rearrange(95);f.pc=179;return;}
case 179:{switch(dev.zprestige.sourceview.FlowFrame.i(f.pop())){case 0:f.pc=143;break;default:f.pc=101;}return;}
case 180:{f.pc=181;return;}
case 181:{f.pc=182;return;}
case 182:{f.pc=183;return;}
case 183:{f.push(net.minecraft.class_3959.class,1);f.pc=184;return;}
case 184:{f.push(f.locals[0],1);f.pc=185;return;}
case 185:{f.push(1,1);f.pc=186;return;}
case 186:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=187;return;}
case 187:{f.push(net.minecraft.class_243.class,1);f.pc=188;return;}
case 188:{f.push(f.locals[11],2);f.pc=189;return;}
case 189:{f.push(4,1);f.pc=190;return;}
case 190:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=191;return;}
case 191:{f.rearrange(91);f.pc=192;return;}
case 192:{f.rearrange(91);f.pc=193;return;}
case 193:{f.rearrange(87);f.pc=194;return;}
case 194:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=195;return;}
case 195:{f.push(3,1);f.pc=196;return;}
case 196:{f.rearrange(95);f.pc=197;return;}
case 197:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=198;return;}
case 198:{f.rearrange(90);f.pc=199;return;}
case 199:{f.rearrange(95);f.pc=200;return;}
case 200:{f.push(2,1);f.pc=201;return;}
case 201:{f.rearrange(95);f.pc=202;return;}
case 202:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=203;return;}
case 203:{f.rearrange(90);f.pc=204;return;}
case 204:{f.rearrange(95);f.pc=205;return;}
case 205:{f.push(1,1);f.pc=206;return;}
case 206:{f.rearrange(95);f.pc=207;return;}
case 207:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=208;return;}
case 208:{f.rearrange(90);f.pc=209;return;}
case 209:{f.rearrange(95);f.pc=210;return;}
case 210:{f.push(0,1);f.pc=211;return;}
case 211:{f.rearrange(95);f.pc=212;return;}
case 212:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=213;return;}
case 213:{f.push(-1816564739341155725L,2);f.pc=214;return;}
case 214:{f.push(f.locals[9],2);f.pc=215;return;}
case 215:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0026.__indy$0(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=216;return;}
case 216:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0026.class,"a",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=217;return;}
case 217:{f.pc=218;return;}
case 218:{f.pc=219;return;}
case 219:{f.push(net.minecraft.class_3959.class,1);f.pc=220;return;}
case 220:{f.push(f.locals[0],1);f.pc=221;return;}
case 221:{f.push(3,1);f.pc=222;return;}
case 222:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=223;return;}
case 223:{f.push(net.minecraft.class_243.class,1);f.pc=224;return;}
case 224:{f.push(f.locals[11],2);f.pc=225;return;}
case 225:{f.push(4,1);f.pc=226;return;}
case 226:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=227;return;}
case 227:{f.rearrange(91);f.pc=228;return;}
case 228:{f.rearrange(91);f.pc=229;return;}
case 229:{f.rearrange(87);f.pc=230;return;}
case 230:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=231;return;}
case 231:{f.push(3,1);f.pc=232;return;}
case 232:{f.rearrange(95);f.pc=233;return;}
case 233:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=234;return;}
case 234:{f.rearrange(90);f.pc=235;return;}
case 235:{f.rearrange(95);f.pc=236;return;}
case 236:{f.push(2,1);f.pc=237;return;}
case 237:{f.rearrange(95);f.pc=238;return;}
case 238:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=239;return;}
case 239:{f.rearrange(90);f.pc=240;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$2(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 240:{f.rearrange(95);f.pc=241;return;}
case 241:{f.push(1,1);f.pc=242;return;}
case 242:{f.rearrange(95);f.pc=243;return;}
case 243:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=244;return;}
case 244:{f.rearrange(90);f.pc=245;return;}
case 245:{f.rearrange(95);f.pc=246;return;}
case 246:{f.push(0,1);f.pc=247;return;}
case 247:{f.rearrange(95);f.pc=248;return;}
case 248:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=249;return;}
case 249:{f.push(-1816564739341155725L,2);f.pc=250;return;}
case 250:{f.push(f.locals[9],2);f.pc=251;return;}
case 251:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0026.__indy$1(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=252;return;}
case 252:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0026.class,"b",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=253;return;}
case 253:{f.pc=254;return;}
case 254:{f.pc=255;return;}
case 255:{f.push(net.minecraft.class_3959.class,1);f.pc=256;return;}
case 256:{f.push(f.locals[0],1);f.pc=257;return;}
case 257:{f.push(2,1);f.pc=258;return;}
case 258:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=259;return;}
case 259:{f.push(net.minecraft.class_3959.class_3960.class,1);f.pc=260;return;}
case 260:{f.push(f.locals[11],2);f.pc=261;return;}
case 261:{f.push(4,1);f.pc=262;return;}
case 262:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=263;return;}
case 263:{f.rearrange(91);f.pc=264;return;}
case 264:{f.rearrange(91);f.pc=265;return;}
case 265:{f.rearrange(87);f.pc=266;return;}
case 266:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=267;return;}
case 267:{f.push(3,1);f.pc=268;return;}
case 268:{f.rearrange(95);f.pc=269;return;}
case 269:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=270;return;}
case 270:{f.rearrange(90);f.pc=271;return;}
case 271:{f.rearrange(95);f.pc=272;return;}
case 272:{f.push(2,1);f.pc=273;return;}
case 273:{f.rearrange(95);f.pc=274;return;}
case 274:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=275;return;}
case 275:{f.rearrange(90);f.pc=276;return;}
case 276:{f.rearrange(95);f.pc=277;return;}
case 277:{f.push(1,1);f.pc=278;return;}
case 278:{f.rearrange(95);f.pc=279;return;}
case 279:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=280;return;}
case 280:{f.rearrange(90);f.pc=281;return;}
case 281:{f.rearrange(95);f.pc=282;return;}
case 282:{f.push(0,1);f.pc=283;return;}
case 283:{f.rearrange(95);f.pc=284;return;}
case 284:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=285;return;}
case 285:{f.push(-1816564739341155725L,2);f.pc=286;return;}
case 286:{f.push(f.locals[9],2);f.pc=287;return;}
case 287:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0026.__indy$2(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=288;return;}
case 288:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0026.class,"c",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=289;return;}
case 289:{f.pc=290;return;}
case 290:{f.pc=291;return;}
case 291:{f.push(net.minecraft.class_3959.class,1);f.pc=292;return;}
case 292:{f.push(f.locals[0],1);f.pc=293;return;}
case 293:{f.push(4,1);f.pc=294;return;}
case 294:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=295;return;}
case 295:{f.push(net.minecraft.class_3959.class_242.class,1);f.pc=296;return;}
case 296:{f.push(f.locals[11],2);f.pc=297;return;}
case 297:{f.push(4,1);f.pc=298;return;}
case 298:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=299;return;}
case 299:{f.rearrange(91);f.pc=300;return;}
case 300:{f.rearrange(91);f.pc=301;return;}
case 301:{f.rearrange(87);f.pc=302;return;}
case 302:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=303;return;}
case 303:{f.push(3,1);f.pc=304;return;}
case 304:{f.rearrange(95);f.pc=305;return;}
case 305:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=306;return;}
case 306:{f.rearrange(90);f.pc=307;return;}
case 307:{f.rearrange(95);f.pc=308;return;}
case 308:{f.push(2,1);f.pc=309;return;}
case 309:{f.rearrange(95);f.pc=310;return;}
case 310:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=311;return;}
case 311:{f.rearrange(90);f.pc=312;return;}
case 312:{f.rearrange(95);f.pc=313;return;}
case 313:{f.push(1,1);f.pc=314;return;}
case 314:{f.rearrange(95);f.pc=315;return;}
case 315:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=316;return;}
case 316:{f.rearrange(90);f.pc=317;return;}
case 317:{f.rearrange(95);f.pc=318;return;}
case 318:{f.push(0,1);f.pc=319;return;}
case 319:{f.rearrange(95);f.pc=320;return;}
case 320:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=321;return;}
case 321:{f.push(-1816564739341155725L,2);f.pc=322;return;}
case 322:{f.push(f.locals[9],2);f.pc=323;return;}
case 323:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0026.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=324;return;}
case 324:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0026.class,"d",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=325;return;}
case 325:{f.pc=326;return;}
case 326:{f.pc=327;return;}
case 327:{f.push(net.minecraft.class_3959.class,1);f.pc=328;return;}
case 328:{f.push(f.locals[0],1);f.pc=329;return;}
case 329:{f.push(0,1);f.pc=330;return;}
case 330:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=331;return;}
case 331:{f.push(net.minecraft.class_3726.class,1);f.pc=332;return;}
case 332:{f.push(f.locals[11],2);f.pc=333;return;}
case 333:{f.push(4,1);f.pc=334;return;}
case 334:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=335;return;}
case 335:{f.rearrange(91);f.pc=336;return;}
case 336:{f.rearrange(91);f.pc=337;return;}
case 337:{f.rearrange(87);f.pc=338;return;}
case 338:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=339;return;}
case 339:{f.push(3,1);f.pc=340;return;}
case 340:{f.rearrange(95);f.pc=341;return;}
case 341:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=342;return;}
case 342:{f.rearrange(90);f.pc=343;return;}
case 343:{f.rearrange(95);f.pc=344;return;}
case 344:{f.push(2,1);f.pc=345;return;}
case 345:{f.rearrange(95);f.pc=346;return;}
case 346:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=347;return;}
case 347:{f.rearrange(90);f.pc=348;return;}
case 348:{f.rearrange(95);f.pc=349;return;}
case 349:{f.push(1,1);f.pc=350;return;}
case 350:{f.rearrange(95);f.pc=351;return;}
case 351:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=352;return;}
case 352:{f.rearrange(90);f.pc=353;return;}
case 353:{f.rearrange(95);f.pc=354;return;}
case 354:{f.push(0,1);f.pc=355;return;}
case 355:{f.rearrange(95);f.pc=356;return;}
case 356:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=357;return;}
case 357:{f.push(-1816564739341155725L,2);f.pc=358;return;}
case 358:{f.push(f.locals[9],2);f.pc=359;return;}
case 359:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0026.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=360;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$3(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 360:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0026.class,"e",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=361;return;}
case 361:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
