package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Save;
import ua.com.tlftgames.waymc.Settings;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.item.Item;
import ua.com.tlftgames.waymc.natification.Notification;
import ua.com.tlftgames.waymc.place.Place;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.Tutorial;
import ua.com.tlftgames.waymc.screen.ui.UIGroup;

public class PlaceWindowManager extends ActionWindowManager {
    public final static int ACTION_WAIT = 0;
    public final static int ACTION_SEARCH = 1;
    public final static int ACTION_TUTORIAL = 2;
    public final static int VARIANT_RETURN = 0;
    public final static int VARIANT_MOVE = 1;
    public final static int VARIANT_RUN = 2;
    public final static int VARIANT_HIDE = 3;
    public final static int RESULT_BAD = 0;
    public final static int RESULT_GOOD = 1;
    public final static int RESULT_LOST_MONEY = 2;
    public final static int RESULT_LOST_ITEM = 3;
    public final static int SEARCH_QUEST_TYPE_MONEY = 0;
    public final static int SEARCH_QUEST_TYPE_ITEM = 1;
    private boolean moving = false;
    private int crimeSubLife = 0;
    private boolean needTutorial = true;

    public PlaceWindowManager(UIGroup group) {
        super(group, "place");
        needTutorial = Settings.getInstance().getTutorialEnable()
                && !Tutorial.isTutorialShowed(Tutorial.TUTORIAL_CRIME);
    }

