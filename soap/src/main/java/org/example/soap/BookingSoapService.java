package org.example.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;
import org.example.model.entities.PaymentTransaction;
import org.example.model.entities.Reservation;
import org.example.soap.fault.BookingException;

import java.math.BigDecimal;

@WebService(
        serviceName = "BookingService",
        targetNamespace = "http://soap.example.org/booking"
)
public interface BookingSoapService {

    @WebMethod
    Reservation bookSeat(int clientId, int flightId, int seatId) throws BookingException;

    @WebMethod
    Reservation cancelReservation(int reservationId) throws BookingException;

    @WebMethod
    Reservation changeSeat(int reservationId, int newSeatId) throws BookingException;

    @WebMethod
    PaymentTransaction simulatePayment(int reservationId, BigDecimal amount, String currency) throws BookingException;
}