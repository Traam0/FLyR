package org.example.soap.fault;

import jakarta.xml.ws.WebFault;

@WebFault(name="BookingException")
public class BookingException extends Exception {
    private final BookingFault fault;

    public BookingException(String message, BookingFault fault) {
        super(message);
        this.fault = fault;
    }
    public BookingFault getFaultInfo() { return fault; }
}