package ua.com.lastfairytale.staighrem;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

import ua.com.tlftgames.waymc.StaighremGame;
import ua.com.tlftgames.waymc.Tracker;

public class IOSLauncher extends IOSApplication.Delegate implements Tracker {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new StaighremGame(this), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
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