package ua.com.tlftgames.waymc.natification;

import java.util.LinkedList;

import ua.com.tlftgames.waymc.listener.Dispatcher;

public class NotificationManager {
    private LinkedList<Notification> pool;

    public NotificationManager() {
        pool = new LinkedList<Notification>();
    }

    public void addNotification(Notification notification) {
        if (notification.getText() != null && !pool.contains(notification)) {
            pool.add(notification);
            Dispatcher.getInstance().dispatch(Dispatcher.EVENT_NEW_NOTIFICATION);
        }
    }

    public Notification getNextNotification() {
        if (pool.size() == 0)
            return null;
        return pool.getFirst();
    }

    public void removeNotification() {
        pool.removeFirst();
    }
}
