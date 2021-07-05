import java.sql.*;

public class DB {

    public static final String URL_TO_MYSQL_DB = "jdbc:mysql://sql11.freemysqlhosting.net/sql11423204";
    public static final String SQL_USERNAME = "sql11423204";
    public static final String SQL_PASSWORD = "BDcmdUlWb4";

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
