package com.dannygrove.mtls;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.security.KeyChainException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.openintents.openpgp.IOpenPgpService2;
import org.openintents.openpgp.OpenPgpError;
import org.openintents.openpgp.util.OpenPgpApi;
import org.openintents.openpgp.util.OpenPgpServiceConnection;
import org.spongycastle.pkcs.PKCS10CertificationRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;


public class ServerDetailActivity extends AppCompatActivity implements KeyChainAliasCallback {
    private static final String TAG = "ServerDetailActivity";
    private static final int REQUEST_CODE_DETACHED_SIGN = 1;
    private static final String RSA_KEY_ALIAS = "mtls_user_key";
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
    private SharedPreferences settings;
    private FloatingActionButton fab;
    private BottomAppBar appBar;
    private OpenPgpServiceConnection mOpenPgpServiceConnection;
    private Long mSigningKeyId;
    private String csrPublicByteString;
    private String openPgpDetachedSignature;
    private PrivateKey privateKey = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult resultCode: " + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_DETACHED_SIGN: {
                    detachSign(data);
                    break;
                }
            }
        }
    }

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
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        mSigningKeyId = settings.getLong("openpgp_key_id", 0);
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

    private void getOrCreateKey() {
        KeyChain.choosePrivateKeyAlias(this, this, new String[]{KeyProperties.KEY_ALGORITHM_RSA}, null, null, -1, server.name );
    }

    /*
    private KeyPair getKey() {
        try {
            ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(RSA_KEY_ALIAS, null);
            if (entry == null) {
                // Create a key if one doesn't exist
                throw new UnrecoverableEntryException();
            }
            PrivateKey pvtKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            Certificate cert = ((KeyStore.PrivateKeyEntry) entry).getCertificate();
            PublicKey pubKey = cert.getPublicKey();
            return new KeyPair(pubKey, pvtKey);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException e)
        {
            try {
                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
                kpg.initialize(new KeyGenParameterSpec.Builder(
                        RSA_KEY_ALIAS,
                        KeyProperties.PURPOSE_SIGN)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setDigests(KeyProperties.DIGEST_SHA256,
                                KeyProperties.DIGEST_SHA512)
                        .setRandomizedEncryptionRequired(false)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                        .build());
                return kpg.generateKeyPair();
            } catch (NoSuchAlgorithmException|NoSuchProviderException|InvalidAlgorithmParameterException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    */

    void detachSign(Intent data) {
        data.setAction(OpenPgpApi.ACTION_DETACHED_SIGN);
        data.putExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, mSigningKeyId);
        OpenPgpApi api = new OpenPgpApi(this, mOpenPgpServiceConnection.getService());
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(csrPublicByteString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        api.executeApiAsync(data, is, os, new GPGCallback(REQUEST_CODE_DETACHED_SIGN, os));
    }



    void importCertificateIntoAndroid(String certStr) throws CertificateException, KeyStoreException {
        try {
            KeyStore pk12KeyStore = KeyStore.getInstance("PKCS12");
            pk12KeyStore.load(null, "mtls-android".toCharArray());
            ByteArrayInputStream is = new ByteArrayInputStream(certStr.getBytes());
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(is);
            pk12KeyStore.setKeyEntry(server.name, privateKey, null, new Certificate[]{cert});
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            pk12KeyStore.store(os, null);
            Intent certInstallIntent = KeyChain.createInstallIntent();
            Log.d(TAG, String.valueOf(os));
            certInstallIntent.putExtra(KeyChain.EXTRA_PKCS12, String.valueOf(os));
            certInstallIntent.putExtra(KeyChain.EXTRA_KEY_ALIAS, server.name);
            certInstallIntent.putExtra(KeyChain.EXTRA_NAME,  server.name);
            startActivity(certInstallIntent);
        } catch (Exception e) {
            Log.d(TAG, "help");
        }
    }

    private void SendRequestToServer() {
        String url = server.url;
        Map<String, String> params = new HashMap<String, String>();
        String lifetime;
        if (server.lifetime == null) {
            lifetime = String.valueOf(64800);
        } else {
            lifetime = server.lifetime;
        }
        params.put("csr", csrPublicByteString);
        params.put("signature", openPgpDetachedSignature);
        params.put("lifetime", lifetime);
        params.put("type", "CERTIFICATE");
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String cert = (String) response.get("cert");
                    if (cert == null) {
                        Toast.makeText(ServerDetailActivity.this,
                                response.toString(),
                                Toast.LENGTH_LONG).show();;
                    } else {
                        try {
                            importCertificateIntoAndroid(cert);
                        } catch (CertificateException|KeyStoreException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                try {
                    String responseBody = new String(error.networkResponse.data, "UTF-8");
                    JSONObject data = new JSONObject(responseBody);
                    Toast.makeText(ServerDetailActivity.this,
                            data.get("msg").toString(),
                            Toast.LENGTH_LONG).show();
                } catch (JSONException | UnsupportedEncodingException e) {
                    Toast.makeText(ServerDetailActivity.this,
                        "Network error, please check with your admin.",
                        Toast.LENGTH_LONG).show();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    private void createClientCertificate(View view) {
        Log.d(TAG, "createClientCertificate: Creating client certificate for server - " + server.id + " - " + server.name);
        getOrCreateKey();
    }

    public void generateCSRAndSign(KeyPair keyPair) {
        String subjectString = String.join(", ", new String[] {
                "CN=mtls-android",
                "O=" + server.organization_name ,
                "E=" + settings.getString("email", ""),
        });
        PKCS10CertificationRequest csr = CsrHelper.generateCRS(keyPair, subjectString);
        csrPublicByteString = CsrHelper.getCRSPublicBytesInPEM(csr);
        detachSign(new Intent());
    }

    private KeyPair getKeyPairForPrivateKey(PrivateKey privateKey) {
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec priv = kf.getKeySpec(privateKey, RSAPrivateKeySpec.class);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(priv.getModulus(), BigInteger.valueOf(65537));
            PublicKey pubKey = kf.generatePublic(keySpec);
            return new KeyPair(pubKey, privateKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Toast.makeText(this, "Could not get key information.", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private KeyPair createKeyPair() {
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(4096);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
           Toast.makeText(this, "Cannot create RSA Key Pair.", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    public void alias(@Nullable String alias) {
        KeyPair keyPair = null;
        if (alias != null) {
            PrivateKey privateKey = null;
            try {
                privateKey = KeyChain.getPrivateKey(this, "mtls");
            } catch (InterruptedException | KeyChainException e) {
                Toast.makeText(this, "Could not retrieve private key.", Toast.LENGTH_SHORT).show();
            }
            keyPair = getKeyPairForPrivateKey(privateKey);
        } else {
            keyPair = createKeyPair();
        }
        privateKey = keyPair.getPrivate();
        generateCSRAndSign(keyPair);
    }

    private class GPGCallback implements OpenPgpApi.IOpenPgpCallback {
        int requestCode;
        ByteArrayOutputStream os;

        private GPGCallback(int requestCode, ByteArrayOutputStream os) {
            this.requestCode = requestCode;
            this.os = os;
        }

        @Override
        public void onReturn(Intent result) {
            switch(result.getIntExtra(OpenPgpApi.RESULT_CODE, OpenPgpApi.RESULT_CODE_ERROR)) {
                case OpenPgpApi.RESULT_CODE_SUCCESS: {
                    // encrypt/decrypt/sign/verify
                    if (os != null) {
                        try {
                            Log.d(OpenPgpApi.TAG, "result: " + os.toByteArray().length
                                    + " str=" + os.toString("UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            Log.e(TAG, "UnsupportedEncodingException", e);
                        }
                    }
                    if (requestCode == REQUEST_CODE_DETACHED_SIGN) {
                        byte[] detachedSig
                                = result.getByteArrayExtra(OpenPgpApi.RESULT_DETACHED_SIGNATURE);
                        Log.d(OpenPgpApi.TAG, "RESULT_DETACHED_SIGNATURE: " + detachedSig.length
                                + " str=" + new String(detachedSig));
                        openPgpDetachedSignature = new String(detachedSig);
                        SendRequestToServer();
                    }
                    break;
                }
                case OpenPgpApi.RESULT_CODE_USER_INTERACTION_REQUIRED: {

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
                    OpenPgpError error = result.getParcelableExtra(OpenPgpApi.RESULT_ERROR);
                    handleError(error);
                    break;
                }
            }
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
