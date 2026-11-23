package model;

public class Freelancer extends Person {

    private final String skill;

    public Freelancer(int id, String name, String email, String skill) {
        super(id, name, email);
        this.skill = skill;
    }

    public String getSkill() { return skill; }

    @Override
    public String getDetail() { return skill; }
}
