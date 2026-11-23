package model;

/** One line of an invoice: a service, a quantity and the rate used. */
public class InvoiceItem {

    private final String description;
    private final int quantity;
    private final double unitRate;

    public InvoiceItem(String description, int quantity, double unitRate) {
        this.description = description;
        this.quantity = quantity;
        this.unitRate = unitRate;
    }

    public String getDescription() { return description; }

    public int getQuantity() { return quantity; }

    public double getUnitRate() { return unitRate; }

    public double getAmount() { return quantity * unitRate; }
}
