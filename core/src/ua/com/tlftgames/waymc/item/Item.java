package ua.com.tlftgames.waymc.item;

public class Item {
    private String name;
    private int cost;
    private String[] resources;
    private String img;
    private String info;
    private int level;

    public Item(String name, int cost, int level, String[] resources, String img, String info) {
        this.name = name;
        this.cost = cost;
        this.level = level;
        this.resources = resources;
        this.img = img;
        this.info = info;
    }

    public String getName() {
        return this.name;
    }

    public int getCost() {
        return this.cost;
    }

    public int getLevel() {
        return this.level;
    }

    public String[] getResources() {
        return this.resources;
    }

    public void addResource(String resource) {
        String[] newResources = new String[this.resources.length + 1];
        for (int i = 0; i < this.resources.length; i++) {
            newResources[i] = this.resources[i];
        }
        this.resources = newResources;
        this.resources[this.resources.length - 1] = resource;
    }

    public String getImage() {
        return this.img;
    }

    public String getInfo() {
        return this.info;
    }

    public boolean isResource() {
        return (this.level == 0);
    }

    public boolean isCreatable() {
        return (this.level == 1 || this.level == 2);
    }
}
