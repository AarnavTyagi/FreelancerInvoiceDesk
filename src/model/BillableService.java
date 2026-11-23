package model;

/** A service a freelancer can bill for, with its standard rate. */
public class BillableService implements Identifiable {

    private final int id;
    private final int freelancerId;
    private final String description;
    private final double unitRate;
    private String freelancerName;   // filled by the DAO, only used for display

    public BillableService(int id, int freelancerId, String description, double unitRate) {
        this.id = id;
        this.freelancerId = freelancerId;
        this.description = description;
        this.unitRate = unitRate;
    }

    @Override
    public int getId() { return id; }

    public int getFreelancerId() { return freelancerId; }

    public String getDescription() { return description; }

    public double getUnitRate() { return unitRate; }

    public String getFreelancerName() { return freelancerName; }

    public void setFreelancerName(String freelancerName) { this.freelancerName = freelancerName; }

    @Override
    public String toString() {
        return description + " - " + String.format("%.2f", unitRate);
    }
}
