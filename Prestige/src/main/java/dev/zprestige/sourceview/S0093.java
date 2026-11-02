package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0094;
import dev.zprestige.sourceview.S0225;
import dev.zprestige.sourceview.S0364;
import dev.zprestige.sourceview.S0549;
import dev.zprestige.sourceview.S0593;
import net.minecraft.class_2680;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_243;
import net.minecraft.class_3610;

public class S0093 implements S0225 {
   public S0364 a;
   public class_243 b;
   public class_243 c;
   public class_243 d;
   public class_243 e;
   public S0094 f;
   public S0593 g;
   public static long h;
   public static Object[] i;
   public static String[] j;

   public S0093(class_243 var1, class_243 var2, S0593 var3, long var4)  throws Throwable {
      var4 = h ^ var4;
      long var6 = var4 ^ 71503997410734L;
      long var8 = var4 ^ 4340496562606L;
      long var10 = var4 ^ 114862446739809L;
      super();
try {

      this.a = new S0364(var6);
      this.b = var1;
      this.c = var2;
      this.e = var2;
      this.d = new class_243(
         (__indy$0(7365134765976251211L, var4) * 0.5 - 0.25) * 0.01,
         __indy$1(7365134765976251211L, var4) * 0.25 * 0.01,
         (__indy$2(7365134765976251211L, var4) * 0.5 - 0.25) * 0.01
      );
      this.g = var3;
      this.f = __indy$3(this, new Object[]{var10}, 7364711921507337812L, var4);
      __indy$4(this.a, new Object[]{var8}, 7364727345224623556L, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static int e(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (j[var4] != null) {
         return var4;
      }

      Object var5 = i[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 36;
         case 1 -> 27;
         case 2 -> 49;
         case 3 -> 29;
         case 4 -> 37;
         case 5 -> 60;
         case 6 -> 26;
         case 7 -> 8;
         case 8 -> 48;
         case 9 -> 53;
         case 10 -> 0;
         case 11 -> 39;
         case 12 -> 11;
         case 13 -> 12;
         case 14 -> 15;
         case 15 -> 52;
         case 16 -> 14;
         case 17 -> 55;
         case 18 -> 63;
         case 19 -> 23;
         case 20 -> 44;
         case 21 -> 58;
         case 22 -> 9;
         case 23 -> 45;
         case 24 -> 13;
         case 25 -> 41;
         case 26 -> 18;
         case 27 -> 46;
         case 28 -> 61;
         case 29 -> 57;
         case 30 -> 42;
         case 31 -> 31;
         case 32 -> 43;
         case 33 -> 2;
         case 34 -> 6;
         case 35 -> 4;
         case 36 -> 62;
         case 37 -> 19;
         case 38 -> 7;
         case 39 -> 22;
         case 40 -> 25;
         case 41 -> 47;
         case 42 -> 32;
         case 43 -> 5;
         case 44 -> 34;
         case 45 -> 3;
         case 46 -> 56;
         case 47 -> 50;
         case 48 -> 24;
         case 49 -> 59;
         case 50 -> 38;
         case 51 -> 21;
         case 52 -> 1;
         case 53 -> 20;
         case 54 -> 51;
         case 55 -> 28;
         case 56 -> 30;
         case 57 -> 54;
         case 58 -> 16;
         case 59 -> 17;
         case 60 -> 35;
         case 61 -> 40;
         case 62 -> 10;
         default -> 33;
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

      j[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'y' && var8 != 170 && var8 != 181 && var8 != 222) {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 196) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'o') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = g(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'y') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 170) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 181) {
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

   public static Object b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = b(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$5(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_243 b$return$c645a8f7(Object[] var1) {
try {

      return this.c;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field c(Class var0, String var1, Class var2) {
try {

      for (Field var6 : var0.getDeclaredFields()) {
         if (var6.getName().equals(var1) && var6.getType() == var2) {
            return var6;
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method c(Class var0, String var1, Class var2, int var3, Class[] var4) {
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

   public class_243 c$return$c645a8f7(Object[] var1) {
try {

      return this.d;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method h(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = i[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = j[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
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
         var15 = f(Long.parseLong(var6.substring(var12, var17), 36), 0L);
         if (var16 < var13) {
            var14[var16] = var15;
         }

         var12 = var17 + 1;
      }

      Class var23 = var8;

      while (true) {
         Method var26 = c(var23, var10, var15, var13, var14);
         if (var26 != null) {
            i[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = f(1371753693255388L, 0L);
            break;
         }
      }

      var23 = var8;

      while (true) {
         Class[] var27;
         if ((var27 = var23.getInterfaces()) != null) {
            for (int var18 = 0; var18 < var27.length; var18++) {
               Method var19 = d(var27[var18], var10, var15, var13, var14);
               if (var19 != null) {
                  i[var4] = var19;
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
            var23 = f(1371753693255388L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class f(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = e(var0, var2);
      Object var6 = i[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(j[var4]);
            i[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(Class var0, String var1, Class var2, int var3, Class[] var4) {
try {

      Method var5 = c(var0, var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      }

      Class[] var6 = var0.getInterfaces();
      if (var6 != null) {
         for (int var7 = 0; var7 < var6.length; var7++) {
            var5 = d(var6[var7], var1, var2, var3, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field d(Class var0, String var1, Class var2) {
try {

      Field var3 = c(var0, var1, var2);
      if (var3 != null) {
         return var3;
      }

      Class[] var4 = var0.getInterfaces();
      if (var4 != null) {
         for (int var5 = 0; var5 < var4.length; var5++) {
            var3 = d(var4[var5], var1, var2);
            if (var3 != null) {
               return var3;
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
               MethodHandles.insertArguments(__indy$6().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/b0" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static boolean a$return$e4b089a0(Object[] param0) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(5,5);
f.locals[0]=param0;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$14$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=21&&f.pc<31&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=33;continue;}
if(f.pc>=29&&f.pc<47&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=49;continue;}
if(f.pc>=39&&f.pc<58&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=60;continue;}
if(f.pc>=55&&f.pc<77&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=79;continue;}
if(f.pc>=66&&f.pc<88&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=90;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
}



   public S0593 a$return$4f71b56(Object[] var1) {
try {

      return this.g;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0364 a$return$99ec1e1e(Object[] var1) {
try {

      return this.a;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_243 a$return$c645a8f7(Object[] var1) {
try {

      return this.e;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public float a$return$e4b0898c(Object[] var1) {
try {

      float var2 = (Float)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = h ^ var3;
      long var5 = var3 ^ 44892599887962L;
      return __indy$18(1.0F, __indy$17(this.a, new Object[]{var5}, -5283179971968841517L, var3) / var2, -5287174275619890354L, var3);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      i[0] = "i\u0014{F9\u0002\u007f\u0014~\u001c*\u0015h_}\u001a&\u0001y\u0018j\rm\u0001";
      i[1] = int.class;
      j[1] = "java/lang/Integer";
      i[2] = "n\u0015OJS\u0018x\u0015J\u0010@\u000fo^I\u0016L\u001b~\u0019^\u0001\u0007\n:";
      i[3] = "S\u0018?T\u007f:X\u0017.\u001b\u001c7M\u001a!p)5\\\t=\\>8";
      i[4] = "cI\u0010Y3;cI\u0007\u0005?4y\u0002\u0007\u001b?!~sVAfb";
      i[5] = "Xc%\u0004e\u001aXc2Xi\u0015B(2Fi\u0000EYb\u001c9C";
      i[6] = "_ju\u000b\u0012mIjpQ\u0001z^!sW\rnOfd@F|s";
      i[7] = "\u000fPv'\\czp}(M,\u0007hn/Deo";
      i[8] = boolean.class;
      j[8] = "java/lang/Boolean";
      i[9] = "\f&33\u001d\u0004\u001a&6i\u000e\u0013\rm5o\u0002\u0007\u001c*\"xI\u0012]";
      i[10] = "\u0005@}e=\u000ep`vj,A\u0011n}a(\u001be";
      i[11] = float.class;
      j[11] = "java/lang/Float";
      i[12] = ";\u0001k\u001d}\u00150\u000ezR\u001e\u0018%\b";
      i[13] = "\u0012R\u0001umX\u0004R\u0004/~O\u0013\u0019\u0007)r[\u0002^\u0010>9JG";
      i[14] = "``Y=~'\u0015@R2ohtNY9k2\u0000";
      i[15] = void.class;
      j[15] = "java/lang/Void";
      i[16] = double.class;
      j[16] = "java/lang/Double";
      i[17] = "%[!Jx$P{*Eik1u!Nm1E";
      i[18] = "EI{V+\u00140ipY:[Qg{R>\u0001%";
      i[19] = "=i61?,6f'~^\"=m#$";
      i[20] = "T\u001cNs\u0015BV\u000fY1o\u0017\u0002\tU.\u0003%UK\u000fsPrT\u0005\u000b0\bOP\u0011Y9o";
      i[21] = "\b5(\u001bjZ[\"b\u001d\u0005\u0019Q\">\u0006`cR)\"\u0002\u007f^\u0006|7\f\u0005";
      i[22] = "zveyJe4)>(5\u007fF+6iVp:ly(^\u0015";
      i[23] = "$);o:Poky\u000b%Uy4#b&/d5?4%P{0u\u000b";
      i[24] = ";6Z;wK?\"\b2\u0010\u0013m:\u0004%|!9{[|!v=>^-oOk(T<\u0010";
      i[25] = "\u0010\u0011' ;\"QLm3G/.\u0017+=&4I\u0012')<F\u001eA)v6*AF9-G";
      i[26] = "\u001f@{-^\u001eC\u0000&s%\u0006%@\"-X\u0014E\u0015}+\u0015o\u001f\u001b|o^\u000fJDz\"%";
      i[27] = "\u0002\u000e,`!\u0017\u0000\u001d;\"[BT\u001b7=7p\u0005Zkeg'\u0004\u001fm5$\u001eR\tg$[";
      i[28] = "PV\u001b\u0001$7RE\fC^b\u0006C\u0000\\2PQ\u0001Z\u0002c\u0007VGZT!>\u0000QPE^";
      i[29] = "wbRzT:;b]bn8F1Fn\u000f#!4Jz\u0015Q,`Zo^ 9`H.n";
      i[30] = "89e#=Uyd/0AX\u0006?i> Ca:e*:1lnu?q@yng~A";
      i[31] = "G_&jQ0\u001b\u001f{4*(}\u000e`$K3\u001a\u000bl0QAL]t0\u0010*\u0000]{(*";
      i[32] = "\u001b<C<S\u000eH+\t:<RJ+9v\u0001K\u0011<U)\u0006[JM\t{@\rZ!V|PV+}\u0004:\u0006FG\"\u0003*]7";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0094 a$return$99ec0e9e(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = h ^ var2;

      try {
         switch (__indy$19(this.g, 8425105470414560213L, var2)) {
            case 0:
               return new S0095();
            case 1:
               return new S0096();
            default:
               throw new MatchException(null, null);
         }
      } catch (MatchException var4) {
         throw __indy$20(var4, 8424127343937311337L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
try {

      boolean var2 = (Boolean)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = h ^ var3;
      long var5 = var3 ^ 74231399566631L;
      this.e = this.c;
      S0094 var10000 = this.f;
      Object[] var10005 = new Object[]{null, null, var5};
      var10005[1] = var2;
      var10005[0] = this;
      __indy$21(var10000, var10005, -5734669642730719249L, var3);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field g(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = i[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = j[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = f(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = c(var12, var10, var11);
         if (var13 != null) {
            i[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = d(var14[var15], var10, var11);
               if (var13 != null) {
                  i[var4] = var13;
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
            var12 = f(1371753693255388L, 0L);
         }
      }
   
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

   private static native double __indy$0(long var0, long var2) throws Throwable;

   private static native double __indy$1(long var0, long var2) throws Throwable;

   private static native double __indy$2(long var0, long var2) throws Throwable;

   private static native S0094 __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$4(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$5(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$6() throws Throwable;

   private static native String[] __indy$7(long var0, long var2) throws Throwable;

   private static native boolean __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native class_3610 __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native float __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native float __indy$18(float var0, float var1, long var2, long var4) throws Throwable;

   private static native int __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$21(Object var0, Object var1, long var2, long var4) throws Throwable;



private static void __flow$14$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[0],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((net.minecraft.class_2680)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[1]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0093.h,2);f.pc=14;return;}
case 14:{f.push(f.locals[2],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.push(3923130273820082232L,2);f.pc=18;return;}
case 18:{f.push(f.locals[2],2);f.pc=19;return;}
case 19:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$7(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=20;return;}
case 20:{f.locals[4]=f.pop();f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.push(f.locals[1],1);f.pc=24;return;}
case 24:{f.push(3923560565961951426L,2);f.pc=25;return;}
case 25:{f.push(f.locals[2],2);f.pc=26;return;}
case 26:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=27;return;}
case 27:{f.push(f.locals[4],1);f.pc=28;return;}
case 28:{if(f.pop()!=null){f.pc=99;return;}f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=96;return;}f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=39;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.push(3923426611092558067L,2);f.pc=36;return;}
case 36:{f.push(f.locals[2],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{throw (Throwable)f.pop();}
case 39:{f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.push(f.locals[1],1);f.pc=42;return;}
case 42:{f.push(3923369755651938509L,2);f.pc=43;return;}
case 43:{f.push(f.locals[2],2);f.pc=44;return;}
case 44:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=45;return;}
case 45:{f.push(f.locals[4],1);f.pc=46;return;}
case 46:{if(f.pop()!=null){f.pc=99;return;}f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=55;return;}
case 49:{f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.push(3923426611092558067L,2);f.pc=52;return;}
case 52:{f.push(f.locals[2],2);f.pc=53;return;}
case 53:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=54;return;}
case 54:{throw (Throwable)f.pop();}
case 55:{f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=96;return;}f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.pc=66;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.push(3923426611092558067L,2);f.pc=63;return;}
case 63:{f.push(f.locals[2],2);f.pc=64;return;}
case 64:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=65;return;}
case 65:{throw (Throwable)f.pop();}
case 66:{f.pc=67;return;}
case 67:{f.pc=68;return;}
case 68:{f.push(f.locals[1],1);f.pc=69;return;}
case 69:{f.push(3923003482090653070L,2);f.pc=70;return;}
case 70:{f.push(f.locals[2],2);f.pc=71;return;}
case 71:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=72;return;}
case 72:{f.push(3923283553248393738L,2);f.pc=73;return;}
case 73:{f.push(f.locals[2],2);f.pc=74;return;}
case 74:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=75;return;}
case 75:{f.push(f.locals[4],1);f.pc=76;return;}
case 76:{if(f.pop()!=null){f.pc=99;return;}f.pc=77;return;}
case 77:{f.pc=78;return;}
case 78:{f.pc=85;return;}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.push(3923426611092558067L,2);f.pc=82;return;}
case 82:{f.push(f.locals[2],2);f.pc=83;return;}
case 83:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=84;return;}
case 84:{throw (Throwable)f.pop();}
case 85:{f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=102;return;}f.pc=88;return;}
case 88:{f.pc=89;return;}
case 89:{f.pc=96;return;}
case 90:{f.pc=91;return;}
case 91:{f.pc=92;return;}
case 92:{f.push(3923426611092558067L,2);f.pc=93;return;}
case 93:{f.push(f.locals[2],2);f.pc=94;return;}
case 94:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0093.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=95;return;}
case 95:{throw (Throwable)f.pop();}
case 96:{f.pc=97;return;}
case 97:{f.pc=98;return;}
case 98:{f.push(1,1);f.pc=99;return;}
case 99:{f.pc=100;return;}
case 100:{f.pc=101;return;}
case 101:{f.pc=105;return;}
case 102:{f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{f.push(0,1);f.pc=105;return;}
case 105:{f.pc=106;return;}
case 106:{f.pc=107;return;}
case 107:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(1177911024528357670L,2);f.pc=1;return;}
case 1:{f.push(4687494942449982638L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(142496626447990L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0093.class,"h",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(33,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0093.class,"i",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(33,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0093.class,"j",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0093.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
