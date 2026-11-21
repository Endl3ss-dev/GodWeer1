package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0137;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0549;
import net.minecraft.class_2720;
import net.minecraft.class_2856;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import net.minecraft.class_2596;
import net.minecraft.class_746;
import net.minecraft.class_2856.class_2857;

public class S0233 extends S0260 {
   public static long k;
   public static Object[] l;
   public static String[] m;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static CallSite b(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$0().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/d5" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class n(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = m(var0, var2);
      Object var6 = l[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(m[var4]);
            l[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method h(Class var0, String var1, Class var2, int var3, Class[] var4) {
try {

      Method var5 = g(var0, var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      }

      Class[] var6 = var0.getInterfaces();
      if (var6 != null) {
         for (int var7 = 0; var7 < var6.length; var7++) {
            var5 = h(var6[var7], var1, var2, var3, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field h(Class var0, String var1, Class var2) {
try {

      Field var3 = g(var0, var1, var2);
      if (var3 != null) {
         return var3;
      }

      Class[] var4 = var0.getInterfaces();
      if (var4 != null) {
         for (int var5 = 0; var5 < var4.length; var5++) {
            var3 = h(var4[var5], var1, var2);
            if (var3 != null) {
               return var3;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void f() {
try {

      l[0] = "qbO\u0019B\u0013gbJCQ\u0004p)IE]\u0010an^R\u0016\u0002]";
      l[1] = "sO[(~l\u0006oP'o#{wC fj\u0013";
      l[2] = "ywx\u001c~!ywo@r.c<o^r;dM?\u0003#";
      l[3] = "\u000e6a+D&\u000e6vwH)\u0014}viH<\u0013\f\"1\u001f";
      l[4] = ":NI\t\nbOnB\u0006\u001b-.`I\r\u001fwZ";
      l[5] = void.class;
      m[5] = "java/lang/Void";
      l[6] = "Y\u000e~\u007fOCO\u000e{%\\TXEx#P@I\u0002o4\u001bW\b";
      l[7] = "17]L0::8L\u0003S7/5Chf5>&_Dq8";
      l[8] = "\u007fUH!X\u001aiUM{K\r~\u001eN}G\u0019oYYj\f\b|";
      l[9] = "-\r\u0007W[ X-\fXJo9#\u0007SN5M";
      l[10] = "\bXO\n\by\bXXV\u0004v\u0012\u0013XH\u0004c\u0015b\t\u0011\\&";
      l[11] = "\u007f\u0001]dz?a\tG+\u0001\u001f\\$";
      l[12] = "}\u0006\u0015#K6k\u0006\u0010yX!|M\u0013\u007fT5m\n\u0004h\u001f%*";
      l[13] = "99\n\u00198cL\u0019\u0001\u0016),-\u0017\n\u001d-vY";
      l[14] = "PL\u007f\u0002.gPLh^\"hJ\u0007h@\"}Mv9\u0014v8\u001aJgM0}a\u001b3\u0019t";
      l[15] = "D1G\u0019\u0014zO>VVutD5R\f";
      l[16] = "\u0012\u001dB'\u0007\u0004\u001b\u0014{u{E\u001dX\u0015'A\u0010L\u0018A\u001f";
      l[17] = "}\u0016|PY 'UrW50@YfPZ0+QdQOY\u007f\n!WG>z\frU5";
      l[18] = "n<:\n\u0015\u0014j'9\\*\u001b6(#[}Lh\u007f{7\u0010\ff%,Z\u0013\nn(";
      l[19] = "\u0016\u00128+,8\u001f\u001b\u0001zPq\rVnz;y\u000fW{\u0013k%\u001a\u001bp~\"\u007f\u000e\u0016\u0001";
      l[20] = "\u0005PvfVv\u0006V~k=~RU'aQL\u0002\u0017y9=+\u0002X<`W#\u0002Pz\u0006";
      l[21] = " Z\u0015}s\u0013j\u001f\u0014#\u001d\u0017\u0010\u0017P#r\u0017{\u001fR\"g~+CGnl\u0013b\u0019Sc\u001d";
      l[22] = "A\rus Q\n\u0003'&EM\u0011\u001f!v\u0012\u001aKJ|%E\u0012\u000fB,\u007f7Y\u0001\u0010y";
      l[23] = "\u0005\u0018RS8\u0016N\u0016\u0000\u0006]\nU\n\u0006V\n]\u000f_[\u0004]UKW\u000b_/\u001eE\u0005^";
      l[24] = "#>(+n\u0013\u007f +f\u0002\u0014\u001fa,%`E!6)&\u007f}##.;:Ct&-$\u0002";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = d(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$1(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 232 && var8 != 'L' && var8 != 'u' && var8 != 249) {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 254) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 200) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 232) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'L') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'u') {
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

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0137 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(7,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$10$0(f);break;
case 1:__flow$10$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=14&&f.pc<27&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=29;continue;}
if(f.pc>=19&&f.pc<39&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=41;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static int m(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (m[var4] != null) {
         return var4;
      }

      Object var5 = l[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 26;
         case 1 -> 56;
         case 2 -> 17;
         case 3 -> 57;
         case 4 -> 39;
         case 5 -> 15;
         case 6 -> 21;
         case 7 -> 60;
         case 8 -> 52;
         case 9 -> 32;
         case 10 -> 11;
         case 11 -> 31;
         case 12 -> 41;
         case 13 -> 46;
         case 14 -> 59;
         case 15 -> 38;
         case 16 -> 22;
         case 17 -> 43;
         case 18 -> 37;
         case 19 -> 30;
         case 20 -> 55;
         case 21 -> 1;
         case 22 -> 14;
         case 23 -> 10;
         case 24 -> 62;
         case 25 -> 34;
         case 26 -> 7;
         case 27 -> 27;
         case 28 -> 63;
         case 29 -> 0;
         case 30 -> 47;
         case 31 -> 25;
         case 32 -> 6;
         case 33 -> 4;
         case 34 -> 19;
         case 35 -> 44;
         case 36 -> 2;
         case 37 -> 36;
         case 38 -> 18;
         case 39 -> 3;
         case 40 -> 16;
         case 41 -> 28;
         case 42 -> 53;
         case 43 -> 29;
         case 44 -> 58;
         case 45 -> 20;
         case 46 -> 50;
         case 47 -> 61;
         case 48 -> 8;
         case 49 -> 40;
         case 50 -> 24;
         case 51 -> 33;
         case 52 -> 9;
         case 53 -> 23;
         case 54 -> 42;
         case 55 -> 45;
         case 56 -> 54;
         case 57 -> 48;
         case 58 -> 49;
         case 59 -> 12;
         case 60 -> 51;
         case 61 -> 13;
         case 62 -> 5;
         default -> 35;
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

      m[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field o(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = l[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = m[var4];
      int var7 = var6.indexOf(8);
      Class var8 = n(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = n(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = g(var12, var10, var11);
         if (var13 != null) {
            l[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = h(var14[var15], var10, var11);
               if (var13 != null) {
                  l[var4] = var13;
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
            var12 = n(1124316045831865L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method p(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = l[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = m[var4];
      int var7 = var6.indexOf(8);
      Class var8 = n(Long.parseLong(var6.substring(0, var7), 36), 0L);
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
         var15 = n(Long.parseLong(var6.substring(var12, var17), 36), 0L);
         if (var16 < var13) {
            var14[var16] = var15;
         }

         var12 = var17 + 1;
      }

      Class var23 = var8;

      while (true) {
         Method var26 = g(var23, var10, var15, var13, var14);
         if (var26 != null) {
            l[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = n(1124316045831865L, 0L);
            break;
         }
      }

      var23 = var8;

      while (true) {
         Class[] var27;
         if ((var27 = var23.getInterfaces()) != null) {
            for (int var18 = 0; var18 < var27.length; var18++) {
               Method var19 = h(var27[var18], var10, var15, var13, var14);
               if (var19 != null) {
                  l[var4] = var19;
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
            var23 = n(1124316045831865L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method g(Class var0, String var1, Class var2, int var3, Class[] var4) {
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

   public static Field g(Class var0, String var1, Class var2) {
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

   private static native MethodHandle __indy$0() throws Throwable;

   private static native Object __indy$1(MethodHandle var0, Object[] var1) throws Throwable;

   private static native String[] __indy$2(long var0, long var2) throws Throwable;

   private static native class_2596 __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_746 __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native UUID __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native class_2857 __indy$9(long var0, long var2) throws Throwable;

   private static native void __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native UUID __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native class_2857 __indy$13(long var0, long var2) throws Throwable;

   private static native void __indy$14(Object var0, long var1, long var3) throws Throwable;

public S0233()throws Throwable{super();}

private void __flow$10$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0233.k,2);f.pc=1;return;}
case 1:{f.push(34299826242715L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(6043774997229L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.push(-583465135340762980L,2);f.pc=11;return;}
case 11:{f.push(f.locals[2],2);f.pc=12;return;}
case 12:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=13;return;}
case 13:{f.locals[6]=f.pop();f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.push(f.locals[1],1);f.pc=17;return;}
case 17:{f.push(f.locals[6],1);f.pc=18;return;}
case 18:{if(f.pop()!=null){f.pc=47;return;}f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(0,1);f.pc=21;return;}
case 21:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=22;return;}
case 22:{f.push(-583385120130478613L,2);f.pc=23;return;}
case 23:{f.push(f.locals[2],2);f.pc=24;return;}
case 24:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$3(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=25;return;}
case 25:{f.push((f.pop() instanceof net.minecraft.class_2720?1:0),1);f.pc=26;return;}
case 26:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=120;return;}f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.pc=35;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.push(-584013080506428206L,2);f.pc=32;return;}
case 32:{f.push(f.locals[2],2);f.pc=33;return;}
case 33:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=34;return;}
case 34:{throw (Throwable)f.pop();}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(f.locals[1],1);f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.pc=47;return;}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.push(-584013080506428206L,2);f.pc=44;return;}
case 44:{f.push(f.locals[2],2);f.pc=45;return;}
case 45:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=46;return;}
case 46:{throw (Throwable)f.pop();}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(0,1);f.pc=50;return;}
case 50:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=51;return;}
case 51:{f.push(-583276986072872440L,2);f.pc=52;return;}
case 52:{f.push(f.locals[2],2);f.pc=53;return;}
case 53:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0233.__indy$6(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.push(dev.zprestige.sourceview.FlowFrame.pending(net.minecraft.class_2856.class),1);f.pc=57;return;}
case 57:{f.rearrange(89);f.pc=58;return;}
case 58:{f.push(dev.zprestige.sourceview.S0233.b,1);f.pc=59;return;}
case 59:{f.push(-583354459916325646L,2);f.pc=60;return;}
case 60:{f.push(f.locals[2],2);f.pc=61;return;}
case 61:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=62;return;}
case 62:{f.push(-583723206690914008L,2);f.pc=63;return;}
case 63:{f.push(f.locals[2],2);f.pc=64;return;}
case 64:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=65;return;}
case 65:{f.push(-583516756229765003L,2);f.pc=66;return;}
case 66:{f.push(f.locals[2],2);f.pc=67;return;}
case 67:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$9(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=68;return;}
case 68:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.util.UUID.class,net.minecraft.class_2856.class_2857.class},new Object[]{((java.util.UUID)(Object)(a0)),((net.minecraft.class_2856.class_2857)(Object)(a1))});f.pc=69;return;}
case 69:{f.push(f.locals[4],2);f.pc=70;return;}
case 70:{f.push(2,1);f.pc=71;return;}
case 71:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=72;return;}
case 72:{f.rearrange(91);f.pc=73;return;}
case 73:{f.rearrange(91);f.pc=74;return;}
case 74:{f.rearrange(87);f.pc=75;return;}
case 75:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=76;return;}
case 76:{f.push(1,1);f.pc=77;return;}
case 77:{f.rearrange(95);f.pc=78;return;}
case 78:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=79;return;}
case 79:{f.rearrange(90);f.pc=80;return;}
case 80:{f.rearrange(95);f.pc=81;return;}
case 81:{f.push(0,1);f.pc=82;return;}
case 82:{f.rearrange(95);f.pc=83;return;}
case 83:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=84;return;}
case 84:{f.push(-583651875714721854L,2);f.pc=85;return;}
case 85:{f.push(f.locals[2],2);f.pc=86;return;}
case 86:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0233.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=87;return;}
case 87:{f.pc=88;return;}
case 88:{f.pc=89;return;}
case 89:{f.push(dev.zprestige.sourceview.FlowFrame.pending(net.minecraft.class_2856.class),1);f.pc=90;return;}
case 90:{f.rearrange(89);f.pc=91;return;}
case 91:{f.push(dev.zprestige.sourceview.S0233.b,1);f.pc=92;return;}
case 92:{f.push(-583354459916325646L,2);f.pc=93;return;}
case 93:{f.push(f.locals[2],2);f.pc=94;return;}
case 94:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=95;return;}
case 95:{f.push(-583723206690914008L,2);f.pc=96;return;}
case 96:{f.push(f.locals[2],2);f.pc=97;return;}
case 97:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=98;return;}
case 98:{f.push(-583578597447508398L,2);f.pc=99;return;}
case 99:{f.push(f.locals[2],2);f.pc=100;return;}
case 100:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0233.__indy$13(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=101;return;}
case 101:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.util.UUID.class,net.minecraft.class_2856.class_2857.class},new Object[]{((java.util.UUID)(Object)(a0)),((net.minecraft.class_2856.class_2857)(Object)(a1))});f.pc=102;return;}
case 102:{f.push(f.locals[4],2);f.pc=103;return;}
case 103:{f.push(2,1);f.pc=104;return;}
case 104:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=105;return;}
case 105:{f.rearrange(91);f.pc=106;return;}
case 106:{f.rearrange(91);f.pc=107;return;}
case 107:{f.rearrange(87);f.pc=108;return;}
case 108:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=109;return;}
case 109:{f.push(1,1);f.pc=110;return;}
case 110:{f.rearrange(95);f.pc=111;return;}
case 111:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=112;return;}
case 112:{f.rearrange(90);f.pc=113;return;}
case 113:{f.rearrange(95);f.pc=114;return;}
case 114:{f.push(0,1);f.pc=115;return;}
case 115:{f.rearrange(95);f.pc=116;return;}
case 116:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=117;return;}
case 117:{f.push(-583651875714721854L,2);f.pc=118;return;}
case 118:{f.push(f.locals[2],2);f.pc=119;return;}
case 119:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0233.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$10$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.pc=123;return;}
case 123:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-4756148655452173340L,2);f.pc=1;return;}
case 1:{f.push(1934295667687930903L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(250854068603386L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0233.class,"k",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(25,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0233.class,"l",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(25,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0233.class,"m",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0233.f();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
