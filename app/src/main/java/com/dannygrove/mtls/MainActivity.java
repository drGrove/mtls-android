package com.dannygrove.mtls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter serverAdapter;
    private List<ServerListItem> serverList;
    private ServerDBHelper serverDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serverDBHelper = ServerDBHelper.getInstance(this);
        serverAdapter = new ServerAdapter(serverDBHelper.getServers(), this);
        recyclerView.setAdapter(serverAdapter);
    }

    @Override
    protected void  onResume() {
        super.onResume();
        serverAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        serverDBHelper.close();
        super.onDestroy();
    }

    public void showAddServer(View view) {
        Intent intent = new Intent(this, AddServerActivity.class);
        startActivity(intent);
    }

    public void showUserProfile(View view) {
        Intent intent = new Intent(this,  UserProfileActivity.class);
        startActivity(intent);
    }
}
