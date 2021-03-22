package github.albertattard.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class FluentResultSet {

    private final ResultSet resultSet;

    public <T> T single(final SqlFunction<ResultSet, T> function) {
        return optional(function).orElseThrow(() -> new RuntimeException("Expected one row, but found none"));
    }

    public <T> Optional<T> optional(final SqlFunction<ResultSet, T> function) {
        try {
            if (resultSet.next()) {
                final T value = function.apply(resultSet);

                if (resultSet.next()) {
                    throw new RuntimeException("Expected one row, but found more");
                }

                return Optional.of(value);
            }

            return Optional.empty();
        } catch (final SQLException e) {
            throw new RuntimeException("Failed to fetch single result from result set", e);
        }
    }

    public static FluentResultSet of(final ResultSet resultSet) throws NullPointerException {
        return new FluentResultSet(Objects.requireNonNull(resultSet));
    }

    public FluentResultSet(final ResultSet resultSet) {
        this.resultSet = resultSet;
    }
}
