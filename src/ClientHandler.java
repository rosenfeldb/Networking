import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ClientHandler {

    ClientHandler partner;

    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;
    NetworkReading networkReading;
    NetworkWriting networkWriting;


    boolean isController;
//    DataInputStream dataInputStream;
//    DataOutputStream dataOutputStream;



    BlockingQueue<Integer> blockingQueue = new LinkedBlockingDeque<>();



    public ClientHandler (Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        System.out.println("NEW CLIENT HANDLER");
    }

    public void startThreads(){
        networkReading = new NetworkReading();
        networkWriting = new NetworkWriting();

        networkReading.start();
        networkWriting.start();

    }
    class NetworkReading extends Thread{
        public void run() {
            try {

                DataInputStream dataInputStream = new DataInputStream(inputStream);
                while (true) {
                    int value = dataInputStream.readInt();
                    partner.blockingQueue.put(value);

//                    System.out.println("Value: " + value);

//                    int initialD = inputStream.read();
//                    if (initialD == -1) {
//                        break;
//                    }
//                    System.out.println("initial byte: "+ initialD);
//                    int availableBytes = inputStream.available();
//                    byte[] hold = new byte[availableBytes + 1];
//                    hold[0] = (byte) initialD;
//
//                    inputStream.read(hold, 1, availableBytes);
//
//                    partner.blockingQueue.put(hold);
//                    System.out.println("Read: " +hold.length +" bytes");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        // forever loop through and read data

    }

    class NetworkWriting extends Thread{

        public void run() {
            try {

                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                while(true) {
                    int message = blockingQueue.take();
                    dataOutputStream.writeInt(message);

//                    dataOutputStream.flush();
                    //flush?
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
                //pull thing from your blocking queue
        }
    }
}