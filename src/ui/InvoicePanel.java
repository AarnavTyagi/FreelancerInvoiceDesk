package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.BillableServiceDao;
import dao.ClientDao;
import dao.FreelancerDao;
import dao.InvoiceDao;
import exception.InvalidInvoiceException;
import model.BillableService;
import model.Client;
import model.Freelancer;
import model.Invoice;
import model.InvoiceItem;
import service.InvoiceService;

/** Create invoices (top) and see all invoices with their status (bottom). */
public class InvoicePanel extends BasePanel {

    private final FreelancerDao freelancerDao = new FreelancerDao();
    private final ClientDao clientDao = new ClientDao();
    private final BillableServiceDao serviceDao = new BillableServiceDao();
    private final InvoiceDao invoiceDao = new InvoiceDao();
    private final InvoiceService invoiceService = new InvoiceService();

    private final JComboBox<Freelancer> freelancerBox = new JComboBox<>();
    private final JComboBox<Client> clientBox = new JComboBox<>();
    private final JComboBox<BillableService> serviceBox = new JComboBox<>();
    private final JTextField issueField = new JTextField(LocalDate.now().toString());
    private final JTextField dueField = new JTextField(LocalDate.now().plusDays(14).toString());
    private final JTextField taxField = new JTextField("18");
    private final JTextField quantityField = new JTextField("1");

    // The items of the invoice being built (not saved until Create Invoice is pressed)
    private final List<InvoiceItem> draftItems = new ArrayList<>();
    private final DefaultTableModel draftModel =
            readOnlyModel("Service", "Quantity", "Unit Rate", "Amount");

    // The saved invoices currently shown in the table
    private List<Invoice> invoices = new ArrayList<>();

    public InvoicePanel() {
        super("Invoice No", "Freelancer", "Client", "Issue Date", "Due Date",
                "Total", "Paid", "Balance", "Status");

        JPanel form = new JPanel(new GridLayout(0, 4, 8, 8));
        addField(form, "Freelancer", freelancerBox);
        addField(form, "Client", clientBox);
        addField(form, "Issue date (yyyy-MM-dd)", issueField);
        addField(form, "Due date (yyyy-MM-dd)", dueField);
        addField(form, "Tax %", taxField);
        addField(form, "Service", serviceBox);
        addField(form, "Quantity", quantityField);

        JScrollPane draftScroll = new JScrollPane(new JTable(draftModel));
        draftScroll.setPreferredSize(new Dimension(0, 100));

        JButton addItemButton = new JButton("Add Item");
        JButton clearItemsButton = new JButton("Clear Items");
        JButton createButton = new JButton("Create Invoice");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addItemButton);
        buttons.add(clearItemsButton);
        buttons.add(createButton);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(form, BorderLayout.NORTH);
        top.add(draftScroll, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        JButton detailsButton = new JButton("View Selected Invoice");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(detailsButton);
        add(bottom, BorderLayout.SOUTH);

        // Services belong to a freelancer, so changing the freelancer reloads them
        freelancerBox.addActionListener(e -> {
            loadServices();
            clearDraft();
        });
        addItemButton.addActionListener(e -> addItem());
        clearItemsButton.addActionListener(e -> clearDraft());
        createButton.addActionListener(e -> createInvoice());
        detailsButton.addActionListener(e -> showDetails());
    }

    @Override
    public void refresh() {
        try {
            freelancerBox.removeAllItems();
            for (Freelancer f : freelancerDao.getAll()) {
                freelancerBox.addItem(f);
            }
            clientBox.removeAllItems();
            for (Client c : clientDao.getAll()) {
                clientBox.addItem(c);
            }
        } catch (SQLException ex) {
            showDbError(ex);
        }
        loadServices();
        loadInvoices();
    }

