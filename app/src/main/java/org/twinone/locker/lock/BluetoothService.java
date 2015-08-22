package org.twinone.locker.lock;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

public class BluetoothService extends Service {
    private final IBinder mBinder = new BluetoothBinder();

    private BluetoothAdapter bluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private HashMap<String, Boolean> knownBtDevicesMap = new HashMap<String, Boolean>();
    public String pairedDeviceString;
//    Activity activity;
//
//    BluetoothService(Activity activity) {
//        this.activity = activity;
//    }

    // Interface to external class
    // Have trusted devices paired or not when user opens an App
    public boolean hasTrustedDevices () {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If device does not support bluetooth
        if (bluetoothAdapter == null) return false;
        // Device support bluetooth
        else {
            // Find paired devices
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0 && knownBtDevicesMap.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if(isKnownDevices(device.getAddress()) && isTrustedDevices(device.getAddress())){
                        //getSignalStrength();
                        return true;
                    }
                }
            return false;
            }// If no paired devices.
            else return false;
        }
    }

//    public void getSignalStrength() {
//        if (!hasTrustedDevices()) {
//        } else {
//            registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
//        }
//    }

    private final BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
              //  Toast.makeText(getApplicationContext(),"  RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // Keep updating Bluetooth paired condition
    private void update(){


        //Toast.makeText(this, "function called....", Toast.LENGTH_SHORT).show();
        function();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
        String strDate = sdf.format(c.getTime());
    }

    private Handler handler = new Handler( );

    private Runnable runnable = new Runnable( ) {
        public void run ( ) {
            update();
            handler.postDelayed(this,1000*10);
        }
    };

    @Override
    public void onCreate() {
        handler.postDelayed(runnable, 1000);
    }

    private void function() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            //Toast.makeText(this, "No Bluetooth",
              //      Toast.LENGTH_LONG).show();
        }
        else if(!bluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            //this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            Toast.makeText(this, "Enable your BluetoothActivity.",Toast.LENGTH_LONG).show();
//            if (pairedWithBluetooth()) {
//                Toast.makeText(this, "Paired. 1",
//                        Toast.LENGTH_LONG).show();
//                if(hasTrustedDevices()) {
//                    Toast.makeText(this, "Paired with trusted device.",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(this, "Do not paired with trusted device.",
//                            Toast.LENGTH_LONG).show();
//                }
//            } else {
//                Toast.makeText(this, "Not paired. 1",
//                        Toast.LENGTH_LONG).show();
//                if(hasTrustedDevices()) {
//                    Toast.makeText(this, "Paired with trusted device. 1",
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(this, "Do not paired with trusted device. 1",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
          /*  Toast.makeText(this, "Bluetooth is disabled.",
                    Toast.LENGTH_LONG).show();
        */} else {
           /* Toast.makeText(this, "Bluetooth is already on.",
                    Toast.LENGTH_LONG).show();
*/
            if(pairedWithBluetooth()) {

                if (hasTrustedDevices()) {
                 /*   Toast.makeText(this, "Paired with trusted device.",
                            Toast.LENGTH_LONG).show();
              */  } else {
                   /* Toast.makeText(this, "Do not paired with trusted device.",
                            Toast.LENGTH_LONG).show();
                */}
            } else {
               /* Toast.makeText(this, "Unpaired.",
                        Toast.LENGTH_LONG).show();
           */ }

        }
    }

    public class BluetoothBinder extends Binder {
       BluetoothService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
      //  Toast.makeText(this, "Bluetooth Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  Toast.makeText(this, "Bluetooth Service Destroyed", Toast.LENGTH_LONG).show();
    }

    // If paired with Bluetooth, check if this is a known device. If not, pop up alert dialog.
    private boolean pairedWithBluetooth (){
        // Find paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            ArrayList<String> mArrayPairedAdapter = new ArrayList<String>();
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayPairedAdapter.add(device.getName() + "\n" + device.getAddress());

                for (String s : mArrayPairedAdapter) {
                    pairedDeviceString += s + "\t";
                }

                if (!isKnownDevices(device.getAddress())) {
                    openAlert(device.getAddress(), device.getName());
//                    Toast.makeText(this,device.getAddress() + " is a " + String.valueOf(isTrustedDevices(device.getAddress())), Toast.LENGTH_LONG).show();
                }
            }
            return true;
        }
        return false;

    }

    private boolean isKnownDevices(String address) {
        if (knownBtDevicesMap == null || knownBtDevicesMap.isEmpty()) {
//            Toast.makeText(this,"Map is empty" + knownBtDevicesMap.size(), Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            boolean known = knownBtDevicesMap.containsKey(address);
            Log.d(address + " is known device? ", String.valueOf(known)+"!!!!!!!!!!!!!!!!!!!!!!!");
            return known;
        }
    }

    private boolean isTrustedDevices(String address) {
        boolean trust = (boolean)knownBtDevicesMap.get(address);
        Log.d(address +" is trusted device? ",String.valueOf(trust)+"!!!!!!!!!!!!!!!!!!!!!!!");
        return trust;
    }

    private void openAlert(final String address, final String name) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Bluetooth");

        // set dialog message
        alertDialogBuilder
                .setMessage("Is "+name+" a trusted device?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close

                        knownBtDevicesMap.put(address, true);
//                        Toast.makeText(this, name + "is a trusted Device.",
//                                Toast.LENGTH_LONG).show();
                        // current activity
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        knownBtDevicesMap.put(address, false);
//                        Toast.makeText(c, name + "is a untrusted Device.",
//                                Toast.LENGTH_LONG).show();
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