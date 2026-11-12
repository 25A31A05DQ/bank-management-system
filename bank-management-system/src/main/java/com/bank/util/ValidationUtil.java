package com.bank.util;

import com.bank.model.Customer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Server-side input validation. */
public final class ValidationUtil {

    /** Largest amount allowed in one deposit or withdrawal. */
    public static final BigDecimal MAX_TRANSACTION = new BigDecimal("10000000.00");

    /** Largest balance a DECIMAL(15,2) column can hold. */
    public static final BigDecimal MAX_BALANCE = new BigDecimal("9999999999999.99");

    private static final Pattern NAME = Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,99}$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    /** Returns trimmed text, or an empty string when the value is null. */
    public static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    /** Validates a customer's details and returns a list of error messages (empty when valid). */
    public static List<String> validateCustomer(Customer c) {
        List<String> errors = new ArrayList<>();
        if (!NAME.matcher(c.getName()).matches()) {
            errors.add("Name must be 2-100 characters and contain only letters, spaces, dots, apostrophes or hyphens.");
        }
        if (!PHONE.matcher(c.getPhone()).matches()) {
            errors.add("Phone number must contain 10-15 digits, with an optional leading +.");
        }
        if (c.getEmail().length() > 100 || !EMAIL.matcher(c.getEmail()).matches()) {
            errors.add("Enter a valid email address (maximum 100 characters).");
        }
        if (c.getAddress().length() < 5 || c.getAddress().length() > 255) {
            errors.add("Address must be between 5 and 255 characters.");
        }
        return errors;
    }

    /** Parses and validates a money amount. */
    public static BigDecimal parseAmount(String raw) throws BankException {
        String text = clean(raw);
        if (text.isEmpty()) {
            throw new BankException("Amount is required.");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(text);
        } catch (NumberFormatException e) {
            throw new BankException("Amount must be a valid number.");
        }
        if (amount.signum() <= 0) {
            throw new BankException("Amount must be greater than zero.");
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            throw new BankException("Amount can have at most two decimal places.");
        }
        if (amount.compareTo(MAX_TRANSACTION) > 0) {
            throw new BankException("Amount cannot exceed " + MAX_TRANSACTION.toPlainString() + " per transaction.");
        }
        return amount.setScale(2);
    }
}
