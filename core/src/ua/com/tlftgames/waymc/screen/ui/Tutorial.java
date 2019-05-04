package ua.com.tlftgames.waymc.screen.ui;

import java.util.ArrayList;

import ua.com.tlftgames.waymc.GameCore;
import ua.com.tlftgames.waymc.Save;

public class Tutorial {
    public final static int TUTORIAL_CRIME = 0;
    public final static int TUTORIAL_RESIDENTIAL = 1;
    public final static int TUTORIAL_MERCHANT = 2;
    public final static int TUTORIAL_RECREATIONAL = 3;
    public final static int TUTORIAL_INDUSTRIAL = 4;
    public final static int TUTORIAL_QTE_LUCK = 5;
    public final static int TUTORIAL_QTE_REACTION = 6;
    public final static int TUTORIAL_QTE_SPEED = 7;
    public final static int TUTORIAL_QTE_MEMORY = 8;

    static public ArrayList<Integer> getTutorialsShowed() {
        ArrayList<Integer> tutorialsShowed = GameCore.getInstance().getSave().loadTutorialsShowed();
        if (tutorialsShowed == null) {
            tutorialsShowed = new ArrayList<Integer>();
        }
        int size = tutorialsShowed.size();
        if (size < 9) {
            for (int i = 0; i < 9 - size; i++) {
                tutorialsShowed.add(0);
            }
            GameCore.getInstance().getSave().saveProgress(Save.TUTORIALS_SHOWED_KEY, tutorialsShowed);
        }
        return tutorialsShowed;
    }

    static public boolean isTutorialShowed(int tutorial) {
        ArrayList<Integer> tutorialsShowed = Tutorial.getTutorialsShowed();
        return tutorialsShowed.get(tutorial) == 1;
    }

    static public void setTutorialShowed(int tutorial) {
        ArrayList<Integer> tutorialsShowed = Tutorial.getTutorialsShowed();
        tutorialsShowed.set(tutorial, 1);
        GameCore.getInstance().getSave().saveProgress(Save.TUTORIALS_SHOWED_KEY, tutorialsShowed);
    }
}
