package org.twinone.locker.lock;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ThomasZhao on 7/26/15.
 */
public class WifiService extends Service {
    static final public String THE_MESSAGE = "haha1";
    static public String confirmWifi = "N/A";
    static public String confirmWifiH = "N/A";
    static public String confirmWifiW = "N/A";

    SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy |HH|:mm:ss a");
    SimpleDateFormat sdf_day = new SimpleDateFormat("dd:MM:yyyy");



    WifiManager wifi;
    int size = 0;
    List<ScanResult> results;
    String ITEM_KEY = "key";
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    LocalBroadcastManager broadcaster;
   //  LogFragment mLogFragment;

    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
   // private int NOTIFICATION = R.string.local_service_started;
    private void updateList(){

        int max_count_d = 0;
        String max_name_d = "";
        int max_count_n = 0;
        String max_name_n = "";
        HashMap<String, Integer> day = new HashMap<String, Integer>();
        HashMap<String, Integer> night = new HashMap<String, Integer>();




        //test
      //  Toast.makeText(this, "updating....", Toast.LENGTH_SHORT).show();

        try {
            InputStream is = openFileInput("wifiLog");
            if ( is != null ) {

                InputStreamReader inputStreamReader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(inputStreamReader);
                String line = "";

                while ((line = br.readLine()) != null) {
//                    String wifiName = "haha";
//                    String isNight = "yes";
                    String wifiName = line.split(",")[0];
                    String isNight = line.split("\\|")[1];
                    int time = Integer.parseInt(isNight);
                    Log.v("mycontent",line);
                    Log.v("mycontent",wifiName+","+isNight);


                    if ( Math.abs(time-14)<=6){
                        if ( !night.containsKey(wifiName)){
                            night.put(wifiName,0);
                        }
                        else {
                            int cur = night.get(wifiName);
                            night.put(wifiName,cur+1);
                            if ( cur > max_count_d ){
                                max_count_d = cur;
                                max_name_d = wifiName;
                            }
                        }
                    }
                    else{
                        if ( !day.containsKey(wifiName)){
                            day.put(wifiName,0);
                        }
                        else {
                            int cur = day.get(wifiName);
                            day.put(wifiName, cur + 1);
                            if (cur > max_count_n) {
                                max_count_n = cur;
                                max_name_n = wifiName;
                            }
                        }
                    }
                    //Toast.makeText(this, wifiName + "," , Toast.LENGTH_SHORT);
                }

                Log.v("mycontent","d: "+max_name_d+","+max_count_d);
                Log.v("mycontent","n: "+max_name_n+","+max_count_n);
                Calendar c = Calendar.getInstance();
                String strDate = sdf_day.format(c.getTime());
                Log.v("mycontent",max_name_d+","+max_name_n+","+strDate);

                openAlert( "work" ,max_name_d);
                openAlert( "home",max_name_n) ;


                writeToFile("wifiTable", max_name_d + "," + max_name_n + "," + strDate, Context.MODE_PRIVATE);



            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }




    }

    private void update(){
        Calendar c = Calendar.getInstance();

            if (!sdf_day.format(c.getTime()).equals(readFromFile("wifiTable").split(",")[2]))
                updateList();
            else
                Log.v("mycontent",readFromFile("wifiTable").split(",")[2]+"  "+"not today");


        String currentNet = wifi.getConnectionInfo().getSSID();
        int currentIp = wifi.getConnectionInfo().getIpAddress();



        String strDate = sdf.format(c.getTime());

        //int seconds = c.get(Calendar.SECOND);
        //String filename = "wifiLog"+sdf_filename.format(c.getTime());
        String filename = "wifiLog";
        String string = currentNet+","+strDate +"\n";
        FileOutputStream outputStream;


        try {
            outputStream = openFileOutput(filename,  MODE_APPEND);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       // Toast.makeText(this, "updated...."+string, Toast.LENGTH_SHORT).show();

    }

    private Handler handler = new Handler( );

    private Runnable runnable = new Runnable( ) {
        public void run ( ) {
            update();

            handler.postDelayed(this,1000*60*10);
//postDelayed(this,1000)方法安排一个Runnable对象到主线程队列中
        }
    };



    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        WifiService getService() {
            return WifiService.this;
        }
    }
    private void writeToFile(String filename, String content, int mode) {

        filename = filename;
        String string = content;
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, mode);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(String filename) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
//    public void sendResult(String message) {
//        Intent intent = new Intent("haha");
//        if(message != null)
//            intent.putExtra("nothing", message);
//        broadcaster.sendBroadcast(intent);
//    }
    @Override
    public void onCreate() {

        String content = "work,home,02:07:2015";


        writeToFile("wifiTable",content,MODE_PRIVATE);
        String test_content = "\"CMU-SECURE\",02:08:2015 |19|:44:08 PM"+"\n"
                +"\"CMU-SECURE\",02:08:2015 |19|:44:08 PM"+"\n"
                +"\"CMU-SECURE\",02:08:2015 |19|:44:08 PM"+"\n"
                +"\"CMU-SECURE\",02:08:2015 |19|:44:08 PM"+"\n"
                +"\"CMU-SECURE\",02:08:2015 |19|:44:08 PM"+"\n"
                +"\"CMU-GUEST\",02:08:2015 |22|:44:08 PM"+"\n"
                +"\"CMU-GUEST\",02:08:2015 |22|:44:08 PM"+"\n"
                +"\"CMU-GUEST\",02:08:2015 |22|:44:08 PM"+"\n"
                +"\"CMU-GUEST\",02:08:2015 |22|:44:08 PM"+"\n";
        writeToFile("wifiLog",test_content, Context.MODE_PRIVATE);

        //broadcaster = LocalBroadcastManager.getInstance(this);
        handler.postDelayed(runnable, 1000); // 开始Timer
        //handler.removeCallbacks(runnable); //停止Timer
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

      //  onbody b=new onbody(this);

        if (wifi.isWifiEnabled() == false)
        {
           // Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
           // mLogFragment.getLogView().setText("wifi is disabled..making it enabled");

            wifi.setWifiEnabled(true);
        }

        wifi.setWifiEnabled(true);

        arraylist.clear();
        wifi.startScan();
        results = wifi.getScanResults();
        size = results.size();

       // Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
     //   mLogFragment.getLogView().setText("Scanning...");
        String currentNet = wifi.getConnectionInfo().getSSID();
        int currentIp = wifi.getConnectionInfo().getIpAddress();

        StringBuilder sb = new StringBuilder();
        String result = "";

        //workWifi = currentNet;
        //write work to a file


    //    writeToFile("wifiTable",currentNet+","+currentIp, Context.MODE_PRIVATE);

//        String filename = "wifiTable";
//        String string = currentNet+","+currentIp;
//        FileOutputStream outputStream;
//
//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(string.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        String workWifi = readFromFile("wifiTable").split(",")[0];

        if ( currentNet.equals(workWifi)){
            result = "WORK";
        }
        else result = "UNKOWN";
        sb.append("cur="+currentNet+" "+"\n");
        try
        {
            size = size - 1;
            while (size >= 0)
            {
                HashMap<String, String> item1 = new HashMap<String, String>();
                item1.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).capabilities);

                sb.append(results.get(size).SSID + "  " + results.get(size).level+"\n");
                arraylist.add(item1);
                size--;
                // adapter.notifyDataSetChanged();
            }

        }
        catch (Exception e)
        { }
//        this.sendResult(sb.toString());
      //  Toast.makeText(this, "WIFI: " + currentNet+" STATUS: "+result, Toast.LENGTH_SHORT).show();

