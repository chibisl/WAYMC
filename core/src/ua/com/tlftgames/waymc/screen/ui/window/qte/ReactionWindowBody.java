package ua.com.tlftgames.waymc.screen.ui.window.qte;

import static com.badlogic.gdx.math.Interpolation.pow2;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.touchable;

import java.util.ArrayList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
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

public class ReactionWindowBody extends QTEWindowBody {
    private int cardWidth = 120;
    private int cardHeight = 120;
    private ArrayList<Button> cards;
    private float time = 0;
    private float timeOut = 0.7f;
    private float fullTime = 0;
    private float fullTimeOut = 8f;
    private float waitTime = 1.5f;
    private int rowCount = 4;
    private int colCount = 4;
    private int cardCount = 0;
    private boolean run = false;
    private boolean win = true;
    private boolean lastSuccess = false;
    private int lifeDecrease = -3;
    private int cardShowCount = 1;
    private Image timer;
    private float[] timeOuts = { 0.48f, 0.56f, 0.68f };
    private float[] mobileTimeOuts = { 0.39f, 0.42f, 0.45f };

    public ReactionWindowBody(PlaceWindowManager manager, int difficultLevel) {
        super(manager, difficultLevel, "qte.reaction");

        boolean isMobile = Gdx.app.getType() == Application.ApplicationType.Android
                || Gdx.app.getType() == Application.ApplicationType.iOS;

        if (isMobile) {
            waitTime = 1.2f;
        }
        cardCount = rowCount * colCount;
        cardShowCount += difficultLevel;
        cards = new ArrayList<Button>(cardCount);
        timeOut = isMobile ? mobileTimeOuts[difficultLevel] : timeOuts[difficultLevel];
        fullTimeOut -= difficultLevel;
    }

    private Button createCard(float x, float y) {
        Button card = new Button(this.getAtlas().findRegion("card-empty"),
                this.getAtlas().findRegion("card-empty-touched"));
        card.setOrigin(Align.center);
        card.setSize(cardWidth, cardHeight);
        card.setPosition(x, y);
        card.setTouchable(Touchable.disabled);

        return card;
    }

    public void act(float delta) {
        super.act(delta);
        if (!run) {
            return;
        }
        time += delta;
        fullTime += delta;
        this.updateTimer();
        if (time > timeOut) {
            time = 0;
            for (int i = 0; i < cardShowCount; i++) {
                this.showRandomCard();
            }
        }

        if (fullTime > fullTimeOut) {
            stop();
        }
    }

    private void showRandomCard() {
        if (cards.isEmpty()) {
            return;
        }
        boolean isSuccess = isSuccess();
        Button card = getRandomCard();
        card.clearActions();
        card.clearListeners();
        card.addAction(isSuccess ? this.getSuccessActions() : this.getFailActions());

        card.addListener(isSuccess ? this.getSuccessListener() : this.getFailListener());
    }

    private void updateTimer() {
        timer.setScale(Math.max((fullTimeOut - fullTime), 0) / fullTimeOut, 1);
    }

    private boolean isSuccess() {
        lastSuccess = lastSuccess ? Math.random() < 0.5f : true;
        return lastSuccess;
    }

    private Button getRandomCard() {
        int cardIndex = (int) (Math.random() * cards.size());
        Button card = cards.get(cardIndex);
        cards.remove(cardIndex);
        return card;
    }

    private void addCardToStack(Button card) {
        this.cards.add(card);
    }

