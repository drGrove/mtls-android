package com.dannygrove.mtls;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.openintents.openpgp.IOpenPgpService2;
import org.openintents.openpgp.OpenPgpError;
import org.openintents.openpgp.util.OpenPgpApi;
import org.openintents.openpgp.util.OpenPgpServiceConnection;
import org.openintents.openpgp.util.OpenPgpUtils;

import java.util.function.Function;

public class UserSettingsActivity extends AppCompatActivity {
    private UserSettingsFragment mUserSettingsFragment;
    private final String TAG = "UserSettingsActivity";
    private static final int OPENPGP_LIST_KEYS = 42;
    private static final int REQUEST_CODE_GET_KEY_IDS = 9915;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        mUserSettingsFragment = new UserSettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, mUserSettingsFragment)
                .commit();
    }

    public void close(View view) {
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult resultCode: " + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case OPENPGP_LIST_KEYS: {
                    mUserSettingsFragment.getKeyIds(data);
                    break;
                }
            }
        }
    }

    public static class UserSettingsFragment extends PreferenceFragmentCompat {
        private static final String TAG = "UserSettingsFragment";
        private Preference openPgpKey;
        private Activity callingActivity;
        private OpenPgpServiceConnection mOpenPgpServiceConnection;
        private SharedPreferences prefs;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            callingActivity = requireActivity();
            setPreferencesFromResource(R.xml.preferences, rootKey);
            prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            openPgpKey = findPreference("openpgp_key_id");
            String selectedKeyString = prefs.getString("openpgp_key_id_string", null);
            if (selectedKeyString != null) {
                openPgpKey.setSummary(selectedKeyString);
            }
            mOpenPgpServiceConnection = new OpenPgpServiceConnection(
                    callingActivity.getApplicationContext(),
                    "org.sufficientlysecure.keychain",
                    new OpenPgpServiceConnection.OnBound() {
                        @Override
                        public void onBound(IOpenPgpService2 service) {
                            Log.d(OpenPgpApi.TAG, "onBound!");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(OpenPgpApi.TAG, "exception when binding!", e);
                        }
                    }
            );
            mOpenPgpServiceConnection.bindToService();

            openPgpKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getKeyIds(new Intent());
                    return true;
                }
            });
        }

        public void getKeyIds(Intent data) {
            data.setAction(OpenPgpApi.ACTION_GET_SIGN_KEY_ID);
            data.putExtra("OPERATION", "GET_KEY_ID");
            OpenPgpApi api = new OpenPgpApi(getContext(), mOpenPgpServiceConnection.getService());
            api.executeApiAsync(data, null, null, new GPGCallback(REQUEST_CODE_GET_KEY_IDS));
        }


        private class GPGCallback implements OpenPgpApi.IOpenPgpCallback {
            int requestCode;

            private GPGCallback(int requestCode) {
                this.requestCode = requestCode;
            }

            @Override
            public void onReturn(Intent result) {
                switch (result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {
                    case OpenPgpApi.RESULT_CODE_SUCCESS: {
                        switch (requestCode) {
                            case REQUEST_CODE_GET_KEY_IDS: {
                                long keyId = result.getLongExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, 0);
                                String keyIdString = OpenPgpUtils.convertKeyIdToHex(keyId);
                                openPgpKey.setSummary(keyIdString);
                                SharedPreferences.Editor editor = ((SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getActivity())).edit();
                                editor.putLong("openpgp_key_id", keyId);
                                editor.putString("openpgp_key_id_string", keyIdString);
                                editor.apply();
                                break;
                            }
                        }
                        break;
                    }
                    case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {
                        PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
                        try {
                            getActivity().startIntentSenderForResult(pi.getIntentSender(), OPENPGP_LIST_KEYS, null, 0,0,0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.d(TAG, "SendIntentException", e);
                        }
                        break;
                    }
                    case OpenPgpApi.RESULT_CODE_ERROR: {
                        OpenPgpError error = result.getParcelableExtra(OpenPgpApi.RESULT_ERROR);
                        break;
                    }
                }
            }
        }
    }
}