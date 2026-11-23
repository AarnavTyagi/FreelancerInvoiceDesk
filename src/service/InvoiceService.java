package service;

import java.sql.SQLException;
import java.time.LocalDate;

import dao.InvoiceDao;
import dao.PaymentDao;
import exception.InvalidInvoiceException;
import exception.InvalidPaymentException;
import model.Invoice;
import model.Payment;

/** Business rules for invoices and payments. The screens call this class, never the DAOs directly for saving. */
public class InvoiceService {

    private final InvoiceDao invoiceDao = new InvoiceDao();
    private final PaymentDao paymentDao = new PaymentDao();

    public void createInvoice(Invoice invoice) throws InvalidInvoiceException, SQLException {
        if (invoice.getItems().isEmpty()) {
            throw new InvalidInvoiceException("Add at least one service item to the invoice.");
        }
        if (invoice.getDueDate().isBefore(invoice.getIssueDate())) {
            throw new InvalidInvoiceException("The due date cannot be before the issue date.");
        }
        if (invoice.getTaxPercent() < 0 || invoice.getTaxPercent() > 100) {
            throw new InvalidInvoiceException("Tax percent must be between 0 and 100.");
        }
        invoiceDao.add(invoice);
    }

    public void recordPayment(Invoice invoice, double amount, LocalDate date)
            throws InvalidPaymentException, SQLException {
        if (amount <= 0) {
            throw new InvalidPaymentException("The payment amount must be greater than zero.");
        }
        if (amount > invoice.getBalance()) {
            throw new InvalidPaymentException("The payment is more than the balance of "
                    + String.format("%.2f", invoice.getBalance()) + ".");
        }
        paymentDao.add(new Payment(invoice.getId(), amount, date));
    }
}
