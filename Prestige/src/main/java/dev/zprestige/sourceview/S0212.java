package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0035;
import dev.zprestige.sourceview.S0082;
import dev.zprestige.sourceview.S0105;
import dev.zprestige.sourceview.S0117;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0179;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0284;
import dev.zprestige.sourceview.S0285;
import dev.zprestige.sourceview.S0433;
import dev.zprestige.sourceview.S0455;
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
import java.util.List;

public class S0212 {
   public boolean a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   public S0212(long var1)  throws Throwable {
      var1 = b ^ var1;
      long var3 = var1 ^ 73431247940062L;
      super();
try {

      this.a = false;
      __indy$1(__indy$0(2368498559496579061L, var1), new Object[]{this, var3}, 2368582526761358249L, var1);
      __indy$3(S0284.a, __indy$2(this), 2364811460644429969L, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
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
            var12 = b(630978322601862L, 0L);
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
            var23 = b(630978322601862L, 0L);
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
            var23 = b(630978322601862L, 0L);
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
         if (var8 != 181 && var8 != 'W' && var8 != 'h' && var8 != 237) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 214) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'D') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 181) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'W') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'h') {
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
               MethodHandles.insertArguments(__indy$4().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/cm" + " : " + var1 + " : " + var2.toString(), var5);
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

   public static Object a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = a(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$5(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0035 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(10,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$11$0(f);break;
case 1:__flow$11$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=51&&f.pc<58&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=60;continue;}
if(f.pc>=56&&f.pc<76&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=78;continue;}
if(f.pc>=66&&f.pc<87&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=89;continue;}
if(f.pc>=84&&f.pc<112&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=114;continue;}
if(f.pc>=124&&f.pc<130&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=132;continue;}
if(f.pc>=128&&f.pc<143&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=145;continue;}
if(f.pc>=138&&f.pc<158&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=160;continue;}
if(f.pc>=166&&f.pc<185&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=187;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   @S0118
   public void a(S0105 var1) {
try {

      long var2 = b ^ 82042461615984L;

      try {
         if (this.a) {
            __indy$27(var1, new Object[0], -6527078881638479805L, var2);
         }
      } catch (MatchException var4) {
         throw __indy$28(var4, -6527032872403409029L, var2);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
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

   public static void a$return$9b75() {
try {

      c[0] = "\\\u001d'-czJ\u001d\"wpm]V!q|yL\u00116f7iT\u00114mm$h\n4pmc_\u001d";
      c[1] = "6(r309 (wi#.7cto/:&$cxd.j";
      c[2] = "\u0006dq\u0016\nQsDz\u0019\u001b\u001e\u0012Jq\u0012\u001fDf";
      c[3] = boolean.class;
      d[3] = "java/lang/Boolean";
      c[4] = "\u001ei!1Pt\bi$kCc\u001f\"'mOw\u000ee0z\u0004`,";
      c[5] = "obn\u0015\u0001/\u001aBe\u001a\u0010`{Ln\u0011\u0014:\u000f";
      c[6] = void.class;
      d[6] = "java/lang/Void";
      c[7] = "~Mr\u0014@C`Eh['Bq^e\u0001\u0001D";
      c[8] = "B?Uj2\u001fI0D%S\u0011B;@\u007f";
      c[9] = "[wZ<\u0006#Mw_f\u00154Z<\\`\u0019 K{KwR2w";
      c[10] = "3,>\"9,F\f5-(c;\u0014&*!*S";
      c[11] = "=}\u0018v\u001a/+}\u001d,\t8<6\u001e*\u0005,-q\t=N\u00118l\u0007.\u0005";
      c[12] = "_Ztb~[IZq8mL^\u0011r>aXOVe)*HV";
      c[13] = "i>!6n\u0012b10y\r\u001fw<?\u00128\u001df/#>/\u0010";
      c[14] = "\t\u007f/\u001a5[\u001f\u007f*@&L\b4)F*X\u0019s>QaH'";
      c[15] = "muiqI|s}s>+`t`";
      c[16] = "dl)Vj\u001e\u0011L\"Y{QpB)R\u007f\u000b\u0004";
      c[17] = "\u000eiS\u0005d\u0001{IX\nuN\u001aGS\u0001q\u0014n";
      c[18] = "g~w\u001fV_\u0012^|\u0010G\u0010sPw\u001bCJ\u0007";
      c[19] = "1S\u0005o\u000b\u0006'S\u00005\u0018\u00110\u0018\u00033\u0014\u0005!_\u0014$_\u0014\u001a";
      c[20] = "G\u0001P\u0013*\u000e2![\u001c;AS/P\u0017?\u001b'";
      c[21] = "A\u0018D[|~M\u000fY\u0016w|\u0001\u001bQ\u0017hvLSQ\u0005s1J\u000bU\u001bn1j\u000bU\u001bn";
      c[22] = "ug\\;\u0019h/+\u001b%&vN4\u0005z\u001dl, X4\u001b\u0017r?X%Msv0\u001a4&";
      c[23] = "s}m9<\u0016#\u007fhmX\u000e#dp]fW(jb!'\u0004>u\f";
      c[24] = "Su\\\u0012V\f\u0011kX\b/Xm4J\tV@\u00007S\bW2";
      c[25] = "\u0000!Fu\u0014kYqNwiq0#Br\bzT'M0\u0019\u0011";
      c[26] = "y$A.8\u001bhgO\u001fn#\u007f6\u000f~vY#%\u0017y";
      c[27] = "\u0019mqR@7E~iU2$uoo\r\t6\u0017{2C\u000fMId2RY)MkpC2";
      c[28] = "Nn\u0011Fl\u0017\u000e4F\\V\u0010~4H\u0007.DC9Z\u000fnyOg\u001aOkDBu\u0012\u000fV";
      c[29] = "-\u0003ZTSAo\u001d^N*\u0016\u0013\u0010G\b\u0011\u0004q\u0004\u001aF\u0017\u007f/\u001b\u001aWA\u001b+\u0014XF*";
      c[30] = "l\u00153By-}V=s)\u00157\u0018cN!gl\u000bdO";
      c[31] = "{>C!\u001ca!r\u0004?#w@:\u001enB|8>\u0015nG\u001e";
      c[32] = ".[\u001e?P\u0015uH\u0019>4\u000e\u001fB\u0002}P['\u0011\u001ds\u000bg";
      c[33] = "7Z\u0001\u000e_i&\u0019\u000f?\u0007Qd\u001f\u000bU\u001d(=^\rX";
      c[34] = "\b\f\u0019R-\u0014QM\u001f_S\u00071YC\u0006h\u0016SM\u001eHnm\u000bP\u0013Y1\u0015\u000f[\u0013\\S";
      c[35] = "`z3\u001cL\u00160x6H(\b4h\u0014\u0015X\u0014])6HI\n%-=HLh";
      c[36] = "?I]/~4lVStBm*XX\">k,5\u001f\"#h2KO &<V";
      c[37] = ")+{`oJ-3g~VU)?}b*B>P\"&0Q*,cu&NDlz$7D huf&/";
      c[38] = "?E\u000ep*\te\tIn\u0015\u001c\u0004\u0016W1.\rf\u0002\n\u007f(v8\u001d\nn~\u0012<\u0012H\u007f\u0015";
      c[39] = "\\.c\u0005ds\u0006b$\u001b[gg}:D`w\u0005ig\nf\f]tj\u001b9tY\u007fj\u001e[";
   
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
         case 0 -> 5;
         case 1 -> 53;
         case 2 -> 40;
         case 3 -> 38;
         case 4 -> 49;
         case 5 -> 55;
         case 6 -> 50;
         case 7 -> 20;
         case 8 -> 32;
         case 9 -> 26;
         case 10 -> 28;
         case 11 -> 47;
         case 12 -> 54;
         case 13 -> 46;
         case 14 -> 10;
         case 15 -> 19;
         case 16 -> 59;
         case 17 -> 9;
         case 18 -> 3;
         case 19 -> 22;
         case 20 -> 13;
         case 21 -> 41;
         case 22 -> 34;
         case 23 -> 63;
         case 24 -> 12;
         case 25 -> 39;
         case 26 -> 30;
         case 27 -> 25;
         case 28 -> 18;
         case 29 -> 56;
         case 30 -> 62;
         case 31 -> 17;
         case 32 -> 15;
         case 33 -> 37;
         case 34 -> 31;
         case 35 -> 58;
         case 36 -> 21;
         case 37 -> 57;
         case 38 -> 42;
         case 39 -> 4;
         case 40 -> 1;
         case 41 -> 0;
         case 42 -> 24;
         case 43 -> 14;
         case 44 -> 36;
         case 45 -> 27;
         case 46 -> 35;
         case 47 -> 2;
         case 48 -> 44;
         case 49 -> 8;
         case 50 -> 51;
         case 51 -> 60;
         case 52 -> 48;
         case 53 -> 16;
         case 54 -> 7;
         case 55 -> 45;
         case 56 -> 11;
         case 57 -> 52;
         case 58 -> 6;
         case 59 -> 33;
         case 60 -> 29;
         case 61 -> 61;
         case 62 -> 23;
         default -> 43;
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

   public void lambda$new$0(S0082 var1, S0455 var2) {
try {

      long var3 = b ^ 88768799559259L;
      long var5 = var3 ^ 48678037333231L;
      this.a = __indy$30(__indy$29(-7183461505877679444L, var3), new Object[]{var1, var2, var5}, -7183275326495411806L, var3);
   
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

   private static native S0285 __indy$2(S0212 var0) throws Throwable;

   private static native void __indy$3(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$4() throws Throwable;

   private static native Object __indy$5(MethodHandle var0, Object[] var1) throws Throwable;

   private static native String[] __indy$6(long var0, long var2) throws Throwable;

   private static native void __indy$7(long var0, long var2) throws Throwable;

   private static native S0179 __indy$8(long var0, long var2) throws Throwable;

   private static native List __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native Iterator __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$18(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$22(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$24(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$26(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$27(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native S0433 __indy$29(long var0, long var2) throws Throwable;

   private static native boolean __indy$30(Object var0, Object var1, long var2, long var4) throws Throwable;

private void __flow$11$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0212.b,2);f.pc=1;return;}
case 1:{f.push(32575932366704L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(45566645452013L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.push(3272455850671489287L,2);f.pc=11;return;}
case 11:{f.push(f.locals[2],2);f.pc=12;return;}
case 12:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$6(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=13;return;}
case 13:{f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.push(3272564022854046919L,2);f.pc=16;return;}
case 16:{f.push(f.locals[2],2);f.pc=17;return;}
case 17:{Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0212.__indy$7(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1));f.pc=18;return;}
case 18:{f.locals[6]=f.pop();f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.push(3272891075135815879L,2);f.pc=22;return;}
case 22:{f.push(f.locals[2],2);f.pc=23;return;}
case 23:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$8(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=24;return;}
case 24:{f.push(3270761950566516079L,2);f.pc=25;return;}
case 25:{f.push(f.locals[2],2);f.pc=26;return;}
case 26:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=27;return;}
case 27:{f.push(3271062071501558239L,2);f.pc=28;return;}
case 28:{f.push(f.locals[2],2);f.pc=29;return;}
case 29:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=30;return;}
case 30:{f.locals[7]=f.pop();f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{f.push(f.locals[7],1);f.pc=34;return;}
case 34:{f.push(3270989004765117962L,2);f.pc=35;return;}
case 35:{f.push(f.locals[2],2);f.pc=36;return;}
case 36:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=37;return;}
case 37:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=215;return;}f.pc=38;return;}
case 38:{f.push(f.locals[7],1);f.pc=39;return;}
case 39:{f.push(3272374765820657924L,2);f.pc=40;return;}
case 40:{f.push(f.locals[2],2);f.pc=41;return;}
case 41:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=42;return;}
case 42:{f.push((dev.zprestige.sourceview.S0260)(Object)f.pop(),1);f.pc=43;return;}
case 43:{f.locals[8]=f.pop();f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.push(f.locals[8],1);f.pc=47;return;}
case 47:{f.push(3272942142767592561L,2);f.pc=48;return;}
case 48:{f.push(f.locals[2],2);f.pc=49;return;}
case 49:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=50;return;}
case 50:{f.locals[9]=f.pop();f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{f.pc=53;return;}
case 53:{f.push(f.locals[9],1);f.pc=54;return;}
case 54:{f.push(f.locals[6],1);f.pc=55;return;}
case 55:{if(f.pop()!=null){f.pc=124;return;}f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=120;return;}f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.pc=66;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.push(3272715131099770747L,2);f.pc=63;return;}
case 63:{f.push(f.locals[2],2);f.pc=64;return;}
case 64:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=65;return;}
case 65:{throw (Throwable)f.pop();}
case 66:{f.pc=67;return;}
case 67:{f.pc=68;return;}
case 68:{f.push(f.locals[8],1);f.pc=69;return;}
case 69:{f.push(0,1);f.pc=70;return;}
case 70:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=71;return;}
case 71:{f.push(3271264846626594040L,2);f.pc=72;return;}
case 72:{f.push(f.locals[2],2);f.pc=73;return;}
case 73:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$15(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=74;return;}
case 74:{f.push(f.locals[6],1);f.pc=75;return;}
case 75:{if(f.pop()!=null){f.pc=124;return;}f.pc=76;return;}
case 76:{f.pc=77;return;}
case 77:{f.pc=84;return;}
case 78:{f.pc=79;return;}
case 79:{f.pc=80;return;}
case 80:{f.push(3272715131099770747L,2);f.pc=81;return;}
case 81:{f.push(f.locals[2],2);f.pc=82;return;}
case 82:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=83;return;}
case 83:{throw (Throwable)f.pop();}
case 84:{f.pc=85;return;}
case 85:{f.pc=86;return;}
case 86:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=120;return;}f.pc=87;return;}
case 87:{f.pc=88;return;}
case 88:{f.pc=95;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.push(3272715131099770747L,2);f.pc=92;return;}
case 92:{f.push(f.locals[2],2);f.pc=93;return;}
case 93:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=94;return;}
case 94:{throw (Throwable)f.pop();}
case 95:{f.pc=96;return;}
case 96:{f.pc=97;return;}
case 97:{f.pc=98;return;}
case 98:{f.push(f.locals[8],1);f.pc=99;return;}
case 99:{f.push(f.locals[4],2);f.pc=100;return;}
case 100:{f.push(1,1);f.pc=101;return;}
case 101:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=102;return;}
case 102:{f.rearrange(91);f.pc=103;return;}
case 103:{f.rearrange(91);f.pc=104;return;}
case 104:{f.rearrange(87);f.pc=105;return;}
case 105:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=106;return;}
case 106:{f.push(0,1);f.pc=107;return;}
case 107:{f.rearrange(95);f.pc=108;return;}
case 108:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=109;return;}
case 109:{f.push(3271171515300952929L,2);f.pc=110;return;}
case 110:{f.push(f.locals[2],2);f.pc=111;return;}
case 111:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0212.__indy$18(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=112;return;}
case 112:{f.pc=113;return;}
case 113:{f.pc=120;return;}
case 114:{f.pc=115;return;}
case 115:{f.pc=116;return;}
case 116:{f.push(3272715131099770747L,2);f.pc=117;return;}
case 117:{f.push(f.locals[2],2);f.pc=118;return;}
case 118:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$19(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=119;return;}
case 119:{throw (Throwable)f.pop();}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$11$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.pc=123;return;}
case 123:{f.push(f.locals[9],1);f.pc=124;return;}
case 124:{f.pc=125;return;}
case 125:{f.pc=126;return;}
case 126:{f.push(f.locals[6],1);f.pc=127;return;}
case 127:{if(f.pop()!=null){f.pc=166;return;}f.pc=128;return;}
case 128:{f.pc=129;return;}
case 129:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=193;return;}f.pc=130;return;}
case 130:{f.pc=131;return;}
case 131:{f.pc=138;return;}
case 132:{f.pc=133;return;}
case 133:{f.pc=134;return;}
case 134:{f.push(3272715131099770747L,2);f.pc=135;return;}
case 135:{f.push(f.locals[2],2);f.pc=136;return;}
case 136:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=137;return;}
case 137:{throw (Throwable)f.pop();}
case 138:{f.pc=139;return;}
case 139:{f.pc=140;return;}
case 140:{f.push(f.locals[8],1);f.pc=141;return;}
case 141:{f.push(f.locals[6],1);f.pc=142;return;}
case 142:{if(f.pop()!=null){f.pc=197;return;}f.pc=143;return;}
case 143:{f.pc=144;return;}
case 144:{f.pc=151;return;}
case 145:{f.pc=146;return;}
case 146:{f.pc=147;return;}
case 147:{f.push(3272715131099770747L,2);f.pc=148;return;}
case 148:{f.push(f.locals[2],2);f.pc=149;return;}
case 149:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=150;return;}
case 150:{throw (Throwable)f.pop();}
case 151:{f.pc=152;return;}
case 152:{f.pc=153;return;}
case 153:{f.push(0,1);f.pc=154;return;}
case 154:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=155;return;}
case 155:{f.push(3271264846626594040L,2);f.pc=156;return;}
case 156:{f.push(f.locals[2],2);f.pc=157;return;}
case 157:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$22(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=158;return;}
case 158:{f.pc=159;return;}
case 159:{f.pc=166;return;}
case 160:{f.pc=161;return;}
case 161:{f.pc=162;return;}
case 162:{f.push(3272715131099770747L,2);f.pc=163;return;}
case 163:{f.push(f.locals[2],2);f.pc=164;return;}
case 164:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=165;return;}
case 165:{throw (Throwable)f.pop();}
case 166:{f.pc=167;return;}
case 167:{f.pc=168;return;}
case 168:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=193;return;}f.pc=169;return;}
case 169:{f.pc=170;return;}
case 170:{f.pc=171;return;}
case 171:{f.push(f.locals[8],1);f.pc=172;return;}
case 172:{f.push(f.locals[4],2);f.pc=173;return;}
case 173:{f.push(1,1);f.pc=174;return;}
case 174:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=175;return;}
case 175:{f.rearrange(91);f.pc=176;return;}
case 176:{f.rearrange(91);f.pc=177;return;}
case 177:{f.rearrange(87);f.pc=178;return;}
case 178:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=179;return;}
case 179:{f.push(0,1);f.pc=180;return;}
case 180:{f.rearrange(95);f.pc=181;return;}
case 181:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=182;return;}
case 182:{f.push(3271171515300952929L,2);f.pc=183;return;}
case 183:{f.push(f.locals[2],2);f.pc=184;return;}
case 184:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0212.__indy$24(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=185;return;}
case 185:{f.pc=186;return;}
case 186:{f.pc=193;return;}
case 187:{f.pc=188;return;}
case 188:{f.pc=189;return;}
case 189:{f.push(3272715131099770747L,2);f.pc=190;return;}
case 190:{f.push(f.locals[2],2);f.pc=191;return;}
case 191:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0212.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=192;return;}
case 192:{throw (Throwable)f.pop();}
case 193:{f.pc=194;return;}
case 194:{f.pc=195;return;}
case 195:{f.pc=196;return;}
case 196:{f.push(f.locals[8],1);f.pc=197;return;}
case 197:{f.pc=198;return;}
case 198:{f.pc=199;return;}
case 199:{f.push(f.locals[9],1);f.pc=200;return;}
case 200:{f.push(1,1);f.pc=201;return;}
case 201:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=202;return;}
case 202:{f.rearrange(90);f.pc=203;return;}
case 203:{f.rearrange(95);f.pc=204;return;}
case 204:{Object a0=f.pop();f.push(java.lang.Boolean.valueOf((dev.zprestige.sourceview.FlowFrame.i(a0) != 0)),1);f.pc=205;return;}
case 205:{f.push(0,1);f.pc=206;return;}
case 206:{f.rearrange(95);f.pc=207;return;}
case 207:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=208;return;}
case 208:{f.push(3272348590077752653L,2);f.pc=209;return;}
case 209:{f.push(f.locals[2],2);f.pc=210;return;}
case 210:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0212.__indy$26(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=211;return;}
case 211:{f.pc=212;return;}
case 212:{f.pc=213;return;}
case 213:{f.push(f.locals[6],1);f.pc=214;return;}
case 214:{if(f.pop()==null){f.pc=31;return;}f.pc=215;return;}
case 215:{f.pc=216;return;}
case 216:{f.pc=217;return;}
case 217:{f.pc=218;return;}
case 218:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(5982605562861208561L,2);f.pc=1;return;}
case 1:{f.push(-5330811072882077077L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(215668357120955L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0212.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(40,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0212.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(40,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0212.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0212.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
