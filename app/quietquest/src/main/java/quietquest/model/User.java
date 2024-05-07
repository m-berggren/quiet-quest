package quietquest.model;

import java.sql.Timestamp;
import java.util.Date;

public class User {
    private int id;
    private String username;
    private String password;
    private Timestamp created_at;
    private boolean app_sound;
    private boolean sensor_sound;
    private boolean desk_mode;

    /**
     * User constructor
     *
     * @param username
     * @param password
     */
    public User(String username, String password) { //add confirmed_password stuff
        this.username = username;
        this.password = password;
        Date date = new Date();
        this.created_at = new Timestamp(date.getTime());
        this.app_sound = true;
        this.sensor_sound = true;
        this.desk_mode = false;
    }

    public User(int id, String username, boolean app_sound, boolean sensor_sound, boolean desk_mode) {
        this.id = id;
        this.username = username;
        this.password = null;
        Date date = new Date();
        this.created_at = new Timestamp(date.getTime());
        this.app_sound = app_sound;
        this.sensor_sound = sensor_sound;
        this.desk_mode = desk_mode;
    }

    public int getId() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public boolean getAppSound() {
        return app_sound;
    }

    public void toggleAppSound(boolean setting) {
        this.app_sound = setting;
    }

    public boolean getSensorSound() {
        return sensor_sound;
    }

    public void toggleSensorSound(boolean setting) {
        this.sensor_sound = setting;
    }

    public boolean getDeskMode() {
        return desk_mode;
    }

    public void toggleDeskMode(boolean setting) {
        this.desk_mode = setting;
    }
}
