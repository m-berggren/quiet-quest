package quietquest.controller;

/**
 * Interface for updating the UI components of the application where sensor data is displaying.
 */
public interface UIUpdater {
    void updateConnectionStatusUI(boolean connectionStatus);

    void updateLightSensorUI(int lightValue);

    void updateMotionSensorUI(boolean motionDetected);

    void updateUltrasonicSensorUI(int distanceValue);
}
