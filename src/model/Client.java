package model;

public class Client extends Person {

    private final String company;

    public Client(int id, String name, String email, String company) {
        super(id, name, email);
        this.company = company;
    }

    public String getCompany() { return company; }

    @Override
    public String getDetail() { return company; }
}
