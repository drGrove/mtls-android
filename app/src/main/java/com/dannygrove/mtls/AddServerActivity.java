package com.dannygrove.mtls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.security.KeyChain;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AddServerActivity extends AppCompatActivity {

    public static final String TAG = "AddServerActivity";
    private ServerDBHelper serverDBHelper = ServerDBHelper.getInstance(this);
    private FloatingActionButton saveFab;
    //public Certificate caCertificate;
    public String caCertificate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);
        saveFab = findViewById(R.id.fab);
        saveFab.setEnabled(false);
        EditText urlEditText = ((TextInputLayout) findViewById(R.id.url)).getEditText();
        urlEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)  {
                    // Convert to full URL
                    String url = ((String) ((EditText) v).getText().toString());
                    if (url.isEmpty()) {
                        return;
                    }
                    url =  URLUtil.guessUrl(url);
                    if (!URLUtil.isHttpsUrl(url)) {
                        url = url.replace("http", "https");
                    }
                    ((EditText) v).setText(url);
                    getIssuer(v, url);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        serverDBHelper.close();
        super.onDestroy();
    }

    private void getIssuer(View v, String url) {
        final String CA_URL = url + "ca";
        StringRequest caRequest = new StringRequest(Request.Method.GET, CA_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject caJsonObject = new JSONObject(response);

                    // Update Issuer Field
                    EditText issuerEditText = (EditText) ((TextInputLayout)AddServerActivity.this.findViewById(R.id.issuer)).getEditText();
                    issuerEditText.setText(caJsonObject.getString("issuer"));

                    // Store CA Certificate into memory for save operation.
                    AddServerActivity.this.caCertificate = caJsonObject.getString("cert");
                    saveFab.setEnabled(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(caRequest);
    }

    private void addCACertificate() {
        Intent caInstallIntent = KeyChain.createInstallIntent();
        caInstallIntent.putExtra(KeyChain.EXTRA_CERTIFICATE, caCertificate.getBytes());
        caInstallIntent.putExtra(KeyChain.EXTRA_NAME, ((TextInputLayout) AddServerActivity.this.findViewById(R.id.issuer)).getEditText().getText().toString());
        startActivity(caInstallIntent);
    }

    public void addServer(View view) {
        //getIssuer(view);
        Server server = new Server();
        View activityContext = (View) view.getParent();
        server.name = ((TextInputLayout) activityContext.findViewById(R.id.name)).getEditText().getText().toString();
        server.url = ((TextInputLayout) activityContext.findViewById(R.id.url)).getEditText().getText().toString();
        server.organization_name = ((TextInputLayout) activityContext.findViewById(R.id.organization_name)).getEditText().getText().toString();
        server.issuer = ((TextInputLayout) activityContext.findViewById(R.id.issuer)).getEditText().getText().toString();
        Log.i(TAG, "Adding Server");
        serverDBHelper.addServer(server);
        addCACertificate();
        finish();
    }
}
