package com.bank.dao;

import com.bank.model.Customer;
import com.bank.util.DBUtil;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_ACCOUNT_ATTEMPTS = 10;

    /**
     * Inserts a customer with a newly generated unique account number (AC + 10 digits).
     * The generated id and account number are set on the given object.
     */
    public void insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (account_number, name, phone, email, address, balance) "
                + "VALUES (?, ?, ?, ?, ?, 0.00)";
        try (Connection con = DBUtil.getConnection()) {
            for (int attempt = 1; attempt <= MAX_ACCOUNT_ATTEMPTS; attempt++) {
                String accountNumber = generateAccountNumber();
                if (accountExists(con, accountNumber)) {
                    continue;
                }
                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, accountNumber);
                    ps.setString(2, customer.getName());
                    ps.setString(3, customer.getPhone());
                    ps.setString(4, customer.getEmail());
                    ps.setString(5, customer.getAddress());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            customer.setId(keys.getInt(1));
                        }
                    }
                    customer.setAccountNumber(accountNumber);
                    return;
                } catch (SQLIntegrityConstraintViolationException e) {
                    // Account number collided with a concurrent insert, or the email is a duplicate.
                    if (attempt == MAX_ACCOUNT_ATTEMPTS) {
                        throw e;
                    }
                }
            }
            throw new SQLException("Could not generate a unique account number.");
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM customers WHERE email = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Returns all customers, or those whose name or account number contains the keyword. */
    public List<Customer> findAll(String keyword) throws SQLException {
        boolean search = keyword != null && !keyword.isBlank();
        String sql = "SELECT id, account_number, name, phone, email, address, balance, created_at FROM customers "
                + (search ? "WHERE name LIKE ? OR account_number LIKE ? " : "")
                + "ORDER BY name ASC, id ASC";
        List<Customer> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (search) {
                String pattern = "%" + keyword.trim() + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int count() throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM customers");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public BigDecimal totalBalance() throws SQLException {
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COALESCE(SUM(balance), 0) FROM customers");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        }
    }

    private boolean accountExists(Connection con, String accountNumber) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM customers WHERE account_number = ?")) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder("AC");
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        c.setAccountNumber(rs.getString("account_number"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setAddress(rs.getString("address"));
        c.setBalance(rs.getBigDecimal("balance"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
