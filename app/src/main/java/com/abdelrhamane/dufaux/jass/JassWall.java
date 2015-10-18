package com.abdelrhamane.dufaux.jass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.abdelrhamane.dufaux.jass.models.DatabaseHelper;
import com.abdelrhamane.dufaux.jass.models.WallItemAdapter;
import com.abdelrhamane.dufaux.jass.models.record;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 */
public class JassWall extends OrmLiteBaseActivity<DatabaseHelper> {

    private ListView listView;
    private Button newSound;
    private Button fromInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newSound = (Button) findViewById(R.id.Main_activity_NewSound);
        listView = (ListView) findViewById(R.id.listRecord);

        //Bouton nouveau Son
        newSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JassWall.this, recorder_activity.class);
                startActivity(intent);
            }
        });

        fromInternet = (Button) findViewById(R.id.Main_activity_FromInternet);
        fromInternet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JassWall.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

        LoadList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.LoadList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.LoadList();
    }


    public void LoadListFromArray(ArrayList<record> results){

    }
    
    public void LoadList() {
        // Listing des sons
        RuntimeExceptionDao<record, Integer> simpleDao = null;
        try {
            simpleDao = getHelper().getRuntimeExceptionDao(record.class);
            ArrayList<record> Result = (ArrayList<record>) simpleDao.queryForAll();
            WallItemAdapter itemsAdapter =
                    new WallItemAdapter(this, R.layout.item_recorded, Result,simpleDao);
            listView.setAdapter(itemsAdapter);
            itemsAdapter.notifyDataSetChanged();

            listView.setOnItemLongClickListener(
                    new OnItemLongClickListener() {

                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            record r = (record) parent.getItemAtPosition(position);
                            askName(r);
                            return true;
                        }
                    }

            );


        } catch (Exception e) {
            display_alert(e.getMessage());
        }
    }

    private void display_alert(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


    /**
     * Demande le nom du fichier
     */
    private void askName(final record r) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(JassWall.this);
        alertDialog.setTitle("Nom");
        alertDialog.setMessage("Entrez le nouveau nom");

        final EditText input = new EditText(JassWall.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Enregistrer",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        r.setName(input.getText().toString());
                        RuntimeExceptionDao<record, Integer> simpleDao = null;
                        try {
                            simpleDao = getHelper().getRuntimeExceptionDao(record.class);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        r.save(simpleDao);
                    }
                });
        alertDialog.setNegativeButton("Annuler",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }



}
