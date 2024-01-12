package fr.sncf.d2d.micromessenger.server;

import java.util.Date;

/**
 * Objet représentant un message envoyé par un client connecté
 */
public class ConnectedClientMessage {
    
    private final String message;

    private final Date sentAt;

    private final ConnectedClient client;

    public ConnectedClientMessage(ConnectedClient client, String message, Date sentAt) {
        this.message = message;
        this.sentAt = sentAt;
        this.client = client;
    }

    public String getMessage() {
        return message;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public ConnectedClient getClient() {
        return client;
    }

    
}
