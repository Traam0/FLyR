package org.example.rmi;

import org.example.rmi.impl.FlightManagementRmiImpl;
import org.example.rmi.impl.ReservationSyncRmiImpl;
import org.example.rmi.impl.SeatAvailabilityRmiImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiServerMain {
    public static void main(String[] args) throws Exception {
        Registry registry = LocateRegistry.createRegistry(1099);

        registry.rebind("FlightManagementRmi", new FlightManagementRmiImpl());
        registry.rebind("SeatAvailabilityRmi", new SeatAvailabilityRmiImpl());
        registry.rebind("ReservationSyncRmi", new ReservationSyncRmiImpl());

        System.out.println("RMI registry started on port 1099. Services bound.");
    }
}