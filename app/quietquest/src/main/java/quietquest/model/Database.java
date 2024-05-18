package quietquest.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Properties;
import java.util.stream.Collectors;

public class Database {
	private Connection connection;

	public Database() throws SQLException {
		initialize();
	}

	// ==============================* CONNECTION MANAGEMENT *==============================

	/**
	 * Connects to the database using credentials stored as environment variables.
	 * Utilizes 'DB_URL' for JDBC URL, 'DB_USER' and 'DB_PASSWORD' for database credentials.
	 */
	private void connect() throws SQLException {
		String url = "jdbc:postgresql://localhost/quietquest";

		Properties props = new Properties();
		props.setProperty("user", "quietquest");
		props.setProperty("password", "quietquest");
		props.setProperty("ssl", "false");

		connection = DriverManager.getConnection(url, props);
	}

	/**
	 * Disconnects the database connection.
	 */
	public void closeConnection() {
		if (connection == null) {
			return;
		}

		try {
			connection.close();
		} catch (Exception e) {
			throw new RuntimeException("Failed to close database connection.", e);
		}
	}

	// ==============================* INITIALIZATION & SETUP *=============================

	/**
	 * Initializes database and attempts to create the needed SQL tables from a file, if they do not already exist.
	 * It ensures that connection is closed even if error occur while attempting to read file.
	 */
	public void initialize() throws SQLException {
		connect();
		readSqlFile("/quietquest.sql");
		readSqlFile("/demodata.sql");
	}

	/**
	 * Reads SQL statements from a file and executes them. The file, 'quietquest.sql', is located in the resources
	 * directory. All commands are combined into a single string and executed os one batch.
	 */
	private void readSqlFile(String filePath) throws SQLException {
		InputStream inputStream = getClass().getResourceAsStream(filePath);
		if (inputStream == null) {
			throw new RuntimeException("Failed to load SQL file.");
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			// Combines SQL executions into one large string
			// Possible improvement: execute each command one by one, if issues arise with current approach
			String sql = reader.lines().collect(Collectors.joining("\n"));
			try (Statement stmt = connection.createStatement()) {
				stmt.execute(sql);
			}
		} catch (Exception e) {
			throw new SQLException("Failed to initialize database connection or execute SQL file.", e);
		}
	}

	// ==============================* QUEST MANAGEMENT *===================================

