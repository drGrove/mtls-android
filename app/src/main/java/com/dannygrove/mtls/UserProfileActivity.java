package com.dannygrove.mtls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

public class UserProfileActivity extends AppCompatActivity {
    public static final String TAG = "UserProfileActivity";
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout organizationNameTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout countryTextInputLayout;
    private FloatingActionButton saveFab;
    private UserSettings profile;
    private UserSettingsViewModel mUserSettingsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        saveFab = findViewById(R.id.fab);
        emailTextInputLayout = findViewById(R.id.email);
        organizationNameTextInputLayout = findViewById(R.id.organization_name);
        stateTextInputLayout = findViewById(R.id.state);
        countryTextInputLayout = findViewById(R.id.country);
        mUserSettingsViewModel = new ViewModelProvider(this).get(UserSettingsViewModel.class);
        profile = mUserSettingsViewModel.get();
        // Planning for multiple profiles in the future so this is a prep step.
        // This should probably be moved to an onboarding Flow, but that's for a later date.
        if (profile == null) {
            profile = new UserSettings();
            profile.id = new Long(1);
            profile.email = "";
            profile.organizationName = "";
            profile.state = "";
            profile.country = "";
            mUserSettingsViewModel.insert(profile);
        }
        updateForm(profile);
    }

    void updateForm(UserSettings profile) {
        emailTextInputLayout.getEditText().setText(profile.email);
        organizationNameTextInputLayout.getEditText().setText(profile.organizationName);
        stateTextInputLayout.getEditText().setText(profile.state);
        countryTextInputLayout.getEditText().setText(profile.country);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void close(View view) {
        finish();
    }

    public String getValue(TextInputLayout input) {
        return input.getEditText().getText().toString();
    }

    public void saveUserSettings(View view) {
        profile.email = getValue(emailTextInputLayout);
        profile.country = getValue(countryTextInputLayout);
        profile.state = getValue(stateTextInputLayout);
        profile.organizationName = getValue(organizationNameTextInputLayout);
        mUserSettingsViewModel.update(profile);
        finish();
    }
}
