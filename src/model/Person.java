package model;

/** Common data of a freelancer and a client. */
public abstract class Person implements Identifiable {

    private final int id;
    private final String name;
    private final String email;

    protected Person(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public int getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    /** The skill of a freelancer or the company of a client. */
    public abstract String getDetail();

    /** Used by combo boxes to show a person. */
    @Override
    public String toString() {
        return name + " (" + getDetail() + ")";
    }
}
