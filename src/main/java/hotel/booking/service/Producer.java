package hotel.booking.service;

import hotel.booking.entity.BookingRequest;
import hotel.booking.utils.RandomizedBookingTicketGenerator;
import hotel.booking.utils.SynchronizedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    private final SynchronizedQueue<BookingRequest> requests;

    private final AtomicInteger count;

    private final int requestsToGenerate;

    public Producer(SynchronizedQueue<BookingRequest> requests, AtomicInteger count, int requestsToGenerate) {
        this.requests = requests;
        this.count = count;
        this.requestsToGenerate = requestsToGenerate;
    }

    public void produce(){
        while (count.getAndIncrement() < requestsToGenerate) {
            BookingRequest bookingRequest = RandomizedBookingTicketGenerator.newRandomBookingRequest();
            requests.put(bookingRequest);
            logger.debug(String.format("Producer: %s, sent %s", this.toString(), bookingRequest.toString()));
        }
        logger.debug(String.format("Producer: %s, cannot generate more requests", this.toString()));
    }
}
