package ua.com.tlftgames.waymc.screen.ui;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Settings;
import ua.com.tlftgames.waymc.listener.Dispatcher;
import ua.com.tlftgames.waymc.listener.Listener;
import ua.com.tlftgames.waymc.place.Place;
import ua.com.tlftgames.waymc.quest.Quest;
import ua.com.tlftgames.waymc.quest.QuestAction;
import ua.com.tlftgames.waymc.screen.stage.GameStage;
import ua.com.tlftgames.waymc.screen.ui.window.ActionWindow;
import ua.com.tlftgames.waymc.screen.ui.window.IndustrialWindowManager;
import ua.com.tlftgames.waymc.screen.ui.window.InventoryWindow;
import ua.com.tlftgames.waymc.screen.ui.window.MenuWindow;
import ua.com.tlftgames.waymc.screen.ui.window.MerchantWindowManager;
import ua.com.tlftgames.waymc.screen.ui.window.PlaceWindowManager;
import ua.com.tlftgames.waymc.screen.ui.window.RecreationalWindowManager;
import ua.com.tlftgames.waymc.screen.ui.window.ResidentialWindowManager;
import ua.com.tlftgames.waymc.screen.ui.window.SettingsWindow;
import ua.com.tlftgames.waymc.screen.ui.window.TypeWindowManager;

public class UIGroup extends Group implements Listener {
    public final static int MOVE_TYPE_NORMAL = 0;
    public final static int MOVE_TYPE_RETURN = 1;
    public final static int MOVE_TYPE_BY_QUEST = 2;
    private LifeCounter life;
    private MoneyCounter money;
    private Button inventory;
    private Button menuBtn;
    private MenuWindow menu;
    private SettingsWindow settings;
    private ActionWindow window;
    private ResidentialWindowManager residentialWindowManager;
    private MerchantWindowManager merchantWindowManager;
    private RecreationalWindowManager recreationalWindowManager;
    private IndustrialWindowManager industrialWindowManager;
    private PlaceWindowManager placeWindowManager;
    private InventoryWindow inventoryWindow;
    private GameStage stage;
    private boolean windowBeenShowed = false;
    private UIHelper helper;
    private NotificationPopup notificationPopup;

