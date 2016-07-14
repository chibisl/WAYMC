package ua.com.tlftgames.waymc.screen.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.screen.stage.GameStage;
import ua.com.tlftgames.waymc.screen.ui.Button;
import ua.com.tlftgames.waymc.screen.ui.UIGroup;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Station extends Group {
    private int index;
    private Button stationCircle;
    private Label stationLabel;
    private Image attention;
    private Image highAttention;

    public Station(TextureRegion stationRegion, TextureRegion stationRegionTouched, TextureRegion attentionRegion,
            TextureRegion highAttentionRegion, int index, String label, float x, float y, float labelY) {
        super();
        this.setPosition(x, y);
        this.index = index;
        this.setName(label);
        stationCircle = new Button(stationRegion, stationRegionTouched);

        stationCircle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameCore.getInstance().getPlaceManager().setOpenPlace(Station.this.getIndex());
                UIGroup ui = ((GameStage) Station.this.getStage()).getUIGroup();
                ui.getPlaceWindowManager().showPlace(GameCore.getInstance().getPlaceManager().getOpenPlace());
            }
        });
        this.addActor(stationCircle);

        stationLabel = new Label(Translator.getInstance().translate(this.getName()), Config.getInstance().stationStyle);
        int labelX = (index == 12) ? -81 : -75;
        stationLabel.setBounds(labelX, labelY - y, 200, 30);
        stationLabel.setAlignment(Align.center);
        this.addActor(stationLabel);

        attention = Station.createAttention(attentionRegion);
        attention.setPosition(stationCircle.getX() + 20, stationCircle.getY() + 20);
        attention.setVisible(false);
        this.addActor(attention);

        highAttention = Station.createAttention(highAttentionRegion);
        highAttention.setPosition(stationCircle.getX() + 20, stationCircle.getY() + 20);
        highAttention.setVisible(false);
        this.addActor(highAttention);
    }

    public static Image createAttention(TextureRegion region) {
        Image attention = new Image(region);
        attention.setOrigin(Align.center);
        attention.setTouchable(Touchable.disabled);
        attention.addAction(forever(sequence(scaleTo(0.8f, 0.8f, 0.4f), scaleTo(1f, 1f, 0.4f))));
        return attention;
    }

    public int getIndex() {
        return this.index;
    }

    public void showAttention() {
        if (this.highAttention.isVisible())
            this.highAttention.setVisible(false);
        this.attention.setVisible(true);
    }

    public void hideAttentions() {
        this.attention.setVisible(false);
        this.highAttention.setVisible(false);
    }

    public void showHighAttention() {
        if (this.attention.isVisible())
            this.attention.setVisible(false);
        this.highAttention.setVisible(true);
    }

    public void setUnreachable() {
        this.stationCircle.setChecked(true);
        this.stationLabel.setStyle(Config.getInstance().stationTouchedStyle);
    }

    public void setReachable() {
        this.stationCircle.setChecked(false);
        this.stationLabel.setStyle(Config.getInstance().stationStyle);
    }
}
