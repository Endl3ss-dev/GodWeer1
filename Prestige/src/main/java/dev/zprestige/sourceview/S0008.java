package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4599;
import net.minecraft.class_4603;
import net.minecraft.class_757;
import net.minecraft.class_759;
import net.minecraft.class_765;
import net.minecraft.class_9920;
import org.joml.Matrix4f;

public class S0008 {
   public class_757 a;
   public static long b;
   public static String[] c;
   public static String[] d;
   public static Map e;
   public static Object[] f;
   public static String[] g;

   public S0008(class_757 var1)  throws Throwable {
try {

      this.a = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(11,10);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
case 1:__flow$1$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public void b$return$e4b0899c(Object[] var1) {
try {

      class_4587 var3 = (class_4587)(Object)(var1[0]);
      float var2 = (Float)(Object)(var1[1]);
      long var4 = (Long)(Object)(var1[2]);
      var4 = b ^ var4;
      long var6 = var4 ^ 2293998361699L;
      long var8 = var4 ^ 81881830522294L;
      S0177 var10 = __indy$3(
         new Object[]{
            this.a,
            __indy$0(17257, 1611999235350176300L ^ var4),
            class_757.class,
            __indy$1(-2887183004536722774L, var4),
            new Class[]{class_4587.class, __indy$2(-2888654382806939194L, var4)},
            var8
         },
         -2888969585381057068L,
         var4
      );
      __indy$5(var10, new Object[]{new Object[]{var3, __indy$4(var2, -2888739628544902035L, var4)}, var6}, -2888549668081926177L, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite b(Lookup var0, String var1, MethodType var2) {
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
         throw new RuntimeException("dev/zprestige/prestige/H" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class b(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = a(var0, var2);
      Object var6 = f[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(g[var4]);
            f[var4] = var5;
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
      Object var5 = f[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = g[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            f[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  f[var4] = var13;
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
            var12 = b(143924879746194L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void c$return$e4b0899c(Object[] var1) {
try {

      class_4587 var3 = (class_4587)(Object)(var1[0]);
      float var2 = (Float)(Object)(var1[1]);
      long var4 = (Long)(Object)(var1[2]);
      var4 = b ^ var4;
      long var6 = var4 ^ 87953996652418L;
      long var8 = var4 ^ 8318934646359L;
      S0177 var10 = __indy$10(
         new Object[]{
            this.a,
            __indy$7(8397, 7110762972689351264L ^ var4),
            class_757.class,
            __indy$8(-3166057257917732533L, var4),
            new Class[]{class_4587.class, __indy$9(-3168214388257207769L, var4)},
            var8
         },
         -3167969391266800075L,
         var4
      );
      __indy$12(var10, new Object[]{new Object[]{var3, __indy$11(var2, -3168159791403778164L, var4)}, var6}, -3168138808845108162L, var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = f[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = g[var4];
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
            f[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(143924879746194L, 0L);
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
                  f[var4] = var19;
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
            var23 = b(143924879746194L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      f[0] = "1z+r5,'z.(&;01-.*/!v:9a>\u0004";
      f[1] = "vN#\u000bV\u007f\u0003n(\u0004G0b`#\u000fCj\u0016";
      f[2] = ".(\u0014Z\u001f\u0016%'\u0005\u0015~\u0018.,\u0001O";
      f[3] = "- t]Q\u001a; q\u0007B\r,kr\u0001N\u0019=,e\u0016\u0005\t\u001f";
      f[4] = "|gAys`\tGJvb/hIA}fu\u001c";
      f[5] = "E\u0005FUc7N\nW\u001a\u001b4F\u0000";
      f[6] = "8Q2c\u001f\u001f3^#,r\u001f3C7";
      f[7] = "Q<\t\u0000NtZ3\u0018O&tT<\u000b";
      f[8] = float.class;
      g[8] = "java/lang/Float";
      f[9] = "\u0015d\u0007\r[a\u0003d\u0002WHv\u0014/\u0001QDb\u0005h\u0016F\u000fr9";
      f[10] = "Oq)lH':Q\"cYh[_)h]2/";
      f[11] = "4GrF7-\"Gw\u001c$:5\ft\u001a(.$Kc\rc>\u0007";
      f[12] = "8R\"`\u0013~Mr)o\u00021,|\"d\u0006kX";
      f[13] = "D\u0014\u0012\u0016i.O\u001b\u0003Y\u0005-A\u0019\u0001\u0016)";
      f[14] = boolean.class;
      g[14] = "java/lang/Boolean";
      f[15] = "0&&eXZ0;y2dmZ\u0004VUXL1;vi\u0003Hv ";
      f[16] = "cI\u000f\f[W/N\u001fP<w\u000f-;l\u0000Vd\u0012\u001bP[R#\t";
      f[17] = "\u007f|F\u001ew53{VB\u00107+$B\u0013W'B~\u0006\u0003 (-\"\u0003\u0007*I\u007f|F\u001ew53{VB\u0010";
      f[18] = "\u001c7?=|A\u0018u.:LG\"}#6*VG\"o\"%.\u001b#j#rBN&8xL";
      f[19] = "\u0002=U\u0015\u0016q\t4_\u0011p\\b\u000br*L}\t4R\u0016\u0017yN/";
      f[20] = "\u000fJ'+4\u000fY\u0013 o_\u001d?A4!9\fZ\u001ex56t\u0001\u000b(*o\u001b\u0005I9-_";
      f[21] = "8?b-O^1(n\u0015G\"9$usVGfha|.\u001bgm`+BNb?;\u0015";
      f[22] = "o\u0011/fNZ#\u0016?:)H6J?zwO6P;\u0006\u0017\u001f/\u001d7iK\u001a+\u0017V";
      f[23] = "\u001bjdy6Z[hn|YW!f{b?ED97v0=\u0010<m*8A\u0019+a\u0012";
      f[24] = "\u001a\u0005\u0007#f\u0011\u0011\f\r'\u0000\u001eB\u000f\u0010qG\u000e+Z\u001da;\u0005\u001aV\u0015%q`\u001a\u0005\u0007#f\u0011\u0011\f\r'\u0000";
      f[25] = "_\u0006ks3\u0006T\u000fawU\u001d\t\u000fe!<\u00110\u0001e18w_\u0018|w0FS\u00108=U";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_4603 a$return$28c81a9(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = b ^ var2;
      long var4 = var2 ^ 127608699040577L;
      long var6 = var2 ^ 1683574152887L;
      S0120 var8 = __indy$14(
         new Object[]{this.a, __indy$13(13904, 5882422063623759586L ^ var2), class_757.class, class_4603.class, var6}, 7356595190496270164L, var2
      );
      return (class_4603)(Object)(__indy$15(var8, new Object[]{var4}, 7356450632827126209L, var2));
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_9920 a$return$2d45b70(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = b ^ var2;
      long var4 = var2 ^ 135467763023412L;
      long var6 = var2 ^ 16208134886338L;
      S0120 var8 = __indy$17(
         new Object[]{this.a, __indy$16(20867, 6909610700813569096L ^ var2), class_757.class, class_9920.class, var6}, -1485361716699522527L, var2
      );
      return (class_9920)(Object)(__indy$18(var8, new Object[]{var4}, -1485532680214655820L, var2));
   
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

   public static String a(int var0, long var1) {
try {

      int var5 = var0 ^ (int)(var1 & 32767L) ^ 13993;
      if (d[var5] == null) {
         Object[] var4;
         try {
            Long var3 = Thread.currentThread().threadId();
            var4 = (Object[])(Object)(e.get(var3));
            if (var4 == null) {
               var4 = new Object[]{Cipher.getInstance("DES/CBC/PKCS5Padding"), SecretKeyFactory.getInstance("DES"), new IvParameterSpec(new byte[8])};
               e.put(var3, var4);
            }
         } catch (Exception var10) {
            throw new RuntimeException("dev/zprestige/prestige/H", var10);
         }

         byte[] var6 = new byte[8];
         var6[0] = (byte)(var1 >>> 56);

         for (int var7 = 1; var7 < 8; var7++) {
            var6[var7] = (byte)(var1 << var7 * 8 >>> 56);
         }

         DESKeySpec var11 = new DESKeySpec(var6);
         SecretKey var8 = ((SecretKeyFactory)(Object)(var4[1])).generateSecret(var11);
         ((Cipher)(Object)(var4[0])).init(2, var8, (IvParameterSpec)(Object)(var4[2]));
         byte[] var9 = c[var5].getBytes("ISO-8859-1");
         d[var5] = a(((Cipher)(Object)(var4[0])).doFinal(var9));
      }

      return d[var5];
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = a(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$19(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'M' && var8 != 'W' && var8 != 208 && var8 != 198) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 213) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'z') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'M') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'W') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 208) {
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

   public void a$return$e4b0899c(Object[] var1) {
try {

      class_4184 var2 = (class_4184)(Object)(var1[0]);
      float var3 = (Float)(Object)(var1[1]);
      Matrix4f var6 = (Matrix4f)(Object)(var1[2]);
      long var4 = (Long)(Object)(var1[3]);
      var4 = b ^ var4;
      long var7 = var4 ^ 119907147339212L;
      long var9 = var4 ^ 41096586638361L;
      S0177 var11 = __indy$23(
         new Object[]{
            this.a,
            __indy$20(14397, 7204779281828876500L ^ var4),
            class_757.class,
            __indy$21(-7043131452169231611L, var4),
            new Class[]{class_4184.class, __indy$22(-7041906621232742295L, var4), Matrix4f.class},
            var9
         },
         -7041590842109389701L,
         var4
      );
      __indy$25(var11, new Object[]{new Object[]{var2, __indy$24(var3, -7041855733756463678L, var4), var6}, var7}, -7041764523111467408L, var4);
   
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

   public boolean a$return$e4b089a0(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = b ^ var2;
      long var4 = var2 ^ 5108398033278L;
      long var6 = var2 ^ 124441035793544L;
      S0120 var8 = __indy$28(
         new Object[]{this.a, __indy$26(16021, 45628999527617557L ^ var2), class_757.class, __indy$27(6640987132566488681L, var2), var6},
         6640717024421850475L,
         var2
      );
      return __indy$30((Boolean)(Object)(__indy$29(var8, new Object[]{var4}, 6640606533899451390L, var2)), 6641451955002285829L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_759 a$return$c647f34d(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = b ^ var2;
      long var4 = var2 ^ 93564304421597L;
      long var6 = var2 ^ 35804606570283L;
      S0120 var8 = __indy$32(
         new Object[]{this.a, __indy$31(27237, 7419348874552834884L ^ var2), class_757.class, class_759.class, var6}, -6373788303204985144L, var2
      );
      return (class_759)(Object)(__indy$33(var8, new Object[]{var4}, -6373862548368392099L, var2));
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_765 a$return$c647f692(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = b ^ var2;
      long var4 = var2 ^ 137567065185506L;
      long var6 = var2 ^ 9436579393812L;
      S0120 var8 = __indy$35(
         new Object[]{this.a, __indy$34(29131, 8323338317053300436L ^ var2), class_757.class, class_765.class, var6}, -3912245665014466313L, var2
      );
      return (class_765)(Object)(__indy$36(var8, new Object[]{var4}, -3912407836340294046L, var2));
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public float a$return$e4b0898c(Object[] var1) {
try {

      class_4184 var3 = (class_4184)(Object)(var1[0]);
      float var2 = (Float)(Object)(var1[1]);
      boolean var4 = (Boolean)(Object)(var1[2]);
      long var5 = (Long)(Object)(var1[3]);
      var5 = b ^ var5;
      long var7 = var5 ^ 61907606247941L;
      long var9 = var5 ^ 123297664596944L;
      S0177 var11 = __indy$41(
         new Object[]{
            this.a,
            __indy$37(10380, 7905415737313775534L ^ var5),
            class_757.class,
            __indy$38(-3346374088221339744L, var5),
            new Class[]{class_4184.class, __indy$39(-3346374088221339744L, var5), __indy$40(-3346245759206798385L, var5)},
            var9
         },
         -3346619636092216398L,
         var5
      );
      return __indy$45(
         (Float)(Object)(__indy$44(
            var11,
            new Object[]{new Object[]{var3, __indy$42(var2, -3346428754868520437L, var5), __indy$43(var4, -3346898894904631305L, var5)}, var7},
            -3346186366811407943L,
            var5
         )),
         -3346509750972166835L,
         var5
      );
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public Matrix4f a$return$910c513a(Object[] var1) {
try {

      float var2 = (Float)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = b ^ var3;
      long var5 = var3 ^ 80898575585938L;
      long var7 = var3 ^ 2186931254087L;
      S0177 var9 = __indy$48(
         new Object[]{
            this.a, __indy$46(6429, 2868936067356440226L ^ var3), class_757.class, Matrix4f.class, new Class[]{__indy$47(-7415111221780445385L, var3)}, var7
         },
         -7414865683623898331L,
         var3
      );
      return (Matrix4f)(Object)(__indy$50(var9, new Object[]{new Object[]{__indy$49(var2, -7415061228863229284L, var3)}, var5}, -7415030670674742994L, var3));
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int a(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (g[var4] != null) {
         return var4;
      }

      Object var5 = f[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 36;
         case 1 -> 8;
         case 2 -> 29;
         case 3 -> 51;
         case 4 -> 19;
         case 5 -> 40;
         case 6 -> 38;
         case 7 -> 33;
         case 8 -> 20;
         case 9 -> 61;
         case 10 -> 55;
         case 11 -> 44;
         case 12 -> 54;
         case 13 -> 28;
         case 14 -> 7;
         case 15 -> 47;
         case 16 -> 9;
         case 17 -> 3;
         case 18 -> 50;
         case 19 -> 25;
         case 20 -> 56;
         case 21 -> 63;
         case 22 -> 41;
         case 23 -> 27;
         case 24 -> 42;
         case 25 -> 37;
         case 26 -> 5;
         case 27 -> 13;
         case 28 -> 32;
         case 29 -> 59;
         case 30 -> 58;
         case 31 -> 18;
         case 32 -> 24;
         case 33 -> 52;
         case 34 -> 48;
         case 35 -> 26;
         case 36 -> 43;
         case 37 -> 35;
         case 38 -> 39;
         case 39 -> 15;
         case 40 -> 46;
         case 41 -> 31;
         case 42 -> 10;
         case 43 -> 53;
         case 44 -> 45;
         case 45 -> 21;
         case 46 -> 12;
         case 47 -> 2;
         case 48 -> 62;
         case 49 -> 57;
         case 50 -> 0;
         case 51 -> 11;
         case 52 -> 1;
         case 53 -> 34;
         case 54 -> 4;
         case 55 -> 14;
         case 56 -> 16;
         case 57 -> 17;
         case 58 -> 6;
         case 59 -> 49;
         case 60 -> 60;
         case 61 -> 23;
         case 62 -> 22;
         default -> 30;
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

      g[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$51().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/H" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object a(Lookup var0, MutableCallSite var1, String var2, Object[] var3) {
try {

      int var4 = (Integer)(Object)(var3[0]);
      long var5 = (Long)(Object)(var3[1]);
      String var7 = a(var4, var5);
      MethodHandle var8 = MethodHandles.constant(String.class, var7);
      var1.setTarget(MethodHandles.dropArguments(var8, 0, int.class, long.class));
      return var7;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_4599 a$return$28c2fcd(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = b ^ var2;
      long var4 = var2 ^ 19817601884059L;
      long var6 = var2 ^ 113863591015021L;
      S0120 var8 = __indy$53(
         new Object[]{this.a, __indy$52(25862, 48487718817519972L ^ var2), class_757.class, class_4599.class, var6}, -8444946046486611058L, var2
      );
      return (class_4599)(Object)(__indy$54(var8, new Object[]{var4}, -8445029032039797477L, var2));
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static String a(byte[] var0) {
try {

      int var1 = 0;
      int var2;
      char[] var3 = new char[var2 = var0.length];

      for (int var4 = 0; var4 < var2; var4++) {
         int var5;
         if ((var5 = 255 & var0[var4]) < 192) {
            var3[var1++] = (char)var5;
         } else if (var5 < 224) {
            char var6 = (char)((char)(var5 & 31) << 6);
            byte var8 = var0[++var4];
            var6 = (char)(var6 | (char)(var8 & 63));
            var3[var1++] = var6;
         } else if (var4 < var2 - 2) {
            char var12 = (char)((char)(var5 & 15) << '\f');
            byte var9 = var0[++var4];
            var12 = (char)(var12 | (char)(var9 & 63) << 6);
            var9 = var0[++var4];
            var12 = (char)(var12 | (char)(var9 & 63));
            var3[var1++] = var12;
         }
      }

      return new String(var3, 0, var1);
   
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

   private static native String __indy$0(int var0, long var1) throws Throwable;

   private static native Class __indy$1(long var0, long var2) throws Throwable;

   private static native Class __indy$2(long var0, long var2) throws Throwable;

   private static native S0177 __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native Float __indy$4(float var0, long var1, long var3) throws Throwable;

   private static native Object __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$6() throws Throwable;

   private static native String __indy$7(int var0, long var1) throws Throwable;

   private static native Class __indy$8(long var0, long var2) throws Throwable;

   private static native Class __indy$9(long var0, long var2) throws Throwable;

   private static native S0177 __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native Float __indy$11(float var0, long var1, long var3) throws Throwable;

   private static native Object __indy$12(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String __indy$13(int var0, long var1) throws Throwable;

   private static native S0120 __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String __indy$16(int var0, long var1) throws Throwable;

   private static native S0120 __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$18(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$19(MethodHandle var0, Object[] var1) throws Throwable;

   private static native String __indy$20(int var0, long var1) throws Throwable;

   private static native Class __indy$21(long var0, long var2) throws Throwable;

   private static native Class __indy$22(long var0, long var2) throws Throwable;

   private static native S0177 __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native Float __indy$24(float var0, long var1, long var3) throws Throwable;

   private static native Object __indy$25(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String __indy$26(int var0, long var1) throws Throwable;

   private static native Class __indy$27(long var0, long var2) throws Throwable;

   private static native S0120 __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$29(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$31(int var0, long var1) throws Throwable;

   private static native S0120 __indy$32(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$33(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String __indy$34(int var0, long var1) throws Throwable;

   private static native S0120 __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$36(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String __indy$37(int var0, long var1) throws Throwable;

   private static native Class __indy$38(long var0, long var2) throws Throwable;

   private static native Class __indy$39(long var0, long var2) throws Throwable;

   private static native Class __indy$40(long var0, long var2) throws Throwable;

   private static native S0177 __indy$41(Object var0, long var1, long var3) throws Throwable;

   private static native Float __indy$42(float var0, long var1, long var3) throws Throwable;

   private static native Boolean __indy$43(boolean var0, long var1, long var3) throws Throwable;

   private static native Object __indy$44(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native float __indy$45(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$46(int var0, long var1) throws Throwable;

   private static native Class __indy$47(long var0, long var2) throws Throwable;

   private static native S0177 __indy$48(Object var0, long var1, long var3) throws Throwable;

   private static native Float __indy$49(float var0, long var1, long var3) throws Throwable;

   private static native Object __indy$50(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$51() throws Throwable;

   private static native String __indy$52(int var0, long var1) throws Throwable;

   private static native S0120 __indy$53(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$54(Object var0, Object var1, long var2, long var4) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-6384230885601424915L,2);f.pc=1;return;}
case 1:{f.push(7087790305764720363L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(107453876227534L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0008.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(26,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0008.class,"f",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(26,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0008.class,"g",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0008.a$return$9b75();f.pc=15;return;}
case 15:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.util.HashMap.class),1);f.pc=16;return;}
case 16:{f.rearrange(89);f.pc=17;return;}
case 17:{f.push(13,1);f.pc=18;return;}
case 18:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{int.class},new Object[]{dev.zprestige.sourceview.FlowFrame.i(a0)});f.pc=19;return;}
case 19:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0008.class,"e",((java.util.Map)(Object)(f.pop())));f.pc=20;return;}
case 20:{f.push(dev.zprestige.sourceview.S0008.b,2);f.pc=21;return;}
case 21:{f.push(47436926299525L,2);f.pc=22;return;}
case 22:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=23;return;}
case 23:{f.locals[0]=f.pop();f.pc=24;return;}
case 24:{f.push("DES/CBC/PKCS5Padding",1);f.pc=25;return;}
case 25:{Object a0=f.pop();f.push(javax.crypto.Cipher.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=26;return;}
case 26:{f.rearrange(89);f.pc=27;return;}
case 27:{f.locals[2]=f.pop();f.pc=28;return;}
case 28:{f.push(2,1);f.pc=29;return;}
case 29:{f.push("DES",1);f.pc=30;return;}
case 30:{Object a0=f.pop();f.push(javax.crypto.SecretKeyFactory.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=31;return;}
case 31:{f.push(8,1);f.pc=32;return;}
case 32:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=33;return;}
case 33:{f.rearrange(89);f.pc=34;return;}
case 34:{f.push(0,1);f.pc=35;return;}
case 35:{f.push(f.locals[0],2);f.pc=36;return;}
case 36:{f.push(56,1);f.pc=37;return;}
case 37:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=38;return;}
case 38:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=39;return;}
case 39:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=40;return;}
case 40:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=41;return;}
case 41:{f.push(1,1);f.pc=42;return;}
case 42:{f.locals[3]=f.pop();f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.push(f.locals[3],1);f.pc=46;return;}
case 46:{f.push(8,1);f.pc=47;return;}
case 47:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=62;return;}f.pc=48;return;}
case 48:{f.rearrange(89);f.pc=49;return;}
case 49:{f.push(f.locals[3],1);f.pc=50;return;}
case 50:{f.push(f.locals[0],2);f.pc=51;return;}
case 51:{f.push(f.locals[3],1);f.pc=52;return;}
case 52:{f.push(8,1);f.pc=53;return;}
case 53:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a*b,1);f.pc=54;return;}
case 54:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a<<b,2);f.pc=55;return;}
case 55:{f.push(56,1);f.pc=56;return;}
case 56:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=57;return;}
case 57:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=58;return;}
case 58:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=59;return;}
case 59:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=60;return;}
case 60:{f.locals[3]=dev.zprestige.sourceview.FlowFrame.i(f.locals[3])+(1);f.pc=61;return;}
case 61:{f.pc=43;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.DESKeySpec.class),1);f.pc=65;return;}
case 65:{f.rearrange(90);f.pc=66;return;}
case 66:{f.rearrange(95);f.pc=67;return;}
case 67:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=68;return;}
case 68:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.SecretKeyFactory)(Object)receiver).generateSecret(((java.security.spec.KeySpec)(Object)(a0))),1);f.pc=69;return;}
case 69:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.IvParameterSpec.class),1);f.pc=70;return;}
case 70:{f.rearrange(89);f.pc=71;return;}
case 71:{f.push(8,1);f.pc=72;return;}
case 72:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=73;return;}
case 73:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=74;return;}
case 74:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();((javax.crypto.Cipher)(Object)receiver).init(dev.zprestige.sourceview.FlowFrame.i(a0),((java.security.Key)(Object)(a1)),((java.security.spec.AlgorithmParameterSpec)(Object)(a2)));f.pc=75;return;}
case 75:{f.push(11,1);f.pc=76;return;}
case 76:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=77;return;}
case 77:{f.locals[9]=f.pop();f.pc=78;return;}
case 78:{f.push(0,1);f.pc=79;return;}
case 79:{f.locals[7]=f.pop();f.pc=80;return;}
case 80:{f.push("\u00ea\u00a2\u00a3-\u00dd\u0087\u00cf\u001c\u0086\u0002\u00f9\u0089\u00c8\u00a5\u00df\u001c(j\u00d9\u0003\u00fbB\u00db\u00fco;\u0082\n\u00c5\u0098\u00ca\u0084\u00f7\u00891X\u0080Yi1\u00ecI\u00c2\u0002)\u00b9\u00e7\u0088\u00b6w\u00dd\u008be\u00ff\u00f8.\u00f6\u0018\u00a8\u0086\u00bb\u001bG\u00a5\u0088\u00bb\u00cb\u0080O\u0087\u00c8bA\u0005:\u00fc\u00a2K\u0002\u0013%]\u0010jvnss\u00a0\u00c0\u00e5\u00e6\u00e7wC\u00ff\u00cf\u00f3j(\u00d4\u0013\u00db\u0007^\u0097\u0014;\u001d\u00c5E\u00b2\u009brdx\u00ba\u00c0%\u00ae\n\u0090\u00b1\u00adS\u001e\u009fR\u00c2p\u00d4\u00f9\u00bc\u009e$rI\u0083\u00d2$\u0010r\r\u00ca\u0014\u00c6\u00cb\u00f7A\u00a6.\u00b8c%\u00a4\u00e3?0\u0087\u009aO\u00fc\u0081\u008cp\u00ad\u0088\u00f3\u00e2F\u00df\u0005d\u00f9\u00cc[\u00d1x \u0018\u00fb\u0000\u0013N\u0012\u00f6\u0010\u00c2\u0084bNq\u00f4s\u00f2P\u00cf\u0005\u0000_\u00ee\u0080\u0009\u0010g\u0084(\u00b0_\u00db\u008d\u009f\u009b\u00a1\u00e7\u00ab\u001d\u00a4\u00b4\u00dfK\u00cf\u0096\u0093f\u0005_\u0005\u00f5Ti\u0087+\u00cb\u0001\u0098\u0017\u00a6e\u00b0\u00d5\u00c3\u00a9\u00fa\u00ff\u00a4\u00e3\u0010\u00c4\u0087\u00c3\u00cb\u00b7<pO\u00c1\u00a7#\u00ef.\u00d9\u00f6\u0095",1);f.pc=81;return;}
case 81:{f.rearrange(89);f.pc=82;return;}
case 82:{f.locals[6]=f.pop();f.pc=83;return;}
case 83:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=84;return;}
case 84:{f.locals[8]=f.pop();f.pc=85;return;}
case 85:{f.push(16,1);f.pc=86;return;}
case 86:{f.locals[5]=f.pop();f.pc=87;return;}
case 87:{f.push(-1,1);f.pc=88;return;}
case 88:{f.locals[4]=f.pop();f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.locals[4]=dev.zprestige.sourceview.FlowFrame.i(f.locals[4])+(1);f.pc=92;return;}
case 92:{f.push(f.locals[6],1);f.pc=93;return;}
case 93:{f.push(f.locals[4],1);f.pc=94;return;}
case 94:{f.rearrange(89);f.pc=95;return;}
case 95:{f.push(f.locals[5],1);f.pc=96;return;}
case 96:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=97;return;}
case 97:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).substring(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1)),1);f.pc=98;return;}
case 98:{f.push(-1,1);f.pc=99;return;}
case 99:{f.pc=170;return;}
case 100:{f.pc=101;return;}
case 101:{f.pc=102;return;}
case 102:{f.push(f.locals[9],1);f.pc=103;return;}
case 103:{f.rearrange(95);f.pc=104;return;}
case 104:{f.push(f.locals[7],1);f.pc=105;return;}
case 105:{f.locals[7]=dev.zprestige.sourceview.FlowFrame.i(f.locals[7])+(1);f.pc=106;return;}
case 106:{f.rearrange(95);f.pc=107;return;}
case 107:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=108;return;}
case 108:{f.push(f.locals[4],1);f.pc=109;return;}
case 109:{f.push(f.locals[5],1);f.pc=110;return;}
case 110:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=111;return;}
case 111:{f.rearrange(89);f.pc=112;return;}
case 112:{f.locals[4]=f.pop();f.pc=113;return;}
case 113:{f.push(f.locals[8],1);f.pc=114;return;}
case 114:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=120;return;}f.pc=115;return;}
case 115:{f.push(f.locals[6],1);f.pc=116;return;}
case 116:{f.push(f.locals[4],1);f.pc=117;return;}
case 117:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).charAt(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=118;return;}
case 118:{f.locals[5]=f.pop();f.pc=119;return;}
case 119:{f.pc=89;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.push("\u008d\u00eb\u00e7\u0093lqg\u00ae\u00bel\"c\u0007b\u00ef\u0014<\u00ad\u0090\u0015w\u008b\u00f6Dp\u00d6/&\u00da&H\u00c70\u00dc\u00e6\u0016jM\u0089\u00b6\u0096\u0089_\u00afv\u0083\u0091\u009a\u0098\u0004)$w \u00a2\u00d0\u00a0\u00f4+\u00a9\u00b3\u00b1\u001b\u00c4\u00d94tz\u00dc\\\u00a5S\u001b\u001d\u001e[)?\u00a4Y\u009b",1);f.pc=123;return;}
case 123:{f.rearrange(89);f.pc=124;return;}
case 124:{f.locals[6]=f.pop();f.pc=125;return;}
case 125:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=126;return;}
case 126:{f.locals[8]=f.pop();f.pc=127;return;}
case 127:{f.push(32,1);f.pc=128;return;}
case 128:{f.locals[5]=f.pop();f.pc=129;return;}
case 129:{f.push(-1,1);f.pc=130;return;}
case 130:{f.locals[4]=f.pop();f.pc=131;return;}
case 131:{f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.locals[4]=dev.zprestige.sourceview.FlowFrame.i(f.locals[4])+(1);f.pc=134;return;}
case 134:{f.push(f.locals[6],1);f.pc=135;return;}
case 135:{f.push(f.locals[4],1);f.pc=136;return;}
case 136:{f.rearrange(89);f.pc=137;return;}
case 137:{f.push(f.locals[5],1);f.pc=138;return;}
case 138:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=139;return;}
case 139:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).substring(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1)),1);f.pc=140;return;}
case 140:{f.push(0,1);f.pc=141;return;}
case 141:{f.pc=170;return;}
case 142:{f.pc=143;return;}
case 143:{f.pc=144;return;}
case 144:{f.push(f.locals[9],1);f.pc=145;return;}
case 145:{f.rearrange(95);f.pc=146;return;}
case 146:{f.push(f.locals[7],1);f.pc=147;return;}
case 147:{f.locals[7]=dev.zprestige.sourceview.FlowFrame.i(f.locals[7])+(1);f.pc=148;return;}
case 148:{f.rearrange(95);f.pc=149;return;}
case 149:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=150;return;}
case 150:{f.push(f.locals[4],1);f.pc=151;return;}
case 151:{f.push(f.locals[5],1);f.pc=152;return;}
case 152:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=153;return;}
case 153:{f.rearrange(89);f.pc=154;return;}
case 154:{f.locals[4]=f.pop();f.pc=155;return;}
case 155:{f.push(f.locals[8],1);f.pc=156;return;}
case 156:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=162;return;}f.pc=157;return;}
case 157:{f.push(f.locals[6],1);f.pc=158;return;}
case 158:{f.push(f.locals[4],1);f.pc=159;return;}
case 159:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).charAt(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=160;return;}
case 160:{f.locals[5]=f.pop();f.pc=161;return;}
case 161:{f.pc=131;return;}
case 162:{f.pc=163;return;}
case 163:{f.pc=164;return;}
case 164:{f.push(f.locals[9],1);f.pc=165;return;}
case 165:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0008.class,"c",((java.lang.String[])(Object)(f.pop())));f.pc=166;return;}
case 166:{f.push(11,1);f.pc=167;return;}
case 167:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=168;return;}
case 168:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0008.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=169;return;}
case 169:{f.pc=184;return;}
case 170:{f.pc=171;return;}
case 171:{f.pc=172;return;}
case 172:{f.rearrange(95);f.pc=173;return;}
case 173:{f.push("ISO-8859-1",1);f.pc=174;return;}
case 174:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).getBytes(((java.lang.String)(Object)(a0))),1);f.pc=175;return;}
case 175:{f.push(f.locals[2],1);f.pc=176;return;}
case 176:{f.rearrange(95);f.pc=177;return;}
case 177:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.Cipher)(Object)receiver).doFinal(((byte[])(Object)(a0))),1);f.pc=178;return;}
case 178:{f.locals[10]=f.pop();f.pc=179;return;}
case 179:{f.push(f.locals[10],1);f.pc=180;return;}
case 180:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0008.a(((byte[])(Object)(a0))),1);f.pc=181;return;}
case 181:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).intern(),1);f.pc=182;return;}
case 182:{f.rearrange(95);f.pc=183;return;}
case 183:{switch(dev.zprestige.sourceview.FlowFrame.i(f.pop())){case 0:f.pc=142;break;default:f.pc=100;}return;}
case 184:{f.pc=185;return;}
case 185:{f.pc=186;return;}
case 186:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
