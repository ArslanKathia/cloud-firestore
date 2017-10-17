package com.androidkt.mythought;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by brijesh on 6/10/17.
 */

public class Thought implements Parcelable {


    private String userId;

    @PropertyName("Thought")
    private String text;

    @PropertyName("PublishBy")
    private String publisherBy;

    @ServerTimestamp
    private Date timestamp;

    public Thought() {
    }

    public Thought(String text, String publisherBy, String userId) {
        this.text = text;
        this.publisherBy = publisherBy;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Thought{" +
                "text='" + text + '\'' +
                ", publisherBy='" + publisherBy + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPublisherBy() {
        return publisherBy;
    }

    public void setPublisherBy(String publisherBy) {
        this.publisherBy = publisherBy;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.text);
        dest.writeString(this.publisherBy);
        dest.writeLong(this.timestamp != null ? this.timestamp.getTime() : -1);
    }

    protected Thought(Parcel in) {
        this.userId = in.readString();
        this.text = in.readString();
        this.publisherBy = in.readString();
        long tmpTimestamp = in.readLong();
        this.timestamp = tmpTimestamp == -1 ? null : new Date(tmpTimestamp);
    }

    public static final Parcelable.Creator<Thought> CREATOR = new Parcelable.Creator<Thought>() {
        @Override
        public Thought createFromParcel(Parcel source) {
            return new Thought(source);
        }

        @Override
        public Thought[] newArray(int size) {
            return new Thought[size];
        }
    };
}
