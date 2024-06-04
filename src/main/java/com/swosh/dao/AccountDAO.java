package main.java.com.swosh.dao;



import main.java.com.swosh.model.Account;
import main.java.com.swosh.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public void addAccount(Account account) throws SQLException {
        String query = "INSERT INTO accounts (user_id, account_number, balance) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, account.getUserId());
            stmt.setString(2, account.getAccountNumber());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.executeUpdate();
        }
    }

    public void deleteAccount(int accountId) throws SQLException {
        String query = "DELETE FROM accounts WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            stmt.executeUpdate();
        }
    }

    public List<Account> getAccountsByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM accounts WHERE user_id = ?";
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Account account = new Account();
                account.setAccountId(rs.getInt("account_id"));
                account.setUserId(rs.getInt("user_id"));
                account.setAccountNumber(rs.getString("account_number"));
                account.setBalance(rs.getBigDecimal("balance"));
                accounts.add(account);
            }
        }
        return accounts;
    }

    public boolean checkBalance(int accountId, BigDecimal amount) throws SQLException {
        String query = "SELECT balance FROM accounts WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                BigDecimal balance = rs.getBigDecimal("balance");
                return balance.compareTo(amount) >= 0;
            } else {
                return false;
            }
        }
    }

    public boolean accountExists(int accountId) throws SQLException {
        String query = "SELECT account_id FROM accounts WHERE account_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
}
