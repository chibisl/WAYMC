package ua.com.tlftgames.waymc;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class Translator {
    private HashMap<String, String> vocab;
    private static Translator instance;

    public static Translator getInstance() {
        if (instance == null) {
            updateInstance();
        }
        return instance;
    }

    public static void updateInstance() {
        instance = new Translator();
    }

    public void updateVocab(String newLang) {
        this.vocab.clear();
        if (Gdx.files.internal("lang/" + newLang + ".json").exists()) {
            JsonValue translateVocab = new JsonReader().parse(Gdx.files.internal("lang/" + newLang + ".json"));
            JsonValue translate = translateVocab.child();
            while (translate != null) {
                this.vocab.put(translate.name(), translate.asString());
                translate = translate.next();
            }
        }
    }

    public Translator() {
        this.vocab = new HashMap<String, String>();
        this.updateVocab(Settings.getInstance().getLang());
    }

    public String translate(String text) {
        if (text == null)
            return null;
        StringBuilder result = new StringBuilder();
        String[] textParts = text.split("\\+");
        for (String textPart : textParts) {
            if (this.vocab.containsKey(textPart)) {
                result.append(this.vocab.get(textPart));
            } else {
                result.append(textPart);
            }
        }
        return result.toString();
    }

    public String translateWithoutArticulos(String text) {
        String result = this.translate(text);
        if (result == null) return null;
        result = result.replace("a ", "").replace("an ", "").replace("the ", "");
        return result;
    }

    public String getMoneyText(int sum) {
        sum = Math.abs(sum);
        int postfix = 0;
        if (sum < 20) {
            postfix = sum;
        } else {
            postfix = sum - ((int) (sum / 10)) * 10;
        }
        return "money." + postfix;
    }

    public static void dispose() {
        instance = null;
    }
}
