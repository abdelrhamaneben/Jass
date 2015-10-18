package com.abdelrhamane.dufaux.jass.Exceptions;

/**
 * Created by dufaux on 18/10/15.
 */
public class InvalidFileTypeException extends Exception {

    public InvalidFileTypeException() {
        super("Format de fichier invalide");
    }

    public InvalidFileTypeException(String s) {
        super(s);
    }
}