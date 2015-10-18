package com.abdelrhamane.dufaux.jass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abdelrhamane.dufaux.jass.Exceptions.NoListeningException;
import com.abdelrhamane.dufaux.jass.models.DatabaseHelper;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

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

    private record enregistrement;
    /** représente le temps restant d'enregistrement */
    int timer;
    /** repressante le timer qui se lance tous les 10 secondes*/
    Thread scheduler;


    private String outputFile = null;

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
        record.setBackgroundResource(R.color.bleu);
        stop_play.setBackgroundResource(R.color.gris);
        timerLabel.setText(String.valueOf(timer));
        state = State_E.RECORDABLE;
        this.enregistrement = new record((new Date().getTime()) + ".mp3");
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
        this.enregistrement.setName(name);
        simpleDao.create(this.enregistrement);
    }

    /*    PRIMITVES des fonctions _record, _stop et _play*/

    /**
     * Enregistre un son pendant 10 Secondes
     */
    protected void _record(){
        try {
            // lancement de l'enregistrement si il n'y a pas d'enregistrement en cours
            if (state == State_E.RECORDABLE || state == State_E.PLAYABLE) {
                this.enregistrement.record();
                state = State_E.STOPABLE;
                record.setEnabled(false);
                record.setBackgroundResource(R.color.gris);
                stop_play.setBackgroundResource(R.color.rouge);
                record.setEnabled(false);
                stop_play.setEnabled(true);
                display_alert("Recording started");
                launchTimer();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            display_alert(e.getMessage());
            initRecorder();
            return;
        }
    }

    /**
     * Joue le dernier son ajouté
     */
    protected void _play() {
        try {
            this.enregistrement.play();
            display_alert("Playing audio");
            initRecorder();
        } catch (Exception e) {
            display_alert(e.getMessage());
            initRecorder();
            return;
        }
    }

    /**
     * Arrete l'enregistrement audio
     */
    protected void _stop() {
        try {
            this.enregistrement.stopRecord();
            state = State_E.PLAYABLE;
            stop_play.setText("Play");
            stop_play.setBackgroundResource(R.color.bleu);
            display_alert( "Audio recorded successfully");
            scheduler.interrupt();
            askName();
        } catch (NoListeningException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            initRecorder();
        }
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

    private void display_alert(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
