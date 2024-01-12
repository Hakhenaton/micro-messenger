package fr.sncf.d2d.micromessenger.protocol;

import java.io.Serializable;

public class ServerAccept implements Serializable {

    private static final long serialVersionUID = -9553500175629663L;
    
    private final String banner;

    public ServerAccept(String banner){
        this.banner = banner;
    }

    public String getBanner(){
        return this.banner;
    }
}
