package org.twinone.locker.lock;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Skandy on 7/24/2015.
 */
public class Categorise {
    CategoryTable values;
    String pack;
    public Categorise(CategoryTable values){
        Log.d("test", "Categorise builder called");

        this.values=values;

    }

    String output;

    public String categorise_app(String pack){

        Log.d("test", "categorise_app called");
        values.put(pack, "Started");



        this.pack=pack;
        DownloadTask downloadTask = new DownloadTask();

        String url = getURL(pack);
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
        return output;

    }



    private String getURL(String packagename){


        // Building the parameters to the web service
        String parameters = "p="+packagename;
        String token1 = "64ece1a5e3ebafbaf608fa830d26028337ab50d6";
        String token2 = "d02ee3801dc7a749ae21c585bdb80f39cabdff2c";
        String token3 = "ba9130875e5c3375ae93b83a1ce74953f8c70c12";



        // Building the url to the web service
        String url = "https://42matters.com/api/1/apps/lookup.json?access_token="+token3+"&"+parameters;

        Log.d("response is :", url);

        return url;
    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                //Toast.makeText(MainActivity.this,"CAteogorisled dat:"+data,Toast.LENGTH_LONG).show();
                Log.d("---------------------", data);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject j = new JSONObject(result);
                Log.d("test",pack+":"+j.get("category"));
                output = j.getString("category");
                values.put(pack,output);
            } catch (JSONException e) {
                values.put(pack,"Not Ready");

                Log.d("test",pack+":"+"give value here");

                e.printStackTrace();
            }


        /*    ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);*/
        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.d("---------------------", "before conecting");
            // Connecting to url
            urlConnection.connect();

            Log.d("---------------------", "after conecting");

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            Log.d("---------------------", "stream");

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();
            Log.d("---------------------", data);

            br.close();

        }catch(Exception e){
            Log.d("Exception whil", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
