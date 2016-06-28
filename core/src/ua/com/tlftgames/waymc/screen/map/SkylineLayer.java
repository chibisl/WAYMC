package ua.com.tlftgames.waymc.screen.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class SkylineLayer extends Actor {
    private Array<AtlasRegion> regions;

    public SkylineLayer(Array<AtlasRegion> regions) {
        this.regions = regions;
        float width = 0;
        float height = 0;
        if (regions != null) {
            for (AtlasRegion region : regions) {
                width += region.getRegionWidth();
            }
            height = regions.size > 0 ? regions.get(0).getRegionHeight() : 0;
        }
        this.setHeight(height);
        this.setWidth(width);
        this.setX(0);
        this.setY(370);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(Color.WHITE);
        float x = this.getX();
        for (int i = 0; i < regions.size; i++) {
            batch.draw(regions.get(i), x, this.getY());
            x += regions.get(i).getRegionWidth();
        }
        super.draw(batch, parentAlpha);
    }
}
