package github.albertattard.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class FluentStatement {

    private final FluentConnection database;
    private final String query;

    public int executeUpdate() {
        return withStatement(statement -> statement.executeUpdate(query));
    }

    public <T> T single(final SqlFunction<ResultSet, T> function) {
        return withResultSet(resultSet -> FluentResultSet.of(resultSet).single(function));
    }

    public long singleLong() {
        return single(resultSet -> resultSet.getLong(1));
    }

    private <T> T withResultSet(final SqlFunction<ResultSet, T> function) {
        return withStatement(statement -> {
            try (final ResultSet resultSet = statement.executeQuery(query)) {
                return function.apply(resultSet);
            } catch (final SQLException e) {
                throw new RuntimeException("Failed to retrieve data from result set", e);
            }
        });
    }

    private <T> T withStatement(final SqlFunction<Statement, T> function) {
        return database.withConnection(connection -> {
            try (final Statement statement = connection.createStatement()) {
                return function.apply(statement);
            } catch (final SQLException e) {
                throw new RuntimeException("Failed to execute query", e);
            }
        });
    }

    public static FluentStatement of(final FluentConnection database, final String query) {
        return new FluentStatement(Objects.requireNonNull(database), Objects.requireNonNull(query));
    }

    private FluentStatement(final FluentConnection database, final String query) {
        this.database = database;
        this.query = query;
    }
}
