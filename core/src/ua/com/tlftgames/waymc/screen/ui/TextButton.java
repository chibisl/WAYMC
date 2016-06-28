package ua.com.tlftgames.waymc.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

public class TextButton extends Button {
    private GlyphLayout layout = new GlyphLayout();
    private BitmapFontCache cache;
    private Color color;
    private Color colorTouched;
    private BitmapFont font;

    public TextButton(TextureRegion bg, TextureRegion bgTouched, String text, BitmapFont font) {
        this(bg, bgTouched, text, font, font.getColor(), font.getColor());
    }

    public TextButton(TextureRegion bg, TextureRegion bgTouched, String text, BitmapFont font, Color color,
            Color colorTouched) {
        super(bg, bgTouched);
        this.font = font;
        this.color = color;
        this.colorTouched = colorTouched;
        layout.setText(font, text, Color.WHITE, 0, Align.left, false);
        this.setSize(layout.width, layout.height);
        cache = font.newFontCache();
        cache.setText(layout, 0, 0);
    }

    public void setText(String text) {
        layout = new GlyphLayout();
        layout.setText(font, text, Color.WHITE, 0, Align.left, false);
        cache = font.newFontCache();
        cache.setText(layout, 0, 0);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Color color = tempColor.set(getColor());
        color.a *= parentAlpha;
        batch.setColor(color.r, color.g, color.b, color.a);
        if (!(isTouched() || isChecked())) {
            color.mul(this.color);
        } else {
            color.mul(this.colorTouched);
        }
        cache.tint(color);
        cache.setPosition((int) (this.getX() + (this.getWidth() - layout.width) / 2),
                (int) (this.getY() + layout.height + bg.getRegionHeight() + 3));
        cache.draw(batch);
        batch.setColor(Color.WHITE);
    }
}
