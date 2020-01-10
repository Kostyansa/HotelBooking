package hotel.booking.service;

import hotel.booking.entity.BookingRequest;
import hotel.booking.utils.SynchronizedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    private final SynchronizedQueue<BookingRequest> requests;

    private final AtomicInteger countProduced;

    private final ReentrantLock consumeLock;

    private final Condition allConsumed;

    public Consumer(SynchronizedQueue<BookingRequest> requests, AtomicInteger countProduced, ReentrantLock consumeLock, Condition allConsumed) {
        this.requests = requests;
        this.countProduced = countProduced;
        this.consumeLock = consumeLock;
        this.allConsumed = allConsumed;
    }

    public void consume(){
        while ((countProduced.get() < HotelService.generateRequests) || (!requests.isEmpty())) {
            BookingRequest bookingRequest = requests.poll();
            if (bookingRequest == null){
                continue;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exc) {
                logger.error(String.format("Consumer: %s, has been interrupted while consuming", this.toString()));
            }
            logger.debug(String.format("Consumer: %s, consumed %s", this.toString(), bookingRequest.toString()));
        }
    }
}