    private SequenceAction getSuccessActions() {
        return sequence(scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

            @Override
            public void run() {
                ((Button) target).setBg(ReactionWindowBody.this.getAtlas().findRegion("card-success"));
            }

        }, scaleTo(1, 1, 0.3f, pow2), touchable(Touchable.enabled), delay(this.waitTime), touchable(Touchable.disabled),
                new RunnableAction() {

                    @Override
                    public void run() {
                        ReactionWindowBody.this.win = false;
                        GameCore.getInstance().addLife(ReactionWindowBody.this.lifeDecrease);
                    }

                }, repeat(3, sequence(scaleTo(1.5f, 1.5f, 0.1f, pow2), scaleTo(1, 1, 0.2f, pow2))),
                scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

                    @Override
                    public void run() {
                        ((Button) target).setBg(ReactionWindowBody.this.getAtlas().findRegion("card-empty"));
                    }

                }, scaleTo(1, 1, 0.3f, pow2), new RunnableAction() {

                    @Override
                    public void run() {
                        ReactionWindowBody.this.addCardToStack((Button) target);
                    }

                });
    }

    private SequenceAction getFailActions() {
        return sequence(scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

            @Override
            public void run() {
                ((Button) target).setBg(ReactionWindowBody.this.getAtlas().findRegion("card-fail"));
            }

        }, scaleTo(1, 1, 0.3f, pow2), touchable(Touchable.enabled), delay(1f), touchable(Touchable.disabled),
                scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

                    @Override
                    public void run() {
                        ((Button) target).setBg(ReactionWindowBody.this.getAtlas().findRegion("card-empty"));
                    }

                }, scaleTo(1, 1, 0.3f, pow2), new RunnableAction() {

                    @Override
                    public void run() {
                        ReactionWindowBody.this.addCardToStack((Button) target);
                    }

                });
    }

    private ClickListener getSuccessListener() {
        return new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean isTouched = super.touchDown(event, x, y, pointer, button);
                if (isTouched) {
                    Button card = (Button) event.getTarget();
                    card.clearActions();
                    card.addAction(sequence(touchable(Touchable.disabled), scaleTo(0.01f, 1, 0.3f, pow2),
                            new RunnableAction() {

                                @Override
                                public void run() {
                                    ((Button) target)
                                            .setBg(ReactionWindowBody.this.getAtlas().findRegion("card-empty"));
                                }

                            }, scaleTo(1, 1, 0.3f, pow2), new RunnableAction() {

                                @Override
                                public void run() {
                                    ReactionWindowBody.this.addCardToStack((Button) target);
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
                card.clearActions();
                card.addAction(sequence(new RunnableAction() {

                    @Override
                    public void run() {
                        ReactionWindowBody.this.win = false;
                        GameCore.getInstance().addLife(ReactionWindowBody.this.lifeDecrease);
                    }

                }, repeat(3, sequence(scaleTo(1.5f, 1.5f, 0.1f, pow2), scaleTo(1, 1, 0.2f, pow2))),
                        scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

                            @Override
                            public void run() {
                                ((Button) target).setBg(ReactionWindowBody.this.getAtlas().findRegion("card-empty"));
                            }

                        }, scaleTo(1, 1, 0.3f, pow2), new RunnableAction() {

                            @Override
                            public void run() {
                                ReactionWindowBody.this.addCardToStack((Button) target);
                            }

                        }));
            }
        };
    }

    private void stop() {
        run = false;
        for (Button card : cards) {
            card.clearActions();
            card.clearListeners();
            card.setTouchable(Touchable.disabled);
        }

        this.addAction(sequence(delay(2.4f), new RunnableAction() {
            @Override
            public void run() {
                if (ReactionWindowBody.this.win) {
                    ReactionWindowBody.this.success();
                } else {
                    ReactionWindowBody.this.fail();
                }
            }
        }));
    }

    @Override
    protected void show() {
        float step = (this.getWidth() - 60 - cardWidth) / colCount;
        int startX = 30 + (int) step / 2;
        int startY = (int) (step - cardHeight) / 2 - 70;

        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++) {
                float x = col * step + startX;
                float y = row * step + startY;
                Button card = this.createCard(x, y);
                this.addActor(card);
                cards.add(card);
            }

        timer = new Image(this.getAtlas().findRegion("metro-line"));
        timer.setBounds(25, this.getHeight() + 200, this.getWidth() - 50, 10);
        timer.setOrigin(Align.center);
        this.addActor(timer);
        this.addAction(sequence(delay(0.6f), new RunnableAction() {
            @Override
            public void run() {
                ReactionWindowBody.this.run = true;
            }
        }));
    }
}
