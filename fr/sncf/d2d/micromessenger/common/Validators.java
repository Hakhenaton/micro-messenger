package fr.sncf.d2d.micromessenger.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Validators {

    public static boolean isValidPort(int port){
        return port > 0 && port <= 65535;
    }

    public static boolean isValidHost(String host){
        try {
            InetAddress.getByName(host);
            return true;
        } catch (UnknownHostException unknownHostException){
            return false;
        }
    }
}
