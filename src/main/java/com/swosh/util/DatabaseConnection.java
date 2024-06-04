package main.java.com.swosh.util;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/swosh";
    private static final String USER = "root";
    private static final String PASSWORD = "Benim321shurda"; 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                                  "user_id INT AUTO_INCREMENT PRIMARY KEY," +
                                  "personal_number VARCHAR(12) NOT NULL UNIQUE," +
                                  "password VARCHAR(255) NOT NULL," +
                                  "first_name VARCHAR(50)," +
                                  "last_name VARCHAR(50)" +
                                  ")";

        String createAccountsTable = "CREATE TABLE IF NOT EXISTS accounts (" +
                                     "account_id INT AUTO_INCREMENT PRIMARY KEY," +
                                     "user_id INT," +
                                     "account_number VARCHAR(20) NOT NULL UNIQUE," +
                                     "balance DECIMAL(15, 2) NOT NULL," +
                                     "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                                     ")";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                                         "transaction_id INT AUTO_INCREMENT PRIMARY KEY," +
                                         "sender_account_id INT," +
                                         "receiver_account_id INT," +
                                         "amount DECIMAL(15, 2) NOT NULL," +
                                         "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                         "FOREIGN KEY (sender_account_id) REFERENCES accounts(account_id)," +
                                         "FOREIGN KEY (receiver_account_id) REFERENCES accounts(account_id)" +
                                         ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createAccountsTable);
            stmt.execute(createTransactionsTable);
        }
    }
}

