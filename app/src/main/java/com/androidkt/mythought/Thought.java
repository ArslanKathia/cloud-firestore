package com.androidkt.mythought;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by brijesh on 6/10/17.
 */

public class Thought {


    @PropertyName("Thought")
    private String text;

    @PropertyName("PublishBy")
    private String publisherBy;

    @ServerTimestamp
    private Date timestamp;

    public Thought() {
    }

    public Thought(String text, String publisherBy) {
        this.text = text;
        this.publisherBy = publisherBy;
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
}
