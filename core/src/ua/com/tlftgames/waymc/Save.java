package ua.com.tlftgames.waymc;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Save {
    private Preferences save;
    public static final int LIFE_KEY = 0;
    public static final int MONEY_KEY = 1;
    public static final int CURRENT_PLACE_KEY = 2;
    public static final int SEARCH_PLACES_KEY = 3;
    public static final int CURRENT_SEARCH_PLACE_KEY = 4;
    public static final int CURRENT_STEP_KEY = 5;
    public static final int PLACE_CRIME_KEY = 6;
    public static final int MANDATORY_QUESTS_KEY = 7;
    public static final int PLACE_QUESTS_KEY = 8;
    public static final int ITEMS_KEY = 10;
    public static final int QUEST_KEY = 11;
    public static final int INFORMATION_KEY = 12;
    public static final int RECEIPTS_KEY = 13;
    public static final int SUBEDLIFE_KEY = 14;
    public static final int QUEST_ACTION_KEY = 15;
    public static final int RESOURCES_KEY = 16;
    public static final int SEARCHED_PLACES_KEY = 17;
    public static final int RECREATIONAL_CURRENT_EVENT = 18;
    public static final int STEP_COUNT = 19;
    public static final int ACTION_VARIANT_KEY = 20;
    public static final int ACTION_RESULT_KEY = 21;
    public static final int RESIDENTIAL_REST_COST_KEY = 22;
    public static final int INDUSTRIAL_WORK_PAY_KEY = 23;
    public static final int LAST_PLACE_KEY = 24;
    public static final int ACTION_KEY = 25;
    public static final int CLUB_RECEIPT_KEY = 26;
    public static final int FIRST_HORSE_KEY = 27;
    public static final int CASINO_WIN_KEY = 28;
    public static final int SEARCH_ITEMS_KEY = 29;
    public static final int SALE_ITEM_KEY = 30;
    public static final int PLAY_LEVEL_KEY = 31;
    public static final int TUTORIALS_SHOWED_KEY = 32;

    private Preferences getSave() {
        if (save == null) {
            if (Gdx.app.getType() == ApplicationType.Android) {
                this.save = Gdx.app.getPreferences("WAYMC_save");
            } else {
                this.save = Gdx.app.getPreferences("save");
            }
        }
        return this.save;
    }

    public String loadClubReceipt() {
        return this.getSave().getString("club_receipt", "");
    }

    public String loadSaleItem() {
        return this.getSave().getString("sale_item", "");
    }

    public int loadLife() {
        return Integer.decode(this.getSave().getString("life", "-1"));
    }

    public int loadMoney() {
        return Integer.decode(this.getSave().getString("money", "-1"));
    }

    public int loadCurrentStep() {
        return Integer.decode(this.getSave().getString("current_step", "-1"));
    }

    public int loadCurrentSearchPlace() {
        return Integer.decode(this.getSave().getString("current_search_place", "-1"));
    }

    public int loadCurrentPlace() {
        return Integer.decode(this.getSave().getString("current_place", "-1"));
    }

    public int loadLastPlace() {
        return Integer.decode(this.getSave().getString("last_place", "-1"));
    }

    public int loadQuest() {
        return Integer.decode(this.getSave().getString("quest", "-1"));
    }

    public int loadSubedLife() {
        return Integer.decode(this.getSave().getString("subed_life", "0"));
    }

    public int loadQuestAction() {
        return Integer.decode(this.getSave().getString("quest_action", "-1"));
    }

    public int loadRecreationalCurrentEvent() {
        return Integer.decode(this.getSave().getString("recreational_current_event", "1"));
    }

    public int loadStepCount() {
        return Integer.decode(this.getSave().getString("step_count", "0"));
    }

    public int loadActionResult() {
        return Integer.decode(this.getSave().getString("action_result", "-1"));
    }

    public int loadAction() {
        return Integer.decode(this.getSave().getString("action", "-1"));
    }

    public int loadResidentialRestCost() {
        return Integer.decode(this.getSave().getString("residental_rest_cost", "10"));
    }

    public int loadIndustrialWorkPay() {
        return Integer.decode(this.getSave().getString("industrial_work_pay", "10"));
    }

    public int loadActionVariant() {
        return Integer.decode(this.getSave().getString("action_variant", "-1"));
    }

    public int loadFirstHorse() {
        return Integer.decode(this.getSave().getString("first_horse", "0"));
    }

    public int loadCasinoWin() {
        return Integer.decode(this.getSave().getString("casino_win", "10"));
    }

    public int loadPlayLevel() {
        return Integer.decode(this.getSave().getString("play_level", "0"));
    }

    private ArrayList<Integer> getIntegerArray(String saveData) {
        if (saveData.isEmpty())
            return null;
        String[] strings = saveData.replaceAll("[\\[\\]]", "").split(", ");
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (String string : strings) {
            if (!string.contentEquals(""))
                result.add(Integer.decode(string));
        }
        return result;
    }

    private ArrayList<String> getStringArray(String saveData) {
        if (!(saveData != null && !saveData.contentEquals("")))
            return null;
        String[] strings = saveData.replaceAll("[\\[\\]]", "").split(", ");
        ArrayList<String> result = new ArrayList<String>();
        for (String string : strings) {
            if (!string.contentEquals(""))
                result.add(string);
        }
        return result;
    }

    public ArrayList<Integer> loadSearchPlaces() {
        return getIntegerArray(this.getSave().getString("search_places", ""));
    }

    public ArrayList<Integer> loadSearchedPlaces() {
        return getIntegerArray(this.getSave().getString("searched_places", ""));
    }

    public ArrayList<Integer> loadPlaceCrime() {
        return getIntegerArray(this.getSave().getString("place_crime", ""));
    }

    public ArrayList<Integer> loadTutorialsShowed() {
        return getIntegerArray(this.getSave().getString("tutorials_showed", ""));
    }

    public ArrayList<String> loadItems() {
        return getStringArray(this.getSave().getString("items", ""));
    }

    public ArrayList<String> loadInformation() {
        return getStringArray(this.getSave().getString("information", ""));
    }

    public ArrayList<String> loadReceipts() {
        return getStringArray(this.getSave().getString("receipts", ""));
    }

    public ArrayList<String> loadResources() {
        return getStringArray(this.getSave().getString("resources", ""));
    }

    public ArrayList<String> loadSearchItems() {
        return getStringArray(this.getSave().getString("search_items", ""));
    }

    public HashMap<Integer, ArrayList<Integer>> loadMandatoryQuest() {
        String saveData = this.getSave().getString("mandatory_quest", "");
        if (saveData.isEmpty())
            return null;
        return this.getQuestsHashMap(saveData);
    }

    public HashMap<Integer, ArrayList<Integer>> loadPlaceQuest() {
        String saveData = this.getSave().getString("place_quest", "");
        if (saveData.isEmpty())
            return null;
        return this.getQuestsHashMap(saveData);
    }

    public void initProgress() {
        this.getSave().putInteger("game_start", 1);
        this.getSave().flush();
    }

    public void clearProgress() {
        this.getSave().clear();
        this.getSave().flush();
    }

    public boolean hasProgress() {
        return this.getSave().contains("game_start");
    }

    private void save(String key, String value) {
        this.getSave().putString(key, value);
        this.getSave().flush();
    }

    public void saveProgress(int key, String data) {
        String savingKey = null;
        switch (key) {
        case Save.CLUB_RECEIPT_KEY:
            savingKey = "club_receipt";
            break;
        case Save.SALE_ITEM_KEY:
            savingKey = "sale_item";
            break;
        }
        if (savingKey != null)
            this.save(savingKey, data);
    }

    public void saveProgress(int key, int data) {
        String savingKey = null;
        switch (key) {
        case Save.LIFE_KEY:
            savingKey = "life";
            break;
        case Save.MONEY_KEY:
            savingKey = "money";
            break;
        case Save.CURRENT_STEP_KEY:
            savingKey = "current_step";
            break;
        case Save.CURRENT_PLACE_KEY:
            savingKey = "current_place";
            break;
        case Save.LAST_PLACE_KEY:
            savingKey = "last_place";
            break;
        case Save.CURRENT_SEARCH_PLACE_KEY:
            savingKey = "current_search_place";
            break;
        case Save.QUEST_KEY:
            savingKey = "quest";
            break;
        case Save.SUBEDLIFE_KEY:
            savingKey = "subed_life";
            break;
        case Save.QUEST_ACTION_KEY:
            savingKey = "quest_action";
            break;
        case Save.RECREATIONAL_CURRENT_EVENT:
            savingKey = "recreational_current_event";
            break;
        case Save.STEP_COUNT:
            savingKey = "step_count";
            break;
        case Save.ACTION_RESULT_KEY:
            savingKey = "action_result";
            break;
        case Save.RESIDENTIAL_REST_COST_KEY:
            savingKey = "residental_rest_cost";
            break;
        case Save.INDUSTRIAL_WORK_PAY_KEY:
            savingKey = "industrial_work_pay";
            break;
        case Save.ACTION_VARIANT_KEY:
            savingKey = "action_variant";
            break;
        case Save.ACTION_KEY:
            savingKey = "action";
            break;
        case Save.FIRST_HORSE_KEY:
            savingKey = "first_horse";
            break;
        case Save.CASINO_WIN_KEY:
            savingKey = "casino_win";
            break;
        case Save.PLAY_LEVEL_KEY:
            savingKey = "play_level";
            break;
        }
        if (savingKey != null)
            this.save(savingKey, Integer.toString(data));
    }

    public void saveProgress(int key, HashMap<Integer, ArrayList<Integer>> data) {
        String savingKey = null;
        switch (key) {
        case Save.MANDATORY_QUESTS_KEY:
            savingKey = "mandatory_quest";
            break;
        case Save.PLACE_QUESTS_KEY:
            savingKey = "place_quest";
            break;
        }
        if (savingKey != null)
            this.save(savingKey, this.getQuestsString(data));
    }

    public void saveProgress(int key, ArrayList<?> data) {
        String savingKey = null;
        switch (key) {
        case Save.PLACE_CRIME_KEY:
            savingKey = "place_crime";
            break;
        case Save.ITEMS_KEY:
            savingKey = "items";
            break;
        case Save.INFORMATION_KEY:
            savingKey = "information";
            break;
        case Save.RECEIPTS_KEY:
            savingKey = "receipts";
            break;
        case Save.SEARCH_PLACES_KEY:
            savingKey = "search_places";
            break;
        case Save.SEARCHED_PLACES_KEY:
            savingKey = "searched_places";
            break;
        case Save.RESOURCES_KEY:
            savingKey = "resources";
            break;
        case Save.SEARCH_ITEMS_KEY:
            savingKey = "search_items";
            break;
        case Save.TUTORIALS_SHOWED_KEY:
            savingKey = "tutorials_showed";
            break;
        }
        if (savingKey != null)
            this.save(savingKey, data.toString());
    }

    private String getQuestsString(HashMap<Integer, ArrayList<Integer>> quests) {
        StringBuilder placeQuestsStr = new StringBuilder();
        for (Integer key : quests.keySet()) {
            placeQuestsStr.append(key.toString()).append(":").append(quests.get(key).toString()).append(";");
        }
        return placeQuestsStr.toString();
    }

    private HashMap<Integer, ArrayList<Integer>> getQuestsHashMap(String questString) {
        HashMap<Integer, ArrayList<Integer>> result = new HashMap<Integer, ArrayList<Integer>>();
        String[] allDatas = questString.split(";");
        for (String allData : allDatas) {
            if (!allData.isEmpty()) {
                String[] typeDatas = allData.split(":");
                if (typeDatas.length == 2) {
                    String[] quests = typeDatas[1].replaceAll("[\\[\\]]", "").split(", ");
                    ArrayList<Integer> typeData = new ArrayList<Integer>();
                    for (String quest : quests) {
                        if (!quest.isEmpty())
                            typeData.add(Integer.decode(quest));
                    }
                    result.put(Integer.decode(typeDatas[0]), typeData);
                }
            }
        }
        return result;
    }
}
