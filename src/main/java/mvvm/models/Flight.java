package mvvm.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public final class Flight {
    @JsonProperty("id") private int id;
    @JsonProperty("flightNumber") private String flightNumber;
    @JsonProperty("departureCity") private String departureCity;
    @JsonProperty("destinationCity") private String destinationCity;
    @JsonProperty("departureDateTime") private LocalDateTime departureDateTime;
    @JsonProperty("airCraft") private AirCraft airCraft;

    public Flight() {
    }

    public Flight(int id, String flightNumber, String departureCity, String destinationCity,
                  LocalDateTime departureDateTime, AirCraft airCraft) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureDateTime = departureDateTime;
        this.airCraft = airCraft;
    }

    public int getId() {
        return id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public AirCraft getAirCraft() {
        return airCraft;
    }

    public void setAirCraft(AirCraft airCraft) {
        this.airCraft = airCraft;
    }


    public Map<String, Object> toMap() throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(this));
        }
        return map;
    }
}