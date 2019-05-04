package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Save;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.UIGroup;
import ua.com.tlftgames.waymc.screen.ui.UIHelper;

public abstract class ActionWindowManager {
    private int result = -1;
    private String pref = "";
    private int action = -1;
    private int variant = -1;
    private UIGroup group;

    public ActionWindowManager(UIGroup group, String pref) {
        this.group = group;
        this.pref = pref;
    }

    public UIGroup getUIGroup() {
        return this.group;
    }

    public UIHelper getHelper() {
        return this.group.getHelper();
    }

    public TextureAtlas getAtlas() {
        return this.group.getAtlas();
    }

    public ActionWindow getWindow() {
        return this.group.getWindow();
    }

    public void setAction(int action) {
        this.action = action;
        GameCore.getInstance().getSave().saveProgress(Save.ACTION_KEY, this.action);
    }

    public int getAction() {
        return this.action;
    }

    public void setVariant(int variant) {
        this.variant = variant;
        GameCore.getInstance().getSave().saveProgress(Save.ACTION_VARIANT_KEY, this.variant);
    }

    public int getVariant() {
        return this.variant;
    }

    public void loadParams() {
        this.setAction(GameCore.getInstance().getSave().loadAction());
        this.setVariant(GameCore.getInstance().getSave().loadActionVariant());
        this.setResult(GameCore.getInstance().getSave().loadActionResult());
    }

    public void showLoadedAction() {
        if (this.result >= 0 && this.variant >= 0 && this.action >= 0) {
            this.showResult();
        } else if (this.action >= 0) {
            showActionStartText();
        } else {
            showActions();
        }
    }

    public boolean canDoVariant(int variant) {
        return true;
    }

    public boolean hasTextForResult() {
        return false;
    }

    public void setResult(int result) {
        this.result = result;
        GameCore.getInstance().getSave().saveProgress(Save.ACTION_RESULT_KEY, this.result);
    }

    public int getResult() {
        return this.result;
    }

    protected ArrayList<String> getVars() {
        return null;
    }

    protected ArrayList<String> getResultVars() {
        return null;
    }

    protected String getStartAddText() {
        return "";
    }

    protected String getStartText() {
        return null;
    }

    protected String getResultText() {
        return null;
    }

    public void showActionStartText() {
        String startText = (getStartText() == null) ? pref + ".start." + action + this.getStartAddText()
                : getStartText();
        InfoWindowBody questInfo = new InfoWindowBody(startText, this.getVars(), this.getHelper());
        this.getWindow().updateBody(questInfo);
        this.getWindow().setPlaceImageTexture();
        this.getWindow().setBottomButtons(this.getHelper().createNextButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ActionWindowManager.this.showChoices();
            }
        }));
        this.getWindow().show();
    }

    private void showChoices() {
        ArrayList<TextButton> buttons = new ArrayList<TextButton>();
        for (int variant : this.getVariants()) {
            String variantText = (getVariantText(variant) == null) ? pref + ".variant." + variant
                    : getVariantText(variant);
            TextButton btn = this.getHelper().createTextButton(variantText, getVariantVars(variant));
            btn.setUserObject(variant);
            btn.addListener(this.getVariantListener(variant));
            buttons.add(btn);
        }
        ChoicesWindowBody choicesWindow = new ChoicesWindowBody(buttons);
        this.getWindow().updateBody(choicesWindow);
        this.getWindow().setPlaceImageTexture();
        this.getWindow().setBottomButtons(this.getHelper().createBackButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ActionWindowManager.this.showActionStartText();
            }
        }));
        this.getWindow().show();
    }

    protected ClickListener getVariantListener(final int variant) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (ActionWindowManager.this.canDoVariant(variant)) {
                    ActionWindowManager.this.setVariant(variant);
                    ActionWindowManager.this.updateResult();
                    ActionWindowManager.this.showResult();
                }
            }
        };
    }

    protected ArrayList<String> getVariantVars(int variant) {
        return null;
    }

    protected String getVariantText(int variant) {
        return null;
    }

    protected String getResultAddText() {
        return "";
    }

    public void showResult() {
        if (this.hasTextForResult()) {
            String resultText = (getResultText() == null)
                    ? pref + ".result." + variant + "." + result + this.getResultAddText() : getResultText();
            this.getWindow().setPlaceImageTexture();
            this.showActionResult(resultText, this.getResultVars(), new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ActionWindowManager.this.action();
                }
            });
        } else {
            this.action();
        }
    }

    public void showActionResult(String text, final ArrayList<String> vars, final ClickListener listener) {
        InfoWindowBody info = new InfoWindowBody(text, vars, this.getHelper());
        this.getWindow().setBottomButtons(this.getHelper().createOkButtons(listener));
        this.getWindow().updateBody(info);
        this.getWindow().show();
    }

    public void finishAction() {
        this.setAction(-1);
        this.setVariant(-1);
        this.setResult(-1);
    }

    public abstract void action();

    public abstract void showActions();

    public abstract ArrayList<Integer> getVariants();

    protected abstract void updateResult();
}
