package ua.com.tlftgames.waymc.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import ua.com.tlftgames.waymc.Config;
import ua.com.tlftgames.waymc.CoolRandomizer;
import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Save;
import ua.com.tlftgames.waymc.listener.Dispatcher;
import ua.com.tlftgames.waymc.natification.Notification;

public class ItemManager {
    private HashMap<String, Item> items;
    private CoolRandomizer<String> market;
    private CoolRandomizer<String> receipts;
    private ArrayList<String> ownItems;
    private ArrayList<String> receiptsList;
    private ArrayList<String> ownReceipts;
    private ArrayList<String> resources;

    @SuppressWarnings("unchecked")
    public ItemManager() {
        this.items = new HashMap<String, Item>();
        ArrayList<String> marketList = new ArrayList<String>();
        receiptsList = new ArrayList<String>();
        JsonValue itemsData = new JsonReader().parse(Gdx.files.internal("data/items.json"));
        JsonValue itemData = itemsData.child();
        while (itemData != null) {
            Item item = this.createItem(itemData);
            items.put(itemData.name(), item);
            if (item.getLevel() == 0) {
                marketList.add(itemData.name());
            }
            if (item.isCreatable()) {
                receiptsList.add(itemData.name());
            }
            itemData = itemData.next();
        }
        market = new CoolRandomizer<String>(marketList, marketList.size() - 2);
        receipts = new CoolRandomizer<String>((ArrayList<String>) receiptsList.clone(), receiptsList.size() - 1);
        this.ownItems = new ArrayList<String>();
        this.ownReceipts = new ArrayList<String>();
    }

    public void setOwnItem(ArrayList<String> ownItems) {
        this.ownItems = ownItems;
        this.resources = null;
    }

    public ArrayList<String> getOwnItems() {
        return this.ownItems;
    }

    public ArrayList<String> getOwnResources() {
        if (resources == null) {
            resources = new ArrayList<String>();
            for (String item : this.ownItems) {
                if (this.getItem(item).isResource())
                    resources.add(item);
            }
        }
        return resources;
    }

    public void setOwnReceipts(ArrayList<String> ownReceipts) {
        this.ownReceipts = ownReceipts;
    }

    public ArrayList<String> getOwnReceipts() {
        return this.ownReceipts;
    }

    private Item createItem(JsonValue itemData) {
        String name = itemData.name();
        int cost = itemData.getInt("cost");
        int level = itemData.getInt("level");
        String image = itemData.getString("image");
        String info = itemData.name() + ".info";
        String[] resources = itemData.get("resources").asStringArray();
        return new Item(name, cost, level, resources, image, info);
    }

    public void updateResources() {
        ArrayList<String> saveData = new ArrayList<String>();
        for (Item item : this.items.values()) {
            if (item.isCreatable()) {
                String newResource;
                do {
                    newResource = this.market.getRandomElement();
                } while (Arrays.asList(item.getResources()).contains(newResource));
                item.addResource(newResource);
                saveData.add(newResource);
            }
            if (item.getLevel() == 4) {
                for (int i = 0; i < 3; i++) {
                    String newResource = this.market.getRandomElement();
                    item.addResource(newResource);
                    saveData.add(newResource);
                }
            }
        }
        GameCore.getInstance().getSave().saveProgress(Save.RESOURCES_KEY, saveData);
    }

    public void setResources(ArrayList<String> saveData) {
        if (saveData == null)
            return;
        int i = 0;
        for (Item item : this.items.values()) {
            if (i >= saveData.size())
                break;
            if (item.isCreatable()) {
                item.addResource(saveData.get(i));
                i++;
            }
            if (item.getLevel() == 4) {
                for (int j = 0; j < 3; j++) {
                    item.addResource(saveData.get(i));
                    i++;
                }
            }
        }
    }

    public Item getItem(String itemName) {
        return this.items.get(itemName);
    }

