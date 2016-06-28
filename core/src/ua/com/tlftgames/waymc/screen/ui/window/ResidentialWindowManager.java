package ua.com.tlftgames.waymc.screen.ui.window;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Save;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.natification.Notification;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.Tutorial;
import ua.com.tlftgames.waymc.screen.ui.UIGroup;

import java.util.ArrayList;

public class ResidentialWindowManager extends TypeWindowManager {
    public final static int VARIANT_EXPENSIVE = 0;
    public final static int VARIANT_CHEAP = 1;
    public final static int VARIANT_NONE = 2;
    public final static int VARIANT_TRAINING = 3;
    public final static int VARIANT_PLAY = 4;
    public final static int VARIANT_LEAVE = 5;
    public final static int RESULT_BAD = 0;
    public final static int RESULT_NORMAL = 1;
    public final static int RESULT_GOOD = 2;
    public final static int ACTION_REST_ALL = 0;
    public final static int ACTION_REST_EXPENSIVE = 1;
    public final static int ACTION_REST_CHEAP = 2;
    public final static int ACTION_PLAY_CLUB = 3;
    private int restCost = 10;
    private int restCheapCost = 10;
    private float[] playChances = {0, 0.1f, 0.25f, 0.5f, 0.85f};

    public ResidentialWindowManager(UIGroup group) {
        super(group, "residential", Tutorial.TUTORIAL_RESIDENTIAL);
    }

