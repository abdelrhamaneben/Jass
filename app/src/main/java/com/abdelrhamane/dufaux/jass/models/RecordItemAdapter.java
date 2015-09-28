package com.abdelrhamane.dufaux.jass.models;

import android.content.ClipData;
import android.content.Context;
import android.support.v7.internal.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.abdelrhamane.dufaux.jass.Main;
import com.abdelrhamane.dufaux.jass.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdelrhamanebenhammou on 28/09/15.
 */
public class RecordItemAdapter extends ArrayAdapter<record> {
    private RuntimeExceptionDao<record, Integer> myDAO;
    public RecordItemAdapter(Context context, int resource, ArrayList<record> objects,RuntimeExceptionDao<record, Integer> myDAO) {
        super(context, resource, objects);
        this.myDAO = myDAO;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final record r = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.record_name);
        Button play = (Button) convertView.findViewById(R.id.play_btn);
        Button delete = (Button) convertView.findViewById(R.id.delete_btn);
        // Populate the data into the template view using the data object
        name.setText(r.getName());

        // Play record
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    r.play();
                } catch (IOException e) {
                    System.out.println("Impossible de lire le fichier son");
                }
            }
        });

        // Delete Record
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.delete();
                myDAO.delete(r);
                Main activity = (Main)getContext();
                activity.LoadList();

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
