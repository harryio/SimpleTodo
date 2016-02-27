package io.theappx.simpletodo.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.Date;

import io.theappx.simpletodo.BR;
import io.theappx.simpletodo.database.TodoContract;
import io.theappx.simpletodo.helper.RepeatInterval;

@StorIOSQLiteType(table = TodoContract.TABLE_NAME)
public class TodoItem extends BaseObservable implements Parcelable {
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_ID, key = true)
    String uniqueId;
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_TITLE)
    String title = "";
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_DESCRIPTION)
    String description = "";
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_TIME_MILLIS)
    long time;
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_REMIND)
    boolean remind;
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_COLOR)
    int color;
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_DONE)
    boolean done;
    String repeatInterval;

    public TodoItem() {
    }

    public TodoItem(String id) {
        uniqueId = id;
    }

    public TodoItem(TodoItem other) {
        this.uniqueId = other.uniqueId;
        this.title = other.title;
        this.description = other.description;
        this.remind = other.remind;
        this.time = other.time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String pTitle) {
        title = pTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String pDescription) {
        description = pDescription;
    }

    public long getTime() {
        if (remind)
            return time;
        else
            throw new IllegalStateException("Set reminder before getting time value");
    }

    public void setTime(long time) {
        if (remind) this.time = time;
    }

    public Date getDateInstance() {
        if (remind)
            return new Date(time);
        else
            throw new IllegalStateException("Set reminder before getting Date instance");
    }

    public String getId() {
        return uniqueId;
    }

    public boolean isRemind() {
        return remind;
    }

    public void setReminderStatus(boolean shouldRemind) {
        remind = shouldRemind;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Bindable
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
        notifyPropertyChanged(BR.done);
    }

    public String getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(RepeatInterval repeatInterval) {
        this.repeatInterval = repeatInterval.name();
    }

    public boolean isChanged(TodoItem pCloneTodoItem) {
        return (!(this.title.equals(pCloneTodoItem.getTitle()))
                        || !(this.description.equals(pCloneTodoItem.getDescription()))
                        || !(this.color == pCloneTodoItem.getColor())
                        || isRemindStatusChanged(pCloneTodoItem)
                        || isTimeChanged(pCloneTodoItem));
    }

    public boolean isRemindStatusChanged(TodoItem pCloneTodoItem) {
        return !this.remind == pCloneTodoItem.isRemind();
    }

    public boolean isTimeChanged(TodoItem cloneTodoItem) {
        return remind && !(this.time == cloneTodoItem.getTime());
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "uniqueId='" + uniqueId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", mTime='" + new Date(time).toString() + '\'' +
                ", remind=" + remind +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uniqueId);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeLong(this.time);
        dest.writeInt(this.color);
        dest.writeByte(this.remind ? (byte) 1 : (byte) 0);
        dest.writeByte(this.done ? (byte) 1 : (byte) 0);
        dest.writeString(this.repeatInterval);
    }

    protected TodoItem(Parcel in) {
        this.uniqueId = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.time = in.readLong();
        this.color = in.readInt();
        this.remind = in.readByte() != 0;
        this.done = in.readByte() != 0;
        this.repeatInterval = in.readString();
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
