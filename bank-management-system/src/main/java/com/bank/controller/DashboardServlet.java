package com.bank.controller;

import com.bank.dao.CustomerDAO;
import com.bank.dao.TransactionDAO;
import com.bank.model.Transaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("activePage", "dashboard");
        req.setAttribute("pageTitle", "Dashboard");

        int totalCustomers = 0;
        BigDecimal totalBalance = BigDecimal.ZERO;
        List<Transaction> recent = new ArrayList<>();
        try {
            totalCustomers = customerDAO.count();
            totalBalance = customerDAO.totalBalance();
            recent = transactionDAO.findLatest(5);
        } catch (SQLException e) {
            getServletContext().log("Dashboard: database error", e);
            req.setAttribute("error", "Could not load dashboard data. Check the database connection.");
        }

        req.setAttribute("totalCustomers", totalCustomers);
        req.setAttribute("totalBalance", totalBalance);
        req.setAttribute("recentTransactions", recent);
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
