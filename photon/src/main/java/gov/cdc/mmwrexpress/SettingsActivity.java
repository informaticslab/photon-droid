package gov.cdc.mmwrexpress;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupToolbar();
        initNavigationDrawer();
        addSettingsFragment();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationView.setCheckedItem(R.id.nav_settings_fragment);
    }

    private void addSettingsFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        SettingsActivityFragment fragment = new SettingsActivityFragment();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
