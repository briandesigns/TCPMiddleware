import java.net.Socket;

/**
 * Created by brian on 06/10/15.
 */
public class ResourceManagerRunnable implements Runnable {
    Socket clientSocket = null;

    public ResourceManagerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

    }
}
