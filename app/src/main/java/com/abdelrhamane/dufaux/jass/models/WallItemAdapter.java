package com.abdelrhamane.dufaux.jass.models;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.abdelrhamane.dufaux.jass.JassWall;
import com.abdelrhamane.dufaux.jass.R;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by abdelrhamanebenhammou on 28/09/15.
 */
public class WallItemAdapter extends ArrayAdapter<record> {
    private RuntimeExceptionDao<record, Integer> myDAO;
    public WallItemAdapter(Context context, int resource, ArrayList<record> objects, RuntimeExceptionDao<record, Integer> myDAO) {
        super(context, resource, objects);
        this.myDAO = myDAO;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final JassWall activity = (JassWall) getContext();

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
                    MediaPlayer.OnCompletionListener myCallBackFunction = new MediaPlayer.OnCompletionListener()
                    {
                        @Override
                        public void onCompletion(MediaPlayer mp)
                        {
                            stop.setVisibility(View.INVISIBLE);
                            play.setVisibility(View.VISIBLE);
                        }
                    };
                    r.play(myCallBackFunction);
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

        // Delete record
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle("Confirmer la suppression...");
                alertDialog.setMessage("Êtes-vous sur de vouloir supprimer cet élément?");
                alertDialog.setIcon(R.drawable.button_delete);

                alertDialog.setPositiveButton("CONFIRMER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        r.delete();
                        myDAO.delete(r);
                        activity.LoadList();
                    }
                });
                alertDialog.setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();



            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
