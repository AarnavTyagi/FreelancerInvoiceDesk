package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dao.InvoiceDao;
import dao.PaymentDao;
import exception.InvalidPaymentException;
import model.Invoice;
import model.Payment;
import service.InvoiceService;

/** Record full or partial payments against unpaid invoices and see the payment history. */
public class PaymentPanel extends BasePanel {

    private final InvoiceDao invoiceDao = new InvoiceDao();
    private final PaymentDao paymentDao = new PaymentDao();
    private final InvoiceService invoiceService = new InvoiceService();

    private final JComboBox<Invoice> invoiceBox = new JComboBox<>();
    private final JTextField amountField = new JTextField();
    private final JTextField dateField = new JTextField(LocalDate.now().toString());

    public PaymentPanel() {
        super("Payment ID", "Invoice No", "Payment Date", "Amount");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        addField(form, "Unpaid invoice", invoiceBox);
        addField(form, "Amount", amountField);
        addField(form, "Payment date (yyyy-MM-dd)", dateField);

        JButton payButton = new JButton("Record Payment");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(payButton);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // Picking an invoice suggests the full balance. Type a smaller amount for a partial payment.
        invoiceBox.addActionListener(e -> {
            Invoice invoice = (Invoice) invoiceBox.getSelectedItem();
            amountField.setText(invoice == null ? "" : String.valueOf(invoice.getBalance()));
        });
        payButton.addActionListener(e -> recordPayment());
    }

    @Override
    public void refresh() {
        try {
            invoiceBox.removeAllItems();
            invoiceDao.getAll().stream()
                    .filter(invoice -> invoice.getBalance() > 0)
                    .sorted(Comparator.comparing(Invoice::getDueDate))
                    .forEach(invoiceBox::addItem);

            model.setRowCount(0);
            for (Payment p : paymentDao.getAll()) {
                model.addRow(new Object[] { p.getId(), p.getInvoiceNumber(), p.getPaymentDate(), money(p.getAmount()) });
            }
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void recordPayment() {
        Invoice invoice = (Invoice) invoiceBox.getSelectedItem();
        if (invoice == null) {
            showError("There is no unpaid invoice to pay.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());

            invoiceService.recordPayment(invoice, amount, date);

            showInfo("Payment recorded for " + invoice.getInvoiceNumber() + ".");
            refresh();
        } catch (NumberFormatException | DateTimeParseException ex) {
            showError("Enter the amount as a number and the date as yyyy-MM-dd.");
        } catch (InvalidPaymentException ex) {
            showError(ex.getMessage());
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }
}
