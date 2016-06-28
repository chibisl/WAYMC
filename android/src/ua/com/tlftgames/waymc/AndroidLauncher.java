package ua.com.tlftgames.waymc;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;

public class AndroidLauncher extends AndroidApplication implements Tracker {
    private com.google.android.gms.analytics.Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useImmersiveMode = true;
        config.useWakelock = true;
        initialize(new StaighremGame(this), config);
    }

    private com.google.android.gms.analytics.Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }

    @Override
    public void trackScreen(String screenName) {
        com.google.android.gms.analytics.Tracker t = getTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void trackEvent(String category, String action, String label, int value) {
        com.google.android.gms.analytics.Tracker t = getTracker();

        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).setValue(value)
                .build());
    }

    @Override
    public void trackNonInteractionEvent(String category, String action, String label, int value) {
        com.google.android.gms.analytics.Tracker t = getTracker();

        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).setValue(value)
                .setNonInteraction(true).build());
    }

}
