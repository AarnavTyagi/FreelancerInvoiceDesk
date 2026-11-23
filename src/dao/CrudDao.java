package dao;

import java.sql.SQLException;

/** For master data (freelancers, clients, services) that can also be edited and deleted. */
public interface CrudDao<T> extends Dao<T> {

    void update(T item) throws SQLException;

    void delete(int id) throws SQLException;
}
