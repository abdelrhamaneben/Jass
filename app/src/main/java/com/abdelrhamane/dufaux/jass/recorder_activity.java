package com.abdelrhamane.dufaux.jass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abdelrhamane.dufaux.jass.models.DatabaseHelper;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import java.io.IOException;
import java.util.Date;

import com.abdelrhamane.dufaux.jass.models.record;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 *
 */
public class recorder_activity extends OrmLiteBaseActivity<DatabaseHelper> {

    recorder_activity controller;
    Button stop_play,record, btnReturn;
    TextView timerLabel;
    /** représente le temps restant d'enregistrement */
    int timer;
    /** repressante le timer qui se lance tous les 10 secondes*/
    Thread scheduler;

    /**  MediaRecorder et MediaPlayer */
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private String filename = null;

    /** Etat de l'enregistrement */
    private enum State_E {
        RECORDABLE,
        STOPABLE,
        PLAYABLE;
    };
    private State_E state;

    /**
     * Initialise l'enregistrement
     */
    private void initRecorder() {
        timer = 10;
        stop_play.setText(R.string.Stop);
        stop_play.setEnabled(false);
        record.setEnabled(true);
        record.setBackgroundColor(Color.GREEN);

        timerLabel.setText(String.valueOf(timer));
        state = State_E.RECORDABLE;
        filename =  (new Date().getTime()) + ".3gp";
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename;

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }


    /**
     * Création de la vue et des evenements associés
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_activity);
        stop_play=(Button)findViewById(R.id.Recorder_activity_BtnStopPlay);
        record=(Button)findViewById(R.id.Recorder_activity_BtnRecord);
        btnReturn=(Button)findViewById(R.id.Recorder_activity_BtnReturn);
        timerLabel=(TextView)findViewById(R.id.Recorder_activity_LabelTimer);

        initRecorder();

        // Action associé au bouton Record
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _record();
            }
        });

        // action associé au bouton stop et play
        stop_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state == State_E.STOPABLE) {
                    _stop();
                } else if (state == State_E.PLAYABLE) {
                    _play();
                }
            }
        });
        // quitter l'activite
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        controller = this;
    }

    protected void saveRecord(String name) {
        RuntimeExceptionDao<record, Integer> simpleDao = null;
        try {
            simpleDao = getHelper().getRuntimeExceptionDao(record.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        record r = new record(filename);
        r.setName(name);
        simpleDao.create(r);
    }

    /*    PRIMITVES des fonctions _record, _stop et _play*/

    /**
     * Enregistre un son pendant 10 Secondes
     */
    protected void _record() {
        try {
            // lancement e l'enregistrement si il n'y a pas d'enregistrement en cours
            if (state == State_E.RECORDABLE || state == State_E.PLAYABLE) {
                myAudioRecorder.prepare();
                myAudioRecorder.start();
                state = State_E.STOPABLE;
                record.setEnabled(false);
                record.setBackgroundColor(Color.RED);
                launchTimer();
            }
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Toast.makeText(getApplicationContext(), "IllegalStateException", Toast.LENGTH_LONG).show();
            initRecorder();
            return;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_LONG).show();
            initRecorder();
            return;
        }

        record.setEnabled(false);
        stop_play.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
    }

    /**
     * Joue le dernier son ajouté
     */
    protected void _play() {
        MediaPlayer m = new MediaPlayer();

        try {
            m.setDataSource(outputFile);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "IOException 1", Toast.LENGTH_LONG).show();
            initRecorder();
            return;
        }
        try {
            m.prepare();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "IOException 2", Toast.LENGTH_LONG).show();
            initRecorder();
            return;
        }
        m.start();
        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
    }

    /**
     * Arrete l'enregistrement audio
     */
    protected void _stop() {
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        state = State_E.PLAYABLE;
        stop_play.setText("Play");
        System.out.println("Can play");
        Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
        scheduler.interrupt();
        askName();
    }

    /**
     * lance le timer de 10 secondes
     */
    private void launchTimer() {
        scheduler = new Thread() { //new thread
            public void run() {
                boolean b = true;
                try {
                    do {

                        sleep(1000);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                timer--;
                                timerLabel.setText(String.valueOf(timer));
                                if (timer == 0) {
                                   _stop();
                                }
                            }
                        });
                    }
                    while (b == true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                }
            };
        };
        scheduler.start();
    }

    /**
     * Demande le nom du fichier
     */
    private void askName() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(recorder_activity.this);
        alertDialog.setTitle("Name");
        alertDialog.setMessage("Entrer le nom de votre Pitch");

        final EditText input = new EditText(recorder_activity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Enregistrer",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveRecord(input.getText().toString());
                    }
                });
        alertDialog.setNegativeButton("Annuler",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        initRecorder();
                    }
                });
        alertDialog.show();
    }
}
