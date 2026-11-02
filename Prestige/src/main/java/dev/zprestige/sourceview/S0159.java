package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0027;
import dev.zprestige.sourceview.S0123;
import dev.zprestige.sourceview.S0270;
import dev.zprestige.sourceview.S0280;
import dev.zprestige.sourceview.S0445;
import dev.zprestige.sourceview.S0549;
import java.util.ArrayList;
import net.minecraft.class_11391;
import net.minecraft.class_1921;


import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.class_1044;
import net.minecraft.class_1060;
import net.minecraft.class_10868;
import net.minecraft.class_12247;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4588;

public class S0159 {
   public static long a;
   public static Object[] b;
   public static String[] c;

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

   public static Class b(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = a(var0, var2);
      Object var6 = b[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(c[var4]);
            b[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
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

   public static Optional b$return$820383a9(Object[] var0) {
try {

      class_2960 var3 = (class_2960)(Object)(var0[0]);
      long var1 = (Long)(Object)(var0[1]);
      var1 = a ^ var1;
      class_1060 var4 = __indy$1(__indy$0(8729136348361332820L, var1), 8730113097959736254L, var1);
      Optional var5 = __indy$3(__indy$2(var4, var3, 8729665652270697564L, var1), 8728282044117196372L, var1);
      return __indy$11(
         __indy$9(
            __indy$7(__indy$5(var5, __indy$4(), 8728096146241207014L, var1), __indy$6(), 8728096146241207014L, var1), __indy$8(), 8728823377415279628L, var1
         ),
         __indy$10(),
         8728096146241207014L,
         var1
      );
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field c(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = b[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = c[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            b[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  b[var4] = var13;
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
            var12 = b(83448404124424L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = b[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = c[var4];
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
            b[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(83448404124424L, 0L);
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
                  b[var4] = var19;
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
            var23 = b(83448404124424L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$12().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/c1" + " : " + var1 + " : " + var2.toString(), var5);
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

   public static Optional a$return$820383a9(Object[] param0) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(14,7);
f.locals[0]=param0;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$10$0(f);break;
case 1:__flow$10$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=47&&f.pc<54&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=56;continue;}
if(f.pc>=52&&f.pc<68&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=68;continue;}
if(f.pc>=124&&f.pc<132&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=134;continue;}
if(f.pc>=128&&f.pc<143&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=145;continue;}
if(f.pc>=180&&f.pc<189&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=191;continue;}
if(f.pc>=197&&f.pc<206&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=206;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return ((Optional)(Object)f.result);
}


   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 206 && var8 != 220 && var8 != 'c' && var8 != 162) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 236) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 249) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 206) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 220) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'c') {
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

   public static Object a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = a(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$37(var8.asSpreader(Object[].class, var4.length), var4);
   
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
      if (c[var4] != null) {
         return var4;
      }

      Object var5 = b[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 35;
         case 1 -> 47;
         case 2 -> 37;
         case 3 -> 30;
         case 4 -> 55;
         case 5 -> 53;
         case 6 -> 22;
         case 7 -> 20;
         case 8 -> 14;
         case 9 -> 58;
         case 10 -> 46;
         case 11 -> 31;
         case 12 -> 0;
         case 13 -> 36;
         case 14 -> 25;
         case 15 -> 60;
         case 16 -> 61;
         case 17 -> 32;
         case 18 -> 10;
         case 19 -> 33;
         case 20 -> 18;
         case 21 -> 63;
         case 22 -> 50;
         case 23 -> 59;
         case 24 -> 19;
         case 25 -> 26;
         case 26 -> 29;
         case 27 -> 12;
         case 28 -> 23;
         case 29 -> 52;
         case 30 -> 1;
         case 31 -> 21;
         case 32 -> 7;
         case 33 -> 57;
         case 34 -> 43;
         case 35 -> 54;
         case 36 -> 13;
         case 37 -> 48;
         case 38 -> 8;
         case 39 -> 27;
         case 40 -> 51;
         case 41 -> 45;
         case 42 -> 2;
         case 43 -> 9;
         case 44 -> 41;
         case 45 -> 28;
         case 46 -> 6;
         case 47 -> 44;
         case 48 -> 62;
         case 49 -> 4;
         case 50 -> 34;
         case 51 -> 56;
         case 52 -> 42;
         case 53 -> 11;
         case 54 -> 16;
         case 55 -> 40;
         case 56 -> 24;
         case 57 -> 3;
         case 58 -> 39;
         case 59 -> 38;
         case 60 -> 49;
         case 61 -> 5;
         case 62 -> 15;
         default -> 17;
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

      c[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      b[0] = "*1\u000e\u0016q\u001b49\u0014Y\u0010\u001e49\u0017\u0019>\u0002";
      b[1] = "\u0018*O\u0007\u0012\u0012\u0013%^Hs\u001c\u0018.Z\u0012";
      b[2] = "l{R%>,rsHjv,hyP-\u007f7(\\Q*s-ouJ";
      b[3] = ",\u0014.\bt\u0007,\u00149Tx\b6_9Jx\u001d1.i\u0017)";
      b[4] = "6\u0006Q\f\u000bc6\u0006FP\u0007l,MFN\u0007y+<\u0014\u0012P:";
      b[5] = "k6h\u0000Lgu>rO\u0004go4j\b\r|/\u0007l\u0004\u0006{b6j\u0004";
      b[6] = "\bod|:H\bos 6G\u0012$s>6R\u0015U\"ka\u0011";
      b[7] = "]u\\\u001bl\u001f]uKG`\u0010G>KY`\u0005@O\u0019\u00055B";
      b[8] = "DE5\u001d6\nRE0G%\u001dE\u000e3A)\tTI$Vb\u001bg";
      b[9] = "cD]\u00179\t\u0016dV\u0018(Fwj]\u0013,\u001c\u0003";
      b[10] = "F\r\u0010v \u007fP\r\u0015,3hGF\u0016*?|V\u0001\u0001=tkG";
      b[11] = "Y\u0004d$&FY\u0004sx*ICOsf*\\D>$=y\u001f";
      b[12] = "3*l\u000b\u0014v3*{W\u0018y)a{I\u0018l.\u0010,\u0010A'";
      b[13] = "#!t+TKV\u0001\u007f$E\u00047\u000ft/A^C";
      b[14] = "\"\u0006sp\u0011f4\u0006v*\u0002q#Mu,\u000ee2\nb;Et\u0012";
      b[15] = "M \u0014t!u[ \u0011.2bLk\u0012(>v],\u0005?u_";
      b[16] = "uy?\u0015\u0019@\u0000Y4\u001a\b\u000faW?\u0011\fU\u0015";
      b[17] = "Z\u0004qjG\u0002Z\u0004f6K\r@Of(K\u0018G>4v\u0018_\u0003";
      b[18] = "\u001c\u0013\u0014\u0013 r\u0002\u001b\u000e\\Gs\u0013\u0000\u0003\u0006au";
      b[19] = "X_$bRDN_!8ASY\u0014\">MGHS5)\u0006Ut";
      b[20] = "<@\r\u0017\u0000\u0001I`\u0006\u0018\u0011N4x\u0015\u001f\u0018\u0007\\";
      b[21] = "g?\u0017f\u007f'g?\u0000:s(}t\u0000$s=z\u0005Rx*x1";
      b[22] = int.class;
      c[22] = "java/lang/Integer";
      b[23] = "mE%6\t\u0005sM?yj\u0011w";
      b[24] = "\u0015{>KjY\u000bs$\u0004\u0017I\u000b";
      b[25] = "\u001b#+S\u0013#\u0010,:\u001ct!\u0005':WO";
      b[26] = "\\Jj\u0004\u0010$BBpKs0F\u000fY\u000bJ#O";
      b[27] = "X4\u001f\u0011w\u0001F<\u0005^\u0015\u001dA!";
      b[28] = boolean.class;
      c[28] = "java/lang/Boolean";
      b[29] = "b$x7{bt$}mhuco~kdar(i|/q7";
      b[30] = "\u00069\nnE0\r6\u001b!&=\u0018;\u0014J\u0013?\t(\bf\u00042";
      b[31] = "_\u0019|I\u0019\n_\u0019k\u0015\u0015\u0005ERk\u000b\u0015\u0010B#9UFW\u0006Xk\u000b\u0015\u0010B#9UGP\u0006";
      b[32] = "1$|\u000fc78*\u007fF :>*kD=<|?tYz- .b\u000fI('\u001ftYz- .GHk/";
      b[33] = "`\u000bm\u001a@/`\u000bzFL z@zXL5}1(\u0005\u001e\u007f?";
      b[34] = "lhSp&\u007f9m\u00117_h\u0005c\u0007r/>`c\rr6\u0001ne\u000f2cy|v\u0006+_";
      b[35] = "w7,?{3d=%%\u001a\"\u001bjr:+1`e-2~H";
      b[36] = "Z\u001b\u0016AjtH\f@F\u0007tZ\u001e\u001b\u001dkF\tRG@8\u0011Y\u0004\u001f\bzmI^\u0017\u0003\u0007";
      b[37] = "%\u0015W(X\u0001}\u0016Qi%\tr\u0003T7I;&B\ti%Vu\u001cL-H\u000ev\u001a\rP";
      b[38] = "s\u000b:e`\bl\u0002fbZ\u000fz\b{b\u001d\u001f\u0013\u0002`k(\fo\u0012:c#qs\u000b:e`\bl\u0002fbZ";
      b[39] = "\u000bRI\u0013gl\u0016\t\u0003\u0007Xs\u0018Y\t\u0014$u\u001e4\u0018\u00037+\t_\u0012\u001b(md";
      b[40] = ":uT;A\u00050mK},\u000e8p}o\\\u0012Qi[{]\u0014<5\u00069\u0010n";
      b[41] = "e\bu43-'\r%#L9X\u0007$t<o=\u0007.t%PgVq5)4e\n/?L";
      b[42] = "\u001dp\fgd\u0005\u0010y[9\u000f\u001b@=V`c)\u0017}\u000e<0~\u001d+]>l\u001bV\"Mf\u000fNG*\u000fdj\u0005N:W\u0007?\u0014FxUbt\u001dV 6";
      b[43] = "\u0000qZ\u001c\u0005kXr\\]xcWgY\u0003\u0014Q\u0003&\u0002]x<HbPT\u001dwBkSd";
      b[44] = "EN#W\u0006%W]*N:8C^ KV\n\u0011\u001fq\u0016\u0004]AN=^@;@[\"F:";
      b[45] = "\ff\u000b7o3Gl\u00024_3[h\u001293\u0001\n*Ko_jYpCc$+QiN^c)K/\t`'m_er";
      b[46] = "\u0011+V@]`O/\n\f,2ruE\tC*\u000frIM\u0013[\u0011hC\u001e]&\u0016d\u0007N,";
      b[47] = "\b[\r\u0003\u001b\u001bQCRCdA\u0012h\u0007@\u0018QiG\u0002A\u0015Z\u0004\u001b_\u0003X ";
      b[48] = "\u0018Z\u001ct_d\u0014\\If4y\u0013\u001bMbXKA^\u0013>\u0004\u001c\u0013\u001e@>Yy\u0001\t\u001694";
      b[49] = "ln-\\jt\"o;?u#\u0016x9[{'4h]Cq37<mG{\"P96Oq5iw7Y\u0012";
      b[50] = "<jJTC\"rk\\7V~x}K7\u0007ppbB\u000eIqf\u0001";
      b[51] = "\u0000&3Tcr\n>,\u0012\u000e\u007f\u0006( mrz\u0015?e]vp\u0004X";
      b[52] = "p\u001cZR\u0019\u001c>\u001dL1\u0004L4w\u0013Q\u0005Z0GZI\u0011JLKAA\u0002]u\u0005@Wa";
      b[53] = "c@'\u001dT\"bU8\u0005.)jP \u001eu)p,5\t\u0014*3R(R^>\f";
      b[54] = "K\u001aa+\u0013\b\u0005\u001bwH\f_w\rr6\f\bG\tx'k\r\u001c\u0001r0RC\u001d\u0017\u0011";
      b[55] = "0c\u0013A\u001dK25\u0005\u0017gC<$\"D\u0003Q<X\u0000N\u0019Khh\u0004D\b,";
      b[56] = "\u0014'TU&\u007fM?\u000b\u0015Y-\u00195;\u0012::\u0012`\u000b\u00160+u;[\u0017(>\u0018g\u0006UeD";
      b[57] = "0>\u0013s7?~?\u0005\u0010!gh)\u000ejO=m2\u001ems=t/\u0006\u0010sm|6\u001b)=ljU";
      b[58] = "}EH3)M\u007f\u0019\u00169L@B\u0014\u001dr<\u0016'\u0014\u0017r%)+C\u00110+\u0014 \u0004\u0018\"L";
      b[59] = "4\n\u0013Yd\u0004`\u000b\u0015\u0005\u001d\u00027\u0017\u00107 SjLh\u0005vY/\u000b\u0003\u0004l\r!r";
      b[60] = "\u0019)jjl^@15*\u0013\n\u0015+\u0005?u\u0001\n*y//\t\u0001Wy2m\u0002Ag}8|e";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static class_4588 a$return$28c2bed(Object[] var0) {
try {

      class_4588 var3 = (class_4588)(Object)(var0[0]);
      int var4 = (Integer)(Object)(var0[1]);
      long var1 = (Long)(Object)(var0[2]);
      var1 = a ^ var1;
      long var5 = var1 ^ 75356188662889L;
      S0270 var10003 = __indy$38(new Object[0], 5406109520721358854L, var1);
      S0082 var10004 = S0280.b;
      Object[] var10008 = new Object[]{null, null, var5};
      var10008[1] = var4;
      var10008[0] = var10004;
      return __indy$40(var3, new S0445(__indy$39(var10003, var10008, 5405058479259349208L, var1)), 5406190308749037871L, var1);
   
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

   public static Integer lambda$resolveTextureId$1(GpuTexture var0) {
try {

      long var1 = a ^ 44432755919122L;
      return __indy$42(__indy$41((class_10868)(Object)(var0), 8520578810835527921L, var1), 8520717068748610484L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static boolean lambda$resolveTextureId$0(GpuTexture var0) {
try {

      return var0 instanceof class_10868;
   
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

   private static native class_310 __indy$0(long var0, long var2) throws Throwable;

   private static native class_1060 __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native class_1044 __indy$2(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Optional __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native Function __indy$4() throws Throwable;

   private static native Optional __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Function __indy$6() throws Throwable;

   private static native Optional __indy$7(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Predicate __indy$8() throws Throwable;

   private static native Optional __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Function __indy$10() throws Throwable;

   private static native Optional __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$12() throws Throwable;

   private static native String[] __indy$13(long var0, long var2) throws Throwable;

   private static native class_12247 __indy$14(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native Optional __indy$16(long var0, long var2) throws Throwable;

   private static native MatchException __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native Map __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native Set __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native Iterator __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native GpuTextureView __indy$24(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native class_10868 __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native Integer __indy$29(int var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$30(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$32(Object var0, long var1, long var3) throws Throwable;

   private static native Optional __indy$33(long var0, long var2) throws Throwable;

   private static native MatchException __indy$34(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$35(Object var0, int var1, long var2, long var4) throws Throwable;

   private static native Optional __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$37(MethodHandle var0, Object[] var1) throws Throwable;

   private static native S0270 __indy$38(Object var0, long var1, long var3) throws Throwable;

   private static native S0123 __indy$39(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_4588 __indy$40(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native int __indy$41(Object var0, long var1, long var3) throws Throwable;

   private static native Integer __indy$42(int var0, long var1, long var3) throws Throwable;

private static void __flow$10$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[0],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((net.minecraft.class_1921)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[1]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0159.a,2);f.pc=14;return;}
case 14:{f.push(f.locals[2],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.push(f.locals[2],2);f.pc=18;return;}
case 18:{f.rearrange(92);f.pc=19;return;}
case 19:{f.push(21565215403969L,2);f.pc=20;return;}
case 20:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=21;return;}
case 21:{f.locals[4]=f.pop();f.pc=22;return;}
case 22:{f.rearrange(88);f.pc=23;return;}
case 23:{f.push(854275716013087235L,2);f.pc=24;return;}
case 24:{f.push(f.locals[2],2);f.pc=25;return;}
case 25:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$13(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0027.class),1);f.pc=29;return;}
case 29:{f.rearrange(89);f.pc=30;return;}
case 30:{f.push(f.locals[1],1);f.pc=31;return;}
case 31:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{net.minecraft.class_1921.class},new Object[]{((net.minecraft.class_1921)(Object)(a0))});f.pc=32;return;}
case 32:{f.push(f.locals[4],2);f.pc=33;return;}
case 33:{f.push(1,1);f.pc=34;return;}
case 34:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=35;return;}
case 35:{f.rearrange(91);f.pc=36;return;}
case 36:{f.rearrange(91);f.pc=37;return;}
case 37:{f.rearrange(87);f.pc=38;return;}
case 38:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=39;return;}
case 39:{f.push(0,1);f.pc=40;return;}
case 40:{f.rearrange(95);f.pc=41;return;}
case 41:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=42;return;}
case 42:{f.push(854179574892628452L,2);f.pc=43;return;}
case 43:{f.push(f.locals[2],2);f.pc=44;return;}
case 44:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$14(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=45;return;}
case 45:{f.locals[7]=f.pop();f.pc=46;return;}
case 46:{f.locals[6]=f.pop();f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(f.locals[7],1);f.pc=50;return;}
case 50:{f.push(f.locals[6],1);f.pc=51;return;}
case 51:{if(f.pop()!=null){f.pc=78;return;}f.pc=52;return;}
case 52:{f.pc=53;return;}
case 53:{if(f.pop()!=null){f.pc=74;return;}f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=62;return;}
case 56:{f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.push(853866652539563052L,2);f.pc=59;return;}
case 59:{f.push(f.locals[2],2);f.pc=60;return;}
case 60:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=61;return;}
case 61:{throw (Throwable)f.pop();}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.push(855264361677362888L,2);f.pc=65;return;}
case 65:{f.push(f.locals[2],2);f.pc=66;return;}
case 66:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$16(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=67;return;}
case 67:{f.result=f.pop();f.done=true;return;}
case 68:{f.pc=69;return;}
case 69:{f.pc=70;return;}
case 70:{f.push(853866652539563052L,2);f.pc=71;return;}
case 71:{f.push(f.locals[2],2);f.pc=72;return;}
case 72:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=73;return;}
case 73:{throw (Throwable)f.pop();}
case 74:{f.pc=75;return;}
case 75:{f.pc=76;return;}
case 76:{f.pc=77;return;}
case 77:{f.push(f.locals[7],1);f.pc=78;return;}
case 78:{f.pc=79;return;}
case 79:{f.pc=80;return;}
case 80:{f.push(853718772468546390L,2);f.pc=81;return;}
case 81:{f.push(f.locals[2],2);f.pc=82;return;}
case 82:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=83;return;}
case 83:{f.locals[8]=f.pop();f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.pc=86;return;}
case 86:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.util.ArrayList.class),1);f.pc=87;return;}
case 87:{f.rearrange(89);f.pc=88;return;}
case 88:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=89;return;}
case 89:{f.locals[9]=f.pop();f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.pc=92;return;}
case 92:{f.push(f.locals[8],1);f.pc=93;return;}
case 93:{f.push(855489532731533375L,2);f.pc=94;return;}
case 94:{f.push(f.locals[2],2);f.pc=95;return;}
case 95:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$19(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=96;return;}
case 96:{f.push(854499116137378542L,2);f.pc=97;return;}
case 97:{f.push(f.locals[2],2);f.pc=98;return;}
case 98:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=99;return;}
case 99:{f.locals[10]=f.pop();f.pc=100;return;}
case 100:{f.pc=101;return;}
case 101:{f.pc=102;return;}
case 102:{f.push(f.locals[10],1);f.pc=103;return;}
case 103:{f.push(853434314437584987L,2);f.pc=104;return;}
case 104:{f.push(f.locals[2],2);f.pc=105;return;}
case 105:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=106;return;}
case 106:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=180;return;}f.pc=107;return;}
case 107:{f.push(f.locals[10],1);f.pc=108;return;}
case 108:{f.push(855369195481351419L,2);f.pc=109;return;}
case 109:{f.push(f.locals[2],2);f.pc=110;return;}
case 110:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=111;return;}
case 111:{f.push((java.util.Map.Entry)(Object)f.pop(),1);f.pc=112;return;}
case 112:{f.locals[11]=f.pop();f.pc=113;return;}
case 113:{f.pc=114;return;}
case 114:{f.pc=115;return;}
case 115:{f.push(f.locals[11],1);f.pc=116;return;}
case 116:{f.push(855680943213022183L,2);f.pc=117;return;}
case 117:{f.push(f.locals[2],2);f.pc=118;return;}
case 118:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=119;return;}
case 119:{f.push((net.minecraft.class_12247.class_12337)(Object)f.pop(),1);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$10$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.push(854779920552267857L,2);f.pc=121;return;}
case 121:{f.push(f.locals[2],2);f.pc=122;return;}
case 122:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$24(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=123;return;}
case 123:{f.locals[13]=f.pop();f.pc=124;return;}
case 124:{f.pc=125;return;}
case 125:{f.push(f.locals[13],1);f.pc=126;return;}
case 126:{f.push(f.locals[6],1);f.pc=127;return;}
case 127:{if(f.pop()!=null){f.pc=154;return;}f.pc=128;return;}
case 128:{f.pc=129;return;}
case 129:{f.push((f.pop() instanceof net.minecraft.class_11391?1:0),1);f.pc=130;return;}
case 130:{f.push(f.locals[6],1);f.pc=131;return;}
case 131:{if(f.pop()!=null){f.pc=197;return;}f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.pc=140;return;}
case 134:{f.pc=135;return;}
case 135:{f.pc=136;return;}
case 136:{f.push(853866652539563052L,2);f.pc=137;return;}
case 137:{f.push(f.locals[2],2);f.pc=138;return;}
case 138:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=139;return;}
case 139:{throw (Throwable)f.pop();}
case 140:{f.pc=141;return;}
case 141:{f.pc=142;return;}
case 142:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=175;return;}f.pc=143;return;}
case 143:{f.pc=144;return;}
case 144:{f.pc=151;return;}
case 145:{f.pc=146;return;}
case 146:{f.pc=147;return;}
case 147:{f.push(853866652539563052L,2);f.pc=148;return;}
case 148:{f.push(f.locals[2],2);f.pc=149;return;}
case 149:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=150;return;}
case 150:{throw (Throwable)f.pop();}
case 151:{f.pc=152;return;}
case 152:{f.pc=153;return;}
case 153:{f.push(f.locals[13],1);f.pc=154;return;}
case 154:{f.pc=155;return;}
case 155:{f.pc=156;return;}
case 156:{f.push((net.minecraft.class_11391)(Object)f.pop(),1);f.pc=157;return;}
case 157:{f.locals[12]=f.pop();f.pc=158;return;}
case 158:{f.pc=159;return;}
case 159:{f.pc=160;return;}
case 160:{f.push(f.locals[9],1);f.pc=161;return;}
case 161:{f.push(f.locals[12],1);f.pc=162;return;}
case 162:{f.push(855168529090739856L,2);f.pc=163;return;}
case 163:{f.push(f.locals[2],2);f.pc=164;return;}
case 164:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=165;return;}
case 165:{f.push(854322146767082773L,2);f.pc=166;return;}
case 166:{f.push(f.locals[2],2);f.pc=167;return;}
case 167:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=168;return;}
case 168:{f.push(854469209487386192L,2);f.pc=169;return;}
case 169:{f.push(f.locals[2],2);f.pc=170;return;}
case 170:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$29(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=171;return;}
case 171:{f.push(854618577847169314L,2);f.pc=172;return;}
case 172:{f.push(f.locals[2],2);f.pc=173;return;}
case 173:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$30(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=174;return;}
case 174:{f.rearrange(87);f.pc=175;return;}
case 175:{f.pc=176;return;}
case 176:{f.pc=177;return;}
case 177:{f.pc=178;return;}
case 178:{f.push(f.locals[6],1);f.pc=179;return;}
case 179:{if(f.pop()==null){f.pc=100;return;}f.pc=180;return;}
case 180:{f.pc=181;return;}
case 181:{f.pc=182;return;}
case 182:{f.pc=183;return;}
case 183:{f.push(f.locals[9],1);f.pc=184;return;}
case 184:{f.push(f.locals[6],1);f.pc=185;return;}
case 185:{if(f.pop()!=null){f.pc=220;return;}f.pc=186;return;}
case 186:{f.push(853927873319182004L,2);f.pc=187;return;}
case 187:{f.push(f.locals[2],2);f.pc=188;return;}
case 188:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$31(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=189;return;}
case 189:{f.pc=190;return;}
case 190:{f.pc=197;return;}
case 191:{f.pc=192;return;}
case 192:{f.pc=193;return;}
case 193:{f.push(853866652539563052L,2);f.pc=194;return;}
case 194:{f.push(f.locals[2],2);f.pc=195;return;}
case 195:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$32(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=196;return;}
case 196:{throw (Throwable)f.pop();}
case 197:{f.pc=198;return;}
case 198:{f.pc=199;return;}
case 199:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=212;return;}f.pc=200;return;}
case 200:{f.pc=201;return;}
case 201:{f.pc=202;return;}
case 202:{f.push(855264361677362888L,2);f.pc=203;return;}
case 203:{f.push(f.locals[2],2);f.pc=204;return;}
case 204:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$33(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=205;return;}
case 205:{f.result=f.pop();f.done=true;return;}
case 206:{f.pc=207;return;}
case 207:{f.pc=208;return;}
case 208:{f.push(853866652539563052L,2);f.pc=209;return;}
case 209:{f.push(f.locals[2],2);f.pc=210;return;}
case 210:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$34(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=211;return;}
case 211:{throw (Throwable)f.pop();}
case 212:{f.pc=213;return;}
case 213:{f.pc=214;return;}
case 214:{f.pc=215;return;}
case 215:{f.push(f.locals[9],1);f.pc=216;return;}
case 216:{f.push(0,1);f.pc=217;return;}
case 217:{f.push(854890499230340830L,2);f.pc=218;return;}
case 218:{f.push(f.locals[2],2);f.pc=219;return;}
case 219:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$35(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=220;return;}
case 220:{f.pc=221;return;}
case 221:{f.pc=222;return;}
case 222:{f.push((java.lang.Integer)(Object)f.pop(),1);f.pc=223;return;}
case 223:{f.push(855560458220373821L,2);f.pc=224;return;}
case 224:{f.push(f.locals[2],2);f.pc=225;return;}
case 225:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0159.__indy$36(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=226;return;}
case 226:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(4547278647526299323L,2);f.pc=1;return;}
case 1:{f.push(-4308860883589342473L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(115756030443385L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0159.class,"a",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(61,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0159.class,"b",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(61,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0159.class,"c",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0159.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
