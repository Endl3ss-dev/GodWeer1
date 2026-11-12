package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public final class S0593 extends EnumBase {
   public static S0593 LEGACY;
   public static S0593 POP;
   public static S0593[] a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   public S0593(String var1, int var2)  throws Throwable {
      super(var1, var2);
try {

   
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


   public static S0593[] values() {
try {

      return (S0593[])(Object)(a.clone());
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0593 valueOf(String var0, long var1) {
try {

      var1 = b ^ var1;
      return (S0593)(Object)(__indy$1(S0593.class, var0, -6163955399937706483L, var1));
   
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
            var12 = b(483290639318187L, 0L);
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
            var23 = b(483290639318187L, 0L);
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
            var23 = b(483290639318187L, 0L);
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

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'U' && var8 != 'C' && var8 != 186 && var8 != 214) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 200) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 205) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'U') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'C') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 186) {
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
      return __indy$2(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$3().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/s" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0593[] a$return$4b619c3d(Object[] var0) {
try {

      return new S0593[]{LEGACY, POP};
   
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
         case 0 -> 13;
         case 1 -> 10;
         case 2 -> 16;
         case 3 -> 55;
         case 4 -> 59;
         case 5 -> 31;
         case 6 -> 1;
         case 7 -> 33;
         case 8 -> 6;
         case 9 -> 39;
         case 10 -> 62;
         case 11 -> 3;
         case 12 -> 56;
         case 13 -> 8;
         case 14 -> 35;
         case 15 -> 34;
         case 16 -> 5;
         case 17 -> 43;
         case 18 -> 20;
         case 19 -> 41;
         case 20 -> 19;
         case 21 -> 51;
         case 22 -> 29;
         case 23 -> 11;
         case 24 -> 0;
         case 25 -> 28;
         case 26 -> 57;
         case 27 -> 42;
         case 28 -> 26;
         case 29 -> 49;
         case 30 -> 9;
         case 31 -> 53;
         case 32 -> 23;
         case 33 -> 54;
         case 34 -> 38;
         case 35 -> 4;
         case 36 -> 63;
         case 37 -> 48;
         case 38 -> 36;
         case 39 -> 12;
         case 40 -> 14;
         case 41 -> 2;
         case 42 -> 7;
         case 43 -> 22;
         case 44 -> 25;
         case 45 -> 30;
         case 46 -> 58;
         case 47 -> 47;
         case 48 -> 17;
         case 49 -> 61;
         case 50 -> 44;
         case 51 -> 52;
         case 52 -> 15;
         case 53 -> 50;
         case 54 -> 40;
         case 55 -> 32;
         case 56 -> 21;
         case 57 -> 60;
         case 58 -> 18;
         case 59 -> 24;
         case 60 -> 45;
         case 61 -> 46;
         case 62 -> 37;
         default -> 27;
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

      c[0] = "=\u0011Uk\b\u00196\u001eD$c\u001b\"\u001d";
      c[1] = "m'&'$\u0014f(7hI\u0014f5#";
      c[2] = "-!\u0007%\u0013\r&.\u0016jn\u00155)\u001f#";
      c[3] = ":\u0002\u0000\u0000\u007f*,\u0002\u0005Zl=;I\u0006\\`)*\u000e\u0011K+)";
      c[4] = "V$N\u0001%\u0018#\u0004E\u000e4WB\nN\u00050\r6";
      c[5] = "QBAnA\u0007p~WnD]ci@%G[o}QbPL$}\u001e";
      c[6] = "9N/\u0007ua2A>H\u0014o9J:\u0012";
      c[7] = "d\u0017Kz/1b\u0016P.I?^CR*w;gFL<qVe\u0018R.694I^{I";
      c[8] = "\u007f]0~\u0013\u0003!\u0007h\u001b]\fz\u0011b\\Me'WbaB\u001d\"\u0001a|#\\q\u001ef#A\bz\u001c}\u001bBT!\t?}\u001c\u000eyl";
   
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

   private static native S0593[] __indy$0(Object var0, long var1, long var3) throws Throwable;

   private static native Enum __indy$1(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$2(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$3() throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-613117964735395815L,2);f.pc=1;return;}
case 1:{f.push(1903164155554907847L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(100865081599454L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0593.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0593.b,2);f.pc=9;return;}
case 9:{f.push(78984983930588L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[9]=f.pop();f.pc=12;return;}
case 12:{f.push(9,1);f.pc=13;return;}
case 13:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=14;return;}
case 14:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0593.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=15;return;}
case 15:{f.push(9,1);f.pc=16;return;}
case 16:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=17;return;}
case 17:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0593.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=18;return;}
case 18:{dev.zprestige.sourceview.S0593.a$return$9b75();f.pc=19;return;}
case 19:{f.push("DES/CBC/PKCS5Padding",1);f.pc=20;return;}
case 20:{Object a0=f.pop();f.push(javax.crypto.Cipher.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=21;return;}
case 21:{f.rearrange(89);f.pc=22;return;}
case 22:{f.locals[1]=f.pop();f.pc=23;return;}
case 23:{f.push(2,1);f.pc=24;return;}
case 24:{f.push("DES",1);f.pc=25;return;}
case 25:{Object a0=f.pop();f.push(javax.crypto.SecretKeyFactory.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=26;return;}
case 26:{f.push(8,1);f.pc=27;return;}
case 27:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=28;return;}
case 28:{f.rearrange(89);f.pc=29;return;}
case 29:{f.push(0,1);f.pc=30;return;}
case 30:{f.push(f.locals[9],2);f.pc=31;return;}
case 31:{f.push(56,1);f.pc=32;return;}
case 32:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=33;return;}
case 33:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=34;return;}
case 34:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=35;return;}
case 35:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=36;return;}
case 36:{f.push(1,1);f.pc=37;return;}
case 37:{f.locals[2]=f.pop();f.pc=38;return;}
case 38:{f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.push(f.locals[2],1);f.pc=41;return;}
case 41:{f.push(8,1);f.pc=42;return;}
case 42:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=57;return;}f.pc=43;return;}
case 43:{f.rearrange(89);f.pc=44;return;}
case 44:{f.push(f.locals[2],1);f.pc=45;return;}
case 45:{f.push(f.locals[9],2);f.pc=46;return;}
case 46:{f.push(f.locals[2],1);f.pc=47;return;}
case 47:{f.push(8,1);f.pc=48;return;}
case 48:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a*b,1);f.pc=49;return;}
case 49:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a<<b,2);f.pc=50;return;}
case 50:{f.push(56,1);f.pc=51;return;}
case 51:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=52;return;}
case 52:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=53;return;}
case 53:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=54;return;}
case 54:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=55;return;}
case 55:{f.locals[2]=dev.zprestige.sourceview.FlowFrame.i(f.locals[2])+(1);f.pc=56;return;}
case 56:{f.pc=38;return;}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.DESKeySpec.class),1);f.pc=60;return;}
case 60:{f.rearrange(90);f.pc=61;return;}
case 61:{f.rearrange(95);f.pc=62;return;}
case 62:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=63;return;}
case 63:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.SecretKeyFactory)(Object)receiver).generateSecret(((java.security.spec.KeySpec)(Object)(a0))),1);f.pc=64;return;}
case 64:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.IvParameterSpec.class),1);f.pc=65;return;}
case 65:{f.rearrange(89);f.pc=66;return;}
case 66:{f.push(8,1);f.pc=67;return;}
case 67:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=68;return;}
case 68:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=69;return;}
case 69:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();((javax.crypto.Cipher)(Object)receiver).init(dev.zprestige.sourceview.FlowFrame.i(a0),((java.security.Key)(Object)(a1)),((java.security.spec.AlgorithmParameterSpec)(Object)(a2)));f.pc=70;return;}
case 70:{f.push(2,1);f.pc=71;return;}
case 71:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=72;return;}
case 72:{f.locals[0]=f.pop();f.pc=73;return;}
case 73:{f.push(0,1);f.pc=74;return;}
case 74:{f.locals[6]=f.pop();f.pc=75;return;}
case 75:{f.push("\u008bo\u0005\\\u00f4\u00e8\u000fy\u0008\u00f7\u0009\u0005o\u00f9^jU",1);f.pc=76;return;}
case 76:{f.rearrange(89);f.pc=77;return;}
case 77:{f.locals[5]=f.pop();f.pc=78;return;}
case 78:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=79;return;}
case 79:{f.locals[7]=f.pop();f.pc=80;return;}
case 80:{f.push(8,1);f.pc=81;return;}
case 81:{f.locals[4]=f.pop();f.pc=82;return;}
case 82:{f.push(-1,1);f.pc=83;return;}
case 83:{f.locals[3]=f.pop();f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.pc=86;return;}
case 86:{f.locals[3]=dev.zprestige.sourceview.FlowFrame.i(f.locals[3])+(1);f.pc=87;return;}
case 87:{f.push(f.locals[5],1);f.pc=88;return;}
case 88:{f.push(f.locals[3],1);f.pc=89;return;}
case 89:{f.rearrange(89);f.pc=90;return;}
case 90:{f.push(f.locals[4],1);f.pc=91;return;}
case 91:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=92;return;}
case 92:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).substring(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1)),1);f.pc=93;return;}
case 93:{f.push(-1,1);f.pc=94;return;}
case 94:{f.pc=118;return;}
case 95:{f.pc=96;return;}
case 96:{f.pc=97;return;}
case 97:{f.push(f.locals[0],1);f.pc=98;return;}
case 98:{f.rearrange(95);f.pc=99;return;}
case 99:{f.push(f.locals[6],1);f.pc=100;return;}
case 100:{f.locals[6]=dev.zprestige.sourceview.FlowFrame.i(f.locals[6])+(1);f.pc=101;return;}
case 101:{f.rearrange(95);f.pc=102;return;}
case 102:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=103;return;}
case 103:{f.push(f.locals[3],1);f.pc=104;return;}
case 104:{f.push(f.locals[4],1);f.pc=105;return;}
case 105:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=106;return;}
case 106:{f.rearrange(89);f.pc=107;return;}
case 107:{f.locals[3]=f.pop();f.pc=108;return;}
case 108:{f.push(f.locals[7],1);f.pc=109;return;}
case 109:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=115;return;}f.pc=110;return;}
case 110:{f.push(f.locals[5],1);f.pc=111;return;}
case 111:{f.push(f.locals[3],1);f.pc=112;return;}
case 112:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).charAt(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=113;return;}
case 113:{f.locals[4]=f.pop();f.pc=114;return;}
case 114:{f.pc=84;return;}
case 115:{f.pc=116;return;}
case 116:{f.pc=117;return;}
case 117:{f.pc=133;return;}
case 118:{f.pc=119;return;}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.rearrange(95);f.pc=121;return;}
case 121:{f.push("ISO-8859-1",1);f.pc=122;return;}
case 122:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).getBytes(((java.lang.String)(Object)(a0))),1);f.pc=123;return;}
case 123:{f.push(f.locals[1],1);f.pc=124;return;}
case 124:{f.rearrange(95);f.pc=125;return;}
case 125:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.Cipher)(Object)receiver).doFinal(((byte[])(Object)(a0))),1);f.pc=126;return;}
case 126:{f.locals[8]=f.pop();f.pc=127;return;}
case 127:{f.push(f.locals[8],1);f.pc=128;return;}
case 128:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0593.a(((byte[])(Object)(a0))),1);f.pc=129;return;}
case 129:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).intern(),1);f.pc=130;return;}
case 130:{f.rearrange(95);f.pc=131;return;}
case 131:{f.rearrange(87);f.pc=132;return;}
case 132:{f.pc=95;return;}
case 133:{f.pc=134;return;}
case 134:{f.pc=135;return;}
case 135:{f.pc=136;return;}
case 136:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0593.class),1);f.pc=137;return;}
case 137:{f.rearrange(89);f.pc=138;return;}
case 138:{f.push(f.locals[0],1);f.pc=139;return;}
case 139:{f.push(1,1);f.pc=140;return;}
case 140:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=141;return;}
case 141:{f.push(0,1);f.pc=142;return;}
case 142:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.lang.String.class,int.class},new Object[]{((java.lang.String)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1)});f.pc=143;return;}
case 143:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0593.class,"LEGACY",((dev.zprestige.sourceview.S0593)(Object)(f.pop())));f.pc=144;return;}
case 144:{f.pc=145;return;}
case 145:{f.pc=146;return;}
case 146:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0593.class),1);f.pc=147;return;}
case 147:{f.rearrange(89);f.pc=148;return;}
case 148:{f.push(f.locals[0],1);f.pc=149;return;}
case 149:{f.push(0,1);f.pc=150;return;}
case 150:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=151;return;}
case 151:{f.push(1,1);f.pc=152;return;}
case 152:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.lang.String.class,int.class},new Object[]{((java.lang.String)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1)});f.pc=153;return;}
case 153:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0593.class,"POP",((dev.zprestige.sourceview.S0593)(Object)(f.pop())));f.pc=154;return;}
case 154:{f.pc=155;return;}
case 155:{f.pc=156;return;}
case 156:{f.push(0,1);f.pc=157;return;}
case 157:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=158;return;}
case 158:{f.push(6535448617481548611L,2);f.pc=159;return;}
case 159:{f.push(f.locals[9],2);f.pc=160;return;}
case 160:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0593.__indy$0(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=161;return;}
case 161:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0593.class,"a",((dev.zprestige.sourceview.S0593[])(Object)(f.pop())));f.pc=162;return;}
case 162:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
