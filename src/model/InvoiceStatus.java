package model;

public enum InvoiceStatus {
    PENDING("Pending"),
    PARTIALLY_PAID("Partially Paid"),
    PAID("Paid"),
    OVERDUE("Overdue");

    private final String label;

    InvoiceStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
