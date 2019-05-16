package ua.com.tlftgames.waymc.place;

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

    public Place(int index, String name, String info, int type) {
        this.index = index;
        this.name = name;
        this.info = info;
        this.type = (type > Place.TYPE_RESIDENTIAL) ? Place.TYPE_RESIDENTIAL
                : ((type < Place.TYPE_INDUSTRIAL) ? Place.TYPE_INDUSTRIAL : type);
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
}
