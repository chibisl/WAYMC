package ua.com.tlftgames.waymc.screen.stage;

import static com.badlogic.gdx.math.Interpolation.exp10;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.CoolRandomizer;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Manager;
import ua.com.tlftgames.waymc.Settings;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.map.Animator;
import ua.com.tlftgames.waymc.screen.ui.Button;
import ua.com.tlftgames.waymc.screen.ui.MenuBackground;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;
import ua.com.tlftgames.waymc.screen.ui.window.CreditsWindow;
import ua.com.tlftgames.waymc.screen.ui.window.SettingsWindow;

public class MenuStage extends ReturnHandlingStage {
    private TextureAtlas atlas;
    private SettingsWindow settings;
    private CreditsWindow credits;
    private boolean needAnimation = false;
    private UIHelper helper;
    public static final int SMOKE_START_X = 271;
    public static final int SMOKE_START_Y = 538;
    public static final float SMOKE_START_SCALE = 0.055f;
    public static final float SMOKE_START_DELAY = 2.65f;
    public static final float SMOKE_DURATION = 100f;
    private int startY = 134;

    public MenuStage(boolean needAnimation) {
        this.needAnimation = needAnimation;
        Manager.getInstance().load("img/menu.pack", TextureAtlas.class);
        Manager.getInstance().load("sound/Willow and the Light.mp3", Music.class);
    }

    public MenuStage() {
        this(false);
    }

    @Override
    public void start() {
        super.start();
        StageScreen.getInstance().getTracker().trackScreen("menuScreen");
        this.getRoot().setBounds(0, 0, this.getWidth(), this.getHeight());
        this.atlas = Manager.getInstance().get("img/menu.pack", TextureAtlas.class);
        this.helper = new UIHelper(this.atlas);
        float screenX = this.getWidth() - Config.getInstance().gameNeedWidth;
        screenX = screenX < 0 ? screenX * 0.85f : screenX / 2;
        MenuBackground bg = new MenuBackground(this.atlas.findRegions("bg"), screenX);
        this.addActor(bg);
        addSmoke(screenX);
        addCooper(screenX);
        addNewGameBtn();
        addExitBtn();
        addOptionBtn();
        addInfoBtn();
        if (GameCore.getInstance().hasProgress()) {
            addContinueBtn();
        }
        addSettingsWindow();
        addCreditsWindow();
        if (this.music == null) {
            this.music = Manager.getInstance().get("sound/Willow and the Light.mp3", Music.class);
            this.music.setVolume(Settings.getInstance().getMusicVolume());
            this.music.setLooping(false);
            this.music.play();
        }
        this.needAnimation = false;
    }

    private void addCreditsWindow() {
        credits = new CreditsWindow(this.helper);
        this.addActor(credits);
    }

    public SettingsWindow getSettings() {
        return this.settings;
    }

    public CreditsWindow getCredits() {
        return this.credits;
    }

    private void addSmoke(float screenX) {
        Group smokeGroup = new Group();
        smokeGroup.setBounds(0, 0, this.getWidth(), this.getHeight());
        this.addActor(smokeGroup);
        Animator.addSmoke(screenX + SMOKE_START_X, SMOKE_START_Y, SMOKE_START_SCALE, SMOKE_START_DELAY, SMOKE_DURATION,
                new CoolRandomizer<AtlasRegion>(atlas.findRegions("smoke"), 1), smokeGroup);
        for (int i = 0; i < 2000; i++) {
            this.act(SMOKE_START_DELAY / 50);
        }
    }

    private void addCooper(float screenX) {
        Image cooper = new Image(this.atlas.findRegion("cooper"));
        cooper.setPosition(screenX < 0 ? screenX * 0.6f : screenX, 0);
        this.addActor(cooper);

    }

    private void addContinueBtn() {
        TextButton continueBtn = this.helper.createMenuButton(Translator.getInstance().translate("menu.continue"));
        continueBtn.setWidth(300);
        continueBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageScreen.getInstance().setStage(new GameStage());
            }
        });
        continueBtn.setX(this.getWidth() / 2 - 150);
        if (needAnimation) {
            continueBtn.setY(-100);
            continueBtn.addAction(moveTo(continueBtn.getX(), 250, 0.5f, exp10));
        } else {
            continueBtn.setY(250);
        }
        this.addActor(continueBtn);
    }

    protected void startNewGame() {
        GameCore.getInstance().clearProgress();
        if (Settings.getInstance().getIntroEnable()) {
            StageScreen.getInstance().setStage(new SlideStage(SlideStage.TYPE_INTRO));
        } else {
            StageScreen.getInstance().setStage(new GameStage());
        }
    }

    private void addNewGameBtn() {
        TextButton startBtn = this.helper.createMenuButton(Translator.getInstance().translate("menu.newgame"));
        startBtn.setWidth(300);
        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameCore.getInstance().hasProgress()) {
                    MenuStage.this.helper.openDialog(MenuStage.this.getRoot(),
                            Translator.getInstance().translate("menu.newgame.confirm"), new Runnable() {
                        @Override
                        public void run() {
                            MenuStage.this.startNewGame();
                        }
                    }, null);
                } else {
                    MenuStage.this.startNewGame();
                }
            }
        });
        startBtn.setX(this.getWidth() / 2 - 150);
        if (needAnimation) {
            startBtn.setY(-300);
            startBtn.addAction(moveTo(startBtn.getX(), 50, 0.5f, exp10));
        } else {
            startBtn.setY(50);
        }
        this.addActor(startBtn);
    }

    private void addExitBtn() {
        if (Gdx.app.getType() == ApplicationType.WebGL) {
            this.startY = 0;
            return;
        }
        Button exitBtn = this.helper.createButton("exit", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        if (needAnimation) {
            exitBtn.setPosition(this.getWidth() + 100, 30);
            exitBtn.addAction(sequence(delay(0.1f), moveTo(this.getWidth() - 94, 30, 0.5f, exp10)));
        } else {
            exitBtn.setPosition(this.getWidth() - 94, 30);
        }
        this.addActor(exitBtn);
    }

    private void addOptionBtn() {
        Button optionBtn = this.helper.createButton("options", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuStage.this.getSettings().show();
            }
        });
        int y = 30 + startY;
        if (needAnimation) {
            optionBtn.setPosition(this.getWidth() + 100, y);
            optionBtn.addAction(sequence(delay(0.2f), moveTo(this.getWidth() - 94, y, 0.5f, exp10)));
        } else {
            optionBtn.setPosition(this.getWidth() - 94, y);
        }
        this.addActor(optionBtn);
    }

    private void addSettingsWindow() {
        settings = new SettingsWindow(this.helper);
        this.addActor(settings);
    }

    private void addInfoBtn() {
        Button infoBtn = this.helper.createButton("info", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MenuStage.this.getCredits().show();
            }
        });
        int y = 164 + startY;
        if (needAnimation) {
            infoBtn.setPosition(this.getWidth() + 100, y);
            infoBtn.addAction(sequence(delay(0.3f), moveTo(this.getWidth() - 94, y, 0.5f, exp10)));
        } else {
            infoBtn.setPosition(this.getWidth() - 94, y);
        }
        this.addActor(infoBtn);
    }

    @Override
    public boolean allLoaded() {
        return (Manager.getInstance().isLoaded("img/menu.pack")
                && Manager.getInstance().isLoaded("sound/Willow and the Light.mp3"));
    }

    @Override
    public void dispose() {
        this.music.stop();
        this.music.dispose();
        Manager.getInstance().unload("img/menu.pack");
        Manager.getInstance().unload("sound/Willow and the Light.mp3");
        super.dispose();
    }
}
