package ua.com.tlftgames.waymc.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ShadowGroup extends Group {
    protected Image bg;

    public ShadowGroup() {
        addBg();
    }

    private void addBg() {
        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(new Color(0, 0, 0, 0.7f));
        pixmap.fill();
        this.bg = new Image(new Texture(pixmap));
        this.bg.setBounds(0, 0, 0, 0);
        this.addActor(this.bg);
    }

    @Override
    protected void sizeChanged() {
        this.bg.setSize(this.getWidth(), this.getHeight());
    }
}
