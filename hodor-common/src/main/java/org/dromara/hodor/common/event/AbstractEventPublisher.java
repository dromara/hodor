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
 * @author tomgs
 */
public abstract class AbstractEventPublisher<V> implements EventPublisher<V> {

    private final ReentrantLock lock = new ReentrantLock();

    private final Map<Object, Set<HodorEventListener<V>>> listeners = Maps.newConcurrentMap();

    private final Set<HodorEventListener<V>> EMPTY_LISTENERS = Sets.newConcurrentHashSet();

    public AbstractEventPublisher() {
        this.registryListener();
    }

    @Override
    public void addListener(HodorEventListener<V> listener) {
        addListener(listener, Event.DEFAULT_TYPE);
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

    public void removeListener(Object eventType, HodorEventListener<V> objectListener) {
        lock.lock();
        try {
            Set<HodorEventListener<V>> listenerSet = listeners.get(eventType);
            if (listenerSet == null) {
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

    protected Set<HodorEventListener<V>> getListeners(Object eventType) {
        if (listeners.get(eventType) == null) {
            return EMPTY_LISTENERS;
        }
        return listeners.get(eventType);
    }

}
