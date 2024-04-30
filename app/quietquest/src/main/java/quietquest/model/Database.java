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

    // method calls below
    insertData();
  }
    public void disconnect() throws SQLException {
      connection.close();
    }

  public void insertData() throws SQLException {
    String userData = "INSERT INTO \"user\" (username, password, created_at, app_sound, sensor_sound, desk_mode)"
            + " VALUES ('julia', 'taylorswift', current_date, true, true, true)";
  }

  /**
   * Check if the provided username exists in the database
   * @param username
   * @return
   * @throws SQLException
   */
  public boolean checkIfUsernameExists(String username) throws SQLException {
    String sql = "SELECT * FROM \"user\" WHERE username = ?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean usernameExists = false;

    pstmt = connection.prepareStatement(sql);
    pstmt.setString(1, username);
    rs = pstmt.executeQuery();

    if (rs.next()) {
      int count = rs.getInt(1);
      usernameExists = count > 0;
    }
    rs.close();
    pstmt.close();

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
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean correctPassword = false;

    pstmt = connection.prepareStatement(sql);
    pstmt.setString(1, username);
    pstmt.setString(2, password);
    rs = pstmt.executeQuery();

    if (rs.next()) {
      int count = rs.getInt(1);
      correctPassword = count > 0;
    }
      rs.close();
      pstmt.close();

    return correctPassword;
  }

}
