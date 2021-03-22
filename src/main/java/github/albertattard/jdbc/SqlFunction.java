package github.albertattard.jdbc;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlFunction<T, R> {
    R apply(final T t) throws SQLException;
}
