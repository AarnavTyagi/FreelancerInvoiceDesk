package ui;

import javax.swing.JTextField;

import dao.ClientDao;
import model.Client;

public class ClientPanel extends MasterPanel<Client> {

    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField companyField = new JTextField();

    public ClientPanel() {
        super(new ClientDao(), "ID", "Name", "Email", "Company");
        addField(form, "Name", nameField);
        addField(form, "Email", emailField);
        addField(form, "Company", companyField);
    }

    @Override
    protected Client readForm(int id) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String company = companyField.getText().trim();
        if (name.isEmpty() || company.isEmpty() || !email.contains("@")) {
            showError("Enter a name, a valid email and a company.");
            return null;
        }
        return new Client(id, name, email, company);
    }

    @Override
    protected void fillForm(Client c) {
        nameField.setText(c.getName());
        emailField.setText(c.getEmail());
        companyField.setText(c.getCompany());
    }

    @Override
    protected void clearForm() {
        nameField.setText("");
        emailField.setText("");
        companyField.setText("");
    }

    @Override
    protected Object[] toRow(Client c) {
        return new Object[] { c.getId(), c.getName(), c.getEmail(), c.getCompany() };
    }
}
