package org.twinone.locker.lock;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Skandy on 7/24/2015.
 */
public class RetrievePackages {
    public RetrievePackages(Context c){this.context=c;}

    Context context;
    public List<String> getPackages(){
        List<String> installed_packages=new ArrayList<>();

        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            if(pm.getLaunchIntentForPackage(packageInfo.packageName)!=null) {
                installed_packages.add(packageInfo.packageName);
                Log.d("--------------name", "Installed package :" + packageInfo.packageName);
                Log.d("--------------direc", "Source dir : " + packageInfo.sourceDir);
            }
        }
        return installed_packages;
    }
}
