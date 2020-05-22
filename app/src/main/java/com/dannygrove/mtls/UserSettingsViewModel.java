package com.dannygrove.mtls;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

public class UserSettingsViewModel extends AndroidViewModel {
    private MTLSRepository mRepository;
    private UserSettings mUserSettings;

    public UserSettingsViewModel(Application application) {
        super(application);
        mRepository = new MTLSRepository(application);
        mUserSettings = mRepository.getUserSettings();
    }

    UserSettings get() {
        return mUserSettings;
    }

    void update(UserSettings settings) {
        mRepository.updateUserSettings(settings);
    }

    public void insert(UserSettings userSettings) {
        mRepository.insertUserSettings(userSettings);
    }
}
