package gov.cdc.mmwrexpress;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.pushwoosh.PushManager;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**AppManager.java
 * photon-droid
 *
 * Created by jason on 10/16/15.
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */
public class AppManager extends Application{
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    public static SiteCatalystController sc;
    public static PushManager pushManager;

    @Override
    public void onCreate() {
        super.onCreate();

        //set global instance of Shared Prefs and instantiate global editor
        pref = getApplicationContext().getSharedPreferences(MmwrPreferences.PREFS_NAME, 0);
        editor =  pref.edit();

        editor.putString(MmwrPreferences.APP_VERSION, getApplicationVersionName());

        if(!pref.getBoolean(MmwrPreferences.SET_INITIAL_SETTINGS, false)){
            setDefaultPrefs();
            editor.putBoolean(MmwrPreferences.SET_INITIAL_SETTINGS, true).commit();
        }

        //Setup default Realm instance. All classes should call Realm.getDefaultInstance() when using Realm objects.
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        //Create SiteCatalystController instance and log App Launch event.
        sc = new SiteCatalystController();
        sc.trackAppLaunchEvent();

        pushManager = PushManager.getInstance(this);

        //Register for Pushwoosh
        if (pref.getBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS, true)) {
                //Register for push!
                pushManager.registerForPushNotifications();
            try {
                pushManager.onStartup(this);
            } catch (Exception e) {
                //push notifications are not available or AndroidManifest.xml is not configured properly
            }

        }
    }
    private void setDefaultPrefs(){
        editor.putBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS, true);
        editor.putBoolean(MmwrPreferences.AGREED_TO_EULA, false);
        editor.putBoolean(MmwrPreferences.PRELOAD_ARTICLES_LOADED, false);
        editor.putBoolean(MmwrPreferences.REFRESHED_ARTICLE_LIST_ON_FIRST_LAUNCH, false);
        editor.commit();
    }

    public String getApplicationVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {} catch(Exception e){}
        return "";
    }
}

