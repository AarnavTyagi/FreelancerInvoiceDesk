package service;

import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import dao.InvoiceDao;
import dao.PaymentDao;
import model.Invoice;
import model.InvoiceStatus;
import model.Payment;

/** All reports are built with the Stream API from the lists loaded by the DAOs. */
public class ReportService {

    private final InvoiceDao invoiceDao = new InvoiceDao();
    private final PaymentDao paymentDao = new PaymentDao();

    /** Unpaid amount of each client, in alphabetical order of client name. */
    public Map<String, Double> outstandingByClient() throws SQLException {
        return invoiceDao.getAll().stream()
                .filter(invoice -> invoice.getBalance() > 0)
                .collect(Collectors.groupingBy(Invoice::getClientName, TreeMap::new,
                        Collectors.summingDouble(Invoice::getBalance)));
    }

    /** Money actually received in each month, oldest month first. */
    public Map<YearMonth, Double> monthlyEarnings() throws SQLException {
        return paymentDao.getAll().stream()
                .collect(Collectors.groupingBy(payment -> YearMonth.from(payment.getPaymentDate()), TreeMap::new,
                        Collectors.summingDouble(Payment::getAmount)));
    }

    /** All invoices with the largest unpaid balance first. */
    public List<Invoice> collectionReport() throws SQLException {
        return invoiceDao.getAll().stream()
                .sorted(Comparator.comparingDouble(Invoice::getBalance).reversed())
                .collect(Collectors.toList());
    }

    /** Number of invoices in each status (Pending, Partially Paid, Paid, Overdue). */
    public Map<InvoiceStatus, Long> statusSummary() throws SQLException {
        return invoiceDao.getAll().stream()
                .collect(Collectors.groupingBy(Invoice::getStatus, Collectors.counting()));
    }
}
