package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

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
import ua.com.tlftgames.waymc.screen.stage.GameStage;
import ua.com.tlftgames.waymc.screen.ui.Button;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.Tutorial;
import ua.com.tlftgames.waymc.screen.ui.UIGroup;
import ua.com.tlftgames.waymc.screen.ui.window.qte.LuckWindowBody;
import ua.com.tlftgames.waymc.screen.ui.window.qte.MemoryWindowBody;
import ua.com.tlftgames.waymc.screen.ui.window.qte.ReactionWindowBody;
import ua.com.tlftgames.waymc.screen.ui.window.qte.SpeedWindowBody;

public class PlaceWindowManager extends ActionWindowManager {
    public final static int ACTION_WAIT = 0;
    public final static int ACTION_SEARCH = 1;
    public final static int ACTION_TUTORIAL = 2;
    public final static int VARIANT_RETURN = 0;
    public final static int VARIANT_MOVE = 1;
    public final static int VARIANT_RUN = 2;
    public final static int VARIANT_HIDE = 3;
    public final static int QTE_LUCK = 0;
    public final static int QTE_REACTION = 1;
    public final static int QTE_SPEED = 2;
    public final static int QTE_MEMORY = 3;
    public final static int RESULT_BAD = 0;
    public final static int RESULT_GOOD = 1;
    public final static int SEARCH_QUEST_TYPE_MONEY = 0;
    public final static int SEARCH_QUEST_TYPE_ITEM = 1;
    private boolean moving = false;
    private int crimeSubLife = 0;
    private boolean needTutorial = true;
    private boolean needQTELuckTutorial = true;
    private boolean needQTEReactionTutorial = true;
    private boolean needQTESpeedTutorial = true;
    private boolean needQTEMemoryTutorial = true;
    private int[] testQTE = { QTE_LUCK, QTE_MEMORY };
    private int[] lifeQTE = { QTE_REACTION, QTE_SPEED, QTE_LUCK };
    private int qte = -1;
    private int crimeTestCount = 10;
    private int crimeTestStep = 10;
    private int crimeTestKey = 1;
    private LinkedList<Boolean> crimeTests;

    public PlaceWindowManager(UIGroup group) {
        super(group, "place");
        crimeTests = new LinkedList<Boolean>();
        needTutorial = Settings.getInstance().getTutorialEnable()
                && !Tutorial.isTutorialShowed(Tutorial.TUTORIAL_CRIME);
        needQTELuckTutorial = Settings.getInstance().getTutorialEnable()
                && !Tutorial.isTutorialShowed(Tutorial.TUTORIAL_QTE_LUCK);
        needQTEReactionTutorial = Settings.getInstance().getTutorialEnable()
                && !Tutorial.isTutorialShowed(Tutorial.TUTORIAL_QTE_REACTION);
        needQTESpeedTutorial = Settings.getInstance().getTutorialEnable()
                && !Tutorial.isTutorialShowed(Tutorial.TUTORIAL_QTE_SPEED);
        needQTEMemoryTutorial = Settings.getInstance().getTutorialEnable()
                && !Tutorial.isTutorialShowed(Tutorial.TUTORIAL_QTE_MEMORY);
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
        boolean result = this.isCrimeAttack(
                Math.min((int) (GameCore.getInstance().getPlaceManager().getStepCount() / this.crimeTestStep) + 1, 9));
        GameCore.getInstance().getPlaceManager().incStepCount();
        return result;
    }

    private boolean isCrimeAttack(int key) {
        if (GameCore.getInstance().getItemManager().hasItem("cloak_ii")) {
            key = Math.max(1, key - 4);
        }
        if (GameCore.getInstance().getItemManager().hasItem("cloak")) {
            key = Math.max(1, key - 2);
        }
        if (key > this.crimeTestKey || crimeTests.isEmpty()) {
            crimeTestKey = key;
            crimeTests.clear();
            for (int i = 0; i < key; i++) {
                crimeTests.add(true);
            }
            for (int i = key; i < crimeTestCount; i++) {
                crimeTests.add(false);
            }
            Collections.shuffle(crimeTests);
        }
        return crimeTests.removeFirst();
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
        this.getUIGroup().getStage().playSound(GameStage.CRIME_SOUND);
        crimeSubLife = Config.getInstance().crimeSubLife + (int) (Math.random() * 3);
        GameCore.getInstance().getSave().saveProgress(Save.SUBEDLIFE_KEY, crimeSubLife);

        int action = needTutorial ? ACTION_TUTORIAL : ((moving && Math.random() < 0.5f) ? ACTION_WAIT : ACTION_SEARCH);
        this.setAction(action);
        this.showActionStartText();
    }

