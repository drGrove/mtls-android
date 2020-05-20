package com.dannygrove.mtls;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class UserSettingsViewModel extends AndroidViewModel {
    private MTLSRepository mRepository;
    private LiveData<UserSettings> mUserSettings;

    public UserSettingsViewModel(Application application) {
        super(application);
        mRepository = new MTLSRepository(application);
        mUserSettings = mRepository.getUserSettings();
    }

    LiveData<UserSettings> getUserSettings() {
        return mUserSettings;
    }

    public void insert(UserSettings userSettings) {
        mRepository.insertUserSettings(userSettings);
    }
}
