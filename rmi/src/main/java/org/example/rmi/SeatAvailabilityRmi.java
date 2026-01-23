package org.example.rmi;

import org.example.model.entities.Seat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SeatAvailabilityRmi extends Remote {
    boolean isSeatAvailable(int flightId, int seatId) throws RemoteException;
    List<Seat> listAvailableSeats(int flightId) throws RemoteException;
}