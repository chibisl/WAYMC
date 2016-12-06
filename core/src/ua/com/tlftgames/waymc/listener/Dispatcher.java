package ua.com.tlftgames.waymc.listener;

import java.util.ArrayList;
import java.util.HashMap;

public class Dispatcher {
    public static final int EVENT_LIFE_CHANGE = 0;
    public static final int EVENT_MONEY_CHANGE = 1;
    public static final int EVENT_NEW_NOTIFICATION = 2;
    public static final int EVENT_CURRENT_PLACE_CHANGED = 3;
    public static final int EVENT_CURRENT_PLACE_CHANGED_BY_QUEST = 4;
    public static final int EVENT_ITEMS_CHANGED = 5;
    public static final int EVENT_RECEIPTS_CHANGED = 6;
    public static final int EVENT_RETURNED_TO_LAST_PLACE = 7;

    private static Dispatcher instance;
    private HashMap<Integer, ArrayList<Listener>> listeners;

    public static Dispatcher getInstance() {
        if (instance == null) {
            instance = new Dispatcher();
        }
        return instance;
    }

    private Dispatcher() {
        this.listeners = new HashMap<Integer, ArrayList<Listener>>();
    }

    public void addListener(int event, Listener listener) {
        if (listener != null) {
            ArrayList<Listener> eventlisteners = this.listeners.get(event);
            if (eventlisteners == null) {
                eventlisteners = new ArrayList<Listener>();
                this.listeners.put(event, eventlisteners);
            }

            eventlisteners.add(listener);
        }
    }

    public void addListener(int[] events, Listener listener) {
        if (listener != null) {
            for (int i = 0; i < events.length; i++) {
                ArrayList<Listener> eventlisteners = this.listeners.get(events[i]);
                if (eventlisteners == null) {
                    eventlisteners = new ArrayList<Listener>();
                    this.listeners.put(events[i], eventlisteners);
                }

                eventlisteners.add(listener);
            }
        }
    }

    public void removeListener(int event, Listener listener) {
        if (listener != null) {
            ArrayList<Listener> eventlisteners = this.listeners.get(event);
            if (eventlisteners != null) {
                eventlisteners.remove(listener);
            }
        }
    }

    public void removeListener(int[] events, Listener listener) {
        if (listener != null) {
            for (int i = 0; i < events.length; i++) {
                ArrayList<Listener> eventlisteners = this.listeners.get(events[i]);
                if (eventlisteners != null) {
                    eventlisteners.remove(listener);
                }
            }
        }
    }

    public void dispatch(int event) {
        ArrayList<Listener> eventlisteners = this.listeners.get(event);
        if (eventlisteners != null) {
            for (int i = 0; i < eventlisteners.size(); i++) {
                Listener listener = eventlisteners.get(i);
                listener.fireEvent(event);
            }
        }
    }

    public static void dispose() {
        instance = null;
    }
}
