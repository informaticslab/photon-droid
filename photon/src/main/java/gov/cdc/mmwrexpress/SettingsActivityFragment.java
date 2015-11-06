package gov.cdc.mmwrexpress;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends Fragment {
    private View view;
    public SettingsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_settings, container, false);
            CheckBox pushNotifications = (CheckBox) view.findViewById(R.id.allowNotificationsCheckBox);
            if(AppManager.pref.getBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS,true))
                pushNotifications.setChecked(true);
            else
            pushNotifications.setChecked(false);

            pushNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (!AppManager.pref.getBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS, true)) {
                            AppManager.editor.putBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS, true).commit();
                            AppManager.pushManager.registerForPushNotifications();
                        }
                    }
                    else {
                        if (AppManager.pref.getBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS, true)) {
                            AppManager.editor.putBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS, false).commit();
                            AppManager.pushManager.unregisterForPushNotifications();
                        }
                    }
                }
            });
        }
        return view;
    }
}
