package socket_API;


import socket.NotificationMessage;
import socket.NotificationServer;
import socket.NotificationType;

public final class SocketNotificationService {

        private SocketNotificationService() {
            // empêche l’instanciation
        }

        public static void notifyReservationConfirmed(int flightId, int seatId) {
            NotificationServer.broadcast(
                    new NotificationMessage(
                            NotificationType.RESERVATION_CONFIRMED,
                            "Réservation confirmée",
                            flightId,
                            seatId
                    )
            );
        }

        public static void notifyReservationConflict(int flightId, int seatId) {
            NotificationServer.broadcast(
                    new NotificationMessage(
                            NotificationType.RESERVATION_CONFLICT,
                            "Conflit : siège déjà réservé",
                            flightId,
                            seatId
                    )
            );
        }

        public static void notifySeatReleased(int flightId, int seatId) {
            NotificationServer.broadcast(
                    new NotificationMessage(
                            NotificationType.SEAT_RELEASED,
                            "Siège remis en disponibilité",
                            flightId,
                            seatId
                    )
            );
        }
    }


