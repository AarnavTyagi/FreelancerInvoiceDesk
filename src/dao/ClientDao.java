package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Client;

public class ClientDao implements CrudDao<Client> {

    @Override
    public void add(Client c) throws SQLException {
        String sql = "INSERT INTO clients (name, email, company) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getCompany());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Client> getAll() throws SQLException {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT client_id, name, email, company FROM clients ORDER BY name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Client(rs.getInt("client_id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("company")));
            }
        }
        return list;
    }

    @Override
    public void update(Client c) throws SQLException {
        String sql = "UPDATE clients SET name = ?, email = ?, company = ? WHERE client_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getCompany());
            ps.setInt(4, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM clients WHERE client_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
