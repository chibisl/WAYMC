package ua.com.tlftgames.waymc.screen.stage;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Manager;
import ua.com.tlftgames.waymc.Settings;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.ui.TextButton;

public class FinalStage extends ReturnHandlingStage {
    public final static int TYPE_GAME_OVER = 0;
    public final static int TYPE_GAME_WIN = 1;
    private final int btnHeight = 80;
    private String musicFile;
    private int type;

    public FinalStage(int type) {
        GameCore.getInstance().clearProgress();
        if (type < FinalStage.TYPE_GAME_OVER)
            type = FinalStage.TYPE_GAME_OVER;
        if (type > FinalStage.TYPE_GAME_WIN)
            type = FinalStage.TYPE_GAME_WIN;
        this.type = type;
        Manager.getInstance().load("img/menu.pack", TextureAtlas.class);
        if (this.type == FinalStage.TYPE_GAME_OVER)
            musicFile = "sound/Wounded.mp3";
        else
            musicFile = "sound/Reawakening.mp3";
        Manager.getInstance().load(musicFile, Music.class);
    }

    @Override
    public boolean allLoaded() {
        return (Manager.getInstance().isLoaded("img/menu.pack") && Manager.getInstance().isLoaded(musicFile));
    }

    @Override
    public void start() {
        super.start();
        if (this.type == FinalStage.TYPE_GAME_OVER) {
            StageScreen.getInstance().getTracker().trackScreen("gameOverScreen");
        } else {
            StageScreen.getInstance().getTracker().trackScreen("gameWinScreen");
        }
        this.music = Manager.getInstance().get(musicFile, Music.class);
        this.music.setVolume(Settings.getInstance().getMusicVolume());
        this.music.play();

        TextureAtlas atlas = Manager.getInstance().get("img/menu.pack", TextureAtlas.class);
        LabelStyle headerStyle = new LabelStyle();
        headerStyle.font = Config.getInstance().headerFont;
        headerStyle.fontColor = Config.getInstance().textColor;

        String labelText = (type == FinalStage.TYPE_GAME_OVER) ? "game.over" : "game.win";
        Label text = new Label(Translator.getInstance().translate(labelText), headerStyle);
        text.setBounds(50, 150, Config.getInstance().gameWidth - 100, Config.getInstance().gameHeight - 150);
        text.setAlignment(Align.center);
        text.setWrap(true);
        text.getColor().a = 0;
        text.addAction(fadeIn(2f));
        this.addActor(text);

        AtlasRegion btnBg = atlas.findRegion("btn-bg");
        AtlasRegion btnBgTouched = atlas.findRegion("btn-bg-touched");
        Color btnColor = new Color(1, 1, 1, 1);
        Color btnColorTouched = new Color(0.5f, 0.5f, 0.5f, 1);

        TextButton newGame = new TextButton(btnBg, btnBgTouched, Translator.getInstance().translate("btn.new.game"),
                Config.getInstance().bigFont, btnColor, btnColorTouched);
        newGame.setHeight(btnHeight);
        newGame.setPosition(50, 50);
        newGame.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Settings.getInstance().getIntroEnable()) {
                    StageScreen.getInstance().setStage(new SlideStage(SlideStage.TYPE_INTRO));
                } else {
                    StageScreen.getInstance().setStage(new GameStage());
                }
            }
        });
        this.addActor(newGame);

        TextButton exit = new TextButton(btnBg, btnBgTouched, Translator.getInstance().translate("btn.exit"),
                Config.getInstance().bigFont, btnColor, btnColorTouched);
        exit.setHeight(btnHeight);
        exit.setPosition(Config.getInstance().gameWidth - exit.getWidth() - 50, 50);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageScreen.getInstance().setStage(new MenuStage());
            }
        });
        this.addActor(exit);
    }

    @Override
    public void dispose() {
        this.music.stop();
        this.music.dispose();
        Manager.getInstance().unload("img/menu.pack");
        Manager.getInstance().unload(musicFile);
        super.dispose();
    }

}
