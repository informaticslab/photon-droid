package gov.cdc.mmwrexpress;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.pushwoosh.Pushwoosh;
import com.pushwoosh.notification.PushwooshNotificationSettings;

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
    public static Pushwoosh pushManager;
    public static IssuesManager issuesManager;

    @Override
    public void onCreate() {
        super.onCreate();

        //set global instance of Shared Prefs and instantiate global editor
        pref = getApplicationContext().getSharedPreferences(MmwrPreferences.PREFS_NAME, 0);
        editor =  pref.edit();

        editor.putString(MmwrPreferences.APP_VERSION, getApplicationVersionName()).commit();

        if(!pref.getBoolean(MmwrPreferences.SET_INITIAL_SETTINGS, false)){
            setDefaultPrefs();
            editor.putBoolean(MmwrPreferences.SET_INITIAL_SETTINGS, true).commit();
        }

        //Setup default Realm instance. All classes should call Realm.getDefaultInstance() when using Realm objects.
        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);

        //Create SiteCatalystController instance and log App Launch event.
        sc = new SiteCatalystController();
        sc.trackAppLaunchEvent();

        pushManager = Pushwoosh.getInstance();

        //Register for Pushwoosh
        if (pref.getBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS, true)) {
            //Register for push!
            pushManager.registerForPushNotifications();
            PushwooshNotificationSettings.setMultiNotificationMode(true);
            PushwooshNotificationSettings.setNotificationChannelName("MMWR DEV");
        }

        issuesManager = new IssuesManager();
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

