import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Java class to act as client for server, will have GUI for user interaction
 * @see JFrame
 */
public class Client extends JFrame {
    /**
     * Creates a Swing component for sending information to the server
     */
    private JTextField enterField;

    /**
     * Creates a Swing component for displaying information from the server
     */
    private JTextArea displayArea;

    /**
     * Creates an output stream to send packets of information to server
     */
    private ObjectOutputStream output;

    /**
     * Creates an input stream to receive packets of information from server
     */
    private ObjectInputStream input;

    /**
     * Creates a socket that connects to the Server port for communication
     */
    private Socket client;

    /**
     * Creates a button for the user to log-in
     */
    private JButton logInButton;

    /**
     * creates a button for the user to create an account
     */
    private JButton createAccount;

    /**
     * Constructor for Client class, initializes Swing components
     */
    public Client(){
        super("Store");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        enterField = new JTextField(10);
        enterField.setMaximumSize(enterField.getPreferredSize());
        enterField.setEditable(false);
        enterField.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendData(e.getActionCommand());
                        enterField.setText("");
                    }
                }
        );

        add(enterField, BorderLayout.NORTH);

        displayArea = new JTextArea();
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        logInButton = new JButton("Log In");
        createAccount = new JButton("Create Account");
        add(logInButton);
        add(createAccount);

        setSize(300,300);
        setVisible(true);
    }

    /**
     * Set up client to server connection and process information
     */
    public void runClient(){
        try{
            connectToServer();
            getStreams();
            processConnection();
        } catch (EOFException eofException){
            displayMessage("\nClient terminated connection");
        } catch (IOException ioException){
            ioException.printStackTrace();
        }
        finally {
            closeConnections();
        }
    }

    /**
     * Connects clients to server by creating a new Socket
     * @throws IOException if connection failed, throws this exception
     */
    private void connectToServer() throws IOException{
        displayMessage("Attempting connection\n");

        client = new Socket( InetAddress.getLocalHost(), 123);

        displayMessage("Connected to: " + client.getInetAddress().getHostName());
    }

    /**
     * Receives input and output streams from server to allow packet transfers.
     * @throws IOException if connection failed, throws this exception
     */
    private void getStreams() throws IOException{
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush();

        input = new ObjectInputStream(client.getInputStream());
        displayMessage("\nGot I/O streams\n");
    }

    /**
     * Sends and receives packets of information from server
     * @throws IOException if connection failed, throws this exception
     */
    private void processConnection() throws IOException{
        setTextFieldEditable(true);
        String message = "";
        do{
            try{
                message = (String) input.readObject();
                displayMessage("\n" + message);
            } catch (ClassNotFoundException classNotFoundException){
                displayMessage("\nUnknown object type received");
            }
        } while (!message.equals("Server>> TERMINATE"));
    }

    /**
     * Closes all input/output streams and socket connections
     * (probably not necessary but added for now just in case)
     */
    private void closeConnections(){
        displayMessage("\nClosing connection");
        setTextFieldEditable(false);

        try{
            output.close();
            input.close();
            client.close();
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    /**
     * sends information to the server via object stream
     * @param message String of information to send
     */
    private void sendData(String message){
        try{
            output.writeObject("Client>> " + message);
            output.flush();
        } catch (IOException ioException){
            displayArea.append("\nError writing object");
        }
    }

    /**
     * Displays message to GUI
     * @param messageToDisplay String of the message to display
     */
    private void displayMessage( final String messageToDisplay){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        displayArea.append(messageToDisplay);
                    }
                }
        );
    }

    private void displayButton(){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        add(new JButton("Buy"), BorderLayout.CENTER);
                    }
                }
        );
    }

    /**
     * sets the JTextField edibility to either true or false
     * @param editable either true or false
     */
    private void setTextFieldEditable (final boolean editable){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        enterField.setEditable(editable);
                    }
                }
        );
    }
}
