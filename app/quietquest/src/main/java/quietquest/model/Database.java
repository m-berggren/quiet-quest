package quietquest.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("username");
                String password = rs.getString("password");
                boolean appSound = rs.getBoolean("app_sound");
                boolean sensorSound = rs.getBoolean("sensor_sound");
                boolean deskMode = rs.getBoolean("desk_mode");

                return new User(id, username, appSound, sensorSound, deskMode);
            }
        }
        return null;
    }

    public void disconnect() throws SQLException {
        connection.close();
    }

    /**
     * Check if the provided username exists in the database
     *
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
     *
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
            pstmt.setString(2, hashPassword(password));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                correctPassword = true;
            }
        }
        return correctPassword;
    }

    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO \"user\" (username, password, app_sound, sensor_sound, desk_mode) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setBoolean(3, true);
            pstmt.setBoolean(4, true);
            pstmt.setBoolean(5, false);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;

    }

    /**
     * Method to hash the password.
     */
    public static String hashPassword(String password) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // Hash the password as bytes
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            byte[] hashedBytes = messageDigest.digest(passwordBytes);

            // Convert the hashed bytes to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }

            // Return the hashed password as a hexadecimal string
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception if the hashing algorithm is not supported
            throw new RuntimeException("SHA-256 is not supported", e);
        }
    }

}
