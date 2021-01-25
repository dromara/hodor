package org.dromara.hodor.common.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 环形队列<br/>
 * 1、支持可动态调整入队拒绝策略
 *
 * @author tomgs
 * @since 2021/1/21
 */
public class CircleQueue<T> extends AbstractQueue<T> {

  /**
   * The maximum size of array to allocate.
   * Some VMs reserve some header words in an array.
   * Attempts to allocate larger arrays may result in
   * OutOfMemoryError: Requested array size exceeds VM limit
   */
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  private static final int DEFAULT_CAPACITY = 10;

  private Object[] elementData;

  private int size;

  private int head;

  private int tail;

  private RejectedEnqueueHandler<T> rejectedEnqueueHandler;

  public CircleQueue(int initialCapacity, RejectedEnqueueHandler<T> rejectedEnqueueHandler) {
    this.elementData = new Object[initialCapacity];
    this.rejectedEnqueueHandler = rejectedEnqueueHandler;
    this.size = 0;
    this.head = 0;
    this.tail = 0;
  }

  public CircleQueue() {
    this(DEFAULT_CAPACITY, new AbortEnqueuePolicy<>());
  }

  @Override
  public Iterator<T> iterator() {
    throw new UnsupportedOperationException("un support iterator operation");
  }

  @Override
  public int size() {
    if (isEmpty()) {
      return 0;
    }
    return size;
  }

  public int getCapacity() {
    return elementData.length;
  }

  @Override
  public boolean isEmpty() {
    return head == tail && elementData[head] == null;
  }

  public boolean isFull() {
    return head == tail && elementData[head] != null;
  }

  /**
   * 扩缩容操作
   */
  public void resize(int newCapacity) {
    if (newCapacity <= 0) {
      return;
    }
    // overflow-conscious code
    if (newCapacity < DEFAULT_CAPACITY) {
      newCapacity = DEFAULT_CAPACITY;
    }
    if (newCapacity <= size) {
      newCapacity = size + newCapacity;
    }
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(newCapacity);
    }
    if (newCapacity == getCapacity()) {
      return;
    }

    Object[] newElementData = new Object[newCapacity];
    // 扩容
    if (newElementData.length > elementData.length) {
      for (int i = head; i < size + tail; i++) {
        newElementData[i] = elementData[i % size];
      }
      elementData = newElementData;
      tail = size + head;
    } else {
      // 缩容
      System.arraycopy(elementData, head, newElementData, 0, size);
      elementData = newElementData;
      head = 0;
      tail = size;
    }
  }

  private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) // overflow
      throw new OutOfMemoryError();
    return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
  }

  @Override
  public boolean offer(T e) {
    if (e == null) {
      throw new IllegalArgumentException("queue element must be not null.");
    }

    if (isFull()) {
      // 执行相关拒绝策略
      this.getRejectedEnqueueHandler().rejectedExecution(e, this);
      // double check
      if (isFull()) {
        throw new IndexOutOfBoundsException("Exceed queue capacity, Please reset the enqueue rejection policy.");
      }
    }

    elementData[tail++] = e;
    tail = (tail == elementData.length) ? 0 : tail;
    size++;
    return true;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T poll() {
    if (isEmpty()) {
      throw new NoSuchElementException("queue is empty, no such element to poll.");
    }

    Object t = elementData[head];
    elementData[head++] = null;
    head = (head == elementData.length) ? 0 : head;
    size--;

    if (size <= getCapacity() >> 2) {
      resize(getCapacity() >> 1);
    }

    return (T) t;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T peek() {
    if (isEmpty()) {
      return null;
    }
    return (T) elementData[head];
  }

  public RejectedEnqueueHandler<T> getRejectedEnqueueHandler() {
    return rejectedEnqueueHandler;
  }

  public void setRejectedEnqueueHandler(RejectedEnqueueHandler<T> rejectedEnqueueHandler) {
    this.rejectedEnqueueHandler = rejectedEnqueueHandler;
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "[]";
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("Queue size: %s, capacity: %s;\n", size(), getCapacity()));
      sb.append("[");
      if (tail > head) {
        for (int i = head; i < tail; i++) {
          sb.append(elementData[i].toString()).append(", ");
        }
      } else {
        for (int i = head; i < size + tail; i++) {
          sb.append(elementData[i % size].toString()).append(",");
        }
      }
      return sb.toString().substring(0, sb.length() - 1) + "]";
    }

  }

}
