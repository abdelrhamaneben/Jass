package com.abdelrhamane.dufaux.jass.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.abdelrhamane.dufaux.jass.DownloadActivity;
import com.abdelrhamane.dufaux.jass.R;

import java.util.List;

/**
 * Created by dufaux on 19/10/15.
 */
public class DownloadItemAdapter extends ArrayAdapter<String> {

    public DownloadItemAdapter(DownloadActivity downloadActivity, int item_downloadable, List<String> liste) {
        super(downloadActivity,item_downloadable,liste);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final DownloadActivity activity = (DownloadActivity) getContext();

        // Get the data item for this position
        final String filename = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_downloadable, parent, false);
        }
        // Lookup view for data population
        TextView path = (TextView) convertView.findViewById(R.id.item_downloadable_label);
        final ImageButton downloadbutton = (ImageButton) convertView.findViewById(R.id.item_downlodable_download_button);
        // Populate the data into the template view using the data object
        path.setText(filename);

        System.out.println("DISPLAY "+filename);

        // Stop record
        downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.downloadFileFromCurrentDirectory(filename);
            }
        });


        // Return the completed view to render on screen
        return convertView;
    }
}
