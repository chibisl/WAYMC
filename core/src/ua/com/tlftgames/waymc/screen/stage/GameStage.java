package ua.com.tlftgames.waymc.screen.stage;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.CoolRandomizer;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Manager;
import ua.com.tlftgames.waymc.Settings;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.map.Animator;
import ua.com.tlftgames.waymc.screen.map.Metro;
import ua.com.tlftgames.waymc.screen.map.SkylineLayer;
import ua.com.tlftgames.waymc.screen.map.Station;
import ua.com.tlftgames.waymc.screen.map.World;
import ua.com.tlftgames.waymc.screen.map.WorldScrollPane;
import ua.com.tlftgames.waymc.screen.ui.UIGroup;

import java.util.ArrayList;

public class GameStage extends ReturnHandlingStage {
    public static final int MOVE_SOUND = 0;
    public static final int SMOKE_START_X = 784;
    public static final int SMOKE_START_Y = 585;
    public static final float SMOKE_START_SCALE = 0.05f;
    public static final float SMOKE_START_DELAY = 6.4f;
    public static final float SMOKE_DURATION = 200f;
    private UIGroup uiElements;
    private WorldScrollPane worldScrollPane;
    private ArrayList<Sound> sounds;
    private float cityMgToFg = 0.64f;
    private float cityBgToFg = 0.17f;
    private int skyHeight = 350;
    private Group leftHighAttentionMarker;
    private Group rightHighAttentionMarker;
    private Group leftAttentionMarker;
    private Group rightAttentionMarker;

    public GameStage() {
        Manager.getInstance().load("img/game.pack", TextureAtlas.class);
        Manager.getInstance().load("sound/Despair and Triumph.mp3", Music.class);
        Manager.getInstance().load("sound/move.mp3", Sound.class);
        sounds = new ArrayList<Sound>();
    }

    @Override
    public void start() {
        super.start();
        StageScreen.getInstance().getTracker().trackScreen("gameScreen");
        if (!GameCore.getInstance().hasProgress()) {
            GameCore.getInstance().initProgress();
        } else {
            GameCore.getInstance().loadSavedParams();
        }
        sounds.add(Manager.getInstance().get("sound/move.mp3", Sound.class));

        TextureAtlas atlas = Manager.getInstance().get("img/game.pack", TextureAtlas.class);

        this.addSky();
        this.addCityBg(atlas);
        Group cityMg = this.addCityMg(atlas);

        worldScrollPane = new WorldScrollPane(this, new World(atlas), atlas);
        this.addActor(worldScrollPane);

        for (int i = 0; i < 2000; i++) {
            this.act(SMOKE_START_DELAY / 50);
        }

        Animator.addZeppelin(atlas.findRegion("zeppelin"), cityMg);
        
        if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.WebGL) {
        	this.addScrollRegions();
        }

        this.addAttentionMarkers(atlas);

        uiElements = new UIGroup(atlas, this);
        uiElements.init();
        this.addActor(uiElements);
        if (this.music == null) {
            this.music = Manager.getInstance().get("sound/Despair and Triumph.mp3", Music.class);
            this.music.setVolume(Settings.getInstance().getMusicVolume());
            this.music.setLooping(true);
            this.music.play();
        }
        
