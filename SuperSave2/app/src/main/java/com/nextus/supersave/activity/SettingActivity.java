package com.nextus.supersave.activity;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.nextus.supersave.R;

/**
 * Created by chosw on 2016-07-01.
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ListPreference listPreference = (ListPreference) findPreference("list_preference");
        ListPreference listPreference02 = (ListPreference) findPreference("list_preference_02");
        EditTextPreference editTextPreference = (EditTextPreference) findPreference("edit_preference");

        listPreference.setOnPreferenceChangeListener(this);
        listPreference02.setOnPreferenceChangeListener(this);
        editTextPreference.setOnPreferenceChangeListener(this);

        if(listPreference.getValue()!= null)
            listPreference.setSummary(listPreference.getValue());
        if(listPreference02.getValue()!= null)
            listPreference02.setSummary(listPreference02.getValue());
        if(editTextPreference.getText()!= null)
            editTextPreference.setSummary(editTextPreference.getText());

        /*
        listPreference.setSummary(listPreference.getValue());
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return true;
            }
        });

        listPreference02.setSummary(listPreference02.getValue());
        listPreference02.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String)newValue);
                return true;
            }
        });
        */
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch(preference.getKey()) {
            case "setting_1_1":
                Toast.makeText(this,"box1 checked" , Toast.LENGTH_SHORT).show();
                break;
            case "list_preference":
                ListPreference listPreference = (ListPreference) findPreference("list_preference");
                listPreference.setSummary(listPreference.getValue());
                listPreference.notify();
                break;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        preference.setSummary((String)newValue);
        return true;
    }
}
