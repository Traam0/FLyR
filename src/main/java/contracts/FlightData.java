package contracts;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record FlightData(
        @JsonProperty("id") int id,
        @JsonProperty("flightNumber") String flightNumber,
        @JsonProperty("departureCity") String departureCity,
        @JsonProperty("destinationCity") String destinationCity,
        @JsonProperty("departureDateTime") LocalDateTime departureDateTime
) {
}

