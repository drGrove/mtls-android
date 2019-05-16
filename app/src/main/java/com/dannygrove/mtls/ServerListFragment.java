package com.dannygrove.mtls;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ServerListFragment extends Fragment {
    private Activity activity = null;
    private static final String TAG = "ServerListFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_servers, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();

        FloatingActionButton addServerFab = activity.findViewById(R.id.add_server_fab);
        addServerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Hello?");
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new NewServerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }
}
