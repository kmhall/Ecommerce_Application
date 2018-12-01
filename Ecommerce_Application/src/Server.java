import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Java class to act as server for clients, will communicate information between database and clients
 */
public class Server {
    /**
     * ServerSocket object to assign a specific port to the server
     */
    private ServerSocket server; // server socket

    /**
     * Socket object to allow client connections to server
     */
    private Socket connection; //connection to client

    /**
     * Server class constructor, creates the initializes the ServerSocket object
     */
    public Server(){
        try{
            server = new ServerSocket(123, 100);
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }
}
