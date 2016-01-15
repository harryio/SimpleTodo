package io.theappx.simpletodo.model;

public class TodoItem {
    private String mTitle;
    private String mDescription;
    private String mDateTime;
    private boolean shouldRemind;

    public TodoItem(String pTitle) {
        mTitle = pTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String pTitle) {
        mTitle = pTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String pDescription) {
        mDescription = pDescription;
    }

    public String getDateTime() {
        if (shouldRemind) return mDateTime;
        else throw new IllegalStateException("Remind not set");
    }

    public void setDate(String pDateTime) {
        if (shouldRemind) mDateTime = pDateTime;
        else throw new IllegalStateException("Remind not set");
    }

    public boolean shouldBeReminded() {
        return shouldRemind;
    }

    public void setShouldRemind(boolean pShouldRemind) {
        shouldRemind = pShouldRemind;
    }
}