    private void loadServices() {
        serviceBox.removeAllItems();
        Freelancer freelancer = (Freelancer) freelancerBox.getSelectedItem();
        if (freelancer == null) {
            return;
        }
        try {
            for (BillableService s : serviceDao.getByFreelancer(freelancer.getId())) {
                serviceBox.addItem(s);
            }
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void loadInvoices() {
        try {
            invoices = invoiceDao.getAll();
            model.setRowCount(0);
            for (Invoice inv : invoices) {
                model.addRow(new Object[] { inv.getInvoiceNumber(), inv.getFreelancerName(), inv.getClientName(),
                        inv.getIssueDate(), inv.getDueDate(), money(inv.getTotal()), money(inv.getPaidAmount()),
                        money(inv.getBalance()), inv.getStatus() });
            }
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void addItem() {
        BillableService service = (BillableService) serviceBox.getSelectedItem();
        if (service == null) {
            showError("This freelancer has no services yet. Add one in the Services tab.");
            return;
        }
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0) {
                showError("The quantity must be at least 1.");
                return;
            }
            InvoiceItem item = new InvoiceItem(service.getDescription(), quantity, service.getUnitRate());
            draftItems.add(item);
            draftModel.addRow(new Object[] { item.getDescription(), item.getQuantity(),
                    money(item.getUnitRate()), money(item.getAmount()) });
        } catch (NumberFormatException ex) {
            showError("The quantity must be a whole number.");
        }
    }

    private void clearDraft() {
        draftItems.clear();
        draftModel.setRowCount(0);
    }

    private void createInvoice() {
        Freelancer freelancer = (Freelancer) freelancerBox.getSelectedItem();
        Client client = (Client) clientBox.getSelectedItem();
        if (freelancer == null || client == null) {
            showError("Add a freelancer and a client first.");
            return;
        }
        try {
            Invoice invoice = new Invoice(freelancer.getId(), client.getId(),
                    LocalDate.parse(issueField.getText().trim()),
                    LocalDate.parse(dueField.getText().trim()),
                    Double.parseDouble(taxField.getText().trim()));
            draftItems.forEach(invoice::addItem);

            invoiceService.createInvoice(invoice);

            showInfo("Invoice " + invoice.getInvoiceNumber() + " created. Total: " + money(invoice.getTotal()));
            clearDraft();
            loadInvoices();
        } catch (NumberFormatException | DateTimeParseException ex) {
            showError("Enter the tax as a number and the dates as yyyy-MM-dd.");
        } catch (InvalidInvoiceException ex) {
            showError(ex.getMessage());
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void showDetails() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= invoices.size()) {
            showError("Select an invoice in the table first.");
            return;
        }
        Invoice inv = invoices.get(row);

        StringBuilder text = new StringBuilder();
        text.append("Invoice: ").append(inv.getInvoiceNumber()).append('\n');
        text.append("From:    ").append(inv.getFreelancerName()).append('\n');
        text.append("To:      ").append(inv.getClientName()).append('\n');
        text.append("Issued:  ").append(inv.getIssueDate()).append("    Due: ").append(inv.getDueDate());
        text.append("\n\n");
        for (InvoiceItem item : inv.getItems()) {
            text.append(String.format("%-28s %3d x %9.2f = %10.2f%n",
                    item.getDescription(), item.getQuantity(), item.getUnitRate(), item.getAmount()));
        }
        text.append(String.format("%nSubtotal:        %10.2f%n", inv.getSubtotal()));
        text.append(String.format("Tax (%.1f%%):      %10.2f%n", inv.getTaxPercent(), inv.getTaxAmount()));
        text.append(String.format("Total:           %10.2f%n", inv.getTotal()));
        text.append(String.format("Paid:            %10.2f%n", inv.getPaidAmount()));
        text.append(String.format("Balance:         %10.2f%n", inv.getBalance()));
        text.append("Status:          ").append(inv.getStatus());

        JTextArea area = new JTextArea(text.toString());
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, area, "Invoice details", JOptionPane.PLAIN_MESSAGE);
    }
}
