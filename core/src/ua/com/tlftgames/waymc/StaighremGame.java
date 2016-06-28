package ua.com.tlftgames.waymc;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import ua.com.tlftgames.waymc.listener.Dispatcher;
import ua.com.tlftgames.waymc.screen.StageScreen;

public class StaighremGame extends Game {
    private Tracker tracker;

    public StaighremGame(Tracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void create() {

        // Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width,
        // Gdx.graphics.getDesktopDisplayMode().height, true);
        Gdx.input.setCatchBackKey(true);
        StageScreen screen = StageScreen.getInstance();
        screen.setTracker(tracker);
        this.setScreen(screen);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        Manager.getInstance().dispose();
        Config.dispose();
        GameCore.dispose();
        Settings.dispose();
        Translator.dispose();
        Dispatcher.dispose();
    }
}
