package ua.com.tlftgames.waymc.screen.ui.window;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.Settings;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;

public class SettingsWindow extends Window {
    private Slider musicSlider;
    private Slider soundSlider;
    private String startLang;
    private TextButton enBtn;
    private TextButton ruBtn;
    private TextButton ukBtn;
    private TextButton introBtn;
    private TextButton tutorialBtn;
    private Label langLabel;
    private Label soundLabel;
    private Label musicLabel;

    public SettingsWindow(UIHelper helper) {
        super(helper, 840, 604);
        this.bg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsWindow.this.hide();
            }
        });
        this.addMusicLevel();
        this.addSoundLevel();
        this.addIntroEnabled();
        this.addTutorialEnabled();
        this.addLangChoice();
        this.setBottomButtons(this.getHelper().createReturnButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsWindow.this.hide();
            }
        }));
    }

    private void addTutorialEnabled() {
        tutorialBtn = this.getHelper().createCheckButton("tutorial.enable");
        tutorialBtn.setPosition(this.windowGroup.getWidth() - tutorialBtn.getWidth() - 100, 250);
        if (Settings.getInstance().getTutorialEnable()) {
            tutorialBtn.setTouched(true);
        }
        tutorialBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enable = !Settings.getInstance().getTutorialEnable();
                Settings.getInstance().setTutorialEnable(enable);
                ((TextButton) event.getListenerActor()).setTouched(enable);
            }
        });
        this.windowGroup.addActor(tutorialBtn);
    }

    private void addIntroEnabled() {
        introBtn = this.getHelper().createCheckButton("intro.enable");
        introBtn.setPosition(100, 250);
        if (Settings.getInstance().getIntroEnable()) {
            introBtn.setTouched(true);
        }
        introBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enable = !Settings.getInstance().getIntroEnable();
                Settings.getInstance().setIntroEnable(enable);
                ((TextButton) event.getListenerActor()).setTouched(enable);
            }
        });
        this.windowGroup.addActor(introBtn);
    }

    public boolean isLangChanged() {
        return !this.startLang.contentEquals(Settings.getInstance().getLang());
    }

    private void addLangChoice() {
        langLabel = new Label(Translator.getInstance().translate("lang.choice"), Config.getInstance().headerStyle);
        langLabel.setBounds((this.windowGroup.getWidth() - 400) / 2, 160, 400, 75);
        langLabel.setAlignment(Align.center);
        this.windowGroup.addActor(langLabel);

        /*enBtn = this.getHelper().createCheckButton("lang.en");
        enBtn.setPosition(this.windowGroup.getWidth() / 2 - enBtn.getWidth() - 200, 125);
        if (Settings.getInstance().getLang().contentEquals("en")) {
            enBtn.setTouchable(Touchable.disabled);
            enBtn.setTouched(true);
        }
        enBtn.setUserObject("en");
        enBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsWindow.this.changeLang((String) event.getListenerActor().getUserObject());
            }
        });
        this.windowGroup.addActor(enBtn);*/

        ruBtn = this.getHelper().createCheckButton("lang.ru");
        //ruBtn.setPosition((this.windowGroup.getWidth() - ruBtn.getWidth()) / 2, 125);
        ruBtn.setPosition(this.windowGroup.getWidth() / 2 - ruBtn.getWidth() - 100, 125);
        if (Settings.getInstance().getLang().contentEquals("ru")) {
            ruBtn.setTouchable(Touchable.disabled);
            ruBtn.setTouched(true);
        }
        ruBtn.setUserObject("ru");
        ruBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsWindow.this.changeLang((String) event.getListenerActor().getUserObject());
            }
        });
        this.windowGroup.addActor(ruBtn);

        ukBtn = this.getHelper().createCheckButton("lang.uk");
        ukBtn.setPosition(this.windowGroup.getWidth() / 2 + 100, 125);
        if (Settings.getInstance().getLang().contentEquals("uk")) {
            ukBtn.setTouchable(Touchable.disabled);
            ukBtn.setTouched(true);
        }
        ukBtn.setUserObject("uk");
        ukBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsWindow.this.changeLang((String) event.getListenerActor().getUserObject());
            }
        });
        this.windowGroup.addActor(ukBtn);
    }

    private void addSoundLevel() {
        soundLabel = new Label(Translator.getInstance().translate("sound.volume"), Config.getInstance().headerStyle);
        soundLabel.setBounds((this.windowGroup.getWidth() - 400) / 2, 375, 400, 75);
        soundLabel.setAlignment(Align.center);
        this.windowGroup.addActor(soundLabel);
        soundSlider = this.createSlider();
        soundSlider.setPosition(60, 340);
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                Settings.getInstance().setSoundVolume(slider.getValue());
            }
        });
        this.windowGroup.addActor(soundSlider);
    }

    private void addMusicLevel() {
        musicLabel = new Label(Translator.getInstance().translate("music.volume"), Config.getInstance().headerStyle);
        musicLabel.setBounds((this.windowGroup.getWidth() - 400) / 2, 500, 400, 75);
        musicLabel.setAlignment(Align.center);
        this.windowGroup.addActor(musicLabel);
        musicSlider = this.createSlider();
        musicSlider.setPosition(60, 465);
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                Settings.getInstance().setMusicVolume(slider.getValue());
                StageScreen.getInstance().getStage().getMusic().setVolume(slider.getValue());
            }
        });
        this.windowGroup.addActor(musicSlider);
    }

    private Slider createSlider() {
        NinePatch background = new NinePatch(this.getAtlas().findRegion("slider-bg"), 20, 20, 20, 20);
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle(new NinePatchDrawable(background),
                new TextureRegionDrawable(this.getAtlas().findRegion("slider-knob")));
        Slider slider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
        slider.setWidth(this.windowGroup.getWidth() - 120);
        return slider;
    }

    public void changeLang(String lang) {
        Settings.getInstance().setLang(lang);
        Translator.getInstance().updateVocab(lang);
        soundLabel.setText(Translator.getInstance().translate("sound.volume"));
        musicLabel.setText(Translator.getInstance().translate("music.volume"));
        langLabel.setText(Translator.getInstance().translate("lang.choice"));
        introBtn.setText(Translator.getInstance().translate("intro.enable"));
        tutorialBtn.setText(Translator.getInstance().translate("tutorial.enable"));
        /*if (lang.contentEquals("en")) {
            enBtn.setTouchable(Touchable.disabled);
            enBtn.setTouched(true);
            ruBtn.setTouchable(Touchable.enabled);
            ruBtn.setTouched(false);
            ukBtn.setTouchable(Touchable.enabled);
            ukBtn.setTouched(false);
        } else */if (lang.contentEquals("ru")) {
            ruBtn.setTouchable(Touchable.disabled);
            ruBtn.setTouched(true);
            //enBtn.setTouchable(Touchable.enabled);
            //enBtn.setTouched(false);
            ukBtn.setTouchable(Touchable.enabled);
            ukBtn.setTouched(false);
        } else {
            ukBtn.setTouchable(Touchable.disabled);
            ukBtn.setTouched(true);
            //enBtn.setTouchable(Touchable.enabled);
            //enBtn.setTouched(false);
            ruBtn.setTouchable(Touchable.enabled);
            ruBtn.setTouched(false);
        }
        this.setTransform(false);
    }

    @Override
    public void show() {
        startLang = Settings.getInstance().getLang();
        musicSlider.setValue(Settings.getInstance().getMusicVolume());
        soundSlider.setValue(Settings.getInstance().getSoundVolume());
        super.show();
    }

    @Override
    public void hide() {
        if (isLangChanged()) {
            this.addAction(sequence(delay(0.5f), run(new Runnable() {
                @Override
                public void run() {
                    StageScreen.getInstance().reloadStage();
                }
            })));
        }
        super.hide();
    }
}
