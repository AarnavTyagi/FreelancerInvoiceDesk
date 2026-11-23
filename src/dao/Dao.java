package dao;

import java.sql.SQLException;
import java.util.List;

/** Basic database operations that every DAO offers. T is the type of object it stores. */
public interface Dao<T> {

    void add(T item) throws SQLException;

    List<T> getAll() throws SQLException;
}