    public void showPlace(Place place) {
        WindowBody placeInfo = new PlaceWindowBody(this, place);
        this.getWindow().updateBody(placeInfo);
        this.getWindow().setImageTexture(this.getAtlas().findRegion("place", place.getIndex()));
        this.getWindow().setBottomButtons(this.getHelper().createReturnButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlaceWindowManager.this.getUIGroup().hideWindowAndWait();
            }
        }));
        this.getWindow().show();
    }

    public void startPlaceEvents(boolean moving) {
        this.moving = moving;
        startCrime();
    }

    public boolean testCrime() {
        double test = (float) GameCore.getInstance().getPlaceManager().getCurrentPlace().getCrimeLevel()
                / (Config.getInstance().allCrimeLevel);
        GameCore.getInstance().getPlaceManager().updatePlaceCrime();
        return Math.random() < test;
    }

    protected void startCrime() {
        if (needTutorial || testCrime()) {
            showCrimeWindow();
            return;
        }
        startSearch();
    }

    public void showCrimeWindow() {
        if (!GameCore.getInstance().setCurrentStep(GameCore.STEP_CRIME))
            return;
        crimeSubLife = Config.getInstance().crimeSubLife + (int) (Math.random() * 2);
        GameCore.getInstance().getSave().saveProgress(Save.SUBEDLIFE_KEY, crimeSubLife);

        int action = needTutorial ? ACTION_TUTORIAL : ((moving && Math.random() < 0.5f) ? ACTION_WAIT : ACTION_SEARCH);
        this.setAction(action);
        this.showActionStartText();
    }

    public void startSearch() {
        Place place = GameCore.getInstance().getPlaceManager().getCurrentPlace();
        int needSearchPlace = GameCore.getInstance().getPlaceManager().getCurrentShowedSearchPlace();
        if ((place.getIndex() == needSearchPlace || place.getType() == needSearchPlace)
                && !GameCore.getInstance().getPlaceManager().isSearchedPlace(place)) {
            int searchIndex = GameCore.getInstance().getPlaceManager().getCurrentSearchPlaceIndex();
            StageScreen.getInstance().getTracker().trackEvent("MainQuest", "search", "step" + searchIndex, 1);
            if (GameCore.getInstance().getPlaceManager().isSearchIndexInArray(searchIndex,
                    Config.getInstance().searchNeedMoneyIndexes)) {
                this.startSearchQuest(SEARCH_QUEST_TYPE_MONEY);
            } else if (GameCore.getInstance().getPlaceManager().isSearchIndexInArray(searchIndex,
                    Config.getInstance().searchNeedItemIndexes)) {
                this.startSearchQuest(SEARCH_QUEST_TYPE_ITEM);
            } else {
                this.makeSearch();
            }
            return;
        }
        this.getUIGroup().getCurrentTypeWindowManager().startMandatoryQuest();
    }

    private void startSearchQuest(final int quest_type) {
        ArrayList<String> vars = new ArrayList<String>();
        switch (quest_type) {
        case SEARCH_QUEST_TYPE_MONEY:
            vars.add(Integer.toString(Config.getInstance().searchNeedMoneySum));
            vars.add(Translator.getInstance().getMoneyText(Config.getInstance().searchNeedMoneySum));
            break;
        case SEARCH_QUEST_TYPE_ITEM:
            Item needItem = GameCore.getInstance().getItemManager().getItem(Config.getInstance().searchNeedItemName);
            vars.addAll(Arrays.asList(needItem.getResources()));
            break;
        }
        InfoWindowBody questInfo = new InfoWindowBody(
                GameCore.getInstance().getPlaceManager().getCurrentSearchText() + ".start", vars, this.getHelper());
        this.getWindow().updateBody(questInfo);
        // TODO change to search image
        this.getWindow().setPlaceImageTexture();
        this.getWindow().setBottomButtons(this.getHelper().createNextButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlaceWindowManager.this.showSearchQuestChoices(quest_type);
            }
        }));
        this.getWindow().show();
    }

    private void showSearchQuestChoices(final int quest_type) {
        ArrayList<TextButton> buttons = new ArrayList<TextButton>();
        TextButton acceptBtn = this.getHelper()
                .createTextButton(GameCore.getInstance().getPlaceManager().getCurrentSearchText() + ".variant.accept");
        switch (quest_type) {
        case SEARCH_QUEST_TYPE_MONEY:
            acceptBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (GameCore.getInstance().getMoney() < Config.getInstance().searchNeedMoneySum) {
                        GameCore.getInstance().getNotificationManager()
                                .addNotification(new Notification("money", "notification.money.not.match"));
                    } else {
                        GameCore.getInstance().addMoney(-1 * Config.getInstance().searchNeedMoneySum);
                        PlaceWindowManager.this.makeSearch();
                    }
                }
            });
            break;
        case SEARCH_QUEST_TYPE_ITEM:
            acceptBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!GameCore.getInstance().getItemManager()
                            .canCreateItem(Config.getInstance().searchNeedItemName)) {
                        GameCore.getInstance().getNotificationManager()
                                .addNotification(new Notification("money", "notification.resources.not.enough"));
                    } else {
                        Item needItem = GameCore.getInstance().getItemManager()
                                .getItem(Config.getInstance().searchNeedItemName);
                        for (String resource : needItem.getResources()) {
                            GameCore.getInstance().getItemManager().removeOwnItem(resource);
                        }
                        PlaceWindowManager.this.makeSearch();
                    }
                }
            });
            break;
        }
        buttons.add(acceptBtn);

        TextButton cancelBtn = this.getHelper()
                .createTextButton(GameCore.getInstance().getPlaceManager().getCurrentSearchText() + ".variant.cancel");
        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlaceWindowManager.this.getUIGroup().getCurrentTypeWindowManager().startMandatoryQuest();
            }
        });
        buttons.add(cancelBtn);

        ChoicesWindowBody choicesWindow = new ChoicesWindowBody(buttons);
        this.getWindow().updateBody(choicesWindow);
        // TODO: change to search image
        this.getWindow().setPlaceImageTexture();
        this.getWindow().setBottomButtons(this.getHelper().createBackButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlaceWindowManager.this.startSearchQuest(quest_type);
            }
        }));
        this.getWindow().show();
    }

    private void makeSearch() {
        if (GameCore.getInstance().search()) {
            if (GameCore.getInstance().isGameWin()) {
                GameCore.getInstance().gameWin();
                return;
            }
            ArrayList<String> vars = new ArrayList<String>();
            if (GameCore.getInstance().getPlaceManager().isSearchIndexInArray(
                    GameCore.getInstance().getPlaceManager().getCurrentSearchPlaceIndex(),
                    Config.getInstance().searchNeedItemIndexes)) {
                Item needItem = GameCore.getInstance().getItemManager()
                        .getItem(Config.getInstance().searchNeedItemName);
                vars.addAll(Arrays.asList(needItem.getResources()));
            }
            vars.add("place." + GameCore.getInstance().getPlaceManager().getCurrentShowedSearchPlace());
            this.getWindow().setPlaceImageTexture();
            PlaceWindowManager.this.showActionResult(GameCore.getInstance().getPlaceManager().getCurrentSearchText(),
                    vars, new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            if (GameCore.getInstance().getPlaceManager().isSearchIndexInArray(
                                    GameCore.getInstance().getPlaceManager().getCurrentSearchPlaceIndex(),
                                    Config.getInstance().searchNeedItemIndexes)) {
                                GameCore.getInstance().getItemManager()
                                        .addOwnReceipt(Config.getInstance().searchNeedItemName);
                            }
                            PlaceWindowManager.this.getUIGroup().getCurrentTypeWindowManager().startMandatoryQuest();
                        }
                    });
        } else {
            this.getWindow().setPlaceImageTexture();
            PlaceWindowManager.this.showActionResult(
                    "search.fail." + GameCore.getInstance().getPlaceManager().getCurrentSearchPlaceIndex(), null,
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            PlaceWindowManager.this.getUIGroup().getCurrentTypeWindowManager().startMandatoryQuest();
                        }
                    });
        }
        PlaceWindowManager.this.getUIGroup().getStage().getWorld().updateAttentions();
    }

    @Override
    public void loadParams() {
        crimeSubLife = GameCore.getInstance().getSave().loadSubedLife();
        super.loadParams();
    }

    @Override
    public void action() {
        if (getResult() == RESULT_BAD) {
            switch (getVariant()) {
            case VARIANT_RETURN:
                GameCore.getInstance().getPlaceManager().returnToLastPlace();
                break;
            case VARIANT_MOVE:
            case VARIANT_HIDE:
            case VARIANT_RUN:
                GameCore.getInstance().addLife(-1 * crimeSubLife);
                break;
            }
        }
        if (getResult() == RESULT_LOST_MONEY) {
            GameCore.getInstance().addMoney(-1 * (Config.getInstance().runLostMoney + (int) (Math.random() * 6)));
        }
        if (getResult() == RESULT_GOOD) {
            GameCore.getInstance().getNotificationManager()
                    .addNotification(new Notification("success", "notification.run.success"));
        }

        this.finishAction();
        this.startSearch();
    }

    @Override
    public boolean hasTextForResult() {
        return getResult() != RESULT_GOOD;
    }

    @Override
    public ArrayList<Integer> getVariants() {
        ArrayList<Integer> variants = new ArrayList<Integer>();
        switch (this.getAction()) {
        case ACTION_WAIT:
            variants.add(VARIANT_RETURN);
            variants.add(VARIANT_MOVE);
            break;
        case ACTION_TUTORIAL:
        case ACTION_SEARCH:
            variants.add(VARIANT_RUN);
            variants.add(VARIANT_HIDE);
            break;
        }
        return variants;
    }

    @Override
    protected void updateResult() {
        if (needTutorial) {
            this.setResult(RESULT_GOOD);
            needTutorial = false;
            Tutorial.setTutorialShowed(Tutorial.TUTORIAL_CRIME);
            return;
        }
        float test = GameCore.getInstance().getItemManager().hasItem("cloak_ii") ? 0.1f
                : (GameCore.getInstance().getItemManager().hasItem("cloak") ? 0.25f : 0.5f);
        int result = (Math.random() < test) ? RESULT_BAD : RESULT_GOOD;
        if (getVariant() == VARIANT_RUN && result == RESULT_BAD) {
            if (GameCore.getInstance().getMoney() >= Config.getInstance().runLostMoney && Math.random() < 0.75f) {
                result = RESULT_LOST_MONEY;
            } else if (GameCore.getInstance().getItemManager().getOwnResources().size() > 0) {
                result = RESULT_LOST_ITEM;
                GameCore.getInstance().getItemManager().lostRandomResource();
            }
        }
        this.setResult(result);
    }

    @Override
    public void showActions() {
        this.getUIGroup().getCurrentTypeWindowManager().showActions();
    }
}
