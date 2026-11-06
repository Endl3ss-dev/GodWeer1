/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.events.orbit.listeners;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.manager.events.orbit.listeners.IListener;

@Environment(value=EnvType.CLIENT)
public class LambdaListener
implements IListener {
    private static final LookupFactory LOOKUP_FACTORY;
    private static final Map<Method, MethodHandle> methodHandleCache;
    private final Class<?> target;
    private final boolean isStatic;
    private final int priority;
    private final Consumer<Object> executor;
    public final Class<?> owner;

    public LambdaListener(Factory factory, Class<?> klass, Object object, Method method) {
        this.target = method.getParameters()[0].getType();
        this.isStatic = Modifier.isStatic(method.getModifiers());
        this.priority = method.getAnnotation(EventHandler.class).priority();
        this.owner = klass;
        try {
            this.executor = this.isStatic ? LambdaListener.staticLambdaFactory(factory, klass, method).invoke() : LambdaListener.instanceLambdaFactory(factory, klass, method).invoke(object);
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    private static MethodHandle staticLambdaFactory(Factory factory, Class<?> klass, Method method) throws Throwable {
        String name = method.getName();
        MethodHandles.Lookup lookup = LOOKUP_FACTORY.of(factory, klass);
        MethodType methodType = MethodType.methodType(Void.TYPE, method.getParameters()[0].getType());
        MethodHandle methodHandle = lookup.findStatic(klass, name, methodType);
        MethodType invokedType = MethodType.methodType(Consumer.class);
        return LambdaMetafactory.metafactory(lookup, "accept", invokedType, MethodType.methodType(Void.TYPE, Object.class), methodHandle, methodType).getTarget();
    }

    private static MethodHandle instanceLambdaFactory(Factory factory, Class<?> klass, Method method) throws Throwable {
        MethodHandle lambdaFactory = methodHandleCache.get(method);
        if (lambdaFactory != null) {
            return lambdaFactory;
        }
        String name = method.getName();
        MethodHandles.Lookup lookup = LOOKUP_FACTORY.of(factory, klass);
        MethodType methodType = MethodType.methodType(Void.TYPE, method.getParameters()[0].getType());
        MethodHandle methodHandle = lookup.findVirtual(klass, name, methodType);
        MethodType invokedType = MethodType.methodType(Consumer.class, klass);
        lambdaFactory = LambdaMetafactory.metafactory(lookup, "accept", invokedType, MethodType.methodType(Void.TYPE, Object.class), methodHandle, methodType).getTarget();
        methodHandleCache.put(method, lambdaFactory);
        return lambdaFactory;
    }

    @Override
    public void call(Object event) {
        this.executor.accept(event);
    }

    @Override
    public Class<?> getTarget() {
        return this.target;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean isStatic() {
        return this.isStatic;
    }

    public static boolean requireLambdaFactoryRegistration() {
        return LambdaListener.LOOKUP_FACTORY.requireLambdaFactory;
    }

    static {
        LookupFactory lookupFactory;
        methodHandleCache = new ConcurrentHashMap<Method, MethodHandle>();
        try {
            try {
                final Method privateLookupInMethod = MethodHandles.class.getDeclaredMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
                lookupFactory = new LookupFactory(true){

                    @Override
                    public MethodHandles.Lookup of(Factory lambdaFactory, Class<?> klass) throws ReflectiveOperationException {
                        return lambdaFactory.create(privateLookupInMethod, klass);
                    }
                };
            }
            catch (NoSuchMethodException e) {
                final Constructor lookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
                lookupConstructor.setAccessible(true);
                lookupFactory = new LookupFactory(false){

                    @Override
                    public MethodHandles.Lookup of(Factory lambdaFactory, Class<?> klass) throws ReflectiveOperationException {
                        return (MethodHandles.Lookup)lookupConstructor.newInstance(klass);
                    }
                };
            }
        }
        catch (ReflectiveOperationException | SecurityException e) {
            lookupFactory = new LookupFactory(false){

                @Override
                public MethodHandles.Lookup of(Factory lambdaFactory, Class<?> klass) {
                    return MethodHandles.publicLookup().in(klass);
                }
            };
        }
        LOOKUP_FACTORY = lookupFactory;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Factory {
        public MethodHandles.Lookup create(Method var1, Class<?> var2) throws ReflectiveOperationException;
    }

    @Environment(value=EnvType.CLIENT)
    private static abstract class LookupFactory {
        public final boolean requireLambdaFactory;

        public LookupFactory(boolean requireLambdaFactory) {
            this.requireLambdaFactory = requireLambdaFactory;
        }

        public abstract MethodHandles.Lookup of(Factory var1, Class<?> var2) throws ReflectiveOperationException;
    }
}

