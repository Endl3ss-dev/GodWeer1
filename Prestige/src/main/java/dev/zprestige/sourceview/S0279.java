package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

public class S0279 {
   public static Random a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
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

   public static float b$return$e4b0898c(Object[] var0) {
try {

      float var5 = (Float)(Object)(var0[0]);
      float var1 = (Float)(Object)(var0[1]);
      float var4 = (Float)(Object)(var0[2]);
      long var2 = (Long)(Object)(var0[3]);
      var2 = b ^ var2;
      return __indy$1(var1, __indy$0(var4, var5, 6135836462948911469L, var2), 6135758178053928434L, var2);
   
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
            var12 = b(438618385493748L, 0L);
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
            var23 = b(438618385493748L, 0L);
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
            var23 = b(438618385493748L, 0L);
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
               MethodHandles.insertArguments(__indy$2().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/dn" + " : " + var1 + " : " + var2.toString(), var5);
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

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 253 && var8 != 238 && var8 != 'T' && var8 != 'Q') {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 204) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'E') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 253) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 238) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'T') {
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

   public static float a$return$e4b0898c(Object[] var0) {
try {

      float var4 = (Float)(Object)(var0[0]);
      float var3 = (Float)(Object)(var0[1]);
      long var1 = (Long)(Object)(var0[2]);
      var1 = b ^ var1;
      long var5 = var1 ^ 60111729527913L;
      float var10000 = var4 + __indy$4(a, -2701091372180189491L, var1) * (var3 - var4);
      Object[] var10005 = new Object[]{null, null, null, var5};
      var10005[2] = var3;
      var10005[1] = var4;
      var10005[0] = var10000;
      return __indy$5(var10005, -2702143923643703048L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int a$return$e4b0898f(Object[] var0) {
try {

      int var1 = (Integer)(Object)(var0[0]);
      int var4 = (Integer)(Object)(var0[1]);
      long var2 = (Long)(Object)(var0[2]);
      var2 = b ^ var2;
      return var1 + __indy$6(a, var4 - var1 + 1, -2165051429859061405L, var2);
   
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
         case 0 -> 31;
         case 1 -> 1;
         case 2 -> 52;
         case 3 -> 43;
         case 4 -> 19;
         case 5 -> 54;
         case 6 -> 33;
         case 7 -> 53;
         case 8 -> 41;
         case 9 -> 7;
         case 10 -> 35;
         case 11 -> 29;
         case 12 -> 60;
         case 13 -> 42;
         case 14 -> 55;
         case 15 -> 10;
         case 16 -> 11;
         case 17 -> 36;
         case 18 -> 44;
         case 19 -> 28;
         case 20 -> 34;
         case 21 -> 25;
         case 22 -> 57;
         case 23 -> 6;
         case 24 -> 2;
         case 25 -> 9;
         case 26 -> 63;
         case 27 -> 20;
         case 28 -> 38;
         case 29 -> 49;
         case 30 -> 47;
         case 31 -> 21;
         case 32 -> 8;
         case 33 -> 3;
         case 34 -> 15;
         case 35 -> 50;
         case 36 -> 27;
         case 37 -> 59;
         case 38 -> 40;
         case 39 -> 17;
         case 40 -> 30;
         case 41 -> 4;
         case 42 -> 23;
         case 43 -> 37;
         case 44 -> 0;
         case 45 -> 62;
         case 46 -> 61;
         case 47 -> 56;
         case 48 -> 58;
         case 49 -> 48;
         case 50 -> 14;
         case 51 -> 24;
         case 52 -> 16;
         case 53 -> 26;
         case 54 -> 45;
         case 55 -> 46;
         case 56 -> 51;
         case 57 -> 18;
         case 58 -> 32;
         case 59 -> 5;
         case 60 -> 39;
         case 61 -> 12;
         case 62 -> 13;
         default -> 22;
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

      c[0] = "\u000e-s)S\u001f\u0005\"bf0\u0012\u0010$";
      c[1] = float.class;
      d[1] = "java/lang/Float";
      c[2] = "\u0016%\n;rw\u0000%\u000faa`\u0017n\fgmt\u0006)\u001bp&c\u001c";
      c[3] = "iX6\n\u0011 \u001cx=\u0005\u0000o}v6\u000e\u00045\t";
      c[4] = "\u0002,2ewZ\u001c$(*\u000bN\u0006)+i";
      c[5] = int.class;
      d[5] = "java/lang/Integer";
      c[6] = "Ak@k\u0005.JdQ$d AoU~";
      c[7] = "~C']\u001eI!R(FrO(Xjh\u0016N,T\u0016X\u001fX'\u0015,B\u0014\u0015E";
      c[8] = ".xjp 7)da2[;\u0017>6%%+oz`8fQiiw*fksb:H";
      c[9] = "]<-\u007f<d\u0002-\"dPb\u000b'`E6xfk%h!c\u0002:\u007f|?\u0004Znpu7`\u000b4dkP";
      c[10] = "se_\tgu3{;UeaJ|VAn,pf]\f\fo'sY\r6u,>;Na`(?\u0001Tj-J";
      c[11] = "\u001e\r(\u0015rN^\u0013LIxL'\u0014!]{\u0017\u001d\u000e*\u0010\u0019TJ\u001b.\u0011#NAVLRt[EWvH\u007f\u0016'";
   
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

   private static native float __indy$0(float var0, float var1, long var2, long var4) throws Throwable;

   private static native float __indy$1(float var0, float var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$2() throws Throwable;

   private static native Object __indy$3(MethodHandle var0, Object[] var1) throws Throwable;

   private static native float __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native float __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$6(Object var0, int var1, long var2, long var4) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-835873860770747940L,2);f.pc=1;return;}
case 1:{f.push(-7319719085665765025L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(179895409274136L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0279.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(12,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0279.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(12,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0279.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0279.a$return$9b75();f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.util.Random.class),1);f.pc=18;return;}
case 18:{f.rearrange(89);f.pc=19;return;}
case 19:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=20;return;}
case 20:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0279.class,"a",((java.util.Random)(Object)(f.pop())));f.pc=21;return;}
case 21:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
