package com.bank.dao;

import com.bank.model.Admin;
import com.bank.util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    /** Returns the admin with the given username, or null if none exists. */
    public Admin findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash FROM admin WHERE username = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setId(rs.getInt("id"));
                    admin.setUsername(rs.getString("username"));
                    admin.setPasswordHash(rs.getString("password_hash"));
                    return admin;
                }
                return null;
            }
        }
    }
}
