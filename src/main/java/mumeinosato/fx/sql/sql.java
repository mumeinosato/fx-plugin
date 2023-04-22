package mumeinosato.fx.sql;
import java.sql.*;

public class SQL {
    private Connection connection;

    public void SQLiteConnector(String dbPath) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    public void createTable() throws SQLException {
        // usersテーブルの作成
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "uuid TEXT NOT NULL,"
                + "fx REAL NOT NULL"
                + ");";

        Statement stmt = connection.createStatement();
        stmt.execute(sqlUsers);

        // ratesテーブルの作成
        String sqlRates = "CREATE TABLE IF NOT EXISTS rates ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "rate REAL NOT NULL"
                + ");";

        stmt.execute(sqlRates);
    }

    public void updaterate(String dbPath, double rate) {
        try {
            SQLiteConnector(dbPath);
            createTable();

            // Check if rate data exists
            String selectSql = "SELECT * FROM rates";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If data exists, update the rate
                String updateSql = "UPDATE rates SET rate = ?";
                pstmt = connection.prepareStatement(updateSql);
                pstmt.setDouble(1, rate);
                pstmt.executeUpdate();
            } else {
                // If data doesn't exist, insert a new row
                String insertSql = "INSERT INTO rates (rate) VALUES (?)";
                pstmt = connection.prepareStatement(insertSql);
                pstmt.setDouble(1, rate);
                pstmt.executeUpdate();
            }

            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public double getRate(String dbPath) {
        try {
            SQLiteConnector(dbPath);

            String selectSql = "SELECT * FROM rates";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double rate = rs.getDouble("rate");
                closeConnection();;
                return rate;
            } else {
                closeConnection();;
                return Double.NaN;
            }
        }  catch (SQLException e) {
            e.printStackTrace();
            return Double.NaN;
        }
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

