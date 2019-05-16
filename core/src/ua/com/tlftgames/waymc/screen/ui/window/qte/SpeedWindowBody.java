package ua.com.tlftgames.waymc.screen.ui.window.qte;

import static com.badlogic.gdx.math.Interpolation.pow2;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.touchable;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.screen.ui.Button;
import ua.com.tlftgames.waymc.screen.ui.window.PlaceWindowManager;

public class SpeedWindowBody extends QTEWindowBody {
    private int cardWidth = 120;
    private int cardHeight = 120;
    private ArrayList<Button> cards;
    private float time = 0;
    private float timeOut = 4.9f;
    private int rowCount = 4;
    private int colCount = 5;
    private int cardCount = 0;
    private int successCardCount = 10;
    private boolean run = false;
    private boolean win = true;
    private ArrayList<Button> successCards;
    private Image timer;
    private int lifeDecrease = -3;

    public SpeedWindowBody(PlaceWindowManager manager, int difficultLevel) {
        super(manager, difficultLevel, "qte.speed");
        cardCount = rowCount * colCount;
        cards = new ArrayList<Button>(cardCount);
        timeOut -= difficultLevel * 0.5f;
        successCardCount += difficultLevel;
        successCards = new ArrayList<Button>(successCardCount);
    }

    private Button createCard(int index) {
        boolean isSuccess = index < successCardCount;
        Button card = new Button(this.getAtlas().findRegion("card-empty"),
                this.getAtlas().findRegion("card-empty-touched"));
        card.setOrigin(Align.center);
        card.setSize(cardWidth, cardHeight);
        card.setTouchable(Touchable.disabled);
        if (isSuccess) {
            successCards.add(card);
        }
        card.addAction(this.getShowActions(isSuccess));
        card.addListener(isSuccess ? this.getSuccessListener() : this.getFailListener());

        return card;
    }

    public void act(float delta) {
        super.act(delta);
        if (!run) {
            return;
        }
        time += delta;
        this.updateTimer();
        if (time > timeOut) {
            stop();
        }
    }

    private void updateTimer() {
        timer.setScale(Math.max((timeOut - time), 0) / timeOut, 1);
    }

    private SequenceAction getShowActions(final boolean isSuccess) {
        return sequence(scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

            @Override
            public void run() {
                ((Button) target)
                        .setBg(SpeedWindowBody.this.getAtlas().findRegion(isSuccess ? "card-success" : "card-fail"));
            }

        }, scaleTo(1, 1, 0.3f, pow2), touchable(Touchable.enabled));
    }

    private ClickListener getSuccessListener() {
        return new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean isTouched = super.touchDown(event, x, y, pointer, button);
                if (isTouched) {
                    Button card = (Button) event.getTarget();
                    card.setTouchable(Touchable.disabled);
                    card.addAction(sequence(new RunnableAction() {

                        @Override
                        public void run() {
                            SpeedWindowBody.this.successCards.remove((Button) target);
                        }

                    }, scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

                        @Override
                        public void run() {
                            ((Button) target).setBg(SpeedWindowBody.this.getAtlas().findRegion("card-empty"));
                        }

                    }, scaleTo(1, 1, 0.3f, pow2), new RunnableAction() {

                        @Override
                        public void run() {
                            if (SpeedWindowBody.this.successCards.isEmpty()) {
                                SpeedWindowBody.this.stop();
                            }
                        }

                    }));
                }
                return isTouched;
            }
        };
    }

    private ClickListener getFailListener() {
        return new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Button card = (Button) event.getTarget();
                card.setTouchable(Touchable.disabled);
                card.addAction(sequence(new RunnableAction() {

                    @Override
                    public void run() {
                        SpeedWindowBody.this.win = false;
                        GameCore.getInstance().addLife(SpeedWindowBody.this.lifeDecrease);
                    }

                }, repeat(3, sequence(scaleTo(1.5f, 1.5f, 0.1f, pow2), scaleTo(1, 1, 0.2f, pow2))),
                        touchable(Touchable.enabled)));
            }
        };
    }

    private void stop() {
        run = false;
        for (Button card : cards) {
            card.clearListeners();
            card.setTouchable(Touchable.disabled);
        }

        for (Button successCard : successCards) {
            SpeedWindowBody.this.win = false;
            successCard.addAction(repeat(3, sequence(scaleTo(1.5f, 1.5f, 0.1f, pow2), scaleTo(1, 1, 0.2f, pow2))));
        }

        this.addAction(sequence(delay(1f), new RunnableAction() {
            @Override
            public void run() {
                if (SpeedWindowBody.this.win) {
                    SpeedWindowBody.this.success();
                } else {
                    GameCore.getInstance().addLife(SpeedWindowBody.this.lifeDecrease * successCards.size());
                    SpeedWindowBody.this.fail();
                }
            }
        }));
    }

    @Override
    protected void show() {
        float step = (this.getWidth() - 50) / colCount;
        int startX = 25 + (int) (step - cardWidth) / 2;
        int startY = (int) (step - cardHeight) / 2 - 60;

        for (int i = 0; i < cardCount; i++) {
            cards.add(this.createCard(i));
        }

        Collections.shuffle(cards);

        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++) {
                float x = col * step + startX;
                float y = row * step + startY;
                Button card = cards.get(row * colCount + col);
                card.setPosition(x, y);
                this.addActor(card);
            }

        timer = new Image(this.getAtlas().findRegion("metro-line"));
        timer.setBounds(25, this.getHeight() + 200, this.getWidth() - 50, 10);
        timer.setOrigin(Align.center);
        this.addActor(timer);
        this.addAction(sequence(delay(0.6f), new RunnableAction() {
            @Override
            public void run() {
                SpeedWindowBody.this.run = true;
            }
        }));
    }
}
