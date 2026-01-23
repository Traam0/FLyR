package org.example.client;

import jakarta.xml.ws.Service;
import org.example.model.entities.Reservation;
import org.example.soap.BookingSoapService;

import javax.xml.namespace.QName;
import java.net.URL;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        URL wsdl = new URL("http://localhost:8081/booking?wsdl");
        QName qname = new QName("http://soap.example.org/booking", "BookingService");

        Service service = Service.create(wsdl, qname);
        BookingSoapService proxy = service.getPort(BookingSoapService.class);

        Reservation r = proxy.bookSeat(1, 1, 1);
        System.out.println("Reservation id=" + r.getId() + " status=" + r.getStatus());
    }
}