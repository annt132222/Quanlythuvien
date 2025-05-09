package uet.group1.librarymanagement.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, K> {
    Optional<T> findById(K id);
    List<T> findAll();
    boolean insert(T entity);
    boolean update(T entity);
    boolean delete(K id);
}
