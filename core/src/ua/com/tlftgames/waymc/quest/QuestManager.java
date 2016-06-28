package ua.com.tlftgames.waymc.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Save;
import ua.com.tlftgames.waymc.place.Place;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestManager {
    private HashMap<Integer, ArrayList<Integer>> placeQuests;
    private HashMap<Integer, ArrayList<Integer>> mandatoryQuests;
    private ArrayList<Quest> quests;
    private int lastQuest = -1;

    public QuestManager() {
        placeQuests = new HashMap<Integer, ArrayList<Integer>>();
        mandatoryQuests = new HashMap<Integer, ArrayList<Integer>>();
        quests = new ArrayList<Quest>();
        JsonValue questsData = new JsonReader().parse(Gdx.files.internal("data/quests.json"));
        for (int i = 0; i < questsData.size; i++) {
            Quest quest = this.createQuest(questsData.get(i), i);
            quests.add(quest);
            if (quest.getType() == Quest.TYPE_OPEN) {
                ArrayList<Integer> quests = placeQuests.get(quest.getPlaceType());
                if (quests == null) {
                    quests = new ArrayList<Integer>();
                    placeQuests.put(quest.getPlaceType(), quests);
                }
                quests.add(i);
            }
        }
    }

    private Quest createQuest(JsonValue questData, int index) {
        String text = questData.getString("text");
        int placeType = questData.getInt("place");
        int type = questData.getInt("type");
        boolean once = questData.getBoolean("once");
        Quest quest = new Quest(text, placeType, type, once, index);
        if (questData.has("need")) {
            this.addNeeds(quest, questData.get("need"));
        }
        if (questData.has("image")) {
            quest.setImage(questData.getString("image"));
        }
        JsonValue choices = questData.get("choices");
        for (int i = 0; i < choices.size; i++) {
            quest.addChoice(this.createChoice(choices.get(i), i));
        }
        return quest;
    }

    private QuestChoice createChoice(JsonValue choiceData, int index) {
        QuestChoice questChoice = new QuestChoice(choiceData.getString("text"), index);
        if (choiceData.has("need")) {
            this.addNeeds(questChoice, choiceData.get("need"));
        }
        JsonValue actions = choiceData.get("actions");
        for (int i = 0; i < actions.size; i++) {
            questChoice.addAction(this.createAction(actions.get(i), 10 * index + i));
        }
        return questChoice;
    }

    private void addNeeds(Actualized obj, JsonValue needs) {
        if (needs.has("needLife")) {
            obj.setNeedLife(needs.getInt("needLife"));
        }
        if (needs.has("unneedLife")) {
            obj.setUnneedLife(needs.getInt("unneedLife"));
        }
        if (needs.has("needMoney")) {
            obj.setNeedMoney(needs.getInt("needMoney"));
        }
        if (needs.has("unneedMoney")) {
            obj.setUnneedMoney(needs.getInt("unneedMoney"));
        }
        if (needs.has("needItems")) {
            obj.setNeedItems(needs.getString("needItems").split(","));
        }
        if (needs.has("needReceipts")) {
            obj.setNeedReceipts(needs.getString("needReceipts").split(","));
        }
        if (needs.has("needInformation")) {
            obj.setNeedInformation(needs.getString("needInformation").split(","));
        }
    }

    private QuestAction createAction(JsonValue actionsData, int index) {
        QuestAction questAction = new QuestAction(actionsData.getString("text"), index);
        if (actionsData.has("need")) {
            this.addNeeds(questAction, actionsData.get("need"));
        }
        JsonValue action = actionsData.get("action");
        if (action.has("moveTo")) {
            questAction.setMoveTo(action.getInt("moveTo"));
        }
        if (action.has("changeLife")) {
            questAction.setChangeLife(action.getInt("changeLife"));
        }
        if (action.has("changeMoney")) {
            questAction.setChangeMoney(action.getInt("changeMoney"));
        }
        if (action.has("changeItem")) {
            questAction.setChangeItem(action.getString("changeItem"));
        }
        if (action.has("changeReceipt")) {
            questAction.setChangeReceipt(action.getString("changeReceipt"));
        }
        if (action.has("changeInformation")) {
            questAction.setChangeInformation(action.getString("changeInformation"));
        }
        if (action.has("addQuest")) {
            questAction.setAddQuest(action.getInt("addQuest"));
        }
        if (action.has("endGame")) {
            questAction.setEndGame(action.getBoolean("endGame"));
        }
        return questAction;
    }

    public Quest getMandatoryQuest() {
        Quest quest = getMandatoryQuestForPlace(GameCore.getInstance().getPlaceManager().getCurrentPlace());
        if (quest != null)
            this.setOpenQuest(quest.getIndex());
        return quest;
    }

    public Quest getNextQuest() {
        Quest nextQuest = null;
        if (Math.random() < 0.4f) {
            nextQuest = getQuestForPlace(GameCore.getInstance().getPlaceManager().getCurrentPlace());
        }
        if (nextQuest != null)
            this.setOpenQuest(nextQuest.getIndex());

        return nextQuest;
    }

    private void setOpenQuest(int index) {
        GameCore.getInstance().getSave().saveProgress(Save.QUEST_KEY, index);
    }

    public int loadOpenQuest() {
        return GameCore.getInstance().getSave().loadQuest();
    }

    public void removeQuest(int index) {
        this.removeQuest(getQuest(index));
    }

    public void addQuest(Integer index) {
        this.addQuest(this.getQuest(index));
    }

    public void addQuest(Quest quest) {
        this.getQuestContainer(quest).add(this.quests.indexOf(quest));
        GameCore.getInstance().getSave().saveProgress(Save.MANDATORY_QUESTS_KEY, this.getMandatoryQuests());
        GameCore.getInstance().getSave().saveProgress(Save.PLACE_QUESTS_KEY, this.getPlaceQuests());
    }

    private ArrayList<Integer> getQuestContainer(Quest quest) {
        ArrayList<Integer> quests;
        if (quest.getType() == Quest.TYPE_MANDATORY) {
            quests = mandatoryQuests.get(quest.getPlaceType());
            if (quests == null) {
                quests = new ArrayList<Integer>();
                mandatoryQuests.put(quest.getPlaceType(), quests);
            }
        } else {
            quests = placeQuests.get(quest.getPlaceType());
            if (quests == null) {
                quests = new ArrayList<Integer>();
                placeQuests.put(quest.getPlaceType(), quests);
            }
        }

        return quests;
    }

    public HashMap<Integer, ArrayList<Integer>> getPlaceQuests() {
        return this.placeQuests;
    }

    public HashMap<Integer, ArrayList<Integer>> getMandatoryQuests() {
        return this.mandatoryQuests;
    }

    public void setPlaceQuests(HashMap<Integer, ArrayList<Integer>> placeQuests) {
        this.placeQuests = placeQuests;
    }

    public void setMandatoryQuests(HashMap<Integer, ArrayList<Integer>> mandatoryQuests) {
        this.mandatoryQuests = mandatoryQuests;
    }

    public void removeQuest(Quest quest) {
        Integer index = quests.indexOf(quest);
        ArrayList<Integer> quests;
        if (quest.getType() == Quest.TYPE_MANDATORY) {
            quests = mandatoryQuests.get(quest.getPlaceType());
        } else {
            quests = placeQuests.get(quest.getPlaceType());
        }
        if (quests != null) {
            quests.remove(index);
        }
        GameCore.getInstance().getSave().saveProgress(Save.MANDATORY_QUESTS_KEY, this.getMandatoryQuests());
        GameCore.getInstance().getSave().saveProgress(Save.PLACE_QUESTS_KEY, this.getPlaceQuests());
    }

    public void removeQuest(Integer index) {
        this.removeQuest(this.getQuest(index));
    }

    public Quest getMandatoryQuestForPlace(Place place) {
        ArrayList<Integer> mandatoryForPlace = this.mandatoryQuests.get(place.getIndex());
        if (mandatoryForPlace == null) {
            mandatoryForPlace = new ArrayList<Integer>();
        }
        ArrayList<Integer> typeQuests = this.mandatoryQuests.get(place.getType());
        if (typeQuests != null) {
            mandatoryForPlace.addAll(typeQuests);
        }
        ArrayList<Integer> allQuests = this.mandatoryQuests.get(Place.TYPE_ALL);
        if (allQuests != null) {
            mandatoryForPlace.addAll(allQuests);
        }
        if (mandatoryForPlace.isEmpty()) {
            return null;
        }
        for (Integer questIndex : mandatoryForPlace) {
            Quest mandatoryQuest = this.quests.get(questIndex);
            if (mandatoryQuest.isActual())
                return mandatoryQuest;
        }
        return null;
    }

    public Quest getQuestForPlace(Place place) {
        ArrayList<Integer> quests = new ArrayList<Integer>();
        ArrayList<Integer> placeQuests = this.placeQuests.get(place.getIndex());
        if (placeQuests != null) {
            quests.addAll(placeQuests);
        }
        ArrayList<Integer> typeQuests = this.placeQuests.get(place.getType());
        if (typeQuests != null) {
            quests.addAll(typeQuests);
        }
        ArrayList<Integer> allQuests = this.placeQuests.get(Place.TYPE_ALL);
        if (allQuests != null) {
            quests.addAll(allQuests);
        }
        if (quests.isEmpty()) {
            return null;
        }
        while (!quests.isEmpty()) {
            if (lastQuest >= 0) {
                quests.remove(Integer.valueOf(lastQuest));
            }
            int randowIndex = (int) (Math.random() * quests.size());
            int questIndex = quests.get(randowIndex);
            Quest placeQuest = this.quests.get(questIndex);
            if (placeQuest.isActual()) {
                lastQuest = questIndex;
                return placeQuest;
            }
            quests.remove(randowIndex);
        }

        return null;
    }

    public Quest getQuest(int index) {
        if (index >= 0 && index < this.quests.size()) {
            return this.quests.get(index);
        }
        return null;
    }

}
