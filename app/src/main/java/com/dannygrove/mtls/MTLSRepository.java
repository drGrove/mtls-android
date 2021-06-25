package com.dannygrove.mtls;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MTLSRepository {
    private ServerDAO serverDAO;
    private LiveData<List<Server>> mServers;

    MTLSRepository(Application application) {
        MTLSDatabase db = MTLSDatabase.getDatabase(application);
        serverDAO = db.serverDAO();
        mServers = serverDAO.getServer();
    }

    LiveData<List<Server>> getServers() {
        return mServers;
    }

    Server getServer(Long id) {
        return serverDAO.getServer(id);
    }

    void insertServer(Server server) {
        MTLSDatabase.databaseWriteExecutor.execute(() -> {
            serverDAO.insert(server);
        });
    }

    void updateServer(Server server) {
        MTLSDatabase.databaseWriteExecutor.execute(() -> {
            serverDAO.update(server);
        });
    }

    void deleteServer(Long id) {
        MTLSDatabase.databaseWriteExecutor.execute(() -> {
            serverDAO.delete(id);
        });
    }
}
