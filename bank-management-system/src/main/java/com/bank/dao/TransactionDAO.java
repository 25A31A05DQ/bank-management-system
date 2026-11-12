package com.bank.dao;

import com.bank.model.Transaction;
import com.bank.util.BankException;
import com.bank.util.DBUtil;
import com.bank.util.ValidationUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public static final String DEPOSIT = "DEPOSIT";
    public static final String WITHDRAWAL = "WITHDRAWAL";

    /**
     * Applies a deposit or withdrawal inside one database transaction:
     * lock the customer row, check the balance, update it and record the transaction.
     * Everything is rolled back if any step fails.
     *
     * @return the new balance
     */
    public BigDecimal perform(int customerId, String type, BigDecimal amount) throws SQLException, BankException {
        if (!DEPOSIT.equals(type) && !WITHDRAWAL.equals(type)) {
            throw new BankException("Invalid transaction type.");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new BankException("Amount must be greater than zero.");
        }

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);
            try {
                BigDecimal current;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT balance FROM customers WHERE id = ? FOR UPDATE")) {
                    ps.setInt(1, customerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new BankException("The selected account does not exist.");
                        }
                        current = rs.getBigDecimal("balance");
                    }
                }

                BigDecimal updated;
                if (DEPOSIT.equals(type)) {
                    updated = current.add(amount);
                    if (updated.compareTo(ValidationUtil.MAX_BALANCE) > 0) {
                        throw new BankException("This deposit would exceed the maximum allowed balance.");
                    }
                } else {
                    if (current.compareTo(amount) < 0) {
                        throw new BankException("Insufficient balance. Available balance is "
                                + current.toPlainString() + ".");
                    }
                    updated = current.subtract(amount);
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE customers SET balance = ? WHERE id = ?")) {
                    ps.setBigDecimal(1, updated);
                    ps.setInt(2, customerId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO transactions (customer_id, type, amount) VALUES (?, ?, ?)")) {
                    ps.setInt(1, customerId);
                    ps.setString(2, type);
                    ps.setBigDecimal(3, amount);
                    ps.executeUpdate();
                }

                con.commit();
                return updated;
            } catch (SQLException | BankException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }
    }

    /** Latest transactions first, limited to the given number of rows. */
    public List<Transaction> findLatest(int limit) throws SQLException {
        String sql = "SELECT t.id, t.customer_id, c.account_number, c.name, t.type, t.amount, t.transaction_date "
                + "FROM transactions t JOIN customers c ON c.id = t.customer_id "
                + "ORDER BY t.transaction_date DESC, t.id DESC LIMIT ?";
        List<Transaction> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getLong("id"));
                    t.setCustomerId(rs.getInt("customer_id"));
                    t.setAccountNumber(rs.getString("account_number"));
                    t.setCustomerName(rs.getString("name"));
                    t.setType(rs.getString("type"));
                    t.setAmount(rs.getBigDecimal("amount"));
                    t.setTransactionDate(rs.getTimestamp("transaction_date"));
                    list.add(t);
                }
            }
        }
        return list;
    }
}
