package dev.zprestige.sourceview;

import java.util.ArrayList;
import java.util.Vector;

public class S0549 implements S0546 {
   public long a;
   public int[] b;
   public S0546 c;
   public long d;
   public long[] j;
   public static long[] k;
   public static int[] e;
   public static ArrayList f;
   public static Vector m;
   public static int i;
   public static Object o;
   public static int h;
   public static int l;
   public static int n;

   public S0549(long var1)  throws Throwable {
try {

      this.a = var1;
      this.b = e;
      this.j = k;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   private static void __initializeClass() throws Throwable {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(3,4);
while(!f.done){try{switch(f.pc/120){
case 0:__flow$1$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


   @Override
   public boolean equals(Object var1) {
try {

      if (this == var1) {
         return true;
      } else {
         return var1 instanceof S0549 ? this.a(56) == ((S0549)(Object)(var1)).a(56) : false;
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public int hashCode() {
try {

      return (int)this.a(8);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public int[] b$return$12d40f() {
try {

      return this.b;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public void b(long var1) {
try {

      this.d = var1;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void b(S0548 var0) {
try {

      c$return$9b75();
      int[] var10000 = e = new int[64];
      var10000[0] = -42;
      var10000[1] = -29;
      var10000[2] = -9;
      var10000[3] = -29;
      var10000[4] = -20;
      var10000[5] = -12;
      var10000[6] = -40;
      var10000[7] = -21;
      var10000[8] = -43;
      var10000[9] = -38;
      var10000[10] = -33;
      var10000[11] = 9;
      var10000[12] = -41;
      var10000[13] = -47;
      var10000[14] = -15;
      var10000[15] = -5;
      var10000[16] = -42;
      var10000[17] = 12;
      var10000[18] = -38;
      var10000[19] = -18;
      var10000[20] = 5;
      var10000[21] = -40;
      var10000[22] = -33;
      var10000[23] = -4;
      var10000[24] = 20;
      var10000[25] = -23;
      var10000[26] = -9;
      var10000[27] = 4;
      var10000[28] = 21;
      var10000[29] = 15;
      var10000[30] = 29;
      var10000[31] = -31;
      var10000[32] = 29;
      var10000[33] = -17;
      var10000[34] = -15;
      var10000[35] = 9;
      var10000[36] = -5;
      var10000[37] = 18;
      var10000[38] = -25;
      var10000[39] = -20;
      var10000[40] = -5;
      var10000[41] = 5;
      var10000[42] = 42;
      var10000[43] = 33;
      var10000[44] = -13;
      var10000[45] = 5;
      var10000[46] = 40;
      var10000[47] = 38;
      var10000[48] = 23;
      var10000[49] = 15;
      var10000[50] = 17;
      var10000[51] = 43;
      var10000[52] = -2;
      var10000[53] = 41;
      var10000[54] = 2;
      var10000[55] = 33;
      var10000[56] = 38;
      var10000[57] = 13;
      var10000[58] = 42;
      var10000[59] = 20;
      var10000[60] = 47;
      var10000[61] = 40;
      var10000[62] = 31;
      var10000[63] = 25;
      var0.c$return$9b75();
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public boolean b(S0546 var1) {
try {

      if (this == var1) {
         return true;
      } else {
         return var1 instanceof S0549 ? this.a(56, 63) - ((S0549)(Object)(var1)).a(56, 63) <= 0L : true;
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void c$return$9b75() {
try {

      byte var0 = 0;
      a(0, f.size() - 1, f, new ArrayList(f), var0);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0546 c(long var0) {
try {

      int var2 = (int)a(var0, h, 63, e, k);
      if (var2 < i) {
         return (S0546)(Object)(f.get(var2));
      }

      if (f.size() % l == 0) {
         e = (int[])(Object)(e.clone());
      }

      S0549 var3 = new S0549(var0);
      f.add(var3);
      return var3;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0546 d(long var0) {
try {

      return new S0549(var0);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public long a(int var1, int var2) {
try {

      return a(this.a, var1, var2, this.b, this.j);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static long a(long var0, int var2, int var3, int[] var4, long[] var5) {
try {

      long var6 = 0L;
      int var8 = var4.length;

      for (int var9 = 0; var9 < var8; var9++) {
         long var10 = var0 & k[var9];
         long var12 = (long)var4[var9];
         if (var10 != 0L) {
            if (var12 > 0) {
               var10 >>>= var12;
            } else if (var12 < 0) {
               var10 <<= ~var12 + 1;
            }

            var6 |= var10;
         }
      }

      byte var13 = 64;
      long var14 = var6;
      long var11 = (long)(var13 - 1 - var3);
      if (var11 > 0) {
         var14 <<= var11;
      }

      long var15 = (long)(var2 + var13 - 1 - var3);
      if (var15 > 0) {
         var14 >>>= var15;
      }

      return var14;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a(int var0, int var1, ArrayList var2, ArrayList var3, int var4) {
try {

      if (var0 < var1) {
         int var5 = var0 + (var1 - var0) / 2;
         if (++var4 < n) {
            a(var0, var5, var2, var3, var4);
            a(var5 + 1, var1, var2, var3, var4);
         }

         a(var0, var5, var1, var2, var3);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a(int var0, int var1, int var2, ArrayList var3, ArrayList var4) {
try {

      int var5 = var0;
      int var6 = var1 + 1;

      for (int var7 = var0; var7 <= var2; var7++) {
         var4.set(var7, var3.get(var7));
      }

      while (var5 <= var1 && var6 <= var2) {
         S0546 var8;
         if (((S0546)(Object)(var4.get(var5))).b((S0546)(Object)(var4.get(var6)))) {
            var8 = (S0546)(Object)(var4.get(var5++));
         } else {
            var8 = (S0546)(Object)(var4.get(var6++));
         }

         var3.set(var0, var8);
         var0++;
      }

      while (var5 <= var1) {
         var3.set(var0, var4.get(var5));
         var0++;
         var5++;
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public long a(long var1) {
try {

      long var3 = this.a(8, 55);
      long var5 = this.a ^ var1 ^ this.d;
      this.a = var5;
      if (this.c != null) {
         this.c.a(var1);
      }

      return var3;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public void a(S0546 var1) {
try {

      if (this != var1) {
         if (this.c == null) {
            this.c = var1;
         } else {
            this.c.a(var1);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static S0546 a(long var0, long var2, Object var4) {
try {

      S0548.a(var0 > 0L);
      S0546 var5 = d(var0);
      S0546 var6 = d(var2);
      S0546 var7 = S0548.a(var5, var6);
      if (var4 != null) {
         m.add(var4);
      }

      return var7;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a(S0548 var0) {
try {

      i = f.size();
      c$return$9b75();
      var0.d();
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public long a(int var1) {
try {

      return this.a(0, var1 - 1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static Object g() {
try {

      return o;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   public static void a0() {
try {

      e = new int[]{
         -33,
         -55,
         -25,
         -8,
         -43,
         -25,
         -1,
         1,
         -1,
         1,
         -24,
         8,
         -34,
         -36,
         -21,
         -1,
         1,
         -36,
         -18,
         -33,
         -35,
         -22,
         -38,
         -25,
         -30,
         -38,
         -6,
         25,
         -13,
         -32,
         25,
         -28,
         6,
         33,
         24,
         21,
         18,
         -3,
         -24,
         -6,
         3,
         13,
         -8,
         22,
         -13,
         6,
         34,
         43,
         25,
         36,
         8,
         -7,
         33,
         36,
         30,
         35,
         55,
         13,
         7,
         28,
         38,
         32,
         24,
         38
      };
      f.add(new S0549(-7649176990319478997L));
      f.add(new S0549(6021278898969724677L));
      f.add(new S0549(7115027366131081075L));
      f.add(new S0549(-7651428517436295381L));
      f.add(new S0549(6062377975152644802L));
      f.add(new S0549(2243170933948983133L));
      f.add(new S0549(-4094356763992397963L));
      f.add(new S0549(-1107148554846165954L));
      f.add(new S0549(-1286430344283762525L));
      f.add(new S0549(-1831038926418664309L));
      f.add(new S0549(-6354719162614405529L));
      f.add(new S0549(-8506449974601101173L));
      f.add(new S0549(8633968285251542579L));
      f.add(new S0549(-6249007606196702135L));
      f.add(new S0549(-7188844375264876873L));
      f.add(new S0549(-4345092557189540506L));
      f.add(new S0549(-2632335675495490175L));
      f.add(new S0549(-1243545347136972287L));
      f.add(new S0549(-3523611573846561655L));
      f.add(new S0549(6935692940177033288L));
      f.add(new S0549(-4858100291223264181L));
      f.add(new S0549(-2450504512435145929L));
      f.add(new S0549(713245897628964038L));
      f.add(new S0549(-470162520600120837L));
      f.add(new S0549(-7261871967915938009L));
      f.add(new S0549(9112052544143724587L));
      f.add(new S0549(-741517486873938593L));
      f.add(new S0549(8274905796648157430L));
      f.add(new S0549(8182818563061477568L));
      f.add(new S0549(683098719807329197L));
      f.add(new S0549(-1931611121395223946L));
      f.add(new S0549(-2936086504366924589L));
      f.add(new S0549(8508012099847734425L));
      f.add(new S0549(-3051339683445750871L));
      f.add(new S0549(7823862609310475825L));
      f.add(new S0549(-630333041359874273L));
      f.add(new S0549(-4294871533233534140L));
      f.add(new S0549(1665642233147838456L));
      f.add(new S0549(-914499574569628890L));
      f.add(new S0549(-8587818986200196448L));
      f.add(new S0549(802182560945210048L));
      f.add(new S0549(8117652667534771398L));
      f.add(new S0549(5102926607849366974L));
      f.add(new S0549(-4642530281704379163L));
      f.add(new S0549(-2314903740319445028L));
      f.add(new S0549(-8196762515845565991L));
      f.add(new S0549(488425702816181875L));
      f.add(new S0549(-7744709769461338676L));
      f.add(new S0549(-1384914714294381625L));
      f.add(new S0549(6376193074664079881L));
      f.add(new S0549(7655649855320857536L));
      f.add(new S0549(-6040061248367809216L));
      f.add(new S0549(-8070919431904585849L));
      f.add(new S0549(-4917252096074680760L));
      f.add(new S0549(-3778892574057899659L));
      f.add(new S0549(3156297505560575669L));
      f.add(new S0549(-481657315393033657L));
      f.add(new S0549(-5834579944880358322L));
      f.add(new S0549(-6733363871950849573L));
      f.add(new S0549(1900324777150013938L));
      f.add(new S0549(-98767447440803693L));
      f.add(new S0549(4984646333241370078L));
      f.add(new S0549(1386049793193599528L));
      f.add(new S0549(6546454295836122586L));
      e = (int[])(Object)(e.clone());
      f.add(new S0549(598505703703054076L));
      f.add(new S0549(-6788878158731728931L));
      f.add(new S0549(-7615399728562849056L));
      f.add(new S0549(5412278113605174787L));
      f.add(new S0549(3876012969123456412L));
      f.add(new S0549(2556530039102354286L));
      f.add(new S0549(7114219646074815386L));
      f.add(new S0549(1319545010852995849L));
      f.add(new S0549(-3181654918938933361L));
      f.add(new S0549(-8040568353090958328L));
      f.add(new S0549(436836193471832771L));
      f.add(new S0549(1801225361431303141L));
      f.add(new S0549(-8527082700926010378L));
      f.add(new S0549(7105411707124131576L));
      f.add(new S0549(-2401476458321484200L));
      f.add(new S0549(4698931183202614429L));
      f.add(new S0549(939920379903731653L));
      f.add(new S0549(2850135864196478584L));
      f.add(new S0549(7950866530573663355L));
      f.add(new S0549(1624806897574438667L));
      f.add(new S0549(2984381551225378849L));
      f.add(new S0549(-6525414702637831360L));
      f.add(new S0549(7856637914043130739L));
      f.add(new S0549(-71219325941204838L));
      f.add(new S0549(-3423773354536227450L));
      f.add(new S0549(4038897098582724368L));
      f.add(new S0549(-5481139332747355726L));
      f.add(new S0549(-7268684146465480007L));
      f.add(new S0549(-424467516851135237L));
      f.add(new S0549(-6163682273045483688L));
      f.add(new S0549(-829591249395319264L));
      f.add(new S0549(-6552711216695385862L));
      f.add(new S0549(-4010836276203184759L));
      f.add(new S0549(1912939921244024221L));
      f.add(new S0549(1464061502064492756L));
      f.add(new S0549(7211103416390488433L));
      f.add(new S0549(-6812485095391364001L));
      f.add(new S0549(7516102967173829627L));
      f.add(new S0549(2253214245848895883L));
      f.add(new S0549(3501659090213013028L));
      f.add(new S0549(-4955735216145303780L));
      f.add(new S0549(4668281060420621632L));
      f.add(new S0549(4559816488748107966L));
      f.add(new S0549(-583459211740016166L));
      f.add(new S0549(-5601445603293499194L));
      f.add(new S0549(6728168624509042648L));
      f.add(new S0549(-2200880934729577809L));
      f.add(new S0549(314220719538636150L));
      f.add(new S0549(7585486904458493985L));
      f.add(new S0549(7160671427046692518L));
      f.add(new S0549(-2548128555449861275L));
      f.add(new S0549(9169620906008940582L));
      f.add(new S0549(2220623140383037870L));
      f.add(new S0549(-8255853930542279380L));
      f.add(new S0549(-9133093825005027372L));
      f.add(new S0549(-2619394491688261731L));
      f.add(new S0549(-5731627583554575565L));
      f.add(new S0549(-3104788460067762055L));
      f.add(new S0549(-8631057032720844289L));
      f.add(new S0549(-3385516179611325473L));
      f.add(new S0549(480030360162921353L));
      f.add(new S0549(6039286667926823083L));
      f.add(new S0549(8084756544564434374L));
      f.add(new S0549(7840685803402984610L));
      f.add(new S0549(6652567368545854410L));
      f.add(new S0549(3761748448922613992L));
      f.add(new S0549(-2875716036569566065L));
      f.add(new S0549(7514968061014522184L));
      f.add(new S0549(8046063563255234393L));
      f.add(new S0549(-7138091896967431311L));
      f.add(new S0549(7013345211789515738L));
      f.add(new S0549(-3419900200944686621L));
      f.add(new S0549(-1790361784662263329L));
      f.add(new S0549(-7520036821077760811L));
      f.add(new S0549(4571498940008874769L));
      f.add(new S0549(-6301920718176892225L));
      f.add(new S0549(6847852723986915512L));
      f.add(new S0549(-8871120406912112542L));
      f.add(new S0549(8704261413817878067L));
      f.add(new S0549(-3003163276626445084L));
      f.add(new S0549(-5001333279867624603L));
      f.add(new S0549(-1804155245918331457L));
      f.add(new S0549(7998271564227728631L));
      f.add(new S0549(2965203111727702834L));
      f.add(new S0549(-1429584554936332603L));
      f.add(new S0549(9120542136657028315L));
      f.add(new S0549(7314805189062818572L));
      f.add(new S0549(565362480503707533L));
      f.add(new S0549(2637788516199297132L));
      f.add(new S0549(3795287927194643303L));
      f.add(new S0549(-5081752671471368731L));
      f.add(new S0549(2100195983037871765L));
      f.add(new S0549(8914697494315747397L));
      f.add(new S0549(-8645312619684117312L));
      f.add(new S0549(-6921559395901636935L));
      f.add(new S0549(-7858487034989571839L));
      f.add(new S0549(-3473413541210276334L));
      f.add(new S0549(-8620318942608024785L));
      f.add(new S0549(-3009680880449566834L));
      f.add(new S0549(4827410162002474661L));
      f.add(new S0549(-6648596759026214636L));
      f.add(new S0549(2447686181178206431L));
      f.add(new S0549(3307052928172701441L));
      f.add(new S0549(-8449655312355517012L));
      f.add(new S0549(517118881723393964L));
      f.add(new S0549(1410799239327452363L));
      f.add(new S0549(-4919438492050825310L));
      f.add(new S0549(3840204540650184027L));
      f.add(new S0549(7209964992474337491L));
      f.add(new S0549(1036349358075585583L));
      f.add(new S0549(6790989864654878992L));
      f.add(new S0549(7013345212322192346L));
      f.add(new S0549(3895912108304790012L));
      f.add(new S0549(-4029791209564322295L));
      f.add(new S0549(5933335750904547587L));
      f.add(new S0549(-3123819758615211174L));
      f.add(new S0549(-349522063931870891L));
      f.add(new S0549(2478831087730658872L));
      f.add(new S0549(753531996277914732L));
      f.add(new S0549(5906584303072952396L));
      f.add(new S0549(4450735810476265692L));
      f.add(new S0549(3521849621924305754L));
      f.add(new S0549(-3345912550615431478L));
      f.add(new S0549(-5696675560956383113L));
      f.add(new S0549(-5803350423011154371L));
      f.add(new S0549(-1391315349120264828L));
      f.add(new S0549(6060990998616473848L));
      f.add(new S0549(-4608273948488772909L));
      f.add(new S0549(-7536452400372430142L));
      f.add(new S0549(-6613449593881108506L));
      f.add(new S0549(293304533072705787L));
      f.add(new S0549(2573834056112886171L));
      f.add(new S0549(-7490694996649590109L));
      f.add(new S0549(-7329261363439713554L));
      f.add(new S0549(107078086729902191L));
      f.add(new S0549(2335789328028052309L));
      f.add(new S0549(-282894071089477983L));
      f.add(new S0549(-7727019617399716639L));
      f.add(new S0549(-2099843059773517341L));
      f.add(new S0549(-7484670715823761952L));
      f.add(new S0549(362424616834316660L));
      f.add(new S0549(4559276986041958463L));
      f.add(new S0549(-3492797716540693541L));
      f.add(new S0549(3735841482398294446L));
      f.add(new S0549(2546247923713449332L));
      f.add(new S0549(7508822172419760337L));
      e = (int[])(Object)(e.clone());
      f.add(new S0549(-4507573312931593981L));
      f.add(new S0549(4969688108754446360L));
      f.add(new S0549(-7327447264897149503L));
      f.add(new S0549(-8096040726734255061L));
      f.add(new S0549(3192865056809656223L));
      f.add(new S0549(724386031136409549L));
      f.add(new S0549(-4328348439121582172L));
      f.add(new S0549(4044404171858881298L));
      f.add(new S0549(-4562651201026727673L));
      f.add(new S0549(-412568846541351614L));
      f.add(new S0549(-8884997054237220797L));
      f.add(new S0549(-5290813164180535145L));
      f.add(new S0549(969152962983353066L));
      f.add(new S0549(-1479276402428381667L));
      f.add(new S0549(3095460421324039010L));
      f.add(new S0549(-1741233852586857765L));
      f.add(new S0549(-6449438365641848396L));
      f.add(new S0549(-6911484174626547151L));
      f.add(new S0549(-7538052100831702449L));
      f.add(new S0549(-3736586654999969262L));
      f.add(new S0549(-228127507834083523L));
      f.add(new S0549(2964522404659409421L));
      f.add(new S0549(-5401466814156212649L));
      f.add(new S0549(4531987010705171481L));
      f.add(new S0549(-8681760582742267861L));
      f.add(new S0549(-550212750669905301L));
      f.add(new S0549(8485470948937086188L));
      f.add(new S0549(392497127337159576L));
      f.add(new S0549(1220601375613365252L));
      f.add(new S0549(6813717532953358307L));
      f.add(new S0549(5904279722566813125L));
      f.add(new S0549(-3251071057764477494L));
      f.add(new S0549(6764064667525819760L));
      f.add(new S0549(-6550736302283523773L));
      f.add(new S0549(1418700438429957847L));
      f.add(new S0549(-1963393459583863107L));
      f.add(new S0549(-3819149580893653667L));
      f.add(new S0549(-566714325194679909L));
      f.add(new S0549(7163264590829405160L));
      f.add(new S0549(7189741961086549003L));
      f.add(new S0549(-726759380906604024L));
      f.add(new S0549(8651176871732178519L));
      f.add(new S0549(5591912164137918826L));
      f.add(new S0549(8962426294288102163L));
      f.add(new S0549(533259435991812287L));
      f.add(new S0549(-4889101532730600864L));
      f.add(new S0549(4654419175048418499L));
      f.add(new S0549(-7754839692875981863L));
      f.add(new S0549(-6490793937330822568L));
      f.add(new S0549(1779556679324156225L));
      f.add(new S0549(-886376610571498787L));
      f.add(new S0549(-239099577974482963L));
      f.add(new S0549(-2189039278564277722L));
      f.add(new S0549(2682756190070748239L));
      f.add(new S0549(-7528036009271097703L));
      f.add(new S0549(-2844856053560503144L));
      f.add(new S0549(4164976268298949625L));
      f.add(new S0549(4224194717724061635L));
      f.add(new S0549(-8903858860338734213L));
      f.add(new S0549(-9182452504025957095L));
      f.add(new S0549(5722204980547692226L));
      f.add(new S0549(-8415979233145353894L));
      f.add(new S0549(5632274728948935092L));
      f.add(new S0549(1701073020487537599L));
      f.add(new S0549(-3446584507993455726L));
      f.add(new S0549(-8759840797971617519L));
      f.add(new S0549(6274238678108271039L));
      f.add(new S0549(3019857854548910541L));
      f.add(new S0549(-2579755742048294136L));
      f.add(new S0549(6178400002013856079L));
      f.add(new S0549(8136924942375159791L));
      f.add(new S0549(7908235846183619198L));
      f.add(new S0549(-1421870622184207720L));
      f.add(new S0549(1129436866060775448L));
      f.add(new S0549(-7174273600719475041L));
      f.add(new S0549(6365430048010859300L));
      f.add(new S0549(4335967243742614086L));
      f.add(new S0549(7178116539055578143L));
      f.add(new S0549(-3876667482938256636L));
      f.add(new S0549(1762237054424866060L));
      f.add(new S0549(5168951482988790105L));
      f.add(new S0549(3534917506846842045L));
      f.add(new S0549(-5210238126239421448L));
      f.add(new S0549(-376715362765793929L));
      f.add(new S0549(-8205928572130504419L));
      f.add(new S0549(-1701579417749604138L));
      f.add(new S0549(-490584826277096727L));
      f.add(new S0549(-5483618421257560661L));
      f.add(new S0549(-155714947325897395L));
      f.add(new S0549(447825010347655123L));
      f.add(new S0549(7785860798578226872L));
      f.add(new S0549(-6858601882960796241L));
      f.add(new S0549(-8434789379110081313L));
      f.add(new S0549(3250345891831838315L));
      f.add(new S0549(-2243473517525509944L));
      f.add(new S0549(238483228380432187L));
      f.add(new S0549(7598384277512142590L));
      f.add(new S0549(7972894426536829483L));
      f.add(new S0549(-8577407054518260091L));
      f.add(new S0549(-8509938871972282608L));
      f.add(new S0549(8208526115894662137L));
      f.add(new S0549(-9080651589604252519L));
      f.add(new S0549(5762029147522677143L));
      f.add(new S0549(-7206761513318781507L));
      f.add(new S0549(149021489830997631L));
      f.add(new S0549(4221060753902180172L));
      f.add(new S0549(346917538632320944L));
      f.add(new S0549(2900533090829468808L));
      f.add(new S0549(1351562871504462918L));
      f.add(new S0549(3003253653435103343L));
      f.add(new S0549(-2802318660257690500L));
      f.add(new S0549(-8140978134261112558L));
      f.add(new S0549(-9000428116156804155L));
      f.add(new S0549(-7766926974756299131L));
      f.add(new S0549(-3666394438566047388L));
      f.add(new S0549(-1925754878210956931L));
      f.add(new S0549(-4731318957191578091L));
      f.add(new S0549(-5640193736764572020L));
      f.add(new S0549(5012151065066929557L));
      f.add(new S0549(-6772790365730791238L));
      f.add(new S0549(-1877919425177077902L));
      f.add(new S0549(6838707680855638647L));
      f.add(new S0549(3707218867313986359L));
      f.add(new S0549(5334697164688405229L));
      f.add(new S0549(2209768819642973280L));
      f.add(new S0549(1747691013822565278L));
      f.add(new S0549(3707218867276237623L));
      f.add(new S0549(14853951359637924L));
      f.add(new S0549(5500920932239751725L));
      f.add(new S0549(3584996334702698606L));
      f.add(new S0549(1293401758081391198L));
      f.add(new S0549(-2618821376564613293L));
      f.add(new S0549(4946019319576176285L));
      f.add(new S0549(-2565718076225246616L));
      f.add(new S0549(-5823445943452878704L));
      f.add(new S0549(-1843343212271465827L));
      f.add(new S0549(-5571243344248681403L));
      f.add(new S0549(-3426042744963093114L));
      f.add(new S0549(-3227678706738354621L));
      f.add(new S0549(4127432454445875651L));
      f.add(new S0549(-7257574875923685255L));
      f.add(new S0549(9184072969325197538L));
      f.add(new S0549(-7487014796638551434L));
      f.add(new S0549(2852604758792850905L));
      f.add(new S0549(1628345351610981302L));
      f.add(new S0549(-7858840903612052111L));
      f.add(new S0549(3152497757318344037L));
      f.add(new S0549(5640637659136476993L));
      f.add(new S0549(8024918156175388807L));
      f.add(new S0549(902279764272780834L));
      f.add(new S0549(4162368278033845103L));
      e = (int[])(Object)(e.clone());
      f.add(new S0549(9035885924355795442L));
      f.add(new S0549(-2383892320639031841L));
      f.add(new S0549(-7968927618843480297L));
      f.add(new S0549(1819601608400353300L));
      f.add(new S0549(7109669670800431397L));
      f.add(new S0549(5475312939540424170L));
      f.add(new S0549(-4977187420753839346L));
      f.add(new S0549(-807121908856456310L));
      f.add(new S0549(1100529213284449858L));
      f.add(new S0549(8619457328723057101L));
      f.add(new S0549(854120386567158670L));
      f.add(new S0549(8802137452414066739L));
      f.add(new S0549(-5481366347102839383L));
      f.add(new S0549(7847371306412183404L));
      f.add(new S0549(741913688461880527L));
      f.add(new S0549(-7757153689159361350L));
      f.add(new S0549(-6892605942376367479L));
      f.add(new S0549(7995361466660913872L));
      f.add(new S0549(6649665343611064195L));
      f.add(new S0549(-7876275862232681429L));
      f.add(new S0549(-2561124447871992606L));
      f.add(new S0549(5986368074398878947L));
      f.add(new S0549(8846186829863880020L));
      f.add(new S0549(7669519456527987048L));
      f.add(new S0549(-5798699426951820081L));
      f.add(new S0549(-547765375561617929L));
      f.add(new S0549(-3734352445229039086L));
      f.add(new S0549(4148504748129536577L));
      f.add(new S0549(-4953796719980899236L));
      f.add(new S0549(-7034128960803392775L));
      f.add(new S0549(1863630688749393656L));
      f.add(new S0549(-7784651855807078391L));
      f.add(new S0549(-7485561786949184877L));
      f.add(new S0549(7912092214529251083L));
      f.add(new S0549(-9175052361318473424L));
      f.add(new S0549(5033335044920531374L));
      f.add(new S0549(145428828407112855L));
      f.add(new S0549(706459091942160412L));
      f.add(new S0549(6101851233520824852L));
      f.add(new S0549(7818286517929283394L));
      f.add(new S0549(-2031543907312097190L));
      f.add(new S0549(-8519949837384812223L));
      f.add(new S0549(-6741447927674274290L));
      f.add(new S0549(-1008823245031301532L));
      f.add(new S0549(147964057019424140L));
      f.add(new S0549(-6662652089804892848L));
      f.add(new S0549(7885223008856918985L));
      f.add(new S0549(-7694260132411185701L));
      f.add(new S0549(-1829830024333489653L));
      f.add(new S0549(6802132255692503247L));
      f.add(new S0549(-6403308769558910047L));
      f.add(new S0549(-8228962538177585033L));
      f.add(new S0549(8275181439206457694L));
      f.add(new S0549(4974389503511956982L));
      f.add(new S0549(9166104389268944944L));
      f.add(new S0549(-8230377356994822848L));
      f.add(new S0549(544128274098877330L));
      f.add(new S0549(4925135953141930003L));
      f.add(new S0549(-1132776994769930150L));
      f.add(new S0549(3560607613155094804L));
      f.add(new S0549(6259024284252470999L));
      f.add(new S0549(2310030550478356473L));
      f.add(new S0549(5769554425761096831L));
      f.add(new S0549(3840182987297821556L));
      f.add(new S0549(6052274730067885821L));
      f.add(new S0549(4360619511364215196L));
      f.add(new S0549(552732582140815384L));
      f.add(new S0549(7469209389182829210L));
      f.add(new S0549(3223174063826680129L));
      f.add(new S0549(-9058735539716290016L));
      f.add(new S0549(4432820030474291616L));
      f.add(new S0549(5295910778554173854L));
      f.add(new S0549(-2029076850390525924L));
      f.add(new S0549(-1614626581330337385L));
      f.add(new S0549(8396678466604906004L));
      f.add(new S0549(510785927279739985L));
      f.add(new S0549(1824007638767086727L));
      f.add(new S0549(1612105165281563045L));
      f.add(new S0549(7490253072091146579L));
      f.add(new S0549(-3963602946598801208L));
      f.add(new S0549(-3236490049851622210L));
      f.add(new S0549(-3457667714076108306L));
      f.add(new S0549(7154472939818439907L));
      f.add(new S0549(-3285046754612840325L));
      f.add(new S0549(-1958129843801226489L));
      f.add(new S0549(-2440894942280872083L));
      f.add(new S0549(6717516198193632873L));
      f.add(new S0549(-3671788272970387124L));
      f.add(new S0549(-4724946842410074629L));
      f.add(new S0549(-8126390047296113604L));
      f.add(new S0549(-8642214516611679845L));
      f.add(new S0549(-687342861358744318L));
      f.add(new S0549(2112303941230088962L));
      f.add(new S0549(-3955396209230230514L));
      f.add(new S0549(51672895699272424L));
      f.add(new S0549(8198065535924326222L));
      f.add(new S0549(2268214940211254591L));
      f.add(new S0549(-3302476877391067853L));
      f.add(new S0549(-390890878898933521L));
      f.add(new S0549(-5823247906111658340L));
      f.add(new S0549(7677069175059439791L));
      f.add(new S0549(-743379054980606337L));
      f.add(new S0549(2714782591602420950L));
      f.add(new S0549(3162505932962500600L));
      f.add(new S0549(67020481479601040L));
      f.add(new S0549(1508705572514919840L));
      f.add(new S0549(6809845283582499616L));
      f.add(new S0549(-4673100068722590363L));
      f.add(new S0549(-4845198820828080079L));
      f.add(new S0549(6698231729282425649L));
      f.add(new S0549(2416159116879224374L));
      f.add(new S0549(1197337001695656654L));
      f.add(new S0549(-6311080791365363670L));
      f.add(new S0549(-400144637383126864L));
      f.add(new S0549(-9210186395272016963L));
      f.add(new S0549(8344751206387992903L));
      f.add(new S0549(3902936370882686556L));
      f.add(new S0549(5382945887048254474L));
      f.add(new S0549(4999909663385772064L));
      f.add(new S0549(-511057243617691169L));
      f.add(new S0549(2749242909607077890L));
      f.add(new S0549(-1415087044807206189L));
      f.add(new S0549(2135504652165291765L));
      f.add(new S0549(697363811122641924L));
      f.add(new S0549(2257982372647910656L));
      f.add(new S0549(-3709075209219025935L));
      f.add(new S0549(5322646016101479201L));
      f.add(new S0549(274127640865488684L));
      f.add(new S0549(-4077796808440698927L));
      f.add(new S0549(-5081946843548307785L));
      f.add(new S0549(-4325439648359611519L));
      f.add(new S0549(-4526624045412905775L));
      f.add(new S0549(5578796072761222230L));
      f.add(new S0549(-2051157482484143167L));
      f.add(new S0549(-2609734439402827141L));
      f.add(new S0549(2837216223228577452L));
      f.add(new S0549(5180226259353583937L));
      f.add(new S0549(-8992894653239729604L));
      f.add(new S0549(-1455606636788764008L));
      f.add(new S0549(-1284143922600941688L));
      f.add(new S0549(868978258659498271L));
      f.add(new S0549(-6215175181676027251L));
      f.add(new S0549(-2509648422241305684L));
      f.add(new S0549(-1821244077372800426L));
      f.add(new S0549(3718575180984075362L));
      f.add(new S0549(6845583331949437112L));
      f.add(new S0549(-5042750171742278578L));
      f.add(new S0549(-8585425014297291472L));
      f.add(new S0549(-8655874187941514161L));
      f.add(new S0549(6346110152718083926L));
      f.add(new S0549(5153715718038374590L));
      f.add(new S0549(4774123094208051593L));
      f.add(new S0549(2834541042014381412L));
      f.add(new S0549(-3582181015230097655L));
      f.add(new S0549(-1019396610755716008L));
      f.add(new S0549(-5846814379084437544L));
      f.add(new S0549(-1010277375771873799L));
      f.add(new S0549(2269795881230703218L));
      f.add(new S0549(-7062056896997212705L));
      f.add(new S0549(-1679697373161939447L));
      f.add(new S0549(-2245725042427733816L));
      f.add(new S0549(-5796899720068075298L));
      f.add(new S0549(-760475274441803744L));
      f.add(new S0549(1009521602034960175L));
      f.add(new S0549(-6919916631721824128L));
      f.add(new S0549(8714001708050828496L));
      f.add(new S0549(-6234963159224780552L));
      f.add(new S0549(-8708291627771801059L));
      f.add(new S0549(-2387869161932670525L));
      f.add(new S0549(-6649885403994102489L));
      f.add(new S0549(-6003267671257199589L));
      e = (int[])(Object)(e.clone());
      f.add(new S0549(-7440526568183905889L));
      f.add(new S0549(6156758668110287452L));
      f.add(new S0549(-2180869133918455012L));
      f.add(new S0549(-4897876226090702751L));
      f.add(new S0549(-6203943409593918500L));
      f.add(new S0549(-7956029911936723963L));
      f.add(new S0549(-4894248378385632503L));
      f.add(new S0549(-5035664644857577919L));
      f.add(new S0549(-316275458786100225L));
      f.add(new S0549(7247428582350608130L));
      f.add(new S0549(-6876090141930314800L));
      f.add(new S0549(1382098645648098426L));
      f.add(new S0549(-5517584460492084528L));
      f.add(new S0549(4209862795273782085L));
      f.add(new S0549(1148486162738993508L));
      f.add(new S0549(2537911749232126327L));
      f.add(new S0549(679489308090067613L));
      f.add(new S0549(5447176630578314611L));
      f.add(new S0549(3181049237446743148L));
      f.add(new S0549(-8688476427300017079L));
      f.add(new S0549(-5298271687525067686L));
      f.add(new S0549(4270056678766172429L));
      f.add(new S0549(2945008548930711460L));
      f.add(new S0549(7911230163538260109L));
      f.add(new S0549(-2599258955774664346L));
      f.add(new S0549(-5394707107938652861L));
      f.add(new S0549(-2428099541409323017L));
      f.add(new S0549(-6720260663785708036L));
      f.add(new S0549(6937663518819585567L));
      f.add(new S0549(3898066813745748459L));
      f.add(new S0549(-3837544784039908015L));
      f.add(new S0549(7831599176526910745L));
      f.add(new S0549(5897909866754633760L));
      f.add(new S0549(2812739633753263226L));
      f.add(new S0549(-4259278317348770596L));
      f.add(new S0549(4316306091048109095L));
      f.add(new S0549(-6542169212731619922L));
      f.add(new S0549(3870792203987504056L));
      f.add(new S0549(-5644960290693587352L));
      f.add(new S0549(1495819553149533510L));
      f.add(new S0549(-1676049854900666518L));
      f.add(new S0549(5026770972139786519L));
      f.add(new S0549(-4432860700731047657L));
      f.add(new S0549(-404452542131410348L));
      f.add(new S0549(6167850145663711516L));
      f.add(new S0549(-7161259033177445761L));
      f.add(new S0549(-7935331784882986385L));
      f.add(new S0549(6650308208397998438L));
      f.add(new S0549(-3880908349645617801L));
      f.add(new S0549(-8050948936463234364L));
      f.add(new S0549(576550016234137024L));
      f.add(new S0549(-2142488216229812598L));
      f.add(new S0549(8803635047380366296L));
      f.add(new S0549(-7442963949812401493L));
      f.add(new S0549(-6221256135674372324L));
      f.add(new S0549(1987074826707156729L));
      f.add(new S0549(1262692516382253483L));
      f.add(new S0549(1256630894800980638L));
      f.add(new S0549(-5244262098849063481L));
      f.add(new S0549(1889117797242472467L));
      f.add(new S0549(3828015087508153307L));
      f.add(new S0549(7567945637854714064L));
      f.add(new S0549(-3233151798691820529L));
      f.add(new S0549(3596735949522644038L));
      f.add(new S0549(-1966143512881344948L));
      f.add(new S0549(5072240701308363185L));
      f.add(new S0549(1011453269306704631L));
      f.add(new S0549(-9106465775666771632L));
      f.add(new S0549(3777172214928757932L));
      f.add(new S0549(2975749223989780795L));
      f.add(new S0549(3865455161114591693L));
      f.add(new S0549(-2476618753328617637L));
      f.add(new S0549(-5506836053213281519L));
      f.add(new S0549(4957881590102746311L));
      f.add(new S0549(-5387352714388643840L));
      f.add(new S0549(-4880542776371311388L));
      f.add(new S0549(3556698929397729278L));
      f.add(new S0549(1379588467864332037L));
      f.add(new S0549(-8286519606776733339L));
      f.add(new S0549(-7597059744730779482L));
      f.add(new S0549(-4171703978307540362L));
      f.add(new S0549(-8503296912472232814L));
      f.add(new S0549(-1836000162419816933L));
      f.add(new S0549(-8459800239775838177L));
      f.add(new S0549(68237791024391869L));
      f.add(new S0549(-1594673341560067631L));
      f.add(new S0549(-1586157104595294501L));
      f.add(new S0549(996447186451924476L));
      f.add(new S0549(-4044667892809757484L));
      e = (int[])(Object)(e.clone());
      f.add(new S0549(7194611807492143477L));
      f.add(new S0549(-4352393048069519588L));
      f.add(new S0549(3906805856339614210L));
      f.add(new S0549(5042661540534080612L));
      f.add(new S0549(260368455315165630L));
      f.add(new S0549(7400637213508074159L));
      f.add(new S0549(8281333240160128986L));
      f.add(new S0549(2480365330496235741L));
      f.add(new S0549(-511056966059623971L));
      f.add(new S0549(-4313960534301964935L));
      f.add(new S0549(5918507970513861399L));
      f.add(new S0549(-7700478525502827930L));
      f.add(new S0549(-4905263155643456743L));
      f.add(new S0549(-4634131963090861169L));
      f.add(new S0549(7487598097565324318L));
      f.add(new S0549(-160771419540465096L));
      f.add(new S0549(-2588026457577557069L));
      f.add(new S0549(5152696234099833531L));
      f.add(new S0549(-3215688213030996125L));
      f.add(new S0549(7833138498952916627L));
      f.add(new S0549(-7211227333287858577L));
      f.add(new S0549(-8437616579748087040L));
      f.add(new S0549(-6366014460863168035L));
      f.add(new S0549(-7414008550681395440L));
      f.add(new S0549(-3105147124278922083L));
      f.add(new S0549(-8034488008081184114L));
      f.add(new S0549(6414056424210632766L));
      f.add(new S0549(236241547618744006L));
      f.add(new S0549(-6324463900945014517L));
      f.add(new S0549(7393944907926405664L));
      f.add(new S0549(1096199277483231935L));
      f.add(new S0549(-5250178245348591369L));
      f.add(new S0549(-6032329200928558043L));
      f.add(new S0549(3798297198429766254L));
      f.add(new S0549(2939788152909114367L));
      f.add(new S0549(3335773287657439048L));
      f.add(new S0549(-7268416394473303142L));
      f.add(new S0549(7396375367881182017L));
      f.add(new S0549(-6650287388764453287L));
      f.add(new S0549(-698928160411988668L));
      f.add(new S0549(-2580182530330002117L));
      f.add(new S0549(2574829008595077523L));
      f.add(new S0549(2277037531626032968L));
      f.add(new S0549(97174581517551533L));
      f.add(new S0549(-522268387495371998L));
      f.add(new S0549(8132937484404317113L));
      f.add(new S0549(-2432486304166371790L));
      f.add(new S0549(4077027749470964641L));
      f.add(new S0549(-8546536924051911923L));
      f.add(new S0549(3548708528391679548L));
      f.add(new S0549(8199031872002032713L));
      f.add(new S0549(-9035984241698882776L));
      f.add(new S0549(8008878767808386559L));
      f.add(new S0549(2133604434935026528L));
      f.add(new S0549(6020794306108167782L));
      f.add(new S0549(-1369418506439763782L));
      f.add(new S0549(-3795071103210604255L));
      f.add(new S0549(-7489123775042719052L));
      f.add(new S0549(-2913421222176884288L));
      f.add(new S0549(1186517764039389177L));
      f.add(new S0549(-5143342074004177217L));
      f.add(new S0549(-665959220152718065L));
      f.add(new S0549(6533592956801420154L));
      f.add(new S0549(1671200554272959843L));
      f.add(new S0549(-8632649837157512931L));
      f.add(new S0549(-7721067306330703562L));
      f.add(new S0549(-4444849123756221879L));
      f.add(new S0549(-5482134716169106735L));
      f.add(new S0549(-4941681585582219511L));
      f.add(new S0549(-3121554493212692642L));
      f.add(new S0549(-7633694913667279977L));
      f.add(new S0549(2978566078011787624L));
      f.add(new S0549(-4156110841717767763L));
      f.add(new S0549(-4986900496118599635L));
      f.add(new S0549(2333555117716056917L));
      f.add(new S0549(-8198501786811748618L));
      f.add(new S0549(-7517525642441491462L));
      f.add(new S0549(-7408585420761217213L));
      f.add(new S0549(-6665066358654180103L));
      f.add(new S0549(6330722968131740176L));
      f.add(new S0549(-1059806530309099883L));
      f.add(new S0549(-3750474676767440882L));
      f.add(new S0549(3365372585042491392L));
      f.add(new S0549(-5660756737929676716L));
      f.add(new S0549(-8549262200946064635L));
      f.add(new S0549(-7269661545783398932L));
      f.add(new S0549(-733020014666880481L));
      f.add(new S0549(491166540543540322L));
      f.add(new S0549(-7979609498273952106L));
      f.add(new S0549(-4058214869452769295L));
      f.add(new S0549(6041096742414723469L));
      f.add(new S0549(8885428953281285970L));
      f.add(new S0549(-8330022530239753654L));
      f.add(new S0549(1158348072175901835L));
      f.add(new S0549(8908141079845941244L));
      f.add(new S0549(3376831176586142397L));
      f.add(new S0549(5810831267685733851L));
      f.add(new S0549(-6838693081607167987L));
      f.add(new S0549(-9016515759500685353L));
      f.add(new S0549(-6414939439895580597L));
      e = (int[])(Object)(e.clone());
      f.add(new S0549(7712788214418993318L));
      f.add(new S0549(7695466496764279985L));
      f.add(new S0549(-7714353975616493005L));
      f.add(new S0549(-4489093845492857878L));
      f.add(new S0549(7371936273344859364L));
      f.add(new S0549(-1275720082921138482L));
      f.add(new S0549(7205850615503784474L));
      f.add(new S0549(-8695489996825025194L));
      f.add(new S0549(-2699136083910985977L));
      f.add(new S0549(7517470908442235209L));
      f.add(new S0549(528299576118159307L));
      f.add(new S0549(3904920259224149390L));
      f.add(new S0549(1596277818611700562L));
      f.add(new S0549(993337473434171345L));
      f.add(new S0549(-347822687323467049L));
      f.add(new S0549(8495130231125778379L));
      f.add(new S0549(-1624794849513101516L));
      f.add(new S0549(-1406911749201454344L));
      f.add(new S0549(6369741211469356398L));
      f.add(new S0549(8797472250477284093L));
      f.add(new S0549(-3946286476244711619L));
      f.add(new S0549(2296979928492808976L));
      f.add(new S0549(-1882153090704318253L));
      f.add(new S0549(4581136110266874596L));
      f.add(new S0549(8964330307289485741L));
      f.add(new S0549(-3013368468762667339L));
      f.add(new S0549(7917634157973433430L));
      f.add(new S0549(-339396814779504544L));
      f.add(new S0549(-6289771260813322396L));
      f.add(new S0549(5140750397409107881L));
      f.add(new S0549(-3569350557602230405L));
      f.add(new S0549(3589057271848037803L));
      f.add(new S0549(2480195133585928898L));
      f.add(new S0549(-8563404018675753072L));
      f.add(new S0549(1501621240813736459L));
      f.add(new S0549(8322358959384581471L));
      f.add(new S0549(-653027339065365112L));
      f.add(new S0549(-203049341709825718L));
      f.add(new S0549(1640587613390039050L));
      f.add(new S0549(-4530501174930534868L));
      f.add(new S0549(3186914366195451143L));
      f.add(new S0549(-495667689800903707L));
      f.add(new S0549(-3412607594115736718L));
      f.add(new S0549(3941910061354199523L));
      f.add(new S0549(7266353397777843178L));
      f.add(new S0549(5299115493849569322L));
      f.add(new S0549(-5284298558306306876L));
      f.add(new S0549(-3659932271796936772L));
      f.add(new S0549(6363527853669208252L));
      f.add(new S0549(6223300590167008608L));
      f.add(new S0549(-1864308400053814804L));
      f.add(new S0549(5020203188742886829L));
      f.add(new S0549(-7656958068870220240L));
      f.add(new S0549(-6158169085610555379L));
      f.add(new S0549(3692083573231337056L));
      f.add(new S0549(-3393312413326356140L));
      f.add(new S0549(-3278959195806944513L));
      f.add(new S0549(-6741460312569373039L));
      f.add(new S0549(347202854676663823L));
      f.add(new S0549(9147550993149053471L));
      f.add(new S0549(-2956119407498607380L));
      f.add(new S0549(820711975769859052L));
      f.add(new S0549(-4169280023403398061L));
      f.add(new S0549(-1664372214857417168L));
      f.add(new S0549(-590704556100656662L));
      f.add(new S0549(-4986950013872162698L));
      f.add(new S0549(2789778908193713454L));
      f.add(new S0549(6793043414595196157L));
      f.add(new S0549(-1037076853726009203L));
      f.add(new S0549(5852035236592156208L));
      f.add(new S0549(-6200970324026265889L));
      f.add(new S0549(9207262951156712642L));
      f.add(new S0549(-4632257845710430727L));
      f.add(new S0549(-1949141042671654070L));
      f.add(new S0549(1712458643542428529L));
      f.add(new S0549(-469217054423708197L));
      f.add(new S0549(-8339253408185516058L));
      f.add(new S0549(-9097062387585685666L));
      f.add(new S0549(-5898815773687287774L));
      f.add(new S0549(3018262258718932077L));
      f.add(new S0549(-3266638052767450498L));
      f.add(new S0549(9197071657903865415L));
      f.add(new S0549(-8626593925640026839L));
      f.add(new S0549(3636384374962437865L));
      f.add(new S0549(3598533075560352841L));
      f.add(new S0549(598464885550297277L));
      f.add(new S0549(-9021356661347896217L));
      f.add(new S0549(2696907247995016989L));
      f.add(new S0549(-6682714967232296324L));
      f.add(new S0549(9096347890628772427L));
      f.add(new S0549(-1090276406924284300L));
      f.add(new S0549(2738830194971780341L));
      f.add(new S0549(9173474092542983781L));
      f.add(new S0549(3980057531705299575L));
      f.add(new S0549(9076395743205490895L));
      f.add(new S0549(6665385205431997546L));
      f.add(new S0549(5978409233283138729L));
      f.add(new S0549(-6572364943168987259L));
      f.add(new S0549(-2700636807329387364L));
      f.add(new S0549(314376251440174350L));
      f.add(new S0549(2178485714412297087L));
      f.add(new S0549(-4678629540761556242L));
      f.add(new S0549(2813768477697956669L));
      f.add(new S0549(6473100734578730815L));
      f.add(new S0549(5132789364398787043L));
      f.add(new S0549(7568509416046274923L));
      f.add(new S0549(2987557685089745228L));
      f.add(new S0549(-7898730875441670778L));
      f.add(new S0549(-6327015860235129863L));
      f.add(new S0549(-3061600056543297309L));
      f.add(new S0549(-7885594535860153749L));
      f.add(new S0549(-5088726872925575201L));
      f.add(new S0549(-279557395697052887L));
      f.add(new S0549(-5059569935258661798L));
      f.add(new S0549(5144175481554119360L));
      f.add(new S0549(5145137001628849827L));
      f.add(new S0549(4459048807940030528L));
      f.add(new S0549(5572761078225506389L));
      f.add(new S0549(6946343296763491301L));
      f.add(new S0549(-8247950924415051668L));
      f.add(new S0549(-6892178977169308984L));
      f.add(new S0549(2860908877147668580L));
      f.add(new S0549(8389875924984551131L));
      f.add(new S0549(-7837477156994051789L));
      f.add(new S0549(5979250714775373091L));
      f.add(new S0549(-8575118083221853905L));
      f.add(new S0549(5065170967328814853L));
      f.add(new S0549(6034304712688596381L));
      f.add(new S0549(-8262456589652832291L));
      f.add(new S0549(4057760127383493089L));
      f.add(new S0549(-6536483149914686195L));
      f.add(new S0549(-7057226478305285580L));
      f.add(new S0549(-2276648798847236774L));
      f.add(new S0549(-7088309748852975981L));
      f.add(new S0549(6764246248527996567L));
      f.add(new S0549(-4767895482633794629L));
      f.add(new S0549(7639477256377047313L));
      f.add(new S0549(3254641459175126542L));
      f.add(new S0549(-5892809626544927852L));
      f.add(new S0549(-8451974756748527164L));
      f.add(new S0549(-7207548773927264842L));
      f.add(new S0549(6251354815369572362L));
      f.add(new S0549(-1741097637663778349L));
      f.add(new S0549(-1455699196600976679L));
      f.add(new S0549(3962492123447708411L));
      f.add(new S0549(-8343235660590081785L));
      f.add(new S0549(4854501433774555060L));
      f.add(new S0549(-1006775160805184546L));
      f.add(new S0549(8768356302166058583L));
      f.add(new S0549(-5670894312275186800L));
      f.add(new S0549(-957387463701784230L));
      f.add(new S0549(6795793872424428392L));
      f.add(new S0549(6680277039348179569L));
      f.add(new S0549(-3405180155750486128L));
      f.add(new S0549(3057723924304428272L));
      f.add(new S0549(8083942167017750512L));
      f.add(new S0549(-2737940618383116000L));
      f.add(new S0549(7045502413935400764L));
      f.add(new S0549(-6434528628234510374L));
      f.add(new S0549(-862295425977616044L));
      f.add(new S0549(1395771803559074046L));
      f.add(new S0549(-2730866161202372565L));
      f.add(new S0549(-7722642209356324973L));
      f.add(new S0549(2332215265890837285L));
      f.add(new S0549(-4253656950882455563L));
      f.add(new S0549(-7728192884731362617L));
      f.add(new S0549(3093059450706615432L));
      f.add(new S0549(-5735993243944070834L));
      f.add(new S0549(-6266069353599292116L));
      f.add(new S0549(-673340647682499633L));
      f.add(new S0549(7872798747974524270L));
      f.add(new S0549(8493520739996341774L));
      f.add(new S0549(5857253770843153373L));
      f.add(new S0549(-4765643959845499973L));
      f.add(new S0549(-6232708946981591178L));
      f.add(new S0549(3065826825390304272L));
      f.add(new S0549(1441037477329781369L));
      f.add(new S0549(4152762753096482725L));
      f.add(new S0549(-3897932891261772417L));
      f.add(new S0549(4939836025532366917L));
      f.add(new S0549(-8564937325964315121L));
      f.add(new S0549(189426204863350150L));
      f.add(new S0549(-2397670309091051061L));
      f.add(new S0549(3740872170489287438L));
      f.add(new S0549(3174528580908881835L));
      f.add(new S0549(-8472966096842777521L));
      f.add(new S0549(522332441238110122L));
      f.add(new S0549(-3570260265733238616L));
      f.add(new S0549(4343834606291111473L));
      f.add(new S0549(-6301920720324375875L));
      f.add(new S0549(-2002697860815070621L));
      f.add(new S0549(4039098239795458170L));
      f.add(new S0549(9088215209415693907L));
      f.add(new S0549(5383599228868490261L));
      f.add(new S0549(-4543056355689603646L));
      f.add(new S0549(803650443156070349L));
      f.add(new S0549(7933809019983005343L));
      f.add(new S0549(-3185550684862070952L));
      f.add(new S0549(8184939181750474030L));
      f.add(new S0549(-4229285685234962125L));
      f.add(new S0549(6925288150231779723L));
      f.add(new S0549(-594259770676744733L));
      f.add(new S0549(4805933878746116602L));
      f.add(new S0549(-1990415304951264198L));
      f.add(new S0549(-1300573969819467403L));
      f.add(new S0549(-5656272849585499630L));
      f.add(new S0549(2788663697590890289L));
      f.add(new S0549(872124729684145294L));
      f.add(new S0549(2782830030895018713L));
      f.add(new S0549(-5444779487771833916L));
      f.add(new S0549(-2292928071932837069L));
      f.add(new S0549(1690547361515928732L));
      e = (int[])(Object)(e.clone());
      f.add(new S0549(4719169037526681117L));
      f.add(new S0549(-3256019495843030798L));
      f.add(new S0549(8370848702113378162L));
      f.add(new S0549(-3182816304933364074L));
      f.add(new S0549(-5536066397984171097L));
      f.add(new S0549(-4606541427073572774L));
      f.add(new S0549(-8531820942907454968L));
      f.add(new S0549(-2842302799697713057L));
      f.add(new S0549(5398531189571838586L));
      f.add(new S0549(-3360471561423308034L));
      f.add(new S0549(-1211326480497262408L));
      f.add(new S0549(-9177304161132158670L));
      f.add(new S0549(8536165228647253209L));
      f.add(new S0549(301667013962286619L));
      f.add(new S0549(3918335916533838169L));
      f.add(new S0549(-7264640875275267614L));
      f.add(new S0549(5326659848824823264L));
      f.add(new S0549(6272455313033724816L));
      f.add(new S0549(-2007142943078168786L));
      f.add(new S0549(-1762342400393967688L));
      f.add(new S0549(8204882385165282039L));
      f.add(new S0549(-3970639071513930466L));
      f.add(new S0549(-5909824587242363575L));
      f.add(new S0549(-7301531263734886490L));
      f.add(new S0549(2774258194185512091L));
      f.add(new S0549(-2316092914535840579L));
      f.add(new S0549(-8062975401052903667L));
      f.add(new S0549(1513688553973688576L));
      f.add(new S0549(-8048619942367478027L));
      f.add(new S0549(-8700452580139984744L));
      f.add(new S0549(4541323920961645235L));
      f.add(new S0549(5622477093704612171L));
      f.add(new S0549(-7529354852373567802L));
      f.add(new S0549(8692938983714204675L));
      f.add(new S0549(4320334541173692160L));
      f.add(new S0549(2165381819523911310L));
      f.add(new S0549(-5401466539777427883L));
      f.add(new S0549(-1457066280182390271L));
      f.add(new S0549(-6593225478385424258L));
      f.add(new S0549(3887757676308222485L));
      f.add(new S0549(4390247573865702185L));
      f.add(new S0549(-975531344639310538L));
      f.add(new S0549(-5322463644179199402L));
      f.add(new S0549(3304713002920425823L));
      f.add(new S0549(-5003170204534129545L));
      f.add(new S0549(-4737265935219864611L));
      f.add(new S0549(8167176918928909958L));
      f.add(new S0549(2091366035898471110L));
      f.add(new S0549(6028870184522288756L));
      f.add(new S0549(-8390803182440983692L));
      f.add(new S0549(-2194049687281186033L));
      f.add(new S0549(3764295413774586699L));
      f.add(new S0549(5090602499112393082L));
      f.add(new S0549(6204958381496205473L));
      f.add(new S0549(-483999320727586844L));
      f.add(new S0549(-4737901003089343178L));
      f.add(new S0549(-3819216775427432528L));
      f.add(new S0549(2603914573530791753L));
      f.add(new S0549(-4080449522620730798L));
      f.add(new S0549(7187986564409110307L));
      f.add(new S0549(1248207096013739096L));
      f.add(new S0549(1482935395900690164L));
      f.add(new S0549(-732250285299444104L));
      f.add(new S0549(-2777808176305580448L));
      f.add(new S0549(5685134678571647473L));
      f.add(new S0549(-4803955333969873251L));
      f.add(new S0549(-7218702004224467918L));
      f.add(new S0549(-408781639139341256L));
      f.add(new S0549(7423838660430720611L));
      f.add(new S0549(3186265442379620626L));
      f.add(new S0549(880982727147841540L));
      f.add(new S0549(-6652910649204114037L));
      f.add(new S0549(6421594837778346626L));
      f.add(new S0549(8734627079830095654L));
      f.add(new S0549(3401042475438664071L));
      f.add(new S0549(8686164524294400372L));
      f.add(new S0549(-7994008184575590198L));
      f.add(new S0549(1003726280887846557L));
      f.add(new S0549(-6633172495155185735L));
      f.add(new S0549(-4903634081229890392L));
      e = (int[])(Object)(e.clone());
      f.add(new S0549(3810666062847284202L));
      f.add(new S0549(8193238053266081953L));
      f.add(new S0549(-910473303158080063L));
      f.add(new S0549(1510710320105582464L));
      f.add(new S0549(6119547849153084244L));
      f.add(new S0549(-9097080254616082596L));
      f.add(new S0549(2453309364982278915L));
      f.add(new S0549(1428864716851425489L));
      f.add(new S0549(834886697050471438L));
      f.add(new S0549(5628184341340421779L));
      f.add(new S0549(-225450230864496206L));
      f.add(new S0549(-5092323542095960086L));
      f.add(new S0549(-7258634621414054121L));
      f.add(new S0549(138488544827293748L));
      f.add(new S0549(7787387533426278490L));
      f.add(new S0549(2362544755208080360L));
      f.add(new S0549(-7651428790166718677L));
      f.add(new S0549(4706401009199834738L));
      f.add(new S0549(-8159027205701861011L));
      f.add(new S0549(4187024226679156932L));
      f.add(new S0549(-2251397477680174408L));
      f.add(new S0549(-1875109863369901574L));
      f.add(new S0549(3742504564061056353L));
      f.add(new S0549(5207670545388548668L));
      f.add(new S0549(-3543142568600086601L));
      f.add(new S0549(-5319778845379539590L));
      f.add(new S0549(-4327583043142017551L));
      f.add(new S0549(-6269875470200604504L));
      f.add(new S0549(2132491892759151713L));
      f.add(new S0549(-2190017132284774107L));
      f.add(new S0549(-2153235570788781483L));
      f.add(new S0549(1110451489463170035L));
      f.add(new S0549(-4863505189110815173L));
      f.add(new S0549(-4871499107408356459L));
      f.add(new S0549(-6751554858271831534L));
      f.add(new S0549(-8484665350853740103L));
      f.add(new S0549(1213466536913401524L));
      f.add(new S0549(9164678511328683350L));
      f.add(new S0549(-2294658575955335643L));
      f.add(new S0549(2983413820997007181L));
      f.add(new S0549(516269715986144493L));
      f.add(new S0549(1002902454721271606L));
      f.add(new S0549(-4756108639326659202L));
      f.add(new S0549(-1457344272118854601L));
      f.add(new S0549(-5855041488619967095L));
      f.add(new S0549(3788774842963954925L));
      f.add(new S0549(-4584006087905589620L));
      f.add(new S0549(8895608808092109166L));
      f.add(new S0549(-4520331482439903158L));
      f.add(new S0549(2915055581362826283L));
      f.add(new S0549(6124617638305744335L));
      f.add(new S0549(-5866354177327619872L));
      f.add(new S0549(-7119209777805569301L));
      f.add(new S0549(5524368387715549261L));
      f.add(new S0549(-3414880855938529228L));
      f.add(new S0549(-667829738203557157L));
      f.add(new S0549(8975470373520983923L));
      f.add(new S0549(-5294499619751781768L));
      f.add(new S0549(-4748445344093794681L));
      f.add(new S0549(3599351568937820734L));
      f.add(new S0549(4396325482560539220L));
      e = new int[]{
         -6,
         -60,
         -31,
         -21,
         -14,
         -36,
         6,
         -13,
         -47,
         -3,
         -17,
         -45,
         3,
         -4,
         -15,
         -4,
         -7,
         4,
         14,
         4,
         13,
         -24,
         -30,
         7,
         21,
         -29,
         -14,
         17,
         -16,
         15,
         -12,
         -1,
         1,
         31,
         -14,
         -2,
         -10,
         2,
         -9,
         -21,
         14,
         36,
         12,
         -16,
         16,
         24,
         10,
         9,
         14,
         -4,
         -12,
         -12,
         30,
         4,
         29,
         47,
         45,
         -1,
         1,
         16,
         21,
         60,
         12,
         12
      };
   
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

private static void __flow$1$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(52,1);f.pc=1;return;}
case 1:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0549.class,"h",dev.zprestige.sourceview.FlowFrame.i(f.pop()));f.pc=2;return;}
case 2:{f.push(17,1);f.pc=3;return;}
case 3:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0549.class,"n",dev.zprestige.sourceview.FlowFrame.i(f.pop()));f.pc=4;return;}
case 4:{f.push(128,1);f.pc=5;return;}
case 5:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0549.class,"l",dev.zprestige.sourceview.FlowFrame.i(f.pop()));f.pc=6;return;}
case 6:{f.push(64,1);f.pc=7;return;}
case 7:{f.push(new long[dev.zprestige.sourceview.FlowFrame.i(f.pop())],1);f.pc=8;return;}
case 8:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0549.class,"k",((long[])(Object)(f.pop())));f.pc=9;return;}
case 9:{f.push(1L,2);f.pc=10;return;}
case 10:{f.locals[0]=f.pop();f.pc=11;return;}
case 11:{f.push(0,1);f.pc=12;return;}
case 12:{f.locals[2]=f.pop();f.pc=13;return;}
case 13:{f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.push(f.locals[2],1);f.pc=16;return;}
case 16:{f.push(64,1);f.pc=17;return;}
case 17:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a>=b){f.pc=28;return;}f.pc=18;return;}
case 18:{f.push(dev.zprestige.sourceview.S0549.k,1);f.pc=19;return;}
case 19:{f.push(f.locals[2],1);f.pc=20;return;}
case 20:{f.push(f.locals[0],2);f.pc=21;return;}
case 21:{Object v=f.pop();int index=dev.zprestige.sourceview.FlowFrame.i(f.pop());Object array=f.pop();((long[])array)[index]=dev.zprestige.sourceview.FlowFrame.j(v);f.pc=22;return;}
case 22:{f.push(f.locals[0],2);f.pc=23;return;}
case 23:{f.push(1,1);f.pc=24;return;}
case 24:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop());long a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a<<b,2);f.pc=25;return;}
case 25:{f.locals[0]=f.pop();f.pc=26;return;}
case 26:{f.locals[2]=dev.zprestige.sourceview.FlowFrame.i(f.locals[2])+(1);f.pc=27;return;}
case 27:{f.pc=13;return;}
case 28:{f.pc=29;return;}
case 29:{f.pc=30;return;}
case 30:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.lang.Object.class),1);f.pc=31;return;}
case 31:{f.rearrange(89);f.pc=32;return;}
case 32:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=33;return;}
case 33:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0549.class,"o",((java.lang.Object)(Object)(f.pop())));f.pc=34;return;}
case 34:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.util.Vector.class),1);f.pc=35;return;}
case 35:{f.rearrange(89);f.pc=36;return;}
case 36:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=37;return;}
case 37:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0549.class,"m",((java.util.Vector)(Object)(f.pop())));f.pc=38;return;}
case 38:{f.push(dev.zprestige.sourceview.FlowFrame.pending(java.util.ArrayList.class),1);f.pc=39;return;}
case 39:{f.rearrange(89);f.pc=40;return;}
case 40:{Object receiver=f.pop();f.construct(receiver,new Class<?>[]{},new Object[]{});f.pc=41;return;}
case 41:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0549.class,"f",((java.util.ArrayList)(Object)(f.pop())));f.pc=42;return;}
case 42:{dev.zprestige.sourceview.S0549.a0();f.pc=43;return;}
case 43:{f.push(dev.zprestige.sourceview.S0549.f,1);f.pc=44;return;}
case 44:{Object receiver=f.pop();f.push(((java.util.ArrayList)(Object)receiver).size(),1);f.pc=45;return;}
case 45:{dev.zprestige.sourceview.FlowFrame.staticField(dev.zprestige.sourceview.S0549.class,"i",dev.zprestige.sourceview.FlowFrame.i(f.pop()));f.pc=46;return;}
case 46:{dev.zprestige.sourceview.S0549.c$return$9b75();f.pc=47;return;}
case 47:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
