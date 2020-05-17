package com.dannygrove.mtls;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ViewHolder> {

    private List<Server> serverList;
    private Context context;

    public ServerAdapter(List<Server> serverList, Context context) {
        this.serverList = serverList;
        this.context = context;
    }

    public void updateServerList(List<Server> newList) {
        serverList.clear();
        serverList.addAll(newList);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.server_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Server server = serverList.get(position);

        holder.textViewHead.setText(server.name);
        holder.textViewSubhead.setText(server.organization_name);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(this,  ServerItemActivity.class);
            }
        });
    }


    @Override
    public int getItemCount() {
        return serverList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewHead;
        public TextView textViewSubhead;
        public LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewSubhead = (TextView) itemView.findViewById(R.id.textViewSubHead);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.serverListItem);
        }
    }
}
