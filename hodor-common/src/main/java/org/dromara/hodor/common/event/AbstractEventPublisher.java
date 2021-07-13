package org.dromara.hodor.common.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * abstract event publisher
 *
 * @param <V>
 */
public abstract class AbstractEventPublisher<V> implements EventPublisher<V> {

    private final Map<Object, Set<HodorEventListener<V>>> listeners = Maps.newConcurrentMap();

    private final ReentrantLock lock = new ReentrantLock();

    public AbstractEventPublisher() {
        this.registerListener();
    }

    public void publish(V v, Object eventType) {
        publish(new Event<>(v, eventType));
    }

    public void addListener(HodorEventListener<V> objectListener, Object eventType) {
        lock.lock();
        try {
            if (listeners.get(eventType) == null) {
                listeners.put(eventType, Sets.newCopyOnWriteArraySet(Lists.newArrayList(objectListener)));
            } else {
                listeners.get(eventType).add(objectListener);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeListener(HodorEventListener<V> objectListener, Object eventType) {
        lock.lock();
        try {
            Set<HodorEventListener<V>> listenerSet = listeners.get(eventType);
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

    public void removeListener(Object eventType) {
        lock.lock();
        try {
            listeners.remove(eventType);
        } finally {
            lock.unlock();
        }
    }

    public void publish(Event<V> event) {
        Set<HodorEventListener<V>> listeners = getListeners(event.getEventType());
        for (HodorEventListener<V> listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void clearListener() {
        lock.lock();
        try {
            listeners.clear();
        } finally {
            lock.unlock();
        }
    }

    public Set<HodorEventListener<V>> getListeners(Object eventType) {
        if (listeners.get(eventType) == null) {
            return Sets.newHashSet();
        }
        return listeners.get(eventType);
    }

}
