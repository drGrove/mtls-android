package com.dannygrove.mtls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawableWrapper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.database.Observable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class ServerDetailActivity extends AppCompatActivity {
    private static final String TAG = "ServerDetailActivity";
    private Server server;
    private static final int EDIT_MODE = 1;
    private static final int STANDARD_MODE = 0;
    private int CURRENT_MODE = 0;
    private EditText name;
    private EditText serverUrl;
    private EditText organizationName;
    private EditText issuer;
    private ServerViewModel mServerViewModel;
    private Long serverId;
    private FloatingActionButton fab;
    private BottomAppBar appBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_detail);
        mServerViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        name = ((TextInputLayout) findViewById(R.id.name)).getEditText();
        serverUrl = ((TextInputLayout) findViewById(R.id.url)).getEditText();
        organizationName = ((TextInputLayout) findViewById(R.id.organization_name)).getEditText();
        issuer = ((TextInputLayout) findViewById(R.id.issuer)).getEditText();
        fab = findViewById(R.id.fab);
        appBar = findViewById(R.id.bar);
        setInputFocus(false);
        getIncomingIntent();
    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        if (getIntent().hasExtra("id")) {
            Long id = getIntent().getLongExtra("id", 0);
            server = mServerViewModel.getServer(id);
            updateForm(server);
        } else {
            Log.d(TAG, "getIncomingIntent: Missing id intent extra, closing intent");
            finish();
        }
    }

    private void setInputFocus(Boolean setFocus) {
        name.clearFocus();
        name.setFocusableInTouchMode(setFocus);
        serverUrl.setFocusableInTouchMode(setFocus);
        serverUrl.clearFocus();
        organizationName.setFocusableInTouchMode(setFocus);
        organizationName.clearFocus();
    }

    private void updateForm(Server server) {
        name.setText(server.name);
        serverUrl.setText(server.url);
        organizationName.setText(server.organization_name);
        issuer.setText(server.issuer);
    }

    public void toggleEditMode(View view) {
        if (CURRENT_MODE == EDIT_MODE) {
            CURRENT_MODE = STANDARD_MODE;
            setInputFocus(false);
            setFabIcon(R.drawable.ic_sync_24dp);
            setAppBarIcon(R.drawable.ic_edit_24dp);
            updateForm(server);
        } else {
            CURRENT_MODE = EDIT_MODE;
            setInputFocus(true);
            setFabIcon(R.drawable.ic_save_24dp);
            setAppBarIcon(R.drawable.ic_cancel_24dp);
        }
    }

    public void setFabIcon(int icon) {
        fab.setImageResource(icon);
    }

    public void setAppBarIcon(int icon) {
        appBar.setNavigationIcon(icon);
    }

    public void ctaClick(View view) {
        if (CURRENT_MODE == EDIT_MODE) {
            updateServer(view);
        } else {
            createClientCertificate(view);
        }
    }

    private void updateServer(View view) {
        Log.d(TAG, "updateServer: Updating details for server - " + server.id + " - " + server.name);
        server.name = name.getText().toString();
        server.url = serverUrl.getText().toString();
        server.organization_name = organizationName.getText().toString();
        mServerViewModel.update(server);
        toggleEditMode(view);
    }

    private void createClientCertificate(View view) {
        Log.d(TAG, "createClientCertificate: Creating client certificate for server - " + server.id + " - " + server.name);
        //KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
    }
}
