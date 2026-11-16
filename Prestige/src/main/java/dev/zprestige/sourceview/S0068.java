package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_241;

public class S0068 {
   public static S0121 a;
   public static S0121 b;
   public class_241 c;
   public static long d;
   public static Object[] e;
   public static String[] f;

   public S0068(class_241 var1)  throws Throwable {
try {

      this.c = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(4,8);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
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

   public void b$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      long var4 = (Long)(Object)(var1[1]);
      var4 = d ^ var4;
      long var6 = var4 ^ 14687677248830L;
      S0121 var10000 = b;
      class_241 var10001 = this.c;
      float var10002 = (float)var2;
      Object[] var10005 = new Object[]{null, null, var6};
      var10005[1] = var10002;
      var10005[0] = var10001;
      __indy$4(var10000, var10005, -3639108814911640451L, var4);
   
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
            var12 = b(772895799898571L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void c$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      double var4 = (Double)(Object)(var1[1]);
      long var6 = (Long)(Object)(var1[2]);
      var6 = d ^ var6;
      long var8 = var6 ^ 23437271855005L;
      long var10 = var6 ^ 33415312967430L;
      Object[] var10004 = new Object[]{null, var10};
      var10004[0] = var2;
      __indy$5(this, var10004, -4785185996046984457L, var6);
      var10004 = new Object[]{null, var8};
      var10004[0] = var4;
      __indy$6(this, var10004, -4785301299820105402L, var6);
   
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
            var23 = b(772895799898571L, 0L);
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
            var23 = b(772895799898571L, 0L);
         }
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
         if (var8 != 'o' && var8 != 'w' && var8 != 'R' && var8 != 226) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'b') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 241) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'o') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'w') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'R') {
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

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$7().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/ac" + " : " + var1 + " : " + var2.toString(), var5);
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
      return __indy$8(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
try {

      double var2 = (Double)(Object)(var1[0]);
      long var4 = (Long)(Object)(var1[1]);
      var4 = d ^ var4;
      long var6 = var4 ^ 7075091946917L;
      S0121 var10000 = a;
      class_241 var10001 = this.c;
      float var10002 = (float)var2;
      Object[] var10005 = new Object[]{null, null, var6};
      var10005[1] = var10002;
      var10005[0] = var10001;
      __indy$9(var10000, var10005, -6492991212559170330L, var4);
   
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
         case 0 -> 9;
         case 1 -> 32;
         case 2 -> 4;
         case 3 -> 62;
         case 4 -> 42;
         case 5 -> 21;
         case 6 -> 39;
         case 7 -> 35;
         case 8 -> 23;
         case 9 -> 41;
         case 10 -> 2;
         case 11 -> 6;
         case 12 -> 36;
         case 13 -> 53;
         case 14 -> 37;
         case 15 -> 49;
         case 16 -> 60;
         case 17 -> 13;
         case 18 -> 47;
         case 19 -> 29;
         case 20 -> 26;
         case 21 -> 61;
         case 22 -> 54;
         case 23 -> 50;
         case 24 -> 11;
         case 25 -> 31;
         case 26 -> 27;
         case 27 -> 57;
         case 28 -> 52;
         case 29 -> 15;
         case 30 -> 10;
         case 31 -> 7;
         case 32 -> 34;
         case 33 -> 33;
         case 34 -> 1;
         case 35 -> 40;
         case 36 -> 48;
         case 37 -> 58;
         case 38 -> 12;
         case 39 -> 30;
         case 40 -> 38;
         case 41 -> 0;
         case 42 -> 43;
         case 43 -> 17;
         case 44 -> 51;
         case 45 -> 8;
         case 46 -> 46;
         case 47 -> 44;
         case 48 -> 55;
         case 49 -> 22;
         case 50 -> 59;
         case 51 -> 25;
         case 52 -> 20;
         case 53 -> 63;
         case 54 -> 14;
         case 55 -> 45;
         case 56 -> 56;
         case 57 -> 24;
         case 58 -> 19;
         case 59 -> 16;
         case 60 -> 28;
         case 61 -> 18;
         case 62 -> 5;
         default -> 3;
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

      e[0] = "\u0017d_b\n3\u0001dZ8\u0019$\u0016/Y>\u00150\u0007hN)^!!";
      e[1] = "=[\u000f.\u007fwH{\u0004!n8)u\u000f*jb]";
      e[2] = void.class;
      f[2] = "java/lang/Void";
      e[3] = "\u001fX!p\u0012\u0004\tX$*\u0001\u0013\u001e\u0013',\r\u0007\u000fT0;F\u0017-";
      e[4] = "!\u0019\u0006.O1T9\r!^~57\u0006*Z$A";
      e[5] = "Qw#s#^Zx2<K^Tw!";
      e[6] = "\n\u000b:L&O\u0001\u0004+\u0003KO\u0001\u0019?";
      e[7] = "\u000f\u0001\u001b33\"\u0019\u0001\u001ei 5\u000eJ\u001do,!\u001f\r\nxg3\b";
      e[8] = "_\u0004I'@1*$B(Q~K*I#U$?";
      e[9] = ".\u0012e\u0011sX[2n\u001eb\u0017:<e\u0015fMN";
      e[10] = "X\u0010\u0006Z\u0019sS\u001f\u0017\u0015x}X\u0014\u0013O";
      e[11] = "\u0006l\u0007Mq\u0003\u0003\u007f\u0010G\u001a lL8!&\u0017\u0006z\u001eCp\u0015Qj";
      e[12] = "/\u0014\u00077\u0005\u0018|PN<|O\u0013TTu\u0000I}ZP2\u0017%*\u0016\u0004wGEv\u0003M(|";
      e[13] = "d-E.Q@z/LUQz9<\u0016)W\u001478Q>;C{l\u0014n[\u001fn%KU";
      e[14] = "H\r\u001f\u0005o\u0006\u001bIV\u000e\u0016RtMLGjW\u001aCH\u0000};M\u000f\u001cE-[\u0011\u001aU\u001a\u0016";
      e[15] = "\u0010\u0001\u0000=I;\u0012\u0015\r-7+*T\u0002yK.DZ\u0006>\\BI\u0010\u0003;]xW\u0012\n@";
   
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

   private static native void __indy$4(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$7() throws Throwable;

   private static native Object __indy$8(MethodHandle var0, Object[] var1) throws Throwable;

   private static native void __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(6665325078947719681L,2);f.pc=1;return;}
case 1:{f.push(-5852937042992085161L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(63339520958854L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0068.class,"d",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0068.d,2);f.pc=9;return;}
case 9:{f.push(24313787698701L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[0]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[0],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(25031919922735L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(88);f.pc=18;return;}
case 18:{f.push(16,1);f.pc=19;return;}
case 19:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=20;return;}
case 20:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0068.class,"e",((java.lang.Object[])(Object)(f.pop())));f.pc=21;return;}
case 21:{f.push(16,1);f.pc=22;return;}
case 22:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=23;return;}
case 23:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0068.class,"f",((java.lang.String[])(Object)(f.pop())));f.pc=24;return;}
case 24:{dev.zprestige.sourceview.S0068.a$return$9b75();f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.push(net.minecraft.class_241.class,1);f.pc=28;return;}
case 28:{f.push("x",1);f.pc=29;return;}
case 29:{f.push(-3352125551217049476L,2);f.pc=30;return;}
case 30:{f.push(f.locals[0],2);f.pc=31;return;}
case 31:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0068.__indy$0(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=32;return;}
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
case 57:{f.push(-3351821069355091048L,2);f.pc=58;return;}
case 58:{f.push(f.locals[0],2);f.pc=59;return;}
case 59:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0068.__indy$1(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=60;return;}
case 60:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0068.class,"a",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.push(net.minecraft.class_241.class,1);f.pc=64;return;}
case 64:{f.push("y",1);f.pc=65;return;}
case 65:{f.push(-3352125551217049476L,2);f.pc=66;return;}
case 66:{f.push(f.locals[0],2);f.pc=67;return;}
case 67:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0068.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=68;return;}
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
case 93:{f.push(-3351821069355091048L,2);f.pc=94;return;}
case 94:{f.push(f.locals[0],2);f.pc=95;return;}
case 95:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0068.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=96;return;}
case 96:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0068.class,"b",((dev.zprestige.sourceview.S0121)(Object)(f.pop())));f.pc=97;return;}
case 97:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
