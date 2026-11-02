package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_1304;

// $VF: synthetic class
public class S0075 {
   public static int[] a;
   public static Object[] b;
   public static String[] c;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(3,6);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$0$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=25&&f.pc<35&&error instanceof java.lang.NoSuchFieldError){f.clear();f.push(error,1);f.pc=37;continue;}
if(f.pc>=40&&f.pc<51&&error instanceof java.lang.NoSuchFieldError){f.clear();f.push(error,1);f.pc=53;continue;}
if(f.pc>=56&&f.pc<67&&error instanceof java.lang.NoSuchFieldError){f.clear();f.push(error,1);f.pc=69;continue;}
if(f.pc>=72&&f.pc<83&&error instanceof java.lang.NoSuchFieldError){f.clear();f.push(error,1);f.pc=85;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static Class b(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = a(var0, var2);
      Object var6 = b[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(c[var4]);
            b[var4] = var5;
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

   public static Field c(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = b[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = c[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            b[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  b[var4] = var13;
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
            var12 = b(236738186496915L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = b[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = c[var4];
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
            b[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(236738186496915L, 0L);
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
                  b[var4] = var19;
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
            var23 = b(236738186496915L, 0L);
         }
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

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 203 && var8 != 251 && var8 != 198 && var8 != 212) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 207) {
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
            if (var8 == 203) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 251) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 198) {
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
      return __indy$9(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$10().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/aj" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int a(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (c[var4] != null) {
         return var4;
      }

      Object var5 = b[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 60;
         case 1 -> 48;
         case 2 -> 40;
         case 3 -> 61;
         case 4 -> 57;
         case 5 -> 42;
         case 6 -> 9;
         case 7 -> 35;
         case 8 -> 10;
         case 9 -> 33;
         case 10 -> 1;
         case 11 -> 55;
         case 12 -> 7;
         case 13 -> 43;
         case 14 -> 18;
         case 15 -> 13;
         case 16 -> 31;
         case 17 -> 12;
         case 18 -> 47;
         case 19 -> 8;
         case 20 -> 16;
         case 21 -> 21;
         case 22 -> 11;
         case 23 -> 54;
         case 24 -> 30;
         case 25 -> 14;
         case 26 -> 28;
         case 27 -> 51;
         case 28 -> 15;
         case 29 -> 3;
         case 30 -> 46;
         case 31 -> 27;
         case 32 -> 41;
         case 33 -> 39;
         case 34 -> 38;
         case 35 -> 20;
         case 36 -> 62;
         case 37 -> 17;
         case 38 -> 45;
         case 39 -> 22;
         case 40 -> 63;
         case 41 -> 59;
         case 42 -> 19;
         case 43 -> 37;
         case 44 -> 36;
         case 45 -> 52;
         case 46 -> 32;
         case 47 -> 56;
         case 48 -> 0;
         case 49 -> 24;
         case 50 -> 34;
         case 51 -> 23;
         case 52 -> 44;
         case 53 -> 25;
         case 54 -> 26;
         case 55 -> 53;
         case 56 -> 49;
         case 57 -> 29;
         case 58 -> 5;
         case 59 -> 6;
         case 60 -> 58;
         case 61 -> 50;
         case 62 -> 4;
         default -> 2;
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

      c[var4] = new String(var13);
      return var4;
   
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

   public static void a$return$9b75() {
try {

      b[0] = "<j\u001f(\"1<j\bt.>&!\bj.+!PZ5\u007fl";
      b[1] = "\u000699bGg0\u001c9bP;<\u0013#)P%<\u0006$X\u0002zmAl";
      b[2] = int.class;
      c[2] = "java/lang/Integer";
      b[3] = ":\u0004U\u0013P\u00161\u000bD\\1\u0018:\u0000@\u0006";
      b[4] = "MW\f*\u0007TKV\u0012S\u000f\nF\n\u0007\u0004_R\u0014Rk5X\fR\bT3Y\u0012";
      b[5] = "U]\u0010B%1S\\\u000e;-o^\u0000\u001bl}7\rUw]ziJ\u0002H[{w";
      b[6] = "*\u0007*\u0003!p,\u00064z).!Z!-yvr\u0000M\u001c~(5Xr\u001a\u007f6";
      b[7] = "e@2X.McA,!&\u0013n\u001d9vvK<CUGq\u0015z\u001fjAp\u000b";
      b[8] = "l\u001b<\u0004egj\u001a\"}d\"fC=\u0014gX3D2F:e3Me\u0004\u0003";
      b[9] = "%\bv\"GC#\th[_\u0015'L| !E{\n{dP\u000e)N\u007f[";
   
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

   private static native class_1304[] __indy$0(long var0, long var2) throws Throwable;

   private static native class_1304 __indy$1(long var0, long var2) throws Throwable;

   private static native int __indy$2(Object var0, long var1, long var3) throws Throwable;

   private static native class_1304 __indy$3(long var0, long var2) throws Throwable;

   private static native int __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native class_1304 __indy$5(long var0, long var2) throws Throwable;

   private static native int __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native class_1304 __indy$7(long var0, long var2) throws Throwable;

   private static native int __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$9(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$10() throws Throwable;

private static void __flow$0$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-3381705040352110024L,2);f.pc=1;return;}
case 1:{f.push(-886790620336282559L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(171291236637943L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{f.push(129637010552010L,2);f.pc=8;return;}
case 8:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=9;return;}
case 9:{f.locals[0]=f.pop();f.pc=10;return;}
case 10:{f.push(10,1);f.pc=11;return;}
case 11:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=12;return;}
case 12:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0075.class,"b",((java.lang.Object[])(Object)(f.pop())));f.pc=13;return;}
case 13:{f.push(10,1);f.pc=14;return;}
case 14:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=15;return;}
case 15:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0075.class,"c",((java.lang.String[])(Object)(f.pop())));f.pc=16;return;}
case 16:{dev.zprestige.sourceview.S0075.a$return$9b75();f.pc=17;return;}
case 17:{f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.push(-4181526575648135318L,2);f.pc=20;return;}
case 20:{f.push(f.locals[0],2);f.pc=21;return;}
case 21:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0075.__indy$0(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=22;return;}
case 22:{f.push(java.lang.reflect.Array.getLength(f.pop()),1);f.pc=23;return;}
case 23:{f.push(new int[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=24;return;}
case 24:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0075.class,"a",((int[])(Object)(f.pop())));f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.push(dev.zprestige.sourceview.S0075.a,1);f.pc=27;return;}
case 27:{f.push(-4180706664886476045L,2);f.pc=28;return;}
case 28:{f.push(f.locals[0],2);f.pc=29;return;}
case 29:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0075.__indy$1(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=30;return;}
case 30:{f.push(-4181506859731070327L,2);f.pc=31;return;}
case 31:{f.push(f.locals[0],2);f.pc=32;return;}
case 32:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0075.__indy$2(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=33;return;}
case 33:{f.push(1,1);f.pc=34;return;}
case 34:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((int[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v);f.pc=35;return;}
case 35:{f.pc=36;return;}
case 36:{f.pc=40;return;}
case 37:{f.pc=38;return;}
case 38:{f.pc=39;return;}
case 39:{f.locals[2]=f.pop();f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.push(dev.zprestige.sourceview.S0075.a,1);f.pc=43;return;}
case 43:{f.push(-4180640281420333680L,2);f.pc=44;return;}
case 44:{f.push(f.locals[0],2);f.pc=45;return;}
case 45:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0075.__indy$3(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=46;return;}
case 46:{f.push(-4181506859731070327L,2);f.pc=47;return;}
case 47:{f.push(f.locals[0],2);f.pc=48;return;}
case 48:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0075.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=49;return;}
case 49:{f.push(2,1);f.pc=50;return;}
case 50:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((int[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v);f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{f.pc=56;return;}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.locals[2]=f.pop();f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.push(dev.zprestige.sourceview.S0075.a,1);f.pc=59;return;}
case 59:{f.push(-4180557710159108986L,2);f.pc=60;return;}
case 60:{f.push(f.locals[0],2);f.pc=61;return;}
case 61:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0075.__indy$5(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=62;return;}
case 62:{f.push(-4181506859731070327L,2);f.pc=63;return;}
case 63:{f.push(f.locals[0],2);f.pc=64;return;}
case 64:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0075.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=65;return;}
case 65:{f.push(3,1);f.pc=66;return;}
case 66:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((int[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v);f.pc=67;return;}
case 67:{f.pc=68;return;}
case 68:{f.pc=72;return;}
case 69:{f.pc=70;return;}
case 70:{f.pc=71;return;}
case 71:{f.locals[2]=f.pop();f.pc=72;return;}
case 72:{f.pc=73;return;}
case 73:{f.pc=74;return;}
case 74:{f.push(dev.zprestige.sourceview.S0075.a,1);f.pc=75;return;}
case 75:{f.push(-4180493566647122943L,2);f.pc=76;return;}
case 76:{f.push(f.locals[0],2);f.pc=77;return;}
case 77:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0075.__indy$7(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=78;return;}
case 78:{f.push(-4181506859731070327L,2);f.pc=79;return;}
case 79:{f.push(f.locals[0],2);f.pc=80;return;}
case 80:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0075.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=81;return;}
case 81:{f.push(4,1);f.pc=82;return;}
case 82:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((int[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v);f.pc=83;return;}
case 83:{f.pc=84;return;}
case 84:{f.pc=88;return;}
case 85:{f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.locals[2]=f.pop();f.pc=88;return;}
case 88:{f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
