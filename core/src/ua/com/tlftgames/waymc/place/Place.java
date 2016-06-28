package ua.com.tlftgames.waymc.place;

import ua.com.tlftgames.waymc.Config;

public class Place {
    public final static int TYPE_ALL = -1;
    public final static int TYPE_RESIDENTIAL = -2;
    public final static int TYPE_RECREATIONAL = -3;
    public final static int TYPE_MERCHANT = -4;
    public final static int TYPE_INDUSTRIAL = -5;
    private int index;
    private String name;
    private String info;
    private int type;
    private int crimeLevel;

    public Place(int index, String name, String info, int type, int crimeLevel) {
        this.index = index;
        this.name = name;
        this.info = info;
        this.type = (type > Place.TYPE_RESIDENTIAL) ? Place.TYPE_RESIDENTIAL
                : ((type < Place.TYPE_INDUSTRIAL) ? Place.TYPE_INDUSTRIAL : type);
        this.crimeLevel = crimeLevel;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public String getInfo() {
        return this.info;
    }

    public int getType() {
        return this.type;
    }

    public int getCrimeLevel() {
        return this.crimeLevel;
    }

    public void addCrime(int add) {
        this.crimeLevel = this.crimeLevel + add;
        if (this.crimeLevel > Config.getInstance().allCrimeLevel - 1) {
            this.crimeLevel = Config.getInstance().allCrimeLevel - 1;
        }
    }

    public void subCrime(int sub) {
        this.crimeLevel = this.crimeLevel - sub;
        if (this.crimeLevel < 0) {
            this.crimeLevel = 0;
        }
    }

    public void setCrime(int crime) {
        this.crimeLevel = crime;
    }
}
