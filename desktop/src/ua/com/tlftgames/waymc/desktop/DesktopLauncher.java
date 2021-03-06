package ua.com.tlftgames.waymc.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ua.com.tlftgames.waymc.StaighremGame;
import ua.com.tlftgames.waymc.Tracker;

public class DesktopLauncher implements Tracker {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1280;
        config.height = 720;
        config.title = "Who Are You, Mr. Cooper?";
        config.resizable = false;
        config.fullscreen = true;
        config.allowSoftwareMode = true;
        config.preferencesDirectory = ".WAYMC";
        config.addIcon("img/icon.png", FileType.Internal);
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
