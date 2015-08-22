package org.twinone.locker.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.twinone.locker.lock.FetchAddressIntentService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.google.android.gms.location.LocationServices.API;
import static com.google.android.gms.location.LocationServices.FusedLocationApi;

/**
 * Created by Jhalak on 26-Jul-15.
 */

public class AddressUpdate implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    protected static final String TAG = "location-updates-sample";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
//    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
//    protected final static String LOCATION_KEY = "location-key";
//    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    // UI Widgets.
//
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
//
    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

//    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    //  protected static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    //   protected Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;


    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    private Context c;

    public AddressUpdate(Context c){

        Log.i(TAG,"Inside the address update constructor");
        this.c=c;
Log.i("Context", String.valueOf(c));
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        mAddressRequested = true;
//       this.mLatitudeTextView = mLatitudeTextView;
//       this.mLongitudeTextView = mLongitudeTextView;
//        this.mLastUpdateTimeTextView=mLastUpdateTimeTextView;


        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.

        Log.i(TAG,"calling buildgoogleapiclient method");

        buildGoogleApiClient();

        Log.i(TAG, "creating the addressresultreceiver object");

        mResultReceiver = new AddressResultReceiver(new Handler());

    }

    // AddressUpdate addressUpdate = new AddressUpdate(c);


    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */

    protected synchronized void buildGoogleApiClient() {

        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(c)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        Log.i(TAG,"calling the create location request method");

        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */

    protected void createLocationRequest() {

        Log.i(TAG, "creating location request");
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.


        if (mCurrentLocation == null) {
            Log.i(TAG, "got current loocation null");
            mCurrentLocation = FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            Log.i(TAG, "got current loocation NOW");

            if ( mCurrentLocation != null) {
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    //Toast.makeText(c, "no geocoder available", Toast.LENGTH_LONG).show();

                    return;
                }
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                Log.i(TAG, "Calling method to fetch address");
                startIntentService();
            }

        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        Log.i(TAG, "starting the location updates");
        startLocationUpdates();

    }


    protected void startIntentService() {

        Log.i(TAG, "startIntentservice method called!!");
        // Create an intent for passing to the intent service responsible for fetching the address.
        Log.i(TAG, "creating an intent to fetch the address");
        Intent intent = new Intent(c, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurrentLocation);

        //Toast.makeText(c,"-----"+mCurrentLocation.getLatitude(),Toast.LENGTH_SHORT).show();


        Log.i("inside strtintenservice", "" + mCurrentLocation.getLatitude());
        Log.i("inside strtintenservice",""+mCurrentLocation.getLongitude());

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        Log.i(TAG, "Starting the service to fetch address");
        c.startService(intent);
    }

    //protected void showToast(String text) {
    // Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
    //}

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Callback that fires when the location changes.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onLocationChanged(Location location) {

        Log.i(TAG, "inside On location changed method");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mAddressRequested = true;


        Log.i(TAG, "Location Updated");

        Log.i("Latitude", String.valueOf(mCurrentLocation.getLatitude()));
        Log.i("Longitude", String.valueOf(mCurrentLocation.getLongitude()));

        if (mCurrentLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                //Toast.makeText(c, "no geocoder available", Toast.LENGTH_LONG).show();

                return;
            }


            startIntentService();

        }
        Log.i(TAG,"Calling the startIntentService method");
        startIntentService();

        Log.i(TAG, "Calling the addressstore method");
        mResultReceiver.addressStore(c);

        updateUI();


    }


    private void updateUI() {


    }






    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        Log.i(TAG,"Calling the startLocationUpdates method");
        FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
//    @Override
//    protected void onStart() {
//      super.onStart();
//
//            mGoogleApiClient.connect();
//
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        // Within {@code onPause()}, we pause location updates, but leave the
//        // connection to GoogleApiClient intact.  Here, we resume receiving
//        // location updates if the user has requested them.
//
//        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
}

//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
//        if (mGoogleApiClient.isConnected()) {
//
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        mGoogleApiClient.disconnect();
//
//        super.onStop();
//    }

/**
 * Receiver for data sent from FetchAddressIntentService.
 */
class AddressResultReceiver extends ResultReceiver {



    protected TextView mLocationAddressTextView;
    public String mAddressOutput= "";
    //time variables
    long t1=0;

    HashMap<String,Integer> home= new HashMap<>();
    HashMap<String,Integer> work= new HashMap<>();
    LRU homeCache= new LRU(3,0.1f);
    LRU workCache= new LRU(3,0.1f);
    String homeAddress="";
    String workAddress="";

    public AddressResultReceiver(Handler handler) {
        super(handler);
       // this.mLocationAddressTextView = mLocationAddressTextView;
    }

