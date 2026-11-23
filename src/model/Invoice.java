package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Invoice {

    private int id;
    private String invoiceNumber;
    private final int freelancerId;
    private final int clientId;
    private String freelancerName;   // filled by the DAO, only used for display
    private String clientName;       // filled by the DAO, only used for display
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private final double taxPercent;
    private double paidAmount;
    private final List<InvoiceItem> items = new ArrayList<>();

    public Invoice(int freelancerId, int clientId, LocalDate issueDate, LocalDate dueDate, double taxPercent) {
        this.freelancerId = freelancerId;
        this.clientId = clientId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.taxPercent = taxPercent;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }

    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public int getFreelancerId() { return freelancerId; }

    public int getClientId() { return clientId; }

    public String getFreelancerName() { return freelancerName; }

    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }

    public String getClientName() { return clientName; }

    public void setClientName(String clientName) { this.clientName = clientName; }

    public LocalDate getIssueDate() { return issueDate; }

    public LocalDate getDueDate() { return dueDate; }

    public double getTaxPercent() { return taxPercent; }

    public double getPaidAmount() { return paidAmount; }

    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public List<InvoiceItem> getItems() { return items; }

    public void addItem(InvoiceItem item) { items.add(item); }

    public double getSubtotal() {
        return round(items.stream().mapToDouble(InvoiceItem::getAmount).sum());
    }

    public double getTaxAmount() {
        return round(getSubtotal() * taxPercent / 100);
    }

    public double getTotal() {
        return round(getSubtotal() + getTaxAmount());
    }

    public double getBalance() {
        return round(getTotal() - paidAmount);
    }

    /** The status is worked out from the payments and the due date, so it never goes out of date. */
    public InvoiceStatus getStatus() {
        if (getBalance() <= 0) {
            return InvoiceStatus.PAID;
        }
        if (LocalDate.now().isAfter(dueDate)) {
            return InvoiceStatus.OVERDUE;
        }
        if (paidAmount > 0) {
            return InvoiceStatus.PARTIALLY_PAID;
        }
        return InvoiceStatus.PENDING;
    }

    /** Used by combo boxes to show an invoice. */
    @Override
    public String toString() {
        return invoiceNumber + " | " + clientName + " | Balance " + String.format("%.2f", getBalance());
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
