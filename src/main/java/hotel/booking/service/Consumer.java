package hotel.booking.service;

import hotel.booking.entity.BookingRequest;
import hotel.booking.utils.SynchronizedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Consumer extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private static final long pingPeriod = 1000;

    private final SynchronizedQueue<BookingRequest> requests;

    private final Duration timeout;

    public Consumer(SynchronizedQueue<BookingRequest> requests, Duration timeout) {
        this.requests = requests;
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            boolean isTimeoutEnded = false;
            while ((!requests.isEmpty()) || !isTimeoutEnded) {
                BookingRequest bookingRequest = requests.poll();
                if (bookingRequest == null) {
                    int i = 0;
                    while((bookingRequest == null) && (i++ < (timeout.toMillis()/pingPeriod + 1))) {
                        Thread.sleep(pingPeriod);
                        bookingRequest = requests.poll();
                    }
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

