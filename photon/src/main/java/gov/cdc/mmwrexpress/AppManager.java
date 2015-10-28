package gov.cdc.mmwrexpress;

import android.app.Application;
import android.content.SharedPreferences;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by jason on 10/16/15.
 */
public class AppManager extends Application{
    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;
    public static SiteCatalystController sc;

    @Override
    public void onCreate() {
        super.onCreate();

        //set global instance of Shared Prefs and instantiate global editor
        pref = getApplicationContext().getSharedPreferences(MmwrPreferences.PREFS_NAME, 0);
        editor =  pref.edit();

        //Setup default Realm instance. All classes should call Realm.getDefaultInstance() when using Realm objects.
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        //Create SiteCatalystController instance and log App Launch event.
        sc = new SiteCatalystController();
        sc.trackAppLaunchEvent();
    }
}

