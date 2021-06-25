package com.dannygrove.mtls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_SERVER_REQUEST = 1;
    private RecyclerView recyclerView;
    private ServerListAdapter adapter;
    private ServerViewModel mServerViewModel;
    private List<Server> serverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(serverListItemTouchHelperCallback).attachToRecyclerView(recyclerView);
        adapter = new ServerListAdapter(this);
        recyclerView.setAdapter(adapter);
        mServerViewModel = new ViewModelProvider(this).get(ServerViewModel.class);

        mServerViewModel.getServers().observe(this, new Observer<List<Server>>() {
            @Override
            public void onChanged(List<Server> servers) {
                adapter.setServers(servers);
                serverList = servers;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showAddServer(View view) {
        Intent intent = new Intent(this, AddServerActivity.class);
        startActivity(intent);
    }

    public void showUserProfile(View view) {
        Intent intent = new Intent(this,  UserSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_SERVER_REQUEST) {
            if (resultCode == RESULT_OK) {
                //updateServerList();
            }
        }
    }

    ItemTouchHelper.SimpleCallback serverListItemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d("MainActivity", "Does a thing");
            Integer position = viewHolder.getAdapterPosition();
            Server server = serverList.get(position);
            mServerViewModel.delete(server.id);
            serverList.remove(position);
        }
    };
}
