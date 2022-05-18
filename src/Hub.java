import java.io.*;
import java.net.*;
import java.util.ArrayList;

class Hub {

    public static void main(String[] args) throws IOException {

        int numPlayers = 0;
        int port = 8008;
        ArrayList<ClientHandler> clients = new ArrayList<>();

        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            try {
                Socket holdSocket;
                holdSocket = serverSocket.accept();

                System.out.println("Here in HUB");

                ClientHandler newClient = new ClientHandler(holdSocket);
                clients.add(newClient);
                numPlayers++;

                if (numPlayers>1){
                    newClient.partner = clients.get(0);
                    clients.get(0).partner = newClient;
                    newClient.startThreads();
                    clients.get(0).startThreads();

                }





            } catch (IOException ioe) {
                System.out.println("ACK! ACK!! It's an Exception!! hub");
                System.out.println(ioe);
            }
        }
    }
}