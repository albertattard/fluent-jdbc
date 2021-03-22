package github.albertattard.jdbc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@DisplayName("Fluent connection")
class FluentConnectionTest {

    @Test
    @DisplayName("should close the connection even when exceptions are thrown")
    void shouldCloseTheConnectionEvenWhenExceptionsAreThrown() throws SQLException {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);

        when(dataSource.getConnection()).thenReturn(connection);

        final SQLException simulatedError = new SQLException("Simulating an error");
        final RuntimeException exception = assertThrows(RuntimeException.class, () ->
                FluentConnection.of(dataSource).withConnection(c -> {
                    throw simulatedError;
                }));

        assertThat(exception).isNotNull();
        assertThat(exception.getCause()).isEqualTo(simulatedError);

        verify(dataSource, times(1)).getConnection();
        verify(connection, times(1)).close();
        verifyNoMoreInteractions(dataSource, connection);
    }
}