    public String[] getNeededItems() {
        if (!this.hasReceipt("sprayer"))
            return null;
        String[] resources = this.getItem("sprayer").getResources();
        ArrayList<String> neededResources = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            if (!this.hasItem(resources[i]))
                neededResources.add(resources[i]);
        }
        if (neededResources.isEmpty())
            return null;
        return neededResources.toArray(new String[1]);
    }

    public String getItemFromMarket() {
        return market.getRandomElement();
    }

    public String getRandomItemReceipt() {
        return receipts.getRandomElement();
    }

    public void addOwnItem(String item) {
        if (this.ownItems.size() < Config.getInstance().itemsMaxCount) {
            this.ownItems.add(item);
            this.resources = null;
            GameCore.getInstance().getSave().saveProgress(Save.ITEMS_KEY, this.ownItems);
            GameCore.getInstance().getNotificationManager()
                    .addNotification(new Notification(this.getItem(item).getImage(), "notification.item.added"));
            Dispatcher.getInstance().dispatch(Dispatcher.EVENT_ITEMS_CHANGED);
        }
    }

    public void addOwnItem(Item item) {
        this.addOwnItem(item.getName());
    }

    public void removeOwnItem(String item) {
        if (this.hasItem(item)) {
            this.ownItems.remove(item);
            this.resources = null;
            GameCore.getInstance().getSave().saveProgress(Save.ITEMS_KEY, this.ownItems);
            Dispatcher.getInstance().dispatch(Dispatcher.EVENT_ITEMS_CHANGED);
        }
    }

    public void removeOwnItem(Item item) {
        this.removeOwnItem(item.getName());
    }

    public boolean hasItem(String item) {
        return this.ownItems.contains(item);
    }

    public void addOwnReceipt(String receipt) {
        if (!this.hasReceipt(receipt)) {
            this.ownReceipts.add(receipt);
            GameCore.getInstance().getSave().saveProgress(Save.RECEIPTS_KEY, this.ownReceipts);
            GameCore.getInstance().getNotificationManager()
                    .addNotification(new Notification(this.getItem(receipt).getImage(), "notification.receipt.added"));
            Dispatcher.getInstance().dispatch(Dispatcher.EVENT_RECEIPTS_CHANGED);
        }
    }

    public void removeOwnReceipt(String receipt) {
        if (this.hasReceipt(receipt)) {
            this.ownReceipts.remove(receipt);
            GameCore.getInstance().getSave().saveProgress(Save.RECEIPTS_KEY, this.ownReceipts);
            Dispatcher.getInstance().dispatch(Dispatcher.EVENT_RECEIPTS_CHANGED);
        }
    }

    public boolean hasReceipt(String receipt) {
        return this.ownReceipts.contains(receipt);
    }

    public String getItemForSale() {
        return this.ownItems.get((int) (Math.random() * this.ownItems.size()));
    }

    public void createOwnItem(Item receipt) {
        for (String itemName : receipt.getResources()) {
            if (!this.hasItem(itemName))
                return;
        }
        for (String itemName : receipt.getResources()) {
            this.removeOwnItem(itemName);
        }
        this.addOwnItem(receipt);
    }

    public void addStartMaterialsAndReceipts() {
        Item receiptItem = this.getItem(receipts.getRandomElement());
        for (int i = 0; i < Config.getInstance().startReceiptsCount; i++) {
            while (receiptItem.getLevel() == 2) {
                receiptItem = this.getItem(receipts.getRandomElement());
            }
            this.ownReceipts.add(receiptItem.getName());
        }
        GameCore.getInstance().getSave().saveProgress(Save.RECEIPTS_KEY, this.ownReceipts);
        String[] resources = receiptItem.getResources();
        String unneededItem = resources[0] + resources[1] + resources[2];
        switch ((int) (Math.random() * 3)) {
        case 0:
            this.ownItems.add(resources[0]);
            this.ownItems.add(resources[1]);
            break;
        case 1:
            this.ownItems.add(resources[0]);
            this.ownItems.add(resources[2]);
            break;
        case 2:
            this.ownItems.add(resources[2]);
            this.ownItems.add(resources[1]);
            break;
        }
        for (int i = 0; i < Config.getInstance().startMaterialsCount - 2; i++) {
            String addItem;
            do {
                addItem = this.market.getRandomElement();
            } while (unneededItem.contains(addItem) || this.hasItem(addItem));
            this.ownItems.add(addItem);
        }
        GameCore.getInstance().getSave().saveProgress(Save.ITEMS_KEY, this.ownItems);
    }

    public void lostRandomResource() {
        ArrayList<String> resources = this.getOwnResources();
        String lostItem = resources.get((int) (Math.random() * resources.size()));
        GameCore.getInstance().getNotificationManager()
                .addNotification(new Notification(this.getItem(lostItem).getImage(), "notification.item.losted"));
        this.removeOwnItem(lostItem);
    }

    public boolean canCreateItem(String receipt) {
        Item receiptItem = GameCore.getInstance().getItemManager().getItem(receipt);
        for (String resource : receiptItem.getResources()) {
            if (!this.hasItem(resource))
                return false;
        }
        return true;
    }

    public boolean deconstructItem(Item item) {
        if (this.canDeconstruct(item)) {
            this.removeOwnItem(item);
            int loseResources = (item.isCreatable()) ? 1 : 0;
            int lostResource = (loseResources == 1) ? (int) (Math.random() * item.getResources().length) : -1;
            int i = 0;
            for (String resource : item.getResources()) {
                if (i++ == lostResource)
                    continue;
                this.addOwnItem(resource);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean canDeconstruct(Item item) {
        int loseResources = (item.isCreatable()) ? 1 : 0;
        return (Config.getInstance().itemsMaxCount >= this.getOwnItems().size() + item.getResources().length - 1
                - loseResources);
    }

    public boolean hasAllReceipts() {
        return this.ownReceipts.size() >= this.receiptsList.size();
    }
}
