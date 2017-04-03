package ua.com.tlftgames.waymc.screen.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.screen.stage.GameStage;

public class WorldScrollPane extends ScrollPane {
    private GameStage stage;
    private World world;
    private NinePatchDrawable fadeLeft;
    private NinePatchDrawable fadeRight;

    public WorldScrollPane(GameStage stage, World world, TextureAtlas atlas) {
        super(world);
        this.stage = stage;
        this.world = world;
        this.world.setScrollPane(this);
        NinePatch shadowLeft = new NinePatch(atlas.findRegion("shadow-left"), 218, 1, 3, 3);
        NinePatch shadowRight = new NinePatch(atlas.findRegion("shadow-right"), 218, 1, 3, 3);
        this.fadeLeft = new NinePatchDrawable(shadowLeft);
        this.fadeLeft.getPatch().setColor(new Color(1, 1, 1, 0));
        this.fadeRight = new NinePatchDrawable(shadowRight);
        this.fadeRight.getPatch().setColor(new Color(1, 1, 1, 1));
        this.setFadeScrollBars(false);
        this.setOverscroll(false, false);
        this.setScrollingDisabled(false, true);
        this.setBounds(0, 0, Config.getInstance().gameWidth, Config.getInstance().gameHeight);
        this.setSmoothScrolling(true);
        this.layout();
        this.scrollToPin();
    }

    @Override
    protected float getMouseWheelX() {
        return 0;
    }

    public void scrollToPin() {
        this.scrollToPin(this.world.getPin().getX());
    }

    public void scrollToAttention(int attentionType) {
        this.scrollToPin(this.world.getMetro().getAttentionX(attentionType));
    }

    public void scrollToPin(float nextX) {
        if (nextX < this.getScrollX() + this.getWidth() / 4 || nextX > this.getScrollX() + 3 * this.getWidth() / 4) {
            this.setScrollX(nextX - this.getWidth() / 2);
            this.updateVisualScroll();
        }
    }

    public World getWorld() {
        return this.world;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (this.getVisualScrollX() != this.getScrollX() || this.isPanning() || this.isFlinging()) {
        	updateDecoration();
        }
    }
    
    @Override
    public void updateVisualScroll() {
    	super.updateVisualScroll();
    	this.updateDecoration();
    }
    
    public void updateDecoration() {
    	this.fadeLeft.getPatch().setColor(new Color(1, 1, 1, this.getScrollPercentX()));
        this.fadeRight.getPatch().setColor(new Color(1, 1, 1, 1 - this.getScrollPercentX()));
        updateAttentionMarkers();
    }

    public void updateAttentionMarkers() {
        Metro metro = this.world.getMetro();
        stage.updateHighAttentionMarkers(this.getScrollX(), metro.getAttentionX(Metro.ATTENTION_HIGH_LEFT),
                metro.getAttentionX(Metro.ATTENTION_HIGH_RIGHT));
        stage.updateAttentionMarkers(this.getScrollX(), metro.getAttentionX(Metro.ATTENTION_LEFT),
                metro.getAttentionX(Metro.ATTENTION_RIGHT));
        stage.updatePinAttentionMarkers(this.getScrollX(), getWorld().getPin().getX(),
        		getWorld().getPin().getX());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
        this.fadeLeft.draw(batch, 0, 0, this.fadeLeft.getMinWidth(), this.getHeight());
        this.fadeRight.draw(batch, this.getWidth() - this.fadeRight.getMinWidth(), 0, this.fadeRight.getMinWidth(),
                this.getHeight());
    }

}
