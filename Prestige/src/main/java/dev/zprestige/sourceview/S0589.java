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

public final class S0589 extends EnumBase {
   public static S0589 NONE;
   public static S0589 NORMAL;
   public static S0589 CHAMS;
   public static S0589 AURA;
   public static S0589 RAINBOW;
   public S0270 a;
   public static S0589[] b;
   public static long c;
   public static Object[] d;
   public static String[] e;

   public S0589(String var1, int var2, S0270 var3)  throws Throwable {
      super(var1, var2);
try {

      this.a = var3;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(19,10);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
case 1:__flow$1$1(f);break;
case 2:__flow$1$2(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static S0589[] values() {
try {

      return (S0589[])(Object)(b.clone());
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0589 valueOf(String var0, long var1) {
try {

      var1 = c ^ var1;
      return (S0589)(Object)(__indy$1(S0589.class, var0, -2428859034409521967L, var1));
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class b(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = a(var0, var2);
      Object var6 = d[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(e[var4]);
            d[var4] = var5;
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
      Object var5 = d[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = e[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            d[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  d[var4] = var13;
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
            var12 = b(462144623407146L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = d[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = e[var4];
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
            d[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(462144623407146L, 0L);
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
                  d[var4] = var19;
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
            var23 = b(462144623407146L, 0L);
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

   public S0270 a$return$99ec1c6c(Object[] var1) {
try {

      return this.a;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$2().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/o" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'g' && var8 != 'o' && var8 != 'a' && var8 != 212) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 251) {
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
            if (var8 == 'g') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'o') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'a') {
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
      return __indy$3(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int a(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (e[var4] != null) {
         return var4;
      }

      Object var5 = d[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 5;
         case 1 -> 13;
         case 2 -> 55;
         case 3 -> 26;
         case 4 -> 2;
         case 5 -> 43;
         case 6 -> 59;
         case 7 -> 19;
         case 8 -> 24;
         case 9 -> 58;
         case 10 -> 62;
         case 11 -> 54;
         case 12 -> 25;
         case 13 -> 9;
         case 14 -> 44;
         case 15 -> 30;
         case 16 -> 47;
         case 17 -> 6;
         case 18 -> 56;
         case 19 -> 33;
         case 20 -> 22;
         case 21 -> 7;
         case 22 -> 60;
         case 23 -> 48;
         case 24 -> 46;
         case 25 -> 8;
         case 26 -> 14;
         case 27 -> 51;
         case 28 -> 23;
         case 29 -> 38;
         case 30 -> 27;
         case 31 -> 12;
         case 32 -> 39;
         case 33 -> 63;
         case 34 -> 49;
         case 35 -> 4;
         case 36 -> 16;
         case 37 -> 11;
         case 38 -> 10;
         case 39 -> 21;
         case 40 -> 36;
         case 41 -> 3;
         case 42 -> 1;
         case 43 -> 18;
         case 44 -> 35;
         case 45 -> 20;
         case 46 -> 32;
         case 47 -> 52;
         case 48 -> 50;
         case 49 -> 42;
         case 50 -> 41;
         case 51 -> 15;
         case 52 -> 53;
         case 53 -> 31;
         case 54 -> 61;
         case 55 -> 17;
         case 56 -> 29;
         case 57 -> 40;
         case 58 -> 34;
         case 59 -> 37;
         case 60 -> 57;
         case 61 -> 45;
         case 62 -> 0;
         default -> 28;
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

      e[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      d[0] = "\f.\u00154\u0010>\u0007!\u0004{{<\u0013\"";
      d[1] = "\u0017\\?_o\u0011\u001cS.\u0010\u0002\u0011\u001cN:";
      d[2] = "\b\n\u0003\u0013{,\u0003\u0005\u0012\\\u00064\u0010\u0002\u001b\u0015";
      d[3] = "/YNg##9YK=04.\u0012H;< ?U_,w<";
      d[4] = "1jNE\u000eBDJEJ\u001f\r%DNA\u001bWQ";
      d[5] = "=`NC.t\u001c\\XC+.\u000fKO\b((\u0003_^O??HC\u0011";
      d[6] = "mr\u001b\u0011sKf}\n^\u0012Emv\u000e\u0004";
      d[7] = "\u0002B\u007fX&@H\bsd6\u0014\\Nl#&}\u0001\rf\u001e\"\r\u0004\f<\u000fHG\u0001Qc\u00069\u0005RWndr\fF\u000foY8FJ3";
      d[8] = "jO\u0004\u000b8o2\u0015W\u000f^8PEW\u0011!n,\u0002ZInQk\u0011L\u001c=l2\u0014\u0006\b^";
   
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

   public static S0589[] a$return$4b619bc1(Object[] var0) {
try {

      return new S0589[]{NONE, NORMAL, CHAMS, AURA, RAINBOW};
   
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

   private static native S0589[] __indy$0(Object var0, long var1, long var3) throws Throwable;

   private static native Enum __indy$1(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$2() throws Throwable;

   private static native Object __indy$3(MethodHandle var0, Object[] var1) throws Throwable;

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(9139310274121214260L,2);f.pc=1;return;}
case 1:{f.push(-4137156750468945001L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(139343310812193L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0589.class,"c",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0589.c,2);f.pc=9;return;}
case 9:{f.push(134515275331968L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[9]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[9],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(51720055191496L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[11]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(92);f.pc=18;return;}
case 18:{f.push(29600253070114L,2);f.pc=19;return;}
case 19:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=20;return;}
case 20:{f.locals[13]=f.pop();f.pc=21;return;}
case 21:{f.rearrange(92);f.pc=22;return;}
case 22:{f.push(2087398881080L,2);f.pc=23;return;}
case 23:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=24;return;}
case 24:{f.locals[15]=f.pop();f.pc=25;return;}
case 25:{f.rearrange(92);f.pc=26;return;}
case 26:{f.push(74010005649317L,2);f.pc=27;return;}
case 27:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=28;return;}
case 28:{f.locals[17]=f.pop();f.pc=29;return;}
case 29:{f.rearrange(88);f.pc=30;return;}
case 30:{f.push(9,1);f.pc=31;return;}
case 31:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=32;return;}
case 32:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0589.class,"d",((java.lang.Object[])(Object)(f.pop())));f.pc=33;return;}
case 33:{f.push(9,1);f.pc=34;return;}
case 34:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=35;return;}
case 35:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0589.class,"e",((java.lang.String[])(Object)(f.pop())));f.pc=36;return;}
case 36:{dev.zprestige.sourceview.S0589.a$return$9b75();f.pc=37;return;}
case 37:{f.push("DES/CBC/PKCS5Padding",1);f.pc=38;return;}
case 38:{Object a0=f.pop();f.push(javax.crypto.Cipher.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=39;return;}
case 39:{f.rearrange(89);f.pc=40;return;}
case 40:{f.locals[1]=f.pop();f.pc=41;return;}
case 41:{f.push(2,1);f.pc=42;return;}
case 42:{f.push("DES",1);f.pc=43;return;}
case 43:{Object a0=f.pop();f.push(javax.crypto.SecretKeyFactory.getInstance(((java.lang.String)(Object)(a0))),1);f.pc=44;return;}
case 44:{f.push(8,1);f.pc=45;return;}
case 45:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=46;return;}
case 46:{f.rearrange(89);f.pc=47;return;}
case 47:{f.push(0,1);f.pc=48;return;}
case 48:{f.push(f.locals[9],2);f.pc=49;return;}
case 49:{f.push(56,1);f.pc=50;return;}
case 50:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=51;return;}
case 51:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=52;return;}
case 52:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=53;return;}
case 53:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=54;return;}
case 54:{f.push(1,1);f.pc=55;return;}
case 55:{f.locals[2]=f.pop();f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.push(f.locals[2],1);f.pc=59;return;}
case 59:{f.push(8,1);f.pc=60;return;}
case 60:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=75;return;}f.pc=61;return;}
case 61:{f.rearrange(89);f.pc=62;return;}
case 62:{f.push(f.locals[2],1);f.pc=63;return;}
case 63:{f.push(f.locals[9],2);f.pc=64;return;}
case 64:{f.push(f.locals[2],1);f.pc=65;return;}
case 65:{f.push(8,1);f.pc=66;return;}
case 66:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a*b,1);f.pc=67;return;}
case 67:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a<<b,2);f.pc=68;return;}
case 68:{f.push(56,1);f.pc=69;return;}
case 69:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a>>>b,2);f.pc=70;return;}
case 70:{f.push((int)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=71;return;}
case 71:{f.push((byte)(dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=72;return;}
case 72:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=73;return;}
case 73:{f.locals[2]=dev.zprestige.sourceview.FlowFrame.i(f.locals[2])+(1);f.pc=74;return;}
case 74:{f.pc=56;return;}
case 75:{f.pc=76;return;}
case 76:{f.pc=77;return;}
case 77:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.DESKeySpec.class),1);f.pc=78;return;}
case 78:{f.rearrange(90);f.pc=79;return;}
case 79:{f.rearrange(95);f.pc=80;return;}
case 80:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=81;return;}
case 81:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.SecretKeyFactory)(Object)receiver).generateSecret(((java.security.spec.KeySpec)(Object)(a0))),1);f.pc=82;return;}
case 82:{f.push(dev.zprestige.sourceview.FlowFrame.pending(javax.crypto.spec.IvParameterSpec.class),1);f.pc=83;return;}
case 83:{f.rearrange(89);f.pc=84;return;}
case 84:{f.push(8,1);f.pc=85;return;}
case 85:{f.push(new byte[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=86;return;}
case 86:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{byte[].class},new Object[]{((byte[])(Object)(a0))});f.pc=87;return;}
case 87:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();((javax.crypto.Cipher)(Object)receiver).init(dev.zprestige.sourceview.FlowFrame.i(a0),((java.security.Key)(Object)(a1)),((java.security.spec.AlgorithmParameterSpec)(Object)(a2)));f.pc=88;return;}
case 88:{f.push(5,1);f.pc=89;return;}
case 89:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=90;return;}
case 90:{f.locals[0]=f.pop();f.pc=91;return;}
case 91:{f.push(0,1);f.pc=92;return;}
case 92:{f.locals[6]=f.pop();f.pc=93;return;}
case 93:{f.push("\u00af\u00c7u\u009c_\u00f9\u00d8\u00db\u0008\u0092\u0085\u00a8\u00a8\u000cd\u00e2\u0008$\u00b9G\u00dan\u00e9\u00c6Z",1);f.pc=94;return;}
case 94:{f.rearrange(89);f.pc=95;return;}
case 95:{f.locals[5]=f.pop();f.pc=96;return;}
case 96:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=97;return;}
case 97:{f.locals[7]=f.pop();f.pc=98;return;}
case 98:{f.push(8,1);f.pc=99;return;}
case 99:{f.locals[4]=f.pop();f.pc=100;return;}
case 100:{f.push(-1,1);f.pc=101;return;}
case 101:{f.locals[3]=f.pop();f.pc=102;return;}
case 102:{f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{f.locals[3]=dev.zprestige.sourceview.FlowFrame.i(f.locals[3])+(1);f.pc=105;return;}
case 105:{f.push(f.locals[5],1);f.pc=106;return;}
case 106:{f.push(f.locals[3],1);f.pc=107;return;}
case 107:{f.rearrange(89);f.pc=108;return;}
case 108:{f.push(f.locals[4],1);f.pc=109;return;}
case 109:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=110;return;}
case 110:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).substring(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1)),1);f.pc=111;return;}
case 111:{f.push(-1,1);f.pc=112;return;}
case 112:{f.pc=178;return;}
case 113:{f.pc=114;return;}
case 114:{f.pc=115;return;}
case 115:{f.push(f.locals[0],1);f.pc=116;return;}
case 116:{f.rearrange(95);f.pc=117;return;}
case 117:{f.push(f.locals[6],1);f.pc=118;return;}
case 118:{f.locals[6]=dev.zprestige.sourceview.FlowFrame.i(f.locals[6])+(1);f.pc=119;return;}
case 119:{f.rearrange(95);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=121;return;}
case 121:{f.push(f.locals[3],1);f.pc=122;return;}
case 122:{f.push(f.locals[4],1);f.pc=123;return;}
case 123:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=124;return;}
case 124:{f.rearrange(89);f.pc=125;return;}
case 125:{f.locals[3]=f.pop();f.pc=126;return;}
case 126:{f.push(f.locals[7],1);f.pc=127;return;}
case 127:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=133;return;}f.pc=128;return;}
case 128:{f.push(f.locals[5],1);f.pc=129;return;}
case 129:{f.push(f.locals[3],1);f.pc=130;return;}
case 130:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).charAt(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=131;return;}
case 131:{f.locals[4]=f.pop();f.pc=132;return;}
case 132:{f.pc=102;return;}
case 133:{f.pc=134;return;}
case 134:{f.pc=135;return;}
case 135:{f.push("\u00b9`\u00b8m\u0082\u00a9\u00d5!\u0008\u00a2_TI\u00a1[\u00bc\u0009",1);f.pc=136;return;}
case 136:{f.rearrange(89);f.pc=137;return;}
case 137:{f.locals[5]=f.pop();f.pc=138;return;}
case 138:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).length(),1);f.pc=139;return;}
case 139:{f.locals[7]=f.pop();f.pc=140;return;}
case 140:{f.push(8,1);f.pc=141;return;}
case 141:{f.locals[4]=f.pop();f.pc=142;return;}
case 142:{f.push(-1,1);f.pc=143;return;}
case 143:{f.locals[3]=f.pop();f.pc=144;return;}
case 144:{f.pc=145;return;}
case 145:{f.pc=146;return;}
case 146:{f.locals[3]=dev.zprestige.sourceview.FlowFrame.i(f.locals[3])+(1);f.pc=147;return;}
case 147:{f.push(f.locals[5],1);f.pc=148;return;}
case 148:{f.push(f.locals[3],1);f.pc=149;return;}
case 149:{f.rearrange(89);f.pc=150;return;}
case 150:{f.push(f.locals[4],1);f.pc=151;return;}
case 151:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=152;return;}
case 152:{Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).substring(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1)),1);f.pc=153;return;}
case 153:{f.push(0,1);f.pc=154;return;}
case 154:{f.pc=178;return;}
case 155:{f.pc=156;return;}
case 156:{f.pc=157;return;}
case 157:{f.push(f.locals[0],1);f.pc=158;return;}
case 158:{f.rearrange(95);f.pc=159;return;}
case 159:{f.push(f.locals[6],1);f.pc=160;return;}
case 160:{f.locals[6]=dev.zprestige.sourceview.FlowFrame.i(f.locals[6])+(1);f.pc=161;return;}
case 161:{f.rearrange(95);f.pc=162;return;}
case 162:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=163;return;}
case 163:{f.push(f.locals[3],1);f.pc=164;return;}
case 164:{f.push(f.locals[4],1);f.pc=165;return;}
case 165:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=166;return;}
case 166:{f.rearrange(89);f.pc=167;return;}
case 167:{f.locals[3]=f.pop();f.pc=168;return;}
case 168:{f.push(f.locals[7],1);f.pc=169;return;}
case 169:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=175;return;}f.pc=170;return;}
case 170:{f.push(f.locals[5],1);f.pc=171;return;}
case 171:{f.push(f.locals[3],1);f.pc=172;return;}
case 172:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).charAt(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=173;return;}
case 173:{f.locals[4]=f.pop();f.pc=174;return;}
case 174:{f.pc=144;return;}
case 175:{f.pc=176;return;}
case 176:{f.pc=177;return;}
case 177:{f.pc=192;return;}
case 178:{f.pc=179;return;}
case 179:{f.pc=180;return;}
case 180:{f.rearrange(95);f.pc=181;return;}
case 181:{f.push("ISO-8859-1",1);f.pc=182;return;}
case 182:{Object a0=f.pop();Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).getBytes(((java.lang.String)(Object)(a0))),1);f.pc=183;return;}
case 183:{f.push(f.locals[1],1);f.pc=184;return;}
case 184:{f.rearrange(95);f.pc=185;return;}
case 185:{Object a0=f.pop();Object receiver=f.pop();f.push(((javax.crypto.Cipher)(Object)receiver).doFinal(((byte[])(Object)(a0))),1);f.pc=186;return;}
case 186:{f.locals[8]=f.pop();f.pc=187;return;}
case 187:{f.push(f.locals[8],1);f.pc=188;return;}
case 188:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0589.a(((byte[])(Object)(a0))),1);f.pc=189;return;}
case 189:{Object receiver=f.pop();f.push(((java.lang.String)(Object)receiver).intern(),1);f.pc=190;return;}
case 190:{f.rearrange(95);f.pc=191;return;}
case 191:{switch(dev.zprestige.sourceview.FlowFrame.i(f.pop())){case 0:f.pc=155;break;default:f.pc=113;}return;}
case 192:{f.pc=193;return;}
case 193:{f.pc=194;return;}
case 194:{f.pc=195;return;}
case 195:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0589.class),1);f.pc=196;return;}
case 196:{f.rearrange(89);f.pc=197;return;}
case 197:{f.push(f.locals[0],1);f.pc=198;return;}
case 198:{f.push(4,1);f.pc=199;return;}
case 199:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=200;return;}
case 200:{f.push(0,1);f.pc=201;return;}
case 201:{f.push(null,1);f.pc=202;return;}
case 202:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.lang.String.class,int.class,dev.zprestige.sourceview.S0270.class},new Object[]{((java.lang.String)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),((dev.zprestige.sourceview.S0270)(Object)(a2))});f.pc=203;return;}
case 203:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0589.class,"NONE",((dev.zprestige.sourceview.S0589)(Object)(f.pop())));f.pc=204;return;}
case 204:{f.pc=205;return;}
case 205:{f.pc=206;return;}
case 206:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0589.class),1);f.pc=207;return;}
case 207:{f.rearrange(89);f.pc=208;return;}
case 208:{f.push(f.locals[0],1);f.pc=209;return;}
case 209:{f.push(0,1);f.pc=210;return;}
case 210:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=211;return;}
case 211:{f.push(1,1);f.pc=212;return;}
case 212:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0273.class),1);f.pc=213;return;}
case 213:{f.rearrange(89);f.pc=214;return;}
case 214:{f.push(f.locals[15],2);f.pc=215;return;}
case 215:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{long.class},new Object[]{dev.zprestige.sourceview.FlowFrame.j(a0)});f.pc=216;return;}
case 216:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.lang.String.class,int.class,dev.zprestige.sourceview.S0270.class},new Object[]{((java.lang.String)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),((dev.zprestige.sourceview.S0270)(Object)(a2))});f.pc=217;return;}
case 217:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0589.class,"NORMAL",((dev.zprestige.sourceview.S0589)(Object)(f.pop())));f.pc=218;return;}
case 218:{f.pc=219;return;}
case 219:{f.pc=220;return;}
case 220:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0589.class),1);f.pc=221;return;}
case 221:{f.rearrange(89);f.pc=222;return;}
case 222:{f.push(f.locals[0],1);f.pc=223;return;}
case 223:{f.push(3,1);f.pc=224;return;}
case 224:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=225;return;}
case 225:{f.push(2,1);f.pc=226;return;}
case 226:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0272.class),1);f.pc=227;return;}
case 227:{f.rearrange(89);f.pc=228;return;}
case 228:{f.push(f.locals[11],2);f.pc=229;return;}
case 229:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{long.class},new Object[]{dev.zprestige.sourceview.FlowFrame.j(a0)});f.pc=230;return;}
case 230:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.lang.String.class,int.class,dev.zprestige.sourceview.S0270.class},new Object[]{((java.lang.String)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),((dev.zprestige.sourceview.S0270)(Object)(a2))});f.pc=231;return;}
case 231:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0589.class,"CHAMS",((dev.zprestige.sourceview.S0589)(Object)(f.pop())));f.pc=232;return;}
case 232:{f.pc=233;return;}
case 233:{f.pc=234;return;}
case 234:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0589.class),1);f.pc=235;return;}
case 235:{f.rearrange(89);f.pc=236;return;}
case 236:{f.push(f.locals[0],1);f.pc=237;return;}
case 237:{f.push(2,1);f.pc=238;return;}
case 238:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=239;return;}
case 239:{f.push(3,1);f.pc=240;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$2(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 240:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0271.class),1);f.pc=241;return;}
case 241:{f.rearrange(89);f.pc=242;return;}
case 242:{f.push(f.locals[17],2);f.pc=243;return;}
case 243:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{long.class},new Object[]{dev.zprestige.sourceview.FlowFrame.j(a0)});f.pc=244;return;}
case 244:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.lang.String.class,int.class,dev.zprestige.sourceview.S0270.class},new Object[]{((java.lang.String)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),((dev.zprestige.sourceview.S0270)(Object)(a2))});f.pc=245;return;}
case 245:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0589.class,"AURA",((dev.zprestige.sourceview.S0589)(Object)(f.pop())));f.pc=246;return;}
case 246:{f.pc=247;return;}
case 247:{f.pc=248;return;}
case 248:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0589.class),1);f.pc=249;return;}
case 249:{f.rearrange(89);f.pc=250;return;}
case 250:{f.push(f.locals[0],1);f.pc=251;return;}
case 251:{f.push(1,1);f.pc=252;return;}
case 252:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=253;return;}
case 253:{f.push(4,1);f.pc=254;return;}
case 254:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0274.class),1);f.pc=255;return;}
case 255:{f.rearrange(89);f.pc=256;return;}
case 256:{f.push(f.locals[13],2);f.pc=257;return;}
case 257:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{long.class},new Object[]{dev.zprestige.sourceview.FlowFrame.j(a0)});f.pc=258;return;}
case 258:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{java.lang.String.class,int.class,dev.zprestige.sourceview.S0270.class},new Object[]{((java.lang.String)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),((dev.zprestige.sourceview.S0270)(Object)(a2))});f.pc=259;return;}
case 259:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0589.class,"RAINBOW",((dev.zprestige.sourceview.S0589)(Object)(f.pop())));f.pc=260;return;}
case 260:{f.pc=261;return;}
case 261:{f.pc=262;return;}
case 262:{f.push(0,1);f.pc=263;return;}
case 263:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=264;return;}
case 264:{f.push(8746081210455756432L,2);f.pc=265;return;}
case 265:{f.push(f.locals[9],2);f.pc=266;return;}
case 266:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0589.__indy$0(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=267;return;}
case 267:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0589.class,"b",((dev.zprestige.sourceview.S0589[])(Object)(f.pop())));f.pc=268;return;}
case 268:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
