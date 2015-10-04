package com.abdelrhamane.dufaux.jass;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.abdelrhamane.dufaux.jass.models.DatabaseHelper;
import com.abdelrhamane.dufaux.jass.models.WallItemAdapter;
import com.abdelrhamane.dufaux.jass.models.record;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;

public class JassWall extends OrmLiteBaseActivity<DatabaseHelper> {

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jasswall);
        listView = (ListView) findViewById(R.id.listRecord);
    }

    public void LoadList() {
        // Listing des sons
        RuntimeExceptionDao<record, Integer> simpleDao = null;
        try {
            ArrayList<record> Result = new ArrayList<record>();
            simpleDao = getHelper().getRuntimeExceptionDao(record.class);
            WallItemAdapter itemsAdapter =
                    new WallItemAdapter(this, R.layout.item_record, Result,simpleDao);
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
