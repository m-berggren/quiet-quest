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
import java.sql.PreparedStatement;
import java.sql.*;

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
  public void disconnect() throws SQLException {
    connection.close();
  }

  /**
   * Check if the provided username exists in the database
   * @param username
   * @return
   * @throws SQLException
   */

  public boolean checkIfUsernameExists(String username) throws SQLException {
    String sql = "SELECT * FROM \"user\" WHERE username = ?";
    boolean usernameExists = false;

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, username);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        usernameExists = true;
      }
    }
    return usernameExists;
  }

  /**
   * Check if the provided password matches the provided username
   * @param username
   * @param password
   * @return
   * @throws SQLException
   */
  public boolean checkIfPasswordCorrect(String username, String password) throws SQLException {
    String sql = "SELECT * FROM \"user\" WHERE username = ? AND password = ?";
    boolean correctPassword = false;

    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
      pstmt.setString(1, username);
      pstmt.setString(2, password);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        correctPassword = true;
        }
      }
    return correctPassword;
  }
}
