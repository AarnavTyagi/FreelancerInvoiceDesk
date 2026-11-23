package ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Freelancer Invoice and Payment Desk");

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Freelancers", new FreelancerPanel());
        tabs.addTab("Clients", new ClientPanel());
        tabs.addTab("Services", new ServicePanel());
        tabs.addTab("Invoices", new InvoicePanel());
        tabs.addTab("Payments", new PaymentPanel());
        tabs.addTab("Reports", new ReportPanel());

        // Reload the data every time a tab is opened
        tabs.addChangeListener(e -> ((BasePanel) tabs.getSelectedComponent()).refresh());
        ((BasePanel) tabs.getSelectedComponent()).refresh();

        add(tabs);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
