package ui;

import javax.swing.JTextField;

import dao.FreelancerDao;
import model.Freelancer;

public class FreelancerPanel extends MasterPanel<Freelancer> {

    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField skillField = new JTextField();

    public FreelancerPanel() {
        super(new FreelancerDao(), "ID", "Name", "Email", "Skill");
        addField(form, "Name", nameField);
        addField(form, "Email", emailField);
        addField(form, "Skill", skillField);
    }

    @Override
    protected Freelancer readForm(int id) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String skill = skillField.getText().trim();
        if (name.isEmpty() || skill.isEmpty() || !email.contains("@")) {
            showError("Enter a name, a valid email and a skill.");
            return null;
        }
        return new Freelancer(id, name, email, skill);
    }

    @Override
    protected void fillForm(Freelancer f) {
        nameField.setText(f.getName());
        emailField.setText(f.getEmail());
        skillField.setText(f.getSkill());
    }

    @Override
    protected void clearForm() {
        nameField.setText("");
        emailField.setText("");
        skillField.setText("");
    }

    @Override
    protected Object[] toRow(Freelancer f) {
        return new Object[] { f.getId(), f.getName(), f.getEmail(), f.getSkill() };
    }
}
