package com.abdelrhamane.dufaux.jass;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.abdelrhamane.dufaux.jass.models.DatabaseHelper;
import com.abdelrhamane.dufaux.jass.models.record;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.List;

public class Main extends OrmLiteBaseActivity<DatabaseHelper> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RuntimeExceptionDao<record, Integer> simpleDao = null;
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
        }
    }
}
