package com.abdelrhamane.dufaux.jass.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abdelrhamane.dufaux.jass.Main;
import com.abdelrhamane.dufaux.jass.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.ArrayList;

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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_recorded, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.record_name);
        final ImageButton play = (ImageButton) convertView.findViewById(R.id.play_button);
        final ImageButton stop = (ImageButton) convertView.findViewById(R.id.stop_button);
        ImageButton delete = (ImageButton) convertView.findViewById(R.id.delete_button);
        // Populate the data into the template view using the data object
        name.setText(r.getName());

        // Play record
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    r.play();
                    play.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    System.out.println("Impossible de lire le fichier audio");
                }
            }
        });

        // Stop record
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.stopPlay();
                stop.setVisibility(View.INVISIBLE);
                play.setVisibility(View.VISIBLE);
            }
        });

        // Delete Record
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r.delete();
                myDAO.delete(r);
                Main activity = (Main) getContext();
                activity.LoadList();

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
