import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
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
     * Create buy button to be displayed for buyers
     */
    private JButton buy;

    /**
     * Create textField to use for buying items
     */
    private JTextField buyItemID;

    /**
     * Create sell button to be displayed for sellers
     */
    private JButton sell;
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

        buy = new JButton("Buy");
        buy.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyItemID = new JTextField(20);
        buyItemID.setMaximumSize( buyItemID.getPreferredSize() );
        sell = new JButton("Sell item");
        sell.setAlignmentX(Component.CENTER_ALIGNMENT);

        displayArea = new JTextArea();
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        logInButton = new JButton("Log In");
        logInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(logInButton);
        createAccount = new JButton("Create Account");
        createAccount.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(createAccount);

        add(buyItemID);
        add(buy);
        add(sell);
        sell.setVisible(false);
        buy.setVisible(false);
        buyItemID.setVisible(false);

        ActionHandler handler = new ActionHandler();
        logInButton.addActionListener(handler);
        createAccount.addActionListener(handler);
        buy.addActionListener(handler);
        sell.addActionListener(handler);

        setSize(600,600);
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
        //displayMessage("Attempting connection\n");

        client = new Socket( InetAddress.getLocalHost(), 12345);

        //displayMessage("Connected to: " + client.getInetAddress().getHostName());
    }

    /**
     * Receives input and output streams from server to allow packet transfers.
     * @throws IOException if connection failed, throws this exception
     */
    private void getStreams() throws IOException{
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush();

        input = new ObjectInputStream(client.getInputStream());
        //displayMessage("\nGot I/O streams\n");
    }

    /**
     * Sends and receives packets of information from server
     * @throws IOException if connection failed, throws this exception
     */
    private void processConnection() throws IOException{
        String message = "";
        do{
            try{
                message = (String) input.readObject();
                if (message.split(",")[0].equals("items")){
                    displayMessage("\n" + message);
                }
                else if(message.equals("userInDatabase")){
                    JOptionPane.showMessageDialog(null, "Username already taken\n");
                }
                else if(message.equals("incorrectCredentials")){
                    JOptionPane.showMessageDialog(null, "Incorrect log in credentials");
                }
                else{
                    String[] person = message.split(",");
                    if (person[2].equals("1")){
                        buyItemID.setVisible(true);
                        buy.setVisible(true);
                    }
                    if (person[3].toCharArray()[0] == '1'){ // for extra character at end of string, probably a "\n"
                        sell.setVisible(true);
                    }
                    logInButton.setVisible(false);
                    createAccount.setVisible(false);
                }
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
        //displayMessage("\nClosing connection");

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
            output.writeObject(message);
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
                        displayArea.setText(messageToDisplay);
                    }
                }
        );
    }

    /**
     * Private class of Client and also implements ActionListener.
     * @see ActionListener
     */
    private class ActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == logInButton){
                JFrame loggingin = new JFrame("Log In");
                loggingin.setSize(400, 200);

                Container contentPane = loggingin.getContentPane();

                JPanel userNamePanel = new JPanel();
                userNamePanel.setLayout(new BoxLayout(userNamePanel, BoxLayout.LINE_AXIS));
                JLabel userNameLabel = new JLabel("Username:");
                JTextField username = new JTextField();
                userNamePanel.add(userNameLabel);
                userNamePanel.add(username);

                JPanel passwordPanel = new JPanel();
                passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.LINE_AXIS));
                JLabel passwordLabel = new JLabel("Password:");
                JTextField password = new JTextField();
                passwordPanel.add(passwordLabel);
                passwordPanel.add(password);
                passwordPanel.setBorder(BorderFactory.createEmptyBorder(30,10,30,10));

                JButton submit = new JButton("Log In");
                submit.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                String message = "1,";
                                if (username.getText().equals("") || password.getText().equals("")){
                                    JOptionPane.showMessageDialog(null, "Please fill in all fields");
                                }
                                else {
                                    message = message + username.getText() + "," + password.getText();
                                    sendData(message);
                                    loggingin.dispatchEvent(new WindowEvent(loggingin, WindowEvent.WINDOW_CLOSING));
                                }
                            }
                        }
                );

                contentPane.add(userNamePanel, BorderLayout.NORTH);
                contentPane.add(passwordPanel, BorderLayout.CENTER);
                contentPane.add(submit, BorderLayout.SOUTH);
                loggingin.setVisible(true);
            }
            else if (e.getSource() == createAccount){
                JFrame createAccount = new JFrame("Create Account");
                createAccount.setSize(400, 200);

                Container contentPane = createAccount.getContentPane();

                JPanel userNamePanel = new JPanel();
                userNamePanel.setLayout(new BoxLayout(userNamePanel, BoxLayout.LINE_AXIS));
                JLabel userNameLabel = new JLabel("Username:");
                JTextField username = new JTextField();
                userNamePanel.add(userNameLabel);
                userNamePanel.add(username);

                JPanel passwordPanel = new JPanel();
                passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.LINE_AXIS));
                JLabel passwordLabel = new JLabel("Password:");
                JTextField password = new JTextField();
                passwordPanel.add(passwordLabel);
                passwordPanel.add(password);
                //passwordPanel.setBorder(BorderFactory.createEmptyBorder(30,10,30,10));

                JPanel buyerAndSeller = new JPanel();
                buyerAndSeller.setLayout(new BoxLayout(buyerAndSeller, BoxLayout.LINE_AXIS));
                JLabel buyerLabel = new JLabel("Buyer:");
                JCheckBox buyerCheck = new JCheckBox();
                JLabel sellerLabel = new JLabel("Seller:");
                JCheckBox sellerCheck = new JCheckBox();
                buyerAndSeller.add(buyerLabel);
                buyerAndSeller.add(buyerCheck);
                buyerAndSeller.add(sellerLabel);
                buyerAndSeller.add(sellerCheck);

                JButton submit = new JButton("Create Account");
                submit.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (username.getText().equals("")|| username.getText().equals("") || (!buyerCheck.isSelected() && !sellerCheck.isSelected())){
                                    JOptionPane.showMessageDialog(null, "Please fill in all fields");
                                }
                                else{
                                    String message = "0,";
                                    message = message + username.getText() + "," + password.getText() + ",";
                                    if (buyerCheck.isSelected()){
                                        message = message + "1";
                                        if (sellerCheck.isSelected()){
                                            message = message + "," + "1";
                                        }
                                        else{
                                            message = message + "," + "0";
                                        }
                                    }
                                    else if (sellerCheck.isSelected()){
                                        message = message + "0" + "," + "1";
                                    }
                                    sendData(message);
                                    createAccount.dispatchEvent(new WindowEvent(createAccount, WindowEvent.WINDOW_CLOSING));
                                }
                            }
                        }
                );

                contentPane.add(userNamePanel, BorderLayout.NORTH);
                contentPane.add(passwordPanel, BorderLayout.CENTER);
                contentPane.add(buyerAndSeller, BorderLayout.EAST);
                contentPane.add(submit, BorderLayout.SOUTH);
                createAccount.setVisible(true);
            }
            else if (e.getSource() == buy){
                String itemID = "3," + buyItemID.getText();
                sendData(itemID);
            }
            else if (e.getSource() == sell){
                JFrame sellItem = new JFrame("Item to sell");
                sellItem.setSize(400, 200);

                Container contentPane = sellItem.getContentPane();

                JPanel itemNamePanel = new JPanel();
                itemNamePanel.setLayout(new BoxLayout(itemNamePanel, BoxLayout.LINE_AXIS));
                JLabel itemNameLabel = new JLabel("Item Name:");
                JTextField itemName = new JTextField(20);
                itemName.setMaximumSize( itemName.getPreferredSize() );
                itemNamePanel.add(itemNameLabel);
                itemNamePanel.add(itemName);

                JPanel sellerNamePanel = new JPanel();
                sellerNamePanel.setLayout(new BoxLayout(sellerNamePanel, BoxLayout.LINE_AXIS));
                JLabel sellerNameLabel = new JLabel("Seller Name:");
                JTextField sellerName = new JTextField(20);
                sellerName.setMaximumSize( sellerName.getPreferredSize() );
                sellerNamePanel.add(sellerNameLabel);
                sellerNamePanel.add(sellerName);
                //passwordPanel.setBorder(BorderFactory.createEmptyBorder(30,10,30,10));

                JPanel pricePanel = new JPanel();
                pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.LINE_AXIS));
                JLabel priceLabel = new JLabel("Price:");
                JTextField price = new JTextField(20);
                price.setMaximumSize( price.getPreferredSize() );
                pricePanel.add(priceLabel);
                pricePanel.add(price);

                JPanel descriptionPanel = new JPanel();
                descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.LINE_AXIS));
                JLabel descriptionLabel = new JLabel("Description:");
                JTextField description = new JTextField(20);
                description.setMaximumSize( price.getPreferredSize() );
                descriptionPanel.add(descriptionLabel);
                descriptionPanel.add(description);

                JButton submit = new JButton("Sell Item");
                submit.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                               if (itemName.getText().equals("") || sellerName.getText().equals("") || price.getText().equals("") || description.getText().equals("")) {
                                   JOptionPane.showMessageDialog(null, "Please fill in all fields");
                               }
                               else{
                                   String message = "2,";
                                   message = message + itemName.getText() + "," + price.getText() + "," + description.getText() + "," + sellerName.getText();
                                   sendData(message);
                                   sellItem.dispatchEvent(new WindowEvent(sellItem, WindowEvent.WINDOW_CLOSING));
                               }
                            }
                        }
                );

                JPanel info = new JPanel();
                info.setLayout(new BoxLayout(info, BoxLayout.PAGE_AXIS));
                info.add(descriptionPanel, BorderLayout.NORTH);
                info.add(pricePanel, BorderLayout.CENTER);
                info.add(sellerNamePanel, BorderLayout.SOUTH);

                contentPane.add(itemNamePanel, BorderLayout.NORTH);
                contentPane.add(info, BorderLayout.CENTER);
                contentPane.add(submit, BorderLayout.SOUTH);
                sellItem.setVisible(true);
            }
        }
    }
}
