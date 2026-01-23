package org.example.soap;

import jakarta.xml.ws.Endpoint;

public class ServerMain {
    public static void main(String[] args) {
        String url = "http://localhost:8081/booking";
        Endpoint.publish(url, new BookingSoapServiceImpl());
        System.out.println("SOAP up: " + url + "?wsdl");
    }
}