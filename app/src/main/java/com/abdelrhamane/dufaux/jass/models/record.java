package com.abdelrhamane.dufaux.jass.models;

/**
 * Created by abdelrhamanebenhammou on 14/09/15.
 */
import com.j256.*;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "record")
class record {
    @DatabaseField
    private String name;
    @DatabaseField(index = true)
    private String filename;
    @DatabaseField
    private boolean favorite;
    //durate in seconde
    @DatabaseField
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
