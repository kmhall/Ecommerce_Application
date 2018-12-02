import java.sql.*;

public class DatabaseConnection {

    private final String DATABASE_URL = "jdbc:mysql://s-l112.engr.uiowa.edu/engr_class030";
    private final String USERNAME = "engr_class030";
    private final String PASSWORD = "engr_class030-xyz";


    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;


    public void closeOperation(){
        try {
            resultSet.close();
            statement.close();
            connection.close();
        } //end try
        catch (SQLException e) {
            e.printStackTrace();
        }//end catch
    }

    public String getItems(){
        String itemList  = "";

        try {
            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            //query database
            resultSet = statement.executeQuery("SELECT * FROM `items`");

            //process query results
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            while (resultSet.next()){
                for(int i=1; i<numberOfColumns;i++){
                    if(i +1 != numberOfColumns){
                        itemList += resultSet.getString(i) + ",";

                    }else{
                        itemList += resultSet.getString(i) + "\n";
                    }
                }//end while

            }//end while


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            closeOperation();
        }

        return itemList;
    }


    public void connect(){
        try {

            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            //query database
            resultSet = statement.executeQuery("SELECT * FROM `items`");

            //process query results
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            for(int i=1;i<numberOfColumns;i++){
                System.out.print(metaData.getColumnName(i) + " ");
            }
            System.out.println("\n");

            while (resultSet.next()){
                for(int i=1; i<numberOfColumns;i++){
                    System.out.print(resultSet.getObject(i));
                }//end for
            }//end while

        } catch (SQLException e) {
            e.printStackTrace();
        }//end catch

         finally {
           closeOperation();
        }//end finally
    }



}


