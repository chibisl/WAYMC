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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.screen.ui.Button;
import ua.com.tlftgames.waymc.screen.ui.window.PlaceWindowManager;

public class MemoryWindowBody extends QTEWindowBody {
    private int cardWidth = 120;
    private int cardHeight = 120;
    private ArrayList<Button> cards;
    private float waitTimeOut = 2.1f;
    private int rowCount = 4;
    private int colCount = 4;
    private int cardCount = 0;
    private int successCardCount = 7;
    private boolean win = true;
    private ArrayList<Button> successCards;

    public MemoryWindowBody(PlaceWindowManager manager, int difficultLevel) {
        super(manager, difficultLevel, "qte.memory");
        cardCount = rowCount * colCount;
        cards = new ArrayList<Button>(cardCount);
        waitTimeOut -= (float) difficultLevel * 0.6f;

        successCardCount += difficultLevel;
        successCards = new ArrayList<Button>(successCardCount);
    }

    private Button createCard(int index) {
        boolean isSuccess = index < successCardCount;
        Button card = new Button(this.getAtlas().findRegion(isSuccess ? "card-success" : "card-fail"),
                this.getAtlas().findRegion("card-empty-touched"));
        card.setOrigin(Align.center);
        card.setSize(cardWidth, cardHeight);
        if (isSuccess) {
            successCards.add(card);
        }
        card.setTouchable(Touchable.disabled);
        card.addAction(this.getShowActions());
        card.addListener(isSuccess ? this.getSuccessListener() : this.getFailListener());

        return card;
    }

    private SequenceAction getShowActions() {
        return sequence(delay(waitTimeOut), scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

            @Override
            public void run() {
                ((Button) target).setBg(MemoryWindowBody.this.getAtlas().findRegion("card-empty"));
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
                            MemoryWindowBody.this.successCards.remove((Button) target);
                        }

                    }, scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

                        @Override
                        public void run() {
                            ((Button) target).setBg(MemoryWindowBody.this.getAtlas().findRegion("card-success"));
                        }

                    }, scaleTo(1, 1, 0.3f, pow2), new RunnableAction() {

                        @Override
                        public void run() {
                            if (MemoryWindowBody.this.successCards.isEmpty()) {
                                MemoryWindowBody.this.stop();
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
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean isTouched = super.touchDown(event, x, y, pointer, button);
                if (isTouched) {
                    Button card = (Button) event.getTarget();
                    card.setTouchable(Touchable.disabled);
                    card.addAction(sequence(scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

                        @Override
                        public void run() {
                            ((Button) target).setBg(MemoryWindowBody.this.getAtlas().findRegion("card-fail"));
                        }

                    }, scaleTo(1, 1, 0.3f, pow2), new RunnableAction() {

                        @Override
                        public void run() {
                            MemoryWindowBody.this.win = false;
                            MemoryWindowBody.this.stop();
                        }

                    }, repeat(3, sequence(scaleTo(1.5f, 1.5f, 0.1f, pow2), scaleTo(1, 1, 0.2f, pow2)))));
                }
                return isTouched;
            }
        };
    }

    private void stop() {
        for (Button card : cards) {
            card.clearListeners();
            card.setTouchable(Touchable.disabled);
        }

        for (Button successCard : successCards) {
            successCard.addAction(sequence(scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

                @Override
                public void run() {
                    ((Button) target).setBg(MemoryWindowBody.this.getAtlas().findRegion("card-success"));
                }

            }, scaleTo(1, 1, 0.3f, pow2),
                    repeat(3, sequence(scaleTo(1.5f, 1.5f, 0.1f, pow2), scaleTo(1, 1, 0.2f, pow2)))));
        }

        this.addAction(sequence(delay(1.6f), new RunnableAction() {
            @Override
            public void run() {
                if (MemoryWindowBody.this.win) {
                    MemoryWindowBody.this.success();
                } else {
                    MemoryWindowBody.this.fail();
                }
            }
        }));
    }

    @Override
    protected void show() {
        float step = (this.getWidth() - 50 - cardWidth) / colCount;
        int startX = 25 + (int) step / 2;
        int startY = (int) (step - cardHeight) / 2 - 50;

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
    }
}
