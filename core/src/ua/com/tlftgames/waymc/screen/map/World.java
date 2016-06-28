package ua.com.tlftgames.waymc.screen.map;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.CoolRandomizer;
import ua.com.tlftgames.waymc.GameCore;

import static com.badlogic.gdx.math.Interpolation.exp10;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class World extends Group {
    private SkylineLayer skyline;
    private Metro metro;
    private float pinY = 290;
    private Image pin;
    private WorldScrollPane scrollPane;
    private CoolRandomizer<AtlasRegion> smokeRandom;
    public static final int SMOKE1_START_X = 428;
    public static final int SMOKE1_START_Y = 600;
    public static final int SMOKE2_START_X = 453;
    public static final int SMOKE2_START_Y = 575;
    public static final int SMOKE3_START_X = 1710;
    public static final int SMOKE3_START_Y = 592;
    public static final float SMOKE_START_SCALE = 0.05f;
    public static final float SMOKE3_START_SCALE = 0.07f;
    public static final float SMOKE1_START_DELAY = 2.7f;
    public static final float SMOKE2_START_DELAY = 2.1f;
    public static final float SMOKE_DURATION = 75f;

    public World(TextureAtlas atlas) {
        addSkyline(atlas);
        this.setBounds(0, 0, skyline.getWidth(), Config.getInstance().gameHeight);
        addMetro(atlas);
        this.addPin(atlas.findRegion("pin"));
        smokeRandom = new CoolRandomizer<AtlasRegion>(atlas.findRegions("smoke"), 1);
        Animator.addSmoke(SMOKE1_START_X, SMOKE1_START_Y, SMOKE_START_SCALE, SMOKE1_START_DELAY, SMOKE_DURATION,
                smokeRandom, this);
        Animator.addSmoke(SMOKE2_START_X, SMOKE2_START_Y, SMOKE_START_SCALE, SMOKE2_START_DELAY, SMOKE_DURATION,
                smokeRandom, this);
        Animator.addSmoke(SMOKE3_START_X, SMOKE3_START_Y, SMOKE3_START_SCALE, SMOKE1_START_DELAY, SMOKE_DURATION,
                smokeRandom, this);
    }

    private void addMetro(TextureAtlas atlas) {
        metro = new Metro(atlas);
        this.addActor(metro);
    }

    private void addSkyline(TextureAtlas atlas) {
        skyline = new SkylineLayer(atlas.findRegions("city-fg"));
        this.addActor(skyline);
    }

    public void movePin() {
        float nextX = this.getNexPinX();
        this.movePin(nextX);
        this.scrollPane.scrollToPin(nextX);
    }

    public Metro getMetro() {
        return this.metro;
    }

    private float getNexPinX() {
        return 48 + GameCore.getInstance().getPlaceManager().getCurrentPlaceIndex() * this.getMetro().stationLineWidth;
    }

    private void addPin(AtlasRegion pinRegion) {
        pin = new Image(pinRegion);
        pin.setPosition(this.getNexPinX(), this.pinY);
        this.addActor(pin);
    }

    public void movePin(float newX) {
        this.scrollPane.setTouchable(Touchable.disabled);
        this.pin.addAction(sequence(delay(0.5f), moveTo(newX, this.pinY, 0.7f, exp10), run(new Runnable() {
            @Override
            public void run() {
                World.this.scrollPane.setTouchable(Touchable.enabled);
                World.this.getMetro().updateReach();
            }
        })));
    }

    public Image getPin() {
        return this.pin;
    }

    public void setScrollPane(WorldScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public void updateAttentions() {
        this.metro.updateAttentions();
        if (this.scrollPane != null) {
            this.scrollPane.updateAttentionMarkers();
        }
    }
}
