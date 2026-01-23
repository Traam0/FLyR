package socket;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

    public class NotificationServer {

        private static final int PORT = 9090;

        // Clients connectés (thread-safe)
        private static final Set<ClientHandler> clients =
                ConcurrentHashMap.newKeySet();

        public static void main(String[] args) {
            System.out.println("📡 Notification Socket Server démarré sur le port " + PORT);

            try (ServerSocket serverSocket = new ServerSocket(PORT)) {

                while (true) {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket);
                    clients.add(handler);
                    new Thread(handler).start();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Diffusion à tous les clients
        public static void broadcast(NotificationMessage message) {
            for (ClientHandler client : clients) {
                client.send(message);
            }
        }

        public static void removeClient(ClientHandler client) {
            clients.remove(client);
        }
    }


