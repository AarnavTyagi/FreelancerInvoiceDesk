package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Freelancer;

public class FreelancerDao implements CrudDao<Freelancer> {

    @Override
    public void add(Freelancer f) throws SQLException {
        String sql = "INSERT INTO freelancers (name, email, skill) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getName());
            ps.setString(2, f.getEmail());
            ps.setString(3, f.getSkill());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Freelancer> getAll() throws SQLException {
        List<Freelancer> list = new ArrayList<>();
        String sql = "SELECT freelancer_id, name, email, skill FROM freelancers ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Freelancer(rs.getInt("freelancer_id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("skill")));
            }
        }
        return list;
    }

    @Override
    public void update(Freelancer f) throws SQLException {
        String sql = "UPDATE freelancers SET name = ?, email = ?, skill = ? WHERE freelancer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getName());
            ps.setString(2, f.getEmail());
            ps.setString(3, f.getSkill());
            ps.setInt(4, f.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM freelancers WHERE freelancer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
