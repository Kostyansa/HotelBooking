package hotel.booking;

import hotel.booking.service.HotelService;

public class Main {

    public static void main(String[] args) {
        HotelService hotelService = new HotelService();
        hotelService.startProcessing();
    }
}
