package com.dannygrove.mtls;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        saveFab = findViewById(R.id.fab);
        emailTextInputLayout = findViewById(R.id.email);
        organizationNameTextInputLayout = findViewById(R.id.organization_name);
        stateTextInputLayout = findViewById(R.id.state);
        countryTextInputLayout = findViewById(R.id.country);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void close(View view) {
        finish();
    }
}
