package model;

import java.time.LocalDate;

public class Payment {

    private int id;
    private final int invoiceId;
    private String invoiceNumber;   // filled by the DAO, only used for display
    private final double amount;
    private final LocalDate paymentDate;

    public Payment(int invoiceId, double amount, LocalDate paymentDate) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getInvoiceId() { return invoiceId; }

    public String getInvoiceNumber() { return invoiceNumber; }

    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public double getAmount() { return amount; }

    public LocalDate getPaymentDate() { return paymentDate; }
}
