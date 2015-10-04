package com.abdelrhamane.dufaux.jass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.abdelrhamane.dufaux.jass.models.DatabaseHelper;
import com.abdelrhamane.dufaux.jass.models.RecordItemAdapter;
import com.abdelrhamane.dufaux.jass.models.record;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;

/**
 *
 */
public class Main extends OrmLiteBaseActivity<DatabaseHelper> {

    private ListView listView;
    private Button newSound;
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
                Intent intent = new Intent(Main.this, recorder_activity.class);
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

    public void LoadList() {
        // Listing des sons
        RuntimeExceptionDao<record, Integer> simpleDao = null;
        try {
            simpleDao = getHelper().getRuntimeExceptionDao(record.class);
            ArrayList<record> Result = (ArrayList<record>) simpleDao.queryForAll();
            RecordItemAdapter itemsAdapter =
                    new RecordItemAdapter(this, R.layout.item_recorded, Result,simpleDao);
            listView.setAdapter(itemsAdapter);
            itemsAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            display_alert(e.getMessage());
        }
    }

    private void display_alert(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
