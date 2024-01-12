package fr.sncf.d2d.micromessenger.protocol;

import java.io.Serializable;
import java.util.Date;

public class ServerIncomingMessage implements Serializable {

    private static final long serialVersionUID = -49110475928L;
    
    private final Date sentAt;

    private final String username;

    private final String message;

    public ServerIncomingMessage(String message, String username, Date sentAt) {
        this.sentAt = sentAt;
        this.username = username;
        this.message = message;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
