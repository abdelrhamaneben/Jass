package com.abdelrhamane.dufaux.jass.Exceptions;

/**
 * Created by abdelrhamanebenhammou on 22/09/15.
 */
public class AlreadyListeningException extends Exception {
    public AlreadyListeningException() {
        super("Un enregistrement est déjà en cours");
    }
}
