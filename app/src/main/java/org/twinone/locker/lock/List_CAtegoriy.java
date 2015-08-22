package org.twinone.locker.lock;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twinone.locker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class List_CAtegoriy extends Activity {
    public static boolean UPDATING = false;
    public static String UPDATEINFO = "NOT STARTED";

    private HashMap<String, Boolean> updateLog ;
    TextView text=null;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("test", "List_CAtegoriy called");

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.list_catergory);

        final CategoryTable values = new CategoryTable();

        //retrieve a list of all packages currently on the phone
        RetrievePackages r = new RetrievePackages(this);
        final List<String> packages = r.getPackages();

        //Collections.shuffle(packages);


        //categorise apps on the phone by hitting the 42matters.com api
        final Categorise c = new Categorise(values);
        Log.d("test", "package size = "+packages.size());

        Thread t= new Thread(new Runnable() {
            @Override
            public void run() {
                UPDATING = true;

                int i=0;
                Log.d("test", "package size = "+packages.size());

                while(i<packages.size()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    c.categorise_app(packages.get(i));
                    UPDATEINFO = "updating: "+i+"/"+ packages.size();

                   // Toast.makeText(List_CAtegoriy.this, UPDATEINFO, Toast.LENGTH_SHORT).show();

                    i++;
                }

                //write to the file
                ObjectOutputStream os = null;
                try {
                    FileOutputStream fos = openFileOutput("category.txt", Context.MODE_PRIVATE);
                    os = new ObjectOutputStream(fos);
                    os.writeObject(values);
                    os.close();
                    fos.close();



//                    FileInputStream fis = openFileInput("category.txt");
//                    ObjectInputStream is = new ObjectInputStream(fis);
//                    CategoryTable simpleClass = (CategoryTable) is.readObject();
//
//                    String s="";
//                    for (Map.Entry entry : simpleClass.table.entrySet()) {
//                        Log.d("----read from file----", "package name:" + entry.getKey() + "value name:" + entry.getValue());
//                        s="package name:" + entry.getKey() + "value name:" + entry.getValue()+"\n";
//                    }
//
//
//                    is.close();
//                    fis.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                UPDATING = false;
                UPDATEINFO = "FINISHED";
//trigger


            }
        });



        //display the list of package names along with category
        //listView = (ListView) findViewById(R.id.listx);

        Log.d("test", "step1");



        String[] values_of_list;
        List<String> l = new ArrayList<String>();
        try{
            readFile(l);
            if (!(l.size()==packages.size())){
                Log.d("list", "not right");
                t.start();

            }

            Log.d("list", "is right");
        }catch(IOException e){
            t.start();
         //   Toast.makeText(this, "updating   ", Toast.LENGTH_SHORT).show();

        }

//        values_of_list=l.toArray(new String[l.size()]);
//
//
//
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, android.R.id.text1, values_of_list);
//        listView.setAdapter(adapter);






    }

    private void readFile(List<String> l) throws IOException{

        FileInputStream fis = null;
       try {
           Log.d("AAAAAAAAAAAAAAAAAAAAA", "entererd");
           fis = openFileInput("category.txt");
           ObjectInputStream is = new ObjectInputStream(fis);
           CategoryTable simpleClass = (CategoryTable) is.readObject();
           String s = "";


           for (Map.Entry entry : simpleClass.table.entrySet()) {
               l.add("package:" + entry.getKey() + "\n" + "Category:" + entry.getValue());
               Log.d("----read from file----", "package:" + entry.getKey() + "Category:" + entry.getValue());
               s = "package name:" + entry.getKey() + "value name:" + entry.getValue() + "\n";
           }
           is.close();
           fis.close();
       }
        catch(ClassNotFoundException e){
            Log.d("class not found ","class not found exception");
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //display the list of package names along with category
        //listView = (ListView) findViewById(R.id.listx);


        String[] values_of_list;
        List<String> l = new ArrayList<String>();
        try{
            readFile(l);
        }catch(IOException e){
        }
        values_of_list=l.toArray(new String[l.size()]);




       // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
             //   android.R.layout.simple_list_item_1, android.R.id.text1, values_of_list);
        //listView.setAdapter(adapter);


    }



}
