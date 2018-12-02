import java.sql.*;

public class DatabaseConnection {

    private final String DATABASE_URL = "jdbc:mysql://s-l112.engr.uiowa.edu/engr_class030";
    private final String USERNAME = "engr_class030";
    private final String PASSWORD = "engr_class030-xyz";


    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;



//    String accountInfo
    public void createUser(){
//        String[] arr = accountInfo.split(",");

        try {
            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            String query = " INSERT INTO users (username,password,buy,sell) VALUES ('Brad', '456','1', '1')";

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

//    public String validateLogin(String[] arr){
//
//    }


    public String getItems() {
        String itemList = "";

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
                for (int i = 1; i < numberOfColumns; i++) {
                    if (i + 1 != numberOfColumns) {
                        itemList += resultSet.getString(i) + ",";

                    } else {
                        itemList += resultSet.getString(i) + "\n";
                    }
                }//end for
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


