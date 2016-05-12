package com.zjl.checkticket.setting;


import com.alibaba.fastjson.JSON;
import com.zjl.checkticket.R;
import com.zjl.checkticket.TicketDataManager;
import com.zjl.checkticket.model.Park;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                if (getActivity() != null) {
                    getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                    getActivity().finish();
                }
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment implements Observer {
        private static final String TAG = "DataSyncPreference";
        public static final String PARKS_PREFERENCE_NAME = "park_list";

        static class ParksPreferenceModel {
            private CharSequence[] entries;
            private CharSequence[] values;

            public ParksPreferenceModel() {
            }

            public ParksPreferenceModel(CharSequence[] entries, CharSequence[] values) {
                this.values = values;
                this.entries = entries;
            }

            public CharSequence[] getEntries() {
                return entries;
            }

            public void setEntries(CharSequence[] entries) {
                this.entries = entries;
            }

            public CharSequence[] getValues() {
                return values;
            }

            public void setValues(CharSequence[] values) {
                this.values = values;
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            TicketDataManager.getInstance().addObserver(this);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));

            ListPreference parkPref = (ListPreference) findPreference("selected_park");

            if (parkPref.getEntries() == null) {
                Log.i(TAG, "onCreate: park preference is null");

//                ParksPreferenceModel model = readParksFromSharedPreference();
//                if (model != null) {
//                    parkPref.setEntries(model.getEntries());
//                    parkPref.setEntryValues(model.getValues());
//                }

                if (parkPref.getEntries() == null) {
                    parkPref.setEnabled(false);
                    TicketDataManager.getInstance().fetchParks();
                    Log.i(TAG, "onCreate: fetch parks from server");
                } else {
                    parkPref.setEnabled(true);
                    bindPreferenceSummaryToValue(parkPref);
                }
            }

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            TicketDataManager.getInstance().deleteObserver(this);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                if (getActivity() != null) {
                    getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                    getActivity().finish();
                }
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void update(Observable observable, Object data) {
            if (observable == TicketDataManager.getInstance()) {
                TicketDataManager.MessageBundle message = (TicketDataManager.MessageBundle) data;
                TicketDataManager.MessageType type = message.getType();
                Log.d(TAG, "update: " + type);

                if (type.equals(TicketDataManager.MessageType.PARKS_DATA_CHANGED)) {
                    ArrayList<Park> parks = (ArrayList<Park>) message.getEntity();

                    if (parks != null && !parks.isEmpty()) {
                        final ListPreference parkPref = (ListPreference) findPreference("selected_park");
                        final CharSequence[] entries = new CharSequence[parks.size()];
                        final CharSequence[] values = new CharSequence[parks.size()];
                        synchronized (TicketDataManager.SYNCHRONIZE_LOCK_PARKS) {
                            int idx = 0;
                            for (Park park : parks) {
                                entries[idx] = park.getName();
                                values[idx] = park.getId();
                                idx++;
                            }
                        }

                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    parkPref.setEntries(entries);
                                    parkPref.setEntryValues(values);

                                    parkPref.setEnabled(true);
                                    bindPreferenceSummaryToValue(parkPref);
                                }
                            });
                        }

//                        writeParksToSharedPreference(new ParksPreferenceModel(entries, values));
                    }

                }
            }
        }

        private void writeParksToSharedPreference(ParksPreferenceModel model) {
            String parksJson = JSON.toJSONString(model);
            getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString(PARKS_PREFERENCE_NAME, parksJson).commit();
        }

        private ParksPreferenceModel readParksFromSharedPreference() {
            ParksPreferenceModel model = null;
            String parksJson = getActivity().getPreferences(Context.MODE_PRIVATE).getString(PARKS_PREFERENCE_NAME, null);

            if (parksJson != null) {
                Log.d(TAG, "readParksFromSharedPreference: parksJson = " + parksJson);
                try {
                    model = JSON.parseObject(parksJson, ParksPreferenceModel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return model;
        }
    }
}
