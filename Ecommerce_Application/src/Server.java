import com.sun.org.apache.bcel.internal.classfile.Unknown;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Java class to act as server for clients, will communicate information between database and clients
 */
public class Server implements Runnable{
    /**
     * Instance of DatabaseConnection to allow database calls
     */
    private DatabaseConnection databaseConnection;

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
    public Server(Socket connection){
        this.connection = connection;
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
        //sendData(message);

        String itemList  = databaseConnection.getItems();
        sendData(itemList);

        do {
            try{
                message = (String) input.readObject();
                String[] messageArray = message.split(",");

                //Create String of the message without the first index

                String messageWithoutFirstIndex = "";
                for (int i = 1; i < messageArray.length; i++) {
                    if (i + 1 != messageArray.length) {
                        messageWithoutFirstIndex += messageArray[i] + ",";

                    } else {
                        messageWithoutFirstIndex += messageArray[i];
                    }
                }

                String validationStatus = "";

                //Create user
                if(messageArray[0].equals("0")) {
                    validationStatus = databaseConnection.createUser(messageWithoutFirstIndex);
                    System.out.println(validationStatus);
                }
                //Login
                else if(messageArray[0].equals("1")){

                    validationStatus = databaseConnection.validateLogin(messageWithoutFirstIndex);
                    System.out.println(validationStatus);
                    sendData(validationStatus);
                }
                //Sell
                else if(messageArray[0].equals("2")){
                    databaseConnection.buyItem(messageWithoutFirstIndex);
                }
                //Buy
                else if(messageArray[0].equals("3")){
                    validationStatus = databaseConnection.buyItem(messageWithoutFirstIndex);

                }
//                System.out.println(message);
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
            output.writeObject(message);
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

    @Override
    public void run() {
        databaseConnection = new DatabaseConnection();

        try{
            while (true){
                try{
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
}



