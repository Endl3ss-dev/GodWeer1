package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0292;
import dev.zprestige.sourceview.S0391;
import dev.zprestige.sourceview.S0468;


public record S0467(int bR, boolean bS, boolean bT) implements S0391 {

   public S0467(int var1, boolean var2)  throws Throwable {
      this(var1, var2, var2);
try {

   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public final boolean equals(Object var1) {
try {

      return S0468.__indy$0(this, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public final String toString() {
try {

      return S0468.__indy$1(this);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public final int hashCode() {
try {

      return S0468.__indy$2(this);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
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
if(f.pc>=43&&f.pc<62&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=64;continue;}
if(f.pc>=53&&f.pc<75&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=77;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}



   @Override
   public boolean a$return$9b79() {
try {

      return true;
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public void a(S0292 param1) {
dev.zprestige.sourceview.FlowFrame f=new dev.zprestige.sourceview.FlowFrame(5,5);
f.locals[0]=this;
f.locals[1]=param1;
while(!f.done){try{switch(f.pc/120){
case 0:__flow$7$0(f);break;
default:throw new VerifyError("Invalid source chunk "+f.pc);}}catch(Throwable error){
if(f.pc>=8&&f.pc<16&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=18;continue;}
if(f.pc>=14&&f.pc<33&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=35;continue;}
if(f.pc>=24&&f.pc<46&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=48;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}







private void __flow$5$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0468.b,2);f.pc=1;return;}
case 1:{f.push(94898883203423L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[1]=f.pop();f.pc=4;return;}
case 4:{f.push(-992068222640160502L,2);f.pc=5;return;}
case 5:{f.push(f.locals[1],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$3(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.locals[3]=f.pop();f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bT"),1);f.pc=12;return;}
case 12:{f.push(f.locals[3],1);f.pc=13;return;}
case 13:{if(f.pop()!=null){f.pc=39;return;}f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.push(f.locals[0],1);f.pc=16;return;}
case 16:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bS"),1);f.pc=17;return;}
case 17:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a==b){f.pc=88;return;}f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=26;return;}
case 20:{f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(-991918588161518007L,2);f.pc=23;return;}
case 23:{f.push(f.locals[1],2);f.pc=24;return;}
case 24:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$4(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=25;return;}
case 25:{throw (Throwable)f.pop();}
case 26:{f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.pc=29;return;}
case 29:{f.push(f.locals[0],1);f.pc=30;return;}
case 30:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bT"),1);f.pc=31;return;}
case 31:{f.pc=32;return;}
case 32:{f.pc=39;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=35;return;}
case 35:{f.push(-991918588161518007L,2);f.pc=36;return;}
case 36:{f.push(f.locals[1],2);f.pc=37;return;}
case 37:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=38;return;}
case 38:{throw (Throwable)f.pop();}
case 39:{f.pc=40;return;}
case 40:{f.pc=41;return;}
case 41:{f.push(f.locals[3],1);f.pc=42;return;}
case 42:{if(f.pop()!=null){f.pc=83;return;}f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=70;return;}f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.pc=53;return;}
case 47:{f.pc=48;return;}
case 48:{f.pc=49;return;}
case 49:{f.push(-991918588161518007L,2);f.pc=50;return;}
case 50:{f.push(f.locals[1],2);f.pc=51;return;}
case 51:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=52;return;}
case 52:{throw (Throwable)f.pop();}
case 53:{f.pc=54;return;}
case 54:{f.pc=55;return;}
case 55:{f.push(f.locals[0],1);f.pc=56;return;}
case 56:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bR"),1);f.pc=57;return;}
case 57:{f.push(-992179419695398959L,2);f.pc=58;return;}
case 58:{f.push(f.locals[1],2);f.pc=59;return;}
case 59:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0468.__indy$7(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=60;return;}
case 60:{f.push(f.locals[3],1);f.pc=61;return;}
case 61:{if(f.pop()==null){f.pc=88;return;}f.pc=62;return;}
case 62:{f.pc=63;return;}
case 63:{f.pc=70;return;}
case 64:{f.pc=65;return;}
case 65:{f.pc=66;return;}
case 66:{f.push(-991918588161518007L,2);f.pc=67;return;}
case 67:{f.push(f.locals[1],2);f.pc=68;return;}
case 68:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$8(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=69;return;}
case 69:{throw (Throwable)f.pop();}
case 70:{f.pc=71;return;}
case 71:{f.pc=72;return;}
case 72:{f.pc=73;return;}
case 73:{f.push(f.locals[0],1);f.pc=74;return;}
case 74:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bR"),1);f.pc=75;return;}
case 75:{f.pc=76;return;}
case 76:{f.pc=83;return;}
case 77:{f.pc=78;return;}
case 78:{f.pc=79;return;}
case 79:{f.push(-991918588161518007L,2);f.pc=80;return;}
case 80:{f.push(f.locals[1],2);f.pc=81;return;}
case 81:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$9(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=82;return;}
case 82:{throw (Throwable)f.pop();}
case 83:{f.pc=84;return;}
case 84:{f.pc=85;return;}
case 85:{f.push(-992038395630793739L,2);f.pc=86;return;}
case 86:{f.push(f.locals[1],2);f.pc=87;return;}
case 87:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0468.__indy$10(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=88;return;}
case 88:{f.pc=89;return;}
case 89:{f.pc=90;return;}
case 90:{f.pc=91;return;}
case 91:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}

private void __flow$7$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0468.b,2);f.pc=1;return;}
case 1:{f.push(58097632504011L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[2]=f.pop();f.pc=4;return;}
case 4:{f.push(-8381456550449710946L,2);f.pc=5;return;}
case 5:{f.push(f.locals[2],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$11(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.locals[4]=f.pop();f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bS"),1);f.pc=12;return;}
case 12:{f.push(f.locals[4],1);f.pc=13;return;}
case 13:{if(f.pop()!=null){f.pc=54;return;}f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{if(dev.zprestige.sourceview.FlowFrame.i(f.pop())==0){f.pc=41;return;}f.pc=16;return;}
case 16:{f.pc=17;return;}
case 17:{f.pc=24;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=20;return;}
case 20:{f.push(-8381306842889785379L,2);f.pc=21;return;}
case 21:{f.push(f.locals[2],2);f.pc=22;return;}
case 22:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$12(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=23;return;}
case 23:{throw (Throwable)f.pop();}
case 24:{f.pc=25;return;}
case 25:{f.pc=26;return;}
case 26:{f.push(f.locals[0],1);f.pc=27;return;}
case 27:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bR"),1);f.pc=28;return;}
case 28:{f.push(-8381356640802421179L,2);f.pc=29;return;}
case 29:{f.push(f.locals[2],2);f.pc=30;return;}
case 30:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0468.__indy$13(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=31;return;}
case 31:{f.push(f.locals[4],1);f.pc=32;return;}
case 32:{if(f.pop()==null){f.pc=59;return;}f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.pc=41;return;}
case 35:{f.pc=36;return;}
case 36:{f.pc=37;return;}
case 37:{f.push(-8381306842889785379L,2);f.pc=38;return;}
case 38:{f.push(f.locals[2],2);f.pc=39;return;}
case 39:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$14(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=40;return;}
case 40:{throw (Throwable)f.pop();}
case 41:{f.pc=42;return;}
case 42:{f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.push(f.locals[0],1);f.pc=45;return;}
case 45:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bR"),1);f.pc=46;return;}
case 46:{f.pc=47;return;}
case 47:{f.pc=54;return;}
case 48:{f.pc=49;return;}
case 49:{f.pc=50;return;}
case 50:{f.push(-8381306842889785379L,2);f.pc=51;return;}
case 51:{f.push(f.locals[2],2);f.pc=52;return;}
case 52:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0468.__indy$15(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=53;return;}
case 53:{throw (Throwable)f.pop();}
case 54:{f.pc=55;return;}
case 55:{f.pc=56;return;}
case 56:{f.push(-8381215620831202719L,2);f.pc=57;return;}
case 57:{f.push(f.locals[2],2);f.pc=58;return;}
case 58:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0468.__indy$16(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=59;return;}
case 59:{f.pc=60;return;}
case 60:{f.pc=61;return;}
case 61:{f.pc=62;return;}
case 62:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
