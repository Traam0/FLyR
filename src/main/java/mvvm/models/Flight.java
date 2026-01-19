package mvvm.models;

import java.time.LocalDateTime;

public final class Flight {
    private int id;
    private String flightNumber;
    private String departureCity;
    private String destinationCity;
    private LocalDateTime departureDateTime;
    private AirCraft airCraft;

    public Flight() {}

    public Flight(int id, String flightNumber, String departureCity, String destinationCity,
                  LocalDateTime departureDateTime, AirCraft airCraft) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureDateTime = departureDateTime;
        this.airCraft = airCraft;
    }

    public int getId() { return id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public LocalDateTime getDepartureDateTime() { return departureDateTime; }
    public void setDepartureDateTime(LocalDateTime departureDateTime) { this.departureDateTime = departureDateTime; }

    public AirCraft getAirCraft() { return airCraft; }
    public void setAirCraft(AirCraft airCraft) { this.airCraft = airCraft; }
}