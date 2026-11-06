package dev.zprestige.sourceview;

import com.mojang.authlib.GameProfile;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class S0166 implements S0225 {
   public static long a;
   public static Object[] c;
   public static String[] d;

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
      if (d[var4] != null) {
         return var4;
      }

      Object var5 = c[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 22;
         case 1 -> 20;
         case 2 -> 2;
         case 3 -> 8;
         case 4 -> 53;
         case 5 -> 27;
         case 6 -> 54;
         case 7 -> 37;
         case 8 -> 0;
         case 9 -> 61;
         case 10 -> 62;
         case 11 -> 59;
         case 12 -> 4;
         case 13 -> 55;
         case 14 -> 31;
         case 15 -> 21;
         case 16 -> 48;
         case 17 -> 10;
         case 18 -> 3;
         case 19 -> 15;
         case 20 -> 34;
         case 21 -> 9;
         case 22 -> 28;
         case 23 -> 32;
         case 24 -> 51;
         case 25 -> 42;
         case 26 -> 39;
         case 27 -> 29;
         case 28 -> 56;
         case 29 -> 46;
         case 30 -> 43;
         case 31 -> 44;
         case 32 -> 36;
         case 33 -> 60;
         case 34 -> 18;
         case 35 -> 45;
         case 36 -> 30;
         case 37 -> 35;
         case 38 -> 23;
         case 39 -> 47;
         case 40 -> 26;
         case 41 -> 7;
         case 42 -> 5;
         case 43 -> 38;
         case 44 -> 12;
         case 45 -> 50;
         case 46 -> 13;
         case 47 -> 6;
         case 48 -> 33;
         case 49 -> 11;
         case 50 -> 41;
         case 51 -> 49;
         case 52 -> 25;
         case 53 -> 17;
         case 54 -> 14;
         case 55 -> 40;
         case 56 -> 57;
         case 57 -> 63;
         case 58 -> 24;
         case 59 -> 1;
         case 60 -> 52;
         case 61 -> 58;
         case 62 -> 19;
         default -> 16;
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

   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'n' && var8 != 232 && var8 != 'w' && var8 != 221) {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 220) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 165) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = g(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'n') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 232) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'w') {
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

   public static boolean b$return$e4b089a0(Object[] var0) {
try {

      class_1657 var1 = (class_1657)(Object)(var0[0]);
      long var2 = (Long)(Object)(var0[1]);
      var2 = a ^ var2;
      return __indy$0(var1, 3917234018112314470L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static class_243 b$return$c645a8f7(Object[] var0) {
try {

      long var1 = (Long)(Object)(var0[0]);
      var1 = a ^ var1;
      long var3 = var1 ^ 49962382593028L;
      return __indy$2(new Object[]{__indy$1(b, -2202960135086569445L, var1), var3}, -2206649691896095936L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = b(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$3(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static class_243 c$return$c645a8f7(Object[] var0) {
try {

      class_1297 var1 = (class_1297)(Object)(var0[0]);
      long var2 = (Long)(Object)(var0[1]);
      var2 = a ^ var2;
      return new class_243(__indy$4(var1, 2298568192951353341L, var2), __indy$5(var1, 2299979971568926317L, var2), __indy$6(var1, 2298156675963116437L, var2));
   
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

   public static Method h(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = c[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = d[var4];
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
            c[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = f(1368241697185335L, 0L);
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
            var23 = f(1368241697185335L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class f(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = e(var0, var2);
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

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$7().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/c8" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static String a$return$35725684(Object[] var0) {
try {

      GameProfile var3 = (GameProfile)(Object)(var0[0]);
      long var1 = (Long)(Object)(var0[1]);
      var1 = a ^ var1;
      return __indy$8(var3, -4462319362863087618L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static boolean a$return$e4b089a0(Object[] var0) {
try {

      long var1 = (Long)(Object)(var0[0]);
      var1 = a ^ var1;
      long var3 = var1 ^ 121904208108296L;
      return __indy$10(new Object[]{__indy$9(b, 4774244059549477687L, var1), var3}, 4777413423428097777L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static class_243 a$return$c645a8f7(Object[] var0) {
try {

      class_1297 var3 = (class_1297)(Object)(var0[0]);
      long var1 = (Long)(Object)(var0[1]);
      var1 = a ^ var1;
      return __indy$11(var3, -4432076359030660682L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static double a$return$e4b0898a(Object[] var0) {
try {

      class_1657 var1 = (class_1657)(Object)(var0[0]);
      long var2 = (Long)(Object)(var0[1]);
      var2 = a ^ var2;
      long var4 = var2 ^ 39318658454649L;
      class_243 var6 = __indy$12(new Object[]{var1, var4}, 8727247624454030141L, var2);
      class_243 var7 = __indy$13(var6, 0.0, -256.0, 0.0, 8726965698426216305L, var2);
      class_3965 var8 = __indy$17(
         __indy$14(b, 8724344297718917818L, var2),
         new class_3959(var6, var7, __indy$15(8724049992550577919L, var2), __indy$16(8724562424141426470L, var2), var1),
         8723589445186600651L,
         var2
      );

      try {
         return __indy$18(var8, 8727079871513765103L, var2) == __indy$19(8724500653324064928L, var2)
            ? __indy$20(var1, 8727289195163508396L, var2) - __indy$22(__indy$21(var8, 8727047249141867551L, var2), 8726851527410133478L, var2)
            : 256.0;
      } catch (MatchException var9) {
         throw __indy$23(var9, 8724411778631284437L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      c[0] = "i<\u0012\u0015\n|`2\u0011\\Ir\u007f'\u0017W\u000eq$\u0014\u001eV\u0002Cx<\u0019R\u000bv";
      c[1] = "\"S0pc\u001b)\\!?\u001e\u0003:[(v";
      c[2] = "cR\u0014oT\u0018cR\u00033X\u0017y\u0019\u0003-X\u0002~hSp\t";
      c[3] = "\u007f>:R.\u000b\u007f>-\u000e\"\u0004eu-\u0010\"\u0011b\u0004yHu";
      c[4] = "r}\u0001-\u0013\u0006d}\u0004w\u0000\u0011s6\u0007q\f\u0005bq\u0010fG\u0015.";
      c[5] = "1\u000fl!\u0017VD/g.\u0006\u0019%!l%\u0002CQ";
      c[6] = "\u0012\u001b7\u000b\u007fY\u0012\u001b WsV\bP IsC\u000f!q\u0011!";
      c[7] = ">x?\u00157j>x(I;e$3(W;p#Bz\tc4";
      c[8] = double.class;
      d[8] = "java/lang/Double";
      c[9] = "fK-O\u0003\u0012fK:\u0013\u000f\u001d|\u0000:\r\u000f\b{qhW[L";
      c[10] = boolean.class;
      d[10] = "java/lang/Boolean";
      c[11] = "\u0004W.\u0015XSqw%\u001aI\u001c\u0010y.\u0011MFd";
      c[12] = "Ri+zCmRi<&ObH\"<8OwOSig\u0016";
      c[13] = "3P\u000b\u0005R\u00163P\u001cY^\u0019)\u001b\u001cG^\f.jL\u0012\nF";
      c[14] = "i\u0004\u001dOYdi\u0004\n\u0013UksO\n\rU~t>ZX\u00028";
      c[15] = "al\u0002~\u001a\u0000jc\u00131y\r\u007fn\u001cZL\u000fn}\u0000v[\u0002";
      c[16] = "\"Hw\u0006\u000eC\"H`Z\u0002L8\u0003`D\u0002Y?r1\u001bZ\u000e/Ab[\u0010u~\u00193";
      c[17] = "(`5b\u001cD(`\">\u0010K2+\" \u0010^5ZruD\u0014bf--\u0002^\u00197u~";
      c[18] = "$\u0016.2\nt$\u00169n\u0006{>]9p\u0006n9,i%R$n\u00106}\u0014n\u0015@c*W";
      c[19] = "\u000f\u0000lF:c\u0004\u000f}\t[m\u000f\u0004yS";
      c[20] = "G>-\u0015Ll\u0011>oN0p\u001blvAg B8.-\u000e GxuI@~\u0001~";
      c[21] = "a*b{\u0004\u0004+m/oi]<*rr\u0005ohi-)S8`=jqPA0-tgiQaki,\u000eX+f#\u0015";
      c[22] = "c!PrXe80\u000f:<3;!Tgkdev\f\u000b\u0006>e,\u000etS4#6";
      c[23] = "y\u000fv\u0002Hh)\u0006yQ+c*\u001fy\bGQyZ#_+86\u0011#\u0006Dg;\u001bfo";
      c[24] = "sN$\u0016\u007f+(_{^\u001b}+N \u0003L*t\u0013{o+o:M)Sa(wY";
      c[25] = "La$_s\\H0l\u0003\u0014\\wc&[k\r\u00071-_s5\u001ez3A,ELq7Y\u0014";
      c[26] = "ySf&\u0003Lh\u0017*#>Nr\u0001<wi\u0019(Wb\u001bT\u001f-QewE[aT";
      c[27] = "b\u0003esd!rUoq\u0003thSsfT#2\u0002'\nh'{Gp1xqqE";
      c[28] = "TN)G\u000fGTG4\u0007bS^S/Q5\u0004\u0000\u0003v\fbVO\\1P\u0018VFAq";
      c[29] = "K+i/SU\u001d++t/B\u001bh6pCpI/o'\u001e'J\u007fikBW\u0004k7)/";
      c[30] = ">\u0001\u001c\u0002\u0005%h\u0001^Yy9bSGV.j2\u0000\u0012:Gi>GD^\t7xA";
      c[31] = "}p:\u000e\bj+aoh\f3ubSQ\u0003<v2l\u0001Q(!\u000f";
      c[32] = "P_\u0013)Y\u0002\u0006_Qr%\u001e\f\rH}rNU[\u0012\u0011\u001bNP\u0019KuU\u0010\u0016\u001f";
      c[33] = "\u00051:G)\u001b\u0001`r\u001bN\u001b>e2\u0017$\u000fQ#:Z0r\u00021wZ#\u0002L%)\u0018N";
      c[34] = "2\u00172GZ}b\u001e=\u00149va\u0007=MUD6@f\u0013\t\u00132E`S^w|\u001b&U9";
      c[35] = "(\n\u0013\tnl,[[U\to\u0013^\u001bYcx|\u0018\u0013\u0014w\u0005-\u0010\u0013R`jr\u001d\u0019\u0017\t";
      c[36] = "sp1H\u00196=do\nt(.vjX#\u007ft&74JxrbiP\u0004&4d";
      c[37] = "\u000f\u0001U]{NA\u0015\u000b\u001f\u0016[^\u0016\nFzi\nRQ\u0018\u0016\u0000\rW\u0013FrNS\u0011\u0015!(\u0000\u000e\u0013\rEf^H\u0015j\u001f(\u0003J\r\u000eQvELjVJ)B^\u001a\u0018^w\u00003";
      c[38] = "g\nk\u0002%\u000bn@fH\u001c\tcF6\u001ep;7\u0005iI l2Qi\u0005q\u001c|E7G\u001c";
      c[39] = "G\u0017Y\u0007;&N]TM\u0002$C[\u0004\u001bn\u0016\u0017\u0018[L9AD\u0018ZA?-U\\\u0016D\u0002";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field g(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = c[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = d[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = f(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = c(var12, var10, var11);
         if (var13 != null) {
            c[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = d(var14[var15], var10, var11);
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
            var12 = f(1368241697185335L, 0L);
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

   private static native boolean __indy$0(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$2(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$3(MethodHandle var0, Object[] var1) throws Throwable;

   private static native double __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native MethodHandle __indy$7() throws Throwable;

   private static native String __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$13(Object var0, double var1, double var3, double var5, long var7, long var9) throws Throwable;

   private static native class_638 __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native class_3960 __indy$15(long var0, long var2) throws Throwable;

   private static native class_242 __indy$16(long var0, long var2) throws Throwable;

   private static native class_3965 __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_240 __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native class_240 __indy$19(long var0, long var2) throws Throwable;

   private static native double __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$23(Object var0, long var1, long var3) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-3187951686264687421L,2);f.pc=1;return;}
case 1:{f.push(4540501363517620033L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(187608950918289L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0166.class,"a",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(40,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0166.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(40,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0166.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0166.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
