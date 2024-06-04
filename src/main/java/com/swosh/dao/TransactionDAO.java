package main.java.com.swosh.dao;


import main.java.com.swosh.model.Transaction;
import main.java.com.swosh.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void createTransaction(Transaction transaction) throws SQLException {
        String deductBalanceQuery = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
        String addBalanceQuery = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        String insertTransactionQuery = "INSERT INTO transactions (sender_account_id, receiver_account_id, amount) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); 

            
            if (!checkBalance(conn, transaction.getSenderAccountId(), transaction.getAmount())) {
                throw new SQLException("Otillräckligt saldo!");
            }

            try (PreparedStatement deductStmt = conn.prepareStatement(deductBalanceQuery);
                 PreparedStatement addStmt = conn.prepareStatement(addBalanceQuery);
                 PreparedStatement insertStmt = conn.prepareStatement(insertTransactionQuery)) {

                
                deductStmt.setBigDecimal(1, transaction.getAmount());
                deductStmt.setInt(2, transaction.getSenderAccountId());
                deductStmt.executeUpdate();

               
                addStmt.setBigDecimal(1, transaction.getAmount());
                addStmt.setInt(2, transaction.getReceiverAccountId());
                addStmt.executeUpdate();

                
                insertStmt.setInt(1, transaction.getSenderAccountId());
                insertStmt.setInt(2, transaction.getReceiverAccountId());
                insertStmt.setBigDecimal(3, transaction.getAmount());
                insertStmt.executeUpdate();

                conn.commit(); 
            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            } finally {
                conn.setAutoCommit(true); 
            }
        }
    }

    private boolean checkBalance(Connection conn, int accountId, BigDecimal amount) throws SQLException {
        String query = "SELECT balance FROM accounts WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
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

    public List<Transaction> getTransactionsByDateRange(int accountId, Timestamp startDate, Timestamp endDate) throws SQLException {
        String query = "SELECT * FROM transactions WHERE (sender_account_id = ? OR receiver_account_id = ?) AND transaction_date BETWEEN ? AND ? ORDER BY transaction_date";
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            stmt.setInt(2, accountId);
            stmt.setTimestamp(3, startDate);
            stmt.setTimestamp(4, endDate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(rs.getInt("transaction_id"));
                transaction.setSenderAccountId(rs.getInt("sender_account_id"));
                transaction.setReceiverAccountId(rs.getInt("receiver_account_id"));
                transaction.setAmount(rs.getBigDecimal("amount"));
                transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
                transactions.add(transaction);
            }
        }
        return transactions;
    }
}
