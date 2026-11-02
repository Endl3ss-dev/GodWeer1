package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0109;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0549;
import net.minecraft.class_1747;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_304;
import net.minecraft.class_315;
import net.minecraft.class_638;
import net.minecraft.class_746;

public class S0316 extends S0260 {
   public boolean i = false;
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
         throw new RuntimeException("dev/zprestige/prestige/eL" + " : " + var1 + " : " + var2.toString(), var5);
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

      l[0] = "F\u0010^P$EF\u0010I\f(J\\[I\u0012(_[*\u0019O|";
      l[1] = "\u0006{\\c\u0016\u000f\u0006{K?\u001a\u0000\u001c0K!\u001a\u0015\u001bA\u001b}O";
      l[2] = "=-r%WU=-ey[Z'feg[O \u001708\u0002";
      l[3] = "tC\u0001\u0018#ctC\u0016D/ln\b\u0016Z/yiyG\u0005}2";
      l[4] = "{jL\u000e\b){j[R\u0004&a![L\u00043fP\n\u0016]p";
      l[5] = "6m0;VB m5aEU7&6gIA&a!p\u0002S\u001a";
      l[6] = "Q1(\u0015P\u001d$\u0011#\u001aARY\t0\u001dH\u001b1";
      l[7] = "E\u0010I-}[E\u0010^qqT_[^oqAX*\u000e2 ";
      l[8] = "h,pyLEh,g%@Jrgg;@_u\u00163c\u0017";
      l[9] = "\u001ar=b\f\u0000\u001ar*>\u0000\u000f\u00009* \u0000\u001a\u0007Hx{XP";
      l[10] = "\u001d\u00191\t^\u000b\u001d\u0019&UR\u0004\u0007R&KR\u0011\u0000#t\u0010\nP";
      l[11] = float.class;
      m[11] = "java/lang/Float";
      l[12] = "R\u0001!\u001dr>R\u00016A~1HJ6_~$O;g\u0000(c";
      l[13] = boolean.class;
      m[13] = "java/lang/Boolean";
      l[14] = void.class;
      m[14] = "java/lang/Void";
      l[15] = "I\u001aP}>U_\u001aU'-BHQV!!VY\u0016A6j@a";
      l[16] = "^I7xICUF&7*N@K)\\\u001fLQX5p\bA";
      l[17] = "G#T_HKQ#Q\u0005[\\FhR\u0003WHW/E\u0014\u001cX\u001b";
      l[18] = ";\u001dTP\t%N=__\u0018j/3TT\u001c0[";
      l[19] = "z\u001d6=\bhz\u001d!a\u0004g`V!\u007f\u0004rg'p'V";
      l[20] = "Q\u001c=ur4Z\u0013,:\u0013:Q\u0018(`";
      l[21] = "qR}\u00129@yCf{gH~\\l,0\u0019(\u0002\u0000B8Jp_gG0Dq";
      l[22] = "Bgnax{Ky1\"\u0016g\u0016|?|zUK;e#\u00168J|2{o2\u0018g3\u001b-i\u0019df -z\u0004<_";
      l[23] = "5g\u0011\u0016x\u0000szPU\u0015\u0011\u000e!\u0011\u0019y\u001carJXk{";
      l[24] = "Jl~]0m\u001bv~QL}\u0016k%P\u001b*H<}<qb\u001cw~\u0000w#\u0016{";
      l[25] = "@/m\u007f]\u0004\u00115ms!\u0014\u001c(6rvCCum\u001e\u0018\u001dL?<g\u0011\u0003\u0013|";
      l[26] = "/KoC\u001b\u0017o\u00158T!\u0016|Xa_M$.\u00159\t!L!_pUZJ(B18";
      l[27] = "gG;\u0012*Na\u00061\u001e\u0015\u00177J0\u0004y%a\bl^(reDh\fuJ8Lj\u001b\u0015";
      l[28] = "sD%\\\u000f<u\u0005/P0e#I.J\\Wp\rq\u001c0>![5\u0017T~\u007f\f\"-";
      l[29] = "\nQO!\u00163\f\u0010E-)jZ\\D7EX\t\u0018\u0018o)1XN_jMq\u0006\u0019HP";
      l[30] = "akU1\u000b\u000bk9N0k\u00176&I;\u0007%gk\u0017g[rk;U'\u0011\f0!B>kHj&D<\u0012B8=E\\";
      l[31] = "\u001ahz6XR\u001a{gna\fL\u007fx5\r>\u001d>$m]i\u0010hhhX\u0007E>eba";
      l[32] = "\u0018OB\\5\u0001\u0012\u001dY]U\u001dO\u0002^V9/\u001bF\u0006\u000eix\u0018OB\\5\u0001\u0012\u001dY]U";
      l[33] = "#>#\u001coxr$#\u0010\u0013h\u007f9x\u0011D? e$}qlc=,\u0014y}x";
      l[34] = "O.nL8<J&`M_>\u001bcmH3\fL$1\u001ff[Gt}\u0015f5\u0012\"p\u001f_j\u0006|i\u001ef5Fft/";
      l[35] = "U.(\u0015\\t]ui\u001f9-<\"qCF;]/j\u0010KDV/&\fF%[4u\u00019";
      l[36] = "\u001b5A\u001b)e\u0001q\u0016GQ4q#ONn`\u000e(UKl^\u001d(\u0012G2gN!REQ";
   
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
         if (var8 != 'S' && var8 != 'M' && var8 != 235 && var8 != 'm') {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 201) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'p') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'S') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'M') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 235) {
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
   public void a(S0109 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(9,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$10$0(f);break;
case 1:__flow$10$1(f);break;
case 2:__flow$10$2(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=14&&f.pc<31&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=33;continue;}
if(f.pc>=29&&f.pc<52&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=54;continue;}
if(f.pc>=60&&f.pc<65&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=67;continue;}
if(f.pc>=85&&f.pc<99&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=101;continue;}
if(f.pc>=137&&f.pc<154&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=156;continue;}
if(f.pc>=152&&f.pc<180&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=182;continue;}
if(f.pc>=162&&f.pc<191&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=193;continue;}
if(f.pc>=188&&f.pc<220&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=222;continue;}
if(f.pc>=199&&f.pc<231&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=233;continue;}
if(f.pc>=250&&f.pc<256&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=258;continue;}
if(f.pc>=254&&f.pc<270&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=272;continue;}
if(f.pc>=264&&f.pc<281&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=283;continue;}
if(f.pc>=289&&f.pc<308&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=310;continue;}
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
         case 0 -> 49;
         case 1 -> 54;
         case 2 -> 43;
         case 3 -> 17;
         case 4 -> 44;
         case 5 -> 55;
         case 6 -> 26;
         case 7 -> 30;
         case 8 -> 42;
         case 9 -> 58;
         case 10 -> 62;
         case 11 -> 57;
         case 12 -> 11;
         case 13 -> 20;
         case 14 -> 0;
         case 15 -> 33;
         case 16 -> 4;
         case 17 -> 34;
         case 18 -> 31;
         case 19 -> 52;
         case 20 -> 47;
         case 21 -> 24;
         case 22 -> 15;
         case 23 -> 7;
         case 24 -> 13;
         case 25 -> 32;
         case 26 -> 60;
         case 27 -> 38;
         case 28 -> 25;
         case 29 -> 53;
         case 30 -> 8;
         case 31 -> 22;
         case 32 -> 9;
         case 33 -> 37;
         case 34 -> 27;
         case 35 -> 39;
         case 36 -> 29;
         case 37 -> 18;
         case 38 -> 1;
         case 39 -> 50;
         case 40 -> 61;
         case 41 -> 23;
         case 42 -> 14;
         case 43 -> 3;
         case 44 -> 46;
         case 45 -> 6;
         case 46 -> 59;
         case 47 -> 21;
         case 48 -> 48;
         case 49 -> 5;
         case 50 -> 36;
         case 51 -> 16;
         case 52 -> 35;
         case 53 -> 56;
         case 54 -> 10;
         case 55 -> 2;
         case 56 -> 28;
         case 57 -> 51;
         case 58 -> 40;
         case 59 -> 19;
         case 60 -> 12;
         case 61 -> 45;
         case 62 -> 63;
         default -> 41;
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
            var12 = n(1428517379113988L, 0L);
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
            var23 = n(1428517379113988L, 0L);
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
            var23 = n(1428517379113988L, 0L);
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

   private static native class_746 __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native class_1799 __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native class_1792 __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native class_1799 __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native class_1792 __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native float __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native class_2338 __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native class_2338 __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native class_2680 __indy$19(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native class_2338 __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native class_2680 __indy$24(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native class_2338 __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native class_2338 __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native class_2680 __indy$31(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$32(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$33(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$34(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$37(Object var0, long var1, long var3) throws Throwable;

   private static native class_315 __indy$38(Object var0, long var1, long var3) throws Throwable;

   private static native class_304 __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$40(Object var0, boolean var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$41(Object var0, long var1, long var3) throws Throwable;

   private static native class_315 __indy$42(Object var0, long var1, long var3) throws Throwable;

   private static native class_304 __indy$43(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$44(Object var0, boolean var1, long var2, long var4) throws Throwable;







private void __flow$10$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0316.k,2);f.pc=1;return;}
case 1:{f.push(77742256541483L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(38584226824686L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.push(-8100117728714575995L,2);f.pc=11;return;}
case 11:{f.push(f.locals[2],2);f.pc=12;return;}
case 12:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=13;return;}
case 13:{f.locals[6]=f.pop();f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.push(dev.zprestige.sourceview.S0316.b,1);f.pc=17;return;}
case 17:{f.push(-8100483484934196542L,2);f.pc=18;return;}
case 18:{f.push(f.locals[2],2);f.pc=19;return;}
case 19:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=20;return;}
case 20:{f.push(-8100794136842036452L,2);f.pc=21;return;}
case 21:{f.push(f.locals[2],2);f.pc=22;return;}
case 22:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=23;return;}
case 23:{f.push(-8100324504110231010L,2);f.pc=24;return;}
case 24:{f.push(f.locals[2],2);f.pc=25;return;}
case 25:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=26;return;}
case 26:{f.push((f.pop() instanceof net.minecraft.class_1747?1:0),1);f.pc=27;return;}
case 27:{f.push(f.locals[6],1);f.pc=28;return;}
case 28:{if(f.pop()!=null){f.pc=60;return;}f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=79;return;}f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=39;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.push(-8098719626324618683L,2);f.pc=36;return;}
case 36:{f.push(f.locals[2],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{throw (Throwable)f.pop();}
case 39:{f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.push(dev.zprestige.sourceview.S0316.b,1);f.pc=42;return;}
case 42:{f.push(-8100483484934196542L,2);f.pc=43;return;}
case 43:{f.push(f.locals[2],2);f.pc=44;return;}
case 44:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=45;return;}
case 45:{f.push(-8100743470421478187L,2);f.pc=46;return;}
case 46:{f.push(f.locals[2],2);f.pc=47;return;}
case 47:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=48;return;}
case 48:{f.push(-8100324504110231010L,2);f.pc=49;return;}
case 49:{f.push(f.locals[2],2);f.pc=50;return;}
case 50:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=51;return;}
case 51:{f.push((f.pop() instanceof net.minecraft.class_1747?1:0),1);f.pc=52;return;}
case 52:{f.pc=53;return;}
case 53:{f.pc=60;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.push(-8098719626324618683L,2);f.pc=57;return;}
case 57:{f.push(f.locals[2],2);f.pc=58;return;}
case 58:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=59;return;}
case 59:{throw (Throwable)f.pop();}
case 60:{f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.push(f.locals[6],1);f.pc=63;return;}
case 63:{if(f.pop()!=null){f.pc=76;return;}f.pc=64;return;}
case 64:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=79;return;}f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.pc=73;return;}
case 67:{f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.push(-8098719626324618683L,2);f.pc=70;return;}
case 70:{f.push(f.locals[2],2);f.pc=71;return;}
case 71:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=72;return;}
case 72:{throw (Throwable)f.pop();}
case 73:{f.pc=74;return;}
case 74:{f.pc=75;return;}
case 75:{f.push(1,1);f.pc=76;return;}
case 76:{f.pc=77;return;}
case 77:{f.pc=78;return;}
case 78:{f.pc=82;return;}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.push(0,1);f.pc=82;return;}
case 82:{f.pc=83;return;}
case 83:{f.pc=84;return;}
case 84:{f.locals[7]=f.pop();f.pc=85;return;}
case 85:{f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.push(dev.zprestige.sourceview.S0316.b,1);f.pc=88;return;}
case 88:{f.push(-8100483484934196542L,2);f.pc=89;return;}
case 89:{f.push(f.locals[2],2);f.pc=90;return;}
case 90:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=91;return;}
case 91:{f.push(-8100419332654212839L,2);f.pc=92;return;}
case 92:{f.push(f.locals[2],2);f.pc=93;return;}
case 93:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=94;return;}
case 94:{f.push(Float.intBitsToFloat(1116471296),1);f.pc=95;return;}
case 95:{float b=dev.zprestige.sourceview.FlowFrame.f(f.pop()),a=dev.zprestige.sourceview.FlowFrame.f(f.pop());f.push(dev.zprestige.sourceview.FlowFrame.compareFloat(a,b,1),1);f.pc=96;return;}
case 96:{f.push(f.locals[6],1);f.pc=97;return;}
case 97:{if(f.pop()!=null){f.pc=111;return;}f.pc=98;return;}
case 98:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())>=0){f.pc=114;return;}f.pc=99;return;}
case 99:{f.pc=100;return;}
case 100:{f.pc=107;return;}
case 101:{f.pc=102;return;}
case 102:{f.pc=103;return;}
case 103:{f.push(-8098719626324618683L,2);f.pc=104;return;}
case 104:{f.push(f.locals[2],2);f.pc=105;return;}
case 105:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=106;return;}
case 106:{throw (Throwable)f.pop();}
case 107:{f.pc=108;return;}
case 108:{f.pc=109;return;}
case 109:{f.pc=110;return;}
case 110:{f.push(1,1);f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.pc=113;return;}
case 113:{f.locals[7]=f.pop();f.pc=114;return;}
case 114:{f.pc=115;return;}
case 115:{f.pc=116;return;}
case 116:{f.pc=117;return;}
case 117:{f.push(f.locals[4],2);f.pc=118;return;}
case 118:{f.push(1,1);f.pc=119;return;}
case 119:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$10$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.rearrange(91);f.pc=121;return;}
case 121:{f.rearrange(91);f.pc=122;return;}
case 122:{f.rearrange(87);f.pc=123;return;}
case 123:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=124;return;}
case 124:{f.push(0,1);f.pc=125;return;}
case 125:{f.rearrange(95);f.pc=126;return;}
case 126:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=127;return;}
case 127:{f.push(-8099085349482677516L,2);f.pc=128;return;}
case 128:{f.push(f.locals[2],2);f.pc=129;return;}
case 129:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=130;return;}
case 130:{f.push(-8100596764605973266L,2);f.pc=131;return;}
case 131:{f.push(f.locals[2],2);f.pc=132;return;}
case 132:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=133;return;}
case 133:{f.push(-8098765675485330246L,2);f.pc=134;return;}
case 134:{f.push(f.locals[2],2);f.pc=135;return;}
case 135:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=136;return;}
case 136:{f.locals[8]=f.pop();f.pc=137;return;}
case 137:{f.pc=138;return;}
case 138:{f.pc=139;return;}
case 139:{f.push(dev.zprestige.sourceview.S0316.b,1);f.pc=140;return;}
case 140:{f.push(-8100511792852560023L,2);f.pc=141;return;}
case 141:{f.push(f.locals[2],2);f.pc=142;return;}
case 142:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=143;return;}
case 143:{f.push(f.locals[8],1);f.pc=144;return;}
case 144:{f.push(-8100006139514004093L,2);f.pc=145;return;}
case 145:{f.push(f.locals[2],2);f.pc=146;return;}
case 146:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$19(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=147;return;}
case 147:{f.push(-8100703226653559966L,2);f.pc=148;return;}
case 148:{f.push(f.locals[2],2);f.pc=149;return;}
case 149:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=150;return;}
case 150:{f.push(f.locals[6],1);f.pc=151;return;}
case 151:{if(f.pop()!=null){f.pc=243;return;}f.pc=152;return;}
case 152:{f.pc=153;return;}
case 153:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=239;return;}f.pc=154;return;}
case 154:{f.pc=155;return;}
case 155:{f.pc=162;return;}
case 156:{f.pc=157;return;}
case 157:{f.pc=158;return;}
case 158:{f.push(-8098719626324618683L,2);f.pc=159;return;}
case 159:{f.push(f.locals[2],2);f.pc=160;return;}
case 160:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=161;return;}
case 161:{throw (Throwable)f.pop();}
case 162:{f.pc=163;return;}
case 163:{f.pc=164;return;}
case 164:{f.push(dev.zprestige.sourceview.S0316.b,1);f.pc=165;return;}
case 165:{f.push(-8100511792852560023L,2);f.pc=166;return;}
case 166:{f.push(f.locals[2],2);f.pc=167;return;}
case 167:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=168;return;}
case 168:{f.push(f.locals[8],1);f.pc=169;return;}
case 169:{f.push(-8098765675485330246L,2);f.pc=170;return;}
case 170:{f.push(f.locals[2],2);f.pc=171;return;}
case 171:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=172;return;}
case 172:{f.push(-8100006139514004093L,2);f.pc=173;return;}
case 173:{f.push(f.locals[2],2);f.pc=174;return;}
case 174:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$24(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=175;return;}
case 175:{f.push(-8100703226653559966L,2);f.pc=176;return;}
case 176:{f.push(f.locals[2],2);f.pc=177;return;}
case 177:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=178;return;}
case 178:{f.push(f.locals[6],1);f.pc=179;return;}
case 179:{if(f.pop()!=null){f.pc=243;return;}f.pc=180;return;}
case 180:{f.pc=181;return;}
case 181:{f.pc=188;return;}
case 182:{f.pc=183;return;}
case 183:{f.pc=184;return;}
case 184:{f.push(-8098719626324618683L,2);f.pc=185;return;}
case 185:{f.push(f.locals[2],2);f.pc=186;return;}
case 186:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=187;return;}
case 187:{throw (Throwable)f.pop();}
case 188:{f.pc=189;return;}
case 189:{f.pc=190;return;}
case 190:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=239;return;}f.pc=191;return;}
case 191:{f.pc=192;return;}
case 192:{f.pc=199;return;}
case 193:{f.pc=194;return;}
case 194:{f.pc=195;return;}
case 195:{f.push(-8098719626324618683L,2);f.pc=196;return;}
case 196:{f.push(f.locals[2],2);f.pc=197;return;}
case 197:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=198;return;}
case 198:{throw (Throwable)f.pop();}
case 199:{f.pc=200;return;}
case 200:{f.pc=201;return;}
case 201:{f.push(dev.zprestige.sourceview.S0316.b,1);f.pc=202;return;}
case 202:{f.push(-8100511792852560023L,2);f.pc=203;return;}
case 203:{f.push(f.locals[2],2);f.pc=204;return;}
case 204:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=205;return;}
case 205:{f.push(f.locals[8],1);f.pc=206;return;}
case 206:{f.push(-8098765675485330246L,2);f.pc=207;return;}
case 207:{f.push(f.locals[2],2);f.pc=208;return;}
case 208:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$29(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=209;return;}
case 209:{f.push(-8098765675485330246L,2);f.pc=210;return;}
case 210:{f.push(f.locals[2],2);f.pc=211;return;}
case 211:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$30(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=212;return;}
case 212:{f.push(-8100006139514004093L,2);f.pc=213;return;}
case 213:{f.push(f.locals[2],2);f.pc=214;return;}
case 214:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$31(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=215;return;}
case 215:{f.push(-8100703226653559966L,2);f.pc=216;return;}
case 216:{f.push(f.locals[2],2);f.pc=217;return;}
case 217:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$32(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=218;return;}
case 218:{f.push(f.locals[6],1);f.pc=219;return;}
case 219:{if(f.pop()!=null){f.pc=250;return;}f.pc=220;return;}
case 220:{f.pc=221;return;}
case 221:{f.pc=228;return;}
case 222:{f.pc=223;return;}
case 223:{f.pc=224;return;}
case 224:{f.push(-8098719626324618683L,2);f.pc=225;return;}
case 225:{f.push(f.locals[2],2);f.pc=226;return;}
case 226:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$33(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=227;return;}
case 227:{throw (Throwable)f.pop();}
case 228:{f.pc=229;return;}
case 229:{f.pc=230;return;}
case 230:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=246;return;}f.pc=231;return;}
case 231:{f.pc=232;return;}
case 232:{f.pc=239;return;}
case 233:{f.pc=234;return;}
case 234:{f.pc=235;return;}
case 235:{f.push(-8098719626324618683L,2);f.pc=236;return;}
case 236:{f.push(f.locals[2],2);f.pc=237;return;}
case 237:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$34(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=238;return;}
case 238:{throw (Throwable)f.pop();}
case 239:{f.pc=240;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$10$2(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 240:{f.pc=241;return;}
case 241:{f.pc=242;return;}
case 242:{f.push(1,1);f.pc=243;return;}
case 243:{f.pc=244;return;}
case 244:{f.pc=245;return;}
case 245:{f.locals[7]=f.pop();f.pc=246;return;}
case 246:{f.pc=247;return;}
case 247:{f.pc=248;return;}
case 248:{f.pc=249;return;}
case 249:{f.push(f.locals[7],1);f.pc=250;return;}
case 250:{f.pc=251;return;}
case 251:{f.pc=252;return;}
case 252:{f.push(f.locals[6],1);f.pc=253;return;}
case 253:{if(f.pop()!=null){f.pc=289;return;}f.pc=254;return;}
case 254:{f.pc=255;return;}
case 255:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=324;return;}f.pc=256;return;}
case 256:{f.pc=257;return;}
case 257:{f.pc=264;return;}
case 258:{f.pc=259;return;}
case 259:{f.pc=260;return;}
case 260:{f.push(-8098719626324618683L,2);f.pc=261;return;}
case 261:{f.push(f.locals[2],2);f.pc=262;return;}
case 262:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$35(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=263;return;}
case 263:{throw (Throwable)f.pop();}
case 264:{f.pc=265;return;}
case 265:{f.pc=266;return;}
case 266:{f.pc=267;return;}
case 267:{f.push(f.locals[0],1);f.pc=268;return;}
case 268:{f.push(f.locals[6],1);f.pc=269;return;}
case 269:{if(f.pop()!=null){f.pc=316;return;}f.pc=270;return;}
case 270:{f.pc=271;return;}
case 271:{f.pc=278;return;}
case 272:{f.pc=273;return;}
case 273:{f.pc=274;return;}
case 274:{f.push(-8098719626324618683L,2);f.pc=275;return;}
case 275:{f.push(f.locals[2],2);f.pc=276;return;}
case 276:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$36(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=277;return;}
case 277:{throw (Throwable)f.pop();}
case 278:{f.pc=279;return;}
case 279:{f.pc=280;return;}
case 280:{f.push(((dev.zprestige.sourceview.S0316)(Object)f.pop()).i,1);f.pc=281;return;}
case 281:{f.pc=282;return;}
case 282:{f.pc=289;return;}
case 283:{f.pc=284;return;}
case 284:{f.pc=285;return;}
case 285:{f.push(-8098719626324618683L,2);f.pc=286;return;}
case 286:{f.push(f.locals[2],2);f.pc=287;return;}
case 287:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$37(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=288;return;}
case 288:{throw (Throwable)f.pop();}
case 289:{f.pc=290;return;}
case 290:{f.pc=291;return;}
case 291:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=320;return;}f.pc=292;return;}
case 292:{f.pc=293;return;}
case 293:{f.pc=294;return;}
case 294:{f.push(dev.zprestige.sourceview.S0316.b,1);f.pc=295;return;}
case 295:{f.push(-8098860045740041586L,2);f.pc=296;return;}
case 296:{f.push(f.locals[2],2);f.pc=297;return;}
case 297:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$38(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=298;return;}
case 298:{f.push(-8100249346917445298L,2);f.pc=299;return;}
case 299:{f.push(f.locals[2],2);f.pc=300;return;}
case 300:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$39(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=301;return;}
case 301:{f.push(0,1);f.pc=302;return;}
case 302:{f.push(-8098632121926288014L,2);f.pc=303;return;}
case 303:{f.push(f.locals[2],2);f.pc=304;return;}
case 304:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0316.__indy$40(((java.lang.Object)(Object)(a0)),(dev.zprestige.sourceview.FlowFrame.i(a1) != 0),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=305;return;}
case 305:{f.pc=306;return;}
case 306:{f.pc=307;return;}
case 307:{f.push(f.locals[0],1);f.pc=308;return;}
case 308:{f.pc=309;return;}
case 309:{f.pc=316;return;}
case 310:{f.pc=311;return;}
case 311:{f.pc=312;return;}
case 312:{f.push(-8098719626324618683L,2);f.pc=313;return;}
case 313:{f.push(f.locals[2],2);f.pc=314;return;}
case 314:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$41(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=315;return;}
case 315:{throw (Throwable)f.pop();}
case 316:{f.pc=317;return;}
case 317:{f.pc=318;return;}
case 318:{f.push(0,1);f.pc=319;return;}
case 319:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0316.class,receiver,"i",(dev.zprestige.sourceview.FlowFrame.i(value) != 0));f.pc=320;return;}
case 320:{f.pc=321;return;}
case 321:{f.pc=322;return;}
case 322:{f.pc=323;return;}
case 323:{f.done=true;return;}
case 324:{f.pc=325;return;}
case 325:{f.pc=326;return;}
case 326:{f.pc=327;return;}
case 327:{f.push(dev.zprestige.sourceview.S0316.b,1);f.pc=328;return;}
case 328:{f.push(-8098860045740041586L,2);f.pc=329;return;}
case 329:{f.push(f.locals[2],2);f.pc=330;return;}
case 330:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$42(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=331;return;}
case 331:{f.push(-8100249346917445298L,2);f.pc=332;return;}
case 332:{f.push(f.locals[2],2);f.pc=333;return;}
case 333:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0316.__indy$43(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=334;return;}
case 334:{f.push(1,1);f.pc=335;return;}
case 335:{f.push(-8098632121926288014L,2);f.pc=336;return;}
case 336:{f.push(f.locals[2],2);f.pc=337;return;}
case 337:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0316.__indy$44(((java.lang.Object)(Object)(a0)),(dev.zprestige.sourceview.FlowFrame.i(a1) != 0),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=338;return;}
case 338:{f.pc=339;return;}
case 339:{f.pc=340;return;}
case 340:{f.push(f.locals[0],1);f.pc=341;return;}
case 341:{f.push(1,1);f.pc=342;return;}
case 342:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0316.class,receiver,"i",(dev.zprestige.sourceview.FlowFrame.i(value) != 0));f.pc=343;return;}
case 343:{f.pc=344;return;}
case 344:{f.pc=345;return;}
case 345:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

public S0316()throws Throwable{super();}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-3589562129518092548L,2);f.pc=1;return;}
case 1:{f.push(9140444372079369432L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(243174874459416L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0316.class,"k",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(37,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0316.class,"l",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(37,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0316.class,"m",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0316.f();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
