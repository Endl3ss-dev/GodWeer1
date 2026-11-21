package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0049;
import dev.zprestige.sourceview.S0106;
import dev.zprestige.sourceview.S0118;
import dev.zprestige.sourceview.S0133;
import dev.zprestige.sourceview.S0151;
import dev.zprestige.sourceview.S0251;
import dev.zprestige.sourceview.S0260;
import dev.zprestige.sourceview.S0390;
import dev.zprestige.sourceview.S0549;
import dev.zprestige.sourceview.S0599;
import net.minecraft.class_1657;
import net.minecraft.class_3966;


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
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_239;
import net.minecraft.class_2561;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;

public class S0322 extends S0260 {
   public S0251 d;
   public S0251 a;
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
         throw new RuntimeException("dev/zprestige/prestige/eR" + " : " + var1 + " : " + var2.toString(), var5);
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

      l[0] = "x\u0005\\X\fVx\u0005K\u0004\u0000YbNK\u001a\u0000Le?\u001bOW\t";
      l[1] = "$+\u000f\u001f(c$+\u0018C$l>`\u0018]$y9\u0011J\u0003|=";
      l[2] = "Usg[g\u0007Csb\u0001t\u0010T8a\u0007x\u0004E\u007fv\u00103\u0016y";
      l[3] = "\u0007>.=G&r\u001e%2Vi\u000f\u000665_ g";
      l[4] = "%W\ri9R%W\u001a55]?\u001c\u001a+5H8mKrb\n";
      l[5] = ",E\u000e\r\u0017%,E\u0019Q\u001b*6\u000e\u0019O\u001b?1\u007fI\u0012J";
      l[6] = "2\u001c\\\u0014;Y2\u001cKH7V(WKV7C/&\u001a\to";
      l[7] = "&4E/@^S\u0014N Q\u00112\u001aE+UKF";
      l[8] = void.class;
      m[8] = "java/lang/Void";
      l[9] = "C\u000bqB&|U\u000bt\u00185kB@w\u001e9\u007fS\u0007`\troK\u0007b\u0002(\"w\u001cb\u001f(e@\u000b";
      l[10] = "\u0001%ADV+\u0017%D\u001eE<\u0000nG\u0018I(\u0011)P\u000f\u0002=3";
      l[11] = ".\u0011\u0003G8A%\u001e\u0012\bEY6\u0019\u001bA";
      l[12] = "\u0007\u0004\"<\u007fg\u0007\u00045`sh\u001dO5~s}\u001a>d!+*\n\r7aaQ[Uf";
      l[13] = "\u0018&wH\u0010\"m\u0006|G\u0001m\f\bwL\u00057x";
      l[14] = boolean.class;
      m[14] = "java/lang/Boolean";
      l[15] = "\rA9?0c\u001bA<e#t\f\n?c/`\u001dM(tdv;";
      l[16] = "\f>l\u0002\u001a\u000f\u00071}My\u0002\u0012<r&L\u0000\u0003/n\n[\r";
      l[17] = "U\u0014@}1K^\u001bQ2]HP\u0019S}q";
      l[18] = "psf\u0011\u000ePfscK\u001dGq8`M\u0011S`\u007fwZZD_";
      l[19] = "%l{UO%.cj\u001a.+%hn@";
      l[20] = "&\u00194F\n\u00100\u00191\u001c\u0019\u0007'R2\u001a\u0015\u00136\u0015%\r^\u0001\t";
      l[21] = "V\u001bf\u001buY#;m\u0014d\u0016B5f\u001f`L6";
      l[22] = "\u0017sE$'[\t{_kEG\u000ef";
      l[23] = "o[;:^5qS!u94`H,/\u001f2";
      l[24] = "\u001c\u0012Th\u0004\u0019\u001c\u0012C4\b\u0016\u0006YC*\b\u0003\u0001(\u0011t]";
      l[25] = "@L/<f\u007fVL*fuhA\u0007)`y|P@>w2mG";
      l[26] = "J\nCqS\\?*H~B\u0013^$CuFI*";
      l[27] = "ohF\u0017\bWdgWXeSd{c\u0013WN`gS\u0013";
      l[28] = "\u0002A\u0003#uOwa\b,d\u0000\u0016o\u0003'`Zb";
      l[29] = "BG\u0000S5f7g\u000b\\$)Vi\u0000W s\"";
      l[30] = "N\"_\r\fjN\"HQ\u0000eTiHO\u0000pS\u0018\u001c\u0017W";
      l[31] = "O\u0001k^\u0017y:!`Q\u00066[/kZ\u0002l/";
      l[32] = "_jyr)\u001dIj|(:\n^!\u007f.6\u001eOfh9}\u0014";
      l[33] = "\u0010N$/9jR\u001bd+Zr/\u001c|\u007fgaS\u000bsha\u001b";
      l[34] = "BCJ^\u000f\u001b\u0014\u0003\u0018ErI{\u0003\u0019^\u0003\u001f\u001f@E]J#";
      l[35] = "7\u000bAy\u000e5p\b\u000exk*cK\u001c$\u0007\u00183\u000bC|kt3H\u0002:\u001a$w\r\u0013C";
      l[36] = "1gV-\\\u00117kO,3\u0018k{IqdO5(\u0010\u001d\u000f\u0019w,Lv\u000bMzr";
      l[37] = "n[2\u0001n!?@pQTy\u0002O-X6lnFt\u0003i\u0010;\u0017u\u00021j|\u0014:\u0003T";
      l[38] = "?>!Z-\u0012928[B\u001be\">\u0006\u0015L;ufj&\u001a}(j\u0010#K5,";
      l[39] = "hRDU\u001e\u0004>\u0012\u0016NcUQL\u001fK\u0001@=EF\u0010^<lP\u0006R\tC6E\u0004@c";
      l[40] = "\u0019z_t\u000b\"\\j\\ur+':\r7I'VxXwM";
      l[41] = "\u000e\u001fGx\u001as^[\u0002icmX^cz\u0019cSM86]>KMQ<R;_\"";
      l[42] = "2+tG\u0005ia }\u001bu{['u\u0010\u0015lb$~\u000b\t\u001214g\u001b\u000b+2?|\u0007u";
      l[43] = "'l'ijc-c\"}\u0005ap4g~dllRzocwfnbgg;\u0017cjkx7ec`l{\n";
      l[44] = "6\u0019{?Um<Dood<0]Yc\u0014 Y\u0017nr\u0019a+\u0017du\u001a\\";
      l[45] = "J|&v\u0004#C?.k;9*d*tY.Fms/\u0006R\u0017x3mQ-Mm1\u007f;";
      l[46] = "\u0016(YaH&D A\u000f\u0010'T0\\c\"s\u0017o\u000b5us\u0014m\u0001j\u000f4\u0017\"\u0000\u000f";
      l[47] = "\u0001\u001dshXh\u000b@g8i?\u0003RkY\u00056\u0016Dhb\u0011&\f\u0019\u0017";
      l[48] = "Cr?8B\u001eJih<y\u0002Mb=+.U\u00140dG\u0016\u0011Jpb5\u001f\n\u001dt";
      l[49] = "4Q\u0002wcz=\u0012\nj\\aTI\u000eu>w8@W.a\u000bd\u0013Wk3bn\u001cR\u007f\\";
      l[50] = "\bt\u000f_fZF{\u0000F\u001c\u000bYw\u0019AK\\\u0003!D-,\u000bJh\u0007\u0012b\u0004Eq";
      l[51] = "\u0000b\r\u0007l\u001fB7M\u0003\u000f\u0007?:S\\m\u0012S3\n\u00072n\u000e/JA2\u001c\u000e%MB\u000f";
      l[52] = "ru6\u001aP\nt:5D4\\\u007fm<O]PFc<_Y6){$_\tD)q#\\4";
      l[53] = "K\n+za9N[c~Q-N\b6q\u0006}\u0015Xh\u001d`2S\u0018oo`8T\u001b";
      l[54] = "\u0013R!\u007f#U\u001a\u0011)b\u001cNsJ-}~X\u001fCt&!$NV4dv[\u0014C6v\u001c";
      l[55] = "_\u0006W\u000f\u0012XI\u0013\r\nr\u00014\u0010S\b\u0014\u0017\u000f\u0004C\u0012Ih";
      l[56] = "pA=\u0017Q:zN8\u0003>-)\u001b|\u0004y=@\u0013n\u0011X,{\u0007~\u000b\u0005SpA=\u0017Q:zN8\u0003>";
      l[57] = "\u0014\u000ft\u000e\u0001'\u0003\u0000c\b{:\u0006\u0001eZ\u0007<\u0000lp\f\u001fjK]zQ\u000b:z";
      l[58] = "\u0018\r\u00138\u0006\u0000\u001cY\u001efg\u000eI\u001e\u000ee\u000b<\u001d]Q2\\k\u0014\f\u001cp\u001dTZ\u0003\u0013ig";
      l[59] = "\u001e9J/&GHy\u00184[\u0016''\u001119\u0003K.Hjf\u007fB8\u000b.:\u0003Z8\u0016?[";
      l[60] = "K25j=|B)bn\u0006`E\"7yQ7\u001cpk\u0015isB0hg`h\u00154";
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, Object[] var4) {
try {

      int var5 = var4.length - 2;
      long var6 = (Long)(Object)(var4[var5]);
      long var9 = (Long)(Object)(var4[++var5]);
      MethodHandle var8 = d(var0, var1, var2, var3, var6, var9);
      var1.setTarget(MethodHandles.explicitCastArguments(var8, var3));
      return __indy$1(var8.asSpreader(Object[].class, var4.length), var4);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static MethodHandle d(Lookup var0, MutableCallSite var1, String var2, MethodType var3, long var4, long var6) {
try {

      char var8 = var2.charAt(0);
      MethodHandle var9 = null;
      Field var10 = null;
      Method var11 = null;

      try {
         if (var8 != 'y' && var8 != 'i' && var8 != 201 && var8 != 252) {
            var11 = p(var4, var6);
            Class var17 = var11.getDeclaringClass();
            String var19 = var11.getName();
            MethodType var20 = MethodType.methodType(var11.getReturnType(), var11.getParameterTypes());
            if (var8 == 214) {
               var9 = var0.findVirtual(var17, var19, var20);
            } else if (var8 == 216) {
               var9 = var0.findStatic(var17, var19, var20);
            } else {
               var9 = var0.findSpecial(var17, var19, var20, var17);
            }
         } else {
            var10 = o(var4, var6);
            Class var12 = var10.getDeclaringClass();
            String var18 = var10.getName();
            Class var14 = var10.getType();
            if (var8 == 'y') {
               var9 = var0.findGetter(var12, var18, var14);
            } else if (var8 == 'i') {
               var9 = var0.findSetter(var12, var18, var14);
            } else if (var8 == 201) {
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
   public void a(S0049 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(8,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$9$0(f);break;
case 1:__flow$9$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=14&&f.pc<29&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=29;continue;}
if(f.pc>=45&&f.pc<53&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=55;continue;}
if(f.pc>=51&&f.pc<92&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=94;continue;}
if(f.pc>=100&&f.pc<111&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=113;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   @S0118
   public void a(S0151 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(9,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$10$0(f);break;
case 1:__flow$10$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=14&&f.pc<29&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=29;continue;}
if(f.pc>=43&&f.pc<49&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=51;continue;}
if(f.pc>=65&&f.pc<77&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=79;continue;}
if(f.pc>=69&&f.pc<89&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=91;continue;}
if(f.pc>=104&&f.pc<112&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=114;continue;}
if(f.pc>=110&&f.pc<151&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=153;continue;}
if(f.pc>=159&&f.pc<170&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=172;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   @S0118
   public void a(S0106 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(9,7);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$11$0(f);break;
case 1:__flow$11$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=14&&f.pc<27&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=29;continue;}
if(f.pc>=24&&f.pc<39&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=39;continue;}
if(f.pc>=53&&f.pc<63&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=63;continue;}
if(f.pc>=77&&f.pc<83&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=85;continue;}
if(f.pc>=99&&f.pc<111&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=113;continue;}
if(f.pc>=103&&f.pc<123&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=125;continue;}
if(f.pc>=138&&f.pc<146&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=148;continue;}
if(f.pc>=144&&f.pc<185&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=187;continue;}
if(f.pc>=193&&f.pc<214&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=216;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   @S0118
   public void a(S0133 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(7,8);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$12$0(f);break;
case 1:__flow$12$1(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=8&&f.pc<23&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=23;continue;}
if(f.pc>=55&&f.pc<71&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=73;continue;}
if(f.pc>=60&&f.pc<124&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=126;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   public static MatchException a(MatchException var0) {
try {

      return var0;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
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
         case 0 -> 20;
         case 1 -> 42;
         case 2 -> 22;
         case 3 -> 0;
         case 4 -> 16;
         case 5 -> 32;
         case 6 -> 28;
         case 7 -> 12;
         case 8 -> 47;
         case 9 -> 61;
         case 10 -> 27;
         case 11 -> 34;
         case 12 -> 49;
         case 13 -> 5;
         case 14 -> 14;
         case 15 -> 17;
         case 16 -> 45;
         case 17 -> 50;
         case 18 -> 40;
         case 19 -> 35;
         case 20 -> 52;
         case 21 -> 29;
         case 22 -> 26;
         case 23 -> 41;
         case 24 -> 58;
         case 25 -> 21;
         case 26 -> 15;
         case 27 -> 9;
         case 28 -> 4;
         case 29 -> 8;
         case 30 -> 3;
         case 31 -> 6;
         case 32 -> 11;
         case 33 -> 62;
         case 34 -> 23;
         case 35 -> 54;
         case 36 -> 60;
         case 37 -> 56;
         case 38 -> 63;
         case 39 -> 57;
         case 40 -> 19;
         case 41 -> 33;
         case 42 -> 44;
         case 43 -> 39;
         case 44 -> 59;
         case 45 -> 36;
         case 46 -> 2;
         case 47 -> 53;
         case 48 -> 10;
         case 49 -> 51;
         case 50 -> 43;
         case 51 -> 25;
         case 52 -> 13;
         case 53 -> 31;
         case 54 -> 38;
         case 55 -> 1;
         case 56 -> 48;
         case 57 -> 24;
         case 58 -> 7;
         case 59 -> 30;
         case 60 -> 18;
         case 61 -> 46;
         case 62 -> 37;
         default -> 55;
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
            var12 = n(1367428306469871L, 0L);
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
            var23 = n(1367428306469871L, 0L);
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
            var23 = n(1367428306469871L, 0L);
         }
      }
   
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

   private static native Object __indy$1(MethodHandle var0, Object[] var1) throws Throwable;

   private static native String[] __indy$2(long var0, long var2) throws Throwable;

   private static native Object __indy$3(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$4(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$5(Object var0, long var1, long var3) throws Throwable;

   private static native class_1297 __indy$6(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$7(Object var0, long var1, long var3) throws Throwable;

   private static native S0390 __indy$8(long var0, long var2) throws Throwable;

   private static native class_2561 __indy$9(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$10(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$11(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$12(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$13(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$14(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$15(long var0, long var2) throws Throwable;

   private static native Object __indy$16(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$17(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$18(Object var0, long var1, long var3) throws Throwable;

   private static native class_239 __indy$19(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$20(Object var0, long var1, long var3) throws Throwable;

   private static native class_240 __indy$21(Object var0, long var1, long var3) throws Throwable;

   private static native class_240 __indy$22(long var0, long var2) throws Throwable;

   private static native MatchException __indy$23(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$24(Object var0, long var1, long var3) throws Throwable;

   private static native class_1297 __indy$25(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$26(Object var0, long var1, long var3) throws Throwable;

   private static native S0390 __indy$27(long var0, long var2) throws Throwable;

   private static native class_2561 __indy$28(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$29(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$30(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$31(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$32(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$33(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$34(long var0, long var2) throws Throwable;

   private static native S0599 __indy$35(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$36(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$37(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$38(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$39(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$40(Object var0, long var1, long var3) throws Throwable;

   private static native class_239 __indy$41(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$42(Object var0, long var1, long var3) throws Throwable;

   private static native class_240 __indy$43(Object var0, long var1, long var3) throws Throwable;

   private static native class_240 __indy$44(long var0, long var2) throws Throwable;

   private static native MatchException __indy$45(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$46(Object var0, long var1, long var3) throws Throwable;

   private static native class_1297 __indy$47(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$48(Object var0, long var1, long var3) throws Throwable;

   private static native S0390 __indy$49(long var0, long var2) throws Throwable;

   private static native class_2561 __indy$50(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$51(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$52(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$53(Object var0, long var1, long var3) throws Throwable;

   private static native class_746 __indy$54(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$55(Object var0, boolean var1, long var2, long var4) throws Throwable;

   private static native void __indy$56(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$57(Object var0, long var1, long var3) throws Throwable;

   private static native String[] __indy$58(long var0, long var2) throws Throwable;

   private static native Object __indy$59(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$60(Object var0, long var1, long var3) throws Throwable;

   private static native MatchException __indy$61(Object var0, long var1, long var3) throws Throwable;

   private static native S0390 __indy$62(long var0, long var2) throws Throwable;

   private static native List __indy$63(Object var0, long var1, long var3) throws Throwable;

   private static native Iterator __indy$64(Object var0, long var1, long var3) throws Throwable;

   private static native boolean __indy$65(Object var0, long var1, long var3) throws Throwable;

   private static native Object __indy$66(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$67(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native boolean __indy$68(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$69(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$70(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native class_124 __indy$71(long var0, long var2) throws Throwable;

   private static native String __indy$72(Object var0, long var1, long var3) throws Throwable;

   private static native class_124 __indy$73(long var0, long var2) throws Throwable;

   private static native String __indy$74(Object var0, long var1, long var3) throws Throwable;

   private static native String __indy$75(String var0, String var1, String var2) throws Throwable;

   private static native void __indy$76(Object var0, Object var1, long var2, long var4) throws Throwable;

   private static native MatchException __indy$77(Object var0, long var1, long var3) throws Throwable;

   private static native void __indy$78(Object var0, Object var1, long var2, long var4) throws Throwable;

public S0322()throws Throwable{super();}

private void __flow$9$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0322.k,2);f.pc=1;return;}
case 1:{f.push(107524329489355L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(83850499134855L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.push(8360364188461281971L,2);f.pc=11;return;}
case 11:{f.push(f.locals[2],2);f.pc=12;return;}
case 12:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$2(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=13;return;}
case 13:{f.locals[6]=f.pop();f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.push(f.locals[0],1);f.pc=17;return;}
case 17:{f.push(((dev.zprestige.sourceview.S0322)(Object)f.pop()).d,1);f.pc=18;return;}
case 18:{f.push(8358875267895178474L,2);f.pc=19;return;}
case 19:{f.push(f.locals[2],2);f.pc=20;return;}
case 20:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$3(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=21;return;}
case 21:{f.push((java.lang.Boolean)(Object)f.pop(),1);f.pc=22;return;}
case 22:{f.push(8358807913581387732L,2);f.pc=23;return;}
case 23:{f.push(f.locals[2],2);f.pc=24;return;}
case 24:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=25;return;}
case 25:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=35;return;}f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.done=true;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.push(8360887168012725725L,2);f.pc=32;return;}
case 32:{f.push(f.locals[2],2);f.pc=33;return;}
case 33:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=34;return;}
case 34:{throw (Throwable)f.pop();}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(f.locals[1],1);f.pc=39;return;}
case 39:{f.push(0,1);f.pc=40;return;}
case 40:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=41;return;}
case 41:{f.push(8359875119316602016L,2);f.pc=42;return;}
case 42:{f.push(f.locals[2],2);f.pc=43;return;}
case 43:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$6(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=44;return;}
case 44:{f.locals[7]=f.pop();f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.push(f.locals[7],1);f.pc=48;return;}
case 48:{f.push((f.pop() instanceof net.minecraft.class_1657?1:0),1);f.pc=49;return;}
case 49:{f.push(f.locals[6],1);f.pc=50;return;}
case 50:{if(f.pop()!=null){f.pc=100;return;}f.pc=51;return;}
case 51:{f.pc=52;return;}
case 52:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=119;return;}f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.pc=61;return;}
case 55:{f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.push(8360887168012725725L,2);f.pc=58;return;}
case 58:{f.push(f.locals[2],2);f.pc=59;return;}
case 59:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$7(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=60;return;}
case 60:{throw (Throwable)f.pop();}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.push(8360773804222122972L,2);f.pc=65;return;}
case 65:{f.push(f.locals[2],2);f.pc=66;return;}
case 66:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$8(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=67;return;}
case 67:{f.push(f.locals[7],1);f.pc=68;return;}
case 68:{f.push(8360240323782855769L,2);f.pc=69;return;}
case 69:{f.push(f.locals[2],2);f.pc=70;return;}
case 70:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=71;return;}
case 71:{f.push(8360691642051218589L,2);f.pc=72;return;}
case 72:{f.push(f.locals[2],2);f.pc=73;return;}
case 73:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=74;return;}
case 74:{f.push(f.locals[4],2);f.pc=75;return;}
case 75:{f.push(2,1);f.pc=76;return;}
case 76:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=77;return;}
case 77:{f.rearrange(91);f.pc=78;return;}
case 78:{f.rearrange(91);f.pc=79;return;}
case 79:{f.rearrange(87);f.pc=80;return;}
case 80:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=81;return;}
case 81:{f.push(1,1);f.pc=82;return;}
case 82:{f.rearrange(95);f.pc=83;return;}
case 83:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=84;return;}
case 84:{f.rearrange(90);f.pc=85;return;}
case 85:{f.rearrange(95);f.pc=86;return;}
case 86:{f.push(0,1);f.pc=87;return;}
case 87:{f.rearrange(95);f.pc=88;return;}
case 88:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=89;return;}
case 89:{f.push(8359144540211620624L,2);f.pc=90;return;}
case 90:{f.push(f.locals[2],2);f.pc=91;return;}
case 91:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$11(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.pc=100;return;}
case 94:{f.pc=95;return;}
case 95:{f.pc=96;return;}
case 96:{f.push(8360887168012725725L,2);f.pc=97;return;}
case 97:{f.push(f.locals[2],2);f.pc=98;return;}
case 98:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=99;return;}
case 99:{throw (Throwable)f.pop();}
case 100:{f.pc=101;return;}
case 101:{f.pc=102;return;}
case 102:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=119;return;}f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{f.pc=105;return;}
case 105:{f.push(f.locals[1],1);f.pc=106;return;}
case 106:{f.push(0,1);f.pc=107;return;}
case 107:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=108;return;}
case 108:{f.push(8359981348722244458L,2);f.pc=109;return;}
case 109:{f.push(f.locals[2],2);f.pc=110;return;}
case 110:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0322.__indy$13(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.pc=119;return;}
case 113:{f.pc=114;return;}
case 114:{f.pc=115;return;}
case 115:{f.push(8360887168012725725L,2);f.pc=116;return;}
case 116:{f.push(f.locals[2],2);f.pc=117;return;}
case 117:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=118;return;}
case 118:{throw (Throwable)f.pop();}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$9$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$10$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0322.k,2);f.pc=1;return;}
case 1:{f.push(6813484749398L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(48079492826138L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.push(2709087901060293422L,2);f.pc=11;return;}
case 11:{f.push(f.locals[2],2);f.pc=12;return;}
case 12:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$15(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=13;return;}
case 13:{f.locals[6]=f.pop();f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.push(f.locals[0],1);f.pc=17;return;}
case 17:{f.push(((dev.zprestige.sourceview.S0322)(Object)f.pop()).d,1);f.pc=18;return;}
case 18:{f.push(2710559204763034999L,2);f.pc=19;return;}
case 19:{f.push(f.locals[2],2);f.pc=20;return;}
case 20:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$16(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=21;return;}
case 21:{f.push((java.lang.Boolean)(Object)f.pop(),1);f.pc=22;return;}
case 22:{f.push(2710345065579826761L,2);f.pc=23;return;}
case 23:{f.push(f.locals[2],2);f.pc=24;return;}
case 24:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$17(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=25;return;}
case 25:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=35;return;}f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.done=true;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.push(2709673204550827072L,2);f.pc=32;return;}
case 32:{f.push(f.locals[2],2);f.pc=33;return;}
case 33:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=34;return;}
case 34:{throw (Throwable)f.pop();}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.push(dev.zprestige.sourceview.S0322.b,1);f.pc=39;return;}
case 39:{f.push(2709198545174823839L,2);f.pc=40;return;}
case 40:{f.push(f.locals[2],2);f.pc=41;return;}
case 41:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$19(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=42;return;}
case 42:{f.locals[7]=f.pop();f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.push(f.locals[7],1);f.pc=46;return;}
case 46:{f.push(f.locals[6],1);f.pc=47;return;}
case 47:{if(f.pop()!=null){f.pc=65;return;}f.pc=48;return;}
case 48:{if(f.pop()!=null){f.pc=61;return;}f.pc=49;return;}
case 49:{f.pc=50;return;}
case 50:{f.pc=57;return;}
case 51:{f.pc=52;return;}
case 52:{f.pc=53;return;}
case 53:{f.push(2709673204550827072L,2);f.pc=54;return;}
case 54:{f.push(f.locals[2],2);f.pc=55;return;}
case 55:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=56;return;}
case 56:{throw (Throwable)f.pop();}
case 57:{f.pc=58;return;}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.done=true;return;}
case 61:{f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=64;return;}
case 64:{f.push(f.locals[7],1);f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.push(f.locals[6],1);f.pc=68;return;}
case 68:{if(f.pop()!=null){f.pc=97;return;}f.pc=69;return;}
case 69:{f.pc=70;return;}
case 70:{f.push(2710790950656914955L,2);f.pc=71;return;}
case 71:{f.push(f.locals[2],2);f.pc=72;return;}
case 72:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$21(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=73;return;}
case 73:{f.push(2710224002444823612L,2);f.pc=74;return;}
case 74:{f.push(f.locals[2],2);f.pc=75;return;}
case 75:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$22(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=76;return;}
case 76:{Object b=f.pop(),a=f.pop();if(a!=b){f.pc=178;return;}f.pc=77;return;}
case 77:{f.pc=78;return;}
case 78:{f.pc=85;return;}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.push(2709673204550827072L,2);f.pc=82;return;}
case 82:{f.push(f.locals[2],2);f.pc=83;return;}
case 83:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$23(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=84;return;}
case 84:{throw (Throwable)f.pop();}
case 85:{f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.pc=88;return;}
case 88:{f.push(f.locals[7],1);f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=97;return;}
case 91:{f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.push(2709673204550827072L,2);f.pc=94;return;}
case 94:{f.push(f.locals[2],2);f.pc=95;return;}
case 95:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$24(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=96;return;}
case 96:{throw (Throwable)f.pop();}
case 97:{f.pc=98;return;}
case 98:{f.pc=99;return;}
case 99:{f.push((net.minecraft.class_3966)(Object)f.pop(),1);f.pc=100;return;}
case 100:{f.push(2709952851573995886L,2);f.pc=101;return;}
case 101:{f.push(f.locals[2],2);f.pc=102;return;}
case 102:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$25(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=103;return;}
case 103:{f.locals[8]=f.pop();f.pc=104;return;}
case 104:{f.pc=105;return;}
case 105:{f.pc=106;return;}
case 106:{f.push(f.locals[8],1);f.pc=107;return;}
case 107:{f.push((f.pop() instanceof net.minecraft.class_1657?1:0),1);f.pc=108;return;}
case 108:{f.push(f.locals[6],1);f.pc=109;return;}
case 109:{if(f.pop()!=null){f.pc=159;return;}f.pc=110;return;}
case 110:{f.pc=111;return;}
case 111:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=178;return;}f.pc=112;return;}
case 112:{f.pc=113;return;}
case 113:{f.pc=120;return;}
case 114:{f.pc=115;return;}
case 115:{f.pc=116;return;}
case 116:{f.push(2709673204550827072L,2);f.pc=117;return;}
case 117:{f.push(f.locals[2],2);f.pc=118;return;}
case 118:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$26(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=119;return;}
case 119:{throw (Throwable)f.pop();}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$10$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.pc=123;return;}
case 123:{f.push(2709487483983059521L,2);f.pc=124;return;}
case 124:{f.push(f.locals[2],2);f.pc=125;return;}
case 125:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$27(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=126;return;}
case 126:{f.push(f.locals[8],1);f.pc=127;return;}
case 127:{f.push(2709176581428420036L,2);f.pc=128;return;}
case 128:{f.push(f.locals[2],2);f.pc=129;return;}
case 129:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$28(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=130;return;}
case 130:{f.push(2709552037894954240L,2);f.pc=131;return;}
case 131:{f.push(f.locals[2],2);f.pc=132;return;}
case 132:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$29(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=133;return;}
case 133:{f.push(f.locals[4],2);f.pc=134;return;}
case 134:{f.push(2,1);f.pc=135;return;}
case 135:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=136;return;}
case 136:{f.rearrange(91);f.pc=137;return;}
case 137:{f.rearrange(91);f.pc=138;return;}
case 138:{f.rearrange(87);f.pc=139;return;}
case 139:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=140;return;}
case 140:{f.push(1,1);f.pc=141;return;}
case 141:{f.rearrange(95);f.pc=142;return;}
case 142:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=143;return;}
case 143:{f.rearrange(90);f.pc=144;return;}
case 144:{f.rearrange(95);f.pc=145;return;}
case 145:{f.push(0,1);f.pc=146;return;}
case 146:{f.rearrange(95);f.pc=147;return;}
case 147:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=148;return;}
case 148:{f.push(2710254738168216205L,2);f.pc=149;return;}
case 149:{f.push(f.locals[2],2);f.pc=150;return;}
case 150:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$30(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=151;return;}
case 151:{f.pc=152;return;}
case 152:{f.pc=159;return;}
case 153:{f.pc=154;return;}
case 154:{f.pc=155;return;}
case 155:{f.push(2709673204550827072L,2);f.pc=156;return;}
case 156:{f.push(f.locals[2],2);f.pc=157;return;}
case 157:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$31(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=158;return;}
case 158:{throw (Throwable)f.pop();}
case 159:{f.pc=160;return;}
case 160:{f.pc=161;return;}
case 161:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=178;return;}f.pc=162;return;}
case 162:{f.pc=163;return;}
case 163:{f.pc=164;return;}
case 164:{f.push(f.locals[1],1);f.pc=165;return;}
case 165:{f.push(0,1);f.pc=166;return;}
case 166:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=167;return;}
case 167:{f.push(2709470733328362231L,2);f.pc=168;return;}
case 168:{f.push(f.locals[2],2);f.pc=169;return;}
case 169:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0322.__indy$32(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=170;return;}
case 170:{f.pc=171;return;}
case 171:{f.pc=178;return;}
case 172:{f.pc=173;return;}
case 173:{f.pc=174;return;}
case 174:{f.push(2709673204550827072L,2);f.pc=175;return;}
case 175:{f.push(f.locals[2],2);f.pc=176;return;}
case 176:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$33(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=177;return;}
case 177:{throw (Throwable)f.pop();}
case 178:{f.pc=179;return;}
case 179:{f.pc=180;return;}
case 180:{f.pc=181;return;}
case 181:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$11$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0322.k,2);f.pc=1;return;}
case 1:{f.push(102094524302964L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(f.locals[2],2);f.pc=5;return;}
case 5:{f.rearrange(92);f.pc=6;return;}
case 6:{f.push(124600115204152L,2);f.pc=7;return;}
case 7:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=8;return;}
case 8:{f.locals[4]=f.pop();f.pc=9;return;}
case 9:{f.rearrange(88);f.pc=10;return;}
case 10:{f.push(8195082371604526860L,2);f.pc=11;return;}
case 11:{f.push(f.locals[2],2);f.pc=12;return;}
case 12:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$34(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=13;return;}
case 13:{f.locals[6]=f.pop();f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.pc=16;return;}
case 16:{f.push(f.locals[1],1);f.pc=17;return;}
case 17:{f.push(0,1);f.pc=18;return;}
case 18:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=19;return;}
case 19:{f.push(8195598509716327854L,2);f.pc=20;return;}
case 20:{f.push(f.locals[2],2);f.pc=21;return;}
case 21:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$35(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=22;return;}
case 22:{f.push(f.locals[6],1);f.pc=23;return;}
case 23:{if(f.pop()!=null){f.pc=53;return;}f.pc=24;return;}
case 24:{f.pc=25;return;}
case 25:{f.push(dev.zprestige.sourceview.S0599.PRE,1);f.pc=26;return;}
case 26:{Object b=f.pop(),a=f.pop();if(a==b){f.pc=45;return;}f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.pc=35;return;}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.push(8194558476688107618L,2);f.pc=32;return;}
case 32:{f.push(f.locals[2],2);f.pc=33;return;}
case 33:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$36(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=34;return;}
case 34:{throw (Throwable)f.pop();}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.pc=38;return;}
case 38:{f.done=true;return;}
case 39:{f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.push(8194558476688107618L,2);f.pc=42;return;}
case 42:{f.push(f.locals[2],2);f.pc=43;return;}
case 43:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$37(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=44;return;}
case 44:{throw (Throwable)f.pop();}
case 45:{f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.pc=48;return;}
case 48:{f.push(f.locals[0],1);f.pc=49;return;}
case 49:{f.push(((dev.zprestige.sourceview.S0322)(Object)f.pop()).d,1);f.pc=50;return;}
case 50:{f.push(8196425791028638037L,2);f.pc=51;return;}
case 51:{f.push(f.locals[2],2);f.pc=52;return;}
case 52:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$38(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.push((java.lang.Boolean)(Object)f.pop(),1);f.pc=56;return;}
case 56:{f.push(8196356240038828651L,2);f.pc=57;return;}
case 57:{f.push(f.locals[2],2);f.pc=58;return;}
case 58:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$39(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=59;return;}
case 59:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=69;return;}f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.done=true;return;}
case 63:{f.pc=64;return;}
case 64:{f.pc=65;return;}
case 65:{f.push(8194558476688107618L,2);f.pc=66;return;}
case 66:{f.push(f.locals[2],2);f.pc=67;return;}
case 67:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$40(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=68;return;}
case 68:{throw (Throwable)f.pop();}
case 69:{f.pc=70;return;}
case 70:{f.pc=71;return;}
case 71:{f.pc=72;return;}
case 72:{f.push(dev.zprestige.sourceview.S0322.b,1);f.pc=73;return;}
case 73:{f.push(8195244353999525821L,2);f.pc=74;return;}
case 74:{f.push(f.locals[2],2);f.pc=75;return;}
case 75:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$41(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=76;return;}
case 76:{f.locals[7]=f.pop();f.pc=77;return;}
case 77:{f.pc=78;return;}
case 78:{f.pc=79;return;}
case 79:{f.push(f.locals[7],1);f.pc=80;return;}
case 80:{f.push(f.locals[6],1);f.pc=81;return;}
case 81:{if(f.pop()!=null){f.pc=99;return;}f.pc=82;return;}
case 82:{if(f.pop()!=null){f.pc=95;return;}f.pc=83;return;}
case 83:{f.pc=84;return;}
case 84:{f.pc=91;return;}
case 85:{f.pc=86;return;}
case 86:{f.pc=87;return;}
case 87:{f.push(8194558476688107618L,2);f.pc=88;return;}
case 88:{f.push(f.locals[2],2);f.pc=89;return;}
case 89:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$42(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=90;return;}
case 90:{throw (Throwable)f.pop();}
case 91:{f.pc=92;return;}
case 92:{f.pc=93;return;}
case 93:{f.pc=94;return;}
case 94:{f.done=true;return;}
case 95:{f.pc=96;return;}
case 96:{f.pc=97;return;}
case 97:{f.pc=98;return;}
case 98:{f.push(f.locals[7],1);f.pc=99;return;}
case 99:{f.pc=100;return;}
case 100:{f.pc=101;return;}
case 101:{f.push(f.locals[6],1);f.pc=102;return;}
case 102:{if(f.pop()!=null){f.pc=131;return;}f.pc=103;return;}
case 103:{f.pc=104;return;}
case 104:{f.push(8195690305470849577L,2);f.pc=105;return;}
case 105:{f.push(f.locals[2],2);f.pc=106;return;}
case 106:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$43(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=107;return;}
case 107:{f.push(8196266231414282270L,2);f.pc=108;return;}
case 108:{f.push(f.locals[2],2);f.pc=109;return;}
case 109:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$44(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=110;return;}
case 110:{Object b=f.pop(),a=f.pop();if(a!=b){f.pc=222;return;}f.pc=111;return;}
case 111:{f.pc=112;return;}
case 112:{f.pc=119;return;}
case 113:{f.pc=114;return;}
case 114:{f.pc=115;return;}
case 115:{f.push(8194558476688107618L,2);f.pc=116;return;}
case 116:{f.push(f.locals[2],2);f.pc=117;return;}
case 117:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$45(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=118;return;}
case 118:{throw (Throwable)f.pop();}
case 119:{f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$11$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.push(f.locals[7],1);f.pc=123;return;}
case 123:{f.pc=124;return;}
case 124:{f.pc=131;return;}
case 125:{f.pc=126;return;}
case 126:{f.pc=127;return;}
case 127:{f.push(8194558476688107618L,2);f.pc=128;return;}
case 128:{f.push(f.locals[2],2);f.pc=129;return;}
case 129:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$46(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=130;return;}
case 130:{throw (Throwable)f.pop();}
case 131:{f.pc=132;return;}
case 132:{f.pc=133;return;}
case 133:{f.push((net.minecraft.class_3966)(Object)f.pop(),1);f.pc=134;return;}
case 134:{f.push(8194850559536377164L,2);f.pc=135;return;}
case 135:{f.push(f.locals[2],2);f.pc=136;return;}
case 136:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$47(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=137;return;}
case 137:{f.locals[8]=f.pop();f.pc=138;return;}
case 138:{f.pc=139;return;}
case 139:{f.pc=140;return;}
case 140:{f.push(f.locals[8],1);f.pc=141;return;}
case 141:{f.push((f.pop() instanceof net.minecraft.class_1657?1:0),1);f.pc=142;return;}
case 142:{f.push(f.locals[6],1);f.pc=143;return;}
case 143:{if(f.pop()!=null){f.pc=193;return;}f.pc=144;return;}
case 144:{f.pc=145;return;}
case 145:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=222;return;}f.pc=146;return;}
case 146:{f.pc=147;return;}
case 147:{f.pc=154;return;}
case 148:{f.pc=149;return;}
case 149:{f.pc=150;return;}
case 150:{f.push(8194558476688107618L,2);f.pc=151;return;}
case 151:{f.push(f.locals[2],2);f.pc=152;return;}
case 152:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$48(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=153;return;}
case 153:{throw (Throwable)f.pop();}
case 154:{f.pc=155;return;}
case 155:{f.pc=156;return;}
case 156:{f.pc=157;return;}
case 157:{f.push(8194390137331846755L,2);f.pc=158;return;}
case 158:{f.push(f.locals[2],2);f.pc=159;return;}
case 159:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$49(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=160;return;}
case 160:{f.push(f.locals[8],1);f.pc=161;return;}
case 161:{f.push(8195064060024539622L,2);f.pc=162;return;}
case 162:{f.push(f.locals[2],2);f.pc=163;return;}
case 163:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$50(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=164;return;}
case 164:{f.push(8194327903809718562L,2);f.pc=165;return;}
case 165:{f.push(f.locals[2],2);f.pc=166;return;}
case 166:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$51(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=167;return;}
case 167:{f.push(f.locals[4],2);f.pc=168;return;}
case 168:{f.push(2,1);f.pc=169;return;}
case 169:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=170;return;}
case 170:{f.rearrange(91);f.pc=171;return;}
case 171:{f.rearrange(91);f.pc=172;return;}
case 172:{f.rearrange(87);f.pc=173;return;}
case 173:{Object a0=f.pop();f.push(java.lang.Long.valueOf(dev.zprestige.sourceview.FlowFrame.j(a0)),1);f.pc=174;return;}
case 174:{f.push(1,1);f.pc=175;return;}
case 175:{f.rearrange(95);f.pc=176;return;}
case 176:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=177;return;}
case 177:{f.rearrange(90);f.pc=178;return;}
case 178:{f.rearrange(95);f.pc=179;return;}
case 179:{f.push(0,1);f.pc=180;return;}
case 180:{f.rearrange(95);f.pc=181;return;}
case 181:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=182;return;}
case 182:{f.push(8196156305079643823L,2);f.pc=183;return;}
case 183:{f.push(f.locals[2],2);f.pc=184;return;}
case 184:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$52(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=185;return;}
case 185:{f.pc=186;return;}
case 186:{f.pc=193;return;}
case 187:{f.pc=188;return;}
case 188:{f.pc=189;return;}
case 189:{f.push(8194558476688107618L,2);f.pc=190;return;}
case 190:{f.push(f.locals[2],2);f.pc=191;return;}
case 191:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$53(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=192;return;}
case 192:{throw (Throwable)f.pop();}
case 193:{f.pc=194;return;}
case 194:{f.pc=195;return;}
case 195:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=222;return;}f.pc=196;return;}
case 196:{f.pc=197;return;}
case 197:{f.pc=198;return;}
case 198:{f.push(dev.zprestige.sourceview.S0322.b,1);f.pc=199;return;}
case 199:{f.push(8195357963792385068L,2);f.pc=200;return;}
case 200:{f.push(f.locals[2],2);f.pc=201;return;}
case 201:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$54(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=202;return;}
case 202:{f.push(0,1);f.pc=203;return;}
case 203:{f.push(8196310986208309962L,2);f.pc=204;return;}
case 204:{f.push(f.locals[2],2);f.pc=205;return;}
case 205:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0322.__indy$55(((java.lang.Object)(Object)(a0)),(dev.zprestige.sourceview.FlowFrame.i(a1) != 0),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=206;return;}
case 206:{f.pc=207;return;}
case 207:{f.pc=208;return;}
case 208:{f.push(f.locals[1],1);f.pc=209;return;}
case 209:{f.push(0,1);f.pc=210;return;}
case 210:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=211;return;}
case 211:{f.push(8195323915016797909L,2);f.pc=212;return;}
case 212:{f.push(f.locals[2],2);f.pc=213;return;}
case 213:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0322.__indy$56(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=214;return;}
case 214:{f.pc=215;return;}
case 215:{f.pc=222;return;}
case 216:{f.pc=217;return;}
case 217:{f.pc=218;return;}
case 218:{f.push(8194558476688107618L,2);f.pc=219;return;}
case 219:{f.push(f.locals[2],2);f.pc=220;return;}
case 220:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$57(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=221;return;}
case 221:{throw (Throwable)f.pop();}
case 222:{f.pc=223;return;}
case 223:{f.pc=224;return;}
case 224:{f.pc=225;return;}
case 225:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$12$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0322.k,2);f.pc=1;return;}
case 1:{f.push(2811546465525L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(-4378737251787668083L,2);f.pc=5;return;}
case 5:{f.push(f.locals[2],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$58(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.locals[4]=f.pop();f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{f.push(((dev.zprestige.sourceview.S0322)(Object)f.pop()).a,1);f.pc=12;return;}
case 12:{f.push(-4377829238078962732L,2);f.pc=13;return;}
case 13:{f.push(f.locals[2],2);f.pc=14;return;}
case 14:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$59(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=15;return;}
case 15:{f.push((java.lang.Boolean)(Object)f.pop(),1);f.pc=16;return;}
case 16:{f.push(-4378043341494638358L,2);f.pc=17;return;}
case 17:{f.push(f.locals[2],2);f.pc=18;return;}
case 18:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$60(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=19;return;}
case 19:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())!=0){f.pc=29;return;}f.pc=20;return;}
case 20:{f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.done=true;return;}
case 23:{f.pc=24;return;}
case 24:{f.pc=25;return;}
case 25:{f.push(-4379269254717269277L,2);f.pc=26;return;}
case 26:{f.push(f.locals[2],2);f.pc=27;return;}
case 27:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$61(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=28;return;}
case 28:{throw (Throwable)f.pop();}
case 29:{f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.push(-4379455701672411934L,2);f.pc=33;return;}
case 33:{f.push(f.locals[2],2);f.pc=34;return;}
case 34:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$62(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=35;return;}
case 35:{f.push(-4378826219376089052L,2);f.pc=36;return;}
case 36:{f.push(f.locals[2],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$63(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{f.push(-4378265911913365526L,2);f.pc=39;return;}
case 39:{f.push(f.locals[2],2);f.pc=40;return;}
case 40:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$64(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=41;return;}
case 41:{f.locals[5]=f.pop();f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.push(f.locals[5],1);f.pc=45;return;}
case 45:{f.push(-4379740145036948042L,2);f.pc=46;return;}
case 46:{f.push(f.locals[2],2);f.pc=47;return;}
case 47:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$65(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=48;return;}
case 48:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=144;return;}f.pc=49;return;}
case 49:{f.push(f.locals[5],1);f.pc=50;return;}
case 50:{f.push(-4379475638332606744L,2);f.pc=51;return;}
case 51:{f.push(f.locals[2],2);f.pc=52;return;}
case 52:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$66(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=53;return;}
case 53:{f.push((java.lang.String)(Object)f.pop(),1);f.pc=54;return;}
case 54:{f.locals[6]=f.pop();f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.pc=57;return;}
case 57:{f.push(f.locals[1],1);f.pc=58;return;}
case 58:{f.push(f.locals[4],1);f.pc=59;return;}
case 59:{if(f.pop()!=null){f.pc=132;return;}f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.push(0,1);f.pc=62;return;}
case 62:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=63;return;}
case 63:{f.push(-4377672367498972114L,2);f.pc=64;return;}
case 64:{f.push(f.locals[2],2);f.pc=65;return;}
case 65:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$67(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=66;return;}
case 66:{f.push(f.locals[6],1);f.pc=67;return;}
case 67:{f.push(-4379240500466508157L,2);f.pc=68;return;}
case 68:{f.push(f.locals[2],2);f.pc=69;return;}
case 69:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$68(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3)),1);f.pc=70;return;}
case 70:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=139;return;}f.pc=71;return;}
case 71:{f.pc=72;return;}
case 72:{f.pc=79;return;}
case 73:{f.pc=74;return;}
case 74:{f.pc=75;return;}
case 75:{f.push(-4379269254717269277L,2);f.pc=76;return;}
case 76:{f.push(f.locals[2],2);f.pc=77;return;}
case 77:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$69(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=78;return;}
case 78:{throw (Throwable)f.pop();}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.pc=82;return;}
case 82:{f.push(f.locals[1],1);f.pc=83;return;}
case 83:{f.push(f.locals[6],1);f.pc=84;return;}
case 84:{f.push(1,1);f.pc=85;return;}
case 85:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=86;return;}
case 86:{f.rearrange(90);f.pc=87;return;}
case 87:{f.rearrange(95);f.pc=88;return;}
case 88:{f.push(0,1);f.pc=89;return;}
case 89:{f.rearrange(95);f.pc=90;return;}
case 90:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=91;return;}
case 91:{f.push(-4377906328546467997L,2);f.pc=92;return;}
case 92:{f.push(f.locals[2],2);f.pc=93;return;}
case 93:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0322.__indy$70(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=94;return;}
case 94:{f.pc=95;return;}
case 95:{f.pc=96;return;}
case 96:{f.push(f.locals[1],1);f.pc=97;return;}
case 97:{f.push(-4377775680459652538L,2);f.pc=98;return;}
case 98:{f.push(f.locals[2],2);f.pc=99;return;}
case 99:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$71(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=100;return;}
case 100:{f.push(-4378295573753651112L,2);f.pc=101;return;}
case 101:{f.push(f.locals[2],2);f.pc=102;return;}
case 102:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$72(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=103;return;}
case 103:{f.push(f.locals[6],1);f.pc=104;return;}
case 104:{f.push(-4378580231197783003L,2);f.pc=105;return;}
case 105:{f.push(f.locals[2],2);f.pc=106;return;}
case 106:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$73(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=107;return;}
case 107:{f.push(-4378295573753651112L,2);f.pc=108;return;}
case 108:{f.push(f.locals[2],2);f.pc=109;return;}
case 109:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$74(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=110;return;}
case 110:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$75(((java.lang.String)(Object)(a0)),((java.lang.String)(Object)(a1)),((java.lang.String)(Object)(a2))),1);f.pc=111;return;}
case 111:{f.push(1,1);f.pc=112;return;}
case 112:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=113;return;}
case 113:{f.rearrange(90);f.pc=114;return;}
case 114:{f.rearrange(95);f.pc=115;return;}
case 115:{f.push(0,1);f.pc=116;return;}
case 116:{f.rearrange(95);f.pc=117;return;}
case 117:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((Object[])array)[index]=v;f.pc=118;return;}
case 118:{f.push(-4379623317431980367L,2);f.pc=119;return;}
case 119:{f.push(f.locals[2],2);f.pc=120;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$12$1(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 120:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0322.__indy$76(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=121;return;}
case 121:{f.pc=122;return;}
case 122:{f.pc=123;return;}
case 123:{f.push(f.locals[1],1);f.pc=124;return;}
case 124:{f.pc=125;return;}
case 125:{f.pc=132;return;}
case 126:{f.pc=127;return;}
case 127:{f.pc=128;return;}
case 128:{f.push(-4379269254717269277L,2);f.pc=129;return;}
case 129:{f.push(f.locals[2],2);f.pc=130;return;}
case 130:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0322.__indy$77(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=131;return;}
case 131:{throw (Throwable)f.pop();}
case 132:{f.pc=133;return;}
case 133:{f.pc=134;return;}
case 134:{f.push(0,1);f.pc=135;return;}
case 135:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=136;return;}
case 136:{f.push(-4378908436621538220L,2);f.pc=137;return;}
case 137:{f.push(f.locals[2],2);f.pc=138;return;}
case 138:{Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0322.__indy$78(((java.lang.Object)(Object)(a0)),((java.lang.Object)(Object)(a1)),dev.zprestige.sourceview.FlowFrame.j(a2),dev.zprestige.sourceview.FlowFrame.j(a3));f.pc=139;return;}
case 139:{f.pc=140;return;}
case 140:{f.pc=141;return;}
case 141:{f.pc=142;return;}
case 142:{f.push(f.locals[4],1);f.pc=143;return;}
case 143:{if(f.pop()==null){f.pc=42;return;}f.pc=144;return;}
case 144:{f.pc=145;return;}
case 145:{f.pc=146;return;}
case 146:{f.pc=147;return;}
case 147:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(-4043796637148319039L,2);f.pc=1;return;}
case 1:{f.push(-6717732448416354002L,2);f.pc=2;return;}
case 2:{f.push(java.lang.invoke.MethodHandles.lookup(),1);f.pc=3;return;}
case 3:{Object receiver=f.pop();f.push(((java.lang.invoke.MethodHandles.Lookup)(Object)receiver).lookupClass(),1);f.pc=4;return;}
case 4:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0549.a(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1),((java.lang.Object)(Object)(a2))),1);f.pc=5;return;}
case 5:{f.push(234046532387370L,2);f.pc=6;return;}
case 6:{Object a0=f.pop();Object receiver=f.pop();f.push(((dev.zprestige.sourceview.S0546)(Object)receiver).a(dev.zprestige.sourceview.FlowFrame.j(a0)),2);f.pc=7;return;}
case 7:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0322.class,"k",dev.zprestige.sourceview.FlowFrame.j(f.pop()));f.pc=8;return;}
case 8:{f.push(61,1);f.pc=9;return;}
case 9:{f.push(java.lang.reflect.Array.newInstance(java.lang.Object.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=10;return;}
case 10:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0322.class,"l",((java.lang.Object[])(Object)(f.pop())));f.pc=11;return;}
case 11:{f.push(61,1);f.pc=12;return;}
case 12:{f.push(java.lang.reflect.Array.newInstance(java.lang.String.class,dev.zprestige.sourceview.FlowFrame.i(f.pop())),1);f.pc=13;return;}
case 13:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0322.class,"m",((java.lang.String[])(Object)(f.pop())));f.pc=14;return;}
case 14:{dev.zprestige.sourceview.S0322.f();f.pc=15;return;}
case 15:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
