import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


final class CommunicationConstants {
    public static final int RETRY_COUNT = 30;
    public static final int RETRY_BACKOFF_TIME = 1000;

    public static void createDelay() {
        long milliseconds = 5000;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + milliseconds;

        while (System.currentTimeMillis() < endTime) {
        }
    }

}

class Server implements Runnable {

    private static ServerSocket serverSocket;
    private static Socket receiverSocket;
    private int serverPort;
    private static DataInputStream dIn;
    private Window window;

    public Server(int serverPort,  Window window) throws IOException {
        this.serverPort = serverPort;
        serverSocket = new ServerSocket(serverPort);
        this.window = window;

    }

    public void acceptNewClient() throws IOException {
        receiverSocket = serverSocket.accept();
        // set the data input stream to the socker
        dIn = new DataInputStream(receiverSocket.getInputStream());
    }

    public void closeReceiverSocket() throws IOException {
        if (receiverSocket != null) {
            receiverSocket.close();
        }
    }

    public void receiveMessage() throws IOException {
        System.out.println();
        String receivedMessage = dIn.readUTF();
        System.out.println("Message: " + receivedMessage);
        window.setReceivedMessageToTextField(receivedMessage);
       // SwingUtilities.invokeLater(() -> messageHandler.accept(receivedMessage));
    }

    public void finalize() {
        try {
            serverSocket.close();
            receiverSocket.close();
        } catch (IOException e) {

        }

    }

    @Override
    public void run() {
        try {
            acceptNewClient();
            // after connection is established keep checking for messages

            while (true) {
                try {
                    receiveMessage();
                } catch (Exception e) {
                    System.out.println("Exception. Message: " + e.getMessage());

                    // if exception occurs break the receiving connection and wait for it again
                    closeReceiverSocket();
                    System.out.println("Connection terminated. Looking for client to connect..");
                    acceptNewClient();
                    System.out.println("Client connected sucessfully on Port: " + receiverSocket.getPort());
                }
            }
        } catch (Exception e) {

        }
    }

}

class Client implements Runnable {
    private static Socket senderSocket;
    private static DataOutputStream dOut = null;
    private static final Scanner sc = new Scanner(System.in);
    private int receiverPort;
    //private Window window;
    private static BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    public Client(int receiverPort, Window window) {
        this.receiverPort = receiverPort;
       // this.window = window;
    }

    public static void enqueueMessage(String msg) {
        messageQueue.add(msg);
    }

    private boolean initializeClientSocket() throws IOException {
        senderSocket = new Socket("localhost", receiverPort);
        System.out.println("Client Is Connected, Ready For Communication!");
        dOut = new DataOutputStream(senderSocket.getOutputStream());
        return true;
    }

    private void sendMessage() throws IOException, InterruptedException {
        String messageToSend = messageQueue.take();
       // System.out.print("Enter Message: ");
      //  messageToSend = sc.nextLine();
        dOut.writeUTF(messageToSend);
        dOut.flush();
        System.out.println(messageToSend);
    }

    @Override
    public void run() {
        int local_retry_count = 0;
        // initialize client socket in the beginning of connection
        try {
            initializeClientSocket();
        } catch (IOException e) {
            System.out.println("Failed to establish client socket");
        }

        while (true) {

            try {
                System.out.println("Sending Message..");
                sendMessage();
                local_retry_count = 0;
                System.out.println("Set retry count to 0 as connections are sucessful");
            } catch (IOException e) {

                while (local_retry_count < CommunicationConstants.RETRY_COUNT) {

                    ++local_retry_count;
                    try {
                        System.out.println("Exception Occurred. " + e.getMessage() + " Retrying Connection .."
                                + local_retry_count + "th time..");
                        Thread.sleep(CommunicationConstants.RETRY_BACKOFF_TIME);
                        initializeClientSocket();
                        break;
                    } catch (IOException e1) {
                        System.out.println("Failed initializing socket..");
                    } catch (InterruptedException e1) {
                        System.out.println("Failed initializing socket..");
                    }
                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    
    }

}

public class Communication implements ICommunication {
    private String contextMessage;
    private String receivedMessage;
    private Thread clientThread, serverThread;
    private Server  server;
    private Client client;

    public Communication(Server server, Client client){
        this.server = server;
        this.client = client;
        clientThread = new Thread(client);
        serverThread = new Thread(server);
    }

    @Override
    public void sendMessage(String message) {
        contextMessage = message;
    }

    @Override
    public String receiveMessage() {
        return receivedMessage;
    }


    public boolean initializeConnection() {
        serverThread.start();
        CommunicationConstants.createDelay();
        clientThread.start();
        return true;
    }


}
