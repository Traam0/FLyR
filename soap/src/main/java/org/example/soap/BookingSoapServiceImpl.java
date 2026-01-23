package org.example.soap;

import jakarta.jws.WebService;
import org.example.db.Db;
import org.example.model.entities.*;
import org.example.model.enums.*;
import org.example.soap.fault.BookingException;
import org.example.soap.fault.BookingFault;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@WebService(
        endpointInterface = "org.example.soap.BookingSoapService",
        serviceName = "BookingService",
        targetNamespace = "http://soap.example.org/booking"
)
public class BookingSoapServiceImpl implements BookingSoapService {

    @Override
    public Reservation bookSeat(int clientId, int flightId, int seatId) throws BookingException {
        try (Connection cn = Db.getConnection()) {
            cn.setAutoCommit(false);

            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT id FROM reservation " +
                            "WHERE flight_id=? AND seat_id=? AND reservation_status='CONFIRMED' " +
                            "FOR UPDATE")) {
                ps.setInt(1, flightId);
                ps.setInt(2, seatId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        cn.rollback();
                        throw fault("SEAT_ALREADY_RESERVED", "Seat already reserved for this flight");
                    }
                }
            }

            int reservationId;
            LocalDateTime now = LocalDateTime.now();
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO reservation(client_id, flight_id, seat_id, reservation_status, created_at) " +
                            "VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, clientId);
                ps.setInt(2, flightId);
                ps.setInt(3, seatId);
                ps.setString(4, "CONFIRMED");
                ps.setTimestamp(5, Timestamp.valueOf(now));
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    reservationId = keys.getInt(1);
                }
            }

            cn.commit();
            return loadReservation(cn, reservationId);

        } catch (BookingException e) {
            throw e;
        } catch (Exception e) {
            throw fault("INTERNAL_ERROR", e.getMessage());
        }
    }

    @Override
    public Reservation cancelReservation(int reservationId) throws BookingException {
        try (Connection cn = Db.getConnection()) {
            cn.setAutoCommit(false);

            String status;
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT reservation_status FROM reservation WHERE id=? FOR UPDATE")) {
                ps.setInt(1, reservationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { cn.rollback(); throw fault("RESERVATION_NOT_FOUND", "Not found"); }
                    status = rs.getString(1);
                }
            }

            if (!"CANCELLED".equalsIgnoreCase(status)) {
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE reservation SET reservation_status='CANCELLED' WHERE id=?")) {
                    ps.setInt(1, reservationId);
                    ps.executeUpdate();
                }
            }

            cn.commit();
            return loadReservation(cn, reservationId);

        } catch (BookingException e) {
            throw e;
        } catch (Exception e) {
            throw fault("INTERNAL_ERROR", e.getMessage());
        }
    }

    @Override
    public Reservation changeSeat(int reservationId, int newSeatId) throws BookingException {
        try (Connection cn = Db.getConnection()) {
            cn.setAutoCommit(false);

            int flightId;
            String status;

            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT flight_id, reservation_status FROM reservation WHERE id=? FOR UPDATE")) {
                ps.setInt(1, reservationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { cn.rollback(); throw fault("RESERVATION_NOT_FOUND", "Not found"); }
                    flightId = rs.getInt(1);
                    status = rs.getString(2);
                }
            }

            if ("CANCELLED".equalsIgnoreCase(status)) {
                cn.rollback();
                throw fault("RESERVATION_CANCELLED", "Cannot change a cancelled reservation");
            }

            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT id FROM reservation " +
                            "WHERE flight_id=? AND seat_id=? AND reservation_status='CONFIRMED' " +
                            "FOR UPDATE")) {
                ps.setInt(1, flightId);
                ps.setInt(2, newSeatId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        cn.rollback();
                        throw fault("SEAT_ALREADY_RESERVED", "New seat already reserved for this flight");
                    }
                }
            }

            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE reservation SET seat_id=? WHERE id=?")) {
                ps.setInt(1, newSeatId);
                ps.setInt(2, reservationId);
                ps.executeUpdate();
            }

            cn.commit();
            return loadReservation(cn, reservationId);

        } catch (BookingException e) {
            throw e;
        } catch (Exception e) {
            throw fault("INTERNAL_ERROR", e.getMessage());
        }
    }

    @Override
    public PaymentTransaction simulatePayment(int reservationId, BigDecimal amount, String currency) throws BookingException {
        try (Connection cn = Db.getConnection()) {
            cn.setAutoCommit(false);

            String status;
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT reservation_status FROM reservation WHERE id=? FOR UPDATE")) {
                ps.setInt(1, reservationId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) { cn.rollback(); throw fault("RESERVATION_NOT_FOUND", "Not found"); }
                    status = rs.getString(1);
                }
            }

            if ("CANCELLED".equalsIgnoreCase(status)) {
                cn.rollback();
                throw fault("RESERVATION_CANCELLED", "Cannot pay a cancelled reservation");
            }

            cn.commit();

            Reservation res = loadReservation(cn, reservationId);
            return new PaymentTransaction(
                    0,
                    res,
                    amount,
                    currency,
                    PaymentStatus.SUCCESS,
                    LocalDateTime.now(),
                    "PAY-" + UUID.randomUUID()
            );

        } catch (BookingException e) {
            throw e;
        } catch (Exception e) {
            throw fault("INTERNAL_ERROR", e.getMessage());
        }
    }

    private Reservation loadReservation(Connection cn, int reservationId) throws Exception {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT id, client_id, flight_id, seat_id, reservation_status, created_at " +
                        "FROM reservation WHERE id=?")) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw fault("RESERVATION_NOT_FOUND", "Not found");

                int clientId = rs.getInt("client_id");
                int flightId = rs.getInt("flight_id");
                int seatId   = rs.getInt("seat_id");

                ReservationStatus st = ReservationStatus.valueOf(rs.getString("reservation_status"));
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                Client client = loadClient(cn, clientId);
                Flight flight = loadFlight(cn, flightId);
                Seat seat     = loadSeat(cn, flightId, seatId); // status computed from reservation table

                return new Reservation(reservationId, client, flight, seat, st, createdAt);
            }
        }
    }

    private Client loadClient(Connection cn, int clientId) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT id, first_name, last_name, email, phone, passport_number, birth_date " +
                        "FROM client WHERE id=?")) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                LocalDate bd = rs.getDate("birth_date").toLocalDate();
                return new Client(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("passport_number"),
                        bd
                );
            }
        }
    }

    private Flight loadFlight(Connection cn, int flightId) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT f.id, f.flight_number, f.departure_city, f.destination_city, f.departure_date_time, " +
                        "a.id AS aid, a.code, a.model, a.total_capacity, a.economy_capacity, a.business_capacity " +
                        "FROM flight f JOIN aircraft a ON a.id=f.aircraft_id WHERE f.id=?")) {
            ps.setInt(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
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
        }
    }

    private Seat loadSeat(Connection cn, int flightId, int seatId) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT s.id, s.seat_number, s.seat_class, s.aircraft_id, " +
                        "a.id AS aid, a.code, a.model, a.total_capacity, a.economy_capacity, a.business_capacity " +
                        "FROM seat s JOIN aircraft a ON a.id=s.aircraft_id WHERE s.id=?")) {
            ps.setInt(1, seatId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                Aircraft ac = new Aircraft(
                        rs.getInt("aid"),
                        rs.getString("code"),
                        rs.getString("model"),
                        rs.getInt("total_capacity"),
                        rs.getInt("economy_capacity"),
                        rs.getInt("business_capacity")
                );

                SeatClass seatClass = SeatClass.valueOf(rs.getString("seat_class"));

                boolean reserved;
                try (PreparedStatement ps2 = cn.prepareStatement(
                        "SELECT 1 FROM reservation " +
                                "WHERE flight_id=? AND seat_id=? AND reservation_status='CONFIRMED' LIMIT 1")) {
                    ps2.setInt(1, flightId);
                    ps2.setInt(2, seatId);
                    try (ResultSet r2 = ps2.executeQuery()) {
                        reserved = r2.next();
                    }
                }

                SeatStatus st = reserved ? SeatStatus.RESERVED : SeatStatus.FREE;

                return new Seat(
                        rs.getInt("id"),
                        rs.getString("seat_number"),
                        seatClass,
                        st,
                        ac
                );
            }
        }
    }

    private BookingException fault(String code, String message) {
        BookingFault f = new BookingFault();
        f.code = code;
        f.message = message;
        return new BookingException(message, f);
    }
}