package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class S0364 {
   public long a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   public S0364(long var1)  throws Throwable {
      var1 = b ^ var1;
      super();
try {

      this.a = __indy$0(3190040142573676398L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
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

   public boolean b$return$e4b089a0(Object[] var1) {
try {

      S0255 var2 = (S0255)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = b ^ var3;
      long var5 = var3 ^ 19613694202557L;
      float var10001 = __indy$1(var2, new Object[0], -5354010982094728986L, var3);
      Object[] var10004 = new Object[]{null, var5};
      var10004[0] = var10001;
      return __indy$2(this, var10004, -5354119062310747259L, var3);
   
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
            var12 = b(777260133085729L, 0L);
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
            var23 = b(777260133085729L, 0L);
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
            var23 = b(777260133085729L, 0L);
         }
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

   public boolean a$return$e4b089a0(Object[] var1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(6,5);
f.locals[0]=this;
f.locals[1]=var1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$9$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=22&&f.pc<36&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=38;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
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
               MethodHandles.insertArguments(__indy$7().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/f5" + " : " + var1 + " : " + var2.toString(), var5);
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
         if (var8 != 202 && var8 != 'Y' && var8 != 164 && var8 != 211) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 231) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 219) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 202) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'Y') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 164) {
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
         case 0 -> 63;
         case 1 -> 40;
         case 2 -> 48;
         case 3 -> 26;
         case 4 -> 33;
         case 5 -> 61;
         case 6 -> 2;
         case 7 -> 47;
         case 8 -> 38;
         case 9 -> 62;
         case 10 -> 0;
         case 11 -> 14;
         case 12 -> 4;
         case 13 -> 21;
         case 14 -> 42;
         case 15 -> 24;
         case 16 -> 37;
         case 17 -> 1;
         case 18 -> 50;
         case 19 -> 25;
         case 20 -> 17;
         case 21 -> 18;
         case 22 -> 46;
         case 23 -> 7;
         case 24 -> 56;
         case 25 -> 35;
         case 26 -> 5;
         case 27 -> 51;
         case 28 -> 8;
         case 29 -> 60;
         case 30 -> 15;
         case 31 -> 53;
         case 32 -> 6;
         case 33 -> 27;
         case 34 -> 41;
         case 35 -> 58;
         case 36 -> 11;
         case 37 -> 16;
         case 38 -> 45;
         case 39 -> 44;
         case 40 -> 31;
         case 41 -> 34;
         case 42 -> 57;
         case 43 -> 36;
         case 44 -> 12;
         case 45 -> 32;
         case 46 -> 10;
         case 47 -> 30;
         case 48 -> 20;
         case 49 -> 22;
         case 50 -> 43;
         case 51 -> 13;
         case 52 -> 55;
         case 53 -> 49;
         case 54 -> 19;
         case 55 -> 54;
         case 56 -> 29;
         case 57 -> 59;
         case 58 -> 23;
         case 59 -> 3;
         case 60 -> 39;
         case 61 -> 52;
         case 62 -> 9;
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

      d[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      c[0] = "n7\u0011>mEx7\u0014d~Ro|\u0017brF~;\u0000u9S?";
      c[1] = "-\u001ezNE!X>qATn90zJP4M";
      c[2] = boolean.class;
      d[2] = "java/lang/Boolean";
      c[3] = "{\u00068BNJm\u0006=\u0018]]zM>\u001eQIk\n)\t\u001a^N";
      c[4] = "\u0007\u001c\u001ds;(r<\u0016|*g\u00132\u001dw.=g";
      c[5] = float.class;
      d[5] = "java/lang/Float";
      c[6] = " \u000b<\u0013^\u00176\u000b9IM\u0000!@:OA\u00140\u0007-X\n\u0006\f";
      c[7] = "K@vi\"$>`}f3kCxna:\"+";
      c[8] = "i\u0016\u000bLEob\u0019\u001a\u0003&bw\u0014\u0015h\u0013`f\u0007\tD\u0004m";
      c[9] = "i\u0019>[tdb\u0016/\u0014\tqp\f-W";
      c[10] = long.class;
      d[10] = "java/lang/Long";
      c[11] = "#jK\u0010~a(eZ_\u001fo#n^\u0005";
      c[12] = "!\rr5H\ro\u00010w&\u001b\u001dWy+\u001f\u0018-R%tIq";
      c[13] = "sDgK\u000b2tR)t[\n,W{J\r{&Fm\u000423s\u0017&\u0006Su+R)t";
      c[14] = "Wie\u0012\u007fxV21\u0014\u0019{mhv\u0018')\u001cbg\u000ei\u0016V,:OenV>w\u001c\u0019";
      c[15] = "eqU\u001fZlbg\u001b \nT=}O_^i9'\u001aYcicu[\u001d^m9 ] ";
      c[16] = "\u001b^\nW#TI\nIS\u001cNXI\u0002\u0003zYyR\u001d\u0003YDAW\u0019\u0015\u001c\u001a\u001fB\u001c\f&\\L\rFn";
   
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

   public float a$return$e4b0898c(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = b ^ var2;
      return (float)(__indy$8(5272152699970863617L, var2) - this.a);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = b ^ var2;
      this.a = __indy$9(-4015787681231682706L, var2);
   
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

   private static native long __indy$0(long var0, long var2) throws Throwable;

   private static native float __indy$1(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$2(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$3(MethodHandle var0, Object[] var1) throws Throwable;

   private static native String[] __indy$4(long var0, long var2) throws Throwable;

   private static native long __indy$5(long var0, long var2) throws Throwable;

   private static native MatchException __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native MethodHandle __indy$7() throws Throwable;

   private static native long __indy$8(long var0, long var2) throws Throwable;

   private static native long __indy$9(long var0, long var2) throws Throwable;



private void __flow$9$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.lang.Float)(Object)f.pop(),1);f.pc=5;return;}
case 5:{Object receiver=f.pop();f.push(((java.lang.Float)(Object)receiver).floatValue(),1);f.pc=6;return;}
case 6:{f.locals[4]=f.pop();f.pc=7;return;}
case 7:{f.rearrange(89);f.pc=8;return;}
case 8:{f.push(1,1);f.pc=9;return;}
case 9:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=10;return;}
case 10:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=11;return;}
case 11:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=12;return;}
case 12:{f.locals[2]=f.pop();f.pc=13;return;}
case 13:{f.rearrange(87);f.pc=14;return;}
case 14:{f.push(dev.zprestige.sourceview.S0364.b,2);f.pc=15;return;}
case 15:{f.push(f.locals[2],2);f.pc=16;return;}
case 16:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=17;return;}
case 17:{f.locals[2]=f.pop();f.pc=18;return;}
case 18:{f.push(-1093434768232970906L,2);f.pc=19;return;}
case 19:{f.push(f.locals[2],2);f.pc=20;return;}
case 20:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0364.__indy$4(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=21;return;}
case 21:{f.locals[5]=f.pop();f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(-1093150724309844993L,2);f.pc=25;return;}
case 25:{f.push(f.locals[2],2);f.pc=26;return;}
case 26:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0364.__indy$5(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),2);f.pc=27;return;}
case 27:{f.push(f.locals[0],1);f.pc=28;return;}
case 28:{f.push(((dev.zprestige.sourceview.S0364)(Object)f.pop()).a,2);f.pc=29;return;}
case 29:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a-b,2);f.pc=30;return;}
case 30:{f.push((float)(dev.zprestige.sourceview.FlowFrame.j(f.pop())),1);f.pc=31;return;}
case 31:{f.push(f.locals[4],1);f.pc=32;return;}
case 32:{float b=dev.zprestige.sourceview.FlowFrame.f(f.pop()),a=dev.zprestige.sourceview.FlowFrame.f(f.pop());f.push(dev.zprestige.sourceview.FlowFrame.compareFloat(a,b,-1),1);f.pc=33;return;}
case 33:{f.push(f.locals[5],1);f.pc=34;return;}
case 34:{if(f.pop()!=null){f.pc=47;return;}f.pc=35;return;}
case 35:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())<=0){f.pc=50;return;}f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=44;return;}
case 38:{f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.push(-1093356861567967166L,2);f.pc=41;return;}
case 41:{f.push(f.locals[2],2);f.pc=42;return;}
case 42:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0364.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=43;return;}
case 43:{throw (Throwable)f.pop();}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.push(1,1);f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.pc=53;return;}
case 50:{f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{f.push(0,1);f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(2193087737990763533L,2);f.pc=1;return;}
case 1:{f.push(1320054179505560519L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(91757236295433L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0364.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(17,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0364.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(17,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0364.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0364.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
