package gov.cdc.mmwrexpress;

/**DeviceRegistration.java
 * photon-droid
 *
 * Created by greg on 7/9/15.
 * Copyright (c) 2015 Informatics Research and Development Lab. All rights reserved.
 */

public class DeviceRegistration {

    private String registration_id;
    private String device_id = "";
    private Boolean active;

    public DeviceRegistration(String token, String deviceId) {
        this.registration_id = token;
        this.device_id = deviceId;
        this.active = true;
    }
}
