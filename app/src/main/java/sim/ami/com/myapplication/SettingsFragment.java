package sim.ami.com.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private final String PRIVACY_LINK = "http://google.com";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preference preference = findPreference("privacy_policy");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_LINK));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {

                }
                return false;
            }
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Indicate here the XML resource you created above that holds the preferences
        addPreferencesFromResource(R.xml.preferences);
    }
}
