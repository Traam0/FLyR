package org.example.model.entities;

import org.example.model.enums.SeatClass;
import org.example.model.enums.SeatStatus;

public class Seat {
    private int id;
    private String seatNumber;
    private SeatClass seatClass;
    private SeatStatus status;
    private Aircraft aircraft;

    public Seat() {}

    public Seat(int id, String seatNumber, SeatClass seatClass, SeatStatus status, Aircraft aircraft) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.status = status;
        this.aircraft = aircraft;
    }

    public int getId() { return id; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public SeatClass getSeatClass() { return seatClass; }
    public void setSeatClass(SeatClass seatClass) { this.seatClass = seatClass; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public Aircraft getAircraft() { return aircraft; }
    public void setAircraft(Aircraft aircraft) { this.aircraft = aircraft; }
}