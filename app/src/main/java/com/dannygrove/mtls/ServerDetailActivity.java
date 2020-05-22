package com.dannygrove.mtls;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.MasterKeys;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.openintents.openpgp.IOpenPgpService2;
import org.openintents.openpgp.OpenPgpError;
import org.openintents.openpgp.util.OpenPgpApi;
import org.openintents.openpgp.util.OpenPgpKeyPreference;
import org.openintents.openpgp.util.OpenPgpServiceConnection;
import org.spongycastle.asn1.smime.SMIMEEncryptionKeyPreferenceAttribute;
import org.spongycastle.pkcs.PKCS10CertificationRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

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
    private UserSettingsViewModel mUserSettingsViewModel;
    private UserSettings userSettings;
    private FloatingActionButton fab;
    private BottomAppBar appBar;
    private OpenPgpServiceConnection mOpenPgpServiceConnection;
    private long mSigningKeyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_detail);
        mServerViewModel = new ViewModelProvider(this).get(ServerViewModel.class);
        mUserSettingsViewModel = new ViewModelProvider(this).get(UserSettingsViewModel.class);
        name = ((TextInputLayout) findViewById(R.id.name)).getEditText();
        serverUrl = ((TextInputLayout) findViewById(R.id.url)).getEditText();
        organizationName = ((TextInputLayout) findViewById(R.id.organization_name)).getEditText();
        issuer = ((TextInputLayout) findViewById(R.id.issuer)).getEditText();
        fab = findViewById(R.id.fab);
        appBar = findViewById(R.id.bar);
        setInputFocus(false);
        getIncomingIntent();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        mSigningKeyId = settings.getLong("openpgp_key", 0);
        mKey = (OpenPgpKeyPreference) findPreference("openpgp_key");
        mOpenPgpServiceConnection = new OpenPgpServiceConnection(
                getApplicationContext(),
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
    }

    private void getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents");
        if (getIntent().hasExtra("id")) {
            Long id = getIntent().getLongExtra("id", 0);
            server = mServerViewModel.getServer(id);
            userSettings = mUserSettingsViewModel.get();
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

    private KeyPair getKey() {
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            kpg.initialize(new KeyGenParameterSpec.Builder(
                    masterKeyAlias,
                    KeyProperties.PURPOSE_SIGN)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setDigests(KeyProperties.DIGEST_SHA256,
                            KeyProperties.DIGEST_SHA512)
                    .setRandomizedEncryptionRequired(false)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .build());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return kpg.generateKeyPair();
    }

    private void getGPGClearSignSignature(String data) {
        // Call out to OpenKeyChain and get a signature back
        Intent gpgIntent = new Intent();
        gpgIntent.setAction(OpenPgpApi.ACTION_DETACHED_SIGN);
        gpgIntent.putExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, mSigningKeyId);
        OpenPgpApi api = new OpenPgpApi(this, mOpenPgpServiceConnection.getService());
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        api.executeApiAsync(gpgIntent, is, null, new GPGCallback(9916));
    }

    void importCertificateIntoAndroid() {

    }

    private void SendRequestToServer(String csrPublicByeString, String clearSignSignature, Server server, UserSettings userSettings) {
        String url = server.url;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("SendRequestToServer", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void  onErrorResponse(VolleyError error) {
                Log.d("SendRequestToServer - Failure", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String lifetime;
                if (server.lifetime == null) {
                    lifetime = String.valueOf(64800);
                } else {
                   lifetime = server.lifetime;
                }
                params.put("csr", csrPublicByeString);
                params.put("signature", clearSignSignature);
                params.put("lifetime", lifetime);
                params.put("type", "CERTIFICATE");
                return params;
            }
        };
    }

    private void createClientCertificate(View view) {
        Log.d(TAG, "createClientCertificate: Creating client certificate for server - " + server.id + " - " + server.name);
        KeyPair keyPair = getKey();
        String subjectString = String.join(", ", new String[] {
                "CN=mtls-android",
                "O=" + server.organization_name ,
                "ST=" + userSettings.state,
                "C=" + userSettings.country,
                "E=" + userSettings.email,
        });
        PKCS10CertificationRequest csr = CsrHelper.generateCRS(keyPair, subjectString);
        String csrPublicByteString = CsrHelper.getCRSPublicBytesInPEM(csr);
        getGPGClearSignSignature(csrPublicByteString);
       //SendRequestToServer(csrPublicByteString, clearSignSignature, server, userSettings);
    }



    private void showToast(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ServerDetailActivity.this,
                        message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GPGCallback implements OpenPgpApi.IOpenPgpCallback {
        int requestCode;

        private GPGCallback(int requestCode) {
            this.requestCode = requestCode;
        }

        @Override
        public void onReturn(Intent result) {
            switch(result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {
                case OpenPgpApi.RESULT_CODE_SUCCESS: {
                    handleSuccess(result, requestCode);
                    break;
                }
                case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {
                    showToast("RESULT_CODE_USER_INTERACTION_REQUIRED");

                    PendingIntent pi = result.getParcelableExtra(OpenPgpApi.RESULT_INTENT);
                    try {
                        ServerDetailActivity.this.startIntentSenderFromChild(
                                ServerDetailActivity.this, pi.getIntentSender(),
                                requestCode, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "SendIntentException", e);
                    }
                    break;
                }
                case OpenPgpApi.RESULT_CODE_ERROR: {
                    showToast("RESULT_CODE_ERROR");

                    OpenPgpError error = result.getParcelableExtra(OpenPgpApi.RESULT_ERROR);
                    handleError(error);
                    break;
                }
            }
        }

        private void handleSuccess(Intent result, int requestCode) {
            Log.d(TAG, "We out here");
        }

        private void handleError(final OpenPgpError error) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(ServerDetailActivity.this,
                            "onError id:" + error.getErrorId() + "\n\n" + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onError getErrorId:" + error.getErrorId());
                    Log.e(TAG, "onError getMessage:" + error.getMessage());
                }
            });
        }
    }
}
