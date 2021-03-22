package github.albertattard.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class FluentConnection {

    private final DataSource dataSource;

    public FluentStatement statement(final String query) {
        return FluentStatement.of(this, query);
    }

    public FluentPreparedStatement preparedStatement(final String query) {
        return FluentPreparedStatement.of(this, query);
    }

    public <T> T withConnection(final SqlFunction<Connection, T> function) {
        try (final Connection connection = dataSource.getConnection()) {
            return function.apply(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create and use connection", e);
        }
    }

    public static FluentConnection of(final DataSource dataSource) throws NullPointerException {
        return new FluentConnection(Objects.requireNonNull(dataSource));
    }

    private FluentConnection(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
