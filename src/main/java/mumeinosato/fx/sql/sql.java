package mumeinosato.fx.sql;
import java.sql.*;

public class SQL {
    private Connection connection;

    public void SQLiteConnector(String dbPath) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    public void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "uuid TEXT NOT NULL,"
                + "fx REAL NOT NULL"
                + ");";

        Statement stmt = connection.createStatement();
        stmt.execute(sql);
    }

    public  Connection getConnection() {
        return this.connection;
    }

    public void closeConnection() throws SQLException{
        if (this.connection != null) {
            this.connection.close();
        }
    }
}
