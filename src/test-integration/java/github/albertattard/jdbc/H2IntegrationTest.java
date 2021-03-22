package github.albertattard.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("H2 integration test")
public class H2IntegrationTest {

    public static DataSource dataSource() {
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:test_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        config.setUsername("sa");
        config.setPassword("");
        return new HikariDataSource(config);
    }

    public static String read(final String relativeFile) {
        final String resourcePath = String.format("/samples/%s.sql", relativeFile);
        try (final Reader reader = new BufferedReader(new InputStreamReader(H2IntegrationTest.class.getResourceAsStream(resourcePath), StandardCharsets.UTF_8))) {
            final StringBuilder builder = new StringBuilder();
            for (int c; (c = reader.read()) != -1; ) {
                builder.append((char) c);
            }
            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to read the sample file %s (%s)", relativeFile, resourcePath), e);
        }
    }

    @Test
    @DisplayName("should create a table, populate it and then query it")
    void shouldCreateATablePopulateItAndThenQueryIt() {
        final DataSource dataSource = dataSource();
        final FluentConnection connection = FluentConnection.of(dataSource);

        connection.statement(read("create_name_table")).executeUpdate();
        connection.preparedStatement("INSERT INTO name (id, name, surname, email_address) VALUES (?,?,?,?)")
                .withParameters(preparedStatement -> {
                    int index = 1;
                    preparedStatement.setLong(index++, 1);
                    preparedStatement.setString(index++, "Albert");
                    preparedStatement.setString(index++, "Attard");
                    preparedStatement.setString(index, "albertattard@gmail.com");
                })
                .executeUpdate();

        final long count = connection
                .statement("SELECT COUNT(*) FROM name")
                .singleLong();
        assertEquals(1, count);
    }
}
