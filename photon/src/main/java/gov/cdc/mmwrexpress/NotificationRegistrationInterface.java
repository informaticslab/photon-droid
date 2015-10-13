package gov.cdc.mmwrexpress;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Headers;

public interface NotificationRegistrationInterface {

    @FormUrlEncoded
    @Headers({"Authorization: Basic YW5kcm9pZC1kZXZpY2U6cHVzaGRyb2lk"})
    @POST("/device/gcm/")
    //DeviceRegistration register(@Field("registration_id") String token);
    DeviceRegistration register(@Field("registration_id") String token, @Field("device_id") String device, @Field("active") Boolean active);

}

