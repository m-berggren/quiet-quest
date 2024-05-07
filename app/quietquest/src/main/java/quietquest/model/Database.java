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
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;
import java.sql.PreparedStatement;
import java.sql.*;

public class Database {
    private Connection connection;

    public Database() throws SQLException {
        connect();

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

    public void connect() throws SQLException {
        String url = "jdbc:postgresql://localhost/quietquest";
        Properties props = new Properties();
        props.setProperty("user", "quietquest");
        props.setProperty("password", "quietquest");
        props.setProperty("ssl", "false");
        connection = DriverManager.getConnection(url, props);
    }

    public ArrayList<Quest> getAllQuests(User user) throws SQLException {
        ArrayList<Quest> quests = new ArrayList<>();
        int userId;

        String userSql = "SELECT * FROM \"user\" where \"username\" = ?";
        try (PreparedStatement userStmt = connection.prepareStatement(userSql)) {
            userStmt.setString(1, user.getUsername());
            try (ResultSet rs = userStmt.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                } else {
                    throw new RuntimeException("User not found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to retrieve user ID.", e);
            }

            String questSql = "SELECT * FROM quest WHERE user_id = ?";

            try (PreparedStatement questStmt = connection.prepareStatement(questSql)) {
                questStmt.setInt(1, userId);
                ResultSet rs = questStmt.executeQuery();
                while (rs.next()) {
                    String title = rs.getString("title");
                    String description = rs.getString("detail");
                    boolean completionState = rs.getBoolean("completion_state");
                    Timestamp startTime = rs.getTimestamp("start_time");
                    Timestamp completeTime = rs.getTimestamp("complete_time");
                    int boxOpenTimes = rs.getInt("box_open_times");

                    ArrayList<Activity> activities = getActivitiesForQuest(rs.getInt("id"));
                    Quest quest = new Quest(title, description, activities, completionState, startTime, completeTime, boxOpenTimes);
                    quests.add(quest);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return quests;
        }
    }

    private ArrayList<Activity> getActivitiesForQuest(int questId) throws SQLException {
        ArrayList<Activity> activities = new ArrayList<>();

        // Sample retrieval of tasks
        String sql = "SELECT * FROM \"task\" WHERE quest_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, questId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String description = rs.getString("description");
                    Timestamp startTime = rs.getTimestamp("start_time");
                    Timestamp endTime = rs.getTimestamp("end_time");
                    boolean completionState = rs.getBoolean("completion_state");

                    Task task = new Task(description, startTime, endTime, completionState);
                    activities.add(task);
                }
            }
        }
        if (activities.isEmpty()) { // Means it is not an ArrayList of Task but of PomodoroTimer
            sql = "SELECT * FROM \"pomodoro_quest\" WHERE quest_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, questId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int focusTime = rs.getInt("focus_time");
                        int breakTime = rs.getInt("break_time");
                        int intervals = rs.getInt("interval");

                        PomodoroTimer pomodoro = new PomodoroTimer(focusTime, breakTime, intervals);
                        activities.add(pomodoro);
                    }
                }
            }
        }
        return activities;
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM \"user\" WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String password = rs.getString("password");
                boolean appSound = rs.getBoolean("app_sound");
                boolean sensorSound = rs.getBoolean("sensor_sound");
                boolean deskMode = rs.getBoolean("desk_mode");

                return new User(id, username, password, appSound, sensorSound, deskMode);
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

    public void createQuest(User user, Quest quest) {
        String sql = "INSERT INTO \"quest\" (user_id, completion_state, title, detail, start_time, complete_time, box_open_times) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setBoolean(2, false);
            pstmt.setString(3, quest.getTitle());
            pstmt.setString(4, quest.getDescription());
            pstmt.setTimestamp(5, null); // Quest not yet started
            pstmt.setTimestamp(6, null); // Quest not yet started
            pstmt.setInt(7, 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param user
     * @param quest
     * @return
     */
    public void updateQuest(User user, Quest quest) {
        // Update Quest object
        int user_id = user.getId();
        String title = quest.getTitle();
        String sql = "UPDATE \"quest\" SET completion_state = ?, complete_time = ?, start_time = ?, box_open_times = ? WHERE user_id = ? AND title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, quest.getCompletionState());
            pstmt.setTimestamp(2, quest.getCompleteTime());
            pstmt.setTimestamp(3, quest.getStartTime());
            pstmt.setInt(4, quest.getBoxOpenTimes());
            pstmt.setInt(5, user.getId());
            pstmt.setString(6, quest.getTitle());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Delete old activities for simplicity
        deleteActivities(user, quest);

        // Re-create activities
        for (Activity activity : quest.getActivities()) {
            if (activity instanceof PomodoroTimer pomodoro) {
                createPomodoroTimer(user, quest, pomodoro);
            } else if (activity instanceof Task task) {
                createTask(user, quest, task);
            }
        }
    }

    public void deleteActivities(User user, Quest quest) {

    }

    public int getQuestIdByTitle(User user, Quest quest) {
        String sql = "SELECT id FROM \"quest\" WHERE user_id = ? AND title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, quest.getTitle());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                return id;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void createTask(User user, Quest quest, Task task) {
        String sql = "INSERT INTO \"task\" (quest_id, description, start_time, end_time, completion_state) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, getQuestIdByTitle(user, quest));
            pstmt.setString(2, task.getDescription());
            pstmt.setTimestamp(3, task.getStartTime());
            pstmt.setTimestamp(4, task.getEndTime()); // What should it be?
            pstmt.setBoolean(5, task.getCompletionState());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createPomodoroTimer(User user, Quest quest, PomodoroTimer pomodoro) {
        String sql = "INSERT INTO \"pomodoro_quest\" (quest_id, focus_time, break_time, interval) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, getQuestIdByTitle(user, quest));
            pstmt.setInt(2, pomodoro.getFocusTime());
            pstmt.setInt(3, pomodoro.getBreakTime());
            pstmt.setInt(4, pomodoro.getIntervals());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateTask(User user, Quest quest, Task task) {
        String sql = "UPDATE \"task\" SET start_time = ?, end_time = ?, completion_state = ? WHERE description = ? AND quest_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int questId = getQuestIdByTitle(user, quest);

            pstmt.setTimestamp(1, task.getStartTime());
            pstmt.setTimestamp(2, task.getEndTime());
            pstmt.setBoolean(3, task.getCompletionState());
            pstmt.setString(4, task.getDescription());
            pstmt.setInt(5, questId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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
