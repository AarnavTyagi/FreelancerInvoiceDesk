package exception;

/** Thrown when a payment is zero, negative or more than the invoice balance. */
public class InvalidPaymentException extends Exception {

    public InvalidPaymentException(String message) {
        super(message);
    }
}
