package ua.com.tlftgames.waymc.screen.ui.window;

import java.util.ArrayList;

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

public class IndustrialWindowManager extends TypeWindowManager {
    public final static int VARIANT_HARD = 0;
    public final static int VARIANT_SIMPLE = 1;
    public final static int VARIANT_NONE = 2;
    public final static int RESULT_BAD = 0;
    public final static int RESULT_NORMAL = 1;
    public final static int RESULT_GOOD = 2;
    public final static int ACTION_WORK_ALL = 0;
    public final static int ACTION_WORK_HARD = 1;
    public final static int ACTION_WORK_SIMPLE = 2;
    private int workPay = 10;
    private int workSimplePay = 10;

    public IndustrialWindowManager(UIGroup group) {
        super(group, "industrial", Tutorial.TUTORIAL_INDUSTRIAL);
    }

    protected void showWorkshop() {
        WorkshopWindowBody workshopBody = new WorkshopWindowBody(this);

        StageScreen.getInstance().getTracker().trackEvent("Industrial", "action", "workshop", 1);
        this.getWindow().updateBody(workshopBody);
        this.getWindow().setPlaceImageTexture();
        this.getWindow().setBottomButtons(this.getHelper().createReturnButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                IndustrialWindowManager.this.setQuestStartText("quest.after.workshop");
                IndustrialWindowManager.this.startQuest();
            }
        }));
        this.getWindow().show();
    }

    @Override
    public ArrayList<TextButton> getButtons() {
        ArrayList<TextButton> buttons = new ArrayList<TextButton>();
        TextButton work = this.getHelper().createTextButton("btn.work");
        work.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                IndustrialWindowManager.this.findWork();
            }
        });
        buttons.add(work);
        TextButton workshop = this.getHelper().createTextButton("btn.workshop");
        workshop.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameCore.getInstance().getItemManager().getOwnResources().size() > 0) {
                    IndustrialWindowManager.this.showWorkshop();
                } else {
                    GameCore.getInstance().getNotificationManager()
                            .addNotification(new Notification("inventory", "notification.resources.not.found"));
                }
            }
        });
        buttons.add(workshop);
        return buttons;
    }

    protected void findWork() {
        StageScreen.getInstance().getTracker().trackEvent("Industrial", "action", "work", 1);
        this.workPay = Config.getInstance().workAddMoney + (int) (Math.random() * 11);
        GameCore.getInstance().getSave().saveProgress(Save.INDUSTRIAL_WORK_PAY_KEY, this.workPay);
        this.workSimplePay = this.workPay / 2;
        if (Math.random() < 0.5f) {
            this.setAction(ACTION_WORK_ALL);
        } else if (Math.random() < 0.5f) {
            this.setAction(ACTION_WORK_HARD);
        } else {
            this.setAction(ACTION_WORK_SIMPLE);
        }
        this.showActionStartText();
    }

    @Override
    protected ArrayList<String> getVars() {
        ArrayList<String> vars = new ArrayList<String>();
        if (this.getAction() == ACTION_WORK_ALL || this.getAction() == ACTION_WORK_HARD) {
            vars.add(Integer.toString(workPay));
            vars.add(Translator.getInstance().getMoneyText(workPay));
        }
        if (this.getAction() == ACTION_WORK_ALL || this.getAction() == ACTION_WORK_SIMPLE) {
            vars.add(Integer.toString(workSimplePay));
            vars.add(Translator.getInstance().getMoneyText(workSimplePay));
        }
        return vars;
    }

    protected void updateResult() {
        setResult((Math.random() < 0.8f) ? RESULT_NORMAL : ((Math.random() < 0.65f) ? RESULT_GOOD : RESULT_BAD));
    }

    public void action() {
        if (this.getVariant() != VARIANT_NONE) {
            int addMoney = workPay;
            int subLife = -1 * Config.getInstance().workSubLife;
            if (this.getVariant() == VARIANT_HARD) {
                StageScreen.getInstance().getTracker().trackEvent("Industrial", "choice", "workHard", 1);
                switch (this.getResult()) {
                case RESULT_BAD:
                    subLife = subLife * 2;
                    break;
                case RESULT_GOOD:
                    subLife = subLife / 2;
                    break;
                }
            } else {
                StageScreen.getInstance().getTracker().trackEvent("Industrial", "choice", "workSimple", 1);
                addMoney = workSimplePay;
                subLife = 0;
                switch (this.getResult()) {
                case RESULT_BAD:
                    addMoney = 0;
                    break;
                case RESULT_GOOD:
                    addMoney = (int) (addMoney * 1.5f);
                    break;
                }
            }
            if (addMoney > 0)
                GameCore.getInstance().addMoney(addMoney);
            if (subLife < 0)
                GameCore.getInstance().addLife(subLife);
        }
        this.setQuestStartText("quest.after.work." + (this.getVariant() == VARIANT_NONE ? "fail" : "done"));
        this.finishAction();
        this.startQuest();
    }

    @Override
    public void loadParams() {
        this.workPay = GameCore.getInstance().getSave().loadIndustrialWorkPay();
        this.workSimplePay = this.workPay / 2;
        super.loadParams();
    }

    public boolean hasTextForResult() {
        return !(getVariant() == VARIANT_NONE || getResult() == RESULT_NORMAL);
    }

    @Override
    public ArrayList<Integer> getVariants() {
        ArrayList<Integer> variants = new ArrayList<Integer>();
        switch (this.getAction()) {
        case ACTION_WORK_ALL:
            variants.add(VARIANT_HARD);
            variants.add(VARIANT_SIMPLE);
            variants.add(VARIANT_NONE);
            break;
        case ACTION_WORK_HARD:
            variants.add(VARIANT_HARD);
            variants.add(VARIANT_NONE);
            break;
        case ACTION_WORK_SIMPLE:
            variants.add(VARIANT_SIMPLE);
            variants.add(VARIANT_NONE);
            break;
        }
        return variants;
    }
}
