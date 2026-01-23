package org.example.rmi.impl;

import org.example.db.Db;
import org.example.model.entities.Aircraft;
import org.example.model.entities.Seat;
import org.example.model.enums.SeatClass;
import org.example.model.enums.SeatStatus;
import org.example.rmi.SeatAvailabilityRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatAvailabilityRmiImpl extends UnicastRemoteObject implements SeatAvailabilityRmi {

    public SeatAvailabilityRmiImpl() throws RemoteException { super(); }

    @Override
    public boolean isSeatAvailable(int flightId, int seatId) throws RemoteException {
        String sql =
                "SELECT 1 FROM reservation " +
                        "WHERE flight_id=? AND seat_id=? AND reservation_status='CONFIRMED' LIMIT 1";

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            ps.setInt(2, seatId);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        } catch (Exception e) {
            throw new RemoteException("DB error in isSeatAvailable", e);
        }
    }

    @Override
    public List<Seat> listAvailableSeats(int flightId) throws RemoteException {
        String sql =
                "SELECT s.id, s.seat_number, s.seat_class, " +
                        "a.id AS aid, a.code, a.model, a.total_capacity, a.economy_capacity, a.business_capacity " +
                        "FROM flight f " +
                        "JOIN aircraft a ON a.id = f.aircraft_id " +
                        "JOIN seat s ON s.aircraft_id = a.id " +
                        "WHERE f.id=? AND s.id NOT IN ( " +
                        "   SELECT r.seat_id FROM reservation r " +
                        "   WHERE r.flight_id=? AND r.reservation_status='CONFIRMED' " +
                        ") " +
                        "ORDER BY s.seat_number";

        List<Seat> out = new ArrayList<>();
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            ps.setInt(2, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Aircraft ac = new Aircraft(
                            rs.getInt("aid"),
                            rs.getString("code"),
                            rs.getString("model"),
                            rs.getInt("total_capacity"),
                            rs.getInt("economy_capacity"),
                            rs.getInt("business_capacity")
                    );

                    out.add(new Seat(
                            rs.getInt("id"),
                            rs.getString("seat_number"),
                            SeatClass.valueOf(rs.getString("seat_class")),
                            SeatStatus.FREE,
                            ac
                    ));
                }
            }
            return out;
        } catch (Exception e) {
            throw new RemoteException("DB error in listAvailableSeats", e);
        }
    }
}