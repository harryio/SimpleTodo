package io.theappx.simpletodo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.Date;

import io.theappx.simpletodo.database.TodoContract;

@StorIOSQLiteType(table = TodoContract.TABLE_NAME)
public class TodoItem implements Parcelable {
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_ID, key = true)
    String mUniqueId;
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_TITLE)
    String mTitle = "";
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_DESCRIPTION)
    String mDescription = "";
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_TIME_MILLIS)
    long time;
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_REMIND)
    boolean shouldRemind;

    public TodoItem() {
    }

    public TodoItem(String id) {
        mUniqueId = id;
    }

    public TodoItem(TodoItem other) {
        this.mUniqueId = other.mUniqueId;
        this.mTitle = other.mTitle;
        this.mDescription = other.mDescription;
        this.shouldRemind = other.shouldRemind;
        this.time = other.time;
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

    public long getTime() {
        if (shouldRemind)
            return time;
        else
            throw new IllegalStateException("Set reminder before getting time value");
    }

    public void setTime(long time) {
        if (shouldRemind) this.time = time;
    }

    public Date getDateInstance() {
        if (shouldRemind)
            return new Date(time);
        else
            throw new IllegalStateException("Set reminder before getting Date instance");
    }

    public String getId() {
        return mUniqueId;
    }

    public boolean shouldBeReminded() {
        return shouldRemind;
    }

    public void setShouldRemind(boolean pShouldRemind) {
        shouldRemind = pShouldRemind;
    }

    public boolean isChanged(TodoItem pCloneTodoItem) {
        return (!(this.mTitle.equals(pCloneTodoItem.getTitle()))
                        || !(this.mDescription.equals(pCloneTodoItem.getDescription()))
                        || isRemindStatusChanged(pCloneTodoItem)
                        || isTimeChanged(pCloneTodoItem));
    }

    public boolean isRemindStatusChanged(TodoItem pCloneTodoItem) {
        return !this.shouldRemind == pCloneTodoItem.shouldBeReminded();
    }

    public boolean isTimeChanged(TodoItem cloneTodoItem) {
        return !(this.time == cloneTodoItem.getTime());
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "mUniqueId='" + mUniqueId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mTime='" + new Date(time).toString() + '\'' +
                ", shouldRemind=" + shouldRemind +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUniqueId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mDescription);
        dest.writeLong(this.time);
        dest.writeByte(shouldRemind ? (byte) 1 : (byte) 0);
    }

    protected TodoItem(Parcel in) {
        this.mUniqueId = in.readString();
        this.mTitle = in.readString();
        this.mDescription = in.readString();
        this.time = in.readLong();
        this.shouldRemind = in.readByte() != 0;
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        public TodoItem createFromParcel(Parcel source) {
            return new TodoItem(source);
        }

        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };
}