    @Override
    public ArrayList<TextButton> getButtons() {
        ArrayList<TextButton> buttons = new ArrayList<TextButton>();
        TextButton rest = this.getHelper().createTextButton("btn.rest");
        rest.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ResidentialWindowManager.this.findRest();
            }
        });
        buttons.add(rest);

        TextButton playClub = this.getHelper().createTextButton("btn.play.club");
        playClub.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ResidentialWindowManager.this.playClub();
            }
        });
        buttons.add(playClub);

        return buttons;
    }

    private void playClub() {
        StageScreen.getInstance().getTracker().trackEvent("Residential", "action", "playClub", 1);
        this.setAction(ACTION_PLAY_CLUB);
        this.showActionStartText();
    }

    protected void findRest() {
        StageScreen.getInstance().getTracker().trackEvent("Residential", "action", "findRest", 1);
        this.restCost = Config.getInstance().startRestCost + (int) (Math.random() * 6);
        GameCore.getInstance().getSave().saveProgress(Save.RESIDENTIAL_REST_COST_KEY, this.restCost);
        this.restCheapCost = this.restCost / 3;

        if (Math.random() >= 0.5f) {
            this.setAction(ACTION_REST_ALL);
        } else if (Math.random() >= 0.5f) {
            this.setAction(ACTION_REST_EXPENSIVE);
        } else {
            this.setAction(ACTION_REST_CHEAP);
        }
        this.showActionStartText();
    }

    @Override
    public boolean canDoVariant(int variant) {
        if (variant == VARIANT_CHEAP || variant == VARIANT_EXPENSIVE || variant == VARIANT_TRAINING) {
            int cost = (variant == VARIANT_CHEAP) ? restCheapCost :
                    ((variant == VARIANT_EXPENSIVE) ? this.restCost : Config.getInstance().playClubCost);
            if (GameCore.getInstance().getMoney() < cost) {
                GameCore.getInstance().getNotificationManager()
                        .addNotification(new Notification("money", "notification.money.not.match"));
                return false;
            }
        }
        return true;
    }

    @Override
    public void action() {
        if (this.getVariant() >= VARIANT_EXPENSIVE && this.getVariant() <= VARIANT_NONE) {
            if (this.getVariant() != VARIANT_NONE) {
                int subMoney = -1 * restCost;
                int addLife = Config.getInstance().restAddLife;
                if (getVariant() == VARIANT_EXPENSIVE) {
                    StageScreen.getInstance().getTracker().trackEvent("Residential", "choice", "restExpensive", 1);
                    switch (getResult()) {
                        case RESULT_BAD:
                            addLife = addLife / 2;
                            break;
                        case RESULT_GOOD:
                            subMoney = subMoney / 2;
                            break;
                    }
                } else {
                    StageScreen.getInstance().getTracker().trackEvent("Residential", "choice", "restCheap", 1);
                    subMoney = -1 * restCheapCost;
                    addLife = Config.getInstance().restCheapAddLife;
                    if (getResult() == RESULT_GOOD) {
                        addLife = addLife * 2;
                    }
                }
                GameCore.getInstance().addMoney(subMoney);
                GameCore.getInstance().addLife(addLife);
            }
            this.setQuestStartText("quest.after.rest." + (this.getVariant() == VARIANT_NONE ? "fail" : "done"));
        } else if (this.getVariant() >= VARIANT_TRAINING && this.getVariant() <= VARIANT_LEAVE) {
            if (this.getVariant() != VARIANT_LEAVE && getResult() == RESULT_GOOD) {
                if (getVariant() == VARIANT_TRAINING) {
                    StageScreen.getInstance().getTracker().trackEvent("Residential", "choice", "playTraining", 1);
                    GameCore.getInstance().addMoney(-1 * Config.getInstance().playClubCost);
                    this.updatePlayLevel(1);
                } else {
                    StageScreen.getInstance().getTracker().trackEvent("Residential", "choice", "playTournament", 1);
                    GameCore.getInstance().addMoney(Config.getInstance().playClubWin);
                }
            }
            if (getVariant() == VARIANT_PLAY) this.updatePlayLevel(-1);
            this.setQuestStartText("quest.after." + (this.getResult() == RESULT_BAD ? "fail" : "success"));
        }
        this.finishAction();
        this.startQuest();
    }

    @Override
    public void loadParams() {
        this.restCost = GameCore.getInstance().getSave().loadResidentialRestCost();
        this.restCheapCost = this.restCost / 3;
        super.loadParams();
    }

    @Override
    protected ArrayList<String> getVars() {
        ArrayList<String> vars = new ArrayList<String>();
        if (this.getAction() == ACTION_REST_ALL || this.getAction() == ACTION_REST_EXPENSIVE) {
            vars.add(Integer.toString(restCost));
            vars.add(Translator.getInstance().getMoneyText(restCost));
        }
        if (this.getAction() == ACTION_REST_ALL || this.getAction() == ACTION_REST_CHEAP) {
            vars.add(Integer.toString(restCheapCost));
            vars.add(Translator.getInstance().getMoneyText(restCheapCost));
        }
        if (this.getAction() == ACTION_PLAY_CLUB) {
            vars.add(Integer.toString(Config.getInstance().playClubWin));
            vars.add(Translator.getInstance().getMoneyText(Config.getInstance().playClubWin));
            vars.add(Integer.toString(Config.getInstance().playClubCost));
            vars.add(Translator.getInstance().getMoneyText(Config.getInstance().playClubCost));
        }
        return vars;
    }

    public boolean hasTextForResult() {
        return !(getVariant() == VARIANT_NONE || getVariant() == VARIANT_LEAVE || getResult() == RESULT_NORMAL);
    }

    @Override
    public ArrayList<Integer> getVariants() {
        ArrayList<Integer> variants = new ArrayList<Integer>();
        switch (getAction()) {
            case ACTION_REST_ALL:
                variants.add(VARIANT_EXPENSIVE);
                variants.add(VARIANT_CHEAP);
                variants.add(VARIANT_NONE);
                break;
            case ACTION_REST_EXPENSIVE:
                variants.add(VARIANT_EXPENSIVE);
                variants.add(VARIANT_NONE);
                break;
            case ACTION_REST_CHEAP:
                variants.add(VARIANT_CHEAP);
                variants.add(VARIANT_NONE);
                break;
            case ACTION_PLAY_CLUB:
                variants.add(VARIANT_TRAINING);
                variants.add(VARIANT_PLAY);
                variants.add(VARIANT_LEAVE);
                break;
        }
        return variants;
    }

    @Override
    protected void updateResult() {
        int result = RESULT_NORMAL;
        int playLevel = GameCore.getInstance().getSave().loadPlayLevel();
        switch(this.getVariant()) {
            case VARIANT_CHEAP:
            case VARIANT_EXPENSIVE:
                if (Math.random() < 0.8f) result = (Math.random() < 0.5f) ? RESULT_GOOD : RESULT_BAD;
                if (getVariant() == VARIANT_CHEAP && result == RESULT_BAD) {
                    if (GameCore.getInstance().getItemManager().getOwnResources().size() == 0) {
                        result = RESULT_NORMAL;
                    } else {
                        GameCore.getInstance().getItemManager().lostRandomResource();
                    }
                }
                break;
            case VARIANT_PLAY:
                result = (Math.random() < this.playChances[playLevel]) ? RESULT_GOOD : RESULT_BAD;
                break;
            case VARIANT_TRAINING:
                result = (playLevel < 1 || Math.random() < 0.75f) ? RESULT_GOOD : RESULT_BAD;
                break;
        }
        this.setResult(result);
    }

    private void updatePlayLevel(int add) {
        int playLevel = GameCore.getInstance().getSave().loadPlayLevel();
        playLevel +=add;
        if (playLevel > 4) playLevel = 4;
        else if (playLevel < 0) playLevel = 0;
        GameCore.getInstance().getSave().saveProgress(Save.PLAY_LEVEL_KEY, playLevel);
    }

}
