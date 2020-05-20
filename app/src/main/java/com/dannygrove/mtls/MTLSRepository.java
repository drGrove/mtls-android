package com.dannygrove.mtls;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class MTLSRepository {
    private UserSettingsDAO mUserSettingsDao;
    private LiveData<UserSettings> mUserSettings;
    private ServerDAO mServerDao;
    private LiveData<List<Server>> mServers;

    MTLSRepository(Application application) {
        MTLSDatabase db = MTLSDatabase.getDatabase(application);
        mUserSettingsDao = db.userSettingsDAO();
        mUserSettings = mUserSettingsDao.getUserSettings(1);
        mServerDao = db.serverDAO();
        mServers = mServerDao.getServer();
    }

    LiveData<UserSettings> getUserSettings() {
        return mUserSettings;
    }

    void insertUserSettings(UserSettings userSettings) {
        MTLSDatabase.databaseWriteExecutor.execute(() -> {
            mUserSettingsDao.insert(userSettings);
        });
    }

    LiveData<List<Server>> getServers() {
        return mServers;
    }

    Server getServer(Long id) {
        return mServerDao.getServer(id);
    }

    void insertServer(Server server) {
        MTLSDatabase.databaseWriteExecutor.execute(() -> {
            mServerDao.insert(server);
        });
    }

    void updateServer(Server server) {
        MTLSDatabase.databaseWriteExecutor.execute(() -> {
            mServerDao.update(server);
        });
    }

    void deleteServer(Long id) {
        MTLSDatabase.databaseWriteExecutor.execute(() -> {
            mServerDao.delete(id);
        });
    }
}
