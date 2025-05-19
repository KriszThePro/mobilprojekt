package hu.szollosikrisztian.mobilprojekt.models;

import java.util.Objects;

public class MessageModel {

    private String id;
    private String sender;
    private String textMessage;
    private String base64Image;
    private long timestamp;

    // Required for Firestore
    public MessageModel() { }

    public MessageModel(String str, boolean isImage, String sender, long timestamp) {
        this.textMessage = isImage ? null : str;
        this.base64Image = isImage ? str : null;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageModel that = (MessageModel) o;
        return timestamp == that.timestamp &&
                Objects.equals(textMessage, that.textMessage) &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(base64Image, that.base64Image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textMessage, sender, timestamp, base64Image);
    }
}