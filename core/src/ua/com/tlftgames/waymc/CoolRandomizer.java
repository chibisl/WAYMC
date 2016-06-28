package ua.com.tlftgames.waymc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import com.badlogic.gdx.utils.Array;

public class CoolRandomizer<T> {
    private LinkedList<T> used;
    private ArrayList<T> data;

    public CoolRandomizer(ArrayList<T> data, int repeatStep) {
        repeatStep = (repeatStep < 1) ? 1 : ((repeatStep > data.size() - 1) ? data.size() - 1 : repeatStep);

        used = new LinkedList<T>();
        this.data = data;
        for (int i = 0; i < repeatStep; i++) {
            int index = (int) (Math.random() * this.data.size());
            used.add(this.data.get(index));
            this.data.remove(index);
        }
    }

    public CoolRandomizer(Array<T> data, int repeatStep) {
        this(new ArrayList<T>(Arrays.asList(data.toArray())), repeatStep);
    }

    public T getRandomElement() {
        if (this.data.size() > 0) {
            int index = (int) (Math.random() * this.data.size());
            T result = this.data.get(index);
            if (this.used.size() > 0) {
                this.data.remove(index);
                this.data.add(this.used.removeFirst());
                this.used.add(result);
            }
            return result;
        }
        return null;
    }

    public void removeElement(T element) {
        if (this.data.contains(element)) {
            this.data.remove(element);
            if (this.used.size() > 0)
                this.data.add(this.used.removeFirst());
        }
        if (this.used.contains(element)) {
            this.used.remove(element);
        }
    }

    public void addElement(T element) {
        if (this.data.size() == 0) {
            this.data.add(element);
        } else {
            this.used.add(element);
        }
    }
}
