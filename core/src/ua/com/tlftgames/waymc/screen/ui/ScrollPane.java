package ua.com.tlftgames.waymc.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class ScrollPane extends com.badlogic.gdx.scenes.scene2d.ui.ScrollPane {
    private NinePatchDrawable fadeTop;
    private NinePatchDrawable fadeBottom;

    public ScrollPane(Actor widget, NinePatch fadeTop, NinePatch fadeBottom) {
        super(widget);
        this.layout();
        this.fadeTop = new NinePatchDrawable(fadeTop);
        this.fadeTop.getPatch().setColor(new Color(1, 1, 1, 0));
        this.fadeBottom = new NinePatchDrawable(fadeBottom);
        this.fadeBottom.getPatch().setColor(new Color(1, 1, 1, 1));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (this.getHeight() < this.getWidget().getHeight()
                && (this.isPanning() || this.isFlinging() || this.getVisualScrollY() != this.getScrollY())) {
            this.fadeTop.getPatch().setColor(new Color(1, 1, 1, this.getScrollPercentY()));
            this.fadeBottom.getPatch().setColor(new Color(1, 1, 1, 1 - this.getScrollPercentY()));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(Color.WHITE);
        if (this.getHeight() < this.getWidget().getHeight()) {
            this.fadeTop.draw(batch, this.getX(), this.getY() + this.getHeight() - this.fadeTop.getMinHeight(),
                    this.getWidth(), this.fadeTop.getMinHeight());
            this.fadeBottom.draw(batch, this.getX(), this.getY(), this.getWidth(), this.fadeBottom.getMinHeight());
        }
    }
}
