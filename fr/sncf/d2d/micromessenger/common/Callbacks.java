package fr.sncf.d2d.micromessenger.common;

import java.util.function.Consumer;

/**
 * Classe utilitaire pour les callbacks
 */
public class Callbacks {
   
    /**
     * @return un callback qui ignore le paramètre d'entrée et ne fait rien
     */
    public static <T> Consumer<T> ignore(){
        return t -> {};
    }

    /**
     * @return un callback qui ne fait rien
     */
    public static Runnable noop(){
        return () -> {};
    }
}
