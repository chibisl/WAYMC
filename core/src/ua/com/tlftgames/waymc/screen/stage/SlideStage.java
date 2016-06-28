package ua.com.tlftgames.waymc.screen.stage;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Manager;
import ua.com.tlftgames.waymc.Settings;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.screen.StageScreen;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class SlideStage extends ReturnHandlingStage {
    public static final int TYPE_INTRO = 0;
    public static final int TYPE_OUTRO = 1;
    private int currentSlide = 0;
    private Slide[] slides;
    private float fadeInDuration = 1f;
    private float delayDuration = 70f;
    private float fadeOutDuration = 0.2f;
    private int type;
    private TextureAtlas atlas;
    private String musicFile;

    public SlideStage(int type) {
        super();
        if (type < SlideStage.TYPE_INTRO) {
            type = SlideStage.TYPE_INTRO;
        }
        if (type > SlideStage.TYPE_OUTRO) {
            type = SlideStage.TYPE_OUTRO;
        }
        this.type = type;
        if (type == SlideStage.TYPE_INTRO) {
            Manager.getInstance().load("img/intro.pack", TextureAtlas.class);
            musicFile = "sound/Past the Edge.mp3";
        } else {
            Manager.getInstance().load("img/outro.pack", TextureAtlas.class);
            musicFile = "sound/On the Shore.mp3";
        }
        Manager.getInstance().load(musicFile, Music.class);
    }

    @Override
    public boolean allLoaded() {
        if (type == SlideStage.TYPE_INTRO) {
            return (Manager.getInstance().isLoaded("img/intro.pack") &&
                Manager.getInstance().isLoaded(musicFile));
        } else {
            return (Manager.getInstance().isLoaded("img/outro.pack") &&
                Manager.getInstance().isLoaded(musicFile));
        }
    }

    public void nextSlide() {
        slides[currentSlide].clearActions();
        slides[currentSlide].addAction(sequence(fadeOut(SlideStage.this.fadeOutDuration), run(new Runnable() {
            @Override
            public void run() {
                SlideStage.this.incCurrentSlide();
                SlideStage.this.showNextSlide();
            }
        })));
    }

    public void incCurrentSlide() {
        this.currentSlide++;
    }

    public void showNextSlide() {
        showNextSlide(false);
    }

    public void showNextSlide(boolean isCurrentEpigraph) {
        float delay = isCurrentEpigraph ? this.delayDuration / 2 : this.delayDuration;
        if (currentSlide < slides.length) {
            slides[currentSlide].addAction(sequence(fadeIn(this.fadeInDuration), delay(delay), run(new Runnable() {
                @Override
                public void run() {
                    SlideStage.this.nextSlide();
                }
            })));
        } else {
            if (this.type == SlideStage.TYPE_INTRO) {
                StageScreen.getInstance().setStage(new GameStage());
            } else {
                StageScreen.getInstance().setStage(new FinalStage(FinalStage.TYPE_GAME_WIN));
            }
        }
    }

    @Override
    public void start() {
        super.start();
        if (type == SlideStage.TYPE_INTRO) {
            this.atlas = Manager.getInstance().get("img/intro.pack", TextureAtlas.class);
            StageScreen.getInstance().getTracker().trackScreen("introScreen");
        } else {
            GameCore.getInstance().clearProgress();
            this.atlas = Manager.getInstance().get("img/outro.pack", TextureAtlas.class);
            StageScreen.getInstance().getTracker().trackScreen("outroScreen");
        }
        this.music = Manager.getInstance().get(musicFile, Music.class);
        this.music.setVolume(Settings.getInstance().getMusicVolume());
        this.music.setLooping(true);
        this.music.play();

        String pref = (this.type == SlideStage.TYPE_INTRO ? "intro" : "outro");
        int slidesCount = (this.type == SlideStage.TYPE_INTRO ? 5 : 4);
        this.slides = new Slide[slidesCount];
        for (int i = 0; i < slidesCount; i++) {
            if (this.type == SlideStage.TYPE_INTRO && i == 0) {
                this.slides[i] = new EpigraphSlide(Translator.getInstance().translate("epigraph.text"),
                        Translator.getInstance().translate("epigraph.author"));
            } else {
                this.slides[i] = new Slide(Translator.getInstance().translate(pref + "." + i), pref + "-" + i);
            }
            this.addActor(this.slides[i]);
        }
        this.addCaptureListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SlideStage.this.nextSlide();
            }
        });
        this.showNextSlide(true);
    }

    private class Slide extends Group {
        public Slide() {
            this.setBounds(0, 0, Config.getInstance().gameWidth, Config.getInstance().gameHeight);
        }

        public Slide(String text, String texture) {
            this();
            if (SlideStage.this.atlas != null) {
                Array<TextureAtlas.AtlasRegion> regions = SlideStage.this.atlas.findRegions(texture);
                int imageX = 20;
                for (TextureAtlas.AtlasRegion region : regions) {
                    Image slideImage = new Image(region);
                    slideImage.setPosition(imageX, this.getHeight() / 2);
                    this.addActor(slideImage);
                    imageX += region.getRegionWidth();
                }
            }
            Label label = new Label(text, Config.getInstance().normalStyle);
            label.setBounds(20, 20, this.getWidth() - 40, this.getHeight() / 2 - 30);
            label.setAlignment(Align.topLeft);
            label.setWrap(true);
            this.addActor(label);
            this.getColor().a = 0f;
        }
    }

    private class EpigraphSlide extends Slide {

        public EpigraphSlide(String text, String author) {
            super();
            Label label = new Label(text, Config.getInstance().epigraphStyle);
            label.setWrap(true);
            label.setAlignment(Align.left);
            label.setBounds(50, 50, this.getWidth() - 100, this.getHeight() - 100);
            this.addActor(label);

            Label labelAuthor = new Label(author, Config.getInstance().headerStyle);
            labelAuthor.setAlignment(Align.right);
            labelAuthor.setBounds(50, 50, this.getWidth() - 100, this.getHeight() / 2);
            this.addActor(labelAuthor);
            this.getColor().a = 0f;
        }
    }

    @Override
    public void dispose() {
        this.music.stop();
        this.music.dispose();
        Manager.getInstance().unload(musicFile);
        if (type == SlideStage.TYPE_INTRO) {
            Manager.getInstance().unload("img/intro.pack");
        }
        super.dispose();
    }

}
