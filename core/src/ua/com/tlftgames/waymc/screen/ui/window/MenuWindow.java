package ua.com.tlftgames.waymc.screen.ui.window;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.stage.MenuStage;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;

public class MenuWindow extends Window {
    private SettingsWindow optionWindow;

    public MenuWindow(UIHelper helper, SettingsWindow settings) {
        super(helper, 450, 540);
        this.optionWindow = settings;
        addResumeBtn();
        addOptionBtn();
        addExitBtn();

        this.bg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuWindow.this.hide();
            }
        });
    }

    public SettingsWindow getOptionWindow() {
        return this.optionWindow;
    }

    private void addOptionBtn() {
        TextButton optionBtn = this.getHelper().createMenuButton("game.menu.option");
        optionBtn.setBounds(75, 250, 300, 75);
        optionBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuWindow.this.hide();
                MenuWindow.this.getOptionWindow().show();
            }
        });
        this.windowGroup.addActor(optionBtn);
    }

    private void addExitBtn() {
        TextButton exitBtn = this.getHelper().createMenuButton("game.menu.exit");
        exitBtn.setBounds(75, 80, 300, 75);
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageScreen.getInstance().setStage(new MenuStage());
            }
        });
        this.windowGroup.addActor(exitBtn);
    }

    private void addResumeBtn() {
        TextButton resumeBtn = this.getHelper().createMenuButton("game.menu.resume");
        resumeBtn.setBounds(75, 400, 300, 75);
        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuWindow.this.hide();
            }
        });
        this.windowGroup.addActor(resumeBtn);
    }
}
