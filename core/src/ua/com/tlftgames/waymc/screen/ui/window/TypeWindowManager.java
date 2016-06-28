package ua.com.tlftgames.waymc.screen.ui.window;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Settings;
import ua.com.tlftgames.waymc.natification.Notification;
import ua.com.tlftgames.waymc.quest.Quest;
import ua.com.tlftgames.waymc.quest.QuestAction;
import ua.com.tlftgames.waymc.quest.QuestChoice;
import ua.com.tlftgames.waymc.screen.StageScreen;
import ua.com.tlftgames.waymc.screen.ui.TextButton;
import ua.com.tlftgames.waymc.screen.ui.Tutorial;
import ua.com.tlftgames.waymc.screen.ui.UIGroup;

import java.util.ArrayList;

public abstract class TypeWindowManager extends ActionWindowManager {
    private String questStartText = null;
    protected boolean needTutorial = true;
    protected int tutorial;

    public TypeWindowManager(UIGroup group, String pref, int tutorial) {
        super(group, pref);
        this.tutorial = tutorial;
        needTutorial = Settings.getInstance().getTutorialEnable() && !Tutorial.isTutorialShowed(tutorial);
    }

    public void setQuestStartText(String text) {
        this.questStartText = text;
    }

    public void showActions() {
        this.setQuestStartText(null);
        if (!GameCore.getInstance().setCurrentStep(GameCore.STEP_ACTION))
            return;
        this.getWindow().updateBody(new ChoicesWindowBody(this.getButtons()));
        this.getWindow().setPlaceImageTexture();
        this.getWindow().setBottomButtons(this.getHelper().createReturnButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TypeWindowManager.this.getWindow().hide();
            }
        }));
        this.getWindow().show();
    }

    protected void startMandatoryQuest() {
        Quest quest = GameCore.getInstance().getQuestManager().getMandatoryQuest();
        if (quest != null) {
            this.showQuest(quest, true);
            return;
        }
        startPlaceWindow();
    }

    protected void startPlaceWindow() {
        if (!this.getStartWindow())
            this.showActions();
    }

    protected void startQuest() {
        Quest quest = GameCore.getInstance().getQuestManager().getNextQuest();
        if (quest != null) {
            this.showQuest(quest, false);
            return;
        }
        this.getUIGroup().hideWindowAndWait();
    }

    public boolean getStartWindow() {
        if (needTutorial) {
            //TODO: change to tutorial image
            this.getWindow().setPlaceImageTexture();
            this.showActionResult("tutorial." + tutorial, null, new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showActions();
                }
            });
            needTutorial = false;
            Tutorial.setTutorialShowed(tutorial);
            return true;
        }
        return false;
    }

    public void showQuest(Quest quest, boolean isMandatory) {
        GameCore.getInstance().nullQuestAction();
        int step = GameCore.STEP_QUEST;
        if (isMandatory) {
            step = GameCore.STEP_MANDATORY_QUEST;
            questStartText = null;
        }
        if (!GameCore.getInstance().setCurrentStep(step))
            return;
        this.showQuestStart(quest);
    }

    public void showQuestStart(final Quest quest) {
        StageScreen.getInstance().getTracker().trackNonInteractionEvent("Quests", "start", quest.getText(), 1);
        String text = (questStartText == null) ? quest.getText() : questStartText + "+" + quest.getText();
        InfoWindowBody questInfo = new InfoWindowBody(text, null, this.getHelper());
        this.getWindow().updateBody(questInfo);
        if (quest.getImage() != null) {
            this.getWindow().setImageTexture(this.getAtlas().findRegion(quest.getImage()));
        } else {
            this.getWindow().setPlaceImageTexture();
        }
        this.getWindow().setBottomButtons(this.getHelper().createNextButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TypeWindowManager.this.showQuestChoices(quest);
            }
        }));
        this.getWindow().show();
    }

    public void showQuestChoices(final Quest quest) {
        ArrayList<QuestChoice> choices = quest.getChoices();
        int choicesCount = choices.size();
        ArrayList<TextButton> buttons = new ArrayList<TextButton>();
        for (int i = 0; i < choicesCount; i++) {
            TextButton btn = this.createChoice(choices.get(i));
            buttons.add(btn);
        }
        ChoicesWindowBody choicesWindow = new ChoicesWindowBody(buttons);
        this.getWindow().updateBody(choicesWindow);
        if (quest.getImage() != null) {
            this.getWindow().setImageTexture(this.getAtlas().findRegion(quest.getImage()));
        } else {
            this.getWindow().setPlaceImageTexture();
        }
        this.getWindow().setBottomButtons(this.getHelper().createBackButton(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                TypeWindowManager.this.showQuestStart(quest);
            }
        }));
        this.getWindow().show();
    }

    private TextButton createChoice(QuestChoice choice) {
        TextButton btn = this.getHelper().createTextButton(choice.getText());
        btn.setUserObject(choice.getRandomAction());
        ClickListener listener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final QuestAction questAction = ((QuestAction) event.getListenerActor().getUserObject());
                TypeWindowManager.this.showQuestAction(questAction);
            }
        };
        if (choice.isMoneyNeed()) {
            listener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GameCore.getInstance().getNotificationManager()
                            .addNotification(new Notification("money", "notification.money.not.match"));
                }
            };
        }
        btn.addListener(listener);
        return btn;
    }

    public void showQuestAction(final QuestAction action) {
        GameCore.getInstance().saveQuestAction(action);
        StageScreen.getInstance().getTracker().trackEvent("Quests", "action", action.getText(), 1);
        if (action.getQuest().getImage() != null) {
            this.getWindow().setImageTexture(this.getAtlas().findRegion(action.getQuest().getImage()));
        } else {
            this.getWindow().setPlaceImageTexture();
        }
        this.showActionResult(action.getText(), null, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.action();
                if (GameCore.getInstance().getLife() > 0) {
                    TypeWindowManager.this.getUIGroup().getStage().getWorld().updateAttentions();
                    if (GameCore.getInstance().getCurrentStep() == GameCore.STEP_MANDATORY_QUEST) {
                        TypeWindowManager.this.startPlaceWindow();
                    } else {
                        TypeWindowManager.this.getUIGroup().hideWindowAndWait();
                    }
                }
            }
        });
    }

    public abstract ArrayList<TextButton> getButtons();

    public abstract void action();

    public abstract ArrayList<Integer> getVariants();

    protected abstract void updateResult();
}
