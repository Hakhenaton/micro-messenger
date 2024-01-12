package fr.sncf.d2d.micromessenger.protocol;

import java.io.Serializable;

public class ClientConnection implements Serializable {

    private static final long serialVersionUID = 3876587625847265L;
    
    private final String username;

    public ClientConnection(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}
