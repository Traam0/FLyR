package org.example.model.entities;

import org.example.model.enums.TransactionType;

import java.time.LocalDateTime;

public class TransactionLog {
    private int id;
    private TransactionType type;
    private String description;
    private LocalDateTime createdAt;

    private int clientId;
    private int flightId;
    private int reservationId;

    public TransactionLog() {}

    public TransactionLog(int id, TransactionType type, String description, LocalDateTime createdAt,
                          int clientId, int flightId, int reservationId) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.createdAt = createdAt;
        this.clientId = clientId;
        this.flightId = flightId;
        this.reservationId = reservationId;
    }

    public int getId() { return id; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }

    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
}
