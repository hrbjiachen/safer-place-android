package ca.bcit.psychopass;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.setting_fragment, new SettingsFragment());
        ft.commit();

    }
}
