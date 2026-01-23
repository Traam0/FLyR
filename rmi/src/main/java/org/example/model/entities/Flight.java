package org.example.model.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.example.soap.adapters.LocalDateTimeAdapter;

import java.time.LocalDateTime;

@XmlRootElement(name="Flight")
@XmlAccessorType(XmlAccessType.FIELD)
public class Flight {
    private int id;
    private String flightNumber;
    private String departureCity;
    private String destinationCity;

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime departureDateTime;

    private Aircraft aircraft;

    public Flight() {}

    public Flight(int id, String flightNumber, String departureCity, String destinationCity,
                  LocalDateTime departureDateTime, Aircraft aircraft) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.departureCity = departureCity;
        this.destinationCity = destinationCity;
        this.departureDateTime = departureDateTime;
        this.aircraft = aircraft;
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

    public Aircraft getAircraft() { return aircraft; }
    public void setAircraft(Aircraft aircraft) { this.aircraft = aircraft; }
}