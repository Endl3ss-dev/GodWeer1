package dev.zprestige.sourceview;

import java.awt.Color;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class S0259 {
   public ArrayList a = new ArrayList();
   public static long db;
   public static Object[] sb;
   public static String[] tb;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static void e() {
try {

      sb[0] = "u',Mgqk/6\u0002\bvm'#` wk";
      sb[1] = "Sv\u0004x6\nXy\u00157W\u0004Sr\u0011m";
      sb[2] = boolean.class;
      tb[2] = "java/lang/Boolean";
      sb[3] = "<'o\u0012i\t25/kd\u001daU+Sh\u001218}\u0005d\u0012\rl`[v\u0015t:sWiq";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int e(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (tb[var4] != null) {
         return var4;
      }

      Object var5 = sb[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 0;
         case 1 -> 54;
         case 2 -> 17;
         case 3 -> 41;
         case 4 -> 14;
         case 5 -> 55;
         case 6 -> 21;
         case 7 -> 30;
         case 8 -> 25;
         case 9 -> 10;
         case 10 -> 40;
         case 11 -> 44;
         case 12 -> 29;
         case 13 -> 20;
         case 14 -> 50;
         case 15 -> 53;
         case 16 -> 35;
         case 17 -> 23;
         case 18 -> 28;
         case 19 -> 32;
         case 20 -> 1;
         case 21 -> 61;
         case 22 -> 3;
         case 23 -> 6;
         case 24 -> 12;
         case 25 -> 8;
         case 26 -> 45;
         case 27 -> 60;
         case 28 -> 43;
         case 29 -> 11;
         case 30 -> 46;
         case 31 -> 15;
         case 32 -> 33;
         case 33 -> 24;
         case 34 -> 18;
         case 35 -> 4;
         case 36 -> 19;
         case 37 -> 26;
         case 38 -> 63;
         case 39 -> 47;
         case 40 -> 34;
         case 41 -> 52;
         case 42 -> 49;
         case 43 -> 37;
         case 44 -> 31;
         case 45 -> 9;
         case 46 -> 57;
         case 47 -> 7;
         case 48 -> 59;
         case 49 -> 48;
         case 50 -> 38;
         case 51 -> 5;
         case 52 -> 27;
         case 53 -> 58;
         case 54 -> 16;
         case 55 -> 36;
         case 56 -> 42;
         case 57 -> 2;
         case 58 -> 22;
         case 59 -> 13;
         case 60 -> 51;
         case 61 -> 56;
         case 62 -> 62;
         default -> 39;
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

      tb[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = b(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$0(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'C' && var8 != 'c' && var8 != 217 && var8 != 'd') {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 238) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 234) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = g(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'C') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'c') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 217) {
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

   public static Method c(Class var0, String var1, Class var2, int var3, Class[] var4) {
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

   public static Field c(Class var0, String var1, Class var2) {
try {

      for (Field var6 : var0.getDeclaredFields()) {
         if (var6.getName().equals(var1) && var6.getType() == var2) {
            return var6;
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method h(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = sb[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = tb[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
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
         var15 = f(Long.parseLong(var6.substring(var12, var17), 36), 0L);
         if (var16 < var13) {
            var14[var16] = var15;
         }

         var12 = var17 + 1;
      }

      Class var23 = var8;

      while (true) {
         Method var26 = c(var23, var10, var15, var13, var14);
         if (var26 != null) {
            sb[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = f(103327770070892L, 0L);
            break;
         }
      }

      var23 = var8;

      while (true) {
         Class[] var27;
         if ((var27 = var23.getInterfaces()) != null) {
            for (int var18 = 0; var18 < var27.length; var18++) {
               Method var19 = d(var27[var18], var10, var15, var13, var14);
               if (var19 != null) {
                  sb[var4] = var19;
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
            var23 = f(103327770070892L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite f(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$1().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/dU" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class f(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = e(var0, var2);
      Object var6 = sb[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(tb[var4]);
            sb[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field d(Class var0, String var1, Class var2) {
try {

      Field var3 = c(var0, var1, var2);
      if (var3 != null) {
         return var3;
      }

      Class[] var4 = var0.getInterfaces();
      if (var4 != null) {
         for (int var5 = 0; var5 < var4.length; var5++) {
            var3 = d(var4[var5], var1, var2);
            if (var3 != null) {
               return var3;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(Class var0, String var1, Class var2, int var3, Class[] var4) {
try {

      Method var5 = c(var0, var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      }

      Class[] var6 = var0.getInterfaces();
      if (var6 != null) {
         for (int var7 = 0; var7 < var6.length; var7++) {
            var5 = d(var6[var7], var1, var2, var3, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0254 a$return$99ec19e1(Object[] var1) {
try {

      String var4 = (String)(Object)(var1[0]);
      int var3 = (Integer)(Object)(var1[1]);
      int var2 = (Integer)(Object)(var1[2]);
      int var7 = (Integer)(Object)(var1[3]);
      long var5 = (Long)(Object)(var1[4]);
      var5 = db ^ var5;
      S0254 var8 = new S0254(var4, var3, var2, var7);
      __indy$2(this.a, var8, -3589982973330092586L, var5);
      return var8;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0255 a$return$99ec1a00(Object[] var1) {
try {

      String var5 = (String)(Object)(var1[0]);
      float var7 = (Float)(Object)(var1[1]);
      float var8 = (Float)(Object)(var1[2]);
      float var2 = (Float)(Object)(var1[3]);
      float var6 = (Float)(Object)(var1[4]);
      long var3 = (Long)(Object)(var1[5]);
      var3 = db ^ var3;
      S0255 var9 = new S0255(var5, var7, var8, var2, var6);
      __indy$3(this.a, var9, -1320194426390139306L, var3);
      return var9;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0256 a$return$99ec1a1f(Object[] var1) {
try {

      String var4 = (String)(Object)(var1[0]);
      String var2 = (String)(Object)(var1[1]);
      String[] var3 = (String[])(Object)(var1[2]);
      long var5 = (Long)(Object)(var1[3]);
      var5 = db ^ var5;
      S0256 var7 = new S0256(var4, var2, var3);
      __indy$4(this.a, var7, 4763309020714697185L, var5);
      return var7;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0258 a$return$99ec1a5d(Object[] var1) {
try {

      String var2 = (String)(Object)(var1[0]);
      String var4 = (String)(Object)(var1[1]);
      String var3 = (String)(Object)(var1[2]);
      long var5 = (Long)(Object)(var1[3]);
      var5 = db ^ var5;
      S0258 var7 = new S0258(var2, var4, var3);
      __indy$5(this.a, var7, 38508153071616883L, var5);
      return var7;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0253 a$return$99ec19c2(Object[] var1) {
try {

      String var6 = (String)(Object)(var1[0]);
      float var5 = (Float)(Object)(var1[1]);
      float var7 = (Float)(Object)(var1[2]);
      float var2 = (Float)(Object)(var1[3]);
      long var3 = (Long)(Object)(var1[4]);
      var3 = db ^ var3;
      S0253 var8 = new S0253(var6, var5, var7, var2);
      __indy$6(this.a, var8, 5636710283913072066L, var3);
      return var8;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0251 a$return$99ec1984(Object[] var1) {
try {

      String var2 = (String)(Object)(var1[0]);
      boolean var3 = (Boolean)(Object)(var1[1]);
      long var4 = (Long)(Object)(var1[2]);
      var4 = db ^ var4;
      S0251 var6 = new S0251(var2, var3);
      __indy$7(this.a, var6, 4397375012673322749L, var4);
      return var6;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0250 a$return$99ec1965(Object[] var1) {
try {

      String var2 = (String)(Object)(var1[0]);
      int var5 = (Integer)(Object)(var1[1]);
      long var3 = (Long)(Object)(var1[2]);
      var3 = db ^ var3;
      S0250 var6 = new S0250(var2, var5);
      __indy$8(this.a, var6, 1689021754461213835L, var3);
      return var6;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0257 a$return$99ec1a3e(Object[] var1) {
try {

      String var4 = (String)(Object)(var1[0]);
      String[] var3 = (String[])(Object)(var1[1]);
      boolean[] var2 = (boolean[])(Object)(var1[2]);
      long var5 = (Long)(Object)(var1[3]);
      var5 = db ^ var5;
      S0257 var7 = new S0257(var4, var3, var2);
      __indy$9(this.a, var7, -6525033372468205L, var5);
      return var7;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0252 a$return$99ec19a3(Object[] var1) {
try {

      String var4 = (String)(Object)(var1[0]);
      Color var5 = (Color)(Object)(var1[1]);
      long var2 = (Long)(Object)(var1[2]);
      var2 = db ^ var2;
      S0252 var6 = new S0252(var4, var5);
      __indy$10(this.a, var6, 4276711985950718114L, var2);
      return var6;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field g(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = sb[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = tb[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = f(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = c(var12, var10, var11);
         if (var13 != null) {
            sb[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = d(var14[var15], var10, var11);
               if (var13 != null) {
                  sb[var4] = var13;
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
            var12 = f(103327770070892L, 0L);
         }
      }
   
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

   private static native Object __indy$0(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$1() throws Throwable;

   private static native boolean __indy$2(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$4(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$7(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$8(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$10(Object var0, Object var1, long var2, long var4) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(4453334024007971215L,2);f.pc=1;return;}
case 1:{f.push(-4153632545621367162L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(272288397011182L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0259.class,"db",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(4,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0259.class,"sb",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(4,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0259.class,"tb",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0259.e();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
