package ua.com.tlftgames.waymc.natification;

public class Notification {
    private String imageTitle;
    private String text;

    public Notification(String imageTitle, String text) {
        this.imageTitle = imageTitle;
        this.text = text;
    }

    public String getImage() {
        return this.imageTitle;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public boolean equals(Object o) {
        Notification n = (Notification) o;
        return (n.getText() == this.text);
    }

}
