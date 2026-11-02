package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_310;

public abstract class S0268 {
   public static class_310 a;
   public static long b;
   public S0388 c;
   public static long o;
   public static Object[] B;
   public static String[] C;

   public S0268(String var1, String var2, boolean var3, long var4)  throws Throwable {
      var4 = o ^ var4;
      long var6 = var4 ^ 24988614648383L;
      super();
try {

      Object[] var10006 = new Object[]{null, null, null, var6};
      var10006[2] = var3;
      var10006[1] = var2;
      var10006[0] = var1;
      this.c = __indy$0(var10006, 7300427087011319242L, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0268(String var1, String var2, long var3)  throws Throwable {
      var3 = o ^ var3;
      long var5 = var3 ^ 42604230717095L;
      this(var1, var2, false, var5);
try {

   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(2,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$2$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static CallSite e(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$3().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/dc" + " : " + var1 + " : " + var2.toString(), var5);
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

   public static Class b(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = a(var0, var2);
      Object var6 = B[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(C[var4]);
            B[var4] = var5;
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
      Object var5 = B[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = C[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            B[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  B[var4] = var13;
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
            var12 = b(441413935791507L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = B[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = C[var4];
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
            B[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(441413935791507L, 0L);
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
                  B[var4] = var19;
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
            var23 = b(441413935791507L, 0L);
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
         if (var8 != 200 && var8 != 'V' && var8 != 165 && var8 != 207) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'D') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 225) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 200) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'V') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 165) {
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
      if (C[var4] != null) {
         return var4;
      }

      Object var5 = B[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 48;
         case 1 -> 32;
         case 2 -> 5;
         case 3 -> 10;
         case 4 -> 1;
         case 5 -> 41;
         case 6 -> 19;
         case 7 -> 35;
         case 8 -> 17;
         case 9 -> 29;
         case 10 -> 22;
         case 11 -> 23;
         case 12 -> 57;
         case 13 -> 36;
         case 14 -> 39;
         case 15 -> 8;
         case 16 -> 2;
         case 17 -> 47;
         case 18 -> 3;
         case 19 -> 25;
         case 20 -> 14;
         case 21 -> 49;
         case 22 -> 20;
         case 23 -> 38;
         case 24 -> 56;
         case 25 -> 37;
         case 26 -> 26;
         case 27 -> 15;
         case 28 -> 46;
         case 29 -> 21;
         case 30 -> 6;
         case 31 -> 24;
         case 32 -> 54;
         case 33 -> 28;
         case 34 -> 9;
         case 35 -> 51;
         case 36 -> 30;
         case 37 -> 60;
         case 38 -> 43;
         case 39 -> 0;
         case 40 -> 40;
         case 41 -> 33;
         case 42 -> 44;
         case 43 -> 53;
         case 44 -> 16;
         case 45 -> 27;
         case 46 -> 55;
         case 47 -> 4;
         case 48 -> 31;
         case 49 -> 61;
         case 50 -> 59;
         case 51 -> 58;
         case 52 -> 12;
         case 53 -> 42;
         case 54 -> 18;
         case 55 -> 34;
         case 56 -> 63;
         case 57 -> 52;
         case 58 -> 62;
         case 59 -> 13;
         case 60 -> 45;
         case 61 -> 11;
         case 62 -> 7;
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

      C[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = a(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$4(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      B[0] = "7\u001cb\u0000W\u001f7\u001cu\\[\u0010-WuB[\u0005*&%\u001f\n";
      B[1] = "\u000f\b\\7S\t\u0004\u0007Mx.\u001c\u0016\u001dO;";
      B[2] = long.class;
      C[2] = "java/lang/Long";
      B[3] = "\u001awR^ad\fwW\u0004rs\u001b<T\u0002~g\n{C\u00155r-";
      B[4] = "\u0012V?ctdgv4le+\u0006x?gaqr";
      B[5] = "l\nU0H\bz\nPj[\u001fmASlW\u000b|\u0006D{\u001c\u001e\\";
      B[6] = "z,\u001e^s\u0019q#\u000f\u0011\u0012\u0017z(\u000bK";
      B[7] = "\u0013\u0002\u000b$E;\u001f\u000759\u0011,O^Y\u000bEm\u0012\u00005`GnW\u0000^lBP";
      B[8] = "j\u0016\u0017\u000f;L\u007fI\u0018h5A`T\u0010\u000e\"`{K\u0010-?X~O\u0006hg_v\u0012F\u00160ZyC}";
      B[9] = "a{G)I\u000bc\u007fWq(\u0006[9\u0006{H\u000573\u0007l\u0012o`s\u0006i\u0016\u0014=s\u0007v(";
   
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

   public S0388 a$return$99ec21df(Object[] var1) {
try {

      return this.c;
   
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

   private static native S0388 __indy$0(Object var0, long var1, long var3) throws Throwable;

   private static native class_310 __indy$1(long var0, long var2) throws Throwable;

   private static native long __indy$2(long var0, long var2) throws Throwable;

   private static native MethodHandle __indy$3() throws Throwable;

   private static native Object __indy$4(MethodHandle var0, Object[] var1) throws Throwable;

private static void __flow$2$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-320902995486953408L,2);f.pc=1;return;}
case 1:{f.push(-3923300758080057942L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(84168822287594L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0268.class,"o",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0268.o,2);f.pc=9;return;}
case 9:{f.push(121908975678478L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[0]=f.pop();f.pc=12;return;}
case 12:{f.push(10,1);f.pc=13;return;}
case 13:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=14;return;}
case 14:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0268.class,"B",((java.lang.Object[])(Object)(f.pop())));f.pc=15;return;}
case 15:{f.push(10,1);f.pc=16;return;}
case 16:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=17;return;}
case 17:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0268.class,"C",((java.lang.String[])(Object)(f.pop())));f.pc=18;return;}
case 18:{dev.zprestige.sourceview.S0268.a$return$9b75();f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.push(-6243794992293665190L,2);f.pc=22;return;}
case 22:{f.push(f.locals[0],2);f.pc=23;return;}
case 23:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0268.__indy$1(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=24;return;}
case 24:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0268.class,"a",((net.minecraft.class_310)(Object)(f.pop())));f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.push(-6243600345756954100L,2);f.pc=28;return;}
case 28:{f.push(f.locals[0],2);f.pc=29;return;}
case 29:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0268.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),2);f.pc=30;return;}
case 30:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0268.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=31;return;}
case 31:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
