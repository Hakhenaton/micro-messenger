package fr.sncf.d2d.micromessenger.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageFormatter {
    
    public static String format(Date sentAt, String username, String message){
        return String.format(
            "%s %s : %s",
            new SimpleDateFormat("hh:mm a").format(sentAt),
            username,
            message
        );
    }
}
