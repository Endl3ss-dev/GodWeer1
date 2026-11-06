package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0117;
import dev.zprestige.sourceview.S0221;
import dev.zprestige.sourceview.S0225;
import dev.zprestige.sourceview.S0549;
import net.minecraft.class_310;
import net.minecraft.class_3675;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_1041;
import net.minecraft.class_304;
import net.minecraft.class_315;
import net.minecraft.class_3675.class_306;

public final class S0220 implements S0225 {
   public static int a;
   public static boolean c;
   public static long d;
   public static Object[] e;
   public static String[] f;

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
      if (f[var4] != null) {
         return var4;
      }

      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 55;
         case 1 -> 59;
         case 2 -> 57;
         case 3 -> 5;
         case 4 -> 3;
         case 5 -> 8;
         case 6 -> 54;
         case 7 -> 29;
         case 8 -> 19;
         case 9 -> 21;
         case 10 -> 18;
         case 11 -> 53;
         case 12 -> 2;
         case 13 -> 52;
         case 14 -> 9;
         case 15 -> 48;
         case 16 -> 30;
         case 17 -> 15;
         case 18 -> 33;
         case 19 -> 51;
         case 20 -> 7;
         case 21 -> 14;
         case 22 -> 4;
         case 23 -> 47;
         case 24 -> 24;
         case 25 -> 58;
         case 26 -> 62;
         case 27 -> 12;
         case 28 -> 41;
         case 29 -> 39;
         case 30 -> 60;
         case 31 -> 17;
         case 32 -> 20;
         case 33 -> 50;
         case 34 -> 23;
         case 35 -> 56;
         case 36 -> 27;
         case 37 -> 26;
         case 38 -> 38;
         case 39 -> 16;
         case 40 -> 45;
         case 41 -> 37;
         case 42 -> 35;
         case 43 -> 63;
         case 44 -> 6;
         case 45 -> 32;
         case 46 -> 10;
         case 47 -> 61;
         case 48 -> 22;
         case 49 -> 28;
         case 50 -> 42;
         case 51 -> 13;
         case 52 -> 36;
         case 53 -> 49;
         case 54 -> 25;
         case 55 -> 31;
         case 56 -> 11;
         case 57 -> 40;
         case 58 -> 34;
         case 59 -> 43;
         case 60 -> 46;
         case 61 -> 0;
         case 62 -> 44;
         default -> 1;
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

