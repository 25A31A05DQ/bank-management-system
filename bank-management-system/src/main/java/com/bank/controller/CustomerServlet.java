package com.bank.controller;

import com.bank.dao.CustomerDAO;
import com.bank.model.Customer;
import com.bank.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** GET /customers lists and searches; GET/POST /customers/add creates a customer. */
@WebServlet(urlPatterns = {"/customers", "/customers/add"})
public class CustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/customers/add".equals(req.getServletPath())) {
            showAddForm(req, resp);
        } else {
            showList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/customers/add".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        createCustomer(req, resp);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = ValidationUtil.clean(req.getParameter("q"));
        if (q.length() > 100) {
            q = q.substring(0, 100);
        }

        List<Customer> customers = new ArrayList<>();
        try {
            customers = customerDAO.findAll(q);
        } catch (SQLException e) {
            getServletContext().log("Customer list: database error", e);
            req.setAttribute("error", "Could not load customers. Check the database connection.");
        }

        req.setAttribute("activePage", "customers");
        req.setAttribute("pageTitle", "Customers");
        req.setAttribute("customers", customers);
        req.setAttribute("q", q);
        req.getRequestDispatcher("/WEB-INF/views/customers.jsp").forward(req, resp);
    }

    private void showAddForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("activePage", "add-customer");
        req.setAttribute("pageTitle", "Add customer");
        req.getRequestDispatcher("/WEB-INF/views/add-customer.jsp").forward(req, resp);
    }

    private void createCustomer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Customer c = new Customer();
        c.setName(ValidationUtil.clean(req.getParameter("name")));
        c.setPhone(ValidationUtil.clean(req.getParameter("phone")));
        c.setEmail(ValidationUtil.clean(req.getParameter("email")));
        c.setAddress(ValidationUtil.clean(req.getParameter("address")));

        List<String> errors = ValidationUtil.validateCustomer(c);
        try {
            if (errors.isEmpty() && customerDAO.emailExists(c.getEmail())) {
                errors.add("A customer with this email address already exists.");
            }
            if (errors.isEmpty()) {
                customerDAO.insert(c);
                req.getSession().setAttribute("flashSuccess",
                        "Customer added. Account number: " + c.getAccountNumber());
                resp.sendRedirect(req.getContextPath() + "/customers");
                return;
            }
        } catch (SQLException e) {
            getServletContext().log("Add customer: database error", e);
            errors.add("Could not save the customer because of a database error. Please try again.");
        }

        req.setAttribute("errors", errors);
        req.setAttribute("form", c);
        showAddForm(req, resp);
    }
}
