package quietquest.controller;

public interface UIUpdater {
    public void updateConnectionStatusUI(boolean connectionStatus);
    public void updateLightSensorUI(int lightValue);
    public void updateMotionSensorUI(boolean motionDetected);
    public void updateUltrasonicSensorUI(int distanceValue);
}
