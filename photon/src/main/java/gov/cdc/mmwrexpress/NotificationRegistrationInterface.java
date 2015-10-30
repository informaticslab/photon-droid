package gov.cdc.mmwrexpress;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Headers;

/**NotificationRegistrationInterface.java
 * photon-droid
 *
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public interface NotificationRegistrationInterface {

    @FormUrlEncoded
    @Headers({"Authorization: Basic YW5kcm9pZC1kZXZpY2U6cHVzaGRyb2lk"})
    @POST("/device/gcm/")
    //DeviceRegistration register(@Field("registration_id") String token);
    DeviceRegistration register(@Field("registration_id") String token, @Field("device_id") String device, @Field("active") Boolean active);

}

