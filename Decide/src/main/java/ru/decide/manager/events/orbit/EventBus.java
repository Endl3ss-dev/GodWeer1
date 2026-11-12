/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.manager.events.orbit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.manager.events.orbit.ICancellable;
import ru.decide.manager.events.orbit.IEventBus;
import ru.decide.manager.events.orbit.NoLambdaFactoryException;
import ru.decide.manager.events.orbit.listeners.ArrayBackedImmutableList;
import ru.decide.manager.events.orbit.listeners.IListener;
import ru.decide.manager.events.orbit.listeners.LambdaListener;
import ru.decide.manager.events.orbit.listeners.ListOperations;

@Environment(value=EnvType.CLIENT)
public class EventBus
implements IEventBus {
    private final Map<Object, List<IListener>> listenerCache;
    private final Map<Class<?>, List<IListener>> staticListenerCache;
    private final Map<Class<?>, List<IListener>> listenerMap;
    private final Supplier<List<IListener>> listenerListFactory;
    private final List<LambdaFactoryInfo> lambdaFactoryInfos = new CopyOnWriteArrayList<LambdaFactoryInfo>();

    public EventBus(Map<Object, List<IListener>> listenerCache, Map<Class<?>, List<IListener>> staticListenerCache, Map<Class<?>, List<IListener>> listenerMap, Supplier<List<IListener>> listenerListFactory) {
        this.listenerCache = listenerCache;
        this.staticListenerCache = staticListenerCache;
        this.listenerMap = listenerMap;
        this.listenerListFactory = listenerListFactory;
    }

    public static EventBus threadSafe() {
        return new EventBus(Collections.synchronizedMap(new IdentityHashMap()), new ConcurrentHashMap(), new ConcurrentHashMap(), CopyOnWriteArrayList::new);
    }

    public static EventBus threadUnsafe() {
        return new EventBus(new IdentityHashMap<Object, List<IListener>>(), new IdentityHashMap(), new IdentityHashMap(), CopyOnWriteArrayList::new);
    }

    @Override
    public void registerLambdaFactory(String packagePrefix, LambdaListener.Factory factory) {
        this.lambdaFactoryInfos.add(new LambdaFactoryInfo(packagePrefix, factory));
    }

    @Override
    public boolean isListening(Class<?> eventClass) {
        List<IListener> listeners = this.listenerMap.get(eventClass);
        return listeners != null && !listeners.isEmpty();
    }

    public boolean isSubscribed(IListener listener) {
        List<IListener> listeners = this.listenerMap.get(listener.getTarget());
        return listeners != null && ListOperations.contains(listeners, listener);
    }

    public boolean isSubscribed(Object object) {
        return this.listenerCache.containsKey(object);
    }

    public boolean isSubscribed(Class<?> staticListener) {
        List<IListener> listeners = this.staticListenerCache.get(staticListener);
        for (IListener listener : listeners) {
            if (!(listener instanceof LambdaListener)) continue;
            LambdaListener lambdaListener = (LambdaListener)listener;
            if (lambdaListener.owner != staticListener) break;
            if (this.isSubscribed(listener)) continue;
            return true;
        }
        return true;
    }

    public boolean areAnySubscribed(Class<?> staticListener) {
        List<IListener> listeners = this.staticListenerCache.get(staticListener);
        if (listeners == null) {
            return false;
        }
        for (IListener listener : listeners) {
            if (!this.isSubscribed(listener)) continue;
            return true;
        }
        return false;
    }

    public boolean areAllSubscribed(Class<?> staticListener) {
        List<IListener> listeners = this.staticListenerCache.get(staticListener);
        if (listeners == null) {
            return false;
        }
        for (IListener listener : listeners) {
            if (this.isSubscribed(listener)) continue;
            return false;
        }
        return true;
    }

    @Override
    public <T> T post(T event) {
        List<IListener> listeners = this.listenerMap.get(event.getClass());
        if (listeners != null) {
            for (IListener listener : listeners) {
                try {
                    listener.call(event);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return event;
    }

    @Override
    public <T extends ICancellable> T post(T event) {
        List<IListener> listeners = this.listenerMap.get(event.getClass());
        if (listeners != null) {
            event.setCancelled(false);
            for (IListener listener : listeners) {
                try {
                    listener.call(event);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (!event.isCancelled()) continue;
                break;
            }
        }
        return event;
    }

    @Override
    public void subscribe(Object object) {
        if (this.listenerCache.containsKey(object)) {
            return;
        }
        this.subscribe(this.getInstanceListeners(object), false);
    }

    @Override
    public void subscribe(Class<?> klass) {
        List<IListener> listeners = this.getStaticListeners(klass);
        this.subscribe(listeners, true);
    }

    @Override
    public void subscribe(IListener listener) {
        this.subscribe(listener, true);
    }

    private void subscribe(List<IListener> listeners, boolean check) {
        for (IListener listener : listeners) {
            this.subscribe(listener, check);
        }
    }

    private void subscribe(IListener listener, boolean check) {
        List listeners = this.listenerMap.computeIfAbsent(listener.getTarget(), klass -> this.listenerListFactory.get());
        if (!check || !ListOperations.contains(listeners, listener)) {
            ListOperations.insert(listeners, listener);
        }
    }

    @Override
    public void unsubscribe(Object object) {
        List<IListener> listeners = this.listenerCache.get(object);
        if (listeners != null) {
            this.unsubscribe(listeners);
            this.listenerCache.remove(object);
        }
    }

    @Override
    public void unsubscribe(Class<?> klass) {
        List<IListener> listeners = this.staticListenerCache.get(klass);
        if (listeners != null) {
            this.unsubscribe(listeners);
        }
    }

    @Override
    public void unsubscribe(IListener listener) {
        List<IListener> listeners = this.listenerMap.get(listener.getTarget());
        if (listeners != null) {
            ListOperations.remove(listeners, listener);
        }
    }

    private void unsubscribe(List<IListener> listeners) {
        for (IListener listener : listeners) {
            this.unsubscribe(listener);
        }
    }

    private List<IListener> getStaticListeners(Class<?> klass) {
        return this.staticListenerCache.computeIfAbsent(klass, o -> this.getListeners((Class<?>)o, null, true));
    }

    private List<IListener> getInstanceListeners(Object object) {
        return this.listenerCache.computeIfAbsent(object, o -> this.getListeners(o.getClass(), o, false));
    }

    private List<IListener> getListeners(Class<?> klass, Object object, boolean staticOnly) {
        ArrayList<IListener> listeners = new ArrayList<IListener>();
        this.getListeners(listeners, klass, object, staticOnly);
        return new ArrayBackedImmutableList<IListener>(listeners);
    }

    private void getListeners(List<IListener> listeners, Class<?> klass, Object object, boolean staticOnly) {
        while (klass != null) {
            LambdaListener.Factory factory = null;
            for (Method method : klass.getDeclaredMethods()) {
                if (!this.isValid(method, staticOnly)) continue;
                if (LambdaListener.requireLambdaFactoryRegistration() && factory == null) {
                    factory = this.getLambdaFactory(klass);
                }
                listeners.add(new LambdaListener(factory, klass, object, method));
            }
            klass = klass.getSuperclass();
        }
    }

    private boolean isValid(Method method, boolean staticOnly) {
        if (staticOnly && !Modifier.isStatic(method.getModifiers())) {
            return false;
        }
        if (!method.isAnnotationPresent(EventHandler.class)) {
            return false;
        }
        if (method.getReturnType() != Void.TYPE) {
            return false;
        }
        if (method.getParameterCount() != 1) {
            return false;
        }
        return !method.getParameters()[0].getType().isPrimitive();
    }

    private LambdaListener.Factory getLambdaFactory(Class<?> klass) {
        for (LambdaFactoryInfo info : this.lambdaFactoryInfos) {
            if (!klass.getName().startsWith(info.packagePrefix)) continue;
            return info.factory;
        }
        throw new NoLambdaFactoryException(klass);
    }

    @Environment(value=EnvType.CLIENT)
    private record LambdaFactoryInfo(String packagePrefix, LambdaListener.Factory factory) {
    }
}

