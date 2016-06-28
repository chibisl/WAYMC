package ua.com.tlftgames.waymc.screen.ui;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.listener.Dispatcher;
import ua.com.tlftgames.waymc.listener.Listener;

public class MoneyCounter extends Actor implements Listener {
    private TextureRegion bg;
    private BitmapFont font;
    private String money;

    public MoneyCounter(TextureRegion bg, BitmapFont font) {
        this.bg = bg;
        this.setSize(bg.getRegionWidth(), bg.getRegionHeight());
        this.font = font;
        this.font.setColor(0, 0, 0, 1);
        this.money = String.valueOf(GameCore.getInstance().getMoney());
        Dispatcher.getInstance().addListener(Dispatcher.EVENT_MONEY_CHANGE, this);
        this.setPosition(Config.getInstance().gameWidth - this.getWidth(),
                Config.getInstance().gameHeight - this.getHeight());

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(Color.WHITE);
        batch.draw(this.bg, getX(), getY());
        for (int i = 0; i < money.length(); i++) {
            float x = this.getX() + 30 + 28f * (5 - money.length() + i);
            this.font.draw(batch, money.subSequence(i, i + 1), x, getY() + this.getHeight() - 12);
        }
    }

    public int getCurrentMoney() {
        return Integer.parseInt(money);
    }

    public void setCurrentMoney(Integer money) {
        this.money = money.toString();
    }

    @Override
    public void fireEvent(int event) {
        ChangeMoneyAction changeMoneyAction = new ChangeMoneyAction();
        changeMoneyAction.setEndMoney(GameCore.getInstance().getMoney());
        changeMoneyAction.setDuration(0.2f);
        float delay = 0.5f;
        if (((UIGroup) this.getParent()).getStage().getWorld().getPin().hasActions()) {
            delay = 2.5f;
        }
        this.addAction(sequence(delay(delay), changeMoneyAction));
    }

    private class ChangeMoneyAction extends TemporalAction {
        private int end, start;

        @Override
        protected void begin() {
            this.start = ((MoneyCounter) target).getCurrentMoney();
        }

        public void setEndMoney(int end) {
            this.end = end;
        }

        @Override
        protected void update(float percent) {
            ((MoneyCounter) target).setCurrentMoney((int) (start + (end - start) * percent));
        }
    }
}
