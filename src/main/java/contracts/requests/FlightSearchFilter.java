package contracts.requests;

import jakarta.annotation.Nonnull;

import java.util.StringJoiner;

public record FlightSearchFilter(String departureCity,
                                 String arrivalCity,
                                 String from,
                                 String to,
                                 int passengerCount,
                                 boolean roundTrip) {
    @Override
    @Nonnull
    public String toString() {
        StringJoiner query = new StringJoiner("&", "", "");

        if (departureCity != null && !departureCity.isEmpty()) {
            query.add("departure=" + departureCity);
        }
        if (arrivalCity != null && !arrivalCity.isEmpty()) {
            query.add("arrival=" + arrivalCity);
        }
        if (from != null && !from.isEmpty()) {
            query.add("from=" + from);
        }
        if (to != null && !to.isEmpty()) {
            query.add("to=" + to);
        }
        if (passengerCount > 0) {
            query.add("passengerCount=" + passengerCount);
        }
        query.add("roundTrip=" + roundTrip);

        return query.toString().replace(" ", "%20");
    }
}
