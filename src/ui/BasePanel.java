package ui;

import java.awt.BorderLayout;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** Common parts of every screen: a table in the centre plus small helper methods. */
public abstract class BasePanel extends JPanel {

    protected final DefaultTableModel model;
    protected final JTable table;

    protected BasePanel(String... columns) {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        model = readOnlyModel(columns);
        table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /** Called when the tab is opened, so the data on screen is always fresh. */
    public void refresh() { }

    protected static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    protected void addField(JPanel form, String label, JComponent field) {
        form.add(new JLabel(label));
        form.add(field);
    }

    protected static String money(double amount) {
        return String.format("%.2f", amount);
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showDbError(SQLException ex) {
        // SQL state starting with 23 means a duplicate value or a record still used elsewhere
        if (ex.getSQLState() != null && ex.getSQLState().startsWith("23")) {
            showError("This record already exists or is still linked to other records.");
        } else {
            showError("Database error: " + ex.getMessage());
        }
    }
}
