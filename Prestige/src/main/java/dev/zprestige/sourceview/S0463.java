package dev.zprestige.sourceview;
import dev.zprestige.sourceview.S0292;
import dev.zprestige.sourceview.S0391;
import dev.zprestige.sourceview.S0464;


public record S0463(int bN, int bO) implements S0391 {

   @Override
   public final boolean equals(Object var1) {
try {

      return S0464.__indy$0(this, var1);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public final String toString() {
try {

      return S0464.__indy$1(this);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public final int hashCode() {
try {

      return S0464.__indy$2(this);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @Override
   public void a(S0292 var1) {
try {

      long var2 = S0464.b ^ 8226728227095L;
      S0464.__indy$3(this.bN, -8381248925144454856L, var2);
   
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
if(f.pc>=14&&f.pc<30&&error instanceof java.lang.MatchException){f.clear();f.push(error,1);f.pc=32;continue;}
throw dev.zprestige.sourceview.Sneaky.raise(error);}}
}


private void __flow$5$0(dev.zprestige.sourceview.FlowFrame f)throws Throwable{switch(f.pc){
case 0:{f.push(dev.zprestige.sourceview.S0464.b,2);f.pc=1;return;}
case 1:{f.push(112024989255811L,2);f.pc=2;return;}
case 2:{long b=dev.zprestige.sourceview.FlowFrame.j(f.pop()),a=dev.zprestige.sourceview.FlowFrame.j(f.pop());f.push(a^b,2);f.pc=3;return;}
case 3:{f.locals[1]=f.pop();f.pc=4;return;}
case 4:{f.push(-992188966323963432L,2);f.pc=5;return;}
case 5:{f.push(f.locals[1],2);f.pc=6;return;}
case 6:{Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0464.__indy$4(dev.zprestige.sourceview.FlowFrame.j(a0),dev.zprestige.sourceview.FlowFrame.j(a1)),1);f.pc=7;return;}
case 7:{f.locals[3]=f.pop();f.pc=8;return;}
case 8:{f.pc=9;return;}
case 9:{f.pc=10;return;}
case 10:{f.push(f.locals[0],1);f.pc=11;return;}
case 11:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bO"),1);f.pc=12;return;}
case 12:{f.push(f.locals[3],1);f.pc=13;return;}
case 13:{if(f.pop()!=null){f.pc=38;return;}f.pc=14;return;}
case 14:{f.pc=15;return;}
case 15:{f.push(f.locals[0],1);f.pc=16;return;}
case 16:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bN"),1);f.pc=17;return;}
case 17:{int b=dev.zprestige.sourceview.FlowFrame.i(f.pop()),a=dev.zprestige.sourceview.FlowFrame.i(f.pop());if(a==b){f.pc=43;return;}f.pc=18;return;}
case 18:{f.pc=19;return;}
case 19:{f.pc=26;return;}
case 20:{f.pc=21;return;}
case 21:{f.pc=22;return;}
case 22:{f.push(-992081400257085733L,2);f.pc=23;return;}
case 23:{f.push(f.locals[1],2);f.pc=24;return;}
case 24:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0464.__indy$5(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=25;return;}
case 25:{throw (Throwable)f.pop();}
case 26:{f.pc=27;return;}
case 27:{f.pc=28;return;}
case 28:{f.push(f.locals[0],1);f.pc=29;return;}
case 29:{f.push(dev.zprestige.sourceview.FlowFrame.recordField(f.pop(),"bO"),1);f.pc=30;return;}
case 30:{f.pc=31;return;}
case 31:{f.pc=38;return;}
case 32:{f.pc=33;return;}
case 33:{f.pc=34;return;}
case 34:{f.push(-992081400257085733L,2);f.pc=35;return;}
case 35:{f.push(f.locals[1],2);f.pc=36;return;}
case 36:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();f.push(dev.zprestige.sourceview.S0464.__indy$6(((java.lang.Object)(Object)(a0)),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2)),1);f.pc=37;return;}
case 37:{throw (Throwable)f.pop();}
case 38:{f.pc=39;return;}
case 39:{f.pc=40;return;}
case 40:{f.push(-992005660408966996L,2);f.pc=41;return;}
case 41:{f.push(f.locals[1],2);f.pc=42;return;}
case 42:{Object a2=f.pop();Object a1=f.pop();Object a0=f.pop();dev.zprestige.sourceview.S0464.__indy$7(dev.zprestige.sourceview.FlowFrame.i(a0),dev.zprestige.sourceview.FlowFrame.j(a1),dev.zprestige.sourceview.FlowFrame.j(a2));f.pc=43;return;}
case 43:{f.pc=44;return;}
case 44:{f.pc=45;return;}
case 45:{f.pc=46;return;}
case 46:{f.done=true;return;}
default:throw new VerifyError("Invalid source PC "+f.pc);}}
}
