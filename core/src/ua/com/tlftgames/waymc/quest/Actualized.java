package ua.com.tlftgames.waymc.quest;

import ua.com.tlftgames.waymc.GameCore;

public class Actualized {
    private int needLife = 0;
    private int unneedLife = 100;
    private int needMoney = -1000;
    private int unneedMoney = 9999999;
    private String[] needItems;
    private String[] needReceipts;
    private String[] needInformation;

    public void setNeedLife(int life) {
        this.needLife = life;
    }

    public void setUnneedLife(int life) {
        this.unneedLife = life;
    }

    public void setNeedMoney(int money) {
        this.needMoney = money;
    }

    public void setUnneedMoney(int money) {
        this.unneedMoney = money;
    }

    public void setNeedItems(String[] items) {
        this.needItems = items;
    }

    public void setNeedReceipts(String[] receipts) {
        this.needReceipts = receipts;
    }

    public void setNeedInformation(String[] information) {
        this.needInformation = information;
    }

    public boolean isMoneyNeed() {
        return GameCore.getInstance().getMoney() < this.needMoney;
    }

    public boolean isActual() {
        if (GameCore.getInstance().getLife() < this.needLife)
            return false;
        if (GameCore.getInstance().getLife() >= this.unneedLife)
            return false;
        if (GameCore.getInstance().getMoney() >= this.unneedMoney)
            return false;
        if (needItems != null) {
            for (String item : this.needItems) {
                if (item.startsWith("-")) {
                    if (GameCore.getInstance().getItemManager().hasItem(item.substring(1)))
                        return false;
                } else if (!GameCore.getInstance().getItemManager().hasItem(item))
                    return false;
            }
        }
        if (needReceipts != null) {
            for (String receipt : this.needReceipts) {
                if (receipt.startsWith("-")) {
                    if (GameCore.getInstance().getItemManager().hasReceipt(receipt.substring(1)))
                        return false;
                } else if (!GameCore.getInstance().getItemManager().hasReceipt(receipt))
                    return false;
            }
        }
        if (needInformation != null) {
            for (String information : this.needInformation) {
                if (information.startsWith("-")) {
                    if (GameCore.getInstance().hasInformation(information.substring(1)))
                        return false;
                } else if (!GameCore.getInstance().hasInformation(information))
                    return false;
            }
        }
        return true;
    }
}
