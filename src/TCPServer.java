import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by brian on 05/10/15.
 * Class that can handle multiple tcp client connections
 * Once listner picks up a client, it hands it off to a Connectionrunnable
 */
public class TCPServer implements Runnable{
    int serverPort;
    String serverType;
    ServerSocket serverSocket=null;
    boolean isStopped=false;
    Thread runningThread = null;
    public static final String MIDDLEWARE = "MIDDLEWARE";
    public static final String CAR_RM = "CAR_RM";
    public static final String FLIGHT_RM = "FLIGHT_RM";
    public static final String ROOM_RM = "ROOM_RM";

    public static RMHashtable m_itemHT_customer = new RMHashtable();
    public static RMHashtable m_itemHT_car = new RMHashtable();
    public static RMHashtable m_itemHT_room = new RMHashtable();
    public static RMHashtable m_itemHT_flight = new RMHashtable();




    public TCPServer(int port, String serverType) {
        this.serverPort = port;
        this.serverType = serverType;
    }


    public void run() {
        synchronized(this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        System.out.println("TCPServer up and running");
        listenForClient();
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch(IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void listenForClient() {
        while(! isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("server Stopped1");
                    return;
                }
                throw new RuntimeException ("error accepting client connection", e);
            }
            if (this.serverType.equals(MIDDLEWARE)) {
                new Thread(new MiddlewareRunnable(clientSocket)).start();
            } else if(this.serverType.equals(CAR_RM)) {
                new Thread(new ResourceManagerRunnable(clientSocket,CAR_RM)).start();
            } else if (this.serverType.equals(FLIGHT_RM)) {
                new Thread(new ResourceManagerRunnable(clientSocket,FLIGHT_RM)).start();
            } else if (this.serverType.equals(ROOM_RM)) {
                new Thread(new ResourceManagerRunnable(clientSocket,ROOM_RM)).start();
            }
            System.out.println("A client connected");
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port!", e);
        }
    }

    public static void main(String[] args) {
        TCPServer server = new TCPServer(Integer.parseInt(args[0]), args[1]);
        new Thread(server).start();
    }
}


