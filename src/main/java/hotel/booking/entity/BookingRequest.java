package hotel.booking.entity;


import java.time.LocalDate;
import java.util.Objects;

public class BookingRequest {

    private long id;
    private LocalDate date;
    private String hotel;
    private String name;

    public BookingRequest(long id, LocalDate date, String hotel, String name) {
        this.id = id;
        this.date = date;
        this.hotel = hotel;
        this.name = name;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRequest that = (BookingRequest) o;
        return id == that.id &&
                date.equals(that.date) &&
                hotel.equals(that.hotel);
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                "id=" + id +
                ", date=" + date +
                ", hotel='" + hotel + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, hotel);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
