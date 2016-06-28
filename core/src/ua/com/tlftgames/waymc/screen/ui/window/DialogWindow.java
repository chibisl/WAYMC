package ua.com.tlftgames.waymc.screen.ui.window;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.screen.ui.Button;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;

public class DialogWindow extends Window {
    private Label text;
    public DialogWindow(UIHelper helper, String text, final Runnable okAction, final Runnable cancelAction) {
        super(helper, 600, 250);
        this.text = new Label(text, Config.getInstance().normalStyle);
        this.text.setAlignment(Align.topLeft);
        this.text.setWrap(true);
        this.text.setBounds(30, 124, this.windowWidth - 60, this.windowHeight - 154);
        this.windowGroup.addActor(this.text);

        this.setBottomButtons(new Button[] { this.getHelper().createButton("ok", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (okAction != null) okAction.run();
                        DialogWindow.this.hide();
                    }
                }),
                null,
                this.getHelper().createButton("cancel", new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (cancelAction != null) cancelAction.run();
                        DialogWindow.this.hide();
                    }
                })
        });
    }

    @Override
    protected void afterHide() {
        this.clear();
        this.remove();
    }
}
