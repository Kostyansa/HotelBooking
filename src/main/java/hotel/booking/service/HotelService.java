package hotel.booking.service;

import hotel.booking.entity.BookingRequest;
import hotel.booking.utils.SynchronizedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HotelService {

    private static final Logger logger = LoggerFactory.getLogger(HotelService.class);

    private final int producerThreadsCount;

    private final int consumerThreadsCount;

    public final int requestsToGenerate;

    private final Duration timeout;

    private final AtomicInteger countProduced = new AtomicInteger();

    private final SynchronizedQueue<BookingRequest> requests;

    private final List<Thread> producerThreads = new LinkedList<>();

    private final List<Thread> consumerThreads = new LinkedList<>();

    public HotelService(int producerThreadsCount, int consumerThreadsCount, int queueSize, int requestsToGenerate, Duration timeout) {
        if (consumerThreadsCount < 1) {
            throw new IllegalArgumentException("There must be at least one Consumer Thread");
        }
        if (requestsToGenerate < 0) {
            throw new IllegalArgumentException("Amount of the request to generate should be positive or zero");
        }
        if (timeout.isNegative()) {
            throw new IllegalArgumentException("Timeout must be positive or zero");
        }
        this.producerThreadsCount = producerThreadsCount;
        this.consumerThreadsCount = consumerThreadsCount;
        this.requestsToGenerate = requestsToGenerate;
        this.timeout = timeout;
        requests = new SynchronizedQueue<>(queueSize);
    }

    private void initializeThreads() {
        logger.debug("Started initializing threads");
        for (int i = 0; i < producerThreadsCount; i++) {
            Producer producer = new Producer(requests, countProduced, requestsToGenerate);
            producerThreads.add(producer);
        }
        for (int i = 0; i < consumerThreadsCount; i++) {
            Consumer consumer = new Consumer(requests, timeout);
            consumerThreads.add(consumer);
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
        for (Thread thread : producerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {

                logger.error("Producer thread has been interrupted", e);
            }
        }
        for (Thread thread : consumerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {

                logger.error("Consumer thread has been interrupted", e);
            }
        }
    }
}
