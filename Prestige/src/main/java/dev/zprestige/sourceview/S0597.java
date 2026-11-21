package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class S0597 extends EnumBase {
   public static S0597 X;
   public static S0597 Y;
   public static S0597[] a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   public S0597(String var1, int var2)  throws Throwable {
      super(var1, var2);
try {

   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(2,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static S0597[] values() {
try {

      return (S0597[])(Object)(a.clone());
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0597 valueOf(String var0, long var1) {
try {

      var1 = b ^ var1;
      return (S0597)(Object)(__indy$1(S0597.class, var0, 1342853677028775757L, var1));
   
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
            var12 = b(470227436916079L, 0L);
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
            var23 = b(470227436916079L, 0L);
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
            var23 = b(470227436916079L, 0L);
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
         if (var8 != 'S' && var8 != 231 && var8 != 196 && var8 != 'C') {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 203) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 163) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'S') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 231) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 196) {
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
               MethodHandles.insertArguments(__indy$2().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/w" + " : " + var1 + " : " + var2.toString(), var5);
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
      return __indy$3(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0597[] a$return$4b619cb9(Object[] var0) {
try {

      return new S0597[]{X, Y};
   
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
         case 0 -> 12;
         case 1 -> 57;
         case 2 -> 31;
         case 3 -> 19;
         case 4 -> 61;
         case 5 -> 26;
         case 6 -> 7;
         case 7 -> 10;
         case 8 -> 45;
         case 9 -> 38;
         case 10 -> 3;
         case 11 -> 4;
         case 12 -> 53;
         case 13 -> 36;
         case 14 -> 5;
         case 15 -> 54;
         case 16 -> 2;
         case 17 -> 35;
         case 18 -> 39;
         case 19 -> 30;
         case 20 -> 32;
         case 21 -> 43;
         case 22 -> 48;
         case 23 -> 44;
         case 24 -> 14;
         case 25 -> 28;
         case 26 -> 34;
         case 27 -> 55;
         case 28 -> 20;
         case 29 -> 22;
         case 30 -> 62;
         case 31 -> 15;
         case 32 -> 27;
         case 33 -> 52;
         case 34 -> 37;
         case 35 -> 58;
         case 36 -> 23;
         case 37 -> 18;
         case 38 -> 6;
         case 39 -> 13;
         case 40 -> 60;
         case 41 -> 63;
         case 42 -> 59;
         case 43 -> 21;
         case 44 -> 16;
         case 45 -> 41;
         case 46 -> 42;
         case 47 -> 50;
         case 48 -> 33;
         case 49 -> 17;
         case 50 -> 1;
         case 51 -> 25;
         case 52 -> 51;
         case 53 -> 49;
         case 54 -> 56;
         case 55 -> 29;
         case 56 -> 8;
         case 57 -> 0;
         case 58 -> 11;
         case 59 -> 46;
         case 60 -> 47;
         case 61 -> 40;
         case 62 -> 24;
         default -> 9;
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

   public static void a$return$9b75() {
try {

      c[0] = "y\u0013?ykBr\u001c.6\u0000@f\u001f";
      c[1] = "(-ACp\u000f#\"P\f\u001d\u000f#?D";
      c[2] = "xWD4\u0012BsXU{oZ`_\\2";
      c[3] = "yC\u000f\u00171\u0017oC\nM\"\u0000x\b\tK.\u0014iO\u001e\\e\u0010";
      c[4] = "up_t(#\u0000PT{9la^_p=6\u0015";
      c[5] = "RMw-O\u0010sqa-JJ`fvfILlrg!^['v(";
      c[6] = "P\u001biCqX[\u0014x\f\u0010VP\u001f|V";
      c[7] = "+7|W\u0001=/5\f[\u000f=;bKKf`vwlFY%<p3%_''ihT\u001b >q\fH^)4hhL\\Y";
      c[8] = ">]7\f\u0004YyV8YnI\u0004V6C\u0012N|\u001fwNW ?\u001fr\u000e\u0002N?U6Wn";
   
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

   private static native S0597[] __indy$0(Object var0, long var1, long var3) throws Throwable;

   private static native Enum __indy$1(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$2() throws Throwable;

   private static native Object __indy$3(MethodHandle var0, Object[] var1) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(6583536290456598144L,2);f.pc=1;return;}
case 1:{f.push(5952309712979135903L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(278997931315264L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0597.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0597.b,2);f.pc=9;return;}
case 9:{f.push(50641156663233L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[0]=f.pop();f.pc=12;return;}
case 12:{f.push(9,1);f.pc=13;return;}
case 13:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=14;return;}
case 14:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0597.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=15;return;}
case 15:{f.push(9,1);f.pc=16;return;}
case 16:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=17;return;}
case 17:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0597.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=18;return;}
case 18:{dev.zprestige.sourceview.S0597.a$return$9b75();f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0597.class),1);f.pc=22;return;}
case 22:{f.rearrange(89);f.pc=23;return;}
case 23:{f.push("X",1);f.pc=24;return;}
case 24:{f.push(0,1);f.pc=25;return;}
case 25:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.lang.String.class,int.class},new Object[]{((java.lang.String)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1)});f.pc=26;return;}
case 26:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0597.class,"X",((dev.zprestige.sourceview.S0597)(Object)(f.pop())));f.pc=27;return;}
case 27:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0597.class),1);f.pc=28;return;}
case 28:{f.rearrange(89);f.pc=29;return;}
case 29:{f.push("Y",1);f.pc=30;return;}
case 30:{f.push(1,1);f.pc=31;return;}
case 31:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.lang.String.class,int.class},new Object[]{((java.lang.String)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1)});f.pc=32;return;}
case 32:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0597.class,"Y",((dev.zprestige.sourceview.S0597)(Object)(f.pop())));f.pc=33;return;}
case 33:{f.push(0,1);f.pc=34;return;}
case 34:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=35;return;}
case 35:{f.push(-7845146912454044308L,2);f.pc=36;return;}
case 36:{f.push(f.locals[0],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0597.__indy$0(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0597.class,"a",((dev.zprestige.sourceview.S0597[])(Object)(f.pop())));f.pc=39;return;}
case 39:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
