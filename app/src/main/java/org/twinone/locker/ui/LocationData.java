package org.twinone.locker.ui;

/**
 * Created by abhishek on 8/1/2015.
 */
public class LocationData {
    int status=2;

    public void setStatus(int status) {
        this.status = status;
    }

    private static LocationData singleton= new LocationData();
    private LocationData(){

    }

    public static LocationData getInstance(){
        return singleton;
    }
    public int getStatus(){ return status;}
}

