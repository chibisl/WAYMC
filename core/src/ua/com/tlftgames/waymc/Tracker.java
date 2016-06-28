package ua.com.tlftgames.waymc;

public interface Tracker {
    public void trackScreen(String screenName);
    public void trackEvent(String category, String action, String label, int value);
    public void trackNonInteractionEvent(String category, String action, String label, int value);
}
