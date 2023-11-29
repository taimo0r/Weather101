package com.taimoor.weather101.Activities;

import android.os.Bundle;
import android.preference.RingtonePreference;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.taimoor.weather101.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
//            bindSummaryToValue(findPreference("key_full_name"));
//            bindSummaryToValue(findPreference("key_email"));
//            bindSummaryToValue(findPreference("key_gender"));
//            bindSummaryToValue(findPreference("key_phone_number"));
        }
    }

    private static void bindSummaryToValue(Preference preference){
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),""));
    }


    private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            String value = newValue.toString();
            if (preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(value);
                //Set summary to reflect new value
                preference.setSummary(index>0 ? listPreference.getEntries()[index] : null);
            }else if (preference instanceof EditTextPreference){
                preference.setSummary(value);
            }
            return false;
        }
    };

//    private static androidx.preference.Preference.OnPreferenceChangeListener listener = new androidx.preference.Preference().OnPreferenceChangeListener() {
//        @Override
//        public boolean onPreferenceChange(Preference preference, Object newValue) {
//           String value = newValue.toString();
//           if (preference instanceof androidx.preference.ListPreference){
//               ListPreference listPreference = (ListPreference) preference;
//               int index = listPreference.findIndexOfValue(value);
//
//               //Set the summary to reflect new value
//               preference.setSummary(index>0 ? listPreference.getEntries()[index]: null);
//           }else if (preference instanceof EditTextPreference){
//               preference.setSummary(value);
//           }else if (preference instanceof RingtonePreference){
//
//           }
//           return false;
//        }
//    };
}