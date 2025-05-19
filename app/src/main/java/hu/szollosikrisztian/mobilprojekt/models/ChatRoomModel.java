package hu.szollosikrisztian.mobilprojekt.models;

import java.util.List;
import java.util.Date;

public class ChatRoomModel {
    private String name;
    private boolean isPrivate;
    private List<String> participants;
    private Date createdAt;

    public ChatRoomModel() { }

    public ChatRoomModel(String name, boolean isPrivate, List<String> participants, Date createdAt) {
        this.name = name;
        this.isPrivate = isPrivate;
        this.participants = participants;
        this.createdAt = createdAt;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
