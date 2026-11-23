package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.BillableService;

public class BillableServiceDao implements CrudDao<BillableService> {

    private static final String SELECT =
            "SELECT s.service_id, s.freelancer_id, f.name AS freelancer_name, s.description, s.unit_rate "
            + "FROM services s JOIN freelancers f ON f.freelancer_id = s.freelancer_id ";

    @Override
    public void add(BillableService s) throws SQLException {
        String sql = "INSERT INTO services (freelancer_id, description, unit_rate) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getFreelancerId());
            ps.setString(2, s.getDescription());
            ps.setDouble(3, s.getUnitRate());
            ps.executeUpdate();
        }
    }

    @Override
    public List<BillableService> getAll() throws SQLException {
        List<BillableService> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT + "ORDER BY f.name, s.description")) {
            while (rs.next()) {
                list.add(readService(rs));
            }
        }
        return list;
    }

    /** Only the services of one freelancer, used when building an invoice. */
    public List<BillableService> getByFreelancer(int freelancerId) throws SQLException {
        List<BillableService> list = new ArrayList<>();
        String sql = SELECT + "WHERE s.freelancer_id = ? ORDER BY s.description";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, freelancerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(readService(rs));
                }
            }
        }
        return list;
    }

    @Override
    public void update(BillableService s) throws SQLException {
        String sql = "UPDATE services SET freelancer_id = ?, description = ?, unit_rate = ? WHERE service_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, s.getFreelancerId());
            ps.setString(2, s.getDescription());
            ps.setDouble(3, s.getUnitRate());
            ps.setInt(4, s.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM services WHERE service_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Turns the current row of the ResultSet into a BillableService object
    private BillableService readService(ResultSet rs) throws SQLException {
        BillableService s = new BillableService(rs.getInt("service_id"), rs.getInt("freelancer_id"),
                rs.getString("description"), rs.getDouble("unit_rate"));
        s.setFreelancerName(rs.getString("freelancer_name"));
        return s;
    }
}
