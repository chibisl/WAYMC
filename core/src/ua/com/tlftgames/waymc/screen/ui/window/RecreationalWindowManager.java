package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.CoolRandomizer;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Save;
import ua.com.tlftgames.waymc.Translator;
import ua.com.tlftgames.waymc.natification.Notification;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.stage.GameStage;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.Tutorial;
import ua.com.tlftgames.waymc.screen.ui.UIGroup;

public class RecreationalWindowManager extends TypeWindowManager {
    private final int ACTION_INVENTION_CLUB = 0;
    private final int ACTION_CASINO = 1;
    private final int ACTION_RACES = 2;
    private final int VARIANT_ENTER_CLUB = 0;
    private final int VARIANT_RETURN = 1;
    private final int VARIANT_BET_ON_LEADER = 2;
    private final int VARIANT_BET_ON_DARK_HORSE = 3;
    private final int VARIANT_BET_ON_OUTSIDER = 4;
    private final int VARIANT_BET_ON_NONE = 5;
    private final int VARIANT_BUY_FEW = 6;
    private final int VARIANT_BUY_AVERAGE = 7;
    private final int VARIANT_BUY_MANY = 8;
    private final int VARIANT_EXIT = 9;
    private final int RESULT_BAD = 0;
    private final int RESULT_GOOD = 1;
    private final int FIRST_LEADER = 0;
    private final int FIRST_DARK_HORSE = 1;
    private final int FIRST_OUTSIDER = 2;
    private int[] currentEvents;
    private float[] racesChances = { 0.4f, 0.1f };
    private float[] racesMultiply = { 1f, 3, 5 };
    private int[] casinoPrises = { 20, 50, 100 };
    private float[] casinoChances = { 0.2f, 0.35f, 0.55f };
    private CoolRandomizer<Integer> events;
    private String clubReceipt = "";
    private int casinoWin = 0;
    private int firstHorse = 0;
    private int betShift = 2;
    private int casinoShift = 6;

    public RecreationalWindowManager(UIGroup group) {
        super(group, "recreational", Tutorial.TUTORIAL_RECREATIONAL);
        ArrayList<Integer> eventsData = new ArrayList<Integer>();
        for (int i = this.ACTION_INVENTION_CLUB; i <= this.ACTION_RACES; i++) {
            eventsData.add(i);
        }
        events = new CoolRandomizer<Integer>(eventsData, eventsData.size() - 1);
        currentEvents = new int[] { 0, 1 };
    }

    public void setCurrentEvents(int[] events) {
        this.currentEvents = events;
        GameCore.getInstance().getSave().saveProgress(Save.RECREATIONAL_CURRENT_EVENT,
                this.currentEvents[0] * 10 + this.currentEvents[1]);
    }

    @Override
    public ArrayList<TextButton> getButtons() {
        ArrayList<TextButton> buttons = new ArrayList<TextButton>();
        for (int currentEvent : currentEvents) {
            switch (currentEvent) {
            case ACTION_INVENTION_CLUB:
                buttons.add(getClubButton());
                break;
            case ACTION_CASINO:
                buttons.add(getCasinoButton());
                break;
            case ACTION_RACES:
                buttons.add(getRacesButton());
                break;
            }
        }

        return buttons;
    }

