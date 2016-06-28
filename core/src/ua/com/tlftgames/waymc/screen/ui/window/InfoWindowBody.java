package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.ui.ScrollPane;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;

public class InfoWindowBody extends WindowBody {
    private ScrollPane pane;

    public InfoWindowBody(String text, ArrayList<String> vars, UIHelper helper) {
        super();
        String infoText = this.replaceVarsAndTranslate(text, vars, true);

        Label info = new Label(infoText, Config.getInstance().colorStyle);
        info.setWidth(this.getWidth());
        info.setAlignment(Align.topLeft);
        info.setWrap(true);
        pane = helper.createScrollPane(info);
        pane.setBounds(0, 10, this.getWidth(), this.getHeight() - 10);
        this.addActor(pane);
    }

    @Override
    public void afterShow() {
        StageScreen.getInstance().getStage().setScrollFocus(pane);
    }

}
