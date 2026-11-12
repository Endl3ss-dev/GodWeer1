package dev.zprestige.sourceview;

import java.lang.reflect.*;
import bench.prestige.portable.Bridge;

/** Explicit JVM local slots and operand values used by restored control-flow methods. */
public final class FlowFrame {
 public final Object[] locals;
 private final Object[] values;
 private final int[] widths;
 private int top;
 public int pc;
 public boolean done;
 public Object result;
 public FlowFrame(int localCount,int stackCount){locals=new Object[localCount];values=new Object[stackCount+8];widths=new int[stackCount+8];}
 public void push(Object value,int width){values[top]=value;widths[top++]=width;}
 public Object pop(){Object value=values[--top];values[top]=null;return value;}
 public void clear(){while(top>0)pop();}
 public static int i(Object value){return value instanceof Boolean b?b?1:0:value instanceof Character c?c:((Number)value).intValue();}
 public static long j(Object value){return ((Number)value).longValue();}
 public static float f(Object value){return ((Number)value).floatValue();}
 public static double d(Object value){return ((Number)value).doubleValue();}
 private static final ClassValue<java.util.concurrent.ConcurrentHashMap<String,Field>> RECORD_FIELDS=new ClassValue<>(){protected java.util.concurrent.ConcurrentHashMap<String,Field> computeValue(Class<?> type){return new java.util.concurrent.ConcurrentHashMap<>();}};
 public static Object recordField(Object value,String name)throws ReflectiveOperationException{if(value==null)throw new NullPointerException();var fields=RECORD_FIELDS.get(value.getClass());Field field=fields.get(name);if(field==null){field=value.getClass().getDeclaredField(name);field.setAccessible(true);Field previous=fields.putIfAbsent(name,field);if(previous!=null)field=previous;}return field.get(value);}
 private static Field declaredField(Class<?> owner,String name)throws NoSuchFieldException{try{return owner.getDeclaredField(name);}catch(NoSuchFieldException missing){for(Class<?> parent:owner.getInterfaces())try{return declaredField(parent,name);}catch(NoSuchFieldException ignored){}if(owner.getSuperclass()!=null)return declaredField(owner.getSuperclass(),name);throw missing;}}
 public static void staticField(Class<?> owner,String name,Object value)throws ReflectiveOperationException{var fields=RECORD_FIELDS.get(owner);Field field=fields.get(name);if(field==null){field=declaredField(owner,name);Field previous=fields.putIfAbsent(name,field);if(previous!=null)field=previous;}Class<?> holder=field.getDeclaringClass();Class.forName(holder.getName(),true,holder.getClassLoader());Bridge.setStaticField(holder,field,value);}
 public static void instanceField(Class<?> owner,Object receiver,String name,Object value)throws ReflectiveOperationException{if(receiver==null)throw new NullPointerException();var fields=RECORD_FIELDS.get(owner);Field field=fields.get(name);if(field==null){field=declaredField(owner,name);Field previous=fields.putIfAbsent(name,field);if(previous!=null)field=previous;}Bridge.setInstanceField(field.getDeclaringClass(),field,receiver,value);}
 private record Slot(Object value,int width){}
 private Slot take(){int width=widths[top-1];return new Slot(pop(),width);}
 private void put(Slot slot){push(slot.value,slot.width);}
 private static void one(Slot slot){if(slot.width!=1)throw new VerifyError("Expected single-width operand");}
 public void rearrange(int opcode){
  Slot a=take(),b,c,e;
  switch(opcode){
   case 87->one(a);
   case 88->{if(a.width==1){b=take();one(b);}}
   case 89->{one(a);put(a);put(a);}
   case 90->{one(a);b=take();one(b);put(a);put(b);put(a);}
   case 91->{one(a);b=take();if(b.width==2){put(a);put(b);put(a);}else{c=take();one(c);put(a);put(c);put(b);put(a);}}
   case 92->{if(a.width==2){put(a);put(a);}else{b=take();one(b);put(b);put(a);put(b);put(a);}}
   case 93->{if(a.width==2){b=take();one(b);put(a);put(b);put(a);}else{b=take();one(b);c=take();one(c);put(b);put(a);put(c);put(b);put(a);}}
   case 94->{if(a.width==2){b=take();if(b.width==2){put(a);put(b);put(a);}else{c=take();one(c);put(a);put(c);put(b);put(a);}}else{b=take();one(b);c=take();if(c.width==2){put(b);put(a);put(c);put(b);put(a);}else{e=take();one(e);put(b);put(a);put(e);put(c);put(b);put(a);}}}
   case 95->{one(a);b=take();one(b);put(a);put(b);}
   default->throw new VerifyError("Unknown stack operation "+opcode);
  }
 }
 private record Pending(Class<?> type){}
 public static Object pending(Class<?> type)throws ClassNotFoundException {Class.forName(type.getName(),true,type.getClassLoader());return new Pending(type);}
 public void construct(Object value,Class<?>[] types,Object[] arguments)throws Throwable{
  if(!(value instanceof Pending pending))throw new VerifyError("Constructor receiver is already initialized");
  Constructor<?> constructor=pending.type.getDeclaredConstructor(types);if(!constructor.canAccess(null))constructor.setAccessible(true);
  Object instance;try{instance=constructor.newInstance(arguments);}catch(InvocationTargetException error){throw error.getCause();}
  for(int n=0;n<top;n++)if(values[n]==value)values[n]=instance;
  for(int n=0;n<locals.length;n++)if(locals[n]==value)locals[n]=instance;
 }
 public static void monitorEnter(Object value){Bridge.monitorEnter(value);}
 public static void monitorExit(Object value){Bridge.monitorExit(value);}
 public static int compareFloat(float a,float b,int nan){return Float.isNaN(a)||Float.isNaN(b)?nan:a==b?0:a<b?-1:1;}
 public static int compareDouble(double a,double b,int nan){return Double.isNaN(a)||Double.isNaN(b)?nan:a==b?0:a<b?-1:1;}
}
