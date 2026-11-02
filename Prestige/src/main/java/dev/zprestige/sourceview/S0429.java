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
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1665;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public final class S0429 extends class_1665 {
   public boolean a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   public S0429(class_1937 var1, class_1309 var2, class_1799 var3, long var4)  throws Throwable {
      var4 = b ^ var4;
      super(__indy$0(4693965083015244292L, var4), var2, var1, var3, var3);
try {

   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
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
            var12 = b(1142888884030464L, 0L);
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
            var23 = b(1142888884030464L, 0L);
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
            var23 = b(1142888884030464L, 0L);
         }
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

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$1().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/g4" + " : " + var1 + " : " + var2.toString(), var5);
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
      return __indy$2(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'C' && var8 != 222 && var8 != 'L' && var8 != 'Y') {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'J') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'r') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'C') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 222) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'L') {
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
         case 0 -> 22;
         case 1 -> 61;
         case 2 -> 59;
         case 3 -> 26;
         case 4 -> 50;
         case 5 -> 4;
         case 6 -> 32;
         case 7 -> 31;
         case 8 -> 29;
         case 9 -> 63;
         case 10 -> 40;
         case 11 -> 11;
         case 12 -> 10;
         case 13 -> 57;
         case 14 -> 46;
         case 15 -> 49;
         case 16 -> 23;
         case 17 -> 62;
         case 18 -> 9;
         case 19 -> 53;
         case 20 -> 15;
         case 21 -> 55;
         case 22 -> 16;
         case 23 -> 30;
         case 24 -> 60;
         case 25 -> 44;
         case 26 -> 35;
         case 27 -> 5;
         case 28 -> 14;
         case 29 -> 28;
         case 30 -> 33;
         case 31 -> 21;
         case 32 -> 54;
         case 33 -> 56;
         case 34 -> 39;
         case 35 -> 20;
         case 36 -> 1;
         case 37 -> 12;
         case 38 -> 24;
         case 39 -> 0;
         case 40 -> 47;
         case 41 -> 3;
         case 42 -> 25;
         case 43 -> 51;
         case 44 -> 45;
         case 45 -> 52;
         case 46 -> 2;
         case 47 -> 42;
         case 48 -> 37;
         case 49 -> 13;
         case 50 -> 19;
         case 51 -> 27;
         case 52 -> 58;
         case 53 -> 17;
         case 54 -> 34;
         case 55 -> 43;
         case 56 -> 38;
         case 57 -> 6;
         case 58 -> 7;
         case 59 -> 41;
         case 60 -> 18;
         case 61 -> 48;
         case 62 -> 36;
         default -> 8;
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

      c[0] = "FM'TGVPM\"\u000eTAG\u0006!\bXUVA6\u001f\u0013A\u0016";
      c[1] = "7ai'-77a~{!8-*~e!-*[,;yi";
      c[2] = boolean.class;
      d[2] = "java/lang/Boolean";
      c[3] = "UIm[\u001fcUIz\u0007\u0013lO\u0002z\u0019\u0013yHs+AA";
      c[4] = double.class;
      d[4] = "java/lang/Double";
      c[5] = float.class;
      d[5] = "java/lang/Float";
      c[6] = void.class;
      d[6] = "java/lang/Void";
      c[7] = "'n^$\u0013V'nIx\u001fY=%If\u001fL:T\u0019?M\r";
      c[8] = "^4\u001cy\u0015)H4\u0019#\u0006>_\u007f\u001a%\n*N8\r2A8r";
      c[9] = "!z@\r\u0012\u0006TZK\u0002\u0003I)BX\u0005\n\u0000A";
      c[10] = "jwB@I\"jwU\u001cE-p<U\u0002E8wM\u0004]\u001dog~W\u001dW\u00146&\u0006";
      c[11] = "\u001f\u0005\u001a8T9\u0014\n\u000bw74\u0001\u0007\u0004\u001c\u00026\u0010\u0014\u00180\u0015;";
      c[12] = "`\\_\"@Q`\\H~L^z\u0017H`LK}f\u0019?\u0014";
      c[13] = "L\u007f\u0005@\f,L\u007f\u0012\u001c\u0000#V4\u0012\u0002\u00006QE@XVp";
      c[14] = "\u0018\u0000u\u0012\"L\u0006\bo]jL\u001c\u0002w\u001acW\\1q\u0016hP\u0011\u0000w\u0016";
      c[15] = "&O'} \u0006&O0!,\t<\u00040?,\u001c;ubatV";
      c[16] = ">\u007f#RP\n5p2\u001d1\u0004>{6G";
      c[17] = "ysB?N|z{_>!r.?AhM@z{\u001b6!,x$Fj\u0011f#:\u0018\u000f\u001b' sNd\u0018/=r!";
      c[18] = "E%J/h\"\u0007{\u0014~TqxxRy%z\t8\u0015ne\u001b";
      c[19] = "X\u0006YCZ}\u0016\u0012\u001aD3q\u0007\u0019DBd&]O\u001b.\rm\u001f\u0019ILCy\\\u001e";
      c[20] = "p;GA\u0011q `\u0016{D 0jL\u0017vww1\u0012K!vwmL\u001e\u0011<,s\u0012{";
      c[21] = ".}&\u0003DE~&w9\u0011\u0014n,-U#@\"|r\u0001tB)+-\\D\br5s9OBu+/\t\u0005\u0019kuJ\u0002O\u001eu)zH\u0014\u0000+Lv\u0006\u0005\u0006l= \u0006\u0005\u0005\u0012";
      c[22] = "+Pc\u001cw\u0014{\u000b2&\"Ek\u0001hJ\u0010\u0011'^>\u0017G\u0012'\u0002?I,\u0011/\u001f>&{\u0017f\u001eqW-\u0017f\u001d\u000f";
      c[23] = "]be9G\u0004\r94\u0003\u0019Y\f7eTI\u0000Yo\t?J\u0003\u0011(9<OR\u0003";
      c[24] = "X\u0001\\!jy\bZ\r\u001b?(\u0018PWw\r\u007fZ\u0001\u000e!Zy[AOe+/[AL\u001b";
      c[25] = "\nNYo|yDOL7\u0015yV^F4yK\u0007\u0013\u0017b*\u001c\u0002I\u00179x%\u000bDLh\u0015-CZ\u00195*u^\u0019TS%aQ\\Z<g}WO&";
      c[26] = "cy)2]R=3+\u007f/X3>)$Cjg\u007fr\u007f\u001e=ey.$J\r/\"0z/\u0006e%.&\u001fL>;pC\u0014\u00069%,s^]'{I";
      c[27] = "l\t![KAo\u0001<Z$D7T&\u0007s\u0013m\u0004{k\u001f\u00111^'[UJ/\u0000";
      c[28] = "v5k\u0016\r\u001e&n:,XO6d`@j\u0019t8:\u0010=\u001ew?wW\r\u001drne,";
      c[29] = "r\u001ej\u000f\u0013!q\u0016w\u000e|/%RiX\u0010\u001ds\u00113\u0001CJs\u0015nX\u0019z9Np\u0006|";
      c[30] = "8aF\u001c\u007f6h:\u0017&*gx0MJ\u00181:l\u0017\u0018O69kZ]\u007f5<:H&s5u/TW%5u,*";
      c[31] = "#qe\u007fY.s*4E\u0000\u0012 :8$P(n;kyi-eqh|Scd\"5E";
      c[32] = "nY gg(>\u0002q]2y.\b+1\u0000+nX|]gi8\u001602%u>\u0005Lahe-\u0016=7he.h";
      c[33] = "^TDw_\u0010]\\Yv0\u0015\u0005\tC+gB_Y\u001fG\u000b@\u0003\u0003BwA\u001b\u001d]";
      c[34] = "Y\u007f7\u0012I,\t$f(\u001c}\u0019.<D.)Uqj\u0018y*U-kG\u0012)]0j(";
      c[35] = "\u0000R\u000eZ\u0012,P\t_`G}@\u0003\u0005\fu*\u0002R_P\")WR\b\r\u001b Z\tY`\u001bt_\n\u0013^R*Z\u0013b";
      c[36] = "\u0006yt3^'V\"%\t\u000bvF(\u007fe9&\u0005y)\tW\u007fY!i7\u001e!\\8\u0018";
      c[37] = "8@K;7@h\u001b\u001a\u0001b\u0011x\u0011@mPG:M\u001a<\u0007@9JWz7C<\u001bE\u0001";
      c[38] = "7o/IN^g4~s\u001b\u000fw>$\u001f)X0eyJ~Y09$\u0016N\u0013k'zs";
      c[39] = "e6uOrB5m$u'\u0013%g~\u0019\u0015Ci>%uyE>`|E3\u001e >\u0019Ny\u0019>b)\u0004\"\u0007`\u0007\"N%\u0019<7h\u0015;GY;&\u0004=\u0000(m&\u0004>~";
      c[40] = "HiLi\u000f \u00182\u001dSZq\b8G?h!Kd\u0018S\u0006x\u00171QmO&\u0012( ";
      c[41] = "]~@\u007fE\u001d\r%\u0011E\u001b@\f+@\u0012H\u0010Qu,yH\u001a\u00114\u001czMK\u0003";
      c[42] = "\u007feg\r\u0005f|mz\fjh()dZ\u0006Z|m>\fj7u64R\u00014}+5=";
      c[43] = "$E\u0010q\u0019gt\u001eAKL6d\u0014\u001b'~e!HDz)g'\u0005\u00035X1'\u0005\u0000K";
      c[44] = "b5\t_q5 )\u000fL\r??4\u0003Fa\rkw\\\u00116Zl:\u001aLd8\".YK\r";
      c[45] = "i i{FIj{j\"\"\u0019a*q.uI9}/BK\u0010|~q|HK\u007f'";
      c[46] = "\u0019*_YC]\u001a\"BX,XBwX\u0005{\u000f\u0018'\u0006i\u0017\rD}YY]VZ#";
      c[47] = "-r\u001f=(^})N\u0007}\u000fm#\u0014kOY/\u007fN8\u0018^,x\u0003|(]))\u0011\u0007$]`<\rvr]`?s";
      c[48] = "\u0019GqS\u0011{I\u001c iD*Y\u0016z\u0005v}\u001eM$V!|\u001e\u0011z\f\u00116E\u000f$i";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_7454(class_3966 var1) {
try {

      this.a = true;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_24920(class_3965 var1) {
try {

      this.a = true;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_1799 method_7445() {
try {

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_7485(double var1, double var3, double var5, float var7, float var8) {
try {

      long var9 = b ^ 97386683898943L;
      class_243 var11 = __indy$4(__indy$3(new class_243(var1, var3, var5), 5339204520594421781L, var9), var7, 5338576741163750694L, var9);
      __indy$5(this, var11, 5338882236472354750L, var9);
      __indy$9(
         this,
         (float)(
            __indy$8(__indy$6(var11, 5339508498698363363L, var9), __indy$7(var11, 5339694846571538054L, var9), 5338067773948125296L, var9)
               * 180.0F / (float)Math.PI
         ),
         5338355744744273828L,
         var9
      );
      __indy$13(
         this,
         (float)(
            __indy$12(__indy$10(var11, 5338115797877187039L, var9), __indy$11(var11, 5338303921050196604L, var9), 5338067773948125296L, var9)
               * 180.0F / (float)Math.PI
         ),
         5339572894831264227L,
         var9
      );
      __indy$15(this, __indy$14(this, 5338241299058698791L, var9), 5339111351673395189L, var9);
      __indy$17(this, __indy$16(this, 5339959557973722735L, var9), 5338973319015910447L, var9);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_1799 method_57314() {
try {

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_5773() {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(14,11);
f.locals[0]=this;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$20$0(f);break;
case 1:__flow$20$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=17&&f.pc<30&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=32;continue;}
if(f.pc>=21&&f.pc<45&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=47;continue;}
if(f.pc>=104&&f.pc<119&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=119;continue;}
if(f.pc>=131&&f.pc<152&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=154;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static boolean lambda$tick$0(S0429 var0, class_1297 var1) {
try {

      long var2 = b ^ 59667322552911L;
      return __indy$47(var0, var1, -7752189288229472559L, var2);
   
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

   private static native class_1299 __indy$0(long var0, long var2) throws Throwable;

   private static native MethodHandle __indy$1() throws Throwable;

   private static native Object __indy$2(MethodHandle var0, Object[] var1) throws Throwable;

   private static native class_243 __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$4(Object var0, double var1, long var3, long var5) throws Throwable;

   private static native void __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$8(double var0, double var2, long var4, long var6) throws Throwable;

   private static native void __indy$9(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native double __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$12(double var0, double var2, long var4, long var6) throws Throwable;

   private static native void __indy$13(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native float __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$15(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native float __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$17(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native String[] __indy$18(long var0, long var2) throws Throwable;

   private static native Predicate __indy$19(S0429 var0) throws Throwable;

   private static native class_239 __indy$20(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_240 __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native class_240 __indy$22(long var0, long var2) throws Throwable;

   private static native MatchException __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$24(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$32(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$33(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$34(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$37(Object var0, double var1, long var3, long var5) throws Throwable;

   private static native void __indy$38(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$40(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$41(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$42(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$43(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$44(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$45(Object var0, double var1, double var3, double var5, long var7, long var9) throws Throwable;

   private static native void __indy$46(Object var0, double var1, double var3, double var5, long var7, long var9) throws Throwable;

   private static native boolean __indy$47(Object var0, Object var1, long var2, long var4) throws Throwable;

private void __flow$20$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0429.b,2);f.pc=1;return;}
case 1:{f.push(5084558904144L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[1]=f.pop();f.pc=4;return;}
case 4:{f.push(-7099365547062797891L,2);f.pc=5;return;}
case 5:{f.push(f.locals[1],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$18(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.push(f.locals[0],1);f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$19(((dev.zprestige.sourceview.S0429)(Object)(a0))),1);f.pc=12;return;}
case 12:{f.push(-7099067438014402385L,2);f.pc=13;return;}
case 13:{f.push(f.locals[1],2);f.pc=14;return;}
case 14:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$20(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=15;return;}
case 15:{f.locals[4]=f.pop();f.pc=16;return;}
case 16:{f.locals[3]=f.pop();f.pc=17;return;}
case 17:{f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.push(f.locals[3],1);f.pc=20;return;}
case 20:{if(f.pop()!=null){f.pc=60;return;}f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(f.locals[4],1);f.pc=23;return;}
case 23:{f.push(-7100387991201850965L,2);f.pc=24;return;}
case 24:{f.push(f.locals[1],2);f.pc=25;return;}
case 25:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=26;return;}
case 26:{f.push(-7099444482389626170L,2);f.pc=27;return;}
case 27:{f.push(f.locals[1],2);f.pc=28;return;}
case 28:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$22(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=29;return;}
case 29:{Object b=f.pop(),a=f.pop();if(a==b){f.pc=53;return;}f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=38;return;}
case 32:{f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.push(-7099187307701579607L,2);f.pc=35;return;}
case 35:{f.push(f.locals[1],2);f.pc=36;return;}
case 36:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=37;return;}
case 37:{throw (Throwable)f.pop();}
case 38:{f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.push(f.locals[0],1);f.pc=41;return;}
case 41:{f.push(f.locals[4],1);f.pc=42;return;}
case 42:{f.push(-7100658903787839815L,2);f.pc=43;return;}
case 43:{f.push(f.locals[1],2);f.pc=44;return;}
case 44:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0429.__indy$24(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.pc=53;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(-7099187307701579607L,2);f.pc=50;return;}
case 50:{f.push(f.locals[1],2);f.pc=51;return;}
case 51:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=52;return;}
case 52:{throw (Throwable)f.pop();}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.push(f.locals[0],1);f.pc=57;return;}
case 57:{f.push(-7100014990785511804L,2);f.pc=58;return;}
case 58:{f.push(f.locals[1],2);f.pc=59;return;}
case 59:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0429.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.push(f.locals[0],1);f.pc=64;return;}
case 64:{f.push(-7100552705591193278L,2);f.pc=65;return;}
case 65:{f.push(f.locals[1],2);f.pc=66;return;}
case 66:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=67;return;}
case 67:{f.locals[5]=f.pop();f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.pc=70;return;}
case 70:{f.push(f.locals[0],1);f.pc=71;return;}
case 71:{f.push(-7101812561685107087L,2);f.pc=72;return;}
case 72:{f.push(f.locals[1],2);f.pc=73;return;}
case 73:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=74;return;}
case 74:{f.push(f.locals[5],1);f.pc=75;return;}
case 75:{f.push(-7100238457872009588L,2);f.pc=76;return;}
case 76:{f.push(f.locals[1],2);f.pc=77;return;}
case 77:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$29(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=78;return;}
case 78:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=79;return;}
case 79:{f.locals[6]=f.pop();f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.pc=82;return;}
case 82:{f.push(f.locals[0],1);f.pc=83;return;}
case 83:{f.push(-7099811377287826032L,2);f.pc=84;return;}
case 84:{f.push(f.locals[1],2);f.pc=85;return;}
case 85:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$30(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=86;return;}
case 86:{f.push(f.locals[5],1);f.pc=87;return;}
case 87:{f.push(-7098922039360211280L,2);f.pc=88;return;}
case 88:{f.push(f.locals[1],2);f.pc=89;return;}
case 89:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$31(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=90;return;}
case 90:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=91;return;}
case 91:{f.locals[8]=f.pop();f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.pc=94;return;}
case 94:{f.push(f.locals[0],1);f.pc=95;return;}
case 95:{f.push(-7100799270548628738L,2);f.pc=96;return;}
case 96:{f.push(f.locals[1],2);f.pc=97;return;}
case 97:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$32(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=98;return;}
case 98:{f.push(f.locals[5],1);f.pc=99;return;}
case 99:{f.push(-7100711919949860375L,2);f.pc=100;return;}
case 100:{f.push(f.locals[1],2);f.pc=101;return;}
case 101:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$33(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=102;return;}
case 102:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=103;return;}
case 103:{f.locals[10]=f.pop();f.pc=104;return;}
case 104:{f.pc=105;return;}
case 105:{f.pc=106;return;}
case 106:{f.push(f.locals[0],1);f.pc=107;return;}
case 107:{f.push(-7098956525054681981L,2);f.pc=108;return;}
case 108:{f.push(f.locals[1],2);f.pc=109;return;}
case 109:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0429.__indy$34(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=110;return;}
case 110:{f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.push(f.locals[0],1);f.pc=113;return;}
case 113:{f.push(-7100919529209440452L,2);f.pc=114;return;}
case 114:{f.push(f.locals[1],2);f.pc=115;return;}
case 115:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$35(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=116;return;}
case 116:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=125;return;}f.pc=117;return;}
case 117:{f.push(Float.intBitsToFloat(1061997773),1);f.pc=118;return;}
case 118:{f.pc=128;return;}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$20$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.push(-7099187307701579607L,2);f.pc=122;return;}
case 122:{f.push(f.locals[1],2);f.pc=123;return;}
case 123:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$36(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=124;return;}
case 124:{throw (Throwable)f.pop();}
case 125:{f.pc=126;return;}
case 126:{f.pc=127;return;}
case 127:{f.push(Float.intBitsToFloat(1065185444),1);f.pc=128;return;}
case 128:{f.pc=129;return;}
case 129:{f.pc=130;return;}
case 130:{f.locals[12]=f.pop();f.pc=131;return;}
case 131:{f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.push(f.locals[0],1);f.pc=134;return;}
case 134:{f.push(f.locals[5],1);f.pc=135;return;}
case 135:{f.push(f.locals[12],1);f.pc=136;return;}
case 136:{f.push((double)(dev.zprestige.sourceview.FlowFrame.f(f.pop())),2);f.pc=137;return;}
case 137:{f.push(-7099586920579431863L,2);f.pc=138;return;}
case 138:{f.push(f.locals[1],2);f.pc=139;return;}
case 139:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$37(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=140;return;}
case 140:{f.push(-7099686120556646191L,2);f.pc=141;return;}
case 141:{f.push(f.locals[1],2);f.pc=142;return;}
case 142:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0429.__indy$38(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=143;return;}
case 143:{f.pc=144;return;}
case 144:{f.pc=145;return;}
case 145:{f.push(f.locals[0],1);f.pc=146;return;}
case 146:{f.push(f.locals[3],1);f.pc=147;return;}
case 147:{if(f.pop()!=null){f.pc=192;return;}f.pc=148;return;}
case 148:{f.push(-7100109200452599579L,2);f.pc=149;return;}
case 149:{f.push(f.locals[1],2);f.pc=150;return;}
case 150:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$39(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=151;return;}
case 151:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=188;return;}f.pc=152;return;}
case 152:{f.pc=153;return;}
case 153:{f.pc=160;return;}
case 154:{f.pc=155;return;}
case 155:{f.pc=156;return;}
case 156:{f.push(-7099187307701579607L,2);f.pc=157;return;}
case 157:{f.push(f.locals[1],2);f.pc=158;return;}
case 158:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$40(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=159;return;}
case 159:{throw (Throwable)f.pop();}
case 160:{f.pc=161;return;}
case 161:{f.pc=162;return;}
case 162:{f.pc=163;return;}
case 163:{f.push(f.locals[0],1);f.pc=164;return;}
case 164:{f.push(-7100552705591193278L,2);f.pc=165;return;}
case 165:{f.push(f.locals[1],2);f.pc=166;return;}
case 166:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$41(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=167;return;}
case 167:{f.locals[13]=f.pop();f.pc=168;return;}
case 168:{f.pc=169;return;}
case 169:{f.pc=170;return;}
case 170:{f.push(f.locals[0],1);f.pc=171;return;}
case 171:{f.push(f.locals[13],1);f.pc=172;return;}
case 172:{f.push(-7100238457872009588L,2);f.pc=173;return;}
case 173:{f.push(f.locals[1],2);f.pc=174;return;}
case 174:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$42(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=175;return;}
case 175:{f.push(f.locals[13],1);f.pc=176;return;}
case 176:{f.push(-7098922039360211280L,2);f.pc=177;return;}
case 177:{f.push(f.locals[1],2);f.pc=178;return;}
case 178:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$43(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=179;return;}
case 179:{f.push(Double.longBitsToDouble(4584304132692975288L),2);f.pc=180;return;}
case 180:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a-b,2);f.pc=181;return;}
case 181:{f.push(f.locals[13],1);f.pc=182;return;}
case 182:{f.push(-7100711919949860375L,2);f.pc=183;return;}
case 183:{f.push(f.locals[1],2);f.pc=184;return;}
case 184:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0429.__indy$44(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=185;return;}
case 185:{f.push(-7099873163105353392L,2);f.pc=186;return;}
case 186:{f.push(f.locals[1],2);f.pc=187;return;}
case 187:{Object a5=f.pop();Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0429.__indy$45(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2),dev.zprestige.sourceview.FlowFrame.d(a3),dev.zprestige.sourceview.FlowFrame.j(a4),dev.zprestige.sourceview.FlowFrame.j(a5));f.pc=188;return;}
case 188:{f.pc=189;return;}
case 189:{f.pc=190;return;}
case 190:{f.pc=191;return;}
case 191:{f.push(f.locals[0],1);f.pc=192;return;}
case 192:{f.pc=193;return;}
case 193:{f.pc=194;return;}
case 194:{f.push(f.locals[6],2);f.pc=195;return;}
case 195:{f.push(f.locals[8],2);f.pc=196;return;}
case 196:{f.push(f.locals[10],2);f.pc=197;return;}
case 197:{f.push(-7100906490799869972L,2);f.pc=198;return;}
case 198:{f.push(f.locals[1],2);f.pc=199;return;}
case 199:{Object a5=f.pop();Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0429.__indy$46(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2),dev.zprestige.sourceview.FlowFrame.d(a3),dev.zprestige.sourceview.FlowFrame.j(a4),dev.zprestige.sourceview.FlowFrame.j(a5));f.pc=200;return;}
case 200:{f.pc=201;return;}
case 201:{f.pc=202;return;}
case 202:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-4997883381052347620L,2);f.pc=1;return;}
case 1:{f.push(-2010918592361394661L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(197061907525696L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0429.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(49,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0429.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(49,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0429.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0429.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
