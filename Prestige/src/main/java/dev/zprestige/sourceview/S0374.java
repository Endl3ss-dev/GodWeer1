package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0109;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0549;


import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_5224;
import net.minecraft.class_5251;
import net.minecraft.class_5481;
import net.minecraft.class_638;
import net.minecraft.class_746;

public class S0374 extends S0260 {
   public static volatile Set a;
   public static long k;
   public static Object[] l;
   public static String[] m;

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(0,5);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   @Override
   public void e$return$e4b0899c(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      a = new HashSet();
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static CallSite b(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$0().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/fF" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Class n(long var0, long var2) {
try {

      Class var5 = null;
      int var4 = m(var0, var2);
      Object var6 = l[var4];
      Object var10000 = var6;

      try {
         if (var10000 instanceof String) {
            var5 = Class.forName(m[var4]);
            l[var4] = var5;
            return var5;
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8.toString());
      }

      return (Class)(Object)(var6);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method h(Class var0, String var1, Class var2, int var3, Class[] var4) {
try {

      Method var5 = g(var0, var1, var2, var3, var4);
      if (var5 != null) {
         return var5;
      }

      Class[] var6 = var0.getInterfaces();
      if (var6 != null) {
         for (int var7 = 0; var7 < var6.length; var7++) {
            var5 = h(var6[var7], var1, var2, var3, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field h(Class var0, String var1, Class var2) {
try {

      Field var3 = g(var0, var1, var2);
      if (var3 != null) {
         return var3;
      }

      Class[] var4 = var0.getInterfaces();
      if (var4 != null) {
         for (int var5 = 0; var5 < var4.length; var5++) {
            var3 = h(var4[var5], var1, var2);
            if (var3 != null) {
               return var3;
            }
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void f() {
try {

      l[0] = "\u000f{Z\r\u000b\u0011\u0011s@Bl\u0010\u0000hM\u0018J\u0016";
      l[1] = "O\fKpL$D\u0003Z?-*O\b^e";
      l[2] = "/H\u001f6iF9H\u001alzQ.\u0003\u0019jvE?D\u000e}=W\u0003";
      l[3] = "HBN\u0019l\u0003=bE\u0016}L@zV\u0011t\u0005(";
      l[4] = "\u000b(C_|u\u000b(T\u0003pz\u0011cT\u001dpo\u0016\u0012\u0006G$+";
      l[5] = "~\u007f\u0010\u000b;Y~\u007f\u0007W7Vd4\u0007I7CcEV\u0010`\u0001";
      l[6] = "\u0001e$(}o\nj5g\u0000w\u0019m<.";
      l[7] = "\u0018G\u0019Gkw\u000eG\u001c\u001dx`\u0019\f\u001f\u001btt\bK\b\f?a:";
      l[8] = "F\"\fe6TM-\u001d*UYX \u0012A`[I3\u000emwV";
      l[9] = "I(+\u0019B+W 1V?;W";
      l[10] = boolean.class;
      m[10] = "java/lang/Boolean";
      l[11] = ";vr>\u0013h;veb\u001fg!=e|\u001fr&L5!N";
      l[12] = "hUN \n0hUY|\u0006?r\u001eYb\u0006*uo\r:Q";
      l[13] = "GF\u0013\u0004\u0003\u001bGF\u0004X\u000f\u0014]\r\u0004F\u000f\u0001Z|R\u001eVC";
      l[14] = "\u007f\u0012\fWNJ\u007f\u0012\u001b\u000bBEeY\u001b\u0015BPb(MK\u0011\u0017";
      l[15] = "R\n18V1R\n&dZ>HA&zZ+O0w#\u0003k";
      l[16] = "uC;C\r\u0003uC,\u001f\u0001\fo\b,\u0001\u0001\u0019hyz_U[";
      l[17] = "\u0015k\u0019+(4`K\u0012$9{\u0001E\u0019/=!u";
      l[18] = "[~\no\u0004^[~\u001d3\bQA5\u001d-\bDFDHrQ";
      l[19] = "U2@\"\nyK:ZmheL'";
      l[20] = "\u0016\u0014H\u000eh_[\u0017E\u001a\u000eL+AE\u000b2IVM\u0014\u00050 \u0014C\tMvR\u0013D\u0011\u0019\u000e";
      l[21] = "(\u00019\u000f\u0004[1\u001a?3\tL>\u001bCIPC~\\-\u000e\u0004]N";
      l[22] = "= %\u0013gI?z#\u000b\u0002\u001e\u0004}$\u001a|K>>(\u0012:t";
      l[23] = "i\u001f\u000b\u0015\u007fh\"E]\u001a\u0007b?\\\u0002\u001ekPo\u001c]F\u0007<.BSAdg7X\u0005y";
      l[24] = "H,2s;C\u0015(30TQ\u0019~&'\u0003\u0006G)~KdB\u001cw:35\\\bo";
      l[25] = "TjFEI~\u0003=@\u0019&{U-E\u0018JI\u0001a\u0019B\u0018\u001eT+\u001a\u0014]#CmC\r&";
      l[26] = "c~\u001d( C>z\u001ckOQ2,\t|\u0018\u0006mqR\u0010#\u00040{\u0002ptS6'";
      l[27] = "_\u000f\u000b01f\u0014U]?Il\tL\u0002;%^Y\f]bI2\u0018RSd*i\u0001H\u0005\\";
      l[28] = "\u001e'\u0004_aBKf\f\t\u0005]\u001a$T_ioN`\u0005\u0007>8\u001d7\fG~Y\u001cgF_\u0005";
      l[29] = "04.X( k-4\u000e\u0010,f4\u0017\u0015j\"m'LUy,s!>\u0003s?fH";
      l[30] = "Omv]Ki\u0002n{I-\u007frmu\u0018\u0013{\b4:E\u0016\u0016Lmt\u0019@l\u0015\")\u001c-";
      l[31] = "\u001e_\rl|/\u0011\u0003\u0011v\u0010)\\\u0003\u0015{l/Zn\ttjtK\u0014\u0010olH";
      l[32] = "%.RT!S<5Th*@8\u000eE\u00186)|!TQ2[{&L\u0005J";
      l[33] = "Va6p1uW1|hJyEsgk1\u0014F1l?qz\u0001er\u000fu}@7v}rzXc\u000e";
      l[34] = "\u0001\u0013lKG#\u0016U5R<\u007f\u0011\u0004)I@y\u0017i5FF\"\u0006\u0013,]@\u001e";
      l[35] = "%r\u001ccG\u001f*.\u0000y+\u0011w/~g\u0014\u001a+x\u0010 @\u0004\u001b|\u0017a\u0012\u0000i{\u0010yFx";
      l[36] = "n@\u0005JfN8J\u0016_\u000fQ+T\u0003Vt<(\u0016\b\u00024RoB\u001620U.\u0010\u0012@7R6Dj";
      l[37] = "y/@+\u0003\u0007#*@al\u0005#/Ch\u0010l!.Eh\u0015\u0005v?\u0014blS!8\u0017h\u001eT& C\u0010";
      l[38] = "\u0018\u0014'\u0006\"^C\r=P\u001aXN\u0014%Pvj\u0018Pt\f%=\u0012\u0003+\fuVH\u0006+F\u001a";
      l[39] = "kO\u007f\u0001\u000b7:Qk\u0019s*6N{\u0002\u001f\u0018f\u000e$[st'P*]\u0010/>J|e";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public void d$return$e4b0899c(Object[] var1) {
try {

      long var2 = (Long)(Object)(var1[0]);
      a = new HashSet();
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public boolean d$return$e4b089a0(Object[] var1) {
try {

      class_1657 var4 = (class_1657)(Object)(var1[0]);
      long var2 = (Long)(Object)(var1[1]);
      var2 = k ^ var2;
      boolean[] var5 = new boolean[]{false};
      __indy$4(__indy$2(__indy$1(var4, 7381047383011956046L, var2), 7377452809816127019L, var2), __indy$3(var5), 7377236580042694168L, var2);
      return var5[0];
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = d(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$5(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 219 && var8 != 'Y' && var8 != 221 && var8 != 255) {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'X') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 225) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 219) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'Y') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 221) {
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

   @S0118
   public void a(S0109 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(10,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$12$0(f);break;
case 1:__flow$12$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=46&&f.pc<57&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=59;continue;}
if(f.pc>=50&&f.pc<70&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=72;continue;}
if(f.pc>=78&&f.pc<105&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=107;continue;}
if(f.pc>=103&&f.pc<121&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=123;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static boolean a$return$e4b089a0(Object[] param0) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(7,6);
f.locals[0]=param0;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$14$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=41&&f.pc<59&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=61;continue;}
if(f.pc>=56&&f.pc<70&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=72;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
}


   public static int m(long var0, long var2) {
try {

      var0 ^= var2 << 48 | var2;
      int var4 = (int)(var0 >>> 46);
      if (m[var4] != null) {
         return var4;
      }

      Object var5 = l[var4];
      if (!(var5 instanceof String)) {
         return var4;
      }

      byte var6 = 0;

      var6 = switch ((int)(var0 >>> 42 & 63L)) {
         case 0 -> 57;
         case 1 -> 1;
         case 2 -> 22;
         case 3 -> 41;
         case 4 -> 44;
         case 5 -> 45;
         case 6 -> 4;
         case 7 -> 32;
         case 8 -> 10;
         case 9 -> 20;
         case 10 -> 7;
         case 11 -> 27;
         case 12 -> 40;
         case 13 -> 42;
         case 14 -> 23;
         case 15 -> 49;
         case 16 -> 51;
         case 17 -> 21;
         case 18 -> 11;
         case 19 -> 39;
         case 20 -> 16;
         case 21 -> 26;
         case 22 -> 29;
         case 23 -> 60;
         case 24 -> 12;
         case 25 -> 53;
         case 26 -> 25;
         case 27 -> 43;
         case 28 -> 38;
         case 29 -> 15;
         case 30 -> 24;
         case 31 -> 2;
         case 32 -> 28;
         case 33 -> 5;
         case 34 -> 47;
         case 35 -> 31;
         case 36 -> 18;
         case 37 -> 33;
         case 38 -> 36;
         case 39 -> 55;
         case 40 -> 46;
         case 41 -> 59;
         case 42 -> 54;
         case 43 -> 14;
         case 44 -> 0;
         case 45 -> 9;
         case 46 -> 8;
         case 47 -> 6;
         case 48 -> 30;
         case 49 -> 61;
         case 50 -> 35;
         case 51 -> 52;
         case 52 -> 48;
         case 53 -> 62;
         case 54 -> 3;
         case 55 -> 56;
         case 56 -> 63;
         case 57 -> 34;
         case 58 -> 37;
         case 59 -> 17;
         case 60 -> 50;
         case 61 -> 19;
         case 62 -> 13;
         default -> 58;
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

      m[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field o(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = l[var4];
      if (!(var5 instanceof String)) {
         return (Field)(Object)(var5);
      }

      String var6 = m[var4];
      int var7 = var6.indexOf(8);
      Class var8 = n(Long.parseLong(var6.substring(0, var7), 36), 0L);
      int var9 = var6.indexOf(8, ++var7);
      String var10 = var6.substring(var7, var9);
      Class var11 = n(Long.parseLong(var6.substring(++var9), 36), 0L);
      Class var12 = var8;

      while (true) {
         Field var13 = g(var12, var10, var11);
         if (var13 != null) {
            l[var4] = var13;
            return var13;
         }

         Class[] var14 = var12.getInterfaces();
         if (var14 != null) {
            for (int var15 = 0; var15 < var14.length; var15++) {
               var13 = h(var14[var15], var10, var11);
               if (var13 != null) {
                  l[var4] = var13;
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
            var12 = n(76760364989405L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method p(long var0, long var2) {
try {

      int var4 = m(var0, var2);
      Object var5 = l[var4];
      if (!(var5 instanceof String)) {
         return (Method)(Object)(var5);
      }

      String var6 = m[var4];
      int var7 = var6.indexOf(8);
      Class var8 = n(Long.parseLong(var6.substring(0, var7), 36), 0L);
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
         var15 = n(Long.parseLong(var6.substring(var12, var17), 36), 0L);
         if (var16 < var13) {
            var14[var16] = var15;
         }

         var12 = var17 + 1;
      }

      Class var23 = var8;

      while (true) {
         Method var26 = g(var23, var10, var15, var13, var14);
         if (var26 != null) {
            l[var4] = var26;
            return var26;
         }

         if (var23.getName().equals("java.lang.Object")) {
            break;
         }

         if ((var23 = var23.getSuperclass()) == null) {
            var23 = n(76760364989405L, 0L);
            break;
         }
      }

      var23 = var8;

      while (true) {
         Class[] var27;
         if ((var27 = var23.getInterfaces()) != null) {
            for (int var18 = 0; var18 < var27.length; var18++) {
               Method var19 = h(var27[var18], var10, var15, var13, var14);
               if (var19 != null) {
                  l[var4] = var19;
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
            var23 = n(76760364989405L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Field g(Class var0, String var1, Class var2) {
try {

      for (Field var6 : var0.getDeclaredFields()) {
         if (var6.getName().equals(var1) && var6.getType() == var2) {
            return var6;
         }
      }

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Method g(Class var0, String var1, Class var2, int var3, Class[] var4) {
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

   public static boolean lambda$matchesLocalColor$1(boolean[] var0, int var1, class_2583 var2, int var3) {
try {

      long var4 = k ^ 20233264980081L;
      __indy$32(
         __indy$30(__indy$29(__indy$28(b, -2679580126898825162L, var4), -2675198989704560553L, var4), -2675251355576726891L, var4),
         __indy$31(var2, var0),
         -2675324289845173594L,
         var4
      );
      return false;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static boolean lambda$matchesLocalColor$0(class_2583 param0, boolean[] param1, int param2, class_2583 param3, int param4) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(8,6);
f.locals[0]=param0;
f.locals[1]=param1;
f.locals[2]=param2;
f.locals[3]=param3;
f.locals[4]=param4;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$21$0(f);break;
case 1:__flow$21$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=8&&f.pc<14&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=16;continue;}
if(f.pc>=25&&f.pc<34&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=36;continue;}
if(f.pc>=32&&f.pc<50&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=52;continue;}
if(f.pc>=58&&f.pc<64&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=66;continue;}
if(f.pc>=62&&f.pc<80&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=82;continue;}
if(f.pc>=88&&f.pc<101&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=103;continue;}
if(f.pc>=99&&f.pc<116&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=118;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
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

   private static native MethodHandle __indy$0() throws Throwable;

   private static native class_2561 __indy$1(Object var0, long var1, long var3) throws Throwable;

   private static native class_5481 __indy$2(Object var0, long var1, long var3) throws Throwable;

   private static native class_5224 __indy$3(boolean[] var0) throws Throwable;

   private static native boolean __indy$4(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Object __indy$5(MethodHandle var0, Object[] var1) throws Throwable;

   private static native String[] __indy$6(long var0, long var2) throws Throwable;

   private static native class_638 __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native List __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native Iterator __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$13(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$17(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$19(long var0, long var2) throws Throwable;

   private static native Iterator __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native class_2561 __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$24(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$25(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native class_2561 __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native class_5481 __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native class_5224 __indy$31(class_2583 var0, boolean[] var1) throws Throwable;

   private static native boolean __indy$32(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native String[] __indy$33(long var0, long var2) throws Throwable;

   private static native MatchException __indy$34(Object var0, long var1, long var3) throws Throwable;

   private static native class_5251 __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native class_5251 __indy$37(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$38(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native class_5251 __indy$40(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$41(Object var0, long var1, long var3) throws Throwable;

   private static native class_5251 __indy$42(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$43(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$44(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$45(Object var0, long var1, long var3) throws Throwable;





private static void __flow$21$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0374.k,2);f.pc=1;return;}
case 1:{f.push(89380876072845L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[5]=f.pop();f.pc=4;return;}
case 4:{f.push(-8273153026824634080L,2);f.pc=5;return;}
case 5:{f.push(f.locals[5],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$33(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.locals[7]=f.pop();f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[3],1);f.pc=11;return;}
case 11:{f.push(f.locals[7],1);f.pc=12;return;}
case 12:{if(f.pop()!=null){f.pc=25;return;}f.pc=13;return;}
case 13:{if(f.pop()==null){f.pc=124;return;}f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.pc=22;return;}
case 16:{f.pc=17;return;}
case 17:{f.pc=18;return;}
case 18:{f.push(-8273685064303612528L,2);f.pc=19;return;}
case 19:{f.push(f.locals[5],2);f.pc=20;return;}
case 20:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$34(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=21;return;}
case 21:{throw (Throwable)f.pop();}
case 22:{f.pc=23;return;}
case 23:{f.pc=24;return;}
case 24:{f.push(f.locals[3],1);f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.push(-8273865216427975186L,2);f.pc=28;return;}
case 28:{f.push(f.locals[5],2);f.pc=29;return;}
case 29:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$35(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=30;return;}
case 30:{f.push(f.locals[7],1);f.pc=31;return;}
case 31:{if(f.pop()!=null){f.pc=58;return;}f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{if(f.pop()==null){f.pc=124;return;}f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.pc=42;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(-8273685064303612528L,2);f.pc=39;return;}
case 39:{f.push(f.locals[5],2);f.pc=40;return;}
case 40:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$36(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=41;return;}
case 41:{throw (Throwable)f.pop();}
case 42:{f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.push(f.locals[0],1);f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.push(-8273865216427975186L,2);f.pc=48;return;}
case 48:{f.push(f.locals[5],2);f.pc=49;return;}
case 49:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$37(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.pc=58;return;}
case 52:{f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.push(-8273685064303612528L,2);f.pc=55;return;}
case 55:{f.push(f.locals[5],2);f.pc=56;return;}
case 56:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$38(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=57;return;}
case 57:{throw (Throwable)f.pop();}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.push(f.locals[7],1);f.pc=61;return;}
case 61:{if(f.pop()!=null){f.pc=88;return;}f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{if(f.pop()==null){f.pc=124;return;}f.pc=64;return;}
case 64:{f.pc=65;return;}
case 65:{f.pc=72;return;}
case 66:{f.pc=67;return;}
case 67:{f.pc=68;return;}
case 68:{f.push(-8273685064303612528L,2);f.pc=69;return;}
case 69:{f.push(f.locals[5],2);f.pc=70;return;}
case 70:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$39(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=71;return;}
case 71:{throw (Throwable)f.pop();}
case 72:{f.pc=73;return;}
case 73:{f.pc=74;return;}
case 74:{f.push(f.locals[0],1);f.pc=75;return;}
case 75:{f.pc=76;return;}
case 76:{f.pc=77;return;}
case 77:{f.push(-8273865216427975186L,2);f.pc=78;return;}
case 78:{f.push(f.locals[5],2);f.pc=79;return;}
case 79:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$40(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.pc=88;return;}
case 82:{f.pc=83;return;}
case 83:{f.pc=84;return;}
case 84:{f.push(-8273685064303612528L,2);f.pc=85;return;}
case 85:{f.push(f.locals[5],2);f.pc=86;return;}
case 86:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$41(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=87;return;}
case 87:{throw (Throwable)f.pop();}
case 88:{f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.push(f.locals[3],1);f.pc=91;return;}
case 91:{f.push(-8273865216427975186L,2);f.pc=92;return;}
case 92:{f.push(f.locals[5],2);f.pc=93;return;}
case 93:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$42(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=94;return;}
case 94:{f.push(-8277020610935086945L,2);f.pc=95;return;}
case 95:{f.push(f.locals[5],2);f.pc=96;return;}
case 96:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$43(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=97;return;}
case 97:{f.push(f.locals[7],1);f.pc=98;return;}
case 98:{if(f.pop()!=null){f.pc=128;return;}f.pc=99;return;}
case 99:{f.pc=100;return;}
case 100:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=124;return;}f.pc=101;return;}
case 101:{f.pc=102;return;}
case 102:{f.pc=109;return;}
case 103:{f.pc=104;return;}
case 104:{f.pc=105;return;}
case 105:{f.push(-8273685064303612528L,2);f.pc=106;return;}
case 106:{f.push(f.locals[5],2);f.pc=107;return;}
case 107:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$44(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=108;return;}
case 108:{throw (Throwable)f.pop();}
case 109:{f.pc=110;return;}
case 110:{f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.push(f.locals[1],1);f.pc=113;return;}
case 113:{f.push(0,1);f.pc=114;return;}
case 114:{f.push(1,1);f.pc=115;return;}
case 115:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();if(array instanceof boolean[])((boolean[])array)[index]=dev.zprestige.sourceview.FlowFrame.i(v)!=0;else ((byte[])array)[index]=(byte)dev.zprestige.sourceview.FlowFrame.i(v);f.pc=116;return;}
case 116:{f.pc=117;return;}
case 117:{f.pc=124;return;}
case 118:{f.pc=119;return;}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$21$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.push(-8273685064303612528L,2);f.pc=121;return;}
case 121:{f.push(f.locals[5],2);f.pc=122;return;}
case 122:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$45(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=123;return;}
case 123:{throw (Throwable)f.pop();}
case 124:{f.pc=125;return;}
case 125:{f.pc=126;return;}
case 126:{f.pc=127;return;}
case 127:{f.push(0,1);f.pc=128;return;}
case 128:{f.pc=129;return;}
case 129:{f.pc=130;return;}
case 130:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

public S0374()throws Throwable{super();}

private void __flow$12$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0374.k,2);f.pc=1;return;}
case 1:{f.push(80933723106707L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(33445160913915L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.pc=11;return;}
case 11:{f.pc=12;return;}
case 12:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.util.HashSet.class),1);f.pc=13;return;}
case 13:{f.rearrange(89);f.pc=14;return;}
case 14:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=15;return;}
case 15:{f.locals[7]=f.pop();f.pc=16;return;}
case 16:{f.push(6283018420600789822L,2);f.pc=17;return;}
case 17:{f.push(f.locals[2],2);f.pc=18;return;}
case 18:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$6(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.push(dev.zprestige.sourceview.S0374.b,1);f.pc=22;return;}
case 22:{f.push(6283328520431105155L,2);f.pc=23;return;}
case 23:{f.push(f.locals[2],2);f.pc=24;return;}
case 24:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=25;return;}
case 25:{f.push(6283116955211742472L,2);f.pc=26;return;}
case 26:{f.push(f.locals[2],2);f.pc=27;return;}
case 27:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=28;return;}
case 28:{f.push(6286111101422401040L,2);f.pc=29;return;}
case 29:{f.push(f.locals[2],2);f.pc=30;return;}
case 30:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=31;return;}
case 31:{f.locals[8]=f.pop();f.pc=32;return;}
case 32:{f.locals[6]=f.pop();f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.push(f.locals[8],1);f.pc=36;return;}
case 36:{f.push(6286022862742906485L,2);f.pc=37;return;}
case 37:{f.push(f.locals[2],2);f.pc=38;return;}
case 38:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=39;return;}
case 39:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=137;return;}f.pc=40;return;}
case 40:{f.push(f.locals[8],1);f.pc=41;return;}
case 41:{f.push(6282849114846816522L,2);f.pc=42;return;}
case 42:{f.push(f.locals[2],2);f.pc=43;return;}
case 43:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=44;return;}
case 44:{f.push((net.minecraft.class_1657)(Object)f.pop(),1);f.pc=45;return;}
case 45:{f.locals[9]=f.pop();f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.push(f.locals[6],1);f.pc=49;return;}
case 49:{if(f.pop()!=null){f.pc=142;return;}f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.push(f.locals[9],1);f.pc=52;return;}
case 52:{f.push(dev.zprestige.sourceview.S0374.b,1);f.pc=53;return;}
case 53:{f.push(6283194309923395028L,2);f.pc=54;return;}
case 54:{f.push(f.locals[2],2);f.pc=55;return;}
case 55:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=56;return;}
case 56:{Object b=f.pop(),a=f.pop();if(a!=b){f.pc=78;return;}f.pc=57;return;}
case 57:{f.pc=58;return;}
case 58:{f.pc=65;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.push(6283629325458913166L,2);f.pc=62;return;}
case 62:{f.push(f.locals[2],2);f.pc=63;return;}
case 63:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=64;return;}
case 64:{throw (Throwable)f.pop();}
case 65:{f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.pc=68;return;}
case 68:{f.push(f.locals[6],1);f.pc=69;return;}
case 69:{if(f.pop()==null){f.pc=33;return;}f.pc=70;return;}
case 70:{f.pc=71;return;}
case 71:{f.pc=78;return;}
case 72:{f.pc=73;return;}
case 73:{f.pc=74;return;}
case 74:{f.push(6283629325458913166L,2);f.pc=75;return;}
case 75:{f.push(f.locals[2],2);f.pc=76;return;}
case 76:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=77;return;}
case 77:{throw (Throwable)f.pop();}
case 78:{f.pc=79;return;}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.push(f.locals[0],1);f.pc=82;return;}
case 82:{f.push(f.locals[9],1);f.pc=83;return;}
case 83:{f.push(f.locals[4],2);f.pc=84;return;}
case 84:{f.push(2,1);f.pc=85;return;}
case 85:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=86;return;}
case 86:{f.rearrange(91);f.pc=87;return;}
case 87:{f.rearrange(91);f.pc=88;return;}
case 88:{f.rearrange(87);f.pc=89;return;}
case 89:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=90;return;}
case 90:{f.push(1,1);f.pc=91;return;}
case 91:{f.rearrange(95);f.pc=92;return;}
case 92:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=93;return;}
case 93:{f.rearrange(90);f.pc=94;return;}
case 94:{f.rearrange(95);f.pc=95;return;}
case 95:{f.push(0,1);f.pc=96;return;}
case 96:{f.rearrange(95);f.pc=97;return;}
case 97:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=98;return;}
case 98:{f.push(6282923654897141973L,2);f.pc=99;return;}
case 99:{f.push(f.locals[2],2);f.pc=100;return;}
case 100:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$15(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=101;return;}
case 101:{f.push(f.locals[6],1);f.pc=102;return;}
case 102:{if(f.pop()!=null){f.pc=129;return;}f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=132;return;}f.pc=105;return;}
case 105:{f.pc=106;return;}
case 106:{f.pc=113;return;}
case 107:{f.pc=108;return;}
case 108:{f.pc=109;return;}
case 109:{f.push(6283629325458913166L,2);f.pc=110;return;}
case 110:{f.push(f.locals[2],2);f.pc=111;return;}
case 111:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=112;return;}
case 112:{throw (Throwable)f.pop();}
case 113:{f.pc=114;return;}
case 114:{f.pc=115;return;}
case 115:{f.pc=116;return;}
case 116:{f.push(f.locals[7],1);f.pc=117;return;}
case 117:{f.push(f.locals[9],1);f.pc=118;return;}
case 118:{f.push(6286076325200673936L,2);f.pc=119;return;}
case 119:{f.push(f.locals[2],2);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$12$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$17(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.pc=129;return;}
case 123:{f.pc=124;return;}
case 124:{f.pc=125;return;}
case 125:{f.push(6283629325458913166L,2);f.pc=126;return;}
case 126:{f.push(f.locals[2],2);f.pc=127;return;}
case 127:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=128;return;}
case 128:{throw (Throwable)f.pop();}
case 129:{f.pc=130;return;}
case 130:{f.pc=131;return;}
case 131:{f.rearrange(87);f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.pc=134;return;}
case 134:{f.pc=135;return;}
case 135:{f.push(f.locals[6],1);f.pc=136;return;}
case 136:{if(f.pop()==null){f.pc=33;return;}f.pc=137;return;}
case 137:{f.pc=138;return;}
case 138:{f.pc=139;return;}
case 139:{f.pc=140;return;}
case 140:{f.push(f.locals[7],1);f.pc=141;return;}
case 141:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0374.class,"a",((java.util.Set)(Object)(f.pop())));f.pc=142;return;}
case 142:{f.pc=143;return;}
case 143:{f.pc=144;return;}
case 144:{f.pc=145;return;}
case 145:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$14$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[0],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((java.lang.String)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[1]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0374.k,2);f.pc=14;return;}
case 14:{f.push(f.locals[2],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.push(-5451143708469774250L,2);f.pc=18;return;}
case 18:{f.push(f.locals[2],2);f.pc=19;return;}
case 19:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$19(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(dev.zprestige.sourceview.S0374.a,1);f.pc=23;return;}
case 23:{f.push(-5450513262411945287L,2);f.pc=24;return;}
case 24:{f.push(f.locals[2],2);f.pc=25;return;}
case 25:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=26;return;}
case 26:{f.locals[5]=f.pop();f.pc=27;return;}
case 27:{f.locals[4]=f.pop();f.pc=28;return;}
case 28:{f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{f.push(f.locals[5],1);f.pc=31;return;}
case 31:{f.push(-5452708894630574819L,2);f.pc=32;return;}
case 32:{f.push(f.locals[2],2);f.pc=33;return;}
case 33:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=34;return;}
case 34:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=90;return;}f.pc=35;return;}
case 35:{f.push(f.locals[5],1);f.pc=36;return;}
case 36:{f.push(-5451238324738291102L,2);f.pc=37;return;}
case 37:{f.push(f.locals[2],2);f.pc=38;return;}
case 38:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=39;return;}
case 39:{f.push((net.minecraft.class_1657)(Object)f.pop(),1);f.pc=40;return;}
case 40:{f.locals[6]=f.pop();f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.push(f.locals[6],1);f.pc=44;return;}
case 44:{f.push(-5451102964902173967L,2);f.pc=45;return;}
case 45:{f.push(f.locals[2],2);f.pc=46;return;}
case 46:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=47;return;}
case 47:{f.push(-5450691400614697590L,2);f.pc=48;return;}
case 48:{f.push(f.locals[2],2);f.pc=49;return;}
case 49:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$24(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=50;return;}
case 50:{f.push(f.locals[1],1);f.pc=51;return;}
case 51:{f.push(-5452423956462621252L,2);f.pc=52;return;}
case 52:{f.push(f.locals[2],2);f.pc=53;return;}
case 53:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$25(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=54;return;}
case 54:{f.push(f.locals[4],1);f.pc=55;return;}
case 55:{if(f.pop()!=null){f.pc=94;return;}f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.push(f.locals[4],1);f.pc=58;return;}
case 58:{if(f.pop()!=null){f.pc=82;return;}f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=67;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.push(-5450611401513216794L,2);f.pc=64;return;}
case 64:{f.push(f.locals[2],2);f.pc=65;return;}
case 65:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=66;return;}
case 66:{throw (Throwable)f.pop();}
case 67:{f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=85;return;}f.pc=70;return;}
case 70:{f.pc=71;return;}
case 71:{f.pc=78;return;}
case 72:{f.pc=73;return;}
case 73:{f.pc=74;return;}
case 74:{f.push(-5450611401513216794L,2);f.pc=75;return;}
case 75:{f.push(f.locals[2],2);f.pc=76;return;}
case 76:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0374.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=77;return;}
case 77:{throw (Throwable)f.pop();}
case 78:{f.pc=79;return;}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.push(1,1);f.pc=82;return;}
case 82:{f.pc=83;return;}
case 83:{f.pc=84;return;}
case 84:{f.result=f.pop();f.done=true;return;}
case 85:{f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.pc=88;return;}
case 88:{f.push(f.locals[4],1);f.pc=89;return;}
case 89:{if(f.pop()==null){f.pc=28;return;}f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.push(0,1);f.pc=94;return;}
case 94:{f.pc=95;return;}
case 95:{f.pc=96;return;}
case 96:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(5102939357559707114L,2);f.pc=1;return;}
case 1:{f.push(6414396610565000517L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(277872322691336L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0374.class,"k",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(40,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0374.class,"l",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(40,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0374.class,"m",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0374.f();f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.util.HashSet.class),1);f.pc=18;return;}
case 18:{f.rearrange(89);f.pc=19;return;}
case 19:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=20;return;}
case 20:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0374.class,"a",((java.util.Set)(Object)(f.pop())));f.pc=21;return;}
case 21:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