    @Override
    protected ClickListener getVariantListener(final int variant) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlaceWindowManager.this.setVariant(variant);
                PlaceWindowManager.this.setQTE(variant);
                if (PlaceWindowManager.this.isNeedQTETutorial()) {
                    PlaceWindowManager.this.showQTETutorial();
                } else {
                    PlaceWindowManager.this.showQTE();
                }
            }
        };
    }

    private void setQTE(int variant) {
        switch (variant) {
        case VARIANT_RUN:
        case VARIANT_MOVE:
            this.qte = lifeQTE[(int) (Math.random() * lifeQTE.length)];
            break;
        default:
            this.qte = testQTE[(int) (Math.random() * testQTE.length)];
        }
    }

    private boolean isNeedQTETutorial() {
        switch (this.qte) {
        case QTE_REACTION:
            return this.needQTEReactionTutorial;
        case QTE_LUCK:
            return this.needQTELuckTutorial;
        case QTE_SPEED:
            return this.needQTESpeedTutorial;
        case QTE_MEMORY:
            return this.needQTEMemoryTutorial;
        default:
            return false;
        }
    }

    protected void showQTETutorial() {
        switch (this.qte) {
        case QTE_REACTION:
            this.needQTEReactionTutorial = false;
            Tutorial.setTutorialShowed(Tutorial.TUTORIAL_QTE_REACTION);
            break;
        case QTE_LUCK:
            this.needQTELuckTutorial = false;
            Tutorial.setTutorialShowed(Tutorial.TUTORIAL_QTE_LUCK);
            break;
        case QTE_SPEED:
            this.needQTESpeedTutorial = false;
            Tutorial.setTutorialShowed(Tutorial.TUTORIAL_QTE_SPEED);
            break;
        case QTE_MEMORY:
            this.needQTEMemoryTutorial = false;
            Tutorial.setTutorialShowed(Tutorial.TUTORIAL_QTE_MEMORY);
            break;
        }
        this.getWindow().setPlaceImageTexture();
        this.getWindow().updateBody(new InfoWindowBody("place.qte." + this.qte, null, this.getHelper()));
        this.getWindow().setBottomButtons(this.getHelper().createNextButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlaceWindowManager.this.showQTE();
            }
        }));
        this.getWindow().show();
    }

    protected void showQTE() {
        int difficultLevel = Math.min(GameCore.getInstance().getPlaceManager().getStepCount() / 20, 3);
        switch (this.qte) {
        case QTE_REACTION:
            this.getWindow().updateBody(new ReactionWindowBody(this, difficultLevel));
            break;
        case QTE_SPEED:
            this.getWindow().updateBody(new SpeedWindowBody(this, difficultLevel));
            break;
        case QTE_MEMORY:
            this.getWindow().updateBody(new MemoryWindowBody(this, difficultLevel));
            break;
        case QTE_LUCK:
        default:
            this.getWindow().updateBody(new LuckWindowBody(this, difficultLevel));
            break;
        }
        this.getWindow().getImage().setVisible(false);
        this.getWindow().setBottomButtons(new Button[] {});
        this.getWindow().show();
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
                        GameCore.getInstance().getNotificationManager().addNotification(new Notification(
                                Config.getInstance().searchNeedItemName, "notification.resources.not.enough"));
                    } else {
                        Item needItem = GameCore.getInstance().getItemManager()
                                .getItem(Config.getInstance().searchNeedItemName);
                        for (String resource : needItem.getResources()) {
                            GameCore.getInstance().getItemManager().removeOwnItem(resource);
                        }
                        GameCore.getInstance().getItemManager()
                                .removeOwnReceipt(Config.getInstance().searchNeedItemName);
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
        if (needTutorial) {
            needTutorial = false;
            Tutorial.setTutorialShowed(Tutorial.TUTORIAL_CRIME);
        }
        if (getResult() == RESULT_GOOD) {
            GameCore.getInstance().getNotificationManager()
                    .addNotification(new Notification("success", "notification.run.success"));
        } else {
            if (getVariant() == VARIANT_RETURN) {
                GameCore.getInstance().getPlaceManager().returnToLastPlace();
                this.finishAction();
                return;
            } else if (this.qte == QTE_LUCK || this.qte == QTE_MEMORY) {
                GameCore.getInstance().addLife(-1 * crimeSubLife);
            }
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
    public void showActions() {
        this.getUIGroup().getCurrentTypeWindowManager().showActions();
    }

    @Override
    protected void updateResult() {

    }

    @Override
    public void finishAction() {
        super.finishAction();
        this.qte = -1;
    }
}
