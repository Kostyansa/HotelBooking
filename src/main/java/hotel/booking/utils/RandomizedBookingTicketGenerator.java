package hotel.booking.utils;

import hotel.booking.entity.BookingRequest;
import hotel.booking.service.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class RandomizedBookingTicketGenerator {

    static final AtomicInteger count = new AtomicInteger();

    static final ReentrantLock randomLock = new ReentrantLock();

    static final Random random = new Random();

    static ArrayList<String> names = new ArrayList<>(Arrays.asList(
            "John Smith",
            "Petr Ivanov",
            "High Ping",
            "Sum Ting Wong",
            "Lenin",
            "Anna Frank",
            "Marie Curie",
            "Ivan Petrov"
    ));


    static List<String> hotels = Arrays.asList(
            "Grand Budapest",
            "Test Hotel 4",
            "Test Hotel 1",
            "Test Hotel 8",
            "Test Hotel 16",
            "Test Hotel 3",
            "Test Hotel 2"
    );

    static public BookingRequest newRandomBookingRequest() {
        int namesIndex;
        int hotelsIndex;
        int days;
        int id;
        randomLock.lock();
        try {
            namesIndex = random.nextInt(names.size());
            hotelsIndex = random.nextInt(hotels.size());
            days = random.nextInt() % 30;
            id = count.getAndIncrement();
        } finally {
            randomLock.unlock();
        }
        return new BookingRequest(
                id,
                LocalDate.now().plusDays(days),
                hotels.get(hotelsIndex),
                names.get(namesIndex)
        );
    }

}
