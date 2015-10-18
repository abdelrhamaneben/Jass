package com.abdelrhamane.dufaux.jass.models;

/**
 * Created by abdelrhamanebenhammou on 14/09/15.
 */
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.abdelrhamane.dufaux.jass.Exceptions.AlreadyListeningException;
import com.abdelrhamane.dufaux.jass.Exceptions.NoListeningException;
import com.abdelrhamane.dufaux.jass.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@DatabaseTable(tableName = "records")
public class record {
    @DatabaseField
    private String name;
    @DatabaseField(index = true, id = true)
    private String filename;
    @DatabaseField
    private boolean favorite;
    //durate in seconde
    @DatabaseField
    private int durate;

    // No Persistante Properties
    private MediaRecorder myAudioRecorder;
    private String outDirectory =   Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    private MediaPlayer mediaPlayed = null;

    // Contructors
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


    // DAO Functions

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
     */
    public void save(RuntimeExceptionDao<record, Integer> myDAO) {
        if(myDAO.queryForEq("filename", this.filename).isEmpty()) {
            myDAO.create(this);
        }
        else {
            myDAO.update(this);
        }
    }

    public boolean delete() {
        File file = new File(this.outDirectory + this.filename);
        return file.delete();

    }



    public String getFilePath(){
        return  this.outDirectory + this.filename;
    }

    // Media Functions

    /**
     *
     * @throws IOException
     * @throws AlreadyListeningException
     */
    public void record() throws IOException, AlreadyListeningException {
        if(myAudioRecorder != null) throw new AlreadyListeningException();
        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(this.outDirectory + this.filename);
        myAudioRecorder.prepare();
        myAudioRecorder.start();
    }

    /**
     *
     * @throws NoListeningException
     */
    public void stopRecord() throws NoListeningException {
        if(myAudioRecorder == null) throw new NoListeningException();
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
    }


    /**
     *
     * @throws IOException
     */
    public void play() throws IOException {
        stopPlaying();
        mediaPlayed = new MediaPlayer();
        mediaPlayed.setDataSource(this.outDirectory + this.filename);
        mediaPlayed.prepare();
        mediaPlayed.start();
    }

    /**
     *
     * @throws IOException
     */
    public void play(MediaPlayer.OnCompletionListener callback) throws IOException {
        this.play();
        mediaPlayed.setOnCompletionListener(callback);
    }

    public void stopPlay(){
        stopPlaying();
    }

    private void stopPlaying() {
        if (mediaPlayed != null) {
            mediaPlayed.stop();
            mediaPlayed.release();
            mediaPlayed = null;
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
