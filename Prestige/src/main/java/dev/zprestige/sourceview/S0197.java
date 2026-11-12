package dev.zprestige.sourceview;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import org.jetbrains.annotations.NotNull;

public class S0197 implements Iterable {
   public Set a;
   public Set b;
   public static long c;
   public static Object[] d;
   public static String[] e;

   
   public S0197(Object[] var1, long var2)  throws Throwable {
super();
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(4,6);
f.locals[0]=this;
f.locals[1]=var1;
f.locals[2]=var2;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$0$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public boolean e$return$e4b089a0(Object[] var1) {
try {

      Object var4 = var1[0];
      long var2 = (Long)(Object)(var1[1]);
      var2 = c ^ var2;
      return __indy$3(this.a, var4, 1343693252172222047L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @NotNull
   @Override
   public Iterator iterator() {
try {

      long var1 = c ^ 24669690716587L;
      return __indy$4(this.b, -1269784329172123086L, var1);
   
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

   public boolean b$return$e4b089a0(Object[] var1) {
try {

      Object var4 = var1[0];
      long var2 = (Long)(Object)(var1[1]);
      var2 = c ^ var2;
      return __indy$5(this.a, var4, -5977389318574444331L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public boolean c$return$e4b089a0(Object[] var1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(9,7);
f.locals[0]=this;
f.locals[1]=var1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$8$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
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
            var12 = b(70895674830919L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public boolean f$return$e4b089a0(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = c ^ var2;
      return __indy$11(this.a, -1345765270219890837L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public boolean d$return$e4b089a0(Object[] var1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(9,7);
f.locals[0]=this;
f.locals[1]=var1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$11$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
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
            var23 = b(70895674830919L, 0L);
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
            var23 = b(70895674830919L, 0L);
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
      return __indy$17(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 227 && var8 != 238 && var8 != 'N' && var8 != 'p') {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 243) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 181) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 227) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 238) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'N') {
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

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$18().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/cZ" + " : " + var1 + " : " + var2.toString(), var5);
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

   public Set a$return$93f6648b(Object[] var1) {
try {

      return this.b;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public int a$return$e4b0898f(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = c ^ var2;
      return __indy$19(this.a, 2326239022729536271L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = c ^ var2;
      __indy$20(this.a, 3192344759063819801L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public boolean a$return$e4b089a0(Object[] var1) {
try {

      Object var2 = var1[0];
      long var3 = (Long)(Object)(var1[1]);
      var3 = c ^ var3;
      return __indy$21(this.a, var2, 7725715426955577817L, var3);
   
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
         case 0 -> 35;
         case 1 -> 16;
         case 2 -> 22;
         case 3 -> 38;
         case 4 -> 55;
         case 5 -> 27;
         case 6 -> 7;
         case 7 -> 58;
         case 8 -> 20;
         case 9 -> 46;
         case 10 -> 23;
         case 11 -> 18;
         case 12 -> 48;
         case 13 -> 31;
         case 14 -> 43;
         case 15 -> 12;
         case 16 -> 61;
         case 17 -> 33;
         case 18 -> 10;
         case 19 -> 1;
         case 20 -> 0;
         case 21 -> 62;
         case 22 -> 39;
         case 23 -> 42;
         case 24 -> 13;
         case 25 -> 63;
         case 26 -> 56;
         case 27 -> 8;
         case 28 -> 49;
         case 29 -> 9;
         case 30 -> 30;
         case 31 -> 34;
         case 32 -> 60;
         case 33 -> 36;
         case 34 -> 11;
         case 35 -> 52;
         case 36 -> 54;
         case 37 -> 51;
         case 38 -> 50;
         case 39 -> 57;
         case 40 -> 53;
         case 41 -> 19;
         case 42 -> 44;
         case 43 -> 17;
         case 44 -> 21;
         case 45 -> 24;
         case 46 -> 14;
         case 47 -> 47;
         case 48 -> 37;
         case 49 -> 6;
         case 50 -> 45;
         case 51 -> 29;
         case 52 -> 26;
         case 53 -> 25;
         case 54 -> 28;
         case 55 -> 3;
         case 56 -> 5;
         case 57 -> 40;
         case 58 -> 4;
         case 59 -> 59;
         case 60 -> 41;
         case 61 -> 15;
         case 62 -> 2;
         default -> 32;
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

      d[0] = "\u0002\\C?k\n\u001cTYp\f\u000b\rOT**\r";
      d[1] = "8\fI0\rf3\u0003X\u007flh8\b\\%";
      d[2] = "\u0006\u001c\u001a\u001drc\u0010\u001c\u001fGat\u0007W\u001cAm`\u0016\u0010\u000bV&r*";
      d[3] = "JZ\u000b\u0007\u0012\u0018?z\u0000\b\u0003WBb\u0013\u000f\n\u001e*";
      d[4] = "\u0001oShgg\u001fgI'\u001aw\u001f";
      d[5] = boolean.class;
      e[5] = "java/lang/Boolean";
      d[6] = "A\u0001$ymd_\t>6\u0000~G\f7{7xD\u000e";
      d[7] = void.class;
      e[7] = "java/lang/Void";
      d[8] = "\u0007lKq\rP\u0019dQ>`J\u0001aXsWL\u0002cN";
      d[9] = "*ms>\u0015\t_Mx1\u0004F>Cs:\u0000\u001cJ";
      d[10] = "?\u0007rb\u0019\n!\u000fh-T\u0010;\u0005qqE\u001a;\u0012*@X\u00116\u0013vqR\u0011!.ep_24\u0016";
      d[11] = "L?VEsTR7L\n>NH=UV/DH*\u000eg2OE+RV8OR\u0016AW5lG.\u0004o8Xu;Tr4DQ";
      d[12] = int.class;
      e[12] = "java/lang/Integer";
      d[13] = "0oP\u001d\u0011U5yCOvOjtR FLzxTQJMe8?";
      d[14] = "Jg-\u0006p=Egliy4\u0005f\u001d\u0011#!Mgw\rz'u";
      d[15] = "x\u0007,GgRx\u0004u\u0012\u001bDAA*J+\u0013%\u0006!Xc.";
      d[16] = "iJpbN/9N%c5>8[ShQWh\tj7\r67\\ap5i6Nb0H17Ow\f\u000e,m\u000b#iR=iZ\u001a";
      d[17] = "k\"0\u0002\u0012vn4#Pum=.8A\u0018\u0017(w'\u0007\b}4.!?NliwfZ\u0012}m&_";
      d[18] = "X8}[Wx].n\t0x\u0018\u001cw\u001eLhcji_\f \u00066x[]\u0019";
      d[19] = "4\u000eh'rr1\u0018{u\u0015rs\n}situg8g%|`\u00037gd\u0013";
      d[20] = "0L3\u0018\u0005I?Lrw\nDtwn\u0007\u0016-4J:KSHh[>\u001aj";
      d[21] = "V|x1C Sjkc$*\tx~v$|Pwx1X\u007f\u0001~j\f";
      d[22] = "CU<O\u0012*FC/\u001du \u001fZ/\u001b\u0014-\u0003<+N\rs\u0005V7\u0017\u000bKCGjNL.\u001fVn\u001fu";
      d[23] = "j\u0015\r`,i,\u0011\t!\u001d?8\u0003w3l\u00028\u00004n\"0*L\no\"&m|";
      d[24] = "\u000fV.hE$\n@=:\",XSA-\u001e=\fB+1G;4\u0004:l\u001e|QX+hOE";
      d[25] = "\t,L\u0015XoVyGR`oI\u007fFG\u001ciO\u0012\u0003SPaZv\fS\u0011\u000e";
      d[26] = "?\u001d\u000f\u0019\u0000\u0007o\u0019Z\u0018{\u0002d\u0005\u0002\u001b\u001a\u0011c\t\u000f\u0013\u0016$o\u001ceL\u0012\u0010?\u0007\u0004I\u0004\u0003m`^\u001e\u0014Be\u0001[\b\u0007\u0010\u0002";
   
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

   private static native KeySetView __indy$0(long var0, long var2) throws Throwable;

   private static native Set __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$2(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Iterator __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String[] __indy$6(long var0, long var2) throws Throwable;

   private static native Iterator __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$10(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$12(long var0, long var2) throws Throwable;

   private static native Iterator __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$16(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$17(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$18() throws Throwable;

   private static native int __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$21(Object var0, Object var1, long var2, long var4) throws Throwable;







private void __flow$0$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0197.c,2);f.pc=1;return;}
case 1:{f.push(f.locals[2],2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.pc=5;return;}
case 5:{f.pc=6;return;}
case 6:{f.push(f.locals[0],1);f.pc=7;return;}
case 7:{Object receiver=f.pop();if(receiver!=this)f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{f.push(8171779539112000693L,2);f.pc=12;return;}
case 12:{f.push(f.locals[2],2);f.pc=13;return;}
case 13:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$0(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=14;return;}
case 14:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0197.class,receiver,"a",((java.util.Set)(Object)(value)));f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.push(f.locals[0],1);f.pc=18;return;}
case 18:{f.push(f.locals[0],1);f.pc=19;return;}
case 19:{f.push(((dev.zprestige.sourceview.S0197)(Object)f.pop()).a,1);f.pc=20;return;}
case 20:{f.push(8170850793525005920L,2);f.pc=21;return;}
case 21:{f.push(f.locals[2],2);f.pc=22;return;}
case 22:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$1(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=23;return;}
case 23:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0197.class,receiver,"b",((java.util.Set)(Object)(value)));f.pc=24;return;}
case 24:{f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.push(f.locals[0],1);f.pc=27;return;}
case 27:{f.push(((dev.zprestige.sourceview.S0197)(Object)f.pop()).a,1);f.pc=28;return;}
case 28:{f.push(f.locals[1],1);f.pc=29;return;}
case 29:{f.push(8171264414682090356L,2);f.pc=30;return;}
case 30:{f.push(f.locals[2],2);f.pc=31;return;}
case 31:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$2(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=32;return;}
case 32:{f.rearrange(87);f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$8$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.util.Collection)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[2]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[3]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0197.c,2);f.pc=14;return;}
case 14:{f.push(f.locals[3],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[3]=f.pop();f.pc=17;return;}
case 17:{f.push(-7673920542003819572L,2);f.pc=18;return;}
case 18:{f.push(f.locals[3],2);f.pc=19;return;}
case 19:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$6(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(0,1);f.pc=23;return;}
case 23:{f.locals[6]=f.pop();f.pc=24;return;}
case 24:{f.locals[5]=f.pop();f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.push(f.locals[2],1);f.pc=28;return;}
case 28:{f.push(-7672614649730070841L,2);f.pc=29;return;}
case 29:{f.push(f.locals[3],2);f.pc=30;return;}
case 30:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=31;return;}
case 31:{f.locals[7]=f.pop();f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.push(f.locals[7],1);f.pc=35;return;}
case 35:{f.push(-7672418718544831024L,2);f.pc=36;return;}
case 36:{f.push(f.locals[3],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=61;return;}f.pc=39;return;}
case 39:{f.push(f.locals[7],1);f.pc=40;return;}
case 40:{f.push(-7673971134232185979L,2);f.pc=41;return;}
case 41:{f.push(f.locals[3],2);f.pc=42;return;}
case 42:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=43;return;}
case 43:{f.locals[8]=f.pop();f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.push(f.locals[6],1);f.pc=47;return;}
case 47:{f.push(f.locals[0],1);f.pc=48;return;}
case 48:{f.push(((dev.zprestige.sourceview.S0197)(Object)f.pop()).a,1);f.pc=49;return;}
case 49:{f.push(f.locals[8],1);f.pc=50;return;}
case 50:{f.push(-7672685982119446677L,2);f.pc=51;return;}
case 51:{f.push(f.locals[3],2);f.pc=52;return;}
case 52:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$10(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=53;return;}
case 53:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a|b,1);f.pc=54;return;}
case 54:{f.push(f.locals[5],1);f.pc=55;return;}
case 55:{if(f.pop()!=null){f.pc=65;return;}f.pc=56;return;}
case 56:{f.locals[6]=f.pop();f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.push(f.locals[5],1);f.pc=60;return;}
case 60:{if(f.pop()==null){f.pc=32;return;}f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.push(f.locals[6],1);f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$11$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.util.Collection)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[4]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0197.c,2);f.pc=14;return;}
case 14:{f.push(f.locals[2],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.push(-774104798271935731L,2);f.pc=18;return;}
case 18:{f.push(f.locals[2],2);f.pc=19;return;}
case 19:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$12(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(0,1);f.pc=23;return;}
case 23:{f.locals[6]=f.pop();f.pc=24;return;}
case 24:{f.locals[5]=f.pop();f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.push(f.locals[4],1);f.pc=28;return;}
case 28:{f.push(-773361819342805498L,2);f.pc=29;return;}
case 29:{f.push(f.locals[2],2);f.pc=30;return;}
case 30:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=31;return;}
case 31:{f.locals[7]=f.pop();f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.push(f.locals[7],1);f.pc=35;return;}
case 35:{f.push(-772642256241115887L,2);f.pc=36;return;}
case 36:{f.push(f.locals[2],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=61;return;}f.pc=39;return;}
case 39:{f.push(f.locals[7],1);f.pc=40;return;}
case 40:{f.push(-774194947151342780L,2);f.pc=41;return;}
case 41:{f.push(f.locals[2],2);f.pc=42;return;}
case 42:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=43;return;}
case 43:{f.locals[8]=f.pop();f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.push(f.locals[6],1);f.pc=47;return;}
case 47:{f.push(f.locals[0],1);f.pc=48;return;}
case 48:{f.push(((dev.zprestige.sourceview.S0197)(Object)f.pop()).a,1);f.pc=49;return;}
case 49:{f.push(f.locals[8],1);f.pc=50;return;}
case 50:{f.push(-772801744926365537L,2);f.pc=51;return;}
case 51:{f.push(f.locals[2],2);f.pc=52;return;}
case 52:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0197.__indy$16(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=53;return;}
case 53:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a|b,1);f.pc=54;return;}
case 54:{f.push(f.locals[5],1);f.pc=55;return;}
case 55:{if(f.pop()!=null){f.pc=65;return;}f.pc=56;return;}
case 56:{f.locals[6]=f.pop();f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.push(f.locals[5],1);f.pc=60;return;}
case 60:{if(f.pop()==null){f.pc=32;return;}f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.push(f.locals[6],1);f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(647733351790209687L,2);f.pc=1;return;}
case 1:{f.push(-1379091143787204390L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(280919188952181L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0197.class,"c",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(27,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0197.class,"d",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(27,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0197.class,"e",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0197.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
