package org.example.rmi.impl;

import org.example.db.Db;
import org.example.model.entities.Aircraft;
import org.example.model.entities.Flight;
import org.example.rmi.FlightManagementRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightManagementRmiImpl extends UnicastRemoteObject implements FlightManagementRmi {

    public FlightManagementRmiImpl() throws RemoteException { super(); }

    @Override
    public Flight getFlightById(int flightId) throws RemoteException {
        String sql =
                "SELECT f.id, f.flight_number, f.departure_city, f.destination_city, f.departure_date_time, " +
                        "a.id AS aid, a.code, a.model, a.total_capacity, a.economy_capacity, a.business_capacity " +
                        "FROM flight f JOIN aircraft a ON a.id=f.aircraft_id WHERE f.id=?";

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Aircraft ac = new Aircraft(
                        rs.getInt("aid"),
                        rs.getString("code"),
                        rs.getString("model"),
                        rs.getInt("total_capacity"),
                        rs.getInt("economy_capacity"),
                        rs.getInt("business_capacity")
                );

                LocalDateTime dt = rs.getTimestamp("departure_date_time").toLocalDateTime();

                return new Flight(
                        rs.getInt("id"),
                        rs.getString("flight_number"),
                        rs.getString("departure_city"),
                        rs.getString("destination_city"),
                        dt,
                        ac
                );
            }
        } catch (Exception e) {
            throw new RemoteException("DB error in getFlightById", e);
        }
    }

    @Override
    public List<Flight> listFlights() throws RemoteException {
        String sql =
                "SELECT f.id, f.flight_number, f.departure_city, f.destination_city, f.departure_date_time, " +
                        "a.id AS aid, a.code, a.model, a.total_capacity, a.economy_capacity, a.business_capacity " +
                        "FROM flight f JOIN aircraft a ON a.id=f.aircraft_id ORDER BY f.departure_date_time";

        List<Flight> out = new ArrayList<>();
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Aircraft ac = new Aircraft(
                        rs.getInt("aid"),
                        rs.getString("code"),
                        rs.getString("model"),
                        rs.getInt("total_capacity"),
                        rs.getInt("economy_capacity"),
                        rs.getInt("business_capacity")
                );
                LocalDateTime dt = rs.getTimestamp("departure_date_time").toLocalDateTime();

                out.add(new Flight(
                        rs.getInt("id"),
                        rs.getString("flight_number"),
                        rs.getString("departure_city"),
                        rs.getString("destination_city"),
                        dt,
                        ac
                ));
            }
            return out;
        } catch (Exception e) {
            throw new RemoteException("DB error in listFlights", e);
        }
    }

    @Override
    public int addFlight(Flight flight) throws RemoteException {
        if (flight == null || flight.getAircraft() == null)
            throw new RemoteException("Flight or Aircraft is null");

        String sql =
                "INSERT INTO flight(flight_number, departure_city, destination_city, departure_date_time, aircraft_id) " +
                        "VALUES (?,?,?,?,?)";

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getDepartureCity());
            ps.setString(3, flight.getDestinationCity());
            ps.setTimestamp(4, Timestamp.valueOf(flight.getDepartureDateTime()));
            ps.setInt(5, flight.getAircraft().getId());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (Exception e) {
            throw new RemoteException("DB error in addFlight", e);
        }
    }

    @Override
    public boolean updateFlight(Flight flight) throws RemoteException {
        if (flight == null || flight.getAircraft() == null)
            throw new RemoteException("Flight or Aircraft is null");

        String sql =
                "UPDATE flight SET flight_number=?, departure_city=?, destination_city=?, departure_date_time=?, aircraft_id=? " +
                        "WHERE id=?";

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getDepartureCity());
            ps.setString(3, flight.getDestinationCity());
            ps.setTimestamp(4, Timestamp.valueOf(flight.getDepartureDateTime()));
            ps.setInt(5, flight.getAircraft().getId());
            ps.setInt(6, flight.getId());

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RemoteException("DB error in updateFlight", e);
        }
    }

    @Override
    public boolean deleteFlight(int flightId) throws RemoteException {
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM flight WHERE id=?")) {

            ps.setInt(1, flightId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            throw new RemoteException("DB error in deleteFlight (maybe FK restrict)", e);
        }
    }
}