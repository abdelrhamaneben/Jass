package com.abdelrhamane.dufaux.jass.models;

import java.io.ByteArrayInputStream;

/**
 * Created by abdelrhamanebenhammou on 14/09/15.
 */
public class record {
    private String filename;
    private boolean favorite;
    
    /**
     *  Constructor
     * @param filename
     */
    public record(String filename) {
        this.filename = filename;
    }

    /*** GETTERS AND SETTERS ****/
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
