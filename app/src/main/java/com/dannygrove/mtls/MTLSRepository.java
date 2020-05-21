package com.dannygrove.mtls;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MTLSRepository {
    private UserSettingsDAO userSettingsDAO;
    private UserSettings mUserSettings;
    private ServerDAO serverDAO;
    private LiveData<List<Server>> mServers;

    MTLSRepository(Application application) {
        MTLSDatabase db = MTLSDatabase.getDatabase(application);
        userSettingsDAO = db.userSettingsDAO();
        mUserSettings = userSettingsDAO.getUserSettings(1);
        serverDAO = db.serverDAO();
        mServers = serverDAO.getServer();
    }

    UserSettings getUserSettings() {
        return mUserSettings;
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

    void updateUserSettings(UserSettings userSettings) {
        MTLSDatabase.databaseWriteExecutor.execute(() -> {
            userSettingsDAO.update(userSettings);
        });
    }

    void insertUserSettings(UserSettings userSettings) {
        MTLSDatabase.databaseWriteExecutor.execute(() -> {
            userSettingsDAO.insert(userSettings);
        });
    }
}
