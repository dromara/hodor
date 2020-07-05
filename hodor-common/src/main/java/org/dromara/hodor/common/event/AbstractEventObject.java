package org.dromara.hodor.common.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractEventObject<V> implements EventObject<V> {

    private volatile Map<String, Set<ObjectListener<V>>> listeners;
    private final ReentrantLock lock = new ReentrantLock();

    public AbstractEventObject() {
        this.attachListener();
    }

    public abstract void attachListener();

    public void publish(V v, String eventType) {
        notifyListeners(new ObjectEvent<>(v, eventType));
    }

    public void addListener(ObjectListener<V> objectListener, String eventType) {
        lock.lock();
        try {
            if (listeners == null) {
                listeners = Maps.newConcurrentMap();
            }
            if (listeners.get(eventType) == null) {
                listeners.put(eventType, Sets.newCopyOnWriteArraySet(Lists.newArrayList(objectListener)));
            } else {
                listeners.get(eventType).add(objectListener);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeListener(ObjectListener<V> objectListener, String eventType) {
        if (listeners == null) {
            return;
        }
        lock.lock();
        try {
            Set<ObjectListener<V>> listenerSet = listeners.get(eventType);
            if (listenerSet == null) {
                return;
            }
            if (listenerSet.size() == 1) {
                listenerSet.clear();
                return;
            }
            listenerSet.remove(objectListener);
        } finally {
            lock.unlock();
        }
    }

    public void removeListener(String eventType) {
        lock.lock();
        try {
            listeners.remove(eventType);
        } finally {
            lock.unlock();
        }
    }

    public void notifyListeners(ObjectEvent<V> event) {
        if (listeners == null) {
            return;
        }
        String eventType = event.getEventType();
        if (listeners.get(eventType) != null) {
            Set<ObjectListener<V>> listenerSet = listeners.get(eventType);
            for (ObjectListener<V> listener : listenerSet) {
                listener.onEvent(event);
            }
        }
    }

    public void clearListener() {
        lock.lock();
        try {
            if (listeners != null) {
                listeners = null;
            }
        } finally {
            lock.unlock();
        }
    }

}
