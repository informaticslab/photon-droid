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
                            AppManager.sc.trackEvent(Constants.SC_EVENT_ENABLE_PUSH_NOTIFICATIONS,
                                    Constants.SC_PAGE_TITLE_SETTINGS, Constants.SC_SECTION_SETTINGS);
                        }
                    }
                    else {
                        if (AppManager.pref.getBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS, true)) {
                            AppManager.editor.putBoolean(MmwrPreferences.ALLOW_PUSH_NOTIFICATIONS, false).commit();
                            AppManager.pushManager.unregisterForPushNotifications();
                            AppManager.sc.trackEvent(Constants.SC_EVENT_DISABLE_PUSH_NOTIFICATIONS,
                                    Constants.SC_PAGE_TITLE_SETTINGS, Constants.SC_SECTION_SETTINGS);
                        }
                    }
                }
            });
            CheckBox defaultTab = (CheckBox) view.findViewById(R.id.defaultTabCheckbox);
            if(AppManager.pref.getInt(MmwrPreferences.DEFAULT_TAB, Constants.FULL_ARTICLE_TAB) == Constants.FULL_ARTICLE_TAB)
                defaultTab.setChecked(true);
            else
                defaultTab.setChecked(false);

            defaultTab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(AppManager.pref.getInt(MmwrPreferences.DEFAULT_TAB, Constants.FULL_ARTICLE_TAB) == Constants.FULL_ARTICLE_TAB)
                        AppManager.editor.putInt(MmwrPreferences.DEFAULT_TAB, Constants.SUMMARY_TAB).commit();
                    else
                        AppManager.editor.putInt(MmwrPreferences.DEFAULT_TAB, Constants.FULL_ARTICLE_TAB).commit();

                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppManager.sc.trackNavigationEvent(Constants.SC_PAGE_TITLE_SETTINGS, Constants.SC_SECTION_SETTINGS);
    }
}
