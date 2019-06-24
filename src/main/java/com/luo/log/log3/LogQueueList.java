package com.luo.log.log3;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/13.13:25
 * @see
 */
public class LogQueueList<E> implements List<LogQueue<E>> {

    private LogQueue<E> head;
    private LogQueue<E> tail;
    private int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<LogQueue<E>> iterator() {
        return new QueueIterator<>(this);
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public synchronized boolean add(LogQueue<E> item) {
        if (head == null) {
            head = tail = item;
            head.next = tail;
            head.prev = tail;
            tail.next = head;
            tail.prev = head;
        } else {
            tail.next = item;
            head.prev = item;
            item.prev = tail;
            tail = item;
            item.next = head;
        }
        size ++;
        return true;
    }

    @Override
    public synchronized boolean remove(Object o) {
        LogQueue<E> logQueue = head;
        int idx = 0;
        while (logQueue != null && size > idx++) {
            if (logQueue.equals(o)) {
                if (head.equals(o)) {
                    if (size == 1) head = tail = null;
                    else {
                        head = head.next;
                        head.prev = tail;
                        tail.next = head;
                    }
                } else {
                    logQueue.prev.next = logQueue.next;
                    logQueue.next.prev = logQueue.prev;
                    if (tail.equals(logQueue)) {
                        tail = tail.prev;
                    }
                }
                size --;
                return true;
            }
            logQueue = logQueue.next;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends LogQueue<E>> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends LogQueue<E>> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public LogQueue<E> get(int index) {
        LogQueue<E> queue = head;
        for (int i = 1; i <= index; i++) {
            if (queue != null) {
                queue = queue.next;
                continue;
            }
            return null;
        }
        return queue;
    }

    @Override
    public LogQueue<E> set(int index, LogQueue<E> element) {
        return null;
    }

    @Override
    public void add(int index, LogQueue<E> element) {

    }

    @Override
    public LogQueue<E> remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<LogQueue<E>> listIterator() {
        return null;
    }

    @Override
    public ListIterator<LogQueue<E>> listIterator(int index) {
        return null;
    }

    @Override
    public List<LogQueue<E>> subList(int fromIndex, int toIndex) {
        return null;
    }

    /**
     * 迭代器
     * @param <E>
     */
    private class QueueIterator<E> implements Iterator<LogQueue<E>> {

        private LogQueueList<E> logQueueList;
        int cursor;

        private QueueIterator(LogQueueList<E> logQueueList) {
            this.logQueueList = logQueueList;
        }

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public LogQueue<E> next() {
            LogQueue<E> logQueue = logQueueList.get(cursor);
            cursor ++;
            return logQueue;
        }
    }
}
