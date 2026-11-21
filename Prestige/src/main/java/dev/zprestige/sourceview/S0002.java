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
import net.minecraft.class_4184;

public class S0002 {
   public static S0194 a;
   public static S0194 b;
   public class_4184 c;
   public static long d;
   public static Object[] e;
   public static String[] f;

   public S0002(class_4184 var1)  throws Throwable {
try {

      this.c = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(13,10);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
case 1:__flow$1$1(f);break;
case 2:__flow$1$2(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static Class b(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = a(var0, var2);
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

   public void b$return$e4b0899c(Object[] var1) {
try {

      float var2 = (Float)(Object)(var1[0]);
      float var5 = (Float)(Object)(var1[1]);
      long var3 = (Long)(Object)(var1[2]);
      var3 = d ^ var3;
      long var6 = var3 ^ 55335610542964L;

      try {
         __indy$11(
            b,
            new Object[]{this.c, new Object[]{__indy$9(var2, 8504309644386699240L, var3), __indy$10(var5, 8504309644386699240L, var3)}, var6},
            8504384372192640708L,
            var3
         );
      } catch (Throwable var9) {
         throw new RuntimeException(var9);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field c(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = f[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            e[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
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
            var12 = b(588898143613547L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = f[var4];
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
            e[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(588898143613547L, 0L);
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
            var23 = b(588898143613547L, 0L);
         }
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

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$12().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/C" + " : " + var1 + " : " + var2.toString(), var5);
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
      return __indy$13(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'Z' && var8 != 'B' && var8 != 'w' && var8 != 199) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 230) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 239) {
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
            } else if (var8 == 'B') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'w') {
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

   public void a$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      double var4 = (Double)(Object)(var1[1]);
      double var6 = (Double)(Object)(var1[2]);
      long var8 = (Long)(Object)(var1[3]);
      var8 = d ^ var8;
      long var10 = var8 ^ 50275459632695L;

      try {
         __indy$17(
            a,
            new Object[]{
               this.c,
               new Object[]{
                  __indy$14(var2, 1389049995835043712L, var8), __indy$15(var4, 1389049995835043712L, var8), __indy$16(var6, 1389049995835043712L, var8)
               },
               var10
            },
            1389003612862913415L,
            var8
         );
      } catch (Throwable var13) {
         throw new RuntimeException(var13);
      }
   
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
      if (f[var4] != null) {
         return var4;
      }

      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 52;
         case 1 -> 4;
         case 2 -> 51;
         case 3 -> 3;
         case 4 -> 56;
         case 5 -> 61;
         case 6 -> 9;
         case 7 -> 60;
         case 8 -> 24;
         case 9 -> 42;
         case 10 -> 41;
         case 11 -> 2;
         case 12 -> 14;
         case 13 -> 37;
         case 14 -> 5;
         case 15 -> 12;
         case 16 -> 10;
         case 17 -> 20;
         case 18 -> 17;
         case 19 -> 44;
         case 20 -> 57;
         case 21 -> 45;
         case 22 -> 38;
         case 23 -> 15;
         case 24 -> 21;
         case 25 -> 27;
         case 26 -> 50;
         case 27 -> 46;
         case 28 -> 47;
         case 29 -> 54;
         case 30 -> 40;
         case 31 -> 59;
         case 32 -> 13;
         case 33 -> 43;
         case 34 -> 29;
         case 35 -> 49;
         case 36 -> 33;
         case 37 -> 36;
         case 38 -> 16;
         case 39 -> 0;
         case 40 -> 35;
         case 41 -> 26;
         case 42 -> 34;
         case 43 -> 22;
         case 44 -> 8;
         case 45 -> 18;
         case 46 -> 32;
         case 47 -> 19;
         case 48 -> 62;
         case 49 -> 23;
         case 50 -> 53;
         case 51 -> 28;
         case 52 -> 63;
         case 53 -> 39;
         case 54 -> 6;
         case 55 -> 11;
         case 56 -> 31;
         case 57 -> 48;
         case 58 -> 1;
         case 59 -> 30;
         case 60 -> 25;
         case 61 -> 58;
         case 62 -> 55;
         default -> 7;
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

   public static void a$return$9b75() {
try {

      e[0] = "\bf*\u0011cG\u0003i;^\u001bD\u000bc";
      e[1] = "z2<+!\u0006q=-dL\u0006q 9";
      e[2] = "\u0015h\u001c(L\u000b\u001eg\rg$\u000b\u0010h\u001e";
      e[3] = "aF'e\u001emjI6*tn~E=a";
      e[4] = "\u001f\u000e[:A=\t\u000e^`R*\u001eE]f^>\u000f\u0002Jq\u0015.,";
      e[5] = "%d\u007frI[PDt}X\u00141J\u007fv\\NE";
      e[6] = float.class;
      f[6] = "java/lang/Float";
      e[7] = "IH\u0007 \u0005^<h\f/\u0014\u0011]f\u0007$\u0010K)";
      e[8] = "\\:~\u0004\tBW5oKhL\\>k\u0011";
      e[9] = double.class;
      f[9] = "java/lang/Double";
      e[10] = "\ny\u0004L\f|Iff`<\u001av\u0004\u0016\\\u0006yFx\u0002CR";
      e[11] = "\u0001\\\u0016\u001f\u000eI\u0003J\u0014F3wiibvCKS\nR\nWT\u0007";
      e[12] = "7PoBb`=Bp\u0006S_\\qL>#cf\u0012|B7|2";
      e[13] = "\u0019\u000e>Q{3\u001b\u0018<\bF/I\u0007zU\u0001? _k]{=PXjI/Q\u0019\u000e>Q{3\u001b\u0018<\bF";
      e[14] = "\u0016`\u001e\u0001\u000eFSg\u000eN\u007fJ-c\u001a\u0016\rECa\b\u001c\u000e \u0010$\u001c\u000fEDQd[\u0002\u007f";
      e[15] = "\u0018E3\u001e{e\u0012W,ZJxKX \u000f\rh\"\u0002'\u000b1`H\u0002aR2\u0006\u0018E3\u001e{e\u0012W,ZJ";
      e[16] = "KM>VHl\u000eJ.\u00199gpN:AKo\u001eL(KH\nKM>VHl\u000eJ.\u00199";
   
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

   private static native Class __indy$0(long var0, long var2) throws Throwable;

   private static native Class __indy$1(long var0, long var2) throws Throwable;

   private static native Class __indy$2(long var0, long var2) throws Throwable;

   private static native Class __indy$3(long var0, long var2) throws Throwable;

   private static native S0194 __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native Class __indy$5(long var0, long var2) throws Throwable;

   private static native Class __indy$6(long var0, long var2) throws Throwable;

   private static native Class __indy$7(long var0, long var2) throws Throwable;

   private static native S0194 __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native Float __indy$9(float var0, long var1, long var3) throws Throwable;

   private static native Float __indy$10(float var0, long var1, long var3) throws Throwable;

   private static native Object __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$12() throws Throwable;

   private static native Object __indy$13(MethodHandle var0, Object[] var1) throws Throwable;

   private static native Double __indy$14(double var0, long var2, long var4) throws Throwable;

   private static native Double __indy$15(double var0, long var2, long var4) throws Throwable;

   private static native Double __indy$16(double var0, long var2, long var4) throws Throwable;

   private static native Object __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(3996150123411761149L,2);f.pc=1;return;}
case 1:{f.push(-7713141107529485939L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(217247378257038L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0002.class,"d",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0002.d,2);f.pc=9;return;}
case 9:{f.push(34231031656057L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[9]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[9],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(35192513523304L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[11]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(88);f.pc=18;return;}
case 18:{f.push(17,1);f.pc=19;return;}
case 19:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=20;return;}
case 20:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0002.class,"e",((java.lang.Object[])(Object)(f.pop())));f.pc=21;return;}
case 21:{f.push(17,1);f.pc=22;return;}
case 22:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=23;return;}
case 23:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0002.class,"f",((java.lang.String[])(Object)(f.pop())));f.pc=24;return;}
case 24:{dev.zprestige.sourceview.S0002.a$return$9b75();f.pc=25;return;}
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
case 76:{f.push(2,1);f.pc=77;return;}
case 77:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=78;return;}
case 78:{f.locals[0]=f.pop();f.pc=79;return;}
case 79:{f.push(0,1);f.pc=80;return;}
case 80:{f.locals[6]=f.pop();f.pc=81;return;}
case 81:{f.push("ck\u0093\u00c7\u0015\u00f0hb\u0010\u0011N2\u008e1\u00830=\u000f\u0085\u00f6V3\u001bv\r",1);f.pc=82;return;}
case 82:{f.rearrange(89);f.pc=83;return;}
case 83:{f.locals[5]=f.pop();f.pc=84;return;}
case 84:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=85;return;}
case 85:{f.locals[7]=f.pop();f.pc=86;return;}
case 86:{f.push(8,1);f.pc=87;return;}
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
case 100:{f.pc=124;return;}
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
case 123:{f.pc=139;return;}
case 124:{f.pc=125;return;}
case 125:{f.pc=126;return;}
case 126:{f.rearrange(95);f.pc=127;return;}
case 127:{f.push("ISO-8859-1",1);f.pc=128;return;}
case 128:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).getBytes(((java.lang.String)(Object)(a0))),1);f.pc=129;return;}
case 129:{f.push(f.locals[1],1);f.pc=130;return;}
case 130:{f.rearrange(95);f.pc=131;return;}
case 131:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.Cipher)(Object)receiver).doFinal(((byte[])(Object)(a0))),1);f.pc=132;return;}
case 132:{f.locals[8]=f.pop();f.pc=133;return;}
case 133:{f.push(f.locals[8],1);f.pc=134;return;}
case 134:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.a(((byte[])(Object)(a0))),1);f.pc=135;return;}
case 135:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).intern(),1);f.pc=136;return;}
case 136:{f.rearrange(95);f.pc=137;return;}
case 137:{f.rearrange(87);f.pc=138;return;}
case 138:{f.pc=101;return;}
case 139:{f.pc=140;return;}
case 140:{f.pc=141;return;}
case 141:{f.pc=142;return;}
case 142:{f.push(net.minecraft.class_4184.class,1);f.pc=143;return;}
case 143:{f.push(f.locals[0],1);f.pc=144;return;}
case 144:{f.push(0,1);f.pc=145;return;}
case 145:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=146;return;}
case 146:{f.push(-6477432947990861954L,2);f.pc=147;return;}
case 147:{f.push(f.locals[9],2);f.pc=148;return;}
case 148:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.__indy$0(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=149;return;}
case 149:{f.push(3,1);f.pc=150;return;}
case 150:{f.push(java.lang.reflect.Array.newInstance(java.lang.Class.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=151;return;}
case 151:{f.rearrange(89);f.pc=152;return;}
case 152:{f.push(0,1);f.pc=153;return;}
case 153:{f.push(-6477844899892285478L,2);f.pc=154;return;}
case 154:{f.push(f.locals[9],2);f.pc=155;return;}
case 155:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.__indy$1(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=156;return;}
case 156:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=157;return;}
case 157:{f.rearrange(89);f.pc=158;return;}
case 158:{f.push(1,1);f.pc=159;return;}
case 159:{f.push(-6477844899892285478L,2);f.pc=160;return;}
case 160:{f.push(f.locals[9],2);f.pc=161;return;}
case 161:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=162;return;}
case 162:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=163;return;}
case 163:{f.rearrange(89);f.pc=164;return;}
case 164:{f.push(2,1);f.pc=165;return;}
case 165:{f.push(-6477844899892285478L,2);f.pc=166;return;}
case 166:{f.push(f.locals[9],2);f.pc=167;return;}
case 167:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.__indy$3(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=168;return;}
case 168:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=169;return;}
case 169:{f.push(f.locals[11],2);f.pc=170;return;}
case 170:{f.push(5,1);f.pc=171;return;}
case 171:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=172;return;}
case 172:{f.rearrange(91);f.pc=173;return;}
case 173:{f.rearrange(91);f.pc=174;return;}
case 174:{f.rearrange(87);f.pc=175;return;}
case 175:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=176;return;}
case 176:{f.push(4,1);f.pc=177;return;}
case 177:{f.rearrange(95);f.pc=178;return;}
case 178:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=179;return;}
case 179:{f.rearrange(90);f.pc=180;return;}
case 180:{f.rearrange(95);f.pc=181;return;}
case 181:{f.push(3,1);f.pc=182;return;}
case 182:{f.rearrange(95);f.pc=183;return;}
case 183:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=184;return;}
case 184:{f.rearrange(90);f.pc=185;return;}
case 185:{f.rearrange(95);f.pc=186;return;}
case 186:{f.push(2,1);f.pc=187;return;}
case 187:{f.rearrange(95);f.pc=188;return;}
case 188:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=189;return;}
case 189:{f.rearrange(90);f.pc=190;return;}
case 190:{f.rearrange(95);f.pc=191;return;}
case 191:{f.push(1,1);f.pc=192;return;}
case 192:{f.rearrange(95);f.pc=193;return;}
case 193:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=194;return;}
case 194:{f.rearrange(90);f.pc=195;return;}
case 195:{f.rearrange(95);f.pc=196;return;}
case 196:{f.push(0,1);f.pc=197;return;}
case 197:{f.rearrange(95);f.pc=198;return;}
case 198:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=199;return;}
case 199:{f.push(-6476971931981578339L,2);f.pc=200;return;}
case 200:{f.push(f.locals[9],2);f.pc=201;return;}
case 201:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=202;return;}
case 202:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0002.class,"a",((dev.zprestige.sourceview.S0194)(Object)(f.pop())));f.pc=203;return;}
case 203:{f.pc=204;return;}
case 204:{f.pc=205;return;}
case 205:{f.push(net.minecraft.class_4184.class,1);f.pc=206;return;}
case 206:{f.push(f.locals[0],1);f.pc=207;return;}
case 207:{f.push(1,1);f.pc=208;return;}
case 208:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=209;return;}
case 209:{f.push(-6477432947990861954L,2);f.pc=210;return;}
case 210:{f.push(f.locals[9],2);f.pc=211;return;}
case 211:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.__indy$5(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=212;return;}
case 212:{f.push(2,1);f.pc=213;return;}
case 213:{f.push(java.lang.reflect.Array.newInstance(java.lang.Class.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=214;return;}
case 214:{f.rearrange(89);f.pc=215;return;}
case 215:{f.push(0,1);f.pc=216;return;}
case 216:{f.push(-6477343687368866128L,2);f.pc=217;return;}
case 217:{f.push(f.locals[9],2);f.pc=218;return;}
case 218:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.__indy$6(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=219;return;}
case 219:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=220;return;}
case 220:{f.rearrange(89);f.pc=221;return;}
case 221:{f.push(1,1);f.pc=222;return;}
case 222:{f.push(-6477343687368866128L,2);f.pc=223;return;}
case 223:{f.push(f.locals[9],2);f.pc=224;return;}
case 224:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.__indy$7(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=225;return;}
case 225:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=226;return;}
case 226:{f.push(f.locals[11],2);f.pc=227;return;}
case 227:{f.push(5,1);f.pc=228;return;}
case 228:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=229;return;}
case 229:{f.rearrange(91);f.pc=230;return;}
case 230:{f.rearrange(91);f.pc=231;return;}
case 231:{f.rearrange(87);f.pc=232;return;}
case 232:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=233;return;}
case 233:{f.push(4,1);f.pc=234;return;}
case 234:{f.rearrange(95);f.pc=235;return;}
case 235:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=236;return;}
case 236:{f.rearrange(90);f.pc=237;return;}
case 237:{f.rearrange(95);f.pc=238;return;}
case 238:{f.push(3,1);f.pc=239;return;}
case 239:{f.rearrange(95);f.pc=240;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$2(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 240:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=241;return;}
case 241:{f.rearrange(90);f.pc=242;return;}
case 242:{f.rearrange(95);f.pc=243;return;}
case 243:{f.push(2,1);f.pc=244;return;}
case 244:{f.rearrange(95);f.pc=245;return;}
case 245:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=246;return;}
case 246:{f.rearrange(90);f.pc=247;return;}
case 247:{f.rearrange(95);f.pc=248;return;}
case 248:{f.push(1,1);f.pc=249;return;}
case 249:{f.rearrange(95);f.pc=250;return;}
case 250:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=251;return;}
case 251:{f.rearrange(90);f.pc=252;return;}
case 252:{f.rearrange(95);f.pc=253;return;}
case 253:{f.push(0,1);f.pc=254;return;}
case 254:{f.rearrange(95);f.pc=255;return;}
case 255:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=256;return;}
case 256:{f.push(-6476971931981578339L,2);f.pc=257;return;}
case 257:{f.push(f.locals[9],2);f.pc=258;return;}
case 258:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0002.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=259;return;}
case 259:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0002.class,"b",((dev.zprestige.sourceview.S0194)(Object)(f.pop())));f.pc=260;return;}
case 260:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
