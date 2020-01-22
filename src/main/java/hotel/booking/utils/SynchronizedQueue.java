package hotel.booking.utils;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class SynchronizedQueue<T> implements BlockingQueue<T> {

    static class Node<T> {
        T item;

        Node<T> next;

        Node(T item) {
            this.item = item;
        }
    }

    private final int capacity;

    private final AtomicInteger count = new AtomicInteger();

    Node<T> head;

    private Node<T> last;

    private final ReentrantLock popLock = new ReentrantLock();

    private final Condition notEmpty = popLock.newCondition();

    private final ReentrantLock putLock = new ReentrantLock();

    private final Condition notFull = putLock.newCondition();

    public SynchronizedQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        head = new Node<>(null);
        last = head;
    }

    void signalNotEmpty() {
        popLock.lock();
        notEmpty.signal();
        popLock.unlock();
    }

    void signalNotFull() {
        putLock.lock();
        notFull.signal();
        putLock.unlock();
    }

    private void addInQueue(Node<T> node) {
        last.next = node;
        last = node;
    }

    private T popFromQueue() {
        head = head.next;
        return head.item;
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        if (t == null) {
            throw new NullPointerException();
        }
        if (count.get() == capacity) {
            return false;
        }
        int c = -1;
        Node<T> node = new Node<>(t);
        putLock.lock();
        try {
            if (count.get() == capacity) {
                if (timeout <= 0) {
                    return false;
                }
                notFull.await(timeout, unit);
            }
            addInQueue(node);
            c = count.getAndIncrement();
            if (c <= capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return c > -1;
    }

    @Override
    public boolean offer(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        if (count.get() == capacity) {
            return false;
        }
        int c = -1;
        Node<T> node = new Node<>(t);
        putLock.lock();
        try {
            if (count.get() < capacity) {
                addInQueue(node);
                c = count.getAndIncrement();
                if (c <= capacity) {
                    notFull.signal();
                }
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return c > -1;
    }

    @Override
    public T remove() {
        return null;
    }

    @Override
    public void put(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        int c = -1;
        Node<T> node = new Node<>(t);
        putLock.lock();
        try {
            while (count.get() == capacity) {
                notFull.awaitUninterruptibly();
            }
            addInQueue(node);
            c = count.getAndIncrement();
            if (c <= capacity) {
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
    }

    @Override
    public boolean add(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        int c = -1;
        Node<T> node = new Node<>(t);
        putLock.lock();
        try {
            if (count.get() < capacity) {
                addInQueue(node);
                c = count.getAndIncrement();
                if (c <= capacity) {
                    notFull.signal();
                }
            } else {
                throw new IllegalStateException("Queue is full");
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0) {
            signalNotEmpty();
        }
        return true;
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        int c = -1;
        popLock.lock();
        T item = null;
        try {
            if (count.get() == 0) {
                if (timeout <= 0) {
                    return null;
                }
                notEmpty.await(timeout, unit);
            }
            item = popFromQueue();
            c = count.getAndDecrement();
            if (c > 1) {
                notEmpty.signal();
            }
        } finally {
            popLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return item;
    }

    @Override
    public T poll() {
        int c = -1;
        popLock.lock();
        T item = null;
        try {
            if (count.get() != 0) {
                item = popFromQueue();
                c = count.getAndDecrement();
                if (c > 1) {
                    notEmpty.signal();
                }
            }
        } finally {
            popLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return item;
    }

    @Override
    public T element() {
        return null;
    }

    @Override
    public T take() {
        int c = -1;
        popLock.lock();
        T item = null;
        try {
            while (count.get() == 0) {
                notEmpty.awaitUninterruptibly();
            }
            item = popFromQueue();
            c = count.getAndDecrement();
            if (c > 1) {
                notEmpty.signal();
            }
        } finally {
            popLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return item;
    }

    @Override
    public T peek() {
        if (count.get() == 0) {
            return null;
        }
        popLock.lock();
        try {
            Node<T> first = head.next;
            if (first == null)
                return null;
            else
                return first.item;
        } finally {
            popLock.unlock();
        }
    }

    @Override
    public int remainingCapacity() {
        return capacity - count.get();
    }

    @Override
    public boolean remove(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new NotImplementedException();
    }

    @Override
    public void clear() {
        throw new NotImplementedException();
    }

    @Override
    public boolean contains(Object o) {
        throw new NotImplementedException();
    }

    @Override
    public int size() {
        return count.get();
    }

    @Override
    public boolean isEmpty() {
        return count.get() == 0;
    }

    @Override
    public Iterator<T> iterator() {
        throw new NotImplementedException();
    }

    @Override
    public Object[] toArray() {
        throw new NotImplementedException();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new NotImplementedException();
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        throw new NotImplementedException();
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {

        throw new NotImplementedException();
    }
}
