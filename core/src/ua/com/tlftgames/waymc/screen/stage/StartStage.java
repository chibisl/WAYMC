package ua.com.tlftgames.waymc.screen.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ua.com.tlftgames.waymc.screen.StageScreen;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class StartStage extends ReturnHandlingStage {

    private Image developer;
    private float time = 1f;

    private void addDeveloperLogo() {
        developer.setPosition((this.getWidth() - developer.getWidth()) / 2,
                (this.getHeight() - developer.getHeight()) / 2);
        developer.getColor().a = 0f;
        developer.addAction(sequence(fadeIn(this.time), delay(this.time), fadeOut(this.time / 2), run(new Runnable() {
            @Override
            public void run() {
                StageScreen.getInstance().setStage(new MenuStage(true));
            }
        })));
        this.addActor(developer);
    }

    @Override
    public void dispose() {
        developer = null;
        super.dispose();
    }

    @Override
    public void start() {
        super.start();
        Texture developerTexture = new Texture(Gdx.files.internal("img/dev_logo.png"));
        developerTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        developer = new Image(developerTexture);
        addDeveloperLogo();
    }

    @Override
    public boolean allLoaded() {
        return true;
    }
}
