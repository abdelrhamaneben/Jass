package com.abdelrhamane.dufaux.jass.models;

/**
 * Created by abdelrhamanebenhammou on 14/09/15.
 */
public class record {
    private String name;
    private String filename;
    private boolean favorite;
    //durate in seconde
    private int durate;


    /**
     *  Construct
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
