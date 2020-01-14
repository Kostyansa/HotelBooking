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
        this.producerThreadsCount = producerThreadsCount;
        this.consumerThreadsCount = consumerThreadsCount;
        this.requestsToGenerate = requestsToGenerate;
        this.timeout = timeout;
        requests = new SynchronizedQueue<>(queueSize);
    }

    public HotelService() {
        this(6, 3, 5, 15, Duration.ofSeconds(1));
    }

    private void initializeThreads() {
        logger.debug("Started initializing threads");
        for (int i = 0; i < producerThreadsCount; i++) {
            Producer producer = new Producer(requests, countProduced, requestsToGenerate);
            producerThreads.add(new Thread((producer::produce)));
        }
        for (int i = 0; i < consumerThreadsCount; i++) {
            Consumer consumer = new Consumer(requests, timeout);
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
        for (Thread thread : producerThreads){

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("Producer thread has been interrupted", e);
            }
        }
        for (Thread thread : consumerThreads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("Producer thread has been interrupted", e);
            }
        }
    }
}
