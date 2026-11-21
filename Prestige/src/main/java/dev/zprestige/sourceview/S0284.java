package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;
import net.fabricmc.fabric.api.event.Event;
import org.jetbrains.annotations.NotNull;

public final class S0284 {
   public static Event a;
   public static Event b;
   public static Event c;
   public static long d;
   public static Object[] e;
   public static String[] f;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(4,6);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$0$0(f);break;
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
            var12 = b(880928787272768L, 0L);
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
            var23 = b(880928787272768L, 0L);
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
            var23 = b(880928787272768L, 0L);
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
         if (var8 != 220 && var8 != 236 && var8 != 'j' && var8 != 163) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 221) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'l') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 220) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 236) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'j') {
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
               MethodHandles.insertArguments(__indy$3().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/dr" + " : " + var1 + " : " + var2.toString(), var5);
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
      return __indy$4(var8.asSpreader(Object[].class, var4.length), var4);
   
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
         case 0 -> 12;
         case 1 -> 63;
         case 2 -> 35;
         case 3 -> 31;
         case 4 -> 62;
         case 5 -> 6;
         case 6 -> 25;
         case 7 -> 9;
         case 8 -> 49;
         case 9 -> 1;
         case 10 -> 2;
         case 11 -> 52;
         case 12 -> 8;
         case 13 -> 19;
         case 14 -> 43;
         case 15 -> 60;
         case 16 -> 21;
         case 17 -> 16;
         case 18 -> 58;
         case 19 -> 34;
         case 20 -> 11;
         case 21 -> 23;
         case 22 -> 3;
         case 23 -> 5;
         case 24 -> 32;
         case 25 -> 44;
         case 26 -> 20;
         case 27 -> 26;
         case 28 -> 13;
         case 29 -> 48;
         case 30 -> 27;
         case 31 -> 36;
         case 32 -> 59;
         case 33 -> 28;
         case 34 -> 4;
         case 35 -> 22;
         case 36 -> 54;
         case 37 -> 14;
         case 38 -> 50;
         case 39 -> 37;
         case 40 -> 18;
         case 41 -> 47;
         case 42 -> 0;
         case 43 -> 55;
         case 44 -> 61;
         case 45 -> 46;
         case 46 -> 24;
         case 47 -> 51;
         case 48 -> 42;
         case 49 -> 57;
         case 50 -> 29;
         case 51 -> 39;
         case 52 -> 40;
         case 53 -> 33;
         case 54 -> 41;
         case 55 -> 53;
         case 56 -> 17;
         case 57 -> 15;
         case 58 -> 45;
         case 59 -> 38;
         case 60 -> 10;
         case 61 -> 56;
         case 62 -> 7;
         default -> 30;
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

      e[0] = "\u0002M(Q\u001aV\u0014M-\u000b\tA\u0003\u0006.\r\u0005U\u0012A9\u001aNG.";
      e[1] = "q}k'\u0006\f\u0004]`(\u0017CyEs/\u001e\n\u0011";
      e[2] = "|(\\\u0004^\\j(Y^MK}cZXA_l$MO\nHk";
      e[3] = "\u0002\u0001K`\b\u0004\u0014\u0001N:\u001b\u0013\u0003JM<\u0017\u0007\u0012\rZ+\\\u0015\u0017";
      e[4] = "j\u0003a\b7\u0003|\u0003dR$\u0014kHgT(\u0000z\u000fpCc\u0014E";
      e[5] = void.class;
      f[5] = "java/lang/Void";
      e[6] = "M\u0007Ur\u0016-A\u0010H?\u001d/\r\u0004@>\u0002%@L@,\u0019bF\u0014D2\u0004bf\u0014D2\u0004\nB\u0001U3\u00025";
      e[7] = "Yu\u001dv>\u001aRz\f9S\u001aRg\u0018";
      e[8] = "$\tZ<=P:\u0001@suP \u000bX4|K`.Y3pQ'\u0007B";
      e[9] = "\u0002]=S#g\u000eJ \u001e(eB^(\u001f7o\u000f\u0016(\r,(\tN,\u00131()N,\u00131";
      e[10] = " pfi#k6pc30|!;`5<h0|w\"w\u007f6";
      e[11] = "\u0016~>cm&c^5l|i\u0002P>gx3v";
      e[12] = "\u001fc|nYc\u0014lm!8m\u001fgi{";
      e[13] = "\u0007=@'>N\u001b/\u0007E1/^4\u0010)kB\u001e&\u0016t[";
      e[14] = "P 8\u007f\u0010\u000fS!m8.\u0016\u0016}i;C4\u0016ji6d\u0014\u0007sm+.@\u0006|k*\u001e\u0005\u0003!~G\u0013\f\u000enb}V\u0003\b*\u0000yM\u0006\u0007ok,@\u0010\u001e\u0010";
      e[15] = "k.\u0014~CL>/X2/[T{\u0019|UR71]fO2j(^gPY?%H~/";
      e[16] = "\ff>iM\u0013\rm< 6D5<hbVDLxj*N-\u000e7,!P_R{,n6\u0016Ki9/M\u001cN~l\u0010";
   
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

   @NotNull
   public static Event a$return$856489c5(Object[] var0) {
try {

      long var1 = (Long)(Object)(var0[0]);
      var1 = d ^ var1;
      return __indy$6(S0285.class, __indy$5(), 9044267085574317955L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void lambda$makeSimpleEvent$0(S0285[] var0, S0082 var1, S0455 var2) {
try {

      long var3 = d ^ 118387211366824L;
      String[] var10000 = __indy$7(2745328690613848360L, var3);
      S0285[] var6 = var0;
      int var7 = var6.length;
      int var8 = 0;
      String[] var5 = var10000;

      while (var8 < var7) {
         S0285 var9 = var6[var8];
         __indy$8(var9, var1, var2, 2746698902704227639L, var3);
         var8++;
         if (var5 != null) {
            break;
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0285 lambda$makeSimpleEvent$1(S0285[] var0) {
try {

      return __indy$9(var0);
   
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

   public static native Event __indy$0(Object var0, long var1, long var3) throws Throwable;

   public static native Event __indy$1(Object var0, long var1, long var3) throws Throwable;

   public static native Event __indy$2(Object var0, long var1, long var3) throws Throwable;

   public static native MethodHandle __indy$3() throws Throwable;

   public static native Object __indy$4(MethodHandle var0, Object[] var1) throws Throwable;

   public static native Function __indy$5() throws Throwable;

   public static native Event __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   public static native String[] __indy$7(long var0, long var2) throws Throwable;

   public static native void __indy$8(Object var0, Object var1, Object var2, long var3, long var5) throws Throwable;

   public static native S0285 __indy$9(S0285[] var0) throws Throwable;

private static void __flow$0$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-6476619720041136215L,2);f.pc=1;return;}
case 1:{f.push(8345883534297251393L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(230944569046159L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0284.class,"d",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0284.d,2);f.pc=9;return;}
case 9:{f.push(135086646273755L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[0]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[0],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(103013786966875L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(88);f.pc=18;return;}
case 18:{f.push(17,1);f.pc=19;return;}
case 19:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=20;return;}
case 20:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0284.class,"e",((java.lang.Object[])(Object)(f.pop())));f.pc=21;return;}
case 21:{f.push(17,1);f.pc=22;return;}
case 22:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=23;return;}
case 23:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0284.class,"f",((java.lang.String[])(Object)(f.pop())));f.pc=24;return;}
case 24:{dev.zprestige.sourceview.S0284.a$return$9b75();f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.push(f.locals[2],2);f.pc=28;return;}
case 28:{f.push(1,1);f.pc=29;return;}
case 29:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=30;return;}
case 30:{f.rearrange(91);f.pc=31;return;}
case 31:{f.rearrange(91);f.pc=32;return;}
case 32:{f.rearrange(87);f.pc=33;return;}
case 33:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=34;return;}
case 34:{f.push(0,1);f.pc=35;return;}
case 35:{f.rearrange(95);f.pc=36;return;}
case 36:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=37;return;}
case 37:{f.push(8172559771744804614L,2);f.pc=38;return;}
case 38:{f.push(f.locals[0],2);f.pc=39;return;}
case 39:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0284.__indy$0(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=40;return;}
case 40:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0284.class,"a",((net.fabricmc.fabric.api.event.Event)(Object)(f.pop())));f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.push(f.locals[2],2);f.pc=44;return;}
case 44:{f.push(1,1);f.pc=45;return;}
case 45:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=46;return;}
case 46:{f.rearrange(91);f.pc=47;return;}
case 47:{f.rearrange(91);f.pc=48;return;}
case 48:{f.rearrange(87);f.pc=49;return;}
case 49:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=50;return;}
case 50:{f.push(0,1);f.pc=51;return;}
case 51:{f.rearrange(95);f.pc=52;return;}
case 52:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=53;return;}
case 53:{f.push(8172559771744804614L,2);f.pc=54;return;}
case 54:{f.push(f.locals[0],2);f.pc=55;return;}
case 55:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0284.__indy$1(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=56;return;}
case 56:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0284.class,"b",((net.fabricmc.fabric.api.event.Event)(Object)(f.pop())));f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.push(f.locals[2],2);f.pc=60;return;}
case 60:{f.push(1,1);f.pc=61;return;}
case 61:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=62;return;}
case 62:{f.rearrange(91);f.pc=63;return;}
case 63:{f.rearrange(91);f.pc=64;return;}
case 64:{f.rearrange(87);f.pc=65;return;}
case 65:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=66;return;}
case 66:{f.push(0,1);f.pc=67;return;}
case 67:{f.rearrange(95);f.pc=68;return;}
case 68:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=69;return;}
case 69:{f.push(8172559771744804614L,2);f.pc=70;return;}
case 70:{f.push(f.locals[0],2);f.pc=71;return;}
case 71:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0284.__indy$2(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=72;return;}
case 72:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0284.class,"c",((net.fabricmc.fabric.api.event.Event)(Object)(f.pop())));f.pc=73;return;}
case 73:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
