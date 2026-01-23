package socket;

import java.io.ObjectOutputStream;
import java.net.Socket;

    public class ClientHandler implements Runnable {

        private Socket socket;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                System.out.println("Client connecté : " + socket.getRemoteSocketAddress());

                // Le serveur ne reçoit rien : notifications uniquement
                while (!socket.isClosed()) {
                    Thread.sleep(1000);
                }

            } catch (Exception e) {
                System.out.println("Client déconnecté");
            } finally {
                NotificationServer.removeClient(this);
            }
        }

        public synchronized void send(NotificationMessage message) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (Exception e) {
                NotificationServer.removeClient(this);
            }
        }
    }


