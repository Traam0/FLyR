package mvvm.models;


import java.time.LocalDateTime;

public final class Reservation {
    private int id;
    private Client client;
    private Flight flight;
    private Seat seat;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    public Reservation() {}

    public Reservation(int id, Client client, Flight flight, Seat seat,
                       ReservationStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.client = client;
        this.flight = flight;
        this.seat = seat;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }

    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}