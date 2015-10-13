package gov.cdc.mmwrexpress;

import android.util.Log;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

import android.app.Activity;
import android.provider.Settings.Secure;



/**
 * Created by greg on 7/9/15.
 */
public class NotificationRegistrationService {

    public static final String BASE_URL = "http://mmwr.gsledbetter.webfactional.com";
    public static final String TEST_URL = "http://192.168.1.38:8000";

    private NotificationRegistrationInterface service;
    private static final String TAG = "NotificationReg";

    public NotificationRegistrationService() {

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BASE_URL).setLogLevel(RestAdapter.LogLevel.FULL).build();
        service = restAdapter.create(NotificationRegistrationInterface.class);

    }

    public void register(String token, String android_id, Boolean active) {

        DeviceRegistration deviceReg = service.register(token, android_id, active);
//        DeviceRegistration deviceReg = service.register(token);

//        service.register (deviceReg, new Callback<DeviceRegistration>() {
//            @Override
//            public void success(DeviceRegistration user, Response response) {
//                // Access user here after response is parsed
//                Log.d(TAG, "Service success!");
//
//
//            }
//
//            @Override
//            public void failure(RetrofitError retrofitError) {
//                // Log error here since request failed
//                Log.d(TAG, "Service failure!");
//            }
//        });


    }

}

