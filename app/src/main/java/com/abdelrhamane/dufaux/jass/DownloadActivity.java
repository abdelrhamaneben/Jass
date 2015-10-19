package com.abdelrhamane.dufaux.jass;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.abdelrhamane.dufaux.jass.Exceptions.InvalidFileTypeException;
import com.abdelrhamane.dufaux.jass.models.DatabaseHelper;
import com.abdelrhamane.dufaux.jass.models.DownloadItemAdapter;
import com.abdelrhamane.dufaux.jass.models.record;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DownloadActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private static String[] authorizedExtension = new String[] {"MP3","OGG","3GP","MP4"};
    private EditText urlfield;
    private ListView listdownloadable;
    private String directoryPath = "http://www.saladin-web.fr/jass";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        urlfield = (EditText) findViewById(R.id.urltext);
        listdownloadable = (ListView) findViewById(R.id.listDownloadable);

        Button downloadbutton = (Button) findViewById(R.id.downloadbutton);
        downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveFileFromEditText();
            }
        });


        Button btnReturn = (Button) findViewById(R.id.activity_download_return);
        // quitter l'activite
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        String url= directoryPath;
        new DownloadJSONTask().execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void LoadListDownloadable(List<String> liste) {
        System.out.println("LOADLIST");
        //RuntimeExceptionDao<record, Integer> simpleDao = null;
        try {
            //simpleDao = getHelper().getRuntimeExceptionDao(record.class);
            //ArrayList<record> Result = (ArrayList<record>) simpleDao.queryForAll();
            DownloadItemAdapter itemsAdapter = new DownloadItemAdapter(this, R.layout.item_downloadable, liste);
            listdownloadable.setAdapter(itemsAdapter);
            itemsAdapter.notifyDataSetChanged();


        } catch (Exception e) {
            display_alert(e.getMessage());
        }
    }

    public void SaveFileFromEditText(){
        urlfield = (EditText) findViewById(R.id.urltext);
        String url = urlfield.getText().toString();

        //new DownloadFilesTask().execute("http://www.instantsfun.es/audio/badumtss.mp3");
        new DownloadFilesTask().execute(url);
    }

    public void downloadFileFromCurrentDirectory(String filename){
        new DownloadFilesTask().execute(directoryPath + "/" + filename);
    }

    private void display_alert(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private List<String> getListFromJsonArray(JSONArray array) throws JSONException {
        ArrayList<String> fichiers = new ArrayList<String>();
        for (int i = 0; i < array.length(); i++) {
            //JSONObject obj = new JSONObject(array.getString(i));
            fichiers.add(array.getString(i));
        }

        return fichiers;
    }

    private static String InputStreamToString (InputStream in, int bufSize) {
        final StringBuilder out = new StringBuilder();
        final byte[] buffer = new byte[bufSize];
        try {
            for (int ctr; (ctr = in.read(buffer)) != -1;) {
                out.append(new String(buffer, 0, ctr));
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot convert stream to string", e);
        }
        // On retourne la chaine contenant les donnees de l'InputStream
        return out.toString();
    }

    private static String InputStreamToString (InputStream in) {
        // On appelle la methode precedente avec une taille de buffer par defaut
        return InputStreamToString(in, 1024);
    }

    /**
     * PRIVATES CLASS
     */

    /**
     * new DownloadFilesTask().execute(url) to download file. (multiple url does probably work).
     */
    private class DownloadFilesTask extends AsyncTask<String, Integer, Object> {

        /**
         * @param urls
         * @return
         */
        @Override
        protected Object doInBackground(String... urls) {
            int count = urls.length;
            long totalSize = 0;
            for (int i = 0; i < count; i++) {
                return Download(urls[i]);
            }
            return null;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {
        }



        // This is called when doInBackground() is finished
        protected void onPostExecute(Object result) {
            if(result instanceof MalformedURLException) {
                //EditText champurl = (EditText) findViewById(R.id.urltext);
                urlfield.setBackgroundResource(R.drawable.border_alert);
                display_alert("URL incorrect");
            }
            if(result instanceof InvalidFileTypeException){
                urlfield.setBackgroundResource(R.drawable.border_alert);
                display_alert(((InvalidFileTypeException) result).getMessage());
            }
            if(result instanceof Exception){
                display_alert("ERROR: "+((Exception) result).getMessage());
            }
            else{
                //EditText champurl = (EditText) findViewById(R.id.urltext);
                urlfield.setBackgroundResource(android.R.drawable.edit_text);
                display_alert("Son correctement enregistré");
            }
        }




        private Object Download(String urlpath)
        {
            String baseName = FilenameUtils.getBaseName(urlpath);
            String extension = FilenameUtils.getExtension(urlpath);
            String filename = FilenameUtils.getName(urlpath);


            if(!  Arrays.asList(authorizedExtension).contains(extension.toUpperCase())){
                return new InvalidFileTypeException(extension+" n'est pas une extension valide ");
            }

            System.out.println("BASENAME = "+baseName+", FILENAME = "+filename);

            //enregistre record
            record r = new record(filename);
            r.setName(baseName);
            String filePath = r.getFilePath();


            try {


                //set the download URL, a url that points to a file on the internet
                //this is the file to be downloaded
                URL url = new URL(urlpath);

                //create the new connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //set up some things on the connection
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //and connect!
                urlConnection.connect();

                //set the path where we want to save the file
                //in this case, going to save it on the root directory of the
                //sd card.
                File SDCardRoot = Environment.getExternalStorageDirectory();
                //create a new file, specifying the path, and the filename
                //which we want to save the file as.

                System.out.println("creation file = "+filePath);
                File file = new File(filePath);

                //this will be used to write the downloaded data into the file we created
                FileOutputStream fileOutput = new FileOutputStream(file);

                //this will be used in reading the data from the internet
                try{
                    InputStream inputStream = urlConnection.getInputStream();
                    //this is the total size of the file
                    int totalSize = urlConnection.getContentLength();
                    //variable to store total downloaded bytes
                    int downloadedSize = 0;

                    //create a buffer...
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0; //used to store a temporary size of the buffer

                    //now, read through the input buffer and write the contents to the file
                    while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                        //add the data in the buffer to the file in the file output stream (the file on the sd card
                        fileOutput.write(buffer, 0, bufferLength);
                        //add up the size so we know how much is downloaded
                        downloadedSize += bufferLength;
                        //this is where you would do something to report the prgress, like this maybe
                        //updateProgress(downloadedSize, totalSize);
                        //##########################################################################"
                    }
                    fileOutput.close();
                }
                catch(FileNotFoundException e){
                    e.printStackTrace();
                    return e;
                }

                RuntimeExceptionDao<record, Integer> simpleDao = null;
                try {
                    simpleDao = getHelper().getRuntimeExceptionDao(record.class);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return e;
                }
                r.save(simpleDao);


                //catch some possible errors...
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    /*
     * new DownloadJSONTask().execute(url);
     */
    private class DownloadJSONTask extends AsyncTask<String, Integer, JSONArray> {

        private boolean error = false;
        @Override
        protected JSONArray doInBackground(String... urls) {
            JSONArray array = null;
            try {
                array = getJSONFromUrl(urls[0]);
                System.out.println(array);
            } catch (IOException e) {
                error = true;
                e.printStackTrace();
            } catch (JSONException e) {
                error = true;
                e.printStackTrace();
            }
            System.out.println("FIN DE DoInBackground");
            return array;
        }

        // This is called each time you call publishProgress()
        protected void onProgressUpdate(Integer... progress) {
        }



        // This is called when doInBackground() is finished
        protected void onPostExecute(JSONArray result) {
            System.out.println("DEBUT DE onPostExecute");
            System.out.println(result);
            System.out.println(error);

            if(error) {
                display_alert("Impossible de recuperer la liste du serveur.");
            }
            else{
                try {
                    List<String> files = getListFromJsonArray(result);

                    LoadListDownloadable(files);
                } catch (JSONException e) {
                    display_alert("Impossible de recuperer la liste du serveur.");
                }

            }
        }

        private JSONArray getJSONFromUrl(String myurl) throws IOException, JSONException {

            URL url = new URL(myurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            String result = InputStreamToString(inputStream);
            JSONArray array = new JSONArray(result);
            return array;
        }

    }
}