        this.setDebugAll(true);
    }

    private void addScrollRegions() {
    	ScrollRegion leftScrollRegion = new ScrollRegion(-1);
		leftScrollRegion.setBounds(0, 0, 80, this.getHeight());
		this.addActor(leftScrollRegion);
		
		ScrollRegion rightScrollRegion = new ScrollRegion(1);
		rightScrollRegion.setBounds(this.getWidth() - 80, 0, 80, this.getHeight());
		this.addActor(rightScrollRegion);
	}

	private void addAttentionMarkers(TextureAtlas atlas) {
        TextureRegion leftMarker = atlas.findRegion("marker-left");
        TextureRegion rightMarker = atlas.findRegion("marker-right");
        TextureRegion highAttention = atlas.findRegion("high-atention");
        TextureRegion attention = atlas.findRegion("atention");

        this.leftAttentionMarker = this.createAttentionMarker(leftMarker, Station.createAttention(attention), Metro.ATTENTION_LEFT);
        leftAttentionMarker.setPosition(20, 460);
        this.addActor(leftAttentionMarker);

        this.rightAttentionMarker = this.createAttentionMarker(rightMarker, Station.createAttention(attention), Metro.ATTENTION_RIGHT);
        rightAttentionMarker.setPosition(this.getWidth() - rightAttentionMarker.getWidth() - 20, 460);
        this.addActor(rightAttentionMarker);

        this.leftHighAttentionMarker = this.createAttentionMarker(leftMarker, Station.createAttention(highAttention),
                Metro.ATTENTION_HIGH_LEFT);
        leftHighAttentionMarker.setPosition(20, 360);
        this.addActor(leftHighAttentionMarker);

        this.rightHighAttentionMarker = this.createAttentionMarker(rightMarker, Station.createAttention(highAttention),
                Metro.ATTENTION_HIGH_RIGHT);
        rightHighAttentionMarker.setPosition(this.getWidth() - rightHighAttentionMarker.getWidth() - 20, 360);
        this.addActor(rightHighAttentionMarker);

        worldScrollPane.updateAttentionMarkers();
    }

    private Group createAttentionMarker(TextureRegion bg, Image attention, final int attentionType) {
        Group attentionMarker = new Group();
        attentionMarker.setSize(65, 50);
        Image bgImage = new Image(bg);
        bgImage.setPosition((attentionType == Metro.ATTENTION_HIGH_LEFT || attentionType == Metro.ATTENTION_LEFT) ?
                0 : attentionMarker.getWidth() - bgImage.getWidth(),
                (attentionMarker.getHeight() - bgImage.getHeight()) / 2);
        attentionMarker.addActor(bgImage);
        attention.setPosition((attentionType == Metro.ATTENTION_HIGH_LEFT || attentionType == Metro.ATTENTION_LEFT) ?
                17 : 2, 2);
        attentionMarker.addActor(attention);
        attentionMarker.setVisible(false);
        attentionMarker.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameStage.this.worldScrollPane.scrollToAttention(attentionType);
            }
        });
        return attentionMarker;
    }

    private void addSky() {
        Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
        pixmap.setColor(new Color(0.7804f, 0.4236f, 0.28f, 1f));
        pixmap.fill();
        Image sky = new Image(new Texture(pixmap));
        sky.setBounds(0, Config.getInstance().gameHeight - skyHeight, Config.getInstance().gameWidth, skyHeight);
        this.addActor(sky);
    }

    private Group addCityBg(TextureAtlas atlas) {
        Group cityBg = new Group() {
            @Override
            public void act(float delta) {
                if (GameStage.this.worldScrollPane != null) {
                    this.setX(-1 * GameStage.this.worldScrollPane.getScrollX() * GameStage.this.cityBgToFg);
                }
                super.act(delta);
            }
        };

        Animator.addSmoke(SMOKE_START_X, SMOKE_START_Y, SMOKE_START_SCALE, SMOKE_START_DELAY, SMOKE_DURATION,
                new CoolRandomizer<AtlasRegion>(atlas.findRegions("smoke-bg"), 1), cityBg);

        SkylineLayer skylineBg = new SkylineLayer(atlas.findRegions("city-bg"));
        cityBg.addActor(skylineBg);
        cityBg.setBounds(0, 0, skylineBg.getWidth(), this.getHeight());
        this.addActor(cityBg);
        return cityBg;
    }

    private Group addCityMg(TextureAtlas atlas) {
        Group cityMg = new Group() {
            @Override
            public void act(float delta) {
                if (GameStage.this.worldScrollPane != null) {
                    this.setX(-1 * GameStage.this.worldScrollPane.getScrollX() * GameStage.this.cityMgToFg);
                }
                super.act(delta);
            }
        };
        SkylineLayer skylineMg = new SkylineLayer(atlas.findRegions("city-mg"));
        cityMg.addActor(skylineMg);
        cityMg.setBounds(0, 0, skylineMg.getWidth(), this.getHeight());

        this.addActor(cityMg);
        return cityMg;
    }

    @Override
    public void dispose() {
        this.music.stop();
        this.music.dispose();
        Manager.getInstance().unload("sound/Despair and Triumph.mp3");
        Manager.getInstance().unload("img/game.pack");
        Manager.getInstance().unload("sound/move.mp3");
        super.dispose();
    }

    public UIGroup getUIGroup() {
        return this.uiElements;
    }

    public World getWorld() {
        return this.worldScrollPane.getWorld();
    }

    public void playSound(int soundIndex) {
        sounds.get(soundIndex).play(Settings.getInstance().getSoundVolume());
    }

    @Override
    public boolean allLoaded() {
        return (Manager.getInstance().isLoaded("img/game.pack")
                && Manager.getInstance().isLoaded("sound/Despair and Triumph.mp3")
                && Manager.getInstance().isLoaded("sound/move.mp3"));
    }

    private float getLeftDistance(float scrollX, float leftHighAttentionX) {
        return leftHighAttentionX > 0 ? scrollX - leftHighAttentionX : -1;
    }

    private float getRightDistance(float scrollX, float rightHighAttentionX) {
        return rightHighAttentionX > 0 ? rightHighAttentionX - scrollX - this.getWidth() + 75 : -1;
    }

    public void updateHighAttentionMarkers(float scrollX, float leftHighAttentionX, float rightHighAttentionX) {
        updateAttentionMarker(this.leftHighAttentionMarker, getLeftDistance(scrollX, leftHighAttentionX));
        updateAttentionMarker(this.rightHighAttentionMarker, getRightDistance(scrollX, rightHighAttentionX));
    }

    public void updateAttentionMarkers(float scrollX, float leftAttentionX, float rightAttentionX) {
        updateAttentionMarker(this.leftAttentionMarker, getLeftDistance(scrollX, leftAttentionX));
        updateAttentionMarker(this.rightAttentionMarker, getRightDistance(scrollX, rightAttentionX));
    }

    private void updateAttentionMarker(Group marker, float distance) {
        if (marker == null)
            return;
        marker.getColor().a = 1;
        marker.setVisible(true);
        if (distance < 0) {
            marker.setVisible(false);
        } else if (distance < 100) {
            marker.getColor().a = distance / 100;
        }

    }
    
    private class ScrollRegion extends Actor {
    	private boolean hover = false;
    	private float speed = 500;
    	private int direction = 1;
    	
    	public ScrollRegion(int direction) {
    		this.direction = direction;
    		this.addListener(new InputListener() {
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                	ScrollRegion.this.setHover(true);
                }
                
                public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                	ScrollRegion.this.setHover(false);
                }
    		});
    	}
    	
    	public void setHover(boolean hover) {
    		this.hover = hover;
    	}
    	
    	public void act (float delta) {
    		if (hover) {
    			WorldScrollPane pane = GameStage.this.worldScrollPane;
    			float x = pane.getScrollX() + direction * speed * delta;
    			pane.setScrollX(x);
    		}
    	}
    }
}
