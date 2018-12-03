import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

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
     * when user logs in or creates an account, info is saved here
     */
    private String[] userInfo;

    /**
     * Constructor for Client class, initializes Swing components
     */
    public Client(){
        super("Store");
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        buy = new JButton("Buy");
        buy.setAlignmentX(Component.CENTER_ALIGNMENT);
        buyItemID = new JTextField(20);
        buyItemID.setMaximumSize( buyItemID.getPreferredSize() );
        buyItemID.setText("Enter item I.D. to buy item.");
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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeConnections();
            }
        });

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
        client = new Socket( InetAddress.getLocalHost(), 12345);
    }

    /**
     * Receives input and output streams from server to allow packet transfers.
     * @throws IOException if connection failed, throws this exception
     */
    private void getStreams() throws IOException{
        output = new ObjectOutputStream(client.getOutputStream());
        output.flush();

        input = new ObjectInputStream(client.getInputStream());
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
                    displayMessage(message);
                }
                else if(message.equals("userInDatabase")){
                    JOptionPane.showMessageDialog(null, "Username already taken\n");
                }
                else if(message.equals("incorrectCredentials")){
                    JOptionPane.showMessageDialog(null, "Incorrect log in credentials");
                }
                else{
                    String[] person = message.split(",");
                    userInfo = person; //saves to instance variable
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
            } catch (SocketException s){

            }
        } while (!message.equals("Server>> TERMINATE"));
    }

    /**
     * Closes all input/output streams and socket connections
     * (probably not necessary but added for now just in case)
     */
    private void closeConnections(){
        try{
            input.close();
            output.close();
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
                        displayArea.setText("");
                        String[] itemInfo = messageToDisplay.split(",");
                        int counter = 0;
                        for (String item: itemInfo){
                            if (counter!=0){
                                if (counter == 1){
                                    displayArea.append("Item I.D. = " + item + "\n");
                                    counter++;
                                }
                                else if (counter == 2){
                                    displayArea.append("---Name: " + item + "\n");
                                    counter++;
                                }
                                else if (counter == 3){
                                    displayArea.append("---Price: $" + item + "\n");
                                    counter++;
                                }
                                else if (counter == 4){
                                    displayArea.append("---Description: " + item + "\n");
                                    counter++;
                                }
                                else if (counter == 5){
                                    displayArea.append("---Seller: " + item + "\n");
                                    counter++;
                                }
                                else if (counter == 6){
                                    displayArea.append("---Ranking: " + item + "\n");
                                    displayArea.append("\n");
                                    counter = 1;
                                }
                            }
                            else{
                                counter++;
                            }
                        }
                        if (!buy.isVisible() && !sell.isVisible()){
                            displayArea.append("Log in or create account to buy items.");
                        }
                    }
                }
        );
    }

    /**
     * Private class of Client and also implements ActionListener.
     * @see ActionListener
     */
    private class ActionHandler implements ActionListener {
        /**
         * Function that is overridden from ActionListener. Handles all button presses.
         * @param e ActionEvent object that actually registers the action
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == logInButton){
                logInButton.setEnabled(false);
                createAccount.setEnabled(false);
                JFrame loggingin = new JFrame("Log In");
                loggingin.setSize(300, 150);

                Container contentPane = loggingin.getContentPane();

                JPanel userNamePanel = new JPanel();
                userNamePanel.setLayout(new BoxLayout(userNamePanel, BoxLayout.LINE_AXIS));
                JLabel userNameLabel = new JLabel("Username:");
                JTextField username = new JTextField(20);
                username.setMaximumSize( username.getPreferredSize() );
                userNamePanel.add(userNameLabel);
                userNamePanel.add(username);
                userNamePanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

                JPanel passwordPanel = new JPanel();
                passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.LINE_AXIS));
                JLabel passwordLabel = new JLabel("Password:");
                JTextField password = new JTextField(20);
                password.setMaximumSize( password.getPreferredSize() );
                passwordPanel.add(passwordLabel);
                passwordPanel.add(password);

                JButton submit = new JButton("Log In");
                submit.setAlignmentX(Component.CENTER_ALIGNMENT);
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

                loggingin.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        logInButton.setEnabled(true);
                        createAccount.setEnabled(true);
                    }
                });

                contentPane.add(userNamePanel, BorderLayout.NORTH);
                contentPane.add(passwordPanel, BorderLayout.CENTER);
                contentPane.add(submit, BorderLayout.SOUTH);
                loggingin.setVisible(true);
            }
            else if (e.getSource() == createAccount){
                JFrame createAccountFrame = new JFrame("Create Account");
                createAccountFrame.setSize(400, 150);
                logInButton.setEnabled(false);
                createAccount.setEnabled(false);

                Container contentPane = createAccountFrame.getContentPane();

                JPanel userNamePanel = new JPanel();
                userNamePanel.setLayout(new BoxLayout(userNamePanel, BoxLayout.LINE_AXIS));
                JLabel userNameLabel = new JLabel("Username:");
                JTextField username = new JTextField();
                userNamePanel.add(userNameLabel);
                userNamePanel.add(username);
                userNamePanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

                JPanel passwordPanel = new JPanel();
                passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.LINE_AXIS));
                JLabel passwordLabel = new JLabel("Password:");
                JTextField password = new JTextField(20);
                password.setMaximumSize( password.getPreferredSize() );
                passwordPanel.add(passwordLabel);
                passwordPanel.add(password);

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
                submit.setAlignmentX(Component.CENTER_ALIGNMENT);
                submit.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (username.getText().equals("")|| password.getText().equals("") || (!buyerCheck.isSelected() && !sellerCheck.isSelected())){
                                    JOptionPane.showMessageDialog(null, "Please fill in all fields");
                                }
                                else if(username.getText().length() >30){
                                    JOptionPane.showMessageDialog(null, "Username must be under 30 characters");
                                }
                                else if(password.getText().length() >30){
                                    JOptionPane.showMessageDialog(null, "Password must be under 30 characters");
                                }
                                else if(username.getText().contains(",")){
                                    JOptionPane.showMessageDialog(null, "Username cannot contain a comma");
                                }
                                else if(password.getText().contains(",")){
                                    JOptionPane.showMessageDialog(null, "Password cannot contain a comma");
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
                                    else if (sellerCheck.isSelected() ){
                                        message = message + "0" + "," + "1";
                                    }
                                    sendData(message);
                                    createAccountFrame.dispatchEvent(new WindowEvent(createAccountFrame, WindowEvent.WINDOW_CLOSING));
                                }
                            }
                        }
                );

                createAccountFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        logInButton.setEnabled(true);
                        createAccount.setEnabled(true);
                    }
                });

                contentPane.add(userNamePanel, BorderLayout.NORTH);
                contentPane.add(passwordPanel, BorderLayout.CENTER);
                contentPane.add(buyerAndSeller, BorderLayout.EAST);
                contentPane.add(submit, BorderLayout.SOUTH);
                createAccountFrame.setVisible(true);
            }
            else if (e.getSource() == buy){

                if(buyItemID.getText().length() == 0){
                    JOptionPane.showMessageDialog(null, "No I.D. entered");
                }
                 else if(!buyItemID.getText().matches("[0-9]+")  ){
                    JOptionPane.showMessageDialog(null, "Item I.D. must be an integer");
                }

                else {
                     String itemID = "3," + buyItemID.getText();
                     buyItemID.setText("");
                     sendData(itemID);
                 }
            }
            else if (e.getSource() == sell){
                JFrame sellItem = new JFrame("Item to sell");
                sellItem.setSize(350, 200);
                sellItem.setLayout(new BoxLayout(sellItem.getContentPane(), BoxLayout.Y_AXIS));
                buy.setEnabled(false);
                sell.setEnabled(false);

                JPanel itemNamePanel = new JPanel();
                itemNamePanel.setLayout(new FlowLayout());
                JLabel itemNameLabel = new JLabel("Item Name:");
                JTextField itemName = new JTextField(20);
                itemName.setMaximumSize( itemName.getPreferredSize() );
                itemNamePanel.add(itemNameLabel);
                itemNamePanel.add(itemName);

                JPanel pricePanel = new JPanel();
                pricePanel.setLayout(new FlowLayout());
                JLabel priceLabel = new JLabel("Price:");
                JTextField price = new JTextField(20);
                price.setMaximumSize( price.getPreferredSize() );
                pricePanel.add(priceLabel);
                pricePanel.add(price);

                JPanel descriptionPanel = new JPanel();
                descriptionPanel.setLayout(new FlowLayout());
                JLabel descriptionLabel = new JLabel("Description:");
                JTextField description = new JTextField(20);
                description.setMaximumSize( price.getPreferredSize() );
                descriptionPanel.add(descriptionLabel);
                descriptionPanel.add(description);

                JButton submit = new JButton("Sell Item");
                submit.setAlignmentX(Component.CENTER_ALIGNMENT);
                submit.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                               if (itemName.getText().equals("") || price.getText().equals("") || description.getText().equals("")) {
                                   JOptionPane.showMessageDialog(null, "Please fill in all fields");
                               }
                               else if(itemName.getText().length() > 30){
                                   JOptionPane.showMessageDialog(null, "Item name must be under 30 characters");
                               }
                               else if(price.getText().length() > 9){
                                   JOptionPane.showMessageDialog(null, "Price must be under 30 characters");
                               }
                               else if(description.getText().length() > 200){
                                   JOptionPane.showMessageDialog(null, "Description must be under 200 characters");
                               }
                               else if(itemName.getText().contains(",")){
                                   JOptionPane.showMessageDialog(null, "Item name cannot have a comma");
                               }
                               else if(!price.getText().matches("[0-9]+")){
                                   JOptionPane.showMessageDialog(null, "Price must be an integer");
                               }
                               else{
                                   description.setText(description.getText().replace(",",";"));

                                   String message = "2,";
                                   message = message + itemName.getText() + "," + price.getText() + "," + description.getText() + "," + userInfo[0];
                                   sendData(message);
                                   sellItem.dispatchEvent(new WindowEvent(sellItem, WindowEvent.WINDOW_CLOSING));
                               }
                            }
                        }
                );

                sellItem.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        buy.setEnabled(true);
                        sell.setEnabled(true);
                    }
                });

                sellItem.add(itemNamePanel);
                sellItem.add(pricePanel);
                sellItem.add(descriptionPanel);
                sellItem.add(submit);
                sellItem.setVisible(true);
            }
        }
    }
}
