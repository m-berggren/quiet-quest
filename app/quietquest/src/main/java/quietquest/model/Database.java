package quietquest.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.stream.Collectors;

public class Database {
  private Connection connection;

  public Database() throws SQLException {
    String url = "jdbc:postgresql://localhost/quietquest";
    Properties props = new Properties();
    props.setProperty("user", "quietquest");
    props.setProperty("password", "quietquest");
    props.setProperty("ssl", "false");
    connection = DriverManager.getConnection(url, props);

    InputStream inputStream = getClass().getResourceAsStream("/quietquest.sql");
    if (inputStream == null) {
      throw new RuntimeException("Failed to load SQL file.");
    }
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      String sql = reader.lines().collect(Collectors.joining("\n"));
      try (Statement stmt = connection.createStatement()) {
        stmt.execute(sql);
      }
    } catch (Exception e) {
      throw new SQLException("Failed to initialize database connection or execute SQL file.", e);
    }
  }


  public void AddUserToDatabase {
    try
  }
}
