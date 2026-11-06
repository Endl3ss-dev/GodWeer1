package dev.zprestige.sourceview;
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
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1665;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public final class S0431 extends class_1665 {
   public boolean a;
   public static long b;
   public static Object[] c;
   public static String[] d;

   public S0431(class_1937 var1, class_1309 var2, class_1799 var3, long var4)  throws Throwable {
      var4 = b ^ var4;
      super(__indy$0(5239772267451468485L, var4), var2, var1, var3, var3);
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
            var12 = b(1134725188954668L, 0L);
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
            var23 = b(1134725188954668L, 0L);
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
            var23 = b(1134725188954668L, 0L);
         }
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

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$1().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/g6" + " : " + var1 + " : " + var2.toString(), var5);
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
      return __indy$2(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle a(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 248 && var8 != 207 && var8 != 214 && var8 != 220) {
            var11 = d(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'U') {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 194) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = c(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 248) {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 207) {
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
         case 0 -> 39;
         case 1 -> 5;
         case 2 -> 24;
         case 3 -> 59;
         case 4 -> 7;
         case 5 -> 17;
         case 6 -> 15;
         case 7 -> 1;
         case 8 -> 2;
         case 9 -> 14;
         case 10 -> 32;
         case 11 -> 47;
         case 12 -> 0;
         case 13 -> 20;
         case 14 -> 63;
         case 15 -> 9;
         case 16 -> 44;
         case 17 -> 52;
         case 18 -> 61;
         case 19 -> 54;
         case 20 -> 4;
         case 21 -> 16;
         case 22 -> 22;
         case 23 -> 40;
         case 24 -> 58;
         case 25 -> 51;
         case 26 -> 45;
         case 27 -> 46;
         case 28 -> 25;
         case 29 -> 11;
         case 30 -> 38;
         case 31 -> 13;
         case 32 -> 26;
         case 33 -> 21;
         case 34 -> 36;
         case 35 -> 62;
         case 36 -> 57;
         case 37 -> 50;
         case 38 -> 6;
         case 39 -> 23;
         case 40 -> 41;
         case 41 -> 8;
         case 42 -> 30;
         case 43 -> 33;
         case 44 -> 18;
         case 45 -> 10;
         case 46 -> 49;
         case 47 -> 56;
         case 48 -> 12;
         case 49 -> 35;
         case 50 -> 53;
         case 51 -> 37;
         case 52 -> 31;
         case 53 -> 3;
         case 54 -> 55;
         case 55 -> 34;
         case 56 -> 27;
         case 57 -> 60;
         case 58 -> 28;
         case 59 -> 42;
         case 60 -> 43;
         case 61 -> 29;
         case 62 -> 19;
         default -> 48;
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

      c[0] = "f~[bLTp~^8_Cg5]>SWvrJ)\u0018C4";
      c[1] = ".f|QW .fk\r[/4-k\u0013[:3\\9M\u0003~";
      c[2] = boolean.class;
      d[2] = "java/lang/Boolean";
      c[3] = "\u007fOpP3\u0005\u007fOg\f?\ne\u0004g\u0012?\u001fbu5LgU";
      c[4] = "\t~\u0019-L\b\t~\u000eq@\u0007\u00135\u000eo@\u0012\u0014D_7\u0012";
      c[5] = double.class;
      d[5] = "java/lang/Double";
      c[6] = float.class;
      d[6] = "java/lang/Float";
      c[7] = void.class;
      d[7] = "java/lang/Void";
      c[8] = "\u0006\u00018\u001e~y\u0006\u0001/Brv\u001cJ/\\rc\u001b;\u007f\u0005 \"";
      c[9] = ")xR\u0011Cy?xWKPn(3TM\\z9tCZ\u0017h\u0005";
      c[10] = "\u000b`fMG\u001b~@mBVT\u0003X~E_\u001dk";
      c[11] = "HY\u007fk^pHYh7R\u007fR\u0012h)RjUc9v\n=EPj6@F\u0014\b;";
      c[12] = "i \u0001eg\u0002b/\u0010*\u0004\u000fw\"\u001fA1\rf1\u0003m&\u0000";
      c[13] = "\u007f/@&Hm\u007f/WzDbedWdDwb\u0015\u0005>\u00121";
      c[14] = "\u000e</z\u0010\u000b\u0010455X\u000b\n>-rQ\u0010J\r+~Z\u0017\u0007<-~";
      c[15] = "{E\u00103nm{E\u0007obba\u000e\u0007qbwf\u007fV.:";
      c[16] = "\u000250A\u0012x\t:!\u000esv\u00021%T";
      c[17] = "{$aOwL;ni\u0000\u0006O,\"p\u0018j}xf*F\u0006\u0016|5h\u0006>\u0010%#,\u007f<P0na\u0019|\u001a8!\u0010";
      c[18] = ")_\u0017\u0002\u0006\u000bq\\\u0013k\f_0Z\u0012\u0007>\tr\u0006HTi\u000e/DL\t\u0004Y#B\u0004kTRsK\u0012\u0011\u001b^uXu";
      c[19] = "\u0001U$\".'\u0007R\u007f0M*?\u0006x* 9EPt4(@";
      c[20] = "l\"\u0011e)E4!\u0015\f#\u0011u'\u0014`\u0011F2|J3F@4,\u000bu~Fm:O\f";
      c[21] = "\u0007-dN\u0003p\u0005('SfnY%r_19\u0003s-3YeJ5sC[`\t(";
      c[22] = ">0\u0014)RWf3\u0010@Tnk%\u0007*Q\u00142j\u0010y=^+$\u001c,G\u0007d3O@";
      c[23] = "^6DMDb\u00065@$N6G3AH|e\u0002o\u001e\u0015+f[lWCQ)WjD$";
      c[24] = "6#S\u0001\bAn Wh\u0002\u0015/&V\u00040Bmw\fXgAl(HU[\u00118vWh^\u0014,?O\u0013\u0018E4}1";
      c[25] = "\u0018hE\u0001o\r@kAheY\u0001m@\u0004W\tB<\u0016h9X\u0002tY\u0013\u007f\t\u001a6'";
      c[26] = "UN\f:\u0016'\rM\bS\u001csLK\t?.'\u0000\u0014_cy$JZ^\"\u001fd\u0000R\u0011S";
      c[27] = " LNiWYxOJ\u0000]\r9IKlo]u\u0010\u0010\u0000\u0004].QU8\u0002\u00048\u0015,<\u0005\u000b=P\u0014:\\\u001dy)\u0010=S\u0018<\u0011\u0016dE\\E\u0014L?I\u0007?[@9Z`";
      c[28] = "*\\\u0000\u0016\"kr_\u0004\u007f(?3Y\u0005\u0013\u001aiq\u0005_BMn,G[\u001d 9 A\u0013\u007f";
      c[29] = "EbQ,.\t\u001daUE$]\\gT)\u0016\n\u001b<\t|A\f\u001dlK<y\nDz\u000fE";
      c[30] = "dQ?\u001e\u0014><R;w\u0015flP1 F61\u000e]K\u0018y8V0\u001c\u0014\u007fp";
      c[31] = "\u000e\u001e\u0004->\u0007V\u001d\u0000D4S\u0017\u001b\u0001(\u0006\u0003TG^DhR\u0014\u0002\u0018?.\u0003\f@f";
      c[32] = "\u0012eXR\u0011?\u001e9_\u001bp\"Nx\u0007\r\u001c\u0010\u001f5V[OG\u001a;\t\u0013M{JoW\fpvQzZ\u0005L(\u0013m\u001cj\u00197Eh_\r\u000e&Ljg";
      c[33] = "\u0014q\u0006\n)B\u0013uU\u000bV\u001fDp\nW:-\u00101Q\fgz\u00151\u0001H/B\u0013h\u0017\fVF\u0014g\u0012In@MqV0jGBt\u0013\bl\u001eT0j";
      c[34] = "}\u001eG\u0013S`=TO\\\"h&\tROu?|Y\u000f#\u001e;,\u001cO\u001b\u0018b:X";
      c[35] = "m,z~,,-fr1]/:*k)1\u001dli1pbJkk`6$rm2vr]";
      c[36] = "&Q;'[7~R?NQc?T>\"c1\u007f\u0004iN]~%Xa)Jo,ZYsT12S#<X7!4";
      c[37] = "#\u0012MPRv{\u0011I9X\":\u0017HUjtxK\u0012\u0005=s%\t\u0016[P$)\u000f^9";
      c[38] = "\nwtI uRtp *!\u0013rqL\u0018wQ.+\u001eOp\fl/B\"'\u0000jg r,PcqZ= Vp\u0016";
      c[39] = "R#>~\u001f=\u0012i61n5\t4+\"9bSdwNRf\u0003!6vT?\u0015e";
      c[40] = "\u0001i{(\u001aUYj\u007fA\u0010\u0001\u0018l~-\"VZ='{uQ\u00043h&\u000f\u001e\b5{A";
      c[41] = "53/TT-uy'\u001b%.b5>\u0003I\u001c6qdU%qu8n\u0015C1?0!d";
      c[42] = "*\u0015KEa\nr\u0016O,k^3\u0010N@Y\n\u007fO\u0018\u001d\u000e\t5\u0001\u0019]hI\u007f\tV,3Sp\u0001NV|_v\u0012)";
      c[43] = "\u001eQNd)\u0000FRJ\r#T\u0007TKa\u0011\u0000K\u0004\u00145F\u0005F_Tt~\u0003\u001fI\u0010\rz\u0004\u0010LU5|]\u0006\b,1{R\u0003M\u00147\"DG4\u0011myH\u001cN^a\u007f[{";
      c[44] = "\u0001C\u00025\u0005$Y@\u0006\\\u0004|\tB\f\u000bT%\\\u001a``\tc]D\r7\u0005e\u0015";
      c[45] = "_T5;<)\u0000\u0006%?V5\u0004T=:\u0001e\\\u0003cVl6\tT3$3d\u0019P";
      c[46] = "\u001fQ(:MP\b@!8uR\u001b].1\u0019`O\u001eqfN7ID<+\u0010GKA\u007f6u";
      c[47] = "\u0002N\u000fb3\u0013B\u0004\u0007-B\u001bYY\u001a>\u0015L\u0003\tDR~HSL\u0007jx\u0011E\b";
      c[48] = "\u001d\r\u001cG\u001aTE\u000e\u0018.\u0010\u0000\u0004\b\u0019B\"WCSG\u001euQE\u0003\u0006WMW\u001c\u0015B.";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_7454(class_3966 var1) {
try {

      this.a = true;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_24920(class_3965 var1) {
try {

      this.a = true;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_1799 method_7445() {
try {

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_7485(double var1, double var3, double var5, float var7, float var8) {
try {

      long var9 = b ^ 3195591405147L;
      class_243 var11 = __indy$4(__indy$3(new class_243(var1, var3, var5), -3134401153956918195L, var9), var7, -3130471905651489102L, var9);
      __indy$5(this, var11, -3134336740970772249L, var9);
      __indy$9(
         this,
         (float)(
            __indy$8(__indy$6(var11, -3133950155662612720L, var9), __indy$7(var11, -3133400042395233180L, var9), -3133814930621357707L, var9)
               * 180.0F / (float)Math.PI
         ),
         -3133496025398545856L,
         var9
      );
      __indy$13(
         this,
         (float)(
            __indy$12(__indy$10(var11, -3133751776331956931L, var9), __indy$11(var11, -3133668087416082987L, var9), -3133814930621357707L, var9)
               * 180.0F / (float)Math.PI
         ),
         -3130417524363028908L,
         var9
      );
      __indy$15(this, __indy$14(this, -3133522772230376470L, var9), -3130680269972985825L, var9);
      __indy$17(this, __indy$16(this, -3130809695193305469L, var9), -3134195868950352596L, var9);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public class_1799 method_57314() {
try {

      return null;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void method_5773() {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(14,11);
f.locals[0]=this;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$20$0(f);break;
case 1:__flow$20$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=17&&f.pc<30&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=32;continue;}
if(f.pc>=21&&f.pc<45&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=47;continue;}
if(f.pc>=104&&f.pc<119&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=119;continue;}
if(f.pc>=131&&f.pc<152&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=154;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static boolean lambda$tick$0(S0431 var0, class_1297 var1) {
try {

      long var2 = b ^ 29886919141087L;
      return __indy$47(var0, var1, 2019905698813651966L, var2);
   
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

   private static native class_1299 __indy$0(long var0, long var2) throws Throwable;

   private static native MethodHandle __indy$1() throws Throwable;

   private static native Object __indy$2(MethodHandle var0, Object[] var1) throws Throwable;

   private static native class_243 __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$4(Object var0, double var1, long var3, long var5) throws Throwable;

   private static native void __indy$5(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native double __indy$6(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$8(double var0, double var2, long var4, long var6) throws Throwable;

   private static native void __indy$9(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native double __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$12(double var0, double var2, long var4, long var6) throws Throwable;

   private static native void __indy$13(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native float __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$15(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native float __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$17(Object var0, float var1, long var2, long var4) throws Throwable;

   private static native String[] __indy$18(long var0, long var2) throws Throwable;

   private static native Predicate __indy$19(S0431 var0) throws Throwable;

   private static native class_239 __indy$20(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_240 __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native class_240 __indy$22(long var0, long var2) throws Throwable;

   private static native MatchException __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$24(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$32(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$33(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$34(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$35(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$37(Object var0, double var1, long var3, long var5) throws Throwable;

   private static native void __indy$38(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$40(Object var0, long var1, long var3) throws Throwable;

   private static native class_243 __indy$41(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$42(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$43(Object var0, long var1, long var3) throws Throwable;

   private static native double __indy$44(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$45(Object var0, double var1, double var3, double var5, long var7, long var9) throws Throwable;

   private static native void __indy$46(Object var0, double var1, double var3, double var5, long var7, long var9) throws Throwable;

   private static native boolean __indy$47(Object var0, Object var1, long var2, long var4) throws Throwable;

private void __flow$20$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0431.b,2);f.pc=1;return;}
case 1:{f.push(70590289818558L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[1]=f.pop();f.pc=4;return;}
case 4:{f.push(-3932839482694258404L,2);f.pc=5;return;}
case 5:{f.push(f.locals[1],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$18(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.push(f.locals[0],1);f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$19(((dev.zprestige.sourceview.S0431)(Object)(a0))),1);f.pc=12;return;}
case 12:{f.push(-3934063475325713152L,2);f.pc=13;return;}
case 13:{f.push(f.locals[1],2);f.pc=14;return;}
case 14:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$20(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=15;return;}
case 15:{f.locals[4]=f.pop();f.pc=16;return;}
case 16:{f.locals[3]=f.pop();f.pc=17;return;}
case 17:{f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.push(f.locals[3],1);f.pc=20;return;}
case 20:{if(f.pop()!=null){f.pc=60;return;}f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(f.locals[4],1);f.pc=23;return;}
case 23:{f.push(-3934803215182243444L,2);f.pc=24;return;}
case 24:{f.push(f.locals[1],2);f.pc=25;return;}
case 25:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=26;return;}
case 26:{f.push(-3933323448528282454L,2);f.pc=27;return;}
case 27:{f.push(f.locals[1],2);f.pc=28;return;}
case 28:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$22(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=29;return;}
case 29:{Object b=f.pop(),a=f.pop();if(a==b){f.pc=53;return;}f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=38;return;}
case 32:{f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.push(-3933076471141736213L,2);f.pc=35;return;}
case 35:{f.push(f.locals[1],2);f.pc=36;return;}
case 36:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=37;return;}
case 37:{throw (Throwable)f.pop();}
case 38:{f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.push(f.locals[0],1);f.pc=41;return;}
case 41:{f.push(f.locals[4],1);f.pc=42;return;}
case 42:{f.push(-3934336080768280985L,2);f.pc=43;return;}
case 43:{f.push(f.locals[1],2);f.pc=44;return;}
case 44:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0431.__indy$24(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.pc=53;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(-3933076471141736213L,2);f.pc=50;return;}
case 50:{f.push(f.locals[1],2);f.pc=51;return;}
case 51:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=52;return;}
case 52:{throw (Throwable)f.pop();}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.push(f.locals[0],1);f.pc=57;return;}
case 57:{f.push(-3933143035840960800L,2);f.pc=58;return;}
case 58:{f.push(f.locals[1],2);f.pc=59;return;}
case 59:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0431.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.push(f.locals[0],1);f.pc=64;return;}
case 64:{f.push(-3933358872271417637L,2);f.pc=65;return;}
case 65:{f.push(f.locals[1],2);f.pc=66;return;}
case 66:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=67;return;}
case 67:{f.locals[5]=f.pop();f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.pc=70;return;}
case 70:{f.push(f.locals[0],1);f.pc=71;return;}
case 71:{f.push(-3933198385966090137L,2);f.pc=72;return;}
case 72:{f.push(f.locals[1],2);f.pc=73;return;}
case 73:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=74;return;}
case 74:{f.push(f.locals[5],1);f.pc=75;return;}
case 75:{f.push(-3934815478173517067L,2);f.pc=76;return;}
case 76:{f.push(f.locals[1],2);f.pc=77;return;}
case 77:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$29(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=78;return;}
case 78:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=79;return;}
case 79:{f.locals[6]=f.pop();f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.pc=82;return;}
case 82:{f.push(f.locals[0],1);f.pc=83;return;}
case 83:{f.push(-3935205197645443301L,2);f.pc=84;return;}
case 84:{f.push(f.locals[1],2);f.pc=85;return;}
case 85:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$30(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=86;return;}
case 86:{f.push(f.locals[5],1);f.pc=87;return;}
case 87:{f.push(-3933912385306566440L,2);f.pc=88;return;}
case 88:{f.push(f.locals[1],2);f.pc=89;return;}
case 89:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$31(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=90;return;}
case 90:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=91;return;}
case 91:{f.locals[8]=f.pop();f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.pc=94;return;}
case 94:{f.push(f.locals[0],1);f.pc=95;return;}
case 95:{f.push(-3933866203041716825L,2);f.pc=96;return;}
case 96:{f.push(f.locals[1],2);f.pc=97;return;}
case 97:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$32(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=98;return;}
case 98:{f.push(f.locals[5],1);f.pc=99;return;}
case 99:{f.push(-3934264153586824831L,2);f.pc=100;return;}
case 100:{f.push(f.locals[1],2);f.pc=101;return;}
case 101:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$33(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=102;return;}
case 102:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a+b,2);f.pc=103;return;}
case 103:{f.locals[10]=f.pop();f.pc=104;return;}
case 104:{f.pc=105;return;}
case 105:{f.pc=106;return;}
case 106:{f.push(f.locals[0],1);f.pc=107;return;}
case 107:{f.push(-3934633289403610364L,2);f.pc=108;return;}
case 108:{f.push(f.locals[1],2);f.pc=109;return;}
case 109:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0431.__indy$34(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=110;return;}
case 110:{f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.push(f.locals[0],1);f.pc=113;return;}
case 113:{f.push(-3933611572779657699L,2);f.pc=114;return;}
case 114:{f.push(f.locals[1],2);f.pc=115;return;}
case 115:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$35(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=116;return;}
case 116:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=125;return;}f.pc=117;return;}
case 117:{f.push(Float.intBitsToFloat(1061997773),1);f.pc=118;return;}
case 118:{f.pc=128;return;}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$20$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.push(-3933076471141736213L,2);f.pc=122;return;}
case 122:{f.push(f.locals[1],2);f.pc=123;return;}
case 123:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$36(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=124;return;}
case 124:{throw (Throwable)f.pop();}
case 125:{f.pc=126;return;}
case 126:{f.pc=127;return;}
case 127:{f.push(Float.intBitsToFloat(1065185444),1);f.pc=128;return;}
case 128:{f.pc=129;return;}
case 129:{f.pc=130;return;}
case 130:{f.locals[12]=f.pop();f.pc=131;return;}
case 131:{f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.push(f.locals[0],1);f.pc=134;return;}
case 134:{f.push(f.locals[5],1);f.pc=135;return;}
case 135:{f.push(f.locals[12],1);f.pc=136;return;}
case 136:{f.push((double)(dev.zprestige.sourceview.FlowFrame.f(f.pop())),2);f.pc=137;return;}
case 137:{f.push(-3933024866399380649L,2);f.pc=138;return;}
case 138:{f.push(f.locals[1],2);f.pc=139;return;}
case 139:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$37(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=140;return;}
case 140:{f.push(-3934497302029642494L,2);f.pc=141;return;}
case 141:{f.push(f.locals[1],2);f.pc=142;return;}
case 142:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0431.__indy$38(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=143;return;}
case 143:{f.pc=144;return;}
case 144:{f.pc=145;return;}
case 145:{f.push(f.locals[0],1);f.pc=146;return;}
case 146:{f.push(f.locals[3],1);f.pc=147;return;}
case 147:{if(f.pop()!=null){f.pc=192;return;}f.pc=148;return;}
case 148:{f.push(-3933753504291375564L,2);f.pc=149;return;}
case 149:{f.push(f.locals[1],2);f.pc=150;return;}
case 150:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$39(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=151;return;}
case 151:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=188;return;}f.pc=152;return;}
case 152:{f.pc=153;return;}
case 153:{f.pc=160;return;}
case 154:{f.pc=155;return;}
case 155:{f.pc=156;return;}
case 156:{f.push(-3933076471141736213L,2);f.pc=157;return;}
case 157:{f.push(f.locals[1],2);f.pc=158;return;}
case 158:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$40(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=159;return;}
case 159:{throw (Throwable)f.pop();}
case 160:{f.pc=161;return;}
case 161:{f.pc=162;return;}
case 162:{f.pc=163;return;}
case 163:{f.push(f.locals[0],1);f.pc=164;return;}
case 164:{f.push(-3933358872271417637L,2);f.pc=165;return;}
case 165:{f.push(f.locals[1],2);f.pc=166;return;}
case 166:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$41(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=167;return;}
case 167:{f.locals[13]=f.pop();f.pc=168;return;}
case 168:{f.pc=169;return;}
case 169:{f.pc=170;return;}
case 170:{f.push(f.locals[0],1);f.pc=171;return;}
case 171:{f.push(f.locals[13],1);f.pc=172;return;}
case 172:{f.push(-3934815478173517067L,2);f.pc=173;return;}
case 173:{f.push(f.locals[1],2);f.pc=174;return;}
case 174:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$42(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=175;return;}
case 175:{f.push(f.locals[13],1);f.pc=176;return;}
case 176:{f.push(-3933912385306566440L,2);f.pc=177;return;}
case 177:{f.push(f.locals[1],2);f.pc=178;return;}
case 178:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$43(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=179;return;}
case 179:{f.push(Double.longBitsToDouble(4584304132692975288L),2);f.pc=180;return;}
case 180:{double b=dev.zprestige.sourceview.FlowFrame.d(f.pop()),a=dev.zprestige.sourceview.FlowFrame.d(f.pop());f.push(a-b,2);f.pc=181;return;}
case 181:{f.push(f.locals[13],1);f.pc=182;return;}
case 182:{f.push(-3934264153586824831L,2);f.pc=183;return;}
case 183:{f.push(f.locals[1],2);f.pc=184;return;}
case 184:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0431.__indy$44(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),2);f.pc=185;return;}
case 185:{f.push(-3934562054052760268L,2);f.pc=186;return;}
case 186:{f.push(f.locals[1],2);f.pc=187;return;}
case 187:{Object a5=f.pop();Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0431.__indy$45(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2),dev.zprestige.sourceview.FlowFrame.d(a3),dev.zprestige.sourceview.FlowFrame.j(a4),dev.zprestige.sourceview.FlowFrame.j(a5));f.pc=188;return;}
case 188:{f.pc=189;return;}
case 189:{f.pc=190;return;}
case 190:{f.pc=191;return;}
case 191:{f.push(f.locals[0],1);f.pc=192;return;}
case 192:{f.pc=193;return;}
case 193:{f.pc=194;return;}
case 194:{f.push(f.locals[6],2);f.pc=195;return;}
case 195:{f.push(f.locals[8],2);f.pc=196;return;}
case 196:{f.push(f.locals[10],2);f.pc=197;return;}
case 197:{f.push(-3933420359841796884L,2);f.pc=198;return;}
case 198:{f.push(f.locals[1],2);f.pc=199;return;}
case 199:{Object a5=f.pop();Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0431.__indy$46(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.d(a1),dev.zprestige.sourceview.FlowFrame.d(a2),dev.zprestige.sourceview.FlowFrame.d(a3),dev.zprestige.sourceview.FlowFrame.j(a4),dev.zprestige.sourceview.FlowFrame.j(a5));f.pc=200;return;}
case 200:{f.pc=201;return;}
case 201:{f.pc=202;return;}
case 202:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(3135032185919111743L,2);f.pc=1;return;}
case 1:{f.push(-6462406868818591335L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(53352406182906L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0431.class,"b",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(49,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0431.class,"c",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(49,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0431.class,"d",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0431.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
