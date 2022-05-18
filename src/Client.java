import CatlinGraphics2D.AnimationCanvas2D;
import CatlinGraphics2D.GraphicsWindow;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

class Client {

    static GUI gameCanvas;

    //works ?
    private static Robot robot;

    Socket socket;
    InputStream inputStream;
    OutputStream outputStream;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;


    //    PrintStream printStream;
//    InputStreamReader inputStreamReader;
//    BufferedReader bufferedReader;
    boolean isController;

    receiveAction receiver;
    sendScreen screenSender;


    private static BufferedImage buffer;

    boolean isTest;

    int width;
    int height;


    Client (Socket socket, boolean isController, boolean isTest) throws IOException {
        System.out.println("HERE IN CLIENT");
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();



        dataInputStream= new DataInputStream(inputStream);
        dataOutputStream= new DataOutputStream(outputStream);



//
//        printStream = new PrintStream(outputStream);
//        inputStreamReader = new InputStreamReader(inputStream);
//        bufferedReader = new BufferedReader(inputStreamReader);
        this.isController = isController;
        System.out.println("Am i controller? " + isController);
        this.isTest = isTest;

        if (isTest){
            width= 720;
            height = 800;

        }
        else{
            width= 1440;
            height=800;
        }

        receiver = new receiveAction();

        screenSender = new sendScreen();

        if (isController) {
            if (isTest) {
                gameCanvas = new GUI(width, height, isTest);
                GraphicsWindow.makeWindow(gameCanvas, "Remote Control - Testing - 1 Device");
            }
            if (!isTest) {
                gameCanvas = new GUI(width, height, isTest);
                GraphicsWindow.makeWindow(gameCanvas, "Remote Control");
            }
            Clicker clicker = new Clicker();
            gameCanvas.addMouseListener(clicker);
        }else{
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }

        }

//        addMouseMotionListener(clicker);
//
//        Key board = new Key();
//        addKeyListener(board);

    }

    public static void main(String[] args)
    {
        try {
            // STEP 2: CLIENT CONNECTS TO SERVER

            Scanner scanner = new Scanner(System.in);
            System.out.println("Would you like to be controlled (yes or no): ");
            String username = scanner.nextLine();

            System.out.println("Testing? (yes or no): ");
            String isTesting = scanner.nextLine();

            boolean isTest;

            if (isTesting.equals("yes")){
                isTest = true;
            }
            else{
                isTest = false;

            }

            Socket socket = new Socket("localhost", 8008);

            Client client;
            if (username.equals("yes")){
                //controlled
                client = new Client(socket, false, isTest);
                System.out.println("You are being controlled");


//                if (isTest) {
////                    robot.createScreenCapture(new Rectangle(720, 800));
//                }
//                if (!isTest) {
////                    robot.createScreenCapture(new Rectangle(1440, 800));
//                }
            }else{
                //controller
                client = new Client(socket, true, isTest);
                System.out.println("You are the controller");


            }

            client.listenForMessage();

            client.sendMessage();

        }
        catch (IOException ioe) {
            System.out.println("ACK! ACK!! It's an Exception!!");
            System.out.println(ioe);
        }
    }
    public void sendMessage(){
        if (!isController){
            screenSender.start();
        }
    }

    void sendAction (byte type, int number){
        try {
            if (isController) {

                dataOutputStream.writeInt(type);
                dataOutputStream.writeInt(number);
                System.out.println("Sender: type= " + type );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void sendAction (byte type, int number, int num){
        try {
            if (isController) {
                dataOutputStream.writeInt(type);
                dataOutputStream.writeInt(number);
                dataOutputStream.writeInt(num);
                System.out.println("Sender: type= " + type );

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    class sendScreen extends Thread{
        public void run(){
            if (isController){
                return;
            }
            try{
                while (true) {

                    buffer = robot.createScreenCapture(new Rectangle(width, height));

                    int bufferWidth = buffer.getWidth();
                    int bufferHeight = buffer.getHeight();


                    int type = 7;
                    dataOutputStream.writeInt(type);
                    System.out.println("Sender: type= " + type + " width: " + bufferWidth + " height:" + bufferHeight);
                    dataOutputStream.writeInt(bufferWidth);
                    dataOutputStream.writeInt(bufferHeight);


                    for (int y = 0; y < bufferHeight; y++) {
                        for (int x = 0; x < bufferWidth; x++) {
                            dataOutputStream.writeInt(buffer.getRGB(x, y));
                        }
                    }
                    //should i flush? yes
                    dataOutputStream.flush();
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    // STEP 4: CLIENT SENDS A MESSAGE



    public void listenForMessage() {
        receiver.start();

    }

    class receiveAction extends Thread{
        public void run(){
//            int count = 0;
//            printStream.println(username + ": test");
//            printStream.flush();
            try {
                while (true) {
//                    count++;
//                    if (count>50){
//                        break;
//                    }

                    int type = dataInputStream.readInt();
                    if (isController) {
                        System.out.println("Controller: Received Type: " + type);
                    } else {
                        System.out.println("Controlled: Received Type: " + type);
                    }
                    if (type == 7) {
                        //buffered image - controlled to controller
                        int imageWidth = dataInputStream.readInt();
                        int imageHeight = dataInputStream.readInt();
                        System.out.println("Receiver: width: " + imageWidth + ", height " + imageHeight + ",");
                        if (imageWidth > 0 && imageHeight > 0) {
                            buffer = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
                            //BufferedImage.TYPE_INT_RGB
                            for (int y = 0; y < imageHeight; y++) {
                                for (int x = 0; x < imageWidth; x++) {
                                    buffer.setRGB(x, y, dataInputStream.readInt());
                                }
                            }
                            gameCanvas.setBuffer(buffer);
                            System.out.println("Finished receiving image");
                        } else {
                            System.out.println("Width or height were <=0, skipped image");
                        }

                    } else if (type == 1) {
                        int x = dataInputStream.readInt();
                        int y = dataInputStream.readInt();
                        if (isTest) {
                            robot.mouseMove(x, y);
                            //maybe +720
                        } else {
                            robot.mouseMove(x, y);
                        }
                        System.out.println("Received mouse move");

                    } else if (type == 2) {
                        int x = dataInputStream.readInt();
                        int y = dataInputStream.readInt();
                        robot.mouseMove(x,y);
                        robot.mousePress(InputEvent.BUTTON1_MASK);

                    } else if (type == 3) {
                        int x = dataInputStream.readInt();
                        int y = dataInputStream.readInt();
                        robot.mouseMove(x,y);
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);

                    } else if (type == 4) {
                        int keyCode = dataInputStream.readInt();
                        robot.keyPress(keyCode);

                    } else if (type == 5) {
                        int keyCode = dataInputStream.readInt();
                        robot.keyRelease(keyCode);
                    } else if (type == 6) {
                        int x = dataInputStream.readInt();
                        int y = dataInputStream.readInt();
                        robot.mouseMove(x,y);
                        robot.mousePress(InputEvent.BUTTON1_MASK);
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        robot.mouseMove(x + 720,y+50);

                        System.out.println("Received mouse click, (" + x+", "+y+")");

                    } else {
                        System.out.println("Uknown Type - code closed");
                        System.exit(1);
                    }
                }
            }
            catch (IOException e) {
                System.out.println("error in client");
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }


    public class Clicker implements MouseListener, MouseMotionListener {
        int x = 0;
        int y = 0;


        @Override
        public void mouseClicked(MouseEvent e) {
            if (isTest && isController) {
                x = e.getX();
                y = e.getY();
//                robot.mouseMove(x, y);
//                robot.mousePress(InputEvent.BUTTON1_MASK);
//                robot.mouseRelease(InputEvent.BUTTON1_MASK);
//
//                robot.mouseMove(720 + x1, y1 + 38);
                byte type = 6;
                sendAction(type, x, y);
                System.out.println("Sending mouse click, (" + x+", "+y+")");

            }

        }


        //  HOW TO GET BUTTON NUMBER??????

        @Override
        public void mousePressed(MouseEvent e) {
            if (!isTest && isController){
                 x = e.getX();
                 y = e.getY();
                byte type = 2;
                sendAction(type, x,y);
          }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!isTest && isController){
                x = e.getX();
                y = e.getY();
                byte type = 3;

                sendAction(type, x,y);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            x = e.getX();
            y = e.getY();
            if (isController){
                byte type = 1;
                sendAction(type, x, y);
            }

        }

    }

    public class Key implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (!isTest && isController){
                byte type = 4;
                sendAction(type, e.getKeyCode());
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (!isTest && isController){
                byte type = 5;
                sendAction(type, e.getKeyCode());

            }
        }
    }



//        // STEP 7: CLIENT RECEIVES A RESPONSE
//        while (socket.isConnected()){
//
//            String input = scanner.nextLine();
//            printStream.println(input);
//
//            String message = bufferedReader.readLine();
//            System.out.println("CLIENT RECEIVED: " + message);
//        }
//
//
//        // STEP 8B: CLIENT CLOSES CONNECTION
//
//        bufferedReader.close();
//        printStream.close();
////        socket.close();
//
//    }


}