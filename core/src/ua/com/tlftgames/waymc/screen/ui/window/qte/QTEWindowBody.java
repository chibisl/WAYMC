package ua.com.tlftgames.waymc.screen.ui.window.qte;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import ua.com.tlftgames.waymc.screen.ui.window.PlaceWindowManager;
import ua.com.tlftgames.waymc.screen.ui.window.WindowBody;

public class QTEWindowBody extends WindowBody {
    private PlaceWindowManager manager;
    private int difficultLevel;

    public QTEWindowBody(PlaceWindowManager manager, int difficultLevel) {
        super();
        this.manager = manager;
        this.difficultLevel = difficultLevel;
    }

    protected void success() {
        this.manager.setResult(PlaceWindowManager.RESULT_GOOD);
        this.manager.showResult();
    }

    protected void fail() {
        this.manager.setResult(PlaceWindowManager.RESULT_BAD);
        this.manager.showResult();
    }

    public int getDifficultLevel() {
        return this.difficultLevel;
    }

    public TextureAtlas getAtlas() {
        return this.manager.getAtlas();
    }
}
