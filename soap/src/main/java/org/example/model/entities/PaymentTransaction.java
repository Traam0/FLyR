package org.example.model.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.example.model.enums.PaymentStatus;
import org.example.soap.adapters.LocalDateTimeAdapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@XmlRootElement(name="PaymentTransaction")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentTransaction {
    private int id;
    private Reservation reservation;
    private BigDecimal amount;
    private String currency; // ex: "EUR"
    private PaymentStatus status;

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime createdAt;

    private String providerRef; // référence paiement simulée

    public PaymentTransaction() {}

    public PaymentTransaction(int id, Reservation reservation, BigDecimal amount, String currency,
                              PaymentStatus status, LocalDateTime createdAt, String providerRef) {
        this.id = id;
        this.reservation = reservation;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.createdAt = createdAt;
        this.providerRef = providerRef;
    }

    public int getId() { return id; }

    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getProviderRef() { return providerRef; }
    public void setProviderRef(String providerRef) { this.providerRef = providerRef; }
}
