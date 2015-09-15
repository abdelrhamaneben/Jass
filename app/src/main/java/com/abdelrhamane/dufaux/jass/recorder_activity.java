package com.abdelrhamane.dufaux.jass;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

public class recorder_activity extends AppCompatActivity {
    Button stop_play,record;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Boolean free = true;
    private enum State_E {
        RECORDABLE,
        STOPABLE,
        PLAYABLE;
    };
    private State_E state;


    private void initRecorder() {
        stop_play.setText(R.string.Stop);
        stop_play.setEnabled(false);
        state = State_E.RECORDABLE;
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TemporalRecord.3gp";;

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_activity);
        stop_play=(Button)findViewById(R.id.buttonStopPlay);
        record=(Button)findViewById(R.id.buttonRecord);

        initRecorder();

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (state == State_E.RECORDABLE) {
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                        state = State_E.STOPABLE;
                    }

                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "IllegalStateException", Toast.LENGTH_LONG).show();
                    initRecorder();
                    return ;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_LONG).show();
                    initRecorder();
                    return ;
                }

                record.setEnabled(false);
                stop_play.setEnabled(true);

                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });


        stop_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state == State_E.STOPABLE) {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder  = null;
                    state = State_E.PLAYABLE;
                    stop_play.setText("Play");
                    Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
                }
                else if (state == State_E.PLAYABLE) {
                    MediaPlayer m = new MediaPlayer();

                    try {
                        m.setDataSource(outputFile);
                    }
                    catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "IOException 1", Toast.LENGTH_LONG).show();
                        initRecorder();
                        return ;
                    }
                    try {
                        m.prepare();
                    }
                    catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "IOException 2", Toast.LENGTH_LONG).show();
                        initRecorder();
                        return ;
                    }
                    m.start();
                    Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
