package com.bank.util;

/** Business-rule or validation failure whose message is safe to show to the user. */
public class BankException extends Exception {
    private static final long serialVersionUID = 1L;

    public BankException(String message) {
        super(message);
    }
}
