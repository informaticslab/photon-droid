package gov.cdc.mmwrexpress;

/**
 * Created by greg on 7/9/15.
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
