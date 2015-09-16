package com.abdelrhamane.dufaux.jass.models;

/**
 * Created by abdelrhamanebenhammou on 14/09/15.
 */
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;
import java.util.List;

@DatabaseTable(tableName = "records")
public class record {
    @DatabaseField
    private String name;
    @DatabaseField(index = true)
    private String filename;
    @DatabaseField
    private boolean favorite;
    //durate in seconde
    @DatabaseField
    private int durate;


    public record() {
        this.name = null;
        this.favorite = false;
        this.durate = 0;
        this.filename = null;
    }

    public record(String filename) {
        this.name = "unknow";
        this.filename = filename;
        this.favorite = false;
        this.durate = 0;
    }

    public record(String name, String filename, boolean favorite, int durate) {
        this.name = name;
        this.filename = filename;
        this.favorite = favorite;
        this.durate = durate;
    }


    // CRUD

    /**
     *  Return the list of all record
     * @param myDAO
     * @return List<Record>
     */
    public static List<record> all(RuntimeExceptionDao<record, Integer> myDAO) {
        return myDAO.queryForAll();
    }

    /**
     * Load a Record by Filename
     * @param myDAO
     * @param filename
     * @return
     * @throws SQLException
     */
    public static record load(RuntimeExceptionDao<record, Integer> myDAO, String filename) throws SQLException {
        QueryBuilder<record, Integer> builder = myDAO.queryBuilder();
        builder.where().eq("filename",filename);
        return myDAO.queryForFirst(builder.prepare());
    }


    /**
     * Save the current Record in DataBase
     * @param myDAO
     */
    public void save(RuntimeExceptionDao<record, Integer> myDAO) {
        if(myDAO.queryForEq("filename", this.filename).isEmpty()) {
            myDAO.create(this);
        }
        else {
            myDAO.update(this);
        }
    }


    // GETTERS AND SETTERS
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getDurate() {
        return durate;
    }

    public void setDurate(int durate) {
        this.durate = durate;
    }
}
