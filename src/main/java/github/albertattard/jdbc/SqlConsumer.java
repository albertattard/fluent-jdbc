package github.albertattard.jdbc;

import java.sql.SQLException;

@FunctionalInterface
public interface SqlConsumer<T> {

    void accept(final T t) throws SQLException;

    static <E> SqlConsumer<E> blank() {
        return t -> {
        };
    }
}
