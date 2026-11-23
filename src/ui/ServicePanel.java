package ui;

import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import dao.BillableServiceDao;
import dao.FreelancerDao;
import model.BillableService;
import model.Freelancer;

public class ServicePanel extends MasterPanel<BillableService> {

    private final FreelancerDao freelancerDao = new FreelancerDao();
    private final JComboBox<Freelancer> freelancerBox = new JComboBox<>();
    private final JTextField descriptionField = new JTextField();
    private final JTextField rateField = new JTextField();

    public ServicePanel() {
        super(new BillableServiceDao(), "ID", "Freelancer", "Service Description", "Unit Rate");
        addField(form, "Freelancer", freelancerBox);
        addField(form, "Service description", descriptionField);
        addField(form, "Unit rate", rateField);
    }

    @Override
    public void refresh() {
        try {
            freelancerBox.removeAllItems();
            for (Freelancer f : freelancerDao.getAll()) {
                freelancerBox.addItem(f);
            }
        } catch (SQLException ex) {
            showDbError(ex);
        }
        super.refresh();
    }

    @Override
    protected BillableService readForm(int id) {
        Freelancer freelancer = (Freelancer) freelancerBox.getSelectedItem();
        String description = descriptionField.getText().trim();
        try {
            double rate = Double.parseDouble(rateField.getText().trim());
            if (freelancer == null || description.isEmpty() || rate <= 0) {
                showError("Choose a freelancer, enter a description and a rate above zero.");
                return null;
            }
            return new BillableService(id, freelancer.getId(), description, rate);
        } catch (NumberFormatException ex) {
            showError("The unit rate must be a number.");
            return null;
        }
    }

    @Override
    protected void fillForm(BillableService s) {
        for (int i = 0; i < freelancerBox.getItemCount(); i++) {
            if (freelancerBox.getItemAt(i).getId() == s.getFreelancerId()) {
                freelancerBox.setSelectedIndex(i);
            }
        }
        descriptionField.setText(s.getDescription());
        rateField.setText(String.valueOf(s.getUnitRate()));
    }

    @Override
    protected void clearForm() {
        descriptionField.setText("");
        rateField.setText("");
    }

    @Override
    protected Object[] toRow(BillableService s) {
        return new Object[] { s.getId(), s.getFreelancerName(), s.getDescription(), money(s.getUnitRate()) };
    }
}