    private TextButton getClubButton() {
        TextButton club = this.getHelper().createTextButton("btn.club");
        club.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RecreationalWindowManager.this.showClub();
            }
        });
        return club;
    }

    private TextButton getCasinoButton() {
        TextButton casino = this.getHelper().createTextButton("btn.casino");
        casino.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RecreationalWindowManager.this.showCasino();
            }
        });
        return casino;
    }

    private TextButton getRacesButton() {
        TextButton races = this.getHelper().createTextButton("btn.races");
        races.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RecreationalWindowManager.this.showRaces();
            }
        });
        return races;
    }

    protected void showClub() {
        StageScreen.getInstance().getTracker().trackEvent("Recreational", "action", "club", 1);
        clubReceipt = GameCore.getInstance().getItemManager().getRandomItemReceipt();
        GameCore.getInstance().getSave().saveProgress(Save.CLUB_RECEIPT_KEY, clubReceipt);
        this.setAction(ACTION_INVENTION_CLUB);
        this.showActionStartText();
    }

    protected void showCasino() {
        StageScreen.getInstance().getTracker().trackEvent("Recreational", "action", "casino", 1);
        casinoWin = (int) (Math.random() * (Config.getInstance().casinoMaxWin - Config.getInstance().casinoMinWin + 1))
                + Config.getInstance().casinoMinWin;
        GameCore.getInstance().getSave().saveProgress(Save.CASINO_WIN_KEY, casinoWin);
        this.setAction(ACTION_CASINO);
        this.showActionStartText();
    }

    protected void showRaces() {
        StageScreen.getInstance().getTracker().trackEvent("Recreational", "action", "races", 1);
        firstHorse = FIRST_LEADER;
        if (Math.random() <= this.racesChances[1]) {
            firstHorse = FIRST_OUTSIDER;
        } else if (Math.random() <= this.racesChances[0]) {
            firstHorse = FIRST_DARK_HORSE;
        }
        GameCore.getInstance().getSave().saveProgress(Save.FIRST_HORSE_KEY, firstHorse);
        this.setAction(ACTION_RACES);
        this.showActionStartText();
    }

    protected ArrayList<String> getVars() {
        ArrayList<String> vars = new ArrayList<String>();
        switch (this.getAction()) {
        case ACTION_INVENTION_CLUB:
            vars.add(Integer.toString(Config.getInstance().clubEnterPrice));
            vars.add(Translator.getInstance().getMoneyText(Config.getInstance().clubEnterPrice));
            break;
        case ACTION_RACES:
            vars.add(Integer.toString(Config.getInstance().racesBet));
            vars.add(Translator.getInstance().getMoneyText(Config.getInstance().racesBet));
            break;
        case ACTION_CASINO:
            for (int prise : this.casinoPrises) {
                vars.add(Integer.toString(prise));
            }
            vars.add(Translator.getInstance().getMoneyText(this.casinoPrises[2]));
        }
        return vars;
    }

    @Override
    public ArrayList<Integer> getVariants() {
        ArrayList<Integer> variants = new ArrayList<Integer>();
        switch (getAction()) {
        case ACTION_INVENTION_CLUB:
            variants.add(VARIANT_ENTER_CLUB);
            variants.add(VARIANT_RETURN);
            break;
        case ACTION_RACES:
            variants.add(VARIANT_BET_ON_LEADER);
            variants.add(VARIANT_BET_ON_DARK_HORSE);
            variants.add(VARIANT_BET_ON_OUTSIDER);
            variants.add(VARIANT_BET_ON_NONE);
            break;
        case ACTION_CASINO:
            variants.add(VARIANT_BUY_FEW);
            variants.add(VARIANT_BUY_AVERAGE);
            variants.add(VARIANT_BUY_MANY);
            variants.add(VARIANT_EXIT);
            break;
        }
        return variants;
    }

    @Override
    public boolean canDoVariant(int variant) {
        if ((variant == VARIANT_ENTER_CLUB && GameCore.getInstance().getMoney() < Config.getInstance().clubEnterPrice)
                || (variant >= VARIANT_BET_ON_LEADER && variant <= VARIANT_BET_ON_OUTSIDER
                        && GameCore.getInstance().getMoney() < Config.getInstance().racesBet)
                || (variant >= VARIANT_BUY_FEW && variant <= VARIANT_BUY_MANY
                        && GameCore.getInstance().getMoney() < this.casinoPrises[variant - casinoShift])) {
            GameCore.getInstance().getNotificationManager()
                    .addNotification(new Notification("money", "notification.money.not.match"));
            return false;
        }
        return true;
    }

    @Override
    protected void updateResult() {
        if (getVariant() == VARIANT_ENTER_CLUB) {
            this.setResult(!GameCore.getInstance().getItemManager().hasReceipt(clubReceipt) ? RESULT_GOOD : RESULT_BAD);
        } else if (getVariant() >= VARIANT_BET_ON_LEADER && getVariant() <= VARIANT_BET_ON_OUTSIDER) {
            this.setResult((getVariant() - betShift == firstHorse) ? RESULT_GOOD : RESULT_BAD);
        } else if (getVariant() >= VARIANT_BUY_FEW && getVariant() <= VARIANT_BUY_MANY) {
            float test = this.casinoChances[getVariant() - casinoShift];
            if (GameCore.getInstance().getItemManager().hasItem("playing_club_ring")) {
                test *= 2.5f;
            }
            this.setResult(test > Math.random() ? RESULT_GOOD : RESULT_BAD);
        }
    }

    protected ArrayList<String> getResultVars() {
        ArrayList<String> vars = new ArrayList<String>();
        switch (this.getVariant()) {
        case VARIANT_ENTER_CLUB:
            vars.add(clubReceipt);
            break;
        case VARIANT_BET_ON_LEADER:
        case VARIANT_BET_ON_DARK_HORSE:
        case VARIANT_BET_ON_OUTSIDER:
            vars.add("races.horse." + firstHorse);
            int win = (int) (Config.getInstance().racesBet * this.racesMultiply[firstHorse]);
            vars.add(Integer.toString(win));
            vars.add(Translator.getInstance().getMoneyText(win));
            break;
        case VARIANT_BUY_FEW:
        case VARIANT_BUY_AVERAGE:
        case VARIANT_BUY_MANY:
            vars.add(Integer.toString(casinoWin));
            vars.add(Translator.getInstance().getMoneyText(casinoWin));
            break;
        }
        return vars;
    }

    protected String getResultAddText() {
        StringBuilder addText = new StringBuilder();
        switch (this.getVariant()) {
        case VARIANT_ENTER_CLUB:
            if (getResult() == RESULT_GOOD) {
                addText.append("+").append(GameCore.getInstance().getItemManager().getItem(clubReceipt).getInfo())
                        .append("+club.find.receipt.added");
            }
            break;
        }
        return addText.toString();
    }

    @Override
    protected String getResultText() {
        if (getAction() == ACTION_RACES) {
            String resultText = "recreational.result.start";
            if (getResult() == RESULT_BAD) {
                resultText += "+recreational.result.1.0";
            } else {
                resultText += "+recreational.result.1.1";
            }
            return resultText;
        } else if (getAction() == ACTION_CASINO) {
            String resultText;
            if (getResult() == RESULT_BAD) {
                resultText = "+recreational.result.2.0";
            } else {
                resultText = "+recreational.result.2.1";
            }
            return resultText;
        }
        return super.getResultText();
    }

    public boolean hasTextForResult() {
        return !(getVariant() == VARIANT_RETURN || getVariant() == VARIANT_BET_ON_NONE || getVariant() == VARIANT_EXIT);
    }

    @Override
    public void action() {
        int variant = getVariant();
        String label = "";
        switch (variant) {
        case VARIANT_ENTER_CLUB:
            StageScreen.getInstance().getTracker().trackEvent("Recreational", "choice", "clubEnter", 1);
            if (getResult() == RESULT_GOOD) {
                GameCore.getInstance().getItemManager().addOwnReceipt(clubReceipt);
                GameCore.getInstance().addMoney(-1 * Config.getInstance().clubEnterPrice);
            }
            break;
        case VARIANT_BET_ON_LEADER:
        case VARIANT_BET_ON_DARK_HORSE:
        case VARIANT_BET_ON_OUTSIDER:
            label = (variant == VARIANT_BET_ON_LEADER ? "Leader"
                    : (variant == VARIANT_BET_ON_DARK_HORSE ? "DarkHorse" : "Outsider"));
            StageScreen.getInstance().getTracker().trackEvent("Recreational", "choice", "raceBet" + label, 1);
            if (getResult() == RESULT_GOOD) {
                GameCore.getInstance().addMoney((int) (Config.getInstance().racesBet * this.racesMultiply[firstHorse]));
            } else {
                GameCore.getInstance().addMoney(-1 * Config.getInstance().racesBet);
            }
            break;
        case VARIANT_BUY_FEW:
        case VARIANT_BUY_AVERAGE:
        case VARIANT_BUY_MANY:
            label = (variant == VARIANT_BUY_FEW ? "Few" : (variant == VARIANT_BUY_AVERAGE ? "Average" : "Many"));
            StageScreen.getInstance().getTracker().trackEvent("Recreational", "choice", "casinoBuy" + label, 1);
            if (getResult() == RESULT_GOOD) {
                GameCore.getInstance().addMoney(casinoWin);
            } else {
                GameCore.getInstance().addMoney(-1 * this.casinoPrises[getVariant() - casinoShift]);
            }
            break;
        }
        this.setQuestStartText("quest.after." + (this.getResult() == RESULT_BAD ? "fail" : "success"));
        this.finishAction();
        this.startQuest();
    }

    @Override
    public boolean getStartWindow() {
        if (needTutorial) {
            this.getWindow().setPlaceImageTexture();
            this.showActionResult("tutorial." + tutorial, null, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    RecreationalWindowManager.this.showTodayEvents();
                }
            });
            needTutorial = false;
            Tutorial.setTutorialShowed(tutorial);
        } else {
            this.showTodayEvents();
        }
        return true;
    }

    public void showTodayEvents() {
        updateEvents();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < currentEvents.length; i++) {
            if (i == 0) {
                text.append("recreational.start+");
            }
            if (i == 1) {
                text.append("+recreational.and+");
            }
            switch (currentEvents[i]) {
            case ACTION_INVENTION_CLUB:
                text.append("recreational.invention.club");
                break;
            case ACTION_CASINO:
                text.append("recreational.casino");
                break;
            case ACTION_RACES:
                text.append("recreational.races");
                break;
            }
        }
        this.getWindow().setPlaceImageTexture();
        this.showActionResult(text.toString(), null, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                RecreationalWindowManager.this.showActions();
            }
        });
    }

    public void updateEvents() {
        if (GameCore.getInstance().getItemManager().hasAllReceipts()) {
            this.setCurrentEvents(new int[] { 1, 2 });
            return;
        }
        int[] events = new int[2];
        events[0] = this.events.getRandomElement();
        events[1] = this.events.getRandomElement();
        this.setCurrentEvents(events);
    }

    @Override
    public void loadParams() {
        int loadedEvent = GameCore.getInstance().getSave().loadRecreationalCurrentEvent();
        int[] events = { loadedEvent / 10, loadedEvent % 10 };
        this.setCurrentEvents(events);
        this.clubReceipt = GameCore.getInstance().getSave().loadClubReceipt();
        this.firstHorse = GameCore.getInstance().getSave().loadFirstHorse();
        this.casinoWin = GameCore.getInstance().getSave().loadCasinoWin();
        super.loadParams();
    }

    @Override
    protected int getSound() {
        return GameStage.RECREATIONAL_SOUND;
    }
}
