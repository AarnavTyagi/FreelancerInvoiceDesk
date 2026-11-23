package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Payment;

public class PaymentDao implements Dao<Payment> {

    @Override
    public void add(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (invoice_id, amount, payment_date) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, payment.getInvoiceId());
            ps.setDouble(2, payment.getAmount());
            ps.setDate(3, Date.valueOf(payment.getPaymentDate()));
            ps.executeUpdate();
        }
    }

    @Override
    public List<Payment> getAll() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.payment_id, p.invoice_id, i.invoice_number, p.amount, p.payment_date "
                + "FROM payments p JOIN invoices i ON i.invoice_id = p.invoice_id "
                + "ORDER BY p.payment_date DESC, p.payment_id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Payment p = new Payment(rs.getInt("invoice_id"), rs.getDouble("amount"),
                        rs.getDate("payment_date").toLocalDate());
                p.setId(rs.getInt("payment_id"));
                p.setInvoiceNumber(rs.getString("invoice_number"));
                list.add(p);
            }
        }
        return list;
    }
}
