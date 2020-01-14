package hotel.booking;

import hotel.booking.service.HotelService;

import java.time.Duration;

public class Main {

    public static void main(String[] args) {
        HotelService hotelService = new HotelService(
                100,
                200,
                30,
                300,
                Duration.ofMillis(5000));
        hotelService.startProcessing();
    }
}
