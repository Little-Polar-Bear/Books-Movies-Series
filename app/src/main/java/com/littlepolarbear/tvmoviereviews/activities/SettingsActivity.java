package com.littlepolarbear.tvmoviereviews.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.littlepolarbear.tvmoviereviews.InternalAppStorage;
import com.littlepolarbear.tvmoviereviews.R;
import com.littlepolarbear.tvmoviereviews.utils.UIHelper;

/*App preference activity, A class designed so a user can define the app settings and their preferences.*/
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SETTINGS_ACTIVITY";

    // UI class that has convenience methods to help
    // override some methods here if needed.
    private final UIHelper uiHelper = new UIHelper() {
        /*Return the id of the toolbar */
        @Override
        public int getToolbarById() {
            return R.id.material_toolbar;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        // set toolbar
        uiHelper.setToolbarTitle("My Account");
        uiHelper.setToolbarSubtitle("Configure preferences");
        try {
            uiHelper.setToolbarAsSupportActionBar(this, null, true);
        } catch (Exception e) {
            Log.v(TAG, "Error setting up toolbar in SettingsActivity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            String key = preference.getKey();
            if (key.equals("clear_list")) {
                // make sure user knows they are about to clear all from list.
                // allow for an accidental touch of the preference by giving alert dialog.
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SettingsFragment.this.requireContext());
                dialogBuilder.setTitle("Clear My List");
                dialogBuilder.setIcon(R.drawable.ic_baseline_delete_24);
                dialogBuilder.setMessage("Are you sure you want to clear all items in your list?");
                dialogBuilder.setNegativeButton("No", ((dialog, position) -> dialog.dismiss()));
                dialogBuilder.setPositiveButton("Yes", (dialog, position) -> {
                    // clear the users list
                    // construct a storage object and perform request
                    // no callbacks with this method so just provide null.
                    InternalAppStorage storage = new InternalAppStorage(null);
                    // clear all objects from file.
                    storage.doClearStorage(requireContext());
                });
                // show dialog
                dialogBuilder.show();
            } else if (key.equals("send_feedback")) {
                // open an email app for user to communicate through.
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"LittlePolarBearCompany@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Report technical issues or suggest new features for TV & Movie Reviews");

                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
            // return value.
            return super.onPreferenceTreeClick(preference);
        }
    }

    /*Catch the back button in toolbar*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*do a outward transition when back is pressed*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}