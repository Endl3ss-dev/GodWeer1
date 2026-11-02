package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0004;
import dev.zprestige.sourceview.S0005;
import dev.zprestige.sourceview.S0225;
import dev.zprestige.sourceview.S0549;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.class_1923;
import net.minecraft.class_2791;
import net.minecraft.class_631;
import net.minecraft.class_638;
import net.minecraft.class_631.class_3681;

public class S0084 implements Iterator, S0225 {
   public S0005 a;
   public boolean c;
   public int d;
   public class_2791 e;
   public static long f;
   public static Object[] g;
   public static String[] h;

   public S0084(boolean var1, long var2)  throws Throwable {
      var2 = f ^ var2;
      long var4 = var2 ^ 118726270478236L;
      long var6 = var2 ^ 106313485447367L;
      super();
try {

      this.a = new S0005(
         __indy$2(new S0004(__indy$1(__indy$0(b, 8731666619727991839L, var2), 8731634274187763205L, var2)), new Object[]{var6}, 8729036365076705934L, var2)
      );
      this.d = 0;
      this.c = var1;
      __indy$3(this, new Object[]{var4}, 8728953496671294967L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

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
      if (h[var4] != null) {
         return var4;
      }

      Object var5 = g[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 58;
         case 1 -> 35;
         case 2 -> 33;
         case 3 -> 34;
         case 4 -> 48;
         case 5 -> 19;
         case 6 -> 20;
         case 7 -> 22;
         case 8 -> 60;
         case 9 -> 2;
         case 10 -> 62;
         case 11 -> 25;
         case 12 -> 16;
         case 13 -> 49;
         case 14 -> 17;
         case 15 -> 36;
         case 16 -> 57;
         case 17 -> 18;
         case 18 -> 59;
         case 19 -> 55;
         case 20 -> 52;
         case 21 -> 56;
         case 22 -> 45;
         case 23 -> 10;
         case 24 -> 14;
         case 25 -> 29;
         case 26 -> 53;
         case 27 -> 31;
         case 28 -> 54;
         case 29 -> 3;
         case 30 -> 8;
         case 31 -> 43;
         case 32 -> 32;
         case 33 -> 39;
         case 34 -> 42;
         case 35 -> 27;
         case 36 -> 9;
         case 37 -> 5;
         case 38 -> 61;
         case 39 -> 46;
         case 40 -> 1;
         case 41 -> 12;
         case 42 -> 50;
         case 43 -> 41;
         case 44 -> 40;
         case 45 -> 6;
         case 46 -> 0;
         case 47 -> 63;
         case 48 -> 15;
         case 49 -> 11;
         case 50 -> 24;
         case 51 -> 21;
         case 52 -> 38;
         case 53 -> 23;
         case 54 -> 30;
         case 55 -> 26;
         case 56 -> 13;
         case 57 -> 47;
         case 58 -> 7;
         case 59 -> 4;
         case 60 -> 44;
         case 61 -> 51;
         case 62 -> 28;
         default -> 37;
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

      h[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public boolean hasNext() {
try {

      try {
         if (this.e != null) {
            return true;
         }
      } catch (MatchException var1) {
         throw a(var1);
      }

      return false;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public Object next() {
try {

      long var1 = f ^ 120815915125781L;
      long var3 = var1 ^ 84768326185068L;
      return __indy$4(this, new Object[]{var3}, -1215297936083330342L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = b(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$5(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'd' && var8 != 'r' && var8 != 214 && var8 != 186) {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 246) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 227) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = g(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'd') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'r') {
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

   public class_2791 b$return$270e891(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      var2 = f ^ var2;
      long var4 = var2 ^ 61144889762510L;
      return __indy$6(this, new Object[]{var4}, 4355302526136701093L, var2);
   
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

   public static Method h(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = g[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = h[var4];
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
            g[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = f(599015012584241L, 0L);
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
                  g[var4] = var19;
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
            var23 = f(599015012584241L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class f(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = e(var0, var2);
      Object var6 = g[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(h[var4]);
            g[var4] = var5;
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
         throw new RuntimeException("dev/zprestige/prestige/as" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_2791 a$return$270e891(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(10,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$15$0(f);break;
case 1:__flow$15$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=60&&f.pc<97&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=99;continue;}
if(f.pc>=108&&f.pc<115&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=117;continue;}
if(f.pc>=113&&f.pc<146&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=148;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return ((class_2791)(Object)f.result);
}


   public boolean a$return$e4b089a0(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(8,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$16$0(f);break;
case 1:__flow$16$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=41&&f.pc<61&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=63;continue;}
if(f.pc>=59&&f.pc<85&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=87;continue;}
if(f.pc>=93&&f.pc<99&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=101;continue;}
if(f.pc>=97&&f.pc<123&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=125;continue;}
if(f.pc>=131&&f.pc<137&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=139;continue;}
if(f.pc>=135&&f.pc<161&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=163;continue;}
if(f.pc>=169&&f.pc<174&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=176;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
}



   public static void a$return$9b75() {
try {

      g[0] = "\u0014\u0012$\u0010\n\u0010\u0002\u0012!J\u0019\u0007\u0015Y\"L\u0015\u0013\u0004\u001e5[^\u0001\u0003";
      g[1] = "_r\u001c\to\\*R\u0017\u0006~\u0013K\\\u001c\rzI?";
      g[2] = "\u0010'/y\u0005 \u0010'8%\t/\nl8;\t:\r\u001di`Qx";
      g[3] = "Q$!^{H$\u0004*Qj\u0007E\n!Zn]1";
      g[4] = "\u0014Ur\u001f\tt\u0002UwE\u001ac\u0015\u001etC\u0016w\u0004YcT]e8";
      g[5] = "g<x.\u0002\u007f\u0012\u001cs!\u00130o\u0004`&\u001ay\u0007";
      g[6] = "U*D\u000f\u007fqK\"^@2kQ(G\u001c#aQ?\u001c\u000f%kR\"Q@\u0010pP&[\r\u0003aY.@\u000b?gZ\n@\u001c0}";
      g[7] = int.class;
      h[7] = "java/lang/Integer";
      g[8] = "7uZ\ft\u0019<zKC\u0015\u00177qO\u0019";
      g[9] = "\u0015\u0011Xbj\u0011\u0003\u0011]8y\u0006\u0014Z^>u\u0012\u0005\u001dI)>$";
      g[10] = "ZlZ\u0001s\u0010/LQ\u000eb_NBZ\u0005f\u0005:";
      g[11] = "/O*yrlZo!vc#;a*}gyO";
      g[12] = boolean.class;
      h[12] = "java/lang/Boolean";
      g[13] = "K%Ek{o@*T$\u0018bU'[O-`D4Gc:m";
      g[14] = "0pf\u0003}|0pq_qs*;qAqf-J#\u0014\"&";
      g[15] = "Y<\u000e\u0004/@Y<\u0019X#OCw\u0019F#ZD\u0006I\u001br";
      g[16] = "\u0018A5cU\u001c\u0018A\"?Y\u0013\u0002\n\"!Y\u0006\u0005{w~\u0000";
      g[17] = "W;o\u0007ZKW;x[VDMpxEVQJ\u0001-\u001a\u0006";
      g[18] = ",vBu\u001dN:vG/\u000eY-=D)\u0002M<zS>Iz";
      g[19] = "Ks\u001f@G7>S\u0014OVx_]\u001fDR\"+";
      g[20] = "q@r\u0012s\u0005q@eN\u007f\nk\u000beP\u007f\u001flz0\u000f/H|IgOm3,\u0013>\r";
      g[21] = ">y\u000e>\u000e\r<*\u0017n2S6.\u00178e\fnsKT\u000eM8#C$L^>r";
      g[22] = "X\u0004C\u0010>\u0006\u0002\tH\u0019\u0001]c\u0003B\u0019xZ\u0004T\u0006Bk7";
      g[23] = "\u0013\u0016)&\u00038T\u0013\u007f.blB\u0003\u0013{\u0012lOOc9\u0001j\u001e\u007f.8\bqWC\u007f<\u001e\u007f/";
      g[24] = "\u0019\b}L\tBA\u00120Dv@'\u0005$_\r[[QsH\u0017)\u001b\u0000xT\u0017\u0012\\\u0005.\\v";
      g[25] = "\u0014s[ByK\u0002lZSD@\u001cc^D\u0013\u0017C>\u0005(.J\u0003r]W?L\fb";
      g[26] = "Mo\u0006H'l\u0016f\r!tR\u001ac\u0017Zo.N4\u0000@\u001db\u0010<\u0000Xq#\u001f;\u001e!";
      g[27] = "R:S'\u0001'\u0015?\u0005/`x\u00035\u0006:\u0000\u001cR#\u0006&Pl\u00100\u0000w`";
      g[28] = "i(\\D\u0016R2!W-Elc}EVU\u0016-?K\u0016,]m \\TV\u0013/.\u001c-";
      g[29] = "\u0003\u007f(\u0003oGXv#j?yTs9\u0011'\u0005\u0000$.\u000bU@\\lo\u0003<G@(:j";
      g[30] = "2h\n4Ay#n\u0005$&c5p\u0014/JQb=Ou&mbjL!]|*rOH";
      g[31] = "a&V>\u001bMf:\u0012krA5>Oe\u001esax\u0017:N$1xRhN\u00143+K8r";
      g[32] = "YvOC\u0004\u000f\u0002\u007fD*W1\u000ez^QLMZ-IK>\b\u0006e\bCW\u000f\u001a!]*";
      g[33] = "a[1dEBp\u0013)g,\\g\u001d7;@n3[nf\u001796\u00118<\u001cIt\u0002>m,\u0005z\u000e7l\\Gi\bf\\\u001c_3\u001c.0]P4\u0002W";
      g[34] = "z&\r,oAn!\u001a\"\u001fD\u00116\u0000/d_mbW8~-}$\u0017?gB~b\u001a$\u001f";
      g[35] = " QRvJc\"\u0002K&v=(\u0006Kp!bp[\u0016\u001cJ#&\u000b\u001fl\b0 Z";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field g(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = g[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = h[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = f(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = c(var12, var10, var11);
         if (var13 != null) {
            g[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = d(var14[var15], var10, var11);
               if (var13 != null) {
                  g[var4] = var13;
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
            var12 = f(599015012584241L, 0L);
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

   private static native class_638 __indy$0(Object var0, long var1, long var3) throws Throwable;

   private static native class_631 __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native class_3681 __indy$2(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_2791 __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_2791 __indy$4(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$5(MethodHandle var0, Object[] var1) throws Throwable;

   private static native class_2791 __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$7() throws Throwable;

   private static native String[] __indy$8(long var0, long var2) throws Throwable;

   private static native AtomicReferenceArray __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native int __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native AtomicReferenceArray __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$12(Object var0, int var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native class_1923 __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$19(long var0, long var2) throws Throwable;

   private static native class_1923 __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native class_631 __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$24(Object var0, int var1, int var2, long var3, long var5) throws Throwable;

   private static native MatchException __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native class_631 __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$28(Object var0, int var1, int var2, long var3, long var5) throws Throwable;

   private static native MatchException __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native class_631 __indy$32(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$33(Object var0, int var1, int var2, long var3, long var5) throws Throwable;

   private static native MatchException __indy$34(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native class_638 __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native class_631 __indy$37(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$38(Object var0, int var1, int var2, long var3, long var5) throws Throwable;

   private static native MatchException __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$40(Object var0, long var1, long var3) throws Throwable;





private void __flow$16$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((net.minecraft.class_2791)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[4]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0084.f,2);f.pc=14;return;}
case 14:{f.push(f.locals[2],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.push(f.locals[4],1);f.pc=20;return;}
case 20:{f.push(-1826913181135571462L,2);f.pc=21;return;}
case 21:{f.push(f.locals[2],2);f.pc=22;return;}
case 22:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=23;return;}
case 23:{f.push(-1825477898454948800L,2);f.pc=24;return;}
case 24:{f.push(f.locals[2],2);f.pc=25;return;}
case 25:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=26;return;}
case 26:{f.locals[6]=f.pop();f.pc=27;return;}
case 27:{f.push(-1826240159839084058L,2);f.pc=28;return;}
case 28:{f.push(f.locals[2],2);f.pc=29;return;}
case 29:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$19(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.push(f.locals[4],1);f.pc=33;return;}
case 33:{f.push(-1826913181135571462L,2);f.pc=34;return;}
case 34:{f.push(f.locals[2],2);f.pc=35;return;}
case 35:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=36;return;}
case 36:{f.push(-1826435035662147764L,2);f.pc=37;return;}
case 37:{f.push(f.locals[2],2);f.pc=38;return;}
case 38:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=39;return;}
case 39:{f.locals[7]=f.pop();f.pc=40;return;}
case 40:{f.locals[5]=f.pop();f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.push(dev.zprestige.sourceview.S0084.b,1);f.pc=44;return;}
case 44:{f.push(-1827313721772782698L,2);f.pc=45;return;}
case 45:{f.push(f.locals[2],2);f.pc=46;return;}
case 46:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=47;return;}
case 47:{f.push(-1826789911679024756L,2);f.pc=48;return;}
case 48:{f.push(f.locals[2],2);f.pc=49;return;}
case 49:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=50;return;}
case 50:{f.push(f.locals[6],1);f.pc=51;return;}
case 51:{f.push(1,1);f.pc=52;return;}
case 52:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=53;return;}
case 53:{f.push(f.locals[7],1);f.pc=54;return;}
case 54:{f.push(-1825608823325275449L,2);f.pc=55;return;}
case 55:{f.push(f.locals[2],2);f.pc=56;return;}
case 56:{Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$24(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.i(a2),dev.zprestige.sourceview.FlowFrame.j(a3),dev.zprestige.sourceview.FlowFrame.j(a4)),1);f.pc=57;return;}
case 57:{f.push(f.locals[5],1);f.pc=58;return;}
case 58:{if(f.pop()!=null){f.pc=93;return;}f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=188;return;}f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=69;return;}
case 63:{f.pc=64;return;}
case 64:{f.pc=65;return;}
case 65:{f.push(-1826974226350900137L,2);f.pc=66;return;}
case 66:{f.push(f.locals[2],2);f.pc=67;return;}
case 67:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=68;return;}
case 68:{throw (Throwable)f.pop();}
case 69:{f.pc=70;return;}
case 70:{f.pc=71;return;}
case 71:{f.push(dev.zprestige.sourceview.S0084.b,1);f.pc=72;return;}
case 72:{f.push(-1827313721772782698L,2);f.pc=73;return;}
case 73:{f.push(f.locals[2],2);f.pc=74;return;}
case 74:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=75;return;}
case 75:{f.push(-1826789911679024756L,2);f.pc=76;return;}
case 76:{f.push(f.locals[2],2);f.pc=77;return;}
case 77:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=78;return;}
case 78:{f.push(f.locals[6],1);f.pc=79;return;}
case 79:{f.push(1,1);f.pc=80;return;}
case 80:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a-b,1);f.pc=81;return;}
case 81:{f.push(f.locals[7],1);f.pc=82;return;}
case 82:{f.push(-1825608823325275449L,2);f.pc=83;return;}
case 83:{f.push(f.locals[2],2);f.pc=84;return;}
case 84:{Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.i(a2),dev.zprestige.sourceview.FlowFrame.j(a3),dev.zprestige.sourceview.FlowFrame.j(a4)),1);f.pc=85;return;}
case 85:{f.pc=86;return;}
case 86:{f.pc=93;return;}
case 87:{f.pc=88;return;}
case 88:{f.pc=89;return;}
case 89:{f.push(-1826974226350900137L,2);f.pc=90;return;}
case 90:{f.push(f.locals[2],2);f.pc=91;return;}
case 91:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$29(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=92;return;}
case 92:{throw (Throwable)f.pop();}
case 93:{f.pc=94;return;}
case 94:{f.pc=95;return;}
case 95:{f.push(f.locals[5],1);f.pc=96;return;}
case 96:{if(f.pop()!=null){f.pc=131;return;}f.pc=97;return;}
case 97:{f.pc=98;return;}
case 98:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=188;return;}f.pc=99;return;}
case 99:{f.pc=100;return;}
case 100:{f.pc=107;return;}
case 101:{f.pc=102;return;}
case 102:{f.pc=103;return;}
case 103:{f.push(-1826974226350900137L,2);f.pc=104;return;}
case 104:{f.push(f.locals[2],2);f.pc=105;return;}
case 105:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$30(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=106;return;}
case 106:{throw (Throwable)f.pop();}
case 107:{f.pc=108;return;}
case 108:{f.pc=109;return;}
case 109:{f.push(dev.zprestige.sourceview.S0084.b,1);f.pc=110;return;}
case 110:{f.push(-1827313721772782698L,2);f.pc=111;return;}
case 111:{f.push(f.locals[2],2);f.pc=112;return;}
case 112:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$31(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=113;return;}
case 113:{f.push(-1826789911679024756L,2);f.pc=114;return;}
case 114:{f.push(f.locals[2],2);f.pc=115;return;}
case 115:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$32(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=116;return;}
case 116:{f.push(f.locals[6],1);f.pc=117;return;}
case 117:{f.push(f.locals[7],1);f.pc=118;return;}
case 118:{f.push(1,1);f.pc=119;return;}
case 119:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$16$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.push(-1825608823325275449L,2);f.pc=121;return;}
case 121:{f.push(f.locals[2],2);f.pc=122;return;}
case 122:{Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$33(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.i(a2),dev.zprestige.sourceview.FlowFrame.j(a3),dev.zprestige.sourceview.FlowFrame.j(a4)),1);f.pc=123;return;}
case 123:{f.pc=124;return;}
case 124:{f.pc=131;return;}
case 125:{f.pc=126;return;}
case 126:{f.pc=127;return;}
case 127:{f.push(-1826974226350900137L,2);f.pc=128;return;}
case 128:{f.push(f.locals[2],2);f.pc=129;return;}
case 129:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$34(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=130;return;}
case 130:{throw (Throwable)f.pop();}
case 131:{f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.push(f.locals[5],1);f.pc=134;return;}
case 134:{if(f.pop()!=null){f.pc=169;return;}f.pc=135;return;}
case 135:{f.pc=136;return;}
case 136:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=188;return;}f.pc=137;return;}
case 137:{f.pc=138;return;}
case 138:{f.pc=145;return;}
case 139:{f.pc=140;return;}
case 140:{f.pc=141;return;}
case 141:{f.push(-1826974226350900137L,2);f.pc=142;return;}
case 142:{f.push(f.locals[2],2);f.pc=143;return;}
case 143:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$35(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=144;return;}
case 144:{throw (Throwable)f.pop();}
case 145:{f.pc=146;return;}
case 146:{f.pc=147;return;}
case 147:{f.push(dev.zprestige.sourceview.S0084.b,1);f.pc=148;return;}
case 148:{f.push(-1827313721772782698L,2);f.pc=149;return;}
case 149:{f.push(f.locals[2],2);f.pc=150;return;}
case 150:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$36(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=151;return;}
case 151:{f.push(-1826789911679024756L,2);f.pc=152;return;}
case 152:{f.push(f.locals[2],2);f.pc=153;return;}
case 153:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$37(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=154;return;}
case 154:{f.push(f.locals[6],1);f.pc=155;return;}
case 155:{f.push(f.locals[7],1);f.pc=156;return;}
case 156:{f.push(1,1);f.pc=157;return;}
case 157:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a-b,1);f.pc=158;return;}
case 158:{f.push(-1825608823325275449L,2);f.pc=159;return;}
case 159:{f.push(f.locals[2],2);f.pc=160;return;}
case 160:{Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$38(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.i(a2),dev.zprestige.sourceview.FlowFrame.j(a3),dev.zprestige.sourceview.FlowFrame.j(a4)),1);f.pc=161;return;}
case 161:{f.pc=162;return;}
case 162:{f.pc=169;return;}
case 163:{f.pc=164;return;}
case 164:{f.pc=165;return;}
case 165:{f.push(-1826974226350900137L,2);f.pc=166;return;}
case 166:{f.push(f.locals[2],2);f.pc=167;return;}
case 167:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$39(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=168;return;}
case 168:{throw (Throwable)f.pop();}
case 169:{f.pc=170;return;}
case 170:{f.pc=171;return;}
case 171:{f.push(f.locals[5],1);f.pc=172;return;}
case 172:{if(f.pop()!=null){f.pc=185;return;}f.pc=173;return;}
case 173:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=188;return;}f.pc=174;return;}
case 174:{f.pc=175;return;}
case 175:{f.pc=182;return;}
case 176:{f.pc=177;return;}
case 177:{f.pc=178;return;}
case 178:{f.push(-1826974226350900137L,2);f.pc=179;return;}
case 179:{f.push(f.locals[2],2);f.pc=180;return;}
case 180:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$40(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=181;return;}
case 181:{throw (Throwable)f.pop();}
case 182:{f.pc=183;return;}
case 183:{f.pc=184;return;}
case 184:{f.push(1,1);f.pc=185;return;}
case 185:{f.pc=186;return;}
case 186:{f.pc=187;return;}
case 187:{f.pc=191;return;}
case 188:{f.pc=189;return;}
case 189:{f.pc=190;return;}
case 190:{f.push(0,1);f.pc=191;return;}
case 191:{f.pc=192;return;}
case 192:{f.pc=193;return;}
case 193:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$15$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=5;return;}
case 5:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=6;return;}
case 6:{f.locals[2]=f.pop();f.pc=7;return;}
case 7:{f.rearrange(87);f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0084.f,2);f.pc=9;return;}
case 9:{f.push(f.locals[2],2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[2],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(92299072971527L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[4]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(92);f.pc=18;return;}
case 18:{f.push(110664725166101L,2);f.pc=19;return;}
case 19:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=20;return;}
case 20:{f.locals[6]=f.pop();f.pc=21;return;}
case 21:{f.rearrange(88);f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(f.locals[0],1);f.pc=25;return;}
case 25:{f.push(((dev.zprestige.sourceview.S0084)(Object)f.pop()).e,1);f.pc=26;return;}
case 26:{f.locals[9]=f.pop();f.pc=27;return;}
case 27:{f.push(-4791011956495975741L,2);f.pc=28;return;}
case 28:{f.push(f.locals[2],2);f.pc=29;return;}
case 29:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$8(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.push(f.locals[0],1);f.pc=33;return;}
case 33:{f.push(null,1);f.pc=34;return;}
case 34:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0084.class,receiver,"e",((net.minecraft.class_2791)(Object)(value)));f.pc=35;return;}
case 35:{f.locals[8]=f.pop();f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.pc=39;return;}
case 39:{f.push(f.locals[0],1);f.pc=40;return;}
case 40:{f.push(((dev.zprestige.sourceview.S0084)(Object)f.pop()).d,1);f.pc=41;return;}
case 41:{f.push(f.locals[0],1);f.pc=42;return;}
case 42:{f.push(((dev.zprestige.sourceview.S0084)(Object)f.pop()).a,1);f.pc=43;return;}
case 43:{f.push(f.locals[4],2);f.pc=44;return;}
case 44:{f.push(1,1);f.pc=45;return;}
case 45:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=46;return;}
case 46:{f.rearrange(91);f.pc=47;return;}
case 47:{f.rearrange(91);f.pc=48;return;}
case 48:{f.rearrange(87);f.pc=49;return;}
case 49:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=50;return;}
case 50:{f.push(0,1);f.pc=51;return;}
case 51:{f.rearrange(95);f.pc=52;return;}
case 52:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=53;return;}
case 53:{f.push(-4791468813622433195L,2);f.pc=54;return;}
case 54:{f.push(f.locals[2],2);f.pc=55;return;}
case 55:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$9(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=56;return;}
case 56:{f.push(-4791403559506969794L,2);f.pc=57;return;}
case 57:{f.push(f.locals[2],2);f.pc=58;return;}
case 58:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=59;return;}
case 59:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=158;return;}f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.push(f.locals[0],1);f.pc=63;return;}
case 63:{f.push(f.locals[0],1);f.pc=64;return;}
case 64:{f.push(((dev.zprestige.sourceview.S0084)(Object)f.pop()).a,1);f.pc=65;return;}
case 65:{f.push(f.locals[4],2);f.pc=66;return;}
case 66:{f.push(1,1);f.pc=67;return;}
case 67:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=68;return;}
case 68:{f.rearrange(91);f.pc=69;return;}
case 69:{f.rearrange(91);f.pc=70;return;}
case 70:{f.rearrange(87);f.pc=71;return;}
case 71:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=72;return;}
case 72:{f.push(0,1);f.pc=73;return;}
case 73:{f.rearrange(95);f.pc=74;return;}
case 74:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=75;return;}
case 75:{f.push(-4791468813622433195L,2);f.pc=76;return;}
case 76:{f.push(f.locals[2],2);f.pc=77;return;}
case 77:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$11(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=78;return;}
case 78:{f.push(f.locals[0],1);f.pc=79;return;}
case 79:{f.rearrange(89);f.pc=80;return;}
case 80:{f.push(((dev.zprestige.sourceview.S0084)(Object)f.pop()).d,1);f.pc=81;return;}
case 81:{f.rearrange(90);f.pc=82;return;}
case 82:{f.push(1,1);f.pc=83;return;}
case 83:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());f.push(a+b,1);f.pc=84;return;}
case 84:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0084.class,receiver,"d",dev.zprestige.sourceview.FlowFrame.i(value));f.pc=85;return;}
case 85:{f.push(-4791093186955016673L,2);f.pc=86;return;}
case 86:{f.push(f.locals[2],2);f.pc=87;return;}
case 87:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=88;return;}
case 88:{f.push((net.minecraft.class_2791)(Object)f.pop(),1);f.pc=89;return;}
case 89:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0084.class,receiver,"e",((net.minecraft.class_2791)(Object)(value)));f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.pc=92;return;}
case 92:{f.push(f.locals[0],1);f.pc=93;return;}
case 93:{f.push(f.locals[8],1);f.pc=94;return;}
case 94:{if(f.pop()!=null){f.pc=108;return;}f.pc=95;return;}
case 95:{f.push(((dev.zprestige.sourceview.S0084)(Object)f.pop()).e,1);f.pc=96;return;}
case 96:{if(f.pop()==null){f.pc=36;return;}f.pc=97;return;}
case 97:{f.pc=98;return;}
case 98:{f.pc=105;return;}
case 99:{f.pc=100;return;}
case 100:{f.pc=101;return;}
case 101:{f.push(-4791754289609824398L,2);f.pc=102;return;}
case 102:{f.push(f.locals[2],2);f.pc=103;return;}
case 103:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=104;return;}
case 104:{throw (Throwable)f.pop();}
case 105:{f.pc=106;return;}
case 106:{f.pc=107;return;}
case 107:{f.push(f.locals[0],1);f.pc=108;return;}
case 108:{f.pc=109;return;}
case 109:{f.pc=110;return;}
case 110:{f.push(((dev.zprestige.sourceview.S0084)(Object)f.pop()).c,1);f.pc=111;return;}
case 111:{f.push(f.locals[8],1);f.pc=112;return;}
case 112:{if(f.pop()!=null){f.pc=154;return;}f.pc=113;return;}
case 113:{f.pc=114;return;}
case 114:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=158;return;}f.pc=115;return;}
case 115:{f.pc=116;return;}
case 116:{f.pc=123;return;}
case 117:{f.pc=118;return;}
case 118:{f.pc=119;return;}
case 119:{f.push(-4791754289609824398L,2);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$15$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.push(f.locals[2],2);f.pc=121;return;}
case 121:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=122;return;}
case 122:{throw (Throwable)f.pop();}
case 123:{f.pc=124;return;}
case 124:{f.pc=125;return;}
case 125:{f.push(f.locals[0],1);f.pc=126;return;}
case 126:{f.push(f.locals[0],1);f.pc=127;return;}
case 127:{f.push(((dev.zprestige.sourceview.S0084)(Object)f.pop()).e,1);f.pc=128;return;}
case 128:{f.push(f.locals[6],2);f.pc=129;return;}
case 129:{f.push(2,1);f.pc=130;return;}
case 130:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=131;return;}
case 131:{f.rearrange(91);f.pc=132;return;}
case 132:{f.rearrange(91);f.pc=133;return;}
case 133:{f.rearrange(87);f.pc=134;return;}
case 134:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=135;return;}
case 135:{f.push(1,1);f.pc=136;return;}
case 136:{f.rearrange(95);f.pc=137;return;}
case 137:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=138;return;}
case 138:{f.rearrange(90);f.pc=139;return;}
case 139:{f.rearrange(95);f.pc=140;return;}
case 140:{f.push(0,1);f.pc=141;return;}
case 141:{f.rearrange(95);f.pc=142;return;}
case 142:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=143;return;}
case 143:{f.push(-4791321262777179387L,2);f.pc=144;return;}
case 144:{f.push(f.locals[2],2);f.pc=145;return;}
case 145:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$15(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=146;return;}
case 146:{f.pc=147;return;}
case 147:{f.pc=154;return;}
case 148:{f.pc=149;return;}
case 149:{f.pc=150;return;}
case 150:{f.push(-4791754289609824398L,2);f.pc=151;return;}
case 151:{f.push(f.locals[2],2);f.pc=152;return;}
case 152:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0084.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=153;return;}
case 153:{throw (Throwable)f.pop();}
case 154:{f.pc=155;return;}
case 155:{f.pc=156;return;}
case 156:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=36;return;}f.pc=157;return;}
case 157:{f.pc=158;return;}
case 158:{f.pc=159;return;}
case 159:{f.pc=160;return;}
case 160:{f.pc=161;return;}
case 161:{f.push(f.locals[9],1);f.pc=162;return;}
case 162:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-7720038119500491813L,2);f.pc=1;return;}
case 1:{f.push(-8902389126082160562L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(65420479910980L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0084.class,"f",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(36,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0084.class,"g",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(36,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0084.class,"h",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0084.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
