import java.sql.*;

public class DatabaseConnection {

    final String DATABASE_URL = "jdbc:mysql://s-l112.engr.uiowa.edu/engr_class030";
    final String USERNAME = "engr_class030";
    final String PASSWORD = "engr_class030-xyz";


    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private ResultSetMetaData metaData = null;


    public void connect(){
        try {

            //establish connection to database
            connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);

            //create Statement for querying database
            statement = connection.createStatement();

            //query database
            resultSet = statement.executeQuery("SELECT * FROM `items`");

            //process query results
            metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            //for(int i=1;i<numberOfColumns;i++){
            //    System.out.println(metaData.getColumnName(i));
            //}

            while (resultSet.next()){
                for(int i=1; i<numberOfColumns;i++){
                    System.out.println(resultSet.getObject(i));
                }//end while
            }//end try

        } catch (SQLException e) {
            e.printStackTrace();
        }//end catch

         finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } //end try
            catch (SQLException e) {
                e.printStackTrace();
            }//end catch
        }//end finally
    }
}
