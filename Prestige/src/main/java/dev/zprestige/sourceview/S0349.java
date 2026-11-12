package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0258;
import dev.zprestige.sourceview.S0260;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_634;

public class S0349 extends S0260 {
   public S0258 a;
   public static Object[] k;
   public static String[] l;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,1);
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
         throw new RuntimeException("dev/zprestige/prestige/er" + " : " + var1 + " : " + var2.toString(), var5);
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

   public static Class n(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = m(var0, var2);
      Object var6 = k[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(l[var4]);
            k[var4] = var5;
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

      k[0] = "=\u007f,U\u0005{#w6\u001adl={9@X";
      k[1] = "\u0000)(DZ8\u000b&9\u000b;6\u0000-=Q";
      k[2] = "g\u0002$\u0000x\u001dq\u0002!Zk\nfI\"\\g\u001ew\u000e5K,\fK";
      k[3] = "Z\u000e()\u0018x/.#&\t7R60!\u0000~:";
      k[4] = "T\u0011mNf\bT\u0011z\u0012j\u0007NZz\fj\u0012I+/S?";
      k[5] = "\u001b\b\u0011<H<\u0010\u0007\u0000s5$\u0003\u0000\t:";
      k[6] = void.class;
      l[6] = "java/lang/Void";
      k[7] = "~F/co\u0002hF*9|\u0015\u007f\r)?p\u0001nJ>(;\u0016L";
      k[8] = "G5Br;#2\u0015I}*lS\u001bBv.6'";
      k[9] = "&8#=jZ&84afU<s4\u007ff@;\u0002d\"7";
      k[10] = int.class;
      l[10] = "java/lang/Integer";
      k[11] = "ogt\u000e*\u001fygqT9\bn,rR5\u001c\u007fkeE~\u000b@";
      k[12] = "(N\u000fFWm>N\n\u001cDz)\u0005\t\u001aHn8B\u001e\r\u0003x>";
      k[13] = "[$\u0004\u0011Z\u0011P+\u0015^9\u001cE&\u001a5\f\u001eT5\u0006\u0019\u001b\u0013";
      k[14] = boolean.class;
      l[14] = "java/lang/Boolean";
      k[15] = "\u0015\u000f\u0012\fB\u0004\u0013\u0004C|]\u0005\b\u0003\u001f\u0006J.\u0016\u00188\u0001C\fqGO\u0012Z\n\tD\u001f\u0010\u0017hHO\u0010\u0001E\u0010K\u001f\u0012L'";
      k[16] = "r \u0013_Q\u0018/!RN5\u000eOrSED\u0005.0SK\rgs|\u0013G\u000f\u000b2%\u000f\u001e5";
      k[17] = ".qu`{\u0016y~ a\u0012\u0001}`$v~3)!z+\u0012^`|'il\u001c(ax\u0011";
      k[18] = "I)?\u0016?\u0004\r`?N_B\u000f:~V%X\u0014?\u0005\u0015c\u0003\u000e :H'[KP>Se\u0005\u0012mz\u001ae]r";
      k[19] = "#\u000f^U\u000eft\u001a\u0001^>}\u001aLZ\u001cU+\"\u001aYCB\u0017";
      k[20] = "=\u0005Q3Q\u001f\u007fMLl)\u0004j\tQ7E6;H\u000ek\u0011a<\f\u000blI\\xE\u000b4)]7\nXjE\u001cn\u0016\u0001P";
      k[21] = "h\n@`}5!\f\u0016a\u0016=XZ\u0017dk6 YGf&T";
      k[22] = "\u0001^P\u0003apE\u0017P[\u00011]|\u0016E`+U'QF;qZ\u001a\u0015\u000f;):";
      k[23] = "/4H\"E@.3\u0004'4@\u001fbQtH\u0016y8PtH).8\u00002\u000bOt9\u000024";
      k[24] = "\f\u007f\u001cvD!N7\u0001)<:[s\u001crP\b\n2C/\r_\rvF)\\bI?Fq<c\u0006p\u0015/P\"_lL\u0015";
      k[25] = "+Nci\u001f_o\u0007c1\u007f\u0019l^#)\u0004=qK9UD\u001b*\u000b9h\u0000R*SY<EYm\u000fb;A\u0007`7";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 253 && var8 != 'S' && var8 != 246 && var8 != 250) {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'm') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'C') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 253) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'S') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 246) {
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

   @Override
   public void d$return$e4b0899c(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(8,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$10$0(f);break;
case 1:__flow$10$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=30&&f.pc<41&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=43;continue;}
if(f.pc>=35&&f.pc<70&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=72;continue;}
if(f.pc>=49&&f.pc<88&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=90;continue;}
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
      if (l[var4] != null) {
         return var4;
      }

      Object var5 = k[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 19;
         case 1 -> 53;
         case 2 -> 50;
         case 3 -> 27;
         case 4 -> 39;
         case 5 -> 48;
         case 6 -> 7;
         case 7 -> 55;
         case 8 -> 21;
         case 9 -> 22;
         case 10 -> 10;
         case 11 -> 11;
         case 12 -> 5;
         case 13 -> 26;
         case 14 -> 51;
         case 15 -> 6;
         case 16 -> 25;
         case 17 -> 59;
         case 18 -> 58;
         case 19 -> 18;
         case 20 -> 52;
         case 21 -> 13;
         case 22 -> 47;
         case 23 -> 24;
         case 24 -> 0;
         case 25 -> 54;
         case 26 -> 14;
         case 27 -> 37;
         case 28 -> 9;
         case 29 -> 40;
         case 30 -> 31;
         case 31 -> 43;
         case 32 -> 63;
         case 33 -> 56;
         case 34 -> 62;
         case 35 -> 8;
         case 36 -> 35;
         case 37 -> 60;
         case 38 -> 2;
         case 39 -> 49;
         case 40 -> 3;
         case 41 -> 42;
         case 42 -> 44;
         case 43 -> 30;
         case 44 -> 20;
         case 45 -> 29;
         case 46 -> 1;
         case 47 -> 57;
         case 48 -> 34;
         case 49 -> 17;
         case 50 -> 4;
         case 51 -> 41;
         case 52 -> 45;
         case 53 -> 32;
         case 54 -> 28;
         case 55 -> 46;
         case 56 -> 16;
         case 57 -> 15;
         case 58 -> 12;
         case 59 -> 36;
         case 60 -> 23;
         case 61 -> 38;
         case 62 -> 33;
         default -> 61;
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

      l[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field o(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = k[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = l[var4];
      int var7 = var6.indexOf(8);
      Class var8 = n(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = n(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = g(var12, var10, var11);
         if (var13 != null) {
            k[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = h(var14[var15], var10, var11);
               if (var13 != null) {
                  k[var4] = var13;
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
            var12 = n(128191902731900L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method p(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = k[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = l[var4];
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
            k[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = n(128191902731900L, 0L);
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
                  k[var4] = var19;
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
            var23 = n(128191902731900L, 0L);
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

   private static native Object __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native class_634 __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$9(Object var0, int var1, long var2, long var4) throws Throwable;

   private static native void __indy$10(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native class_634 __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$16(Object var0, Object var1, long var2, long var4) throws Throwable;

public S0349()throws Throwable{super();}

private void __flow$10$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
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
case 14:{f.push(3243310169797823408L,2);f.pc=15;return;}
case 15:{f.push(f.locals[2],2);f.pc=16;return;}
case 16:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=17;return;}
case 17:{f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.push(f.locals[0],1);f.pc=20;return;}
case 20:{f.push(((dev.zprestige.sourceview.S0349)(Object)f.pop()).a,1);f.pc=21;return;}
case 21:{f.push(3243440530355145089L,2);f.pc=22;return;}
case 22:{f.push(f.locals[2],2);f.pc=23;return;}
case 23:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=24;return;}
case 24:{f.push((java.lang.String)(Object)f.pop(),1);f.pc=25;return;}
case 25:{f.push(3243694909309046663L,2);f.pc=26;return;}
case 26:{f.push(f.locals[2],2);f.pc=27;return;}
case 27:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=28;return;}
case 28:{f.locals[7]=f.pop();f.pc=29;return;}
case 29:{f.locals[6]=f.pop();f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.push(f.locals[7],1);f.pc=33;return;}
case 33:{f.push(f.locals[6],1);f.pc=34;return;}
case 34:{if(f.pop()!=null){f.pc=96;return;}f.pc=35;return;}
case 35:{f.pc=36;return;}
case 36:{f.push("/",1);f.pc=37;return;}
case 37:{f.push(3242659552253617413L,2);f.pc=38;return;}
case 38:{f.push(f.locals[2],2);f.pc=39;return;}
case 39:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$5(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=40;return;}
case 40:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=78;return;}f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.pc=49;return;}
case 43:{f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.push(3243644981778121285L,2);f.pc=46;return;}
case 46:{f.push(f.locals[2],2);f.pc=47;return;}
case 47:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=48;return;}
case 48:{throw (Throwable)f.pop();}
case 49:{f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{f.push(dev.zprestige.sourceview.S0349.b,1);f.pc=53;return;}
case 53:{f.push(3243165818247662610L,2);f.pc=54;return;}
case 54:{f.push(f.locals[2],2);f.pc=55;return;}
case 55:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=56;return;}
case 56:{f.push(3244180019681102074L,2);f.pc=57;return;}
case 57:{f.push(f.locals[2],2);f.pc=58;return;}
case 58:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=59;return;}
case 59:{f.push((net.minecraft.class_634)(Object)f.pop(),1);f.pc=60;return;}
case 60:{f.push(f.locals[7],1);f.pc=61;return;}
case 61:{f.push(1,1);f.pc=62;return;}
case 62:{f.push(3243415044873408594L,2);f.pc=63;return;}
case 63:{f.push(f.locals[2],2);f.pc=64;return;}
case 64:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=65;return;}
case 65:{f.push(3243552541189146015L,2);f.pc=66;return;}
case 66:{f.push(f.locals[2],2);f.pc=67;return;}
case 67:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0349.__indy$10(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=68;return;}
case 68:{f.push(f.locals[6],1);f.pc=69;return;}
case 69:{if(f.pop()==null){f.pc=103;return;}f.pc=70;return;}
case 70:{f.pc=71;return;}
case 71:{f.pc=78;return;}
case 72:{f.pc=73;return;}
case 73:{f.pc=74;return;}
case 74:{f.push(3243644981778121285L,2);f.pc=75;return;}
case 75:{f.push(f.locals[2],2);f.pc=76;return;}
case 76:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=77;return;}
case 77:{throw (Throwable)f.pop();}
case 78:{f.pc=79;return;}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.push(dev.zprestige.sourceview.S0349.b,1);f.pc=82;return;}
case 82:{f.push(3243165818247662610L,2);f.pc=83;return;}
case 83:{f.push(f.locals[2],2);f.pc=84;return;}
case 84:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=85;return;}
case 85:{f.push(3244180019681102074L,2);f.pc=86;return;}
case 86:{f.push(f.locals[2],2);f.pc=87;return;}
case 87:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=88;return;}
case 88:{f.pc=89;return;}
case 89:{f.pc=96;return;}
case 90:{f.pc=91;return;}
case 91:{f.pc=92;return;}
case 92:{f.push(3243644981778121285L,2);f.pc=93;return;}
case 93:{f.push(f.locals[2],2);f.pc=94;return;}
case 94:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0349.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=95;return;}
case 95:{throw (Throwable)f.pop();}
case 96:{f.pc=97;return;}
case 97:{f.pc=98;return;}
case 98:{f.push((net.minecraft.class_634)(Object)f.pop(),1);f.pc=99;return;}
case 99:{f.push(f.locals[7],1);f.pc=100;return;}
case 100:{f.push(3242701171159341192L,2);f.pc=101;return;}
case 101:{f.push(f.locals[2],2);f.pc=102;return;}
case 102:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0349.__indy$15(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{f.pc=105;return;}
case 105:{f.pc=106;return;}
case 106:{f.push(f.locals[0],1);f.pc=107;return;}
case 107:{f.push(f.locals[4],2);f.pc=108;return;}
case 108:{f.push(1,1);f.pc=109;return;}
case 109:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=110;return;}
case 110:{f.rearrange(91);f.pc=111;return;}
case 111:{f.rearrange(91);f.pc=112;return;}
case 112:{f.rearrange(87);f.pc=113;return;}
case 113:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=114;return;}
case 114:{f.push(0,1);f.pc=115;return;}
case 115:{f.rearrange(95);f.pc=116;return;}
case 116:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=117;return;}
case 117:{f.push(3243264446221539104L,2);f.pc=118;return;}
case 118:{f.push(f.locals[2],2);f.pc=119;return;}
case 119:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0349.__indy$16(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$10$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(26,1);f.pc=1;return;}
case 1:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=2;return;}
case 2:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0349.class,"k",((java.lang.Object[])(Object)(f.pop())));f.pc=3;return;}
case 3:{f.push(26,1);f.pc=4;return;}
case 4:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=5;return;}
case 5:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0349.class,"l",((java.lang.String[])(Object)(f.pop())));f.pc=6;return;}
case 6:{dev.zprestige.sourceview.S0349.f();f.pc=7;return;}
case 7:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
