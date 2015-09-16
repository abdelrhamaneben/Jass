package com.abdelrhamane.dufaux.jass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.abdelrhamane.dufaux.jass.models.DatabaseHelper;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

/**
 *
 */
public class Main extends OrmLiteBaseActivity<DatabaseHelper> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button newSound = (Button) findViewById(R.id.Main_activity_NewSound);

        newSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, recorder_activity.class);
                startActivity(intent);
            }
        });


       /* RuntimeExceptionDao<record, Integer> simpleDao = null;
        try {
            simpleDao = getHelper().getRuntimeExceptionDao(record.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        record r = new record("test informel");
        simpleDao.create(r);

        List<record> Result = simpleDao.queryForAll();


        for(record i : Result) {
            System.out.println(i.toString());
        }*/
    }
}
