package ua.com.tlftgames.waymc.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import ua.com.tlftgames.waymc.StaighremGame;
import ua.com.tlftgames.waymc.Tracker;

public class HtmlLauncher extends GwtApplication implements Tracker {
    static final int WIDTH = 1280;
    static final int HEIGHT = 720;
    static HtmlLauncher instance;

    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration config = new GwtApplicationConfiguration(WIDTH, HEIGHT);

        Element element = Document.get().getElementById("embed-html");
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        element.appendChild(panel.getElement());
        config.rootPanel = panel;

        return config;
    }

    public ApplicationListener createApplicationListener() {
        return new StaighremGame(this);
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