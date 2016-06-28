package ua.com.tlftgames.waymc.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.listener.Dispatcher;
import ua.com.tlftgames.waymc.listener.Listener;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.stage.FinalStage;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class LifeCounter extends Actor implements Listener {
    private TextureRegion bg;
    private NinePatchDrawable mg;
    private TextureRegion fg;
    private final float startWidth = 150;
    private float currentWidth;

    public LifeCounter(TextureRegion bg, NinePatch mg, TextureRegion fg) {
        this.bg = bg;
        this.mg = new NinePatchDrawable(mg);
        this.fg = fg;
        this.setSize(fg.getRegionWidth(), fg.getRegionHeight());
        Dispatcher.getInstance().addListener(Dispatcher.EVENT_LIFE_CHANGE, this);
        this.setPosition(0, Config.getInstance().gameHeight - this.getHeight());
        this.setCurrentLifeWidth(
                41 + (int) (startWidth * GameCore.getInstance().getLife() / Config.getInstance().maxLife));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(Color.WHITE);
        batch.draw(this.bg, getX(), getY());
        mg.draw(batch, getX(), getY(), currentWidth, this.getHeight());
        batch.draw(this.fg, getX(), getY());
    }

    @Override
    public void fireEvent(int event) {
        ChangeLifeAction changeLifeAction = new ChangeLifeAction();
        changeLifeAction
                .setEndWidth(41 + (int) (startWidth * GameCore.getInstance().getLife() / Config.getInstance().maxLife));
        changeLifeAction.setDuration(0.2f);
        float delay = 0.5f;
        if (((UIGroup) this.getParent()).getStage().getWorld().getPin().hasActions()) {
            delay = 2.5f;
        }
        SequenceAction action = sequence(delay(delay), changeLifeAction);
        if (GameCore.getInstance().getLife() <= 0) {
            action.addAction(run(new Runnable() {
                @Override
                public void run() {
                    StageScreen.getInstance().setStage(new FinalStage(FinalStage.TYPE_GAME_OVER));
                }
            }));
        }
        this.addAction(action);
    }

    public float getCurrentLifeWidth() {
        return this.currentWidth;
    }

    public void setCurrentLifeWidth(float currentWidth) {
        this.currentWidth = currentWidth;
    }

    private class ChangeLifeAction extends TemporalAction {
        private float end, start;

        @Override
        protected void begin() {
            this.start = ((LifeCounter) target).getCurrentLifeWidth();
        }

        public void setEndWidth(float end) {
            this.end = end;
        }

        @Override
        protected void update(float percent) {
            ((LifeCounter) target).setCurrentLifeWidth(start + (end - start) * percent);
        }
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
    }
}
