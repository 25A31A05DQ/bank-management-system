package com.bank.controller;

import com.bank.dao.AdminDAO;
import com.bank.filter.AuthFilter;
import com.bank.model.Admin;
import com.bank.util.PasswordUtil;
import com.bank.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String VIEW = "/WEB-INF/views/login.jsp";

    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(AuthFilter.SESSION_USER) != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        if ("1".equals(req.getParameter("logout"))) {
            req.setAttribute("success", "You have been signed out.");
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = ValidationUtil.clean(req.getParameter("username"));
        String password = req.getParameter("password");

        if (username.isEmpty() || password == null || password.isEmpty()) {
            fail(req, resp, username, "Enter both username and password.");
            return;
        }

        try {
            Admin admin = adminDAO.findByUsername(username);
            if (admin != null && PasswordUtil.verify(password, admin.getPasswordHash())) {
                HttpSession old = req.getSession(false);
                if (old != null) {
                    old.invalidate(); // new session id after login prevents session fixation
                }
                HttpSession session = req.getSession(true);
                session.setAttribute(AuthFilter.SESSION_USER, admin.getUsername());
                session.setMaxInactiveInterval(30 * 60);
                resp.sendRedirect(req.getContextPath() + "/dashboard");
                return;
            }
            fail(req, resp, username, "Invalid username or password.");
        } catch (SQLException e) {
            getServletContext().log("Login failed: database error", e);
            fail(req, resp, username, "Cannot reach the database. Check that MySQL is running and db.properties is correct.");
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String username, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.setAttribute("username", username);
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
