package ua.com.tlftgames.waymc.screen.ui;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.item.Item;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.ui.window.DialogWindow;

public class UIHelper {
    private TextureAtlas atlas;
    private AtlasRegion btnBg;
    private AtlasRegion btnBgTouched;
    private int cellGroupWidth = 780;
    private int cellGroupHeight = 100;
    private int cellStartSpace = 1;
    private int cellWidth = 100;
    private int cellSpace = 13;
    private int cellInRow = 7;
    private int itemWidth = 100;
    private int itemSpace = 13;

    public UIHelper(TextureAtlas atlas) {
        this.atlas = atlas;
        btnBg = atlas.findRegion("btn-bg");
        btnBgTouched = atlas.findRegion("btn-bg-touched");
    }

    public TextureAtlas getAtlas() {
        return this.atlas;
    }

    public NinePatch getWindowBg() {
        return new NinePatch(this.getAtlas().findRegion("text-bg"), 50, 50, 35, 35);
    }

    public Button createButton(String texture, ClickListener listener) {
        Button btn = new Button(atlas.findRegion(texture), atlas.findRegion(texture + "-touched"));
        btn.addListener(listener);
        return btn;
    }

    public TextButton createTextButton(String text, ArrayList<String> vars) {
        String transText = Translator.getInstance().translate(text);
        if (vars != null) {
            for (String var : vars) {
                transText = transText.replaceFirst("\\{\\{var\\}\\}", Translator.getInstance().translate(var));
            }
        }
        transText = transText.replace("{{var}}", "");
        TextButton btn = new TextButton(btnBg, btnBgTouched, transText, Config.getInstance().italicFont,
                Config.getInstance().btnColor, Config.getInstance().btnTouchedColor);
        btn.setHeight(60);
        return btn;
    }

    public TextButton createTextButton(String text) {
        TextButton btn = new TextButton(btnBg, btnBgTouched, Translator.getInstance().translate(text),
                Config.getInstance().italicFont, Config.getInstance().btnColor, Config.getInstance().btnTouchedColor);
        btn.setHeight(60);
        return btn;
    }

    public TextButton createMenuButton(String text) {
        TextButton btn = new TextButton(btnBg, btnBgTouched, Translator.getInstance().translate(text),
                Config.getInstance().bigFont, Config.getInstance().btnColor, Config.getInstance().btnTouchedColor);
        btn.setHeight(75);
        return btn;
    }

    public TextButton createCheckButton(String text) {
        TextButton btn = new TextButton(btnBgTouched, btnBg, Translator.getInstance().translate(text),
                Config.getInstance().italicFont, Config.getInstance().btnTouchedColor, Config.getInstance().btnColor);
        btn.setHeight(60);
        return btn;
    }

    public Button[] createNextButton(ClickListener listener) {
        return new Button[] { null, null, this.createButton("forward", listener) };
    }

    public Button[] createBackButton(ClickListener listener) {
        return new Button[] { this.createButton("back", listener), null, null };
    }

    public Button[] createReturnButton(ClickListener listener) {
        return new Button[] { null, null, this.createButton("return", listener) };
    }

    public Button[] createOkButtons(ClickListener listener) {
        return new Button[] { null, this.createButton("ok", listener), null };
    }

    public Group createItemCells() {
        Group cellGroup = new Group();
        cellGroup.setSize(cellGroupWidth, cellGroupHeight);
        for (int i = 0; i < Config.getInstance().itemsMaxCount; i++) {
            Image itemNone = this.createItemNone();
            int inRow = i % cellInRow;
            int rows = i / cellInRow;
            itemNone.setBounds(cellStartSpace + inRow * (cellWidth + cellSpace),
                    cellGroup.getHeight() - cellWidth - rows * (cellWidth + cellSpace), cellWidth, cellWidth);
            cellGroup.addActor(itemNone);
        }
        return cellGroup;
    }

    public Image createItemNone() {
        return new Image(this.getAtlas().findRegion("item-none"));
    }

    public Button createItemCellBtn(Item item, int i, ClickListener listener) {
        Button itemBtn = this.createItemBtn(item, listener);
        int inRow = i % cellInRow;
        int rows = i / cellInRow;
        itemBtn.setPosition(cellStartSpace + inRow * (itemWidth + itemSpace),
                cellGroupHeight - itemWidth - rows * (itemWidth + itemSpace));
        return itemBtn;
    }

    public Button createItemBtn(Item item, ClickListener listener) {
        return this.createItemBtn(item, listener, true);
    }

    public Button createItemBtn(Item item, ClickListener listener, boolean have) {
        Button itemBtn = new Button(this.getAtlas().findRegion(item.getImage() + (have ? "" : "-touched")),
                this.getAtlas().findRegion(item.getImage() + "-touched"));
        itemBtn.setUserObject(item);
        itemBtn.setSize(itemWidth, itemWidth);
        itemBtn.addListener(listener);
        return itemBtn;
    }

    public ScrollPane createScrollPane(Actor widget) {
        ScrollPane pane = new ScrollPane(widget,
                new NinePatch(this.getAtlas().findRegion("text-fade-top"), 4, 4, 50, 50),
                new NinePatch(this.getAtlas().findRegion("text-fade-bottom"), 4, 4, 50, 50));
        pane.setOverscroll(false, false);
        pane.setFadeScrollBars(false);
        pane.setSmoothScrolling(true);
        StageScreen.getInstance().getStage().setScrollFocus(pane);
        return pane;
    }

    public DialogWindow openDialog(Group parent, String text, Runnable okAction, Runnable cancelAction) {
        DialogWindow dialog = new DialogWindow(this, text, okAction, cancelAction);
        parent.addActor(dialog);
        dialog.show();
        return dialog;
    }
}
