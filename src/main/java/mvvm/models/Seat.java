package mvvm.models;

public final class Seat {
    private int id;
    private String seatNumber;
    private SeatClass seatClass;
    private SeatStatus status;
    private AirCraft airCraft;

    public Seat() {}

    public Seat(int id, String seatNumber, SeatClass seatClass, SeatStatus status, AirCraft airCraft) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.status = status;
        this.airCraft = airCraft;
    }

    public int getId() { return id; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public SeatClass getSeatClass() { return seatClass; }
    public void setSeatClass(SeatClass seatClass) { this.seatClass = seatClass; }

    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }

    public AirCraft getAirCraft() { return airCraft; }
    public void setAirCraft(AirCraft airCraft) { this.airCraft = airCraft; }
}