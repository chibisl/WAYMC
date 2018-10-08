package ua.com.tlftgames.waymc.screen.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public abstract class LoadStage extends Stage {
    protected Music music;
    protected Image shadow;
    private boolean loaded = false;

    public LoadStage() {
        this.addLoader();
        this.createShadow();
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    private void addLoader() {
        Image loader = new Image(new Texture(Gdx.files.internal("img/loader.png")));
        loader.setPosition(Config.getInstance().gameWidth - loader.getWidth() - 30, 30);
        loader.setOrigin(Align.center);
        loader.addAction(forever(sequence(rotateTo(-360f, 1f), rotateTo(0f))));
        this.addActor(loader);
    }

    private void createShadow() {
        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 1f));
        pixmap.fill();
        shadow = new Image(new Texture(pixmap));
        shadow.setBounds(0, 0, this.getWidth(), this.getHeight());
        shadow.setVisible(false);
    }

    public Image getShadow() {
        return this.shadow;
    }

    public void start() {
        this.clear();
        this.addActor(shadow);
        this.loaded = true;
    }

    public Music getMusic() {
        return this.music;
    }

    public abstract boolean allLoaded();
}
