package ua.com.tlftgames.waymc.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ua.com.tlftgames.waymc.StaighremGame;
import ua.com.tlftgames.waymc.Tracker;

public class DesktopLauncher implements Tracker {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Who Are You, Mr. Cooper?";
        config.width = 1280;
        config.height = 720;
        config.resizable = false;
        config.fullscreen = true;
        new LwjglApplication(new StaighremGame(new DesktopLauncher()), config);
    }

    @Override
    public void trackScreen(String screenName) {

    }

    @Override
    public void trackEvent(String category, String action, String label, int value) {

    }

    @Override
    public void trackNonInteractionEvent(String category, String action, String label, int value) {

    }
}
