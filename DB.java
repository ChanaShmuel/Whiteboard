import java.sql.*;

public class DB {

    //public static final String URL_TO_MYSQL_DB = "jdbc:mysql://127.0.0.1/whiteboard";
    //public static final String SQL_USERNAME = "root";
    //public static final String SQL_PASSWORD = "qwe123";

    public static String URL_TO_MYSQL_DB;
    public static String SQL_USERNAME;
    public static String SQL_PASSWORD;

    public static void query(String sql, SetPreparedStatement set, QueryListener listener){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try(Connection conn = DriverManager.getConnection(URL_TO_MYSQL_DB, SQL_USERNAME, SQL_PASSWORD)){
                try(PreparedStatement statement = conn.prepareStatement(sql)){
                    if(set != null)
                        set.set(statement);
                    try(ResultSet resultSet = statement.executeQuery()){
                        while (resultSet.next()){
                            listener.onResult(resultSet);
                        }
                    }catch (SQLException throwables){
                        throwables.printStackTrace();
                    }
                }catch (SQLException throwables){
                    throwables.printStackTrace();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int executeUpdateReturnGeneratedKeys(String sql, SetPreparedStatement set){
        try(Connection conn = DriverManager.getConnection(URL_TO_MYSQL_DB, SQL_USERNAME, SQL_PASSWORD)) {
            try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                set.set(statement);
                if(statement.executeUpdate() > 0){
                    try(ResultSet resultSet = statement.getGeneratedKeys()){
                        if(resultSet.next()){
                            return resultSet.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int update(String sql, SetPreparedStatement set){
        int rowsAffected = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            try(Connection conn = DriverManager.getConnection(URL_TO_MYSQL_DB, SQL_USERNAME, SQL_PASSWORD)){
                try(PreparedStatement statement = conn.prepareStatement(sql)){
                    set.set(statement);
                    rowsAffected = statement.executeUpdate();
                }catch (SQLException throwables){
                    throwables.printStackTrace();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public interface QueryListener{
        void onResult(ResultSet resultSet) throws SQLException;
    }

    public interface SetPreparedStatement{
        void set(PreparedStatement statement) throws SQLException;
    }


}
