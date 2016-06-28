package ua.com.tlftgames.waymc.screen.ui.window;

import com.badlogic.gdx.scenes.scene2d.Group;

import ua.com.tlftgames.waymc.screen.ui.Button;

public class WindowBottomButtons extends Group {
    private Button[] buttons;
    private final int btnCount = 3;
    private final int leftBtn = 0;
    private final int centerBtn = 1;
    private final int rightBtn = 2;

    public WindowBottomButtons() {
        super();
        this.setBounds(30, 26, 0, 0);
    }

    @Override
    protected void setParent(Group parent) {
        super.setParent(parent);
        this.setBounds(30, 26, parent.getWidth() - 60, 64);
        this.updateButtons();
    }

    public void setButtons(Button[] buttons) {
        this.clear();
        if (buttons == null) {
            this.buttons = null;
        } else {
            this.buttons = new Button[btnCount];
            for (int i = 0; i < btnCount; i++) {
                if (buttons.length > i)
                    this.buttons[i] = buttons[i];
            }
        }
        this.updateButtons();
    }

    private void updateButtons() {
        if (buttons == null)
            return;
        for (int i = 0; i < btnCount; i++) {
            if (buttons[i] == null)
                continue;
            switch (i) {
            case leftBtn:
                buttons[i].setX(0);
                break;
            case centerBtn:
                buttons[i].setX((this.getWidth() - buttons[i].getWidth()) / 2);
                break;
            case rightBtn:
                buttons[i].setX(this.getWidth() - buttons[i].getWidth());
                break;
            }
            this.addActor(buttons[i]);
        }
    }
}