    /**
     *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {


        Log.i("Result address received", String.valueOf(resultCode));

        // Display the address string or an error message sent from the intent service.
        mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
        LocationData data= LocationData.getInstance();
        Log.v("LOC home",homeAddress);
        Log.v("LOC work", workAddress);
        Log.v("LOC curr",mAddressOutput);

        if(!mAddressOutput.equals("") && homeCache.containsKey(mAddressOutput)){
            //Zero is home
            //One in work
            //2 ins NA
//            Toast.makeText(c, "here 2", Toast.LENGTH_SHORT).show();
        Log.v("CHECK","1");
            data.setStatus(0);
        }else if(!mAddressOutput.equals("") && workCache.containsKey(mAddressOutput)){
             data.setStatus(1);
            Log.v("CHECK", "2");
        }else {
            data.setStatus(2);
            Log.v("CHECK", "3");
        }

        Log.i("Mad Output YAYAYA",mAddressOutput);
        // displayAddressOutput();

        // Show a toast message if an address was found.
        if (resultCode == Constants.SUCCESS_RESULT) {
            Log.i("Address Found HURRAY", mAddressOutput);
        }

        // Reset. Enable the Fetch Address button and stop showing the progress bar.
        //mAddressRequested = false;
    }

//           protected void displayAddressOutput() {
//               mLocationAddressTextView.setText(mAddressOutput);
//           }
private void openAlert(final String address, final String name, Context c) {

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
    // set title
    alertDialogBuilder.setTitle("GPS Address");

    // set dialog message
    alertDialogBuilder
            .setMessage("Is "+address+" your "+name+" location?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // if this button is clicked, close
                    if (name.equals("home")) {
                        homeAddress = address;
                        homeCache.put(address, 10);
                    } else {
                        workAddress = address;
                        workCache.put(address, 8);
                    }
                    dialog.dismiss();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });

    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

    // show it
    alertDialog.show();
}

    public void addressStore(Context c){
        Log.i("New Vrsn ADDRESS 1",mAddressOutput);
        //Added by: Abhishek Shukla (25 JULY)
        //code for capturing every hour address
        //it can enter this loop only thrice in every hour
        //convert current time into date and extract 24 hours
        if(System.currentTimeMillis()-t1>1200000){

                   Date date = new Date(t1);
         DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
            String dateFormatted = formatter.format(date);
            int hour = date.getHours();
            //Logoc to find home and work location
            //home is defined as place where device live at 0,1,2,3,4,5 hours of day
            // so we take sample of three days , and place with minimum 12 out of 18 is taken as home
            // similarly for office we do it, for 11,12,13,14,15,16
            if(hour<=5 && hour>=0)
            {
                int homesize=0;
                for(int i:home.values()){
                    homesize+=i;
                }
                if(homesize>36)
                    home.clear();
                if(home.containsKey(mAddressOutput)){
                    int temp= home.get(mAddressOutput);
                    home.put(mAddressOutput,temp+1);
                }else{
                    home.put(mAddressOutput,1);
                }
                //Toast.makeText(c,homeAddress,Toast.LENGTH_LONG).show();
                Log.i("HOME LEARNED", homeAddress);
                Log.i("home map size",Integer.toString(homesize));
                for(String s:home.keySet()){
                    Log.i("Current S",s);
                    int freq=home.get(s);
                    if(freq>15){
                        //set home addresss as S
                        //update home address  only when it is different from previous location
                        if(!homeAddress.equals(s))
                        openAlert(s,"home",c);
//                        homeAddress=s;
//                        homeCache.put(s,10);
                    }
                }
            }
            //office location learning
            if(hour>=16 && hour<=11)
            {
                int worksize=0;
                for(int i:work.values()){
                    worksize+=i;
                }
                if(worksize>36)
                    work.clear();
                if(work.containsKey(mAddressOutput)){
                    int temp= work.get(mAddressOutput);
                    work.put(mAddressOutput,temp+1);
                }else{
                    work.put(mAddressOutput,1);
                }
               // Toast.makeText(c,workAddress,Toast.LENGTH_LONG).show();
                Log.i("WORK LEARNED", workAddress);

                Log.i("Work map size",Integer.toString(worksize));
                for(String s:work.keySet()){
                    int freq=work.get(s);
                    if(freq>15){
                        //set home addresss as S
                        if(!workAddress.equals(s))
                        openAlert(s,"work",c);
//                        workAddress=s;
//                        workCache.put(s,8);
                    }
                }
            }
            //update t1 with current time
            t1 = System.currentTimeMillis();
        }//if ends here
    }
}



