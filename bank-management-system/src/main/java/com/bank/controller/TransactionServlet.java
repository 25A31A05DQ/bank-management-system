package com.bank.controller;

import com.bank.dao.CustomerDAO;
import com.bank.dao.TransactionDAO;
import com.bank.model.Customer;
import com.bank.util.BankException;
import com.bank.util.ValidationUtil;
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

/** Deposit and withdrawal form (GET) and processing (POST). */
@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Optional ?account=AC123... pre-selects an account (used by the customers table).
        String account = ValidationUtil.clean(req.getParameter("account"));
        showForm(req, resp, account);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String customerIdText = ValidationUtil.clean(req.getParameter("customerId"));
        String type = ValidationUtil.clean(req.getParameter("type"));
        String amountText = ValidationUtil.clean(req.getParameter("amount"));

        req.setAttribute("selectedType", type);
        req.setAttribute("amount", amountText);

        try {
            int customerId;
            try {
                customerId = Integer.parseInt(customerIdText);
            } catch (NumberFormatException e) {
                throw new BankException("Select a customer account.");
            }
            req.setAttribute("selectedId", customerId);

            if (!TransactionDAO.DEPOSIT.equals(type) && !TransactionDAO.WITHDRAWAL.equals(type)) {
                throw new BankException("Choose deposit or withdrawal.");
            }
            BigDecimal amount = ValidationUtil.parseAmount(amountText);

            BigDecimal newBalance = transactionDAO.perform(customerId, type, amount);

            String label = TransactionDAO.DEPOSIT.equals(type) ? "Deposit" : "Withdrawal";
            req.getSession().setAttribute("flashSuccess", label + " of " + money(amount)
                    + " completed. New balance: " + money(newBalance));
            resp.sendRedirect(req.getContextPath() + "/history");
            return;
        } catch (BankException e) {
            req.setAttribute("error", e.getMessage());
        } catch (SQLException e) {
            getServletContext().log("Transaction: database error", e);
            req.setAttribute("error", "The transaction could not be completed because of a database error. "
                    + "No money was moved. Please try again.");
        }
        showForm(req, resp, null);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, String accountNumber)
            throws ServletException, IOException {
        List<Customer> customers = new ArrayList<>();
        try {
            customers = customerDAO.findAll(null);
            if (accountNumber != null && !accountNumber.isEmpty()) {
                for (Customer c : customers) {
                    if (accountNumber.equals(c.getAccountNumber())) {
                        req.setAttribute("selectedId", c.getId());
                    }
                }
            }
        } catch (SQLException e) {
            getServletContext().log("Transaction form: database error", e);
            req.setAttribute("error", "Could not load customer accounts. Check the database connection.");
        }

        req.setAttribute("activePage", "transaction");
        req.setAttribute("pageTitle", "Deposit and withdraw");
        req.setAttribute("customers", customers);
        req.getRequestDispatcher("/WEB-INF/views/transaction.jsp").forward(req, resp);
    }

    private String money(BigDecimal value) {
        String symbol = getServletContext().getInitParameter("currencySymbol");
        return (symbol == null ? "" : symbol) + String.format("%,.2f", value);
    }
}
