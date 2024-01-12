package fr.sncf.d2d.micromessenger.protocol;

import java.io.Serializable;

public class ClientIncomingMessage implements Serializable {

    private static final long serialVersionUID = -19225092282855L;
    
    private final String message;

    public ClientIncomingMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
