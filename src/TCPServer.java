import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by brian on 05/10/15.
 * Class that can handle multiple tcp client connections
 */
public class TCPServer {
    int serverPort;
    ServerSocket serverSocket=null;
    boolean isStopped=false;
    Thread runningThread = null;

    public TCPServer(int port) {
        this.serverPort = port;
    }

    public void run() {
        synchronized(this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        System.out.println("TCPServer up and running");
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
            new Thread(new ConnectionRunnable(clientSocket)).start();
            System.out.println("A client connected");
        }
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

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port!", e);
        }
    }
}
