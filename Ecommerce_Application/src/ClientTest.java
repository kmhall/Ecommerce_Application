import javax.swing.*;

/**
 * Tests the Client class
 */
public class ClientTest {
    public static void main(String[] args){
        Client shopperOrBuyer;
        shopperOrBuyer = new Client();
        shopperOrBuyer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        shopperOrBuyer.runClient();
    }
}
