package ua.com.tlftgames.waymc.quest;

import java.util.ArrayList;

public class Quest extends Actualized {
    public final static int TYPE_OPEN = 0;
    public final static int TYPE_MANDATORY = 1;
    public final static int TYPE_HIDDEN = 2;
    private String text;
    private int index = 0;
    private int placeType = -1;
    private int type = 0;
    private boolean once = false;
    private String image = null;
    private ArrayList<QuestChoice> choices;

    public Quest(String text, int placeType, int type, boolean once, int index) {
        this(text);
        this.placeType = placeType;
        this.type = type;
        this.once = once;
        this.index = index;
    }

    public Quest(String text) {
        this.text = text;
        this.choices = new ArrayList<QuestChoice>();
    }

    public String getText() {
        return this.text;
    }

    public int getPlaceType() {
        return this.placeType;
    }

    public int getType() {
        return this.type;
    }

    public boolean isOnce() {
        return this.once;
    }

    public int getIndex() {
        return this.index;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        if (this.image == null)
            return null;
        return this.image;
    }

    public void addChoice(QuestChoice choise) {
        choise.setQuest(this);
        this.choices.add(choise);
    }

    public ArrayList<QuestChoice> getChoices() {
        ArrayList<QuestChoice> choices = new ArrayList<QuestChoice>();
        for (QuestChoice choice : this.choices) {
            if (choice.isActual())
                choices.add(choice);
        }
        return choices;
    }

    public ArrayList<QuestChoice> getAllChoices() {
        return this.choices;
    }

    @Override
    public boolean isActual() {
        if (this.isMoneyNeed()) return false;
        return super.isActual();
    }
}
