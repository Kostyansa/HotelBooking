import hotel.booking.service.HotelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

public class HotelBookingTest {

    @ParameterizedTest(name = "{index} => producerThreads={0}, consumerThreads={1}, queueSize={2}, requests={3}" +
            "timeoutInMillis={4}, assertionTimeoutInSeconds={5}")
    @CsvSource({
            "300, 200, 5, 100, 2500, 10",
            "100, 1, 5, 4, 1000, 30",
            "1, 100, 100, 100, 2500, 10"

    })
    public void successfulTest(int producerThreads, int consumerThreads, int queueSize, int requests, int timeoutInMillis, int assertionTimeoutInSeconds){
        Assertions.assertTimeout(Duration.ofSeconds(assertionTimeoutInSeconds), () -> {
            HotelService hotelService = new HotelService(
                    producerThreads,
                    consumerThreads,
                    queueSize,
                    requests,
                    Duration.ofMillis(timeoutInMillis));
            hotelService.startProcessing();
        });
    }

    @Test
    public void zeroProducerThreads(){
        Assertions.assertTimeout(Duration.ofSeconds(3), () -> {
            HotelService hotelService = new HotelService(
                    0,
                    100,
                    5,
                    100,
                    Duration.ofMillis(1000));
            hotelService.startProcessing();
        });
    }

    @Test
    public void zeroRequestsThreads(){
        Assertions.assertTimeout(Duration.ofSeconds(3), () -> {
            HotelService hotelService = new HotelService(
                    100,
                    100,
                    5,
                    0,
                    Duration.ofMillis(1000));
            hotelService.startProcessing();
        });
    }

    @Test
    public void negativeRequestsThreads(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            HotelService hotelService = new HotelService(
                    100,
                    100,
                    5,
                    -1,
                    Duration.ofMillis(1000));
            hotelService.startProcessing();
        });
    }

    @Test
    public void zeroConsumerThreads(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            HotelService hotelService = new HotelService(
                    100,
                    0,
                    5,
                    100,
                    Duration.ofMillis(-1000));
            hotelService.startProcessing();
        });
    }
}
