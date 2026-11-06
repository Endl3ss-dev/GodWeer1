package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0185;
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

public abstract class S0249 {
   public String a;
   public Object b;
   public String c;
   public Predicate d;
   public static int[] e;
   public static long g;
   public static Object[] k;
   public static String[] l;

   public S0249(String var1, Object var2)  throws Throwable {
try {

      this.a = var1;
      this.b = var2;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(2,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static void e() {
try {

      k[0] = "TI\"\u0014\\jBI'NO}U\u0002$HCiDE3_\b~{";
      k[1] = "%'";
      k[2] = void.class;
      l[2] = "java/lang/Void";
      k[3] = "MG";
      k[4] = "\u000bF2YT?\u001dF7\u0003G(\n\r4\u0005K<\u001bJ#\u0012\u0000.'";
      k[5] = "k\bG7\u0018p\u001e(L8\t?c0_?\u0000v\u000b";
      k[6] = " \u0006.\u0013Ns+\t?\\/} \u0002;\u0006";
      k[7] = "@Q\u0001.~\u0005^Y\u001ba6\u0005DS\u0003&?\u001e\u0004`\u0005*4\u0019IQ\u0003*";
      k[8] = boolean.class;
      l[8] = "java/lang/Boolean";
      k[9] = "=P{~G\b6_j1$\u0005#ReZ\u0011\u00072Ayv\u0006\n";
      k[10] = "\u00026%\u0014\u0006\u001eF2`\u000eaK8vb\bXDB={\u0004\u001c!";
      k[11] = "i\u0016&7njw\u001f\"Lm\u0013:\u0011<q?x|Cbt\u0004";
      k[12] = "jxO\u001aq]tqKaq$?.S\u0018j\u001envA\u0006\u001b\u001d`$\n\u001b\"[uh\ba";
      k[13] = "^L\u0015\u000enzRHH\u000b\u0004l\u000e\t\t38z\u0005ONX~([Ju\u000e}y\u001f\u0002\f\u0003zu\u0013r";
      k[14] = "}+\u000e\u0007\"\\c\"\n|\"%(}\u0012\u00059\u001fy%\u0000\u001bH";
      k[15] = "tFX3BwjO\\HA\u000e%V\u001a7T~\u007fLO)(0f\u0015[4Xj|@EH";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void b(int[] var0) {
try {

      e = var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int[] b$return$12d40f() {
try {

      return e;
   
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

   public static CallSite b(Lookup var0, String var1, MethodType var2) {
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
         throw new RuntimeException("dev/zprestige/prestige/dK" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
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
      Object var6 = k[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(l[var4]);
            k[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0185
   public String b$return$73cf11cb() {
try {

      return this.c;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field c(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = k[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = l[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            k[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  k[var4] = var13;
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
            var12 = b(439085498155200L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = k[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = l[var4];
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
            k[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(439085498155200L, 0L);
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
                  k[var4] = var19;
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
            var23 = b(439085498155200L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
try {

      String var2 = (String)(Object)(var1[0]);
      this.a = var2;
   
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

   @S0185
   public String a$return$73cf11cb() {
try {

      return this.a;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'e' && var8 != 208 && var8 != 224 && var8 != 'u') {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 186) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 243) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'e') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 208) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 224) {
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

   @S0185
   public boolean a$return$9b79() {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(4,6);
f.locals[0]=this;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$18$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=8&&f.pc<16&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=18;continue;}
if(f.pc>=14&&f.pc<28&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=30;continue;}
if(f.pc>=36&&f.pc<48&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=50;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
}



   public S0249 a$return$99ec1946(Object[] var1) {
try {

      Predicate var2 = (Predicate)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      this.d = var2;
      return this;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static int a(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (l[var4] != null) {
         return var4;
      }

      Object var5 = k[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 46;
         case 1 -> 14;
         case 2 -> 62;
         case 3 -> 1;
         case 4 -> 4;
         case 5 -> 35;
         case 6 -> 63;
         case 7 -> 57;
         case 8 -> 11;
         case 9 -> 51;
         case 10 -> 19;
         case 11 -> 12;
         case 12 -> 42;
         case 13 -> 54;
         case 14 -> 45;
         case 15 -> 56;
         case 16 -> 61;
         case 17 -> 18;
         case 18 -> 24;
         case 19 -> 48;
         case 20 -> 10;
         case 21 -> 13;
         case 22 -> 16;
         case 23 -> 25;
         case 24 -> 39;
         case 25 -> 7;
         case 26 -> 37;
         case 27 -> 3;
         case 28 -> 28;
         case 29 -> 30;
         case 30 -> 6;
         case 31 -> 23;
         case 32 -> 60;
         case 33 -> 15;
         case 34 -> 21;
         case 35 -> 33;
         case 36 -> 49;
         case 37 -> 55;
         case 38 -> 20;
         case 39 -> 47;
         case 40 -> 31;
         case 41 -> 34;
         case 42 -> 29;
         case 43 -> 59;
         case 44 -> 9;
         case 45 -> 2;
         case 46 -> 58;
         case 47 -> 8;
         case 48 -> 32;
         case 49 -> 44;
         case 50 -> 40;
         case 51 -> 0;
         case 52 -> 22;
         case 53 -> 53;
         case 54 -> 17;
         case 55 -> 36;
         case 56 -> 26;
         case 57 -> 41;
         case 58 -> 5;
         case 59 -> 43;
         case 60 -> 50;
         case 61 -> 52;
         case 62 -> 27;
         default -> 38;
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

      l[var4] = new String(var13);
      return var4;
   
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

   @S0185
   public Object a$return$810c345d() {
try {

      return this.b;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0185
   public void a(Object var1) {
try {

      this.b = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0185
   public S0249 setDescription$return$ff46bbe9(String var1) {
try {

      this.c = var1;
      return this;
   
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

   private static native int[] __indy$0(long var0, long var2) throws Throwable;

   private static native void __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native MethodHandle __indy$2() throws Throwable;

   private static native Object __indy$3(MethodHandle var0, Object[] var1) throws Throwable;

   private static native String[] __indy$4(long var0, long var2) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$8(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$9(Object var0, long var1, long var3) throws Throwable;



private void __flow$18$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0249.g,2);f.pc=1;return;}
case 1:{f.push(8666087616443L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[1]=f.pop();f.pc=4;return;}
case 4:{f.push(6392013902014368339L,2);f.pc=5;return;}
case 5:{f.push(f.locals[1],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0249.__indy$4(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.locals[3]=f.pop();f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{f.push(((dev.zprestige.sourceview.S0249)(Object)f.pop()).d,1);f.pc=12;return;}
case 12:{f.push(f.locals[3],1);f.pc=13;return;}
case 13:{if(f.pop()!=null){f.pc=36;return;}f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{if(f.pop()==null){f.pc=56;return;}f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.pc=24;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(6392222208463435920L,2);f.pc=21;return;}
case 21:{f.push(f.locals[1],2);f.pc=22;return;}
case 22:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0249.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=23;return;}
case 23:{throw (Throwable)f.pop();}
case 24:{f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.push(f.locals[0],1);f.pc=27;return;}
case 27:{f.push(((dev.zprestige.sourceview.S0249)(Object)f.pop()).d,1);f.pc=28;return;}
case 28:{f.pc=29;return;}
case 29:{f.pc=36;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.push(6392222208463435920L,2);f.pc=33;return;}
case 33:{f.push(f.locals[1],2);f.pc=34;return;}
case 34:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0249.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=35;return;}
case 35:{throw (Throwable)f.pop();}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(f.locals[0],1);f.pc=39;return;}
case 39:{f.push(6391906290466868099L,2);f.pc=40;return;}
case 40:{f.push(f.locals[1],2);f.pc=41;return;}
case 41:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0249.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=42;return;}
case 42:{f.push(6392047359623414025L,2);f.pc=43;return;}
case 43:{f.push(f.locals[1],2);f.pc=44;return;}
case 44:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0249.__indy$8(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=45;return;}
case 45:{f.push(f.locals[3],1);f.pc=46;return;}
case 46:{if(f.pop()!=null){f.pc=59;return;}f.pc=47;return;}
case 47:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=62;return;}f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.pc=56;return;}
case 50:{f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{f.push(6392222208463435920L,2);f.pc=53;return;}
case 53:{f.push(f.locals[1],2);f.pc=54;return;}
case 54:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0249.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=55;return;}
case 55:{throw (Throwable)f.pop();}
case 56:{f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.push(1,1);f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=65;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.push(0,1);f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(9079498804213215737L,2);f.pc=1;return;}
case 1:{f.push(1026195064180430032L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(23399175985422L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0249.class,"g",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0249.g,2);f.pc=9;return;}
case 9:{f.push(46192071646235L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[0]=f.pop();f.pc=12;return;}
case 12:{f.push(16,1);f.pc=13;return;}
case 13:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=14;return;}
case 14:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0249.class,"k",((java.lang.Object[])(Object)(f.pop())));f.pc=15;return;}
case 15:{f.push(16,1);f.pc=16;return;}
case 16:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=17;return;}
case 17:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0249.class,"l",((java.lang.String[])(Object)(f.pop())));f.pc=18;return;}
case 18:{dev.zprestige.sourceview.S0249.e();f.pc=19;return;}
case 19:{f.push(3104621755214122154L,2);f.pc=20;return;}
case 20:{f.push(f.locals[0],2);f.pc=21;return;}
case 21:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0249.__indy$0(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=22;return;}
case 22:{if(f.pop()==null){f.pc=28;return;}f.pc=23;return;}
case 23:{f.push(3,1);f.pc=24;return;}
case 24:{f.push(new int[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=25;return;}
case 25:{f.push(3104524471103481006L,2);f.pc=26;return;}
case 26:{f.push(f.locals[0],2);f.pc=27;return;}
case 27:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0249.__indy$1(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=28;return;}
case 28:{f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
