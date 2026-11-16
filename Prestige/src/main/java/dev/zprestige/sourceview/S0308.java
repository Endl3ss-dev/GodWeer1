package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0049;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0549;
import dev.zprestige.sourceview.S0591;
import net.minecraft.class_1511;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import net.minecraft.class_1293;
import net.minecraft.class_1297;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_1297.class_5529;

public class S0308 extends S0260 {
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
         throw new RuntimeException("dev/zprestige/prestige/eD" + " : " + var1 + " : " + var2.toString(), var5);
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

      l[0] = "\u0011K\r %\u001b\u0007K\bz6\f\u0010\u0000\u000b|:\u0018\u0001G\u001ckq\u000e1";
      l[1] = "QZX}\u000e_ZUI2mROXFYXP^KZuO]";
      l[2] = "k\rTn>;}\rQ4-,jFR2!8{\u0001E%j*G";
      l[3] = "J^0~GF?~;qV\tBf(v_@*";
      l[4] = "VA'aW\u000e@A\";D\u0019W\n!=H\rFM6*\u0003\u001fy";
      l[5] = ",g 0shYG+?b'8I 4f}L";
      l[6] = "\u0006qoT\u001fZ\u0006qx\b\u0013U\u001c:x\u0016\u0013@\u001bK*HK\u0004";
      l[7] = "\u0003k=\\\u001c\u0005\u0003k*\u0000\u0010\n\u0019 *\u001e\u0010\u001f\u001eQzCA";
      l[8] = "Tqyv\u0006%Tqn*\n*N:n4\n?IK:l]";
      l[9] = ")\u000eM\u0016]\u0010)\u000eZJQ\u001f3EZTQ\n44\b\n\tNc\bUYC\n\u0018^\f\n\t";
      l[10] = void.class;
      m[10] = "java/lang/Void";
      l[11] = "):.$D\u001f):9xH\u00103q9fH\u00054\u0000k8\u0010B";
      l[12] = "\u001b\u000f|FB'\u001b\u000fk\u001aN(\u0001Dk\u0004N=\u00065>P\u0017~";
      l[13] = "C\u0010u*\u001dvC\u0010bv\u0011yY[bh\u0011l^*06I,";
      l[14] = "Fg\u0007*o=Xo\u001de\u0012-X";
      l[15] = "3@lhdS8O}'\u0005]3Dy}";
      l[16] = "~\u0018,s]c~F6\u001fV\u0000mF6!Unm\u0001j\u001fM=yE:qMz%{";
      l[17] = "\u0014\u001fGdoVAN\\5\u0006\r.\u001dKei\bNYX*kg";
      l[18] = "G0_\rL\u0002\b1[\u001dt^\u0014pC\u001a\u0018lG5\u001aGt\u000b\u0003gYC\u000e\u0004\u001c=[}E\u0001\u0016n\u001a\u0010\u001f@Cp#";
      l[19] = "\u0015VQO\u001f$\u0015\nGQ`..S\u0012\u0004\u001d(H\u000e\u0015D\u000eG\u0012\u0003VT\u00026A\u0003\u0014@`";
      l[20] = "1s\u000f=dMu`\u0011f\tAms\u0011j^\u00163$I\u00067\u0013pnM?x\u0012t~";
      l[21] = "\u0005H\u0002\u0011\u0011B[G^\u0000(\u0016ZE\u000b\u0005\u007fB\u0005\u0019^Y(F[E\u0017P\u0012\u0018T\u0019\u0006";
      l[22] = "rMZ.wN!M\u0018:\u0015Z#]G yhu\u0018\u0018{(?pAJ?,\u0005.N\u0016.\u0015\u0000*PY'\u007fMp\u001dAG";
      l[23] = "):a6XY9{=7\"U.\u000570I\u000f*t7hBR@lb5\u001dH+|#i\u001c2";
      l[24] = "i%alu?49d|In7-kb\u001e=gy6\u000eyz=:1tveg8";
      l[25] = "\u001ce'\"l.Oee6\u000e:Mu:,b\b\u001b7`s?_\u001fm+5n5R7f-\u000e";
      l[26] = ")L\u0005S\u007fLzLGG\u001dXx\\\u0018]qj(\u001eE\u0002\u001d\u0003uM\u0000\u0003']z\u0011\u0011:\"Yd^\u0018Po\u0003)Fx";
   
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
         if (var8 != '$' && var8 != 216 && var8 != 227 && var8 != 162) {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'X') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'e') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == '$') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 216) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 227) {
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

   @S0118
   public void a(S0049 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(6,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$9$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=17&&f.pc<30&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=32;continue;}
if(f.pc>=22&&f.pc<47&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=49;continue;}
if(f.pc>=38&&f.pc<59&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=59;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   @Override
   public Set a$return$93f6648b(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      return __indy$15(S0591.Crystal, -2448299948334825904L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
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
         case 0 -> 56;
         case 1 -> 53;
         case 2 -> 2;
         case 3 -> 30;
         case 4 -> 20;
         case 5 -> 33;
         case 6 -> 3;
         case 7 -> 40;
         case 8 -> 49;
         case 9 -> 46;
         case 10 -> 47;
         case 11 -> 0;
         case 12 -> 26;
         case 13 -> 25;
         case 14 -> 17;
         case 15 -> 51;
         case 16 -> 28;
         case 17 -> 6;
         case 18 -> 38;
         case 19 -> 5;
         case 20 -> 44;
         case 21 -> 21;
         case 22 -> 11;
         case 23 -> 63;
         case 24 -> 1;
         case 25 -> 12;
         case 26 -> 45;
         case 27 -> 24;
         case 28 -> 62;
         case 29 -> 14;
         case 30 -> 60;
         case 31 -> 4;
         case 32 -> 41;
         case 33 -> 43;
         case 34 -> 7;
         case 35 -> 15;
         case 36 -> 39;
         case 37 -> 55;
         case 38 -> 16;
         case 39 -> 34;
         case 40 -> 32;
         case 41 -> 58;
         case 42 -> 29;
         case 43 -> 18;
         case 44 -> 48;
         case 45 -> 36;
         case 46 -> 50;
         case 47 -> 31;
         case 48 -> 61;
         case 49 -> 10;
         case 50 -> 37;
         case 51 -> 19;
         case 52 -> 27;
         case 53 -> 54;
         case 54 -> 52;
         case 55 -> 23;
         case 56 -> 59;
         case 57 -> 13;
         case 58 -> 8;
         case 59 -> 42;
         case 60 -> 9;
         case 61 -> 57;
         case 62 -> 22;
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
            var12 = n(1120932609912917L, 0L);
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
            var23 = n(1120932609912917L, 0L);
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
            var23 = n(1120932609912917L, 0L);
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

   private static native class_1297 __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_746 __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native class_6880 __indy$6(long var0, long var2) throws Throwable;

   private static native class_1293 __indy$7(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native class_5529 __indy$10(long var0, long var2) throws Throwable;

   private static native void __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_5529 __indy$12(long var0, long var2) throws Throwable;

   private static native void __indy$13(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native Set __indy$15(Object var0, long var1, long var3) throws Throwable;



private void __flow$9$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0308.k,2);f.pc=1;return;}
case 1:{f.push(80288325639541L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(-7153550575121786035L,2);f.pc=5;return;}
case 5:{f.push(f.locals[2],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.push(f.locals[1],1);f.pc=10;return;}
case 10:{f.push(0,1);f.pc=11;return;}
case 11:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=12;return;}
case 12:{f.push(-7153475093738368322L,2);f.pc=13;return;}
case 13:{f.push(f.locals[2],2);f.pc=14;return;}
case 14:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$3(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=15;return;}
case 15:{f.locals[5]=f.pop();f.pc=16;return;}
case 16:{f.locals[4]=f.pop();f.pc=17;return;}
case 17:{f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.push(f.locals[5],1);f.pc=20;return;}
case 20:{f.push((f.pop() instanceof net.minecraft.class_1511?1:0),1);f.pc=21;return;}
case 21:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=92;return;}f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(dev.zprestige.sourceview.S0308.b,1);f.pc=25;return;}
case 25:{f.push(-7153947471071109868L,2);f.pc=26;return;}
case 26:{f.push(f.locals[2],2);f.pc=27;return;}
case 27:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=28;return;}
case 28:{f.push(f.locals[4],1);f.pc=29;return;}
case 29:{if(f.pop()!=null){f.pc=87;return;}f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=38;return;}
case 32:{f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.push(-7153669639287164780L,2);f.pc=35;return;}
case 35:{f.push(f.locals[2],2);f.pc=36;return;}
case 36:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=37;return;}
case 37:{throw (Throwable)f.pop();}
case 38:{f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.push(-7153086329777982044L,2);f.pc=41;return;}
case 41:{f.push(f.locals[2],2);f.pc=42;return;}
case 42:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$6(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=43;return;}
case 43:{f.push(-7153512265605673117L,2);f.pc=44;return;}
case 44:{f.push(f.locals[2],2);f.pc=45;return;}
case 45:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$7(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=46;return;}
case 46:{if(f.pop()==null){f.pc=65;return;}f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=55;return;}
case 49:{f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.push(-7153669639287164780L,2);f.pc=52;return;}
case 52:{f.push(f.locals[2],2);f.pc=53;return;}
case 53:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=54;return;}
case 54:{throw (Throwable)f.pop();}
case 55:{f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.done=true;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.push(-7153669639287164780L,2);f.pc=62;return;}
case 62:{f.push(f.locals[2],2);f.pc=63;return;}
case 63:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=64;return;}
case 64:{throw (Throwable)f.pop();}
case 65:{f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.pc=68;return;}
case 68:{f.push(f.locals[5],1);f.pc=69;return;}
case 69:{f.push(-7153860385080872303L,2);f.pc=70;return;}
case 70:{f.push(f.locals[2],2);f.pc=71;return;}
case 71:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$10(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=72;return;}
case 72:{f.push(-7152921378055417990L,2);f.pc=73;return;}
case 73:{f.push(f.locals[2],2);f.pc=74;return;}
case 74:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0308.__indy$11(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=75;return;}
case 75:{f.pc=76;return;}
case 76:{f.pc=77;return;}
case 77:{f.push(f.locals[5],1);f.pc=78;return;}
case 78:{f.push(-7153860385080872303L,2);f.pc=79;return;}
case 79:{f.push(f.locals[2],2);f.pc=80;return;}
case 80:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0308.__indy$12(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=81;return;}
case 81:{f.push(-7153795310098433825L,2);f.pc=82;return;}
case 82:{f.push(f.locals[2],2);f.pc=83;return;}
case 83:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0308.__indy$13(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.pc=86;return;}
case 86:{f.push(f.locals[5],1);f.pc=87;return;}
case 87:{f.pc=88;return;}
case 88:{f.pc=89;return;}
case 89:{f.push(-7153018114555847968L,2);f.pc=90;return;}
case 90:{f.push(f.locals[2],2);f.pc=91;return;}
case 91:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0308.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.pc=94;return;}
case 94:{f.pc=95;return;}
case 95:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

public S0308()throws Throwable{super();}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(7209037370114055963L,2);f.pc=1;return;}
case 1:{f.push(8728924628862883385L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(232729504420484L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0308.class,"k",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(27,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0308.class,"l",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(27,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0308.class,"m",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0308.f();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
