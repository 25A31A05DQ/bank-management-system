package com.bank.controller;

import com.bank.dao.TransactionDAO;
import com.bank.model.Transaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int MAX_ROWS = 500;

    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Transaction> transactions = new ArrayList<>();
        try {
            transactions = transactionDAO.findLatest(MAX_ROWS);
        } catch (SQLException e) {
            getServletContext().log("History: database error", e);
            req.setAttribute("error", "Could not load transactions. Check the database connection.");
        }

        req.setAttribute("activePage", "history");
        req.setAttribute("pageTitle", "Transaction history");
        req.setAttribute("transactions", transactions);
        req.setAttribute("maxRows", MAX_ROWS);
        req.getRequestDispatcher("/WEB-INF/views/history.jsp").forward(req, resp);
    }
}
