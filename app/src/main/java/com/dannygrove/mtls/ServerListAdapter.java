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

import java.util.ArrayList;
import java.util.List;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListAdapter.ServerViewHolder> {
    private List<Server> serverList = new ArrayList<Server>();
    private final LayoutInflater mInflator;
    private Context context;

    public ServerListAdapter(Context context) {
        this.context = context;
        mInflator = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ServerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflator.inflate(R.layout.server_list_item, parent, false);
        return new ServerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServerViewHolder holder, int position) {
        final Server server = serverList.get(position);

        holder.textViewHead.setText(server.name);
        holder.textViewSubhead.setText(server.organization_name);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ServerDetailActivity.class);
                intent.putExtra("id", server.id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (serverList != null)
            return serverList.size();
        else return 0;
    }

    public void setServers(List<Server> servers) {
        serverList = servers;
        notifyDataSetChanged();
    }

    class ServerViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewHead;
        public TextView textViewSubhead;
        public LinearLayout linearLayout;

        public ServerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewSubhead = (TextView) itemView.findViewById(R.id.textViewSubHead);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.serverListItem);
        }
    }
}
