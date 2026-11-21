package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_243;

public class S0069 {
   public static S0121 a;
   public static S0121 b;
   public static S0121 c;
   public class_243 d;
   public static long e;
   public static Object[] f;
   public static String[] g;

   public S0069(class_243 var1)  throws Throwable {
try {

      this.d = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(4,8);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
case 1:__flow$1$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public void e$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      double var4 = (Double)(Object)(var1[1]);
      long var6 = (Long)(Object)(var1[2]);
      var6 = e ^ var6;
      long var8 = var6 ^ 85122185872538L;
      S0121 var10000 = a;
      class_243 var10001 = this.d;
      Object[] var10005 = new Object[]{null, null, var8};
      var10005[1] = var2;
      var10005[0] = var10001;
      __indy$6(var10000, var10005, 5752311463224591668L, var6);
      var10000 = c;
      var10001 = this.d;
      var10005 = new Object[]{null, null, var8};
      var10005[1] = var4;
      var10005[0] = var10001;
      __indy$7(var10000, var10005, 5752311463224591668L, var6);
   
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

      double var2 = (Double)(Object)(var1[0]);
      long var4 = (Long)(Object)(var1[1]);
      var4 = e ^ var4;
      long var6 = var4 ^ 7646679739643L;
      S0121 var10000 = b;
      class_243 var10001 = this.d;
      Object[] var10005 = new Object[]{null, null, var6};
      var10005[1] = var2;
      var10005[0] = var10001;
      __indy$8(var10000, var10005, -6362148854909948587L, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void c$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      long var4 = (Long)(Object)(var1[1]);
      var4 = e ^ var4;
      long var6 = var4 ^ 74424035484424L;
      S0121 var10000 = c;
      class_243 var10001 = this.d;
      Object[] var10005 = new Object[]{null, null, var6};
      var10005[1] = var2;
      var10005[0] = var10001;
      __indy$9(var10000, var10005, 2902110463929918118L, var4);
   
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
            var12 = b(526026092895276L, 0L);
         }
      }
   
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
            var23 = b(526026092895276L, 0L);
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
            var23 = b(526026092895276L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void d$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      double var4 = (Double)(Object)(var1[1]);
      double var6 = (Double)(Object)(var1[2]);
      long var8 = (Long)(Object)(var1[3]);
      var8 = e ^ var8;
      long var10 = var8 ^ 124005428690047L;
      S0121 var10000 = a;
      class_243 var10001 = this.d;
      Object[] var10005 = new Object[]{null, null, var10};
      var10005[1] = var2;
      var10005[0] = var10001;
      __indy$10(var10000, var10005, 8588766288766631377L, var8);
      var10000 = b;
      var10001 = this.d;
      var10005 = new Object[]{null, null, var10};
      var10005[1] = var4;
      var10005[0] = var10001;
      __indy$11(var10000, var10005, 8588766288766631377L, var8);
      var10000 = c;
      var10001 = this.d;
      var10005 = new Object[]{null, null, var10};
      var10005[1] = var6;
      var10005[0] = var10001;
      __indy$12(var10000, var10005, 8588766288766631377L, var8);
   
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
         case 0 -> 31;
         case 1 -> 7;
         case 2 -> 24;
         case 3 -> 48;
         case 4 -> 29;
         case 5 -> 38;
         case 6 -> 10;
         case 7 -> 59;
         case 8 -> 26;
         case 9 -> 13;
         case 10 -> 33;
         case 11 -> 12;
         case 12 -> 23;
         case 13 -> 37;
         case 14 -> 3;
         case 15 -> 18;
         case 16 -> 39;
         case 17 -> 28;
         case 18 -> 63;
         case 19 -> 11;
         case 20 -> 35;
         case 21 -> 46;
         case 22 -> 56;
         case 23 -> 41;
         case 24 -> 34;
         case 25 -> 51;
         case 26 -> 17;
         case 27 -> 36;
         case 28 -> 57;
         case 29 -> 61;
         case 30 -> 8;
         case 31 -> 21;
         case 32 -> 19;
         case 33 -> 47;
         case 34 -> 54;
         case 35 -> 52;
         case 36 -> 4;
         case 37 -> 9;
         case 38 -> 58;
         case 39 -> 53;
         case 40 -> 55;
         case 41 -> 22;
         case 42 -> 25;
         case 43 -> 49;
         case 44 -> 1;
         case 45 -> 2;
         case 46 -> 50;
         case 47 -> 16;
         case 48 -> 27;
         case 49 -> 43;
         case 50 -> 30;
         case 51 -> 62;
         case 52 -> 15;
         case 53 -> 40;
         case 54 -> 6;
         case 55 -> 5;
         case 56 -> 45;
         case 57 -> 20;
         case 58 -> 0;
         case 59 -> 32;
         case 60 -> 44;
         case 61 -> 42;
         case 62 -> 14;
         default -> 60;
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

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'D' && var8 != '$' && var8 != 'Z' && var8 != 216) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 181) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'N') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'D') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == '$') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'Z') {
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
      return __indy$13(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$14().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/ad" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
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

   public static void a$return$9b75() {
try {

      f[0] = "\u000bc$mR1\u001dc!7A&\n(\"1M2\u001bo5&\u0006#=";
      f[1] = "(+b6;0]\u000bi9*\u007f<\u0005b2.%H";
      f[2] = void.class;
      g[2] = "java/lang/Void";
      f[3] = "K\t\u0005wB\u0002]\t\u0000-Q\u0015JB\u0003+]\u0001[\u0005\u0014<\u0016\u0011y";
      f[4] = "P=\b\u0003K!%\u001d\u0003\fZnD\u0013\b\u0007^40";
      f[5] = "o{c0\u0006#dtr\u007fl pxy4";
      f[6] = "\u0019H\u0010:v\\\u0012G\u0001u\u001b\\\u0012Z\u0015";
      f[7] = "\"K\u0018\u007f\u001dK)D\t0|E\"O\rj";
      f[8] = "XB\u0005m?P\u0004I\u0002?V05 0\u0000j\u0001\u0015\n\u0012r6S\\I";
      f[9] = "X\u0012m]6\u0013^\u0004q%9-X\u001b{N)]\u001c\u0012rOP\u0014\u001fZc]5_[\n,%";
      f[10] = "Cv\fe\u0010_\u0004!Qvj]y!\u001b}\u0001M\te\u0012t\u00004Ci\u001fl\f\nE\u007f\u0003\u0014";
   
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

   public void a$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      long var4 = (Long)(Object)(var1[1]);
      var4 = e ^ var4;
      long var6 = var4 ^ 49322407180709L;
      S0121 var10000 = a;
      class_243 var10001 = this.d;
      Object[] var10005 = new Object[]{null, null, var6};
      var10005[1] = var2;
      var10005[0] = var10001;
      __indy$15(var10000, var10005, -5554291303769884661L, var4);
   
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

   private static native S0121 __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native Class __indy$2(long var0, long var2) throws Throwable;

   private static native S0121 __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native Class __indy$4(long var0, long var2) throws Throwable;

   private static native S0121 __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$7(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$8(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$10(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$12(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$13(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$14() throws Throwable;

   private static native void __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(7957278894203925895L,2);f.pc=1;return;}
case 1:{f.push(2307561989984823083L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(197011434388248L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0069.class,"e",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0069.e,2);f.pc=9;return;}
case 9:{f.push(25147039489960L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[0]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[0],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(91609456005161L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(88);f.pc=18;return;}
case 18:{f.push(11,1);f.pc=19;return;}
case 19:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=20;return;}
case 20:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0069.class,"f",((java.lang.Object[])(Object)(f.pop())));f.pc=21;return;}
case 21:{f.push(11,1);f.pc=22;return;}
case 22:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=23;return;}
case 23:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0069.class,"g",((java.lang.String[])(Object)(f.pop())));f.pc=24;return;}
case 24:{dev.zprestige.sourceview.S0069.a$return$9b75();f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.push(net.minecraft.class_243.class,1);f.pc=28;return;}
case 28:{f.push("x",1);f.pc=29;return;}
case 29:{f.push(8033414146317671184L,2);f.pc=30;return;}
case 30:{f.push(f.locals[0],2);f.pc=31;return;}
case 31:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0069.__indy$0(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=32;return;}
case 32:{f.push(f.locals[2],2);f.pc=33;return;}
case 33:{f.push(4,1);f.pc=34;return;}
case 34:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=35;return;}
case 35:{f.rearrange(91);f.pc=36;return;}
case 36:{f.rearrange(91);f.pc=37;return;}
case 37:{f.rearrange(87);f.pc=38;return;}
case 38:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=39;return;}
case 39:{f.push(3,1);f.pc=40;return;}
case 40:{f.rearrange(95);f.pc=41;return;}
case 41:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=42;return;}
case 42:{f.rearrange(90);f.pc=43;return;}
case 43:{f.rearrange(95);f.pc=44;return;}
case 44:{f.push(2,1);f.pc=45;return;}
case 45:{f.rearrange(95);f.pc=46;return;}
case 46:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=47;return;}
case 47:{f.rearrange(90);f.pc=48;return;}
case 48:{f.rearrange(95);f.pc=49;return;}
case 49:{f.push(1,1);f.pc=50;return;}
case 50:{f.rearrange(95);f.pc=51;return;}
case 51:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=52;return;}
case 52:{f.rearrange(90);f.pc=53;return;}
case 53:{f.rearrange(95);f.pc=54;return;}
case 54:{f.push(0,1);f.pc=55;return;}
case 55:{f.rearrange(95);f.pc=56;return;}
case 56:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=57;return;}
case 57:{f.push(8033539074353211027L,2);f.pc=58;return;}
case 58:{f.push(f.locals[0],2);f.pc=59;return;}
case 59:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0069.__indy$1(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=60;return;}
case 60:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0069.class,"a",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.push(net.minecraft.class_243.class,1);f.pc=64;return;}
case 64:{f.push("y",1);f.pc=65;return;}
case 65:{f.push(8033414146317671184L,2);f.pc=66;return;}
case 66:{f.push(f.locals[0],2);f.pc=67;return;}
case 67:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0069.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=68;return;}
case 68:{f.push(f.locals[2],2);f.pc=69;return;}
case 69:{f.push(4,1);f.pc=70;return;}
case 70:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=71;return;}
case 71:{f.rearrange(91);f.pc=72;return;}
case 72:{f.rearrange(91);f.pc=73;return;}
case 73:{f.rearrange(87);f.pc=74;return;}
case 74:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=75;return;}
case 75:{f.push(3,1);f.pc=76;return;}
case 76:{f.rearrange(95);f.pc=77;return;}
case 77:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=78;return;}
case 78:{f.rearrange(90);f.pc=79;return;}
case 79:{f.rearrange(95);f.pc=80;return;}
case 80:{f.push(2,1);f.pc=81;return;}
case 81:{f.rearrange(95);f.pc=82;return;}
case 82:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=83;return;}
case 83:{f.rearrange(90);f.pc=84;return;}
case 84:{f.rearrange(95);f.pc=85;return;}
case 85:{f.push(1,1);f.pc=86;return;}
case 86:{f.rearrange(95);f.pc=87;return;}
case 87:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=88;return;}
case 88:{f.rearrange(90);f.pc=89;return;}
case 89:{f.rearrange(95);f.pc=90;return;}
case 90:{f.push(0,1);f.pc=91;return;}
case 91:{f.rearrange(95);f.pc=92;return;}
case 92:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=93;return;}
case 93:{f.push(8033539074353211027L,2);f.pc=94;return;}
case 94:{f.push(f.locals[0],2);f.pc=95;return;}
case 95:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0069.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=96;return;}
case 96:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0069.class,"b",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=97;return;}
case 97:{f.pc=98;return;}
case 98:{f.pc=99;return;}
case 99:{f.push(net.minecraft.class_243.class,1);f.pc=100;return;}
case 100:{f.push("z",1);f.pc=101;return;}
case 101:{f.push(8033414146317671184L,2);f.pc=102;return;}
case 102:{f.push(f.locals[0],2);f.pc=103;return;}
case 103:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0069.__indy$4(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=104;return;}
case 104:{f.push(f.locals[2],2);f.pc=105;return;}
case 105:{f.push(4,1);f.pc=106;return;}
case 106:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=107;return;}
case 107:{f.rearrange(91);f.pc=108;return;}
case 108:{f.rearrange(91);f.pc=109;return;}
case 109:{f.rearrange(87);f.pc=110;return;}
case 110:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=111;return;}
case 111:{f.push(3,1);f.pc=112;return;}
case 112:{f.rearrange(95);f.pc=113;return;}
case 113:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=114;return;}
case 114:{f.rearrange(90);f.pc=115;return;}
case 115:{f.rearrange(95);f.pc=116;return;}
case 116:{f.push(2,1);f.pc=117;return;}
case 117:{f.rearrange(95);f.pc=118;return;}
case 118:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=119;return;}
case 119:{f.rearrange(90);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.rearrange(95);f.pc=121;return;}
case 121:{f.push(1,1);f.pc=122;return;}
case 122:{f.rearrange(95);f.pc=123;return;}
case 123:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=124;return;}
case 124:{f.rearrange(90);f.pc=125;return;}
case 125:{f.rearrange(95);f.pc=126;return;}
case 126:{f.push(0,1);f.pc=127;return;}
case 127:{f.rearrange(95);f.pc=128;return;}
case 128:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=129;return;}
case 129:{f.push(8033539074353211027L,2);f.pc=130;return;}
case 130:{f.push(f.locals[0],2);f.pc=131;return;}
case 131:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0069.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=132;return;}
case 132:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0069.class,"c",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=133;return;}
case 133:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
