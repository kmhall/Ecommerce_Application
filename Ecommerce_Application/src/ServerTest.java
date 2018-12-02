import java.net.ServerSocket;
import java.net.Socket;

/**
 * Tests the Server class
 */
public class ServerTest {
    public static void main(String[] args) throws Exception{
        ServerSocket server = new ServerSocket(123, 100);

        while (true){
            Socket socket = server.accept();
            new Thread(new Server(socket)).start();
        }
    }
}