    public UIGroup(TextureAtlas atlas, GameStage stage) {
        this.stage = stage;
        this.setBounds(0, 0, this.stage.getWidth(), this.stage.getHeight());
        this.setTouchable(Touchable.childrenOnly);
        this.helper = new UIHelper(atlas);
        this.settings = new SettingsWindow(this.getHelper());
        this.menu = new MenuWindow(this.helper, this.settings);
        this.window = new ActionWindow(this.getHelper());
        this.addActor(window);
        residentialWindowManager = new ResidentialWindowManager(this);
        merchantWindowManager = new MerchantWindowManager(this);
        recreationalWindowManager = new RecreationalWindowManager(this);
        industrialWindowManager = new IndustrialWindowManager(this);
        placeWindowManager = new PlaceWindowManager(this);
        this.inventoryWindow = new InventoryWindow(this);
        this.addActor(this.inventoryWindow);

        NinePatch lifeTexture = new NinePatch(atlas.findRegion("life-mg"), 31, 2, 25, 25);
        life = new LifeCounter(atlas.findRegion("life-bg"), lifeTexture, atlas.findRegion("life-fg"));
        this.addActor(life);

        money = new MoneyCounter(atlas.findRegion("money-bg"), Config.getInstance().moneyFont);
        this.addActor(money);

        inventory = new Button(atlas.findRegion("inventory"), atlas.findRegion("inventory-touched"));
        inventory.setPosition(10, 10);
        inventory.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIGroup.this.showInventory();
            }
        });
        this.addActor(inventory);

        menuBtn = new Button(atlas.findRegion("pause"), atlas.findRegion("pause-touched"));
        menuBtn.setPosition(Config.getInstance().gameWidth - menuBtn.getWidth() - 10, 10);
        menuBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIGroup.this.showMenu();
            }
        });
        this.addActor(menuBtn);

        notificationPopup = new NotificationPopup(this.helper);
        notificationPopup.setPosition((this.getWidth() - notificationPopup.getWidth()) / 2,
                this.getHeight() - notificationPopup.getHeight() - 5);
        this.addActor(notificationPopup);

        this.addActor(this.menu);

        this.addActor(this.settings);

        int[] events = { Dispatcher.EVENT_CURRENT_PLACE_CHANGED, Dispatcher.EVENT_ITEMS_CHANGED,
                Dispatcher.EVENT_RETURNED_TO_LAST_PLACE, Dispatcher.EVENT_CURRENT_PLACE_CHANGED_BY_QUEST };
        Dispatcher.getInstance().addListener(events, this);
    }

    public TextureAtlas getAtlas() {
        return this.helper.getAtlas();
    }

    @Override
    public GameStage getStage() {
        return this.stage;
    }

    public UIHelper getHelper() {
        return this.helper;
    }

    public void showMenu() {
        this.menu.show();
    }

    public ActionWindow getWindow() {
        return this.window;
    }

    public TypeWindowManager getTypeWindowManager(Place place) {
        switch (place.getType()) {
        case Place.TYPE_RESIDENTIAL:
            return getResidentialWindowManager();
        case Place.TYPE_MERCHANT:
            return getMerchantWindowManager();
        case Place.TYPE_RECREATIONAL:
            return getRecreationalWindowManager();
        case Place.TYPE_INDUSTRIAL:
            return getIndustrialWindowManager();
        default:
            return null;
        }
    }

    public TypeWindowManager getCurrentTypeWindowManager() {
        return this.getTypeWindowManager(GameCore.getInstance().getPlaceManager().getCurrentPlace());
    }

    public ResidentialWindowManager getResidentialWindowManager() {
        return this.residentialWindowManager;
    }

    public MerchantWindowManager getMerchantWindowManager() {
        return this.merchantWindowManager;
    }

    public RecreationalWindowManager getRecreationalWindowManager() {
        return this.recreationalWindowManager;
    }

    public IndustrialWindowManager getIndustrialWindowManager() {
        return this.industrialWindowManager;
    }

    public PlaceWindowManager getPlaceWindowManager() {
        return this.placeWindowManager;
    }

    public void showInventory() {
        windowBeenShowed = this.getWindow().isVisible();
        if (windowBeenShowed)
            this.getWindow().hide();
        this.inventory.setTouchable(Touchable.disabled);
        this.inventory.setTouched(true);
        this.inventoryWindow.show();
    }

    public void hideInventory() {
        this.inventoryWindow.hide();
        this.inventory.setTouchable(Touchable.enabled);
        this.inventory.setTouched(false);
        if (windowBeenShowed)
            this.getWindow().show();
    }

    public void moveToOpenPlace() {
        this.getStage().playSound(GameStage.MOVE_SOUND);
        GameCore.getInstance().addMoney(-1 * GameCore.getInstance().getPlaceManager().getMoveCost());
        GameCore.getInstance().getPlaceManager().moveToOpenPlace();
    }

    private void movePin(int type) {
        this.getWindow().hide();
        this.getStage().getWorld().movePin();
        switch (type) {
        case MOVE_TYPE_NORMAL:
            this.addAction(sequence(delay(1.5f), run(new Runnable() {
                @Override
                public void run() {
                    UIGroup.this.getPlaceWindowManager().startPlaceEvents(true);
                }
            })));
            break;
        case MOVE_TYPE_BY_QUEST:
            this.addAction(sequence(delay(1.5f), run(new Runnable() {
                @Override
                public void run() {
                    UIGroup.this.getPlaceWindowManager().startSearch();
                }
            })));
            break;
        case MOVE_TYPE_RETURN:
            this.hideWindowAndWait();
            break;
        }
    }

    public void init() {
        int questIndex = GameCore.getInstance().getQuestManager().loadOpenQuest();
        int currentStep = GameCore.getInstance().getCurrentStep();
        switch (currentStep) {
        case GameCore.STEP_START_GAME:
            if (Settings.getInstance().getTutorialEnable()) {
                ArrayList<String> vars = new ArrayList<String>();
                vars.add("place." + GameCore.getInstance().getPlaceManager().getCurrentShowedSearchPlace());
                this.getPlaceWindowManager().getWindow().setPlaceImageTexture();
                this.getPlaceWindowManager().showActionResult(
                        GameCore.getInstance().getPlaceManager().getCurrentSearchText(), vars, new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                UIGroup.this.hideWindowAndWait();
                            }
                        });
            } else {
                if (!GameCore.getInstance().setCurrentStep(GameCore.STEP_WAIT))
                    return;
            }
            break;
        case GameCore.STEP_CRIME:
            this.placeWindowManager.loadParams();
            this.placeWindowManager.showLoadedAction();
            break;
        case GameCore.STEP_MANDATORY_QUEST:
            this.startQuest(questIndex, true);
            break;
        case GameCore.STEP_ACTION:
            this.getCurrentTypeWindowManager().loadParams();
            this.getCurrentTypeWindowManager().showLoadedAction();
            break;
        case GameCore.STEP_QUEST:
            this.startQuest(questIndex, false);
            break;
        }
    }

    private void startQuest(int questIndex, boolean isMandatory) {
        if (questIndex >= 0) {
            Quest quest = GameCore.getInstance().getQuestManager().getQuest(questIndex);
            int questActionIndex = GameCore.getInstance().getSave().loadQuestAction();
            if (questActionIndex >= 0) {
                int choiseIndex = questActionIndex / 10;
                int actionIndex = questActionIndex % 10;
                final QuestAction action = quest.getAllChoices().get(choiseIndex).getAllActions().get(actionIndex);
                if (action != null) {
                    this.getCurrentTypeWindowManager().showQuestAction(action);
                    return;
                }
            }
            this.getCurrentTypeWindowManager().showQuest(quest, isMandatory);
        }
    }

    public void hideWindowAndWait() {
        if (!GameCore.getInstance().setCurrentStep(GameCore.STEP_WAIT))
            return;
        this.getWindow().hide();
    }

    @Override
    public void fireEvent(int event) {
        switch (event) {
        case Dispatcher.EVENT_CURRENT_PLACE_CHANGED:
            movePin(MOVE_TYPE_NORMAL);
            break;
        case Dispatcher.EVENT_CURRENT_PLACE_CHANGED_BY_QUEST:
            movePin(MOVE_TYPE_BY_QUEST);
            break;
        case Dispatcher.EVENT_RETURNED_TO_LAST_PLACE:
            movePin(MOVE_TYPE_RETURN);
            break;
        case Dispatcher.EVENT_ITEMS_CHANGED:
            this.getStage().getWorld().getMetro().updateReach();
            break;
        }
    }
}
