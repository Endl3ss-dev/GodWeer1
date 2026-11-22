package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0038;
import dev.zprestige.sourceview.S0117;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0138;
import dev.zprestige.sourceview.S0225;
import dev.zprestige.sourceview.S0549;
import net.minecraft.class_2824;
import net.minecraft.class_2885;
import net.minecraft.class_2886;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.class_1799;
import net.minecraft.class_2596;
import net.minecraft.class_2868;
import net.minecraft.class_746;

public class S0216 implements S0225 {
   public int a;
   public boolean c;
   public static long d;
   public static Object[] e;
   public static String[] f;

   public S0216(long var1)  throws Throwable {
      var1 = d ^ var1;
      long var3 = var1 ^ 88558921389742L;
      super();
try {

      this.a = -1;
      __indy$1(__indy$0(-2328804608201496003L, var1), new Object[]{this, var3}, -2328758798868701541L, var1);
   
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
      if (f[var4] != null) {
         return var4;
      }

      Object var5 = e[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 17;
         case 1 -> 48;
         case 2 -> 30;
         case 3 -> 26;
         case 4 -> 28;
         case 5 -> 9;
         case 6 -> 15;
         case 7 -> 12;
         case 8 -> 49;
         case 9 -> 8;
         case 10 -> 19;
         case 11 -> 43;
         case 12 -> 37;
         case 13 -> 52;
         case 14 -> 46;
         case 15 -> 18;
         case 16 -> 11;
         case 17 -> 57;
         case 18 -> 56;
         case 19 -> 33;
         case 20 -> 0;
         case 21 -> 3;
         case 22 -> 59;
         case 23 -> 14;
         case 24 -> 27;
         case 25 -> 41;
         case 26 -> 7;
         case 27 -> 38;
         case 28 -> 4;
         case 29 -> 51;
         case 30 -> 62;
         case 31 -> 53;
         case 32 -> 60;
         case 33 -> 29;
         case 34 -> 61;
         case 35 -> 55;
         case 36 -> 58;
         case 37 -> 6;
         case 38 -> 34;
         case 39 -> 42;
         case 40 -> 31;
         case 41 -> 25;
         case 42 -> 22;
         case 43 -> 36;
         case 44 -> 2;
         case 45 -> 24;
         case 46 -> 1;
         case 47 -> 39;
         case 48 -> 54;
         case 49 -> 32;
         case 50 -> 35;
         case 51 -> 21;
         case 52 -> 23;
         case 53 -> 44;
         case 54 -> 10;
         case 55 -> 40;
         case 56 -> 5;
         case 57 -> 47;
         case 58 -> 50;
         case 59 -> 63;
         case 60 -> 16;
         case 61 -> 13;
         case 62 -> 45;
         default -> 20;
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

   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 230 && var8 != 209 && var8 != 'p' && var8 != 'V') {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'L') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 'D') {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = g(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 230) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 209) {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 'p') {
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
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(9,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$4$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=26&&f.pc<35&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=37;continue;}
if(f.pc>=31&&f.pc<47&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=47;continue;}
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
            var23 = f(1677695486588449L, 0L);
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
            var23 = f(1677695486588449L, 0L);
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

   public void a$return$e4b0899c(Object[] var1) {
try {

      int var2 = (Integer)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = d ^ var3;
      long var5 = var3 ^ 88166742632087L;
      this.a = var2;
      this.c = true;
      __indy$8(new Object[]{new class_2868(var2), var5}, 257117171078321746L, var3);
      this.c = false;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0138 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(9,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$13$0(f);break;
case 1:__flow$13$1(f);break;
case 2:__flow$13$2(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=18&&f.pc<27&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=29;continue;}
if(f.pc>=24&&f.pc<45&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=47;continue;}
if(f.pc>=53&&f.pc<59&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=61;continue;}
if(f.pc>=57&&f.pc<73&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=75;continue;}
if(f.pc>=67&&f.pc<84&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=86;continue;}
if(f.pc>=81&&f.pc<104&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=104;continue;}
if(f.pc>=120&&f.pc<126&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=128;continue;}
if(f.pc>=124&&f.pc<143&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=145;continue;}
if(f.pc>=151&&f.pc<157&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=159;continue;}
if(f.pc>=155&&f.pc<174&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=176;continue;}
if(f.pc>=182&&f.pc<188&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=190;continue;}
if(f.pc>=186&&f.pc<223&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=225;continue;}
if(f.pc>=231&&f.pc<251&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=253;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   @S0118
   public void a(S0038 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(7,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$14$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=14&&f.pc<23&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=25;continue;}
if(f.pc>=19&&f.pc<35&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=37;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$36().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/cq" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public int a$return$e4b0898f(Object[] var1) {
try {

      return this.a;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a$return$9b75() {
try {

      e[0] = "\u001c\u001b`TNJ\n\u001be\u000e]]\u001dPf\bQI\f\u0017q\u001f\u001a[0";
      e[1] = "`u9Ky0\u0015U2Dh\u007fhM!Ca6\u0000";
      e[2] = "\u000fcwr.\u0003\u0019cr(=\u0014\u000e(q.1\u0000\u001fof9z\u0010\u0019";
      e[3] = "!C|f\u0016YTcwi\u0007\u00165m|b\u0003LA";
      e[4] = int.class;
      f[4] = "java/lang/Integer";
      e[5] = "=P4|}N+P1&nY<\u001b2 bM-\\%7)]j";
      e[6] = "s%\u0019{\u001e\u001b\u0006\u0005\u0012t\u000fTg\u000b\u0019\u007f\u000b\u000e\u0013";
      e[7] = void.class;
      f[7] = "java/lang/Void";
      e[8] = "aQ\u001a/9\\wQ\u001fu*K`\u001a\u001cs&_q]\u000bdmOt";
      e[9] = "+\u001a\u0018/\u000bX \u0015\t`hU5\u0018\u0006\u000b]W$\u000b\u001a'JZ";
      e[10] = "\u0019hZ\u001b5\u0017lHQ\u0014$X\rFZ\u001f \u0002y";
      e[11] = "+M\t0y=+M\u001elu21\u0006\u001eru'6wN/$";
      e[12] = "ei\u0000V2@ei\u0017\n>O\u007f\"\u0017\u0014>ZxSCLi";
      e[13] = "\u001a\u0006`N\\5o&kAMz\u000e(`JI z";
      e[14] = "(_G9#!>_Bc06)\u0014Ae<\"8SVrw3$";
      e[15] = "\u001b\u0016tyOon6\u007fv^ \u000f8t}Zz{";
      e[16] = "q\u0012\u001aPd\rq\u0012\r\fh\u0002kY\r\u0012h\u0017l(\\K0R";
      e[17] = "2Ebb\u0014~2Eu>\u0018q(\u000eu \u0018d/\u007f'{@.";
      e[18] = "#5\u000ft'$V\u0015\u0004{6k7\u001b\u000fp21C";
      e[19] = boolean.class;
      f[19] = "java/lang/Boolean";
      e[20] = "B,\u0017\t/#T,\u0012S<4Cg\u0011U0 R \u0006B{0J \u0004I!}v;\u0004T!:A,";
      e[21] = "^\u0000M\u0018\u001e<H\u0000HB\r+_KKD\u0001?N\f\\SJ.u";
      e[22] = "\u0017{\bM\nUb[\u0003B\u001b\u001a\u0003U\bI\u001f@w";
      e[23] = "D@9gqxOO((\u0010vDD,r";
      e[24] = ";\u0005J0tul\u0015B^{L>\u0011\u0010cv}v\u001aE4\u0011";
      e[25] = "x\\E|BjkO@r88\u0015]\u0000\u007fZ3iNDbV";
      e[26] = "\u0015:u\\\n`\u0006~hPkux)jP[~\u0003% ]\u000e\u001cE}.U\u0004v\byo^k";
      e[27] = "+[\tG8*n\u001aOS\u0003(uNS@T\u007f+\u0019\u000b,3$x\u001f\u000e\u001c;+%N";
      e[28] = "5N+J<\t=Av\u001b\u0005\\hP'\u0011in;\u0014{I\u0005R4\u00135GyKwN7v";
      e[29] = "\u0004h).sDV&4b\u0015\u0011=v\"p%\u001aFzh}px\u0007j(tu\u0000\\\u007f0x\u0015";
      e[30] = "\u001b.D%d\u0013L>LKh*I*A{cQE`L.\u0001\u0017\u001dnD$kZ\u0019/OK";
      e[31] = "LV_UwzOW\u0019IF~qH]@vv\nD\u0017M#\u0014L\u001c\u0019E)~\u0001\u0018XNF";
      e[32] = "s\u001c=D\u001awrP6\u0007*#H\u000f\"X\u001a(3\u0003hUOJu[f]E 8_'V*";
      e[33] = "`ow&X\bcn1:i\u000f]!m1X\u001b%rlb\ffc|bl\u0014\u001e0}18i";
      e[34] = ":B\u0011Dahh\f\f\b\u0007=\u0003\\\u001a\u001a76xPP\u0017bToIX\u001f|4hV\u0007\u001f\u0007";
      e[35] = "\u001fL \u0017\u001aZ\u000f\u001a*\u0017t\nv\u001b8\u001bD\u0001\r\u0017r\u0016\u0011c\u001cH&\u001b\u0016\bLL'\u001at";
   
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
            var12 = f(1677695486588449L, 0L);
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

   private static native MatchException __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native int __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$7(MethodHandle var0, Object[] var1) throws Throwable;

   private static native void __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$9(long var0, long var2) throws Throwable;

   private static native MatchException __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native class_2596 __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$15(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$16(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native class_2596 __indy$18(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native class_2596 __indy$20(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native class_2596 __indy$23(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$24(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native class_1799 __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$30(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$32(long var0, long var2) throws Throwable;

   private static native MatchException __indy$33(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$34(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$35(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MethodHandle __indy$36() throws Throwable;









private void __flow$4$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=5;return;}
case 5:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=6;return;}
case 6:{f.locals[2]=f.pop();f.pc=7;return;}
case 7:{f.rearrange(87);f.pc=8;return;}
case 8:{f.push(dev.zprestige.sourceview.S0216.d,2);f.pc=9;return;}
case 9:{f.push(f.locals[2],2);f.pc=10;return;}
case 10:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.push(f.locals[2],2);f.pc=13;return;}
case 13:{f.rearrange(92);f.pc=14;return;}
case 14:{f.push(58839704735808L,2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[4]=f.pop();f.pc=17;return;}
case 17:{f.rearrange(92);f.pc=18;return;}
case 18:{f.push(140159301866218L,2);f.pc=19;return;}
case 19:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=20;return;}
case 20:{f.locals[6]=f.pop();f.pc=21;return;}
case 21:{f.rearrange(88);f.pc=22;return;}
case 22:{f.push(3839361929349287995L,2);f.pc=23;return;}
case 23:{f.push(f.locals[2],2);f.pc=24;return;}
case 24:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=25;return;}
case 25:{f.locals[8]=f.pop();f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.push(f.locals[0],1);f.pc=29;return;}
case 29:{f.push(f.locals[8],1);f.pc=30;return;}
case 30:{if(f.pop()!=null){f.pc=103;return;}f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.push(((dev.zprestige.sourceview.S0216)(Object)f.pop()).a,1);f.pc=33;return;}
case 33:{f.push(-1,1);f.pc=34;return;}
case 34:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a!=b){f.pc=53;return;}f.pc=35;return;}
case 35:{f.pc=36;return;}
case 36:{f.pc=43;return;}
case 37:{f.pc=38;return;}
case 38:{f.pc=39;return;}
case 39:{f.push(3838840652331186664L,2);f.pc=40;return;}
case 40:{f.push(f.locals[2],2);f.pc=41;return;}
case 41:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=42;return;}
case 42:{throw (Throwable)f.pop();}
case 43:{f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.done=true;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(3838840652331186664L,2);f.pc=50;return;}
case 50:{f.push(f.locals[2],2);f.pc=51;return;}
case 51:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=52;return;}
case 52:{throw (Throwable)f.pop();}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.push(f.locals[0],1);f.pc=57;return;}
case 57:{f.push(1,1);f.pc=58;return;}
case 58:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0216.class,receiver,"c",(dev.zprestige.sourceview.FlowFrame.i(value) != 0));f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.push(dev.zprestige.sourceview.FlowFrame.pending(net.minecraft.class_2868.class),1);f.pc=62;return;}
case 62:{f.rearrange(89);f.pc=63;return;}
case 63:{f.push(f.locals[6],2);f.pc=64;return;}
case 64:{f.push(1,1);f.pc=65;return;}
case 65:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=66;return;}
case 66:{f.rearrange(91);f.pc=67;return;}
case 67:{f.rearrange(91);f.pc=68;return;}
case 68:{f.rearrange(87);f.pc=69;return;}
case 69:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=70;return;}
case 70:{f.push(0,1);f.pc=71;return;}
case 71:{f.rearrange(95);f.pc=72;return;}
case 72:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=73;return;}
case 73:{f.push(3839693607382662078L,2);f.pc=74;return;}
case 74:{f.push(f.locals[2],2);f.pc=75;return;}
case 75:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=76;return;}
case 76:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{int.class},new Object[]{dev.zprestige.sourceview.FlowFrame.i(a0)});f.pc=77;return;}
case 77:{f.push(f.locals[4],2);f.pc=78;return;}
case 78:{f.push(2,1);f.pc=79;return;}
case 79:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=80;return;}
case 80:{f.rearrange(91);f.pc=81;return;}
case 81:{f.rearrange(91);f.pc=82;return;}
case 82:{f.rearrange(87);f.pc=83;return;}
case 83:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=84;return;}
case 84:{f.push(1,1);f.pc=85;return;}
case 85:{f.rearrange(95);f.pc=86;return;}
case 86:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=87;return;}
case 87:{f.rearrange(90);f.pc=88;return;}
case 88:{f.rearrange(95);f.pc=89;return;}
case 89:{f.push(0,1);f.pc=90;return;}
case 90:{f.rearrange(95);f.pc=91;return;}
case 91:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=92;return;}
case 92:{f.push(3838776726902923397L,2);f.pc=93;return;}
case 93:{f.push(f.locals[2],2);f.pc=94;return;}
case 94:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0216.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=95;return;}
case 95:{f.pc=96;return;}
case 96:{f.pc=97;return;}
case 97:{f.push(f.locals[0],1);f.pc=98;return;}
case 98:{f.push(0,1);f.pc=99;return;}
case 99:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0216.class,receiver,"c",(dev.zprestige.sourceview.FlowFrame.i(value) != 0));f.pc=100;return;}
case 100:{f.pc=101;return;}
case 101:{f.pc=102;return;}
case 102:{f.push(f.locals[0],1);f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{f.pc=105;return;}
case 105:{f.push(-1,1);f.pc=106;return;}
case 106:{Object value=f.pop();Object receiver=f.pop();dev.zprestige.sourceview.FlowFrame.instanceField(dev.zprestige.sourceview.S0216.class,receiver,"a",dev.zprestige.sourceview.FlowFrame.i(value));f.pc=107;return;}
case 107:{f.pc=108;return;}
case 108:{f.pc=109;return;}
case 109:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$13$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0216.d,2);f.pc=1;return;}
case 1:{f.push(105842915434681L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(79982487273910L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(92);f.pc=10;return;}
case 10:{f.push(111138606175735L,2);f.pc=11;return;}
case 11:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=12;return;}
case 12:{f.locals[6]=f.pop();f.pc=13;return;}
case 13:{f.rearrange(88);f.pc=14;return;}
case 14:{f.push(-2769961588570637060L,2);f.pc=15;return;}
case 15:{f.push(f.locals[2],2);f.pc=16;return;}
case 16:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$9(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=17;return;}
case 17:{f.locals[8]=f.pop();f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(f.locals[0],1);f.pc=21;return;}
case 21:{f.push(((dev.zprestige.sourceview.S0216)(Object)f.pop()).a,1);f.pc=22;return;}
case 22:{f.push(f.locals[8],1);f.pc=23;return;}
case 23:{if(f.pop()!=null){f.pc=53;return;}f.pc=24;return;}
case 24:{f.pc=25;return;}
case 25:{f.push(-1,1);f.pc=26;return;}
case 26:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a==b){f.pc=259;return;}f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.pc=35;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.push(-2773807310013229777L,2);f.pc=32;return;}
case 32:{f.push(f.locals[2],2);f.pc=33;return;}
case 33:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=34;return;}
case 34:{throw (Throwable)f.pop();}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(f.locals[1],1);f.pc=39;return;}
case 39:{f.push(0,1);f.pc=40;return;}
case 40:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=41;return;}
case 41:{f.push(-2773718445658845935L,2);f.pc=42;return;}
case 42:{f.push(f.locals[2],2);f.pc=43;return;}
case 43:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$11(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=44;return;}
case 44:{f.push((f.pop() instanceof net.minecraft.class_2868?1:0),1);f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.pc=53;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(-2773807310013229777L,2);f.pc=50;return;}
case 50:{f.push(f.locals[2],2);f.pc=51;return;}
case 51:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=52;return;}
case 52:{throw (Throwable)f.pop();}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.push(f.locals[8],1);f.pc=56;return;}
case 56:{if(f.pop()!=null){f.pc=120;return;}f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=110;return;}f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=67;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.push(-2773807310013229777L,2);f.pc=64;return;}
case 64:{f.push(f.locals[2],2);f.pc=65;return;}
case 65:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=66;return;}
case 66:{throw (Throwable)f.pop();}
case 67:{f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.push(f.locals[0],1);f.pc=70;return;}
case 70:{f.push(((dev.zprestige.sourceview.S0216)(Object)f.pop()).c,1);f.pc=71;return;}
case 71:{f.push(f.locals[8],1);f.pc=72;return;}
case 72:{if(f.pop()!=null){f.pc=120;return;}f.pc=73;return;}
case 73:{f.pc=74;return;}
case 74:{f.pc=81;return;}
case 75:{f.pc=76;return;}
case 76:{f.pc=77;return;}
case 77:{f.push(-2773807310013229777L,2);f.pc=78;return;}
case 78:{f.push(f.locals[2],2);f.pc=79;return;}
case 79:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=80;return;}
case 80:{throw (Throwable)f.pop();}
case 81:{f.pc=82;return;}
case 82:{f.pc=83;return;}
case 83:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=110;return;}f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.pc=92;return;}
case 86:{f.pc=87;return;}
case 87:{f.pc=88;return;}
case 88:{f.push(-2773807310013229777L,2);f.pc=89;return;}
case 89:{f.push(f.locals[2],2);f.pc=90;return;}
case 90:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=91;return;}
case 91:{throw (Throwable)f.pop();}
case 92:{f.pc=93;return;}
case 93:{f.pc=94;return;}
case 94:{f.pc=95;return;}
case 95:{f.push(f.locals[1],1);f.pc=96;return;}
case 96:{f.push(0,1);f.pc=97;return;}
case 97:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=98;return;}
case 98:{f.push(-2770101993524671032L,2);f.pc=99;return;}
case 99:{f.push(f.locals[2],2);f.pc=100;return;}
case 100:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0216.__indy$16(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=101;return;}
case 101:{f.pc=102;return;}
case 102:{f.pc=103;return;}
case 103:{f.done=true;return;}
case 104:{f.pc=105;return;}
case 105:{f.pc=106;return;}
case 106:{f.push(-2773807310013229777L,2);f.pc=107;return;}
case 107:{f.push(f.locals[2],2);f.pc=108;return;}
case 108:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=109;return;}
case 109:{throw (Throwable)f.pop();}
case 110:{f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.pc=113;return;}
case 113:{f.push(f.locals[1],1);f.pc=114;return;}
case 114:{f.push(0,1);f.pc=115;return;}
case 115:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=116;return;}
case 116:{f.push(-2773718445658845935L,2);f.pc=117;return;}
case 117:{f.push(f.locals[2],2);f.pc=118;return;}
case 118:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$18(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=119;return;}
case 119:{f.push((f.pop() instanceof net.minecraft.class_2886?1:0),1);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$13$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.push(f.locals[8],1);f.pc=123;return;}
case 123:{if(f.pop()!=null){f.pc=151;return;}f.pc=124;return;}
case 124:{f.pc=125;return;}
case 125:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=234;return;}f.pc=126;return;}
case 126:{f.pc=127;return;}
case 127:{f.pc=134;return;}
case 128:{f.pc=129;return;}
case 129:{f.pc=130;return;}
case 130:{f.push(-2773807310013229777L,2);f.pc=131;return;}
case 131:{f.push(f.locals[2],2);f.pc=132;return;}
case 132:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$19(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=133;return;}
case 133:{throw (Throwable)f.pop();}
case 134:{f.pc=135;return;}
case 135:{f.pc=136;return;}
case 136:{f.push(f.locals[1],1);f.pc=137;return;}
case 137:{f.push(0,1);f.pc=138;return;}
case 138:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=139;return;}
case 139:{f.push(-2773718445658845935L,2);f.pc=140;return;}
case 140:{f.push(f.locals[2],2);f.pc=141;return;}
case 141:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$20(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=142;return;}
case 142:{f.push((f.pop() instanceof net.minecraft.class_2885?1:0),1);f.pc=143;return;}
case 143:{f.pc=144;return;}
case 144:{f.pc=151;return;}
case 145:{f.pc=146;return;}
case 146:{f.pc=147;return;}
case 147:{f.push(-2773807310013229777L,2);f.pc=148;return;}
case 148:{f.push(f.locals[2],2);f.pc=149;return;}
case 149:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=150;return;}
case 150:{throw (Throwable)f.pop();}
case 151:{f.pc=152;return;}
case 152:{f.pc=153;return;}
case 153:{f.push(f.locals[8],1);f.pc=154;return;}
case 154:{if(f.pop()!=null){f.pc=182;return;}f.pc=155;return;}
case 155:{f.pc=156;return;}
case 156:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=234;return;}f.pc=157;return;}
case 157:{f.pc=158;return;}
case 158:{f.pc=165;return;}
case 159:{f.pc=160;return;}
case 160:{f.pc=161;return;}
case 161:{f.push(-2773807310013229777L,2);f.pc=162;return;}
case 162:{f.push(f.locals[2],2);f.pc=163;return;}
case 163:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=164;return;}
case 164:{throw (Throwable)f.pop();}
case 165:{f.pc=166;return;}
case 166:{f.pc=167;return;}
case 167:{f.push(f.locals[1],1);f.pc=168;return;}
case 168:{f.push(0,1);f.pc=169;return;}
case 169:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=170;return;}
case 170:{f.push(-2773718445658845935L,2);f.pc=171;return;}
case 171:{f.push(f.locals[2],2);f.pc=172;return;}
case 172:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$23(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=173;return;}
case 173:{f.push((f.pop() instanceof net.minecraft.class_2824?1:0),1);f.pc=174;return;}
case 174:{f.pc=175;return;}
case 175:{f.pc=182;return;}
case 176:{f.pc=177;return;}
case 177:{f.pc=178;return;}
case 178:{f.push(-2773807310013229777L,2);f.pc=179;return;}
case 179:{f.push(f.locals[2],2);f.pc=180;return;}
case 180:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$24(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=181;return;}
case 181:{throw (Throwable)f.pop();}
case 182:{f.pc=183;return;}
case 183:{f.pc=184;return;}
case 184:{f.push(f.locals[8],1);f.pc=185;return;}
case 185:{if(f.pop()!=null){f.pc=231;return;}f.pc=186;return;}
case 186:{f.pc=187;return;}
case 187:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=259;return;}f.pc=188;return;}
case 188:{f.pc=189;return;}
case 189:{f.pc=196;return;}
case 190:{f.pc=191;return;}
case 191:{f.pc=192;return;}
case 192:{f.push(-2773807310013229777L,2);f.pc=193;return;}
case 193:{f.push(f.locals[2],2);f.pc=194;return;}
case 194:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=195;return;}
case 195:{throw (Throwable)f.pop();}
case 196:{f.pc=197;return;}
case 197:{f.pc=198;return;}
case 198:{f.push(dev.zprestige.sourceview.S0216.b,1);f.pc=199;return;}
case 199:{f.push(-2769720939410764697L,2);f.pc=200;return;}
case 200:{f.push(f.locals[2],2);f.pc=201;return;}
case 201:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=202;return;}
case 202:{f.push(-2770244375311087893L,2);f.pc=203;return;}
case 203:{f.push(f.locals[2],2);f.pc=204;return;}
case 204:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=205;return;}
case 205:{f.push(f.locals[4],2);f.pc=206;return;}
case 206:{f.push(2,1);f.pc=207;return;}
case 207:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=208;return;}
case 208:{f.rearrange(91);f.pc=209;return;}
case 209:{f.rearrange(91);f.pc=210;return;}
case 210:{f.rearrange(87);f.pc=211;return;}
case 211:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=212;return;}
case 212:{f.push(1,1);f.pc=213;return;}
case 213:{f.rearrange(95);f.pc=214;return;}
case 214:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=215;return;}
case 215:{f.rearrange(90);f.pc=216;return;}
case 216:{f.rearrange(95);f.pc=217;return;}
case 217:{f.push(0,1);f.pc=218;return;}
case 218:{f.rearrange(95);f.pc=219;return;}
case 219:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=220;return;}
case 220:{f.push(-2773743819967333089L,2);f.pc=221;return;}
case 221:{f.push(f.locals[2],2);f.pc=222;return;}
case 222:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=223;return;}
case 223:{f.pc=224;return;}
case 224:{f.pc=231;return;}
case 225:{f.pc=226;return;}
case 226:{f.pc=227;return;}
case 227:{f.push(-2773807310013229777L,2);f.pc=228;return;}
case 228:{f.push(f.locals[2],2);f.pc=229;return;}
case 229:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$29(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=230;return;}
case 230:{throw (Throwable)f.pop();}
case 231:{f.pc=232;return;}
case 232:{f.pc=233;return;}
case 233:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=259;return;}f.pc=234;return;}
case 234:{f.pc=235;return;}
case 235:{f.pc=236;return;}
case 236:{f.pc=237;return;}
case 237:{f.push(f.locals[0],1);f.pc=238;return;}
case 238:{f.push(f.locals[6],2);f.pc=239;return;}
case 239:{f.push(1,1);f.pc=240;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$13$2(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 240:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=241;return;}
case 241:{f.rearrange(91);f.pc=242;return;}
case 242:{f.rearrange(91);f.pc=243;return;}
case 243:{f.rearrange(87);f.pc=244;return;}
case 244:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=245;return;}
case 245:{f.push(0,1);f.pc=246;return;}
case 246:{f.rearrange(95);f.pc=247;return;}
case 247:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=248;return;}
case 248:{f.push(-2770025457355524689L,2);f.pc=249;return;}
case 249:{f.push(f.locals[2],2);f.pc=250;return;}
case 250:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0216.__indy$30(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=251;return;}
case 251:{f.pc=252;return;}
case 252:{f.pc=259;return;}
case 253:{f.pc=254;return;}
case 254:{f.pc=255;return;}
case 255:{f.push(-2773807310013229777L,2);f.pc=256;return;}
case 256:{f.push(f.locals[2],2);f.pc=257;return;}
case 257:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$31(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=258;return;}
case 258:{throw (Throwable)f.pop();}
case 259:{f.pc=260;return;}
case 260:{f.pc=261;return;}
case 261:{f.pc=262;return;}
case 262:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$14$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0216.d,2);f.pc=1;return;}
case 1:{f.push(54155910924449L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(57269757771247L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.push(-1038273648232753948L,2);f.pc=11;return;}
case 11:{f.push(f.locals[2],2);f.pc=12;return;}
case 12:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$32(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=13;return;}
case 13:{f.locals[6]=f.pop();f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.push(f.locals[0],1);f.pc=17;return;}
case 17:{f.push(f.locals[6],1);f.pc=18;return;}
case 18:{if(f.pop()!=null){f.pc=43;return;}f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(((dev.zprestige.sourceview.S0216)(Object)f.pop()).a,1);f.pc=21;return;}
case 21:{f.push(-1,1);f.pc=22;return;}
case 22:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a==b){f.pc=58;return;}f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.pc=31;return;}
case 25:{f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.push(-1037756522149266121L,2);f.pc=28;return;}
case 28:{f.push(f.locals[2],2);f.pc=29;return;}
case 29:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$33(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=30;return;}
case 30:{throw (Throwable)f.pop();}
case 31:{f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.push(f.locals[0],1);f.pc=35;return;}
case 35:{f.pc=36;return;}
case 36:{f.pc=43;return;}
case 37:{f.pc=38;return;}
case 38:{f.pc=39;return;}
case 39:{f.push(-1037756522149266121L,2);f.pc=40;return;}
case 40:{f.push(f.locals[2],2);f.pc=41;return;}
case 41:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0216.__indy$34(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=42;return;}
case 42:{throw (Throwable)f.pop();}
case 43:{f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.push(f.locals[4],2);f.pc=46;return;}
case 46:{f.push(1,1);f.pc=47;return;}
case 47:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=48;return;}
case 48:{f.rearrange(91);f.pc=49;return;}
case 49:{f.rearrange(91);f.pc=50;return;}
case 50:{f.rearrange(87);f.pc=51;return;}
case 51:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=52;return;}
case 52:{f.push(0,1);f.pc=53;return;}
case 53:{f.rearrange(95);f.pc=54;return;}
case 54:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=55;return;}
case 55:{f.push(-1038443070267466313L,2);f.pc=56;return;}
case 56:{f.push(f.locals[2],2);f.pc=57;return;}
case 57:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0216.__indy$35(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-8340597618444264887L,2);f.pc=1;return;}
case 1:{f.push(3084157190247114730L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(175042339549016L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0216.class,"d",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(36,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0216.class,"e",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(36,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0216.class,"f",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0216.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
