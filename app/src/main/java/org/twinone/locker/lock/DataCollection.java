package org.twinone.locker.lock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by Skandy on 8/7/2015.
 */
public class DataCollection
{
    Context c;
    String curWifi;
    int wifi_score;
    String locationStatus;
    String blueToothStatus;
    String onBodyStatus;
    int xFactor;
    String app_cate;
    int app_score;
    String androidId;
    String packageName;
    String confirmWifi;


    public DataCollection(Context c){
        this.c=c;
    }
    public void collectData(String curWifi, int wifi_score, int intGps, String blueToothStatus, String onBodyStatus, int xFactor, String app_cate, int app_score,String androidId, String packageName, String confirmWifi){
        this.confirmWifi = confirmWifi;

        this.curWifi=curWifi;
        this.wifi_score=wifi_score;
        //Zero is home
        //One in work
        //2 ins NA
        if(intGps==0)
            this.locationStatus="Home";
        else if(intGps==1)
            this.locationStatus="Work";
        else
            this.locationStatus="N/A";
        this.blueToothStatus=blueToothStatus;
        this.onBodyStatus=onBodyStatus;
        this.xFactor=xFactor;
        this.app_cate=app_cate;
        this.app_score=app_score;
        this.androidId=androidId;
        this.packageName=packageName;
        ShowDialog();

    }
    public void ShowDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(c);
        LinearLayout layout = new LinearLayout(c);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        /*final TextView info = new TextView(c);
        info.setText("Location Status:" + locationStatus + "\n" + "Bluetooth Status:" + blueToothStatus + "\n" + "OnBody Status:" + onBodyStatus);
        //t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(info);*/

         /* layout.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
*/


        final RatingBar rating=new RatingBar(c);
        rating.setMax(5);
        rating.setNumStars(5);
        rating.setStepSize(1);
        rating.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(rating);

        View line = new View(c);
        line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 10));
        line.setBackgroundColor(0xffff0000);
        layout.addView(line);

        final TextView t = new TextView(c);
        String parts[] = packageName.split("\\.");
        String appName = "N/A";
        if(parts.length>0)  appName =  parts[(parts.length-1)];
        t.setText("App Name is: " + appName + "\n" + "Current Condition is:" + "\n" + "WiFi Status:" + curWifi + "\n" + "Location Status:" + locationStatus + "\n" + "Bluetooth Status:" + blueToothStatus + "\n" + "OnBody Status:" + onBodyStatus + "\n" + "Do you want to open this app under these conditions, Choose Below ");


        t.setTypeface(null, Typeface.BOLD);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
       // t.setTextColor(0xFF00FF00);
        //t.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(t);


        View line2 = new View(c);
        line2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 10));
        line2.setBackgroundColor(0xffff0000);
        layout.addView(line2);



//        final Spinner spinner = new Spinner(c);
//        ArrayList<String> spinnerArray = new ArrayList<String>();
//        spinnerArray.add("yes");
//        spinnerArray.add("no");
//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(spinnerArrayAdapter);
//        spinner.setBackgroundColor(0xffff0000);
//        layout.addView(spinner);




        //popDialog.setIcon(android.R.drawable.btn_star_big_on);
        popDialog.setTitle("How secure you feel opening this app without a password?");
        popDialog.setView(layout);

        // Button OK
        popDialog.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //txtView.setText(String.valueOf(rating.getProgress()));

                       // Log.d("----------------", "" + rating.getRating() + spinner.getSelectedItem().toString());


                        //upload to parse
                        ParseObject testObject = new ParseObject("RatingData");
                        testObject.put("DeviceId", androidId);
                        testObject.put("PackageName", packageName);
                        //testObject.put("WantToBeOpened",spinner.getSelectedItem().toString());
                        testObject.put("WantToBeOpened", "No");
                        testObject.put("Rating", rating.getRating());
                        testObject.put("CurrentWiFi", curWifi);
                        testObject.put("WiFiScore", wifi_score);
                        testObject.put("Location", locationStatus);
                        testObject.put("Bluetooth", blueToothStatus);
                        testObject.put("OnBody", onBodyStatus);
                        testObject.put("Xfactor", xFactor);
                        testObject.put("Category", app_cate);
                        testObject.put("confirmWifiValue", confirmWifi);
                        testObject.put("CurrentScore", app_score);
                        testObject.saveInBackground();
                        dialog.dismiss();

                    }

                })// Button Cancel
                        .setNegativeButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        //upload to parse
                                        ParseObject testObject = new ParseObject("RatingData");
                                        testObject.put("DeviceId", androidId);
                                        testObject.put("PackageName", packageName);
                                        //testObject.put("WantToBeOpened",spinner.getSelectedItem().toString());
                                        testObject.put("WantToBeOpened", "Yes");
                                        testObject.put("Rating", rating.getRating());
                                        testObject.put("CurrentWiFi", curWifi);
                                        testObject.put("WiFiScore", wifi_score);
                                        testObject.put("Location", locationStatus);
                                        testObject.put("Bluetooth", blueToothStatus);
                                        testObject.put("OnBody", onBodyStatus);
                                        testObject.put("Xfactor", xFactor);
                                        testObject.put("Category", app_cate);
                                        testObject.put("confirmWifiValue", confirmWifi);
                                        testObject.put("CurrentScore", app_score);
                                        testObject.saveInBackground();
                                        dialog.dismiss();


                                        dialog.cancel();
                                    }
                                });
        String appStatus =  appName+  curWifi + locationStatus  + blueToothStatus+onBodyStatus;
        if (AppLockService.appSet.add(appStatus)) {
            // popDialog.create();
            AlertDialog alertDialog = popDialog.create();
        /*alertDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.LTGRAY));*/
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

            alertDialog.show();
        }


    }
}
