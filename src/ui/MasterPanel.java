package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import dao.CrudDao;
import model.Identifiable;

/**
 * Template for the three master data screens (freelancers, clients, services).
 * It owns the table, the buttons and the Add / Update / Delete logic once,
 * so each child class only describes its own form fields.
 */
public abstract class MasterPanel<T extends Identifiable> extends BasePanel {

    private final CrudDao<T> dao;
    private List<T> items = new ArrayList<>();
    protected final JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));

    protected MasterPanel(CrudDao<T> dao, String... columns) {
        super(columns);
        this.dao = dao;

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton clearButton = new JButton("Clear Form");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        addButton.addActionListener(e -> addRecord());
        updateButton.addActionListener(e -> updateRecord());
        deleteButton.addActionListener(e -> deleteRecord());
        clearButton.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> {
            T selected = getSelected();
            if (selected != null) {
                fillForm(selected);
            }
        });
    }

    // The four methods every child screen must provide

    /** Builds an object from the form fields, or shows an error and returns null. */
    protected abstract T readForm(int id);

    /** Shows an object in the form fields. */
    protected abstract void fillForm(T item);

    protected abstract void clearForm();

    /** One table row for an object. */
    protected abstract Object[] toRow(T item);

    @Override
    public void refresh() {
        try {
            items = dao.getAll();
            model.setRowCount(0);
            for (T item : items) {
                model.addRow(toRow(item));
            }
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private T getSelected() {
        int row = table.getSelectedRow();
        return (row < 0 || row >= items.size()) ? null : items.get(row);
    }

    private void addRecord() {
        T item = readForm(0);
        if (item == null) {
            return;
        }
        try {
            dao.add(item);
            clearForm();
            refresh();
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void updateRecord() {
        T selected = getSelected();
        if (selected == null) {
            showError("Select a row in the table first.");
            return;
        }
        T item = readForm(selected.getId());
        if (item == null) {
            return;
        }
        try {
            dao.update(item);
            clearForm();
            refresh();
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void deleteRecord() {
        T selected = getSelected();
        if (selected == null) {
            showError("Select a row in the table first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this, "Delete the selected record?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.delete(selected.getId());
            clearForm();
            refresh();
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }
}
