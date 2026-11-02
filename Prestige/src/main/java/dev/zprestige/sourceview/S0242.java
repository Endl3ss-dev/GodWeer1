package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0226;
import dev.zprestige.sourceview.S0241;
import dev.zprestige.sourceview.S0549;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_746;

public class S0242 implements Runnable {
   public int a;
   public S0241 b;
   public static long c;
   public static Object[] d;
   public static String[] e;

   public S0242(S0241 var1, int var2)  throws Throwable {
      this.a = var2;
      this.b = var1;
      super();
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


   @Override
   public void run() {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(6,6);
f.locals[0]=this;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$2$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=14&&f.pc<22&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=22;continue;}
if(f.pc>=28&&f.pc<38&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=40;continue;}
if(f.pc>=36&&f.pc<57&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=57;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
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
            var12 = b(514645872936022L, 0L);
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
            var23 = b(514645872936022L, 0L);
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
            var23 = b(514645872936022L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
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
         throw new RuntimeException("dev/zprestige/prestige/dE" + " : " + var1 + " : " + var2.toString(), var5);
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
         if (var8 != 'w' && var8 != 238 && var8 != 230 && var8 != 216) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'o') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'x') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'w') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 238) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 230) {
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

   public static void a$return$9b75() {
try {

      d[0] = "\u0015b!QD\u0017\u0003b$\u000bW\u0000\u0014)'\r[\u0014\u0005n0\u001a\u0010\u00069";
      d[1] = "4D`J~=AdkEor<|xBf;T";
      d[2] = "`\u000e)`xp`\u000e><t\u007fzE>\"tj}4n\u007f%";
      d[3] = "\u0002\nys\u000fq\u0002\nn/\u0003~\u0018An1\u0003k\u001f0:iT";
      d[4] = "P\u0013,]\"NF\u0013)\u00071YQX*\u0001=M@\u001f=\u0016vZq";
      d[5] = "f6K_J\u0019m9Z\u0010)\u0014x4U{\u001c\u0016i'IW\u000b\u001b";
      d[6] = "HTB1\u000fFV\\X~mZQA";
      d[7] = "\u000b\u0015tBIY\u0000\u001ae\r(W\u000b\u0011aW";
      d[8] = boolean.class;
      e[8] = "java/lang/Boolean";
      d[9] = "R&,A]zD&)\u001bNmSm*\u001dByB*=\n\tiD";
      d[10] = "Oe\nrjN:E\u0001}{\u0001[K\nv\u007f[/";
      d[11] = void.class;
      e[11] = "java/lang/Void";
      d[12] = "OZZ\u0005%\u001e\u0012\u0000\u0017\u0014J\u0017q\u0006\u0018\u0010'\u0017I\u0001\u0016Cs~A\u0007V\u001c&EK\u0005\u001b\u0003J";
      d[13] = ").z(\rl}azQ\\\u0010|j,.Mq7nv<6";
      d[14] = "kp\u001da.P<qZcOP0'Ah\u0018\u0007np\u0019\u0004u\\/#\\~2B #";
      d[15] = "\\\u001e\u001c\u0015f\u0013\u0005\u0014AO\u0003\u0017f^\u001bEn\u0015\u0004\u001eGH}~]\u0003\u0013Fh\u001c\u001d_\u001eU\u0003";
      d[16] = "~|>3ZUxh/2#B.hS7\u0019E.=(xNI+\u0004m7HW9n,tCUB";
   
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
         case 0 -> 7;
         case 1 -> 56;
         case 2 -> 32;
         case 3 -> 13;
         case 4 -> 2;
         case 5 -> 57;
         case 6 -> 9;
         case 7 -> 34;
         case 8 -> 4;
         case 9 -> 39;
         case 10 -> 1;
         case 11 -> 46;
         case 12 -> 48;
         case 13 -> 42;
         case 14 -> 16;
         case 15 -> 3;
         case 16 -> 40;
         case 17 -> 28;
         case 18 -> 62;
         case 19 -> 11;
         case 20 -> 26;
         case 21 -> 50;
         case 22 -> 61;
         case 23 -> 51;
         case 24 -> 45;
         case 25 -> 30;
         case 26 -> 18;
         case 27 -> 54;
         case 28 -> 58;
         case 29 -> 53;
         case 30 -> 21;
         case 31 -> 29;
         case 32 -> 19;
         case 33 -> 6;
         case 34 -> 63;
         case 35 -> 36;
         case 36 -> 43;
         case 37 -> 25;
         case 38 -> 38;
         case 39 -> 55;
         case 40 -> 41;
         case 41 -> 59;
         case 42 -> 37;
         case 43 -> 35;
         case 44 -> 49;
         case 45 -> 52;
         case 46 -> 27;
         case 47 -> 47;
         case 48 -> 14;
         case 49 -> 23;
         case 50 -> 12;
         case 51 -> 17;
         case 52 -> 24;
         case 53 -> 33;
         case 54 -> 60;
         case 55 -> 44;
         case 56 -> 5;
         case 57 -> 15;
         case 58 -> 10;
         case 59 -> 22;
         case 60 -> 20;
         case 61 -> 31;
         case 62 -> 0;
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

      e[var4] = new String(var13);
      return var4;
   
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

   private static native class_746 __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$2(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$4(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native MethodHandle __indy$7() throws Throwable;

   private static native Object __indy$8(MethodHandle var0, Object[] var1) throws Throwable;



private void __flow$2$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0242.c,2);f.pc=1;return;}
case 1:{f.push(106912219397455L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[1]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[1],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(87308200850882L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[3]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.push(-1173682118004309113L,2);f.pc=11;return;}
case 11:{f.push(f.locals[1],2);f.pc=12;return;}
case 12:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0242.__indy$0(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=13;return;}
case 13:{f.locals[5]=f.pop();f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.push(dev.zprestige.sourceview.S0226.b,1);f.pc=17;return;}
case 17:{f.push(-1173484649722408744L,2);f.pc=18;return;}
case 18:{f.push(f.locals[1],2);f.pc=19;return;}
case 19:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0242.__indy$1(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=20;return;}
case 20:{if(f.pop()!=null){f.pc=28;return;}f.pc=21;return;}
case 21:{f.done=true;return;}
case 22:{f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(-1173598344955534672L,2);f.pc=25;return;}
case 25:{f.push(f.locals[1],2);f.pc=26;return;}
case 26:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0242.__indy$2(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=27;return;}
case 27:{throw (Throwable)f.pop();}
case 28:{f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.push(f.locals[0],1);f.pc=32;return;}
case 32:{f.push(((dev.zprestige.sourceview.S0242)(Object)f.pop()).b,1);f.pc=33;return;}
case 33:{f.push(((dev.zprestige.sourceview.S0241)(Object)f.pop()).i,1);f.pc=34;return;}
case 34:{f.push(f.locals[5],1);f.pc=35;return;}
case 35:{if(f.pop()!=null){f.pc=68;return;}f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=63;return;}f.pc=38;return;}
case 38:{f.pc=39;return;}
case 39:{f.pc=46;return;}
case 40:{f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.push(-1173598344955534672L,2);f.pc=43;return;}
case 43:{f.push(f.locals[1],2);f.pc=44;return;}
case 44:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0242.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=45;return;}
case 45:{throw (Throwable)f.pop();}
case 46:{f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.push(f.locals[0],1);f.pc=49;return;}
case 49:{f.push(((dev.zprestige.sourceview.S0242)(Object)f.pop()).b,1);f.pc=50;return;}
case 50:{f.push(((dev.zprestige.sourceview.S0241)(Object)f.pop()).m,1);f.pc=51;return;}
case 51:{f.push(f.locals[0],1);f.pc=52;return;}
case 52:{f.push(-1175035006762961249L,2);f.pc=53;return;}
case 53:{f.push(f.locals[1],2);f.pc=54;return;}
case 54:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0242.__indy$4(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=55;return;}
case 55:{f.rearrange(87);f.pc=56;return;}
case 56:{f.done=true;return;}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.push(-1173598344955534672L,2);f.pc=60;return;}
case 60:{f.push(f.locals[1],2);f.pc=61;return;}
case 61:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0242.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=62;return;}
case 62:{throw (Throwable)f.pop();}
case 63:{f.pc=64;return;}
case 64:{f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.push(f.locals[0],1);f.pc=67;return;}
case 67:{f.push(((dev.zprestige.sourceview.S0242)(Object)f.pop()).a,1);f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.pc=70;return;}
case 70:{f.push(f.locals[3],2);f.pc=71;return;}
case 71:{f.push(2,1);f.pc=72;return;}
case 72:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=73;return;}
case 73:{f.rearrange(91);f.pc=74;return;}
case 74:{f.rearrange(91);f.pc=75;return;}
case 75:{f.rearrange(87);f.pc=76;return;}
case 76:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=77;return;}
case 77:{f.push(1,1);f.pc=78;return;}
case 78:{f.rearrange(95);f.pc=79;return;}
case 79:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=80;return;}
case 80:{f.rearrange(90);f.pc=81;return;}
case 81:{f.rearrange(95);f.pc=82;return;}
case 82:{Object a0=f.pop();f.push(java.lang.Integer.valueOf(dev.zprestige.sourceview.FlowFrame.i(a0)),1);f.pc=83;return;}
case 83:{f.push(0,1);f.pc=84;return;}
case 84:{f.rearrange(95);f.pc=85;return;}
case 85:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=86;return;}
case 86:{f.push(-1173635321465253221L,2);f.pc=87;return;}
case 87:{f.push(f.locals[1],2);f.pc=88;return;}
case 88:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0242.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.push(f.locals[0],1);f.pc=92;return;}
case 92:{f.push(((dev.zprestige.sourceview.S0242)(Object)f.pop()).b,1);f.pc=93;return;}
case 93:{f.push(1,1);f.pc=94;return;}
case 94:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0241.class,receiver,"i",(dev.zprestige.sourceview.FlowFrame.i(value) != 0));f.pc=95;return;}
case 95:{f.pc=96;return;}
case 96:{f.pc=97;return;}
case 97:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(5944569702043999055L,2);f.pc=1;return;}
case 1:{f.push(2032183825163213192L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(244241500070747L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0242.class,"c",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(17,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0242.class,"d",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(17,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0242.class,"e",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0242.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
