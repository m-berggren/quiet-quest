package quietquest.controller;

public interface UIUpdater {
    void updateConnectionStatusUI(boolean connectionStatus);

    void updateLightSensorUI(int lightValue);

    void updateMotionSensorUI(boolean motionDetected);

    void updateUltrasonicSensorUI(int distanceValue);
}
