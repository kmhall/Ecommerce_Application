import com.sun.org.apache.bcel.internal.classfile.Unknown;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Java class to act as server for clients, will communicate information between database and clients
 */
public class Server {
    /**
     * Instance of DatabaseConnection to allow database calls
     */
    private DatabaseConnection databaseConnection;

    /**
     * ServerSocket object to assign a specific port to the server
     */
    private ServerSocket server; // server socket

    /**
     * Socket object to allow client connections to server
     */
    private Socket connection; //connection to client

    /**
     * ObjectOutputStream object, allows server to send packets to client
     */
    private ObjectOutputStream output; //output stream to client

    /**
     * ObjectInputStream object, allows server to receive packets from client
     */
    private ObjectInputStream input; // input stream to client

    /**
     * Server class constructor, creates and initializes the ServerSocket object, also
     * pulls data from database
     */
    public Server(){

        databaseConnection = new DatabaseConnection();

        try{
            server = new ServerSocket(123, 100);
            while (true){
                try{
                    waitForConnection();
                    getStreams();
                    processConnection();
                }
                catch (EOFException eofException){
                    System.out.println("Server terminated connection");
                }
                finally {
                    closeConnections();
                }
            }
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    /**
     * Connects to client and initializes Socket object
     * @throws IOException if connection failed, throws this exception
     */
    private void waitForConnection() throws IOException{
        connection = server.accept();
    }

    /**
     * Receives input and output streams from client to allow packet transfers.
     * @throws IOException if connection failed, throws this exception
     */
    private void getStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush(); //send message to client of server information

        input = new ObjectInputStream(connection.getInputStream());

        System.out.println("Got I/O Streams");
    }

    /**
     * Sends and receives packets of information from client
     * @throws IOException if connection failed, throws this exception
     */
    private void processConnection() throws IOException{


        String message = "Connection Successful";
        sendData(message);

        String itemList  = databaseConnection.getItems();
        sendData(itemList);

        do {
            try{
                message = (String) input.readObject();
                System.out.println(message);
            } catch (ClassNotFoundException classNotFoundException){
                System.out.println("Unknown object type received");
            }
        } while ( !message.equals("Client>> TERMINATE"));
    }

    /**
     * Send message as packet to client
     * @param message String of information to send to the client
     */
    private void sendData(String message){
        try {
            output.writeObject("Server>> " + message);
            output.flush();
        } catch (IOException ioException){
            System.out.println("Error writing object.");
        }
    }

    /**
     * Closes all input/output streams and socket connections
     * (probably not necessary but added for now just in case)
     */
    private void closeConnections(){
        System.out.println("Terminating connection");
        try{
            output.close();
            input.close();
            connection.close();
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

}



