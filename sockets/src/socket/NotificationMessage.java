package socket;
import java.io.Serializable;
import java.time.LocalDateTime;

    public class NotificationMessage implements Serializable {

        private NotificationType type;
        private String message;
        private int flightId;
        private int seatId;
        private LocalDateTime timestamp;

        public NotificationMessage() {}

        public NotificationMessage(NotificationType type, String message,
                                   int flightId, int seatId) {
            this.type = type;
            this.message = message;
            this.flightId = flightId;
            this.seatId = seatId;
            this.timestamp = LocalDateTime.now();
        }

        public NotificationType getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public int getFlightId() {
            return flightId;
        }

        public int getSeatId() {
            return seatId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }


