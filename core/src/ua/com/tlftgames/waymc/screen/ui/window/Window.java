package ua.com.tlftgames.waymc.screen.ui.window;

import static com.badlogic.gdx.math.Interpolation.exp10;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.screen.ui.Button;
import ua.com.tlftgames.waymc.screen.ui.ShadowGroup;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;

public class Window extends ShadowGroup {
    protected int windowWidth;
    protected int windowHeight;
    protected int windowX = 220;
    protected int windowY = 20;
    protected Group windowGroup;
    private Image windowBg;
    private UIHelper helper;
    private WindowBottomButtons bottomButtons;

    public Window(UIHelper helper) {
        this(helper, helper.isSquareScreen() ? Config.getInstance().gameWidth : 840,
                helper.isSquareScreen() ? Config.getInstance().gameHeight : 680);
    }

    public Window(UIHelper helper, int windowWidth, int windowHeight) {
        super();
        this.helper = helper;
        this.setBounds(0, 0, Config.getInstance().gameWidth, Config.getInstance().gameHeight);
        this.setVisible(false);
        this.windowGroup = new Group();
        windowBg = new Image(new NinePatchDrawable(this.helper.getWindowBg()));
        this.windowGroup.addActor(windowBg);
        this.addActor(this.windowGroup);
        this.setWindowSize(windowWidth, windowHeight);
        bottomButtons = new WindowBottomButtons();
        this.windowGroup.addActor(bottomButtons);
    }

    protected void setWindowSize(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        int parentWidth = Config.getInstance().gameWidth;
        int parentHeight = Config.getInstance().gameHeight;
        if (this.getParent() != null) {
            parentWidth = (int) this.getParent().getWidth();
            parentHeight = (int) this.getParent().getHeight();
        }
        this.windowX = (parentWidth - this.windowWidth) / 2;
        this.windowY = (parentHeight - this.windowHeight) / 2;
        this.windowGroup.setBounds(this.windowX, this.windowY, this.windowWidth, this.windowHeight);
        windowBg.setBounds(0, 0, this.windowWidth, this.windowHeight);
    }

    public UIHelper getHelper() {
        return this.helper;
    }

    public TextureAtlas getAtlas() {
        return this.helper.getAtlas();
    }

    public void setBottomButtons(Button[] buttons) {
        this.bottomButtons.setButtons(buttons);
    }

    @Override
    protected void setParent(Group parent) {
        super.setParent(parent);
        if (parent != null) {
            this.windowX = (int) (parent.getWidth() - this.windowWidth) / 2;
            this.windowY = (int) (parent.getHeight() - this.windowHeight) / 2;
            this.setBounds(0, 0, parent.getWidth(), parent.getHeight());
            this.windowGroup.setBounds(this.windowX, this.windowY, this.windowWidth, this.windowHeight);
        }
    }

    public void show() {
        if (this.isVisible() == false) {
            this.windowGroup.setY(this.getHeight() + this.windowHeight);
            this.windowGroup.setX(this.windowX);
            this.windowGroup.setVisible(true);
            this.windowGroup
                    .addAction(sequence(moveTo(this.windowGroup.getX(), this.windowY, 0.5f, exp10), run(new Runnable() {
                        @Override
                        public void run() {
                            Window.this.afterShow();
                        }
                    })));
            if (this.bg != null) {
                this.bg.getColor().a = 0f;
                this.bg.addAction(fadeIn(0.5f));
            }
            this.setVisible(true);
        }
    }

    public void hide() {
        this.windowGroup.addAction(
                sequence(moveTo(this.windowGroup.getX(), -1 * this.windowHeight, 0.5f, exp10), run(new Runnable() {
                    @Override
                    public void run() {
                        Window.this.setVisible(false);
                        Window.this.afterHide();
                    }
                })));
        if (this.bg != null)
            this.bg.addAction(fadeOut(0.5f));
    }

    protected void afterHide() {

    }

    protected void afterShow() {

    }

    public void hideQuick() {
        this.setVisible(false);
        Window.this.afterHide();
    }

    public void showQuick() {
        if (this.bg != null)
            this.bg.getColor().a = 1;
        this.windowGroup.setPosition(this.windowX, this.windowY);
        this.setVisible(true);
        Window.this.afterShow();
    }

    public void hideWindowGroup() {
        this.windowGroup.setVisible(false);
    }

    public void showWindowGroup() {
        this.windowGroup.setVisible(true);
    }
}
