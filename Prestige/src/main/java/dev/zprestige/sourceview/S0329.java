package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0253;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0433;
import dev.zprestige.sourceview.S0434;
import dev.zprestige.sourceview.S0549;
import net.minecraft.class_310;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_437;

public class S0329 extends S0260 {
   public static S0329 a;
   public S0253 c;
   public static long k;
   public static Object[] l;
   public static String[] m;

   public S0329()  throws Throwable {
try {

      a = this;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

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
         throw new RuntimeException("dev/zprestige/prestige/eY" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public boolean b$return$e4b089a0(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      return false;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static float b$return$e4b0898c(Object[] param0) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(6,5);
f.locals[0]=param0;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$4$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=20&&f.pc<27&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=29;continue;}
if(f.pc>=25&&f.pc<40&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=40;continue;}
if(f.pc>=58&&f.pc<65&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=67;continue;}
if(f.pc>=63&&f.pc<77&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=77;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return dev.zprestige.sourceview.FlowFrame.f(f.result);
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

   public static void f() {
try {

      l[0] = "\u000b:H$.\n\u001d:M~=\u001d\nqNx1\t\u001b6Yoz\u001f6";
      l[1] = "@xGp\u0000%KwV?c(^zYTV*OiExA'";
      l[2] = "\u0005V\u0006,8#\u0013V\u0003v+4\u0004\u001d\u0000p' \u0015Z\u0017gl2)";
      l[3] = "'|B>4^R\\I1%\u0011/DZ6,XG";
      l[4] = ":9IxH\u0015,9L\"[\u0002;rO$W\u0016*5X3\u001c\u0001\u0015";
      l[5] = "x\u0012)c>zs\u001d8,_tx\u0016<v";
      l[6] = "24L2k,9;]}\u0003,74N";
      l[7] = float.class;
      m[7] = "java/lang/Float";
      l[8] = "G\u001f^hOwG\u001fI4Cx]TI*CmZ%\u0019w\u0012";
      l[9] = "}+\u000e][8}+\u0019\u0001W7g`\u0019\u001fW\"`\u0011N@\u0001";
      l[10] = void.class;
      m[10] = "java/lang/Void";
      l[11] = "[q\u0018r=<Mq\u001d(.+Z:\u001e.\"?K}\t9i+\u0006";
      l[12] = "id\u001f\u0000T\u001d\u007fd\u001aZG\nh/\u0019\\K\u001eyh\u000eK\u0000\u000eah\f@ZC]s\f]Z\u0004jd";
      l[13] = "a\u001e\u007f*5\u0000w\u001ezp&\u0017`Uyv*\u0003q\u0012naa\u0017=";
      l[14] = ">\u000bVm1o(\u000bS7\"x?@P1.l.\u0007G&e{\f";
      l[15] = ">\u0018\"\u0015$/K8)\u001a5`*6\"\u00111:^";
      l[16] = "\fo(\u0006\u007f\u001cS+4\bCA<+3Q/MB,1\u000fx";
      l[17] = "\t\u001d%al;VY9oPa9W2,:o\u0002\b=*0";
      l[18] = "l\u0012.iN#g\u0011mUM\u0013'\u001c5iIi,K:UT\"5\u0011:/_u:-";
      l[19] = "n\t'\u007f\u0019\u00021\u0017jl)V_\u001bduN\u0001!\nj9\u0010?`Cm9B^d\u000bhn)";
      l[20] = "\u0013\u0007\u0010\u0001lE\u0013\u0002SF\u0010N*@\u0002\u0014lJX\u0000\u000fEh$";
      l[21] = "hl@8C><cAy%%R(N8E54\u007f\u001a{KL";
      l[22] = "KDn7<\u0001O\u00185!N\u001a\u0018\u00005)\"(LAmqNA\u0013E1>,\u0006\u0012\u0006.NqN\u000eB>/u\u0006\u000b\u0015U";
      l[23] = "1_UEp\u00077\n\u0003K\u001a\u0003i\\\u0001HD\u0004iF\u00054'S5\u0007\tNh\u001f2^h";
      l[24] = "T|\u0012uV+S~L\"90\u0006f\u0015~U\u0002Q'I \bUT+\u000e'R4Pc\u000bp9";
      l[25] = "\no8#\u001d>\u000e3c5o.U:g68y\u000bj>ZQ&\r3s8\u0016'N,";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = d(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$8(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'o' && var8 != 206 && var8 != 200 && var8 != 'V') {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'Z') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'h') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'o') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 206) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 200) {
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

   @Override
   public void d$return$e4b0899c(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(7,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$11$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=18&&f.pc<29&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=31;continue;}
if(f.pc>=23&&f.pc<46&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=48;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
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
         case 0 -> 35;
         case 1 -> 37;
         case 2 -> 24;
         case 3 -> 7;
         case 4 -> 8;
         case 5 -> 15;
         case 6 -> 29;
         case 7 -> 51;
         case 8 -> 44;
         case 9 -> 0;
         case 10 -> 38;
         case 11 -> 17;
         case 12 -> 16;
         case 13 -> 25;
         case 14 -> 14;
         case 15 -> 45;
         case 16 -> 48;
         case 17 -> 36;
         case 18 -> 11;
         case 19 -> 53;
         case 20 -> 34;
         case 21 -> 1;
         case 22 -> 39;
         case 23 -> 49;
         case 24 -> 10;
         case 25 -> 23;
         case 26 -> 54;
         case 27 -> 13;
         case 28 -> 30;
         case 29 -> 3;
         case 30 -> 21;
         case 31 -> 46;
         case 32 -> 2;
         case 33 -> 33;
         case 34 -> 60;
         case 35 -> 5;
         case 36 -> 52;
         case 37 -> 9;
         case 38 -> 26;
         case 39 -> 40;
         case 40 -> 50;
         case 41 -> 32;
         case 42 -> 20;
         case 43 -> 63;
         case 44 -> 61;
         case 45 -> 28;
         case 46 -> 57;
         case 47 -> 59;
         case 48 -> 43;
         case 49 -> 42;
         case 50 -> 19;
         case 51 -> 6;
         case 52 -> 4;
         case 53 -> 56;
         case 54 -> 31;
         case 55 -> 58;
         case 56 -> 18;
         case 57 -> 55;
         case 58 -> 62;
         case 59 -> 41;
         case 60 -> 47;
         case 61 -> 12;
         case 62 -> 27;
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
            var12 = n(367488182117067L, 0L);
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
            var23 = n(367488182117067L, 0L);
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
            var23 = n(367488182117067L, 0L);
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

   private static native String[] __indy$1(long var0, long var2) throws Throwable;

   private static native MatchException __indy$2(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native float __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$8(MethodHandle var0, Object[] var1) throws Throwable;

   private static native String[] __indy$9(long var0, long var2) throws Throwable;

   private static native class_437 __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native S0434 __indy$12(long var0, long var2) throws Throwable;

   private static native void __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native S0433 __indy$15(long var0, long var2) throws Throwable;

   private static native void __indy$16(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

private static void __flow$4$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[0],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=5;return;}
case 5:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=6;return;}
case 6:{f.locals[1]=f.pop();f.pc=7;return;}
case 7:{f.rearrange(87);f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0329.k,2);f.pc=9;return;}
case 9:{f.push(f.locals[1],2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[1]=f.pop();f.pc=12;return;}
case 12:{f.push(-6884028142522596693L,2);f.pc=13;return;}
case 13:{f.push(f.locals[1],2);f.pc=14;return;}
case 14:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$1(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.push(dev.zprestige.sourceview.S0329.a,1);f.pc=18;return;}
case 18:{f.locals[4]=f.pop();f.pc=19;return;}
case 19:{f.locals[3]=f.pop();f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(f.locals[4],1);f.pc=23;return;}
case 23:{f.push(f.locals[3],1);f.pc=24;return;}
case 24:{if(f.pop()!=null){f.pc=54;return;}f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{if(f.pop()!=null){f.pc=46;return;}f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.pc=35;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.push(-6884128042914557891L,2);f.pc=32;return;}
case 32:{f.push(f.locals[1],2);f.pc=33;return;}
case 33:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$2(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=34;return;}
case 34:{throw (Throwable)f.pop();}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(Float.intBitsToFloat(1122369536),1);f.pc=39;return;}
case 39:{f.result=f.pop();f.done=true;return;}
case 40:{f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.push(-6884128042914557891L,2);f.pc=43;return;}
case 43:{f.push(f.locals[1],2);f.pc=44;return;}
case 44:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=45;return;}
case 45:{throw (Throwable)f.pop();}
case 46:{f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(f.locals[4],1);f.pc=50;return;}
case 50:{f.push(((dev.zprestige.sourceview.S0329)(Object)f.pop()).c,1);f.pc=51;return;}
case 51:{f.push(-6883917530305837164L,2);f.pc=52;return;}
case 52:{f.push(f.locals[1],2);f.pc=53;return;}
case 53:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.push((java.lang.Float)(Object)f.pop(),1);f.pc=57;return;}
case 57:{f.locals[5]=f.pop();f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.push(f.locals[5],1);f.pc=61;return;}
case 61:{f.push(f.locals[3],1);f.pc=62;return;}
case 62:{if(f.pop()!=null){f.pc=86;return;}f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{if(f.pop()!=null){f.pc=83;return;}f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.pc=73;return;}
case 67:{f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.push(-6884128042914557891L,2);f.pc=70;return;}
case 70:{f.push(f.locals[1],2);f.pc=71;return;}
case 71:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=72;return;}
case 72:{throw (Throwable)f.pop();}
case 73:{f.pc=74;return;}
case 74:{f.pc=75;return;}
case 75:{f.push(Float.intBitsToFloat(1122369536),1);f.pc=76;return;}
case 76:{f.pc=91;return;}
case 77:{f.pc=78;return;}
case 78:{f.pc=79;return;}
case 79:{f.push(-6884128042914557891L,2);f.pc=80;return;}
case 80:{f.push(f.locals[1],2);f.pc=81;return;}
case 81:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=82;return;}
case 82:{throw (Throwable)f.pop();}
case 83:{f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.push(f.locals[5],1);f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.pc=88;return;}
case 88:{f.push(-6883818423234300174L,2);f.pc=89;return;}
case 89:{f.push(f.locals[1],2);f.pc=90;return;}
case 90:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=91;return;}
case 91:{f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$11$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=5;return;}
case 5:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=6;return;}
case 6:{f.locals[2]=f.pop();f.pc=7;return;}
case 7:{f.rearrange(87);f.pc=8;return;}
case 8:{f.push(f.locals[2],2);f.pc=9;return;}
case 9:{f.rearrange(92);f.pc=10;return;}
case 10:{f.push(22594282158762L,2);f.pc=11;return;}
case 11:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=12;return;}
case 12:{f.locals[4]=f.pop();f.pc=13;return;}
case 13:{f.rearrange(88);f.pc=14;return;}
case 14:{f.push(3243509114378162143L,2);f.pc=15;return;}
case 15:{f.push(f.locals[2],2);f.pc=16;return;}
case 16:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$9(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=17;return;}
case 17:{f.locals[6]=f.pop();f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(dev.zprestige.sourceview.S0329.b,1);f.pc=21;return;}
case 21:{f.push(f.locals[6],1);f.pc=22;return;}
case 22:{if(f.pop()!=null){f.pc=58;return;}f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(3242621127983190617L,2);f.pc=25;return;}
case 25:{f.push(f.locals[2],2);f.pc=26;return;}
case 26:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=27;return;}
case 27:{f.push((f.pop() instanceof dev.zprestige.sourceview.S0434?1:0),1);f.pc=28;return;}
case 28:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=54;return;}f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=37;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{f.push(3243414985344507209L,2);f.pc=34;return;}
case 34:{f.push(f.locals[2],2);f.pc=35;return;}
case 35:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=36;return;}
case 36:{throw (Throwable)f.pop();}
case 37:{f.pc=38;return;}
case 38:{f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.push(3243244884078150461L,2);f.pc=41;return;}
case 41:{f.push(f.locals[2],2);f.pc=42;return;}
case 42:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$12(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=43;return;}
case 43:{f.push(3242684540491843205L,2);f.pc=44;return;}
case 44:{f.push(f.locals[2],2);f.pc=45;return;}
case 45:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0329.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.pc=54;return;}
case 48:{f.pc=49;return;}
case 49:{f.pc=50;return;}
case 50:{f.push(3243414985344507209L,2);f.pc=51;return;}
case 51:{f.push(f.locals[2],2);f.pc=52;return;}
case 52:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=53;return;}
case 53:{throw (Throwable)f.pop();}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.push(dev.zprestige.sourceview.S0329.b,1);f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.push(3243170469743613485L,2);f.pc=61;return;}
case 61:{f.push(f.locals[2],2);f.pc=62;return;}
case 62:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0329.__indy$15(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=63;return;}
case 63:{f.push(3243657064823626773L,2);f.pc=64;return;}
case 64:{f.push(f.locals[2],2);f.pc=65;return;}
case 65:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0329.__indy$16(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.pc=68;return;}
case 68:{f.push(f.locals[0],1);f.pc=69;return;}
case 69:{f.push(f.locals[4],2);f.pc=70;return;}
case 70:{f.push(1,1);f.pc=71;return;}
case 71:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=72;return;}
case 72:{f.rearrange(91);f.pc=73;return;}
case 73:{f.rearrange(91);f.pc=74;return;}
case 74:{f.rearrange(87);f.pc=75;return;}
case 75:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=76;return;}
case 76:{f.push(0,1);f.pc=77;return;}
case 77:{f.rearrange(95);f.pc=78;return;}
case 78:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=79;return;}
case 79:{f.push(3243329967744840911L,2);f.pc=80;return;}
case 80:{f.push(f.locals[2],2);f.pc=81;return;}
case 81:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0329.__indy$17(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=82;return;}
case 82:{f.pc=83;return;}
case 83:{f.pc=84;return;}
case 84:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-3312812709881968787L,2);f.pc=1;return;}
case 1:{f.push(-3594215934441747723L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(173878241333353L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0329.class,"k",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(26,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0329.class,"l",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(26,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0329.class,"m",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0329.f();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
