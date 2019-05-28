package ua.com.tlftgames.waymc;

import java.util.ArrayList;
import java.util.HashMap;

import ua.com.tlftgames.waymc.item.ItemManager;
import ua.com.tlftgames.waymc.listener.Dispatcher;
import ua.com.tlftgames.waymc.natification.Notification;
import ua.com.tlftgames.waymc.natification.NotificationManager;
import ua.com.tlftgames.waymc.place.PlaceManager;
import ua.com.tlftgames.waymc.quest.QuestAction;
import ua.com.tlftgames.waymc.quest.QuestManager;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.stage.FinalStage;
import ua.com.tlftgames.waymc.screen.stage.SlideStage;

public class GameCore {
    public final static int STEP_START_GAME = -1;
    public final static int STEP_WAIT = 0;
    public final static int STEP_CRIME = 1;
    public final static int STEP_MANDATORY_QUEST = 2;
    public final static int STEP_ACTION = 3;
    public final static int STEP_QUEST = 4;
    private static GameCore instance;
    private int life;
    private int money;
    private int currentStep = GameCore.STEP_START_GAME;
    private boolean gameOver = false;
    private boolean gameWin = false;
    private Save save;
    private ArrayList<String> information;
    private QuestManager questManager;
    private ItemManager itemManager;
    private PlaceManager placeManager;
    private NotificationManager notificationManager;

    public static GameCore getInstance() {
        if (instance == null) {
            instance = new GameCore();
        }
        return instance;
    }

    public GameCore() {
        this.save = new Save();
    }

    public boolean isGameWin() {
        return gameWin;
    }

    public void setGameWin() {
        this.gameWin = true;
    }

    private void init() {
        this.life = Config.getInstance().maxLife;
        this.money = Config.getInstance().startMoney;
        this.itemManager = new ItemManager();
        this.placeManager = new PlaceManager();
        this.questManager = new QuestManager();
        this.notificationManager = new NotificationManager();
        this.information = new ArrayList<String>();
    }

    public Save getSave() {
        return this.save;
    }

    public int getLife() {
        return this.life;
    }

    public int addLife(int addLife) {
        if (this.life == 0) {
            return this.life;
        }
        if (addLife < 0) {
            if (this.itemManager.hasItem("shield_ii")) {
                addLife += 4;
            } else if (this.itemManager.hasItem("shield")) {
                addLife += 2;
            }
            addLife = Math.min(-1, addLife);
        }
        int beforeLife = this.life;
        this.life = this.life + addLife;
        if (this.life > Config.getInstance().maxLife) {
            this.life = Config.getInstance().maxLife;
        }
        if (this.life <= 0) {
            this.life = 0;
        }
        this.save.saveProgress(Save.LIFE_KEY, this.life);
        int addedLife = this.life - beforeLife;
        if (addedLife != 0) {
            Dispatcher.getInstance().dispatch(Dispatcher.EVENT_LIFE_CHANGE);
            this.notificationManager
                    .addNotification(new Notification((addedLife < 0) ? "life-decrease" : "life-increase",
                            (addedLife < 0) ? "notification.life.decrease" : "notification.life.increase"));
        }
        return this.life;
    }

    public int getMoney() {
        return this.money;
    }

    public int addMoney(int addMoney) {
        int beforeMoney = this.money;
        this.money = this.money + addMoney;
        if (this.money < 0) {
            this.money = 0;
        }
        this.save.saveProgress(Save.MONEY_KEY, this.money);
        int addedMoney = this.money - beforeMoney;
        if (addedMoney != 0) {
            Dispatcher.getInstance().dispatch(Dispatcher.EVENT_MONEY_CHANGE);
            this.notificationManager
                    .addNotification(new Notification((addedMoney < 0) ? "money-decrease" : "money-increase",
                            (addedMoney < 0) ? "notification.money.decrease" : "notification.money.increase"));
        }
        return this.money;
    }

    public int getCurrentStep() {
        return this.currentStep;
    }

    public boolean setCurrentStep(int step) {
        if (this.gameOver) {
            StageScreen.getInstance().setStage(new FinalStage(FinalStage.TYPE_GAME_OVER));
            return false;
        }
        if (step < GameCore.STEP_WAIT)
            step = GameCore.STEP_WAIT;
        if (step > GameCore.STEP_QUEST)
            step = GameCore.STEP_QUEST;
        this.currentStep = step;
        this.save.saveProgress(Save.CURRENT_STEP_KEY, this.currentStep);
        return true;
    }

    public boolean canMoveToOpenPlace() {
        return this.getMoney() >= this.placeManager.getMoveCost();
    }

