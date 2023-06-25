package mumeinosato.fx.sql;
import mumeinosato.fx.Fx;

import java.sql.*;

public class SQL {
    private Connection connection;

    Fx plugin = Fx.getInstance();
    String dbPath = plugin.getDBPath();

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

    public void updaterate(double rate) {
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
    public double getRate() {
        try {
            SQLiteConnector(dbPath);

            String selectSql = "SELECT * FROM rates";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double rate = rs.getDouble("rate");
                closeConnection();
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

    public void addfx(String uuid, int value) {
        try {
            SQLiteConnector(dbPath);
            String selectSql = "SELECT * FROM users";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If data exists for the given UUID, update the fx value
                double currentFx = rs.getDouble("fx");
                double newFx = currentFx + value;

                String updateSql = "UPDATE users SET fx = ? WHERE uuid = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setDouble(1, newFx);
                updateStmt.setString(2, uuid);
                updateStmt.executeUpdate();
            } else {
                // If data doesn't exist for the given UUID, insert a new row
                String insertSql = "INSERT INTO users (uuid, fx) VALUES (?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setString(1, uuid);
                insertStmt.setDouble(2, value);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int pgetfx(String uuid) {
        try {
            SQLiteConnector(dbPath);
            String selectSql = "SELECT * FROM users WHERE uuid = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Data exists for the given UUID
                closeConnection();
                return 1;
            } else {
                // Data doesn't exist for the given UUID
                closeConnection();
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void subtractfx(String uuid, int value) {
        try {
            SQLiteConnector(dbPath);
            String selectSql = "SELECT * FROM users WHERE uuid = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // If data exists for the given UUID, update the fx value
                double currentFx = rs.getDouble("fx");
                double newFx = currentFx - value;

                String updateSql = "UPDATE users SET fx = ? WHERE uuid = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setDouble(1, newFx);
                updateStmt.setString(2, uuid);
                updateStmt.executeUpdate();
            }
            closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double checkfx(String uuid) {
        try {
            SQLiteConnector(dbPath);
            String selectSql = "SELECT * FROM users WHERE uuid = ?";
            PreparedStatement pstmt = connection.prepareStatement(selectSql);
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Data exists for the given UUID
                double fx = rs.getDouble("fx");
                closeConnection();
                return fx;
            } else {
                // Data doesn't exist for the given UUID
                closeConnection();
                return Double.NaN;
            }
        } catch (SQLException e) {
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