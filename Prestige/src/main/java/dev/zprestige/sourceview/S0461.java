package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0292;
import dev.zprestige.sourceview.S0391;
import dev.zprestige.sourceview.S0462;


public record S0461(boolean bH, boolean bI, int bJ, int bK, int bL, int bM) implements S0391 {

   @Override
   public final boolean equals(Object var1) {
try {

      return S0462.__indy$0(this, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public final String toString() {
try {

      return S0462.__indy$1(this);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public final int hashCode() {
try {

      return S0462.__indy$2(this);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public void a(S0292 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(5,8);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$4$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=8&&f.pc<16&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=18;continue;}
if(f.pc>=14&&f.pc<50&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=52;continue;}
if(f.pc>=24&&f.pc<66&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=68;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   @Override
   public void a$return$9b75() {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(4,5);
f.locals[0]=this;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$5$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=8&&f.pc<18&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=20;continue;}
if(f.pc>=14&&f.pc<31&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=33;continue;}
if(f.pc>=39&&f.pc<45&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=47;continue;}
if(f.pc>=43&&f.pc<65&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=67;continue;}
if(f.pc>=53&&f.pc<81&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=83;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}







private void __flow$4$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0462.b,2);f.pc=1;return;}
case 1:{f.push(75033862701354L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(-8381248846047003483L,2);f.pc=5;return;}
case 5:{f.push(f.locals[2],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$3(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.locals[4]=f.pop();f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bH"),1);f.pc=12;return;}
case 12:{f.push(f.locals[4],1);f.pc=13;return;}
case 13:{if(f.pop()!=null){f.pc=74;return;}f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=58;return;}f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.pc=24;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(-8381289374714056475L,2);f.pc=21;return;}
case 21:{f.push(f.locals[2],2);f.pc=22;return;}
case 22:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=23;return;}
case 23:{throw (Throwable)f.pop();}
case 24:{f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.pc=27;return;}
case 27:{f.push(30186,1);f.pc=28;return;}
case 28:{f.push(2207673194345756308L,2);f.pc=29;return;}
case 29:{f.push(f.locals[2],2);f.pc=30;return;}
case 30:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=31;return;}
case 31:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$5(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=32;return;}
case 32:{f.push(-8381424024706280535L,2);f.pc=33;return;}
case 33:{f.push(f.locals[2],2);f.pc=34;return;}
case 34:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0462.__indy$6(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=35;return;}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.push(f.locals[0],1);f.pc=38;return;}
case 38:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bJ"),1);f.pc=39;return;}
case 39:{f.push(f.locals[0],1);f.pc=40;return;}
case 40:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bK"),1);f.pc=41;return;}
case 41:{f.push(f.locals[0],1);f.pc=42;return;}
case 42:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bL"),1);f.pc=43;return;}
case 43:{f.push(f.locals[0],1);f.pc=44;return;}
case 44:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bM"),1);f.pc=45;return;}
case 45:{f.push(-8381699337156294653L,2);f.pc=46;return;}
case 46:{f.push(f.locals[2],2);f.pc=47;return;}
case 47:{Object a5=f.pop();Object a4=f.pop();Object a3=f.pop();Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0462.__indy$7(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.i(a1),dev.zprestige.sourceview.FlowFrame.i(a2),dev.zprestige.sourceview.FlowFrame.i(a3),dev.zprestige.sourceview.FlowFrame.j(a4),dev.zprestige.sourceview.FlowFrame.j(a5));f.pc=48;return;}
case 48:{f.push(f.locals[4],1);f.pc=49;return;}
case 49:{if(f.pop()==null){f.pc=79;return;}f.pc=50;return;}
case 50:{f.pc=51;return;}
case 51:{f.pc=58;return;}
case 52:{f.pc=53;return;}
case 53:{f.pc=54;return;}
case 54:{f.push(-8381289374714056475L,2);f.pc=55;return;}
case 55:{f.push(f.locals[2],2);f.pc=56;return;}
case 56:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=57;return;}
case 57:{throw (Throwable)f.pop();}
case 58:{f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.push(30186,1);f.pc=62;return;}
case 62:{f.push(2207673194345756308L,2);f.pc=63;return;}
case 63:{f.push(f.locals[2],2);f.pc=64;return;}
case 64:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=65;return;}
case 65:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$9(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=66;return;}
case 66:{f.pc=67;return;}
case 67:{f.pc=74;return;}
case 68:{f.pc=69;return;}
case 69:{f.pc=70;return;}
case 70:{f.push(-8381289374714056475L,2);f.pc=71;return;}
case 71:{f.push(f.locals[2],2);f.pc=72;return;}
case 72:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$10(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=73;return;}
case 73:{throw (Throwable)f.pop();}
case 74:{f.pc=75;return;}
case 75:{f.pc=76;return;}
case 76:{f.push(-8381637743677961921L,2);f.pc=77;return;}
case 77:{f.push(f.locals[2],2);f.pc=78;return;}
case 78:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0462.__indy$11(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=79;return;}
case 79:{f.pc=80;return;}
case 80:{f.pc=81;return;}
case 81:{f.pc=82;return;}
case 82:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$5$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0462.b,2);f.pc=1;return;}
case 1:{f.push(42501255592126L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[1]=f.pop();f.pc=4;return;}
case 4:{f.push(-992005722022525647L,2);f.pc=5;return;}
case 5:{f.push(f.locals[1],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$12(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.locals[3]=f.pop();f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bI"),1);f.pc=12;return;}
case 12:{f.push(f.locals[3],1);f.pc=13;return;}
case 13:{if(f.pop()!=null){f.pc=39;return;}f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.push(f.locals[0],1);f.pc=16;return;}
case 16:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bH"),1);f.pc=17;return;}
case 17:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a==b){f.pc=94;return;}f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=26;return;}
case 20:{f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(-991971549933221519L,2);f.pc=23;return;}
case 23:{f.push(f.locals[1],2);f.pc=24;return;}
case 24:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$13(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=25;return;}
case 25:{throw (Throwable)f.pop();}
case 26:{f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.pc=29;return;}
case 29:{f.push(f.locals[0],1);f.pc=30;return;}
case 30:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bI"),1);f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=39;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.push(-991971549933221519L,2);f.pc=36;return;}
case 36:{f.push(f.locals[1],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{throw (Throwable)f.pop();}
case 39:{f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.push(f.locals[3],1);f.pc=42;return;}
case 42:{if(f.pop()!=null){f.pc=89;return;}f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=73;return;}f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.pc=53;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(-991971549933221519L,2);f.pc=50;return;}
case 50:{f.push(f.locals[1],2);f.pc=51;return;}
case 51:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=52;return;}
case 52:{throw (Throwable)f.pop();}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.push(30880,1);f.pc=56;return;}
case 56:{f.push(7324182485477851723L,2);f.pc=57;return;}
case 57:{f.push(f.locals[1],2);f.pc=58;return;}
case 58:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=59;return;}
case 59:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$16(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=60;return;}
case 60:{f.push(-992109304180676035L,2);f.pc=61;return;}
case 61:{f.push(f.locals[1],2);f.pc=62;return;}
case 62:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0462.__indy$17(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=63;return;}
case 63:{f.push(f.locals[3],1);f.pc=64;return;}
case 64:{if(f.pop()==null){f.pc=94;return;}f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.pc=73;return;}
case 67:{f.pc=68;return;}
case 68:{f.pc=69;return;}
case 69:{f.push(-991971549933221519L,2);f.pc=70;return;}
case 70:{f.push(f.locals[1],2);f.pc=71;return;}
case 71:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$18(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=72;return;}
case 72:{throw (Throwable)f.pop();}
case 73:{f.pc=74;return;}
case 74:{f.pc=75;return;}
case 75:{f.pc=76;return;}
case 76:{f.push(30186,1);f.pc=77;return;}
case 77:{f.push(2207706697616002816L,2);f.pc=78;return;}
case 78:{f.push(f.locals[1],2);f.pc=79;return;}
case 79:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=80;return;}
case 80:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$19(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=81;return;}
case 81:{f.pc=82;return;}
case 82:{f.pc=89;return;}
case 83:{f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.push(-991971549933221519L,2);f.pc=86;return;}
case 86:{f.push(f.locals[1],2);f.pc=87;return;}
case 87:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0462.__indy$20(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=88;return;}
case 88:{throw (Throwable)f.pop();}
case 89:{f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.push(-992460457206353749L,2);f.pc=92;return;}
case 92:{f.push(f.locals[1],2);f.pc=93;return;}
case 93:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0462.__indy$21(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=94;return;}
case 94:{f.pc=95;return;}
case 95:{f.pc=96;return;}
case 96:{f.pc=97;return;}
case 97:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
