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

public class MerchantWindowManager extends TypeWindowManager {
    public final static int VARIANT_BUY_FIRST = 0;
    public final static int VARIANT_BUY_SECOND = 1;
    public final static int VARIANT_BUY_THIRD = 2;
    public final static int VARIANT_CANCEL = 3;
    public final static int VARIANT_SALE = 4;
    public final static int VARIANT_SALE_CANCEL = 5;
    public final static int RESULT_BAD = 0;
    public final static int RESULT_NORMAL = 1;
    public final static int RESULT_GOOD = 2;
    public final static int ACTION_SEARCH = 0;
    public final static int ACTION_SALE = 1;
    private ArrayList<String> searchItems = new ArrayList<String>();
    private String saleItem = "";

    public MerchantWindowManager(UIGroup group) {
        super(group, "merchant", Tutorial.TUTORIAL_MERCHANT);
    }

    @Override
    public ArrayList<TextButton> getButtons() {
        ArrayList<TextButton> buttons = new ArrayList<TextButton>();
        TextButton searchItems = this.getHelper().createTextButton("btn.search.items");
        searchItems.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MerchantWindowManager.this.searchItems();
            }
        });
        buttons.add(searchItems);

        TextButton saleItems = this.getHelper().createTextButton("btn.sale.items");
        saleItems.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameCore.getInstance().getItemManager().getOwnItems().size() > 0) {
                    MerchantWindowManager.this.saleItems();
                } else {
                    GameCore.getInstance().getNotificationManager()
                            .addNotification(new Notification("inventory", "notification.items.not.found"));
                }
            }
        });
        buttons.add(saleItems);

        return buttons;
    }

    private int getSearchItemsCount() {
        return GameCore.getInstance().getItemManager().hasItem("eyeglasses_ii") ? 3
                : (GameCore.getInstance().getItemManager().hasItem("eyeglasses") ? 2 : 1);
    }

    private int getItemCost(String item) {
        int cost = GameCore.getInstance().getItemManager().getItem(item).getCost();
        if (GameCore.getInstance().getItemManager().hasItem("medal")) {
            cost = cost - (int) (cost * Config.getInstance().saleLostPercent);
        }
        return cost;
    }

    private int getItemSaleCost(String item) {
        return (int) (GameCore.getInstance().getItemManager().getItem(item).getCost()
                * (1 - Config.getInstance().saleLostPercent));
    }

    private void searchItems() {
        StageScreen.getInstance().getTracker().trackEvent("Merchant", "action", "searchItems", 1);
        this.searchItems.clear();
        int itemsCount = this.getSearchItemsCount();
        for (int i = 0; i < itemsCount; i++) {
            this.searchItems.add(GameCore.getInstance().getItemManager().getItemFromMarket());
        }
        GameCore.getInstance().getSave().saveProgress(Save.SEARCH_ITEMS_KEY, this.searchItems);
        this.setAction(ACTION_SEARCH);
        this.showActionStartText();
    }

    private void saleItems() {
        StageScreen.getInstance().getTracker().trackEvent("Merchant", "action", "saleItems", 1);
        this.saleItem = GameCore.getInstance().getItemManager().getItemForSale();
        GameCore.getInstance().getSave().saveProgress(Save.SALE_ITEM_KEY, this.saleItem);
        this.setAction(ACTION_SALE);
        this.showActionStartText();
    }

    @Override
    protected String getStartAddText() {
        String addText = super.getStartAddText();
        if (getAction() == ACTION_SEARCH && this.searchItems.size() > 1) {
            addText += "+search.add.second.item";
            if (this.searchItems.size() > 2) {
                addText += "+search.add.third.item";
            }
            addText += "+search.buy.quick";
        }
        return addText;
    }

    @Override
    protected ArrayList<String> getVars() {
        ArrayList<String> vars = new ArrayList<String>();
        if (this.getAction() == ACTION_SEARCH) {
            for (String item : this.searchItems) {
                vars.add(item + ".use");
                int price = this.getItemCost(item);
                vars.add(Integer.toString(price));
                vars.add(Translator.getInstance().getMoneyText(price));
            }
        } else if (getAction() == ACTION_SALE) {
            vars.add(this.saleItem);
            int price = this.getItemSaleCost(this.saleItem);
            vars.add(Integer.toString(price));
            vars.add(Translator.getInstance().getMoneyText(price));
        }
        return vars;
    }

    @Override
    public ArrayList<Integer> getVariants() {
        ArrayList<Integer> variants = new ArrayList<Integer>();
        switch (this.getAction()) {
        case ACTION_SEARCH:
            variants.add(VARIANT_BUY_FIRST);
            if (this.searchItems.size() > 1) {
                variants.add(VARIANT_BUY_SECOND);
            }
            if (this.searchItems.size() > 2) {
                variants.add(VARIANT_BUY_THIRD);
            }
            variants.add(VARIANT_CANCEL);
            break;
        case ACTION_SALE:
            variants.add(VARIANT_SALE);
            variants.add(VARIANT_SALE_CANCEL);
        }
        return variants;
    }

    @Override
    protected ArrayList<String> getVariantVars(int variant) {
        if (variant >= VARIANT_BUY_FIRST && variant <= VARIANT_BUY_THIRD) {
            ArrayList<String> vars = new ArrayList<String>();
            String item = this.searchItems.get(variant);
            vars.add(item + ".use");
            int price = this.getItemCost(item);
            vars.add(Integer.toString(price));
            vars.add(Translator.getInstance().getMoneyText(price));
            return vars;
        }
        return null;
    }

    @Override
    protected String getVariantText(int variant) {
        if (variant >= VARIANT_BUY_FIRST && variant <= VARIANT_BUY_THIRD) {
            return "merchant.variant.search";
        }
        return null;
    }

    @Override
    public boolean canDoVariant(int variant) {
        if (variant >= VARIANT_BUY_FIRST && variant <= VARIANT_BUY_THIRD) {
            if (GameCore.getInstance().getMoney() < this.getItemCost(this.searchItems.get(variant))) {
                GameCore.getInstance().getNotificationManager()
                        .addNotification(new Notification("money", "notification.money.not.match"));
                return false;
            }
            if (variant >= VARIANT_BUY_FIRST && variant <= VARIANT_BUY_THIRD
                    && GameCore.getInstance().getItemManager().getOwnItems().size() == Config.getInstance().itemsMaxCount) {
                GameCore.getInstance().getNotificationManager()
                        .addNotification(new Notification("inventory", "notification.items.max.count.reached"));
                return false;
            }
        }
        if (variant == VARIANT_SALE && !GameCore.getInstance().getItemManager().hasItem(this.saleItem)) {
            GameCore.getInstance().getNotificationManager().addNotification(
                    new Notification(GameCore.getInstance().getItemManager().getItem(this.saleItem).getImage(),
                            "notification.item.not.found"));
            return false;
        }
        return true;
    }

    @Override
    protected void updateResult() {
        if ((getVariant() >= VARIANT_BUY_FIRST && getVariant() <= VARIANT_BUY_THIRD) || getVariant() == VARIANT_SALE) {
            if (getVariant() == VARIANT_SALE) {
                GameCore.getInstance().getItemManager().removeOwnItem(this.saleItem);
            }
            this.setResult(
                    (Math.random() < 0.8f) ? RESULT_NORMAL : ((Math.random() < 0.65f) ? RESULT_GOOD : RESULT_BAD));
        }
    }

    @Override
    public boolean hasTextForResult() {
        return !(getVariant() == VARIANT_CANCEL || getVariant() == VARIANT_SALE_CANCEL || getResult() == RESULT_NORMAL);
    }

    @Override
    protected String getResultText() {
        if (getAction() == ACTION_SEARCH) {
            return "merchant.result.0." + getResult();
        }
        return super.getResultText();
    }

    @Override
    public void loadParams() {
        this.searchItems = GameCore.getInstance().getSave().loadSearchItems();
        this.saleItem = GameCore.getInstance().getSave().loadSaleItem();
        super.loadParams();
    }

    @Override
    public void action() {
        if (getVariant() >= VARIANT_BUY_FIRST && getVariant() <= VARIANT_BUY_THIRD) {
            String item = this.searchItems.get(getVariant());
            int price = this.getItemCost(item);
            if (getResult() == RESULT_GOOD) {
                price = (int) (price * (1 - Config.getInstance().saleLostPercent));
            }
            if (getResult() == RESULT_BAD) {
                item = null;
            }
            if (item != null) {
                GameCore.getInstance().getItemManager().addOwnItem(item);
            }
            GameCore.getInstance().addMoney(-1 * price);
        } else if (getVariant() == VARIANT_SALE) {
            int price = this.getItemSaleCost(this.saleItem);
            if (getResult() == RESULT_GOOD) {
                price = (int) (price * (1 + Config.getInstance().saleLostPercent));
            }
            if (getResult() == RESULT_BAD) {
                price = 0;
            }
            if (price > 0) {
                GameCore.getInstance().addMoney(price);
            }
        }
        this.setQuestStartText("quest.after." + (this.getAction() == ACTION_SEARCH ? "search"
                : "sale." + (this.getVariant() == VARIANT_SALE_CANCEL ? "fail" : "done")));
        this.finishAction();
        this.startQuest();
    }

}
