package ua.com.tlftgames.waymc.screen.map;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.CoolRandomizer;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class Animator {
    public static final int SMOKE_SPREADING = -150;
    public static final float ZEPPELIN_Y = 600f;
    public static final float ZEPPELIN_X_VARIATY = 600f;
    public static final float ZEPPELIN_DURATION = 500f;
    public static final float ZEPPELIN_DELAY = 10f;

    public static Image addSmoke(final float startX, final float startY, final float startScale, final float startDelay,
            final float duration, final CoolRandomizer<AtlasRegion> randomizer, final Group group) {
        final boolean goLeft = (Math.random() < 0.5f);
        final Image smoke = new Image(randomizer.getRandomElement());
        int direction = goLeft ? (int) (-1 * (SMOKE_SPREADING + smoke.getWidth())) : SMOKE_SPREADING;
        smoke.setOrigin(Align.bottom);
        smoke.setPosition(startX - smoke.getWidth() / 2, startY);
        smoke.setScale(startScale);
        smoke.addAction(sequence(delay(startDelay), run(new Runnable() {
            @Override
            public void run() {
                Animator.addSmoke(startX, startY, startScale, startDelay, duration, randomizer, group);
            }
        }), parallel(moveTo(startX + direction, Config.getInstance().gameHeight, duration, Interpolation.sineOut),
                scaleTo(1.5f, 1.5f, duration, Interpolation.pow2In)), run(new Runnable() {
                    @Override
                    public void run() {
                        smoke.remove();
                    }
                })));
        group.addActor(smoke);
        smoke.toBack();
        return smoke;
    }

    public static Image addZeppelin(final AtlasRegion texture, final Group group) {
        final Image zeppelin = new Image(texture);
        float startX = group.getWidth() + zeppelin.getWidth();
        zeppelin.setPosition((float) (startX - zeppelin.getWidth() - Math.random() * ZEPPELIN_X_VARIATY), ZEPPELIN_Y);
        zeppelin.addAction(forever(sequence(moveTo(0 - zeppelin.getWidth(), zeppelin.getY(), ZEPPELIN_DURATION),
                delay((float) (ZEPPELIN_DELAY + Math.random() * ZEPPELIN_DELAY)),
                Actions.sizeTo(-1 * zeppelin.getWidth(), zeppelin.getHeight()),
                moveTo(startX, zeppelin.getY(), ZEPPELIN_DURATION),
                delay((float) (ZEPPELIN_DELAY + Math.random() * ZEPPELIN_DELAY)),
                Actions.sizeTo(zeppelin.getWidth(), zeppelin.getHeight()))));
        group.addActor(zeppelin);
        zeppelin.toFront();
        return zeppelin;
    }
}