	/**
	 * Retrieves all quests associated with a given user from the database.
	 *
	 * @param user the user whose quests are to be retrieved.
	 * @return an ArrayList of {@link Quest} objects representing the user's quests.
	 */
	public ArrayList<Quest> getAllQuests(User user) {
		ArrayList<Quest> quests = new ArrayList<>();

		String questSql = """
				SELECT *
				FROM "quest"
				WHERE user_id = ?
				""";

		try (PreparedStatement questStmt = connection.prepareStatement(questSql)) {
			questStmt.setInt(1, user.getId()); // Query with the unique identifier for User
			ResultSet rs = questStmt.executeQuery();
			while (rs.next()) {
				// Extract quest details from the ResultSet
				int id = rs.getInt("id");
				int user_id = rs.getInt("user_id");
				boolean completionState = rs.getBoolean("completion_state");
				String title = rs.getString("title");
				String detail = rs.getString("detail");
				Timestamp startTime = rs.getTimestamp("start_time");
				Timestamp completeTime = rs.getTimestamp("complete_time");
				int boxOpenTimes = getBoxOpenTimes(id, user_id);
				ArrayList<Activity> activities = getActivitiesFromQuest(id);
				// Create new Quest object and add it to the list
				Quest quest = new Quest(id, user_id, completionState, title, detail, startTime, completeTime, boxOpenTimes, activities);
				quests.add(quest);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return quests;
	}

	public int getBoxOpenTimes(int questId, int userId) {
		String sql = """
				SELECT COUNT(*)
				FROM "box_open_record"
				WHERE quest_id = ? AND user_id = ?
				""";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, questId);
			pstmt.setInt(2, userId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Creates quest record with all task(s) or pomodoro_timer in database.
	 *
	 * @param quest      the record to create.
	 * @param activities is a list of either task(s) or pomodoro_timer.
	 */
	public void createQuest(Quest quest, ArrayList<Activity> activities) {
		// Inserts new quest and returning the id, needed to create activities later
		String sql = """
				INSERT INTO "quest" (
				    user_id,
				    completion_state, 
				    title, 
				    detail, 
				    start_time, 
				    complete_time
				) VALUES (?, ?, ?, ?, ?, ?) 
				RETURNING id;
				""";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, quest.getUserId());
			pstmt.setBoolean(2, false); // Quest not yet completed
			pstmt.setString(3, quest.getTitle());
			pstmt.setString(4, quest.getDetail());
			pstmt.setTimestamp(5, null); // Quest not yet started
			pstmt.setTimestamp(6, null); // Quest not yet started

			ResultSet rs = pstmt.executeQuery(); // Executes query and gets a result set
			if (rs.next()) {
				int questId = rs.getInt("id"); // Gets the id of the inserted quest
				quest.setId(questId); // Sets the id in the Quest object
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		// Creates activities (task or pomodoro_timer) associated with the quest
		createActivities(quest, activities);
	}

	/**
	 * Starts quest and updates start_time for these records.
	 *
	 * @param quest record to update and get id from.
	 */
	public void startQuest(Quest quest) {
		String questSql = """
				UPDATE "quest"
				SET start_time = ?
				WHERE id = ?
				""";

		try (PreparedStatement questStmt = connection.prepareStatement(questSql)) {
			questStmt.setTimestamp(1, Timestamp.from(Instant.now()));
			questStmt.setInt(2, quest.getId());
			questStmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		String taskSql = """
				UPDATE "task"
				SET start_time = ?
				WHERE id = ?
				""";

		try (PreparedStatement taskStmt = connection.prepareStatement(taskSql)) {
			taskStmt.setTimestamp(1, Timestamp.from(Instant.now()));
			taskStmt.setInt(2, quest.getId());
			taskStmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Updates quest and task record in database with complete_time and changes completion_state to true for quest.
	 *
	 * @param quest record to update and get id from.
	 */
	public void completeQuest(Quest quest) {
		String questSql = """
				UPDATE "quest"
				SET
				    complete_time = ?,
				    completion_state = ?
				WHERE id = ?
				""";

		try (PreparedStatement questStmt = connection.prepareStatement(questSql)) {
			questStmt.setTimestamp(1, Timestamp.from(Instant.now()));
			questStmt.setBoolean(2, true);
			questStmt.setInt(3, quest.getId());

			questStmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		String taskSql = """
				UPDATE "task"
				SET end_time = ?
				WHERE id = ?
				""";

		try (PreparedStatement taskStmt = connection.prepareStatement(taskSql)) {
			taskStmt.setTimestamp(1, Timestamp.from(Instant.now()));
			taskStmt.setInt(2, quest.getId());

			taskStmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Updates quest record in database based on the attributes in the quest object
	 *
	 * @param currQuest is the quest with new title and detail record
	 */
	public void updateQuest(Quest currQuest) {
		String sql = """
				UPDATE "quest"
				SET title = ?, detail = ?
				WHERE id = ?
				""";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, currQuest.getTitle());
			pstmt.setString(2, currQuest.getDetail());
			pstmt.setInt(3, currQuest.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		//delete all current tasks of this quest and add a list of tasks of the quest with only descriptions
		String deleteTasksSql = """
				DELETE FROM "task"
				WHERE quest_id = ?
				""";
		try (PreparedStatement pstmt = connection.prepareStatement(deleteTasksSql)) {
			pstmt.setInt(1, currQuest.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		ArrayList<Activity> activities = currQuest.getActivities();
		for (Activity activity : activities) {
			if (activity instanceof Task task) {
				createTask(currQuest, task);
			}
		}


	}

	/**
	 * Deletes quest with a certain id from database.
	 *
	 * @param quest object referenced.
	 */
	public void deleteQuest(Quest quest) {
		String sql = """
				DELETE FROM "quest"
				WHERE id = ?
				""";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, quest.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// ==============================* ACTIVITY MANAGEMENT *================================

	/**
	 * Returns a list of Tasks or PomodoroTimer in relation to the specified quest record.
	 * The list does only ever retain one to several Tasks or one PomodoroTimer, they do not co-exist.
	 *
	 * @param questId is the record in database where id is the needed attribute.
	 * @return an ArrayList of Activity, either Task(s) or PomodoroTimer.
	 */
	public ArrayList<Activity> getActivitiesFromQuest(int questId) {
		ArrayList<Activity> activities = new ArrayList<>();

		String taskSql = """
				SELECT *
				FROM "task"
				WHERE quest_id = ?
				""";
		String pomodoroSql = """
				SELECT *
				FROM "pomodoro_timer"
				WHERE quest_id = ?
				""";

		try (PreparedStatement taskStmt = connection.prepareStatement(taskSql)) {
			taskStmt.setInt(1, questId);

			ResultSet rs = taskStmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String description = rs.getString("description");
				Timestamp startTime = rs.getTimestamp("start_time");
				Timestamp endTime = rs.getTimestamp("end_time");
				boolean completionState = rs.getBoolean("completion_state");

				Task task = new Task(id, questId, description, startTime, endTime, completionState);
				activities.add(task);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		if (activities.isEmpty()) { // Means it is not an ArrayList of Task but of PomodoroTimer
			try (PreparedStatement pomodoroStmt = connection.prepareStatement(pomodoroSql)) {
				pomodoroStmt.setInt(1, questId);

				ResultSet rs = pomodoroStmt.executeQuery();
				while (rs.next()) { // Will only run once
					int focusTime = rs.getInt("focus_time");
					int breakTime = rs.getInt("break_time");
					int intervals = rs.getInt("interval");

					PomodoroTimer pomodoro = new PomodoroTimer(questId, focusTime, breakTime, intervals);
					activities.add(pomodoro);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return activities;
	}

	/**
	 * Creates all activities (task or pomodoro_timer records) related to the quest record.
	 *
	 * @param quest      is the record in database where id is the needed attribute.
	 * @param activities are the list of Tasks or PomodoroTimer created in the application.
	 */
	private void createActivities(Quest quest, ArrayList<Activity> activities) {
		if (activities.isEmpty()) {
			return;
		}
		for (Activity activity : activities) {
			if (activity instanceof PomodoroTimer pomodoro) {
				createPomodoroTimer(quest, pomodoro);
				return; // No further loops after creation
			} else if (activity instanceof Task task) {
				createTask(quest, task);
			}
		}
	}

	// ==============================* TASK MANAGEMENT *====================================

	/**
	 * Creates a Task in the database.
	 *
	 * @param quest object, used to get the id of the quest.
	 * @param task  object with most of the needed attributes to create a task.
	 */
	public void createTask(Quest quest, Task task) {
		String sql = """
				INSERT INTO "task" (
				quest_id,
				description,
				start_time,
				end_time,
				completion_state
				)
				VALUES (?, ?, ?, ?, ?)
				RETURNING id
				""";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, quest.getId());
			pstmt.setString(2, task.getDescription());
			pstmt.setTimestamp(3, task.getStartTime());
			pstmt.setTimestamp(4, task.getEndTime());
			pstmt.setBoolean(5, task.getCompletionState());
			pstmt.executeUpdate();
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int taskId = rs.getInt("id"); // Gets the id of the inserted task
				task.setId(taskId); // Sets the id in the task object
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Updates the end_time value in the specified task record.
	 * Public method so task end_time can be updated directly.
	 *
	 * @param task is the record in database.
	 */
	public void updateTaskEndTime(Task task) {
		String sql = """
				UPDATE "task"
				SET end_time = ?
				WHERE id = ?
				""";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setTimestamp(1, task.getEndTime());
			pstmt.setInt(2, task.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Updates the completion_state value in the specified task record.
	 *
	 * @param task is the record in database.
	 */
	public void updateTaskCompletionState(Task task) {
		String sql = """
				UPDATE "task"
				SET completion_state = ?
				WHERE id = ?
				""";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setBoolean(1, task.getCompletionState());
			pstmt.setInt(2, task.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Delete a task record from the database.
	 *
	 * @param taskId is the task id.
	 */
	public void deleteTask(int taskId) {
		String sql = """
				DELETE FROM "task"
				WHERE id = ?
				""";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, taskId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// ==============================* POMODORO-TIMER MANAGEMENT *==========================

	/**
	 * Creates a PomodoroTimer in the database.
	 *
	 * @param quest    takes a Quest object and grabs the database id.
	 * @param pomodoro is the object that carries mose of the needed attributes to create a PomodoroTimer.
	 */
	public void createPomodoroTimer(Quest quest, PomodoroTimer pomodoro) {
		String sql = """
				INSERT INTO "pomodoro_timer" (
				    quest_id,
				    focus_time,
				    break_time,
				    interval
				) VALUES (?, ?, ?, ?)
				""";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, quest.getId());
			pstmt.setInt(2, pomodoro.getFocusTime());
			pstmt.setInt(3, pomodoro.getBreakTime());
			pstmt.setInt(4, pomodoro.getInterval());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public ArrayList<PomodoroTimer> getAllPomodoroQuests(User user) throws SQLException {
		ArrayList<PomodoroTimer> pomodoroQuests = new ArrayList<>();

		String questSql = "SELECT * FROM pomodoro_timer WHERE quest_id = ?";

		try (PreparedStatement pomodoroStmt = connection.prepareStatement(questSql)) {
			pomodoroStmt.setInt(1, user.getId());
			ResultSet rs = pomodoroStmt.executeQuery();
			while (rs.next()) {
				int focusTime = rs.getInt("focus_time");
				int breakTime = rs.getInt("break_time");
				int interval = rs.getInt("interval");
				int questId = rs.getInt("quest_id");

				PomodoroTimer pomodoro = new PomodoroTimer(questId, focusTime, breakTime, interval);
				pomodoroQuests.add(pomodoro);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return pomodoroQuests;
	}


	// ==============================* USER MANAGEMENT *====================================

	/**
	 * Check if the provided username exists in the database.
	 *
	 * @param username to check if it exists in database.
	 * @return boolean if exist or not.
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
	 * Check if the provided password matches the provided username.
	 *
	 * @param username is a unique identifier in the database.
	 * @param password to check against what is stored in database.
	 * @return boolean value if username && password are correct.
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

	/**
	 * Creates a user in database.
	 *
	 * @param username is the unique identifier.
	 * @param password to be stored along the User.
	 * @return boolean value if User created or not.
	 */
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
	 * Method to get all needed attributes of User with aid of username as a unique identifier.
	 * Most important are the stored id of the User.
	 *
	 * @param username is a unique identifier in the database.
	 * @return User object.
	 */
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

				// Returns a User object when username found
				return new User(id, username, password, appSound, sensorSound, deskMode);
			}
		}
		return null;
	}

	// ==============================* SECURITY & UTILITY *=================================

	/**
	 * Method to hash the password.
	 *
	 * @param password to hash as bytes.
	 * @return a hashed password as a hexadecimal string.
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

	/**
	 * Saves the time a user has opened a quest box during a quest.
	 *
	 * @param username the username of the user who opened the box
	 * @param questID  the quest that was running when box is opened
	 */
	public void saveBoxOpenTimes(String username, int questID) {
		//insert current time as timestamp to box open time
		String sql = "INSERT INTO \"box_open_record\" (user_id, quest_id, time) VALUES (?, ?, CURRENT_TIMESTAMP)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, getUserByUsername(username).getId());
			pstmt.setInt(2, questID);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
