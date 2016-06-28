package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;

import ua.com.tlftgames.waymc.screen.ui.TextButton;

public class ChoicesWindowBody extends WindowBody {

    public ChoicesWindowBody(ArrayList<TextButton> buttons) {
        super();
        this.addButtons(buttons);
    }

    private void addButtons(ArrayList<TextButton> buttons) {
        int btnCount = buttons.size();
        int btnPadding = (btnCount > 1) ? Math.min(
                (int) (this.getHeight() - btnCount * 60) / (btnCount - 1), 50)
                : 0;
        int bottomPadding = (int) ((this.getHeight() - (btnCount * 60) - (btnCount - 1)
                * btnPadding) / 2);
        int i = 0;
        for (TextButton btn : buttons) {
            btn.setBounds(90, this.getHeight() - bottomPadding - 60 - i
                    * (60 + btnPadding), 600, 60);
            this.addActor(btn);
            i++;
        }
    }
}
