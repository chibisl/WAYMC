package ua.com.tlftgames.waymc.quest;

import ua.com.tlftgames.waymc.GameCore;

public class QuestAction extends Actualized {
    private String text;
    private int index;
    private int moveTo = -1;
    private int changeLife = 0;
    private int changeMoney = 0;
    private String changeItem;
    private String changeReceipt;
    private String changeInformation;
    private int addQuest = -1;
    private boolean endGame = false;
    private Quest quest;

    public QuestAction(String text, int index) {
        this.text = text;
        this.index = index;
    }

    public String getText() {
        return this.text;
    }

    public int getIndex() {
        return index;
    }

    public void setMoveTo(int moveTo) {
        this.moveTo = moveTo;
    }

    public void setChangeLife(int changeLife) {
        this.changeLife = changeLife;
    }

    public void setChangeMoney(int changeMoney) {
        this.changeMoney = changeMoney;
    }

    public void setChangeItem(String changeItem) {
        this.changeItem = changeItem;
    }

    public void setChangeReceipt(String changeReceipt) {
        this.changeReceipt = changeReceipt;
    }

    public void setChangeInformation(String changeInformation) {
        this.changeInformation = changeInformation;
    }

    public void setAddQuest(int addQuest) {
        this.addQuest = addQuest;
    }

    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public Quest getQuest() {
        return this.quest;
    }

    public void action() {
        if (quest.isOnce()) {
            GameCore.getInstance().getQuestManager().removeQuest(quest);
        }
        if (endGame) {
            GameCore.getInstance().gameOver();
        }
        if (moveTo >= 0) {
            GameCore.getInstance().getPlaceManager().setCurrentPlace(moveTo, true);
        }
        if (changeLife != 0) {
            GameCore.getInstance().addLife(changeLife);
        }
        if (changeMoney != 0) {
            GameCore.getInstance().addMoney(changeMoney);
        }
        if (changeItem != null) {
            if (changeItem.startsWith("-")) {
                GameCore.getInstance().getItemManager().removeOwnItem(changeItem.substring(1));
            } else if (changeItem.startsWith("+")) {
                GameCore.getInstance().getItemManager().addOwnItem(changeItem.substring(1));
            } else {
                GameCore.getInstance().getItemManager().addOwnItem(changeItem);
            }
        }
        if (changeReceipt != null) {
            if (changeReceipt.startsWith("-")) {
                GameCore.getInstance().getItemManager().removeOwnReceipt(changeReceipt.substring(1));
            } else if (changeReceipt.startsWith("+")) {
                GameCore.getInstance().getItemManager().addOwnReceipt(changeReceipt.substring(1));
            } else {
                GameCore.getInstance().getItemManager().addOwnReceipt(changeReceipt);
            }
        }
        if (changeInformation != null) {
            if (changeInformation.startsWith("-")) {
                GameCore.getInstance().removeInformation(changeInformation.substring(1));
            } else if (changeInformation.startsWith("+")) {
                GameCore.getInstance().addInformation(changeInformation.substring(1));
            } else {
                GameCore.getInstance().addInformation(changeInformation);
            }
        }
        if (addQuest >= 0) {
            GameCore.getInstance().getQuestManager().addQuest(addQuest);
        }
    }

    @Override
    public boolean isActual() {
        return !this.isMoneyNeed() && super.isActual();
    }
}
