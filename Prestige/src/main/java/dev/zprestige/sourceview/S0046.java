package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0117;
import dev.zprestige.sourceview.S0549;
import dev.zprestige.sourceview.S0599;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class S0046 {
   public S0599 a;
   public boolean b = false;
   public static String[] c;
   public static long i;
   public static Object[] j;
   public static String[] k;

   public S0046(S0599 var1)  throws Throwable {
try {

      this.a = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0046()  throws Throwable {
      this(S0599.NONE);
try {

   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(2,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$2$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static void b(String[] var0) {
try {

      c = var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static String[] b$return$7e9e5c04() {
try {

      return c;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class b(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = a(var0, var2);
      Object var6 = j[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(k[var4]);
            j[var4] = var5;
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

   public boolean b$return$e4b089a0(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(7,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$7$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=22&&f.pc<31&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=33;continue;}
if(f.pc>=29&&f.pc<44&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=44;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
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

   public static MatchException b(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field c(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = j[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = k[var4];
      int var7 = var6.indexOf(8);
      Class var8 = b(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = b(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = a(var12, var10, var11);
         if (var13 != null) {
            j[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = b(var14[var15], var10, var11);
               if (var13 != null) {
                  j[var4] = var13;
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
            var12 = b(648549172927962L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method d(long var0, long var2) {
try {

      int var4 = a(var0, var2);
      Object var5 = j[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = k[var4];
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
            j[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = b(648549172927962L, 0L);
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
                  j[var4] = var19;
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
            var23 = b(648549172927962L, 0L);
         }
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
         if (var8 != 't' && var8 != 'c' && var8 != 214 && var8 != 237) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 164) {
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
            if (var8 == 't') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'c') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 214) {
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
      return __indy$8(var8.asSpreader(Object[].class, var4.length), var4);
   
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

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$9().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/aH" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public boolean a$return$e4b089a0(Object[] var1) {
try {

      return this.b;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public S0599 a$return$4f71c10(Object[] var1) {
try {

      return this.a;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(2,2);
f.locals[0]=this;
f.locals[1]=var1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$18$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   public static int a(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (k[var4] != null) {
         return var4;
      }

      Object var5 = j[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 19;
         case 1 -> 52;
         case 2 -> 45;
         case 3 -> 29;
         case 4 -> 55;
         case 5 -> 36;
         case 6 -> 9;
         case 7 -> 28;
         case 8 -> 37;
         case 9 -> 21;
         case 10 -> 42;
         case 11 -> 31;
         case 12 -> 59;
         case 13 -> 44;
         case 14 -> 30;
         case 15 -> 14;
         case 16 -> 46;
         case 17 -> 10;
         case 18 -> 47;
         case 19 -> 16;
         case 20 -> 2;
         case 21 -> 5;
         case 22 -> 26;
         case 23 -> 34;
         case 24 -> 39;
         case 25 -> 62;
         case 26 -> 63;
         case 27 -> 3;
         case 28 -> 43;
         case 29 -> 50;
         case 30 -> 15;
         case 31 -> 40;
         case 32 -> 51;
         case 33 -> 17;
         case 34 -> 27;
         case 35 -> 41;
         case 36 -> 49;
         case 37 -> 0;
         case 38 -> 6;
         case 39 -> 13;
         case 40 -> 53;
         case 41 -> 38;
         case 42 -> 8;
         case 43 -> 33;
         case 44 -> 48;
         case 45 -> 25;
         case 46 -> 20;
         case 47 -> 18;
         case 48 -> 56;
         case 49 -> 54;
         case 50 -> 22;
         case 51 -> 60;
         case 52 -> 61;
         case 53 -> 7;
         case 54 -> 11;
         case 55 -> 32;
         case 56 -> 12;
         case 57 -> 24;
         case 58 -> 23;
         case 59 -> 35;
         case 60 -> 1;
         case 61 -> 58;
         case 62 -> 57;
         default -> 4;
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

      k[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      j[0] = "j(fwrt|(c-ackc`+mwz$w<&eF";
      j[1] = "|\u007f\u000ez1g\t_\u0005u (tG\u0016r)a\u001c";
      j[2] = ")w=u|v\\W6zm9!O%}dpI";
      j[3] = void.class;
      k[3] = "java/lang/Void";
      j[4] = "\"\u000b\u000ezVy4\u000b\u000b En#@\b&Iz2\u0007\u001f1\u0002j*\u0007\u001d:X'\u0016\u001c\u001d'X`!\u000b";
      j[5] = "\f8A\u007f\u0006\u0001\u001a8D%\u0015\u0016\rsG#\u0019\u0002\u001c4P4R\u0013'";
      j[6] = "\u0004\u001bT\u0017? \u000f\u0014EX\\-\u001a\u0019J3i/\u000b\nV\u001f~\"";
      j[7] = "^\"T\u0012f\u0015+\u0002_\u001dwZJ\fT\u0016s\u0000>";
      j[8] = boolean.class;
      k[8] = "java/lang/Boolean";
      j[9] = "Ap\u001eX\u0005&J\u007f\u000f\u0017d(At\u000bM";
      j[10] = "\f,\u0012-\u0011f\u0007=T\u0015\u001e\u001aZ;\u001cdN$\r/B%t";
      j[11] = ".?+\u001e\t!h'(\u000761\u0014y/\u0006R>$xhD_";
      j[12] = "\u0007uz>\u0016\u0013\fd<\u0006\u0019oT}\u007f`M\u0010U|(fsS\t&#8\fR\bq%\u0006";
      j[13] = "\u0007)a!Ke\u0006n#,.<<l|{S>U1m8JU\u0001,!,S1_('>.";
      j[14] = "f\t>\u0013\u0014*m\u0018x+\u001bV0\u001e0ZKhg\nn\u001bql`\u000e}Q\no7\u001d1+";
   
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

   private static native String[] __indy$0(long var0, long var2) throws Throwable;

   private static native void __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$2(long var0, long var2) throws Throwable;

   private static native S0117 __indy$3(long var0, long var2) throws Throwable;

   private static native MatchException __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native S0117 __indy$6(long var0, long var2) throws Throwable;

   private static native boolean __indy$7(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$8(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$9() throws Throwable;



private void __flow$18$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(87);f.pc=2;return;}
case 2:{f.pc=3;return;}
case 3:{f.pc=4;return;}
case 4:{f.push(f.locals[0],1);f.pc=5;return;}
case 5:{f.push(1,1);f.pc=6;return;}
case 6:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0046.class,receiver,"b",(dev.zprestige.sourceview.FlowFrame.i(value) != 0));f.pc=7;return;}
case 7:{f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$7$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=5;return;}
case 5:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=6;return;}
case 6:{f.locals[2]=f.pop();f.pc=7;return;}
case 7:{f.rearrange(87);f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0046.i,2);f.pc=9;return;}
case 9:{f.push(f.locals[2],2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[2],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(79390605381451L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[4]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(88);f.pc=18;return;}
case 18:{f.push(7946844386289586264L,2);f.pc=19;return;}
case 19:{f.push(f.locals[2],2);f.pc=20;return;}
case 20:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0046.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=21;return;}
case 21:{f.locals[6]=f.pop();f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(7946802225962258205L,2);f.pc=25;return;}
case 25:{f.push(f.locals[2],2);f.pc=26;return;}
case 26:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0046.__indy$3(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=27;return;}
case 27:{f.push(f.locals[6],1);f.pc=28;return;}
case 28:{if(f.pop()!=null){f.pc=56;return;}f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{if(f.pop()!=null){f.pc=50;return;}f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=39;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.push(7946988167407788481L,2);f.pc=36;return;}
case 36:{f.push(f.locals[2],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0046.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{throw (Throwable)f.pop();}
case 39:{f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.push(0,1);f.pc=43;return;}
case 43:{f.result=f.pop();f.done=true;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.push(7946988167407788481L,2);f.pc=47;return;}
case 47:{f.push(f.locals[2],2);f.pc=48;return;}
case 48:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0046.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=49;return;}
case 49:{throw (Throwable)f.pop();}
case 50:{f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{f.pc=53;return;}
case 53:{f.push(7946802225962258205L,2);f.pc=54;return;}
case 54:{f.push(f.locals[2],2);f.pc=55;return;}
case 55:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0046.__indy$6(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.push(f.locals[0],1);f.pc=59;return;}
case 59:{f.push(f.locals[4],2);f.pc=60;return;}
case 60:{f.push(2,1);f.pc=61;return;}
case 61:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=62;return;}
case 62:{f.rearrange(91);f.pc=63;return;}
case 63:{f.rearrange(91);f.pc=64;return;}
case 64:{f.rearrange(87);f.pc=65;return;}
case 65:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=66;return;}
case 66:{f.push(1,1);f.pc=67;return;}
case 67:{f.rearrange(95);f.pc=68;return;}
case 68:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=69;return;}
case 69:{f.rearrange(90);f.pc=70;return;}
case 70:{f.rearrange(95);f.pc=71;return;}
case 71:{f.push(0,1);f.pc=72;return;}
case 72:{f.rearrange(95);f.pc=73;return;}
case 73:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=74;return;}
case 74:{f.push(7946931468276374609L,2);f.pc=75;return;}
case 75:{f.push(f.locals[2],2);f.pc=76;return;}
case 76:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0046.__indy$7(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=77;return;}
case 77:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$2$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-6890791224470381746L,2);f.pc=1;return;}
case 1:{f.push(-7184491210921410714L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(218063970530228L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0046.class,"i",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0046.i,2);f.pc=9;return;}
case 9:{f.push(18503138301026L,2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[0]=f.pop();f.pc=12;return;}
case 12:{f.push(15,1);f.pc=13;return;}
case 13:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=14;return;}
case 14:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0046.class,"j",((java.lang.Object[])(Object)(f.pop())));f.pc=15;return;}
case 15:{f.push(15,1);f.pc=16;return;}
case 16:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=17;return;}
case 17:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0046.class,"k",((java.lang.String[])(Object)(f.pop())));f.pc=18;return;}
case 18:{dev.zprestige.sourceview.S0046.a$return$9b75();f.pc=19;return;}
case 19:{f.push(-4710022623866666830L,2);f.pc=20;return;}
case 20:{f.push(f.locals[0],2);f.pc=21;return;}
case 21:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0046.__indy$0(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=22;return;}
case 22:{if(f.pop()==null){f.pc=28;return;}f.pc=23;return;}
case 23:{f.push(2,1);f.pc=24;return;}
case 24:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=25;return;}
case 25:{f.push(-4709748767382667706L,2);f.pc=26;return;}
case 26:{f.push(f.locals[0],2);f.pc=27;return;}
case 27:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0046.__indy$1(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=28;return;}
case 28:{f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
