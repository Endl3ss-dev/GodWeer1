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

public final class S0428 extends class_1665 {
   public boolean a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   public S0428(class_1937 var1, class_1309 var2, class_1799 var3, long var4)  throws Throwable {
      var4 = b ^ var4;
      super(__indy$0(160146893310327664L, var4), var2, var1, var3, var3);
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
            var12 = b(1278835489404021L, 0L);
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
            var23 = b(1278835489404021L, 0L);
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
            var23 = b(1278835489404021L, 0L);
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
         throw new RuntimeException("dev/zprestige/prestige/g3" + " : " + var1 + " : " + var2.toString(), var5);
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
         if (var8 != 'o' && var8 != 222 && var8 != 'x' && var8 != 's') {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'X') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 244) {
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
            } else if (var8 == 222) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'x') {
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
         case 0 -> 0;
         case 1 -> 58;
         case 2 -> 31;
         case 3 -> 9;
         case 4 -> 48;
         case 5 -> 2;
         case 6 -> 16;
         case 7 -> 61;
         case 8 -> 1;
         case 9 -> 35;
         case 10 -> 54;
         case 11 -> 33;
         case 12 -> 36;
         case 13 -> 20;
         case 14 -> 34;
         case 15 -> 51;
         case 16 -> 19;
         case 17 -> 12;
         case 18 -> 6;
         case 19 -> 52;
         case 20 -> 46;
         case 21 -> 40;
         case 22 -> 5;
         case 23 -> 29;
         case 24 -> 47;
         case 25 -> 32;
         case 26 -> 25;
         case 27 -> 55;
         case 28 -> 8;
         case 29 -> 30;
         case 30 -> 14;
         case 31 -> 53;
         case 32 -> 62;
         case 33 -> 42;
         case 34 -> 10;
         case 35 -> 60;
         case 36 -> 23;
         case 37 -> 39;
         case 38 -> 38;
         case 39 -> 11;
         case 40 -> 26;
         case 41 -> 7;
         case 42 -> 18;
         case 43 -> 4;
         case 44 -> 63;
         case 45 -> 28;
         case 46 -> 13;
         case 47 -> 45;
         case 48 -> 59;
         case 49 -> 27;
         case 50 -> 21;
         case 51 -> 17;
         case 52 -> 43;
         case 53 -> 56;
         case 54 -> 3;
         case 55 -> 44;
         case 56 -> 57;
         case 57 -> 49;
         case 58 -> 24;
         case 59 -> 50;
         case 60 -> 15;
         case 61 -> 37;
         case 62 -> 22;
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

      d[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      c[0] = "uI5\nz\tuI\"Vv\u0006o\u0002\"Hv\u0013hsr\u001d!V";
      c[1] = "ETb\n?\u0003ETuV3\f_\u001fuH3\u0019Xn$\u0010a";
      c[2] = "~X\u001c\u0013.MhX\u0019I=Z\u007f\u0013\u001aO1NnT\rXzZ)";
      c[3] = void.class;
      d[3] = "java/lang/Void";
      c[4] = "\u0019)<Rin\u0019)+\u000eea\u0003b+\u0010et\u0004\u0013yN=0";
      c[5] = boolean.class;
      d[5] = "java/lang/Boolean";
      c[6] = double.class;
      d[6] = "java/lang/Double";
      c[7] = "U=U\u001et\u0018^2DQ\u0017\u0015K?K:\"\u0017Z,W\u00165\u001a";
      c[8] = "V\u00194\u000b&<@\u00191Q5+WR2W9?F\u0015%@r-z";
      c[9] = "\u007fd^1Tk\nDU>E$w\\F9Lm\u001f";
      c[10] = "&.8S'a&./\u000f+n<e/\u0011+{;\u0014~Ns,+'-\u000e9Wz\u007f|";
      c[11] = "wgW[!!wg@\u0007-.m,@\u0019-;j]\u0011Fu";
      c[12] = ";z\u0002uP\u0007;z\u0015)\\\b!1\u00157\\\u001d&@Gm\n[";
      c[13] = "w* r}ri\":=5rs(\"z<i3\u001b$v7n~*\"v";
      c[14] = float.class;
      d[14] = "java/lang/Float";
      c[15] = "q1\u001b/\"\u000eq1\fs.\u0001kz\fm.\u0014l\u000b\\4|U";
      c[16] = "U\u001bd\u000b`dU\u001bsWlkOPsIl~H!!\u001744";
      c[17] = ";f\u0007cD\u001c;f\u0010?H\u0013!-\u0010!H\u0006&\\@t\u001f@";
      c[18] = "3\u0016X^p\u00078\u0019I\u0011\u0011\t3\u0012MK";
      c[19] = "\u001d\u0016(Pr\"FGe\u0007B)JPu^.\u001b\u001e\u001c*\bsLVReF>vBHo9x4U\u001dk\u0003! DV\u0015";
      c[20] = "\u001d\u0019k),v\t\u0003aV5!\u0010\u0007|:\u0007uT]\"Vl,\u0007Zy-<'Q\u001e\u001b'.<\u0013\u001b!346l";
      c[21] = "\u001d.4\u000b|0F\u007fy\\L;Jhi\u0005 \t\u001e$9Zt^\u001btb_.%K\u007f4\u001bLbG\u007f4\u000072L)pbp>L)k\u0019 5\u001am\tX4,\u0016j3\u0001 =]\u0014";
      c[22] = " P^\fQB{\u0001\u0013[aE\u001aV\u001c^\rG'\u000f\u0004\u0007Z,&\u0015X\t\n\u0011\u007f\r\u0001^a";
      c[23] = "wXMga\u00112_U#\u0011\u0011J\u0001Z4n\u0007.\u0006B-|{";
      c[24] = "z4mT]\u007f\u007fd=R#($c4Et\u007f~5k)\u001c|xs.\u0010\u0019,(u";
      c[25] = "mwF~e\u000f6&\u000b)U\u000f6 \u001f{\u0002_ouG\u0017<\\%-\u001b('\u0004n=";
      c[26] = "\u0010\u001a4BJKKKy\u0015z@G\\iL\u0016r\u0010\u001b2\u0012J%\u0016@b\u0016\u0018^FK4Rz";
      c[27] = "_{T@\u001d|\u0004*\u0019\u0017-w\b=\tNAE^\u007fU\u0014\u0011\u0012\f|\u001bIM-\u0017$PY-";
      c[28] = "\u0012qdF\u0010\u0016I )\u0011 \u001dE79HL/\u0013ue\u0012\u001exAv+O@GZ.`_ BP9hQ\u001a\u001bD(#/";
      c[29] = "*\r=K0\u0005%\u0018?\u0003\u000bPw\u001a1\u0015gb&W`C45 \u00171\rhX#\u001f>\u0014\u000b\u0004 \ri\u000f:Sk\u0004nr4Iu[n\u0000pRd\u0000Q";
      c[30] = "xMzS\u0014y|Y8M%n|W\"XI\\(\u0016y\u0003\u0014\u000b-K)\u0002Gp}@\u007fF%7q@\u007f]^gz\u0016;?\u0019kz\u0016 DI`,RB";
      c[31] = "\u000bu(\u0000T&\u001fo\"\u007fF}\u0017o4(\u0011'G2XCHwGi#\u0013C!\u0003";
      c[32] = "v2];\f*b(WD\u0015}{,J('+8v\u0013{p,g'\u0010&\u000b|lqTD";
      c[33] = "=!_)l5%=Ca\u0002h:\"BvnZna\u001d!>\r& Rn~72:X\u0011";
      c[34] = "\u0003Y\u0016A\u001c\u0018X\b[\u0016,\u0013T\u001fKO@!\u0003]\u001a\u0016\u0016v\u0003\u001bY\u0019RLZ\u000fHR,";
      c[35] = "C\u0017u\u0004Ws\u0018F8Sgx\u0014Q(\n\u000bJD\u001dqQg!\u0019Fu\u000f\u001cq\u0012\u00101m[}\u0012\u0010*\u0016\u000bvDTHQ\u0007vDO3\u0001\f \u0000-r\u0015\u0015,\u0007\u0017+\u0001\u0004gy";
      c[36] = "\u00149h\\OaOh%\u000b\u007fjC\u007f5R\u0013X\u00148n\u000fF\u000f\u0012c>\b\u001dtBhhL\u007f";
      c[37] = "t+4VY\u000b/zy\u0001i\u0000#miX\u00052u/5\u0002Te',{_\tZ<t0Oi";
      c[38] = "\u001f\u0013\u001d\u0011,4DBPF\u001c?HU@\u001fp\r\u001f\u0017\u0011E,Z\u001fX@\u0007\u007f7\u001cPO\u001e\u001ca[JR@$cCE\u001fx";
      c[39] = "\u000e8\u001a |3UiWwL8Y~G. \n\t=\u0016xLfJaUqtdRn\u0018I";
      c[40] = "O^fqP-\u0014\u000f+&`&\u0018\u0018;\u007f\f\u0014LTd)PC\u0004\u001a+g\u001cy\u0010\u0000!\u0018";
      c[41] = "BNL\u000bA2\u0019\u001f\u0001\\q2\u0019\u0019\u0015\u000e&aIDKb\u0018a\n\u0014\u0011]\u00039A\u0004";
      c[42] = "r(@\u001b\bd)y\rL8o%n\u001d\u0015T]u-AJ816q\u000fJ\u00003.~Br";
      c[43] = "$k\r51y0q\u0007J#\"8q\u0011\u001dtxh-}v-(hw\u0006&&~,";
      c[44] = "V6\r\u001dQCL4\u0000|\bPI+\u0004\u0010:\u0004\ntS@mLK;\u001c\u0000WXQ1c";
      c[45] = "\u000b@=Wf\u000bP\u0011p\u0000V\u0000\\\u0006`Y:2\u000eF0\u000eVZM\u0015=\u0001$\u001eV\u0004f>l\u001dCK~\u00045\tR\u0000\u0000";
      c[46] = "(h(c>\u0018<r\"\u001c'O%v?p\u0015\u001ba,i\u001c3\\)i$&'F#\u0016";
      c[47] = "m\u0015!\u0019u\u00136DlNE\u0018:S|\u0017)*l\u0011 Mz}>\u0012n\u0010%B%J%\u0000EG/]-\u000e\u007f\u001e;Lfp";
      c[48] = "k\"dW#g0s)\u0000\u0013l<d9Y\u007f^k#b\u0007,\tmx2\u0003qr=sdG\u0013";
      c[49] = "\u001cR\nxn\u0004G\u0003G/^\u000fK\u0014Wv2=\u0018Q\u000b)oj\u001c\u0010E  PE\u0004Tk^";
      c[50] = "\u00015{U)\\E.j\u000e\u0016KS5t\u000fzy\u0007v+X-.\u0001s)\u0015h\u0017\u0004#y\u0013\u0016";
      c[51] = "4\u0003@5}-=_P'A}?\nH2\u0016-g]\u0016^+w2\f\u0010`\"+\"\u001e";
      c[52] = "UWN<~#AMDClxIMR\u0014;\"\u0019\u0013>\u007fbr\u0019KE/i$]";
      c[53] = "X\u0002Y#d\u0002\u0003S\u0014tT\t\u000fD\u0004-8;Y\u0003Yuhl\u0013F\u00145(V\u0007\\\u001eJn\u0014\u0010\t\u001ap7\u0000\u0001Bd";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_7454(class_3966 var1) {
try {

      long var2 = b ^ 29371946820516L;
      this.a = true;
      __indy$4(this, __indy$3(var1, -7376009242810635745L, var2), -7375401204131147471L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_24920(class_3965 var1) {
try {

      long var2 = b ^ 124520746804644L;
      this.a = true;
      __indy$6(this, __indy$5(var1, 2278860757416793019L, var2), 2280268750489114417L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_1799 method_7445() {
try {

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_7485(double var1, double var3, double var5, float var7, float var8) {
try {

      long var9 = b ^ 53675793030341L;
      class_243 var11 = __indy$8(__indy$7(new class_243(var1, var3, var5), -1818680238100836744L, var9), var7, -1816073731678186851L, var9);
      __indy$9(this, var11, -1815549453599255530L, var9);
      __indy$13(
         this,
         (float)(
            __indy$12(__indy$10(var11, -1818289574887575302L, var9), __indy$11(var11, -1818349085816187975L, var9), -1815323813067881898L, var9)
               * 180.0F / (float)Math.PI
         ),
         -1815469994230002838L,
         var9
      );
      __indy$17(
         this,
         (float)(
            __indy$16(__indy$14(var11, -1815296149170159479L, var9), __indy$15(var11, -1819148641046518168L, var9), -1815323813067881898L, var9)
               * 180.0F / (float)Math.PI
         ),
         -1818616482319260185L,
         var9
      );
      __indy$19(this, __indy$18(this, -1814959411726497382L, var9), -1818536570458269296L, var9);
      __indy$21(this, __indy$20(this, -1819377091598493245L, var9), -1815151478723075234L, var9);
   
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
if(f.pc>=21&&f.pc<49&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=49;continue;}
if(f.pc>=62&&f.pc<71&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=73;continue;}
if(f.pc>=68&&f.pc<82&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=82;continue;}
if(f.pc>=134&&f.pc<149&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=149;continue;}
if(f.pc>=161&&f.pc<182&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=184;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static boolean lambda$tick$0(S0428 var0, class_1297 var1) {
try {

      long var2 = b ^ 105558247466442L;
      return __indy$53(var0, var1, -6931109795633688422L, var2);
   
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

   private static native void __indy$4(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_243 __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_243 __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$8(Object var0, double var1, long var3, long var5) throws Throwable;

   private static native void __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$12(double var0, double var2, long var4, long var6) throws Throwable;

   private static native void __indy$13(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native double __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$16(double var0, double var2, long var4, long var6) throws Throwable;

   private static native void __indy$17(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native float __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$19(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native float __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$21(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native String[] __indy$22(long var0, long var2) throws Throwable;

   private static native Predicate __indy$23(S0428 var0) throws Throwable;

   private static native class_239 __indy$24(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_240 __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native class_240 __indy$26(long var0, long var2) throws Throwable;

   private static native MatchException __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$28(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$32(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$33(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$34(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$37(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$38(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$40(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$41(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$42(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$43(Object var0, double var1, long var3, long var5) throws Throwable;

   private static native void __indy$44(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$45(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$46(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$47(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$48(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$49(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$50(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$51(Object var0, double var1, double var3, double var5, long var7, long var9) throws Throwable;

   private static native void __indy$52(Object var0, double var1, double var3, double var5, long var7, long var9) throws Throwable;

   private static native boolean __indy$53(Object var0, Object var1, long var2, long var4) throws Throwable;

private void __flow$20$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0428.b,2);f.pc=1;return;}
case 1:{f.push(26986360234431L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[1]=f.pop();f.pc=4;return;}
case 4:{f.push(1997060359900236987L,2);f.pc=5;return;}
case 5:{f.push(f.locals[1],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$22(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.push(f.locals[0],1);f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$23(((dev.zprestige.sourceview.S0428)(Object)(a0))),1);f.pc=12;return;}
case 12:{f.push(1996296319613995880L,2);f.pc=13;return;}
case 13:{f.push(f.locals[1],2);f.pc=14;return;}
case 14:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$24(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=15;return;}
case 15:{f.locals[4]=f.pop();f.pc=16;return;}
case 16:{f.locals[3]=f.pop();f.pc=17;return;}
case 17:{f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.push(f.locals[3],1);f.pc=20;return;}
case 20:{if(f.pop()!=null){f.pc=62;return;}f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(f.locals[4],1);f.pc=23;return;}
case 23:{f.push(1999495981272973582L,2);f.pc=24;return;}
case 24:{f.push(f.locals[1],2);f.pc=25;return;}
case 25:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=26;return;}
case 26:{f.push(1996539971597596237L,2);f.pc=27;return;}
case 27:{f.push(f.locals[1],2);f.pc=28;return;}
case 28:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$26(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=29;return;}
case 29:{Object b=f.pop(),a=f.pop();if(a==b){f.pc=55;return;}f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=38;return;}
case 32:{f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.push(1996987491526697501L,2);f.pc=35;return;}
case 35:{f.push(f.locals[1],2);f.pc=36;return;}
case 36:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=37;return;}
case 37:{throw (Throwable)f.pop();}
case 38:{f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.push(f.locals[0],1);f.pc=42;return;}
case 42:{f.push(f.locals[4],1);f.pc=43;return;}
case 43:{f.push(1997459016266193062L,2);f.pc=44;return;}
case 44:{f.push(f.locals[1],2);f.pc=45;return;}
case 45:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0428.__indy$28(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.done=true;return;}
case 49:{f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.push(1996987491526697501L,2);f.pc=52;return;}
case 52:{f.push(f.locals[1],2);f.pc=53;return;}
case 53:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$29(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=54;return;}
case 54:{throw (Throwable)f.pop();}
case 55:{f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.push(f.locals[0],1);f.pc=59;return;}
case 59:{f.push(1999455832858058405L,2);f.pc=60;return;}
case 60:{f.push(f.locals[1],2);f.pc=61;return;}
case 61:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0428.__indy$30(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.pc=65;return;}
case 65:{f.push(f.locals[0],1);f.pc=66;return;}
case 66:{f.push(f.locals[3],1);f.pc=67;return;}
case 67:{if(f.pop()!=null){f.pc=92;return;}f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.push(((dev.zprestige.sourceview.S0428)(Object)f.pop()).a,1);f.pc=70;return;}
case 70:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=88;return;}f.pc=71;return;}
case 71:{f.pc=72;return;}
case 72:{f.pc=79;return;}
case 73:{f.pc=74;return;}
case 74:{f.pc=75;return;}
case 75:{f.push(1996987491526697501L,2);f.pc=76;return;}
case 76:{f.push(f.locals[1],2);f.pc=77;return;}
case 77:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$31(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=78;return;}
case 78:{throw (Throwable)f.pop();}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.done=true;return;}
case 82:{f.pc=83;return;}
case 83:{f.pc=84;return;}
case 84:{f.push(1996987491526697501L,2);f.pc=85;return;}
case 85:{f.push(f.locals[1],2);f.pc=86;return;}
case 86:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$32(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=87;return;}
case 87:{throw (Throwable)f.pop();}
case 88:{f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.push(f.locals[0],1);f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.pc=94;return;}
case 94:{f.push(1997660470221823839L,2);f.pc=95;return;}
case 95:{f.push(f.locals[1],2);f.pc=96;return;}
case 96:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$33(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=97;return;}
case 97:{f.locals[5]=f.pop();f.pc=98;return;}
case 98:{f.pc=99;return;}
case 99:{f.pc=100;return;}
case 100:{f.push(f.locals[0],1);f.pc=101;return;}
case 101:{f.push(1999360417407374640L,2);f.pc=102;return;}
case 102:{f.push(f.locals[1],2);f.pc=103;return;}
case 103:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$34(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=104;return;}
case 104:{f.push(f.locals[5],1);f.pc=105;return;}
case 105:{f.push(1999047490644364672L,2);f.pc=106;return;}
case 106:{f.push(f.locals[1],2);f.pc=107;return;}
case 107:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$35(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=108;return;}
case 108:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=109;return;}
case 109:{f.locals[6]=f.pop();f.pc=110;return;}
case 110:{f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.push(f.locals[0],1);f.pc=113;return;}
case 113:{f.push(1996647804760153338L,2);f.pc=114;return;}
case 114:{f.push(f.locals[1],2);f.pc=115;return;}
case 115:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$36(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=116;return;}
case 116:{f.push(f.locals[5],1);f.pc=117;return;}
case 117:{f.push(1996481802867178995L,2);f.pc=118;return;}
case 118:{f.push(f.locals[1],2);f.pc=119;return;}
case 119:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$37(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$20$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=121;return;}
case 121:{f.locals[8]=f.pop();f.pc=122;return;}
case 122:{f.pc=123;return;}
case 123:{f.pc=124;return;}
case 124:{f.push(f.locals[0],1);f.pc=125;return;}
case 125:{f.push(1997948823134947332L,2);f.pc=126;return;}
case 126:{f.push(f.locals[1],2);f.pc=127;return;}
case 127:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$38(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=128;return;}
case 128:{f.push(f.locals[5],1);f.pc=129;return;}
case 129:{f.push(1997845037101936323L,2);f.pc=130;return;}
case 130:{f.push(f.locals[1],2);f.pc=131;return;}
case 131:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$39(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=132;return;}
case 132:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=133;return;}
case 133:{f.locals[10]=f.pop();f.pc=134;return;}
case 134:{f.pc=135;return;}
case 135:{f.pc=136;return;}
case 136:{f.push(f.locals[0],1);f.pc=137;return;}
case 137:{f.push(1998341055982748472L,2);f.pc=138;return;}
case 138:{f.push(f.locals[1],2);f.pc=139;return;}
case 139:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0428.__indy$40(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=140;return;}
case 140:{f.pc=141;return;}
case 141:{f.pc=142;return;}
case 142:{f.push(f.locals[0],1);f.pc=143;return;}
case 143:{f.push(1998186342226376537L,2);f.pc=144;return;}
case 144:{f.push(f.locals[1],2);f.pc=145;return;}
case 145:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$41(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=146;return;}
case 146:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=155;return;}f.pc=147;return;}
case 147:{f.push(Float.intBitsToFloat(1061997773),1);f.pc=148;return;}
case 148:{f.pc=158;return;}
case 149:{f.pc=150;return;}
case 150:{f.pc=151;return;}
case 151:{f.push(1996987491526697501L,2);f.pc=152;return;}
case 152:{f.push(f.locals[1],2);f.pc=153;return;}
case 153:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$42(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=154;return;}
case 154:{throw (Throwable)f.pop();}
case 155:{f.pc=156;return;}
case 156:{f.pc=157;return;}
case 157:{f.push(Float.intBitsToFloat(1065185444),1);f.pc=158;return;}
case 158:{f.pc=159;return;}
case 159:{f.pc=160;return;}
case 160:{f.locals[12]=f.pop();f.pc=161;return;}
case 161:{f.pc=162;return;}
case 162:{f.pc=163;return;}
case 163:{f.push(f.locals[0],1);f.pc=164;return;}
case 164:{f.push(f.locals[5],1);f.pc=165;return;}
case 165:{f.push(f.locals[12],1);f.pc=166;return;}
case 166:{f.push((double)(dev.zprestige.sourceview.FlowFrame.f(f.pop())),2);f.pc=167;return;}
case 167:{f.push(1996830532924518375L,2);f.pc=168;return;}
case 168:{f.push(f.locals[1],2);f.pc=169;return;}
case 169:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$43(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=170;return;}
case 170:{f.push(1997336806444902764L,2);f.pc=171;return;}
case 171:{f.push(f.locals[1],2);f.pc=172;return;}
case 172:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0428.__indy$44(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=173;return;}
case 173:{f.pc=174;return;}
case 174:{f.pc=175;return;}
case 175:{f.push(f.locals[0],1);f.pc=176;return;}
case 176:{f.push(f.locals[3],1);f.pc=177;return;}
case 177:{if(f.pop()!=null){f.pc=222;return;}f.pc=178;return;}
case 178:{f.push(1997776956518612397L,2);f.pc=179;return;}
case 179:{f.push(f.locals[1],2);f.pc=180;return;}
case 180:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$45(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=181;return;}
case 181:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=218;return;}f.pc=182;return;}
case 182:{f.pc=183;return;}
case 183:{f.pc=190;return;}
case 184:{f.pc=185;return;}
case 185:{f.pc=186;return;}
case 186:{f.push(1996987491526697501L,2);f.pc=187;return;}
case 187:{f.push(f.locals[1],2);f.pc=188;return;}
case 188:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$46(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=189;return;}
case 189:{throw (Throwable)f.pop();}
case 190:{f.pc=191;return;}
case 191:{f.pc=192;return;}
case 192:{f.pc=193;return;}
case 193:{f.push(f.locals[0],1);f.pc=194;return;}
case 194:{f.push(1997660470221823839L,2);f.pc=195;return;}
case 195:{f.push(f.locals[1],2);f.pc=196;return;}
case 196:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$47(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=197;return;}
case 197:{f.locals[13]=f.pop();f.pc=198;return;}
case 198:{f.pc=199;return;}
case 199:{f.pc=200;return;}
case 200:{f.push(f.locals[0],1);f.pc=201;return;}
case 201:{f.push(f.locals[13],1);f.pc=202;return;}
case 202:{f.push(1999047490644364672L,2);f.pc=203;return;}
case 203:{f.push(f.locals[1],2);f.pc=204;return;}
case 204:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$48(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=205;return;}
case 205:{f.push(f.locals[13],1);f.pc=206;return;}
case 206:{f.push(1996481802867178995L,2);f.pc=207;return;}
case 207:{f.push(f.locals[1],2);f.pc=208;return;}
case 208:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$49(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=209;return;}
case 209:{f.push(Double.longBitsToDouble(4584304132692975288L),2);f.pc=210;return;}
case 210:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a-b,2);f.pc=211;return;}
case 211:{f.push(f.locals[13],1);f.pc=212;return;}
case 212:{f.push(1997845037101936323L,2);f.pc=213;return;}
case 213:{f.push(f.locals[1],2);f.pc=214;return;}
case 214:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0428.__indy$50(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=215;return;}
case 215:{f.push(1996892892333023927L,2);f.pc=216;return;}
case 216:{f.push(f.locals[1],2);f.pc=217;return;}
case 217:{Object a5=f.pop();Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0428.__indy$51(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2),dev.zprestige.sourceview.FlowFrame.d(a3),dev.zprestige.sourceview.FlowFrame.j(a4),dev.zprestige.sourceview.FlowFrame.j(a5));f.pc=218;return;}
case 218:{f.pc=219;return;}
case 219:{f.pc=220;return;}
case 220:{f.pc=221;return;}
case 221:{f.push(f.locals[0],1);f.pc=222;return;}
case 222:{f.pc=223;return;}
case 223:{f.pc=224;return;}
case 224:{f.push(f.locals[6],2);f.pc=225;return;}
case 225:{f.push(f.locals[8],2);f.pc=226;return;}
case 226:{f.push(f.locals[10],2);f.pc=227;return;}
case 227:{f.push(1998454793001832163L,2);f.pc=228;return;}
case 228:{f.push(f.locals[1],2);f.pc=229;return;}
case 229:{Object a5=f.pop();Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0428.__indy$52(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2),dev.zprestige.sourceview.FlowFrame.d(a3),dev.zprestige.sourceview.FlowFrame.j(a4),dev.zprestige.sourceview.FlowFrame.j(a5));f.pc=230;return;}
case 230:{f.pc=231;return;}
case 231:{f.pc=232;return;}
case 232:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-2147227930560893005L,2);f.pc=1;return;}
case 1:{f.push(9042713215132600935L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(220089866874965L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0428.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(54,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0428.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(54,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0428.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0428.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
