package exception;

/** Thrown when an invoice breaks a business rule (no items, bad dates, bad tax). */
public class InvalidInvoiceException extends Exception {

    public InvalidInvoiceException(String message) {
        super(message);
    }
}
