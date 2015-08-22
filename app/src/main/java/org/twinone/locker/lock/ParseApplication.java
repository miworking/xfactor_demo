package org.twinone.locker.lock;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
//import com.parse.ParseCrashReporting;
import com.parse.ParseUser;

public class ParseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize Crash Reporting.
   // ParseCrashReporting.enable(this);

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(this, "PF7qPTccIhZJ3uxnmk6HQt7949Vc8qslzjPmU7D7", "UJgKEowj0YPsHAm7sK2GQ51LTiho05kboAuHQOvz");


    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    // defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);
  }
}
