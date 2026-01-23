package org.example.rmi;

import org.example.model.entities.Flight;
import org.example.model.entities.Seat;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class RmiClientTestMain {
    public static void main(String[] args) throws Exception {
        Registry reg = LocateRegistry.getRegistry("localhost", 1099);

        FlightManagementRmi flightRmi = (FlightManagementRmi) reg.lookup("FlightManagementRmi");
        SeatAvailabilityRmi seatRmi = (SeatAvailabilityRmi) reg.lookup("SeatAvailabilityRmi");

        List<Flight> flights = flightRmi.listFlights();
        System.out.println("Flights count = " + flights.size());

        if (!flights.isEmpty()) {
            int flightId = flights.get(0).getId();
            List<Seat> seats = seatRmi.listAvailableSeats(flightId);
            System.out.println("Available seats for flight " + flightId + " = " + seats.size());
        }
    }
}