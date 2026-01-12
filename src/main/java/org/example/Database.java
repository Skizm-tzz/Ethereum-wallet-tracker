package org.example;

import java.sql.*;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:wallet.db";

    // Подключение к базе
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Инициализация таблицы
    public static void init() {
        String createTransactionsTable =
                "CREATE TABLE IF NOT EXISTS transactions (" +
                        "hash TEXT PRIMARY KEY, " +
                        "from_address TEXT, " +
                        "to_address TEXT, " +
                        "value TEXT, " +
                        "timestamp TEXT" +
                        ");";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTransactionsTable);
            System.out.println("База данных готова");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Проверка наличия транзакции
    public static boolean existsByHash(String hash) {
        String sql = "SELECT 1 FROM transactions WHERE hash = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hash);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Сохранение транзакции
    public static void saveTransaction(String hash, String from, String to, String value, String timestamp) {
        String sql = "INSERT INTO transactions(hash, from_address, to_address, value, timestamp) VALUES(?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hash);
            pstmt.setString(2, from);
            pstmt.setString(3, to);
            pstmt.setString(4, value);
            pstmt.setString(5, timestamp);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
