package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import model.Invoice;
import model.InvoiceStatus;
import service.ReportService;
import util.CsvExporter;

/** Shows one report at a time in the table and can save it as a CSV file. */
public class ReportPanel extends BasePanel {

    private final ReportService reportService = new ReportService();

    // The report currently on screen, kept so it can be exported
    private String[] headers = new String[0];
    private List<String[]> rows = new ArrayList<>();

    public ReportPanel() {
        super();

        JButton outstandingButton = new JButton("Client Outstanding");
        JButton earningsButton = new JButton("Monthly Earnings");
        JButton collectionButton = new JButton("Payment Collection");
        JButton statusButton = new JButton("Status Summary");
        JButton exportButton = new JButton("Export to CSV");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(outstandingButton);
        buttons.add(earningsButton);
        buttons.add(collectionButton);
        buttons.add(statusButton);
        buttons.add(exportButton);
        add(buttons, BorderLayout.NORTH);

        outstandingButton.addActionListener(e -> showOutstanding());
        earningsButton.addActionListener(e -> showEarnings());
        collectionButton.addActionListener(e -> showCollection());
        statusButton.addActionListener(e -> showStatusSummary());
        exportButton.addActionListener(e -> exportCsv());
    }

    private void showOutstanding() {
        try {
            List<String[]> data = new ArrayList<>();
            reportService.outstandingByClient()
                    .forEach((client, amount) -> data.add(new String[] { client, money(amount) }));
            show(new String[] { "Client", "Outstanding Balance" }, data);
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void showEarnings() {
        try {
            List<String[]> data = new ArrayList<>();
            reportService.monthlyEarnings()
                    .forEach((month, amount) -> data.add(new String[] { month.toString(), money(amount) }));
            show(new String[] { "Month", "Payments Received" }, data);
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void showCollection() {
        try {
            List<Invoice> invoices = reportService.collectionReport();
            List<String[]> data = new ArrayList<>();
            for (Invoice inv : invoices) {
                data.add(new String[] { inv.getInvoiceNumber(), inv.getClientName(), inv.getStatus().toString(),
                        money(inv.getTotal()), money(inv.getPaidAmount()), money(inv.getBalance()) });
            }
            double billed = invoices.stream().mapToDouble(Invoice::getTotal).sum();
            double collected = invoices.stream().mapToDouble(Invoice::getPaidAmount).sum();
            data.add(new String[] { "TOTAL", "", "", money(billed), money(collected), money(billed - collected) });
            show(new String[] { "Invoice No", "Client", "Status", "Total", "Collected", "Balance" }, data);
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void showStatusSummary() {
        try {
            Map<InvoiceStatus, Long> counts = reportService.statusSummary();
            List<String[]> data = new ArrayList<>();
            for (InvoiceStatus status : InvoiceStatus.values()) {
                data.add(new String[] { status.toString(), String.valueOf(counts.getOrDefault(status, 0L)) });
            }
            show(new String[] { "Status", "Number of Invoices" }, data);
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void show(String[] newHeaders, List<String[]> newRows) {
        headers = newHeaders;
        rows = newRows;
        model.setColumnIdentifiers(headers);
        model.setRowCount(0);
        for (String[] row : rows) {
            model.addRow(row);
        }
    }

    private void exportCsv() {
        if (rows.isEmpty()) {
            showError("Open a report first, then export it.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("report.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            CsvExporter.export(chooser.getSelectedFile(), headers, rows);
            showInfo("Report saved as " + chooser.getSelectedFile().getName());
        } catch (IOException ex) {
            showError("Could not write the file: " + ex.getMessage());
        }
    }
}
