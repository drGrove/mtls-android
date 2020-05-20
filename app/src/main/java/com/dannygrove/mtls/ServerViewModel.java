package com.dannygrove.mtls;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ServerViewModel extends AndroidViewModel {
    private MTLSRepository mRepository;
    private LiveData<List<Server>> servers;

    public ServerViewModel (Application application) {
        super(application);
        mRepository = new MTLSRepository(application);
        servers = mRepository.getServers();
    }

    LiveData<List<Server>> getServers() {
        return servers;
    }

    Server getServer(Long id) {
       return mRepository.getServer(id);
    }

    public void insert(Server server) { mRepository.insertServer(server);}

    public void delete(Long id) {
        mRepository.deleteServer(id);
    }

    public void update(Server server) {
        mRepository.updateServer(server);
    }
}
