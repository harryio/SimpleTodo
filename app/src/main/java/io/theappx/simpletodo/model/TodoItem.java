package io.theappx.simpletodo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.Date;

import io.theappx.simpletodo.database.TodoContract;
import io.theappx.simpletodo.utils.FormatUtils;

@StorIOSQLiteType(table = TodoContract.TABLE_NAME)
public class TodoItem implements Parcelable {
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_ID, key = true)
    String mUniqueId;
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_TITLE)
    String mTitle = "";
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_DESCRIPTION)
    String mDescription = "";
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_DATE)
    String mDate;
    @StorIOSQLiteColumn(name = TodoContract.COLUMN_TIME)
    String mTime;
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
        this.mDate = other.mDate;
        this.mTime = other.mTime;
        this.shouldRemind = other.shouldRemind;
    }

    protected TodoItem(Parcel in) {
        this.mUniqueId = in.readString();
        this.mTitle = in.readString();
        this.mDescription = in.readString();
        this.mDate = in.readString();
        this.mTime = in.readString();
        this.shouldRemind = in.readByte() != 0;
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

    public String getDate() {
        if (shouldRemind) return mDate;
        else throw new IllegalStateException("Reminder not set");
    }

    public void setDate(String pDate) {
        if (shouldRemind) mDate = pDate;
        else throw new IllegalStateException("Reminder not set");
    }

    public String getTime() {
        if (mTime != null) return mTime;
        else throw new NullPointerException("Time not set");
    }

    public void setTime(String pTime) {
        if (shouldRemind) mTime = pTime;
        else throw new IllegalStateException("Reminder not set");
    }

    public Date getCompleteDate() {
        if (mDate != null && mTime != null) {
            String lDateWithTime = mDate + " " + mTime;
            return FormatUtils.getDateFromString(lDateWithTime);
        } else throw new NullPointerException("Date or Time is null");
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
        return pCloneTodoItem != null &&
                (!(this.mTitle.equals(pCloneTodoItem.getTitle()))
                        || !(this.mDescription.equals(pCloneTodoItem.getDescription()))
                        || isRemindStatusChanged(pCloneTodoItem)
                        || isDateChanged(pCloneTodoItem)
                        || isTimeChanged(pCloneTodoItem));
    }

    public boolean isRemindStatusChanged(TodoItem pCloneTodoItem) {
        return !this.shouldRemind == pCloneTodoItem.shouldBeReminded();
    }

    public boolean isDateChanged(TodoItem pCloneTodoItem) {
        return this.shouldRemind && !this.mDate.equals(pCloneTodoItem.getDate());
    }

    public boolean isTimeChanged(TodoItem pCloneTodoItem) {
        return this.shouldRemind && !this.mTime.equals(pCloneTodoItem.getTime());
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
        dest.writeString(this.mDate);
        dest.writeString(this.mTime);
        dest.writeByte(shouldRemind ? (byte) 1 : (byte) 0);
    }

    public static final Parcelable.Creator<TodoItem> CREATOR = new Parcelable.Creator<TodoItem>() {
        public TodoItem createFromParcel(Parcel source) {
            return new TodoItem(source);
        }

        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };

    @Override
    public String toString() {
        return "TodoItem{" +
                "mUniqueId='" + mUniqueId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mTime='" + mTime + '\'' +
                ", shouldRemind=" + shouldRemind +
                '}';
    }
}
