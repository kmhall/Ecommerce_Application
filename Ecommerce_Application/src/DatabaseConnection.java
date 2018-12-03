import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to connect to SQL database
 */
public class DatabaseConnection {
    /**
     * String that specifies url of database
     */
    private final String DATABASE_URL = "jdbc:mysql://s-l112.engr.uiowa.edu/engr_class030";

    /**
     * String that specifies username of database account
     */
    private final String USERNAME = "engr_class030";

    /**
     * String that specifies password of database account
     */
    private final String PASSWORD = "engr_class030-xyz";

    /**
     * Sets a connection to the database
     */
    private Connection connection = null;

    /**
     * Creates an object to execute queries to database
     */
    private Statement statement = null;

    /**
     * Query of results from database
     */
    private ResultSet resultSet = null;

    /**
     * Adds a user's information to the database
     * @param accountInfo String, account info in format `username,password,buy,sell`.
     */
    public String createUser(String accountInfo){
        String[] arr = accountInfo.split(",");

        Boolean userNameInDatabase = false;

        try {
            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM `users`");

            while (resultSet.next()){
                    if(resultSet.getString(2).equals(arr[0])){
                        userNameInDatabase = true;
                    }
            }
            if(userNameInDatabase == false){
                String query = " INSERT INTO users (username,password,buy,sell) VALUES ('"+arr[0]+"', '"+arr[1]+"','"+arr[2]+"', '"+arr[3]+"')";
                statement.executeUpdate(query);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } //end try
            catch (SQLException e) {
                e.printStackTrace();
            }//end catch
        }

        if(userNameInDatabase){
            System.out.println("Username in database");
            return "userInDatabase";
        }
        System.out.println("Inserted account info into database" + accountInfo);
        return accountInfo;
    }

    /**
     *  Validates if the username and password are both in the database.
     * @param accountInfo Takes a String in format 'username,password'
     * @return String, If id and password match ones in the database then return user data, otherwise return null.
     */
    public String validateLogin(String accountInfo) {

        String[] arr = accountInfo.split(",");

        String output =  "incorrectCredentials";
        try {
            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            //query database
            resultSet = statement.executeQuery("SELECT * FROM `users`");

            //process query results
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (resultSet.next()) {
                if(resultSet.getString(2).equals(arr[0]) && resultSet.getString(3).equals(arr[1])){
                    output = "";
                    for(int i = 2; i <= numberOfColumns; i++) {
                        if (i != numberOfColumns) {
                            output += resultSet.getString(i) + ",";

                        } else {
                            output += resultSet.getString(i) + "\n";
                        }
                    }
                    break;
                }
            }//end while

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } //end try
            catch (SQLException e) {
                e.printStackTrace();
            }//end catch
        }
        return output;
    }

    /**
     * increments the rating of a seller
     * @param user a String of the seller
     */
    private void incrementRating(String user){
        try {
            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            String query = " UPDATE users SET `ranking` = `ranking` + 25 WHERE `username` = '"+user+"'";

            statement.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * See if the item is within the database, if it is: remove the item.
     * Otherwise do nothing and return that the input was invalid.
     *
     * @param id The id of the item attempted to be bought
     * @return If the id is valid, the id will be returned, otherwise return null
     */
    public String buyItem(String id){
        String output = "invalidItem";

        String user;
        try {
            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM `items`");

            while (resultSet.next()){
                if(resultSet.getInt(1) == Integer.parseInt(id)){
                    user = resultSet.getString(5);
                    incrementRating(user);
                    output = id;
                    break;
                }
            }//end while
            if(output.equals(id)) {
                System.out.println("Item with ID "+ id +" was bought from the market.");
                String query = " DELETE FROM items WHERE id = \'" + id + "\'";
                statement.executeUpdate(query);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } //end try
            catch (SQLException e) {
                e.printStackTrace();
            }//end catch
        }

        return output;
    }


    /**
     * Adds an item to the database to be sold
     *
     * @param itemInfo String of item info in the format `item,price,description,seller`
     */
    public void sellItem( String itemInfo){
        String[] arr = itemInfo.split(",");

        try {
            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            System.out.println("User "+ arr[3] + "placed "+ arr[0] +"on the market for $" + arr[1]);
            String query = " INSERT INTO items (item,price,description,seller) VALUES ('"+arr[0]+"', '"+arr[1]+"','"+arr[2]+"', '"+arr[3]+"')";

            statement.executeUpdate(query);


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                statement.close();
                connection.close();
            } //end try
            catch (SQLException e) {
                e.printStackTrace();
            }//end catch
        }
    }

    /**
     * Grabs ratings from all users in database
     * @return Map of users with usernames and ratings
     */
    private Map getRatings(){

        Map<String,String> rankings = new HashMap<>();

        try{
            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            //query database
            resultSet = statement.executeQuery("SELECT * FROM `users`");

            while(resultSet.next()) {
                rankings.put(resultSet.getString(2),resultSet.getString(6));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } //end try
            catch (SQLException e) {
                e.printStackTrace();
            }//end catch  }
        }
        return rankings;
    }

    /**
     * Gets all the items within the items database.
     *
     * @return String, all the items in the database in a csv format.
     * Each column is comma separated and each row is separated by a new line.
     */
    public String getItems() {
        String itemList = "";

        Map<String,String> rankings = getRatings();

        try {
            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            //query database
            resultSet = statement.executeQuery("SELECT * FROM `items`");

            //process query results
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    if (i  != numberOfColumns) {
                        itemList += resultSet.getString(i) + ",";

                    } else {
                        itemList += resultSet.getString(i) + ",";
                    }

                }//end for
                //Add rating by specifying the username Map key-value [username, ranking]
                itemList+= rankings.get(resultSet.getString(5)) + ",";

            }//end while
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } //end try
            catch (SQLException e) {
                e.printStackTrace();
            }//end catch        }
            return itemList;
        }
    }
}


