package mvvm.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Seat {
    @JsonProperty("id")
    private int id;
    @JsonProperty("seatNumber")
    private String seatNumber;
    @JsonProperty("seatClass")
    private SeatClass seatClass;
    @JsonProperty("status")
    SeatStatus status;
    @JsonProperty("airCraft")
    AirCraft airCraft;

    public Seat() {
    }

    public Seat(int id, String seatNumber, SeatClass seatClass, SeatStatus status, AirCraft airCraft) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.status = status;
        this.airCraft = airCraft;
    }

    public int getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public AirCraft getAirCraft() {
        return airCraft;
    }

    public void setAirCraft(AirCraft airCraft) {
        this.airCraft = airCraft;
    }
}