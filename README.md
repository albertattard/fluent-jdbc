# Fluent JDBC Library

A reflection less library for fluent JDBC calls.

Following is a basic example.

```java
final DataSource dataSource = ...;
final FluentConnection connection = FluentConnection.of(dataSource);

connection
  .preparedStatement("INSERT INTO name (id, name, surname, email_address) VALUES (?,?,?,?)")
  .withParameters(preparedStatement -> {
    int index=1;
    preparedStatement.setLong(index++,1);
    preparedStatement.setString(index++,"Albert");
    preparedStatement.setString(index++,"Attard");
    preparedStatement.setString(index,"albertattard@gmail.com");
  })
  .executeUpdate();

final long count = connection
        .statement("SELECT COUNT(*) FROM name")
        .singleLong();

assertEquals(1,count);
```