      f[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void b$return$e4b0899c(Object[] var0) {
try {

      long var1 = (Long)(Object)(var0[0]);
      var1 = d ^ var1;
      long var3 = var1 ^ 139211699224092L;
      String[] var5 = __indy$0(-7950447763453537954L, var1);

      int var10000;
      label20: {
         try {
            var10000 = a;
            if (var5 != null) {
               break label20;
            }

            if (a <= 0) {
               return;
            }
         } catch (MatchException var6) {
            throw __indy$1(var6, -7950054950727775194L, var1);
         }

         var10000 = 0;
      }

      a = var10000;
      __indy$2(new Object[]{var3}, -7951075845976490620L, var1);
   
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

   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 239 && var8 != 213 && var8 != 204 && var8 != 210) {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 226) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'M') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = g(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 239) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 213) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 204) {
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

   public static void c$return$e4b0899c(Object[] param0) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(4,6);
f.locals[0]=param0;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$8$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=16&&f.pc<42&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=44;continue;}
if(f.pc>=21&&f.pc<53&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=55;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static Method h(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = f[var4];
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
            e[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = f(1257461769932581L, 0L);
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
                  e[var4] = var19;
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
            var23 = f(1257461769932581L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class f(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = e(var0, var2);
      Object var6 = e[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(f[var4]);
            e[var4] = var5;
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

   public static void a$return$9b75() {
try {

      e[0] = "\u0003zFI^}\u0003zQ\u0015Rr\u00191Q\u000bRg\u001e@\u0001V\u0003";
      e[1] = "*\tM\u0012(5*\tZN$:0BZP$/73\b\fqm";
      e[2] = "V#4X^9@#1\u0002M.Wh2\u0004A:F/%\u0013\n*G";
      e[3] = "\u001b\u0003drq \u0010\fu=\u0012-\u0005\u0001zV'/\u0014\u0012fz0\"";
      e[4] = "0^Qm\r\u001c&^T7\u001e\u000b1\u0015W1\u0012\u001f R@&Y\r\u001c";
      e[5] = "r-\u0017k\u0017d\u0007\r\u001cd\u0006+z\u0015\u000fc\u000fb\u0012";
      e[6] = ".Z\u000bH\u0015\u0017.Z\u001c\u0014\u0019\u00184\u0011\u001c\n\u0019\r3`LVL";
      e[7] = "O\u001d\u001d,lTO\u001d\np`[UV\nn`NR'Z46\b\u0005\u001b\u0005crN~KY4";
      e[8] = "\u001dR[c|\u000f\u001dRL?p\u0000\u0007\u0019L!p\u0015\u0000h\u001c{&S";
      e[9] = int.class;
      f[9] = "java/lang/Integer";
      e[10] = boolean.class;
      f[10] = "java/lang/Boolean";
      e[11] = "\\\u0012\u001a\u00152\u0005\\\u0012\rI>\nFY\rW>\u001fA(]\nj";
      e[12] = void.class;
      f[12] = "java/lang/Void";
      e[13] = "\u0019\u0001(v\u000f\u0018\u000f\u0001-,\u001c\u000f\u0018J.*\u0010\u001b\t\r9=[\u000b\u0011\r;6\u0001F-\u0016;+\u0001\u0001\u001a\u0001";
      e[14] = "$\tJiFV2\tO3UA%BL5YU4\u0005[\"\u0012D\u000f";
      e[15] = "\u001dA/{\u0006.ha$t\u0017a\to/\u007f\u0013;}";
      e[16] = "L@~Bo=9`uM~rXn~Fz(,";
      e[17] = "'2\u001f.Y\u0012,=\u000ea8\u001c'6\n;";
      e[18] = "P\u0012\u000eLHTBDN\"N\u0003OHXN|T\t\u0016\u000f\u0019+\u0010\tM\\\u001f\u0013QKR?";
      e[19] = "&N8<!1s\r1=\u0019>\u001cNa?'mm\u001b&2rW&H>b#&s\u000f37\u0019";
      e[20] = "bk{6\u001f;5i'%d>Ymd.\u000b-(-f3\u001eT";
      e[21] = "\u0013\u000et`QmO\u0013#l=uB\n$7QG\u0016J~a=,^\u0010}5\u0007.P\b.P";
      e[22] = "'Pg\u0002\f\u0012t\u0000bQr\u0018v\u0004b\u0005%O&Q=iN\u000e'Yj\u0014\u0012\u0013pU";
      e[23] = "OTzu\u00038\u0007Hoi<;~PbbV.\u0000V6hM";
      e[24] = "BMW\u0019lOD\u0019]\u0002\u0013X+NTO+LVF@\u0001r1\u001bT\b\u0011/OA\u001eK\u0016\u0013";
      e[25] = "87/7u\u0019*aoYxB6ir\u000e/\u001dj5\u001ei/B0sz:\u007fGc";
      e[26] = "}lg[e\u0003{1}\u0007\u0014\u001a..x\u0006x(zo)Q+\u007f=h}\u0002)G|*ba*\u0003*1xZ/\u001e,=\u0018^\u007f\u001a3.z\ns\u001f=R";
      e[27] = "\\\feX=?\u0000\u00112TQ'\r\b5\u000f=\u0015ZOiXhB_\u001f0\u0018- \u000b\u00135\u0016Qr\u0010M7T/(Z\u000e0h";
      e[28] = "#H@Bp\u0011!FX\u0011\u0015NrEF\u001cy|&\u0005\u001aG\u0015\u0015cPE\u001b.\u0010~VI{";
      e[29] = "bMkw*\u00037\u000ebv\u0012\u000eX\u001dk+*\u0018%\u0015\u007feseh\u00077u.\u001b2Mtr\u0012";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$16().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/cu" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$e4b0899c(Object[] param0) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(6,7);
f.locals[0]=param0;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$15$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=22&&f.pc<29&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=31;continue;}
if(f.pc>=27&&f.pc<68&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=70;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field g(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = f[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = f(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = c(var12, var10, var11);
         if (var13 != null) {
            e[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = d(var14[var15], var10, var11);
               if (var13 != null) {
                  e[var4] = var13;
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
            var12 = f(1257461769932581L, 0L);
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

   private static native String[] __indy$0(long var0, long var2) throws Throwable;

   private static native MatchException __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$2(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$3(MethodHandle var0, Object[] var1) throws Throwable;

   private static native String[] __indy$4(long var0, long var2) throws Throwable;

   private static native class_1041 __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native class_315 __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native class_304 __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native class_306 __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$10(Object var0, int var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native class_315 __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native class_304 __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$15(Object var0, boolean var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$16() throws Throwable;

   private static native String[] __indy$17(long var0, long var2) throws Throwable;

   private static native MatchException __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native S0117 __indy$19(long var0, long var2) throws Throwable;

   private static native void __indy$20(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native class_315 __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native class_304 __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$24(Object var0, boolean var1, long var2, long var4) throws Throwable;



private static void __flow$15$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[0],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=5;return;}
case 5:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=6;return;}
case 6:{f.locals[1]=f.pop();f.pc=7;return;}
case 7:{f.rearrange(87);f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0220.d,2);f.pc=9;return;}
case 9:{f.push(f.locals[1],2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[1]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[1],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(81162841569345L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[3]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(88);f.pc=18;return;}
case 18:{f.push(-1062277602075872842L,2);f.pc=19;return;}
case 19:{f.push(f.locals[1],2);f.pc=20;return;}
case 20:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$17(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=21;return;}
case 21:{f.locals[5]=f.pop();f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(dev.zprestige.sourceview.S0220.c,1);f.pc=25;return;}
case 25:{f.push(f.locals[5],1);f.pc=26;return;}
case 26:{if(f.pop()!=null){f.pc=93;return;}f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=76;return;}f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=37;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{f.push(-1061751222611120946L,2);f.pc=34;return;}
case 34:{f.push(f.locals[1],2);f.pc=35;return;}
case 35:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=36;return;}
case 36:{throw (Throwable)f.pop();}
case 37:{f.pc=38;return;}
case 38:{f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.push(1,1);f.pc=41;return;}
case 41:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0220.class,"c",(dev.zprestige.sourceview.FlowFrame.i(f.pop()) != 0));f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.push(-1062042243264921536L,2);f.pc=45;return;}
case 45:{f.push(f.locals[1],2);f.pc=46;return;}
case 46:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$19(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=47;return;}
case 47:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0221.class),1);f.pc=48;return;}
case 48:{f.rearrange(89);f.pc=49;return;}
case 49:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=50;return;}
case 50:{f.push(f.locals[3],2);f.pc=51;return;}
case 51:{f.push(2,1);f.pc=52;return;}
case 52:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=53;return;}
case 53:{f.rearrange(91);f.pc=54;return;}
case 54:{f.rearrange(91);f.pc=55;return;}
case 55:{f.rearrange(87);f.pc=56;return;}
case 56:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=57;return;}
case 57:{f.push(1,1);f.pc=58;return;}
case 58:{f.rearrange(95);f.pc=59;return;}
case 59:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=60;return;}
case 60:{f.rearrange(90);f.pc=61;return;}
case 61:{f.rearrange(95);f.pc=62;return;}
case 62:{f.push(0,1);f.pc=63;return;}
case 63:{f.rearrange(95);f.pc=64;return;}
case 64:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=65;return;}
case 65:{f.push(-1062503946109443962L,2);f.pc=66;return;}
case 66:{f.push(f.locals[1],2);f.pc=67;return;}
case 67:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0220.__indy$20(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.pc=76;return;}
case 70:{f.pc=71;return;}
case 71:{f.pc=72;return;}
case 72:{f.push(-1061751222611120946L,2);f.pc=73;return;}
case 73:{f.push(f.locals[1],2);f.pc=74;return;}
case 74:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=75;return;}
case 75:{throw (Throwable)f.pop();}
case 76:{f.pc=77;return;}
case 77:{f.pc=78;return;}
case 78:{f.pc=79;return;}
case 79:{f.push(dev.zprestige.sourceview.S0220.b,1);f.pc=80;return;}
case 80:{f.push(-1062429765507161099L,2);f.pc=81;return;}
case 81:{f.push(f.locals[1],2);f.pc=82;return;}
case 82:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=83;return;}
case 83:{f.push(-1062076800824378402L,2);f.pc=84;return;}
case 84:{f.push(f.locals[1],2);f.pc=85;return;}
case 85:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=86;return;}
case 86:{f.push(1,1);f.pc=87;return;}
case 87:{f.push(-1062309443264459871L,2);f.pc=88;return;}
case 88:{f.push(f.locals[1],2);f.pc=89;return;}
case 89:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0220.__indy$24(((java.lang.Object)(Object)(a0)),(dev.zprestige.sourceview.FlowFrame.i(a1) != 0),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.pc=92;return;}
case 92:{f.push(2,1);f.pc=93;return;}
case 93:{f.pc=94;return;}
case 94:{f.pc=95;return;}
case 95:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0220.class,"a",dev.zprestige.sourceview.FlowFrame.i(f.pop()));f.pc=96;return;}
case 96:{f.pc=97;return;}
case 97:{f.pc=98;return;}
case 98:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$8$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[0],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=5;return;}
case 5:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=6;return;}
case 6:{f.locals[1]=f.pop();f.pc=7;return;}
case 7:{f.rearrange(87);f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0220.d,2);f.pc=9;return;}
case 9:{f.push(f.locals[1],2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[1]=f.pop();f.pc=12;return;}
case 12:{f.push(1339678425479320163L,2);f.pc=13;return;}
case 13:{f.push(f.locals[1],2);f.pc=14;return;}
case 14:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$4(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=15;return;}
case 15:{f.locals[3]=f.pop();f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.pc=18;return;}
case 18:{f.push(dev.zprestige.sourceview.S0220.b,1);f.pc=19;return;}
case 19:{f.push(f.locals[3],1);f.pc=20;return;}
case 20:{if(f.pop()!=null){f.pc=61;return;}f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(1339516682055714430L,2);f.pc=23;return;}
case 23:{f.push(f.locals[1],2);f.pc=24;return;}
case 24:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=25;return;}
case 25:{f.push(dev.zprestige.sourceview.S0220.b,1);f.pc=26;return;}
case 26:{f.push(1338707676044853280L,2);f.pc=27;return;}
case 27:{f.push(f.locals[1],2);f.pc=28;return;}
case 28:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=29;return;}
case 29:{f.push(1339759930239293451L,2);f.pc=30;return;}
case 30:{f.push(f.locals[1],2);f.pc=31;return;}
case 31:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=32;return;}
case 32:{f.push(1339539418614009358L,2);f.pc=33;return;}
case 33:{f.push(f.locals[1],2);f.pc=34;return;}
case 34:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=35;return;}
case 35:{f.push(1339050369317239627L,2);f.pc=36;return;}
case 36:{f.push(f.locals[1],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{f.push(1338926222623116060L,2);f.pc=39;return;}
case 39:{f.push(f.locals[1],2);f.pc=40;return;}
case 40:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=41;return;}
case 41:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=73;return;}f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.pc=50;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.push(1339417004594401051L,2);f.pc=47;return;}
case 47:{f.push(f.locals[1],2);f.pc=48;return;}
case 48:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=49;return;}
case 49:{throw (Throwable)f.pop();}
case 50:{f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{f.push(dev.zprestige.sourceview.S0220.b,1);f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.pc=61;return;}
case 55:{f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.push(1339417004594401051L,2);f.pc=58;return;}
case 58:{f.push(f.locals[1],2);f.pc=59;return;}
case 59:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=60;return;}
case 60:{throw (Throwable)f.pop();}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.push(1338707676044853280L,2);f.pc=64;return;}
case 64:{f.push(f.locals[1],2);f.pc=65;return;}
case 65:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=66;return;}
case 66:{f.push(1339759930239293451L,2);f.pc=67;return;}
case 67:{f.push(f.locals[1],2);f.pc=68;return;}
case 68:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0220.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=69;return;}
case 69:{f.push(0,1);f.pc=70;return;}
case 70:{f.push(1338868130846812276L,2);f.pc=71;return;}
case 71:{f.push(f.locals[1],2);f.pc=72;return;}
case 72:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0220.__indy$15(((java.lang.Object)(Object)(a0)),(dev.zprestige.sourceview.FlowFrame.i(a1) != 0),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=73;return;}
case 73:{f.pc=74;return;}
case 74:{f.pc=75;return;}
case 75:{f.pc=76;return;}
case 76:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(2038856935757642996L,2);f.pc=1;return;}
case 1:{f.push(962162074698052554L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(136083135366004L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0220.class,"d",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(30,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0220.class,"e",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(30,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0220.class,"f",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0220.a$return$9b75();f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.push(0,1);f.pc=18;return;}
case 18:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0220.class,"a",dev.zprestige.sourceview.FlowFrame.i(f.pop()));f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.push(0,1);f.pc=22;return;}
case 22:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0220.class,"c",(dev.zprestige.sourceview.FlowFrame.i(f.pop()) != 0));f.pc=23;return;}
case 23:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
