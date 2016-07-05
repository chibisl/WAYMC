package ua.com.tlftgames.waymc.screen.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.listener.Dispatcher;
import ua.com.tlftgames.waymc.listener.Listener;
import ua.com.tlftgames.waymc.natification.Notification;
import ua.com.tlftgames.waymc.natification.NotificationManager;

import static com.badlogic.gdx.math.Interpolation.exp10;
import static com.badlogic.gdx.math.Interpolation.pow2;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class NotificationPopup extends Group implements Listener {
    private Image image;
    private Label text;
    private UIHelper helper;
    private boolean showed = false;

    public NotificationPopup(UIHelper helper) {
        this.helper = helper;
        this.setSize(500, 100);
        this.setVisible(false);

        Image bg = new Image(new NinePatchDrawable(this.helper.getWindowBg()));
        bg.setBounds(0, 0, this.getWidth(), this.getHeight());
        this.addActor(bg);

        image = new Image();
        image.setBounds(0, 0, 100, 100);
        this.addActor(image);

        text = new Label("", Config.getInstance().normalStyle);
        text.setBounds(100, 20, 400, 60);
        text.setAlignment(Align.left);
        text.setWrap(true);
        this.addActor(text);

        Dispatcher.getInstance().addListener(Dispatcher.EVENT_NEW_NOTIFICATION, this);
    }

    private void showNotification() {
        Notification notification = GameCore.getInstance().getNotificationManager().getNextNotification();
        if (notification != null) {
            image.setDrawable(new TextureRegionDrawable(this.helper.getAtlas().findRegion(notification.getImage())));
            text.setText(Translator.getInstance().translate(notification.getText()));
            float currentY = this.getY();
            this.setY(Config.getInstance().gameHeight + this.getHeight());
            this.setVisible(true);
            this.getColor().a = 1;
            this.addAction(sequence(moveTo(this.getX(), currentY, 0.5f, exp10), delay(1f), fadeOut(2f, pow2),
                    run(new Runnable() {
                        @Override
                        public void run() {
                            NotificationPopup.this.setVisible(false);
                            NotificationManager notificationManager = GameCore.getInstance().getNotificationManager();
                            if (notificationManager != null) {
                                notificationManager.removeNotification();
                                NotificationPopup.this.showNotification();
                            }
                        }
                    })));
        } else {
            showed = false;
        }
    }

    @Override
    public void fireEvent(int event) {
        if (!showed) {
            showed = true;
            showNotification();
        }
    }
}
