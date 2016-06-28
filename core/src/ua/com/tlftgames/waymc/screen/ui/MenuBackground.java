package ua.com.tlftgames.waymc.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class MenuBackground extends Actor {
    private Array<AtlasRegion> textures;

    public MenuBackground(Array<AtlasRegion> textures) {
        this.textures = textures;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(Color.WHITE);
        int i = 0;
        for (AtlasRegion texture : textures) {
            batch.draw(texture, i++ * 320, 420);
        }
    }
}
