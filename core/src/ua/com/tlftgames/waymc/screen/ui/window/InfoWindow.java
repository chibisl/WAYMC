package ua.com.tlftgames.waymc.screen.ui.window;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;

import java.util.ArrayList;

public class InfoWindow extends Window {
    private WindowBottomButtons actionButtons;
    private Image image;
    private Label infoLabel;
    private Label titleLabel;

    public InfoWindow(UIHelper helper) {
        super(helper, 600, 500);
        this.image = new Image();
        this.windowGroup.addActor(this.image);

        this.infoLabel = new Label("", Config.getInstance().colorStyle);
        this.infoLabel.setAlignment(Align.topLeft);
        this.infoLabel.setWrap(true);
        this.infoLabel.setBounds(30, 124, this.windowWidth - 60, this.windowHeight - 344);
        this.windowGroup.addActor(this.infoLabel);

        this.titleLabel = new Label("", Config.getInstance().headerStyle);
        this.titleLabel.setPosition(30, this.windowHeight - 126);
        this.titleLabel.setWidth(this.windowWidth - 60);
        this.titleLabel.setAlignment(Align.center);
        this.titleLabel.setWrap(true);
        this.windowGroup.addActor(this.titleLabel);

        this.actionButtons = new WindowBottomButtons();
        this.windowGroup.addActor(this.actionButtons);
        this.actionButtons.setPosition(30, 124);

        this.setBottomButtons(this.getHelper().createReturnButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                InfoWindow.this.hide();
            }
        }));
    }

    public void setImage(String imageTitle) {
        TextureRegion imageRegion = this.getAtlas().findRegion(imageTitle);
        image.setDrawable(new TextureRegionDrawable(imageRegion));
        image.setSize(imageRegion.getRegionWidth(), imageRegion.getRegionHeight());
        image.setPosition((this.windowWidth - image.getWidth()) / 2, this.windowHeight - 30 - image.getHeight());
    }

    public void setText(String info, ArrayList<String> vars) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("[#").append(Config.getInstance().textColor.toString()).append("]")
                .append(Translator.getInstance().translate(info));
        String transText = textBuilder.toString();
        if (vars != null) {
            for (String var : vars) {
                StringBuilder varBuilder = new StringBuilder();
                varBuilder.append("[#").append(Config.getInstance().selectColor.toString()).append("]")
                        .append(Translator.getInstance().translate(var)).append("[]");
                String transVar = varBuilder.toString();
                transText = transText.replaceFirst("\\{\\{var\\}\\}", transVar);
            }
        }
        transText = transText.replace("{{var}}", "");
        infoLabel.setText(transText);
    }

    public void setText(String info) {
        this.setText(info, null);
    }

    public void setTitle(String title) {
        String transTitle = Translator.getInstance().translateWithoutArticulos(title);
        transTitle = Character.toUpperCase(transTitle.charAt(0)) + transTitle.substring(1);
        titleLabel.setText(transTitle);
    }

    public void setActions(ArrayList<TextButton> buttons) {
        int buttonsCount = (buttons == null) ? 0 : buttons.size();
        TextButton[] actionButtons = { null, null, null };
        this.setWindowSize(this.windowWidth, buttonsCount == 0 ? 436 : 500);
        switch (buttonsCount) {
        case 0:
            break;
        case 1:
            actionButtons[1] = buttons.get(0);
            break;
        case 2:
            actionButtons[0] = buttons.get(0);
            actionButtons[2] = buttons.get(1);
            break;
        default:
            actionButtons[0] = buttons.get(0);
            actionButtons[1] = buttons.get(1);
            actionButtons[2] = buttons.get(2);
            break;
        }
        this.actionButtons.setButtons(actionButtons);
    }

    private void updateChildrenPosition() {
        this.titleLabel.setY(this.windowHeight - 126);
        this.infoLabel.setY(this.windowHeight - 312);
        this.image.setY(this.windowHeight - 30 - image.getHeight());
    }

    @Override
    public void show() {
        updateChildrenPosition();
        super.show();
        this.toFront();
    }
}
