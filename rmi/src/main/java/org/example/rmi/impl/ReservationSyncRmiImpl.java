package org.example.rmi.impl;

import org.example.rmi.ReservationSyncRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ReservationSyncRmiImpl extends UnicastRemoteObject implements ReservationSyncRmi {

    public ReservationSyncRmiImpl() throws RemoteException { super(); }

    @Override
    public void onReservationCreated(int reservationId) throws RemoteException {
        System.out.println("[RMI-SYNC] Reservation created id=" + reservationId);
    }

    @Override
    public void onReservationCancelled(int reservationId) throws RemoteException {
        System.out.println("[RMI-SYNC] Reservation cancelled id=" + reservationId);
    }

    @Override
    public void onReservationSeatChanged(int reservationId, int oldSeatId, int newSeatId) throws RemoteException {
        System.out.println("[RMI-SYNC] Reservation " + reservationId +
                " seat changed " + oldSeatId + " -> " + newSeatId);
    }
}