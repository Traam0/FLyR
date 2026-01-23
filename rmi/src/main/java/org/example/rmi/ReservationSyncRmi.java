package org.example.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReservationSyncRmi extends Remote {
    void onReservationCreated(int reservationId) throws RemoteException;
    void onReservationCancelled(int reservationId) throws RemoteException;
    void onReservationSeatChanged(int reservationId, int oldSeatId, int newSeatId) throws RemoteException;
}