
/**
* this class handles the connection of telnet server with  clients
*/

import java.io.*;
import java.net.*;
import java.util.Properties;


/**
 * Class that implements a client connection to the TCP server
 */
public class ConnectionRunnable implements Runnable
{
    Socket clientSocket = null;

    public ConnectionRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            out.println("Tracer> ");
        } catch (IOException e) {
            System.out.println("exception IO");
        }
    }
}