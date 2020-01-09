package hotel.booking.utils;

import hotel.booking.entity.BookingRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class RandomizedBookingTicketGenerator {

    AtomicInteger count = new AtomicInteger();

    ReentrantLock randomLock = new ReentrantLock();

    static Random random = new Random();

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


    static ArrayList<String> hotels = new ArrayList<>(Arrays.asList(
            "Grand Budapest",
            "Test Hotel 4",
            "Test Hotel 1",
            "Test Hotel 8",
            "Test Hotel 16",
            "Test Hotel 3",
            "Test Hotel 2"
    ));

    public BookingRequest newRandomBookingRequest(){
        int namesIndex;
        int hotelsIndex;
        int days;
        randomLock.lock();
        try {
            namesIndex = random.nextInt(names.size());
            hotelsIndex = random.nextInt(hotels.size());
            days = random.nextInt()%30;
        }
        finally {
            randomLock.unlock();
        }
        return new BookingRequest(
                count.getAndIncrement(),
                LocalDate.now().plusDays(days),
                hotels.get(hotelsIndex),
                names.get(namesIndex)
                );
    }

}
