package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0549;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.class_1921;
import net.minecraft.class_4588;
import net.minecraft.class_4597;

public class S0160 implements class_4597 {
   public class_4597 a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   public S0160(class_4597 var1)  throws Throwable {
try {

      this.a = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
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
            var12 = b(719184864577349L, 0L);
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
            var23 = b(719184864577349L, 0L);
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
            var23 = b(719184864577349L, 0L);
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
         if (var8 != 'o' && var8 != 'X' && var8 != 217 && var8 != 220) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 253) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 209) {
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
            } else if (var8 == 'X') {
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

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
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
         throw new RuntimeException("dev/zprestige/prestige/c2" + " : " + var1 + " : " + var2.toString(), var5);
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

   public static Object a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = a(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$1(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
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
         case 0 -> 5;
         case 1 -> 26;
         case 2 -> 31;
         case 3 -> 11;
         case 4 -> 37;
         case 5 -> 7;
         case 6 -> 32;
         case 7 -> 9;
         case 8 -> 42;
         case 9 -> 55;
         case 10 -> 19;
         case 11 -> 35;
         case 12 -> 22;
         case 13 -> 20;
         case 14 -> 63;
         case 15 -> 27;
         case 16 -> 3;
         case 17 -> 18;
         case 18 -> 49;
         case 19 -> 33;
         case 20 -> 4;
         case 21 -> 23;
         case 22 -> 13;
         case 23 -> 39;
         case 24 -> 1;
         case 25 -> 60;
         case 26 -> 15;
         case 27 -> 17;
         case 28 -> 30;
         case 29 -> 14;
         case 30 -> 24;
         case 31 -> 54;
         case 32 -> 43;
         case 33 -> 61;
         case 34 -> 58;
         case 35 -> 46;
         case 36 -> 56;
         case 37 -> 62;
         case 38 -> 45;
         case 39 -> 2;
         case 40 -> 16;
         case 41 -> 0;
         case 42 -> 6;
         case 43 -> 51;
         case 44 -> 41;
         case 45 -> 57;
         case 46 -> 29;
         case 47 -> 34;
         case 48 -> 50;
         case 49 -> 28;
         case 50 -> 38;
         case 51 -> 12;
         case 52 -> 21;
         case 53 -> 8;
         case 54 -> 48;
         case 55 -> 53;
         case 56 -> 36;
         case 57 -> 25;
         case 58 -> 40;
         case 59 -> 52;
         case 60 -> 10;
         case 61 -> 44;
         case 62 -> 59;
         default -> 47;
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

      c[0] = "\u0014\u0014\u0018[\u0005N\u0002\u0014\u001d\u0001\u0016Y\u0015_\u001e\u0007\u001aM\u0004\u0018\t\u0010Q_8";
      c[1] = "\r\u0012\u0005i\u001a\u0001x2\u000ef\u000bN\u0005*\u001da\u0002\u0007m";
      c[2] = "\u001bk,\u0019r\u000b\u001bk;E~\u0004\u0001 ;[~\u0011\u0006Qi\u000e-S";
      c[3] = "Z'=\u0005$QD/'JETD/$\nkH";
      c[4] = boolean.class;
      d[4] = "java/lang/Boolean";
      c[5] = "\u00184\u00060N9\u0006<\u001c\u007f\u00069\u001c6\u00048\u000f\"\\\u0013\u0005?\u00038\u001b:\u001e";
      c[6] = "9\u001faHL\u000e/\u001fd\u0012_\u00198Tg\u0014S\r)\u0013p\u0003\u0018\u001dl";
      c[7] = "Yw\bx\t\u0003,W\u0003w\u0018LMY\b|\u001c\u00169";
      c[8] = "f\u0012q*\u0011\u0014f\u0012fv\u001d\u001b|Yfh\u001d\u000e{(11EJ";
      c[9] = "\u0007@kZz\u0011\u0007@|\u0006v\u001e\u001d\u000b|\u0018v\u000b\u001az+A/@";
      c[10] = "\u007f7s9N{t8bv/u\u007f3f,";
      c[11] = "!'\u000er\u001cJ7'\u000b(\u000f] l\b.\u0003I1+\u001f9HYw";
      c[12] = "1}\u0016\u0016t*:r\u0007Y\u0017'/\u007f\b2\"%>l\u0014\u001e5(";
      c[13] = "\u001at\u001fuZX\u0011{\u000e:=Z\u0004p\u000eq\u0006";
      c[14] = int.class;
      d[14] = "java/lang/Integer";
      c[15] = "14D\u0015\u00070D\u0014O\u001a\u0016\u007f%\u001aD\u0011\u0012%Q";
      c[16] = "ls0#r0a%2{No<h?$\"]n/c|q\nhh4. sof::N4;d8!(nkm/C";
      c[17] = "hOQu4\\kJV\"YST[[a4\u0002nBVsd:jXE};\\0\bLjY";
      c[18] = "o:c\u007f{s3:uA.\u000b6ly/%sega-D";
      c[19] = "\u00181Kxhr\u001f?El\u0006nL1@rj\\\u001bv\u001a%7\u000b\u001b)\u001fw7y_-\u001bn\u0006";
      c[20] = "N{>1u)\n\u007f:(D:\u000fRd+8*t$<7?9\u000e~z+x[";
      c[21] = ".?r>wFj;v'FQ}#Mg!Qo0wa!\u0004/[w8yV%)3<}O\u0014";
      c[22] = "$-GZ\"\u001a##INL\u0006p-LP 4'm\u0016\u0006qc&lHL.\u0019|*T\u000bL";
      c[23] = "\u0000c=\u0000c\u000bDg9\u0019R\u001e@Jf\u0019?y\u0005=r\r#\u0003Yj8\u001eRF\u0000wm\u0013(\u001aW=~b";
      c[24] = "\u0016\u0018\u000b\u001d@jE\u0018\b\u001a~g)Q\n\u001a\u0011pK\u001eH\\\u0002\u000e\u0019\u001cOM\u0000lV^\t^~";
      c[25] = "C j0q]@%mg\u001cR\u007f4`$q\u0003E-m6!;E91=-I\u0001=5$\u001c";
      c[26] = "^/\u0005oTnW,Qf-4\tnfcI(\u0002\u0012\tuH,\u0006o]j\u001d9o";
   
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

   public static class_4588 lambda$getBuffer$0(class_4588 var0, Integer var1) {
try {

      long var2 = b ^ 109459241655946L;
      long var4 = var2 ^ 115202580902445L;
      int var10001 = __indy$2(var1, 5045133002637978133L, var2);
      Object[] var10004 = new Object[]{null, null, var4};
      var10004[1] = var10001;
      var10004[0] = var0;
      return __indy$3(var10004, 5044350096918495467L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_4588 method_73477(class_1921 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(9,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$16$0(f);break;
case 1:__flow$16$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=23&&f.pc<33&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=35;continue;}
if(f.pc>=28&&f.pc<49&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=51;continue;}
if(f.pc>=41&&f.pc<63&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=65;continue;}
if(f.pc>=57&&f.pc<76&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=76;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return ((class_4588)(Object)f.result);
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

   private static native int __indy$2(Object var0, long var1, long var3) throws Throwable;

   private static native class_4588 __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$4(long var0, long var2) throws Throwable;

   private static native class_4588 __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native Optional __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native Optional __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native Function __indy$14(class_4588 var0) throws Throwable;

   private static native Optional __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$16(Object var0, Object var1, long var2, long var4) throws Throwable;

private void __flow$16$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0160.b,2);f.pc=1;return;}
case 1:{f.push(4475376135049L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(16085512685552L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.push(8287338657175280143L,2);f.pc=11;return;}
case 11:{f.push(f.locals[2],2);f.pc=12;return;}
case 12:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$4(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=13;return;}
case 13:{f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.push(f.locals[0],1);f.pc=16;return;}
case 16:{f.push(((dev.zprestige.sourceview.S0160)(Object)f.pop()).a,1);f.pc=17;return;}
case 17:{f.push(f.locals[1],1);f.pc=18;return;}
case 18:{f.push(8287205560881782046L,2);f.pc=19;return;}
case 19:{f.push(f.locals[2],2);f.pc=20;return;}
case 20:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$5(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=21;return;}
case 21:{f.locals[7]=f.pop();f.pc=22;return;}
case 22:{f.locals[6]=f.pop();f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.pc=25;return;}
case 25:{f.push(f.locals[1],1);f.pc=26;return;}
case 26:{f.push(f.locals[6],1);f.pc=27;return;}
case 27:{if(f.pop()!=null){f.pc=86;return;}f.pc=28;return;}
case 28:{f.pc=29;return;}
case 29:{f.push(8287657883984923167L,2);f.pc=30;return;}
case 30:{f.push(f.locals[2],2);f.pc=31;return;}
case 31:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=32;return;}
case 32:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=82;return;}f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=41;return;}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.push(8286657501159046055L,2);f.pc=38;return;}
case 38:{f.push(f.locals[2],2);f.pc=39;return;}
case 39:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=40;return;}
case 40:{throw (Throwable)f.pop();}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.push(f.locals[1],1);f.pc=44;return;}
case 44:{f.push(8287425785863493560L,2);f.pc=45;return;}
case 45:{f.push(f.locals[2],2);f.pc=46;return;}
case 46:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=47;return;}
case 47:{f.push(f.locals[6],1);f.pc=48;return;}
case 48:{if(f.pop()!=null){f.pc=106;return;}f.pc=49;return;}
case 49:{f.pc=50;return;}
case 50:{f.pc=57;return;}
case 51:{f.pc=52;return;}
case 52:{f.pc=53;return;}
case 53:{f.push(8286657501159046055L,2);f.pc=54;return;}
case 54:{f.push(f.locals[2],2);f.pc=55;return;}
case 55:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=56;return;}
case 56:{throw (Throwable)f.pop();}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.push(8287492432317350753L,2);f.pc=60;return;}
case 60:{f.push(f.locals[2],2);f.pc=61;return;}
case 61:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=62;return;}
case 62:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=82;return;}f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.pc=71;return;}
case 65:{f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.push(8286657501159046055L,2);f.pc=68;return;}
case 68:{f.push(f.locals[2],2);f.pc=69;return;}
case 69:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=70;return;}
case 70:{throw (Throwable)f.pop();}
case 71:{f.pc=72;return;}
case 72:{f.pc=73;return;}
case 73:{f.pc=74;return;}
case 74:{f.push(f.locals[7],1);f.pc=75;return;}
case 75:{f.result=f.pop();f.done=true;return;}
case 76:{f.pc=77;return;}
case 77:{f.pc=78;return;}
case 78:{f.push(8286657501159046055L,2);f.pc=79;return;}
case 79:{f.push(f.locals[2],2);f.pc=80;return;}
case 80:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=81;return;}
case 81:{throw (Throwable)f.pop();}
case 82:{f.pc=83;return;}
case 83:{f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.push(f.locals[1],1);f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.pc=88;return;}
case 88:{f.push(f.locals[4],2);f.pc=89;return;}
case 89:{f.push(2,1);f.pc=90;return;}
case 90:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=91;return;}
case 91:{f.rearrange(91);f.pc=92;return;}
case 92:{f.rearrange(91);f.pc=93;return;}
case 93:{f.rearrange(87);f.pc=94;return;}
case 94:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=95;return;}
case 95:{f.push(1,1);f.pc=96;return;}
case 96:{f.rearrange(95);f.pc=97;return;}
case 97:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=98;return;}
case 98:{f.rearrange(90);f.pc=99;return;}
case 99:{f.rearrange(95);f.pc=100;return;}
case 100:{f.push(0,1);f.pc=101;return;}
case 101:{f.rearrange(95);f.pc=102;return;}
case 102:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=103;return;}
case 103:{f.push(8286722859546682701L,2);f.pc=104;return;}
case 104:{f.push(f.locals[2],2);f.pc=105;return;}
case 105:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=106;return;}
case 106:{f.pc=107;return;}
case 107:{f.pc=108;return;}
case 108:{f.locals[8]=f.pop();f.pc=109;return;}
case 109:{f.pc=110;return;}
case 110:{f.pc=111;return;}
case 111:{f.push(f.locals[8],1);f.pc=112;return;}
case 112:{f.push(f.locals[7],1);f.pc=113;return;}
case 113:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$14(((net.minecraft.class_4588)(Object)(a0))),1);f.pc=114;return;}
case 114:{f.push(8287575536825113529L,2);f.pc=115;return;}
case 115:{f.push(f.locals[2],2);f.pc=116;return;}
case 116:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$15(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=117;return;}
case 117:{f.push(f.locals[7],1);f.pc=118;return;}
case 118:{f.push(8287742497208071719L,2);f.pc=119;return;}
case 119:{f.push(f.locals[2],2);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$16$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0160.__indy$16(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=121;return;}
case 121:{f.push((net.minecraft.class_4588)(Object)f.pop(),1);f.pc=122;return;}
case 122:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-8854970948441867593L,2);f.pc=1;return;}
case 1:{f.push(8547982479740551538L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(76143631525255L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0160.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(27,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0160.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(27,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0160.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0160.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
