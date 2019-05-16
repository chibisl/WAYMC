package ua.com.tlftgames.waymc.screen.ui.window.qte;

import static com.badlogic.gdx.math.Interpolation.pow2;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.touchable;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.screen.ui.Button;
import ua.com.tlftgames.waymc.screen.ui.window.PlaceWindowManager;

public class LuckWindowBody extends QTEWindowBody {
    private int cardWidth = 120;
    private int cardHeight = 120;
    private ArrayList<Button> cards;
    private ArrayList<Integer> successCardNums;
    private int rowCount = 2;
    private int colCount = 3;
    private int cardCount = 0;

    public LuckWindowBody(PlaceWindowManager manager, int difficultLevel) {
        super(manager, difficultLevel, "qte.luck");
        cardCount = rowCount * colCount;
        cards = new ArrayList<Button>(cardCount);
        successCardNums = new ArrayList<Integer>(cardCount);
    }

    private int getSuccessCardsCount() {
        return cardCount - this.getDifficultLevel() - 1;
    }

    private Button createCard(final int position, float x, float y, float centerX, float centerY) {
        Button card = new Button(
                this.getAtlas().findRegion(position < getSuccessCardsCount() ? "card-success" : "card-fail"),
                this.getAtlas().findRegion("card-empty-touched"));
        card.setOrigin(Align.center);
        card.setSize(cardWidth, cardHeight);
        card.setPosition(x, y);
        card.setTouchable(Touchable.disabled);
        card.addAction(sequence(delay(0.3f), scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

            @Override
            public void run() {
                ((Button) target).setBg(LuckWindowBody.this.getAtlas().findRegion("card-empty"));
            }

        }, scaleTo(1, 1, 0.3f, pow2), moveTo(centerX, centerY, 0.3f, pow2), delay(0.2f), moveTo(x, y, 0.3f, pow2),
                touchable(Touchable.enabled)));

        card.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                LuckWindowBody.this.showCards((Button) event.getTarget());
            }
        });

        return card;
    }

    private void showCards(Button selectedCard) {
        final int selectedPosition = cards.indexOf(selectedCard);
        selectedCard.addAction(sequence(delay(0.2f), scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

            @Override
            public void run() {
                ((Button) target).setBg(LuckWindowBody.this.getAtlas()
                        .findRegion(successCardNums.contains(selectedPosition) ? "card-success" : "card-fail"));
            }

        }, scaleTo(1, 1, 0.3f, pow2), delay(1.1f), new RunnableAction() {
            @Override
            public void run() {
                if (successCardNums.contains(selectedPosition)) {
                    LuckWindowBody.this.success();
                } else {
                    LuckWindowBody.this.fail();
                }
            }
        }));

        for (int i = 0; i < cards.size(); i++) {
            final int position = i;
            Button card = cards.get(position);
            card.setTouchable(Touchable.disabled);
            if (card.equals(selectedCard)) {
                continue;
            }
            card.addAction(sequence(delay(0.8f), scaleTo(0.01f, 1, 0.3f, pow2), new RunnableAction() {

                @Override
                public void run() {
                    ((Button) target).setBg(LuckWindowBody.this.getAtlas()
                            .findRegion(successCardNums.contains(position) ? "card-success" : "card-fail"));
                }

            }, scaleTo(1, 1, 0.3f, pow2)));
        }
    }

    @Override
    protected void show() {
        float step = this.getWidth() / colCount;

        float startX = (step - cardWidth) / 2;
        float startY = (this.getHeight() - cardHeight) / 2 - 50;

        float centerX = (this.getWidth() - cardWidth) / 2;
        float centerY = startY + step / 2;

        for (int row = 0; row < rowCount; row++)
            for (int col = 0; col < colCount; col++) {
                int position = row * colCount + col;
                float x = col * step + startX;
                float y = startY + row * step;
                Button card = this.createCard(position, x, y, centerX, centerY);
                this.addActor(card);
                cards.add(card);
                successCardNums.add(position);
            }

        Collections.shuffle(successCardNums);
        successCardNums.subList(0, this.getDifficultLevel() + 1).clear();
    }
}
