package hotel.booking.utils;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class SynchronizedQueue<T> extends AbstractQueue<T> implements BlockingQueue<T> {

    static class Node<T>{
        T item;

        Node<T> next;

        Node(T item){
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
        if (capacity <= 0){
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        head = new Node<>(null);
        last = head;
    }

    @Override
    public boolean offer(T t) {
        if (t == null){
            throw new NullPointerException();
        }
        if (count.get() == capacity){
            return false;
        }
        int c = -1;
        Node<T> node = new Node<>(t);
        putLock.lock();
        try{
            if (count.get() < capacity){
                last.next = node;
                last = node;
                c = count.getAndIncrement();
                if (c <= capacity){
                    notFull.signal();
                }
            }
        }
        finally {
            putLock.unlock();
        }
        if (c == 0){
            notEmpty.signal();
        }
        return c > -1;
    }

    @Override
    public void put(T t) throws InterruptedException {
        if (t == null){
            throw new NullPointerException();
        }
        int c = -1;
        Node<T> node = new Node<>(t);
        putLock.lock();
        try{
            if (count.get() == capacity){
                notFull.await();
            }
            last.next = node;
            last = node;
            c = count.getAndIncrement();
            if (c <= capacity){
                notFull.signal();
            }
        }
        finally {
            putLock.unlock();
        }
        if (c == 0){
            popLock.lock();
            notEmpty.signal();
            popLock.unlock();
        }
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public T poll() {
        return null;
    }

    @Override
    public T peek() {
        return null;
    }
    @Override
    public T take() throws InterruptedException {
        return null;
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return capacity - count.get();
    }

    @Override
    public int size() {
        return count.get();
    }

    @Override
    public Iterator<T> iterator() {
        return null;
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
