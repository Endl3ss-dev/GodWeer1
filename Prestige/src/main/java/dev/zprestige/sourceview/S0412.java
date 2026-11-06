package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0029;
import dev.zprestige.sourceview.S0033;
import dev.zprestige.sourceview.S0051;
import dev.zprestige.sourceview.S0108;
import dev.zprestige.sourceview.S0110;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0139;
import dev.zprestige.sourceview.S0140;
import dev.zprestige.sourceview.S0142;
import dev.zprestige.sourceview.S0148;
import dev.zprestige.sourceview.S0154;
import dev.zprestige.sourceview.S0251;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0549;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_329;
import net.minecraft.class_638;

public class S0412 extends S0260 {
   public S0251 d;
   public S0251 a;
   public S0251 c;
   public S0251 e;
   public S0251 f;
   public S0251 g;
   public S0251 h;
   public S0251 i;
   public S0251 j;
   public S0251 k;
   public static long l;
   public static Object[] m;
   public static String[] n;

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
         throw new RuntimeException("dev/zprestige/prestige/fp" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class n(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = m(var0, var2);
      Object var6 = m[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(n[var4]);
            m[var4] = var5;
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

      m[0] = "*LJ@uv<LO\u001afa+\u0007L\u001cju:@[\u000b!`>";
      m[1] = "3\u001cM\f\u0001\u000e8\u0013\\Cb\u0003-\u001eS(W\u0001<\rO\u0004@\f";
      m[2] = "{Ma}vamMd'evz\u0006g!ibkAp6\"pW";
      m[3] = "\f8:-n1y\u00181\"\u007f~\u0018\u0016:){$l";
      m[4] = void.class;
      n[4] = "java/lang/Void";
      m[5] = "RZd@\u0006aRZs\u001c\nnH\u0011s\u0002\n{O`#_[";
      m[6] = "!\u000f\u0015\u00102 !\u000f\u0002L>/;D\u0002R>:<5W\rg";
      m[7] = "|P(67}w_9y[~y];6w";
      m[8] = boolean.class;
      n[8] = "java/lang/Boolean";
      m[9] = "\\f\u0003\u00037\u0001Jf\u0006Y$\u0016]-\u0005_(\u0002Lj\u0012Hc\u0015s";
      m[10] = "&'\\\u000e\u0006\u0006-(MAg\b&#I\u001b";
      m[11] = "9=\u0012ZTM9=\u0005\u0006XB#v\u0005\u0018XW$\u0007UF\u0000";
      m[12] = "13%/z32;0;\u0011)c>>7}\u001b5~fh*L5(\"3 6|{>)\u0011";
      m[13] = "\u0018\u0016\u0011C\"d\u0015Q\u0014.t\u0019\u0010\u001e\u001cAgfRTPJ\u001d \u0018\u001eBTbbRRI.";
      m[14] = "B$Zo+\\H=Kq\u0014Y\u0018,AgC\u000eFy\u0018\u000b+F\u0002>Nt(N\u0017*";
      m[15] = "\u000flZcDpRfNb-(65GxHyPi\u0016xFA\reT}\u001c;D6Hg-";
      m[16] = "\u0019/d\u001c,-\u00136u\u0002\u0013(C'\u007f\u0014D\u007f\u001cz$x/+@$+\u0017hyN'";
      m[17] = "+\u007f\t\u001d!q$|\u001bXCr\u0015?U[s#$o\u0010\u0010=\u001b";
      m[18] = " 0\u0002vh%}=\u0012gV-z<\u001ck?!C2\u001c{;G *\u00168i!c0\u001avV";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 227 && var8 != 243 && var8 != 239 && var8 != 233) {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 186) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 209) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 227) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 243) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 239) {
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

   @S0118
   public void a(S0139 var1) {
try {

      long var2 = l ^ 100807903569770L;

      try {
         if (__indy$3((Boolean)(Object)(__indy$2(this.c, -6369370598569639412L, var2)), -6369331140668533173L, var2)) {
            __indy$4(var1, new Object[0], -6368990942845122403L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$5(var4, -6369121582579841246L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0110 var1) {
try {

      long var2 = l ^ 99113902796351L;

      try {
         if (__indy$7((Boolean)(Object)(__indy$6(this.d, 3372759220566821721L, var2)), 3372798755695877918L, var2)) {
            __indy$8(var1, new Object[0], 3371452229720916424L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$9(var4, 3371318298933300855L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0154 var1) {
try {

      long var2 = l ^ 48905954093585L;

      try {
         if (__indy$11((Boolean)(Object)(__indy$10(this.i, -6133897219746721929L, var2)), -6133655546188571856L, var2)) {
            __indy$12(var1, new Object[0], -6131726008434014746L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$13(var4, -6131894436398691751L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0051 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(4,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$13$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=4&&f.pc<22&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=24;continue;}
if(f.pc>=16&&f.pc<39&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=41;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   @S0118
   public void a(S0108 var1) {
try {

      long var2 = l ^ 122302276281154L;

      try {
         if (__indy$21((Boolean)(Object)(__indy$20(this.h, 7184164700169651748L, var2)), 7184265627638272611L, var2)) {
            __indy$22(var1, new Object[0], 7184641014281964725L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$23(var4, 7184474228639253258L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0140 var1) {
try {

      long var2 = l ^ 135352243345027L;

      try {
         if (__indy$25((Boolean)(Object)(__indy$24(this.g, -7605924896289672219L, var2)), -7605814889252886622L, var2)) {
            __indy$26(var1, new Object[0], -7604912024775853708L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$27(var4, -7605043618027129141L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0033 var1) {
try {

      long var2 = l ^ 91133880698580L;

      try {
         if (__indy$29((Boolean)(Object)(__indy$28(this.f, -4168791317852265550L, var2)), -4168751748292265995L, var2)) {
            __indy$30(var1, new Object[0], -4169553466255529693L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$31(var4, -4169668155589761380L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0029 var1) {
try {

      long var2 = l ^ 132994727869957L;

      try {
         if (__indy$33((Boolean)(Object)(__indy$32(this.a, 4536339191494744931L, var2)), 4536448949410909988L, var2)) {
            __indy$34(var1, new Object[0], 4536232176026636786L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$35(var4, 4536100025000042061L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0142 var1) {
try {

      long var2 = l ^ 9192080531004L;

      try {
         if (__indy$37((Boolean)(Object)(__indy$36(this.j, 2219466323925744474L, var2)), 2219646665338908445L, var2)) {
            __indy$39(__indy$38(b, 2218858659248048903L, var2), 2218684668956490559L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$40(var4, 2218592747684085364L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0148 var1) {
try {

      long var2 = l ^ 42949259614253L;

      try {
         if (__indy$42((Boolean)(Object)(__indy$41(this.k, 1791323858933136715L, var2)), 1791565901851191564L, var2)) {
            __indy$43(var1, new Object[0], 1791219733972776922L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$44(var4, 1791070402539862117L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int m(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (n[var4] != null) {
         return var4;
      }

      Object var5 = m[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 53;
         case 1 -> 50;
         case 2 -> 38;
         case 3 -> 21;
         case 4 -> 10;
         case 5 -> 23;
         case 6 -> 25;
         case 7 -> 30;
         case 8 -> 16;
         case 9 -> 41;
         case 10 -> 7;
         case 11 -> 14;
         case 12 -> 26;
         case 13 -> 31;
         case 14 -> 35;
         case 15 -> 17;
         case 16 -> 22;
         case 17 -> 2;
         case 18 -> 20;
         case 19 -> 63;
         case 20 -> 19;
         case 21 -> 55;
         case 22 -> 44;
         case 23 -> 62;
         case 24 -> 29;
         case 25 -> 52;
         case 26 -> 1;
         case 27 -> 58;
         case 28 -> 28;
         case 29 -> 43;
         case 30 -> 48;
         case 31 -> 0;
         case 32 -> 34;
         case 33 -> 4;
         case 34 -> 32;
         case 35 -> 33;
         case 36 -> 40;
         case 37 -> 9;
         case 38 -> 5;
         case 39 -> 59;
         case 40 -> 6;
         case 41 -> 15;
         case 42 -> 46;
         case 43 -> 8;
         case 44 -> 51;
         case 45 -> 45;
         case 46 -> 37;
         case 47 -> 47;
         case 48 -> 57;
         case 49 -> 11;
         case 50 -> 54;
         case 51 -> 39;
         case 52 -> 27;
         case 53 -> 3;
         case 54 -> 42;
         case 55 -> 36;
         case 56 -> 24;
         case 57 -> 60;
         case 58 -> 18;
         case 59 -> 61;
         case 60 -> 13;
         case 61 -> 56;
         case 62 -> 49;
         default -> 12;
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

      n[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field o(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = m[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = n[var4];
      int var7 = var6.indexOf(8);
      Class var8 = n(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = n(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = g(var12, var10, var11);
         if (var13 != null) {
            m[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = h(var14[var15], var10, var11);
               if (var13 != null) {
                  m[var4] = var13;
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
            var12 = n(732879092782831L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method p(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = m[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = n[var4];
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
            m[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = n(732879092782831L, 0L);
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
                  m[var4] = var19;
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
            var23 = n(732879092782831L, 0L);
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

   private static native Object __indy$2(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$4(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$8(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$12(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$18(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$22(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$24(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$26(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$30(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$32(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$33(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$34(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$37(Object var0, long var1, long var3) throws Throwable;

   private static native class_329 __indy$38(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$40(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$41(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$42(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$43(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$44(Object var0, long var1, long var3) throws Throwable;

public S0412()throws Throwable{super();}

private void __flow$13$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0412.l,2);f.pc=1;return;}
case 1:{f.push(122205815048775L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.pc=5;return;}
case 5:{f.pc=6;return;}
case 6:{f.push(f.locals[0],1);f.pc=7;return;}
case 7:{f.push(((dev.zprestige.sourceview.S0412)(Object)f.pop()).e,1);f.pc=8;return;}
case 8:{f.push(-3839802855270977759L,2);f.pc=9;return;}
case 9:{f.push(f.locals[2],2);f.pc=10;return;}
case 10:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0412.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=11;return;}
case 11:{f.push((java.lang.Boolean)(Object)f.pop(),1);f.pc=12;return;}
case 12:{f.push(-3839701755988001946L,2);f.pc=13;return;}
case 13:{f.push(f.locals[2],2);f.pc=14;return;}
case 14:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0412.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=15;return;}
case 15:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=47;return;}f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.push(dev.zprestige.sourceview.S0412.b,1);f.pc=18;return;}
case 18:{f.push(-3839818194524462415L,2);f.pc=19;return;}
case 19:{f.push(f.locals[2],2);f.pc=20;return;}
case 20:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0412.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=21;return;}
case 21:{if(f.pop()==null){f.pc=47;return;}f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.pc=30;return;}
case 24:{f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.push(-3841182217062290929L,2);f.pc=27;return;}
case 27:{f.push(f.locals[2],2);f.pc=28;return;}
case 28:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0412.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=29;return;}
case 29:{throw (Throwable)f.pop();}
case 30:{f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{f.push(f.locals[1],1);f.pc=34;return;}
case 34:{f.push(0,1);f.pc=35;return;}
case 35:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=36;return;}
case 36:{f.push(-3841015292336331344L,2);f.pc=37;return;}
case 37:{f.push(f.locals[2],2);f.pc=38;return;}
case 38:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0412.__indy$18(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.pc=47;return;}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.push(-3841182217062290929L,2);f.pc=44;return;}
case 44:{f.push(f.locals[2],2);f.pc=45;return;}
case 45:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0412.__indy$19(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=46;return;}
case 46:{throw (Throwable)f.pop();}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.pc=50;return;}
case 50:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-3722564609427564698L,2);f.pc=1;return;}
case 1:{f.push(-2515507805238668968L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(130699422139222L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0412.class,"l",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(19,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0412.class,"m",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(19,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0412.class,"n",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0412.f();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
