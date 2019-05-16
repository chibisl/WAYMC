package ua.com.tlftgames.waymc.screen.ui.window.qte;

import static com.badlogic.gdx.math.Interpolation.pow2;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.screen.ui.window.PlaceWindowManager;
import ua.com.tlftgames.waymc.screen.ui.window.WindowBody;

abstract public class QTEWindowBody extends WindowBody {
    private PlaceWindowManager manager;
    private int difficultLevel;

    public QTEWindowBody(PlaceWindowManager manager, int difficultLevel, String qteName) {
        super();
        this.manager = manager;
        this.difficultLevel = difficultLevel;
        LabelStyle style = new LabelStyle();
        style.font = Config.getInstance().bigFont;
        style.fontColor = new Color(1, 1, 1, 1);
        Label qteLabel = new Label(Translator.getInstance().translate(qteName), style);
        qteLabel.setBounds(0, 0, this.getWidth(), this.getHeight() - 70);
        qteLabel.setAlignment(Align.top);
        qteLabel.setColor(new Color(1, 1, 1, 0));
        qteLabel.addAction(sequence(fadeIn(1f, pow2), delay(1f), fadeOut(0.5f, pow2), new RunnableAction() {
            @Override
            public void run() {
                QTEWindowBody.this.show();
            }
        }));
        this.addActor(qteLabel);
    }

    protected void success() {
        this.manager.setResult(PlaceWindowManager.RESULT_GOOD);
        this.hide();
    }

    protected void fail() {
        this.manager.setResult(PlaceWindowManager.RESULT_BAD);
        this.hide();
    }

    public int getDifficultLevel() {
        return this.difficultLevel;
    }

    public TextureAtlas getAtlas() {
        return this.manager.getAtlas();
    }

    private void hide() {
        this.addAction(sequence(fadeOut(0.5f, pow2), new RunnableAction() {
            @Override
            public void run() {
                QTEWindowBody.this.manager.showResult();
            }
        }));
    }

    abstract protected void show();
}
