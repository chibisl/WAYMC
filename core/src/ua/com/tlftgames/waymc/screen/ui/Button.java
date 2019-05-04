package ua.com.tlftgames.waymc.screen.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Button extends Actor {
    static protected final Color tempColor = new Color();
    protected TextureRegion bg;
    protected TextureRegion bgTouched;
    private boolean touched = false;
    private boolean checked = false;

    public Button(TextureRegion bg, TextureRegion bgTouched, boolean isChecker) {
        this.bg = bg;
        this.bgTouched = bgTouched;
        this.setSize(bg.getRegionWidth(), bg.getRegionHeight());
        if (isChecker) {
            this.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Button.this.setChecked(!Button.this.isChecked());
                }
            });
        } else {
            this.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    if (!Button.this.isChecked())
                        Button.this.setTouched(true);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    if (!Button.this.isChecked())
                        Button.this.setTouched(false);
                }
            });
        }
    }

    public Button(TextureRegion bg, TextureRegion bgTouched) {
        this(bg, bgTouched, false);
    }

    public Button(TextureRegion bg, boolean isChecker) {
        this(bg, bg, isChecker);
    }

    public Button(TextureRegion bg) {
        this(bg, bg);
    }

    public void setBg(TextureRegion bg) {
        this.bg = bg;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public boolean isTouched() {
        return this.touched;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return this.checked;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = tempColor.set(getColor());
        color.a *= parentAlpha;
        batch.setColor(color.r, color.g, color.b, color.a);
        if (isTouched() || isChecked()) {
            batch.draw(bgTouched, getX() + (this.getWidth() - bgTouched.getRegionWidth()) / 2, getY(),
                    this.getOriginX(), this.getOriginY(), bgTouched.getRegionWidth(), bgTouched.getRegionHeight(),
                    this.getScaleX(), this.getScaleY(), this.getRotation());
        } else {
            batch.draw(bg, getX() + (this.getWidth() - bg.getRegionWidth()) / 2, getY(), this.getOriginX(),
                    this.getOriginY(), bg.getRegionWidth(), bg.getRegionHeight(), this.getScaleX(), this.getScaleY(),
                    this.getRotation());
        }
        super.draw(batch, parentAlpha);
        batch.setColor(Color.WHITE);
    }
}