        // mLogFragment.getLogView().setText(sb.toString());

        // Display a notification about us starting.  We put an icon in the status bar.
        //showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        //mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.
      //  Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
//    private void showNotification() {
//        // In this sample, we'll use the same text for the ticker and the expanded notification
//        CharSequence text = getText(R.string.local_service_started);
//
//        // Set the icon, scrolling text and timestamp
//        Notification notification = new Notification(R.drawable.stat_sample, text,
//                System.currentTimeMillis());
//
//        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, LocalServiceActivities.Controller.class), 0);
//
//        // Set the info for the views that show in the notification panel.
//        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
//                text, contentIntent);
//
//        // Send the notification.
//        mNM.notify(NOTIFICATION, notification);
//    }

    private void openAlert(final String cate, final String name) {
        boolean result=false;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("WiFi Confirmation");

        // set dialog message
        alertDialogBuilder
                .setMessage("Is "+name+" your "+cate+"wifi network?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close

                        //knownBtDevicesMap.put(address, true);
//                        Toast.makeText(this, name + "is a trusted Device.",
//                                Toast.LENGTH_LONG).show();
                        // current activity
                        if(cate.equals("work")) {
                            confirmWifiW = "YES";
                        }
                        else confirmWifiH = "YES";
                        confirmWifi = "H: "+ confirmWifiH+":W: "+confirmWifiW;
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        //knownBtDevicesMap.put(address, false);
//                        Toast.makeText(c, name + "is a untrusted Device.",
//                                Toast.LENGTH_LONG).show();
                        if(cate.equals("work")) {
                            confirmWifiW = "NO";
                        }
                        else confirmWifiH = "NO";
                        confirmWifi = "H: "+ confirmWifiH+":W: "+confirmWifiW;
                        dialog.dismiss();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        // show it
        alertDialog.show();
    }
}
