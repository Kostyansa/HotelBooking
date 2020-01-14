package hotel.booking.service;

import hotel.booking.entity.BookingRequest;
import hotel.booking.utils.SynchronizedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final SynchronizedQueue<BookingRequest> requests;

    private final Duration timeout;

    public Consumer(SynchronizedQueue<BookingRequest> requests, Duration timeout) {
        this.requests = requests;
        this.timeout = timeout;
    }

    public void consume() {
        boolean isTimeoutEnded = false;
        BookingRequest bookingRequest = null;
        try {
            while ((!requests.isEmpty()) || !isTimeoutEnded) {
                bookingRequest = requests.poll();
                if (bookingRequest == null) {
                    Thread.sleep(timeout.toMillis());
                    bookingRequest = requests.poll();
                    if (bookingRequest == null) {
                        isTimeoutEnded = true;
                    }
                    continue;
                }
                Thread.sleep(5000);
                logger.debug(String.format("Consumer: %s, consumed %s", this.toString(), bookingRequest.toString()));
            }
        } catch (InterruptedException exc) {
            logger.error(String.format("Consumer: %s, has been interrupted while consuming", this.toString()));
        }
        logger.debug(String.format("Consumer: %s, terminated successfully", this.toString()));
    }
}

