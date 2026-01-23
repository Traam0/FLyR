package org.example.rmi;

import org.example.model.entities.Flight;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FlightManagementRmi extends Remote {
    Flight getFlightById(int flightId) throws RemoteException;
    List<Flight> listFlights() throws RemoteException;

    int addFlight(Flight flight) throws RemoteException;
    boolean updateFlight(Flight flight) throws RemoteException;
    boolean deleteFlight(int flightId) throws RemoteException;
}