package ua.com.tlftgames.waymc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class Config {
    public int gameWidth;
    public int gameHeight = 720;
    public int gameNeedWidth = 1280;
    public float screenRatio;

    public final int ticketPrice = 5;
    public final int startMoney = 100;
    public final int maxLife = 20;
    public final int crimeLevel = 0;
    public final int crimeSubLife = 3;
    public final int allCrimeLevel = 10;
    public final int restAddLife = 12;
    public final int restCheapAddLife = 5;
    public final int workAddMoney = 60;
    public final int workSubLife = 4;
    public final int startRestCost = 40;
    public final int searchCount = 15;
    public final int[] searchTypeIndexes = { 2, 6, 13 };
    public final int[] searchNeedMoneyIndexes = { 3 };
    public final int searchNeedMoneySum = 50;
    public final int[] searchNeedItemIndexes = { 12 };
    public final String searchNeedItemName = "sprayer";
    public final int itemsMaxCount = 7;
    public final float saleLostPercent = 0.2f;
    public final int clubEnterPrice = 10;
    public final int racesBet = 20;
    public final int casinoMaxWin = 100;
    public final int casinoMinWin = 10;
    public final int startMaterialsCount = 4;
    public final int startReceiptsCount = 1;
    public final int moveDistance = 1;
    public final int runLostMoney = 30;
    public final int playClubCost = 10;
    public final int playClubWin = 100;

    public BitmapFont normalFont;
    public BitmapFont italicFont;
    public BitmapFont colorFont;
    public BitmapFont headerFont;
    public BitmapFont headerItalicFont;
    public BitmapFont stationFont;
    public BitmapFont moneyFont;
    public BitmapFont bigFont;
    public BitmapFont colorBoldFont;
    public LabelStyle normalStyle;
    public LabelStyle headerStyle;
    public LabelStyle colorStyle;
    public LabelStyle stationStyle;
    public LabelStyle stationTouchedStyle;
    public LabelStyle colorBoldStyle;
    public LabelStyle epigraphStyle;
    public Color textColor;
    public Color btnColor;
    public Color btnTouchedColor;
    public Color selectColor;
    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private Config() {

        screenRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
        gameWidth = (int) (gameHeight * screenRatio);

        Texture textureNormal = this.getFontTexture("normal");
        normalFont = this.getFont("normal", textureNormal);
        colorFont = this.getFont("normal", textureNormal);
        colorFont.getData().markupEnabled = true;
        italicFont = this.getFont("italic");
        Texture textureBold = this.getFontTexture("bold");
        colorBoldFont = this.getFont("bold", textureBold);
        colorBoldFont.getData().markupEnabled = true;
        colorBoldFont.getData().setLineHeight(25f);
        stationFont = this.getFont("bold", textureBold);
        moneyFont = this.getFont("money");
        bigFont = this.getFont("big");
        headerFont = this.getFont("header");
        headerItalicFont = this.getFont("headerItalic");
        textColor = new Color(0.65f, 0.65f, 0.65f, 1);
        btnColor = new Color(1, 1, 1, 1);
        btnTouchedColor = new Color(0.5f, 0.5f, 0.5f, 1);
        selectColor = new Color(1f, 1f, 1f, 1);

        normalStyle = new LabelStyle();
        normalStyle.font = normalFont;
        normalStyle.fontColor = textColor;

        headerStyle = new LabelStyle();
        headerStyle.font = headerFont;
        headerStyle.fontColor = textColor;

        colorStyle = new LabelStyle();
        colorStyle.font = colorFont;

        colorBoldStyle = new LabelStyle();
        colorBoldStyle.font = colorBoldFont;

        stationStyle = new LabelStyle();
        stationStyle.font = stationFont;
        stationStyle.fontColor = btnColor;
        stationTouchedStyle = new LabelStyle();
        stationTouchedStyle.font = stationFont;
        stationTouchedStyle.fontColor = btnTouchedColor;

        epigraphStyle = new LabelStyle();
        epigraphStyle.font = headerItalicFont;
        epigraphStyle.fontColor = textColor;
    }

    public static void dispose() {
        instance = null;
    }

    public Texture getFontTexture(String name) {
        Texture texture = new Texture(Gdx.files.internal("font/" + name + ".png"), true);
        texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
        return texture;
    }

    public BitmapFont getFont(String name, Texture texture) {
        return new BitmapFont(Gdx.files.internal("font/" + name + ".fnt"), new TextureRegion(texture), false);
    }

    public BitmapFont getFont(String name) {
        return this.getFont(name, this.getFontTexture(name));
    }
}
