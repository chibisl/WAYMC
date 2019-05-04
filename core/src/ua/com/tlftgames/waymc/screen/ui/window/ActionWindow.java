package ua.com.tlftgames.waymc.screen.ui.window;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;

public class ActionWindow extends Window {
    private WindowBody body;
    private Image image;
    private String imageTextureName = "";
    private InfoWindow info;

    public ActionWindow(UIHelper helper) {
        super(helper);

        image = new Image();
        image.setBounds((this.windowGroup.getWidth() - 780) / 2, this.windowGroup.getHeight() - 230, 780, 200);
        this.windowGroup.addActor(image);

        this.info = new InfoWindow(this.getHelper());
        this.windowGroup.addActor(info);
    }

    public Image getImage() {
        return this.image;
    }

    public InfoWindow getInfo() {
        return this.info;
    }

    private void setImageTextureName(String name) {
        this.imageTextureName = name;
    }

    public void setImageTexture(final AtlasRegion texture) {
        this.image.setVisible(true);
        if (this.imageTextureName.contentEquals(texture.name + "_" + texture.index)) {
            return;
        }

        this.image.addAction(sequence(fadeOut(0.2f), run(new Runnable() {
            @Override
            public void run() {
                ActionWindow.this.getImage().setDrawable(new TextureRegionDrawable(texture));
                ActionWindow.this.setImageTextureName(texture.name + "_" + texture.index);
            }
        }), fadeIn(0.2f)));
    }

    public void setPlaceImageTexture() {
        AtlasRegion image = this.getAtlas().findRegion("place",
                GameCore.getInstance().getPlaceManager().getCurrentPlace().getIndex());
        this.setImageTexture(image);
    }

    public void reset() {
        this.clearBody();
        this.image.setDrawable(null);
    }

    public void clearBody() {
        if (this.body != null) {
            this.body.remove();
            this.body = null;
        }
    }

    public void setBody(WindowBody body) {
        this.clearBody();
        this.body = body;
        this.body.setX((this.windowGroup.getWidth() - this.body.getWidth()) / 2);
        this.windowGroup.addActor(this.body);
        if (this.isVisible()) {
            this.body.getColor().a = 0f;
            this.body.addAction(fadeIn(0.2f));
        }
    }

    public void updateBody(final WindowBody body) {
        if (this.body != null) {
            this.body.addAction(sequence(fadeOut(0.2f), run(new Runnable() {
                @Override
                public void run() {
                    ActionWindow.this.setBody(body);
                }
            })));
        } else {
            ActionWindow.this.setBody(body);
        }
    }

    public WindowBody getBody() {
        return this.body;
    }

    @Override
    protected void afterShow() {
        this.body.afterShow();
    }
}