    public boolean search() {
        this.placeManager.addSearchedPlace(this.placeManager.getCurrentPlaceIndex());
        if (this.placeManager.getCurrentPlaceIndex() == this.placeManager.getCurrentSearchPlace().getIndex()) {
            this.placeManager.updateCurrentSearchPlace();
            return true;
        } else {
            return false;
        }
    }

    public QuestManager getQuestManager() {
        return this.questManager;
    }

    public void loadSavedParams() {
        this.init();
        int savedLife = this.save.loadLife();
        if (savedLife >= 0) {
            this.life = savedLife;
        }
        int savedMoney = this.save.loadMoney();
        if (savedMoney >= 0) {
            this.money = savedMoney;
        }
        int savedCurrentStep = this.save.loadCurrentStep();
        if (savedCurrentStep >= 0) {
            this.currentStep = savedCurrentStep;
        }
        ArrayList<Integer> searchPlaces = this.save.loadSearchPlaces();
        if (searchPlaces != null) {
            this.placeManager.setSearchPlaces(searchPlaces);
        }
        int currentSearchPlace = this.save.loadCurrentSearchPlace();
        if (currentSearchPlace >= 0) {
            this.placeManager.setCurrentSearchPlaceIndex(currentSearchPlace);
        }
        ArrayList<Integer> searchedPlaces = this.save.loadSearchedPlaces();
        if (searchedPlaces != null) {
            this.placeManager.setSearchedPlaces(searchedPlaces);
        }
        int savedCurrentPlace = this.save.loadCurrentPlace();
        if (savedCurrentPlace >= 0) {
            this.placeManager.setCurrentPlace(savedCurrentPlace);
        }
        int savedLastPlace = this.save.loadLastPlace();
        if (savedLastPlace >= 0) {
            this.placeManager.setLastPlace(savedLastPlace);
        }
        int stepCount = this.save.loadStepCount();
        if (stepCount > 0) {
            this.placeManager.setStepCount(stepCount);
        }
        HashMap<Integer, ArrayList<Integer>> savedMandatoryQuest = this.save.loadMandatoryQuest();
        if (savedMandatoryQuest != null) {
            this.questManager.setMandatoryQuests(savedMandatoryQuest);
        }
        HashMap<Integer, ArrayList<Integer>> savedPlaceQuest = this.save.loadPlaceQuest();
        if (savedPlaceQuest != null) {
            this.questManager.setPlaceQuests(savedPlaceQuest);
        }
        ArrayList<String> items = this.save.loadItems();
        if (items != null) {
            this.itemManager.setOwnItem(items);
        }
        ArrayList<String> information = this.save.loadInformation();
        if (information != null) {
            this.information = information;
        }
        ArrayList<String> receipts = this.save.loadReceipts();
        if (receipts != null) {
            this.itemManager.setOwnReceipts(receipts);
        }
        ArrayList<String> resources = this.save.loadResources();
        if (resources != null) {
            this.itemManager.setResources(resources);
        }
    }

    public boolean hasProgress() {
        return this.save.hasProgress();
    }

    public void initProgress() {
        this.save.initProgress();
        this.init();
        this.placeManager.generateSearchPlaces();
        this.itemManager.updateResources();
        this.itemManager.addStartMaterialsAndReceipts();
    }

    public void clearProgress() {
        this.save.clearProgress();
        instance = new GameCore();
    }

    public void gameWin() {
        StageScreen.getInstance().setStage(new SlideStage(SlideStage.TYPE_OUTRO));
    }

    public void gameOver() {
        this.gameOver = true;
    }

    public void addInformation(String information) {
        if (!this.hasInformation(information)) {
            this.information.add(information);
            this.save.saveProgress(Save.INFORMATION_KEY, this.information);
        }
    }

    public void removeInformation(String information) {
        if (this.hasInformation(information)) {
            this.information.remove(information);
            this.save.saveProgress(Save.INFORMATION_KEY, this.information);
        }
    }

    public boolean hasInformation(String information) {
        return this.information.contains(information);
    }

    public ItemManager getItemManager() {
        return this.itemManager;
    }

    public PlaceManager getPlaceManager() {
        return this.placeManager;
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    public void saveQuestAction(QuestAction action) {
        this.save.saveProgress(Save.QUEST_ACTION_KEY, action.getIndex());
    }

    public void nullQuestAction() {
        this.save.saveProgress(Save.QUEST_ACTION_KEY, -1);
    }

    public int getMoveDistance() {
        int addDistance = this.itemManager.hasItem("battery_ii") ? 3 : (this.itemManager.hasItem("battery") ? 1 : 0);
        return Config.getInstance().moveDistance + addDistance;
    }

    public static void dispose() {
        instance = null;
    }
}
