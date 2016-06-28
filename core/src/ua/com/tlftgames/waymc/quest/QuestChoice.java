package ua.com.tlftgames.waymc.quest;

import java.util.ArrayList;

public class QuestChoice extends Actualized {
	private String text;
	private int index;
	private ArrayList<QuestAction> actions;
	private Quest quest;

	public QuestChoice(String text, int index) {
		this.text = text;
		this.index = index;
		this.actions = new ArrayList<QuestAction>();
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
		for (QuestAction action : this.actions) {
			action.setQuest(quest);
		}
	}

	public Quest getQuest() {
		return this.quest;
	}

	public String getText() {
		return this.text;
	}

	public int getIndex() {
		return index;
	}

	public void addAction(QuestAction action) {
		action.setQuest(quest);
		this.actions.add(action);
	}

	public ArrayList<QuestAction> getActions() {
		ArrayList<QuestAction> actions = new ArrayList<QuestAction>();
		for (QuestAction action : this.actions) {
			if (action.isActual())
				actions.add(action);
		}
		return actions;
	}

	public ArrayList<QuestAction> getAllActions() {
		return this.actions;
	}

	public QuestAction getRandomAction() {
		ArrayList<QuestAction> actualActions = this.getActions();
		return actualActions.get((int) (Math.random() * actualActions.size()));
	}
}
