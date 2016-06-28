package ua.com.tlftgames.waymc;

import java.util.Locale;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Settings {
    private static Settings instance;
    private Preferences save;

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    private Preferences getSave() {
        if (save == null) {
            if (Gdx.app.getType() == ApplicationType.Android) {
                this.save = Gdx.app.getPreferences("WAYMC_settings");
            } else {
                this.save = Gdx.app.getPreferences("../.WAYMC/settings");
            }
        }
        return this.save;
    }

    public void setMusicVolume(float musicVolume) {
        this.getSave().putFloat("music_volume", musicVolume);
        this.getSave().flush();
    }

    public float getMusicVolume() {
        return this.getSave().getFloat("music_volume", 1);
    }

    public void setSoundVolume(float soundVolume) {
        this.getSave().putFloat("sound_volume", soundVolume);
        this.getSave().flush();
    }

    public float getSoundVolume() {
        return this.getSave().getFloat("sound_volume", 1);
    }

    public String getLang() {
        return this.getSave().getString("lang", this.getDefaultLang());
    }

    private String getDefaultLang() {
        String systemLocale = java.util.Locale.getDefault().getLanguage();
        if (systemLocale.equals(new Locale("uk").getLanguage())) {
            return "uk";
        }
        // return "en";
        return "ru";
    }

    public void setLang(String lang) {
        this.getSave().putString("lang", lang);
        this.getSave().flush();
    }

    public void setTutorialEnable(boolean enable) {
        this.getSave().putBoolean("tutorial_enable", enable);
        this.getSave().flush();
    }

    public boolean getTutorialEnable() {
        return this.getSave().getBoolean("tutorial_enable", true);
    }

    public void setIntroEnable(boolean enable) {
        this.getSave().putBoolean("intro_enable", enable);
        this.getSave().flush();
    }

    public boolean getIntroEnable() {
        return this.getSave().getBoolean("intro_enable", true);
    }

    public static void dispose() {
        instance = null;
    }
}
