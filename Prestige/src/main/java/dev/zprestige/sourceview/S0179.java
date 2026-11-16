package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0109;
import dev.zprestige.sourceview.S0117;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0185;
import dev.zprestige.sourceview.S0225;
import dev.zprestige.sourceview.S0250;
import dev.zprestige.sourceview.S0252;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0331;
import dev.zprestige.sourceview.S0549;


import java.awt.Color;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.class_638;
import net.minecraft.class_746;

public class S0179 implements S0225 {
   public S0331 a;
   public List c;
   public ConcurrentLinkedQueue d;
   public ConcurrentLinkedQueue e;
   public static boolean f;
   public static long g;
   public static Object[] h;
   public static String[] i;

   public S0179(long var1)  throws Throwable {
      var1 = g ^ var1;
      long var3 = var1 ^ 31757000116027L;
      super();
try {

      this.a = new S0331();
      this.c = new ArrayList();
      this.d = new ConcurrentLinkedQueue();
      this.e = new ConcurrentLinkedQueue();
      __indy$1(__indy$0(-3586091380282493103L, var1), new Object[]{this, var3}, -3586461612652313926L, var1);
   
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
      if (i[var4] != null) {
         return var4;
      }

      Object var5 = h[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 53;
         case 1 -> 34;
         case 2 -> 43;
         case 3 -> 12;
         case 4 -> 36;
         case 5 -> 4;
         case 6 -> 16;
         case 7 -> 40;
         case 8 -> 3;
         case 9 -> 26;
         case 10 -> 46;
         case 11 -> 27;
         case 12 -> 58;
         case 13 -> 21;
         case 14 -> 17;
         case 15 -> 51;
         case 16 -> 41;
         case 17 -> 57;
         case 18 -> 42;
         case 19 -> 5;
         case 20 -> 25;
         case 21 -> 31;
         case 22 -> 11;
         case 23 -> 61;
         case 24 -> 0;
         case 25 -> 13;
         case 26 -> 62;
         case 27 -> 47;
         case 28 -> 29;
         case 29 -> 8;
         case 30 -> 7;
         case 31 -> 15;
         case 32 -> 37;
         case 33 -> 44;
         case 34 -> 1;
         case 35 -> 45;
         case 36 -> 63;
         case 37 -> 14;
         case 38 -> 35;
         case 39 -> 2;
         case 40 -> 38;
         case 41 -> 52;
         case 42 -> 39;
         case 43 -> 28;
         case 44 -> 6;
         case 45 -> 59;
         case 46 -> 50;
         case 47 -> 10;
         case 48 -> 60;
         case 49 -> 48;
         case 50 -> 55;
         case 51 -> 54;
         case 52 -> 22;
         case 53 -> 30;
         case 54 -> 32;
         case 55 -> 18;
         case 56 -> 56;
         case 57 -> 20;
         case 58 -> 23;
         case 59 -> 9;
         case 60 -> 33;
         case 61 -> 49;
         case 62 -> 24;
         default -> 19;
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

      i[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void b$return$9b75() {
try {

      h[0] = " g|~qA6gy$bV!,z\"nB0km5%R\u000e";
      h[1] = "MU0* KFZ!eCFSW.\u000evDBD2\"aI";
      h[2] = "\u0016P\u0003b6+\u0000P\u00068%<\u0017\u001b\u0005>)(\u0006\\\u0012)b::";
      h[3] = "\f\u000fs\u0000d!y/x\u000fun\u00047k\b|'l";
      h[4] = "Qk/v%EOc59h_Ui,eyUU~wTd^X\u007f+en^OF0y`U_[,r~U";
      h[5] = "56!M\u0016{>90\u0002wu524X";
      h[6] = boolean.class;
      i[6] = "java/lang/Boolean";
      h[7] = "JR4dX@JR#8TOP\u0019#&TZWhs{\u0005";
      h[8] = "\u001en\\5\"?\u001enKi.0\u0004%Kw.%\u0003T\u001f/y";
      h[9] = "'.\bRd+'.\u001f\u000eh$=e\u001f\u0010h1:\u0014JO1";
      h[10] = "0\u0010\u001cL\t\u0004&\u0010\u0019\u0016\u001a\u00131[\u001a\u0010\u0016\u0007 \u001c\r\u0007]\u0010\u0002";
      h[11] = "d\u001dk\u0011\"\u000f\u0011=`\u001e3@p3k\u00157\u001a\u0004";
      h[12] = void.class;
      i[12] = "java/lang/Void";
      h[13] = "\u0002]v\u001c}1w}}\u0013l~\u0016sv\u0018h$b";
      h[14] = "#\u001f>x\"\u001f(\u0010/7E\u001d=\u001b/|~";
      h[15] = int.class;
      i[15] = "java/lang/Integer";
      h[16] = "\u0002d!p\u000b\n\u0014d$*\u0018\u001d\u0003/',\u0014\t\u0012h0;_\u001f9";
      h[17] = "vU]f\u001d?\u0003uVi\fpb{]b\b*\u0016";
      h[18] = "y\u001ep,q#o\u001euvb4xUvpn i\u0012ag%7Q";
      h[19] = "+\u0011{}!/=\u0011~'28*Z}!>,;\u001dj6u;\u0004";
      h[20] = "|\u0006&S\u0018Kj\u0006#\t\u000b\\}M \u000f\u0007Hl\n7\u0018LXt\n5\u0013\u0016\u0015H\u00115\u000e\u0016R\u007f\u0006";
      h[21] = "\u007f%EKgli%@\u0011t{~nC\u0017xoo)T\u00003~T";
      h[22] = "\u001ax\\|%?oXWs4p\u000eV\\x0*z";
      h[23] = "]\u0012IWt\u0015(2BXeZI<ISa\u0000=";
      h[24] = ",\u007f\u0015\u0002]\u000f:\u007f\u0010XN\u0018-4\u0013^B\f<s\u0004I\t\u001b\u0006";
      h[25] = "|GE?\u0017%vH\u0016E\u0007An\bU;\u0010~l\u000bJE\u0013|oKWz\u0011\u007fp5";
      h[26] = "N_DQL\"GN^G|5w\u0019Q\u0007G#\u0017[P\u0006\u001d_";
      h[27] = "#m\u001evV3~>\u0011?h8Na\u00198\u000b5 hK9\u0002";
      h[28] = "@Zp:\u0011\nI\bq3v\r.\r\u007f8\u001a]HX56Nd\u001e\u0012\u007f)\u0010\u0004\u0010\u001bm7v";
      h[29] = "~N:\u0006q\\)\u0000y\u000b\u0014L#R#\u0000C\u001b}\u0005{l)R,\u000f{](Bs\u0002";
      h[30] = "\\\u001f|BT<\u000bQ?O1,\u0001\u0003eDf{^^>(\u000f%^TeJJ3Q\u0015";
      h[31] = ".R\u00178\u0007xmUJlzw\u0011T\u001ba\u0003~l\rN-D\u001e";
      h[32] = "S:\u0018a\u001fT\u0012g\b=nJ\u0013\u001a\u001f!\u0012ZhkJe\u0017K\u00152\u001f)P+";
      h[33] = "(P@]i#tZ\u0005_\u0016dpFCKQt\u0019K[\u0016ia`\u0013RJu\u001a(P@]i#tZ\u0005_\u0016";
      h[34] = "!7M2\\\u000e`j]n-\t}>K\n\u0016\u0016t1K:S\u0011k=/";
      h[35] = "\u0015\u000bf\u0016\u000bsTVvJzk@\biTz7I\boJJrN\u0017c.F<\u0012\u001fdS\u001fi^X\u0004";
      h[36] = "y\u001aD{\u0001#m\u0014\u0005ma$\u0013\u0019\u000b}\rvuLAsYOx\u000f\u0013nQ\u007f}\u000e\u001dla";
      h[37] = "Tv^R=g@x\u001fD]a>u\u0011T12X [Ze\u000bQkZO#2Ad\u001f[]";
      h[38] = "\np\u0010\u0016JiIwMB7c5$]\u0012[6Sq\u0017\u001c\u000f\u000f\u0005;]\u0003Qo\u000b2O\u001d7";
      h[39] = "SEp\u0000\u001d\u0002\u0012\u0018`\\l\u0016\u000fNnQ\r\u001b\u0013()_\u0002\u0016\f\u0018lX\u001d\u001ah\u0014\"\u0004\u0015\u001d\u0015MwHR}";
      h[40] = "\u0016B@ ^FUE\u001dt#M)\u0016\r$O\u0019OCG*\u001b \u0019\t\r5E@\u0017\u0000\u001f+#";
      h[41] = "7]Se\u0012X7\u0016\u0000kk\u000b[W\rg\u0000\u0006k\u0012\nx\fbk\u001d\u001by\r\u0002e\u0014\tgk";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'C' && var8 != 'u' && var8 != 165 && var8 != 212) {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'f') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'Z') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = g(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'C') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'u') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 165) {
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

   public void b$return$e4b0899c(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(6,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$5$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=21&&f.pc<33&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=35;continue;}
if(f.pc>=31&&f.pc<50&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=52;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static Object b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = b(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$7(var8.asSpreader(Object[].class, var4.length), var4);
   
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

   public static Method h(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = h[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = i[var4];
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
            h[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = f(373589376329904L, 0L);
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
                  h[var4] = var19;
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
            var23 = f(373589376329904L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class f(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = e(var0, var2);
      Object var6 = h[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(i[var4]);
            h[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
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

   public S0331 a$return$99ec1f73(Object[] var1) {
try {

      return this.a;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0185
   public void a(Color var1) {
try {

      long var2 = g ^ 25394742002328L;
      __indy$9(__indy$8(this.a, new Object[0], 8940843357578707761L, var2), var1, 8941700885872625778L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0185
   public void a(List var1) {
try {

      this.c = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0185
   public List a$return$a9863e32() {
try {

      return this.c;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$10().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/cJ" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0109 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(10,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$19$0(f);break;
case 1:__flow$19$1(f);break;
case 2:__flow$19$2(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=18&&f.pc<28&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=30;continue;}
if(f.pc>=23&&f.pc<39&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=41;continue;}
if(f.pc>=47&&f.pc<57&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=57;continue;}
if(f.pc>=81&&f.pc<89&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=91;continue;}
if(f.pc>=86&&f.pc<100&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=102;continue;}
if(f.pc>=111&&f.pc<120&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=122;continue;}
if(f.pc>=115&&f.pc<132&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=134;continue;}
if(f.pc>=181&&f.pc<186&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=188;continue;}
if(f.pc>=197&&f.pc<206&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=208;continue;}
if(f.pc>=201&&f.pc<218&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=220;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   @S0185
   public void a(int var1) {
try {

      long var2 = g ^ 139508062771763L;
      __indy$34(__indy$32(this.a, new Object[0], -6575450056646449833L, var2), __indy$33(var1, -6575776514520681183L, var2), -6576341301234626343L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(6,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$21$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=21&&f.pc<33&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=35;continue;}
if(f.pc>=31&&f.pc<50&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=52;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static Field g(long var0, long var2) {
try {

      int var4 = e(var0, var2);
      Object var5 = h[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = i[var4];
      int var7 = var6.indexOf(8);
      Class var8 = f(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = f(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = c(var12, var10, var11);
         if (var13 != null) {
            h[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = d(var14[var15], var10, var11);
               if (var13 != null) {
                  h[var4] = var13;
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
            var12 = f(373589376329904L, 0L);
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

   private static native S0117 __indy$0(long var0, long var2) throws Throwable;

   private static native void __indy$1(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String[] __indy$2(long var0, long var2) throws Throwable;

   private static native boolean __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$7(MethodHandle var0, Object[] var1) throws Throwable;

   private static native S0252 __indy$8(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$10() throws Throwable;

   private static native String[] __indy$11(long var0, long var2) throws Throwable;

   private static native class_638 __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$24(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$31(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native S0250 __indy$32(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Integer __indy$33(int var0, long var1, long var3) throws Throwable;

   private static native void __indy$34(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String[] __indy$35(long var0, long var2) throws Throwable;

   private static native boolean __indy$36(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$37(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$38(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$39(Object var0, long var1, long var3) throws Throwable;







private void __flow$19$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0179.g,2);f.pc=1;return;}
case 1:{f.push(103692744652814L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(59542368627597L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(92);f.pc=10;return;}
case 10:{f.push(120551760802286L,2);f.pc=11;return;}
case 11:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=12;return;}
case 12:{f.locals[6]=f.pop();f.pc=13;return;}
case 13:{f.rearrange(88);f.pc=14;return;}
case 14:{f.push(472252759595603471L,2);f.pc=15;return;}
case 15:{f.push(f.locals[2],2);f.pc=16;return;}
case 16:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$11(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=17;return;}
case 17:{f.locals[8]=f.pop();f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(dev.zprestige.sourceview.S0179.b,1);f.pc=21;return;}
case 21:{f.push(f.locals[8],1);f.pc=22;return;}
case 22:{if(f.pop()!=null){f.pc=47;return;}f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(472005104666618989L,2);f.pc=25;return;}
case 25:{f.push(f.locals[2],2);f.pc=26;return;}
case 26:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=27;return;}
case 27:{if(f.pop()==null){f.pc=53;return;}f.pc=28;return;}
case 28:{f.pc=29;return;}
case 29:{f.pc=36;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.push(472056367398426381L,2);f.pc=33;return;}
case 33:{f.push(f.locals[2],2);f.pc=34;return;}
case 34:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=35;return;}
case 35:{throw (Throwable)f.pop();}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(dev.zprestige.sourceview.S0179.b,1);f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.pc=47;return;}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.push(472056367398426381L,2);f.pc=44;return;}
case 44:{f.push(f.locals[2],2);f.pc=45;return;}
case 45:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=46;return;}
case 46:{throw (Throwable)f.pop();}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(471796415337924769L,2);f.pc=50;return;}
case 50:{f.push(f.locals[2],2);f.pc=51;return;}
case 51:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=52;return;}
case 52:{if(f.pop()!=null){f.pc=63;return;}f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.done=true;return;}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.push(472056367398426381L,2);f.pc=60;return;}
case 60:{f.push(f.locals[2],2);f.pc=61;return;}
case 61:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=62;return;}
case 62:{throw (Throwable)f.pop();}
case 63:{f.pc=64;return;}
case 64:{f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.push(f.locals[0],1);f.pc=67;return;}
case 67:{f.push(((dev.zprestige.sourceview.S0179)(Object)f.pop()).d,1);f.pc=68;return;}
case 68:{f.push(469304635692204750L,2);f.pc=69;return;}
case 69:{f.push(f.locals[2],2);f.pc=70;return;}
case 70:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=71;return;}
case 71:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=160;return;}f.pc=72;return;}
case 72:{f.pc=73;return;}
case 73:{f.pc=74;return;}
case 74:{f.push(f.locals[0],1);f.pc=75;return;}
case 75:{f.push(((dev.zprestige.sourceview.S0179)(Object)f.pop()).d,1);f.pc=76;return;}
case 76:{f.push(469473265658340894L,2);f.pc=77;return;}
case 77:{f.push(f.locals[2],2);f.pc=78;return;}
case 78:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=79;return;}
case 79:{f.push((dev.zprestige.sourceview.S0260)(Object)f.pop(),1);f.pc=80;return;}
case 80:{f.locals[9]=f.pop();f.pc=81;return;}
case 81:{f.pc=82;return;}
case 82:{f.pc=83;return;}
case 83:{f.push(f.locals[9],1);f.pc=84;return;}
case 84:{f.push(f.locals[8],1);f.pc=85;return;}
case 85:{if(f.pop()!=null){f.pc=181;return;}f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.push(f.locals[8],1);f.pc=88;return;}
case 88:{if(f.pop()!=null){f.pc=111;return;}f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=97;return;}
case 91:{f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.push(472056367398426381L,2);f.pc=94;return;}
case 94:{f.push(f.locals[2],2);f.pc=95;return;}
case 95:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$19(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=96;return;}
case 96:{throw (Throwable)f.pop();}
case 97:{f.pc=98;return;}
case 98:{f.pc=99;return;}
case 99:{if(f.pop()==null){f.pc=155;return;}f.pc=100;return;}
case 100:{f.pc=101;return;}
case 101:{f.pc=108;return;}
case 102:{f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{f.push(472056367398426381L,2);f.pc=105;return;}
case 105:{f.push(f.locals[2],2);f.pc=106;return;}
case 106:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=107;return;}
case 107:{throw (Throwable)f.pop();}
case 108:{f.pc=109;return;}
case 109:{f.pc=110;return;}
case 110:{f.push(f.locals[9],1);f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.pc=113;return;}
case 113:{f.push(f.locals[8],1);f.pc=114;return;}
case 114:{if(f.pop()!=null){f.pc=140;return;}f.pc=115;return;}
case 115:{f.pc=116;return;}
case 116:{f.push(471902340741878233L,2);f.pc=117;return;}
case 117:{f.push(f.locals[2],2);f.pc=118;return;}
case 118:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=119;return;}
case 119:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=155;return;}f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$19$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=128;return;}
case 122:{f.pc=123;return;}
case 123:{f.pc=124;return;}
case 124:{f.push(472056367398426381L,2);f.pc=125;return;}
case 125:{f.push(f.locals[2],2);f.pc=126;return;}
case 126:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=127;return;}
case 127:{throw (Throwable)f.pop();}
case 128:{f.pc=129;return;}
case 129:{f.pc=130;return;}
case 130:{f.pc=131;return;}
case 131:{f.push(f.locals[9],1);f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.pc=140;return;}
case 134:{f.pc=135;return;}
case 135:{f.pc=136;return;}
case 136:{f.push(472056367398426381L,2);f.pc=137;return;}
case 137:{f.push(f.locals[2],2);f.pc=138;return;}
case 138:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=139;return;}
case 139:{throw (Throwable)f.pop();}
case 140:{f.pc=141;return;}
case 141:{f.pc=142;return;}
case 142:{f.push(f.locals[4],2);f.pc=143;return;}
case 143:{f.push(1,1);f.pc=144;return;}
case 144:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=145;return;}
case 145:{f.rearrange(91);f.pc=146;return;}
case 146:{f.rearrange(91);f.pc=147;return;}
case 147:{f.rearrange(87);f.pc=148;return;}
case 148:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=149;return;}
case 149:{f.push(0,1);f.pc=150;return;}
case 150:{f.rearrange(95);f.pc=151;return;}
case 151:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=152;return;}
case 152:{f.push(469176886272113101L,2);f.pc=153;return;}
case 153:{f.push(f.locals[2],2);f.pc=154;return;}
case 154:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0179.__indy$24(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=155;return;}
case 155:{f.pc=156;return;}
case 156:{f.pc=157;return;}
case 157:{f.pc=158;return;}
case 158:{f.push(f.locals[8],1);f.pc=159;return;}
case 159:{if(f.pop()==null){f.pc=63;return;}f.pc=160;return;}
case 160:{f.pc=161;return;}
case 161:{f.pc=162;return;}
case 162:{f.pc=163;return;}
case 163:{f.push(f.locals[0],1);f.pc=164;return;}
case 164:{f.push(((dev.zprestige.sourceview.S0179)(Object)f.pop()).e,1);f.pc=165;return;}
case 165:{f.push(469304635692204750L,2);f.pc=166;return;}
case 166:{f.push(f.locals[2],2);f.pc=167;return;}
case 167:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=168;return;}
case 168:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=246;return;}f.pc=169;return;}
case 169:{f.pc=170;return;}
case 170:{f.pc=171;return;}
case 171:{f.push(f.locals[0],1);f.pc=172;return;}
case 172:{f.push(((dev.zprestige.sourceview.S0179)(Object)f.pop()).e,1);f.pc=173;return;}
case 173:{f.push(469473265658340894L,2);f.pc=174;return;}
case 174:{f.push(f.locals[2],2);f.pc=175;return;}
case 175:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=176;return;}
case 176:{f.push((dev.zprestige.sourceview.S0260)(Object)f.pop(),1);f.pc=177;return;}
case 177:{f.locals[9]=f.pop();f.pc=178;return;}
case 178:{f.pc=179;return;}
case 179:{f.pc=180;return;}
case 180:{f.push(f.locals[9],1);f.pc=181;return;}
case 181:{f.pc=182;return;}
case 182:{f.pc=183;return;}
case 183:{f.push(f.locals[8],1);f.pc=184;return;}
case 184:{if(f.pop()!=null){f.pc=197;return;}f.pc=185;return;}
case 185:{if(f.pop()==null){f.pc=241;return;}f.pc=186;return;}
case 186:{f.pc=187;return;}
case 187:{f.pc=194;return;}
case 188:{f.pc=189;return;}
case 189:{f.pc=190;return;}
case 190:{f.push(472056367398426381L,2);f.pc=191;return;}
case 191:{f.push(f.locals[2],2);f.pc=192;return;}
case 192:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=193;return;}
case 193:{throw (Throwable)f.pop();}
case 194:{f.pc=195;return;}
case 195:{f.pc=196;return;}
case 196:{f.push(f.locals[9],1);f.pc=197;return;}
case 197:{f.pc=198;return;}
case 198:{f.pc=199;return;}
case 199:{f.push(f.locals[8],1);f.pc=200;return;}
case 200:{if(f.pop()!=null){f.pc=226;return;}f.pc=201;return;}
case 201:{f.pc=202;return;}
case 202:{f.push(471902340741878233L,2);f.pc=203;return;}
case 203:{f.push(f.locals[2],2);f.pc=204;return;}
case 204:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=205;return;}
case 205:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=241;return;}f.pc=206;return;}
case 206:{f.pc=207;return;}
case 207:{f.pc=214;return;}
case 208:{f.pc=209;return;}
case 209:{f.pc=210;return;}
case 210:{f.push(472056367398426381L,2);f.pc=211;return;}
case 211:{f.push(f.locals[2],2);f.pc=212;return;}
case 212:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$29(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=213;return;}
case 213:{throw (Throwable)f.pop();}
case 214:{f.pc=215;return;}
case 215:{f.pc=216;return;}
case 216:{f.pc=217;return;}
case 217:{f.push(f.locals[9],1);f.pc=218;return;}
case 218:{f.pc=219;return;}
case 219:{f.pc=226;return;}
case 220:{f.pc=221;return;}
case 221:{f.pc=222;return;}
case 222:{f.push(472056367398426381L,2);f.pc=223;return;}
case 223:{f.push(f.locals[2],2);f.pc=224;return;}
case 224:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$30(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=225;return;}
case 225:{throw (Throwable)f.pop();}
case 226:{f.pc=227;return;}
case 227:{f.pc=228;return;}
case 228:{f.push(f.locals[6],2);f.pc=229;return;}
case 229:{f.push(1,1);f.pc=230;return;}
case 230:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=231;return;}
case 231:{f.rearrange(91);f.pc=232;return;}
case 232:{f.rearrange(91);f.pc=233;return;}
case 233:{f.rearrange(87);f.pc=234;return;}
case 234:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=235;return;}
case 235:{f.push(0,1);f.pc=236;return;}
case 236:{f.rearrange(95);f.pc=237;return;}
case 237:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=238;return;}
case 238:{f.push(468770068504126920L,2);f.pc=239;return;}
case 239:{f.push(f.locals[2],2);f.pc=240;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$19$2(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 240:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0179.__indy$31(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=241;return;}
case 241:{f.pc=242;return;}
case 242:{f.pc=243;return;}
case 243:{f.pc=244;return;}
case 244:{f.push(f.locals[8],1);f.pc=245;return;}
case 245:{if(f.pop()==null){f.pc=160;return;}f.pc=246;return;}
case 246:{f.pc=247;return;}
case 247:{f.pc=248;return;}
case 248:{f.pc=249;return;}
case 249:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$5$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((dev.zprestige.sourceview.S0260)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[4]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0179.g,2);f.pc=14;return;}
case 14:{f.push(f.locals[2],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.push(8050947923688210232L,2);f.pc=18;return;}
case 18:{f.push(f.locals[2],2);f.pc=19;return;}
case 19:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=20;return;}
case 20:{f.locals[5]=f.pop();f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.push(f.locals[0],1);f.pc=24;return;}
case 24:{f.push(((dev.zprestige.sourceview.S0179)(Object)f.pop()).e,1);f.pc=25;return;}
case 25:{f.push(f.locals[4],1);f.pc=26;return;}
case 26:{f.push(8049573366244055362L,2);f.pc=27;return;}
case 27:{f.push(f.locals[2],2);f.pc=28;return;}
case 28:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$3(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=29;return;}
case 29:{f.push(f.locals[5],1);f.pc=30;return;}
case 30:{if(f.pop()!=null){f.pc=58;return;}f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=61;return;}f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=41;return;}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.push(8050859267630104122L,2);f.pc=38;return;}
case 38:{f.push(f.locals[2],2);f.pc=39;return;}
case 39:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=40;return;}
case 40:{throw (Throwable)f.pop();}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.push(f.locals[0],1);f.pc=45;return;}
case 45:{f.push(((dev.zprestige.sourceview.S0179)(Object)f.pop()).e,1);f.pc=46;return;}
case 46:{f.push(f.locals[4],1);f.pc=47;return;}
case 47:{f.push(8049298045891115648L,2);f.pc=48;return;}
case 48:{f.push(f.locals[2],2);f.pc=49;return;}
case 49:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$5(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.pc=58;return;}
case 52:{f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.push(8050859267630104122L,2);f.pc=55;return;}
case 55:{f.push(f.locals[2],2);f.pc=56;return;}
case 56:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=57;return;}
case 57:{throw (Throwable)f.pop();}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.rearrange(87);f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$21$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((dev.zprestige.sourceview.S0260)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[4]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0179.g,2);f.pc=14;return;}
case 14:{f.push(f.locals[2],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.push(-7402874775516329535L,2);f.pc=18;return;}
case 18:{f.push(f.locals[2],2);f.pc=19;return;}
case 19:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$35(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=20;return;}
case 20:{f.locals[5]=f.pop();f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.pc=23;return;}
case 23:{f.push(f.locals[0],1);f.pc=24;return;}
case 24:{f.push(((dev.zprestige.sourceview.S0179)(Object)f.pop()).d,1);f.pc=25;return;}
case 25:{f.push(f.locals[4],1);f.pc=26;return;}
case 26:{f.push(-7400304026739216453L,2);f.pc=27;return;}
case 27:{f.push(f.locals[2],2);f.pc=28;return;}
case 28:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$36(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=29;return;}
case 29:{f.push(f.locals[5],1);f.pc=30;return;}
case 30:{if(f.pop()!=null){f.pc=58;return;}f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=61;return;}f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=41;return;}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.push(-7402959842052405053L,2);f.pc=38;return;}
case 38:{f.push(f.locals[2],2);f.pc=39;return;}
case 39:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$37(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=40;return;}
case 40:{throw (Throwable)f.pop();}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.push(f.locals[0],1);f.pc=45;return;}
case 45:{f.push(((dev.zprestige.sourceview.S0179)(Object)f.pop()).d,1);f.pc=46;return;}
case 46:{f.push(f.locals[4],1);f.pc=47;return;}
case 47:{f.push(-7400017773404287879L,2);f.pc=48;return;}
case 48:{f.push(f.locals[2],2);f.pc=49;return;}
case 49:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$38(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.pc=58;return;}
case 52:{f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.push(-7402959842052405053L,2);f.pc=55;return;}
case 55:{f.push(f.locals[2],2);f.pc=56;return;}
case 56:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0179.__indy$39(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=57;return;}
case 57:{throw (Throwable)f.pop();}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.rearrange(87);f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(3122907886063884537L,2);f.pc=1;return;}
case 1:{f.push(8073645064456258999L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(42052905545982L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0179.class,"g",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(42,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0179.class,"h",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(42,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0179.class,"i",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0179.b$return$9b75();f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.push(1,1);f.pc=18;return;}
case 18:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0179.class,"f",(dev.zprestige.sourceview.FlowFrame.i(f.pop()) != 0));f.pc=19;return;}
case 19:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
