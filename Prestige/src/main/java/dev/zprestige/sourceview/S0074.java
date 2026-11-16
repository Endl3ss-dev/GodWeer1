package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0034;
import dev.zprestige.sourceview.S0047;
import dev.zprestige.sourceview.S0048;
import dev.zprestige.sourceview.S0109;
import dev.zprestige.sourceview.S0117;
import dev.zprestige.sourceview.S0118;
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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_746;

public class S0074 implements S0225 {
   public Set a;
   public Map c;
   public static long d;
   public static Object[] e;
   public static String[] f;

   public S0074(long var1)  throws Throwable {
      var1 = d ^ var1;
      long var3 = var1 ^ 106554379797719L;
      super();
try {

      this.a = new HashSet();
      this.c = new IdentityHashMap();
      __indy$1(__indy$0(-3615170396352804554L, var1), new Object[]{this, var3}, -3615090210145189726L, var1);
   
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
         case 0 -> 12;
         case 1 -> 19;
         case 2 -> 47;
         case 3 -> 52;
         case 4 -> 34;
         case 5 -> 15;
         case 6 -> 46;
         case 7 -> 33;
         case 8 -> 18;
         case 9 -> 62;
         case 10 -> 30;
         case 11 -> 44;
         case 12 -> 26;
         case 13 -> 17;
         case 14 -> 59;
         case 15 -> 42;
         case 16 -> 1;
         case 17 -> 27;
         case 18 -> 61;
         case 19 -> 11;
         case 20 -> 56;
         case 21 -> 6;
         case 22 -> 5;
         case 23 -> 53;
         case 24 -> 43;
         case 25 -> 60;
         case 26 -> 48;
         case 27 -> 25;
         case 28 -> 35;
         case 29 -> 9;
         case 30 -> 36;
         case 31 -> 55;
         case 32 -> 13;
         case 33 -> 49;
         case 34 -> 40;
         case 35 -> 38;
         case 36 -> 24;
         case 37 -> 50;
         case 38 -> 20;
         case 39 -> 29;
         case 40 -> 39;
         case 41 -> 7;
         case 42 -> 21;
         case 43 -> 58;
         case 44 -> 51;
         case 45 -> 3;
         case 46 -> 4;
         case 47 -> 14;
         case 48 -> 2;
         case 49 -> 63;
         case 50 -> 54;
         case 51 -> 28;
         case 52 -> 32;
         case 53 -> 37;
         case 54 -> 57;
         case 55 -> 10;
         case 56 -> 0;
         case 57 -> 45;
         case 58 -> 31;
         case 59 -> 23;
         case 60 -> 22;
         case 61 -> 16;
         case 62 -> 41;
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

      f[var4] = new String(var13);
      return var4;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public boolean b$return$e4b089a0(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(10,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$3$0(f);break;
case 1:__flow$3$1(f);break;
case 2:__flow$3$2(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=31&&f.pc<39&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=41;continue;}
if(f.pc>=37&&f.pc<52&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=52;continue;}
if(f.pc>=83&&f.pc<89&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=91;continue;}
if(f.pc>=87&&f.pc<102&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=102;continue;}
if(f.pc>=127&&f.pc<133&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=135;continue;}
if(f.pc>=131&&f.pc<146&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=146;continue;}
if(f.pc>=177&&f.pc<183&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=185;continue;}
if(f.pc>=181&&f.pc<215&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=217;continue;}
if(f.pc>=223&&f.pc<230&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=230;continue;}
if(f.pc>=245&&f.pc<251&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=253;continue;}
if(f.pc>=249&&f.pc<265&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=267;continue;}
if(f.pc>=273&&f.pc<278&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=280;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
}



   public static MethodHandle b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'g' && var8 != 'J' && var8 != 193 && var8 != 216) {
            var11 = h(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 'w') {
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
            if (var8 == 'g') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'J') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 193) {
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

   public static Object b(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = b(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$23(var8.asSpreader(Object[].class, var4.length), var4);
   
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
            var23 = f(799909227904672L, 0L);
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
            var23 = f(799909227904672L, 0L);
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

   public static CallSite a(Lookup var0, String var1, MethodType var2) {
try {

      MutableCallSite var3 = new MutableCallSite(var2);

      try {
         var3.setTarget(
            MethodHandles.explicitCastArguments(
               MethodHandles.insertArguments(__indy$24().asCollector(Object[].class, var2.parameterCount()), 0, var0, var3, var1, var2), var2
            )
         );
         return var3;
      } catch (Exception var5) {
         throw new RuntimeException("dev/zprestige/prestige/ai" + " : " + var1 + " : " + var2.toString(), var5);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public boolean a$return$e4b089a0(Object[] param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(10,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$13$0(f);break;
case 1:__flow$13$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=27&&f.pc<38&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=40;continue;}
if(f.pc>=32&&f.pc<51&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=51;continue;}
if(f.pc>=70&&f.pc<76&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=78;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
return (dev.zprestige.sourceview.FlowFrame.i(f.result) != 0);
}



   public Set a$return$93f6648b(Object[] var1) {
try {

      return this.a;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public void a$return$e4b0899c(Object[] var1) {
try {

      class_1657 var2 = (class_1657)(Object)(var1[0]);
      long var3 = (Long)(Object)(var1[1]);
      var3 = d ^ var3;
      __indy$35(this.a, var2, -2410207097957504231L, var3);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0034 var1) {
try {

      long var2 = d ^ 28079711322096L;
      __indy$36(this.a, 4201523121802065363L, var2);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @S0118
   public void a(S0109 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(5,6);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$17$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=15&&f.pc<26&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=28;continue;}
if(f.pc>=24&&f.pc<43&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=45;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static void a$return$9b75() {
try {

      e[0] = "bv\u0018bL@tv\u001d8_Wc=\u001e>SCrz\t)\u0018QN";
      e[1] = "\u0017\u0003\u0010=(/b#\u001b29`\u001f;\b50)w";
      e[2] = "x\u0019xrc8n\u0019}(p/yR~.|;h\u0015i97)u";
      e[3] = "d\r}b\u0016Io\u0002l-uDz\u000fcF@Fk\u001c\u007fjWK";
      e[4] = "e\u0017L)\b\u0004{\u001fVfk\u0010\u007f";
      e[5] = void.class;
      f[5] = "java/lang/Void";
      e[6] = "&h\u00076rR8`\u001dy\u000fB8";
      e[7] = boolean.class;
      f[7] = "java/lang/Boolean";
      e[8] = "\b\u0004X'\u0015J\u0016\fBh]J\f\u0006Z/TQL5\\#_V\u0001\u0004Z#";
      e[9] = "[paQqy[pv\r}vA;v\u0013}cFJ&N,";
      e[10] = "yo6SW(yo!\u000f['c$!\u0011[2dUuI\f";
      e[11] = "!p\u0010\u0000v\u0017*\u007f\u0001O\u0017\u0019!t\u0005\u0015";
      e[12] = "WB-\u0000l-\\M<O\u0000.RO>\u0000,";
      e[13] = "(\n#\u0015=S]*(\u001a,\u001c<$#\u0011(FH";
      e[14] = "\n\u0007d67\u0014\u001c\u0007al$\u0003\u000bLbj(\u0017\u001a\u000bu}c\u0007\u0002\u000bwv9J>\u0010wk9\r\t\u0007";
      e[15] = "+\u0006\t\u0010(]=\u0006\fJ;J*M\u000fL7^;\n\u0018[|O\u0000";
      e[16] = ";DwZ%tNd|U4;/jw^0a[";
      e[17] = "\trcPe2|Rh_t}\u001d\\cTp'i";
      e[18] = "gI\u0005(gWgI\u0012tkX}\u0002\u0012jkMzs@43\t";
      e[19] = "k\u0004\t-+?\u001e$\u0002\":p\u007f*\t)>*\u000b";
      e[20] = "\u0003\u000bQ\u001a<M\u0011\u0016\u0013s,4\u0010E^\n<M\rL\u001fs";
      e[21] = "Y&;\u007fc\u0018\u0000w,zYM0q(&7L\tf:d'";
      e[22] = "cq2\u0006entcp\u0016\u000e>\nsv\u001ac%2{f\u001a\u007fW1mp\u0005`ixv2\u0012\u000e";
      e[23] = "UB[*M\u001bZ\u0006Rc1\u001a\nT\u000e4fMT\u0003VX\u000eH[I\u0005`[\u0018\tE";
      e[24] = "t\u001f\u0013FXSf\u0002Q/H*u\u0018^BP\u0012}\b^^\"\u0017p\u0019^^NL|_\u001d/";
      e[25] = "IF\u001dyyU\u001dV\u001eh\u001a\rp\r\u001bh$\u0000\fJ\u0015~|dJS\r$~\u0018\r]\u001b|\u001a";
      e[26] = "SE:r_$DWxb4w:G~nYo\u0002OnnE\u001d\u0007B\u007fnEq\\N9-4";
      e[27] = "i=\u001aWWYj:\n]k\r7,\n\\k]!>\u000eHU\u0014:|\u0019&";
      e[28] = "Re?hyg\u0002<g`\u0015z\u0015J8}ijn:4}gj\u0002a8;$\u001b";
      e[29] = ",\u0013\u00158t\"/\u0014\u00052Hek\u0013lv6\u007fr_Qxp,moS7*y&R]qyf\u0016P\u0012+,-+^Tx3\u001d";
      e[30] = "M\u0001+\u0002^rN\u0006;\bb\"\u001a\u0001RL\u001c/\u0013MoBZ|\f}m\r\u0000)G@cKS6w";
      e[31] = "\u001eCR\b~~EP\u0019C\u0017sIVG\u0015~\u007fpXG\u0005z\u0019\u0013X[\nfuHT\u001dI\u0017";
      e[32] = "lsk\u001d\u0011Nr+`H!Am7e\u001eMs={=D!\u0019i3w\bMBeu4y";
      e[33] = "v\u000b\u00120t}\"\u001b\u0011!\u0017&O\u0016\t!z>w\u001e\u0019!fLr\u0013\b!f )\u001fNb\u0017";
      e[34] = "NX\u0013\u0017\u0001/\u001e\u0001K\u001fm8\u0016W\u0018\u0000mh\u0000E\u001c\u0014S!\u001b\u0007\u000bz";
      e[35] = "\u000f8\u0010B~\u001b_aHJ\u0012\fT<\u000eFs\u0001HZMQp\u0003\u0003gC\u0017#\u001c3g\u001bW`\u0016_<\u0017\u0011#g";
      e[36] = "p \"<Y\u001b yz45\u000e .@nK\u0005(r}`\rV7B}8M\u0015=.&4\u000bVL";
      e[37] = "K#H:s<\u001bz\u00102\u001f:\u001a$M)r\u0001\u0019A\u0017/by\u0018/Z2pyw|C/m1\u001b'Oi.@";
      e[38] = "F\b`\tN&\u001d\u001b+B'?\u001f\u001el\u0014`/vGx\u0001U0\u001a\u001ctG\u0016AF\b`\tN&\u001d\u001b+B'";
   
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
            var12 = f(799909227904672L, 0L);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static boolean lambda$onTick$0(class_1297 var0) {
try {

      long var1 = d ^ 54266417211906L;
      String[] var3 = __indy$44(-5786730918348959012L, var1);

      try {
         boolean var10000 = __indy$45(var0, -5783661319345229429L, var1);
         if (var3 != null) {
            return var10000;
         }

         if (!var10000) {
            return true;
         }
      } catch (MatchException var4) {
         throw __indy$46(var4, -5786514812942094045L, var1);
      }

      return false;
   
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

   private static native S0117 __indy$5(long var0, long var2) throws Throwable;

   private static native boolean __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$8(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$9(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$11(Object var0, long var1, long var3) throws Throwable;

   private static native S0117 __indy$12(long var0, long var2) throws Throwable;

   private static native boolean __indy$13(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$15(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$18(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$22(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$23(MethodHandle var0, Object[] var1) throws Throwable;

   private static native MethodHandle __indy$24() throws Throwable;

   private static native String[] __indy$25(long var0, long var2) throws Throwable;

   private static native class_746 __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$27(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$29(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$30(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$32(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native Boolean __indy$33(boolean var0, long var1, long var3) throws Throwable;

   private static native Object __indy$34(Object var0, Object var1, Object var2, long var3, long var5) throws Throwable;

   private static native boolean __indy$35(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native void __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$37(long var0, long var2) throws Throwable;

   private static native void __indy$38(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$40(Object var0, long var1, long var3) throws Throwable;

   private static native Predicate __indy$41() throws Throwable;

   private static native boolean __indy$42(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$43(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$44(long var0, long var2) throws Throwable;

   private static native boolean __indy$45(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$46(Object var0, long var1, long var3) throws Throwable;











private void __flow$3$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((net.minecraft.class_1297)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[4]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[2]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0074.d,2);f.pc=14;return;}
case 14:{f.push(f.locals[2],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[2]=f.pop();f.pc=17;return;}
case 17:{f.push(f.locals[2],2);f.pc=18;return;}
case 18:{f.rearrange(92);f.pc=19;return;}
case 19:{f.push(99671251868934L,2);f.pc=20;return;}
case 20:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=21;return;}
case 21:{f.locals[5]=f.pop();f.pc=22;return;}
case 22:{f.rearrange(92);f.pc=23;return;}
case 23:{f.push(86195271010853L,2);f.pc=24;return;}
case 24:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=25;return;}
case 25:{f.locals[7]=f.pop();f.pc=26;return;}
case 26:{f.rearrange(88);f.pc=27;return;}
case 27:{f.push(6452278781204538598L,2);f.pc=28;return;}
case 28:{f.push(f.locals[2],2);f.pc=29;return;}
case 29:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=30;return;}
case 30:{f.locals[9]=f.pop();f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{f.push(f.locals[4],1);f.pc=34;return;}
case 34:{f.push((f.pop() instanceof net.minecraft.class_1657?1:0),1);f.pc=35;return;}
case 35:{f.push(f.locals[9],1);f.pc=36;return;}
case 36:{if(f.pop()!=null){f.pc=83;return;}f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=58;return;}f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.pc=47;return;}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.push(6451499725508708121L,2);f.pc=44;return;}
case 44:{f.push(f.locals[2],2);f.pc=45;return;}
case 45:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=46;return;}
case 46:{throw (Throwable)f.pop();}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.pc=50;return;}
case 50:{f.push(1,1);f.pc=51;return;}
case 51:{f.result=f.pop();f.done=true;return;}
case 52:{f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.push(6451499725508708121L,2);f.pc=55;return;}
case 55:{f.push(f.locals[2],2);f.pc=56;return;}
case 56:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=57;return;}
case 57:{throw (Throwable)f.pop();}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.push(6452363366599466345L,2);f.pc=62;return;}
case 62:{f.push(f.locals[2],2);f.pc=63;return;}
case 63:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$5(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=64;return;}
case 64:{f.push(dev.zprestige.sourceview.S0048.class,1);f.pc=65;return;}
case 65:{f.push(f.locals[7],2);f.pc=66;return;}
case 66:{f.push(2,1);f.pc=67;return;}
case 67:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=68;return;}
case 68:{f.rearrange(91);f.pc=69;return;}
case 69:{f.rearrange(91);f.pc=70;return;}
case 70:{f.rearrange(87);f.pc=71;return;}
case 71:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=72;return;}
case 72:{f.push(1,1);f.pc=73;return;}
case 73:{f.rearrange(95);f.pc=74;return;}
case 74:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=75;return;}
case 75:{f.rearrange(90);f.pc=76;return;}
case 76:{f.rearrange(95);f.pc=77;return;}
case 77:{f.push(0,1);f.pc=78;return;}
case 78:{f.rearrange(95);f.pc=79;return;}
case 79:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=80;return;}
case 80:{f.push(6451572507999447090L,2);f.pc=81;return;}
case 81:{f.push(f.locals[2],2);f.pc=82;return;}
case 82:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$6(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=83;return;}
case 83:{f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.push(f.locals[9],1);f.pc=86;return;}
case 86:{if(f.pop()!=null){f.pc=127;return;}f.pc=87;return;}
case 87:{f.pc=88;return;}
case 88:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=108;return;}f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=97;return;}
case 91:{f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.push(6451499725508708121L,2);f.pc=94;return;}
case 94:{f.push(f.locals[2],2);f.pc=95;return;}
case 95:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=96;return;}
case 96:{throw (Throwable)f.pop();}
case 97:{f.pc=98;return;}
case 98:{f.pc=99;return;}
case 99:{f.pc=100;return;}
case 100:{f.push(1,1);f.pc=101;return;}
case 101:{f.result=f.pop();f.done=true;return;}
case 102:{f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{f.push(6451499725508708121L,2);f.pc=105;return;}
case 105:{f.push(f.locals[2],2);f.pc=106;return;}
case 106:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=107;return;}
case 107:{throw (Throwable)f.pop();}
case 108:{f.pc=109;return;}
case 109:{f.pc=110;return;}
case 110:{f.pc=111;return;}
case 111:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0048.class),1);f.pc=112;return;}
case 112:{f.rearrange(89);f.pc=113;return;}
case 113:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=114;return;}
case 114:{f.push(f.locals[5],2);f.pc=115;return;}
case 115:{f.push(1,1);f.pc=116;return;}
case 116:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=117;return;}
case 117:{f.rearrange(91);f.pc=118;return;}
case 118:{f.rearrange(91);f.pc=119;return;}
case 119:{f.rearrange(87);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$3$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=121;return;}
case 121:{f.push(0,1);f.pc=122;return;}
case 122:{f.rearrange(95);f.pc=123;return;}
case 123:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=124;return;}
case 124:{f.push(6451442512750495039L,2);f.pc=125;return;}
case 125:{f.push(f.locals[2],2);f.pc=126;return;}
case 126:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$9(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=127;return;}
case 127:{f.pc=128;return;}
case 128:{f.pc=129;return;}
case 129:{f.push(f.locals[9],1);f.pc=130;return;}
case 130:{if(f.pop()!=null){f.pc=177;return;}f.pc=131;return;}
case 131:{f.pc=132;return;}
case 132:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=152;return;}f.pc=133;return;}
case 133:{f.pc=134;return;}
case 134:{f.pc=141;return;}
case 135:{f.pc=136;return;}
case 136:{f.pc=137;return;}
case 137:{f.push(6451499725508708121L,2);f.pc=138;return;}
case 138:{f.push(f.locals[2],2);f.pc=139;return;}
case 139:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=140;return;}
case 140:{throw (Throwable)f.pop();}
case 141:{f.pc=142;return;}
case 142:{f.pc=143;return;}
case 143:{f.pc=144;return;}
case 144:{f.push(1,1);f.pc=145;return;}
case 145:{f.result=f.pop();f.done=true;return;}
case 146:{f.pc=147;return;}
case 147:{f.pc=148;return;}
case 148:{f.push(6451499725508708121L,2);f.pc=149;return;}
case 149:{f.push(f.locals[2],2);f.pc=150;return;}
case 150:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$11(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=151;return;}
case 151:{throw (Throwable)f.pop();}
case 152:{f.pc=153;return;}
case 153:{f.pc=154;return;}
case 154:{f.pc=155;return;}
case 155:{f.push(6452363366599466345L,2);f.pc=156;return;}
case 156:{f.push(f.locals[2],2);f.pc=157;return;}
case 157:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$12(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=158;return;}
case 158:{f.push(dev.zprestige.sourceview.S0047.class,1);f.pc=159;return;}
case 159:{f.push(f.locals[7],2);f.pc=160;return;}
case 160:{f.push(2,1);f.pc=161;return;}
case 161:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=162;return;}
case 162:{f.rearrange(91);f.pc=163;return;}
case 163:{f.rearrange(91);f.pc=164;return;}
case 164:{f.rearrange(87);f.pc=165;return;}
case 165:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=166;return;}
case 166:{f.push(1,1);f.pc=167;return;}
case 167:{f.rearrange(95);f.pc=168;return;}
case 168:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=169;return;}
case 169:{f.rearrange(90);f.pc=170;return;}
case 170:{f.rearrange(95);f.pc=171;return;}
case 171:{f.push(0,1);f.pc=172;return;}
case 172:{f.rearrange(95);f.pc=173;return;}
case 173:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=174;return;}
case 174:{f.push(6451572507999447090L,2);f.pc=175;return;}
case 175:{f.push(f.locals[2],2);f.pc=176;return;}
case 176:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$13(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=177;return;}
case 177:{f.pc=178;return;}
case 178:{f.pc=179;return;}
case 179:{f.push(f.locals[9],1);f.pc=180;return;}
case 180:{if(f.pop()!=null){f.pc=245;return;}f.pc=181;return;}
case 181:{f.pc=182;return;}
case 182:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=236;return;}f.pc=183;return;}
case 183:{f.pc=184;return;}
case 184:{f.pc=191;return;}
case 185:{f.pc=186;return;}
case 186:{f.pc=187;return;}
case 187:{f.push(6451499725508708121L,2);f.pc=188;return;}
case 188:{f.push(f.locals[2],2);f.pc=189;return;}
case 189:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=190;return;}
case 190:{throw (Throwable)f.pop();}
case 191:{f.pc=192;return;}
case 192:{f.pc=193;return;}
case 193:{f.push(dev.zprestige.sourceview.FlowFrame.pending(dev.zprestige.sourceview.S0047.class),1);f.pc=194;return;}
case 194:{f.rearrange(89);f.pc=195;return;}
case 195:{f.push(f.locals[4],1);f.pc=196;return;}
case 196:{f.push((net.minecraft.class_1657)(Object)f.pop(),1);f.pc=197;return;}
case 197:{Object a0=f.pop();Object receiver=f.pop();f.construct(receiver,new Class<?>[]{net.minecraft.class_1657.class},new Object[]{((net.minecraft.class_1657)(Object)(a0))});f.pc=198;return;}
case 198:{f.pc=199;return;}
case 199:{f.pc=200;return;}
case 200:{f.push(f.locals[5],2);f.pc=201;return;}
case 201:{f.push(1,1);f.pc=202;return;}
case 202:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=203;return;}
case 203:{f.rearrange(91);f.pc=204;return;}
case 204:{f.rearrange(91);f.pc=205;return;}
case 205:{f.rearrange(87);f.pc=206;return;}
case 206:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=207;return;}
case 207:{f.push(0,1);f.pc=208;return;}
case 208:{f.rearrange(95);f.pc=209;return;}
case 209:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=210;return;}
case 210:{f.push(6451442512750495039L,2);f.pc=211;return;}
case 211:{f.push(f.locals[2],2);f.pc=212;return;}
case 212:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$15(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=213;return;}
case 213:{f.push(f.locals[9],1);f.pc=214;return;}
case 214:{if(f.pop()!=null){f.pc=245;return;}f.pc=215;return;}
case 215:{f.pc=216;return;}
case 216:{f.pc=223;return;}
case 217:{f.pc=218;return;}
case 218:{f.pc=219;return;}
case 219:{f.push(6451499725508708121L,2);f.pc=220;return;}
case 220:{f.push(f.locals[2],2);f.pc=221;return;}
case 221:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=222;return;}
case 222:{throw (Throwable)f.pop();}
case 223:{f.pc=224;return;}
case 224:{f.pc=225;return;}
case 225:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=236;return;}f.pc=226;return;}
case 226:{f.pc=227;return;}
case 227:{f.pc=228;return;}
case 228:{f.push(0,1);f.pc=229;return;}
case 229:{f.result=f.pop();f.done=true;return;}
case 230:{f.pc=231;return;}
case 231:{f.pc=232;return;}
case 232:{f.push(6451499725508708121L,2);f.pc=233;return;}
case 233:{f.push(f.locals[2],2);f.pc=234;return;}
case 234:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=235;return;}
case 235:{throw (Throwable)f.pop();}
case 236:{f.pc=237;return;}
case 237:{f.pc=238;return;}
case 238:{f.pc=239;return;}
case 239:{f.push(f.locals[0],1);f.pc=240;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$3$2(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 240:{f.push(((dev.zprestige.sourceview.S0074)(Object)f.pop()).a,1);f.pc=241;return;}
case 241:{f.push(f.locals[4],1);f.pc=242;return;}
case 242:{f.push(6451123100324082091L,2);f.pc=243;return;}
case 243:{f.push(f.locals[2],2);f.pc=244;return;}
case 244:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$18(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=245;return;}
case 245:{f.pc=246;return;}
case 246:{f.pc=247;return;}
case 247:{f.push(f.locals[9],1);f.pc=248;return;}
case 248:{if(f.pop()!=null){f.pc=273;return;}f.pc=249;return;}
case 249:{f.pc=250;return;}
case 250:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=292;return;}f.pc=251;return;}
case 251:{f.pc=252;return;}
case 252:{f.pc=259;return;}
case 253:{f.pc=254;return;}
case 254:{f.pc=255;return;}
case 255:{f.push(6451499725508708121L,2);f.pc=256;return;}
case 256:{f.push(f.locals[2],2);f.pc=257;return;}
case 257:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$19(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=258;return;}
case 258:{throw (Throwable)f.pop();}
case 259:{f.pc=260;return;}
case 260:{f.pc=261;return;}
case 261:{f.push(f.locals[4],1);f.pc=262;return;}
case 262:{f.push(6450897471231248305L,2);f.pc=263;return;}
case 263:{f.push(f.locals[2],2);f.pc=264;return;}
case 264:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=265;return;}
case 265:{f.pc=266;return;}
case 266:{f.pc=273;return;}
case 267:{f.pc=268;return;}
case 268:{f.pc=269;return;}
case 269:{f.push(6451499725508708121L,2);f.pc=270;return;}
case 270:{f.push(f.locals[2],2);f.pc=271;return;}
case 271:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=272;return;}
case 272:{throw (Throwable)f.pop();}
case 273:{f.pc=274;return;}
case 274:{f.pc=275;return;}
case 275:{f.push(f.locals[9],1);f.pc=276;return;}
case 276:{if(f.pop()!=null){f.pc=289;return;}f.pc=277;return;}
case 277:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=292;return;}f.pc=278;return;}
case 278:{f.pc=279;return;}
case 279:{f.pc=286;return;}
case 280:{f.pc=281;return;}
case 281:{f.pc=282;return;}
case 282:{f.push(6451499725508708121L,2);f.pc=283;return;}
case 283:{f.push(f.locals[2],2);f.pc=284;return;}
case 284:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$22(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=285;return;}
case 285:{throw (Throwable)f.pop();}
case 286:{f.pc=287;return;}
case 287:{f.pc=288;return;}
case 288:{f.push(1,1);f.pc=289;return;}
case 289:{f.pc=290;return;}
case 290:{f.pc=291;return;}
case 291:{f.pc=295;return;}
case 292:{f.pc=293;return;}
case 293:{f.pc=294;return;}
case 294:{f.push(0,1);f.pc=295;return;}
case 295:{f.pc=296;return;}
case 296:{f.pc=297;return;}
case 297:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$13$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(f.locals[1],1);f.pc=1;return;}
case 1:{f.rearrange(89);f.pc=2;return;}
case 2:{f.push(0,1);f.pc=3;return;}
case 3:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=4;return;}
case 4:{f.push((net.minecraft.class_1297)(Object)f.pop(),1);f.pc=5;return;}
case 5:{f.locals[2]=f.pop();f.pc=6;return;}
case 6:{f.rearrange(89);f.pc=7;return;}
case 7:{f.push(1,1);f.pc=8;return;}
case 8:{int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();f.push(((Object[])array)[index],1);f.pc=9;return;}
case 9:{f.push((java.lang.Long)(Object)f.pop(),1);f.pc=10;return;}
case 10:{Object receiver=f.pop();f.push(((java.lang.Long)(Object)receiver).longValue(),2);f.pc=11;return;}
case 11:{f.locals[3]=f.pop();f.pc=12;return;}
case 12:{f.rearrange(87);f.pc=13;return;}
case 13:{f.push(dev.zprestige.sourceview.S0074.d,2);f.pc=14;return;}
case 14:{f.push(f.locals[3],2);f.pc=15;return;}
case 15:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=16;return;}
case 16:{f.locals[3]=f.pop();f.pc=17;return;}
case 17:{f.push(f.locals[3],2);f.pc=18;return;}
case 18:{f.rearrange(92);f.pc=19;return;}
case 19:{f.push(5217238789553L,2);f.pc=20;return;}
case 20:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=21;return;}
case 21:{f.locals[5]=f.pop();f.pc=22;return;}
case 22:{f.rearrange(88);f.pc=23;return;}
case 23:{f.push(-5653030419804697375L,2);f.pc=24;return;}
case 24:{f.push(f.locals[3],2);f.pc=25;return;}
case 25:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$25(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=26;return;}
case 26:{f.locals[7]=f.pop();f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.pc=29;return;}
case 29:{f.push(f.locals[2],1);f.pc=30;return;}
case 30:{f.push(f.locals[7],1);f.pc=31;return;}
case 31:{if(f.pop()!=null){f.pc=66;return;}f.pc=32;return;}
case 32:{f.pc=33;return;}
case 33:{f.push(dev.zprestige.sourceview.S0074.b,1);f.pc=34;return;}
case 34:{f.push(-5652992311990342537L,2);f.pc=35;return;}
case 35:{f.push(f.locals[3],2);f.pc=36;return;}
case 36:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=37;return;}
case 37:{Object b=f.pop(),a=f.pop();if(a!=b){f.pc=57;return;}f.pc=38;return;}
case 38:{f.pc=39;return;}
case 39:{f.pc=46;return;}
case 40:{f.pc=41;return;}
case 41:{f.pc=42;return;}
case 42:{f.push(-5652251432156991714L,2);f.pc=43;return;}
case 43:{f.push(f.locals[3],2);f.pc=44;return;}
case 44:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$27(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=45;return;}
case 45:{throw (Throwable)f.pop();}
case 46:{f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(0,1);f.pc=50;return;}
case 50:{f.result=f.pop();f.done=true;return;}
case 51:{f.pc=52;return;}
case 52:{f.pc=53;return;}
case 53:{f.push(-5652251432156991714L,2);f.pc=54;return;}
case 54:{f.push(f.locals[3],2);f.pc=55;return;}
case 55:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=56;return;}
case 56:{throw (Throwable)f.pop();}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.push(f.locals[0],1);f.pc=61;return;}
case 61:{f.push(((dev.zprestige.sourceview.S0074)(Object)f.pop()).c,1);f.pc=62;return;}
case 62:{f.push(f.locals[2],1);f.pc=63;return;}
case 63:{f.push(-5652351954874876801L,2);f.pc=64;return;}
case 64:{f.push(f.locals[3],2);f.pc=65;return;}
case 65:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$29(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.pc=68;return;}
case 68:{f.push((java.lang.Boolean)(Object)f.pop(),1);f.pc=69;return;}
case 69:{f.locals[8]=f.pop();f.pc=70;return;}
case 70:{f.pc=71;return;}
case 71:{f.pc=72;return;}
case 72:{f.push(f.locals[8],1);f.pc=73;return;}
case 73:{f.push(f.locals[7],1);f.pc=74;return;}
case 74:{if(f.pop()!=null){f.pc=88;return;}f.pc=75;return;}
case 75:{if(f.pop()==null){f.pc=94;return;}f.pc=76;return;}
case 76:{f.pc=77;return;}
case 77:{f.pc=84;return;}
case 78:{f.pc=79;return;}
case 79:{f.pc=80;return;}
case 80:{f.push(-5652251432156991714L,2);f.pc=81;return;}
case 81:{f.push(f.locals[3],2);f.pc=82;return;}
case 82:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$30(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=83;return;}
case 83:{throw (Throwable)f.pop();}
case 84:{f.pc=85;return;}
case 85:{f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.push(f.locals[8],1);f.pc=88;return;}
case 88:{f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.push(-5652412876435116488L,2);f.pc=91;return;}
case 91:{f.push(f.locals[3],2);f.pc=92;return;}
case 92:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$31(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=93;return;}
case 93:{f.result=f.pop();f.done=true;return;}
case 94:{f.pc=95;return;}
case 95:{f.pc=96;return;}
case 96:{f.pc=97;return;}
case 97:{f.push(f.locals[0],1);f.pc=98;return;}
case 98:{f.push(f.locals[2],1);f.pc=99;return;}
case 99:{f.push(f.locals[5],2);f.pc=100;return;}
case 100:{f.push(2,1);f.pc=101;return;}
case 101:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=102;return;}
case 102:{f.rearrange(91);f.pc=103;return;}
case 103:{f.rearrange(91);f.pc=104;return;}
case 104:{f.rearrange(87);f.pc=105;return;}
case 105:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=106;return;}
case 106:{f.push(1,1);f.pc=107;return;}
case 107:{f.rearrange(95);f.pc=108;return;}
case 108:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=109;return;}
case 109:{f.rearrange(90);f.pc=110;return;}
case 110:{f.rearrange(95);f.pc=111;return;}
case 111:{f.push(0,1);f.pc=112;return;}
case 112:{f.rearrange(95);f.pc=113;return;}
case 113:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=114;return;}
case 114:{f.push(-5656211477865080203L,2);f.pc=115;return;}
case 115:{f.push(f.locals[3],2);f.pc=116;return;}
case 116:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$32(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=117;return;}
case 117:{f.locals[9]=f.pop();f.pc=118;return;}
case 118:{f.pc=119;return;}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$13$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.push(f.locals[0],1);f.pc=121;return;}
case 121:{f.push(((dev.zprestige.sourceview.S0074)(Object)f.pop()).c,1);f.pc=122;return;}
case 122:{f.push(f.locals[2],1);f.pc=123;return;}
case 123:{f.push(f.locals[9],1);f.pc=124;return;}
case 124:{f.push(-5656306263462520200L,2);f.pc=125;return;}
case 125:{f.push(f.locals[3],2);f.pc=126;return;}
case 126:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$33((dev.zprestige.sourceview.FlowFrame.i(a0) != 0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=127;return;}
case 127:{f.push(-5652522261032760763L,2);f.pc=128;return;}
case 128:{f.push(f.locals[3],2);f.pc=129;return;}
case 129:{Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$34(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),((java.lang.Object)(Object)(a2)),dev.zprestige.sourceview.FlowFrame.j(a3),dev.zprestige.sourceview.FlowFrame.j(a4)),1);f.pc=130;return;}
case 130:{f.rearrange(87);f.pc=131;return;}
case 131:{f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.push(f.locals[9],1);f.pc=134;return;}
case 134:{f.result=f.pop();f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$17$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0074.d,2);f.pc=1;return;}
case 1:{f.push(16021200551912L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(8816738738781062966L,2);f.pc=5;return;}
case 5:{f.push(f.locals[2],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$37(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.push(f.locals[0],1);f.pc=10;return;}
case 10:{f.push(((dev.zprestige.sourceview.S0074)(Object)f.pop()).c,1);f.pc=11;return;}
case 11:{f.push(8816004310673946362L,2);f.pc=12;return;}
case 12:{f.push(f.locals[2],2);f.pc=13;return;}
case 13:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0074.__indy$38(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=14;return;}
case 14:{f.locals[4]=f.pop();f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.push(f.locals[0],1);f.pc=18;return;}
case 18:{f.push(((dev.zprestige.sourceview.S0074)(Object)f.pop()).a,1);f.pc=19;return;}
case 19:{f.push(8816175783956389601L,2);f.pc=20;return;}
case 20:{f.push(f.locals[2],2);f.pc=21;return;}
case 21:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$39(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=22;return;}
case 22:{f.push(f.locals[4],1);f.pc=23;return;}
case 23:{if(f.pop()!=null){f.pc=51;return;}f.pc=24;return;}
case 24:{f.pc=25;return;}
case 25:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=54;return;}f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.pc=34;return;}
case 28:{f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{f.push(8815818979353175241L,2);f.pc=31;return;}
case 31:{f.push(f.locals[2],2);f.pc=32;return;}
case 32:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$40(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=33;return;}
case 33:{throw (Throwable)f.pop();}
case 34:{f.pc=35;return;}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.push(f.locals[0],1);f.pc=38;return;}
case 38:{f.push(((dev.zprestige.sourceview.S0074)(Object)f.pop()).a,1);f.pc=39;return;}
case 39:{f.push(dev.zprestige.sourceview.S0074.__indy$41(),1);f.pc=40;return;}
case 40:{f.push(8815569151357608765L,2);f.pc=41;return;}
case 41:{f.push(f.locals[2],2);f.pc=42;return;}
case 42:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$42(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.pc=51;return;}
case 45:{f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.push(8815818979353175241L,2);f.pc=48;return;}
case 48:{f.push(f.locals[2],2);f.pc=49;return;}
case 49:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0074.__indy$43(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=50;return;}
case 50:{throw (Throwable)f.pop();}
case 51:{f.pc=52;return;}
case 52:{f.pc=53;return;}
case 53:{f.rearrange(87);f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-9116454308433920823L,2);f.pc=1;return;}
case 1:{f.push(-9051299646168308201L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(192890772643981L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0074.class,"d",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(39,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0074.class,"e",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(39,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0074.class,"f",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0074.a$return$9b75();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
