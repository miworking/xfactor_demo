package org.twinone.locker.lock;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Skandy on 8/9/2015.
 */
public class ListCategory
{
    Context con;
    public static boolean UPDATING = false;
    public static String UPDATEINFO = "NOT STARTED";
    private HashMap<String, Boolean> updateLog ;
    TextView text=null;
    ListView listView;
    List<String> packages;

    CategoryTable values;
    ListCategory(Context c){
        this.con=c;
    }

    public void validate(){
        if(UPDATING) return;
        Log.d("test", "List_Categoriy called");
        //retrieve a list of all packages currently on the phone
        RetrievePackages r = new RetrievePackages(con);
        packages = r.getPackages();
        //categorise apps on the phone by hitting the 42matters.com api
        Log.d("test", "package size = " + packages.size());
        try {
            Map<String,String> prevState = readFile();
            Log.d("test", "prevstate size = " + prevState.size());

            if(prevState.size()!=packages.size()) {
                //in case of new packages being added, restart the asynctask to categorise all apps
                Log.d("status:","Updating new apps that were installed");
                startThread();
            }
        } catch (IOException e) {
            Log.d("Req42matters","first time initialisation");
           // file doens't exist, start the background asynctask to fetch the categories from 42matters API
            startThread();
            e.printStackTrace();
        }
    }

    private void startThread() {
        final CategoryTable v = new CategoryTable();
        Log.d("----------", "entered thread");
        Thread t= new Thread(new Runnable() {
            @Override
            public void run() {
                UPDATING = true;

                int i=0;
                Log.d("test", "package size = "+packages.size());

                Categorise c = new Categorise(v);
                while(i<packages.size()) {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    c.categorise_app(packages.get(i));
                    UPDATEINFO = "updating: "+i+"/"+ packages.size();
                    Log.d("test", "package info = "+UPDATEINFO);

                    // Toast.makeText(List_CAtegoriy.this, UPDATEINFO, Toast.LENGTH_SHORT).show();

                    i++;
                }
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("values;", "" + v.table.size());

                //write to the file
                ObjectOutputStream os = null;
                try {

                    Log.d("test", "writingfile size = "+v.table.size());

                    FileOutputStream fos = con.openFileOutput("category.txt", Context.MODE_PRIVATE);
                    os = new ObjectOutputStream(fos);
                    os.writeObject(v);
                    os.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UPDATING = false;
                UPDATEINFO = "FINISHED";

                try {
                    Map<String,String> m = readFile();
                    Log.d("check prev file size",""+m.size());
                } catch (IOException e) {
                    Log.d("error","error");
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }


    private Map<String,String> readFile() throws IOException{

        CategoryTable simpleClass = null;
        FileInputStream fis = null;
        try {
            Log.d("AAAAAAAAAAAAAAAAAAAAA", "entererd");
            fis = con.openFileInput("category.txt");
            ObjectInputStream is = new ObjectInputStream(fis);
            simpleClass = (CategoryTable) is.readObject();
            String s = "";


            for (Map.Entry entry : simpleClass.table.entrySet()) {
               // l.add("package:" + entry.getKey() + "\n" + "Category:" + entry.getValue());
                Log.d("----read from file----", "package:" + entry.getKey() + "Category:" + entry.getValue());
                s = "package name:" + entry.getKey() + "value name:" + entry.getValue() + "\n";
            }
            is.close();
            fis.close();


        }
        catch(ClassNotFoundException e){
            Log.d("class not found ","class not found exception");

        }
        return simpleClass.table;
    }

}
