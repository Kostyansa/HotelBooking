package hotel.booking.service;

import hotel.booking.entity.BookingRequest;
import hotel.booking.utils.SynchronizedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HotelService {

    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);

    public static final int generateRequests = 15;

    private static final int producerThreadsCount = 3;

    private static final int consumerThreadsCount = 6;

    private static final int queueSize = 5;

    private final AtomicInteger countProduced = new AtomicInteger();

    private final SynchronizedQueue<BookingRequest> requests = new SynchronizedQueue<>(queueSize);

    private final List<Thread> producerThreads = new LinkedList<>();

    private final List<Thread> consumerThreads = new LinkedList<>();

    private final ReentrantLock consumeLock = new ReentrantLock();

    private final Condition allConsumed = consumeLock.newCondition();

    private void initializeThreads() {
        logger.debug("Started initializing threads");
        for (int i = 0; i < producerThreadsCount; i++) {
            Producer producer = new Producer(requests, countProduced);
            producerThreads.add(new Thread((producer::produce)));
        }
        for (int i = 0; i < consumerThreadsCount; i++) {
            Consumer consumer = new Consumer(requests, countProduced, consumeLock, allConsumed);
            consumerThreads.add(new Thread(consumer::consume));
        }
    }

    private void startThreads() {
        for (Thread thread : producerThreads) {
            thread.start();
        }
        for (Thread thread : consumerThreads) {
            thread.start();
        }
    }

    public void startProcessing() {
        initializeThreads();
        startThreads();
    }
}
