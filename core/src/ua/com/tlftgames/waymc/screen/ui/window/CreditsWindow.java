package ua.com.tlftgames.waymc.screen.ui.window;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.screen.ui.ScrollPane;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;

public class CreditsWindow extends Window {

    public CreditsWindow(UIHelper helper) {
        super(helper, 840, 604);
        StringBuilder creditsText = new StringBuilder();
        creditsText.append("[#").append(Config.getInstance().textColor.toString()).append("]")
                .append(Translator.getInstance().translate("credits.text"));
        Label creditsLabel = new Label(creditsText, Config.getInstance().colorStyle);
        creditsLabel.setWrap(true);
        creditsLabel.setAlignment(Align.top);
        ScrollPane pane = helper.createScrollPane(creditsLabel);
        pane.setBounds(30, 90, 780, 484);
        this.windowGroup.addActor(pane);
        this.setBottomButtons(this.getHelper().createReturnButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CreditsWindow.this.hide();
            }
        }));
    }
}
