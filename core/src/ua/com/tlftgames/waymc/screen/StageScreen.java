package ua.com.tlftgames.waymc.screen;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.Manager;
import ua.com.tlftgames.waymc.Tracker;
import ua.com.tlftgames.waymc.screen.stage.LoadStage;
import ua.com.tlftgames.waymc.screen.stage.StartStage;

public class StageScreen implements Screen {
    private boolean needLoad = false;
    private LoadStage stage;
    private static StageScreen instance;
    private Tracker tracker;

    public static StageScreen getInstance() {
        if (instance == null)
            instance = new StageScreen();
        return instance;
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
    }

    public Tracker getTracker() {
        return tracker;
    }

    public StageScreen() {
        this.setStage(new StartStage());
    }

    public void setStage(final LoadStage stage) {
        Gdx.input.setInputProcessor(null);
        if (this.stage != null) {
            LowMusicAction lowMusicAction = new LowMusicAction();
            float startVolume = (this.stage.getMusic() != null) ? this.stage.getMusic().getVolume() : 1;
            lowMusicAction.setStartVolume(startVolume);
            lowMusicAction.setDuration(0.5f);
            this.stage.getShadow().getColor().a = 0f;
            this.stage.getShadow().setVisible(true);
            this.stage.getShadow().toFront();
            this.stage.getShadow().addAction(sequence(parallel(fadeIn(0.5f), lowMusicAction), run(new Runnable() {
                @Override
                public void run() {
                    StageScreen.this.updateStage(stage);
                }
            })));
            this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            this.updateStage(stage);
        }
        // debug for actors
        // stage.setDebugAll(true);
    }

    protected void updateStage(LoadStage stage) {
        this.needLoad = true;
        if (this.stage != null) {
            this.stage.dispose();
        }
        this.stage = stage;
        Gdx.input.setInputProcessor(this.stage);
        this.stage.setViewport(new FitViewport(Config.getInstance().gameWidth, Config.getInstance().gameHeight));
        this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public LoadStage getStage() {
        return stage;
    }

    public void reloadStage() {
        this.stage.clear();
        this.stage.start();
    }

    @Override
    public void render(float delta) {
        if (needLoad && Manager.getInstance().update() && this.stage.allLoaded()) {
            needLoad = false;
            this.stage.start();
        }
        getStage().act(delta);
        Gdx.graphics.getGL20().glClearColor(0, 0, 0, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        getStage().draw();
    }

    @Override
    public void hide() {
        this.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void resize(int width, int height) {
        Vector2 size = Scaling.fit.apply(Config.getInstance().gameWidth, Config.getInstance().gameHeight, width,
                height);
        int viewportX = (int) (width - size.x) / 2;
        int viewportY = (int) (height - size.y) / 2;
        int viewportWidth = (int) size.x;
        int viewportHeight = (int) size.y;
        Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        this.stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        instance = null;
    }

    private class LowMusicAction extends TemporalAction {
        private float startVolume = 1;

        @Override
        protected void update(float percent) {
            Music music = ((LoadStage) target.getStage()).getMusic();
            if (music != null)
                music.setVolume(this.startVolume - percent * this.startVolume);
        }

        public void setStartVolume(float volume) {
            this.startVolume = volume;
        }
    }

}
